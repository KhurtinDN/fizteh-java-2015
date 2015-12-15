package ru.mipt.diht.students.nkarpachev.twitterstream;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

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
                query = result.nextQuery();
            } while ((query != null) && (printedTweets < tweetLimit));
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}
