package ru.mipt.diht.students.annnvl.TwitterStream;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class Stream {
    public static final long PAUSE = 1000;
    private static TwitterStream twitterStream;
    public Stream(TwitterStream twitterStreamm) {
        this.twitterStream = twitterStreamm;
    }
    public static FilterQuery setFilter(Parser param) throws Exception {
        String[] track = new String[1];
        track[0] = param.getQuery();
        long[] followArray = new long[0];
        FilterQuery filter = new FilterQuery(0, followArray, track);
        if (!param.getPlace().equals("")) {
            Location geoLocation;
            geoLocation = new Location(param.getPlace());
            double[][] bounds = {{geoLocation.getBounds().southwest.lng,
                    geoLocation.getBounds().southwest.lat},  //широта южная
                    {geoLocation.getBounds().northeast.lng,  //долгота северная
                            geoLocation.getBounds().northeast.lat}};
            filter.locations(bounds);
        }
        return filter;
    }
    public static void streamPrint(Parser param)
            throws Exception {ConfigurationBuilder cb = new ConfigurationBuilder();cb.setDebugEnabled(false)
                    .setOAuthConsumerKey("3b3vKQPtk7PoHEOekUedoIQPC")
                    .setOAuthConsumerSecret("ADrGDZORevHvt3iF9Ot3xwfMeufol2lsG58XmAcqyCSsGkQZkR")
                    .setOAuthAccessToken("2783476952-M6Pe8LR4gLYeKKDzdwjVKLkcFwMP38qDE1vgvP2")
                    .setOAuthAccessTokenSecret("mfAU8iq63vU3omwqje8SXRQr0QCfonoK4eSjrpX61gKe8");TwitterStream twitterStreamm;twitterStreamm = new TwitterStreamFactory(cb.build()).getInstance();StatusAdapter listener = new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    try {
                        Thread.sleep(PAUSE);
                    } catch (InterruptedException e) {
                        System.out.print(e.getMessage());
                    }
                    System.out.println(new StatusParser(param).printStatus(status));
                }
            };twitterStreamm.addListener(listener);if (param.getQuery() == "" && param.getPlace() == "") {
                twitterStreamm.sample();
            } else {
                FilterQuery filter = setFilter(param);
                twitterStreamm.filter(filter);
            }
            }
}
