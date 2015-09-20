package ru.sopilnyak;

import twitter4j.*;

public class App {

    protected static String queryString;
    protected static boolean isQueryStarted = false;
    protected static boolean hideRetweets = true;

    public static void main(String[] args) {

        // read commands from console
        for (int i = 0; i < args.length; i++) {

            switch (commandNumber(args[i])) {
                case 0: // not a command
                    if (isQueryStarted) {
                        queryString += args[i] + " "; // add to search query
                    }
                    break;

                case 1: // start new query
                    queryString = "";
                    isQueryStarted = true;
                    break;

                case 4: // hide retweets
                    hideRetweets = true;
            }
        }

        if (queryString.equals("")) {
            System.err.println("No query, nothing to find");
        } else {
            addQuery();
        }

    }

    protected static short commandNumber(String arg) {
        if (arg.equals("--query") || arg.equals("-q")) return 1;
        if (arg.equals("--place") || arg.equals("-p")) return 2;
        if (arg.equals("--stream") || arg.equals("-s")) return 3;
        if (arg.equals("-hideRetweets")) return 4;
        if (arg.equals("-limit") || arg.equals("-l")) return 5;
        if (arg.equals("-help") || arg.equals("-h")) return 6;
        return 0;
    }

    protected static void addQuery() {
        System.out.println("Searching for query: " + queryString);

        Twitter twitter = TwitterFactory.getSingleton();
        Query query = new Query(queryString);

        // print the results of the query
        try {
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }
}
