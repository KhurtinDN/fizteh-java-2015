package ru.mipt.diht.students.maxDankow.TwitterStream.solution;

import com.google.maps.model.Geometry;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.GeolocationUtils;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.TwitterStreamUtils;
import twitter4j.*;

import java.util.List;

public class TwitterSearcher {

    private static final int ATTEMPTS_LIMIT = 5;
    private static final int RECONNECT_DELAY_MS = 5000;
    private boolean shouldHideRetweets = false;
    private int tweetsNumberLimit;
    private Geometry locationGeometry = null;
    private String queryText = null;

    public TwitterSearcher(String query,
                           String location,
                           boolean hideRetweets,
                           int tweetsLimit) throws IllegalArgumentException {
        shouldHideRetweets = hideRetweets;
        tweetsNumberLimit = tweetsLimit;
        queryText = query;

        if (location != null) {
            locationGeometry = GeolocationUtils.findLocation(location);
        }

        if (this.queryText == null) {
            throw new IllegalArgumentException("Query is empty.");
        }
    }

    public final void searchTweets() throws InterruptedException, IllegalStateException {
        Twitter twitter = new TwitterFactory().getInstance();

        // Настраиваем запрос.
        Query twitterQuery = new Query();
        assert this.queryText != null;
        twitterQuery.setQuery(this.queryText);
        twitterQuery.setCount(tweetsNumberLimit);

        // Будем подключаться пока не получится.
        QueryResult result = null;
        for (int attempts = 0; attempts < ATTEMPTS_LIMIT; ++attempts) {
            try {
                result = twitter.search(twitterQuery);
                break;
            } catch (TwitterException te) {
                System.err.println("Невозможно выполнить запрос к Twitter. Повторная попытка...");
                Thread.sleep(RECONNECT_DELAY_MS);
            }
        }

        // Сообщаем, если не удалось получить результат или подключиться к Twitter.
        if (result == null) {
            throw new IllegalStateException("Не удалось подключиться к Twitter.");
        }

        // Обрабатываем полученные результаты, получая по очереди все страницы.
        int tweetsCount = 0;
        while (twitterQuery != null && tweetsCount < tweetsNumberLimit) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (TwitterStreamUtils.checkTweet(tweet, locationGeometry, shouldHideRetweets)) {
                    TwitterStreamUtils.printTweet(tweet, true);
                    tweetsCount++;
                }
            }
            twitterQuery = result.nextQuery();
        }

        if (tweetsCount == 0) {
            System.out.println("По Вашему запросу не найдено ни одного твитта.");
        }
    }
}
