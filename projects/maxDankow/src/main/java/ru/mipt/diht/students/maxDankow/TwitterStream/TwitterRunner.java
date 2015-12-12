package ru.mipt.diht.students.maxDankow.TwitterStream;
// todo: переименовать все пакеты в lowercase.

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.maxDankow.TwitterStream.solution.TwitterSearcher;
import ru.mipt.diht.students.maxDankow.TwitterStream.solution.TwitterStreamer;

public class TwitterRunner {

    public static void main(String[] args) {
        // todo: обрабатывать неправильные аргументы (использовать метод JCommander::parse).
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander jCommander = new JCommander(arguments, args);

        if (arguments.isHelp()) {
            jCommander.usage();
            return;
        }

        if (arguments.isStreamMode()) {
            TwitterStreamer twitterStreamer = new TwitterStreamer(
                    arguments.getQueryText(),
                    arguments.getLocationName(),
                    arguments.shouldHideRetweets()
            );

            twitterStreamer.startStream();
        } else {
            TwitterSearcher twitterSearcher = new TwitterSearcher(
                    arguments.getQueryText(),
                    arguments.getLocationName(),
                    arguments.shouldHideRetweets(),
                    arguments.getTweetsNumberLimit());

            try {
                twitterSearcher.searchTweets();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
