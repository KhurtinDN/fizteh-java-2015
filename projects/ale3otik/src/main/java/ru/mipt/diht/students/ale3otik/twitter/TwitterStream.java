/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.ExitException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

public class TwitterStream {

    private static final long SLEEP_TIME = 1000;

    private static StatusAdapter getStatusAdapter(ArgumentsStorage arguments) {
        return new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                if (arguments.isHideRetweets() && status.isRetweet()) {
                    return;
                }
                GeoLocationInfo geoParams = arguments.getGeoLocationInfo();
                if (geoParams != null) {
                    GeoLocation tweetLocation;
                    if (status.getGeoLocation() != null) {
                        tweetLocation = status.getGeoLocation();
                    } else {
                        return;
                    }

                    double myLatitude = geoParams.getLocation().getLatitude();
                    double myLongitude = geoParams.getLocation().getLongitude();
                    double tweetLatitude = tweetLocation.getLatitude();
                    double tweetLongitude = tweetLocation.getLongitude();

                    if (GeoLocationResolver.getSphereDist(
                            myLatitude, myLongitude,
                            tweetLatitude, tweetLongitude) > geoParams.getRadius()) {
                        return;
                    }
                }

                ConsoleUtil.printIntoStdout(TwitterUtil.getFormattedTweetToPrint(status, arguments));

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    ConsoleUtil.printErrorMessage(e.getMessage());
                    Thread.currentThread().interrupt();
                }

            }
        };

    }

    public static void streamStart(ArgumentsStorage arguments, String informationMessage)
            throws ExitException {

        informationMessage += " в потоковом режиме:";
        StatusListener listener = getStatusAdapter(arguments);

        twitter4j.TwitterStream twStream;
        try {
            twStream = twitter4j.TwitterStreamFactory.getSingleton();

            twitter4j.TwitterFactory.getSingleton();

            FilterQuery query = new FilterQuery(arguments.getQuery());
            twStream.addListener(listener);

            ConsoleUtil.printIntoStdout(informationMessage);
            TwitterUtil.getSplitLine();

            if (arguments.getQuery().isEmpty()) {
                twStream.sample();
            } else {
                twStream.filter(query);
            }

        } catch (Exception e) {
            ConsoleUtil.printErrorMessage(e.getMessage());
            throw new ExitException();
        }
    }
}
