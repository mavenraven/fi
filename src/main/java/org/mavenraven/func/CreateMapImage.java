package org.mavenraven.func;

import org.mavenraven.Walk;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.function.Function;

public class CreateMapImage implements Function<Walk, BufferedImage> {
    private final Function<Walk, URL> getMapUrlForWalk;

    public CreateMapImage(Function<Walk, URL> getMapUrlForWalk) {
        this.getMapUrlForWalk = getMapUrlForWalk;
    }

    @Override
    public BufferedImage apply(Walk walk) {
        try {
            var distanceDisplayed = getDisplayedDistance(walk);
            var durationDisplayed = walk.getTotalTime().toString().substring(2);

            var image = ImageIO.read(getMapUrlForWalk.apply(walk));
            addDistanceAndDurationLabels(
                    distanceDisplayed,
                    durationDisplayed,
                    image);
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDistanceAndDurationLabels(
            String distanceDisplayed,
            String durationDisplayed,
            BufferedImage image) {
        var graphics = image.createGraphics();
        graphics.setPaint(Color.black);
        graphics.setFont(new Font("Sans Serif", Font.PLAIN, 50));
        graphics.drawString("distance: " + distanceDisplayed + " km", 50, 100);
        graphics.drawString("duration: " + durationDisplayed, 50, 200);
        graphics.dispose();
    }

    private String getDisplayedDistance(Walk walk) {
        var distanceInKM = walk.getDistanceTraveledInMeters() / 1000;
        var numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(3);
        return numberFormat.format(distanceInKM);
    }
}
