package ru.mipt.diht.students.sopilnyak.moduletests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.mipt.diht.students.sopilnyak.moduletests.library.DateString;

import java.util.Calendar;
import java.util.Date;

public class DateStringTest {

    Calendar current;

    @Before
    public void setCurrentDate() {
        Date currentDate = new Date(2015, 12, 12, 12, 12, 1);
        current = Calendar.getInstance();
        current.setTime(currentDate);
    }

    @Test
    public void testNow() {
        Assert.assertEquals("Только что", DateString.getDate(new Date(2015, 12, 12, 12, 12, 0), current));
    }

    @Test
    public void testMinutes() {
        Assert.assertEquals("5 минут назад", DateString.getDate(new Date(2015, 12, 12, 12, 7, 0), current));
    }

    @Test
    public void testHours() {
        Assert.assertEquals("5 часов назад", DateString.getDate(new Date(2015, 12, 12, 7, 12, 0), current));
    }

    @Test
    public void testDays() {
        Assert.assertEquals("5 дней назад", DateString.getDate(new Date(2015, 12, 7, 12, 12, 0), current));
    }

    @Test
    public void testYears() {
        Assert.assertEquals("210013 дней назад", DateString.getDate(new Date(1440, 12, 12, 12, 12, 0), current));
    }
}
