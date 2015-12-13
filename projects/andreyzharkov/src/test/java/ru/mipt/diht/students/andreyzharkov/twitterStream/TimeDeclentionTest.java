package ru.mipt.diht.students.andreyzharkov.twitterStream;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by Андрей on 12.12.2015.
 */
public class TimeDeclentionTest extends TestCase {
    @Test
    public final void declentionTest() {
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.DAY, 1), "день");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.DAY, 33), "дня");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.DAY, 105), "дней");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.MINUTE, 13), "минут");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.MINUTE, 21), "минута");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.MINUTE, 34), "минуты");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.HOUR, 101), "час");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.HOUR, 72), "часа");
        assertEquals(TimeDeclension.timeInRightForm(TwitterOutputEditor.Time.HOUR, 59), "часов");
    }
}
