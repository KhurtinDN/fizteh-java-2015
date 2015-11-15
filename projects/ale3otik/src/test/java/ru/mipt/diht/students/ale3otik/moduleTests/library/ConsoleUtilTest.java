package ru.mipt.diht.students.ale3otik.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.ConsoleUtil;

/**
 * Created by alex on 08.11.15.
 */
public class ConsoleUtilTest extends TestCase {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BOLD = "\033[1m";
    private static final String testLine = "this is the test line";

    @Test
    public void testStyle() {
        assertEquals(ConsoleUtil.Style.BLUE.line(testLine), ANSI_BLUE + testLine + ANSI_RESET);
        assertEquals(ConsoleUtil.Style.PURPLE.line(testLine), ANSI_PURPLE + testLine + ANSI_RESET);
        assertEquals(ConsoleUtil.Style.BOLD.line(testLine), ANSI_BOLD + testLine + ANSI_RESET);
    }

    @Test
    public void testGetStdoutConsumer() {
        assert (ConsoleUtil.getStdoutConsumer() != null);
    }
}
