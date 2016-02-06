package ru.mipt.diht.students.twitterstream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by mikhail on 16.12.15.
 */
public class TwitterStream {
    public static void main(String[] args) {
        ArgumentInfo argumentInfo = null;

        try {
            argumentInfo = new ArgumentInfo(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            System.out.print(ArgumentInfo.getHelp());

            System.exit(-1);
        }

        OutputManager outputManager = new OutputManager(argumentInfo, new OutputStreamWriter(System.out));

        if (argumentInfo.isHelp()) {
            try {
                outputManager.writeHelp();
            } catch (IOException e) {
                System.err.println("OutputManager can't write help: " + e.getMessage());
            }
        } else {
            Processor processor;

            GoogleGeocoding googleGeocoding = null;
            try {
                 googleGeocoding = new GoogleGeocoding();
            } catch (IOException e) {
                System.err.println("GoogleGeocoding initialization failure: " + e.getMessage());

                System.exit(1);
            }
            NearbyImpl nearby = new NearbyImpl();

            if (!argumentInfo.isStream()) {
                processor = new QueryProcessor(outputManager, argumentInfo, TwitterFactory.getSingleton(),
                        googleGeocoding, nearby);
            } else {
                processor = new StreamProcessor(outputManager, argumentInfo, TwitterStreamFactory.getSingleton(),
                        googleGeocoding, nearby);
            }

            try {
                processor.process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
