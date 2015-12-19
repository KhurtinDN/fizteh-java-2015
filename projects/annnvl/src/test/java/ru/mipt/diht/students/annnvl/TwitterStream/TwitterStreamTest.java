package ru.mipt.diht.students.annnvl.TwitterStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.*;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TwitterStreamTest {
    @Mock
    twitter4j.TwitterStream twitterStream;

    @InjectMocks
    TwitterStream stream;
    public static List<Status> statuses;

    @BeforeClass
    public static void loadSampleData() {
        statuses = Twitter4jTestUtils.tweetsFromJson("/statuses.json");
    }

    @Test
    public void streamResultTest() throws Exception {
        ArgumentCaptor<StatusListener> statusListener = ArgumentCaptor.forClass(StatusListener.class);
        doNothing().when(twitterStream).addListener((StatusListener) statusListener.capture());
        doAnswer(i -> {
            statuses.forEach(s -> statusListener.getValue().onStatus(s));
            return null;
        }).when(twitterStream).filter(any(FilterQuery.class));
        List<Status> tweets = new ArrayList<>();

        Parameters param = new Parameters();
        param.setQuery("mipt");
        param.setStream(true);
        param.setLimit(100);
        param.setHideRetweets(false);
        param.setHelp(false);
        param.setPlace("");

        stream.stream(param, tweets::add);
        assertTrue(tweets.size() == 13);

        verify(twitterStream).addListener((StatusListener) any(StatusAdapter.class));
        verify(twitterStream).filter(any(FilterQuery.class));
    }
}

