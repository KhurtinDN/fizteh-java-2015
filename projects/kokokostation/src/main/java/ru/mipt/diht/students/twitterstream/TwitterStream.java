package ru.mipt.diht.students.twitterstream;

import com.beust.jcommander.ParameterException;

import java.io.OutputStreamWriter;

/**
 * Created by mikhail on 16.12.15.
 */
public class TwitterStream {
    public static void main(String[] args) {
        ArgumentInfo argumentInfo;

        try {
            argumentInfo = new ArgumentInfo(args);
        } catch (ParameterException e) {
            return;
        }

        TwitterStreamStreamer.perform(argumentInfo, new OutputStreamWriter(System.out));
    }
}
