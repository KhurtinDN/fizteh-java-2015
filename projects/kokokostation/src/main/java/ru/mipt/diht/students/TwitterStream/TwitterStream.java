package ru.mipt.diht.students.TwitterStream;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by mikhail on 16.12.15.
 */
public class TwitterStream {
    public static void main(String[] args) {
        ArgumentInfo argumentInfo = ArgumentParser.parse(args);

        TwitterStreamStreamer.perform(argumentInfo, new OutputStreamWriter(System.out));
    }
}

class TwitterStreamStreamer {
    public static void perform(ArgumentInfo argumentInfo, Writer writer) {
        OutputManager outputManager = new OutputManager(argumentInfo, writer);

        if (argumentInfo.isHelp()) {
            outputManager.writeHelp();
        } else {
            if (argumentInfo.getQuery().isEmpty()) {
                System.err.println("Empty query. Not allowed");
                return;
            }

            if (!argumentInfo.isStream()) {
                new QueryProcessor(outputManager, argumentInfo);
            } else {
                new StreamProcessor(outputManager, argumentInfo);
            }
        }
    }
}
