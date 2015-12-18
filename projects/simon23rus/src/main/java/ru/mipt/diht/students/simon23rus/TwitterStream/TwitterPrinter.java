package ru.mipt.diht.students.simon23rus.TwitterStream;

import twitter4j.Status;

import java.util.Date;

/**
 * Created by semenfedotov on 05.12.15.
 */
public class TwitterPrinter {
    public static String makeBlue(String toPaint) {
        return  (char)27 + "[34;1m@" + toPaint;
    }

    public static String printStringWithFormat(Date currentTime, Status tweet, boolean timeIsNeeded) {
        TimeTransformer myOwnTransformer = new TimeTransformer();
        String tweetToShow = "";
        if(timeIsNeeded) {
            long delta = currentTime.getTime() - tweet.getCreatedAt().getTime();
            myOwnTransformer.isItYesterdayTweet(tweet.getCreatedAt());
            tweetToShow += (char)27 + "[35;1;4m" + myOwnTransformer.correctRussianText(delta / 1000);
        }
        tweetToShow += makeBlue(tweet.getUser().getScreenName());
        if (tweet.isRetweet()) {
            tweetToShow += ":" + (char)27 + "[33;4m" + " ретвитнул ";
            tweetToShow += (char)27 + "[31;1m@" + tweet.getRetweetedStatus().getUser().getScreenName();
            tweetToShow += (char)27 + "[0m" + ":";
            tweetToShow += tweet.getText();
//            tweetToShow += tweet.getText().substring(5 + tweet.getRetweetedStatus().getUser().getScreenName().length());
        }
        else {
            tweetToShow += (char)27 + "[0m" + ":" + tweet.getText();
        }
        if (!tweet.isRetweet()) {
            tweetToShow += (char)27 + "[42m" + "(<" + tweet.getRetweetCount() + "> Ретвитов)" + (char) 27 + "[0m";
        }
        System.out.println(tweetToShow);
        return tweetToShow;
    }



}
