package ru.mipt.diht.students.feezboom.Twitter;

import twitter4j.*;
import com.beust.jcommander.*;


/**
 * Created by avk on 30.09.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {

//        String source = TwitterStreamer.getCityString();

//        TwitterStreamer streamer = new TwitterStreamer(args);
//        streamer.startStreamer();

        System.out.println(TwitterStreamer.translitToRussian("Dolgoprudyj"));
        return;
    }

}
