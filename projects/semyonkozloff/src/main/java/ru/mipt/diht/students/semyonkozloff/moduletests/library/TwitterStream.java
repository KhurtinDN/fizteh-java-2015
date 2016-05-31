package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.beust.jcommander.JCommander;

import twitter4j.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.util.List;

import static ru.mipt.diht.students.semyonkozloff.
        moduletests.library.ConnectionChecker.*;
import static
        ru.mipt.diht.students.semyonkozloff.moduletests.library.QueryMaker.*;

public final class TwitterStream {

    private static final int TWEET_PRINT_DELAY = 1000;

    private Configuration configuration;
    private TweetPrinter tweetPrinter;

    public TwitterStream(String[] arguments, Writer writer) {
        tweetPrinter = new TweetPrinter(writer);
        configuration = new Configuration();

        new JCommander(configuration, arguments);
    }

    public void runStream() throws Exception {
        if (!hasConnection("api.twitter.com")) {
            throw new ConnectException("No connection");
        }

        twitter4j.TwitterStream twitterStream =
                new TwitterStreamFactory().getInstance();

        StatusListener tweetListener = new StatusAdapter() {

            @Override
            public void onStatus(Status tweet) {
                if (configuration.shouldHideRetweets() && tweet.isRetweet()) {
                    return;
                }

                try {
                    tweetPrinter.printTweet(tweet);
                    Thread.sleep(TWEET_PRINT_DELAY);
                } catch (InterruptedException | IOException exception) {
                    return;
                }
            }
        };

        twitterStream.addListener(tweetListener);

        FilterQuery filterQuery = makeFilterQuery(configuration);
        twitterStream.filter(filterQuery);
    }

    public void findTweets() throws Exception {
        if (!hasConnection("api.twitter.com")) {
            throw new ConnectException("No connection");
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
                throw new TwitterException("Fail searching tweets", exception);
            }

            if (queryResult == null) {
                break;
            }
            List<Status> tweets = queryResult.getTweets();
            for (Status tweet : tweets) {
                tweetPrinter.printTime(tweet.getCreatedAt());
                tweetPrinter.printTweet(tweet);
                ++tweetsCounter;

                if (tweetsCounter == query.getCount()) {
                    break doubleLoop;
                }
            }
            query = queryResult.nextQuery();

        } while (queryResult.hasNext());

        if (tweetsCounter == 0) { // if no tweets match a query
            tweetPrinter.printMessage("No tweets have been found");
        }
    }

    public void printHelp() {
        JCommander jCommander = new JCommander(new Configuration());
        jCommander.setProgramName("twitterstream");
        jCommander.usage();
    }

    public static void main(String[] args) throws Exception {
        TwitterStream twitterStream =
                new TwitterStream(args, new PrintWriter(System.out));

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
}

