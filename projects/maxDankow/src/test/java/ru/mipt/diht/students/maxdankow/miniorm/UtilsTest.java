package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.mipt.diht.students.maxdankow.miniorm.Utils.camelCaseToLowerCase;


public class UtilsTest {
    @Test
    public void camelCaseToLowerCaseTest() {
        assertEquals("to_lower_case", camelCaseToLowerCase("toLowerCase"));
        assertEquals("to_lower12e", camelCaseToLowerCase("toLower12e"));
        assertEquals("a_b_c", camelCaseToLowerCase("aBC"));
        assertEquals("simple", camelCaseToLowerCase("simple"));
    }
}
