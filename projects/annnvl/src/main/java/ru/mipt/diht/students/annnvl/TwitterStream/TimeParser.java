package ru.mipt.diht.students.annnvl.TwitterStream;

import java.time.LocalDateTime;import java.time.ZoneId;import java.time.temporal.ChronoUnit;import java.util.Date;

public class TimeParser {
    public static String printTime(long currentTimeToFormat, long tweetTimeToFormat) {
        Formatter timeFormatter = new Formatter();
        LocalDateTime currentTime = new Date(currentTimeToFormat).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime tweetTime = new Date(tweetTimeToFormat).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (ChronoUnit.MINUTES.between(tweetTime, currentTime) < 2) {
            return "только что";
        } else {
            if (ChronoUnit.HOURS.between(tweetTime, currentTime) < 1) {
                return (new StringBuilder().
                        append(ChronoUnit.MINUTES.between(tweetTime, currentTime)).
                        append(" ").append(timeFormatter.minutes(ChronoUnit.MINUTES.between(tweetTime, currentTime))).
                        append(" назад").toString());
            } else {
                if (ChronoUnit.DAYS.between(tweetTime, currentTime) < 1) {
                    return (new StringBuilder().
                            append(ChronoUnit.HOURS.between(tweetTime, currentTime)).
                            append(" ").append(timeFormatter.hours(ChronoUnit.HOURS.between(tweetTime, currentTime))).
                            append(" назад").toString());
                } else {
                    LocalDateTime currentDayTime = currentTime.toLocalDate().atStartOfDay();
                    LocalDateTime tweetDayTime = tweetTime.toLocalDate().atStartOfDay();
                    if (ChronoUnit.DAYS.between(tweetDayTime, currentDayTime) == 1) {
                        return ("вчера");
                    } else {
                        return (new StringBuilder().
                                append(ChronoUnit.DAYS.between(tweetDayTime, currentDayTime)).
                                append(" ").append(timeFormatter.
                                days(ChronoUnit.DAYS.between(tweetDayTime, currentDayTime))).
                                append(" назад").toString());
                    }
                }
            }
        }
    }
}
