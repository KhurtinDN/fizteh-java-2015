package ru.mipt.diht.students.nkarpachev.twitterstream;

import twitter4j.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.Math.abs;


public class DateFormatTools {

    private static String[] retweetForms = {"ретвит", "ретвита", "ретвитов"};
    private static String[] minuteForms = {"минута", "минуты", "минут"};
    private static String[] hourForms = {"час", "часа", "часов"};
    private static String[] dayForms = {"день", "дня", "дней"};

    private static final int TWO_DIGITS = 10;
    private static final int CHANGED_FORM_BORDER = 5;

    private static final int TIME_FORM_BORDER = 2;
    private static final int MINUTES_IN_HOUR = 60;

    private enum Noun {
        MINUTE, HOUR, DAY, RETWEET
    }

    public static String getTweetDate(Date srcDate) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tweetDateTime = srcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Duration timeDiff = Duration.between(tweetDateTime, currentDateTime);
        String tweetDate;
        if (timeDiff.toDays() >= TIME_FORM_BORDER) {
            tweetDate = getFormattedString((int) timeDiff.toDays(), Noun.DAY) + " назад";
        }
        if (abs(currentDateTime.getDayOfMonth() - tweetDateTime.getDayOfMonth()) != 0) {
            tweetDate = "вчера";
        }
        if (timeDiff.toMinutes() > MINUTES_IN_HOUR) {
            tweetDate = getFormattedString((int) timeDiff.toHours(), Noun.HOUR) + " назад";
        }
        if (timeDiff.toMinutes() >= TIME_FORM_BORDER) {
            tweetDate = getFormattedString((int) timeDiff.toMinutes(), Noun.MINUTE) + " назад";
        } else {
            tweetDate = "только что";
        }
        return tweetDate;
    }

    public static String getRetweetsCnt(Status tweet) {
        int retweetsNumber = tweet.getRetweetCount();
        return getFormattedString(retweetsNumber, Noun.RETWEET);
    }

    private static String getFormattedString(int amount, Noun entity) {
        return amount + " " + makeForm(amount, entity);
    }

    private static String makeForm(int formNumber, Noun entity) {
        formNumber %= TWO_DIGITS;
        String form = new String();
        if ((formNumber == 0) || (formNumber >= CHANGED_FORM_BORDER)) {
            form = getForm(2, entity);
        }
        if (formNumber == 1) {
            form = getForm(0, entity);
        }
        if ((formNumber > 1) && (formNumber < CHANGED_FORM_BORDER)) {
            form = getForm(1, entity);
        }
        return form;
    }

    private static String getForm(int formNumber, Noun entity) {
        String newNoun = new String();
        switch (entity) {
            case RETWEET:
                newNoun = retweetForms[formNumber];
                break;
            case MINUTE:
                newNoun = minuteForms[formNumber];
                break;
            case HOUR:
                newNoun = hourForms[formNumber];
                break;
            case DAY:
                newNoun = dayForms[formNumber];
                break;
            default:
                break;
        }
        return newNoun;
    }
}



