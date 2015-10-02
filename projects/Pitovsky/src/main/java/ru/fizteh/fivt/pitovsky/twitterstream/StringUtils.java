package main.java.ru.fizteh.fivt.pitovsky.twitterstream;

import java.util.Calendar;
import java.util.Date;

public class StringUtils {
    public static enum TextColor {
        STANDART (0),
        BLACK (30),
        RED (31),
        GREEN (32),
        YELLOW (33),
        BLUE (34),
        MAGENTA (35),
        CYAN (36),
        WHITE (37);

        private int color;
        TextColor(int clr) {
            color = clr;
        }
    }

    private static final char ESCAPE = (char) 27;

    public static String setClr(TextColor tcolor) {
        return "" + ESCAPE + "[" + tcolor.color + "m";
    }
    public static String setStClr() {
        return setClr(TextColor.STANDART);
    }

    private static final int MINUTE = 60 * 1000;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    private static final int NUM_DEC = 10; //checkstyle ask for it
    private static final int NUM_RU_ENDING = 5; //start for new endings
    public static String convertDate(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar tweetCalendar = Calendar.getInstance();
        tweetCalendar.setTime(date);
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            return "еще не опубликовано";
        }
        currentCalendar.roll(Calendar.MINUTE, false);
        currentCalendar.roll(Calendar.MINUTE, false);
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            return "только что";
        }
        currentCalendar = Calendar.getInstance();
        currentCalendar.roll(Calendar.HOUR, false);
        if (tweetCalendar.compareTo(currentCalendar) > 0) {
            int minutes = (int) ((HOUR + currentCalendar.getTimeInMillis()
                    - tweetCalendar.getTimeInMillis()) / MINUTE);
            if (minutes / NUM_DEC != 1 && minutes % NUM_DEC == 1) {
                return minutes + " минуту назад"; //like 1, 21, 31...
            }
            if (minutes / NUM_DEC != 1 && minutes % NUM_DEC > 1
                    && minutes % NUM_DEC < NUM_RU_ENDING) {
                return minutes + " минуты назад";
            }
            return minutes + " минут назад";
        }
        currentCalendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.DAY_OF_YEAR)
                    == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR)
                    == tweetCalendar.get(Calendar.YEAR)) {
            int hours = currentCalendar.get(Calendar.HOUR_OF_DAY)
                    - tweetCalendar.get(Calendar.HOUR_OF_DAY);
            if (hours / NUM_DEC != 1 && hours % NUM_DEC == 1) {
                return hours + " час назад";
            }
            if (hours / NUM_DEC != 1 && hours % NUM_DEC > 1
                    && hours % NUM_DEC < NUM_RU_ENDING) {
                return hours + " часа назад";
            }
            return hours + " часов назад";
        }
        currentCalendar = Calendar.getInstance();
        currentCalendar.roll(Calendar.DATE, false);
        if (currentCalendar.get(Calendar.DAY_OF_YEAR)
                    == tweetCalendar.get(Calendar.DAY_OF_YEAR)
                && currentCalendar.get(Calendar.YEAR)
                    == tweetCalendar.get(Calendar.YEAR)) {
            return "вчера";
        }
        currentCalendar = Calendar.getInstance();
        int days = (int) ((currentCalendar.getTimeInMillis()
                - tweetCalendar.getTimeInMillis()) / DAY) + 1;
        if (days / NUM_DEC != 1 && days % NUM_DEC == 1) {
            return days + " день назад";
        }
        if (days / NUM_DEC != 1 && days % NUM_DEC > 1
                && days % NUM_DEC < NUM_RU_ENDING) {
            return days + " дня назад";
        }
        return days + " дней назад";
    }
}
