package org.mavenraven.func;

import com.jillesvangurp.geo.GeoGeometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.mavenraven.Row;
import org.mavenraven.Walk;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConvertRowsToWalk implements Function<List<Row>, Walk> {
    @Override
    public Walk apply(List<Row> rows) {
        if (rows.size() < 2) {
            throw new IllegalArgumentException(
                    "rows must contain at least 2 elements.");
        }

        var points = rows.stream()
                .map(x -> x.getPoint())
                .collect(Collectors.toList());

        var totalDistance = calculateDistance(points);
        var totalTime = calculateTime(rows);

        return new Walk(
                LineString.fromLngLats(points),
                totalDistance,
                totalTime);
    }

    private Duration calculateTime(List<Row> rows) {
        var firstTime = rows.get(0).getDateTime();

        // not efficient if not array based, but whatever
        var lastTime = rows.get(rows.size() - 1).getDateTime();

        return Duration.between(firstTime, lastTime);
    }

    private double calculateDistance(List<Point> points) {
        var totalDistance = 0D;

        Point last = null;
        for (var point : points) {
            if (last == null) {
                last = point;
                continue;
            }
            var distance = GeoGeometry.distance(
                    last.latitude(),
                    last.longitude(),
                    point.latitude(),
                    point.longitude());
            totalDistance = totalDistance + distance;
            last = point;
        }
        return totalDistance;
    }
}
