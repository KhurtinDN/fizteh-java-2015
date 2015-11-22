package ru.mipt.diht.students.nkarpachev.TwitterStream;

import twitter4j.*;

import java.util.LinkedList;
import java.util.Queue;

public class StreamRunner {

    private static final int SECOND = 1000;

    public static void StreamTweets(String query, boolean doHideRetweets, GeoLocation locale) {
        twitter4j.TwitterStream stream = new TwitterStreamFactory().getInstance();
        Queue<Status> tweetsQueue = new LinkedList<>();

        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                if (!(doHideRetweets) || !(status.isRetweet())) {
                    if (status.getGeoLocation() != null) {
                        GeoLocation tweetLoc = status.getGeoLocation();
                        if (LocationTools.getDistance(tweetLoc, locale) <= LocationTools.locationRadius()) {
                            tweetsQueue.add(status);
                        }
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

        };
        stream.addListener(listener);

        FilterQuery searchQuery = new FilterQuery();
        searchQuery.track(query);

        stream.filter(searchQuery);
        while (true) {
            while (!tweetsQueue.isEmpty()) {
                Status status = tweetsQueue.poll();
                PrintTools.printTweet(status);
            }

            try {
                Thread.sleep(SECOND);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

}
