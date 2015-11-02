package ru.mipt.diht.students.ale3otik.twitter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by alex on 29.09.15.
 */
public class TimeDeterminer {

    public static String getTimeDifference(Date createdAt) {

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime tweetTime = createdAt
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (ChronoUnit.MINUTES.between(tweetTime, currentTime) < 2) {
            return "Только что";
        }


        if (ChronoUnit.HOURS.between(tweetTime, currentTime) < 1) {
            long minutDifference = ChronoUnit.MINUTES.between(tweetTime, currentTime);
            return minutDifference + " "
                    + FormDeclenser.getMinutesDeclension(minutDifference)
                    + " назад";
        }

        LocalDateTime currentDayDate = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime tweetDayDate = tweetTime.toLocalDate().atStartOfDay();

        if (ChronoUnit.DAYS.between(tweetDayDate, currentDayDate) < 1) {
            long hoursDifference = ChronoUnit.HOURS.between(tweetTime, currentTime);
            return hoursDifference + " "
                    + FormDeclenser.getHoursDeclension(hoursDifference)
                    + " назад";
        }

        if (ChronoUnit.DAYS.between(tweetDayDate, currentDayDate) == 1) {
            return "вчера";
        }

        long daysDifference = ChronoUnit.DAYS.between(tweetDayDate, currentDayDate);
        return (daysDifference) + " "
                + FormDeclenser.getDaysDeclension(daysDifference)
                + " назад";
    }
}
