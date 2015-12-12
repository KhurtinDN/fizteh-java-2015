package ru.mipt.diht.students.andreyzharkov.twitterStream;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Created by Андрей on 09.10.2015.
 */
public class ArgumentsList {
    private static final int DEFAULT_TWEETS_LIMIT = 100;

    @Parameter
    private final List<String> parameters = new ArrayList<String>();

    @Parameter(names = {"-q", "--query"}, description =
            "Keywords for looking for, separating by whitespaces", required = true)
    private String query;

    public final String getQueryString() {
        return query;
    }

    @Parameter(names = {"-p", "--place"}, description =
            "Location of looking for")
    private String place = "nearby";

    public final String getPlace() {
        return place;
    }

    public final void checkLocation() {
        place = TwitterStream.checkLocation(place);
    }

    @Parameter(names = {"-s", "--stream"}, description =
            "Stream mode: every second print new tweets, exit by esc")
    private boolean stream = false;

    public final boolean isStream() {
        return stream;
    }

    @Parameter(names = {"-r", "--hideRetweets"}, description =
            "Don't print any retweeted posts")
    private boolean hideretweets = false;

    public final boolean isRetweetsHidden() {
        return hideretweets;
    }

    @Parameter(names = {"-l", "--limit"}, description =
            "Maximum tweets for out. Only if --stream is disabled")
    private int tweetLimit = DEFAULT_TWEETS_LIMIT;

    public final int getTweetLimit() {
        return tweetLimit;
    }

    @Parameter(names = {"-h", "--help"}, description =
            "Print this page and exit", help = true)
    private boolean help = false;

    public final boolean isHelp() {
        return help;
    }
}
