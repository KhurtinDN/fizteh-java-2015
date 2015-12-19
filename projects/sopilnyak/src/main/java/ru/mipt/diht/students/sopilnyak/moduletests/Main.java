package ru.mipt.diht.students.sopilnyak.moduletests;

import ru.mipt.diht.students.sopilnyak.moduletests.library.Arguments;
import ru.mipt.diht.students.sopilnyak.moduletests.library.Results;
import ru.mipt.diht.students.sopilnyak.moduletests.library.UnknownLocationException;
import twitter4j.TwitterException;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if (!Arguments.parse(args)) {
            System.err.println("No query, nothing to find");
            return;
        }

        try {
            Results.sendQuery(Arguments.getQueryString());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }

        System.out.print("Твиты");
        if (Arguments.getQueryString() != null && !Arguments.getQueryString().equals("")) {
            System.out.print(" по запросу \"" + Arguments.getQueryString() + "\"");
        }
        if (Arguments.getIsStreamEnabled()) {
            System.out.print(" в режиме потока");
        }
        if (Arguments.getLocationString() != null && !Arguments.getLocationString().equals("")
                && !Arguments.getIsNearbyEnabled()) {
            System.out.print(" возле местоположения \""
                    + Arguments.getLocationString() + "\"");
            Results.sendGeoQuery(Arguments.getLocationString());
        }
        if (Arguments.getLocationString() != null && (Arguments.getIsNearbyEnabled()
                || Arguments.getLocationString().equals(""))) {
            System.out.print(" возле вашего местоположения");
            Arguments.setIsNearbyEnabled(true);
        } else {
            Arguments.setIsNearbyEnabled(false);
        }
        System.out.println(":");

        try {
            ArrayList<String> result = Results.printResults(Arguments.getQueryString());
            for (String tweet : result) {
                System.out.println(tweet);
            }
        } catch (TwitterException e) {
            System.err.println(e.getMessage());
        } catch (UnknownLocationException e) {
            System.err.println("Невозможно определить местоположение.");
        }

    }

}
