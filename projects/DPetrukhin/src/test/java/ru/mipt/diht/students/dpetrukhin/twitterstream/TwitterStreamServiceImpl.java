package ru.mipt.diht.students.dpetrukhin.moduletests;

/**
 * Created by daniel on 19.12.15.
 */
import ru.mipt.diht.students.dpetrukhin.twitterstream.TSWordsForm;
import twitter4j.Status;

import java.time.LocalDateTime;

public class TwitterStreamServiceImpl implements TwitterStreamService {
    @Override
    public String getTimeBetweenForm(LocalDateTime tweetLDT, LocalDateTime nowLDT) {
        return TSWordsForm.getTimeBetweenForm(tweetLDT, nowLDT);
    }

    @Override
    public String getForm(Integer n, String[] forms) {
        return TSWordsForm.getForm(n, forms);
    }

    @Override
    public String getTimeForm(Status tweet, LocalDateTime localDateTime) {
        return TSWordsForm.getTimeForm(tweet, localDateTime);
    }

    @Override
    public String getRetweetsForm(Integer retweets) {
        return TSWordsForm.getRetweetsForm(retweets);
    }
}