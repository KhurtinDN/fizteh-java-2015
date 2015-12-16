package ru.mipt.diht.students.ale3otik.moduletests.library;

import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.TimeDeterminer;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by alex on 08.11.15.
 */
public class TimeDeterminerTest extends TestCase {
    private LocalDateTime[] dates;

    @BeforeClass
    public void setUp() throws Exception {
        dates = new LocalDateTime[6];
        dates[0] = LocalDateTime.of(2014, 9, 12, 10, 30, 10);
        dates[1] = LocalDateTime.of(2014, 9, 12, 10, 29, 10);
        dates[2] = LocalDateTime.of(2014, 9, 12, 10, 20, 10);
        dates[3] = LocalDateTime.of(2014, 9, 12, 8, 20, 10);
        dates[4] = LocalDateTime.of(2014, 9, 11, 23, 20, 10);
        dates[5] = LocalDateTime.of(2014, 9, 1, 23, 20, 10);

    }

    @Test
    public void testDeterminerDifference() throws Exception {
        String[] results = new String[5];
        for (int i = 0; i < dates.length - 1; ++i) {
            results[i] = TimeDeterminer.getDifferenceOfDates(dates[i + 1], dates[i]);
        }

        assertEquals(results[0], "только что");
        assertEquals(results[1], "9 минут назад");
        assertEquals(results[2], "2 часа назад");
        assertEquals(results[3], "вчера");
        assertEquals(results[4], "10 дней назад");
    }

    @Test
    public void testGettingCurTimeDifference() throws Exception {
        assertEquals(TimeDeterminer.getTimeDifference(new Date(System.currentTimeMillis())),"только что");
    }
}