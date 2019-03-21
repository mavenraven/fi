package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mavenraven.Row;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupRowsTest {

    private List<Row> rows;

    @BeforeEach
    public void setUp() {
        Row r1 = new Row(Point.fromLngLat(1, -1),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("21:15:01"), ZoneOffset.ofHours(0)));

        Row r2 = new Row(Point.fromLngLat(2, -2),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("21:16:19"), ZoneOffset.ofHours(0)));

        Row r3 = new Row(Point.fromLngLat(3, -3),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("22:16:19"), ZoneOffset.ofHours(0)));

        Row r4 = new Row(Point.fromLngLat(99, -99),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("23:16:20"), ZoneOffset.ofHours(0)));

        rows = List.of(r1, r2, r3, r4);
    }

    @Test
    public void itGroupsSeparatelyIfThereIsMoreThanAnHourBetweenRows() {
        List<List<Row>> result = new GroupRows().apply(rows);

        assertAll(() -> {
            assertEquals(3, result.get(0).size());
        }, () -> {
            assertEquals(1, result.get(1).size());
        }, () -> {
            assertEquals(-1, result.get(0).get(0).getPoint().latitude());
        }, () -> {
            assertEquals(1, result.get(0).get(0).getPoint().longitude());
        }, () -> {
            assertEquals(-99, result.get(1).get(0).getPoint().latitude());
        }, () -> {
            assertEquals(99, result.get(1).get(0).getPoint().longitude());
        });

    }
}
