package main.java.ru.fizteh.fivt.pitovsky.twitterstream;

import java.util.Vector;

import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.ResponseList;

public class SearchLocation {
    private static final double DEG_TO_KM = 60 * 1.1515 * 1.609344;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180 / Math.PI;
    private static Vector<GeoLocation> searchLocations;

    public SearchLocation(ResponseList<Place> searchPlaces) {
        searchLocations = new Vector<GeoLocation>(); //*4 becouse all nodes of place box
        for (Place place : searchPlaces) {
            for (int x = 0; x < place.getBoundingBoxCoordinates().length; ++x) {
                for (int y = 0; y < place.getBoundingBoxCoordinates()[x].length; ++y) {
                    searchLocations.add(place.getBoundingBoxCoordinates()[x][y]);
                }
            }
        }
    }

    private static double getCoordinatesDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(lat1 * DEG_TO_RAD) * Math.sin(lat2 * DEG_TO_RAD)
                + Math.cos(lat1  * DEG_TO_RAD) * Math.cos(lat2 * DEG_TO_RAD) * Math.cos(theta * DEG_TO_RAD);
        dist = Math.acos(dist);
        dist = dist * RAD_TO_DEG;
        dist = dist * DEG_TO_KM;
        return dist;
    }
    private double getLocationsDistance(int from, int to) {
        return getCoordinatesDistance(
                searchLocations.get(from).getLatitude(), searchLocations.get(from).getLongitude(),
                searchLocations.get(to).getLatitude(), searchLocations.get(to).getLongitude());
    }

    public final GeoLocation getCenter() {
        if (searchLocations == null) {
            return null;
        }
        double xCenter = 0;
        double yCenter = 0;
        for (GeoLocation location: searchLocations) {
            xCenter = xCenter + location.getLatitude();
            yCenter = yCenter + location.getLongitude();
        }
        return new GeoLocation(xCenter / searchLocations.size(), yCenter / searchLocations.size());
    }
    public final double getRadius() {
        double radius = 0;
        GeoLocation center = getCenter();
        for (GeoLocation location : searchLocations) {
            radius = radius + getCoordinatesDistance(center.getLatitude(), center.getLongitude(),
                    location.getLatitude(), location.getLongitude());
        }
        return (radius / searchLocations.size()) + 1;
    }
    public final double[][] getBoundingBox() {
        if (searchLocations.size() == 0) {
            return null;
        }
        double minX = searchLocations.get(0).getLatitude();
        double maxX = searchLocations.get(0).getLatitude();
        double minY = searchLocations.get(0).getLongitude();
        double maxY = searchLocations.get(0).getLongitude();
        for (GeoLocation location: searchLocations) {
            if (location.getLatitude() < minX) {
                minX = location.getLatitude();
            }
            if (location.getLatitude() > maxX) {
                maxX = location.getLatitude();
            }
            if (location.getLongitude() < minY) {
                minY = location.getLongitude();
            }
            if (location.getLongitude() > maxY) {
                maxY = location.getLongitude();
            }
        }
        double[][] locations = {{minX, minY}, {maxX, maxY}};
        return locations;
    }
}
