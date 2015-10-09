package ru.mipt.diht.students.maxDankow.TwitterStream;
import com.beust.jcommander.Parameter;
import java.util.List;
import java.util.ArrayList;

public class JComanderArgsList {
    private static final int DEFAULT_TWEETS_NUMBER_LIMIT = 100;

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = { "--query", "-q" }, description = "Query or keywords to search")
    private String queryText;

    @Parameter(names = { "--place", "-p" }, description = "Location of search (or nearby - near your position)")
    private String locationStr = null;

    @Parameter(names = { "--stream", "-s" }, description = "Shows new tweets every second")
    private boolean streamMode = false;

    @Parameter(names = { "--hideRetweets" }, description = "Hides retweets")
    private boolean hideRetweets = false;

    @Parameter(names = { "--limit", "-l" }, description = "Sets a limit to number of shown tweet")
    private int tweetNumberLimit = DEFAULT_TWEETS_NUMBER_LIMIT;

    @Parameter(names = {"-h", "--help"}, description = "Print this page and exit", help = true)
    private boolean help = false;

    public final String getQueryText() {
        return queryText;
    }

    public final String getLocationStr() {
        return locationStr;
    }

    public final boolean isStreamMode() {
        return streamMode;
    }

    public final boolean shouldHideRetweets() {
        return hideRetweets;
    }

    public final int getTweetsNumberLimit() {
        return tweetNumberLimit;
    }

    public final boolean isHelp() {
        return help;

    }
}
