package ru.mipt.diht.students.maxDankow.TwitterStream;
import com.beust.jcommander.JCommander;
import com.google.maps.model.Geometry;
import twitter4j.*;

import java.util.LinkedList;
import java.util.List;

public class TwitterStreamClient {

    public static void main(String[] args) {
        JComanderArgsList jCmdArgs = new JComanderArgsList();
        JCommander jCommander = new JCommander(jCmdArgs, args);
        if (jCmdArgs.isHelp()) {
            jCommander.usage();
            return;
        }
        String searchLocation = jCmdArgs.getLocationStr();
        if (searchLocation != null) {
            placeGeometry = TwitterStreamUtils.findLocation(searchLocation);
        }
        String queryText = jCmdArgs.getQueryText();
        if (jCmdArgs.isHideRetweets() && queryText != null) {
            queryText = queryText.concat(" -filer:retweets");
        }

        if (jCmdArgs.isStreamMode()) {
            //todo: make more beautiful
            String[] queryArray = new String[1];
            queryArray[0] = queryText;
            startTwitterStreaming(queryArray, searchLocation);
        } else {
            searchTweets(queryText, searchLocation, jCmdArgs.getTweetsNumberLimit());
        }
        System.exit(0);
    }

    private static final int STREAM_DELAY_MS = 1000;
    private static LinkedList<Status> streamQueue;
    private static Geometry placeGeometry = null;

    private static StatusListener tweetListener = new StatusListener() {
        public void onStatus(Status tweet) {
            if (checkLocation(tweet.getPlace())) {
                streamQueue.add(tweet);
            }
        }

        public void onDeletionNotice(StatusDeletionNotice statusDN) {
        }

        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        }

        public void onException(Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onScrubGeo(long arg0, long arg1) {
        }

        @Override
        public void onStallWarning(StallWarning arg0) {
        }
    };

    private static void startTwitterStreaming(String[] queryText, String searchLocation) {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(queryText);
        /*if (searchLocation != null) {
            Geometry placeGeometry = TwitterStreamUtils.findLocation(searchLocation);
            double[][] locations = new double[2][2];
            locations[0][0] = placeGeometry.bounds.southwest.lat;
            locations[0][1] = placeGeometry.bounds.southwest.lng;
            locations[1][0] = placeGeometry.bounds.northeast.lat;
            locations[1][1] = placeGeometry.bounds.northeast.lng;
            filterQuery.locations(locations);
        }*/
        streamQueue = new LinkedList<>();
        twitterStream.filter(filterQuery);
        while (true) {
            while (!streamQueue.isEmpty()) {
                Status tweet = streamQueue.poll();
                printTweet(tweet, false);
            }
            try {
                Thread.sleep(STREAM_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final double LOCATION_SEARCH_RADIUS_KM = 15;

    private static void searchTweets(String queryText, String searchLocation, int tweetsNumberLimit) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        if (queryText != null) {
            query.setQuery(queryText);
        }
        /*if (searchLocation != null) {
            Geometry placeGeometry = TwitterStreamUtils.findLocation(searchLocation);
            GeoLocation geoLocation = new GeoLocation(placeGeometry.location.lat, placeGeometry.location.lng);
            query.setGeoCode(geoLocation, LOCATION_SEARCH_RADIUS_KM, Query.Unit.km);
        }*/
        query.setCount(tweetsNumberLimit);
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException te) {
            System.err.println(te.getMessage());
            System.exit(te.getErrorCode());
        }
        int tweetsCount = 0;
        while (query != null) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (checkLocation(tweet.getPlace())) {
                    printTweet(tweet, true);
                    tweetsCount++;
                }
            }
            query = result.nextQuery();
            if (tweetsCount >= tweetsNumberLimit) {
                query = null;
            }
        }
        if (tweetsCount == 0)
        {
            System.out.println("No tweets found.");
        }
    }

    private static void printTweet(Status tweet, boolean shouldShowTime) {
        if (shouldShowTime) {
            System.out.print("["
                    + TwitterStreamUtils.convertTimeToRussianWords(tweet.getCreatedAt())
                    + "] ");
        }
        if (!tweet.isRetweet()) {
            int retweetCount = tweet.getRetweetCount();

            System.out.println("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: "
                    + tweet.getText());
            if (retweetCount > 0) {
                System.out.print(" ("
                        + tweet.getRetweetCount()
                        + " ретвитов)");
            }
            System.out.println();
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            System.out.println("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: ретвитнул \033[34m@"
                    + originalTweet.getUser().getScreenName()
                    + "\033[0m: "
                    + originalTweet.getText());
        }
    }

    private static boolean checkLocation(Place place) {
        if (placeGeometry == null) {
            return true;
        }
        if (place == null) {
            return false;
        }
        GeoLocation[][] geoLocations = place.getBoundingBoxCoordinates();
        double southwestLat = geoLocations[0][0].getLatitude();
        double southwestLng = geoLocations[0][1].getLongitude();
        double northeastLat = geoLocations[0][2].getLatitude();
        double northeastLng = geoLocations[0][3].getLongitude();
        double centerLat = (southwestLat + northeastLat) / 2;
        double centerLng = (southwestLng + northeastLng) / 2;
        return (centerLat > placeGeometry.bounds.southwest.lat && centerLat < placeGeometry.bounds.northeast.lat
                && centerLng > placeGeometry.bounds.southwest.lng && centerLng < placeGeometry.bounds.northeast.lng);
    }
}
