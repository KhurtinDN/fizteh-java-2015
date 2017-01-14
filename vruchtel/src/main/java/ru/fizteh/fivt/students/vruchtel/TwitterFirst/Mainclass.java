package ru.fizteh.fivt.students.vruchtel.TwitterFirst;
/**
 * Created by �������� on 07.10.2015.
 */
import twitter4j.Status;
//import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import jdk.net.SocketFlow;
import twitter4j.*;
import com.beust.jcommander.*;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Mainclass {

    public static void main(String args[]) throws Exception {
        try {
            twitterStreamArgs = new TwitterStreamArgs(args);
            textFormatter = new TextFormatter();
            System.out.println("����� �� ������� <" + twitterStreamArgs.getKeyword() + ">:");

            if (twitterStreamArgs.isStreamUse()) {
                workInStreamMode(); //���� ����� ���� --stream
            } else {
                workInCommonMode(); //���� ���� --stream �� �����
            }

            System.exit(0);

        } catch (TwitterException exception) {
            System.err.println("Twitter exception!");
        } catch (IOException exception) {
            System.err.println("Problems with printing or reading data!");
        } catch (InterruptedException exception) {
            System.err.println("Problems with sleeping!");
        }

    }

    private static StatusListener tweetListener = new StatusAdapter(){
        public void onStatus(Status status) {
            if (!twitterStreamArgs.isHideRetweets() || !status.isRetweet()) {
                streamQueue.add(status);
            }
        }
    };

    //���� ����� ���� --stream
    public static void workInStreamMode() throws Exception {
        streamQueue = new ArrayBlockingQueue<Status>(QUEUE_MAX_SIZE);
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(new String[]{twitterStreamArgs.getKeyword()});
        twitterStream.filter(filterQuery);

        Boolean haveToFinish = false;
        int readedSymbol;
        //�����������, ���� �� ��������
        while (true) {
            while(!streamQueue.isEmpty()) {
                Status status = streamQueue.poll();
                //System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                System.out.println(textFormatter.getTextToPrint(status, false));
                Thread.sleep(SLEEP_TIME);
            }

            //��������, ���� �� �����������, �, ���� ����, ����������
            /*while (System.in.available() > 0) {
                readedSymbol = System.in.read();
                if(readedSymbol == 'q' || readedSymbol == EXIT_KEY || readedSymbol == -1) {
                    twitterStream.shutdown();
                    haveToFinish = true;
                    break;
                }
            }
            if(haveToFinish) break;
            Thread.sleep(SLEEP_TIME);*/
        }
    }

    //���� ���� --stream �� �����
    public static void workInCommonMode() throws Exception {
        twitter = new TwitterFactory().getSingleton();
        //TwitterFactory().getInstance() ���������� ������ Twitter, � TwitterFactory().GetSingleton()
        //���������� static Twitter

        query = new Query(twitterStreamArgs.getKeyword());
        queryResult = twitter.search(query);
        Integer tweetsCounter = 0;
        while (tweetsCounter++ < twitterStreamArgs.getLimit()) {
            for (twitter4j.Status status : queryResult.getTweets()) {
                //��� ��������� ������ ������ ����� ����� �������� ��������� �����
                if (!twitterStreamArgs.isHideRetweets() || !status.isRetweet()) {
                    //System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                    System.out.println(textFormatter.getTextToPrint(status, true));
                }

                if (tweetsCounter++ >= twitterStreamArgs.getLimit()) break;

            }
            if (queryResult.hasNext()) {//���� �� ��������� ���������
                queryResult = twitter.search(queryResult.nextQuery());
            } else {
                break;
            }
        }
    }

    private static Twitter twitter;
    private static TwitterStreamArgs twitterStreamArgs;
    private static Query query;
    private static QueryResult queryResult;
    private static BlockingQueue<Status> streamQueue;
    private static TextFormatter textFormatter;
    private static final int QUEUE_MAX_SIZE = 1000;
    private static final int SLEEP_TIME = 1000;
    private static final char EXIT_KEY = (char) 27;
}
