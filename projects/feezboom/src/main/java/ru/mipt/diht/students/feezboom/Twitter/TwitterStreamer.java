package ru.mipt.diht.students.feezboom.Twitter;

import ru.mipt.diht.students.feezboom.StringUtils.StringUtils;
import twitter4j.*;
import java.util.*;

class TwitterStreamer {
    private Twitter twitter;
    private int limit;
    private final JCommanderList params;
    private final int sleepTime = 1000;

    TwitterStreamer(String[] args) {
        this.params = new JCommanderList(args);
    }

    public final void startTwitting() throws Exception {
        if (params.isHelp()) {
            params.printHelp();
            return;
        }

        twitter = TwitterFactory.getSingleton();
        Query query = new Query(params.getQuery());

        //Setting queries limit
        limit = params.getLimit();

        String place = params.getPlace();
        switch (place.toLowerCase()) {
            case "nearby" :
                place = FindGeolocation.getPlaceStringAlternative();
                query = FindGeolocation.setSearchPlace(twitter, query, place);
                break;
            case "anywhere" :
                break;
            default :
                query = FindGeolocation.setSearchPlace(twitter, query, place);
        }

        if (params.isStream()) {
                runStreamer(query);
        } else {
            runSearch(query);
        }
    }

    private void runSearch(Query query) throws Exception {
        QueryResult queryResult;
        boolean again = true;
        int toPrint = limit;
        final int defaultLimitPerPage = 100;

        //Этот while позволяет выводить сколь угодно запрошенное в limit число твитов
        while (again) {
            query.setCount(Math.min(defaultLimitPerPage, toPrint));
            queryResult = twitter.search(query);

            List<Status> statusList = queryResult.getTweets();
            //Check if no tweets:
            if (statusList.isEmpty()) {
                System.out.println("Нет твитов по вашему запросу.");
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
                printTweet(tweet, params.isNoRetweets(), params.isStream());
            }

            toPrint -= statusList.size();
            again = queryResult.hasNext() && toPrint > 0;
            query = queryResult.nextQuery();
        }
    }

    private void runStreamer(Query query) throws Exception {

        System.out.println("Стример был успешно запущен...");

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
                        System.out.println("Возникли проблемы мониторинга : "
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

    @SuppressWarnings("checkstyle:magicnumber")
    private String getTimeFormattedTimeString(Date createdAt) {
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
            if (minutes >= 10 && minutes <= 20) {
                ending = "";
            } else {
                long remainder = minutes % 10;
                if (remainder == 1) {
                    ending = "у";
                } else if (remainder == 2
                        || remainder == 3 || remainder == 4) {
                    ending = "ы";
                } else {
                    ending = "";
                }
            }
            return (delta / min) + " минут" + ending + " назад";
        } else if (delta < day) {
            long hours = delta / hour;
            if (hours > 10 && hours < 20) {
                ending = "ов";
            } else {
                long ostatok = hours % 10;
                if (ostatok == 1) {
                    ending = "";
                } else if (ostatok == 2
                        || ostatok == 3 || ostatok == 4) {
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
            if (days >= 10 && days <= 20) {
                ending = "ней";
            } else {
                long ostatok = days % 10;
                if (ostatok == 1) {
                    ending = "ень";
                } else if (ostatok == 2
                        || ostatok == 3 || ostatok == 4) {
                    ending = "ня";
                } else {
                    ending = "ней";
                }
            }
            return (delta / day) + " д" + ending + " назад";
        }
    }
}
