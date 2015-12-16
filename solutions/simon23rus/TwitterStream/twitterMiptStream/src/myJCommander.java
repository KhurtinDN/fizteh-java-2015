/**
 * Created by semenfedotov on 22.09.15.
 */
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/*  [--query|-q <query or keywords for stream>] \
    [--place|-p <location|'nearby'>] \
    [--stream|-s] \
    [--hideRetweets] \
    [--limit|-l <tweets>] \
    [--help|-h]
*/


public class myJCommander {

        @Parameter
        private List<String> parameters = new ArrayList<>();

        @Parameter(names = "--post", description = "Just to post some tweets")
        public String toPost;


        @Parameter(names = { "--query", "-q" }, description = "Parameters 4 query search")
        public String query;

        public String[] getClearQuery() {
            if(query == null) {
                String[] emptyString = {};
                return emptyString;
            }
            return query.split("\\s");
        }

        @Parameter(names = { "--place", "-p" }, description = "Places what R U looking 4")
        public String place = "aroundtheworld";

        @Parameter(names = {"--stream", "-s"}, description = "To print smth with 1sec delay")
        public boolean stream = false;

        @Parameter(names = "--hideRetweets", description = "thing which just hide Retweets")
        public boolean hideRetweets = false;

        @Parameter(names = {"--limit", "-l"}, description = "How many tweets to show")
        public   int tweetsByQuery = 100;


        @Parameter(names = { "--help", "-h"}, description = "Just piece of help 4 User", help = true)
        public   boolean help = false;
}
