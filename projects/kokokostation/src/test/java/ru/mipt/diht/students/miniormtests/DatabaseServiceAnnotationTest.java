package ru.mipt.diht.students.miniormtests;

import javafx.util.Pair;
import org.junit.Test;
import ru.mipt.diht.students.miniorm.Column;
import ru.mipt.diht.students.miniorm.DatabaseServiceAnnotations;
import ru.mipt.diht.students.miniorm.DatabaseServiceException;

import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class DatabaseServiceAnnotationTest {
    @Test
    public void testNormalTestClass() throws DatabaseServiceException, NoSuchFieldException {
        DatabaseServiceAnnotations databaseServiceAnnotations = new DatabaseServiceAnnotations(NormalTestClass.class);
        Pair<List<Column>, Column> result = databaseServiceAnnotations.parseType();

        assertEquals("testTable", databaseServiceAnnotations.getTableName());
        assertEquals("testIntegerField", result.getKey().get(0).getName());
        assertEquals("boolean_field", result.getKey().get(1).getName());
        assertEquals(NormalTestClass.class.getField("booleanField"), result.getValue().getField());
        assertEquals(Column.Type.INT, result.getKey().get(0).getType());
        assertEquals(Column.Type.BOOLEAN, result.getKey().get(1).getType());
    }

    @Test(expected = DatabaseServiceException.class)
    public void testFirstInvalidTestClass() throws DatabaseServiceException {
        DatabaseServiceAnnotations databaseServiceAnnotations =
                new DatabaseServiceAnnotations(FirstInvalidTestClass.class);

        assertEquals("ru.mipt.diht.students.miniormtests.first_invalid_test_class",
                databaseServiceAnnotations.getTableName());

        databaseServiceAnnotations.parseType();
    }

    @Test(expected = DatabaseServiceException.class)
    public void testSecondInvalidClass() throws DatabaseServiceException {
        new DatabaseServiceAnnotations(SecondInvalidTestClass.class).parseType();
    }
}

@DatabaseServiceAnnotations.Table
class FirstInvalidTestClass {
    public int intField;
    public Writer writerField;

    @DatabaseServiceAnnotations.Column(name = "testIntegerField")
    public Integer integerField;

    @DatabaseServiceAnnotations.Column
    public Boolean booleanField;
}

@DatabaseServiceAnnotations.Table
class SecondInvalidTestClass {
    public int intField;
    public Writer writerField;

    @DatabaseServiceAnnotations.PrimaryKey
    @DatabaseServiceAnnotations.Column(name = "testIntegerField")
    public Integer integerField;

    @DatabaseServiceAnnotations.PrimaryKey
    @DatabaseServiceAnnotations.Column
    public Boolean booleanField;
}