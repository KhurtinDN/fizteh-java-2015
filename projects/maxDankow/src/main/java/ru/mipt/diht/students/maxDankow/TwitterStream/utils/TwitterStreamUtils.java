package ru.mipt.diht.students.maxDankow.TwitterStream.utils;

import com.google.maps.model.Geometry;
import org.apache.commons.lang3.StringUtils;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TwitterStreamUtils {
    // Разграничитель твиттов.
    private static final int DELIMITER_LENGTH = 200;
    public static final String delimiter = StringUtils.repeat("-", DELIMITER_LENGTH);

    public static enum TextColor {
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

        TextColor(int colorCode) {
            this.colorCode = colorCode;
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
            return ChronoUnit.MINUTES.between(anotherTime, currentTime) + " минут назад";
        }
        if (ChronoUnit.DAYS.between(anotherTime, currentTime) < 1) {
            return ChronoUnit.HOURS.between(anotherTime, currentTime) + " часов назад";
        }
        if (ChronoUnit.DAYS.between(anotherTime, currentTime) == 1) {
            return "Вчера";
        }
        return ChronoUnit.DAYS.between(anotherTime, currentTime) + " дней назад";
    }

    public static boolean checkTweet(Status tweet,
                                     Geometry locationGeometry,
                                     boolean shouldHideRetweets) {
        return (GeolocationUtils.checkLocation(tweet.getPlace(), locationGeometry)
                && (!shouldHideRetweets || !tweet.isRetweet()));
    }

    public static String buildColorizedUserName(String originalUserName, TextColor color) {
        return colorizeText("@" + originalUserName, color);
    }

    public static String buildFormattedTweet(Status tweet, boolean shouldShowTime) {
        StringBuilder message = new StringBuilder("");
        if (shouldShowTime) {
            message.append("[")
                    .append(convertTimeToRussianWords(tweet.getCreatedAt(), new Date()))
                    .append("]");
        }
        if (!tweet.isRetweet()) {
            message.append(buildColorizedUserName(tweet.getUser().getScreenName(), TextColor.BLUE))
                    .append(": ")
                    .append(tweet.getText());

            if (tweet.getRetweetCount() > 0) {
                message.append(" (")
                        .append(tweet.getRetweetCount())
                        .append(" ретвитов)");
            }
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            message.append(buildColorizedUserName(tweet.getUser().getScreenName(), TextColor.BLUE))
                    .append(": ретвитнул ")
                    .append(buildColorizedUserName(originalTweet.getUser().getScreenName(), TextColor.BLUE))
                    .append(": ")
                    .append(originalTweet.getText());
        }
        return message.toString();
    }

    public static void printTweet(Status tweet, boolean shouldShowTime) {
        System.out.println(buildFormattedTweet(tweet, shouldShowTime));
        System.out.println(delimiter);
    }
}
