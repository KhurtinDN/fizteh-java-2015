package ru.mipt.diht.students.maxDankow.TwitterStream;

import com.beust.jcommander.JCommander;
import ru.mipt.diht.students.maxDankow.TwitterStream.solution.TwitterSearcher;
import ru.mipt.diht.students.maxDankow.TwitterStream.solution.TwitterStreamer;

public class TwitterRunner {

    public static void main(String[] args) {
        ComandLineArgumentsParser argumentsParser = new ComandLineArgumentsParser();
        JCommander jCommander = new JCommander(argumentsParser, args);
        if (argumentsParser.isHelp()) {
            jCommander.usage();
            return;
        }
        String locationName = argumentsParser.getLocationName();
        String queryText = argumentsParser.getQueryText();
        boolean shouldHideRetweets = argumentsParser.shouldHideRetweets();
        int tweetsNumberLimit = argumentsParser.getTweetsNumberLimit();

        if (argumentsParser.isStreamMode()) {
            TwitterStreamer twitterStreamer = new TwitterStreamer(queryText, locationName,
                    shouldHideRetweets, tweetsNumberLimit);
            twitterStreamer.startStream();
        } else {
            TwitterSearcher twitterSearcher = new TwitterSearcher(queryText, locationName,
                    shouldHideRetweets, tweetsNumberLimit);
            twitterSearcher.searchTweets();
        }
        System.exit(0);
    }
}
