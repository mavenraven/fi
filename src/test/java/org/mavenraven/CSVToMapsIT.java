package org.mavenraven;

import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class CSVToMapsIT {

    private String jarPath;
    private String csvFileLocation;
    private String mapboxAccessToken;
    private String finishedMapFileLocation;
    private String deepAIApiKey;

    @BeforeEach
    public void beforeEach() {
        var buildDir = System.getProperty("buildDirectory");
        var jarName = System.getProperty("jarName");
        var baseDir = System.getProperty("baseDir");
        var testSourceDir = Paths.get(baseDir, "src", "test").toString();
        var csvFixtureName = System.getProperty("csvFixtureName");
        var finishedMapFixtureName = System
                .getProperty("finishedMapFixtureName");

        deepAIApiKey = System.getProperty("deepAIApiKey");
        mapboxAccessToken = System.getProperty("mapboxAccessToken");
        if (mapboxAccessToken == null || deepAIApiKey == null) {
            throw new RuntimeException(
                    "Use 'mvn verify -DmapboxAccessToken=<token> -DdeepAIApiKey=<apiKey>'");
        }

        jarPath = Paths.get(buildDir, jarName).toString();
        csvFileLocation = Paths.get(testSourceDir, "resources", csvFixtureName)
                .toString();
        finishedMapFileLocation = Paths
                .get(testSourceDir, "resources", finishedMapFixtureName)
                .toString();
    }

    @Test
    public void itOutputsTheExpectedMap() throws IOException {
        var rt = Runtime.getRuntime();
        String[] commands = {
                "java",
                "-jar",
                jarPath,
                "--mapboxAccessToken",
                mapboxAccessToken,
                "--csvFileLocation",
                csvFileLocation};
        var proc = rt.exec(commands);

        var resultOutput = IOUtils.toString(
                new BufferedReader(
                        new InputStreamReader(proc.getInputStream())));
        var firstFilePath = resultOutput.lines().findFirst().get();

        int similarityScore = getSimilarityScore(
                firstFilePath,
                finishedMapFileLocation,
                deepAIApiKey);

        assertThat(
                "Images not similar enough. Compare fixture output to actual output and regen fixture if necessary.",
                similarityScore,
                lessThanOrEqualTo(20));
    }

    private static int getSimilarityScore(
            String firstImageLocation,
            String secondImageLocation,
            String apiKey) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("image1", new File(firstImageLocation));
        builder.addBinaryBody("image2", new File(secondImageLocation));

        var entity = builder.build();
        var req = new HttpPost("https://api.deepai.org/api/image-similarity");
        req.setEntity(entity);
        req.setHeader("api-key", apiKey);

        var client = HttpClientBuilder.create().build();
        var resp = client.execute(req);
        var jsonResp = new JsonParser()
                .parse(EntityUtils.toString(resp.getEntity()));

        return jsonResp.getAsJsonObject()
                .get("output")
                .getAsJsonObject()
                .get("distance")
                .getAsInt();
    }
}
