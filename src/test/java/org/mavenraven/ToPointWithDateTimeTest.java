package org.mavenraven;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class ToPointWithDateTimeTest {

    private CSVRecord record;

    @BeforeEach
    void setUp() throws IOException {
        record = CSVParser.parse(
                "2018-03-13 21:15:01 -0500;40.702452;-73.984135;25.913151;65.000000;65.000000;-3.600000;-1.000000",
                CSVFormat.DEFAULT.withDelimiter(';')).getRecords().get(0);
    }

    @Test
    void itTranslatesACSVRecordToAPointWithDateTimeObject() {
        PointWithDateTime result = ToPointWithDateTime.exec(record);
        assertAll(() -> {
            assertEquals(OffsetDateTime.of(2018, 3, 13, 21, 15, 01, 0, ZoneOffset.ofHours(-5)), result.getDateTime());
        }, () -> {
            assertEquals(40.702452, result.getPoint().latitude());
        }, () -> {
            assertEquals(-73.984135, result.getPoint().longitude());
        });
    }
}