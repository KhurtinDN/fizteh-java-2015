package ru.mipt.diht.students.twitterstream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Created by mikhail on 16.12.15.
 */

public class ArgumentInfo {
    public static final int NO_LIMIT = -1;

    @Parameter (names = {"--query", "-q"}, required = true, description = "Search keywords")
    private String query;
    @Parameter (names = {"--place", "-p"}, description = "Location to search")
    private String place = "";
    @Parameter (names = {"--stream", "-s"}, description = "Stream mode")
    private boolean stream;
    @Parameter (names = "--hideRetweets", description = "Set if you want to hide retweets")
    private boolean hideRetweets;
    @Parameter (names = {"--help", "-h"}, description = "Set if you want to read help", help = true)
    private boolean help;
    @Parameter (names = {"--limit", "-l"}, description =
            "Accepts maximum number of tweets to display; ignored in stream mode")
    private int limit = NO_LIMIT;
    @Parameter (names = {"--nearby", "-n"}, description = "Location for search is set according to your IP")
    private boolean nearby;

    private final JCommander jCommander = new JCommander(this);

    private ArgumentInfo() {}

    public ArgumentInfo(String... args) throws ParameterException {
        jCommander.parse(args);
    }

    public static String getHelp() {
        StringBuilder result = new StringBuilder();
        new ArgumentInfo().jCommander.usage(result);

        return result.toString();
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
