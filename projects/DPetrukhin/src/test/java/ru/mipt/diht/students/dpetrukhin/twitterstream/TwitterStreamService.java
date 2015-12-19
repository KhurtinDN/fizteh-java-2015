package ru.mipt.diht.students.dpetrukhin.moduletests;

/**
 * Created by daniel on 19.12.15.
 */
import twitter4j.Status;

import java.time.LocalDateTime;

public interface TwitterStreamService {
    String getForm(Integer n, String[] forms);
    String getTimeBetweenForm(LocalDateTime tweetLDT, LocalDateTime nowLDT);
    String getRetweetsForm(Integer retweets);
    String getTimeForm(Status tweet, LocalDateTime localDateTime);
}