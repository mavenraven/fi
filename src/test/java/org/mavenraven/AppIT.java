package org.mavenraven;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Paths;

public class AppIT {

    private String jarPath;
    private String csvFileLocation;
    private String mapboxAccessToken;

    @BeforeEach
    public void beforeEach() {
        String buildDir = System.getProperty("buildDirectory");
        String jarName = System.getProperty("jarName");
        String testSourceDir = System.getProperty("testSourceDirectory");
        String csvFixtureName = System.getProperty("csvFixtureName");

        mapboxAccessToken = System.getProperty("mapboxAccessToken");
        if (mapboxAccessToken == null) {
            throw new RuntimeException("Use 'mvn verify -DmapboxAccessToken=<token>'");
        }

        jarPath = Paths.get(buildDir, jarName).toString();
        csvFileLocation = Paths.get(testSourceDir, "resources", csvFixtureName).toString();
    }

    @Test
    public void itRunsTheCli() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = { "java", "-jar", jarPath, "--mapboxAccessToken", mapboxAccessToken, "--csvFileLocation",
                csvFileLocation };
        Process proc = rt.exec(commands);

        String resultOutput = IOUtils.toString(new BufferedReader(new InputStreamReader(proc.getInputStream())));
        String resultError = IOUtils.toString(new BufferedReader(new InputStreamReader(proc.getErrorStream())));

        assertThat("error output: " + resultError, resultOutput, containsString("Hello World!"));
    }
}
