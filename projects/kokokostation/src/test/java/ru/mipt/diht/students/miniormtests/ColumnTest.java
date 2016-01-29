package ru.mipt.diht.students.miniormtests;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.diht.students.miniorm.Column;
import ru.mipt.diht.students.miniorm.DatabaseServiceException;

import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;

/**
 * Created by mikhail on 29.01.16.
 */
public class ColumnTest {
    @Test(expected = DatabaseServiceException.class)
    public void testInvalidIntField() throws NoSuchFieldException, DatabaseServiceException {
        new Column("test", TestClass.class.getField("intField"));
    }

    @Test(expected = DatabaseServiceException.class)
    public void testInvalidWriterField() throws NoSuchFieldException, DatabaseServiceException {
        new Column("test", TestClass.class.getField("writerField"));
    }

    @Test
    public void ValidFields() throws NoSuchFieldException, DatabaseServiceException {
        Column integerColumn = new Column("test", TestClass.class.getField("integerField"));
        Assert.assertEquals(Column.Type.INT, integerColumn.getType());

        Column booleanColumn = new Column("test", TestClass.class.getField("booleanField"));
        Assert.assertEquals(true, booleanColumn.checkIfSuits(new Boolean(false)));
        Assert.assertEquals(false, booleanColumn.checkIfSuits(new LinkedList<>()));

        Column doubleColumn = new Column("test", TestClass.class.getField("doubleField"));
        Assert.assertEquals("12.01", doubleColumn.toSQL(new Double(12.01)));
        Assert.assertEquals("NULL", doubleColumn.toSQL(null));

        Column stringColumn = new Column("test", TestClass.class.getField("stringField"));
        Assert.assertEquals(true, stringColumn.checkIfSuits("test"));
        Assert.assertEquals(false, stringColumn.checkIfSuits(new Integer(5)));
        Assert.assertEquals("\'test\'", stringColumn.toSQL("test"));

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