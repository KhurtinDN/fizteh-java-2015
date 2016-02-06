package ru.mipt.diht.students.miniormtests;

import org.junit.Test;

import static ru.mipt.diht.students.miniorm.StringProcessor.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 05.02.16.
 */
public class StringProcessorTest {
    @Test
    public void test() {
        assertThat(fromCamelCaseToLowerUnderscore("testWord"), is("test_word"));
        assertThat(fromCamelCaseToLowerUnderscore("TestWord"), is("test_word"));

        assertThat(erase2LastLetters("test"), is("te"));
        assertThat(erase2LastLetters("st"), is(""));
        assertThat(erase2LastLetters("t"), is(""));
    }
}