package ru.mipt.diht.students.lenazherdeva.twitterStream;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 19.10.2015.
 */
public class Search {
    private static final int RADIUS = 1;
    private final Twitter twitterr;

    public Search(Twitter loctwitterr) {
        this.twitterr = loctwitterr;
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

    public final List<String> searchResult(Parameters param) throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey("3b3vKQPtk7PoHEOekUedoIQPC")
                .setOAuthConsumerSecret("ADrGDZORevHvt3iF9Ot3xwfMeufol2lsG58XmAcqyCSsGkQZkR")
                .setOAuthAccessToken("2783476952-M6Pe8LR4gLYeKKDzdwjVKLkcFwMP38qDE1vgvP2")
                .setOAuthAccessTokenSecret("mfAU8iq63vU3omwqje8SXRQr0QCfonoK4eSjrpX61gKe8");
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
        if (statusCount == 0) {
            throw new NoTweetsException("Твиты по запросу не найдены");
        }
        return tweets.stream().map(new StatusParser(param)::printStatus).collect(toList());
    }
}
