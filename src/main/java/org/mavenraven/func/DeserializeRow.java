package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVRecord;
import org.mavenraven.Row;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class DeserializeRow implements Function<CSVRecord, Row> {
    public Row apply(CSVRecord record) {
        return new Row(
                Point.fromLngLat(
                        Double.parseDouble(record.get(2)),
                        Double.parseDouble(record.get(1))),
                OffsetDateTime.parse(
                        record.get(0),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));
    }
}
