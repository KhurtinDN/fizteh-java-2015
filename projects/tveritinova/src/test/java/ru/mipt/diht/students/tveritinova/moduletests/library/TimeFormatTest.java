package ru.mipt.diht.students.tveritinova.moduletests.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.tveritinova.TwitterStream.TimeFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeFormatTest {
    Calendar cal;

    @Before
    public void setUp() {
        cal = new GregorianCalendar();
    }

    @Test
    public void testGetTimeFormat() {

        cal.set(2015, Calendar.NOVEMBER, 10, 8, 0, 0);
        Date d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 10, 8, 0, 30);
        Date d2 = cal.getTime();
        Assert.assertEquals("[только что]"
                , TimeFormat.getTimeFormat(d1, cal, d2));

        cal.set(2015, Calendar.NOVEMBER, 10, 8, 0, 0);
        d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 10, 8, 50, 0);
        d2 = cal.getTime();
        Assert.assertEquals("[50 минут назад]"
                , TimeFormat.getTimeFormat(d1, cal, d2));

        cal.set(2015, Calendar.NOVEMBER, 10, 6, 0, 0);
        d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 10, 8, 13, 0);
        d2 = cal.getTime();
        Assert.assertEquals("[2 часов назад]"
                , TimeFormat.getTimeFormat(d1, cal, d2));

        cal.set(2015, Calendar.NOVEMBER, 10, 23, 0, 0);
        d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 11, 0, 1, 14);
        d2 = cal.getTime();
        Assert.assertEquals("[вчера]", TimeFormat.getTimeFormat(d1, cal, d2));

        cal.set(2015, Calendar.NOVEMBER, 10, 6, 0, 0);
        d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 11, 8, 0, 0);
        d2 = cal.getTime();
        Assert.assertEquals("[вчера]", TimeFormat.getTimeFormat(d1, cal, d2));

        cal.set(2015, Calendar.NOVEMBER, 10, 6, 0, 0);
        d1 = cal.getTime();
        cal.set(2015, Calendar.NOVEMBER, 15, 8, 0, 0);
        d2 = cal.getTime();
        Assert.assertEquals("[5 дней назад]"
                , TimeFormat.getTimeFormat(d1, cal, d2));
    }

}
