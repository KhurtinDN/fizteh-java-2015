package ru.fizteh.fivt.students.vruchtel.moduletests.library;

/**
 * Created by Серафима on 24.11.2015.
 */
import twitter4j.Status;

import java.util.Stack;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;

public class TweetsFormatter {
    static String getColorisedNick(Status status) {
        return ANSI_BLUE + "@" + status.getUser().getScreenName() + ": " + ANSI_RESET;
    }

    static String getTextToPrint(Status status, boolean printTime) {
        String printingText = "";

        if(printTime) {
            printingText += "[" + timeFormatter.getTweetTime(status.getCreatedAt().getTime()) + "] ";
        }

        printingText += getColorisedNick(status);

        if(status.isRetweet()) {
            printingText += "ретвитнул " + getColorisedNick(status.getRetweetedStatus());
        }

        printingText += status.getText();

        if(!status.isRetweet() && status.isRetweeted()) {
            printingText += " (" + status.getRetweetCount() + " ретвитов)";
        }

        return printingText;
    }

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static TimeFormatter timeFormatter;
}