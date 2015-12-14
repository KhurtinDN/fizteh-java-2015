package ru.mipt.diht.students.elinrin;

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


    static final String[][] wordPlural = {
            {"ретвит", "ретвита", "ретвитов" },
            {"минута", "минуты", "минут" },
            {"час", "часа", "часов" },
            {"день", "дня", "дней"}
    };

    public static int pluralForm(long numeral) {
        numeral = abs(numeral) % 100;
        if (numeral > 10 && numeral < 20)
            return NOUN_FORM_3;
        if ((numeral % 10) > 1 && (numeral % 10) < 5)
            return NOUN_FORM_2;
        if ((numeral % 10) == 1)
            return NOUN_FORM_1;
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
                    .append(wordPlural[MIN_MOD][pluralForm(goneTime.toMinutes())]).append(" назад");
        } else if (goneTime.toDays() <= 0) {
            result.append(goneTime.toHours()).append(" ")
                    .append(wordPlural[HOUR_MOD][pluralForm(goneTime.toHours())]).append(" назад");
        } else if ( goneTime.toDays() == 1) {
            result.append("Вчера");
        } else {
            result.append(goneTime.toDays()).append(" ")
                    .append(wordPlural[DAY_MOD][pluralForm(goneTime.toDays())]).append(" назад");
        }
        return result.append(" ] ").toString();
    }

    public static String print(Status tweet, boolean stream) {
        StringBuilder result = new StringBuilder();

        if (!stream) {
            result.append(tweetDate(tweet.getCreatedAt()));
        }

        result.append(printUserName(tweet));

        if (tweet.isRetweet() ) {
            result.append("ретвитнул ").append(printUserName(tweet.getRetweetedStatus()))
                    .append(tweet.getRetweetedStatus().getText());
        } else {
            result.append(tweet.getText());
        }
        if (!tweet.isRetweet() && tweet.getRetweetCount() != 0) {
            result.append(" (").append(tweet.getRetweetCount()).append(" ")
                    .append(wordPlural[RETWEET_MOD][pluralForm(tweet.getRetweetCount())]).append(")");
        }

        result.append("\n");
        for (int i = 0; i < LENGTH; i++) {
            result.append("-");
        }

        return result.toString();
    }

    public static String printUserName(Status tweet) {
        StringBuilder result = new StringBuilder();
        result.append("@").append("\033[34m").append(tweet.getUser().getScreenName())
                .append( "\033[0m").append(": ");
        return result.toString();
    }

}
