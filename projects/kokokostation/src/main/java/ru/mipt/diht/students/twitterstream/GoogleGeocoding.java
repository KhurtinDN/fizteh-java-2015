package ru.mipt.diht.students.twitterstream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

import java.io.IOException;

/**
 * Created by mikhail on 28.01.16.
 */
public class GoogleGeocoding implements Geocoding {
    private static final String GOOGLE_PROPERTIES = "google.properties";
    private static final String API_KEY_FIELD = "apiKey";

    private GeoApiContext context;

    public GoogleGeocoding() throws IOException {
            context = new GeoApiContext().setApiKey(PropertiesHelper.getProperty(
                    GOOGLE_PROPERTIES, API_KEY_FIELD));
    }

    public GeocodingResult[] getGeocodingResult(String location) throws Exception {
        return GeocodingApi.geocode(context, location).await();
    }
}
