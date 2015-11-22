package ru.mipt.diht.students.nkarpachev.TwitterStream;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLngBounds;
import twitter4j.*;

import java.util.List;

public class LocationTools {

    public static final double EARTH_RADIUS = 6371;
    private static double latitude;
    private static double longtitude;
    private static double radius;

    public static void setProperties(String locationName) {
        try {
            final Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(locationName).getGeocoderRequest();
            GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> results = geocodeResponse.getResults();

            latitude = results.get(0).getGeometry().getLocation().getLat().doubleValue();
            longtitude = results.get(0).getGeometry().getLocation().getLng().doubleValue();
            LatLngBounds bounds = results.get(0).getGeometry().getBounds();
            radius = getRadius(bounds);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void setGeoLocation(Query query) {
        query.setGeoCode(getGeoLocation(), radius, Query.Unit.km);
    }

    private static double getRadius(LatLngBounds bnds) {
        double lat1 = Math.toRadians(bnds.getNortheast().getLat().doubleValue());
        double lng1 = Math.toRadians(bnds.getNortheast().getLng().doubleValue());
        double lat2 = Math.toRadians(bnds.getSouthwest().getLat().doubleValue());
        double lng2 = Math.toRadians(bnds.getNortheast().getLng().doubleValue());
        double dLng = lng2 - lng1;
        double angleDistance = Math.atan(Math.sqrt(Math.pow(Math.cos(lat2) * Math.sin(dLng), 2.0)
                + Math.pow(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng), 2.0))
                / (Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(dLng)));
        return EARTH_RADIUS * angleDistance;
    }

    public static double locationRadius() {
        return radius;
    }

    public static GeoLocation getGeoLocation() {
        return new GeoLocation(latitude, longtitude);
    }

    public static double getDistance(GeoLocation location1, GeoLocation location2) {

        double lat1 = location1.getLatitude();
        double lng1 = location1.getLongitude();
        double lat2 = location2.getLatitude();
        double lng2 = location2.getLongitude();

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lng1 = Math.toRadians(lng1);
        lng2 = Math.toRadians(lng2);
        return EARTH_RADIUS * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng1 - lng2));
    }

}
