package ru.mipt.diht.students.annnvl.TwitterStream;

import twitter4j.*;
import com.beust.jcommander.JCommander;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class TwitterStream {

    public static final int PAUSE = 1000;

    public static void printTime(Status status) {
        PrintTime printer = new PrintTime();
        System.out.print(new StringBuilder().append("[")
                .append(printer.printTime(status.getCreatedAt().getTime(), System.currentTimeMillis())).append("]"));
    }

    public static void printTweet(Status status, boolean hideRetweets) {
        if (status.isRetweet()) {
            if (!hideRetweets) {
                printTime(status);
                System.out.println(new StringBuilder().append("@").append(status.getUser().getName())
                        .append(" ретвитнул: @").append(status.getRetweetedStatus().getUser().getName()).append(": ")
                        .append(status.getRetweetedStatus().getText()).toString());
            }
        } else {
            Format retweetFormat = new Format();
            printTime(status);
            System.out.print(new StringBuilder().append("@").append(status.getUser().getName()).append(": ")
                    .append(status.getText()).append(retweetFormat.strForm(status.getRetweetCount())));
            System.out.println();
        }
    }

    public static Query setQuery(Parameters param) throws Exception {
        Query query = new Query(param.getQuery());
        if (!param.getPlace().isEmpty()) {
            FindPlace googleFindPlace;
            googleFindPlace = new FindPlace(param.getPlace());
            GeoLocation geoLocation;
            geoLocation = new GeoLocation(googleFindPlace.getLocation().lat, googleFindPlace.getLocation().lng);
            query.setGeoCode(geoLocation, googleFindPlace.getRadius(), Query.KILOMETERS);
        }
        return query;
    }

    public static List<Status> search(Parameters param) throws Exception {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = setQuery(param);
        QueryResult result;
        int limit = param.getLimit();
        int statusCount = 0;
        List<Status> tweets = new ArrayList<>();
        do {
            result = twitter.search(query);
            for (Status status : result.getTweets()) {
                if (status.isRetweet() && param.isHideRetweets())
                    continue;
                tweets.add(status);
                printTime(status);
                printTweet(status, param.isHideRetweets());
                statusCount++;
                limit--;
                if (limit == 0) {
                    break;
                }
            }
            query = result.nextQuery();
        } while (query != null && limit > 0);
        if (statusCount == 0) {
            System.out.println("Подходящих твитов нет");
        }
        return tweets;
    }

    public static FilterQuery setFilter(Parameters param) throws Exception{
        String[] track = new String[1];
        track[0] = param.getQuery();
        long[] follow = new long[0];
        FilterQuery filter = new FilterQuery(0, follow, track);
        if (!param.getPlace().isEmpty()) {
            FindPlace googleFindPlace;
            googleFindPlace = new FindPlace(param.getPlace());
            double[][] bounds = {{googleFindPlace.getBounds().southwest.lng, googleFindPlace.getBounds().southwest.lat},
                    {googleFindPlace.getBounds().northeast.lng, googleFindPlace.getBounds().northeast.lat}};
            filter.locations(bounds);
        }
        return filter;
    }

    public static void stream(Parameters param, StatusListener listener) throws Exception {
        twitter4j.TwitterStream twitterStream;
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        if (param.getQuery() == "" && param.getPlace() == "") {
            twitterStream.sample();
        } else {
            FilterQuery filter = setFilter(param);
            twitterStream.filter(filter);
        }
    }

    public static void main(String[] args) throws Exception {
        final Parameters param = new Parameters();
        JCommander cmd = new JCommander(param, args);
        if (param.isHelp()) {
            cmd.usage();
            System.exit(0);
        }
        if (param.isStream()) {
            StatusListener listener = new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    printTweet(status, param.isHideRetweets());
                    try {
                        sleep(PAUSE);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            };
            try {
                stream(param, listener);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
                stream(param, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                search(param);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
                search(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
};

