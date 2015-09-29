package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 29.09.15.
 */
public final class FormDeclenser {
    private static final int RETWEET_INDEX = 0;
    private static final int MINUTS_INDEX = 1;
    private static final int HOURS_INDEX = 2;
    private static final int DAYS_INDEX = 3;

    private static final int MOD100 = 100;
    private static final int MOD10 = 10;
    private static final int SMALL_LEFT_BOUND = 2;
    private static final int SMALL_RIGHT_BOUND = 4;
    private static final int HUGE_RIGHT_BOUND = 19;


    private static final String[][] FORM_ARRAY = {
            {"ретвит", "ретвита", "ретвитов"},
            {"минута", "минуты", "минут"},
            {"час", "часа", "часов"},
            {"день", "дня", "дней"}
    };

    private static int getRightForm(int count) {
        return getRightForm((long) count);
    }

    private static int getRightForm(long count) {
        long hundredMod = count % MOD100;
        if (hundredMod > SMALL_RIGHT_BOUND
                && hundredMod <= HUGE_RIGHT_BOUND) {
            return 2;
        }

        long tenMod = count % MOD10;
        if (tenMod >= SMALL_LEFT_BOUND
                && tenMod <= SMALL_RIGHT_BOUND) {
            return 1;
        }

        if (tenMod == 1) {
            return 0;
        }

        return 2;

    }

    public static String getTweetsDeclension(int count) {
        int formIndex = getRightForm(count);
        return FORM_ARRAY[RETWEET_INDEX][formIndex];
    }

    public static String getMinutsDeclension(long count) {
        int formIndex = getRightForm(count);
        return FORM_ARRAY[MINUTS_INDEX][formIndex];
    }

    public static String getHoursDeclension(long count) {
        int formIndex = getRightForm(count);
        return FORM_ARRAY[HOURS_INDEX][formIndex];
    }

    public static String getDaysDeclension(long count) {
        int formIndex = getRightForm(count);
        return FORM_ARRAY[DAYS_INDEX][formIndex];
    }
}
