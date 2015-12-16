package model;

import twitter4j.Status;

/**
 * Class represents Tweet object.
 */
public class Tweet {
    private String text;
    private TwitterUser author;
    private long time;
    private long retweetCount;
    private Tweet retweetedTweet;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TwitterUser getAuthor() {
        return author;
    }

    public void setAuthor(TwitterUser author) {
        this.author = author;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    public void setRetweetedTweet(Tweet retweetedTweet) {
        this.retweetedTweet = retweetedTweet;
    }

    public boolean isRetweet() {
        return null != retweetedTweet;
    }

    public boolean isNotRetweet() {
        return !isRetweet();
    }

    /**
     * Factory method to convert twitter4j.Status object to internal model - Tweet object.
     * @param twitter4jStatus twitter4j.Status object
     * @return Tweet object
     */
    public static Tweet valueOf(Status twitter4jStatus) {
        Tweet tweet = new Tweet();
        if (twitter4jStatus.getRetweetedStatus() != null) {
            tweet.setRetweetedTweet(valueOf(twitter4jStatus.getRetweetedStatus()));
        }
        tweet.setAuthor(TwitterUser.valueOf(twitter4jStatus.getUser()));
        tweet.setText(twitter4jStatus.getText());
        tweet.setTime(twitter4jStatus.getCreatedAt().getTime());
        tweet.setRetweetCount(twitter4jStatus.getRetweetCount());

        return tweet;
    }
}
