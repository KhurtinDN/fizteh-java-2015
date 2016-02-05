package ru.mipt.diht.students.miniormtests;

import org.junit.Test;

import static org.junit.Assert.*;
import static ru.mipt.diht.students.miniorm.StringProcessor.*;

/**
 * Created by mikhail on 05.02.16.
 */
public class StringProcessorTest {
    @Test
    public void test() {
        assertEquals("test_word", fromCamelCaseToLowerUnderscore("testWord"));
        assertEquals("test_word", fromCamelCaseToLowerUnderscore("TestWord"));

        assertEquals("te", erase2LastLetters("test"));
        assertEquals("", erase2LastLetters("st"));
        assertEquals("", erase2LastLetters("t"));
    }
}