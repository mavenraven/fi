package org.mavenraven;

import com.mapbox.geojson.Point;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class GroupPoints {
    public static List<List<Point>> exec(List<PointWithDateTime> points) {
        ArrayList<List<Point>> outside = new ArrayList<>();
        ArrayList<Point> inside = new ArrayList<>();
        outside.add(inside);

        OffsetDateTime last = null;

        for (PointWithDateTime pwd : points) {
            OffsetDateTime current = pwd.getDateTime();
            if (last == null) {
                last = current;
            }

            Duration duration = Duration.between(last, current);
            if (duration.compareTo(Duration.ofHours(1)) > 0) {
                inside = new ArrayList<>();
                outside.add(inside);
            }

            inside.add(pwd.getPoint());
            last = current;
        }

        return outside;

    }
}
