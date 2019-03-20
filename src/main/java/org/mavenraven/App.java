package org.mavenraven;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

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

        System.out.println("Hello World!");
    }
}
