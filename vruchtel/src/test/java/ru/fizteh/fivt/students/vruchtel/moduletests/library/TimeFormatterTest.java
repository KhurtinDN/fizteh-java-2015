package ru.fizteh.fivt.students.vruchtel.moduletests.library;

import org.junit.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by Серафима on 29.11.2015.
 */
public class TimeFormatterTest extends Assert {

    @BeforeClass
    public static void setUpCalendarsData() {

        //создаём различные даты
        date0 = new GregorianCalendar();//сегодня

        date1 = new GregorianCalendar();//вчера
        date1.add(Calendar.DATE, -1);

        date2 = new GregorianCalendar();//совпадает только день
        date2.add(Calendar.MONTH, -2);
        date2.add(Calendar.YEAR, -2);

        date3 = new GregorianCalendar();//совпадает только месяц
        date3.add(Calendar.DATE, -2);
        date3.add(Calendar.YEAR, -2);

        date4 = new GregorianCalendar();//совпадает только год
        date4.add(Calendar.DATE, -2);
        date4.add(Calendar.MONTH, -2);

        date5 = new GregorianCalendar();//совпадают день и месяц
        date5.add(Calendar.YEAR, -2);

        date6 = new GregorianCalendar();//совпадают день и год
        date6.add(Calendar.MONTH, -2);

        date7 = new GregorianCalendar();//совпадают месяц и год
        date7.add(Calendar.DATE, -2);

        date8 = new GregorianCalendar();//ничего не совпадает
        date8.add(Calendar.DATE, -2);
        date8.add(Calendar.MONTH, -2);
        date8.add(Calendar.YEAR, -2);

        //аналогичные на вчера
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
        Assert.assertThat(true, equalTo(timeFormatter.isToday(date0)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date1)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date2)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date3)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date4)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date5)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date6)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date7)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date8)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date9)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date10)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date11)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date12)));
        Assert.assertThat(false, equalTo(timeFormatter.isToday(date13)));
    }

    @Test
    public void TestingIsYesterday() {
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date0)));
        Assert.assertThat(true, equalTo(timeFormatter.isYesterday(date1)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date2)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date3)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date4)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date5)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date6)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date7)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date8)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date9)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date10)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date11)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date12)));
        Assert.assertThat(false, equalTo(timeFormatter.isYesterday(date13)));
    }

    @Test
    public void TestingGetTweetTime() {
        Assert.assertThat("Только что", equalTo(timeFormatter.getTweetTime(time0)));
        Assert.assertThat("Только что", equalTo(timeFormatter.getTweetTime(time1)));
        Assert.assertThat("30 минут назад", equalTo(timeFormatter.getTweetTime(time2)));
        date0.roll(Calendar.HOUR, -2);
        if(timeFormatter.isToday(date0)) {
            Assert.assertThat("2 часов назад", equalTo(timeFormatter.getTweetTime(time3)));
        } else if(timeFormatter.isYesterday(date0)){
            Assert.assertThat("Вчера", equalTo(timeFormatter.getTweetTime(time3)));
        }
        Assert.assertThat("7 дней назад", equalTo(timeFormatter.getTweetTime(time4)));
    }

    private static TimeFormatter timeFormatter;

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
}
