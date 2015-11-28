package ru.mipt.diht.studens.commands;

import ru.mipt.diht.studens.PrintTweet;
import ru.mipt.diht.studens.TwitterProvider;
import ru.mipt.diht.studens.exception.HandlerException;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class StreamCommand extends Commands {

    @Override
    public void execute(TwitterProvider twitterPr) {

        List<Status> statusList = null;
        try {
            statusList = twitterPr.twitter().getHomeTimeline();
        } catch (TwitterException e) {
            HandlerException.handler(e);
        }
        for (Status status: statusList) {
            if ( (!twitterPr.isHideRetweets()) || !(status.isRetweet()) )
                System.out.println(new PrintTweet().print(status, false));

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }

}
