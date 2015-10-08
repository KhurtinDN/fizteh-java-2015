package ru.mipt.diht.students.maxDankow.TwitterStream;
import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.util.LinkedList;
import java.util.List;

public class TwitterStreamClient {

    public static void main(String[] args) {
        JComanderArgsList jCmdArgs = new JComanderArgsList();
        new JCommander(jCmdArgs, args);
        //todo: if isHelp, print usage
        hideRetweets = jCmdArgs.isHideRetweets();
        // Определяем место поиска
        searchLocation = jCmdArgs.getLocationStr();
        //GeoLocation location = TwitterStreamUtils.findLocation(searchLocation);
        //GeoLocation location = new GeoLocation();
        TwitterStreamUtils.findLocation("Рыбинск");

        if (jCmdArgs.isStreamMode()) {
            //todo: make more beautiful
            String[] queryArray = new String[1];
            queryArray[0] = jCmdArgs.getQueryText();
            startTwitterStreaming(queryArray);
        } else {
            searchTweets(jCmdArgs.getQueryText(), jCmdArgs.getTweetsNumberLimit());
        }
    }

    private static final int STREAM_DELAY_MS = 1000;
    private static boolean hideRetweets;
    private static String searchLocation;
    private static LinkedList<Status> streamQueue;

    private static StatusListener tweetListener = new StatusListener() {
        public void onStatus(Status tweet) {
            if (checkTweetConditions(tweet, hideRetweets, searchLocation)) {
                streamQueue.add(tweet);
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

    private static void startTwitterStreaming(String[] queryText) {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(queryText);
        streamQueue = new LinkedList<>();
        twitterStream.filter(filterQuery);
        while (true) {
            while (!streamQueue.isEmpty()) {
                Status tweet = streamQueue.poll();
                printTweet(tweet, false);
            }
            try {
                Thread.sleep(STREAM_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void searchTweets(String queryText, int tweetsNumberLimit) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        query.setQuery(queryText);
        //query.set;
        query.setCount(tweetsNumberLimit);
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException te) {
            System.err.println(te.getMessage());
            System.exit(te.getErrorCode());
        }
        int tweetsCount = 0;
        while (query != null) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (checkTweetConditions(tweet, hideRetweets, searchLocation)) {
                    printTweet(tweet, true);
                    tweetsCount++;
                }
            }
            query = result.nextQuery();
            if (tweetsCount >= tweetsNumberLimit) {
                query = null;
            }
        }
    }

    private static boolean checkTweetConditions(Status tweet, boolean hideRetweets, String locationName) {
        //todo: replace this with query suffix. Let's Twitter hide retweets himself.
        return (!tweet.isRetweet() || hideRetweets);
    }

    private static void printTweet(Status tweet, boolean shouldShowTime) {
        if (shouldShowTime) {
            System.out.print("["
                    + TwitterStreamUtils.convertTimeToRussianWords(tweet.getCreatedAt())
                    + "] ");
        }
        if (!tweet.isRetweet()) {
            int retweetCount = tweet.getRetweetCount();

            System.out.println("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: "
                    + tweet.getText());
            if (retweetCount > 0) {
                System.out.print(" ("
                        + tweet.getRetweetCount()
                        + " ретвитов)");
            }
            System.out.println();
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            System.out.println("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: ретвитнул \033[34m@"
                    + originalTweet.getUser().getScreenName()
                    + "\033[0m: "
                    + originalTweet.getText());
        }
    }
}
