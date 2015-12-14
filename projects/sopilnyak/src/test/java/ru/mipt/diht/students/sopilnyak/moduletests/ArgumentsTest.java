package ru.mipt.diht.students.sopilnyak.moduletests;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.sopilnyak.moduletests.library.Arguments;

import java.io.PrintStream;

import static org.mockito.Mockito.mock;

public class ArgumentsTest {

    PrintStream out;

    @Before
    public void setUp() {
        out = mock(PrintStream.class);
    }

    @Test
    public void testHelp() {
        String[] args = {"--help abcd"};
        Arguments.parse(args);
    }
}
