package ru.mipt.diht.students.IrinaMudrova.Twitter.library;

import ru.mipt.diht.students.IrinaMudrova.Twitter.library.exceptions.TwitterParameterException;
import org.junit.*;
import org.junit.rules.*;
import junit.framework.Assert;

import static org.junit.Assert.*;

public class TwitterOptionsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHelpOption1() throws TwitterParameterException {
        String[] args1 = {"-h"}, args2 = {"--help"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        assertEquals(opt.isNeedToShowHelp(), true);
        opt = new TwitterOptions().parse(args2);
        assertEquals(opt.isNeedToShowHelp(), true);
    }
    @Test
    public void testHelpOption2() throws TwitterParameterException {
        String[] args = {"-s"};
        TwitterOptions opt = new TwitterOptions().parse(args);
        assertEquals(opt.isNeedToShowHelp(), false);
    }

    @Test
    public void testHelpOption3() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-h", "smth"};
        TwitterOptions opt = new TwitterOptions().parse(args);
        assertEquals(opt.isNeedToShowHelp(), false);
    }

    @Test
    public void testQueryOption1() throws TwitterParameterException {
        String[] args1 = {"-q", "Muuu"}, args2 = {"--query", "Muuu"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        assertEquals(opt.isSetQuery(), true);
        Assert.assertEquals(opt.getQuery(), "Muuu");
        opt = new TwitterOptions().parse(args2);
        Assert.assertEquals(opt.isSetQuery(), true);
        Assert.assertEquals(opt.getQuery(), "Muuu");
    }

    @Test
    public void testQueryOption2() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-q"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testPlaceOption1() throws TwitterParameterException {
        String[] args1 = {"-p", "Barnaul"}, args2 = {"--place", "Barnaul"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        Assert.assertEquals(opt.isSetPlace(), true);
        Assert.assertEquals(opt.getPlace(), "Barnaul");
        opt = new TwitterOptions().parse(args2);
        Assert.assertEquals(opt.isSetPlace(), true);
        Assert.assertEquals(opt.getPlace(), "Barnaul");
    }

    @Test
    public void testPlaceOption2() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-p"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testStreamOption1() throws TwitterParameterException {
        String[] args1 = {"-s"}, args2 = {"--stream"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        Assert.assertEquals(opt.isStreaming(), true);
        opt = new TwitterOptions().parse(args2);
        Assert.assertEquals(opt.isStreaming(), true);
    }

    @Test
    public void testStreamOption2() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-s", "smth"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testHideRetweetsOption1() throws TwitterParameterException {
        String[] args1 = {"--hideRetweets"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        Assert.assertEquals(opt.isHidingRetweets(), true);
    }

    @Test
    public void testHideRetweetsOption2() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"--hideRetweets", "smth"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }


    @Test
    public void testLimitOption1() throws TwitterParameterException {
        String[] args1 = {"-l", "1"}, args2 = {"--limit", "9"};
        TwitterOptions opt;
        opt = new TwitterOptions().parse(args1);
        Assert.assertEquals(opt.isSetLimit(), true);
        Assert.assertEquals(opt.getLimit(), 1);
        opt = new TwitterOptions().parse(args2);
        Assert.assertEquals(opt.isSetLimit(), true);
        Assert.assertEquals(opt.getLimit(), 9);
    }

    @Test
    public void testLimitOption2() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-l", "-7"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testLimitOption3() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-l", "sdf"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testCrossingStreamAndLimitOption() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-l", "10", "-s"};
        TwitterOptions opt = new TwitterOptions().parse(args);
    }

    @Test
    public void testStrangeOption() throws TwitterParameterException {
        thrown.expect(TwitterParameterException.class);
        String[] args = {"-smth"};
        TwitterOptions opt = new TwitterOptions().parse(args);
        Assert.assertEquals(opt.isNeedToShowHelp(), false);
    }
}
