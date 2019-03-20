package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.api.staticmap.v1.models.StaticPolylineAnnotation;
import com.mapbox.core.utils.MapboxUtils;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import okhttp3.HttpUrl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] argv) {
        Args args = new Args();

        JCommander jCommander = JCommander.newBuilder().addObject(args).build();
        jCommander.setProgramName(App.class.getSimpleName());
        try {
            jCommander.parse(argv);
        } catch (ParameterException e) {
            e.usage();
            System.exit(1);
        }

        try {
            Reader in = new FileReader(args.csvFileLocation);
            try {
                CSVParser parsed = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(in);
                List<List<Point>> grouped = GroupPoints.exec(parsed.getRecords().stream()
                        .map(x -> ToPointWithDateTime.exec(x)).collect(Collectors.toList()));
                List<LineString> lineStrings = grouped.stream().map(x -> LineString.fromLngLats(x))
                        .collect(Collectors.toList());
                MapboxStaticMap map = MapboxStaticMap.builder().accessToken(args.mapboxAccessToken)
                        .geoJson(lineStrings.get(3)).cameraPoint(lineStrings.get(3).coordinates().get(0)).height(1024)
                        .width(1024).retina(true).cameraAuto(true).logo(false).build();
                HttpUrl url = map.url();
                int x = 1;
            } catch (IOException e) {
                System.err.println("Unable to parse CSV file: " + e.getMessage());
                System.exit(1);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(args.csvFileLocation + " not found.");
            System.exit(1);
        }

        System.out.println("Hello World!");
    }
}
