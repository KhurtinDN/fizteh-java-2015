package ru.mipt.diht.students.lenazherdeva.twitterStream;

/**
 * Created by admin on 23.10.2015.
 */

public class Formatter {
    @SuppressWarnings("checkstyle:magicnumber")
    private static String formatScheme(long n, String[] cases) {
        n = n % 100;
        if (11 <= n
                && n <= 19) {
            return cases[0];
        }
        n = n % 10;
        if (n == 1) {
            return cases[1];
        }
        if (2 <= n
                && n <= 4) {
            return cases[2];
        }
        return cases[0];
    }

    static final String[] RETWEETS_CASES = {"ретвитов", "ретвит", "ретвита"};
    public static String retweet(long n) {
        return formatScheme(n, RETWEETS_CASES); }

    static final String[] MINUTES_CASES = {"минут", "минуту", "минуты"};
    public static String minutes(long n) {
        return formatScheme(n, MINUTES_CASES); }

    static final String[] HOURS_CASES = {"часов", "час", "часа"};
    public static String hours(long n) {
        return formatScheme(n, HOURS_CASES);
    }

    static final String[] DAYS_CASES = {"дней", "день", "дня"};
    public static String days(long n) {
        return formatScheme(n, DAYS_CASES);
    }
}
