package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;
import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import ru.mipt.diht.students.elinrin.twitterstream.PrintTweet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

public class QueryCommand extends Commands {

    private String searchString;

    @Override
    public final void execute(final TwitterProvider twitterPr) {

        Query query = new Query(searchString);
        QueryResult result = null;
        try {
            result = twitterPr.twitter().search(query);
        } catch (TwitterException e) {
            HandlerOfException.handler(e);
        }
        for (Status status : result.getTweets()) {
            if ((!twitterPr.isHideRetweets()) || !(status.isRetweet())) {
                System.out.println(new PrintTweet().print(status, false));

            }
        }

    }

    @Override
    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    protected final void putArguments(final String[] args) {
        searchString = args[1];
    }
}
