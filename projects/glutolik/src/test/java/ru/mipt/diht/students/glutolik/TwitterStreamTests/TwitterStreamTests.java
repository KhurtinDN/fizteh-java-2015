package ru.mipt.diht.students.glutolik.TwitterStreamTests;

import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.mipt.diht.students.glutolik.TwitterStream.TerminalArguments;
import ru.mipt.diht.students.glutolik.TwitterStream.TwitterStreamUtils;
import twitter4j.*;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by glutolik on 15.12.15.
 */
public class TwitterStreamTests {
    @Mock
    private static Twitter twitter;
    @Mock
    private static TerminalArguments args;

    private static List<Status> tweets;

    @BeforeClass
    static public void setUp() throws Exception {
        twitter = mock(Twitter.class);
        args = mock(TerminalArguments.class);
        tweets = Twitter4jTests.tweetsFromJson("tweets.json");

        Mockito.mock(TwitterStreamUtils.class);

        Mockito.mock(TwitterFactory.class);
        Mockito.when(TwitterFactory.getSingleton()).thenReturn(twitter);

        QueryResult resultForJava = mock(QueryResult.class);
        when(resultForJava.getTweets()).thenReturn(tweets);
        when(twitter.search(argThat(hasProperty("query", equalTo("java"))))).thenReturn(resultForJava);

        QueryResult emptyResult = mock(QueryResult.class);
        when(emptyResult.getTweets()).thenReturn(new LinkedList<>());
        when(twitter.search(argThat(hasProperty("query", not(equalTo("java")))))).thenReturn(emptyResult);
    }
}
