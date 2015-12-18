package ru.mipt.diht.students.lenazherdeva.moduleTests.twitterStream;
import org.junit.Test;
import ru.mipt.diht.students.lenazherdeva.twitterStream.TimeParser;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

// Created by admin on 09.11.15.
public class TimeParserTest {
   @Test
   public void testFormatTime() throws Exception {
       assertThat(TimeParser.printTime(
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli(),
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(0, 0, 12).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli()), is("23 часа назад"));
       assertThat(TimeParser.printTime(
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(12, 20, 00).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli(),
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(12, 18, 01).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli()), is("только что"));
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
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(12, 20, 01).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli(),
               LocalDate.of(2009, Month.FEBRUARY, 18)
                       .atTime(12, 18, 00).atZone(ZoneId.systemDefault())
                       .toInstant().toEpochMilli()), is("2 минуты назад"));
   }
}
