/**
 * Created by admin on 23.10.2015.
 */

public class TimeFormatter {
    public static final long TEN = 10;
    public static final long FIVE = 5;
    public static final long TWELVE = 12;
    public static final long ELEVEN = 11;
    public static final long DAY = 1000 * 60 * 60 * 24;

    public static String minutes(long minutes) {
        if (minutes % TEN == 1 && minutes != ELEVEN) {
            return "минуту";
        } else {
            if (minutes % TEN > 1 && minutes % TEN < FIVE
                    && minutes != TWELVE) {
                return "минуты";
            } else {
                return "минут";
            }
        }
    }
    public static String hours(long hours) {
        if (hours % TEN == 1 && hours != ELEVEN) {
            return "час";
        } else {
            if (hours % TEN > 1 && hours % TEN < FIVE
                    && hours != TWELVE) {
                return "часа";
            } else {
                return "часов";
            }
        }
    }
    public static String days(long days) {
        if (days % TEN == 1 && days != ELEVEN) {
            return "день";
        } else {
            if (days % TEN > 1 && days % TEN < FIVE
                    && days != TWELVE) {
                return "дня";
            } else {
                return "дней";
            }
        }
    }
    public static boolean today(long tweettime, long currentTime) {
        long currentDay = currentTime / DAY;
        long todayTime = currentTime - currentDay * DAY;
        return todayTime >= currentTime - tweettime;
    }
    public static boolean yesterday(long tweettime, long currentTime) {
        long currentDay = currentTime / DAY;
        long todayTime = currentTime - (currentDay - 1) * DAY;
        return todayTime >= currentTime - tweettime;
    }
}

