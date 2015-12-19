package ru.mipt.diht.students.IrinaMudrova.Twitter.library;


import org.junit.*;
import twitter4j.*;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class TwitterListenerTest extends TwitterListener {

    @Test
    public void testListener1() {
        init(false);
        Status status = mock(Status.class);
        User user = mock(User.class);
        when(status.isRetweet()).thenReturn(false);
        when(status.getCreatedAt()).thenReturn(new Date());
        when(status.getUser()).thenReturn(user);
        when(user.getScreenName()).thenReturn("Vasya Pupkin");
        when(status.getText()).thenReturn("Petya is the best friend of mine");
        when(status.isRetweeted()).thenReturn(false);
        when(status.getRetweetCount()).thenReturn(-1);
        onStatus(status);
        String result = pollTweetStr();
        assertEquals(result,
                TweetFormatter.USER_HIGHLIGHT_BEGIN + "@Vasya Pupkin" + TweetFormatter.USER_HIGHLIGHT_END
                        + ": Petya is the best friend of mine");
        assertEquals(pollTweetStr(), null);
    }

    @Test
    public void testListener2() {
        init(true);
        Status status = mock(Status.class);
        User user = mock(User.class);
        when(status.isRetweet()).thenReturn(true);
        onStatus(status);
        String result = pollTweetStr();
        assertEquals(result, null);
    }
}
