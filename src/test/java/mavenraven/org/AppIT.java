package mavenraven.org;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class AppIT {

    @Test
    public void itRunsTheCli() throws IOException {
        String buildDir = System.getProperty("buildDirectory");
        String jarName = System.getProperty("jarName");
        String jarPath = Paths.get(buildDir, jarName).toString();
        Runtime rt = Runtime.getRuntime();
        String[] commands = { "java", "-jar", jarPath };
        Process proc = rt.exec(commands);

        BufferedReader resultOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = resultOutput.readLine();

        assertEquals(line, "Hello World!");

    }
}
