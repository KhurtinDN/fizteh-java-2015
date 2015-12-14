package ru.mipt.diht.students.sopilnyak.moduletests.library;

import twitter4j.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Results {

    public static final int MAX_TWEETS = 100;
    public static final int RADIUS = 10;
    public static final int ATTEMPTS = 20;
    public static final int SLEEP_TIME = 1000;

    private static GeoQuery geoQuery;
    private static Query query;

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

    public static ArrayList<String> printResults() throws UnknownLocationException, TwitterException {
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
                    while (true) {
                        QueryResult result = twitter.search(query);
                        for (Status status : result.getTweets()) {
                            if (!Format.getResultsStreamNotEnabled(status).isEmpty()) {
                                tweetsList.add(Format.getResultsStreamEnabled(status));
                            }
                            try {
                                Thread.sleep(SLEEP_TIME); // sleep for 1 second
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (!result.getTweets().isEmpty()) {
                            Status status = result.getTweets().get(0);
                            query.setSinceId(status.getId());
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
            }
        }
        return true;
    }
}
