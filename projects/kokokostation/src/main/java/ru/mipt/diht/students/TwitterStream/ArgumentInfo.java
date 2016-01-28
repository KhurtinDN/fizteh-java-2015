package ru.mipt.diht.students.TwitterStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Created by mikhail on 16.12.15.
 */

public class ArgumentInfo {
    public static final int NO_LIMIT = -1;

    @Parameter (names = {"--query", "-q"})
    private String query = "";
    @Parameter (names = {"--place", "-p"})
    private String place = "";
    @Parameter (names = {"--stream", "-s"})
    private boolean stream;
    @Parameter (names = "--hideRetweets")
    private boolean hideRetweets;
    @Parameter (names = {"--help", "-h"})
    private boolean help;
    @Parameter (names = {"--limit", "-l"})
    private int limit = NO_LIMIT;
    @Parameter (names = {"--nearby", "-n"})
    private boolean nearby;

    public ArgumentInfo(String[] args) {
        try {
            new JCommander(this, args);
        } catch (ParameterException e) {
            System.err.println("Wrong parameter: " + e.getMessage());
            System.exit(0);
        }
    }

    public int getLimit() {
        return limit;
    }

    public String getQuery() {
        return query;
    }

    public String getPlace() {
        return place;
    }

    public boolean isNearby() {
        return nearby;
    }

    public boolean isStream() {
        return stream;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public boolean isHelp() {
        return help;
    }
}
