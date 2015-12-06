package main;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import twitter4j.*;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Worker {

    private boolean hideRetweets;
    private Twitter twitter;
    private ArrayList<String> places = new ArrayList<>();

    public Worker(boolean _hideRetweets, String place) {
        hideRetweets = _hideRetweets;
        twitter = new TwitterFactory().getInstance();

        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBuQLwjdz5j4zx2CBH02Aae6RwugsjdIVg");
        try {
            GeocodingResult result = GeocodingApi.geocode(context, place).await()[0];

            System.out.println("Your location: " + result.geometry.location.lat + ", " + result.geometry.location.lng);

            GeoQuery geoQuery = new GeoQuery(new GeoLocation(result.geometry.location.lat, result.geometry.location.lng));
            geoQuery.setAccuracy(place);
            ResponseList<Place> places = twitter.searchPlaces(geoQuery);

            for (Place p: places) {
                System.out.print(p);
                this.places.add(p.getId());
            }

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    Query getQuery(String queryStr, int count) {
        Query query = new Query(queryStr);
        if (count > 0 && count <= 100)
            query.setCount(count);
        return query;
    }

    void proceedResult(PrintStream stream, QueryResult result, boolean timing) throws TwitterException {
        List<Status> tweets = result.getTweets();
        this.printTweets(stream, tweets, timing);
    }

    QueryResult performQuerySearch(Query query) {
        QueryResult result;
        try {
            result = this.twitter.search(query);
            return result;
        } catch (TwitterException e){
            if (e.isCausedByNetworkIssue()) {
                System.err.println("No internet connection");
            }
            else
                this.handleException(e);
            return performQuerySearch(query);
        }

    }

    public void performQuery(PrintStream stream, String queryStr, int count) {
        try {
            Query query = this.getQuery(queryStr, count);
            QueryResult result;
            int index = 0;
            do {
                result = this.performQuerySearch(query);
                proceedResult(stream, result, true);
                index += query.getCount();
            } while ((query = result.nextQuery()) != null && index < count);
        } catch (TwitterException e) {
            this.handleException(e);
        }
    }

    void doStreaming(PrintStream stream, Query query) throws  TwitterException {
        QueryResult result;
        do {
            result = this.twitter.search(query);
            proceedResult(stream, result, false);
        } while ((query = result.nextQuery()) != null);
    }

    public void performStream(PrintStream stream, String queryStr) {

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
    }

    void printTweets(PrintStream stream, List<Status> tweets, boolean timing) {
        for (Status tweet: tweets) {
            if (tweet.getPlace() != null) {
                String id = tweet.getPlace().getId();
                if (this.places.contains(id)) {
                    this.printTweet(stream, tweet, timing);
                }
            }
        }
    }

    void printTweet(PrintStream stream, Status tweet, boolean timing) {
        User user = tweet.getUser();
        Date date = tweet.getCreatedAt();
        DateFormat df = new SimpleDateFormat("HH:mm E d LLL yyyy");
        String format = ((timing) ? df.format(date) + " " : "") + Worker.bluedText("@" + user.getScreenName()) + ": ";

        if (tweet.isRetweeted()) {
            format += tweet.getText() + " (" + tweet.getRetweetCount() + " ретвитов)";
        } else if (tweet.isRetweet()) {
            Status retweetedStatus = tweet.getRetweetedStatus();
            format += "ретвитнул " + Worker.bluedText(" @" +  retweetedStatus.getUser().getScreenName()) + ": " + retweetedStatus.getText();
        } else {
            format += tweet.getText();
        }

        if (!tweet.isRetweet() || !this.hideRetweets)
            stream.println(format);
    }

    private static String bluedText(String text) {
        return (char) 27 + "[34m" + text + (char) 27 + "[0m";
    }

    void handleException(TwitterException e) {
        System.err.println(e + "Worker failed :( : " + e.getMessage());
        System.exit(-1);
    }
}
