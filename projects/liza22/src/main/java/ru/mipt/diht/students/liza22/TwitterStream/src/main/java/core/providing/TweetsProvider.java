package core.providing;

import config.TwitterConfig;
import core.handling.TweetHandler;

/**
 * Tweets provider interface.
 */
public interface TweetsProvider {

    void init(TwitterConfig twitterConfig);

    void provide(TweetHandler handler);
}
