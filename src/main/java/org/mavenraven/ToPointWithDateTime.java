package org.mavenraven;

import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVRecord;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class ToPointWithDateTime {
    public static PointWithDateTime exec(CSVRecord record) {
        return new PointWithDateTime(
                Point.fromLngLat(Double.parseDouble(record.get(2)), Double.parseDouble(record.get(1))),
                OffsetDateTime.parse(record.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));
    }
}
