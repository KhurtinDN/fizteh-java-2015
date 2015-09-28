
/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import com.beust.jcommander.JCommander;
import twitter4j.*;
import twitter4j.StatusListener;

import java.util.List;


public class TwitterStream {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final int TRIES_LIMIT = 1;
    private static final int HUNDREED_NUM = 100;
    private static final int TWENTY_NUM = 20;
    private static final int TEN_NUM = 10;
    private static final int TWO_NUM = 2;
    private static final int FIVE_NUM = 5;
    private static final int FOUR_NUM = 4;
    private static final int ONE_NUM = 1;
    private static final String ANSI_BOLD = "\033[1m";

    private static class SplitLine {
        private static final int SPLIT_LINE_LENGTH = 80;
        private static String splitLine = "";

        private static void makeSplitLine() {
            for (int i = 0; i < SPLIT_LINE_LENGTH; ++i) {
                splitLine += "-";
            }
        }

        public static void print() {
            if (splitLine.length() != SPLIT_LINE_LENGTH) {
                makeSplitLine();
            }

            System.out.println(splitLine);
        }
    }

    private static String getRetweetInfo(Status status) {
        String answerStr = "";

        int countTweets = status.getRetweetCount();
        if (countTweets > 0) {
            String ending = "";
            if (countTweets % HUNDREED_NUM >= FIVE_NUM
                    && countTweets % HUNDREED_NUM <= TWENTY_NUM) {
                ending = "ов";
            } else {
                int lastNumber = status.getRetweetCount() % TEN_NUM;
                if (lastNumber >= TWO_NUM && lastNumber <= FOUR_NUM) {
                    ending = "а";
                } else if (!(lastNumber == ONE_NUM)) {
                    ending = "ов";
                }
            }

            answerStr += "(" + ANSI_BOLD
                    + countTweets
                    + ANSI_RESET
                    + " ретвит"
                    + ending
                    + ")";
        }
        return answerStr;
    }

    private static void printFormattedTweet(Status status, JCommanderParser jcp) {

        SplitLine splitLine = new SplitLine();
        String tweetText = status.getText();
        String time = "";

        if (!jcp.isStream()) {
            time = "[" + status.getCreatedAt().toString() + "]" + " ";
        }
        String outputString = time + ANSI_BLUE + ANSI_BOLD + "@"
                + status.getUser()
                .getScreenName()
                + ANSI_RESET
                + ": ";

        if (status.isRetweet()) {
            int firstNameIndex = tweetText.indexOf('@', 0) + 1;
            int lastNameIndex = tweetText.indexOf(':', firstNameIndex);
            String tweetAuthor = tweetText.substring(firstNameIndex, lastNameIndex - 1);
            tweetText = tweetText.substring(lastNameIndex + 1);
            outputString += "ретвитнул "
                    + ANSI_BLUE + ANSI_BOLD + "@"
                    + tweetAuthor
                    + ANSI_RESET + ":";

        }

        outputString += tweetText;
        outputString += getRetweetInfo(status);

        System.out.println(outputString);

        splitLine.print();

    }

    private static void streamStart(JCommanderParser jcp) {

        twitter4j.TwitterStream twStream = twitter4j
                .TwitterStreamFactory.getSingleton();

        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                printFormattedTweet(status, jcp);
            }
        };

        twStream.addListener(listener);
        twStream.filter(jcp.getQuery());
    }

    private static void printSingleTwitterQuery(final JCommanderParser jCommanderParser) {

        int tries = 0;
        QueryResult result = null;

        while (tries < TRIES_LIMIT) {
            try {
                ++tries;
                Twitter twitter = TwitterFactory.getSingleton();
                if (jCommanderParser.getQuery().length() == 0) {
                    System.out.println("Задан пустой запрос. Невозможно осуществить поиск");
                    System.exit(1);
                }

                Query query = new Query(jCommanderParser.getQuery());

                query.setCount(jCommanderParser.getLimit());

                result = twitter.search(query);
            } catch (TwitterException e) {
                e.getErrorCode();
                System.err.print(e.getMessage() + " \n"
                        + "Try connect. Tries:" + tries + ".");
            }
        }

        if (tries == TRIES_LIMIT + 1) {
            System.err.println("ERROR: Couldn't set connection");
            System.exit(-1);
        }

        List<Status> tweets = result.getTweets();

        int countTweets = 0;
        for (Status status : tweets) {
            if (!jCommanderParser.isHideRetweets() || !status.isRetweet()) {
                ++countTweets;
                printFormattedTweet(status, jCommanderParser);
            }
        }

        if (countTweets == 0) {
            System.out.println("по запросу \""
                    + jCommanderParser.getQuery() + "\" ничего не найдено.");
        }
    }

    public static void main(String[] args) throws TwitterException {
        System.out.println("\n\nTwitter 0.1 welcome\n\n");
        JCommanderParser jcp = new JCommanderParser();
        JCommander jcm = new JCommander(jcp, args);
        jcm.setProgramName("TwitterStream");

        if (jcp.isHelp()) {
            jcm.usage();
            return;
        }

        if (jcp.isStream()) {
            streamStart(jcp);
        } else {
            printSingleTwitterQuery(jcp);
        }
    }
}
