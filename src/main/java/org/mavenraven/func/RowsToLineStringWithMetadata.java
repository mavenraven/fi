package org.mavenraven.func;

import com.jillesvangurp.geo.GeoGeometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.mavenraven.LineStringWithMetadata;
import org.mavenraven.Row;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RowsToLineStringWithMetadata implements Function<List<Row>, LineStringWithMetadata> {
    @Override
    public LineStringWithMetadata apply(List<Row> rows) {
        if (rows.size() < 2) {
            throw new IllegalArgumentException("rows must contain at least 2 elements.");
        }

        var points = rows.stream().map(x -> x.getPoint()).collect(Collectors.toList());
        double totalDistance = 0;

        Point last = null;
        for (var point : points) {
            if (last == null) {
                last = point;
                continue;
            }
            var distance = GeoGeometry.distance(last.latitude(), last.longitude(), point.latitude(), point.longitude());
            totalDistance = totalDistance + distance;
            last = point;
        }

        var firstTime = rows.get(0).getDateTime();

        // not efficient if not array based, but whatever
        var lastTime = rows.get(rows.size() - 1).getDateTime();

        var totalTime = Duration.between(firstTime, lastTime);

        return new LineStringWithMetadata(LineString.fromLngLats(points), totalDistance, totalTime);
    }
}
