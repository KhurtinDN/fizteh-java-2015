package ru.mipt.diht.students.pitovsky.twitterstream;

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
            + "is equals '-p " + Main.PLACE_NEARBY + "' - near of your location")
    private String place = Main.PLACE_ANYWHERE;

    @Parameter(names = {"-q", "--query"},
            description = "Necessary to use! Keywords for looking for, separating by whitespaces", required = true)
    private String query;

    @Parameter(names = {"-s", "--stream"},
            description = "Stream mode: every second print new tweets")
    private boolean stream = false;

    @Parameter(names = {"-r", "--hideRetweets"},
            description = "Don't print any retweeted posts")
    private boolean hideRetweets = false;

    @Parameter(names = {"-h", "--help"},
            description = "Print this page and exit", help = true)
    private boolean help = false;

    @Parameter(names = {"-d", "--debug"},
            description = "Print some debug info while working", help = true)
    private boolean debug = false;


    public final int getTweetLimit() {
        return tweetLimit;
    }

    public final String getPlace() {
        return place;
    }

    public final String getQueryString() {
        return query;
    }


    public final boolean isStream() {
        return stream;
    }

    public final boolean isRetweetsHidden() {
        return hideRetweets;
    }

    public final boolean isHelp() {
        return help;
    }

    public final boolean isDebugMode() {
        return debug;
    }
}
