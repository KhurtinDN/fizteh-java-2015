package util;

import twitter4j.Status;
import java.time.LocalDateTime;
import java.time.ZoneId;

enum WordForm { FIRST, SECOND, THIRD }

public class TimeString {

    public static String timeOfTweet(Status status) {

        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime tweetDateTime = LocalDateTime.ofInstant(status.getCreatedAt()
                .toInstant(), ZoneId.systemDefault());

        long minutes = java.time.Duration.between(tweetDateTime, nowDateTime)
                .toMinutes();
        long hours = java.time.Duration.between(tweetDateTime, nowDateTime)
                .toHours();
        long days = java.time.Duration.between(tweetDateTime, nowDateTime)
                .toDays();

        if (days > 0) {
            if (days == 1) {
                return "Вчера";
            } else {
                return formDays(days) + " назад";
            }
        }

        if (hours >= 1) {
            return formHours(hours) + " назад";
        }

        if (minutes <= 1) {
            return "Только что";
        }

        return formMinutes(minutes) + " назад";
    }

    public static WordForm findForm(long number) {
        if (number % 100 >= 11 && number % 100 <= 19) {
            return WordForm.FIRST;
        }
        if (number % 10 == 1) {
            return WordForm.SECOND;
        }
        if (number % 10 >= 2 && number % 10 <= 4) {
            return WordForm.THIRD;
        }
        return WordForm.FIRST;
    }

    public static String formDays(long days) {
        WordForm form = findForm(days);
        switch (form) {
            case FIRST: return days + " дней";
            case SECOND: return days + " день";
            case THIRD: return days + " дня";
            default: return null;
        }
    }


    public static String formHours(long hours) {
        WordForm form = findForm(hours);
        switch (form) {
            case FIRST: return hours + " часов";
            case SECOND: return hours + " час";
            case THIRD: return hours + " часа";
            default: return null;
        }
    }

    public static String formMinutes(long minutes) {
        WordForm form = findForm(minutes);
        switch (form) {
            case FIRST: return minutes + " минут";
            case SECOND: return minutes + " минуту";
            case THIRD: return minutes + " минуты";
            default: return null;
        }
    }


}
