package ru.mipt.diht.studens;

import twitter4j.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.Math.abs;

public class PrintTweet {

    static final int LENGTH = 170;
    static final int MIN = 1;
    static final int HOUR = 2;
    static final int DAY = 3;
    static final int RETW = 0;

    static final int FORM_1 = 0;
    static final int FORM_2 = 1;
    static final int FORM_3 = 2;


    static final String[][] wordPlural = {
            {"ретвит", "ретвита", "ретвитов" },
            {"минута", "минуты", "минут" },
            {"час", "часа", "часов" },
            {"день", "дня", "дней"}
    };

    public static int pluralForm(long n)
    {
        n = abs(n) % 100;
        long n1 = n % 10;
        if (n > 10 && n < 20) return FORM_3;
        if (n1 > 1 && n1 < 5) return FORM_2;
        if (n1 == 1) return FORM_1;
        return FORM_3;
    }

    public static String tweetDate(Date givenDate) {

        StringBuilder result = new StringBuilder().append("[ ");

        LocalDateTime givenTime = LocalDateTime.ofInstant(givenDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime nowTime = LocalDateTime.now();
        Duration goneTime = Duration.between(givenTime, nowTime);

        if (goneTime.toMinutes() < 2) {
            result.append("Только что");
        } else if (goneTime.toHours() <= 0) {
            result.append(goneTime.toMinutes()).append(" ").append(wordPlural[MIN][pluralForm(goneTime.toMinutes())]).append(" назад");
        } else if (goneTime.toDays() <= 0) {
            result.append(goneTime.toHours()).append(" ").append(wordPlural[HOUR][pluralForm(goneTime.toHours())]).append(" назад");
        } else if ( goneTime.toDays() == 1) {
            result.append("Вчера");
        } else {
            result.append(goneTime.toDays()).append(" ").append(wordPlural[DAY][pluralForm(goneTime.toDays())]).append(" назад");
        }
        return result.append(" ] ").toString();
    }

    public static String print(Status tweet, boolean stream) {
        StringBuilder result = new StringBuilder();

        if (!stream) {
            result.append(tweetDate(tweet.getCreatedAt()));
        }

        result.append("@").append("\033[34m").append(tweet.getUser().getScreenName())
                .append( "\033[0m").append(": ");

        if (tweet.isRetweet() ) {
            result.append("ретвитнул @").append("\033[34m").append(tweet.getRetweetedStatus().getUser().getScreenName())
                    .append( "\033[0m ").append(tweet.getRetweetedStatus().getText());
        } else {
            result.append(tweet.getText());
        }
        if (!tweet.isRetweet() && tweet.getRetweetCount() != 0) {
            result.append(" (").append(tweet.getRetweetCount()).append(" ").append(wordPlural[RETW][pluralForm(tweet.getRetweetCount())]).append(")");
        }

        result.append("\n");
        for (int i = 0; i < LENGTH; i++) {
            result.append("-");
        }

        return result.toString();
    }
}
