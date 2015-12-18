package ru.mipt.diht.students.simon23rus.TwitterStream;


import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.*;


/**
 * Created by semenfedotov on 06.12.15.

 */


@RunWith(MockitoJUnitRunner.class)
public class TimeTransformerTest extends TestCase {
//    private final List<Integer> deltasInSecondsForRussian = new ArrayList<>();
//    List<Integer> deltasInSecondsForYesterday = new ArrayList<>();


//    @Before
//    public static void setUpDeltasInSecondsForRussian() {
//
//    }
//
//    @Before
//    public static void setUpIsItYesterdayTweet() {
//
//    }
    @Test
    public void testCorrectRussianText() {
        final int SECONDS_IN_MINUTE = 60;
        List<Long> deltasInSeconds = new ArrayList<Long>();
        deltasInSeconds.add(2L);
        deltasInSeconds.add((2L + 2 * SECONDS_IN_MINUTE));
        deltasInSeconds.add((2L + 10 * SECONDS_IN_MINUTE));
        deltasInSeconds.add((2L + 70 * SECONDS_IN_MINUTE));
        deltasInSeconds.add((2L + 60 * 60 * SECONDS_IN_MINUTE));
        deltasInSeconds.add((2L + 200 * 24 * 60 * SECONDS_IN_MINUTE));

        assertEquals("[<Только что>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(0)));
        assertEquals("[<2 минуты назад>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(1)));
        assertEquals("[<10 минут назад>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(2)));
        assertEquals("[<1 час назад>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(3)));
        assertEquals("[<2 дня назад>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(4)));
        assertEquals("[<200 дней назад>] ", TimeTransformer.correctRussianText(deltasInSeconds.get(5)));
    }

    @Test
    public void testIsYesterdayTweet() {
        System.out.println("Testim");
        final int SECONDS_IN_MINUTE = 60;
        List<Date> tweetsDates = new ArrayList<Date>();
        tweetsDates.add(new GregorianCalendar(2015, Calendar.DECEMBER, 12).getTime());
        tweetsDates.add(new GregorianCalendar(2015, Calendar.NOVEMBER, 12).getTime());
        tweetsDates.add(new GregorianCalendar(2015, Calendar.MARCH, 12).getTime());
        tweetsDates.add(new GregorianCalendar(2015, Calendar.DECEMBER, 11).getTime());
        tweetsDates.add(new GregorianCalendar(2015, Calendar.DECEMBER, 10).getTime());
        tweetsDates.add(new GregorianCalendar(1998, Calendar.DECEMBER, 12).getTime());
        tweetsDates.add(new GregorianCalendar(2015, Calendar.DECEMBER, 12).getTime());


        LocalDate today = LocalDate.now();
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(0)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(1)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(2)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(3)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(4)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(5)));
        assertEquals(false, TimeTransformer.isItYesterdayTweet(tweetsDates.get(6)));
    }
}