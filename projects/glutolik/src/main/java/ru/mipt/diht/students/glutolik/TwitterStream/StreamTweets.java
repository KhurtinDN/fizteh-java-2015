package ru.mipt.diht.students.glutolik.TwitterStream;

import com.google.maps.model.Geometry;
import twitter4j.*;

import java.util.LinkedList;


/**
 * Created by glutolik on 13.12.15.
 */
public class StreamTweets {

    private String[] keyWord = null;
    private LinkedList<Status> streamQueue;
    private static final int DELAY = 1000;
    private static final int LIMIT = 25;
    private boolean hideRetweets = false;
    private Geometry location = null;

    public StreamTweets(String key, String loc, boolean hide) throws IllegalArgumentException {
        if (location != null) {
            location = GeolocationUtils.getCoordinates(loc);
        }
        if (key == null) {
            throw new IllegalArgumentException("Key shouldn't be empty");
        }
        keyWord = new String[]{key};
        hideRetweets = hide;
    }

    public final void beginStream() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        twitterStream.addListener(statusAdapter);

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(keyWord);

        twitterStream.filter(filterQuery);
        streamQueue = new LinkedList<>();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            synchronized (streamQueue) {
                if (!streamQueue.isEmpty()) {
                    Status tweet = streamQueue.poll();
                    TwitterStreamUtils.printTweet(tweet, false);
                }
            }
        }
    }

    private StatusAdapter statusAdapter = new StatusAdapter() {
        public void onStatus(Status tweet) {
            if (streamQueue.size() < LIMIT && TwitterStreamUtils.check(tweet, location, hideRetweets)) {
                synchronized (streamQueue) {
                    streamQueue.add(tweet);
                }
            }
        }
    };
}
