/**
 * Created by admin on 23.10.2015.
 */

public class Formatter {
    static final int MOD100 = 100;
    static final int MOD10 = 10;
    static final int ELEVEN = 11;
    static final int NINETEEN = 19;
    static final int ONE = 1;
    static final int TWO = 2;
    static final int FOUR = 4;

    private static String declensionScheme(long n, String[] cases) {
        n = n % MOD100;
        if (ELEVEN <= n
                && n <= NINETEEN) {
            return cases[0];
        }

        n = n % MOD10;
        if (n == ONE) {
            return cases[1];
        }
        if (TWO <= n
                && n <= FOUR) {
            return cases[2];
        }
        return cases[0];
    }

    static final String[] RETWEETS_CASES = {"ретвитов", "ретвит", "ретвита"};
    static String retweet(long n) {
        return declensionScheme(n, RETWEETS_CASES);
    }

    static final String[] MINUTES_CASES = {"минут", "минуту", "минуты"};
    static String minutes(long n) {
        return declensionScheme(n, MINUTES_CASES);
    }

    static final String[] HOURS_CASES = {"часов", "час", "часа"};
    static String hours(long n) {
        return declensionScheme(n, HOURS_CASES);
    }

    static final String[] DAYS_CASES = {"дней", "день", "дня"};
    static String days(long n) {
        return declensionScheme(n, DAYS_CASES);
    }
}

