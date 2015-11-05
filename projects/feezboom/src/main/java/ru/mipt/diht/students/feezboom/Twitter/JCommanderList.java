package ru.mipt.diht.students.feezboom.Twitter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class JCommanderList {

    private static final int TWEETS_LIMIT = 100;
    private JCommander cmd;

    @Parameter(names = { "-q", "--query" },
            description = "Your query words",
            required = true)
    private String query = "";

    @Parameter(names = { "-p", "--place" },
            description = "Where to search ('nearby' is able to use)")
    private String place = "anywhere";

    @Parameter(names = { "-s", "--stream"},
            description = "Streaming mode")
    private Boolean isStream = false;

    @Parameter(names = { "-l", "--limit"},
            description = "Tweets to show(only for non streaming mode)")
    private Integer limit = TWEETS_LIMIT;

    @Parameter(names = "--hideRetweets",
            description = "Hiding Retweets")
    private boolean noRetweets = false;

    @Parameter(names = { "-h", "--help"},
            description = "Help mode", help = true)
    private boolean help = false;

    public final String getQuery() {
        return query;
    }

    public final String getPlace() {
        return place;
    }

    public final Integer getLimit() {
        return limit;
    }

    public final boolean isNoRetweets() {
        return noRetweets;
    }

    public final boolean isStream() {
        return isStream;
    }

    public final boolean isHelp() {
        return help;
    }

    JCommanderList(String[] args) {
        cmd = new JCommander(this, args);
        cmd.setProgramName("TwitterStreamer");
        cmd.setAcceptUnknownOptions(true);
    }

    public final void printHelp() {
        cmd.usage();
    }
}
