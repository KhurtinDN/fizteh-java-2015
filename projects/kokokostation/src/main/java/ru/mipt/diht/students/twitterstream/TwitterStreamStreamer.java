package ru.mipt.diht.students.twitterstream;

import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

import java.io.Writer;

/**
 * Created by mikhail on 28.01.16.
 */
public class TwitterStreamStreamer {
    public static void perform(ArgumentInfo argumentInfo, Writer writer) {
        OutputManager outputManager = new OutputManager(argumentInfo, writer);

        if (argumentInfo.isHelp()) {
            outputManager.writeHelp();
        } else {
            if (argumentInfo.getQuery().isEmpty()) {
                System.err.println("Empty query. Not allowed");
                return;
            }

            Processor processor;

            if (!argumentInfo.isStream()) {
                processor = new QueryProcessor(outputManager, argumentInfo, TwitterFactory.getSingleton(),
                        GoogleGeocoding::getGeocodingResult, Nearby::nearby);
            } else {
                processor = new StreamProcessor(outputManager, argumentInfo, TwitterStreamFactory.getSingleton(),
                        GoogleGeocoding::getGeocodingResult, Nearby::nearby);
            }

            processor.process();
        }
    }
}
