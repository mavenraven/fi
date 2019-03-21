package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mavenraven.Row;

import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetRowsFromFileTest {

    private CSVParser csv;
    private Function<List<Row>, List<List<Row>>> grouper;
    private Function<CSVRecord, Row> deserializer;
    private FileReader fileReader;

    @BeforeEach
    public void setUp() throws IOException {
        String fileLocation = this.getClass().getClassLoader().getResource("gps_dataset.csv").getFile();
        fileReader = new FileReader(fileLocation);
        deserializer = (x) -> new Row(Point.fromLngLat(Double.parseDouble(x.get(2)), Double.parseDouble(x.get(1))),
                OffsetDateTime.now());

    }

    @Test
    public void itDeserializesUsingPassedInFunction() {
        var result = new GetRowsFromFile(deserializer).apply(fileReader);

        assertAll(() -> {
            assertEquals(40.702452, result.get(0).getPoint().latitude());
        }, () -> {
            assertEquals(-73.984135, result.get(0).getPoint().longitude());
        });
    }
}