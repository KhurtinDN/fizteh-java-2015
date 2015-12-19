package ru.mipt.diht.students.ale3otik.moduletests.miniorm;

import org.junit.Test;
import ru.mipt.diht.students.ale3otik.miniorm.H2DBTypeResolver;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 16.12.15.
 */
public class H2DBTypeResolverTest {
    public class MyTestClass {
    }

    @Test
    public void testClasses() {
        assertThat(H2DBTypeResolver.resolve(Integer.class), equalTo("INTEGER"));
        assertThat(H2DBTypeResolver.resolve(String.class), equalTo("VARCHAR(1000)"));
        assertThat(H2DBTypeResolver.resolve(Double.class), equalTo("DOUBLE"));
        assertThat(H2DBTypeResolver.resolve(Date.class), equalTo("DATE"));
    }

    @Test
    public void testPrimitives() {
        assertThat(H2DBTypeResolver.resolve(int.class), equalTo("INTEGER"));
        assertThat(H2DBTypeResolver.resolve(double.class), equalTo("DOUBLE"));
        assertThat(H2DBTypeResolver.resolve(char.class), equalTo("CHAR"));
    }

    @Test
    public void testFail() {
        assertThat(H2DBTypeResolver.resolve(MyTestClass.class), nullValue());
    }
}