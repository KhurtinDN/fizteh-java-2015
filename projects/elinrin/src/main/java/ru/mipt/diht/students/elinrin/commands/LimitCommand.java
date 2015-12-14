package ru.mipt.diht.students.elinrin.commands;

import ru.mipt.diht.students.elinrin.TwitterProvider;
import ru.mipt.diht.students.elinrin.exception.HandlerException;
import ru.mipt.diht.students.elinrin.PrintTweet;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class LimitCommand extends Commands {
    private int number;

    @Override
    public void execute(TwitterProvider twitterPr) {


        List<Status> statusList = null;
        try {
            statusList = twitterPr.twitter().getHomeTimeline();

        } catch (TwitterException e) {
            HandlerException.handler(e);
        }
        for (Status status: statusList) {
            if ((!twitterPr.isHideRetweets()) || !(status.isRetweet()))
                System.out.println(new PrintTweet().print(status, false));
            number-=1;
            if (number == 0) break;
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
