package ru.mipt.diht.students.ale3otik.moduletests.library;

import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.FormDeclenser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 28.10.15.
 */

public class FormDeclenserTest {
    @Test
    public void testGetTweetsDeclensionForm() {
        assertThat(FormDeclenser.getTweetsDeclension(1), equalTo("ретвит"));
        assertThat(FormDeclenser.getTweetsDeclension(2), equalTo("ретвита"));
        assertThat(FormDeclenser.getTweetsDeclension(4), equalTo("ретвита"));
        assertThat(FormDeclenser.getTweetsDeclension(5), equalTo("ретвитов"));
        assertThat(FormDeclenser.getTweetsDeclension(9), equalTo("ретвитов"));
        assertThat(FormDeclenser.getTweetsDeclension(10), equalTo("ретвитов"));
        assertThat(FormDeclenser.getTweetsDeclension(11), equalTo("ретвитов"));
        assertThat(FormDeclenser.getTweetsDeclension(13), equalTo("ретвитов"));
        assertThat(FormDeclenser.getTweetsDeclension(21), equalTo("ретвит"));
        assertThat(FormDeclenser.getTweetsDeclension(22), equalTo("ретвита"));
        assertThat(FormDeclenser.getTweetsDeclension(104), equalTo("ретвита"));
        assertThat(FormDeclenser.getTweetsDeclension(200101), equalTo("ретвит"));
    }

    @Test
    public void testGetHoursDeclensionForm() {
        assertThat(FormDeclenser.getHoursDeclension(1), equalTo("час"));
        assertThat(FormDeclenser.getHoursDeclension(2), equalTo("часа"));
        assertThat(FormDeclenser.getHoursDeclension(3), equalTo("часа"));
        assertThat(FormDeclenser.getHoursDeclension(5), equalTo("часов"));
        assertThat(FormDeclenser.getHoursDeclension(19), equalTo("часов"));
        assertThat(FormDeclenser.getHoursDeclension(21), equalTo("час"));
        assertThat(FormDeclenser.getHoursDeclension(22), equalTo("часа"));
        assertThat(FormDeclenser.getHoursDeclension(24), equalTo("часа"));
        assertThat(FormDeclenser.getHoursDeclension(25), equalTo("часов"));
        assertThat(FormDeclenser.getHoursDeclension(100), equalTo("часов"));
        assertThat(FormDeclenser.getHoursDeclension(200101), equalTo("час"));
    }

    @Test
    public void testGetDaysDeclensionForm() {
        assertThat(FormDeclenser.getDaysDeclension(1), equalTo("день"));
        assertThat(FormDeclenser.getDaysDeclension(2), equalTo("дня"));
        assertThat(FormDeclenser.getDaysDeclension(5), equalTo("дней"));
        assertThat(FormDeclenser.getDaysDeclension(7), equalTo("дней"));
        assertThat(FormDeclenser.getDaysDeclension(10), equalTo("дней"));
        assertThat(FormDeclenser.getDaysDeclension(13), equalTo("дней"));
        assertThat(FormDeclenser.getDaysDeclension(21), equalTo("день"));
        assertThat(FormDeclenser.getDaysDeclension(25), equalTo("дней"));
        assertThat(FormDeclenser.getDaysDeclension(121), equalTo("день"));
    }

    @Test
    public void testGetMinutesDeclensionForm() {
        assertThat(FormDeclenser.getMinutesDeclension(1), equalTo("минута"));
        assertThat(FormDeclenser.getMinutesDeclension(2), equalTo("минуты"));
        assertThat(FormDeclenser.getMinutesDeclension(5), equalTo("минут"));
        assertThat(FormDeclenser.getMinutesDeclension(110), equalTo("минут"));
        assertThat(FormDeclenser.getMinutesDeclension(21), equalTo("минута"));
        assertThat(FormDeclenser.getMinutesDeclension(27), equalTo("минут"));
        assertThat(FormDeclenser.getMinutesDeclension(23), equalTo("минуты"));
        assertThat(FormDeclenser.getMinutesDeclension(100), equalTo("минут"));
        assertThat(FormDeclenser.getMinutesDeclension(20500), equalTo("минут"));
        assertThat(FormDeclenser.getMinutesDeclension(100501), equalTo("минута"));
        assertThat(FormDeclenser.getMinutesDeclension(1256), equalTo("минут"));
    }
}
