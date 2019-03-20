package org.mavenraven.func;

import com.mapbox.geojson.Point;
import org.mavenraven.Row;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GroupRows implements Function<List<Row>, List<List<Row>>> {
    public List<List<Row>> apply(List<Row> rows) {
        var outside = new ArrayList<List<Row>>();
        var inside = new ArrayList<Row>();
        outside.add(inside);

        OffsetDateTime last = null;

        for (Row row : rows) {
            var current = row.getDateTime();
            if (last == null) {
                last = current;
            }

            Duration duration = Duration.between(last, current);
            if (duration.compareTo(Duration.ofHours(1)) > 0) {
                inside = new ArrayList<>();
                outside.add(inside);
            }

            inside.add(row);
            last = current;
        }

        return outside;

    }
}
