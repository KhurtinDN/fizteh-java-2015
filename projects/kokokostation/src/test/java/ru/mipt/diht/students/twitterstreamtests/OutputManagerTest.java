package ru.mipt.diht.students.twitterstreamtests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.mipt.diht.students.twitterstream.ArgumentInfo;
import ru.mipt.diht.students.twitterstream.OutputManager;
import twitter4j.Status;

import java.io.CharArrayWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{});
        OutputManager outputManager = new OutputManager(argumentInfo, writer);

        outputManager.write("test");

        assertEquals("test" + System.lineSeparator(), writer.toString());
    }

    @Test
    public void testWriteTweet() {
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test",
                "--hideRetweets", "-l", "1", "-h"});

        Status tweet = Mockito.mock(Status.class);

        when(tweet.getRetweetedStatus().getUser().getName()).thenReturn("Vasya");

    }
}