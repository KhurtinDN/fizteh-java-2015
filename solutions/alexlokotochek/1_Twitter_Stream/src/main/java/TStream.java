import JCmdParser.Parser;
import com.beust.jcommander.JCommander;
import com.google.code.geocoder.*;
import com.google.code.geocoder.model.*;
import twitter4j.*;
import java.io.*;
import java.net.URL;
import java.util.List;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import static java.lang.System.exit;


public class TStream {
    public static void main (String[] args) {
        Parser jcp = new Parser();
        new JCommander(jcp, args);

        if (jcp.stream) {

            runStream(jcp);

        } else {
            Query query = formQuery(jcp);
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                QueryResult result = twitter.search(query);
                for (Status status : result.getTweets()) {
                    System.out.print((char) 27 + "[34m" + "@" + status.getUser().getScreenName());
                    System.out.println((char) 27 + "[0m" + ": " + status.getText());
                }
            } catch (Exception te) {
                System.out.print("Error in Twitter4j: " + te.getMessage());
            }
        }
    }

    static StatusListener tweetListener = new StatusListener(){
        public void onStatus(Status status) {
            System.out.print((char) 27 + "[34m" + "@" + status.getUser().getScreenName());
            System.out.println((char) 27 + "[0m" + ": " + status.getText());
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
        @Override
        public void onScrubGeo(long arg0, long arg1) {}
        @Override
        public void onStallWarning(StallWarning arg0) {}
    };

    public static void runStream (Parser jcp) {

        System.out.print("I'm in runstream!");
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);

        FilterQuery filterQuery = formFilterQuery(jcp);
        twitterStream.filter(filterQuery);
    }

    private static FilterQuery formFilterQuery (Parser jcp) {
        FilterQuery filterQuery = new FilterQuery();
        if (!"".equals(jcp.query) && !"any".equals(jcp.place)) {
            System.out.println("Twitter API doesn't support streaming with both location and query");
            exit(1);
        }
        if (!"".equals(jcp.query)) {
            String queryString = jcp.query;
            queryString += jcp.hideRetweets ? " +exclude:retweets" : "";
            String[] jcpQueryArray = {queryString};
            filterQuery.track(jcpQueryArray);
        }
        if (!"any".equals(jcp.place)) {
            GeoLocation geo = getGeo(jcp.place);
            double[][] coords = {{geo.getLatitude()-15, geo.getLongitude()-15},
                                 {geo.getLatitude()+15, geo.getLongitude()+15}};
            filterQuery.locations(coords);
        }
        return filterQuery;
    }

    private static Query formQuery (Parser jcp) {
        String queryString = jcp.query;
        queryString += jcp.hideRetweets ? " +exclude:retweets" : "";
        Query query = new Query(queryString);
        query.setCount(jcp.limit);
        if (!"any".equals(jcp.place))
            query.setGeoCode(getGeo(jcp.place), 100, Query.Unit.km);

        return query;
    }

    private static GeoLocation getGeo (String place) {
        if ("nearby".equals(place))
            place = getGeoIP();

        try {
            final Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(place).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> geocoderResult = geocoderResponse.getResults();
            float latitude = geocoderResult.get(0).getGeometry().getLocation().getLat().floatValue();
            float longitude = geocoderResult.get(0).getGeometry().getLocation().getLng().floatValue();
            GeoLocation gl = new GeoLocation(latitude, longitude);
            return gl;
        } catch (Exception ge) {
            System.out.println("Error in Geocoder: " + ge.getMessage());
        }
        return null;
    }

    private static String getGeoIP () {
        try {
            URL url = new URL("http://www.telize.com/geoip");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            JSONObject line = new JSONObject(reader.readLine());
            System.out.println("Your city: " + line.getString("city"));
            return line.getString("city");
        } catch (Exception ipe) {
            System.out.println("Error in ip location: " + ipe.getMessage());
        }
        return null;
    }

}
