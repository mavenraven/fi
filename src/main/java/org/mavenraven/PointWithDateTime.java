package org.mavenraven;

import com.mapbox.geojson.Point;

import java.time.LocalDate;
import java.time.LocalTime;

public class PointWithDateTime {
    private Point point;
    private LocalDate date;
    private LocalTime time;

    public PointWithDateTime(Point point, LocalDate date, LocalTime time) {
        this.point = point;
        this.date = date;
        this.time = time;
    }

    public Point getPoint() {
        return point;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}
