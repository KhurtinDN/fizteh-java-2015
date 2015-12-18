package ru.mipt.diht.students.sopilnyak.miniorm;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class H2TypeTest {
    public class SomeClass {
    }

    @Test
    public void testResolveType() {
        assertEquals(H2Type.resolveType(int.class), "INTEGER");
        assertEquals(H2Type.resolveType(Date.class), "DATE");
        assertNull(H2Type.resolveType(SomeClass.class));
    }
}
