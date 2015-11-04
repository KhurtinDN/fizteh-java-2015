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


public class MyJCommander {
        private static final int MAX_TWEETS_NUMBER = 100;

        @Parameter(names = "--post", description = "Just to post some tweets")
        public String toPost;


        @Parameter(names = { "--query", "-q" }, description = "Parameters for query search")
        public String query;

        public String[] getClearQuery() {
            if(query == null) {
                String[] emptyString = {};
                return emptyString;
            }
            return query.split("\\s");
        }

        @Parameter(names = { "--place", "-p" }, description = "Places what are you looking for")
        public String place = "aroundtheworld";

        @Parameter(names = {"--stream", "-s"}, description = "To print smth with 1sec delay")
        public boolean stream = false;

        @Parameter(names = "--hideRetweets", description = "Thing which just hide Retweets")
        public boolean hideRetweets = false;

        @Parameter(names = {"--limit", "-l"}, description = "How many tweets to show")
        public   int tweetsByQuery = MAX_TWEETS_NUMBER;


        @Parameter(names = { "--help", "-h"}, description = "Just piece of help 4 User", help = true)
        public   boolean help = false;
}
