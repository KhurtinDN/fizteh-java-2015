package ru.mipt.diht.students.maxDankow.TwitterStream.solution;

import com.google.maps.model.Geometry;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.GeolocationUtils;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.TwitterStreamUtils;
import twitter4j.*;

import java.util.List;

public class TwitterSearcher {

    // Ограничение количества попыток переподключения.
    private static final int ATTEMPTS_LIMIT = 5;
    private static final int RECONNECT_DELAY_MS = 5000;
    private boolean shouldHideRetweets = false;
    private int tweetsNumberLimit;
    private Geometry locationGeometry = null;
    private String queryText = null;

    public TwitterSearcher(String query, String location,
                           boolean hideRetweets, int tweetsLimit)
            throws NullPointerException {
        shouldHideRetweets = hideRetweets;
        tweetsNumberLimit = tweetsLimit;
        if (location != null) {
            locationGeometry = GeolocationUtils.findLocation(location);
        }
        queryText = query;
        if (queryText == null) {
            throw new NullPointerException("Query is empty.");
        }
    }

    public final void searchTweets() {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        assert queryText != null;
        query.setQuery(queryText);
        query.setCount(tweetsNumberLimit);
        QueryResult result = null;
        // Будем подключаться пока не получится
        for (int attempts = 0; attempts < ATTEMPTS_LIMIT; ++attempts) {
            try {
                result = twitter.search(query);
                break;
            } catch (TwitterException te) {
                System.err.println("Невозможно выполнить запрос к Twitter. Повторная попытка...");
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assert result != null;
        int tweetsCount = 0;
        while (query != null) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (TwitterStreamUtils.checkTweet(tweet, locationGeometry, shouldHideRetweets)) {
                    TwitterStreamUtils.printTweet(tweet, true);
                    tweetsCount++;
                }
            }
            query = result.nextQuery();
            if (tweetsCount >= tweetsNumberLimit) {
                query = null;
            }
        }
        if (tweetsCount == 0) {
            System.out.println("No tweets found.");
        }
    }
}
