package ru.fizteh.fivt.students.bulgakova.TwitterStream;

/**
 * Created by Bulgakova Daria, 496.
 */

import twitter4j.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TwitterMain {

    private static Twitter twitter;
    private static TwitterParser twitterParser;
    private static TwitterOutput twitterOutput;

    private static Query query;
    private static QueryResult queryResult;
    private static BlockingQueue<Status> streamQueue;

    private static final int QUEUE_MAX_SIZE = 1000;
    private static final int SLEEP_TIME = 1000;


    public static void main(String[] args) throws Exception {
        try {
            twitterParser = new TwitterParser(args);
            twitterOutput = new TwitterOutput();

            if (twitterParser.getIfStream()) {
                streamMode();
            } else {
                usualMode();
            }
            System.exit(0);
        } catch (TwitterException twitterException) {
            System.err.println("Error: twitter");
        } catch (InterruptedException interruptedException) {
            System.err.println("Error: sleeping");
        }

        System.exit(0);
    }

    private static StatusListener tweetListener = new StatusAdapter() {
        public void onStatus(Status status) {
            if (!twitterParser.getHideRetweets() || !status.isRetweet()) {
                streamQueue.add(status);
            }
        }
    };

    private static void usualMode() throws Exception {

        twitter = new TwitterFactory().getInstance();  //Singleton???
        query = new Query(twitterParser.getKeyword());
        queryResult = twitter.search(query);

        for (int i = 0; i < twitterParser.getLimit(); i++) {
            for (twitter4j.Status status : queryResult.getTweets()) {
                if (!status.isRetweet() || !twitterParser.getHideRetweets()) {
                    TwitterOutput.printTweet(status, true);
                }

                i++;
                if (i >= twitterParser.getLimit()) {
                    break;
                }

                if(queryResult.hasNext()) {
                    queryResult = twitter.search(queryResult.nextQuery());
                } else {
                    break;
                }
            }
        }

    }

    private static void streamMode() throws Exception {

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        streamQueue = new ArrayBlockingQueue<Status>(QUEUE_MAX_SIZE);
        FilterQuery filterQuery = new FilterQuery();

        filterQuery.track(new String[]{twitterParser.getKeyword()});
        twitterStream.filter(filterQuery);

        while(true) {
            while (!streamQueue.isEmpty()) {
                Status status = streamQueue.poll();
                TwitterOutput.printTweet(status, false);
                Thread.sleep(SLEEP_TIME);
            }
        }

    }

}


