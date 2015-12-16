package core.quering;

import config.Arguments;
import utils.GeoUtils;
import twitter4j.*;

/**
 * FilterQuery builder to build query for Twitter searching
 * based on arguments (query, place).
 */
public class FilterQueryBuilder {
    private Twitter twitter;

    public FilterQueryBuilder(Twitter twitter) {
        this.twitter = twitter;
    }

    public FilterQuery buildQuery() {
        Arguments arguments = Arguments.getInstance();

        FilterQuery query = new FilterQuery();
        query.track(arguments.getKeywords());

        // calculate and set place for tweets
        try {
            Place place = GeoUtils.findPlaceByName(twitter, arguments.getPlace());
            GeoLocation[] vertices = place.getBoundingBoxCoordinates()[0];
            /*
             * We try to find two points of box
             * - the first with MIN latitude and longitude
             * - the second with MAX latitude and longitude
             * These two points will be used as filtering location for tweets.
             *
             * For additional details see:
             * https://dev.twitter.com/streaming/overview/request-parameters#locations
             * Checked with "New York City" ({-74,40},{-73,41})
             */
            double minLongitude = Double.MAX_VALUE, minLatitude = Double.MAX_VALUE;
            double maxLongitude = -Double.MAX_VALUE, maxLatitude = -Double.MAX_VALUE;
            for (GeoLocation vertex : vertices) {
                minLongitude = Math.min(minLongitude, vertex.getLongitude());
                minLatitude = Math.min(minLatitude, vertex.getLatitude());
                maxLongitude = Math.max(maxLongitude, vertex.getLongitude());
                maxLatitude = Math.max(maxLatitude, vertex.getLatitude());
            }
            double[][] locations = {{minLongitude, minLatitude}, {maxLongitude, maxLatitude}};
            query.locations(locations);
        } catch (TwitterException te) {
            System.err.println("Searching of places has been crashed with error = \"" + te.getMessage() + "\"");
            System.err.println("Search query will be created without place condition.");
        }

        return query;
    }
}
