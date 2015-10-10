import twitter4j.*;

import java.util.Date;
import java.util.List;

/**
 * @since Twitter4J 2.1.7
 */
public class SearchTweets {
    /**
     * Usage: java twitter4j.examples.search.SearchTweets [query]
     *
     * @param args search query
     */
	
	/*java TwitterStream \
    [--query|-q <query or keywords for stream>] \
    [--place|-p <location|'nearby'>] \
    [--stream|-s] \
    [--hideRetweets] \
    [--limit|-l <tweets>] \
    [--help|-h] */
	
	
	public static void searchQuery(Query query, String place, boolean isStream, boolean isHideRetweets, int limit) {
		Twitter twitter = new TwitterFactory().getInstance();
		if(limit != -1) {
			query.setCount(limit);
		}
        try {
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                StringBuilder sb = new StringBuilder();
                for (Status tweet : tweets) {
                	if(tweet.isRetweet() && isHideRetweets) {
                		continue;
                	}
                	Date time = tweet.getCreatedAt();
                	sb.append(time);
                	String userName = tweet.getUser().getScreenName();
                	sb.append("	@" + userName +":	");
                	String text = tweet.getText();
                	if(tweet.isRetweet()) {
                		String retweetedName = tweet.getRetweetedStatus().getUser().getScreenName();
                		sb.append("ретвитнул	@" + retweetedName + ":	");
                		text = tweet.getRetweetedStatus().getText();
                	}
                	sb.append(text);
                	int retweetedCount = tweet.getRetweetCount();
                	if(retweetedCount > 0) {
                		sb.append(" " + retweetedCount + " ретвитов");
                	}
                	System.out.println(sb.toString());
                	sb.setLength(0);
                }
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
	}
    		
    public static void main(String[] args) {
    	String queryName = "";
    	boolean isStream = false;
    	boolean isHideRetweets = false;
    	int limit = -1;
    	for(int i = 0; i < args.length; i++) {
    		if(args[i].equals("-q")) {
    			queryName = args[i+1];
    		} else if(args[i].equals("--hideRetweets")) {
    			isHideRetweets = true;
    		} else if(args[i].equals("-s")) {
    			isStream = true;
    		} else if(args[i].equals("-l")) {
    			limit = Integer.parseInt(args[i+1]);
    		}
    	}
    	Query query = new Query(queryName);
    	searchQuery(query, null, isStream, isHideRetweets, limit);
    	
    }
}