/**
 ** Created by avk on 30.09.15.
 **/

package ru.mipt.diht.students.feezboom.Twitter;

import com.beust.jcommander.ParameterException;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            TwitterStreamer streamer = new TwitterStreamer(args);
            streamer.startTwitting();
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            System.err.println("Try to use --help.");
            System.exit(0);
        }
    }
}
