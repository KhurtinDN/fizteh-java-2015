package ru.mipt.diht.students.TwitterStream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

/**
 * Created by mikhail on 28.01.16.
 */
public class GoogleGeocoding {
    private static final String GOOGLE_PROPERTIES = "google.properties";
    private static final String API_KEY_FIELD = "apiKey";

    public static GeocodingResult[] getGeocodingResult(String location) throws Exception {
        GeoApiContext context = new GeoApiContext().setApiKey(PropertiesHelper.getProperty(
                GOOGLE_PROPERTIES, API_KEY_FIELD));

        return GeocodingApi.geocode(context, location).await();
    }
}
