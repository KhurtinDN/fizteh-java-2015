package ru.mipt.diht.students.ale3otik.moduletests.miniorm;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.miniorm.H2DBTypeResolver;

import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created by alex on 16.12.15.
 */
public class H2DBTypeResolverTest extends TestCase {
    public class MyTestClass {
    }
    public void testClasses() {
        assertEquals(H2DBTypeResolver.resolve(Integer.class),"INTEGER");
        assertEquals(H2DBTypeResolver.resolve(String.class), "VARCHAR(1000)");
        assertEquals(H2DBTypeResolver.resolve(Double.class), "DOUBLE");
        assertEquals(H2DBTypeResolver.resolve(Date.class), "DATE");
    }

    @Test
    public void testPrimitives() {
        assertEquals(H2DBTypeResolver.resolve(int.class), "INTEGER");
        assertEquals(H2DBTypeResolver.resolve(double.class),"DOUBLE");
        assertEquals(H2DBTypeResolver.resolve(char.class),"CHAR");
    }


    public void testFail() {
        assertNull(H2DBTypeResolver.resolve(MyTestClass.class));
    }
}