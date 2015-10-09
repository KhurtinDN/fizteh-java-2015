package ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ru.fizteh.fivt.pitovsky.twitterstream.ConsoleUtils.TextColor;
import ru.fizteh.fivt.pitovsky.twitterstream.SearchLocation.SearchLocationException;
import twitter4j.FilterQuery;
import twitter4j.GeoQuery;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;

class TwitterClient {

    private static final int STREAM_SLEEP_TIME = 1000; //in ms
    private static final int STREAM_EXIT_KEY = 27; //escape-key
    private static final int STREAM_MAX_QUEUE_SIZE = 1000; //i think, it is impossible, more than 1000 tweets per sec

    private static final int MINUTE = 60 * 1000;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private Twitter twitter;
    private BlockingQueue<Status> tweetsQueue;
    private boolean hideRetweets;


    public static String convertDate(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar tweetCalendar = Calendar.getInstance();
        tweetCalendar.setTime(date);
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            return "еще не опубликовано";
        }
        currentCalendar.add(Calendar.MINUTE, -1);
        currentCalendar.add(Calendar.MINUTE, -1); //because "-2 is a magic number"
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            return "только что";
        }
        currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.HOUR, -1);
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            return StringRuUtils.getNumeralsAgo("минута", ((int) (HOUR + currentCalendar.getTimeInMillis()
                    - tweetCalendar.getTimeInMillis()) / MINUTE));
        }
        currentCalendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR) == tweetCalendar.get(Calendar.YEAR)) {
            return StringRuUtils.getNumeralsAgo("час", currentCalendar.get(Calendar.HOUR_OF_DAY)
                    - tweetCalendar.get(Calendar.HOUR_OF_DAY));
        }
        currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.DATE, -1);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR) == tweetCalendar.get(Calendar.YEAR)) {
            return "вчера";
        }
        currentCalendar = Calendar.getInstance();
        return StringRuUtils.getNumeralsAgo("день", (int) ((currentCalendar.getTimeInMillis()
                - tweetCalendar.getTimeInMillis()) / DAY) + 1);
    }

    private static String prettyName(User user) {
        return ConsoleUtils.colorizeString("@" + user.getScreenName(), TextColor.BLUE);
    }

    private static String tweetOneString(Status tweet, boolean withDate, boolean withPlace) {
        StringBuilder tweetOut = new StringBuilder();
        if (withDate) {
            tweetOut.append(ConsoleUtils.colorizeString("[" + convertDate(tweet.getCreatedAt()) + "]",
                    TextColor.GREEN));
        }
        tweetOut.append(prettyName(tweet.getUser()));
        if (tweet.isRetweet()) {
            tweetOut.append(" (ретвитнул ");
            tweetOut.append(prettyName(tweet.getRetweetedStatus().getUser()));
            tweetOut.append("): ");
            tweetOut.append(tweet.getRetweetedStatus().getText());
        } else {
            tweetOut.append(": ");
            tweetOut.append(tweet.getText());
        }
        if (tweet.getRetweetCount() > 0) {
            tweetOut.append(" (");
            tweetOut.append(tweet.getRetweetCount() + " ");
            tweetOut.append(StringRuUtils.getNumeralWord("ретвит", tweet.getRetweetCount()));
            tweetOut.append(")");
        }
        if (withPlace) {
            Place place = tweet.getPlace();
            if (place != null) {
                tweetOut.append(ConsoleUtils.colorizeString("<" + place.getFullName() + ":"
                 + place.getCountryCode() + ">", TextColor.MAGENTA));
            }
        }
        return tweetOut.toString();
    }

    private StatusListener tweetListener = new StatusAdapter() {
        public void onStatus(Status tweet) {
            if ((!hideRetweets || !tweet.isRetweet())) {
                if (tweetsQueue.size() < STREAM_MAX_QUEUE_SIZE - 1) {
                    tweetsQueue.add(tweet);
                }
            }
        }
    };

    public SearchLocation findLocation(String region) throws TwitterException {
        GeoQuery gquery = new GeoQuery("192.168.1.1"); //an useless ip
        gquery.setQuery(region);
        ResponseList<Place> searchPlaces = twitter.searchPlaces(gquery);
        SearchLocation location;
        try {
            location = new SearchLocation(searchPlaces);
        } catch (SearchLocationException sle) {
            location = null;
        }
        return location;
    }

    TwitterClient() { //codestyler says, that 'public' modifier is redundant in this case
        twitter = new TwitterFactory().getInstance();
    }

    public void startStreaming(String queryString, boolean ifHideRetweets,
            SearchLocation searchLocation, boolean debug) throws TwitterException {
        tweetsQueue = new ArrayBlockingQueue<Status>(STREAM_MAX_QUEUE_SIZE);
        hideRetweets = ifHideRetweets; //for use it in tweetListener
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
                System.out.println(tweetOneString(tweet, false, debug));
            }
            try {
                /* Unfortunately, we have not 'raw' mode in java
                 * for its console, and we can read only after
                 * '\n' symbol.
                 */
                boolean needExit = false;
                while (System.in.available() > 0) {
                    int cm = System.in.read();
                    if (cm == 'q' || cm == STREAM_EXIT_KEY || cm == -1) {
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
                throw new TwitterException("Cannot wait in this thread, interrupted", ex);
            }
        }
    }

    public void printTweets(String queryString, boolean hideretweets,
            SearchLocation searchLocation, int limit, boolean debug) throws TwitterException {
        hideRetweets = hideretweets;
        Query query = new Query(queryString);
        if (searchLocation != null) {
            query.setGeoCode(searchLocation.getCenter(), searchLocation.getRadius(), Query.Unit.km);
        }

        query.setCount(limit);
        int count = 0;
        while (query != null) {
            QueryResult result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (!hideRetweets || !tweet.isRetweet()) {
                    System.out.println(tweetOneString(tweet, true, debug));
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
