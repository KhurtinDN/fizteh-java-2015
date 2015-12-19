package ru.mipt.diht.students.egdeliya.TwitterStream;

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.util.List;
import java.util.Scanner;
import java.util.Vector;

@SuppressWarnings("checkstyle:magicnumber")
public class TwitterStreamer {
    private Twitter twitter;
    private String usersQuery;
    private JCommanderParser jCommander = new JCommanderParser();
    private String[] arguments;

    private static final int RADIUS = 50;

    public TwitterStreamer(String[] args) {
        arguments = args;

        //парсим входую строку
        new JCommander(jCommander, arguments);
    }

    public final void help() {
        //пользователь запрашивает справку
        if (jCommander.isHelp()) {
            JCommander jCommand = new JCommander(jCommander, arguments);
            jCommand.usage();
        }
    }

    public final void query() throws TwitterException, InterruptedException {
        twitter = TwitterFactory.getSingleton();
        Query twitterQuery = new Query(usersQuery);

        //если установлено место
        if (jCommander.getLocation() != "") {
            twitterQuery = place(twitterQuery);
        }

       //если указан параметр stream
        if (jCommander.isStream()) {
            streamRunner(twitterQuery);
        } else {

            //если установлен лимит
            if (jCommander.getLimit() < Integer.MAX_VALUE) {
                twitterQuery.setCount(jCommander.getLimit());
            }

            try {
                QueryResult result = twitter.search(twitterQuery);

                //если по запросу не найдены твиты
                if (result.getTweets().size() == 0) {
                    System.out.println("There is no tweets for " + usersQuery);
                }

                for (Status status : result.getTweets()) {
                    printTweet(status);
                }
            } catch (TwitterException t) {
                System.err.println(t.getMessage());
            }
        }
    }

    public final void streamRunner(Query twitterQuery) throws InterruptedException, TwitterException {

        TwitterStream streamer = new TwitterStreamFactory().getInstance();
        StatusAdapter listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                printTweet(status);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
            @Override
            public void onException(Exception ex) {
                System.err.println(ex.getMessage());
            }
        };

        streamer.addListener(listener);

        //фильтруем запросы
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(twitterQuery.getQuery());
        streamer.filter(filterQuery);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            Thread.sleep(1000);
        }
    }

    public final void printTweet(Status status) {

        //если параметр stream не установлен
        if (!jCommander.isStream()) {
            System.out.println(status.getCreatedAt());
        }

        //красит ник в синий цвет
        System.out.println("@" + "\033[34m@" + status.getUser().getName()
                + "\033[0m: " + " : " + status.getText());

        //если не нужно прятать ретвиты
        if (!jCommander.isHideRetweets() && !jCommander.isStream()) {
            try {
                List<Status> statusList = twitter.getRetweets(status.getId());
                for (Status node : statusList) {
                    System.out.println("@" + " \033[34m@" + node.getUser().getName() + "\033[0m: " + " : "
                            + node.getText());
                }
            } catch (TwitterException t) {
                System.err.println(t.getMessage());
            }
        }
    }

    public final Query place(Query twitterQuery) throws TwitterException {

        Vector<GeoLocation> locations = new Vector<>();

        //устанавливаем магический ip
        GeoQuery geoQuery = new GeoQuery("0.0.0.0");
        geoQuery.setQuery(jCommander.getLocation());

        //получаем информацию о местах
        //в эпсилон окрестности нашего места
        ResponseList<Place> responseList;
        responseList = twitter.searchPlaces(geoQuery);

        for (Place geoPlace : responseList) {

            for (int i = 0; i < geoPlace.getBoundingBoxCoordinates().length; i++) {
                for (int j = 0;
                     j < geoPlace.getBoundingBoxCoordinates()[i].length; j++) {
                    locations.add(geoPlace.getBoundingBoxCoordinates()[i][j]);
                    //System.out.println(geoPlace.getBoundingBoxCoordinates()[i][j]);
                }
            }
        }

        //вычисляем центр
        double x = 0;
        double y = 0;
        for (GeoLocation location : locations) {
            x += location.getLatitude();
            y += location.getLongitude();
        }
        x /= locations.size();
        y /= locations.size();

        GeoLocation ourLocation = new GeoLocation(x, y);
        twitterQuery.setGeoCode(ourLocation, RADIUS, Query.Unit.km);

        return twitterQuery;
    }

    public final void twitterStreamRun() {

        //проверяем, что есть параметр help
        help();
        usersQuery = jCommander.getUsersQuery();

        try {
            query();
        } catch (TwitterException t) {
            System.err.println(t.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

}
