package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mavenraven.Row;
import org.mavenraven.Walk;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ConvertRowsToWalkTest {

    private List<Row> rows;
    private Walk result;
    private Row r1;

    @BeforeEach
    public void setUp() {
        r1 = new Row(
                Point.fromLngLat(-73.984135, 40.702452),
                OffsetDateTime.of(1, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC));
        var r2 = new Row(
                Point.fromLngLat(-73.986720, 40.702794),
                OffsetDateTime.of(1, 1, 1, 1, 30, 0, 0, ZoneOffset.UTC));

        var r3 = new Row(
                Point.fromLngLat(-73.984167, 40.702374),
                OffsetDateTime.of(1, 1, 1, 2, 30, 0, 0, ZoneOffset.UTC));

        rows = List.of(r1, r2, r3);
        result = new ConvertRowsToWalk().apply(rows);
    }

    @Test
    public void itAddsAllPointsToLineString() {
        assertAll(() -> {
            assertEquals(
                    40.702452,
                    result.getLineString().coordinates().get(0).latitude());
        }, () -> {
            assertEquals(
                    -73.984135,
                    result.getLineString().coordinates().get(0).longitude());
        }, () -> {
            assertEquals(
                    40.702794,
                    result.getLineString().coordinates().get(1).latitude());
        }, () -> {
            assertEquals(
                    -73.986720,
                    result.getLineString().coordinates().get(1).longitude());
        }, () -> {
            assertEquals(
                    40.702374,
                    result.getLineString().coordinates().get(2).latitude());
        }, () -> {
            assertEquals(
                    -73.984167,
                    result.getLineString().coordinates().get(2).longitude());
        });
    }

    @Test
    public void itThrowsIfThereAreLessThanTwoPoints() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ConvertRowsToWalk().apply(List.of(r1));
        });
    }

    @Test
    public void itCalculatesTheTotalDistance() {
        // using https://andrew.hedges.name/experiments/haversine/ for expected
        var totalInM = (0.221 + 0.22) * 1000;
        assertEquals(totalInM, result.getDistanceTraveledInMeters(), 1);
    }

    @Test
    public void itCalculatesTheTotalTime() {
        assertEquals(Duration.ofMinutes(90), result.getTotalTime());
    }
}
