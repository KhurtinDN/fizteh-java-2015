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
        double latitude1 = Math.toRadians(bnds.getNortheast().getLat().doubleValue());
        double longtitude1 = Math.toRadians(bnds.getNortheast().getLng().doubleValue());
        double latitude2 = Math.toRadians(bnds.getSouthwest().getLat().doubleValue());
        double longtitude2 = Math.toRadians(bnds.getNortheast().getLng().doubleValue());
        double longtitudeDiff = longtitude2 - longtitude1;
        double angleDistance = Math.atan(Math.sqrt(Math.pow(Math.cos(latitude2) * Math.sin(longtitudeDiff), 2.0)
                + Math.pow(Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longtitudeDiff), 2.0))
                / (Math.sin(latitude1) * Math.sin(latitude2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longtitudeDiff)));
        return EARTH_RADIUS * angleDistance;
    }

    public static double locationRadius() {
        return radius;
    }

    public static GeoLocation getGeoLocation() {
        return new GeoLocation(latitude, longtitude);
    }

    public static double getDistance(GeoLocation location1, GeoLocation location2) {

        double latitude1 = location1.getLatitude();
        double longitude1 = location1.getLongitude();
        double latitude2 = location2.getLatitude();
        double longitude2 = location2.getLongitude();

        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude1 = Math.toRadians(longitude1);
        longitude2 = Math.toRadians(longitude2);
        return EARTH_RADIUS * Math.acos(Math.sin(latitude1) * Math.sin(latitude2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude1 - longitude2));
    }

}
