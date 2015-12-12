package ru.mipt.diht.students.maxDankow.TwitterStream.solution;

import com.google.maps.model.Geometry;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.GeolocationUtils;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.TwitterStreamUtils;
import twitter4j.*;

import java.util.LinkedList;

public class TwitterStreamer {
    // Очередь для твиттов в режиме stream.
    // todo: заменить на потокобезопасную очередь.
    private LinkedList<Status> streamQueue;
    // Используется для ограничения сильного разрастания очереди твиттов в режиме stream.
    private static final int STREAM_TWEETS_LIMIT = 10000;
    // Задержка при печати новых твиттов.
    private static final int STREAM_DELAY_MS = 1000;
    private boolean shouldHideRetweets = false;
    // Представляет прямоугольные координаты местности, по которой ведется поиск.
    private Geometry locationGeometry = null;
    private String[] queryText = null;

    public TwitterStreamer(String query,
                           String location,
                           boolean hideRetweets) throws IllegalArgumentException {
        if (location != null) {
            locationGeometry = GeolocationUtils.getLocationBoxCoordinates(location);
        }
        if (query == null) {
            throw new IllegalArgumentException("Query is empty.");
        }
        queryText = new String[]{query};
        shouldHideRetweets = hideRetweets;
    }

    public final void startStream() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        // Устанавливаем обработчик новых твиттов.
        twitterStream.addListener(statusAdapter);

        // Настраиваем запрос.
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(queryText);

        // Запускаем поток твиттов.
        twitterStream.filter(filterQuery);
        streamQueue = new LinkedList<>();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(STREAM_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // Обрабатываем очередной твитт.
            if (!streamQueue.isEmpty()) {
                Status tweet = streamQueue.poll();
                TwitterStreamUtils.printTweet(tweet, false);
            }
        }
    }

    // Callback обработчик новых твиттов.
    private StatusAdapter statusAdapter = new StatusAdapter() {
        public void onStatus(Status tweet) {
            if (TwitterStreamUtils.checkTweet(tweet, locationGeometry, shouldHideRetweets)
                    && streamQueue.size() < STREAM_TWEETS_LIMIT) {
                streamQueue.add(tweet);
            }
        }
    };
}
