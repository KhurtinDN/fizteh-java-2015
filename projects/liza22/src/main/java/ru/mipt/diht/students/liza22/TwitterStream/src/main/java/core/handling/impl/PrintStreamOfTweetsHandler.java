package core.handling.impl;

import config.Arguments;
import config.Constants;
import core.handling.TweetHandler;
import model.Tweet;
import utils.TextUtils;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This handler is used in STREAM mode of application.
 * It prints tweets every Constants.PRINT_TWEET_DELAY_SECS (by default every 1 second).
 *
 * When new tweet is obtained (method 'handle' invoked) this handler stores this tweet
 * to internal queue. When next print step is come, handler takes tweet from queue
 * (by 'poll' method) and prints this tweet.
 * In case when no available tweet in queue, it will print nothing or
 * Constants.NO_TWEET_MESSAGE in VERBOSE mode.
 */
public final class PrintStreamOfTweetsHandler implements TweetHandler {
    public static final int STRING_SIZE = 256;
    public static final int KILO = 1_000;
    private PrintStream out;
    private Queue<Tweet> tweetQueue;

    private boolean started = false;
    private AtomicLong tweetCounter = new AtomicLong(0);

    public PrintStreamOfTweetsHandler(final PrintStream outStream) {
        this.out = outStream;
        tweetQueue = new ArrayDeque<>();
    }

    @Override
    public void handle(Tweet tweet) {
        tweetQueue.offer(tweet);

        if (!started) {
            // schedule timer with task of printing tweets from queue
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Tweet next = tweetQueue.poll();
                    if (next != null) {
                        out.println("Tweet#" + tweetCounter.incrementAndGet() + ":");
                        out.println(formatTweet(next));
                    } else {
                        if (Arguments.getInstance().isVerboseMode()) {
                            out.println(Constants.NO_TWEET_MESSAGE);
                        }
                    }
                }
            }, 0, Constants.PRINT_TWEET_DELAY_SECS * KILO);
            started = true;
        }
    }

    /*
     * Print format is the following:
     *
     * If tweet IS NOT retweeted
     * ----------------------------------------------------------------------------------------
     * @<nick1>: <text1>
     * ----------------------------------------------------------------------------------------
     *
     * If tweet IS retweeted
     * ----------------------------------------------------------------------------------------
     * @<nick2>: ретвитнул @<another_nick>: <text> (<retweets count> ретвитов)
     * ----------------------------------------------------------------------------------------
     *
     * @param tweet tweet object to be printed
     * @return text representation of tweet according to format
     */
    private static String formatTweet(Tweet tweet) {
        StringBuilder tweetView = new StringBuilder(STRING_SIZE);
        tweetView.append("----------------------------------------------------------------------------------------\n");
        if (tweet.isNotRetweet()) {
            tweetView.append("@").append(getNickname(tweet)).
                    append(": ").append(tweet.getText());
        } else {
            Tweet retweetedTweet = tweet.getRetweetedTweet();
            tweetView.append("@").append(getNickname(tweet)).
                    append(": ретвитнул @").append(getNickname(retweetedTweet)).
                    append(": ").append(retweetedTweet.getText()).
                    append(" (").append(retweetedTweet.getRetweetCount()).append(" ретвитов)");
        }
        tweetView.append("\n----------------------------------------------------------------------------------------");
        return tweetView.toString();
    }

    private static String getNickname(Tweet tweet) {
        String nick = tweet.getAuthor().getName();
        return TextUtils.coloredText(nick, TextUtils.COLOR_BLUE);
    }
}
