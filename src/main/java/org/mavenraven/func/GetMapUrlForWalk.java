package org.mavenraven.func;

import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import org.mavenraven.Walk;

import java.net.URL;
import java.util.function.Function;

public class GetMapUrlForWalk implements Function<Walk, URL> {

	private final String accessToken;

	public GetMapUrlForWalk(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public URL apply(Walk walk) {
		return MapboxStaticMap.builder()
				.accessToken(accessToken)
				.geoJson(walk.getLineString())
				.cameraPoint(walk.getLineString().coordinates().get(0))
				.height(1024)
				.width(1024)
				.retina(true)
				.cameraAuto(true)
				.logo(false)
				.build()
				.url()
				.url();
	}
}
