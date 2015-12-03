package ru.fizteh.fivt.students.vruchtel.moduletests.library;

import twitter4j.*;

import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Серафима on 24.11.2015.
 */
public class WorkingModes {

    public WorkingModes(TwitterStreamArgs _twitterStreamArgs) {
        twitterStreamArgs = _twitterStreamArgs;
    }

    private static StatusListener tweetListener = new StatusAdapter(){
        public void onStatus(Status status) {
            if (!twitterStreamArgs.isHideRetweets() || !status.isRetweet()) {
                streamQueue.add(status);
            }
        }
    };

    //если задан флаг --stream
    public static void workInStreamMode(Writer writer) throws Exception {
        streamQueue = new ArrayBlockingQueue<Status>(QUEUE_MAX_SIZE);
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(new String[]{twitterStreamArgs.getKeyword()});
        twitterStream.filter(filterQuery);

        //выполняться, пока не прервёмся
        while (true) {
            while(!streamQueue.isEmpty()) {
                Status status = streamQueue.poll();
                writer.write(tweetsFormatter.getTextToPrint(status, false) + "\n");
                writer.flush();
                Thread.sleep(SLEEP_TIME);
            }
        }
    }

    //если флаг --stream не задан
    public static void workInCommonMode(Writer writer, int counter) throws Exception {
        twitter = new TwitterFactory().getSingleton();

        query = new Query(twitterStreamArgs.getKeyword());
        queryResult = twitter.search(query);
        Integer tweetsCounter = 0;
        while (tweetsCounter++ < twitterStreamArgs.getLimit()) {
            for (twitter4j.Status status : queryResult.getTweets()) {
                //для красивого вывода вообще нужно будет написать отдельный класс
                if (!twitterStreamArgs.isHideRetweets() || !status.isRetweet()) {
                    writer.write(tweetsFormatter.getTextToPrint(status, true) + "\n");
                    writer.flush();
                }

                if (tweetsCounter++ >= twitterStreamArgs.getLimit()) break;

            }
            if (queryResult.hasNext()) {//есть ли следующая страничка
                queryResult = twitter.search(queryResult.nextQuery());
            } else {
                break;
            }
        }
    }

    private static TwitterStreamArgs twitterStreamArgs;
    private static TweetsFormatter tweetsFormatter;
    private static Twitter twitter;
    private static Query query;
    private static QueryResult queryResult;
    private static BlockingQueue<Status> streamQueue;
    private static final int QUEUE_MAX_SIZE = 1000;
    private static final int SLEEP_TIME = 1000;
}
