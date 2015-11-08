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

        return getDifferenceOfDates(tweetTime, currentTime);
    }

    public static String getDifferenceOfDates(LocalDateTime firstDate, LocalDateTime secondDate) {
        if (ChronoUnit.MINUTES.between(firstDate, secondDate) < 2) {
            return "только что";
        }


        if (ChronoUnit.HOURS.between(firstDate, secondDate) < 1) {
            long minutDifference = ChronoUnit.MINUTES.between(firstDate, secondDate);
            return minutDifference + " "
                    + FormDeclenser.getMinutesDeclension(minutDifference)
                    + " назад";
        }

        LocalDateTime secondDayDate = secondDate.toLocalDate().atStartOfDay();
        LocalDateTime firstDayDate = firstDate.toLocalDate().atStartOfDay();

        if (ChronoUnit.DAYS.between(firstDayDate, secondDayDate) < 1) {
            long hoursDifference = ChronoUnit.HOURS.between(firstDate, secondDate);
            return hoursDifference + " "
                    + FormDeclenser.getHoursDeclension(hoursDifference)
                    + " назад";
        }

        if (ChronoUnit.DAYS.between(firstDayDate, secondDayDate) == 1) {
            return "вчера";
        }

        long daysDifference = ChronoUnit.DAYS.between(firstDayDate, secondDayDate);
        return (daysDifference) + " "
                + FormDeclenser.getDaysDeclension(daysDifference)
                + " назад";
    }
}
