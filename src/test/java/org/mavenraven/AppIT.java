package org.mavenraven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class AppIT {

    private String jarPath;
    private String csvFileLocation;

    @BeforeEach
    public void beforeEach() {
        String buildDir = System.getProperty("buildDirectory");
        String jarName = System.getProperty("jarName");
        String testSourceDir = System.getProperty("testSourceDirectory");
        String csvFixtureName = System.getProperty("csvFixtureName");

        jarPath = Paths.get(buildDir, jarName).toString();
        csvFileLocation = Paths.get(testSourceDir, "resources", csvFixtureName).toString();
    }

    @Test
    public void itRunsTheCli() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = { "java", "-jar", jarPath, csvFileLocation };
        Process proc = rt.exec(commands);

        BufferedReader resultOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = resultOutput.readLine();
        assertEquals(line, "Hello World!");
    }
}
