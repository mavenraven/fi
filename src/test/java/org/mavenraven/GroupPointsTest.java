package org.mavenraven;

import com.mapbox.geojson.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupPointsTest {

    private List<PointWithDateTime> pointWithDateTimes;

    @BeforeEach
    void setUp() {
        PointWithDateTime p1 = new PointWithDateTime(Point.fromLngLat(1, -1),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("21:15:01"), ZoneOffset.ofHours(0)));

        PointWithDateTime p2 = new PointWithDateTime(Point.fromLngLat(2, -2),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("21:16:19"), ZoneOffset.ofHours(0)));

        PointWithDateTime p3 = new PointWithDateTime(Point.fromLngLat(3, -3),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("22:16:19"), ZoneOffset.ofHours(0)));

        PointWithDateTime p4 = new PointWithDateTime(Point.fromLngLat(99, -99),
                OffsetDateTime.of(LocalDate.parse("2018-03-13"), LocalTime.parse("23:16:20"), ZoneOffset.ofHours(0)));

        pointWithDateTimes = List.of(p1, p2, p3, p4);
    }

    @Test
    void itGroupsSeparatelyIfThereIsMoreThanAnHourBetweenDataPoints() {
        List<List<Point>> result = GroupPoints.exec(pointWithDateTimes);

        assertAll(() -> {
            assertEquals(3, result.get(0).size());
        }, () -> {
            assertEquals(1, result.get(1).size());
        }, () -> {
            assertEquals(-1, result.get(0).get(0).latitude());
        }, () -> {
            assertEquals(1, result.get(0).get(0).longitude());
        }, () -> {
            assertEquals(-99, result.get(1).get(0).latitude());
        }, () -> {
            assertEquals(99, result.get(1).get(0).longitude());
        });

    }
}
