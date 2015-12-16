package core.quering;

import config.Arguments;
import config.Constants;
import utils.GeoUtils;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SearchQuery builder to build query for tweets streaming
 * based on arguments (keywords, place).
 */
public class SearchQueryBuilder {
    private Twitter twitter;

    public SearchQueryBuilder(Twitter _twitter) {
        this.twitter = _twitter;
    }

    public final Query buildQuery() {
        Arguments arguments = Arguments.getInstance();
        Query query = new Query();
        // set query for tweets
        query.setQuery(arguments.getQuery());
        // set limit of tweets if specified in arguments
        if (arguments.getLimitOfTweets() != Constants.NO_TWEETS_LIMIT) {
            query.setCount(arguments.getLimitOfTweets());
        }
        // calculate and set place for tweets
        try {
            Place place = GeoUtils.findPlaceByName(twitter, arguments.getPlace());
            GeoLocation[] vertices = place.getBoundingBoxCoordinates()[0];
            /*
             * Implemented approach which described in the task:
             *
             * Для Twitter.search использовать среднее арифметическое широты и долготы
             * Place.getBoundingBoxCoordinates() и радиус как половину максимального
             * расстояния между точками.
             */
            GeoLocation center = getCenter(vertices);
            double radius = getRadius(vertices);
            query.setGeoCode(center, radius, Query.Unit.mi);
        } catch (TwitterException te) {
            System.err.println("Searching of places has been crashed with error = \"" + te.getMessage() + "\"");
            System.err.println("Search query will be created without place condition.");
        }

        return query;
    }

    public static GeoLocation getCenter(GeoLocation[] vertices) {
        double centerLatitude, centerLongitude;
        double[] latitudes = new double[vertices.length];
        double[] longitudes = new double[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            GeoLocation vertex = vertices[i];
            latitudes[i] = vertex.getLatitude();
            longitudes[i] = vertex.getLongitude();
        }
        centerLatitude = getArithmeticMean(latitudes);
        centerLongitude = getArithmeticMean(longitudes);
        return new GeoLocation(centerLatitude, centerLongitude);
    }

    public static double getRadius(GeoLocation[] vertices) {
        List<Double> distances = new ArrayList<>();
        // calculate distances between all vertices
        for (int i = 0; i < vertices.length - 1; i++) {
            for (int j = i + 1; j < vertices.length; j++) {
                GeoLocation vertex1 = vertices[i];
                GeoLocation vertex2 = vertices[j];
                distances.add(GeoUtils.distanceBetweenTwoCoordinates(
                                vertex1.getLatitude(), vertex1.getLongitude(),
                                vertex2.getLatitude(), vertex2.getLongitude())
                );
            }
        }
        // sort list in reverse order (from MAX to MIN)
        // and get the first - MAX of all distances
        Collections.sort(distances, Collections.reverseOrder());
        return distances.get(0);
    }

    public static double getArithmeticMean(double[] numbers) {
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return sum / numbers.length;
    }
}
