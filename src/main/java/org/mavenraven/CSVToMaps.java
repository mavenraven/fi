package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.mavenraven.func.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CSVToMaps {
    public static void main(String[] argv) {
        Args args = new Args();

        JCommander jCommander = JCommander.newBuilder().addObject(args).build();
        jCommander.setProgramName(CSVToMaps.class.getSimpleName());
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
            var walks = groups.stream().map(convertRowsToWalk).collect(Collectors.toList());

            int i = 1;
            var tmpDir = Files.createTempDirectory("csvToMaps").toString();
            for (var walk : walks) {
                var image = createMapImage.apply(walk);

                var filePath = Paths.get(tmpDir, "map" + i + ".png").toString();
                i++;

                var file = new File(filePath);
                ImageIO.write(image, "png", file);
                System.out.println(file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
