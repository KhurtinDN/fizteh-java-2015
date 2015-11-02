/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.StreamStartFailedException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

public class TwitterStreamLauncher {

    private static final long SLEEP_TIME = 1000;

    private static StatusAdapter createStatusAdapter(Arguments arguments) {
        return new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                if (arguments.isHideRetweets() && status.isRetweet()) {
                    return;
                }
                GeoLocationInfo geoParams = arguments.getGeoLocationInfo();
                if (geoParams != null) {
                    GeoLocation tweetLocation;
                    if (status.getGeoLocation() == null) {
                        return;
                    }
                    tweetLocation = status.getGeoLocation();

                    if (GeoLocationResolver.getSphereDist(
                            geoParams.getLocation(), tweetLocation) > geoParams.getRadius()) {
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

    public static void streamStart(Arguments arguments, String informationMessage)
            throws StreamStartFailedException {

        informationMessage += " в потоковом режиме:";
        StatusListener listener = createStatusAdapter(arguments);

        TwitterStream twStream;
        try {
            twStream = TwitterStreamFactory.getSingleton();

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
            throw new StreamStartFailedException(e.getMessage());
        }
    }
}
