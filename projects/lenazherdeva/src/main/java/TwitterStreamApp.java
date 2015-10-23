/**
 * Created by admin on 27.09.2015.
 */

import com.beust.jcommander.JCommander;
import twitter4j.*;
import java.util.Date;
import java.util.List;

public class TwitterStreamApp {
    //color for nicks
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final long MIN = 1000 * 60;
    public static final long HOUR = 1000 * 60 * 60;
    public static final long TEN = 10;
    public static final long FIVE = 5;
    public static final long TWELVE = 12;
    public static final long ELEVEN = 11;
    public static final long DAY = 1000 * 60 * 60 * 24;
    public static final long PAUSE = 1000;


    public static void main(String[] args) throws Exception {
        Parameters param = new Parameters();
        JCommander cmd = null;
        try {
            cmd = new JCommander(param, args);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        if (param.isHelp()) {
            cmd.usage();
            System.exit(0);
        }
        if (param.isStream()) {
            try {
                streamPrint(param);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
                streamPrint(param);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                print(param);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
                print(param);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public static void streamPrint(Parameters param)  //stream-режим
            throws Exception {
        twitter4j.TwitterStream twitterStream;
        twitterStream = new TwitterStreamFactory().getInstance();
        StatusAdapter listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                try {
                    Thread.sleep(PAUSE);
                } catch (InterruptedException e) {
                    System.out.print(e.getMessage());
                }
                printStatus(status, param.hideRetweets());
            }
        };
        twitterStream.addListener(listener);

        if (param.getQuery() == "" && param.getLocation() == "") {
            twitterStream.sample();
        } else {
            FilterQuery filter = setFilter(param);
            twitterStream.filter(filter);
        }
    }

    public static void print(Parameters param) throws Exception {
        Twitter twitter = new TwitterFactory().getInstance();
        int limit = param.getLimit();
        Integer statusCounter = 0;
        try {
            Query query = setQuery(param);
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                Thread.sleep(PAUSE);
                for (Status tweet : tweets) {
                    printTime(tweet);
                    printStatus(tweet, param.hideRetweets());
                    limit--;
                    statusCounter++;
                    if (limit == 0) {
                        break;
                    }
                }
                query = result.nextQuery();
            } while (query != null && limit > 0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        if (statusCounter == 0) {
            System.out.println("По данному запросу результатов не найдено");
            System.exit(-1);
        }
    }

    public static FilterQuery setFilter(Parameters param) throws Exception {
        String[] track = new String[1];
        track[0] = param.getQuery();
        long[] followArray = new long[0];
        FilterQuery filter = new FilterQuery(0, followArray, track);
        if (!param.getLocation().equals("")) {
            GoogleGeoLocation findPlace;
            findPlace = new GoogleGeoLocation((param.getLocation()));
            double[][] bounds = {{findPlace.getBounds().southwest.lng,
                    findPlace.getBounds().southwest.lat},  //широта южная
                    {findPlace.getBounds().northeast.lng,  //долгота северная
                            findPlace.getBounds().northeast.lat}};
            filter.locations(bounds);
        }
        return filter;
    }

    public static Query setQuery(Parameters param) throws Exception {
        Query query = new Query(param.getQuery());
        //set place
        if (!param.getLocation().equals("")) {
            if (!param.getLocation().equals("nearby")) {
                GoogleGeoLocation googleFindPlace;
                googleFindPlace = new GoogleGeoLocation(param.getLocation());
                GeoLocation geoLocation;
                //широта:
                geoLocation = new GeoLocation(googleFindPlace.getLocation().lat,
                        googleFindPlace.getLocation().lng);  //долгота
                query.setGeoCode(geoLocation,
                        googleFindPlace.getRadius(), Query.KILOMETERS);
            }
        }
        return query;
    }

    //print tweets
    public static void printStatus(Status status, boolean hideRetweets) {
        if (status.isRetweet()) {
            if (!hideRetweets) {
                System.out.println(ANSI_BLUE
                        + "@" + status.getUser().getScreenName()
                        + ANSI_RESET + ": ретвитнул " + ANSI_BLUE
                        + "@"
                        + status.getRetweetedStatus().getUser().getScreenName()
                        + ANSI_RESET + ": "
                        + status.getRetweetedStatus().getText());
            }
        } else {
            System.out.print(ANSI_BLUE
                    + "@" + status.getUser().getScreenName()
                    + ANSI_RESET + ": " + status.getText());
            if (status.getRetweetCount() != 0) {
                System.out.print("(" + status.getRetweetCount() + " "
                        + retweets(status.getRetweetCount()) + ")");
            }
            System.out.println();
        }
    }

    public static String retweets(long count) {
        if (count % TEN == 1 && count != ELEVEN) {
            return "ретвит";
        } else {
            if (count % TEN > 1 && count % TEN < FIVE
                    && count != TWELVE) {
                return "ретвита";
            } else {
                return "ретвитов";
            }
        }
    }
    public static void printTime(Status status) {
        TimeFormatter timeFormatter = new TimeFormatter();
        Date date = new Date();
        long currentTime = date.getTime();
        long tweettime = status.getCreatedAt().getTime();
        long minBetween = (currentTime - tweettime) / MIN;
        long hoursBetween = (currentTime - tweettime) / HOUR;
        long daysBetween = (currentTime - tweettime) / DAY;
        System.out.print("[");
        if (minBetween <= 2) {
            System.out.print("только что");
        } else {
            if (hoursBetween < 1) {
                System.out.print(minBetween + " "
                        + timeFormatter.minutes(minBetween)
                        + " назад ");
            } else {
                if (timeFormatter.today(tweettime, currentTime)) {
                    System.out.print(hoursBetween + " "
                            + timeFormatter.hours(hoursBetween)
                            + " назад ");
                } else {
                    if (timeFormatter.yesterday(tweettime, currentTime)) {
                        System.out.print("вчера");
                    } else {
                        System.out.print(daysBetween + " "
                                + timeFormatter.days(daysBetween)
                                + " назад");
                    }
                }
            }
        }
        System.out.print("]");
    }
}

