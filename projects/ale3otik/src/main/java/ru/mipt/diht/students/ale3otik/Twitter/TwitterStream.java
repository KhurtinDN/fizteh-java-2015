
/**
 * Created by alex on 21.09.15.
 */

package ru.mipt.diht.students.ale3otik.Twitter;

import com.beust.jcommander.JCommander;
import twitter4j.*;
import twitter4j.StatusListener;


public class TwitterStream {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLUE = "\u001B[34m";

    private static void streamStart(FilterQuery query) {

        twitter4j.TwitterStream twStream = twitter4j
                .TwitterStreamFactory.getSingleton();

        StatusListener listener = new StatusListener() {


            @Override
            public void onStatus(Status status) {
                System.out
                        .println(ANSI_BLUE
                                + status.getUser()
                                .getScreenName()
                                + ANSI_RESET
                                + " : " + status.getText());
            }

            @Override
            public void onDeletionNotice(
                    StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(
                    int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long var1, long var3) {
            }

            @Override
            public void onStallWarning(StallWarning var1) {
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        twStream.addListener(listener);
        twStream.filter(query);

    }

    public static void main(String[] args) throws TwitterException {
        System.out.println("\n\nTwitter 0.1 welcome\n\n");
        JCommanderParser jcp = new JCommanderParser();
        JCommander jcm = new JCommander(jcp, args);
        jcm.setProgramName("TwitterStream");
        String query = jcp.getQuery();

        if (jcp.isStream()) {
            System.out.println("\nStart stream >>> ");
            streamStart(new FilterQuery(query));
        }
        if (jcp.isHelp()) {
            System.out.println("Help");
        }
    }
}
