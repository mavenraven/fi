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

        var csvToGroupedRows = new CSVToGroupedRows(new GroupRows(), new DeserializeRow());
        var rowsToWalk = new ConvertRowsToWalk();
        var getMapUrlForWalk = new GetMapUrlForWalk(args.mapboxAccessToken);
        var createMapImage = new CreateMapImage(getMapUrlForWalk);

        try {
            Reader in = new FileReader(args.csvFileLocation);
            var parsed = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(in);
            var groups = csvToGroupedRows.apply(parsed);
            var walks = groups.stream().map(rowsToWalk).collect(Collectors.toList());
            for (var walk : walks) {
                var image = createMapImage.apply(walk);
                var file = File.createTempFile("map", ".png");
                ImageIO.write(image, "png", file);
                System.out.println(file.getAbsolutePath());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello World!");
    }
}
