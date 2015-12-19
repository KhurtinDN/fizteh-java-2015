package ru.fizteh.fivt.students.bulgakova.TwitterStream;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import twitter4j.Status;
import twitter4j.User;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TwitterOutputTest extends Assert {

    private static TwitterOutput twitterOutput;

    private static GregorianCalendar date0;
    private static GregorianCalendar date1;
    private static GregorianCalendar date2;
    private static GregorianCalendar date3;
    private static GregorianCalendar date4;
    private static GregorianCalendar date5;
    private static GregorianCalendar date6;
    private static GregorianCalendar date7;
    private static GregorianCalendar date8;
    private static GregorianCalendar date9;
    private static GregorianCalendar date10;
    private static GregorianCalendar date11;
    private static GregorianCalendar date12;
    private static GregorianCalendar date13;

    private static Long time0;
    private static Long time1;
    private static Long time2;
    private static Long time3;
    private static Long time4;

    public static final Long SECOND = 1000L;
    public static final Long MINUTE = SECOND * 60;
    public static final Long HOUR = MINUTE * 60;
    public static final Long DAY = HOUR * 24;

    private static Status status1;
    private static Status status2;
    private static Status status3;

    private static User user1;
    private static User user2;
    private static User user3;


    @BeforeClass
    public static void setUpCalendarsData() {


        date0 = new GregorianCalendar();

        date1 = new GregorianCalendar();
        date1.add(Calendar.DATE, -1);

        date2 = new GregorianCalendar();
        date2.add(Calendar.MONTH, -2);
        date2.add(Calendar.YEAR, -2);

        date3 = new GregorianCalendar();
        date3.add(Calendar.DATE, -2);
        date3.add(Calendar.YEAR, -2);

        date4 = new GregorianCalendar();
        date4.add(Calendar.DATE, -2);
        date4.add(Calendar.MONTH, -2);

        date5 = new GregorianCalendar();
        date5.add(Calendar.YEAR, -2);

        date6 = new GregorianCalendar();
        date6.add(Calendar.MONTH, -2);

        date7 = new GregorianCalendar();
        date7.add(Calendar.DATE, -2);

        date8 = new GregorianCalendar();
        date8.add(Calendar.DATE, -2);
        date8.add(Calendar.MONTH, -2);
        date8.add(Calendar.YEAR, -2);


        date9 = new GregorianCalendar();//совпадает только день
        date9.add(Calendar.DATE, -1);
        date9.add(Calendar.MONTH, -2);
        date9.add(Calendar.YEAR, -2);

        date10 = new GregorianCalendar();//совпадает только месяц
        date10.add(Calendar.DATE, -1);
        date10.add(Calendar.DATE, -2);
        date10.add(Calendar.YEAR, -2);

        date11 = new GregorianCalendar();//совпадает только год
        date11.add(Calendar.DATE, -2);
        date11.add(Calendar.MONTH, -2);

        date12 = new GregorianCalendar();//совпадают день и месяц
        date12.add(Calendar.DATE, -1);
        date12.add(Calendar.YEAR, -2);

        date13 = new GregorianCalendar();//совпадают день и год
        date13.add(Calendar.DATE, -1);
        date13.add(Calendar.MONTH, -2);

        time0 = new Date().getTime();
        time1 = new Date().getTime() - MINUTE;
        time2 = new Date().getTime() - 30 * MINUTE;
        time3 = new Date().getTime() - 2 * HOUR;
        time4 = new Date().getTime() - 7 * DAY;
    }

    @Test
    public void TestingIsToday() {
        Assert.assertThat(true, equalTo(twitterOutput.ifToday(date0)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date1)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date2)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date3)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date4)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date5)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date6)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date7)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date8)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date9)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date10)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date11)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date12)));
        Assert.assertThat(false, equalTo(twitterOutput.ifToday(date13)));
    }

    @Test
    public void TestingIsYesterday() {
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date0)));
        Assert.assertThat(true, equalTo(twitterOutput.ifYesterday(date1)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date2)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date3)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date4)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date5)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date6)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date7)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date8)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date9)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date10)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date11)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date12)));
        Assert.assertThat(false, equalTo(twitterOutput.ifYesterday(date13)));
    }

    @Test
    public void TestingGetTweetTime() {
        Assert.assertThat("Только что", equalTo(twitterOutput.getTweetTime(time0)));
        Assert.assertThat("Только что", equalTo(twitterOutput.getTweetTime(time1)));
        Assert.assertThat("30 минут назад", equalTo(twitterOutput.getTweetTime(time2)));
        date0.roll(Calendar.HOUR, -2);
        if (twitterOutput.ifToday(date0)) {
            Assert.assertThat("2 часов назад", equalTo(twitterOutput.getTweetTime(time3)));
        } else if (twitterOutput.ifYesterday(date0)) {
            Assert.assertThat("Вчера", equalTo(twitterOutput.getTweetTime(time3)));
        }
        Assert.assertThat("7 дней назад", equalTo(twitterOutput.getTweetTime(time4)));
    }
    ///////////////////////////////


    @Before
    public void SetUpStatuses() {
        status1 = mock(Status.class);
        user1 = mock(User.class);
        when(user1.getScreenName()).thenReturn("John");
        when(status1.getUser()).thenReturn(user1);
        when(status1.getCreatedAt()).thenReturn(new Date());//Только что
        when(status1.isRetweet()).thenReturn(true);//ретвит
        when(status1.getText()).thenReturn("Hello!");


        status2 = mock(Status.class);
        user2 = mock(User.class);
        when(user2.getScreenName()).thenReturn("Mary");
        when(status2.getUser()).thenReturn(user2);
        when(status2.getCreatedAt()).thenReturn(new Date());//Только что
        when(status2.isRetweet()).thenReturn(false);//не ретвит
        when(status2.getText()).thenReturn("Hello!");
        when(status2.isRetweeted()).thenReturn(true);
        when(status2.getRetweetCount()).thenReturn(1);

        status3 = mock(Status.class);
        user3 = mock(User.class);
        when(user3.getScreenName()).thenReturn("Alex");
        when(status3.getUser()).thenReturn(user3);
        when(status3.getCreatedAt()).thenReturn(new Date());//Только что
        when(status3.isRetweet()).thenReturn(false);//не ретвит
        when(status3.getText()).thenReturn("Hello!");
        when(status3.isRetweeted()).thenReturn(false);

        when(status1.getRetweetedStatus()).thenReturn(status3);//ретвитнул @Alex
    }

    @Test
    public void GetTextToPrintTest() {
        Assert.assertThat("[Только что] \u001B[34m@John: \u001B[0mретвитнул \u001B[34m@Alex:" +
                " \u001B[0mHello!", equalTo(twitterOutput.printTweet(status1, true)));
        Assert.assertThat("[Только что] \u001B[34m@Mary: \u001B[0mHello! (1 ретвитов)",
                equalTo(twitterOutput.printTweet(status2, true)));
        Assert.assertThat("[Только что] \u001B[34m@Alex: \u001B[0mHello!",
                equalTo(twitterOutput.printTweet(status3, true)));

        Assert.assertThat("\u001B[34m@John: \u001B[0mретвитнул \u001B[34m@Alex: \u001B[0mHello!",
                equalTo(twitterOutput.printTweet(status1, false)));
        Assert.assertThat("\u001B[34m@Mary: \u001B[0mHello! (1 ретвитов)",
                equalTo(twitterOutput.printTweet(status2, false)));
        Assert.assertThat("\u001B[34m@Alex: \u001B[0mHello!",
                equalTo(twitterOutput.printTweet(status3, false)));
    }

}
