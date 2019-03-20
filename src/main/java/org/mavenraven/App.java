package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.geojson.LineString;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.csv.CSVFormat;
import org.mavenraven.func.CSVToGroupedRows;
import org.mavenraven.func.RowGrouper;
import org.mavenraven.func.RowDeserializer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
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

        var csvToGroupedRows = new CSVToGroupedRows(new RowGrouper(), new RowDeserializer());

        try {
            Reader in = new FileReader(args.csvFileLocation);
            var parsed = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(in);
            var grouped = csvToGroupedRows.apply(parsed);
            /*
             * var grouped = new RowGrouper().apply( parsed.getRecords().stream().map(new
             * RowDeserializer()).collect(Collectors.toList())); var lineStrings = grouped.stream().map(x ->
             * LineString.fromLngLats(x)).collect(Collectors.toList()); var map =
             * MapboxStaticMap.builder().accessToken(args.mapboxAccessToken).geoJson(lineStrings.get(3))
             * .cameraPoint(lineStrings.get(3).coordinates().get(0)).height(1024).width(1024).retina(true)
             * .cameraAuto(true).logo(false).build(); var url = map.url();
             */
            int x = 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello World!");
    }
}
