/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.function.Consumer;


public class TwitterStreamLauncher {

    private static final long BASE_SLEEP_TIME = 1000;
    private TwitterStream twStream;
    private Consumer<String> consumer;
    private Arguments arguments;
    private long sleepTime;

    public TwitterStreamLauncher(TwitterStream twitterStreamClient,
                                 Consumer<String> newConsumer,
                                 Arguments receivedArguments,
                                 long timeSleepMillis) {
        this.twStream = twitterStreamClient;
        this.consumer = newConsumer;
        this.arguments = receivedArguments;
        if (timeSleepMillis == 0) {
            this.sleepTime = BASE_SLEEP_TIME;
        } else {
            this.sleepTime = timeSleepMillis;
        }
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
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    ConsoleUtil.printErrorMessage(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    public final void streamStart(StringBuilder informationMessage) {
        informationMessage.append(" в потоковом режиме:");
        StatusListener listener = createStatusAdapter();

        FilterQuery query = new FilterQuery(arguments.getQuery());

        twStream.addListener(listener);

        informationMessage.append("\n").append(TwitterUtils.getSplitLine());
        print(informationMessage.toString());

        if (arguments.getQuery().isEmpty()) {
            twStream.sample();
        } else {
            twStream.filter(query);
        }
    }
}
