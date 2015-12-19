package ru.mipt.diht.students.IrinaMudrova.Twitter.library;

import twitter4j.Status;
import twitter4j.StatusAdapter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

/**
 * Created by ura on 27.09.15.
 */
public class TwitterListener extends StatusAdapter {
    private Queue<String> outs;
    private static final int LIMIT = 1000000;
    private TweetFormatter tweetFormatter;
    private boolean hideRetweets;

    protected void setTweetFormatter(TweetFormatter tweetFormatterArg) {
        tweetFormatter = tweetFormatterArg;
    }

    public TwitterListener init(boolean hideRetweetsArg) {
        outs = new ConcurrentLinkedQueue<String>();
        tweetFormatter = new TweetFormatter();
        hideRetweets = hideRetweetsArg;
        return this;
    }

    public void addTweetStr(String str) {
        // There is no need to store more
        // And memory can run out
        if (outs.size() < LIMIT) {
            outs.add(str);
        }
    }

    public String pollTweetStr() {
        return outs.poll();
    }

    @Override
    public void onStatus(Status status) {
        if (!status.isRetweet() || !hideRetweets) {
            addTweetStr(tweetFormatter.oneTweetToStr(status, TweetFormatter.ShowTime.no));
        }
    }
    /*
    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        return;
    }
    @Override
    public void onStallWarning(StallWarning warning) {
        System.err.println("Warning:" + warning);
    }
    @Override
    public void onException(Exception ex) {
        System.err.println("Exception: " + ex.getMessage());
    }
    @Override
    public void onScrubGeo(long a, long b) { }
    @Override
    public void onTrackLimitationNotice(int a) { }
    */
}
