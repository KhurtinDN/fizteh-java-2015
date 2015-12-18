package ru.mipt.diht.students.maxdankow.twitterstream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import ru.mipt.diht.students.maxdankow.twitterstream.solution.TwitterSearcher;
import ru.mipt.diht.students.maxdankow.twitterstream.solution.TwitterStreamer;

public class TwitterRunner {

    public static void main(String[] args) {
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander jCommander = null;
        try {
            jCommander = new JCommander(arguments, args);
        } catch (ParameterException pe) {
            System.out.println("Неверные аргументы коммандной строки: " + pe.getMessage());
            System.exit(0);
        }

        if (arguments.isHelp()) {
            jCommander.usage();
            return;
        }

        if (arguments.isStreamMode()) {
            TwitterStreamer twitterStreamer = null;
            try {
                twitterStreamer = new TwitterStreamer(
                        arguments.getQueryText(),
                        arguments.getLocationName(),
                        arguments.shouldHideRetweets()
                );
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка TwitterStream: " + e.getMessage());
                System.exit(0);
            }
            twitterStreamer.startStream();
        } else {
            TwitterSearcher twitterSearcher = null;
            try {
                twitterSearcher = new TwitterSearcher(
                    arguments.getQueryText(),
                    arguments.getLocationName(),
                    arguments.shouldHideRetweets(),
                    arguments.getTweetsNumberLimit());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка TwitterSearcher: " + e.getMessage());
                System.exit(0);
            }
            try {
                twitterSearcher.searchTweets();
            } catch (InterruptedException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
