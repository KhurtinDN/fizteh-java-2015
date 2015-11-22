package ru.mipt.diht.students.nkarpachev.TwitterStream;

import twitter4j.*;

import java.util.List;

public class SearchRunner {

    public static void getTweetsByQuery(Query query, boolean doHideRetweets, int tweetLimit) {
        int printedTweets = 0;
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    if (printedTweets >= tweetLimit) {
                        break;
                    }
                    if (!doHideRetweets || !(tweet.isRetweet())) {
                        PrintTools.printTweet(tweet);
                        printedTweets++;
                    }
                }
            } while (((query = result.nextQuery()) != null) && (printedTweets < tweetLimit));
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}
