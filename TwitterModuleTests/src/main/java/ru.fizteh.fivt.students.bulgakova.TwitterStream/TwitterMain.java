package ru.fizteh.fivt.students.bulgakova.TwitterStream;

/**
 * Created by Bulgakova Daria, 496.
 */

import twitter4j.*;

import java.io.Writer;
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


    public TwitterMain(TwitterParser _twitterParser) {
        twitterParser = _twitterParser;
    }

    private static StatusListener tweetListener = new StatusAdapter() {
        public void onStatus(Status status) {
            if (!twitterParser.getHideRetweets() || !status.isRetweet()) {
                streamQueue.add(status);
            }
        }
    };

    private static void usualMode(Writer writer, int counter) throws Exception {
        twitter = new TwitterFactory().getSingleton();
        query = new Query(twitterParser.getKeyword());
        queryResult = twitter.search(query);
        Integer tweetsCounter = 0;

        while (tweetsCounter++ < twitterParser.getLimit()) {
            for (twitter4j.Status status : queryResult.getTweets()) {
                if (!twitterParser.getHideRetweets() || !status.isRetweet()) {
                    writer.write(twitterOutput.printTweet(status, true) + "\n");
                    writer.flush();
                }

                if (tweetsCounter++ >= twitterParser.getLimit()) break;

            }
            if (queryResult.hasNext()) {
                queryResult = twitter.search(queryResult.nextQuery());
            } else {
                break;
            }
        }
    }

    private static void streamMode(Writer writer) throws Exception {

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        streamQueue = new ArrayBlockingQueue<Status>(QUEUE_MAX_SIZE);
        FilterQuery filterQuery = new FilterQuery();

        filterQuery.track(new String[]{twitterParser.getKeyword()});
        twitterStream.filter(filterQuery);

        while(true) {
            while (!streamQueue.isEmpty()) {
                Status status = streamQueue.poll();
                writer.write(twitterOutput.printTweet(status, false) + "\n");
                writer.flush();
                Thread.sleep(SLEEP_TIME);
            }
        }
    }

}


