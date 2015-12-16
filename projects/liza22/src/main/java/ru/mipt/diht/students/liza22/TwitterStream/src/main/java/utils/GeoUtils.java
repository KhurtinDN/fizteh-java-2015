package utils;

import twitter4j.*;

import static java.lang.Math.*;

public final class GeoUtils {

    private GeoUtils() { }

    /*
     * Finds twitter4j.Place object by place name.
     * @param twitter initialized twitter instance
     * @param placeName name of place to search
     * @return found twitter4j.Place object
     * @throws TwitterException
     */
    public static Place findPlaceByName(Twitter twitter, String placeName) throws TwitterException {
        GeoQuery geoQuery = new GeoQuery((String) null);
        geoQuery.setQuery(placeName);
        // we need the only one place
        geoQuery.setMaxResults(1);
        ResponseList<Place> places = twitter.searchPlaces(geoQuery);
        if (places.isEmpty()) {
            throw new IllegalArgumentException("Place by name = '" + placeName + "' not found");
        }
        return places.get(0);
    }

    /*
     * Calculates distance between two points based on their longitude and latitude.
     * @see <a href="http://stackoverflow.com/a/3694410/1983808">
     *
     * @param lat1 latitude of point_1
     * @param lon1 longitude of point_1
     * @param lat2 latitude of point_2
     * @param lon2 longitude of point_2
     * @return distance in miles
     */
    public static double distanceBetweenTwoCoordinates(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = sin(toRadians(lat1)) * sin(toRadians(lat2))
                + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
        dist = acos(dist);
        dist = toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }
}
