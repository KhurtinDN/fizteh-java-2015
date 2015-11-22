package ru.mipt.diht.students.tveritinova.TwitterStream;

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TwitterStreamApp {
    private static final long ONE_SECOND = 1000;
    private MyJCommander jc = new MyJCommander();
    private Location location;

    public TwitterStreamApp(String[] args) {
        new JCommander(jc, args);

        if (jc.getLocation() != null) {
            location = new Location(jc.getLocation());
        }
    }

    public final void run() {
        if (jc.getIsStream()) {
            startStreamMode();
        } else {
            startNotStreamMode();
        }
    }

    public final String printStatusStream(Status status) {

        if (status.isRetweet() && jc.getIsHideRetweets()) {
            return "";
        }

        String printString = "-----------------------------\n"
                + "\033[34m@"
                + status.getUser().getName()
                + "\033[0m: ";

        if (status.isRetweet()) {
            String retweetUserName = status.getText()
                    .split("RT ")[1]
                    .split(":\\s+")[0];
            printString += "ретвитнул "
                    + "\033[34m"
                    + retweetUserName
                    + "\033[0m"
                    + status.getText().split(retweetUserName)[1] + "\n";
        } else {
            printString += status.getText()
                    + " (" + status.getRetweetCount() + " ретвитов)\n";
        }

        return printString;
    }

    public final String printStatusNotStream(Status status, Calendar cal
            , Date now) {

        if (status.isRetweet() && jc.getIsHideRetweets()) {
            return "";
        }

        String timeFormat = TimeFormat.getTimeFormat(status.getCreatedAt(),
                cal, now);

        String printString = "-----------------------------\n"
                + timeFormat
                + " \033[34m@"
                + status.getUser().getName()
                + "\033[0m: ";
        if (status.isRetweet()) {
            String retweetUserName = status.getText()
                    .split("RT ")[1].split(":\\s+")[0];
            printString += "ретвитнул "
                    + "\033[34m"
                    + retweetUserName
                    + "\033[0m"
                    + status.getText()
                    .split(retweetUserName)[1] + "\n";
        } else {
             printString += status.getText()
                    + " ("
                    + status.getRetweetCount()
                    + " ретвитов)\n";
        }

        return printString;
    }

    public final void startStreamMode() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {

                System.out.print(printStatusStream(status));

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

    public final void startNotStreamMode() {
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
                System.out.print(
                        printStatusNotStream(currentStatus, cal, now));
            }
        } catch (TwitterException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
