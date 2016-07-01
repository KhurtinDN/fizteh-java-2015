package ru.mipt.diht.students.feezboom.Twitter;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import twitter4j.Status;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * * Created by avk on 19.12.15.
 **/

public class TwitterStreamerTest extends TestCase {
    TwitterStreamer myStreamer;
    Status mockedStatus;

    Date mockedDate;
    Calendar calendar;
    Date time;

    @Before
    public void init() throws Exception {
        String[] args = new String[6];
        args[0] = "--query";
        args[1] = "Привет";
        args[2] = "--limit";
        args[3] = "10";
        args[4] = "--place";
        args[5] = "Долгопрудный";

        myStreamer = new TwitterStreamer(args);

        mockedStatus = mock(Status.class);

        calendar = new GregorianCalendar();
        calendar.set(2015, Calendar.DECEMBER, 18, 1, 7, 0);
        time = calendar.getTime();

        mockedDate = mock(Date.class);

        when(mockedDate.getTime()).thenReturn(time.getTime());// Типа вчера
        when(mockedStatus.getCreatedAt()).thenReturn(time); // Типа создан вчера

    }

    @Test
    public void testTimeString() {
        System.out.println("HELOOOOO, I'm going to test it!");
    }

    @Test
    public void testFormattedWord() {
        Date date = new Date();

    }

    @Test
    public void testTweetTime() {
        GregorianCalendar calen = new GregorianCalendar(2015, 11, 18);
        Date date = calen.getTime();

 /*       assertEquals(myStreamer.getTimeFormattedTimeString(mockedDate),
                "Вчера");
        System.out.println("НАШЕ СЛОВО ==" + myStreamer.getTimeFormattedTimeString(mockedDate));
*/
    }

    @Test
    public void testTweetWord() {
       /* assertEquals(myStreamer.getTweetWord(11), "ретвитов");
        assertEquals(myStreamer.getTweetWord(0), "ретвитов");
        assertEquals(myStreamer.getTweetWord(1), "ретвит");
        assertEquals(myStreamer.getTweetWord(2), "ретвита");
        assertEquals(myStreamer.getTweetWord(3), "ретвита");
        assertEquals(myStreamer.getTweetWord(4), "ретвита");
        assertEquals(myStreamer.getTweetWord(5), "ретвитов");
        assertEquals(myStreamer.getTweetWord(6), "ретвитов");
        assertEquals(myStreamer.getTweetWord(7), "ретвитов");
        assertEquals(myStreamer.getTweetWord(8), "ретвитов");
        assertEquals(myStreamer.getTweetWord(9), "ретвитов");
        assertEquals(myStreamer.getTweetWord(10), "ретвитов");
        assertEquals(myStreamer.getTweetWord(11), "ретвитов");
        assertEquals(myStreamer.getTweetWord(12), "ретвитов");
        assertEquals(myStreamer.getTweetWord(13), "ретвитов");
        assertEquals(myStreamer.getTweetWord(14), "ретвитов");
        assertEquals(myStreamer.getTweetWord(15), "ретвитов");
        assertEquals(myStreamer.getTweetWord(16), "ретвитов");
        assertEquals(myStreamer.getTweetWord(17), "ретвитов");
        assertEquals(myStreamer.getTweetWord(18), "ретвитов");
        assertEquals(myStreamer.getTweetWord(19), "ретвитов");
        assertEquals(myStreamer.getTweetWord(20), "ретвитов");
        assertEquals(myStreamer.getTweetWord(21), "ретвит");
        assertEquals(myStreamer.getTweetWord(22), "ретвита");
        assertEquals(myStreamer.getTweetWord(23), "ретвита");
        assertEquals(myStreamer.getTweetWord(100), "ретвитов");
        assertEquals(myStreamer.getTweetWord(151), "ретвит");*/
    }
}