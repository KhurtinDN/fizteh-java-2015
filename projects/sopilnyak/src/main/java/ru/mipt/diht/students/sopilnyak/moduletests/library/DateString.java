package ru.mipt.diht.students.sopilnyak.moduletests.library;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateString {

    public static String getDate(Date date) {
        Calendar currentCal = Calendar.getInstance();
        return getDate(date, currentCal);
    }

    public static String getDate(Date date, Calendar currentCal) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date current = currentCal.getTime();

        final long diffFull = current.getTime() - date.getTime();

        if (calendar.get(Calendar.YEAR)
                == currentCal.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR)
                == currentCal.get(Calendar.DAY_OF_YEAR) - 1) { // yesterday
            return "Вчера";
        }

        long diffDays = TimeUnit.DAYS.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffDays > 0) {
            return "" + diffDays + " дней назад";
        }

        long diffHours = TimeUnit.HOURS.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffHours >= 1) {
            return "" + diffHours + " часов назад";
        }

        long diffMinutes = TimeUnit.MINUTES.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffMinutes >= 2) {
            return "" + diffMinutes + " минут назад";
        }

        return "Только что";

    }

}
