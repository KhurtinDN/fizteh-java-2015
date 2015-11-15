package ru.mipt.diht.students.tveritinova.TwitterStream;

import twitter4j.Status;

import java.util.Calendar;
import java.util.Date;

class TimeFormat {
    private static final long ONE_MINUTE = 60000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000;
    public static final int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;

    static String getTimeFormat(Status currentStatus, Calendar cal,
                                Date now) {
        //int dayOfYear = Calendar.DAY_OF_YEAR;
        String timeFormat = "";
        boolean key = false;

        long statusTime = currentStatus.getCreatedAt().getTime();

        cal.setTime(now);
        //только что

        if (cal.getTimeInMillis() - statusTime < 2 * ONE_MINUTE) {
            timeFormat = "[только что]";
            key = true;
        }

        //n минут назад

        if ((cal.getTimeInMillis() - statusTime < ONE_HOUR) && (!key)) {
            int minutesCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_MINUTE);
            timeFormat = "[" + minutesCount + " минут назад]";
            key = true;
        }

        //n часов назад

        int dayOfYearNow = cal.get(DAY_OF_YEAR);
        cal.setTime(currentStatus.getCreatedAt());
        int dayOfYearTweeet = cal.get(DAY_OF_YEAR);

        if (!key && (dayOfYearNow == dayOfYearTweeet)) {
            cal.setTime(now);
            int hoursCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_HOUR);
            timeFormat = "[" + hoursCount + " часов назад]";
            key = true;
        }

        //вчера
        cal.setTime(now);
        if (!key && ((dayOfYearNow == dayOfYearTweeet + 1)
                || ((dayOfYearTweeet == cal.getActualMaximum(DAY_OF_YEAR))
                && (dayOfYearNow == 1)))) {
            timeFormat = "[вчера]";
            key = true;
        }

        //n дней назад
        cal.setTime(now);
        if (!key) {
            int daysCount = (int) ((cal.getTimeInMillis() - statusTime)
                    / ONE_DAY);
            timeFormat = "[" + daysCount + " дней назад]";
        }

        return timeFormat;
    }
}
