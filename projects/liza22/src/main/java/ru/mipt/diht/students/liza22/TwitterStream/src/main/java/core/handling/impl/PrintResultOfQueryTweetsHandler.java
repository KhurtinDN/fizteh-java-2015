package core.handling.impl;

import core.handling.TweetHandler;
import model.Tweet;
import utils.TextUtils;

import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This handler is used in QUERY mode of application.
 * It prints tweets every time when new tweet is come.
 *
 * When new tweet is obtained (method 'handle' invoked) this handler prints
 * formatted representation of this new tweet.
 */
public class PrintResultOfQueryTweetsHandler implements TweetHandler {
    private PrintStream out;

    private AtomicLong tweetCounter = new AtomicLong(0);

    public PrintResultOfQueryTweetsHandler(PrintStream outStream) {
        this.out = outStream;
    }

    @Override
    public final void handle(Tweet tweet) {
        out.println("Tweet#" + tweetCounter.incrementAndGet() + ":");
        out.println(formatTweet(tweet));
    }

    /*
     * Print format is the following:
     *
     * If tweet IS NOT retweeted
     * ----------------------------------------------------------------------------------------
     * [<tweet_time>] @<nick1>: <text1>
     * ----------------------------------------------------------------------------------------
     *
     * If tweet IS retweeted
     * ----------------------------------------------------------------------------------------
     * [<retweet_time>] @<nick2>: ретвитнул @<another_nick>: <text> (<retweets count> ретвитов)
     * ----------------------------------------------------------------------------------------
     *
     * @param tweet tweet object to be printed
     * @return text representation of tweet according to format
     */
    private static String formatTweet(Tweet tweet) {
        StringBuilder tweetView = new StringBuilder(256);
        tweetView.append("----------------------------------------------------------------------------------------\n");
        if (tweet.isNotRetweet()) {
            tweetView.append("[").append(formatTime(tweet.getTime())).append("] ").
                    append("@").append(getNickname(tweet)).
                    append(": ").append(tweet.getText());
        } else {
            Tweet retweetedTweet = tweet.getRetweetedTweet();
            tweetView.append("[").append(formatTime(tweet.getTime())).append("] ").
                    append("@").append(getNickname(tweet)).
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

    /*
     * Time format is the following:
     * Время должно быть в формате:
     *      "Только что" - если менее 2х минут назад
     *      "n минут назад" - если менее часа назад (n - цифрами)
     *      "n часов назад" - если более часа, но сегодня (n - цифрами)
     *      "вчера" - если вчера
     *      "n дней назад" - в остальных случаях (n - цифрами)
     *
     * @param then the tweet's time in milliseconds
     * @return text representation of the tweet's time according to format
     */
    private static String formatTime(long then) {
        long now = new Date().getTime();
        long diffInMinutes = (now - then) / 1_000 / 60; // milliseconds/1_000/60 = minutes
        if (diffInMinutes < 2) {
            return "Только что";
        } else if (TimeUnit.MINUTES.toHours(diffInMinutes) < 1) {
            return diffInMinutes + " минут назад";
        } else if (TimeUnit.MINUTES.toHours(diffInMinutes) >= 1 && TimeUnit.MINUTES.toDays(diffInMinutes) < 1) {
            return TimeUnit.MINUTES.toHours(diffInMinutes) + " часов назад";
        } else if (TimeUnit.MINUTES.toDays(diffInMinutes) >= 1 && TimeUnit.MINUTES.toDays(diffInMinutes) < 2) {
            return "вчера";
        } else {
            return TimeUnit.MINUTES.toDays(diffInMinutes) + " дней назад";
        }
    }
}
