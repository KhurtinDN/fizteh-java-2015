package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 29.09.15.
 */
public final class FormDeclenser {

    private static final String[] RETWEET_ARRAY = {"ретвит", "ретвита", "ретвитов"};
    private static final String[] MINUTES_ARRAY = {"минута", "минуты", "минут"};
    private static final String[] HOURS_ARRAY = {"час", "часа", "часов"};
    private static final String[] DAYS_ARRAY = {"день", "дня", "дней"};


    private static int getRightForm(int count) {
        return getRightForm((long) count);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private static int getRightForm(long count) {
        long hundredMod = count % 100;
        if (hundredMod > 4 && hundredMod <= 19) {
            return 2;
        }

        long tenMod = count % 10;
        if (tenMod >= 2 && tenMod <= 4) {
            return 1;
        }

        if (tenMod == 1) {
            return 0;
        }

        return 2;
    }

    public static String getTweetsDeclension(int count) {
        int formIndex = getRightForm(count);
        return RETWEET_ARRAY[formIndex];
    }

    public static String getMinutesDeclension(long count) {
        int formIndex = getRightForm(count);
        return MINUTES_ARRAY[formIndex];
    }

    public static String getHoursDeclension(long count) {
        int formIndex = getRightForm(count);
        return HOURS_ARRAY[formIndex];
    }

    public static String getDaysDeclension(long count) {
        int formIndex = getRightForm(count);
        return DAYS_ARRAY[formIndex];
    }
}
