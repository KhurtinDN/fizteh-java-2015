package ru.mipt.diht.students.tveritinova.moduletests.library;

import com.beust.jcommander.JCommander;
import junit.framework.Assert;
import org.junit.Test;
import ru.mipt.diht.students.tveritinova.TwitterStream.MyJCommander;

public class MyJCommanderTest {
    MyJCommander mjc = new MyJCommander();
    String[] args;

    @Test
    public void test() {
        args = new String[]{"--help"
                , "-s"
                , "-q", "abc"
                , "-p", "Moscow"
                , "--hideRetweets"
                , "-l", "20"};
        new JCommander(mjc, args);
        Assert.assertTrue(mjc.getIsHelp());
        Assert.assertTrue(mjc.getIsStream());
        Assert.assertEquals("abc", mjc.getQuery());
        Assert.assertEquals("Moscow", mjc.getLocation());
        Assert.assertTrue(mjc.getIsHideRetweets());
        Assert.assertEquals(20, mjc.getLimit());
    }
}
