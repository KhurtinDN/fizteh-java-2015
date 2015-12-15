package ru.mipt.diht.students.glutolik.TwitterStream;

import com.google.maps.model.Geometry;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by glutolik on 13.12.15.
 */
public class TwitterStreamUtils {


    public static enum Colors {
        CLEAR(0),
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        MAGENTA(35),
        CYAN(36),
        WHITE(37);

        private int code;

        Colors(int colorCode) {
            this.code = colorCode;
        }

        public String getColor() {
            return "\033[" + code + "m";
        }
    }

    public static String paint(String text, Colors color) {
        return color.getColor() + text + Colors.CLEAR.getColor();
    }

    public static String spellTime(Date anotherDate, Date currentDate) {
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

    public static boolean check(Status tweet, Geometry location, boolean hide) {
        return (GeolocationUtils.checkLocation(tweet.getPlace(), location) && (!hide || !tweet.isRetweet()));
    }

    public static String paintName(String userName, Colors color) {
        return paint("@" + userName, color);
    }


    public static String formate(Status tweet, boolean showTime) {
        StringBuilder message = new StringBuilder("");
        if (showTime) {
            message.append("[")
                    .append(spellTime(tweet.getCreatedAt(), new Date()))
                    .append("]");
        }
        if (!tweet.isRetweet()) {
            message.append(paintName(tweet.getUser().getScreenName(), Colors.BLUE))
                    .append(": ")
                    .append(tweet.getText());

            if (tweet.getRetweetCount() > 0) {
                message.append(" (")
                        .append(tweet.getRetweetCount())
                        .append(" ретвитов)");
            }
        } else {
            Status nativeTweet = tweet.getRetweetedStatus();
            message.append(paintName(tweet.getUser().getScreenName(), Colors.BLUE))
                    .append(": ретвитнул ")
                    .append(paintName(nativeTweet.getUser().getScreenName(), Colors.BLUE))
                    .append(": ")
                    .append(nativeTweet.getText());
        }
        return message.toString();
    }

    public static void printTweet(Status tweet, boolean showTime) {
        System.out.println(formate(tweet, showTime));
    }
}
