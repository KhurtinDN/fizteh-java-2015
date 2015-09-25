package JCmdParser;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names = { "-q", "--query" }, description = "Query to search")
    public String query = "";

    @Parameter(names = { "-p", "--place" }, description = "Location to search, -all- if everywhere")
    public String place = "any";

    @Parameter(names = { "-s", "--stream" }, description = "Stream mode")
    public boolean stream = false;

    @Parameter(names = "--hideRetweets", description = "Hide retweets mode")
    public boolean hideRetweets = false;

    @Parameter(names = { "-l", "--limit" }, description = "Limit of tweets displayed, 0 if no limits")
    public int limit = 100;
}
