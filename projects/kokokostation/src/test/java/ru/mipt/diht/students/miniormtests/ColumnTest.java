package ru.mipt.diht.students.miniormtests;

import org.junit.Test;
import ru.mipt.diht.students.miniorm.Column;
import ru.mipt.diht.students.miniorm.DatabaseServiceException;

import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by mikhail on 29.01.16.
 */
public class ColumnTest {
    @Test (expected = DatabaseServiceException.class)
    public void testInvalidIntField() throws NoSuchFieldException, DatabaseServiceException {
        new Column("test", TestClass.class.getField("intField"));
    }

    @Test (expected = DatabaseServiceException.class)
    public void testInvalidWriterField() throws NoSuchFieldException, DatabaseServiceException {
        new Column("test", TestClass.class.getField("writerField"));
    }

    @Test
    public void ValidFields() throws NoSuchFieldException, DatabaseServiceException {
        Column integerColumn = new Column("test", TestClass.class.getField("integerField"));
        assertThat(integerColumn.getType(), is(Column.Type.INT));

        Column booleanColumn = new Column("test", TestClass.class.getField("booleanField"));
        assertThat(booleanColumn.checkIfSuits(false), is(true));
        assertThat(booleanColumn.checkIfSuits(new LinkedList<>()), is(false));

        Column doubleColumn = new Column("test", TestClass.class.getField("doubleField"));
        assertThat(doubleColumn.toSQL(12.01), is("12.01"));
        assertThat(doubleColumn.toSQL(null), is("NULL"));

        Column stringColumn = new Column("test", TestClass.class.getField("stringField"));
        assertThat(stringColumn.checkIfSuits("test"), is(true));
        assertThat(stringColumn.checkIfSuits(5), is(false));
        assertThat(stringColumn.toSQL("test"), is("\'test\'"));

        new Column("test", TestClass.class.getField("dateField"));
        new Column("test", TestClass.class.getField("timeField"));
    }

}

class TestClass {
    public int intField;
    public Writer writerField;

    public Integer integerField;
    public Boolean booleanField;
    public Double doubleField;
    public String stringField;
    public Date dateField;
    public Time timeField;
}