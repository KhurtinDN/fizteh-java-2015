package ru.mipt.diht.students.nkarpachev.TwitterStream;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import twitter4j.*;

public class TwitterStream {

    public static void main(String[] args) {

        JCommanderArgs jargs = new JCommanderArgs();
        JCommander jcommander = new JCommander(jargs);

        try {
            jcommander.parse(args);
        } catch (ParameterException exc) {
            System.out.println(exc.getMessage());
            System.exit(1);
        }

        if (jargs.printHelp()) {
            System.out.println("TwitterStream is a small application for retrieving and accumulating twitter tweets.");
            jcommander.usage();
            System.exit(0);
        }

        String locationQuery = jargs.getLocation();
        LocationTools.setProperties(locationQuery);

        String textQuery = jargs.getQuery();
        Query query = new Query(textQuery);

        boolean doHideRetweets = jargs.hideRetweets();
        int tweetsLimit = jargs.getTweetsLimit();

        boolean isStream = jargs.isStream();

        if (isStream) {
            StreamRunner.StreamTweets(textQuery, doHideRetweets, LocationTools.getGeoLocation());
        }
        else {
            LocationTools.setGeoLocation(query);
            SearchRunner.getTweetsByQuery(query, doHideRetweets, tweetsLimit);
        }
    }


}
