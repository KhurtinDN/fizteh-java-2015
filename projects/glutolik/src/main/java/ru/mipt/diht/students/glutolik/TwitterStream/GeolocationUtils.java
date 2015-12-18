package ru.mipt.diht.students.glutolik.TwitterStream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import twitter4j.GeoLocation;
import twitter4j.Place;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by glutolik on 13.12.15.
 */
public class GeolocationUtils {
    protected static final String GOOGLE_KEY = readGoogleKey();

    private static String readGoogleKey() {
        Properties properties = new Properties();
        try (FileInputStream propertiesFile = new FileInputStream("geolocation.properties")) {
            properties.load(propertiesFile);
            return properties.getProperty("key");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Geometry getCoordinates(String location) {
        GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_KEY);
        GeocodingApiRequest geoRequest = GeocodingApi.geocode(context, location);
        try {
            GeocodingResult[] locations = geoRequest.await();
            return locations[0].geometry;
        } catch (Exception e) {
            System.err.println("Couldn't connect to google.com");
            return null;
        }
    }

    public static boolean checkLocation(Place place, Geometry searchLocationForm) {
        if (searchLocationForm == null) {
            return true;
        }
        if (place == null) {
            return false;
        }

        double latitudeSum = 0, longitudeSum = 0;
        GeoLocation[][] geoLocations = place.getBoundingBoxCoordinates();

        for (int i = 0; i < geoLocations[0].length; ++i) {
            latitudeSum += geoLocations[0][i].getLatitude();
            longitudeSum += geoLocations[0][i].getLongitude();
        }
        double latitude = latitudeSum / geoLocations[0].length;
        double longtitude = longitudeSum / geoLocations[0].length;

        return (latitude > searchLocationForm.bounds.southwest.lat
                && latitude < searchLocationForm.bounds.northeast.lat
                && longtitude > searchLocationForm.bounds.southwest.lng
                && longtitude < searchLocationForm.bounds.northeast.lng);
    }

}
