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
        cb.setOAuthConsumerKey("lu5Ffghv61aS5ChuIz4np9bGb");
        cb.setOAuthConsumerSecret("8zxe6YeyjNWz3spLTmP20tw8sYBoOhjPmyR2QFSrDZTKyrrMpM");
        cb.setOAuthAccessToken("3707816955-iJ42EK8Bs3BKAGooVUO5IbwHBsGQE61GeS9bkxh");
        cb.setOAuthAccessTokenSecret("iF7iCnc4I5CqncyFJ1zaShgfBDScJ3oqzwomzIeE0qAKr");

        return new TwitterFactory(cb.build()).getInstance();
    }
}
