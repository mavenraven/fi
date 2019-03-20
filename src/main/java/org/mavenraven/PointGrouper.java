package org.mavenraven;

import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVParser;

import java.lang.reflect.Array;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PointGrouper {
    public List<List<Point>> exec(List<PointWithDateTime> points) {
        ArrayList<List<Point>> outside = new ArrayList<>();
        ArrayList<Point> inside = new ArrayList<>();
        outside.add(inside);

        LocalDateTime last = null;

        for (PointWithDateTime pwd: points) {
            LocalDateTime current = LocalDateTime.of(pwd.getDate(), pwd.getTime());
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
