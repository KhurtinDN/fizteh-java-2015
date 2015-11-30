package ru.mipt.diht.studens;

import ru.mipt.diht.studens.exception.HandlerException;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.util.TimeSpanConverter;

import java.awt.*;
import java.sql.Time;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.List;

public class RunTwitter {
    public static void main(String[] args) throws TwitterException {



        try {
            Twitter twitter = new TwitterFactory().getInstance();
            TwitterProvider twitterPr = new TwitterProvider(twitter);
            if (args.length == 0) {
                InteractiveParse.parse(twitterPr);
            } else {
                PackageParse.parse(twitterPr, args);
            }
        } catch (IllegalArgumentException e) {
            HandlerException.handler(e);
        }
    }
}
