package ru.mipt.diht.students.sopilnyak.moduletests.library;

import twitter4j.Status;

public class Format {

    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";

    public static String formatTweet(Status status) {
        return "@" + BLUE
                + status.getUser().getScreenName()
                + RESET + getRetweetSource(status)
                + ": " + status.getText()
                + retweetCount(status);
    }

    protected static String getResultsStreamNotEnabled(Status status) {
        // print tweets
        if ((!status.isRetweet()
                || !Arguments.getHideRetweets())) { // hide retweets
            return "["
                    + DateString.getDate(status.getCreatedAt())
                    + "] " + formatTweet(status);
        }
        return "";
    }

    protected static String getResultsStreamEnabled(Status status) {
        if (!status.isRetweet()
                || !Arguments.getHideRetweets()) { // hide retweets
            return formatTweet(status);
        }
        return "";
    }

    protected static String retweetCount(Status status) {
        if (status.getRetweetCount() > 0) {
            return " (" + status.getRetweetCount() + " ретвитов)";
        }
        return "";
    }

    protected static String getRetweetSource(Status status) {
        if (status.isRetweet()) {
            return " ретвитнул "
                    + "@" + BLUE
                    + status.getRetweetedStatus().
                    getUser().getScreenName() + RESET;
        }
        return "";
    }

}
