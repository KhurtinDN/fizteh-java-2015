package ru.mipt.diht.studens.commands;

import ru.mipt.diht.studens.PrintTweet;
import ru.mipt.diht.studens.TwitterProvider;
import ru.mipt.diht.studens.exception.HandlerException;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class LimitCommand extends Commands {
    private int number;

    @Override
    public void execute(TwitterProvider twitterPr) {

        twitterPr.changeParameterLimit(number);
        List<Status> statusList = null;
        try {
            statusList = twitterPr.twitter().getHomeTimeline();

        } catch (TwitterException e) {
            HandlerException.handler(e);
        }
        for (Status status: statusList) {
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
        number = Integer.parseInt(args[1]);
    }
}
