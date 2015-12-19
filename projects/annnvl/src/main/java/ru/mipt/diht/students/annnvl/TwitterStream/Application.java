package ru.mipt.diht.students.annnvl.TwitterStream;

import twitter4j.*;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class Application {
    public static void main(String[] args) throws Exception{
        Parser parameters = new Parser();
        JCommander command = null;
        try {
            command = new JCommander(parameters, args);
        } catch (Exception errargs){
            System.out.println(errargs.getMessage());
        }

        if(parameters.isHelp()){
            try {
                command.usage();
            } catch (NullPointerException ex){
                System.out.println(ex.getMessage());
            }
            System.exit(0);
        }

        if (parameters.isStream()) {
            try {
                TwitterStream twitterStream;
                twitterStream = new TwitterStreamFactory().getInstance();
                Stream stream = new Stream(twitterStream);
                stream.streamPrint(parameters);
            } catch (TwitterException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                Twitter twitter = new TwitterFactory().getInstance();
                Search search = new Search(twitter);
                search.searchResult(parameters).stream().forEach(System.out::println);
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

    }
}
