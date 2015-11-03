package ru.fizteh.fivt.students.popova.TwitterFirst;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
/**
 * Created by V on 29.10.2015.
 */
public class TwitterStreamJavaUtil {
    public static TwitterStream getStream(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("*****");
        cb.setOAuthConsumerSecret("*****");
        cb.setOAuthAccessToken("*****");
        cb.setOAuthAccessTokenSecret("*****");

        return new TwitterStreamFactory(cb.build()).getInstance();
    }
}
