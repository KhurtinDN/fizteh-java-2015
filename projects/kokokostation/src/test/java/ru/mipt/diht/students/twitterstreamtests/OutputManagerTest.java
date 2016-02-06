package ru.mipt.diht.students.twitterstreamtests;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.ArgumentInfo;
import ru.mipt.diht.students.twitterstream.OutputManager;
import twitter4j.Status;

import java.io.CharArrayWriter;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class OutputManagerTest {
    private CharArrayWriter writer;

    @Before
    public void setWriter() {
        writer = new CharArrayWriter();
    }

    @Test
    public void testWrite() {
        ArgumentInfo argumentInfo = new ArgumentInfo("-q", "test");
        OutputManager outputManager = new OutputManager(argumentInfo, writer);

        outputManager.write("test");

        assertThat(writer.toString(), is("test" + System.lineSeparator()));
    }

    @Test
    public void testWriteTweet() {
        ArgumentInfo argumentInfo = new ArgumentInfo("-q", "test", "-l", "1", "-h");

        Status tweet = mock(Status.class, RETURNS_DEEP_STUBS);

        when(tweet.getRetweetedStatus().getUser().getName()).thenReturn("Вася");
        when(tweet.getText()).thenReturn("Поел").thenReturn("Поспал").thenReturn("Поел");
        when(tweet.getUser().getName()).thenReturn("Петя");

        Date[] testDate = new Date[3];
        for (int i = 0; i < testDate.length; i++) {
            testDate[i] = new Date();
        }

        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, -5);
        testDate[0].setTime(time.getTimeInMillis());
        time.add(Calendar.HOUR, -24);
        testDate[1].setTime(time.getTimeInMillis());
        time.add(Calendar.HOUR, -71);
        testDate[2].setTime(time.getTimeInMillis());

        when(tweet.getCreatedAt()).thenReturn(testDate[0]).thenReturn(testDate[1]).thenReturn(testDate[2]);

        when(tweet.isRetweet()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(tweet.getRetweetCount()).thenReturn(63).thenReturn(0);

        OutputManager outputManager = new OutputManager(argumentInfo, writer);
        for (int i = 0; i < 3; i++) {
            assertThat(outputManager.writeTweet(tweet), is(true));
        }

        assertThat(writer.toString(), is("5 минут назад @\033[34mПетя\033[0m: ретвитнул @Вася: Поел" + System.lineSeparator() +
                        "Вчера @\033[34mПетя\033[0m: Поспал (63 ретвита)" + System.lineSeparator() +
                        "3 дня назад @\033[34mПетя\033[0m: Поел" + System.lineSeparator()));
    }

    @Test
    public void testHideRetweetsWriteTweet() {
        ArgumentInfo argumentInfo = new ArgumentInfo("-q", "test", "--hideRetweets", "-l", "1", "-h");

        Status tweet = mock(Status.class, RETURNS_DEEP_STUBS);

        when(tweet.getText()).thenReturn("Поспал");
        when(tweet.getUser().getName()).thenReturn("Петя");

        Date testDate = new Date();

        Calendar time = Calendar.getInstance();
        time.add(Calendar.HOUR, -24);
        testDate.setTime(time.getTimeInMillis());

        when(tweet.getCreatedAt()).thenReturn(testDate);

        when(tweet.isRetweet()).thenReturn(true).thenReturn(false);
        when(tweet.getRetweetCount()).thenReturn(10000013);

        OutputManager outputManager = new OutputManager(argumentInfo, writer);
        assertThat(outputManager.writeTweet(tweet), is(false));
        assertThat(outputManager.writeTweet(tweet), is(true));

        assertThat(writer.toString(),
                is("Вчера @\033[34mПетя\033[0m: Поспал (10000013 ретвитов)" + System.lineSeparator()));
    }
}