package ru.mipt.diht.students.tveritinova.moduletests.library;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.tveritinova.TwitterStream.TwitterStreamApp;
import twitter4j.Status;
import twitter4j.User;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterStreamAppTest {
    TwitterStreamApp appStream;
    TwitterStreamApp appNotStream;
    User mockedUser;
    Status mockedStatus;
    Status mockedStatusRetweet;

    Calendar cal;
    Date now;
    Date statusTime;

    @Before
    public void setUp() {
        String[] argsStream = {"-s"};
        appStream = new TwitterStreamApp(argsStream);

        String[] argsNotStream = {"--hideRetweets"};
        appNotStream = new TwitterStreamApp(argsNotStream);

        mockedStatus = mock(Status.class);
        mockedUser = mock(User.class);
        when(mockedUser.getName()).thenReturn("Vasya");
        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedStatus.getText()).thenReturn("Hello!");
        when(mockedStatus.isRetweet()).thenReturn(false);
        when(mockedStatus.getRetweetCount()).thenReturn(4);

        cal = new GregorianCalendar();
        cal.set(2015, Calendar.NOVEMBER, 22, 10, 0, 10);
        statusTime = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 22, 10, 10, 10);
        now = cal.getTime();
        when(mockedStatus.getCreatedAt()).thenReturn(statusTime);

        mockedStatusRetweet = mock(Status.class);
        when(mockedStatusRetweet.getUser()).thenReturn(mockedUser);
        when(mockedStatusRetweet.getText()).thenReturn("RT @Boba: Bobi Boba!");
        when(mockedStatusRetweet.isRetweet()).thenReturn(true);
        when(mockedStatusRetweet.getCreatedAt()).thenReturn(statusTime);
    }

    @Test
    public void testPrintStatusStream() {
        Assert.assertEquals("-----------------------------\n"
                + "\033[34m@Vasya"
                + "\033[0m: Hello! (4 ретвитов)\n"
                , appStream.printStatusStream(mockedStatus));

        Assert.assertEquals("-----------------------------\n"
                + "\033[34m@Vasya"
                + "\033[0m: ретвитнул "
                + "\033[34m@Boba"
                + "\033[0m: Bobi Boba!\n"
                , appStream.printStatusStream(mockedStatusRetweet));
    }

    @Test
    public void testPrintStatusNotStream() {

        Assert.assertEquals("-----------------------------\n"
                + "[10 минут назад] "
                + "\033[34m@Vasya"
                + "\033[0m: Hello! (4 ретвитов)\n"
                , appNotStream.printStatusNotStream(mockedStatus, cal, now));

        Assert.assertEquals(""
                , appNotStream.printStatusNotStream(mockedStatusRetweet, cal, now));
    }
}
