package ru.mipt.diht.students.ale3otik.moduletests.library;

import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.ConsoleUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 08.11.15.
 */
public class ConsoleUtilTest {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BOLD = "\033[1m";
    private static final String testLine = "this is the test line";

    @Test
    public void testStyle() {
        assertThat(ConsoleUtil.Style.BLUE.line(testLine), equalTo(ANSI_BLUE + testLine + ANSI_RESET));
        assertThat(ConsoleUtil.Style.PURPLE.line(testLine), equalTo(ANSI_PURPLE + testLine + ANSI_RESET));
        assertThat(ConsoleUtil.Style.BOLD.line(testLine), equalTo(ANSI_BOLD + testLine + ANSI_RESET));
    }

    @Test
    public void testGetStdoutConsumer() {
        assertThat(ConsoleUtil.getStdoutConsumer(), notNullValue());
    }
}
