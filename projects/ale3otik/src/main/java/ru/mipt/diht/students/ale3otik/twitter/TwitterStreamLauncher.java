/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.function.Consumer;


public class TwitterStreamLauncher {

    private static final long SLEEP_TIME = 1000;
    private TwitterStream twStream;
    private Consumer<String> consumer;
    private Arguments arguments;

    public TwitterStreamLauncher(TwitterStream twitterStreamClient,
                                 Consumer<String> newConsumer,
                                 Arguments receivedArguments) {
        this.twStream = twitterStreamClient;
        this.consumer = newConsumer;
        this.arguments = receivedArguments;
    }

    private void print(String str) {
        consumer.accept(str);
    }

    public final StatusAdapter createStatusAdapter() {
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

                print(TwitterUtils.getFormattedTweetToPrint(status, arguments));

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    ConsoleUtil.printErrorMessage(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    public final void streamStart(String informationMessage) {
        informationMessage += " в потоковом режиме:";
        StatusListener listener = createStatusAdapter();

        FilterQuery query = new FilterQuery(arguments.getQuery());
        if (arguments.getGeoLocationInfo() != null) {
        }
        twStream.addListener(listener);

        print(informationMessage + TwitterUtils.getSplitLine());

        if (arguments.getQuery().isEmpty()) {
            twStream.sample();
        } else {
            twStream.filter(query);
        }
    }
}
