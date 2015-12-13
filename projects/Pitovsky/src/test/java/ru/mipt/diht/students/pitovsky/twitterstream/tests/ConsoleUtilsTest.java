package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import org.junit.Test;

import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils;
import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils.TextColor;
import junit.framework.TestCase;

public class ConsoleUtilsTest extends TestCase {
    private static char ESCAPE = (char)27;
    
    @Test
    public final void testColorizeString() {
        assertEquals("" + ESCAPE + "[32mSTRING" + ESCAPE + "[0m",
                ConsoleUtils.colorizeString("STRING", TextColor.GREEN));
    }
}
