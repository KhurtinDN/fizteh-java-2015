package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.mipt.diht.students.maxdankow.miniorm.Utils.camelCaseToLowerCase;


public class UtilsTest {
    @Test
    public final void camelCaseToLowerCaseTest() {
        assertEquals("to_lower_case", camelCaseToLowerCase("toLowerCase"));
        assertEquals("to_lower12e", camelCaseToLowerCase("toLower12e"));
        assertEquals("a_b_c", camelCaseToLowerCase("ABC"));
        assertEquals("simple", camelCaseToLowerCase("simple"));
        assertEquals("simple", camelCaseToLowerCase("Simple"));
    }

    @Test
    public final void getColumnListTest() {
        List<ItemColumn> expectedList = new ArrayList<>();
        Field[] fields = Student.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        expectedList.add(new ItemColumn("FIO", "VARCHAR(255)", fields[0]));
        expectedList.add(new ItemColumn("group_id", "INTEGER", fields[1]));
        expectedList.add(new ItemColumn("has_salary", "BOOLEAN", fields[2]));

        List<ItemColumn> actualList = Utils.analyseColumns(Student.class).getKey();
        // Т.к. порядок не ганантирован, то проверяем на равенство без его учета.
        assertTrue(expectedList.containsAll(actualList)
                && actualList.containsAll(expectedList));
    }

    @Test
    public final void getTableNameTest() {
        assertEquals("SiMpLe", Utils.getTableName(Simple.class));
        assertEquals("student", Utils.getTableName(Student.class));
        assertEquals("double_primary_key", Utils.getTableName(DoublePrimaryKey.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getTableNameNoAnnotationTest() {
        assertEquals("utils_test", Utils.getTableName(UtilsTest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void doublePrimaryKeyExceptionTest() {
        Utils.analyseColumns(DoublePrimaryKey.class);
    }
}
