package ru.fizteh.fivt.students.bulgakova.TwitterStream;

/**
 * Created by Bulgakova Daria, 496.
 */

import twitter4j.Status;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;

public class TwitterOutput {

    public static final Long SECOND = 1000L;
    public static final Long MINUTE = SECOND * 60;
    public static final Long TWO_MINUTES = MINUTE * 2;
    public static final Long HOUR = MINUTE * 60;
    public static final Long DAY = HOUR * 24;

    static boolean ifToday(GregorianCalendar date) {
        GregorianCalendar today = new GregorianCalendar();

        if ( (today.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) && (today.get(Calendar.MONTH) == date.get(Calendar.MONTH)) && (today.get(Calendar.YEAR) == date.get(Calendar.YEAR))) {
            return true;
        } else {
            return false;
        }
    }

    static boolean ifYesterday(GregorianCalendar date) {
        GregorianCalendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        if ((yesterday.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) && (yesterday.get(Calendar.MONTH) == date.get(Calendar.MONTH)) && (yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR))) {
            return true;
        } else {
            return false;
        }
    }

    static String getTweetTime(Status status) {
        String stringTweetTime = "";
        GregorianCalendar tweetTime = new GregorianCalendar();
        tweetTime.setTime(new Date(status.getCreatedAt().getTime()));
        Long passedTime = (new Date().getTime() - status.getCreatedAt().getTime());

        if(passedTime < TWO_MINUTES) {
            stringTweetTime += "только что";

        } else if(passedTime < HOUR) {
            Long minutes = passedTime / MINUTE;
            stringTweetTime += minutes.toString() + " минут назад";

        } else if(ifToday(tweetTime)) {
            Long hours = passedTime / HOUR;
            stringTweetTime += hours.toString() + " часов назад";

        } else if(ifYesterday(tweetTime)) {
            stringTweetTime += "вчера";

        } else {
            Long days = passedTime / DAY;
            stringTweetTime += days.toString() + " дней назад";

        }

        return stringTweetTime;
    }


    static void printHelp(){
        System.out.println("_________________________________________О ПРОГРАММЕ_________________________________________");
        System.out.println("TwitterStream - консольное приложение, выводящее на экран поток твитов по заданным условиям:");
        System.out.println("\t [--query|-q] - задает ключевое слово, по которому будет осуществляться поиск твитов");
        System.out.println("\t [--stream|-s] - если параметр задан, приложение должно равномерно и непрерывно с задержкой в секунду печатать твиты на экран");
        System.out.println("\t [--hideRetweets] - если параметр задан, нужно фильтровать ретвиты");
        System.out.println("\t [--limit|-l] - вывод только определенного количества твитов, не применимо для --stream режима");
        System.out.println("\t [--help|-h] - печать справки");
    }

    static void printTweet(Status status, Boolean ifTime) {
        String stringTweet = "";

        if (ifTime) {
            stringTweet += "[" + getTweetTime(status) + "] ";
        }

        // "\u001B[34m", "\u001B[0m" - цвет ника
        stringTweet += "\u001B[34m" + " @" + status.getUser().getScreenName() + ": " + "\u001B[0m";

        if (status.isRetweet()) {
            stringTweet += "ретвитнул " + "\u001B[34m" + " @" + status.getRetweetedStatus().getUser().getScreenName() + ": " + "\u001B[0m";
        }

        stringTweet += status.getText();

        if (!status.isRetweet() && status.isRetweeted()) {
            stringTweet += "(" + status.getRetweetCount() + " ретвитов)";
        }

        System.out.println(stringTweet);

    }

}
