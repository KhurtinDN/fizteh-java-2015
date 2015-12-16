package core.providing.impl;

import config.Arguments;
import config.Constants;
import config.TwitterConfig;
import core.handling.TweetHandler;
import core.providing.TweetsProvider;
import core.quering.SearchQueryBuilder;
import model.Tweet;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

/**
 * Tweets by query provider, provides requested tweets to handler.
 *
 * Query is built by SearchQueryBuilder and method 'search' of
 * Twitter instance is invoked.
 *
 * QueryResult contains list of statuses, which transformed to
 * internal Tweet objects, after that these tweets are sent to
 * handler.
 */
public class TweetsByQueryProvider implements TweetsProvider {
    private Twitter twitter;
    private Query query;

    @Override
    public void init(TwitterConfig twitterConfig) {
        twitter = new TwitterFactory(twitterConfig.getConfiguration()).getInstance();
        query = new SearchQueryBuilder(twitter).buildQuery();
    }

    @Override
    public void provide(TweetHandler handler) {
        try {
            QueryResult result = twitter.search(query);
            if (result.getTweets() == null || result.getTweets().isEmpty()) {
                System.out.println("No any tweets by specified query and place found");
            }
            for (Status status : result.getTweets()) {
                // transform status to Tweet object and send to handler
                Tweet tweet = Tweet.valueOf(status);
                if (Arguments.getInstance().hideRetweets() && tweet.isRetweet()) {
                    // skip this tweet
                    continue;
                }
                handler.handle(tweet);
            }
        } catch (TwitterException e) {
            System.err.println("Twitter has been occasionally crashed with error: \"" + e.getMessage() + "\"");
            System.err.println("Try one more time... [timeout = " + Constants.RECONNECT_TIMEOUT_SECS + " secs]");
            timeout(Constants.RECONNECT_TIMEOUT_SECS);
            provide(handler);
        }
    }

    private static void timeout(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
