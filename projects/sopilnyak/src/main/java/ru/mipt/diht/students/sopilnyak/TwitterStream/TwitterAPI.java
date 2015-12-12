package ru.mipt.diht.students.sopilnyak.TwitterStream;

import twitter4j.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TwitterAPI extends App {

    public static final int MAX_TWEETS = 100;
    public static final int RADIUS = 10;
    public static final int SLEEP_TIME = 1000;
    public static final int ATTEMPTS = 20;

    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";

    protected static void addQuery() {

        Query query = new Query(getQueryString());
        GeoQuery geoQuery;
        try {
            String ip = Inet4Address.getLocalHost().getHostAddress();
            geoQuery = new GeoQuery(ip);

            query.setCount(MAX_TWEETS); // max number of tweets
            if (getLimit() != -1) {
                query.setCount(getLimit());
            }

            System.out.print("Твиты");
            if (getQueryString() != null && !getQueryString().equals("")) {
                System.out.print(" по запросу \"" + getQueryString() + "\"");
            }
            if (getIsStreamEnabled()) {
                System.out.print(" в режиме потока");
            }
            if (getLocationString() != null && !getLocationString().equals("")
                    && !getIsNearbyEnabled()) {
                System.out.print(" возле местоположения \""
                        + getLocationString() + "\"");
                geoQuery.setQuery(getLocationString());
            }
            if (getLocationString() != null && (getIsNearbyEnabled()
                    || getLocationString().equals(""))) {
                System.out.print(" возле вашего местоположения");
                setIsNearbyEnabled(true);
            } else {
                setIsNearbyEnabled(false);
            }
            System.out.println(":");

            printResults(query, geoQuery);

        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
        }

    }

    protected static void printResults(Query query, GeoQuery geoQuery) {
        Twitter twitter = TwitterFactory.getSingleton();
        int attempts = 0;

        // print the results of the query
        while (true) {

            try {

                if (getLocationString() != null) {
                    ResponseList<Place> places =
                            twitter.searchPlaces(geoQuery);

                    if (places.size() == 0) {
                        System.out.
                                println("Невозможно определить "
                                        + "местоположение");
                        return;

                    } else {
                        Place place = places.get(0); // get first place
                        // search in radius by coordinates
                        query.setGeoCode(place.
                                        getBoundingBoxCoordinates()[0][0],
                                RADIUS, Query.KILOMETERS);
                    }
                }

                if (!getIsStreamEnabled()) {

                    QueryResult result = twitter.search(query);
                    getResultsStreamNotEnabled(result);

                    if (result.getTweets().isEmpty()) {
                        // nothing was found
                        System.out.println("По вашему запросу "
                                + "ничего не найдено.");
                    }

                } else {

                    QueryResult result = twitter.search(query);
                    getResultsStreamEnabled(result, query);
                }

                break; // no need to try again

            } catch (TwitterException e) {
                if (e.isCausedByNetworkIssue()) {
                    System.err.println("Ошибка соединения: "
                            + e.getErrorMessage()
                            + ". Повторная попытка...");
                    try {
                        Thread.sleep(SLEEP_TIME); // sleep for 1 second
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    // try again
                    if (++attempts == ATTEMPTS) {
                        System.err.println("Не удалось.");
                        break;
                    }
                } else {
                    System.err.println("Ошибка TwitterAPI: "
                            + e.getErrorMessage());
                    break;
                }
            }
        }
    }

    protected static void getResultsStreamNotEnabled(QueryResult result) throws TwitterException {

        // send a query

        for (Status status : result.getTweets()) {
            // print tweets
            if ((!status.isRetweet()
                    || !getHideRetweets())) { // hide retweets

                System.out.print("["
                        + getDate(status.getCreatedAt())
                        + "] ");

                System.out.println("@" + BLUE
                        + status.getUser().getScreenName()
                        + RESET + getRetweetSource(status)
                        + ": " + status.getText()
                        + retweetCount(status));
            }
        }
    }

    protected static void getResultsStreamEnabled(QueryResult result, Query query) throws TwitterException {
        while (true) {
            for (Status status : result.getTweets()) {
                if (!status.isRetweet() || !getHideRetweets()) {
                    // hide retweets

                    System.out.println("@" + BLUE
                            + status.getUser().getScreenName()
                            + RESET + getRetweetSource(status)
                            + ": " + status.getText()
                            + retweetCount(status));

                    try {
                        Thread.sleep(SLEEP_TIME);
                        // sleep for 1 second
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!result.getTweets().isEmpty()) {
                Status status = result.getTweets().get(0);
                query.setSinceId(status.getId());
            }
        }
    }

    protected static String getDate(Date date) {

        Calendar currentCal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date current = currentCal.getTime();

        final long diffFull = current.getTime() - date.getTime();

        if (calendar.get(Calendar.YEAR)
                == currentCal.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR)
                == currentCal.get(Calendar.DAY_OF_YEAR) - 1) { // yesterday
            return "Вчера";
        }

        long diffDays = TimeUnit.DAYS.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffDays > 0) {
            return "" + diffDays + " дней назад";
        }

        long diffHours = TimeUnit.HOURS.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffHours >= 1) {
            return "" + diffHours + " часов назад";
        }

        long diffMinutes = TimeUnit.MINUTES.convert(
                diffFull, TimeUnit.MILLISECONDS);
        if (diffMinutes >= 2) {
            return "" + diffMinutes + " минут назад";
        }

        return "Только что";

    }

    protected static String retweetCount(Status status) {
        if (status.getRetweetCount() > 0) {
            return status.getRetweetCount() + "";
        }
        return "";
    }

    protected static String getRetweetSource(Status status) {
        if (status.isRetweet()) {
            return " ретвитнул "
                    + "@" + BLUE
                    + status.getRetweetedStatus().
                    getUser().getScreenName() + RESET;
        }
        return "";
    }
}
