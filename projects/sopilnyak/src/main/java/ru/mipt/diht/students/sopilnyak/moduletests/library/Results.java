package ru.mipt.diht.students.sopilnyak.moduletests.library;

import twitter4j.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Results {

    public static final int MAX_TWEETS = 100;
    public static final int RADIUS = 10;
    public static final int ATTEMPTS = 20;
    public static final int SLEEP_TIME = 1000;
    public static final int QUEUE_SIZE = 1000;
    public static final double RADIUS_COORDINATES = 0.1;

    private static GeoQuery geoQuery;
    private static Query query;
    private static ArrayBlockingQueue<Status> tweetsQueue;
    private static double[][] boundingBox = null;

    public static void sendQuery(String queryString) throws UnknownHostException {
        query = new Query(queryString);

        String ip = Inet4Address.getLocalHost().getHostAddress();
        geoQuery = new GeoQuery(ip);

        query.setCount(MAX_TWEETS); // max number of tweets
        if (Arguments.getLimit() != -1) {
            query.setCount(Arguments.getLimit());
        }
    }

    public static void sendGeoQuery(String qeoQueryString) {
        geoQuery.setQuery(qeoQueryString);
    }

    public static ArrayList<String> printResults(String queryString) throws UnknownLocationException, TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        int attempts = 0;
        ArrayList<String> tweetsList = new ArrayList<>(); // result list

        while (true) {
            try {
                locationHandler(twitter);

                if (!Arguments.getIsStreamEnabled()) {
                    QueryResult result = twitter.search(query);
                    for (Status status : result.getTweets()) {
                        if (!Format.getResultsStreamNotEnabled(status).isEmpty()) {
                            tweetsList.add(Format.getResultsStreamNotEnabled(status));
                        }
                    }

                    if (result.getTweets().isEmpty()) {
                        throw new TwitterException("Nothing was found");
                    }

                } else {
                    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
                    tweetsQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
                    twitterStream.addListener(listener);

                    FilterQuery filterQuery = new FilterQuery();
                    if (queryString != null) {
                        filterQuery.track(new String[]{queryString});
                    } else {
                        filterQuery.track(new String[]{""}); // empty query
                    }
                    if (boundingBox != null) {
                        filterQuery.locations(boundingBox);
                    }

                    twitterStream.filter(filterQuery);
                    while (true) {
                        while (!tweetsQueue.isEmpty()) {
                            Status status = tweetsQueue.poll();
                            Format.getResultsStreamEnabled(status);
                        }
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                break; // no need to try again

            } catch (TwitterException e) {
                if (e.isCausedByNetworkIssue()) {
                    try {
                        Thread.sleep(SLEEP_TIME); // sleep for 1 second
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    // try again
                    if (++attempts == ATTEMPTS) {
                        throw new TwitterException("Network problem"); // network problem
                    }
                } else { // throw other exceptions further
                    throw new TwitterException(e.getMessage());
                }
            }
        }
        return tweetsList;
    }

    private static StatusListener listener = new StatusAdapter() {
        @Override
        public void onStatus(Status status) {
            tweetsQueue.add(status);
        }
    };

    public static boolean locationHandler(Twitter twitter) throws TwitterException, UnknownLocationException {
        if (geoQuery.getQuery() != null) {
            ResponseList<Place> places =
                    twitter.searchPlaces(geoQuery);

            if (places.size() == 0) {
                throw new UnknownLocationException(); // unknown location

            } else {
                Place place = places.get(0); // get first place
                // search in radius by coordinates
                query.setGeoCode(place.
                                getBoundingBoxCoordinates()[0][0],
                        RADIUS, Query.KILOMETERS);

                boundingBox = new double[2][2];
                boundingBox[0][0] = place.getBoundingBoxCoordinates()[0][0].getLatitude() - RADIUS_COORDINATES;
                boundingBox[0][1] = place.getBoundingBoxCoordinates()[0][0].getLongitude() - RADIUS_COORDINATES;
                boundingBox[1][0] = place.getBoundingBoxCoordinates()[0][0].getLatitude() + RADIUS_COORDINATES;
                boundingBox[1][1] = place.getBoundingBoxCoordinates()[0][0].getLongitude() + RADIUS_COORDINATES;
            }
        }
        return true;
    }
}
