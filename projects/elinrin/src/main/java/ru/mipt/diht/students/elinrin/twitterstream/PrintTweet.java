package ru.mipt.diht.students.elinrin.twitterstream;

import twitter4j.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.Math.abs;

public class PrintTweet {

    static final int LENGTH = 170;
    static final int MIN_MOD = 1;
    static final int HOUR_MOD = 2;
    static final int DAY_MOD = 3;
    static final int RETWEET_MOD = 0;

    static final int NOUN_FORM_1 = 0;
    static final int NOUN_FORM_2 = 1;
    static final int NOUN_FORM_3 = 2;

    static final int ONE = 1;
    static final int FIVE = 5;
    static final int TEN = 10;
    static final int TWENTY = 20;
    static final int HUNDRED = 100;


    static final String BLUE = "\033[34m";
    static final String BLACK = "\033[0m";


    static final String[][] NOUN_FORM = {
            {"ретвит", "ретвита", "ретвитов" },
            {"минута", "минуты", "минут" },
            {"час", "часа", "часов" },
            {"день", "дня", "дней"}
    };

    public static int pluralForm(long numeral) {
        numeral = abs(numeral) % HUNDRED;
        if (numeral > TEN && numeral < TWENTY) {
            return NOUN_FORM_3;
        }
        if ((numeral % TEN) > ONE && (numeral % TEN) < FIVE) {
            return NOUN_FORM_2;
        }
        if ((numeral % TEN) == ONE) {
            return NOUN_FORM_1;
        }
        return NOUN_FORM_3;
    }

    public static String tweetDate(Date givenDate) {

        StringBuilder result = new StringBuilder().append("[ ");

        LocalDateTime givenTime = LocalDateTime.ofInstant(givenDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime nowTime = LocalDateTime.now();
        Duration goneTime = Duration.between(givenTime, nowTime);

        if (goneTime.toMinutes() < 2) {
            result.append("Только что");
        } else if (goneTime.toHours() <= 0) {
            result.append(goneTime.toMinutes()).append(" ")
                    .append(NOUN_FORM[MIN_MOD][pluralForm(goneTime.toMinutes())]).append(" назад");
        } else if (goneTime.toDays() <= 0) {
            result.append(goneTime.toHours()).append(" ")
                    .append(NOUN_FORM[HOUR_MOD][pluralForm(goneTime.toHours())]).append(" назад");
        } else if (goneTime.toDays() == 1) {
            result.append("Вчера");
        } else {
            result.append(goneTime.toDays()).append(" ")
                    .append(NOUN_FORM[DAY_MOD][pluralForm(goneTime.toDays())]).append(" назад");
        }
        return result.append(" ] ").toString();
    }

    public static String print(Status tweet, boolean stream) {
        StringBuilder result = new StringBuilder();

        if (!stream) {
            result.append(tweetDate(tweet.getCreatedAt()));
        }

        result.append(printUserName(tweet));

        if (tweet.isRetweet()) {
            result.append("ретвитнул ").append(printUserName(tweet.getRetweetedStatus()))
                    .append(tweet.getRetweetedStatus().getText());
        } else {
            result.append(tweet.getText());
        }
        if (!tweet.isRetweet() && tweet.getRetweetCount() != 0) {
            result.append(" (").append(tweet.getRetweetCount()).append(" ")
                    .append(NOUN_FORM[RETWEET_MOD][pluralForm(tweet.getRetweetCount())]).append(")");
        }

        result.append("\n");
        for (int i = 0; i < LENGTH; i++) {
            result.append("-");
        }

        return result.toString();
    }

    public static String printUserName(Status tweet) {
        String result  = "@" + BLUE + tweet.getUser().getScreenName()
                + BLACK + ": ";
        return result;
    }

}
