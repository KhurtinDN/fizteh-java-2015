package ru.mipt.diht.students.annnvl.TwitterStream;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private static final int RADIUS = 1;
    private final Twitter twitter;
    public Search(Twitter loctwitter) {
        this.twitter = loctwitter;
    }
    public static Query setQuery(Parser param) throws Exception {
        Query query = new Query(param.getQuery());
        if (!param.getPlace().equals("")) {
            if (!param.getPlace().equals("nearby")) {
                Location googleFindPlace;
                googleFindPlace = new Location(param.getPlace());
                GeoLocation geoLocation;
                //широта:
                geoLocation = new GeoLocation(googleFindPlace.getLocation().lat, googleFindPlace.getLocation().lng);
                query.setGeoCode(geoLocation, googleFindPlace.getRadius(), Query.KILOMETERS);
            }
        }
        return query;
    }
    public final List<String> searchResult(Parser param) throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey("qUMuGf1CDg1n8RT1ZKyPxYVxb")
                .setOAuthConsumerSecret("ROnW8zh225ncTrbW5ZmqBNoCir9ktjgXdyNWvzO1i8RPMAmkrf")
                .setOAuthAccessToken("3782742196-MgaebvnRBwHYk1LhONdOAK14zOIyE9XBLvTdpFh")
                .setOAuthAccessTokenSecret("4Tz6FYRCGkEJzZGqFC5eX1jljlNOdbhDjZnkYT41gV8Wg");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        Query query = setQuery(param);
        QueryResult result;
        int limit = param.getLimit();
        int statusCount = 0;
        List<Status> tweets = new ArrayList<Status>();
        do {
            result = twitter.search(query);
            for (Status status : result.getTweets()) {
                tweets.add(status);
                statusCount++;
                limit--;
                if (limit == 0) {
                    break;
                }
            }
            query = result.nextQuery();
        } while (query != null && limit > 0);
        /*if (statusCount == 0) {
            throw new NoTweetsException("Твиты по запросу не найдены");
        }*/
        return tweets.stream().map(new StatusParser(param)::printStatus).collect(toList());
    }
}
