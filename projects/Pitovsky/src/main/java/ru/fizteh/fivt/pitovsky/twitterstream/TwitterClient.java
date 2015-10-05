package ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.fizteh.fivt.pitovsky.twitterstream.ConsoleUtils.TextColor;
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

    private static final int MINUTE = 60 * 1000;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    private static final int NUM_DEC = 10; //checkstyle ask for it
    private static final int NUM_RU_ENDING = 5; //start for new endings in ru lang

    private Twitter twitter;
    private LinkedList<Status> tweetsQueue;
    private boolean hideRetweets;

    private static String getStringMinutesAgo(int minutes) {
        if (minutes / NUM_DEC != 1 && minutes % NUM_DEC == 1) {
            return minutes + " минуту назад"; //like 1, 21, 31...
        }
        if (minutes / NUM_DEC != 1 && minutes % NUM_DEC > 1
                && minutes % NUM_DEC < NUM_RU_ENDING) {
            return minutes + " минуты назад";
        }
        return minutes + " минут назад";
    }

    private static String getStringHoursAgo(int hours) {
        if (hours / NUM_DEC != 1 && hours % NUM_DEC == 1) {
            return hours + " час назад";
        }
        if (hours / NUM_DEC != 1 && hours % NUM_DEC > 1
                && hours % NUM_DEC < NUM_RU_ENDING) {
            return hours + " часа назад";
        }
        return hours + " часов назад";
    }

    private static String getStringDaysAgo(int days) {
        if (days / NUM_DEC != 1 && days % NUM_DEC == 1) {
            return days + " день назад";
        }
        if (days / NUM_DEC != 1 && days % NUM_DEC > 1
                && days % NUM_DEC < NUM_RU_ENDING) {
            return days + " дня назад";
        }
        return days + " дней назад";
    }

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
            return getStringMinutesAgo((int) ((HOUR + currentCalendar.getTimeInMillis()
                    - tweetCalendar.getTimeInMillis()) / MINUTE));
        }
        currentCalendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR) == tweetCalendar.get(Calendar.YEAR)) {
            return getStringHoursAgo(currentCalendar.get(Calendar.HOUR_OF_DAY)
                    - tweetCalendar.get(Calendar.HOUR_OF_DAY));
        }
        currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.DATE, -1);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR) == tweetCalendar.get(Calendar.YEAR)) {
            return "вчера";
        }
        currentCalendar = Calendar.getInstance();
        return getStringDaysAgo((int) ((currentCalendar.getTimeInMillis()
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
            tweetOut.append(" (ретвитнул "
                    + prettyName(tweet.getRetweetedStatus().getUser()) + "): "
                    + tweet.getRetweetedStatus().getText());
        } else {
            tweetOut.append(": " + tweet.getText());
        }
        if (tweet.getRetweetCount() > 0) {
            tweetOut.append(" (" + tweet.getRetweetCount() + " ретвитов)");
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
        SearchLocation location = new SearchLocation(searchPlaces);
        if (!location.isValid()) {
            return null;
        }
        return location;
    }

    TwitterClient() {
        twitter = new TwitterFactory().getInstance();
    }

    public void startStreaming(String queryString, boolean hideretweets,
            SearchLocation searchLocation, boolean debug) throws TwitterException {
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
            SearchLocation searchLocation, int limit, boolean debug) throws TwitterException {
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
