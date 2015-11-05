package ru.mipt.diht.students.tveritinova.TwitterStream;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import twitter4j.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


class TwitterStreamApp {
    private static final long ONE_SECOND = 1000;
    private static final int RADIUS = 50;
    private boolean isStream = false;
    private boolean isHelp = false;
    private boolean isHideRetweets = false;
    private int limit = -1;
    private String query;
    private String location;

    TwitterStreamApp(String[] args) {
        takeArgs(args);
    }

    public void run() {
        if (isHelp) {
            System.out.println("Аргументы:\n");
            System.out.println("\t[--query|-q <параметр поиска твитов>]");
            System.out.println("\t[--place|-p <location>]  "
                    + "//искать по заданному региону (например, "
                    + "\"долгопрудный\", \"москва\", \"татарстан\")");
            System.out.println("\t[--stream|-s]      "
                    + "равномерно и непрерывно с задержкой в "
                    + "1 секунду печать твиты на экран");
            System.out.println("\t[--hideRetweets]         "
                    + "//фильтровать ретвиты");
            System.out.println("\t[--limit|-l <tweets>]    "
                    + "выводить столько твитов. Не применимо для --stream "
                    + "режима");
            System.out.println("\t[--help|-h]              "
                    + "//печатает справку");
        }
        if (isStream) {
            startStreamMode();
        } else {
            startNotStreamMode();
        }
    }

    private void takeArgs(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--query") || args[i].equals("-q")) {
                query = args[++i];
                continue;
            }

            if (args[i].equals("--place") || args[i].equals("-p")) {
                location = args[++i];
                continue;
            }

            if (args[i].equals("--stream") || args[i].equals("-s")) {
                isStream = true;
                continue;
            }

            if (args[i].equals("--hideRetweets")) {
                isHideRetweets = true;
                continue;
            }

            if (args[i].equals("--limit") || args[i].equals("-l")) {
                limit = Integer.parseInt(args[++i]);
                continue;
            }

            if (args[i].equals("--help") || args[i].equals("-h")) {
                isHelp = true;
            }
        }

    }

    private void startStreamMode() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {

                if (status.isRetweet() && isHideRetweets) {
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

                try {
                    Thread.sleep(ONE_SECOND);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDelNotice) {

            }
            @Override
            public void onTrackLimitationNotice(int i) {

            }
            @Override
            public void onScrubGeo(long l, long l1) {

            }
            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }
            @Override
            public void onException(Exception e) {

            }
        };

        twitterStream.addListener(listener);

        System.out.println(query);

        FilterQuery q = new FilterQuery();
        q.track(query.split(","));

        twitterStream.filter(q);
    }

    private void startNotStreamMode() {
        Twitter twitter = new TwitterFactory().getInstance();
        Query q = new Query(query);
        q.count(limit);

        if (location != null) {

            Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
                    .setAddress(location).getGeocoderRequest();
            try {
                GeocodeResponse gcResponse = geocoder.geocode(geocoderRequest);
                List<GeocoderResult> gcResult = gcResponse.getResults();
                double latitude = gcResult.get(0)
                        .getGeometry().getLocation().getLat().floatValue();
                double longitude = gcResult.get(0)
                        .getGeometry().getLocation().getLng().floatValue();
                GeoLocation geoLocation = new GeoLocation(latitude, longitude);
                q.setGeoCode(geoLocation, RADIUS, Query.Unit.km);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        try {
            QueryResult resultList = twitter.search(q);

            Status[] resultArray = resultList.getTweets()
                    .toArray(new Status[resultList.getTweets().size()]);

            Calendar cal = new GregorianCalendar();
            Date now = cal.getTime();

            for (Status currentStatus: resultArray) {

                String timeFormat = TimeFormat.getTimeFormat(currentStatus,
                        cal, now);

                System.out.print(timeFormat
                        + " \033[34m@"
                        + currentStatus.getUser().getName()
                        + "\033[0m : ");
                if (currentStatus.isRetweet()) {
                    String retweetUserName = currentStatus.getText()
                            .split("RT ")[1].split(":\\s+")[0];
                    System.out.println("ретвитнул "
                            + "\033[34m"
                            + retweetUserName
                            + "\033[0m"
                            + currentStatus.getText()
                            .split(retweetUserName)[1]);
                } else {
                    System.out.println(currentStatus.getText()
                            + " ("
                            + currentStatus.getRetweetCount()
                            + " ретвитов)");
                }

                System.out.println("\n-----------------------------\n");
            }
        } catch (TwitterException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

class TimeFormat {
    private static final long ONE_MINUTE = 60000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000;
    public static final int DAY_OF_YEAR = Calendar.DAY_OF_YEAR;

    static String getTimeFormat(Status currentStatus, Calendar cal,
                                       Date now) {
        //int dayOfYear = Calendar.DAY_OF_YEAR;
        String timeFormat = "";
        boolean key = false;

        long statusTime = currentStatus.getCreatedAt().getTime();

        cal.setTime(now);
        //только что

        if (cal.getTimeInMillis() - statusTime < 2 * ONE_MINUTE) {
            timeFormat = "[только что]";
            key = true;
        }

        //n минут назад

        if ((cal.getTimeInMillis() - statusTime < ONE_HOUR) && (!key)) {
            int minutesCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_MINUTE);
            timeFormat = "[" + minutesCount + " минут назад]";
            key = true;
        }

        //n часов назад

        int dayOfYearNow = cal.get(DAY_OF_YEAR);
        cal.setTime(currentStatus.getCreatedAt());
        int dayOfYearTweeet = cal.get(DAY_OF_YEAR);

        if (!key && (dayOfYearNow == dayOfYearTweeet)) {
            cal.setTime(now);
            int hoursCount = (int)
                    ((cal.getTimeInMillis() - statusTime) / ONE_HOUR);
            timeFormat = "[" + hoursCount + " часов назад]";
            key = true;
        }

        //вчера
        cal.setTime(now);
        if (!key && ((dayOfYearNow == dayOfYearTweeet + 1)
                || ((dayOfYearTweeet == cal.getActualMaximum(DAY_OF_YEAR))
                && (dayOfYearNow == 1)))) {
            timeFormat = "[вчера]";
            key = true;
        }

        //n дней назад
        cal.setTime(now);
        if (!key) {
            int daysCount = (int) ((cal.getTimeInMillis() - statusTime)
                    / ONE_DAY);
            timeFormat = "[" + daysCount + " дней назад]";
        }

        return timeFormat;
    }
}

public class TwitterStreamTveritinova {
    public static void main(String[] args) {

        TwitterStreamApp app = new TwitterStreamApp(args);
        app.run();
    }
}

