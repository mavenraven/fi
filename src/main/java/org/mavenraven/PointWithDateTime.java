package org.mavenraven;

import com.mapbox.geojson.Point;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class PointWithDateTime {
    private Point point;
    private OffsetDateTime dateTime;

    public PointWithDateTime(Point point, OffsetDateTime dateTime) {
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
