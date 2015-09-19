package main.java.ru.fizteh.fivt.pitovsky;

import java.util.List;

import com.beust.jcommander.JCommander;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * 
 * @author Peter Pitovsky
 *
 */
public class Main {
	
	public static void main(String[] args) {
		String[] argstmp = {"-p", "urkaloc", "-q", "NY", "--limit", "15", "-r"}; 
		JCommanderList jcl = new JCommanderList();
		JCommander jcomm = new JCommander(jcl, argstmp);
		
		if (jcl.isHelp()) {
			System.out.println("This program can print in stdout some tweets, filtered by options:");
			jcomm.usage();
			return;
		}
		String[] rquery = jcl.getQuery();
		System.out.println(jcl.getPlace() + " and " + jcl.getTweetLimit() + "; " + jcl.isStream());
		for (int i = 0; i < rquery.length; ++i)
			System.out.println(rquery[i]);
		
		Twitter twitter = new TwitterFactory().getInstance();
        try {
            Query query = new Query(jcl.getQueryString());
            QueryResult result;
            int count = 0;
            while (query != null) {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                	if (!jcl.isRetweetsHidden() || !tweet.isRetweet()) {
	                    System.out.println("[" + tweet.getCreatedAt() + "] @" + tweet.getUser().getScreenName() + 
	                    					" - " + tweet.getText());
	                    ++count;
                	}
                    query = result.nextQuery();
                    if (jcl.getTweetLimit() > 0 && count >= jcl.getTweetLimit()) {
                    	query = null;
                    	break;
                    }
                }
            } 
        } catch (TwitterException te) {
            te.printStackTrace();
            System.err.println("Failed to search tweets: " + te.getMessage());
        }
	}
}