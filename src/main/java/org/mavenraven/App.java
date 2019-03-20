package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.csv.CSVFormat;
import org.mavenraven.func.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] argv) {
        Args args = new Args();

        JCommander jCommander = JCommander.newBuilder().addObject(args).build();
        jCommander.setProgramName(App.class.getSimpleName());
        try {
            jCommander.parse(argv);
        } catch (ParameterException e) {
            e.usage();
            System.exit(1);
        }

        var convertRowsToWalk = new ConvertRowsToWalk();
        var groupRows = new GroupRows();
        var getMapUrlForWalk = new GetMapUrlForWalk(args.mapboxAccessToken);
        var createMapImage = new CreateMapImage(getMapUrlForWalk);
        var getRowsFromFile = new GetRowsFromFile(new DeserializeRow());

        try {
            var in = new FileReader(args.csvFileLocation);
            var rows = getRowsFromFile.apply(in);
            var groups = groupRows.apply(rows);
            var walks = groups.stream().map(convertRowsToWalk);
            walks.forEach(walk -> {
                try {
                    var image = createMapImage.apply(walk);
                    var file = File.createTempFile("map", ".png");
                    ImageIO.write(image, "png", file);
                    System.out.println(file.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
