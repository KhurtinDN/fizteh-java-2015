package ru.mipt.diht.students.nkarpachev.twitterstream;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class JCommanderArgs {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter (names = { "-q" , "--query" }, description = "Query or keywords or stream", required = true)
    private String query;

    @Parameter (names = { "-p" , "--place" }, description = "Location to search tweets; enter name or nearby keyword")
    private String location;

    @Parameter (names = { "-s" , "--stream" }, description = "Enter stream mode")
    private boolean isStream = false;

    @Parameter (names = { "--hideRetweets"}, description = "Filter retweets")
    private boolean doHideRetweets = false;

    private static final int MAX_TWEETS = 100;

    @Parameter (names = { "-l", "--limit" }, description = "Maximal number of tweets to print")
    private int tweetsLimit = MAX_TWEETS;

    @Parameter (names = { "-h", "--help"}, description = "Print help page", help = true)
    private boolean printHelp = false;

    public final String getQuery() {
        return query;
    }

    public final String getLocation() {
        return location;
    }

    public final boolean isStream() {
        return isStream;
    }

    public final boolean hideRetweets() {
        return doHideRetweets;
    }

    public final int getTweetsLimit() {
        return tweetsLimit;
    }

    public final boolean printHelp() {
        return printHelp;
    }
}
