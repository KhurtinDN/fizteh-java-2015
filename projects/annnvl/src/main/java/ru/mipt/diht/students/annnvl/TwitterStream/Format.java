package ru.mipt.diht.students.annnvl.TwitterStream;

public class Format {

    public static final String[] MINUTES = {" минуту ", " минуты ", " минут "};
    public static final String[] HOURS = {" час ", " часа ", " часов "};
    public static final String[] DAYS = {" день ", " дня ", " дней "};
    public static final String[] RETWEETS = {" ретвит", " ретвита", " ретвитов"};

    public static final short FIVE = 5;
    public static final short TEN = 10;
    public static final short ELEVEN = 11;
    public static final long FIFTEEN = 15;
    public static final long HUNDRED = 100;

    public static int strForm(long number) {
        if (number % TEN == 1 && number % HUNDRED != ELEVEN) {
            return 0;
        }
        if (number % TEN > 1 && number % TEN < FIVE && (number % HUNDRED < FIVE || number % HUNDRED > FIFTEEN)) {
            return 1;
        }
        return 2;
    }
}


