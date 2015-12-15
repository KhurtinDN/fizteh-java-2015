package ru.mipt.diht.students.nkarpachev.twitterstream;

import twitter4j.Status;

public class PrintTools {
    private static final String ANSI_BLUE = "\033[34m";
    private static final String ANSI_RESET = " \033[0m";

    public static String paintBlue(String userName) {
        return new String(ANSI_BLUE + userName + ANSI_RESET);
    }

    public static void printTweet(Status tweet) {
        StringBuilder outputString = new StringBuilder();
        String date = DateFormatTools.getTweetDate(tweet.getCreatedAt());
        outputString.append("[ " + date + " ]" + "@");
        outputString.append(paintBlue(tweet.getUser().getScreenName()));
        if (!tweet.isRetweet()) {
            outputString.append(":" + tweet.getText() + " ");
            outputString.append("(" + DateFormatTools.getRetweetsCnt(tweet) + ")");
        } else {
            outputString.append(" : " + "ретвитнул " + "@");
            outputString.append(tweet.getRetweetedStatus().getUser().getScreenName());
            outputString.append(": " + tweet.getRetweetedStatus().getText());
        }
        System.out.println(outputString.toString());
    }
}
