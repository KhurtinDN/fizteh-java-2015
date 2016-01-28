package ru.mipt.diht.students.twitterstreamtests;

import com.beust.jcommander.ParameterException;
import org.junit.Assert;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.ArgumentInfo;

/**
 * Created by mikhail on 28.01.16.
 */
public class ArgumentInfoTest {
    @Test
    public void testCorrectParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test",
                "--hideRetweets", "-l", "1", "-h"});
        Assert.assertEquals(argumentInfo.getQuery(), "test");
        Assert.assertEquals(argumentInfo.isHideRetweets(), true);
        Assert.assertEquals(argumentInfo.getPlace(), "");
        Assert.assertEquals(argumentInfo.isStream(), false);
    }

    @Test(expected = ParameterException.class)
    public void testBadParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test",
                "--hideRetweets", "-l", "1", "--badParameter"});
    }
}
