package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import org.junit.Test;

import ru.mipt.diht.students.pitovsky.twitterstream.StringRuUtils;
import junit.framework.TestCase;

public class StringRuUtilsTest extends TestCase {
    @Test
    public final void testGetNumeralWord() {
        assertEquals(StringRuUtils.getNumeralWord("час", 1), "час");
    }
}
