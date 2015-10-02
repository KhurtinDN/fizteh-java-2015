package main.java.ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import main.java.ru.fizteh.fivt.pitovsky.twitterstream.StringUtils.TextColor;
import twitter4j.FilterQuery;
import twitter4j.GeoQuery;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;

class TwitterClient {

    private static final int STREAM_SLEEP_TIME = 1000; //in ms
    private static final int EXIT_KEY = 27; //escape-key

    private Twitter twitter;
    private LinkedList<Status> tweetsQueue;
    private boolean hideRetweets;

    private static String prettyName(User user) {
        return StringUtils.setClr(TextColor.BLUE) + "@" + user.getScreenName()
                + StringUtils.setStClr();
    }

    private static String tweetOneString(Status tweet, boolean withDate) {
        StringBuilder tweetOut = new StringBuilder();
        if (withDate) {
            tweetOut.append("["
                    + StringUtils.setClr(TextColor.GREEN)
                    + StringUtils.convertDate(tweet.getCreatedAt())
                    + StringUtils.setStClr() + "] ");
        }
        tweetOut.append(prettyName(tweet.getUser()));
        if (tweet.isRetweet()) {
            tweetOut.append(" (ретвитнул "
                    + prettyName(tweet.getRetweetedStatus().getUser()) + "): "
                    + tweet.getRetweetedStatus().getText());
        } else {
            tweetOut.append(": " + tweet.getText());
        }
        if (tweet.getRetweetCount() > 0) {
            tweetOut.append(" (" + tweet.getRetweetCount() + " ретвитов)");
        }
        /*Place place = tweet.getPlace();
        if (place != null) {
            tweetOut.append(StringUtils.setClr(TextColor.MAGENTA) + "<" + place.getFullName() + ":"
             + place.getCountryCode() + ">" + StringUtils.setStClr());
        }*/
        return tweetOut.toString();
    }

    private StatusListener tweetListener = new StatusListener() {
        public void onStatus(Status tweet) {
            if ((!hideRetweets || !tweet.isRetweet())) {
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

    public SearchLocation findLocation(String region) throws TwitterException {
        GeoQuery gquery = new GeoQuery("192.168.1.1"); //an useless ip
        gquery.setQuery(region);
        ResponseList<Place> searchPlaces = twitter.searchPlaces(gquery);
        return new SearchLocation(searchPlaces);
    }

    TwitterClient() {
        twitter = new TwitterFactory().getInstance();
    }

    public void startStreaming(String queryString, boolean hideretweets,
            SearchLocation searchLocation) throws TwitterException {
        tweetsQueue = new LinkedList<Status>();
        hideRetweets = hideretweets; //for use it in tweetListener
        TwitterStream tstream = new TwitterStreamFactory().getInstance();
        tstream.addListener(tweetListener);
        FilterQuery fquery = new FilterQuery();
        String[] queryArray = new String[1];
        queryArray[0] = queryString;
        fquery.track(queryArray);
        if (searchLocation != null) {
            fquery.locations(searchLocation.getBoundingBox());
        }
        tstream.filter(fquery); //start a new thread for listing
        while (true) {
            while (!tweetsQueue.isEmpty()) {
                Status tweet = tweetsQueue.poll();
                System.out.println(tweetOneString(tweet, false));
            }
            try {
                /* Unfortunately, we have not 'raw' mode in java
                 * for its console, and we can read only after
                 * '\n' symbol.
                 */
                boolean needExit = false;
                while (System.in.available() > 0) {
                    int cm = System.in.read();
                    if (cm == 'q' || cm == EXIT_KEY || cm == -1) {
                        tstream.shutdown();
                        needExit = true;
                        break;
                    }
                }
                if (needExit) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(STREAM_SLEEP_TIME);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void printTweets(String queryString, boolean hideretweets,
            SearchLocation searchLocation, int limit) throws TwitterException {
        hideRetweets = hideretweets;
        Query query = new Query(queryString);
        if (searchLocation != null) {
            query.setGeoCode(searchLocation.getCenter(), searchLocation.getRadius(), Query.Unit.km);
            //System.err.println("from (" + searchLocation.getCenter().getLatitude() + "; "
            //        + searchLocation.getCenter().getLongitude() + "), r = " + searchLocation.getRadius());
        }

        query.setCount(limit);
        int count = 0;
        while (query != null) {
            QueryResult result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (!hideRetweets || !tweet.isRetweet()) {
                    System.out.println(tweetOneString(tweet, true));
                    ++count;
                }
                query = result.nextQuery();
                if (count >= limit) {
                    query = null;
                    break;
                }
            }
        }
    }
}
