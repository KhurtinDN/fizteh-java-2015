package ru.mipt.diht.students.annnvl.TwitterStream;

import twitter4j.Status;

public class StatusParser {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private Parser param;
    public StatusParser(Parser paramDef) {
        param = paramDef;
    }
    public final String printStatus(Status status) {
        long currentTimeToFormat = System.currentTimeMillis();
        long tweetTimeToFormat = status.getCreatedAt().getTime();
        StringBuilder stringStatus = new StringBuilder();
        if (!param.isHideRetwitts()) {
            if (status.isRetweet()) {
                stringStatus.append("[").
                        append(TimeParser.printTime(currentTimeToFormat, tweetTimeToFormat)).append("] ")
                        .append(ANSI_BLUE)
                        .append("@").append(status.getUser().getScreenName())
                        .append(ANSI_RESET).append(": ретвитнул " + ANSI_BLUE)
                        .append("@")
                        .append(status.getRetweetedStatus().getUser().getScreenName())
                        .append(ANSI_RESET).append(": ")
                        .append(status.getRetweetedStatus().getText());
            }
        } else {
            stringStatus.append("[").
                    append(TimeParser.printTime(currentTimeToFormat, tweetTimeToFormat)).append("] ")
                    .append(ANSI_BLUE)
                    .append("@").append(status.getUser().getScreenName())
                    .append(ANSI_RESET).append(": ").append(status.getText());
            if (status.getRetweetCount() != 0) {
                stringStatus.append("(").append(status.getRetweetCount()).append(" ")
                        .append(Formatter.retweet(status.getRetweetCount()))
                        .append(")");
            }
        }
        return stringStatus.toString();
    }
}
