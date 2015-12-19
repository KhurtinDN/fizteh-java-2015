package ru.mipt.diht.students.annnvl.TwitterStream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PrintTime {

    public static String printTime(long tweetForm, long curForm) {

        Format timeFormat = new Format();

        LocalDateTime curTime = new Date(curForm).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime tweetTime = new Date(tweetForm).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (ChronoUnit.MINUTES.between(tweetTime, curTime) < 2) {
            return "только что";
        } else {
            if (ChronoUnit.HOURS.between(tweetTime, curTime) < 1) {
                return new StringBuilder().append(ChronoUnit.MINUTES.between(tweetTime, curTime))
                        .append(timeFormat.MINUTES[timeFormat.strForm(ChronoUnit.MINUTES.between(tweetTime, curTime))])
                        .append("назад").toString();
            } else {
                if (ChronoUnit.DAYS.between(tweetTime, curTime) < 1) {
                    return new StringBuilder().append(ChronoUnit.HOURS.between(tweetTime, curTime))
                            .append(timeFormat.HOURS[timeFormat.strForm(ChronoUnit.HOURS.between(tweetTime, curTime))])
                            .append("назад").toString();
                } else {
                    LocalDateTime tweetDateTime = tweetTime.toLocalDate().atStartOfDay();
                    LocalDateTime curDateTime = curTime.toLocalDate().atStartOfDay();
                    if (ChronoUnit.DAYS.between(tweetDateTime, curDateTime) == 1) {
                        return "вчера";
                    } else {
                        return new StringBuilder().append(ChronoUnit.DAYS.between(tweetDateTime, curDateTime))
                                .append(timeFormat.DAYS[timeFormat.strForm(ChronoUnit.DAYS.between(tweetDateTime, curDateTime))])
                                .append("назад").toString();
                    }
                }
            }
        }
    }
}
