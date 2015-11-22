package ru.mipt.diht.students.tveritinova.TwitterStream;

import java.util.Calendar;
import java.util.Date;

public class TimeFormat {
    private static final long ONE_MINUTE = 60000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000;
    public static final int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;

    public static String getTimeFormat(Date current, Calendar cal,
                                Date now) {

        long statusTime = current.getTime();
        cal.setTime(now);

        //только что

        if (cal.getTimeInMillis() - statusTime < 2 * ONE_MINUTE) {
            return "[только что]";
        }

        //n минут назад

        if (cal.getTimeInMillis() - statusTime < ONE_HOUR) {
            int minutesCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_MINUTE);
            return "[" + minutesCount + " минут назад]";
        }

        //n часов назад

        int dayOfYearNow = cal.get(DAY_OF_YEAR);
        cal.setTime(current);
        int dayOfYearTweeet = cal.get(DAY_OF_YEAR);

        if (dayOfYearNow == dayOfYearTweeet) {
            cal.setTime(now);
            int hoursCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_HOUR);
            return "[" + hoursCount + " часов назад]";
        }

        //вчера
        cal.setTime(now);
        if ((dayOfYearNow == dayOfYearTweeet + 1)
                || ((dayOfYearTweeet == cal.getActualMaximum(DAY_OF_YEAR))
                && (dayOfYearNow == 1))) {
            return "[вчера]";
        }

        //n дней назад
        cal.setTime(now);
        int daysCount = (int) ((cal.getTimeInMillis() - statusTime) / ONE_DAY);
            return "[" + daysCount + " дней назад]";
    }
}
