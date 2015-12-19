package ru.mipt.diht.students.ale3otik.moduletests.library;

import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.TimeDeterminer;

import java.time.LocalDateTime;
import java.util.Date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
/**
 * Created by alex on 08.11.15.
 */
public class TimeDeterminerTest {
    @Test
    public void testDeterminerDifference() throws Exception {
        LocalDateTime[] dates = new LocalDateTime[] {
                LocalDateTime.of(2014, 9, 12, 10, 30, 10),
                LocalDateTime.of(2014, 9, 12, 10, 29, 10),
                LocalDateTime.of(2014, 9, 12, 10, 20, 10),
                LocalDateTime.of(2014, 9, 12, 8, 20, 10),
                LocalDateTime.of(2014, 9, 11, 23, 20, 10),
                LocalDateTime.of(2014, 9, 1, 23, 20, 10)
        };
        String[] results = new String[5];
        for (int i = 0; i < dates.length - 1; ++i) {
            results[i] = TimeDeterminer.getDifferenceOfDates(dates[i + 1], dates[i]);
        }

        assertThat(results[0], equalTo("только что"));
        assertThat(results[1], equalTo("9 минут назад"));
        assertThat(results[2], equalTo("2 часа назад"));
        assertThat(results[3], equalTo("вчера"));
        assertThat(results[4], equalTo("10 дней назад"));
    }

    @Test
    public void testGettingCurTimeDifference() throws Exception {
        assertThat(TimeDeterminer
                .getTimeDifference(new Date(System.currentTimeMillis())),equalTo("только что"));
    }
}