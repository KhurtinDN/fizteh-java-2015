package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.*;
import twitter4j.TwitterStream;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by mikhail on 22.01.16.
 */
public class StreamProcessor implements Processor {
    private static final long SLEEP_TIME = 1000;
    private final OutputManager outputManager;
    private final ArgumentInfo argumentInfo;
    private final twitter4j.TwitterStream twitterStream;
    private final Function<String, GeocodingResult[]> geocodingResultProducer;
    private final Supplier<GeoLocation> nearby;

    public StreamProcessor(OutputManager outputManager, ArgumentInfo argumentInfo, TwitterStream twitterStream,
                           Function<String, GeocodingResult[]> geocodingResultProducer, Supplier<GeoLocation> nearby) {
        this.outputManager = outputManager;
        this.argumentInfo = argumentInfo;
        this.twitterStream = twitterStream;
        this.geocodingResultProducer = geocodingResultProducer;
        this.nearby = nearby;
    }

    public static boolean fits(Status status, List<BoxLocation> boxLocations) {
        if (boxLocations.size() == 0 || status.isRetweet()) {
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

    @Override
    public void process() {
        List<BoxLocation> boxLocations = LocationGetter.getLocations(new BoxLocationLocationFactoryFactory().get(),
                argumentInfo.getPlace(),
                geocodingResultProducer,
                argumentInfo.isNearby() ? nearby : null);

        StatusListener listener = new StatusListener() {
            //filter twitter4j'a для twitterstream ставит OR между всеми условиями, которые в него пихают, нам же
            //необходимо AND. Самое простое решение - место вручную отфильтровать, что я и сделаю.
            @Override
            public void onStatus(Status status) {
                if (!fits(status, boxLocations)) {
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
                System.err.println("Something went wrong with twitter4j.twitterstream: " + e.getMessage());
            }
        };

        twitterStream.addListener(listener);

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(new String[]{argumentInfo.getQuery()});

        twitterStream.filter(filterQuery);
    }
}
