package ru.mipt.diht.students.feezboom.Twitter;

import ru.mipt.diht.students.feezboom.StringUtils.StringUtils;
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

    private String[] args;
    private final int sleepTime = 1000;
    private final int tweetsLimit = 100;



    public static String getCityString() throws IOException {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner =
                new Scanner(urlConnection.getInputStream(), "MacCyrillic");

        //I must find it from a source code (country)
        final int neededLine = 6;
        String substring = "";

        for (int i = 0; i < neededLine; i++) {
            substring = scanner.nextLine();
        }

        //Finding country from source code
        int first = substring.indexOf("—трана: ") + "—трана: ".length();
        int last = substring.indexOf(", –егион");
        String country = substring.substring(first, last);

        //Finding city from source code
        first = substring.indexOf("√ород: ") + "√ород: ".length();
        last = substring.indexOf("\">");
        String city = substring.substring(first, last);


        scanner.close();

        //If country == Russia -> city's name has russian equivalent
        if (country.equals("Russian Federation")) {
            city = StringUtils.translitToRussian(city);
        }
        return city;
    }

    public static double[] getCoordinates() throws Exception {
        URL url = new URL("http://ip2geolocation.com/");
        URLConnection urlConnection = url.openConnection();
        Scanner scanner = new Scanner(urlConnection.getInputStream(), "UTF-8");
        //todo
        //here is planned to return latitude and longitude
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

        //returning geolocation by received latitude and longitude by ip
        return new GeoLocation(latitude, longitude);
    }

    public TwitterStreamer(String[] myargs) {
        this.args = myargs;
    }

    public final void startTwitting() throws Exception {
        final int
                queryNum = 0,
                placeNum = 1,
                streamNum = 2,
                hideNum = 3,
                limitNum = 4,
                helpNum = 5;

        boolean isQuery = false,
                isPlace = false,
                isStream = false,
                hideRetweets = false,
                isLimit = false,
                isHelp = false;

        int[] requestedParams = {-1, -1, -1, -1, -1, -1};

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--query") || args[i].equals("-q")) {
                requestedParams[queryNum] = i + 1;
                isQuery = true;
            }
            if (args[i].equals("--place") || args[i].equals("-p")) {
                requestedParams[placeNum] = i + 1;
                isPlace = true;
            }
            if (args[i].equals("--stream") || args[i].equals("-s")) {
                isStream = true;
            }
            if (args[i].equals("--hideRetweets")) {
                hideRetweets = true;
            }
            if (args[i].equals("--limit") || args[i].equals("-l")) {
                requestedParams[limitNum] = i + 1;
                isLimit = true;
            }
            if (args[i].equals("--help") || args[i].equals("-h")) {
                isHelp = true;
            }
        }

        if (!isQuery) {
            System.err.println("Query is essential argument!");
            System.exit(1);
        }

        Twitter twitter = TwitterFactory.getSingleton();

        //Here we'll create new query using string query
        String queryString = args[requestedParams[queryNum]];
        Query query = new Query(queryString);
        int index = requestedParams[limitNum];
        if ((index) != -1) {
            int limit = Integer.parseInt(args[index]);

            if (limit < 0 || limit > tweetsLimit) {
                System.err.println("Can not set limit not in range 0..100");
                System.exit(1);
            }
            query.setCount(limit);
        }
        //Here, if we are requested to do it,
        // we must ADD LOCATION for the query
        String location = "anywhere";
        if (isPlace) {
            location = args[requestedParams[placeNum]];

            if (location.equals("nearby") || location.equals("Nearby")) {
                location = getCityString();
            }
            query = setSearchPlace(twitter, query, location);
        }

        if (isStream) {
            runStreamer(query);
        } else {
            //Next, searching tweets by query, get Class QueryResult
            QueryResult queryResult = twitter.search(query);
            //Next Getting list of tweets by list <Status>
            List<Status> statusList = queryResult.getTweets();
            //Then trying to print it on the screen
            //Check if no tweets:
            if (statusList.isEmpty()) {
                System.out.printf("There is no tweets on your query here");
                return;
            }
            System.out.println(
                    "Твиты по запросу "
                            + queryString
                            + " для "
                            + location
                            + ":"
            );
            for (Status tweet : statusList) {
                printTweet(tweet, hideRetweets, isStream);
            }
        }

    }

    private void runStreamer(Query query) throws Exception {

        System.out.println("Streamer successfully run...");

        TwitterStream streamer = new TwitterStreamFactory().getInstance();
        StatusAdapter listener = new StatusAdapter() {
                    @Override
                    public void onStatus(Status status) {
                        printTweet(status, true, true);
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

    private void printTweetTime(Status tweet) {
        String timeToPrint = getTimeFormattedTimeString(tweet.getCreatedAt());
        System.out.print("[" + timeToPrint + "]");
    }

    private void printTweet(Status tweet,
                            boolean hideRetweets,
                            boolean isStream) {
        System.out.println("-----------------------------------------");
        if (!isStream) {
            printTweetTime(tweet);
        }
        System.out.print("@"
                + StringUtils.paint(tweet.getUser().getScreenName(), "cyan")
                + " : "
                + tweet.getText());

        int retweetCount = tweet.getRetweetCount();
        if (retweetCount != 0) {
            System.out.println(" ("
                    + tweet.getRetweetCount() + " "
                    + getTweetFormattedString(tweet.getRetweetCount()) + ")");
            if (!hideRetweets) {
                printRetweets(tweet, isStream);
            }
        } else {
            System.out.println();
        }
    }

    private void printRetweets(Status tweet, boolean isStream) {
        Status retweet = tweet.getRetweetedStatus();
        if (retweet == null) {
            return;
        }
        if (!isStream) {
            printTweetTime(retweet);
        }
        System.out.print(
                "@"
                + StringUtils.paint(retweet.getUser().getScreenName(), "cyan")
                + " ");
        System.out.print("ретвитнул @" + tweet.getUser().getScreenName()
                + ": ");
        System.out.println(retweet.getText());
    }

    private Query setSearchPlace(Twitter twitter, Query query,
                                 String placeString) throws Exception {
        //Search by places
        Vector<GeoLocation> locations = new Vector<>();
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
        final double minRadius = 10;
        double radius = 0;
        for (GeoLocation geoLocation : locations) {
            radius += getDistanceBetweenCoordinates(x, y,
                    geoLocation.getLatitude(),
                    geoLocation.getLongitude());
        }
        radius /= locations.size();
        if (radius < minRadius) {
            radius = minRadius;
        }
//        System.out.println("Place = " + placeString + " Radius = " + radius);
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

    private String getTweetFormattedString(int tweetsNumber) {
        final byte
                ten = 10,
                exceptStart = 11,
                exceptFinish = 19,
                ovMin = 5,
                ovMax = 9,
                ovEx = 0,
                zeroNum = 1;
        int remainder = tweetsNumber % ten;
        if (tweetsNumber >= exceptStart && tweetsNumber <= exceptFinish
                || remainder >= ovMin && remainder <= ovMax
                || remainder == ovEx) {
            return "ретвитов";
        } else if (remainder == zeroNum) {
            return "ретвит";
        } else {
            return "ретвита";
        }
    }

    private String getTimeFormattedTimeString(Date createdAt) {
        //Remainders
        final byte one = 1;
        final byte two = 2;
        final byte three = 3;
        final byte four = 4;
        final byte ten = 10;
        final byte twenty = 20;
        //Times
        final long sec = 1000;
        final long min = sec * 60;
        final long hour = min * 60;
        final long day = hour * 24;
        //Getting today's date and current time.
        Date date = Calendar.getInstance().getTime();
        String ending;
        long delta = date.getTime() - createdAt.getTime();
        assert (delta >= 0);
        if (delta < 2 * min) {
            return "Только что";
        } else if (delta < hour) {
            long minutes = delta / min;
            if (minutes >= ten && minutes <= twenty) {
                ending = "";
            } else {
                long ostatok = minutes % ten;
                if (ostatok == one) {
                    ending = "у";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "ы";
                } else {
                    ending = "";
                }
            }
            return (delta / min) + " минут" + ending + " назад";
        } else if (delta < day) {
            long hours = delta / hour;
            if (hours > ten && hours < twenty) {
                ending = "ов";
            } else {
                long ostatok = hours % ten;
                if (ostatok == one) {
                    ending = "";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "а";
                } else {
                    ending = "ов";
                }
            }
            return (delta / hour) + " час" + ending + " назад";
        } else if (delta < 2 * day) {
            return "Вчера";
        } else {
            long days = delta / day;
            if (days >= ten && days <= twenty) {
                ending = "ней";
            } else {
                long ostatok = days % ten;
                if (ostatok == 1) {
                    ending = "ень";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "ня";
                } else {
                    ending = "ней";
                }
            }
            return (delta / day) + " д" + ending + " назад";
        }
    }
}
