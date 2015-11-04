package ru.mipt.diht.students.maxDankow.TwitterStream.solution;

import com.google.maps.model.Geometry;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.*;
import twitter4j.*;

import java.util.LinkedList;

public class TwitterStreamer {
    // Очередь для твиттов в режиме stream.
    private static LinkedList<Status> streamQueue;
    // Используется для ограничения сильного разрастания очереди твиттов в режиме stream.
    private static final int STREAM_TWEETS_LIMIT = 10000;
    private static final int STREAM_DELAY_MS = 1000;
    private boolean shouldHideRetweets = false;
    private int tweetsNumberLimit;
    private Geometry locationGeometry = null;
    private String queryText[] = null;

    public TwitterStreamer(String query, String location,
                           boolean hideRetweets, int tweetsLimit)
            throws NullPointerException {
        shouldHideRetweets = hideRetweets;
        tweetsNumberLimit = tweetsLimit;
        if (location != null) {
            locationGeometry = GeolocationUtils.findLocation(location);
        }
        if (query == null) {
            throw new NullPointerException("Query is empty.");
        }
        queryText = new String[]{query};
    }

    public void startStream() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(statusAdapter);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(queryText);
        streamQueue = new LinkedList<>();
        twitterStream.filter(filterQuery);
        while (true) {
            while (!streamQueue.isEmpty()) {
                Status tweet = streamQueue.poll();
                TwitterStreamUtils.printTweet(tweet, false);
                try {
                    Thread.sleep(STREAM_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(STREAM_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private StatusAdapter statusAdapter = new StatusAdapter() {
        public void onStatus(Status tweet) {
            if (TwitterStreamUtils.checkTweet(tweet, locationGeometry, shouldHideRetweets) &&
                    streamQueue.size() < STREAM_TWEETS_LIMIT) {
                streamQueue.add(tweet);
            }
        }
    };
}
