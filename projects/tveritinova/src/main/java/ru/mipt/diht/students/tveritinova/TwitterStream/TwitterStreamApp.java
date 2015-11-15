package ru.mipt.diht.students.tveritinova.TwitterStream;

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class TwitterStreamApp {
    private static final long ONE_SECOND = 1000;
    private MyJCommander jc = new MyJCommander();
    private Location location;

    TwitterStreamApp(String[] args) {
        new JCommander(jc, args);

        if (jc.getLocation() != null) {
            location = new Location(jc.getLocation());
        }
    }

    public void run() {
        if (jc.getIsStream()) {
            startStreamMode();
        } else {
            startNotStreamMode();
        }
    }

    private void printStatusNotStream(Status status) {

        if (status.isRetweet() && jc.getIsHideRetweets()) {
            return;
        }

        System.out.print("\033[34m@"
                + status.getUser().getName()
                + "\033[0m : ");

        if (status.isRetweet()) {
            String retweetUserName = status.getText()
                    .split("RT ")[1]
                    .split(":\\s+")[0];
            System.out.println("ретвитнул "
                    + "\033[34m"
                    + retweetUserName
                    + "\033[0m"
                    + status.getText().split(retweetUserName)[1]);
        } else {
            System.out.println(status.getText()
                    + " (" + status.getRetweetCount() + " ретвитов)");
        }

        System.out.println("\n-----------------------------\n");
    }

    private void printStatusStream(Status status, Calendar cal, Date now) {

        String timeFormat = TimeFormat.getTimeFormat(status,
                cal, now);

        System.out.print(timeFormat
                + " \033[34m@"
                + status.getUser().getName()
                + "\033[0m : ");
        if (status.isRetweet()) {
            String retweetUserName = status.getText()
                    .split("RT ")[1].split(":\\s+")[0];
            System.out.println("ретвитнул "
                    + "\033[34m"
                    + retweetUserName
                    + "\033[0m"
                    + status.getText()
                    .split(retweetUserName)[1]);
        } else {
            System.out.println(status.getText()
                    + " ("
                    + status.getRetweetCount()
                    + " ретвитов)");
        }

        System.out.println("\n-----------------------------\n");
    }

    private void startStreamMode() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {

                printStatusNotStream(status);

                try {
                    Thread.sleep(ONE_SECOND);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        twitterStream.addListener(listener);

        System.out.println(jc.getQuery());

        FilterQuery fq = new FilterQuery();
        fq.track(jc.getQuery().split(","));
        if (location != null) {
            location.setFilterQueryLocation(fq);
        }

        twitterStream.filter(fq);
    }

    private void startNotStreamMode() {
        Twitter twitter = new TwitterFactory().getInstance();
        Query q = new Query(jc.getQuery());
        q.count(jc.getLimit());

        if (location != null) {
            location.setQueryLocation(q);
        }

        try {
            QueryResult resultList = twitter.search(q);

            Status[] resultArray = resultList.getTweets()
                    .toArray(new Status[resultList.getTweets().size()]);

            Calendar cal = new GregorianCalendar();
            Date now = cal.getTime();

            for (Status currentStatus: resultArray) {
                printStatusStream(currentStatus, cal, now);
            }
        } catch (TwitterException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
