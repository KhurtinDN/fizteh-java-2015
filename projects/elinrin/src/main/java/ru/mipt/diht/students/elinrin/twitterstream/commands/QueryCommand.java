package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;
import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerException;
import ru.mipt.diht.students.elinrin.twitterstream.PrintTweet;
import twitter4j.*;

public class QueryCommand extends Commands {

    private String searchString;

    @Override
    public void execute(TwitterProvider twitterPr) {

        Query query = new Query(searchString);
        QueryResult result = null;
        try {
            result = twitterPr.twitter().search(query);
        } catch (TwitterException e) {
            HandlerException.handler(e);
        }
        for (Status status : result.getTweets()) {
            if ( (!twitterPr.isHideRetweets()) || !(status.isRetweet()) )
                System.out.println(new PrintTweet().print(status, false));
        }

    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        searchString = args[1];
    }
}
