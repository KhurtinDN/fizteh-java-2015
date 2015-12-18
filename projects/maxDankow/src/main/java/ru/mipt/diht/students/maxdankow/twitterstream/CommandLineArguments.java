package ru.mipt.diht.students.maxdankow.twitterstream;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
    private static final int DEFAULT_TWEETS_NUMBER_LIMIT = 100;

    @Parameter(names = {"--query", "-q"}, description = "Query or keywords to search")
    private String queryText = null;

    @Parameter(names = {"--place", "-p"}, description = "Location of search")
    private String locationName = null;

    @Parameter(names = {"--stream", "-s"}, description = "Shows new tweets every second")
    private boolean streamMode = false;

    @Parameter(names = {"--hideRetweets"}, description = "Hides retweets")
    private boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"}, description = "Sets a limit to number of shown tweets")
    private int tweetsNumberLimit = DEFAULT_TWEETS_NUMBER_LIMIT;

    @Parameter(names = {"-h", "--help"}, description = "Print this page and exit", help = true)
    private boolean help = false;

    public final String getQueryText() {
        return queryText;
    }

    public final String getLocationName() {
        return locationName;
    }

    public final boolean isStreamMode() {
        return streamMode;
    }

    public final boolean shouldHideRetweets() {
        return hideRetweets;
    }

    public final int getTweetsNumberLimit() {
        return tweetsNumberLimit;
    }

    public final boolean isHelp() {
        return help;
    }
}
