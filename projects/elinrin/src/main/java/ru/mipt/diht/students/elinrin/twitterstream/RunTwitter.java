package ru.mipt.diht.students.elinrin.twitterstream;

import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class RunTwitter {
    public static void main(final String[] args)  {



        try {
            Twitter twitter = new TwitterFactory().getInstance();
            TwitterProvider twitterPr = new TwitterProvider(twitter);
            if (args.length == 0) {
                InteractiveParse.parse(twitterPr);
            } else {
                PackageParse.parse(twitterPr, args);
            }
        } catch (IllegalArgumentException e) {
            HandlerOfException.handler(e);
        }
    }
}
