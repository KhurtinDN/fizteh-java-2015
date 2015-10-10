package ru.mipt.diht.students.feezboom.Twitter;

import twitter4j.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by avk on 02.10.15.
 */
public class TwitterStreamer {

    private static final double DEG_TO_KM = 60 * 1.1515 * 1.609344;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180 / Math.PI;
    private static final long MILLISEC = 1;
    private static final long SEC = 1000;
    private static final long MIN = SEC * 60;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long YEAR = DAY * 365;

    private static final byte ZERO = 0;
    private static final byte ONE = 1;
    private static final byte TWO = 2;
    private static final byte THREE = 3;
    private static final byte FOUR = 4;
    private static final byte FIVE = 5;
    private static final byte SIX = 6;
    private static final byte SEVEN = 7;
    private static final byte EIGHT = 8;
    private static final byte NINE = 9;
    private static final byte TEN = 10;
    private static final byte TWENTY = 20;
    private static final byte THIRTY = 30;
    private static final byte FORTY = 40;

    private String[] args;
    private final int sleepTime = 1000;
    private final int tweetsLimit = 100;



    public static String getCityString() throws IOException {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner = new Scanner(urlConnection.getInputStream(), "UTF-8");

        //I must find it from a source code (country)
        String country = "Russian Federation";
        final int NumberOflineWithCity = 6;
        String substring = "";

        for (int i = 0; i < NumberOflineWithCity; i++) {
            substring = scanner.nextLine();
        }

        int first = substring.lastIndexOf(' ') + 1;
        int last = substring.lastIndexOf('"');

        String City = substring.substring(first, last);
        scanner.close();

        if (country.equals("Russian Federation")) {
            City = StringUtils.translitToRussian(City);
        }
        return City;
    }
    public static double[] GetCoordinates() throws Exception{
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner = new Scanner(urlConnection.getInputStream(), "UTF-8");

        String line = "";

//        while (line.indexOf());
        double[] coord = {0, 0};
        return coord;
    }
    public static GeoLocation getGeoLocation() throws Exception {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner = new Scanner(urlConnection.getInputStream(), "UTF-8");

        final int latitudeLine = 132;
        //longitude line is 133, there is no need to make variable

        for (int i = 1; i < latitudeLine; i++) {
            scanner.nextLine();
        }

        String latStr = scanner.nextLine();
        String lonStr = scanner.nextLine();

        double latitude, longitude;

        int first = latStr.lastIndexOf("\">") + "\">".length();
        int last  = latStr.lastIndexOf("</td>");

        latitude = Double.parseDouble(latStr.substring(first, last));

        first = lonStr.lastIndexOf("\">") + "\">".length();
        last  = lonStr.lastIndexOf("</td>");

        longitude = Double.parseDouble(lonStr.substring(first, last));

        return new GeoLocation(latitude, longitude);
    }

    public TwitterStreamer(String[] myargs) {
        this.args = myargs;
    }

    public final void startStreamer() throws Exception {
        final int
                queryNum = 0,
                placeNum = 1,
                streamNum = 2,
                hideNum = 3,
                limitNum = 4,
                helpNum = 5;

        int[] requestedParams = {-1, -1, -1, -1, -1, -1};


        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--query") || args[i].equals("-q")) {
                requestedParams[queryNum] = i + 1;
            }
            if (args[i].equals("--place") || args[i].equals("-p")) {
                if (requestedParams[queryNum] == -1) {
                    throw new Exception("no query for place!");
                }
                requestedParams[placeNum] = i + 1;
            }
            if (args[i].equals("--stream") || args[i].equals("-s")) {
                requestedParams[streamNum] = i + 1;
            }
            if (args[i].equals("--hideRetweets")) {
                requestedParams[hideNum] = i;
            }
            if (args[i].equals("--limit") || args[i].equals("-l")) {
                requestedParams[limitNum] = i + 1;
            }
            if (args[i].equals("--help") || args[i].equals("-h")) {
                requestedParams[helpNum] = i;
            }
        }



        Twitter twitter = TwitterFactory.getSingleton();
        //Here we'll create new query using string query

        if (requestedParams[queryNum] != -1) {

            String queryString = args[requestedParams[queryNum]];
            Query query = new Query(queryString);
            int index = requestedParams[limitNum];
            if ((index) != -1) {
                int limit = Integer.parseInt(args[index]);

                if (limit < 0 || limit > tweetsLimit) {
                    throw new Exception("wrong limit!");
                }
                query.setCount(limit);
            }
            //Here, if we are requested to do it,
            // we must ADD LOCATION for the query
            String location = "anywhere";
            if (requestedParams[placeNum] != -1) {
                location = args[requestedParams[placeNum]];

                if (location.equals("nearby") || location.equals("Nearby")) {
                    location = getCityString();
                }
                query = setSearchPlace(twitter, query, location);
            }

            if (requestedParams[streamNum] != -1) {
                runStreamer(query);
            } else {

                //Next, searching tweets by query, get Class QueryResult
                QueryResult queryResult = twitter.search(query);
                //Next Getting list of tweets by list <Status>
                List<Status> statusList = queryResult.getTweets();
                //Then trying to print it on the screen

                System.out.println(
                        "Твиты по запросу "
                                + queryString
                                + " для "
                                + location
                                + ":"
                );
                for (Status tweet : statusList) {
                    printTweet(tweet, requestedParams[hideNum] != -1);
                }
            }
        }

    }

    private void runStreamer(Query query) throws Exception {

        System.out.println("Streamer successfully run...");

        TwitterStream streamer = new TwitterStreamFactory().getInstance();
        StatusAdapter listener = new StatusAdapter() {
                    @Override
                    public void onStatus(Status status) {
                        printTweet(status, true);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    @Override
                    public void onException(Exception ex) {
                        System.out.println("Problems listening : "
                                + ex.getMessage());
                    }
                };
        streamer.addListener(listener);

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(query.getQuery());
        streamer.filter(filterQuery);


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            Thread.sleep(sleepTime);
        }
    }

    private void printTweet(Status tweet, boolean hideRetweets) {
        System.out.println("-----------------------------------------");
        String timeToPrint = getTimeFormattedTimeString(tweet.getCreatedAt());
        System.out.print("[" + timeToPrint + "]" + "@");
        System.out.print(tweet.getUser().getScreenName() + " : "
                + tweet.getText());

        int retweetCount = tweet.getRetweetCount();
        if (retweetCount != 0) {
            System.out.println(" (" + tweet.getRetweetCount() + " ретвитов)");
            if (!hideRetweets) {
                printRetweets(tweet);
            }
        } else {
            System.out.println();
        }

    }

    private void printRetweets(Status tweet) {
        Status retweet = tweet.getRetweetedStatus();
        if (retweet == null) {
            return;
        }
        String timeToPrint = getTimeFormattedTimeString(retweet.getCreatedAt());
        System.out.print("[" + timeToPrint + "]");
        System.out.print("@" + retweet.getUser().getScreenName() + " ");
        System.out.print("ретвитнул @" + tweet.getUser().getScreenName()
                + ": ");
        System.out.println(retweet.getText());
    }

    private Query setSearchPlace(Twitter twitter, Query query,
                                 String placeString) throws Exception {
        if (placeString.equals("nearby") || placeString.equals("Nearby")) {
            placeString = getCityString();
        }
        //Search by places

        Vector<GeoLocation> locations = new Vector<GeoLocation>();

        GeoQuery geoQuery = new GeoQuery("0.0.0.0");
        geoQuery.setQuery(placeString);
        //Then getting list of places:
        ResponseList<Place> responseList;
        responseList = twitter.searchPlaces(geoQuery);
        //Then looking through responseList,
        // we will find the center of coordinates
        for (Place place : responseList) {

            for (int i = 0; i < place.getBoundingBoxCoordinates().length; i++) {
                for (int j = 0;
                     j < place.getBoundingBoxCoordinates()[i].length; j++) {
                    locations.add(place.getBoundingBoxCoordinates()[i][j]);
                }
            }
        }

        //Then getting center
        double x = 0, y = 0;
        for (GeoLocation geoLocation : locations) {
            x += geoLocation.getLatitude();
            y += geoLocation.getLongitude();
        }
        x /= locations.size();
        y /= locations.size();
        //Center is OK

        //Then getting Radius
        double radius = 0;
        for (GeoLocation geoLocation : locations) {
            radius += getDistanceBetweenCoordinates(x, y,
                    geoLocation.getLatitude(),
                    geoLocation.getLongitude());
        }
        radius /= locations.size();
        System.out.println("Place = " + placeString + " Radius = " + radius);
        //Radius is OK

        //Then making geolocation for query
        GeoLocation ourLocation = new GeoLocation(x, y);
        query.setGeoCode(ourLocation, radius, Query.Unit.km);
        //OK

        return query;
    }

    private double getDistanceBetweenCoordinates(double latitude1,
                                                 double longitude1,
                                                 double latitude2,
                                                 double longitude2) {

        double theta = longitude1 - longitude2;
        double dist = Math.sin(latitude1 * DEG_TO_RAD)
                * Math.sin(latitude2 * DEG_TO_RAD)
                + Math.cos(latitude1  * DEG_TO_RAD)
                * Math.cos(latitude2 * DEG_TO_RAD)
                * Math.cos(theta * DEG_TO_RAD);
        dist = Math.acos(dist);
        dist = dist * RAD_TO_DEG;
        dist = dist * DEG_TO_KM;
        return dist;
    }

    private String getTimeFormattedTimeString(Date createdAt) {
        //Getting today's date and current time.
        Date date = Calendar.getInstance().getTime();
        String ending;
        long delta = date.getTime() - createdAt.getTime();
        assert (delta >= 0);
        if (delta < 2 * MIN) {
            return "Только что";
        } else if (delta < HOUR) {
            long minutes = delta / MIN;
            if (minutes >= TEN && minutes <= TWENTY) {
                ending = "";
            } else {
                long ostatok = minutes % TEN;
                if (ostatok == ONE) {
                    ending = "у";
                } else if (ostatok == TWO
                        || ostatok == THREE || ostatok == FOUR) {
                    ending = "ы";
                } else {
                    ending = "";
                }
            }
            return (delta / MIN) + " минут" + ending + " назад";
        } else if (delta < DAY) {
            long hours = delta / HOUR;
            if (hours > TEN && hours < TWENTY) {
                ending = "ов";
            } else {
                long ostatok = hours % TEN;
                if (ostatok == ONE) {
                    ending = "";
                } else if (ostatok == TWO
                        || ostatok == THREE || ostatok == FOUR) {
                    ending = "а";
                } else {
                    ending = "ов";
                }
            }
            return (delta / HOUR) + " час" + ending + " назад";
        } else if (delta < 2 * DAY) {
            return "Вчера";
        } else {
            long days = delta / DAY;
            if (days >= TEN && days <= TWENTY) {
                ending = "ней";
            } else {
                long ostatok = days % TEN;
                if (ostatok == 1) {
                    ending = "ень";
                } else if (ostatok == TWO
                        || ostatok == THREE || ostatok == FOUR) {
                    ending = "ня";
                } else {
                    ending = "ней";
                }
            }
            return (delta / DAY) + " д" + ending + " назад";
        }
    }
}
