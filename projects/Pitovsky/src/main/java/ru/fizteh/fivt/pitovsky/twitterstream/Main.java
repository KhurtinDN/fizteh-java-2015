package main.java.ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import main.java.ru.fizteh.fivt.pitovsky.twitterstream.StringUtils.TextColor;

import com.beust.jcommander.JCommander;

import twitter4j.FilterQuery;
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

    private static final int STREAM_SLEEP_TIME = 1000; //in ms
    private static final int COUNTRY_CODE_LEN = 2; //like RU, EN

    private static String getUrlSource(String url) throws IOException {
        URL realurl = new URL(url);
        URLConnection urlcon = realurl.openConnection();
        BufferedReader brin = new BufferedReader(
                new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder retstr = new StringBuilder();
        while ((inputLine = brin.readLine()) != null) {
            retstr.append(inputLine);
        }
        brin.close();

        return retstr.toString();
    }

    private static String prettyName(User user) {
        return StringUtils.setClr(TextColor.BLUE) + "@" + user.getScreenName()
                + StringUtils.setStClr();
    }

    private static String tweetOneString(Status tweet, boolean withDate) {
        String tweetOut = "";
        if (withDate) {
            tweetOut = "[" + StringUtils.setClr(TextColor.GREEN)
                    + StringUtils.convertDate(tweet.getCreatedAt())
                    + StringUtils.setStClr() + "] ";
        }
        tweetOut = tweetOut + prettyName(tweet.getUser());
        if (tweet.isRetweet()) {
            tweetOut = tweetOut + " (ретвитнул "
                    + prettyName(tweet.getRetweetedStatus().getUser()) + "): "
                    + tweet.getRetweetedStatus().getText();
        } else {
            tweetOut = tweetOut + ": " + tweet.getText();
        }
        if (tweet.getRetweetCount() > 0) {
            tweetOut = tweetOut + " (" + tweet.getRetweetCount() + " ретвитов)";
        }
        /*Place place = tweet.getPlace();
        if (place != null) {
            tweetOut = tweetOut + "<" + place.getFullName() + ":"
             + place.getCountryCode() + ">";
        }*/
        return tweetOut;
    }

    private static boolean hideRetweets;
    private static String searchPlace;
    private static LinkedList<Status> tweetsQueue;

    private static boolean isGoodTweet(Status tweet) {
        return (!hideRetweets || !tweet.isRetweet())
                && (searchPlace.equals("anywhere")
                || (tweet.getPlace() != null
                && tweet.getPlace().getCountryCode().equals(searchPlace)));
    }

    private static StatusListener tweetListener = new StatusListener() {
        public void onStatus(Status tweet) {
            if (isGoodTweet(tweet)) {
                tweetsQueue.add(tweet);
            }
        }
        public void onDeletionNotice(StatusDeletionNotice statusDN) {
        }
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        }
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
        @Override
        public void onScrubGeo(long arg0, long arg1) {
        }
        @Override
        public void onStallWarning(StallWarning arg0) {
        }
    };

    public static void main(String[] args) {
        //String[] argstmp = {"-q", "#Moscow", "-s"};
        JCommanderList jcl = new JCommanderList();
        JCommander jcomm = new JCommander(jcl, args);

        if (jcl.isHelp() || jcl.getQueryString() == null) {
            System.out.println("This program can print in stdout some tweets,"
                    + "searched at twitter.com and filtered by options:");
            jcomm.usage();
            return;
        }

        Twitter twitter = new TwitterFactory().getInstance();

        searchPlace = jcl.getPlace();
        if (jcl.getPlace().equals("nearby")) {
            try {
                String wipsource = getUrlSource("http://api.wipmania.com/");
                searchPlace = wipsource.substring(wipsource.length()
                        - COUNTRY_CODE_LEN); //is site like"ip.ip.ip.ip</br>cc"
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
                while (!tweetsQueue.isEmpty()) {
                    Status tweet = tweetsQueue.poll();
                    System.out.println(tweetOneString(tweet, false));
                }
                //ToDO: break by EOF or <ESC>.Is it possible without new thread?
                try {
                    Thread.sleep(STREAM_SLEEP_TIME);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            try {
                Query query = new Query(jcl.getQueryString());
                QueryResult result;
                query.setCount(jcl.getTweetLimit());
                int count = 0;
                while (query != null) {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        if (isGoodTweet(tweet)) {
                            System.out.println(tweetOneString(tweet, true));
                            ++count;
                        }
                        query = result.nextQuery();
                        if (count >= jcl.getTweetLimit()) {
                            query = null;
                            break;
                        }
                    }
                }
            } catch (TwitterException te) {
                te.printStackTrace();
                System.err.println("Failed to search: " + te.getMessage());
            }
        }
    }
}
