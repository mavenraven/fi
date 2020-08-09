package main

import "fmt"
import "encoding/csv"
import "os"
import "time"
import "strconv"
import "github.com/umahmood/haversine"
import "net/url"
import "github.com/hashicorp/go-retryablehttp"
import "encoding/json"
import "io"
import "io/ioutil"
import "log"

func main() {

	records := toCsvRecord(os.Stdin, ';')
	rows := toRow(records)
	groups := toGroupedRow(rows)
	walks := toWalk(groups)
	payloads := toUrl(walks, os.Args[1])
	makeMap(payloads)
	select {}
}

func toCsvRecord(reader io.Reader, delimiter rune) <-chan []string {
	csvReader := csv.NewReader(reader)
	csvReader.Comma = delimiter
	/*
		We don't care about the exact number of fields. We only care that at least 3 exist for a given row.
		No need to overspecify.
	*/
	csvReader.FieldsPerRecord = -1
	//TODO: max line length, csv checker is happy to try to load gigabyte long lines

	out := make(chan []string)
	go func() {
		defer close(out)
		for {
			record, err := csvReader.Read()
			if err == io.EOF {
				break
			}
			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				continue
			}
			out <- record
		}
	}()
	return out
}

type row struct {
	lat  float64
	lon  float64
	time time.Time
}

func toRow(in <-chan []string) <-chan row {
	out := make(chan row)
	go func() {
		defer close(out)
		for record := range in {
			if len(record) < 3 {
				err := fmt.Errorf("record must have at least 3 fields %v", record)
				fmt.Fprintln(os.Stderr, err)
				continue
			}

			parseErrors := make([]error, 0, 3)
			//layout was found using dateparse.ParseFormat from https://github.com/araddon/dateparse.
			layout := "2006-01-02 15:04:05 -0700"
			time, err := time.Parse(layout, record[0])
			if err != nil {
				parseErrors = append(parseErrors, fmt.Errorf("could not parse time for record %v: %w", record, err))
			}

			lat, err := strconv.ParseFloat(record[1], 64)
			if err != nil {
				parseErrors = append(parseErrors, fmt.Errorf("could not parse latitude for record %v: %w", record, err))
			}

			lon, err := strconv.ParseFloat(record[2], 64)
			if err != nil {
				parseErrors = append(parseErrors, fmt.Errorf("could not parse longitude for record %v: %w", record, err))
			}

			if len(parseErrors) != 0 {
				for _, e := range parseErrors {
					fmt.Fprintln(os.Stderr, e)
				}
				continue
			}

			out <- row{lat: lat, lon: lon, time: time}
		}
	}()
	return out
}

func toGroupedRow(in <-chan row) <-chan []row {
	out := make(chan []row)
	/*
		Chosen somewhat arbitrarily.
		The data of a group needs to be small enough to fit inside a URL.
		Also needed to prevent loading a malicious or malformed file into memory with billions of rows in the same group.
	*/
	maxGroupSize := 100
	go func() {
		defer close(out)

		var last *time.Time
		group := make([]row, 0, maxGroupSize)
		for r := range in {
			if len(group) > maxGroupSize {
				err := fmt.Errorf("group exceeded max size of %v", maxGroupSize)
				fmt.Fprintln(os.Stderr, err)
				group = make([]row, 0, maxGroupSize)
				continue
			}
			// For the first row.
			if last == nil {
				timeCopy := r.time
				last = &timeCopy
			}

			if !inSameWalk(*last, r.time) {
				out <- group
				group = make([]row, 0, maxGroupSize)
			}

			group = append(group, r)
			timeCopy := r.time
			last = &timeCopy

		}
		out <- group
	}()
	return out
}

func inSameWalk(last, current time.Time) bool {
	diff := current.Sub(last)

	if diff < 0 {
		diff = diff * -1
	}

	return diff > time.Hour
}

type walk struct {
	LineString       geojson
	DistanceTraveled float64
	TotalTime        time.Duration
}

type geojson struct {
	FeatureType string      `json:"type"`
	Coordinates [][]float64 `json:"coordinates"`
}

func toWalk(in <-chan []row) <-chan walk {
	out := make(chan walk)
	go func() {
		defer close(out)
		for group := range in {
			if len(group) < 2 {
				err := fmt.Errorf("group must have at least 2 rows %v", group)
				fmt.Fprintln(os.Stderr, err)
				continue
			}

			last := group[len(group)-1]
			totalTime := last.time.Sub(group[0].time)

			coords := make([][]float64, len(group))
			lineString := geojson{FeatureType: "LineString", Coordinates: coords}

			for i := range lineString.Coordinates {
				lineString.Coordinates[i] = []float64{group[i].lon, group[i].lat}
			}

			out <- walk{LineString: lineString, DistanceTraveled: distanceTraveledInMi(group), TotalTime: totalTime}
		}
	}()

	return out
}

func distanceTraveledInMi(group []row) float64 {
	totalDistance := float64(0)
	var last *haversine.Coord

	for _, row := range group {
		if last == nil {
			last = &haversine.Coord{Lat: row.lat, Lon: row.lon}
			continue
		}

		current := haversine.Coord{Lat: row.lat, Lon: row.lon}
		mi, _ := haversine.Distance(*last, current)
		totalDistance += mi
	}

	return totalDistance
}

type payload struct {
	url  string
	walk walk
}

func toUrl(in <-chan walk, apiToken string) <-chan payload {
	out := make(chan payload)
	go func() {
		defer close(out)

		for w := range in {
			json, err := json.Marshal(w.LineString)
			if err != nil {
				wrapped := fmt.Errorf("could not marshall json for linestring in walk %v: %w", w, err)
				fmt.Fprintln(os.Stderr, wrapped)
				continue
			}

			escaped := url.PathEscape(string(json))

			urlStr := fmt.Sprintf("https://api.mapbox.com/styles/v1/mapbox/streets-v10/static/geojson(%s)/auto/1024x1024@2x?access_token=%s&logo=false", escaped, apiToken)
			out <- payload{url: urlStr, walk: w}

		}
	}()
	return out
}

type output struct {
	FileName         string        `json:"file_name"`
	DistanceTraveled float64       `json:"distance_traveled"`
	TotalTime        time.Duration `json:"total_time"`
}

func makeMap(in <-chan payload) {
	for p := range in {
		go func(p payload) {
			devNullLogger := log.New(ioutil.Discard, "", 0)
			c := retryablehttp.NewClient()
			c.Logger = devNullLogger

			resp, err := c.Get(p.url)
			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}

			defer resp.Body.Close()

			tmp, err := ioutil.TempFile("", "*.png")
			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}

			if _, err := io.Copy(tmp, resp.Body); err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}

			output, err := json.Marshal(&output{
				FileName:         tmp.Name(),
				DistanceTraveled: p.walk.DistanceTraveled,
				TotalTime:        p.walk.TotalTime,
			})

			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}

			fmt.Println(string(output))
		}(p)
	}
}
