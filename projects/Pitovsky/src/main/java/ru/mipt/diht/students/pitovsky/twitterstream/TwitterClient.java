package ru.mipt.diht.students.pitovsky.twitterstream;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils.TextColor;
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
    private static final int STREAM_MAX_QUEUE_SIZE = 1000; //i know, it is impossible, more than 1000 tweets per sec

    private Twitter twitter;
    private BlockingQueue<Status> tweetsQueue;
    private boolean hideRetweets;
    private boolean isDebugMode;


    public static String convertDate(Date date) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tweetDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (tweetDateTime.isAfter(currentDateTime)) {
            return "еще не опубликовано";
        }
        Duration timeAfterTweet = Duration.between(tweetDateTime, currentDateTime);
        if (timeAfterTweet.toMinutes() < 2) {
            return "только что";
        }
        if (timeAfterTweet.toHours() < 1) {
            return StringRuUtils.getNumeralsAgo("минута", (int) (timeAfterTweet.toMinutes()));
        }
        if (currentDateTime.toLocalDate().equals(tweetDateTime.toLocalDate())) {
            return StringRuUtils.getNumeralsAgo("час", (int) (timeAfterTweet.toHours()));
        }

        if (currentDateTime.toLocalDate().minusDays(1).equals(tweetDateTime.toLocalDate())) {
            return "вчера";
        }
        return StringRuUtils.getNumeralsAgo("день", (int) (timeAfterTweet.toDays()));
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
                tweetsQueue.add(tweet);
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

    TwitterClient(boolean needHideRetweets, boolean withDebug) {
        hideRetweets = needHideRetweets;
        isDebugMode = withDebug;
        twitter = new TwitterFactory().getInstance();
    }

    public void startStreaming(String queryString, SearchLocation searchLocation) throws TwitterException {
        tweetsQueue = new ArrayBlockingQueue<Status>(STREAM_MAX_QUEUE_SIZE);
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(new String[] {queryString});
        /** woh, some problems:
         * "Start consuming public statuses that match one or more filter predicates." - can we read in documentation,
         * but we need statuses which match ALL of the filter predicates. I'm working on it.
         */
        if (searchLocation != null) {
            filterQuery.locations(searchLocation.getBoundingBox());
        }
        twitterStream.filter(filterQuery); //start a new thread for listing
        while (true) {
            while (!tweetsQueue.isEmpty()) {
                Status tweet = tweetsQueue.poll();
                System.out.println(tweetOneString(tweet, false, isDebugMode));
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
                        twitterStream.shutdown();
                        needExit = true;
                        break;
                    }
                }
                if (needExit) {
                    break;
                }
            } catch (IOException e) {
                System.err.println("cannot read from stdin: " + e.getMessage());
            }
            try {
                Thread.sleep(STREAM_SLEEP_TIME);
            } catch (InterruptedException ex) {
                throw new TwitterException("Cannot wait in this thread, interrupted", ex);
            }
        }
    }

    public void printTweets(String queryString,  SearchLocation searchLocation, int limit) throws TwitterException {
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
                    System.out.println(tweetOneString(tweet, true, isDebugMode));
                    ++count;
                }
                if (count >= limit) {
                    query = null;
                    break;
                }
            }
            query = result.nextQuery();
        }
    }
}
