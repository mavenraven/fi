package org.mavenraven.func;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mavenraven.Row;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVToGroupedRows implements Function<CSVParser, List<List<Row>>> {
    private final Function<List<Row>, List<List<Row>>> grouper;
    private final Function<CSVRecord, Row> rowDeserializer;

    public CSVToGroupedRows(Function<List<Row>, List<List<Row>>> grouper, Function<CSVRecord, Row> rowDeserializer) {
        this.grouper = grouper;
        this.rowDeserializer = rowDeserializer;
    }

    @Override
    public List<List<Row>> apply(CSVParser csvRecords) {
        try {
            var rows = csvRecords.getRecords().stream().map(rowDeserializer).collect(Collectors.toList());

            return grouper.apply(rows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
