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
        cb.setOAuthConsumerKey("lu5Ffghv61aS5ChuIz4np9bGb");
        cb.setOAuthConsumerSecret("8zxe6YeyjNWz3spLTmP20tw8sYBoOhjPmyR2QFSrDZTKyrrMpM");
        cb.setOAuthAccessToken("3707816955-iJ42EK8Bs3BKAGooVUO5IbwHBsGQE61GeS9bkxh");
        cb.setOAuthAccessTokenSecret("iF7iCnc4I5CqncyFJ1zaShgfBDScJ3oqzwomzIeE0qAKr");
        return new TwitterStreamFactory(cb.build()).getInstance();
    }
}
