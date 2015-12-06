/**
 ** Created by avk on 30.09.15.
 **/

package ru.mipt.diht.students.feezboom.Twitter;

import com.beust.jcommander.ParameterException;

class Main {

    public static void main(String[] args) throws Exception {
        try {
            TwitterStreamer streamer = new TwitterStreamer(args);
            streamer.startTwitting();
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            System.err.println("Попробуйте --help.");
            System.exit(1);
        }
    }
}
