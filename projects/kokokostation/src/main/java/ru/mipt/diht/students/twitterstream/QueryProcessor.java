package ru.mipt.diht.students.twitterstream;

import twitter4j.*;

import java.util.List;
import java.util.Vector;

/**
 * Created by mikhail on 16.12.15.
 */
public class QueryProcessor implements Processor {
    private static final int MAX_ATTEMPTS = 5,
            ATTEMPT_WAIT = 1000;
    private final ArgumentInfo argumentInfo;
    private final OutputManager outputManager;
    private final Twitter twitter = TwitterFactory.getSingleton();
    private int tweetsCount = 0;

    QueryProcessor(OutputManager outputManager, ArgumentInfo argumentInfo) {
        this.argumentInfo = argumentInfo;
        this.outputManager = outputManager;
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
        Query q = new Query(argumentInfo.getQuery());

        if (argumentInfo.getPlace().isEmpty() && !argumentInfo.isNearby()) {
            Query[] result = new Query[1];
            result[0] = q;
            return result;
        }

        Vector<CircleLocation> locationsFound = LocationGetter.getLocations(
                new CircleLocationLocationFactoryFactory().get(),
                argumentInfo.getPlace(), argumentInfo.isNearby());

        Query[] result = new Query[locationsFound.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = q.geoCode(locationsFound.get(i).getGeoLocation(),
                    locationsFound.get(i).getRadius(), String.valueOf(Query.KILOMETERS));
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
