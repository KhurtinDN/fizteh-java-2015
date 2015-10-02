package ru.fizteh.fivt.pitovsky.twitterstream;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

public class JCommanderList {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    private static final int MAX_TWEET_LIMIT = 300;

    @Parameter(names = { "-l", "--limit" },
            description = "Maximum tweets count. Only if --stream is disabled")
    private int tweetLimit = MAX_TWEET_LIMIT;

    @Parameter(names = {"-p", "--place"},
            description = "Location of looking for, without args"
            + "is equals nearby - near of your location")
    private String place = "anywhere";

    @Parameter(names = {"-q", "--query"},
            description = "Keywords for looking for, separating by whitespaces")
    private String query;

    @Parameter(names = {"-s", "--stream"},
            description = "Stream mode: every second print new tweets")
    private boolean stream = false;

    @Parameter(names = {"-r", "--hideRetweets"},
            description = "Don't print any retweeted posts")
    private boolean hideretweets = false;

    @Parameter(names = {"-h", "--help"},
            description = "Print this page and exit", help = true)
    private boolean help = false;


    public final int getTweetLimit() {
        return tweetLimit;
    }

    public final String getPlace() {
        return place;
    }

    public final String getQueryString() {
        return query;
    }

    public final String[] getQuery() {
        if (query == null) {
            String[] emptyarr = {};
            return emptyarr;
        }
        return query.split("[\\s,.]+");
    }

    public final boolean isStream() {
        return stream;
    }

    public final boolean isRetweetsHidden() {
        return hideretweets;
    }

    public final boolean isHelp() {
        return help;
    }
}
