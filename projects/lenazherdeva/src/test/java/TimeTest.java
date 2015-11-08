/*
import org.junit.Test;
import java.time.Month;
import java.time.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
/**
 * Created by admin on 09.11.15.

public class TimeTest {
    @Test
    public void testFormatTime() throws Exception {
        assertThat(TimeParser.printTime(
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(0, 0, 12).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()), is("23 часа назад"));
        assertThat(TimeParser.printTime(
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(12, 20, 00).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(12, 18, 01).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()), is("Только что"));
        assertThat(TimeParser.printTime(
                LocalDate.of(2012, Month.MARCH, 01)
                        .atTime(12, 20, 00).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                LocalDate.of(2012, Month.FEBRUARY, 28)
                        .atTime(9, 18, 01).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()), is("2 дня назад"));

        assertThat(TimeParser.printTime(
                LocalDate.of(2013, Month.MARCH, 01)
                        .atTime(12, 20, 00).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                LocalDate.of(2013, Month.FEBRUARY, 28)
                        .atTime(9, 18, 01).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()), is("вчера"));

        assertThat(TimeParser.printTime(
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(12, 20, 01).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                LocalDate.of(1997, Month.FEBRUARY, 18)
                        .atTime(12, 18, 00).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()), is("2 минуты назад"));
    }
}
*/