package ru.mipt.diht.students.andreyzharkov.twitterStream;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import twitter4j.*;

/**
 * Created by Андрей on 10.10.2015.
 */
public class TwitterOutputEditor {
    //colors
    private static final String SET_BLUE_TEXT_COLOR = (char) 27 + "[" + (char) 34 + "m";
    private static final String SET_STANDART_TEXT_COLOR = (char) 27 + "[" + (char) 37 + "m";
    //private static final String SET_BLUE_TEXT_COLOR = "";
    //private static final String SET_STANDART_TEXT_COLOR = "";
    private static final int SLEEP_TIME = 1000;
    private static final int QUEUE_MAX_SIZE = 1000;
    private static final char EXIT_KEY = (char) 27;
    private static final String SEARCH_BY_LOCATION_FAILED = "Search by location failed! Show results from anywhere.";

    enum Time { MINUTE, HOUR, DAY }

    private Twitter twitter;
    private ArgumentsList programArguments;
    private BlockingQueue<Status> streamQueue;

    TwitterOutputEditor(ArgumentsList prArguments) {
        twitter = new TwitterFactory().getInstance();
        this.programArguments = prArguments;
    }


    public final String convertTime(Date date) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime tweetDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration timeAfterTweet = Duration.between(tweetDateTime, currentDateTime);
        if (timeAfterTweet.toMinutes() < 2) {
            return "только что";
        }
        if (timeAfterTweet.toHours() < 1) {
            return (int) (timeAfterTweet.toMinutes()) + " "
                    + TimeDeclension.timeInRightForm(Time.MINUTE, (int) (timeAfterTweet.toMinutes())) + " назад";
        }
        if (currentDateTime.toLocalDate().equals(tweetDateTime.toLocalDate())) {
            return (int) (timeAfterTweet.toHours()) + " "
                    + TimeDeclension.timeInRightForm(Time.HOUR, (int) (timeAfterTweet.toHours())) + " назад";
        }
        if (currentDateTime.toLocalDate().minusDays(1).equals(tweetDateTime.toLocalDate())) {
            return "вчера";
        }
        return (int) (timeAfterTweet.toDays()) + " "
                + TimeDeclension.timeInRightForm(Time.DAY, (int) (timeAfterTweet.toDays())) + " назад";
    }

    final String convertNick(User user) {
        return SET_BLUE_TEXT_COLOR + "@" + user.getName() + SET_STANDART_TEXT_COLOR;
    }

    final String convertRetweetsCount(int count) {
        if (count == 0) {
            return "";
        }
        if (count == 1) {
            return "(1 ретвит)";
        }
        return "(" + count + " ретвитов)";
    }

    final void printOneTweet(Status tweet, boolean withDate) {
        StringBuilder result = new StringBuilder();
        if (withDate) {
            result.append("[");
            result.append(convertTime(tweet.getCreatedAt()));
            result.append("] ");
        }
        result.append(convertNick(tweet.getUser()));
        result.append(" ");
        if (tweet.isRetweet()) {
            result.append("ретвитнул ");
            result.append(convertNick(tweet.getRetweetedStatus().getUser()));
        }

        result.append(tweet.getText());
        result.append(" ");
        result.append(convertRetweetsCount(tweet.getRetweetCount()));
        System.out.println(result);
    }

    public final LocationSearcher findLocation(String region) throws TwitterException {
        if (region.equals("anywhere")) {
            return null;
        }
        GeoQuery geoQuery = new GeoQuery("1.1.1.1"); //ip need for init
        ResponseList<Place> searchPlaces;
        LocationSearcher location;
        try {
            geoQuery.setQuery(region);
            searchPlaces = twitter.searchPlaces(geoQuery);
            location = new LocationSearcher(searchPlaces);
            return location;
        } catch (Exception ex) {
            try {
                geoQuery.setQuery(Translator.translate("ru-en", region));
                searchPlaces = twitter.searchPlaces(geoQuery);
                location = new LocationSearcher(searchPlaces);
                return location;
            } catch (Exception e) {
                System.out.println(SEARCH_BY_LOCATION_FAILED);
                return null;
            }
        }
    }

    final void simpleMode() {
        while (true) {
            try {
                Query query = new Query(programArguments.getQueryString());
                LocationSearcher location = findLocation(programArguments.getPlace());
                if (location != null) {
                    //System.out.println("Search by location success!");
                    query.setGeoCode(location.getCenter(), location.getRadius(), Query.Unit.km);
                }

                QueryResult result = twitter.search(query);
                int tweetsCounter = 0;

                System.out.print("Твиты по запросу \""
                        + programArguments.getQueryString() + "\" для "
                        + programArguments.getPlace() + ":");
                if (result.getTweets().size() == 0) {
                    System.out.println("ничего не найдено!");
                    return;
                } else {
                    System.out.println("");
                }
                do {
                    for (Status status : result.getTweets()) {
                        if (!(status.isRetweet() && programArguments.isRetweetsHidden())) {
                            printOneTweet(status, true);
                            if (++tweetsCounter == programArguments.getTweetLimit()) {
                                return;
                            }
                        }
                    }
                    if (result.hasNext()) {
                        result = twitter.search(result.nextQuery());
                    } else {
                        break;
                    }
                } while (tweetsCounter < programArguments.getTweetLimit());
                if (tweetsCounter == 0) {
                    System.out.println("ничего не найдено!");
                }
                return;
            } catch (TwitterException te) {
                //te.printStackTrace();
                System.err.println("Failed to run twitter. I'm trying once more.. ");
            }
        }
    }

    private StatusListener tweetListener = new StatusAdapter() {
        public void onStatus(Status tweet) {
            if ((!programArguments.isRetweetsHidden() || !tweet.isRetweet())) {
                streamQueue.add(tweet);
            }
        }
    };

    final void streamMode() {
        while (true) {
            try {
                streamQueue = new ArrayBlockingQueue<Status>(QUEUE_MAX_SIZE);
                twitter4j.TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
                twitterStream.addListener(tweetListener);
                FilterQuery filterQuery = new FilterQuery();
                filterQuery.track(new String[]{programArguments.getQueryString()});
                LocationSearcher location = findLocation(programArguments.getPlace());
                if (location != null) {
                    filterQuery.locations(location.getBoundingBox());
                }
                twitterStream.filter(filterQuery);
                System.out.println("Stream mode on! Press 'q'&&Enter to exit!");

                while (true) {
                    while (!streamQueue.isEmpty()) {
                        Status tweet = streamQueue.poll();
                        printOneTweet(tweet, false);
                    }
                    try {
                        boolean needExit = false;
                        while (System.in.available() > 0) {
                            int c = System.in.read();
                            if (c == 'q' || c == EXIT_KEY || c == -1) {
                                twitterStream.shutdown();
                                needExit = true;
                                break;
                            }
                        }
                        if (needExit) {
                            break;
                        }
                    } catch (IOException e) {
                        System.err.println("cannot read from stdin: " + e.getMessage());
                    }
                    Thread.sleep(SLEEP_TIME);
                }
                return;
            } catch (TwitterException te) {
                te.printStackTrace();
                System.err.println("Failed to run twitter. I'm trying once more.. ");
            } catch (InterruptedException e) {
                System.err.println("Failed to sleep!");
            }
        }
    }
}
