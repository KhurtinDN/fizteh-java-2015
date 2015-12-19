package ru.mipt.diht.students.annnvl.TwitterStream;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;

public class Parser {
    private static final int STANDART_LIMIT = 100;
    @Parameter(names = {"-l", "--limit"},
            validateWith = PositiveInteger.class,
            description = "Number of tweets to show(only for no streaming mode)")
    private int limit = STANDART_LIMIT;

    @Parameter(names = {"-s", "--stream"}, description = "Stream mode")
    private boolean stream = false;
    @Parameter(names = {"--hideRetweets"}, description = "Ignore retweets")
    private boolean hideRetweets = false;

    @Parameter(names = { "-h", "--help"}, description = "Help mode", help = true)
    private boolean help = false;

    @Parameter(names = {"-p", "--place"}, description = "Location or 'nearby'")
    private String place = "";

    @Parameter(names = {"-q", "--query"}, description = "Query or keywords for stream", required = true)
    private String query = "";

    public final String getQuery() {
        return query;
    }
    public final String getPlace() {
        return place;
    }
    public final Integer getLimit() {
        return limit;
    }
    public final boolean isStream() {
        return stream;
    }
    public final boolean isHideRetwitts() {
        return hideRetweets;
    }
    public final boolean isHelp() {
        return help;
    }
}
