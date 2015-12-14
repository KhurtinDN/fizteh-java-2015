package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;
import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import ru.mipt.diht.students.elinrin.twitterstream.PrintTweet;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class LimitCommand extends Commands {
    private int number;

    @Override
    public final void execute(final TwitterProvider twitterPr) {


        List<Status> statusList = null;
        try {
            statusList = twitterPr.twitter().getHomeTimeline();

        } catch (TwitterException e) {
            HandlerOfException.handler(e);
        }
        for (Status status: statusList) {
            if ((!twitterPr.isHideRetweets()) || !(status.isRetweet())) {
                System.out.println(new PrintTweet().print(status, false));
            }
            number -= 1;
            if (number == 0) {
                break;
            }
        }

    }

    @Override
    protected final int numberOfArguments() {
        return 1;
    }

    @Override
    protected final void putArguments(final String[] args) {
        number = Integer.parseInt(args[1]);
    }
}
