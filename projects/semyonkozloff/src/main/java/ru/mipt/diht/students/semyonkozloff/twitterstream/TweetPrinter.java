package ru.mipt.diht.students.semyonkozloff.twitterstream;

import com.google.common.base.Strings;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TweetPrinter {

    private static final int DELIMITER_LENGTH = 160;

    private static final String DELIMITER =
            Strings.repeat("-", DELIMITER_LENGTH);

    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void printTweet(Status tweet) {
        System.out.print("@" + ANSI_BLUE
                + tweet.getUser().getName() + ANSI_RESET + ": ");

        if (tweet.isRetweet()) {
            System.out.print("retweeted "
                    + "@" + ANSI_BLUE
                    + tweet.getRetweetedStatus().getUser().getName()
                    + ANSI_RESET
                    + ": " + tweet.getText());
        } else {
            System.out.print(tweet.getText() + " ");
            if (tweet.isRetweeted()) {
                System.out.print("(" + ANSI_GREEN + tweet.getRetweetCount()
                        + " retweets"
                        + ANSI_RESET + ")");
            }
        }
        System.out.print("\n" + DELIMITER + "\n");
    }

    public static void printTime(Date date) {

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tweetTime = date.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        System.out.print("[" + ANSI_CYAN);

        if (ChronoUnit.MINUTES.between(tweetTime, currentTime) < 2) {
            System.out.print("just now");
        } else if (ChronoUnit.HOURS.between(tweetTime, currentTime) < 1) {
            System.out.print(ChronoUnit.MINUTES.between(tweetTime, currentTime)
                    + " minutes ago");
        } else if (ChronoUnit.DAYS.between(tweetTime, currentTime) < 1) {
            System.out.print(ChronoUnit.HOURS.between(tweetTime, currentTime)
                    + " hours ago");
        } else if (ChronoUnit.DAYS.between(tweetTime, currentTime) == 1) {
            System.out.print("yesterday");
        } else {
            System.out.print(ChronoUnit.DAYS.between(tweetTime, currentTime)
                    + " days ago");
        }

        System.out.print(ANSI_RESET + "] ");
    }
}
