/**
 * Created by admin on 27.09.2015.
 */

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;

public class TwitterStreamApp {
    //color for nicks
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
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
                   // printTime(tweet);
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
            System.out.println(new StringBuilder().
                    append("Failed to search tweets: ").append(te.getMessage()).toString());
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
        Formatter retweetsFormatter = new Formatter();
        if (status.isRetweet()) {
            if (!hideRetweets) {
                printTime(status);
                System.out.println(new StringBuilder().append(ANSI_BLUE).append("@").
                        append(status.getUser().getScreenName()).append(ANSI_RESET).
                        append(": ретвитнул ").append(ANSI_BLUE).append("@").
                        append(status.getRetweetedStatus().getUser().getScreenName()).
                        append(ANSI_RESET).append(": ").
                        append(status.getRetweetedStatus().getText()).toString());
            }
        } else {
            printTime(status);
            System.out.print(new StringBuilder().
                    append(ANSI_BLUE).append("@").
                    append(status.getUser().getScreenName()).
                    append(ANSI_RESET).append(": ").
                    append(status.getText()).toString());
            if (status.getRetweetCount() != 0) {
                System.out.print(new StringBuilder().append("(").
                        append(status.getRetweetCount()).append(" ").
                        append(retweetsFormatter.retweet(status.getRetweetCount())).
                        append(")").toString());
            }
            System.out.println();
        }
    }

    public static void printTime(Status status) {
        Formatter timeFormatter = new Formatter();
        long currentTimeToFormat = System.currentTimeMillis();
        long tweetTimeToFormat = status.getCreatedAt().getTime();

        LocalDateTime currentTime = new Date(currentTimeToFormat).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime tweetTime = new Date(tweetTimeToFormat).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        System.out.print("[");
        if (ChronoUnit.MINUTES.between(tweetTime, currentTime) <= 2) {
            System.out.print("только что");
        } else {
            if (ChronoUnit.HOURS.between(tweetTime, currentTime) < 1) {
                System.out.print(new StringBuilder().
                        append(ChronoUnit.MINUTES.between(tweetTime, currentTime)).
                        append(" ").append(timeFormatter.minutes(ChronoUnit.MINUTES.between(tweetTime, currentTime))).
                        append(" назад").toString());
            } else {
                if (ChronoUnit.DAYS.between(tweetTime, currentTime) < 1) {
                    System.out.print(new StringBuilder().
                            append(ChronoUnit.HOURS.between(tweetTime, currentTime)).
                            append(" ").append(timeFormatter.hours(ChronoUnit.HOURS.between(tweetTime, currentTime))).
                            append(" назад").toString());
                    } else {
                    if (ChronoUnit.DAYS.between(tweetTime, currentTime) == 1) {
                        System.out.print("вчера");
                    } else {
                        System.out.print(new StringBuilder().
                                append(ChronoUnit.DAYS.between(tweetTime, currentTime)).
                                append(" ").append(timeFormatter.days(ChronoUnit.DAYS.between(tweetTime, currentTime))).
                                append(" назад").toString());
                    }
                }
            }
        }
        System.out.print("] ");
    }
}

