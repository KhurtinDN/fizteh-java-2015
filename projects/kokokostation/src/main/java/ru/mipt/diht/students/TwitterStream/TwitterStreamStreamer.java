package ru.mipt.diht.students.TwitterStream;

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
                processor = new QueryProcessor(outputManager, argumentInfo);
            } else {
                processor = new StreamProcessor(outputManager, argumentInfo);
            }

            processor.process();
        }
    }
}
