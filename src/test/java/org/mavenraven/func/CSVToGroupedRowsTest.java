package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mavenraven.Row;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CSVToGroupedRowsTest {

    private CSVParser csv;
    private Function<List<Row>, List<List<Row>>> grouper;
    private Function<CSVRecord, Row> deserializer;

    @BeforeEach
    void setUp() throws IOException {
        csv = CSVParser.parse(
                "2018-03-13,40.702452,-73.984135,25.913151,65.000000,65.000000,-3.600000,-1.000000\n"
                        + "2018-03-14,41.702452,-74.984135,25.913151,65.000000,65.000000,-3.600000,-1.000000\n"
                        + "2018-03-15,42.702452,-75.984135,25.913151,65.000000,65.000000,-3.600000,-1.000000\n",
                CSVFormat.DEFAULT);

        grouper = (x) -> List.of(List.of(x.get(0)), List.of(x.get(1), x.get(2)));

        deserializer = (x) -> new Row(Point.fromLngLat(Double.parseDouble(x.get(2)), Double.parseDouble(x.get(1))),
                OffsetDateTime.now());

    }

    @Test
    void itGroupsAndDeserializesUsingPassedInFunctions() {
        var result = new CSVToGroupedRows(grouper, deserializer).apply(csv);

        assertAll(() -> {
            assertEquals(1, result.get(0).size());
        }, () -> {
            assertEquals(2, result.get(1).size());
        }, () -> {
            assertEquals(41.702452, result.get(1).get(0).getPoint().latitude());
        }, () -> {
            assertEquals(-74.984135, result.get(1).get(0).getPoint().longitude());
        });
    }
}