package org.mavenraven.func;

import org.mavenraven.Walk;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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
            var distanceInKM = walk.getDistanceTraveledInMeters() / 1000;
            var numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(3);
            var distanceDisplayed = numberFormat.format(distanceInKM);
            var durationDisplayed = walk.getTotalTime().toString().substring(2);

            var image = ImageIO.read(getMapUrlForWalk.apply(walk));
            var graphics = image.createGraphics();
            graphics.setPaint(Color.black);
            graphics.setFont(new Font("Sans Serif", Font.PLAIN, 50));
            graphics.drawString("distance: " + distanceDisplayed + " km", 50, 100);
            // graphics.drawString("duration: " + durationDisplayed, 50, 200);
            graphics.dispose();
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
