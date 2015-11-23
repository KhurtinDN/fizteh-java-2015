package ru.mipt.diht.students.nkarpachev.twitterstream;

import twitter4j.Status;

public class PrintTools {

    public static void printColored(String userName) {
        System.out.print("\033[34m" + userName + " \033[0m");
    }

    public static void printTweet(Status tweet) {
        if (!tweet.isRetweet()) {
            String date = DateFormatTools.getTweetDate(tweet.getCreatedAt());
            System.out.print("[ " + date + " ]" + "@");
            printColored(tweet.getUser().getScreenName());
            System.out.print(":" + tweet.getText());
            System.out.println(DateFormatTools.getRetweetsCnt(tweet));

        }
        else {
            String date = DateFormatTools.getTweetDate(tweet.getCreatedAt());
            System.out.print("[ " + date + " ]" + "@");
            printColored(tweet.getUser().getScreenName());
            System.out.print(" : " + "ретвитнул " + "@");
            printColored(tweet.getRetweetedStatus().getUser().getScreenName());
            System.out.println(": " + tweet.getRetweetedStatus().getText());
        }
    }
}
