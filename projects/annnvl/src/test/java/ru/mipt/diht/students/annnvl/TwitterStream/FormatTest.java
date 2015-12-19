package ru.mipt.diht.students.annnvl.TwitterStream;

import org.junit.Assert;
import org.junit.Test;

public class FormatTest {
    @Test
    public void testFormat() throws Exception {
        Assert.assertEquals("минут", Format.MINUTES[Format.strForm(0)]);
        Assert.assertEquals("минуту", Format.MINUTES[Format.strForm(1)]);
        Assert.assertEquals("минуты", Format.MINUTES[Format.strForm(2)]);
        Assert.assertEquals("минут", Format.MINUTES[Format.strForm(10)]);
        Assert.assertEquals("минуты", Format.MINUTES[Format.strForm(32)]);
        Assert.assertEquals("минут", Format.MINUTES[Format.strForm(55)]);
        Assert.assertEquals("минуту", Format.MINUTES[Format.strForm(121)]);
        Assert.assertEquals("минут", Format.MINUTES[Format.strForm(500)]);
        Assert.assertEquals("минуты", Format.MINUTES[Format.strForm(672)]);
        Assert.assertEquals("минут", Format.MINUTES[Format.strForm(765)]);
        Assert.assertEquals("дней", Format.DAYS[Format.strForm(0)]);
        Assert.assertEquals("день", Format.DAYS[Format.strForm(1)]);
        Assert.assertEquals("дня", Format.DAYS[Format.strForm(2)]);
        Assert.assertEquals("дня", Format.DAYS[Format.strForm(32)]);
        Assert.assertEquals("дней", Format.DAYS[Format.strForm(55)]);
        Assert.assertEquals("день", Format.DAYS[Format.strForm(121)]);
        Assert.assertEquals("дней", Format.DAYS[Format.strForm(500)]);
        Assert.assertEquals("дня", Format.DAYS[Format.strForm(672)]);
        Assert.assertEquals("дней", Format.DAYS[Format.strForm(765)]);
        Assert.assertEquals("часов", Format.HOURS[Format.strForm(0)]);
        Assert.assertEquals("час", Format.HOURS[Format.strForm(1)]);
        Assert.assertEquals("часа", Format.HOURS[Format.strForm(2)]);
        Assert.assertEquals("часа", Format.HOURS[Format.strForm(3)]);
        Assert.assertEquals("часов", Format.HOURS[Format.strForm(5)]);
        Assert.assertEquals("часов", Format.HOURS[Format.strForm(7)]);
        Assert.assertEquals("час", Format.HOURS[Format.strForm(21)]);
        Assert.assertEquals("часа", Format.HOURS[Format.strForm(32)]);
        Assert.assertEquals("часов", Format.HOURS[Format.strForm(55)]);
        Assert.assertEquals("час", Format.HOURS[Format.strForm(121)]);
        Assert.assertEquals("ретвитов",  Format.RETWEETS[Format.strForm(0)]);
        Assert.assertEquals("ретвит", Format.RETWEETS[Format.strForm(1)]);
        Assert.assertEquals("ретвита", Format.RETWEETS[Format.strForm(2)]);
        Assert.assertEquals("ретвита", Format.RETWEETS[Format.strForm(3)]);
        Assert.assertEquals("ретвитов", Format.RETWEETS[Format.strForm(5)]);
        Assert.assertEquals("ретвитов", Format.RETWEETS[Format.strForm(7)]);
        Assert.assertEquals("ретвитов", Format.RETWEETS[Format.strForm(10)]);
        Assert.assertEquals("ретвита", Format.RETWEETS[Format.strForm(32)]);
        Assert.assertEquals("ретвитов",Format.RETWEETS[Format.strForm(55)]);
        Assert.assertEquals("ретвит", Format.RETWEETS[Format.strForm(121)]);
        Assert.assertEquals("ретвитов", Format.RETWEETS[Format.strForm(500)]);
    }
}
