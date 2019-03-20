package org.mavenraven;

import com.mapbox.geojson.Point;

import java.time.OffsetDateTime;

public class Row {
    private Point point;
    private OffsetDateTime dateTime;

    public Row(Point point, OffsetDateTime dateTime) {
        this.point = point;
        this.dateTime = dateTime;
    }

    public Point getPoint() {
        return point;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }
}
