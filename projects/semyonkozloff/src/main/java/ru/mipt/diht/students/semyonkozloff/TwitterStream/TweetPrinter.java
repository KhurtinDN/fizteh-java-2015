package ru.mipt.diht.students.semyonkozloff.twitterstream;

import com.google.common.base.Strings;

import twitter4j.Status;

import java.util.Calendar;
import java.util.Date;

public class TweetPrinter {

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
        System.out.print("[" + ANSI_CYAN);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar currentCalendar = Calendar.getInstance();
        long timeDifference = currentCalendar.getTimeInMillis()
                - calendar.getTimeInMillis();

        if (timeDifference <= 2 * MS_IN_MINUTE) {
            System.out.print("just now");
        } else if (timeDifference <= MS_IN_HOUR) {
            System.out.print((int) (timeDifference / MS_IN_MINUTE)
                    + " minutes ago");
        } else if (currentCalendar.get(Calendar.YEAR)
                == calendar.get(Calendar.YEAR) && // if today
                currentCalendar.get(Calendar.DAY_OF_YEAR)
                        == calendar.get(Calendar.DAY_OF_YEAR)) {
            System.out.print((int) (timeDifference / MS_IN_HOUR)
                    + " hours ago");
        } else if (timeDifference < N_HOURS_IN_DAY * MS_IN_HOUR) {
            System.out.print("yesterday");
        } else {
            System.out.print((int) (timeDifference / MS_IN_DAY) + " days ago");
        }

        System.out.print(ANSI_RESET + "] ");
    }

    private static final int DELIMITER_LENGTH = 160;
    private static final String DELIMITER =
            Strings.repeat("-", DELIMITER_LENGTH);

    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final long MS_IN_DAY = 86400000;
    private static final long MS_IN_HOUR = 3600000;
    private static final long MS_IN_MINUTE = 60000;

    private static final int N_HOURS_IN_DAY = 24;

}
