package mavenraven.org;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {
            "-mapboxAccessToken" }, description = "See https://docs.mapbox.com/help/how-mapbox-works/access-tokens/")
    private String mapboxAccessToken;

    @Parameter(description = "input CSV file location")
    private String csvFileLocation;
}
