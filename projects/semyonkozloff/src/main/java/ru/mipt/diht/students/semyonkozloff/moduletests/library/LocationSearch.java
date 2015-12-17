package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.Math.*;

public class LocationSearch {

    private static final int EARTH_RADIUS = 6366;
    private static final int N_DEGREES_IN_SEMICIRCLE = 180;

    public static Geometry findLocation(String location)
            throws Exception {
        if (location == null) {
            return null;
        }

        String gmapsApiKey = null;
        try {
            File keyFile = new File("googlemaps.properties");
            gmapsApiKey = new Scanner(keyFile).useDelimiter("\\Z").next();
        } catch (FileNotFoundException exception) {
            FileNotFoundException fileNotFoundException =
                    new FileNotFoundException("Unable to find file "
                            + "\"googlemaps.properties\" ");
            fileNotFoundException.initCause(exception);
            throw fileNotFoundException;
        }

        GeoApiContext context = new GeoApiContext().setApiKey(gmapsApiKey);
        GeocodingResult[] geocodingResults = null;
        try {
            geocodingResults = GeocodingApi.geocode(context, location).await();
        } catch (Exception e) {
            throw new Exception("Fail of getting geocoding result ", e);
        }

        return geocodingResults[0].geometry;
    }

    public static double computeCoordinatesDistance(LatLng a, LatLng b) {
        double k = (float) (N_DEGREES_IN_SEMICIRCLE / PI);

        double a1 = a.lat / k;
        double a2 = a.lng / k;
        double b1 = b.lat / k;
        double b2 = b.lng / k;

        double t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2);
        double t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2);
        double t3 = sin(a1) * sin(b1);

        return EARTH_RADIUS * acos(t1 + t2 + t3);
    }

    /*
    public boolean isTweetFromLocation(Status tweet, Geometry locationBox) {
        Place tweetPlace = tweet.getPlace();
        if (tweetPlace == null) {
            return false;
        }

        GeoLocation[][] tweetLocation =
        tweet.getPlace().getBoundingBoxCoordinates();
        if (tweetLocation == null) {
            return false;
        }

        double longitude = (tweetLocation[0][0].getLongitude()
            + tweetLocation[0][1].getLongitude()) / 2;
        double latitude = (tweetLocation[0][0].getLatitude()
            + tweetLocation[0][1].getLatitude()) / 2;

        return longitude > locationBox.bounds.southwest.lng &&
                longitude < locationBox.bounds.northeast.lng &&
                latitude > locationBox.bounds.southwest.lat &&
                latitude < locationBox.bounds.northeast.lat;
    }
    */
}
