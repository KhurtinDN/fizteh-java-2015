package ru.fizteh.fivt.students.popova.TwitterFirst;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by V on 04.11.2015.
 */
public class TwitterJavaUtil {
    public static Twitter getTweets(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("*****");
        cb.setOAuthConsumerSecret("*****");
        cb.setOAuthAccessToken("*****");
        cb.setOAuthAccessTokenSecret("*****");

        return new TwitterFactory(cb.build()).getInstance();
    }
}
