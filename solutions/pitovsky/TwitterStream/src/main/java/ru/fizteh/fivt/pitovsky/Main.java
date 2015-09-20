package main.java.ru.fizteh.fivt.pitovsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import com.beust.jcommander.JCommander;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.TwitterStream;

/**
 * 
 * @author Peter Pitovsky
 *
 */
public class Main {
	
	/**
	 * get html-source of internet site
	 * @param url - url of site
	 * @return source in string
	 * @throws IOException
	 */
	private static String getUrlSource(String url) throws IOException {
        URL realurl = new URL(url);
        URLConnection urlcon = realurl.openConnection();
        BufferedReader brin = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder retstr = new StringBuilder();
        while ((inputLine = brin.readLine()) != null)
            retstr.append(inputLine);
        brin.close();

        return retstr.toString();
    }
	
	private static String setClr(int color) {
		return "" + (char)27 + "[" + color + "m"; //Esc-line
	}
	
	/**
	 * get one string with one tweet
	 * @param tweet tweet to resolve
	 * @return string with time, author, retweets and other
	 */
	private static String tweetOneString(Status tweet) {
		String tweetOut = "[" + tweet.getCreatedAt() + "] " + 
				setClr(34) + "@" + tweet.getUser().getScreenName() + setClr(0);
		if (tweet.isRetweet()) {
			tweetOut = tweetOut + " (retweeted from " + tweet.getRetweetedStatus().getUser().getScreenName() + ")";
		}
		tweetOut = tweetOut + ": " + tweet.getText();
		if (tweet.getRetweetCount() > 0) {
			tweetOut = tweetOut + " (" + tweet.getRetweetCount() + " retweets)";
		}
		Place place = tweet.getPlace();
		if (place != null) {
			tweetOut = tweetOut + "<" + place.getFullName() + ":" + place.getCountryCode() + ">";
		}
		return tweetOut;
	}
	
	private static boolean hideRetweets;
	private static String searchPlace;
	private static LinkedList<Status> tweetsQueue;
	
	private static boolean isGoodTweet(Status tweet) {
		return (!hideRetweets || !tweet.isRetweet()) && 
				(searchPlace.equals("anywhere") || 
				(tweet.getPlace() != null && tweet.getPlace().getCountryCode().equals(searchPlace)));
	}
	
	/**
	 * tweet listener for --stream-mode
	 */
	static StatusListener tweetListener = new StatusListener(){
        public void onStatus(Status tweet) {
        	if (isGoodTweet(tweet)) {
        		tweetsQueue.add(tweet);
        	}
        }
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
		@Override
		public void onScrubGeo(long arg0, long arg1) {}
		@Override
		public void onStallWarning(StallWarning arg0) {}
    };
	
	public static void main(String[] args) {
		//String[] argstmp = {"-q", "#Moscow", "-s"}; 
		JCommanderList jcl = new JCommanderList();
		JCommander jcomm = new JCommander(jcl, args);
		
		if (jcl.isHelp() || jcl.getQueryString() == null) {
			System.out.println("This program can print in stdout some tweets, filtered by options:");
			jcomm.usage();
			return;
		}
		/*String[] rquery = jcl.getQuery();
		System.out.println(jcl.getPlace() + " and " + jcl.getTweetLimit() + "; " + jcl.isStream());
		for (int i = 0; i < rquery.length; ++i)
			System.out.println(rquery[i]);*/
		
		Twitter twitter = new TwitterFactory().getInstance();
			
		searchPlace = jcl.getPlace();
		if (jcl.getPlace().equals("nearby")) {
			try {
				String wipsource = getUrlSource("http://api.wipmania.com/");
				searchPlace = wipsource.substring(wipsource.length() - 2); //this site look like "ip.ip.ip.ip</br>cc"
			} catch (IOException e) {
				e.printStackTrace();
				searchPlace = "anywhere";
			}
		}
		hideRetweets = jcl.isRetweetsHidden();
		
		if (jcl.isStream()) {
			tweetsQueue = new LinkedList<Status>();
			TwitterStream tstream = new TwitterStreamFactory().getInstance();	
			tstream.addListener(tweetListener);
			FilterQuery fquery = new FilterQuery();
			fquery.track(jcl.getQuery());
			tstream.filter(fquery); //start a new thread for listing new tweets
			while (true) {
				if (!tweetsQueue.isEmpty()) {
					Status tweet = tweetsQueue.poll();
					System.out.println(tweetOneString(tweet));
				}
				try {
				    Thread.sleep(1000);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		
		else {
	        try {
	            Query query = new Query(jcl.getQueryString());
	            QueryResult result;
	            int count = 0;
	            while (query != null) {
	                result = twitter.search(query);
	                List<Status> tweets = result.getTweets();
	                for (Status tweet : tweets) {
	                	if (isGoodTweet(tweet)) {
		                    System.out.println(tweetOneString(tweet));
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
}