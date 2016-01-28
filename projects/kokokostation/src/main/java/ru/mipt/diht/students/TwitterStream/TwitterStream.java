package ru.mipt.diht.students.TwitterStream;

import java.io.OutputStreamWriter;

/**
 * Created by mikhail on 16.12.15.
 */
public class TwitterStream {
    public static void main(String[] args) {
        ArgumentInfo argumentInfo = new ArgumentInfo(args);

        TwitterStreamStreamer.perform(argumentInfo, new OutputStreamWriter(System.out));
    }
}
