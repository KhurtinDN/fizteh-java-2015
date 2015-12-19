package ru.mipt.diht.students.annnvl.TwitterStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PrintTimeTest {
    @Test
    public void timePrinterTest() {

        assertThat(PrintTime.printTime(LocalDate.of(2009, Month.MARCH, 20).atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.of(2009, Month.DECEMBER, 21).atTime(0, 0, 0)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), is("только что"));

        assertThat(PrintTime.printTime(LocalDate.of(2009, Month.MARCH, 19).atTime(0, 0, 1)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.of(2009, Month.MARCH, 20).atTime(0, 0, 2)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), is("вчера"));

        assertThat(PrintTime.printTime(LocalDate.of(2009, Month.MARCH, 20).atTime(0, 0, 1)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.of(2009, Month.MARCH, 21).atTime(0, 0, 0)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), is("23 часа назад"));

        assertThat(PrintTime.printTime(LocalDate.of(2009, Month.MARCH, 19).atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.of(2009, Month.MARCH, 21).atTime(0, 0, 0)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), is("2 дня назад"));

        assertThat(PrintTime.printTime(LocalDate.of(2009, Month.MARCH, 19).atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                LocalDate.of(2009, Month.MARCH, 20).atTime(0, 2, 0)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), is("2 минуты назад"));
    }
}
