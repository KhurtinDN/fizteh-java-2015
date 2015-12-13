package ru.mipt.diht.students.nkarpachev.twitterstream;

import twitter4j.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.Math.abs;


public class DateFormatTools {

    public static String getTweetDate(Date srcDate) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tweetDateTime = srcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Duration timeDiff = Duration.between(tweetDateTime, currentDateTime);
        if (timeDiff.toDays() >= 2) {
            return getFormattedString((int) timeDiff.toDays(), "день") + " назад";
        }
        if (abs(currentDateTime.getDayOfMonth() - tweetDateTime.getDayOfMonth()) != 0) {
            return "вчера";
        }
        if (timeDiff.toMinutes() > 60) {
            return getFormattedString((int) timeDiff.toHours(), "час") + " назад";
        }
        if (timeDiff.toMinutes() >= 2) {
            return getFormattedString((int) timeDiff.toMinutes(), "минута") + " назад";
        } else return "только что";
    }
    
    public static String getRetweetsCnt(Status tweet) {
        int retweetsNumber = tweet.getRetweetCount();
        return getFormattedString(retweetsNumber, "ретвит");
    }

    private static String getFormattedString(int amount, String measure) {
        return amount + makeForm(getForm(amount), measure);
    }

    private static String getForm(int formNumber) {
        formNumber %= 100;
        if (formNumber / 10 == 1) {
            return "PL";
        }
        formNumber %= 10;
        String form = "I";
        switch (formNumber) {
            case 1:
                form = "I";
            break;
            case 2:
            case 3:
            case 4:
                // C - changed form
                form = "C";
            break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                // PL - changed and plural form
                form = "PL";
            break;

        }
        return form;
    }

    private static String makeForm(String form, String noun) {
        String nounForm = "единиц";
        switch (noun) {
            case "минута":
                if (form.equals("I")) {
                    nounForm = " минуту";
                }
                if (form.equals("C")) {
                    nounForm = " минуты";
                }
                if (form.equals("PL")) {
                    nounForm = " минут";
                }
                break;

            case "час":
                if (form.equals("I")) {
                    nounForm = " час";
                }
                if (form.equals("C")) {
                    nounForm = " часа";
                }
                if (form.equals("PL")) {
                    nounForm = " часов";
                }
                break;

            case "день":
                if (form.equals("I")) {
                    nounForm = " день";
                }
                if (form.equals("C")) {
                    nounForm = " дня";
                }
                if (form.equals("PL")) {
                    nounForm = " дней";
                }
                break;

            case "ретвмт" :
                if (form.equals("I")) {
                    nounForm = " ретвит";
                }
                if (form.equals("C")) {
                    nounForm = " ретвита";
                }
                if (form.equals("PL")) {
                    nounForm = " ретвитов";
                }
                break;
        }
        return nounForm;
    }
}



