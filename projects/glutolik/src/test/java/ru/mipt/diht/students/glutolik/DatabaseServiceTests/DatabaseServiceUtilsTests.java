package ru.mipt.diht.students.glutolik.DatabaseServiceTests;

import org.junit.Test;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseServiceUtils;
import ru.mipt.diht.students.glutolik.MiniORM.TColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.mipt.diht.students.glutolik.MiniORM.DatabaseServiceUtils.camelCaseToLowerCase;

/**
 * Created by glutolik on 19.12.15.
 */
public class DatabaseServiceUtilsTests {
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
        List<TColumn> expectedList = new ArrayList<>();
        Field[] fields = Student.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        expectedList.add(new TColumn("FIO", "VARCHAR(255)", fields[0]));
        expectedList.add(new TColumn("group_id", "INTEGER", fields[1]));
        expectedList.add(new TColumn("has_salary", "BOOLEAN", fields[2]));

        List<TColumn> actualList = DatabaseServiceUtils.analyseColumns(Student.class).getKey();
        // Т.к. порядок не ганантирован, то проверяем на равенство без его учета.
        assertTrue(expectedList.containsAll(actualList)
                && actualList.containsAll(expectedList));
    }

    @Test
    public final void getTableNameTest() {
        assertEquals("SiMpLe", DatabaseServiceUtils.getTableName(Simple.class));
        assertEquals("student", DatabaseServiceUtils.getTableName(Student.class));
        assertEquals("double_primary_key", DatabaseServiceUtils.getTableName(DoublePrimaryKey.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getTableNameNoAnnotationTest() {
        assertEquals("utils_test", DatabaseServiceUtils.getTableName(DatabaseServiceUtilsTests.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void doublePrimaryKeyExceptionTest() {
        DatabaseServiceUtils.analyseColumns(DoublePrimaryKey.class);
    }
}
