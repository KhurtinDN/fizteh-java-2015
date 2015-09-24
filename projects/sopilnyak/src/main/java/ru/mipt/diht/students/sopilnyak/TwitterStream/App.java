package ru.mipt.diht.students.sopilnyak.TwitterStream;

import twitter4j.*;

import java.io.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class App {

    private static String queryString;
    private static boolean isQueryStarted = false;
    private static String locationString;
    private static boolean isLocationStarted = false;
    private static boolean isNearbyEnabled = false;
    private static boolean isStreamEnabled = false;
    private static boolean isSetLimitStarted = false;
    private static int limit = -1;
    private static boolean hideRetweets = false;

    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";
    public static final String HELP = "help.txt";
    public static final int MAX_TWEETS = 100;
    public static final int RADIUS = 10;
    public static final int SLEEP_TIME = 1000;
    public static final int ATTEMPTS = 20;

    public static final int C1 = 1;
    public static final int C2 = 2;
    public static final int C3 = 3;
    public static final int C4 = 4;
    public static final int C5 = 5;
    public static final int C6 = 6;
    public static final int C7 = 7;

    public static void main(String[] args) {

        // read commands from console
        for (int i = 0; i < args.length; i++) {

            switch (commandNumber(args[i])) {
                case 0: // not a command
                    if (isQueryStarted) {
                        queryString += args[i] + " "; // add to search query
                    }
                    if (isLocationStarted) {
                        locationString += args[i] + " "; // location
                    }
                    if (isSetLimitStarted) {
                        limit = Integer.parseInt(args[i]);
                        isSetLimitStarted = false;
                    }
                    break;

                case C1: // start new query
                    queryString = "";
                    isQueryStarted = true;
                    isLocationStarted = false;
                    break;

                case C2: // location
                    locationString = "";
                    isLocationStarted = true;
                    isQueryStarted = false;
                    break;

                case C3: // stream mode
                    isStreamEnabled = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case C4: // hide retweets
                    hideRetweets = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case C5: // set limit
                    isSetLimitStarted = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                case C6: // help
                    isQueryStarted = false;
                    isLocationStarted = false;
                    showHelp();
                    break;

                case C7: // nearby
                    isNearbyEnabled = true;
                    isQueryStarted = false;
                    isLocationStarted = false;
                    break;

                default:
                    break;
            }
        }

        if (queryString != null
                && !queryString.equals("")) { // remove space in the end
            queryString = queryString.substring(0,
                    queryString.length() - 1);
        }

        if (locationString != null
                && !locationString.equals("")) { // remove space in the end
            locationString = locationString.substring(0,
                    locationString.length() - 1);
        }

        if ((queryString == null
                || queryString.equals("")) && locationString == null) {
            System.err.println("No query, nothing to find");
        } else {
            addQuery();
        }

    }

    protected static short commandNumber(String arg) {
        if (arg.equals("--query") || arg.equals("-q")) {
            return C1;
        }
        if (arg.equals("--place") || arg.equals("-p")) {
            return C2;
        }
        if (arg.equals("--stream") || arg.equals("-s")) {
            return C3;
        }
        if (arg.equals("--hideRetweets")) {
            return C4;
        }
        if (arg.equals("--limit") || arg.equals("-l")) {
            return C5;
        }
        if (arg.equals("--help") || arg.equals("-h")) {
            return C6;
        }
        if (arg.equals("nearby") && isLocationStarted) {
            return C7;
        }
        return 0;
    }

    protected static void addQuery() {

        Twitter twitter = TwitterFactory.getSingleton();
        Query query = new Query(queryString);
        GeoQuery geoQuery;
        try {
            String ip = Inet4Address.getLocalHost().getHostAddress();
            geoQuery = new GeoQuery(ip);

            query.setCount(MAX_TWEETS); // max number of tweets
            if (limit != -1) {
                query.setCount(limit);
            }

            System.out.print("Твиты");
            if (queryString != null && !queryString.equals("")) {
                System.out.print(" по запросу \"" + queryString + "\"");
            }
            if (isStreamEnabled) {
                System.out.print(" в режиме потока");
            }
            if (locationString != null && !locationString.equals("")
                    && !isNearbyEnabled) {
                System.out.print(" возле местоположения \""
                        + locationString + "\"");
                geoQuery.setQuery(locationString);
            }
            if (locationString != null && (isNearbyEnabled
                    || locationString.equals(""))) {
                System.out.print(" возле вашего местоположения");
                isNearbyEnabled = true;
            } else {
                isNearbyEnabled = false;
            }
            System.out.println(":");

            int attempts = 0;

            // print the results of the query
            while (true) {

                try {

                    if (locationString != null) {
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

                    if (!isStreamEnabled) {

                        QueryResult result = twitter.search(query);
                        // send a query

                        for (Status status : result.getTweets()) {
                            // print tweets
                            if ((!status.isRetweet()
                                    || !hideRetweets)) { // hide retweets

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

                        if (result.getTweets().isEmpty()) {
                            // nothing was found
                            System.out.println("По вашему запросу "
                                    + "ничего не найдено.");
                        }

                    } else {

                        while (true) {
                            QueryResult result = twitter.search(query);
                            for (Status status : result.getTweets()) {
                                if (!status.isRetweet() || !hideRetweets) {
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
        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
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

    protected static void showHelp() {
        File file = new File(HELP);

        try {

            if (!file.exists()) {
                System.err.println("Нет файла help");
                return;
            }

            BufferedReader in = new BufferedReader(
                    new FileReader(file.getAbsoluteFile()));

            try {
                String string;
                while ((string = in.readLine()) != null) {
                    System.out.println(string);
                }
            } catch (IOException e) {
                System.err.println("Проблема с чтением файла");
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Нет файла help");
        } catch (IOException e) {
            System.err.println("Проблема с чтением файла");
        }
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
