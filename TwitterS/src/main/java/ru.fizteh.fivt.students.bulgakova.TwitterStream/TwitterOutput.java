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
            stringTweetTime += "������ ���";

        } else if(passedTime < HOUR) {
            Long minutes = passedTime / MINUTE;
            stringTweetTime += minutes.toString() + " ����� �����";

        } else if(ifToday(tweetTime)) {
            Long hours = passedTime / HOUR;
            stringTweetTime += hours.toString() + " ����� �����";

        } else if(ifYesterday(tweetTime)) {
            stringTweetTime += "�����";

        } else {
            Long days = passedTime / DAY;
            stringTweetTime += days.toString() + " ���� �����";

        }

        return stringTweetTime;
    }


    static void printHelp(){
        System.out.println("_________________________________________� ���������_________________________________________");
        System.out.println("TwitterStream - ���������� ����������, ��������� �� ����� ����� ������ �� �������� ��������:");
        System.out.println("\t [--query|-q] - ������ �������� �����, �� �������� ����� �������������� ����� ������");
        System.out.println("\t [--stream|-s] - ���� �������� �����, ���������� ������ ���������� � ���������� � ��������� � ������� �������� ����� �� �����");
        System.out.println("\t [--hideRetweets] - ���� �������� �����, ����� ����������� �������");
        System.out.println("\t [--limit|-l] - ����� ������ ������������� ���������� ������, �� ��������� ��� --stream ������");
        System.out.println("\t [--help|-h] - ������ �������");
    }

    static void printTweet(Status status, Boolean ifTime) {
        String stringTweet = "";

        if (ifTime) {
            stringTweet += "[" + getTweetTime(status) + "] ";
        }

        // "\u001B[34m", "\u001B[0m" - ���� ����
        stringTweet += "\u001B[34m" + " @" + status.getUser().getScreenName() + ": " + "\u001B[0m";

        if (status.isRetweet()) {
            stringTweet += "��������� " + "\u001B[34m" + " @" + status.getRetweetedStatus().getUser().getScreenName() + ": " + "\u001B[0m";
        }

        stringTweet += status.getText();

        if (!status.isRetweet() && status.isRetweeted()) {
            stringTweet += "(" + status.getRetweetCount() + " ��������)";
        }

        System.out.println(stringTweet);

    }

}
