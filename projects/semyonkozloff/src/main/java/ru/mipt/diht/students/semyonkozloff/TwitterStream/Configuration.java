package ru.mipt.diht.students.semyonkozloff.TwitterStream;

import com.beust.jcommander.Parameter;

public final class Configuration {

    @Parameter(names = { "--query", "-q" },
            description = "Search tweets for a query.", required = true)
    private String query = null;

    public String getQuery() {
        return query;
    }

    @Parameter(names = { "--place", "-p" },
            description = "Search for a place.")
    private String location = null;

    public String getLocation() {
        return location;
    }

    @Parameter(names = { "--stream", "-s" },
            description = "Run a stream."
                    + "The app will print tweets with 1 second delay.")
    private boolean isStream = false;

    public boolean isStream() {
        return isStream;
    }

    @Parameter(names = { "--hideRetweets", "-r" },
            description = "Filter retweets.")
    private boolean shouldHideRetweets = false;

    public boolean shouldHideRetweets() {
        return shouldHideRetweets;
    }

    private static final int DEFAULT_LIMIT = 16;

    @Parameter(names = { "--limit", "-l" },
            description = "Print only LIMIT tweets.")
    private int limit = DEFAULT_LIMIT;

    public int getLimit() {
        return limit;
    }

    @Parameter(names = { "--help", "-h" },
            description = "Print help.", help = true)
    private boolean isHelp = false;

    public boolean isHelp() {
        return isHelp;
    }
}
