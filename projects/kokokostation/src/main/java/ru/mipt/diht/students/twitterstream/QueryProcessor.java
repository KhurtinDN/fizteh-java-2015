package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.*;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by mikhail on 16.12.15.
 */
public class QueryProcessor implements Processor {
    private static final int MAX_ATTEMPTS = 5,
            ATTEMPT_WAIT = 1000;

    private final OutputManager outputManager;
    private final ArgumentInfo argumentInfo;
    private final Twitter twitter;
    private final Function<String, GeocodingResult[]> geocodingResultProducer;
    private final Supplier<GeoLocation> nearby;
    private int tweetsCount = 0;

    public QueryProcessor(OutputManager outputManager, ArgumentInfo argumentInfo, Twitter twitter,
                          Function<String, GeocodingResult[]> geocodingResultProducer, Supplier<GeoLocation> nearby) {
        this.outputManager = outputManager;
        this.argumentInfo = argumentInfo;
        this.twitter = twitter;
        this.geocodingResultProducer = geocodingResultProducer;
        this.nearby = nearby;
    }

    @Override
    public void process() {
        Query[] queries = composeQueries();

        for (Query q : queries) {
            try {
                QueryResult queryResult = twitterSearch(q);

                while (writeTweets(queryResult.getTweets())) {
                    if (queryResult.hasNext()) {
                        queryResult = twitterSearch(queryResult.nextQuery());
                    } else {
                        break;
                    }
                }
            } catch (TwitterException e) {
                System.err.println("Twitter can't process a query: " + e.getMessage());
            }
        }

        if (tweetsCount == 0) {
            outputManager.write("No tweets found.");
        }
    }

    private QueryResult twitterSearch(Query q) throws TwitterException {
        for (int i = 0;; ++i) {
            try {
                return twitter.search(q);
            } catch (TwitterException e) {
                if (i == MAX_ATTEMPTS - 1) {
                    throw e;
                }

                System.err.println("Some problems with Twitter. Maybe next attempt will be more successful: "
                        + e.getMessage());
                try {
                    Thread.sleep(ATTEMPT_WAIT);
                } catch (InterruptedException e1) {
                    System.err.println("Thread can't sleep: " + e1.getMessage());
                }
            }
        }
    }

    private Query[] composeQueries() {
        if (argumentInfo.getPlace().isEmpty() && !argumentInfo.isNearby()) {
            Query[] result = new Query[1];
            result[0] = new Query(argumentInfo.getQuery());
            return result;
        }

        List<CircleLocation> circleLocations = LocationGetter.getLocations(
                new CircleLocationLocationFactoryFactory().get(),
                argumentInfo.getPlace(), geocodingResultProducer,
                argumentInfo.isNearby() ? nearby : null);

        Query[] result = new Query[circleLocations.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Query(argumentInfo.getQuery()).geoCode(circleLocations.get(i).getGeoLocation(),
                    circleLocations.get(i).getRadius(), String.valueOf(Query.KILOMETERS));
        }

        return result;
    }

    private boolean writeTweets(List<Status> tweets) {
        for (Status tweet : tweets) {
            if (argumentInfo.getLimit() == tweetsCount && argumentInfo.getLimit() != ArgumentInfo.NO_LIMIT) {
                return false;
            }

            if (outputManager.writeTweet(tweet)) {
                ++tweetsCount;
            }
        }

        return true;
    }
}
