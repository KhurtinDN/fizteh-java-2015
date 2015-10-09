package ru.mipt.diht.students.maxDankow.TwitterStream;

import com.beust.jcommander.JCommander;
import com.google.maps.model.Geometry;
import twitter4j.*;

import java.util.LinkedList;
import java.util.List;

public class TwitterStreamClient {

    private static final int STREAM_DELAY_MS = 1000;
    // Используется для ограничения сильного разрастания очереди твиттов в режиме stream.
    private static final int STREAM_TWEETS_LIMIT = 10000;
    // Очередь для твиттов в режиме stream.
    private static LinkedList<Status> streamQueue;
    // Содержит такие полезные поля, как прямоугольные координаты (Bounding Box Coordinates) локации, и ее координаты.
    // Используется для фильтрации фильтров по месту.
    private static Geometry placeGeometry = null;
    private static boolean shouldHideRetweets = false;
    // Ограничение количества попыток переподключения.
    private static final int ATTEMPTS_LIMIT = 5;
    private static final int RECONNECT_DELAY_MS = 5000;
    // Длинна строки разграничителя твиттов.
    private static final int DELIM_LENGTH = 200;

    public static void main(String[] args) {
        JComanderArgsList cmdArguments = new JComanderArgsList();
        JCommander jCommander = new JCommander(cmdArguments, args);
        if (cmdArguments.isHelp()) {
            jCommander.usage();
            return;
        }
        String searchLocation = cmdArguments.getLocationStr();
        if (searchLocation != null) {
            placeGeometry = TwitterStreamUtils.findLocation(searchLocation);
        }
        String queryText = cmdArguments.getQueryText();
        shouldHideRetweets = cmdArguments.shouldHideRetweets();

        if (cmdArguments.isStreamMode()) {
            String[] queryArray = new String[1];
            queryArray[0] = queryText;
            startTwitterStreaming(queryArray);
        } else {
            searchTweets(queryText, cmdArguments.getTweetsNumberLimit());
        }
        System.exit(0);
    }

    private static boolean checkTweet(Status tweet) {
        return (checkLocation(tweet.getPlace()) && (!shouldHideRetweets || !tweet.isRetweet()));
    }

    private static void startTwitterStreaming(String[] queryText) {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(queryText);
        streamQueue = new LinkedList<>();
        twitterStream.filter(filterQuery);
        while (true) {
            while (!streamQueue.isEmpty()) {
                Status tweet = streamQueue.poll();
                printTweet(tweet, false);
                try {
                    Thread.sleep(STREAM_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(STREAM_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void searchTweets(String queryText, int tweetsNumberLimit) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        if (queryText != null) {
            query.setQuery(queryText);
        }
        query.setCount(tweetsNumberLimit);
        QueryResult result = null;
        // Будем подключаться пока не получится
        for (int attempts = 0; attempts < ATTEMPTS_LIMIT; ++attempts) {
            try {
                result = twitter.search(query);
            } catch (TwitterException te) {
                System.err.println("Невозможно выполнить запрос к Twitter. Повторная попытка...");
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assert result != null;
        int tweetsCount = 0;
        while (query != null) {
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (checkTweet(tweet)) {
                    printTweet(tweet, true);
                    tweetsCount++;
                }
            }
            query = result.nextQuery();
            if (tweetsCount >= tweetsNumberLimit) {
                query = null;
            }
        }
        if (tweetsCount == 0) {
            System.out.println("No tweets found.");
        }
    }

    private static void printDelim() {
        for (int i = 0; i < DELIM_LENGTH; ++i) {
            System.out.print("-");
        }
        System.out.println();
    }

    private static void printTweet(Status tweet, boolean shouldShowTime) {
        if (shouldShowTime) {
            System.out.print("["
                    + TwitterStreamUtils.convertTimeToRussianWords(tweet.getCreatedAt())
                    + "] ");
        }
        if (!tweet.isRetweet()) {
            int retweetCount = tweet.getRetweetCount();

            System.out.print("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: "
                    + tweet.getText());
            if (retweetCount > 0) {
                System.out.print(" ("
                        + tweet.getRetweetCount()
                        + " ретвитов)");
            }
            System.out.println();
        } else {
            Status originalTweet = tweet.getRetweetedStatus();
            System.out.println("\033[34m@"
                    + tweet.getUser().getScreenName()
                    + "\033[0m: ретвитнул \033[34m@"
                    + originalTweet.getUser().getScreenName()
                    + "\033[0m: "
                    + originalTweet.getText());
        }
        printDelim();
    }

    private static boolean checkLocation(Place place) {
        if (placeGeometry == null) {
            return true;
        }
        if (place == null) {
            return false;
        }
        // Берем среднее значение координат заданной области.
        double latitudeSum = 0;
        double longitudeSum = 0;
        GeoLocation[][] geoLocations = place.getBoundingBoxCoordinates();
        for (int i = 0; i < geoLocations[0].length; ++i) {
            latitudeSum += geoLocations[0][i].getLatitude();
            longitudeSum += geoLocations[0][i].getLongitude();
        }
        double centerLat = latitudeSum / geoLocations[0].length;
        double centerLng = longitudeSum / geoLocations[0].length;
        return (centerLat > placeGeometry.bounds.southwest.lat && centerLat < placeGeometry.bounds.northeast.lat
                && centerLng > placeGeometry.bounds.southwest.lng && centerLng < placeGeometry.bounds.northeast.lng);
    }

    private static StatusListener tweetListener = new StatusListener() {
        public void onStatus(Status tweet) {
            if (checkTweet(tweet) && streamQueue.size() < STREAM_TWEETS_LIMIT) {
                streamQueue.add(tweet);
            }
        }

        public void onDeletionNotice(StatusDeletionNotice statusDN) {
        }

        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        }

        public void onException(Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onScrubGeo(long arg0, long arg1) {
        }

        @Override
        public void onStallWarning(StallWarning arg0) {
        }
    };
}
