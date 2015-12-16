package util;

import org.junit.Test;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

/**
 * Created by lokotochek on 13.12.15.
 */
public class TimeStringTest {

    @Test
    public void testFindForm() throws Exception {
        long number = 23;
        WordForm form = TimeString.findForm(number);
        assertEquals(WordForm.THIRD, form);
        number = 21;
        form = TimeString.findForm(number);
        assertEquals(WordForm.SECOND, form);
        number = 115;
        form = TimeString.findForm(number);
        assertEquals(WordForm.FIRST, form);
    }

    @Test
    public void testFormDays() throws Exception {
        long days = 7;
        String form = TimeString.formDays(days);
        assertEquals("7 дней", form);
        days = 21;
        form = TimeString.formDays(days);
        assertEquals("21 день", form);
        days = 3;
        form = TimeString.formDays(days);
        assertEquals("3 дня", form);
    }

    @Test
    public void testFormHours() throws Exception {
        long hours = 7;
        String form = TimeString.formHours(hours);
        assertEquals("7 часов", form);
        hours = 21;
        form = TimeString.formHours(hours);
        assertEquals("21 час", form);
        hours = 3;
        form = TimeString.formHours(hours);
        assertEquals("3 часа", form);
    }

    @Test
    public void testFormMinutes() throws Exception {
        long minutes = 7;
        String form = TimeString.formMinutes(minutes);
        assertEquals("7 минут", form);
        minutes = 21;
        form = TimeString.formMinutes(minutes);
        assertEquals("21 минуту", form);
        minutes = 3;
        form = TimeString.formMinutes(minutes);
        assertEquals("3 минуты", form);
    }
}