package ru.mipt.diht.students.glutolik.TwitterStream;

import com.google.maps.model.Geometry;
import twitter4j.*;

import java.util.List;

/**
 * Created by glutolik on 13.12.15.
 */
public class SearchTweets {
    private String keyWord = null;
    private static final int MAX_TRIES = 8;
    private static final int DELAY = 5000;
    private int numberOfTweets;
    private boolean hideRetweets = false;
    private Geometry location = null;

    public SearchTweets(String key, String loc, boolean hide, int limit) throws IllegalArgumentException {
        hideRetweets = hide;
        numberOfTweets = limit;
        keyWord = key;

        if (keyWord == null) {
            throw new IllegalArgumentException("Empty key");
        }

        if (loc != null) {
            location = GeolocationUtils.getCoordinates(loc);
        }
    }


    public final void search() throws InterruptedException, IllegalStateException {
        Twitter twitter = new TwitterFactory().getInstance();

        Query query = new Query();
        query.setQuery(keyWord);
        //query.setCount(100);

        QueryResult result = null;
        for (int tries = 0; tries < MAX_TRIES; tries++) {
            try {
                result = twitter.search(query);
            } catch (TwitterException exept) {
                System.err.println("Couldn't make a request to Twitter.com. The cause is " + exept.getMessage());
                Thread.sleep(DELAY);
            }
        }

        if (result == null) {
            throw new IllegalStateException("Couldn't connect to Twitter.com");
        }

        int number = 0;
        while (query != null && number < numberOfTweets) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (TwitterStreamUtils.check(tweet, location, hideRetweets)) {
                    TwitterStreamUtils.printTweet(tweet, true);
                    number++;
                }
                query = result.nextQuery();
            }
        }
        if (number == 0) {
            System.out.println("Didn't find any tweets");
        }
    }
}
