/**
 * Created by admin on 27.09.2015.
 */
//parsing using by jcommander
import com.beust.jcommander.Parameter;
public class Parameters {
    @Parameter(names = {"-l", "--limit"},
            description =
                    "Number of tweets to show(only for no streaming mode)")
    private Integer limit = Integer.MAX_VALUE;

    @Parameter(names = {"-s", "--stream"},
            description = "Stream")
    private boolean isStream = false;

    @Parameter(names = {"--hideRetweets"},
            description = "HideRetweets")
    private boolean hideRetweets = false;

    @Parameter(names = { "-h", "--help"},
            description = "Help mode", help = true)
    private boolean help = false;

    @Parameter(names = {"-p", "--place"},
            description = "location or 'nearby'")
    private String place = "";

    @Parameter(names = {"-q", "--query"},
            description = "Query or keywords for stream", required = true)
    private String query = "";

    public final Boolean isStream() {
        return isStream;
    }

    public final boolean hideRetweets() {
        return hideRetweets;
    }

    public final Integer getLimit() {
        return limit;
    }

    public final boolean isHelp() {
        return help;
    }

    public final String getLocation() {
        return place;
    }
    public final String getQuery() {
        return query;
    }
}

