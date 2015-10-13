package ru.mipt.diht.students.semyonkozloff.twitterstream;

import com.beust.jcommander.JCommander;

import twitter4j.*;

import java.util.List;

import static ru.mipt.diht.students.semyonkozloff.twitterstream.TweetPrinter.*;
import static ru.mipt.diht.students.semyonkozloff.twitterstream.QueryMaker.*;
import static
        ru.mipt.diht.students.semyonkozloff.twitterstream.ConnectionChecker.*;

public final class TwitterStream {

    public TwitterStream(String[] arguments) {
        configuration = new Configuration();
        new JCommander(configuration, arguments);
    }

    public void runStream() {
        if (!hasConnection("api.twitter.com")) {
            System.err.print("No connection.");
            System.exit(1);
        }

        twitter4j.TwitterStream twitterStream =
                new TwitterStreamFactory().getInstance();

        StatusListener tweetListener = new StatusAdapter() {

            public void onStatus(Status tweet) {
                if (configuration.shouldHideRetweets() && tweet.isRetweet()) {
                    return;
                }

                printTweet(tweet);

                try {
                    Thread.sleep(TWEET_PRINT_DELAY);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    System.err.print("Thread can't sleep: ");
                    exception.printStackTrace(System.err);
                    System.exit(1);
                }
            }

            public void onException(Exception exception) {
                System.err.print("Stream error: ");
                exception.printStackTrace(System.err);
                System.exit(1);
            }
        };

        twitterStream.addListener(tweetListener);

        FilterQuery filterQuery = makeFilterQuery(configuration);
        twitterStream.filter(filterQuery);
    }

    public void findTweets() {
        if (!hasConnection("api.twitter.com")) {
            System.err.print("No connection.");
            System.exit(1);
        }

        Twitter twitter = new TwitterFactory().getInstance();
        Query query = makeQuery(configuration);

        long tweetsCounter = 0;
        QueryResult queryResult = null;
        doubleLoop:
        do {
            try {
                queryResult = twitter.search(query);
            } catch (TwitterException exception) {
                System.err.print("Fail of searching tweets: ");
                exception.printStackTrace(System.err);
                System.exit(1);
            }

            if (queryResult == null) {
                break;
            }
            List<Status> tweets = queryResult.getTweets();
            for (Status tweet : tweets) {
                printTime(tweet.getCreatedAt());
                printTweet(tweet);
                ++tweetsCounter;

                if (tweetsCounter == query.getCount()) {
                    break doubleLoop;
                }
            }
            query = queryResult.nextQuery();

        } while (queryResult.hasNext());

        if (tweetsCounter == 0) { // if no tweets match a query
            System.out.println("No tweets found");
        }
    }

    public void printHelp() {
        JCommander jCommander = new JCommander(new Configuration());
        jCommander.setProgramName("TwitterStream");
        jCommander.usage();
    }

    public static void main(String[] args) {
        TwitterStream twitterStream = new TwitterStream(args);

        if (twitterStream.configuration.getQuery() != null) {
            if (twitterStream.configuration.isStream()) {
                twitterStream.runStream();
            } else {
                twitterStream.findTweets();
            }
        } else if (twitterStream.configuration.isHelp()) {
            twitterStream.printHelp();
        }
    }

    private Configuration configuration = null;

    private static final int TWEET_PRINT_DELAY = 1000;
}

