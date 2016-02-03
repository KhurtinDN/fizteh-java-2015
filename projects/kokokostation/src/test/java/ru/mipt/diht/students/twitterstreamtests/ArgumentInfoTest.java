package ru.mipt.diht.students.twitterstreamtests;

import com.beust.jcommander.ParameterException;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.ArgumentInfo;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 28.01.16.
 */
public class ArgumentInfoTest {
    @Test
    public void testCorrectParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test",
                "--hideRetweets", "-l", "1", "-h"});
        assertEquals(argumentInfo.getQuery(), "test");
        assertEquals(argumentInfo.isHideRetweets(), true);
        assertEquals(argumentInfo.getPlace(), "");
        assertEquals(argumentInfo.isStream(), false);
    }

    @Test (expected = ParameterException.class)
    public void testBadParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test",
                "--hideRetweets", "-l", "1", "--badParameter"});
    }
}
