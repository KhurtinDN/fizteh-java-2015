import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by admin on 05.11.2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class FormatterTest extends TestCase {
    static final int STEPS = 100;
    static final int FORMATTER_STEP = 100;
    @Test
    public void testFormatterHours() throws Exception {
        Map <Integer, String> answers = new HashMap<>();
        answers.put(1, "час");
        answers.put(2, "часа");
        answers.put(5, "часов");
        answers.put(11, "часов");
        answers.put(21, "час");
        answers.put(22, "часа");
        answers.put(26, "часов");
        answers.put(111, "часов");
        answers.put(123, "часа");
        for (Integer value: answers.keySet()) {
            for(int i=0; i < STEPS; ++i) {
                assertThat(Formatter.hours(value + i * FORMATTER_STEP ), is(answers.get(value)));
            }
        }
    }

    public void testFormatterMinutes() throws Exception {
        Map <Integer, String> answers = new HashMap<>();
        answers.put(1, "минуту");
        answers.put(2, "минуты");
        answers.put(5, "минут");
        answers.put(11, "минут");
        answers.put(21, "минуту");
        answers.put(22, "минуты");
        answers.put(26, "минут");
        answers.put(111, "минут");
        answers.put(123, "минуты");
        for (Integer value: answers.keySet()) {
            for(int i=0; i < STEPS; ++i) {
                assertThat(Formatter.minutes(value + i * FORMATTER_STEP), is(answers.get(value)));
            }
        }
    }

    public void testFormatterRetweets() throws Exception {
        Map <Integer, String> answers = new HashMap<>();
        answers.put(1, "ретвит");
        answers.put(2, "ретвита");
        answers.put(5, "ретвитов");
        answers.put(11, "ретвитов");
        answers.put(21, "ретвит");
        answers.put(22, "ретвита");
        answers.put(26, "ретвитов");
        answers.put(111, "ретвитов");
        answers.put(123, "ретвита");
        for (Integer value: answers.keySet()) {
            for(int i=0; i < STEPS; ++i) {
                assertThat(Formatter.retweet(value + i * FORMATTER_STEP ), is(answers.get(value)));
            }
        }
    }
}
