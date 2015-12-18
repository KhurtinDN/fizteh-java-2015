package ru.mipt.diht.students.egdeliya.TwitterStream;

/**
 * Created by Эгделия on 18.12.2015.
 */
public class TwitterStreamRunner {
    public static void main(String[] args) {
        TwitterStreamer tweet = new TwitterStreamer(args);
        tweet.twitterStreamRun();
    }
}
