package ru.mipt.diht.students.IrinaMudrova.Twitter.library;


import twitter4j.*;

public class TwitterStreamAssistFactory {
    public TwitterOptions newTwitterOptions() { return new TwitterOptions(); }
    public TwitterStream newTwitterStream() { return new TwitterStreamFactory().getInstance(); }
    public TweetFormatter newTweetFormatter() { return new TweetFormatter(); }
    public Twitter newTwitter() {
        return new TwitterFactory().getInstance();
    }
    public TwitterListener newTwitterListener() {
        return new TwitterListener();
    }
    public FilterQuery newFilterQuery() { return new FilterQuery(); }
    public YandexPlaces newYandexPlaces() { return new YandexPlaces(); }
    public Query newQuery() { return new Query(); }

}
