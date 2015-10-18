package ru.mipt.diht.students.feezboom.Twitter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class JCommanderList {

    private static int tweetsLimit = 100;
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
    private Integer limit = tweetsLimit;

    @Parameter(names = "--hideRetweets",
            description = "Hiding Retweets")
    private boolean noRetweets = false;

    @Parameter(names = { "-h", "--help"},
            description = "Help mode", help = true)
    private boolean help = false;

    public String getQuery() {
        return query;
    }

    public String getPlace() {
        return place;
    }

    public Integer getLimit() {
        return limit;
    }

    public boolean noRetweets() {
        return noRetweets;
    }

    public boolean isStream() {
        return isStream;
    }

    public boolean isHelp() {
        return help;
    }

    JCommanderList(String[] args) {
        cmd = new JCommander(this, args);
        cmd.setProgramName("TwitterStreamer");
        cmd.setAcceptUnknownOptions(true);
    }

    public void getHelp() {
        cmd.usage();
    }
}