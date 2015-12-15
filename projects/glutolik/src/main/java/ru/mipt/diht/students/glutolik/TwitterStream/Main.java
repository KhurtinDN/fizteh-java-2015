package ru.mipt.diht.students.glutolik.TwitterStream;

import com.beust.jcommander.JCommander;

/**
 * Created by glutolik on 13.12.15.
 */
public class Main {
    public static void main(String[] args) {

        TerminalArguments arguments = new TerminalArguments();
        JCommander jCommander = new JCommander(arguments, args);

        if (arguments.isHelp()) {
            jCommander.usage();
            return;
        }

        if (arguments.isStream()) {
            StreamTweets streamTweets = new StreamTweets(
                    arguments.getKeyWord(),
                    arguments.getLocation(),
                    arguments.isHideRetweets()
            );

            streamTweets.beginStream();
        } else {
            SearchTweets searchTweets = new SearchTweets(
                    arguments.getKeyWord(),
                    arguments.getLocation(),
                    arguments.isHideRetweets(),
                    arguments.getLimit());

            try {
                searchTweets.search();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
