package ru.mipt.diht.students.simon23rus.TwitterStream;


import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import twitter4j.Status;
import twitter4j.User;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by semenfedotov on 06.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TwitterPrinterTest extends TestCase {

    @Test
    public void makeBlueTest() {
        List<String> toPaint = new ArrayList<String>();
        toPaint.add("Снег в лесу закутал елку,");
        toPaint.add("Спрятал елку от ребят.");
        toPaint.add("Ночью елка втихомолку");
        toPaint.add("Пробежала в детский сад.");
        toPaint.add("А у нас в саду веселье,");
        toPaint.add("Пляшет шумный хоровод.");
        toPaint.add("Под молоденькой елью");
        toPaint.add("Мы встречаем Новый год!");
        System.out.println(toPaint);
        assertEquals(((char) 27 + "[34;1m@" + toPaint.get(0)).toString(), TwitterPrinter.makeBlue(toPaint.get(0)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(1)).toString(), TwitterPrinter.makeBlue(toPaint.get(1)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(2)).toString(), TwitterPrinter.makeBlue(toPaint.get(2)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(3)).toString(), TwitterPrinter.makeBlue(toPaint.get(3)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(4)).toString(), TwitterPrinter.makeBlue(toPaint.get(4)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(5)).toString(), TwitterPrinter.makeBlue(toPaint.get(5)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(6)).toString(), TwitterPrinter.makeBlue(toPaint.get(6)));
        assertEquals(((char)27 + "[34;1m@" + toPaint.get(7)).toString(), TwitterPrinter.makeBlue(toPaint.get(7)));


    }

    @Test
    public void printStringWithFormatTest() {
        //testing tweet
        Status tweet = mock(Status.class);
        User user = mock(User.class);
        when(tweet.getCreatedAt()).thenReturn(new GregorianCalendar(2007, Calendar.SEPTEMBER, 7).getTime());
        when(tweet.isRetweet()).thenReturn(false);
        when(tweet.getText()).thenReturn("p^k ili logarithm");
        when(tweet.getRetweetCount()).thenReturn(497);
        when(tweet.getUser()).thenReturn(user);
        when(user.getScreenName()).thenReturn("sobaka");
        Date currentTime = new GregorianCalendar(2007, Calendar.OCTOBER, 22).getTime();
        assertEquals("\u001B[35;1;4m[<45 дней назад>] \u001B[34;1m@sobaka\u001B[0m:p^k ili logarithm\u001B[42m(<497> Ретвитов)\u001B[0m", TwitterPrinter.printStringWithFormat(currentTime, tweet, true));
        assertEquals("\u001B[34;1m@sobaka\u001B[0m:p^k ili logarithm\u001B[42m(<497> Ретвитов)\u001B[0m", TwitterPrinter.printStringWithFormat(currentTime, tweet, false));
    }

}
