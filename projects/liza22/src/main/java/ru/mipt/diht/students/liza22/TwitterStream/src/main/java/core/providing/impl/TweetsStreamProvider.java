package core.providing.impl;

import config.Arguments;
import config.Constants;
import config.TwitterConfig;
import core.handling.TweetHandler;
import core.providing.TweetsProvider;
import core.quering.FilterQueryBuilder;
import model.Tweet;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Tweets stream provider, provides tweets to TweetHandler.
 *
 * This class implements StatusAdapter and registers itself
 * as StatusListener in TwitterStream.
 * Every time when new status obtained the transformation to
 * internal Tweet object performed, after that this Tweet sent
 * to the handler.
 */
public class TweetsStreamProvider extends StatusAdapter implements TweetsProvider {
    private Configuration configuration;
    private AccessToken accessToken;

    private TwitterStream twitterStream;
    private FilterQuery filterQuery;

    private TweetHandler tweetHandler;

    @Override
    public void init(TwitterConfig twitterConfig) {
        configuration = twitterConfig.getConfiguration();
        accessToken = twitterConfig.getAccessToken();
    }

    @Override
    public void provide(TweetHandler handler) {
        this.tweetHandler = handler;
        connect();
    }

    @Override
    public void onStatus(Status status) {
        Tweet tweet = Tweet.valueOf(status);
        if (Arguments.getInstance().hideRetweets() && tweet.isRetweet()) {
            // skip this tweet
            return;
        }
        tweetHandler.handle(tweet);
    }

    @Override
    public void onException(Exception e) {
        System.err.println("Twitter stream has been occasionally crashed with error: \"" + e.getMessage() + "\"");
        System.err.println("Try to reconnect... [timeout = " + Constants.RECONNECT_TIMEOUT_SECS + " secs]");
        reconnect();
    }

    private void connect() {
        twitterStream = new TwitterStreamFactory(configuration).getInstance(accessToken);
        Twitter twitter = new TwitterFactory(configuration).getInstance();
        filterQuery = new FilterQueryBuilder(twitter).buildQuery();
        // register itself as status listener
        // method 'onStatus' will execute every time when new tweet obtained
        twitterStream.addListener(this);
        twitterStream.filter(filterQuery);
    }

    private void disconnect() {
        if (twitterStream != null) {
            twitterStream.cleanUp();
            twitterStream.shutdown();
            // help GC
            twitterStream = null;
        }
    }

    private void reconnect() {
        // reconnect is performed with timeout between disconnect and new connect
        disconnect();
        timeout(Constants.RECONNECT_TIMEOUT_SECS);
        connect();
    }

    private static void timeout(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
