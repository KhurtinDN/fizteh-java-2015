package config;

import com.beust.jcommander.Parameter;

import java.util.List;

/**
 * Arguments storage.
 * Used JCommander library.
 *
 * @see <a href="http://jcommander.org/">http://jcommander.org/</a>
 */
public final class Arguments {
    private static final Arguments INSTANCE = new Arguments();

    private Arguments() { }

    public static Arguments getInstance() { return INSTANCE; }

    @Parameter(names = {"--query", "-q"},
            required = true,
            description = "Query or keywords for stream")
    private List<String> keywords;

    @Parameter(names = {"--place", "-p"},
            required = true,
            description = "Location for search tweets")
    private String place;

    @Parameter(names = {"--stream", "-s"},
            description = "Stream mode when tweets printed with delay")
    private boolean streamMode;

    @Parameter(names = "--hideRetweets",
            description = "Hides retweets at all")
    private boolean hideRetweets;

    @Parameter(names = {"--limit", "-l"},
            description = "Limits the number of printed tweets")
    private Integer limitOfTweets = Constants.NO_TWEETS_LIMIT;

    @Parameter(names = {"--help", "-h"},
            help = true,
            description = "Requests the help page")
    private boolean helpRequest;

    @Parameter(names = {"--verbose", "-v"},
            description = "Verbose mode to print more information")
    private boolean verbose;

    /**
     * Array of keywords in case of tweets stream requested.
     * @return array of keywords to be tracked
     */
    public String[] getKeywords() {
        String[] keywordsArray = new String[keywords.size()];
        return keywords.toArray(keywordsArray);
    }

    /**
     * Suppose that query for Search tweets is element with 0 index.
     * @return search tweets query
     */
    public String getQuery() {
        return keywords.get(0);
    }

    public String getPlace() {
        return place;
    }

    public boolean isStreamMode() {
        return streamMode;
    }

    public boolean hideRetweets() {
        return hideRetweets;
    }

    public Integer getLimitOfTweets() {
        return limitOfTweets;
    }

    public boolean isHelpRequest() {
        return helpRequest;
    }

    public boolean isVerboseMode() {
        return verbose;
    }

    @Override
    public String toString() {
        return "Arguments{"
                + "keywords=" + keywords
                + ", place='" + place
                + '\''
                + ", streamMode=" + streamMode
                + ", hideRetweets=" + hideRetweets
                + ", limitOfTweets=" + limitOfTweets
                + ", helpRequest=" + helpRequest
                + ", verboseMode=" + verbose
                + '}';
    }
}
