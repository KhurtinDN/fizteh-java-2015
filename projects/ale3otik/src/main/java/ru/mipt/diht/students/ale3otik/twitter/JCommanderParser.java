package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 23.09.15.
 */

import com.beust.jcommander.Parameter;

class JCommanderParser {
    private static final int STANDART_LIMIT = 200;

    @Parameter(names = {"--query", "-q"},
            description = "set query parameters")
    private String query = "";

    @Parameter(names = {"--place", "-p"},
            description = "set location")
    private String location = "";

    @Parameter(names = {"--stream", "-s"},
            description = "set type of action: stream ")
    private boolean stream = false;

    @Parameter(names = {"--hideRetweets"},
            description = "tweets only")
    private boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"},
            description = "set limit on quantity of tweets")
    private int limit = STANDART_LIMIT;

    @Parameter(names = {"--help", "-h"},
            description = "print help manual")
    private boolean help = false;

    public String getQuery() {
        return query;
    }

    public String getLocation() {
        return location;
    }

    public boolean isStream() {
        return stream;
    }

    public boolean isHideRetweets() {
        return hideRetweets;
    }

    public int getLimit() {
        if (limit <= 0) {
            return STANDART_LIMIT;
        }
        return limit;
    }

    public boolean isHelp() {
        return help;
    }

}
/**
 * [--query|-q <query or keywords for stream>] \
 * [--place|-p <location|'nearby'>] \
 * [--stream|-s] \
 * [--hideRetweets] \
 * [--limit|-l <tweets>] \
 * [--help|-h]
 */

