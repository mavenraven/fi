package org.mavenraven;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(required = true, names = {
            "--mapboxAccessToken" }, description = "See https://docs.mapbox.com/help/how-mapbox-works/access-tokens/.")
    private String mapboxAccessToken;

    @Parameter(required = true, names = { "--csvFileLocation" }, description = "Location of input GPS CSV data.")
    private String csvFileLocation;

}
