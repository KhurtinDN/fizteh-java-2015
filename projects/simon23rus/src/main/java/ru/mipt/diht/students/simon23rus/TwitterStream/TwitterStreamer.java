package ru.mipt.diht.students.simon23rus.TwitterStream;


import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TwitterStreamer {

    private static final int SECONDS_IN_YEAR = 60 * 60 * 24 * 365;
    private static final int SECONDS_IN_MONTH = 60 * 60 * 24 * 30;
    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final String EXCLUDE_RETWEETS = " +exclude:retweets";
    private static final String AROUND_THE_WORLD = "nearby";
    private static boolean isItYesterday;


    public static StatusAdapter listener = new StatusAdapter(){
        private TwitterPrinter myOwnPrinter;
        public void onStatus(Status givenTweet) {
            Date currentDate = Calendar.getInstance().getTime();
            myOwnPrinter.printStringWithFormat(currentDate,givenTweet, false);
            try {
                Thread.sleep(1000);                 //1000=1.
            } catch(InterruptedException ex) {
                System.out.println("Current Thread has interrupted");
            }
        }
    };






    public static void main(String[] args) throws TwitterException, IOException, InterruptedException, JSONException {

        MyJCommander jct = new MyJCommander();
        new JCommander(jct, args);

        GeoCodeConverter myOwnConverter = new GeoCodeConverter();
        TwitterPrinter myOwnPrinter = new TwitterPrinter();
        if (jct.help) {
            System.out.println("Now you are getting hints for usage this applocation"
                    + "\n[--query|-q <query or keywords for stream>]\n"
                    + "[--place|-p <location|'nearby'>]\n"
                    + "[--stream|-s]\n"
                    + "[--hideRetweets]\n"
                    + "[--limit|-l <tweets>]\n"
                    + "[--help|-h]\n");
            return;
        }

        Twitter twitter = new TwitterFactory().getInstance();
        if (jct.toPost != null) {
            twitter.updateStatus(jct.toPost);
        }


        System.out.println("It is your searching place  " + jct.place);


        if (jct.stream) {
            TwitterStream myTwitterStream = new TwitterStreamFactory().getInstance();
            myTwitterStream.addListener(listener);
            FilterQuery myFilter = new FilterQuery();
            ArrayList<String> toTrack = new ArrayList<String>();
            toTrack.add(jct.query);
            myFilter.track(toTrack.toArray(new String[toTrack.size()]));
            myTwitterStream.filter(myFilter);
        }
        else {
            if (jct.hideRetweets) {
                jct.query += EXCLUDE_RETWEETS;
            }
            Query query = new Query(jct.query);
            QueryResult result;
            query.setCount(jct.tweetsByQuery);
            System.out.println("bu");

//            query.setGeoCode(myOwnConverter.getCoordinates(jct.place), 400, Query.Unit.km);
            GeoLocation queryLocation = myOwnConverter.getCoordinates(jct.place);
            int counter = 0;
            do {
                result = twitter.search(query);
                List<Status> tweetsFound = result.getTweets();
                for (Status tweet : tweetsFound) {
                    GeoLocation tweetLocation;
                    if(tweet.getGeoLocation() != null) {
                        tweetLocation = new GeoLocation(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude());
                    }
                    else {
                        continue;
                    }
                    if(queryLocation == null) {
                        System.out.println("Can't resolve your Location");
                        return;
                    }
                    if(myOwnConverter.near(queryLocation, tweetLocation, 40)) {
                        Date currentDate = Calendar.getInstance().getTime();
                        myOwnPrinter.printStringWithFormat(currentDate, tweet, true);

                        ++counter;
                        if (counter == jct.tweetsByQuery) {
                            return;
                        }
                    }
                }
            } while ((counter < jct.tweetsByQuery) && (query = result.nextQuery()) != null);
        }

    }

}
