package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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

        try {
            Reader in = new FileReader(args.csvFileLocation);
            try {
                CSVParser parsed = CSVFormat.DEFAULT.parse(in);
            } catch (IOException e) {
                System.err.println("Unable to parse CSV file: " + e.getMessage());
                System.exit(1);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(args.csvFileLocation + " not found.");
            System.exit(1);
        }

        System.out.println("Hello World!");
    }
}
