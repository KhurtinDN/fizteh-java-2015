package ru.mipt.diht.students.maxDankow.TwitterStream.utils;

import com.google.maps.model.Geometry;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TwitterStreamUtils {
    private static final long MINUTE_MS = 1000 * 60;
    private static final long HOUR_MS = MINUTE_MS * 60;
    private static final long DAY_MS = HOUR_MS * 24;
    // Длинна строки разграничителя твиттов.
    private static final int DELIM_LENGTH = 200;

    public enum TextColor {
        CLEAR(0),
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        MAGENTA(35),
        CYAN(36),
        WHITE(37);

        private int colorCode;

        TextColor(int newColorCode) {
            colorCode = newColorCode;
        }

        public String getEscapeCodePrefix() {
            return "\033[" + colorCode + "m";
        }
    }

    public static String colorizeText(String text, TextColor color) {
        return color.getEscapeCodePrefix() + text + TextColor.CLEAR.getEscapeCodePrefix();
    }

    public static String convertTimeToRussianWords(Date anotherDate, Date currentDate) {
        LocalDateTime currentTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime anotherTime = anotherDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (ChronoUnit.MINUTES.between(anotherTime, currentTime) < 2) {
            return "Только что";
        }
        if (ChronoUnit.HOURS.between(anotherTime, currentTime) < 1) {
            return "" + ChronoUnit.MINUTES.between(anotherTime, currentTime) + " минут назад";
        }
        if (ChronoUnit.DAYS.between(anotherTime, currentTime) < 1) {
            return "" + ChronoUnit.HOURS.between(anotherTime, currentTime) + " часов назад";
        }
        if (ChronoUnit.DAYS.between(anotherTime, currentTime) == 1) {
            return "Вчера";
        }
        return "" + ChronoUnit.DAYS.between(anotherTime, currentTime) + " дней назад";
    }

    public static boolean checkTweet(Status tweet, Geometry locationGeometry,
                                     boolean shouldHideRetweets) {
        return (GeolocationUtils.checkLocation(tweet.getPlace(), locationGeometry)
                && (!shouldHideRetweets || !tweet.isRetweet()));
    }

    private static String buildDelim() {
        String delim = "-";
        for (int i = 0; i < DELIM_LENGTH; ++i) {
            delim = delim.concat("-");
        }
        return delim;
    }

    public static String buildUserName(String originalUserName) {
        return "@" + originalUserName;
    }

    public static String buildFormattedTweet(Status tweet, boolean shouldShowTime) {
        StringBuilder message = new StringBuilder("");
        if (shouldShowTime) {
            message.append("[");
            message.append(TwitterStreamUtils.convertTimeToRussianWords(tweet.getCreatedAt(), new Date()));
            message.append("]");
        }
        if (!tweet.isRetweet()) {
            int retweetCount = tweet.getRetweetCount();

            message.append(colorizeText(buildUserName(tweet.getUser().getScreenName()), TextColor.BLUE));
            message.append(": ");
            message.append(tweet.getText());
            if (retweetCount > 0) {
                message.append(" (");
                message.append(tweet.getRetweetCount());
                message.append(" ретвитов)");
            }
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            message.append(colorizeText(buildUserName(tweet.getUser().getScreenName()), TextColor.BLUE));
            message.append(": ретвитнул ");
            message.append(colorizeText(buildUserName(originalTweet.getUser().getScreenName()), TextColor.BLUE));
            message.append(": ");
            message.append(originalTweet.getText());
        }
        return message.toString();
    }

    public static void printTweet(Status tweet, boolean shouldShowTime) {
        System.out.println(buildFormattedTweet(tweet, shouldShowTime));
        System.out.println(buildDelim());
    }
}
