package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import org.junit.Test;
import junit.framework.TestCase;

import ru.mipt.diht.students.pitovsky.twitterstream.StringRuUtils;

public class StringRuUtilsTest extends TestCase {
    @Test
    public final void testGetNumeralWord() {
        assertEquals("час", StringRuUtils.getNumeralWord("час", 1));
        assertEquals("дня", StringRuUtils.getNumeralWord("день", 3));
        assertEquals("минут", StringRuUtils.getNumeralWord("минута", 25));
        assertEquals("дней", StringRuUtils.getNumeralWord("день", 112));
        assertEquals("ретвитов", StringRuUtils.getNumeralWord("ретвит", 12));
    }
    
    @Test
    public final void testGetNumeralsAgo() {
        assertEquals("getNumeralsAgo failed, ", "5 минут назад", StringRuUtils.getNumeralsAgo("минута", 5));
    }
}
