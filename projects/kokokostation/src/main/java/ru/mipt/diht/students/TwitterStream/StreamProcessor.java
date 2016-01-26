package ru.mipt.diht.students.TwitterStream;

import twitter4j.*;

import java.util.Vector;

/**
 * Created by mikhail on 22.01.16.
 */
class StreamProcessor {
    private static final long SLEEP_TIME = 1000;
    private Vector<BoxLocation> boxLocations;

    StreamProcessor(OutputManager outputManager, ArgumentInfo argumentInfo) {
        boxLocations = LocationGetter.getLocations(new BoxLocationFactory(), argumentInfo.getPlace(), argumentInfo.isNearby());

        twitter4j.TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener() {
            //filter twitter4j'a для TwitterStream ставит OR между всеми условиями, которые в него пихают, нам же
            //необходимо AND. Самое простое решение - место вручную отфильтровать, что я и сделаю.
            @Override
            public void onStatus(Status status) {
                if (!fits(status)) {
                    return;
                }

                try {
                    if (outputManager.writeTweet(status)) {
                        Thread.sleep(SLEEP_TIME);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Thread can't sleep: " + e.getMessage());
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {
                System.out.println("Got track limitation notice:" + i);
            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception e) {
                System.err.println("Something went wrong with twitter4j.TwitterStream: " + e.getMessage());
                System.exit(1);
            }
        };

        twitterStream.addListener(listener);

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(new String[]{argumentInfo.getQuery()});

        twitterStream.filter(filterQuery);
    }

    private boolean fits(Status status) {
        if (boxLocations.size() == 0) {
            return true;
        }

        GeoLocation geoLocation = status.getGeoLocation();
        if (geoLocation == null) {
            return false;
        }

        for (BoxLocation boxLocation : boxLocations) {
            if (boxLocation.contains(geoLocation)) {
                return true;
            }
        }

        return false;
    }
}
