package ru.mipt.diht.students.feezboom.Twitter;

/**
 * Created by avk on 30.09.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        TwitterStreamer streamer = new TwitterStreamer(args);
        streamer.startTwitting();
        return;
    }
}
