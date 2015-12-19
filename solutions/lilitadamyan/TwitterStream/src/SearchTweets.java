import twitter4j.*;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
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
	
	public static void searchQuery(Query query, boolean isStream, boolean isHideRetweets, int limit) {
		Twitter twitter = new TwitterFactory().getInstance();

        try {
            QueryResult result;
            List<String> tweetsToPrint = new ArrayList<>();
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
                	if(isStream){
                		System.out.println(sb.toString());
                	} else if (limit != tweetsToPrint.size()) {
                		tweetsToPrint.add(sb.toString());
                	} else {
                		break;
                	}
                	sb.setLength(0);
                }
                if(!isStream && limit == tweetsToPrint.size()) {
                	break;
                }
                
            } while ((query = result.nextQuery()) != null);
            
            if(tweetsToPrint.size() != 0) {
            	for(int i = 0; i < tweetsToPrint.size(); i++) {
            		System.out.println(tweetsToPrint.get(i));
            	}
            }
            
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
	}
	
	/*java TwitterStream \
    [--query|-q <query or keywords for stream>] \
    [--place|-p <location|'nearby'>] \
    [--stream|-s] \
    [--hideRetweets] \
    [--limit|-l <tweets>] \
    [--help|-h] */
    		
    public static void main(String[] args) {
    	try{
    		String queryName = "";
        	boolean isStream = false;
        	boolean isHideRetweets = false;
        	int limit = -1;
        	for(int i = 0; i < args.length; i++) {
        		if(args.length == 0) {
        			throw new IllegalArgumentException("no argument is specified. Valid arguments:");
        		} else if(args.length > 7) {
        			throw new IllegalArgumentException("too many arguments. Valid arguments:");
        		}
        		if(args[i].equals("-q")) {
        			queryName = args[i+1];
        		} else if(args[i].equals("--hideRetweets")) {
        			isHideRetweets = true;
        		} else if(args[i].equals("-s")) {
        			isStream = true;
        		} else if(args[i].equals("-l")) {
        			limit = Integer.parseInt(args[i+1]);
        		} else if(args[i].equals("-h")) {
        			System.out.println("java TwitterStream "
					+ "[--query|-q <query or keywords for stream>]"
					+ "[--stream|-s] [--hideRetweets] "
					+ "[--limit|-l <tweets>]");
        		}
        		if(queryName.isEmpty()) {
        			throw new IllegalArgumentException("no query is specified. Valid arguments:");
        		}      		        		
        	}
        	Query query = new Query(queryName);
        	searchQuery(query, isStream, isHideRetweets, limit);
    	} catch(IllegalArgumentException e) {
    		System.out.println(e);
    		System.out.println("java TwitterStream "
					+ "[--query|-q <query or keywords for stream>]"
					+ "[--stream|-s] [--hideRetweets] "
					+ "[--limit|-l <tweets>]");
    	}
    }
}
