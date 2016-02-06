package ru.mipt.diht.students.twitterstreamtests;

import com.beust.jcommander.ParameterException;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.ArgumentInfo;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 28.01.16.
 */
public class ArgumentInfoTest {
    @Test
    public void testCorrectParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo("-q", "test",
                "--hideRetweets", "-l", "1", "-h");
        assertThat(argumentInfo.getQuery(), is("test"));
        assertThat(argumentInfo.isHideRetweets(), is(true));
        assertThat(argumentInfo.getPlace(), is(""));
        assertThat(argumentInfo.isStream(), is(false));
    }

    @Test (expected = ParameterException.class)
    public void testBadParams() {
        ArgumentInfo argumentInfo = new ArgumentInfo("-q", "test",
                "--hideRetweets", "-l", "1", "--badParameter");
    }
}