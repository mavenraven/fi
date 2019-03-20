package org.mavenraven;

import com.mapbox.geojson.LineString;

import java.time.Duration;

public class LineStringWithMetadata {
    private LineString lineString;
    private double distanceTraveled;
    private Duration totalTime;

    public LineStringWithMetadata(LineString lineString, double distanceTraveled, Duration totalTime) {
        this.lineString = lineString;
        this.distanceTraveled = distanceTraveled;
        this.totalTime = totalTime;
    }

    public LineString getLineString() {
        return lineString;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public Duration getTotalTime() {
        return totalTime;
    }
}
