package ru.fizteh.fivt.students.vruchtel.moduletests.library;

import twitter4j.Status;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Серафима on 24.11.2015.
 */
public class TimeFormatter {
    static boolean isToday(GregorianCalendar date) {
        GregorianCalendar todayDate = new GregorianCalendar();

        return  todayDate.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                && todayDate.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                && todayDate.get(Calendar.YEAR) == date.get(Calendar.YEAR);
    }

    static boolean isYesterday(GregorianCalendar date) {
        GregorianCalendar yesterdayDate = new GregorianCalendar();
        yesterdayDate.add(Calendar.DAY_OF_MONTH, -1);

        return  yesterdayDate.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                && yesterdayDate.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                && yesterdayDate.get(Calendar.YEAR) == date.get(Calendar.YEAR);
    }

    static String getTweetTime(Long tweetCreatedTime) {
        String printingTime = "";
        GregorianCalendar tweetTime = new GregorianCalendar();
        tweetTime.setTime(new Date(tweetCreatedTime));

        Long passedTime = (new Date().getTime() - tweetCreatedTime);

        if(passedTime < TWO_MINUTES) {
            printingTime += "Только что";
        } else if(passedTime < HOUR) {
            Long minutes = passedTime / MINUTE;
            printingTime += minutes.toString() + " минут назад";
        } else if(isToday(tweetTime)) {
            Long hours = passedTime / HOUR;
            printingTime += hours.toString() + " часов назад";
        } else if(isYesterday(tweetTime)) {
            printingTime += "Вчера";
        } else {
            Long days = passedTime / DAY;
            printingTime += days.toString() + " дней назад";
        }

        return printingTime;
    }

    public static final Long SECOND = 1000L;
    public static final Long MINUTE = SECOND * 60;
    public static final Long TWO_MINUTES = MINUTE * 2;
    public static final Long HOUR = MINUTE * 60;
    public static final Long DAY = HOUR * 24;
}
