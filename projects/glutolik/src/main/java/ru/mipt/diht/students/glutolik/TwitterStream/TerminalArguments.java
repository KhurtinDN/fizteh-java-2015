package ru.mipt.diht.students.glutolik.TwitterStream;

import com.beust.jcommander.Parameter;

/**
 * Created by glutolik on 13.12.15.
 */
public class TerminalArguments {

    private static final int DEFAULT_LIMIT = 25;

    @Parameter(names = {"--query", "-q"}, description = "Query or keywords for stream")
    private String keyWord = null;

    @Parameter(names = {"--place", "-p"}, description = "Where do you want to find tweets from")
    private String location = null;

    @Parameter(names = {"--stream", "-s"}, description = "Show new tweets")
    private boolean streamMode = false;

    @Parameter(names = {"--hideRetweets"}, description = "Hides retweets")
    private boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"}, description = "Number of test will be shown")
    private int limit = DEFAULT_LIMIT;

    @Parameter(names = {"-h", "--help"}, description = "print help page", help = true)
    private boolean help = false;

    public final String getKeyWord() {
        return keyWord;
    }

    public final String getLocation() {
        return location;
    }

    public final boolean isStream() {
        return streamMode;
    }

    public final boolean isHideRetweets() {
        return hideRetweets;
    }

    public final int getLimit() {
        return limit;
    }

    public final boolean isHelp() {
        return help;
    }
}

