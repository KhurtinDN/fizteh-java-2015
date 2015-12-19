package ru.mipt.diht.students.IrinaMudrova.Twitter;

//import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.*;

import ru.mipt.diht.students.IrinaMudrova.Twitter.library.TweetFormatter;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.TwitterListener;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.TwitterOptions;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.TwitterStreamAssistFactory;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.YandexPlaces;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.exceptions.PlaceNotFoundException;
import ru.mipt.diht.students.IrinaMudrova.Twitter.library.exceptions.TwitterParameterException;
import twitter4j.*;

public class TwitterStream {
    protected TwitterOptions opt;
    protected Twitter twitter;
    protected Query query;
    protected FilterQuery filterQuery;
    protected QueryResult queryResult;
    protected twitter4j.TwitterStream ts;
    protected TwitterStreamAssistFactory factory;
    protected PrintStream err;
    protected PrintStream out;
    protected TweetFormatter tweetFormatter;


    public static void main(String[] args) {
        try {
            TwitterStream twitterStream = new TwitterStream();
            twitterStream.init();
            twitterStream.startTwitterStream(args);

        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    protected void init() {
        err = System.err;
        out = System.out;
        factory = new TwitterStreamAssistFactory();
        tweetFormatter = factory.newTweetFormatter();
        opt = factory.newTwitterOptions();
    }

    protected void startTwitterStream(String[] args) throws Exception {
        try {
            opt.parse(args);
        } catch (TwitterParameterException e) {
            opt.usage(out);
            return;
        }
        if (opt.isNeedToShowHelp()) {
            opt.usage(out);
            return;
        }

        if (opt.isStreaming()) {
            startStreaming();
        } else {
            genAndShowResult();
        }
    }

    protected void startStreaming() throws InterruptedException {
        ts = factory.newTwitterStream();
        TwitterListener tl = factory.newTwitterListener();
        ts.addListener(tl.init(opt.isHidingRetweets()));
        makeFilterQuery();
        ts.filter(filterQuery);
        final Integer ms2s = new Integer(1000);
        while (true) {
            Thread.sleep(ms2s);
            err.println("Iteration");
            String outStr = tl.pollTweetStr();
            if (outStr != null) {
                out.println(outStr);
            }
        }
    }


    protected void makeFilterQuery() {
        filterQuery = factory.newFilterQuery();
        filterQuery.track(opt.getQuery());
        if (opt.isSetPlace()) {
            try {
                YandexPlaces places = factory.newYandexPlaces();
                places.setPlaceQuery(opt.getPlace());
                double bnds[][] = places.calcBounds();
                double revBnds[][] = {{bnds[0][1], bnds[0][0]}, {bnds[1][1], bnds[1][0]}};
                filterQuery.locations(revBnds);
            } catch (PlaceNotFoundException e) {
                err.println("Problem with --place option. Program will be continued without it.");
            }
        }
    }

    protected void genAndShowResult() throws TwitterException {
        makeQuery();
        try {
            twitter = factory.newTwitter();
            queryResult = twitter.search(query);
        } catch (Exception e) {
            err.println("Some problem with query or internet connection");
            throw e;
        }
        showTweets(queryResult.getTweets());
    }

    protected void showTweets(List<Status> tweets) {
        if (opt.isHidingRetweets()) {
            tweets = tweets.stream()
                    .filter(p -> !p.isRetweet()).collect(Collectors.toList());
        }
        out.println("There are " + String.valueOf(tweets.size()) + " tweets about your query without limitation");
        if (opt.isSetLimit()) {
            tweets = tweets.subList(0, Math.min(tweets.size(), opt.getLimit()));
        }
        if (tweets.size() > 0) {
            for (Status t : tweets) {
                showOneTweet(t, TweetFormatter.ShowTime.yes);
            }
        } else {
            out.println("There are not any tweets.");
        }
    }

    protected void showOneTweet(Status tweet, TweetFormatter.ShowTime showTime) {
        out.println(tweetFormatter.oneTweetToStr(tweet, showTime));
    }

    protected void makeQuery() {
        err.println("QueryStr is: " + opt.getQuery() + tweetFormatter.clauseStr(opt.isHidingRetweets(),
                tweetFormatter.clauseStr(opt.getQuery().length() > 0, "+") + "exclude:retweets"));
        query = factory.newQuery();
        query.setQuery(opt.getQuery() + tweetFormatter.clauseStr(opt.isHidingRetweets(),
                tweetFormatter.clauseStr(opt.getQuery().length() > 0, "+") + "exclude:retweets"));
        if (opt.isSetLimit()) {
            query.setCount(opt.getLimit());
        }
        if (opt.isSetPlace()) {
            try {
                YandexPlaces places = factory.newYandexPlaces();
                places.setPlaceQuery(opt.getPlace());
                double[] coord = places.calcCoord();
                query.setGeoCode(new GeoLocation(coord[1], coord[0]), places.calcRadiusKm(), Query.Unit.km);
            } catch (PlaceNotFoundException e) {
                err.println("Sorry but --place option is failed, and run will be continued without it");
            }
        }
    }

}
