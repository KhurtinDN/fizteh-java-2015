package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 10.10.15.
 */
public class TwitterSingleQuery {
    private Twitter twitter;

    public TwitterSingleQuery(Twitter twitterClient) {
        this.twitter = twitterClient;
    }

    public final String getSingleQueryResult(Arguments arguments, String informationMessage)
            throws ConnectionFailedException, TwitterException {

        ArrayList<Status> allTweets = getSingleQueryStatuses(arguments);

        StringBuilder answerString = new StringBuilder();
        answerString.append(informationMessage).append(":");
        answerString.append("\n").append(TwitterUtils.getSplitLine());

        if (allTweets.isEmpty()) {
            answerString.append("\n").append("Ничего не найдено :(");
        }

        for (Status status : allTweets) {
            answerString.append("\n").append(TwitterUtils.getFormattedTweetToPrint(status, arguments));
        }
        return answerString.toString();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public final ArrayList<Status> getSingleQueryStatuses(Arguments arguments)
            throws ConnectionFailedException, TwitterException {

        int tries = 0;

        ArrayList<Status> allTweets = new ArrayList<Status>();

        Query query = new Query(arguments.getQuery());
        GeoLocationInfo locationInfo = arguments.getGeoLocationInfo();
        if (locationInfo != null) {
            query.geoCode(locationInfo.getLocation(),
                    locationInfo.getRadius(), GeoLocationResolver.RADIUS_UNIT);
        }

        QueryResult result;
        while (tries < TwitterUtils.TRIES_LIMIT) {
            try {
                while (allTweets.size() < arguments.getLimit()) {
                    result = twitter.search(query);

                    List<Status> newTweets = result.getTweets();
                    if (newTweets.isEmpty()) {
                        break;
                    }

                    for (Status status : newTweets) {
                        if (allTweets.size() >= arguments.getLimit()) {
                            break;
                        }

                        if (!arguments.isHideRetweets() || !status.isRetweet()) {
                            allTweets.add(status);
                        }
                    }

                    query = result.nextQuery();
                    if (query == null) {
                        break;
                    }
                }
                break;
            } catch (TwitterException e) {
                if (e.isCausedByNetworkIssue()) {
                    ++tries;
                    ConsoleUtil.printErrorMessage(e.getMessage() + " Попыток: " + tries);
                } else {
                    throw e;
                }
            }
        }

        if (tries == TwitterUtils.TRIES_LIMIT) {
            throw new ConnectionFailedException("Не удалось восстановить соединение");
        }

        Collections.reverse(allTweets);

        return allTweets;
    }
}
