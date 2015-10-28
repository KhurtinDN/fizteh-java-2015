package ru.mipt.diht.students.ale3otik.twitter;

import ru.mipt.diht.students.ale3otik.twitter.exceptions.ExitException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 10.10.15.
 */
public class TwitterSingleQuery {
    public static void printSingleTwitterQuery(ArgumentsStorage arguments, String informationMessage)
            throws ExitException {
        informationMessage += ":";

        int tries = 0;

        List<Status> allTweets = new ArrayList<Status>();
        QueryResult result = null;

        Twitter twitter = TwitterFactory.getSingleton();
        Query query = new Query(arguments.getQuery());

        GeoLocationInfo locationInfo = arguments.getGeoLocationInfo();
        if (locationInfo != null) {
            query.geoCode(locationInfo.getLocation(),
                    locationInfo.getRadius(), GeoLocationResolver.RADIUS_UNIT);
        }

        while (tries < TwitterUtil.TRIES_LIMIT) {

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
                    System.err.println(e.getMessage() + " Попыток: " + tries);
                } else {
                    System.err.print(e.getMessage());
                    throw new ExitException();
                }
            }
        }

        if (tries == TwitterUtil.TRIES_LIMIT) {
            System.err.println("ОШИБКА: Не удалось установить соединение");
            throw new ExitException();
        }

        System.out.println(informationMessage);
        TwitterUtil.printSplitLine();


        Collections.reverse(allTweets);


        if (allTweets.isEmpty()) {
            System.out.println("Ничего не найдено :(");
        }

        for (Status status : allTweets) {
            TwitterUtil.printFormattedTweet(status, arguments);
        }

    }
}
