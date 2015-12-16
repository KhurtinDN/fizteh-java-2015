package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.google.common.base.Strings;

import twitter4j.Status;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class TweetPrinter {

    private static final int DELIMITER_LENGTH = 160;

    private static final String DELIMITER =
            Strings.repeat("-", DELIMITER_LENGTH);

    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private Writer writer;

    public TweetPrinter(Writer initialWriter) {
        this.writer = initialWriter;
    }

    public void printTweet(Status tweet) throws IOException {
        writer.write("@" + ANSI_BLUE
                + tweet.getUser().getName() + ANSI_RESET + ": ");

        if (tweet.isRetweet()) {
            writer.write("retweeted "
                    + "@" + ANSI_BLUE
                    + tweet.getRetweetedStatus().getUser().getName()
                    + ANSI_RESET
                    + ": " + tweet.getText());
        } else {
            System.out.print(tweet.getText() + " ");
            if (tweet.isRetweeted()) {
                writer.write("(" + ANSI_GREEN + tweet.getRetweetCount()
                        + " retweets"
                        + ANSI_RESET + ")");
            }
        }
        writer.write("\n" + DELIMITER + "\n");
    }

    public void printTime(Date date) throws IOException {

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tweetTime = date.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        writer.write("[" + ANSI_CYAN);

        if (ChronoUnit.MINUTES.between(tweetTime, currentTime) < 2) {
            writer.write("just now");
        } else if (ChronoUnit.HOURS.between(tweetTime, currentTime) < 1) {
            writer.write(ChronoUnit.MINUTES.between(tweetTime, currentTime)
                    + " minutes ago");
        } else if (ChronoUnit.DAYS.between(tweetTime, currentTime) < 1) {
            writer.write(ChronoUnit.HOURS.between(tweetTime, currentTime)
                    + " hours ago");
        } else if (ChronoUnit.DAYS.between(tweetTime, currentTime) == 1) {
            writer.write("yesterday");
        } else {
            writer.write(ChronoUnit.DAYS.between(tweetTime, currentTime)
                    + " days ago");
        }

        writer.write(ANSI_RESET + "] ");
    }
}
