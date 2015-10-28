package ru.mipt.diht.students.ale3otik.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.FormDeclenser;

/**
 * Created by alex on 28.10.15.
 */

public class FormDeclenserTest extends TestCase {
    @Test
    public void testGetTweetsDeclensionForm() {

        assertEquals(FormDeclenser.getTweetsDeclension(1), "ретвит");
        assertEquals(FormDeclenser.getTweetsDeclension(2), "ретвита");
        assertEquals(FormDeclenser.getTweetsDeclension(4), "ретвита");
        assertEquals(FormDeclenser.getTweetsDeclension(5), "ретвитов");
        assertEquals(FormDeclenser.getTweetsDeclension(9), "ретвитов");
        assertEquals(FormDeclenser.getTweetsDeclension(10), "ретвитов");
        assertEquals(FormDeclenser.getTweetsDeclension(11), "ретвитов");
        assertEquals(FormDeclenser.getTweetsDeclension(13), "ретвитов");
        assertEquals(FormDeclenser.getTweetsDeclension(21), "ретвит");
        assertEquals(FormDeclenser.getTweetsDeclension(22), "ретвита");
        assertEquals(FormDeclenser.getTweetsDeclension(104), "ретвита");
        assertEquals(FormDeclenser.getTweetsDeclension(200101), "ретвит");
    }

    @Test
    public void testGetHoursDeclensionForm() {
        assertEquals(FormDeclenser.getHoursDeclension(1), "час");
        assertEquals(FormDeclenser.getHoursDeclension(2), "часа");
        assertEquals(FormDeclenser.getHoursDeclension(3), "часа");
        assertEquals(FormDeclenser.getHoursDeclension(5), "часов");
        assertEquals(FormDeclenser.getHoursDeclension(19), "часов");
        assertEquals(FormDeclenser.getHoursDeclension(21), "час");
        assertEquals(FormDeclenser.getHoursDeclension(22), "часа");
        assertEquals(FormDeclenser.getHoursDeclension(24), "часа");
        assertEquals(FormDeclenser.getHoursDeclension(25), "часов");
        assertEquals(FormDeclenser.getHoursDeclension(100), "часов");
        assertEquals(FormDeclenser.getHoursDeclension(200101), "час");
    }

    @Test
    public void testGetDaysDeclensionForm() {
        assertEquals(FormDeclenser.getDaysDeclension(1), "день");
        assertEquals(FormDeclenser.getDaysDeclension(2), "дня");
        assertEquals(FormDeclenser.getDaysDeclension(5), "дней");
        assertEquals(FormDeclenser.getDaysDeclension(7), "дней");
        assertEquals(FormDeclenser.getDaysDeclension(10), "дней");
        assertEquals(FormDeclenser.getDaysDeclension(13), "дней");
        assertEquals(FormDeclenser.getDaysDeclension(21), "день");
        assertEquals(FormDeclenser.getDaysDeclension(25), "дней");
        assertEquals(FormDeclenser.getDaysDeclension(121), "день");
    }

    @Test
    public void testGetMinutesDeclensionForm() {
        assertEquals(FormDeclenser.getMinutesDeclension(1), "минута");
        assertEquals(FormDeclenser.getMinutesDeclension(2), "минуты");
        assertEquals(FormDeclenser.getMinutesDeclension(5), "минут");
        assertEquals(FormDeclenser.getMinutesDeclension(110), "минут");
        assertEquals(FormDeclenser.getMinutesDeclension(21), "минута");
        assertEquals(FormDeclenser.getMinutesDeclension(27), "минут");
        assertEquals(FormDeclenser.getMinutesDeclension(23), "минуты");
        assertEquals(FormDeclenser.getMinutesDeclension(100), "минут");
        assertEquals(FormDeclenser.getMinutesDeclension(20500), "минут");
        assertEquals(FormDeclenser.getMinutesDeclension(100501), "минута");
        assertEquals(FormDeclenser.getMinutesDeclension(1256), "минут");
    }
}
