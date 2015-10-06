
/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import com.beust.jcommander.JCommander;
import org.json.JSONException;
import twitter4j.*;
import twitter4j.StatusListener;

import java.util.Collections;
import java.util.List;
import java.io.IOException;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import javafx.util.Pair;


public class TwitterStream {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BOLD = "\033[1m";
    private static final int TRIES_LIMIT = 1; // limit of connections attempts
    private static final long SLEEP_TIME = 1000;

    private static class SplitLine {
        private static final int SPLIT_LINE_LENGTH = 80;
        private static String splitLine = "";

        private static void makeSplitLine() {
            for (int i = 0; i < SPLIT_LINE_LENGTH; ++i) {
                splitLine += "-";
            }
        }

        public static void print() {
            if (splitLine.length() != SPLIT_LINE_LENGTH) {
                makeSplitLine();
            }

            System.out.println(splitLine);
        }
    }

    private static String getRetweetInfo(Status status) {
        String answerStr = "";

        int countTweets = status.getRetweetCount();
        if (countTweets > 0) {

            String retweetDeclension =
                    FormDeclenser.getTweetsDeclension(countTweets);
            answerStr += "(" + ANSI_BOLD
                    + countTweets
                    + ANSI_RESET
                    + " " + retweetDeclension
                    + ")";
        }
        return answerStr;
    }

    private static void printFormattedTweet(Status status, JCommanderParser jcp) {

        String tweetText = status.getText();
        String time = "";

        if (!jcp.isStream()) {
            time = "["
                    + TimeDeterminer.getTimeDifference(status.getCreatedAt())
                    + "]" + " ";
        }
        String outputString = time + ANSI_BLUE + ANSI_BOLD + "@"
                + status.getUser()
                .getScreenName()
                + ANSI_RESET
                + ": ";

        if (status.isRetweet()) {
            int firstNameIndex = tweetText.indexOf('@', 0) + 1;
            int lastNameIndex = tweetText.indexOf(':', firstNameIndex);
            String tweetAuthor = tweetText.substring(firstNameIndex, lastNameIndex - 1);
            tweetText = tweetText.substring(lastNameIndex + 1);
            outputString += "ретвитнул "
                    + ANSI_BLUE + ANSI_BOLD + "@"
                    + tweetAuthor
                    + ANSI_RESET + ":";

        }

        outputString += tweetText;
        outputString += getRetweetInfo(status);

        System.out.println(outputString);

        SplitLine.print();

    }

    private static void streamStart(JCommanderParser jcp,
                                    Pair<GeoLocation, Double> geoParams) {

        twitter4j.TwitterStream twStream = twitter4j
                .TwitterStreamFactory.getSingleton();

        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                if (jcp.isHideRetweets() && status.isRetweet()) {
                    return;
                }
                if (geoParams != null) {
                    GeoLocation tweetLocation = null;
                    if (status.getGeoLocation() != null) {
                        tweetLocation = status.getGeoLocation();
                    } else {

//                    if (status.getUser().getLocation() != null) {
//                        try {
//
//                            tweetLocation =
//                                GeoLocationResolver
//                                    .getGeoLocation(status.getUser().getLocation()).getKey();
//
//                            } catch (Exception e) {
//                                return;
//                            }
//                    } else {
                        return;
                    }

                    double myLatitude = geoParams.getKey().getLatitude();
                    double myLongitude = geoParams.getKey().getLongitude();
                    double tweetLatitude = tweetLocation.getLatitude();
                    double tweetLongitude = tweetLocation.getLongitude();

                    if (GeoLocationResolver.getSphereDist(
                            myLatitude, myLongitude,
                            tweetLatitude, tweetLongitude) > geoParams.getValue()) {
                        return;
                    }
                }

                printFormattedTweet(status, jcp);

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }

            }
        };

        FilterQuery query = new FilterQuery(jcp.getQuery());
        twStream.addListener(listener);

        if (jcp.getQuery().length() == 0) {
            twStream.sample();
        } else {
            twStream.filter(query);
        }
    }

    private static void printSingleTwitterQuery(final JCommanderParser jCommanderParser,
                                                Pair<GeoLocation, Double> geoParams) {

        int tries = 0;
        QueryResult result = null;

        while (tries < TRIES_LIMIT) {
            try {
                ++tries;
                Twitter twitter = TwitterFactory.getSingleton();
                if (jCommanderParser.getQuery().length() == 0) {
                    System.out.println("Задан пустой запрос. Невозможно осуществить поиск");
                    System.exit(1);
                }

                Query query = new Query(jCommanderParser.getQuery());

                query.setCount(jCommanderParser.getLimit());

                if (geoParams != null) {
                    query.geoCode(geoParams.getKey(),
                            geoParams.getValue(), GeoLocationResolver.RADIUS_UNIT);
                }

                result = twitter.search(query);
            } catch (TwitterException e) {
                e.getErrorCode();
                System.err.print(e.getMessage() + " \n"
                        + "Try connect. Tries:" + tries + ".");
            }
        }

        if (tries == TRIES_LIMIT + 1) {
            System.err.println("ERROR: Couldn't set connection");
            System.exit(-1);
        }

        List<Status> tweets = result.getTweets();
        Collections.reverse(tweets);

        int countTweets = 0;
        for (Status status : tweets) {
            if (!jCommanderParser.isHideRetweets() || !status.isRetweet()) {
                ++countTweets;
                printFormattedTweet(status, jCommanderParser);
            }
        }

        if (countTweets == 0) {
            System.out.println("по запросу \""
                    + jCommanderParser.getQuery() + "\" ничего не найдено.");
        }
    }

    public static void main(String[] args) throws TwitterException {

        System.out.print(ANSI_PURPLE
                + ANSI_BOLD
                + "\nTwitter 0.1 ::: welcome \n\n"
                + ANSI_RESET);

        JCommanderParser jcp = new JCommanderParser();
        try {
            JCommander jcm = new JCommander(jcp, args);
            jcm.setProgramName("TwitterStream");

            if (jcp.isHelp()) {
                jcm.usage();
                return;
            }


            String curLocationRequest = "";
            Pair<GeoLocation, Double> geoParams = null;
            try {
                if (jcp.getLocation() != "") {
                    if (jcp.getLocation().equals("nearby")) {
                        curLocationRequest = GeoLocationResolver.getNameOfCurrentLocation();
                    } else {
                        curLocationRequest = jcp.getLocation();
                    }
                    geoParams = GeoLocationResolver
                            .getGeoLocation(curLocationRequest);

                    System.out.println("location is " + curLocationRequest
                            + ", latitude :"
                            + geoParams.getKey().getLatitude()
                            + " longitude :"
                            + geoParams.getKey().getLongitude()
                            + ", radius(km): "
                            + geoParams.getValue());
                }
            } catch (IOException | LocationException | JSONException e) {
                e.getMessage();
                System.out.println("Can't detect location\n" + "Region: World ");
            }

            if (jcp.isStream()) {
                streamStart(jcp, geoParams);
            } else {
                printSingleTwitterQuery(jcp, geoParams);
            }
        } catch (Exception e) {

            System.out.println(e.getMessage());
            JCommander jcm = new JCommander(jcp, "");
            jcm.setProgramName("TwitterStream");
            jcm.usage();
        }
    }
}
