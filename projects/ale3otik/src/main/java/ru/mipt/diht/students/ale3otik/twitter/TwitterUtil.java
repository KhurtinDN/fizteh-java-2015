package ru.mipt.diht.students.ale3otik.twitter;

import com.google.common.base.Strings;
import twitter4j.Status;

/**
 * Created by alex on 10.10.15.
 */
public class TwitterUtil {

    public static final int TRIES_LIMIT = 3;
    private static final int SEPARATOR_LENGTH = 80;

    public static String getSplitLine() {
        return Strings.repeat("-", SEPARATOR_LENGTH);
    }

    public static String getUserNameFromat(final String name) {
        return ConsoleUtil.Style.BLUE.line(ConsoleUtil.Style.BOLD.line("@" + name));
    }

    public static String getFormattedTweetToPrint(Status status, Arguments arguments) {

        String tweetText = status.getText();
        String time = "";

        if (!arguments.isStream()) {
            time = "["
                    + TimeDeterminer.getTimeDifference(status.getCreatedAt())
                    + "]" + " ";
        }
        String outputString = time + getUserNameFromat(status.getUser().getScreenName()) + ": ";

        if (status.isRetweet()) {
            int firstNameIndex = tweetText.indexOf('@', 0) + 1;
            int lastNameIndex = tweetText.indexOf(':', firstNameIndex);
            String tweetAuthor = tweetText.substring(firstNameIndex, lastNameIndex - 1);
            tweetText = tweetText.substring(lastNameIndex + 1);

            outputString += "ретвитнул " + getUserNameFromat(tweetAuthor) + ":";
        }

        outputString += tweetText;
        outputString += getRetweetInfo(status);

        return outputString + '\n' + getSplitLine();
    }

    private static String getRetweetInfo(Status status) {
        String answerStr = "";

        int countTweets = status.getRetweetCount();
        if (countTweets > 0) {

            String retweetDeclension =
                    FormDeclenser.getTweetsDeclension(countTweets);
            answerStr += "("
                    + ConsoleUtil.Style.BOLD.line(new Integer(countTweets).toString())
                    + " " + retweetDeclension
                    + ")";
        }
        return answerStr;
    }
}
