package main.java.ru.fizteh.fivt.pitovsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import com.beust.jcommander.JCommander;

import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

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
        BufferedReader in = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }
	
	/**
	 * get one string with one tweet
	 * @param tweet tweet to resolve
	 * @return string with time, author, retweets and other
	 */
	private static String tweetOneString(Status tweet) {
		String tweetOut = "[" + tweet.getCreatedAt() + "] @" + tweet.getUser().getScreenName();
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
	
	public static void main(String[] args) {
		String[] argstmp = {"-p", "nearby", "-q", "#Moscow", "--limit", "150"}; 
		JCommanderList jcl = new JCommanderList();
		JCommander jcomm = new JCommander(jcl, argstmp);
		
		if (jcl.isHelp()) {
			System.out.println("This program can print in stdout some tweets, filtered by options:");
			jcomm.usage();
			return;
		}
		/*String[] rquery = jcl.getQuery();
		System.out.println(jcl.getPlace() + " and " + jcl.getTweetLimit() + "; " + jcl.isStream());
		for (int i = 0; i < rquery.length; ++i)
			System.out.println(rquery[i]);*/
		
		Twitter twitter = new TwitterFactory().getInstance();
		
		String searchPlace = jcl.getPlace();
		if (jcl.getPlace().equals("nearby")) {
			try {
				String wipsource = getUrlSource("http://api.wipmania.com/");
				searchPlace = wipsource.substring(wipsource.length() - 2); //this site look like "ip.ip.ip.ip</br>cc"
			} catch (IOException e) {
				e.printStackTrace();
				searchPlace = "anywhere";
			}
		}
		
        try {
            Query query = new Query(jcl.getQueryString());
            QueryResult result;
            int count = 0;
            while (query != null) {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                	if ((!jcl.isRetweetsHidden() || !tweet.isRetweet()) && 
                			(searchPlace.equals("anywhere") || 
                			(tweet.getPlace() != null && tweet.getPlace().getCountryCode().equals(searchPlace)))) {
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