package config;

/**
 * Application constants storage.
 */
public final class Constants {
    /**
     * Reconnect timeout to twitter in seconds .
     */
    public static final int RECONNECT_TIMEOUT_SECS = 10;
    /**
     * Default value of limit argument.
     */
    public static final int NO_TWEETS_LIMIT = -1;
    /**
     * Delay between two printing of tweets.
     */
    public static final int PRINT_TWEET_DELAY_SECS = 1;
    /**
     * Message which is printed in verbose mode when no any tweet to print for stream.
     */
    public static final String NO_TWEET_MESSAGE = "...";
    /**
     * Name of resource - twitter config file.
     */
    public static final String TWITTER_CONFIG_FILE = "twitter.cfg";
    /**
     * Name of resource - help content file.
     */
    public static final String HELP_FILE = "help.txt";
}
