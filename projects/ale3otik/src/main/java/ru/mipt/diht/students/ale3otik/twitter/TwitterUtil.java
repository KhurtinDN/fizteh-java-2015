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

    public static String getUserNameStyle(final String name) {
        return ConsoleUtil.Style.BLUE.line(ConsoleUtil.Style.BOLD.line("@" + name));
    }

    public static String getFormattedTweetToPrint(Status status, Arguments arguments) {

        String time = "";

        if (!arguments.isStream()) {
            time = "["
                    + TimeDeterminer.getTimeDifference(status.getCreatedAt())
                    + "]" + " ";
        }
        StringBuilder outputString = new StringBuilder();
        outputString.append(time + getUserNameStyle(status.getUser().getScreenName()) + ": ");

        String tweetText = status.getText();
        if (status.isRetweet()) {
            AuthorNameParser parser = new AuthorNameParser(tweetText);
            outputString
                    .append("ретвитнул ")
                    .append(getUserNameStyle(parser.getTweetAuthor()))
                    .append(":");
            tweetText = tweetText.substring(parser.getEndIndex());
        }

        outputString.append(tweetText).append(getRetweetInfo(status));
        outputString.append('\n').append(getSplitLine());
        return outputString.toString();
    }

    private static class AuthorNameParser {
        private String tweetAuthor;
        private int endParsingIndex;

        AuthorNameParser(String tweetText) {
            this.endParsingIndex = 0;
            int firstNameIndex = tweetText.indexOf('@', 0) + 1;
            int lastNameIndex = tweetText.indexOf(':', firstNameIndex);
            this.tweetAuthor = tweetText.substring(firstNameIndex, lastNameIndex - 1);
            endParsingIndex = lastNameIndex + 1;
        }

        public String getTweetAuthor() {
            return this.tweetAuthor;
        }

        public int getEndIndex() {
            return endParsingIndex;
        }
    }

    private static String getRetweetInfo(Status status) {
        if (status.getRetweetCount() > 0) {
            String retweetDeclension =
                    FormDeclenser.getTweetsDeclension(status.getRetweetCount());
            return new StringBuilder().append("(")
                    .append(ConsoleUtil.Style.BOLD.line(new Integer(status.getRetweetCount()).toString()))
                    .append(" ").append(retweetDeclension).append(")").toString();
        }
        return "";
    }
}
