package ru.mipt.diht.students.elinrin.twitterstream.commands;

import ru.mipt.diht.students.elinrin.twitterstream.PrintTweet;
import ru.mipt.diht.students.elinrin.twitterstream.TwitterProvider;
import ru.mipt.diht.students.elinrin.twitterstream.exception.HandlerOfException;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

import static java.lang.Thread.sleep;

public class StreamCommand extends Commands {

    static final int SLEEP_TIME = 1000;
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

            try {
                sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected final int numberOfArguments() {
        return 0;
    }

}
