package ru.mipt.diht.students.maxDankow.TwitterStream.utils;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import twitter4j.GeoLocation;
import twitter4j.Place;

public class GeolocationUtils {
    protected static final String GOOGLE_API_KEY = "AIzaSyCltC9cSKnrnqOApw5TQ155nwEBW-ZUt1E";

    public static Geometry findLocation(String placeName) {
        GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_API_KEY);
        GeocodingApiRequest geoRequest = GeocodingApi.geocode(context, placeName);
        Geometry place;
        try {
            GeocodingResult[] locations = geoRequest.await();
            place = locations[0].geometry;
        } catch (Exception e) {
            System.err.println("Google geolocation error.");
            return null;
        }
        return place;
    }

    public static boolean checkLocation(Place tweetPlace, Geometry searchLocationGeometry) {
        if (searchLocationGeometry == null) {
            return true;
        }
        if (tweetPlace == null) {
            return false;
        }
        // Берем среднее значение координат заданной области.
        double latitudeSum = 0;
        double longitudeSum = 0;
        GeoLocation[][] geoLocations = tweetPlace.getBoundingBoxCoordinates();
        for (int i = 0; i < geoLocations[0].length; ++i) {
            latitudeSum += geoLocations[0][i].getLatitude();
            longitudeSum += geoLocations[0][i].getLongitude();
        }
        double centerLat = latitudeSum / geoLocations[0].length;
        double centerLng = longitudeSum / geoLocations[0].length;
        return (centerLat > searchLocationGeometry.bounds.southwest.lat
                && centerLat < searchLocationGeometry.bounds.northeast.lat
                && centerLng > searchLocationGeometry.bounds.southwest.lng
                && centerLng < searchLocationGeometry.bounds.northeast.lng);
    }
}
