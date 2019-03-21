package org.mavenraven;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Paths;

public class CSVToMapsIT {

    private String jarPath;
    private String csvFileLocation;
    private String mapboxAccessToken;
    private String finishedMapFileLocation;

    @BeforeEach
    public void beforeEach() {
        var buildDir = System.getProperty("buildDirectory");
        var jarName = System.getProperty("jarName");
        var baseDir = System.getProperty("baseDir");
        var testSourceDir = Paths.get(baseDir, "src", "test").toString();
        var csvFixtureName = System.getProperty("csvFixtureName");
        var finishedMapFixtureName = System.getProperty("finishedMapFixtureName");

        mapboxAccessToken = System.getProperty("mapboxAccessToken");
        if (mapboxAccessToken == null) {
            throw new RuntimeException("Use 'mvn verify -DmapboxAccessToken=<token>'");
        }

        jarPath = Paths.get(buildDir, jarName).toString();
        csvFileLocation = Paths.get(testSourceDir, "resources", csvFixtureName).toString();
        finishedMapFileLocation = Paths.get(testSourceDir, "resources", finishedMapFixtureName).toString();
    }

    @Test
    public void itOutputsTheExpectedMap() throws IOException {
        var rt = Runtime.getRuntime();
        String[] commands = { "java", "-jar", jarPath, "--mapboxAccessToken", mapboxAccessToken, "--csvFileLocation",
                csvFileLocation };
        var proc = rt.exec(commands);

        var resultOutput = IOUtils.toString(new BufferedReader(new InputStreamReader(proc.getInputStream())));
        var resultError = IOUtils.toString(new BufferedReader(new InputStreamReader(proc.getErrorStream())));
        System.out.println("std output: " + resultOutput);
        System.out.println("srd error: " + resultError);
        var firstFilePath = resultOutput.lines().findFirst().get();
        var inputStream = new FileInputStream(new File(firstFilePath));
        var actualHash = DigestUtils.sha512Hex(inputStream);
        var expectedHash = DigestUtils.sha512Hex(new FileInputStream((new File(finishedMapFileLocation))));

        assertEquals(expectedHash, actualHash,
                "Hashes do not match. If the image rendering logic has changed, overwrite the fixture image.");
    }
}
