package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mockito.Mockito;

import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

public class JSONTweet {
    public final String userName;
    public final String text;
    public final Date time;
    public final JSONTweet baseTweet;
    public final int retweetCount;
    public final JSONPlace place;
    public final String result;
    
    public JSONTweet(JSONObject tweet) throws JSONException {
        userName = tweet.getString("userName");
        text = tweet.getString("text");
        result = tweet.getString("result");
        try {
            time = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(tweet.getString("time"));
        } catch (ParseException pe) {
            throw new JSONException(pe);
        }
        retweetCount = tweet.getInt("retweetCount");
        JSONObject placeJSON;
        try {
            placeJSON = tweet.getJSONObject("place");
        } catch (JSONException e) {
            placeJSON = null;
        }
        if (placeJSON != null) {
            place = new JSONPlace(placeJSON);
        } else {
            place = null;
        }
        JSONObject baseTweetJSON;
        try {
            baseTweetJSON = tweet.getJSONObject("baseTweet");
        } catch (JSONException e) {
            baseTweetJSON = null;
        }
        if (baseTweetJSON != null) {
            baseTweet = new JSONTweet(baseTweetJSON);
        } else {
            baseTweet = null;
        }
    }
    
    public Status getMockedTweet() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getScreenName()).thenReturn(userName);
        
        Place tweetPlace = null;
        if (place != null) {
            tweetPlace = place.getMockedPlace();
        }
        
        Status tweet = Mockito.mock(Status.class);
        Mockito.when(tweet.getUser()).thenReturn(user);
        Mockito.when(tweet.getCreatedAt()).thenReturn(time);
        Mockito.when(tweet.getPlace()).thenReturn(tweetPlace);
        Mockito.when(tweet.getRetweetCount()).thenReturn(retweetCount);
        Mockito.when(tweet.getText()).thenReturn(text);
        Mockito.when(tweet.isRetweet()).thenReturn(baseTweet != null);
        if (baseTweet != null) {
            Status mockedBaseTweet = baseTweet.getMockedTweet();
            Mockito.when(tweet.getRetweetedStatus()).thenReturn(mockedBaseTweet);
        } else {
            Mockito.when(tweet.getRetweetedStatus()).thenReturn(null);
        }
        return tweet;
    }
}
