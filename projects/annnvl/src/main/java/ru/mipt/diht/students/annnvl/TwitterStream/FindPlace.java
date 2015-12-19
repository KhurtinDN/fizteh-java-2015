package ru.mipt.diht.students.annnvl.TwitterStream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class FindPlace {
    private static final double R = 6371;
    private GeocodingResult[] result;
    private double radius;

    FindPlace(String place) {
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyCAhkvmjepUzQUh9pA7g0K4QoQY2ncBno8");
        try {
            result = GeocodingApi.geocode(context, place).await();
            radius = calculateRadius();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private double calculateRadius() {
        double x1 = Math.toRadians(result[0].geometry.bounds.northeast.lat);
        double x2 = Math.toRadians(result[0].geometry.bounds.southwest.lat);
        double dx = x1 - x2;
        double lambda1;
        lambda1 = Math.toRadians(result[0].geometry.bounds.northeast.lng);
        double lambda2;
        lambda2 = Math.toRadians(result[0].geometry.bounds.southwest.lng);
        double dLambda = lambda1 - lambda2;

        double a = Math.sin(dx / 2) * Math.sin(dx / 2) + Math.cos(x1) * Math.cos(x2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance / 2;
    }

    public final LatLng getLocation() {
        return result[0].geometry.location;
    }
    public final double getRadius() {
        return radius;
    }
    public final Bounds getBounds() {
        return result[0].geometry.bounds;
    }
};
