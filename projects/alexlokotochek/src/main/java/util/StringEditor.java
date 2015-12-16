package util;

import twitter4j.Status;

public class StringEditor {

    public static String colourName(String name) {
        return (char) 27 + "[34m" + "@" + name + (char) 27 + "[0m" + ": ";
    }

    public static String tweetStringToPrint(Status status) {
        return "[" + TimeString.timeOfTweet(status) + "] "
                + colourName(status.getUser().getScreenName())
                + status.getText();
    }

}
