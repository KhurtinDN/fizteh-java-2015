package ru.mipt.diht.students.lenazherdeva.twitterStream;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by admin on 29.11.2015.
 */
public class StreamMode {
    public static final long PAUSE = 1000;
    private static TwitterStream twitterStream;

    public StreamMode(TwitterStream twitterStreamm) {
        this.twitterStream = twitterStreamm;
    }
    public static FilterQuery setFilter(Parameters param) throws Exception {
        String[] track = new String[1];
        track[0] = param.getQuery();
        long[] followArray = new long[0];
        FilterQuery filter = new FilterQuery(0, followArray, track);
        if (!param.getLocation().equals("")) {
            GoogleGeoLocation geoLocation;
            geoLocation = new GoogleGeoLocation(param.getLocation());
            filter.locations(geoLocation.getBounds());

        }
        return filter;
    }

    public static void streamPrint(Parameters param)  //stream-режим
            throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey("3b3vKQPtk7PoHEOekUedoIQPC")
                .setOAuthConsumerSecret("ADrGDZORevHvt3iF9Ot3xwfMeufol2lsG58XmAcqyCSsGkQZkR")
                .setOAuthAccessToken("2783476952-M6Pe8LR4gLYeKKDzdwjVKLkcFwMP38qDE1vgvP2")
                .setOAuthAccessTokenSecret("mfAU8iq63vU3omwqje8SXRQr0QCfonoK4eSjrpX61gKe8");
        TwitterStream twitterStreamm;
        twitterStreamm = new TwitterStreamFactory(cb.build()).getInstance();
        StatusAdapter listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                try {
                    Thread.sleep(PAUSE);
                } catch (InterruptedException e) {
                    System.out.print(e.getMessage());
                }

                System.out.println(new StatusParser(param).printStatus(status));
            }
        };
        twitterStreamm.addListener(listener);
        if (param.getQuery() == "" && param.getLocation() == "") {
            twitterStreamm.sample();
        } else {
            FilterQuery filter = setFilter(param);
            twitterStreamm.filter(filter);
        }
    }

}
