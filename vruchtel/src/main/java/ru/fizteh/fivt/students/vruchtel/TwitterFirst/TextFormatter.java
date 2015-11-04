package ru.fizteh.fivt.students.vruchtel.TwitterFirst;

/**
 * Created by Серафима on 09.10.2015.
 */

import twitter4j.Status;

import java.util.Stack;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;

public class TextFormatter {
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

    static String getTweetTime(Status status) {
        String printingTime = "";
        GregorianCalendar tweetTime = new GregorianCalendar();
        tweetTime.setTime(new Date(status.getCreatedAt().getTime()));

        Long passedTime = (new Date().getTime() - status.getCreatedAt().getTime());

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

    static String getColorisedNick(Status status) {
        return ANSI_BLUE + "@" + status.getUser().getScreenName() + ": " + ANSI_RESET;
    }

    static String getTextToPrint(Status status, boolean printTime) {
        String printingText = "";

        if(printTime) {
            printingText += "[" + getTweetTime(status) + "] ";
        }

        printingText += getColorisedNick(status);

        if(status.isRetweet()) {
            printingText += "ретвитнул " + getColorisedNick(status.getRetweetedStatus());
        }

        printingText += status.getText();

        if(!status.isRetweet() && status.isRetweeted()) {
            printingText += " (" + status.getRetweetCount() + " ретвитов)";
        }

        return printingText;
    }

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static final Long SECOND = 1000L;
    public static final Long MINUTE = SECOND * 60;
    public static final Long TWO_MINUTES = MINUTE * 2;
    public static final Long HOUR = MINUTE * 60;
    public static final Long DAY = HOUR * 24;
}
