package ru.mipt.diht.students.feezboom.Twitter;

import ru.mipt.diht.students.feezboom.StringUtils.StringUtils;
import twitter4j.*;
import java.util.*;

public class TwitterStreamer {
    private Twitter twitter;
    private JCommanderList params;
    private final int sleepTime = 1000;

    public TwitterStreamer(String[] args) {
        this.params = new JCommanderList(args);
    }

    public final void startTwitting() throws Exception {
        if (params.isHelp()) {
            params.getHelp();
            System.exit(0);
        }

        twitter = TwitterFactory.getSingleton();
        Query query = new Query(params.getQuery());

        final int tweetsLimit = 100;
        int limit = params.getLimit();
        if (limit >= 0 && limit <= tweetsLimit) {
            query.setCount(limit);
        } else {
            System.err.println("Limit " + limit + " was not set.");
        }

        String city = params.getPlace();
        switch (city) {
            case "nearby" : case "Nearby" :
                city = FindGeolocation.getCityStringAlternative();
                query = FindGeolocation.setSearchPlace(twitter, query, city);
                break;
            case "anywhere" : case "Anywhere" :
                break;
            default :
                query = FindGeolocation.setSearchPlace(twitter, query, city);
        }

        if (params.isStream()) {
                runStreamer(query);
        } else {
            runSearch(query);
        }
        System.exit(0);
    }

    private void runSearch(Query query) throws Exception {
        QueryResult queryResult = twitter.search(query);
        List<Status> statusList = queryResult.getTweets();
        //Check if no tweets:
        if (statusList.isEmpty()) {
            System.err.printf("There is no tweets on your query here.");
            return;
        }
        System.out.println(
                "Твиты по запросу "
                        + params.getQuery()
                        + " для "
                        + params.getPlace()
                        + ":"
        );
        for (Status tweet : statusList) {
            printTweet(tweet, params.noRetweets(), params.isStream());
        }
    }

    private void runStreamer(Query query) throws Exception {

        System.out.println("Streamer successfully run...");

        TwitterStream streamer = new TwitterStreamFactory().getInstance();
        StatusAdapter listener = new StatusAdapter() {
                    @Override
                    public void onStatus(Status status) {
                        printTweet(status, true, true);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    @Override
                    public void onException(Exception ex) {
                        System.out.println("Problems listening : "
                                + ex.getMessage());
                    }
                };
        streamer.addListener(listener);

        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(query.getQuery());
        streamer.filter(filterQuery);


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            Thread.sleep(sleepTime);
        }
    }

    private void printTweetTime(Status tweet) {
        String timeToPrint = getTimeFormattedTimeString(tweet.getCreatedAt());
        System.out.print("[" + timeToPrint + "]");
    }

    private void printTweet(Status tweet,
                            boolean hideRetweets,
                            boolean isStream) {
        System.out.println("-----------------------------------------");
        if (!isStream) {
            printTweetTime(tweet);
        }
        System.out.print("@"
                + StringUtils.getPainted(tweet.getUser().getScreenName(),
                "cyan")
                + " : "
                + tweet.getText());

        int retweetCount = tweet.getRetweetCount();
        if (retweetCount != 0) {
            System.out.println(" ("
                    + tweet.getRetweetCount() + " "
                    + getTweetWord(tweet.getRetweetCount()) + ")");
            if (!hideRetweets) {
                printRetweets(tweet, isStream);
            }
        } else {
            System.out.println();
        }
    }

    private void printRetweets(Status tweet, boolean isStream) {
        Status retweet = tweet.getRetweetedStatus();
        if (retweet == null) {
            return;
        }
        if (!isStream) {
            printTweetTime(retweet);
        }
        System.out.print(
                "@"
                + StringUtils.getPainted(retweet.getUser().getScreenName(),
                        "cyan")
                + " ");
        System.out.print("ретвитнул @" + tweet.getUser().getScreenName()
                + ": ");
        System.out.println(retweet.getText());
    }

    private String getTweetWord(int tweetsNumber) {
        final byte
                ten = 10,
                exceptStart = 11,
                exceptFinish = 19,
                ovMin = 5,
                ovMax = 9,
                ovEx = 0,
                zeroNum = 1;
        int remainder = tweetsNumber % ten;
        if (tweetsNumber >= exceptStart && tweetsNumber <= exceptFinish
                || remainder >= ovMin && remainder <= ovMax
                || remainder == ovEx) {
            return "ретвитов";
        } else if (remainder == zeroNum) {
            return "ретвит";
        } else {
            return "ретвита";
        }
    }

    private String getTimeFormattedTimeString(Date createdAt) {
        //Remainders
        final byte one = 1;
        final byte two = 2;
        final byte three = 3;
        final byte four = 4;
        final byte ten = 10;
        final byte twenty = 20;
        //Times
        final long sec = 1000;
        final long min = sec * 60;
        final long hour = min * 60;
        final long day = hour * 24;
        //Getting today's date and current time.
        Date date = Calendar.getInstance().getTime();
        String ending;
        long delta = date.getTime() - createdAt.getTime();
        assert (delta >= 0);
        if (delta < 2 * min) {
            return "Только что";
        } else if (delta < hour) {
            long minutes = delta / min;
            if (minutes >= ten && minutes <= twenty) {
                ending = "";
            } else {
                long ostatok = minutes % ten;
                if (ostatok == one) {
                    ending = "у";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "ы";
                } else {
                    ending = "";
                }
            }
            return (delta / min) + " минут" + ending + " назад";
        } else if (delta < day) {
            long hours = delta / hour;
            if (hours > ten && hours < twenty) {
                ending = "ов";
            } else {
                long ostatok = hours % ten;
                if (ostatok == one) {
                    ending = "";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "а";
                } else {
                    ending = "ов";
                }
            }
            return (delta / hour) + " час" + ending + " назад";
        } else if (delta < 2 * day) {
            return "Вчера";
        } else {
            long days = delta / day;
            if (days >= ten && days <= twenty) {
                ending = "ней";
            } else {
                long ostatok = days % ten;
                if (ostatok == 1) {
                    ending = "ень";
                } else if (ostatok == two
                        || ostatok == three || ostatok == four) {
                    ending = "ня";
                } else {
                    ending = "ней";
                }
            }
            return (delta / day) + " д" + ending + " назад";
        }
    }
}
