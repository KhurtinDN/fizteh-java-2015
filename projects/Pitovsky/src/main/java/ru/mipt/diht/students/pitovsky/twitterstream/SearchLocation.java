package ru.mipt.diht.students.pitovsky.twitterstream;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.ResponseList;

public class SearchLocation {
    private static final double DEGREES_TO_KM = 60 * 1.1515 * 1.609344;

    private List<GeoLocation> searchLocations = new ArrayList<GeoLocation>();
    private GeoLocation center = null;
    private double radius = -1;
    private double[][] boundingBox = null;

    public SearchLocation(ResponseList<Place> searchPlaces) throws SearchLocationException {
        Place biggestPlace = null;
        double maxDiametr = 0;
        for (Place place : searchPlaces) {
            double diametr = 0;
            GeoLocation[][] placeBoundingBox = place.getBoundingBoxCoordinates();
            if (placeBoundingBox.length < 1 || placeBoundingBox[0].length < 1) {
                throw new SearchLocationException("invalid place");
            }
            GeoLocation firstNode = placeBoundingBox[0][0];
            for (int x = 0; x < placeBoundingBox.length; ++x) {
                for (int y = 0; y < placeBoundingBox[x].length; ++y) {
                    double distance = getCoordinatesDistance(placeBoundingBox[x][y], firstNode);
                    if (distance > diametr) {
                        diametr = distance;
                    }
                }
            }
            if (diametr > maxDiametr) {
                maxDiametr = diametr;
                biggestPlace = place;
            }
        }
        if (biggestPlace == null) {
            throw new SearchLocationException("too few places in placelist");
        }
        for (int x = 0; x < biggestPlace.getBoundingBoxCoordinates().length; ++x) {
            for (int y = 0; y < biggestPlace.getBoundingBoxCoordinates()[x].length; ++y) {
                searchLocations.add(biggestPlace.getBoundingBoxCoordinates()[x][y]);
            }
        }
    }

    private static double getCoordinatesDistance(GeoLocation locationFrom, GeoLocation locationTo) {
        double theta = locationFrom.getLongitude() - locationTo.getLongitude();
        double distance = Math.sin(Math.toRadians(locationFrom.getLatitude()))
                * Math.sin(Math.toRadians(locationTo.getLatitude()))
                + Math.cos(Math.toRadians(locationFrom.getLatitude()))
                * Math.cos(Math.toRadians(locationTo.getLatitude()))
                    * Math.cos(Math.toRadians(theta));
        distance = Math.acos(distance);
        return Math.toDegrees(distance) * DEGREES_TO_KM;
    }

    public final GeoLocation getCenter() {
        if (center != null) {
            return center;
        }
        double xCenter = 0;
        double yCenter = 0;
        for (GeoLocation location: searchLocations) {
            xCenter = xCenter + location.getLatitude();
            yCenter = yCenter + location.getLongitude();
        }
        center = new GeoLocation(xCenter / searchLocations.size(), yCenter / searchLocations.size());
        return center;
    }

    public final double getRadius() {
        if (radius > 0) {
            return radius;
        }
        radius = 0;
        GeoLocation currentCenter = getCenter();
        for (GeoLocation location : searchLocations) {
            radius = radius + getCoordinatesDistance(currentCenter, location);
        }
        radius = (radius / searchLocations.size()) + 1;
        return radius;
    }

    public final double[][] getBoundingBox() {
        if (boundingBox != null) {
            return boundingBox;
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
        boundingBox = new double[][] {{minY, minX}, {maxY, maxX}};
        return boundingBox;
    }

    public final String toString() {
        return "(" + getCenter().getLatitude() + ", "
                + getCenter().getLongitude() + ") with r = "
                + getRadius() + ".";
    }
}
