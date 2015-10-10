package ru.mipt.diht.students.ale3otik.twitter;

import com.google.common.base.Strings;
import twitter4j.Status;

/**
 * Created by alex on 10.10.15.
 */
public class TwitterUtil {

    public static final int TRIES_LIMIT = 3;
    private static final int SEPARATOR_LENGTH = 80;

    public static void printSplitLine() {
        System.out.println(Strings.repeat("-", SEPARATOR_LENGTH));
    }

    public static void printFormattedTweet(Status status, ArgumentsStorage arguments) {

        String tweetText = status.getText();
        String time = "";

        if (!arguments.isStream()) {
            time = "["
                    + TimeDeterminer.getTimeDifference(status.getCreatedAt())
                    + "]" + " ";
        }
        String outputString = time + ConsoleColor.getParamsEscape(
                new ConsoleColor.Param[]{ConsoleColor.Param.blue, ConsoleColor.Param.bold})
                + "@"
                + status.getUser()
                .getScreenName()
                + ConsoleColor.getResetEscape()
                + ": ";

        if (status.isRetweet()) {
            int firstNameIndex = tweetText.indexOf('@', 0) + 1;
            int lastNameIndex = tweetText.indexOf(':', firstNameIndex);
            String tweetAuthor = tweetText.substring(firstNameIndex, lastNameIndex - 1);
            tweetText = tweetText.substring(lastNameIndex + 1);

            outputString += "ретвитнул "
                    + ConsoleColor.getParamsEscape(
                    new ConsoleColor.Param[]{ConsoleColor.Param.blue, ConsoleColor.Param.bold})
                    + "@"
                    + tweetAuthor
                    + ConsoleColor.getResetEscape() + ":";
        }

        outputString += tweetText;
        outputString += getRetweetInfo(status);

        System.out.println(outputString);

        printSplitLine();

    }

    private static String getRetweetInfo(Status status) {
        String answerStr = "";

        int countTweets = status.getRetweetCount();
        if (countTweets > 0) {

            String retweetDeclension =
                    FormDeclenser.getTweetsDeclension(countTweets);
            answerStr += "("
                    + ConsoleColor.getParamsEscape(
                    new ConsoleColor.Param[]{ConsoleColor.Param.bold})
                    + countTweets
                    + ConsoleColor.getResetEscape()
                    + " " + retweetDeclension
                    + ")";
        }
        return answerStr;
    }


}
