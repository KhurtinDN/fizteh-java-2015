package ru.mipt.diht.students.semyonkozloff.twitterstream;

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

    public static Geometry findLocation(String location) {
        if (location == null) {
            return null;
        }

        String gmapsApiKey = null;
        try {
            File keyFile = new File("googlemaps.properties");
            gmapsApiKey = new Scanner(keyFile).useDelimiter("\\Z").next();
        } catch (FileNotFoundException exception) {
            System.err.print("Can't find file \"googlemaps.properties\": ");
            exception.printStackTrace(System.err);
            System.exit(1);
        }

        GeoApiContext context = new GeoApiContext().setApiKey(gmapsApiKey);
        GeocodingResult[] geocodingResults = null;
        try {
            geocodingResults = GeocodingApi.geocode(context, location).await();
        } catch (Exception exception) {
            System.err.print("Fail of getting geocoding result: ");
            exception.printStackTrace(System.err);
            System.exit(1);
        }

        return geocodingResults[0].geometry;
    }

    public static double computeCoordinatesDistance(LatLng a, LatLng b) {
        double pk = (float) (N_DEGREES_IN_SEMICIRCLE / PI);

        double a1 = a.lat / pk;
        double a2 = a.lng / pk;
        double b1 = b.lat / pk;
        double b2 = b.lng / pk;

        double t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2);
        double t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2);
        double t3 = sin(a1) * sin(b1);
        double tt = acos(t1 + t2 + t3);

        return EARTH_RADIUS * tt;
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

    private static final int EARTH_RADIUS = 6366;
    private static final int N_DEGREES_IN_SEMICIRCLE = 180;
}
