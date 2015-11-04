package ru.mipt.diht.students.maxDankow.TwitterStream.utils;

import com.google.maps.model.Geometry;
import twitter4j.Status;

import java.util.Calendar;
import java.util.Date;

public class TwitterStreamUtils {
    private static final long MINUTE_MS = 1000 * 60;
    private static final long HOUR_MS = MINUTE_MS * 60;
    private static final long DAY_MS = HOUR_MS * 24;
    // Длинна строки разграничителя твиттов.
    private static final int DELIM_LENGTH = 200;

    public static enum TextColor {
        CLEAR (0),
        BLACK (30),
        RED (31),
        GREEN (32),
        YELLOW (33),
        BLUE (34),
        MAGENTA (35),
        CYAN (36),
        WHITE (37);

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

    public static String convertTimeToRussianWords(Date anotherDate) {
        Calendar currentTime = Calendar.getInstance();
        Calendar anotherTime = Calendar.getInstance();
        anotherTime.setTime(anotherDate);
        long timeDeltaMs = currentTime.getTime().getTime() - anotherTime.getTime().getTime();
        if (timeDeltaMs < 2 * MINUTE_MS) {
            return "Только что";
        }
        if (timeDeltaMs < HOUR_MS) {
            return "" + timeDeltaMs / MINUTE_MS + " минут назад";
        }
        if (currentTime.get(Calendar.DAY_OF_MONTH) == anotherTime.get(Calendar.DAY_OF_MONTH)) {
            return "" + timeDeltaMs / HOUR_MS + " часов назад";
        }
        currentTime.add(Calendar.DAY_OF_MONTH, -1);
        if (currentTime.before(anotherTime)) {
            return "Вчера";
        }
        return "" + timeDeltaMs / DAY_MS + " дней назад";
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

    private static String buildUserName(String originalUserName) {
        return "@" + originalUserName;
    }

    public static void printTweet(Status tweet, boolean shouldShowTime) {
        if (shouldShowTime) {
            System.out.print("["
                    + TwitterStreamUtils.convertTimeToRussianWords(tweet.getCreatedAt())
                    + "] ");
        }
        if (!tweet.isRetweet()) {
            int retweetCount = tweet.getRetweetCount();

            System.out.print(colorizeText(buildUserName(tweet.getUser().getScreenName()), TextColor.BLUE)
                    + ": "
                    + tweet.getText());
            if (retweetCount > 0) {
                System.out.print(" ("
                        + tweet.getRetweetCount()
                        + " ретвитов)");
            }
            System.out.println();
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            System.out.println(colorizeText(buildUserName(tweet.getUser().getScreenName()), TextColor.BLUE)
                    + ": ретвитнул "
                    + colorizeText(buildUserName(originalTweet.getUser().getScreenName()), TextColor.BLUE)
                    + ": "
                    + originalTweet.getText());
        }
        System.out.println(buildDelim());
    }
}
