package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by alex on 10.10.15.
 */
public class TwitterSingleQuery {
    public static final int TRIES_LIMIT = 3;

    private Twitter twitter;
    private Consumer<String> errorConsumer;
    private void errPrint(String str) {
        errorConsumer.accept(str);
    }

    public TwitterSingleQuery(Twitter twitterClient) {
        this.twitter = twitterClient;
        this.errorConsumer = (x) -> ConsoleUtil.printErrorMessage(x);
    }

    public TwitterSingleQuery(Twitter twitterClient, Consumer<String> errConsumer) {
        this.twitter = twitterClient;
        this.errorConsumer = errConsumer;
    }

    public final String getSingleQueryResult(TwitterClientArguments arguments, StringBuilder informationMessage)
            throws ConnectionFailedException, TwitterException {

        List<Status> allTweets = getSingleQueryStatuses(arguments);

        StringBuilder answerString = informationMessage;
        answerString.append(":").append("\n").append(TwitterUtils.getSplitLine());

        if (allTweets.isEmpty()) {
            answerString.append("\n").append("Ничего не найдено :(");
        }

        for (Status status : allTweets) {
            answerString.append("\n").append(TwitterUtils.getFormattedTweetToPrint(status, arguments));
        }
        return answerString.toString();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public final List<Status> getSingleQueryStatuses(TwitterClientArguments arguments)
            throws ConnectionFailedException, TwitterException {

        int tries = 0;

        List<Status> allTweets = new ArrayList<>();

        Query query = new Query(arguments.getQuery());
        GeoLocationInfo locationInfo = arguments.getGeoLocationInfo();
        if (locationInfo != null) {
            query.geoCode(locationInfo.getLocation(),
                    locationInfo.getRadius(), GeoLocationResolver.RADIUS_UNIT);
        }

        QueryResult result;
        while (tries < TRIES_LIMIT) {
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
                    errPrint(e.getMessage() + " Попыток: " + tries);
                } else {
                    throw e;
                }
            }
        }

        if (tries == TRIES_LIMIT) {
            throw new ConnectionFailedException("Не удалось восстановить соединение");
        }

        Collections.reverse(allTweets);

        return allTweets;
    }
}
