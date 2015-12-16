package ru.mipt.diht.students.simon23rus.DatabaseService.TClassAnnotations;

import junit.framework.TestCase;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.rules.JunitRuleImpl;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.simon23rus.CQL.data.CollectionQuery;
import ru.mipt.diht.students.simon23rus.DatabaseService.DatabaseService;
import ru.mipt.diht.students.simon23rus.TwitterStream.TwitterStreamer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by semenfedotov on 16.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class SQLTypeConverterTest extends TestCase {
    List<Class> toTest = new ArrayList<>();

    @Before
    public void setUp() {
        toTest.add(String.class);
        toTest.add(BigDecimal.class);
        toTest.add(boolean.class);
        toTest.add(byte.class);
        toTest.add(short.class);
        toTest.add(int.class);
        toTest.add(Integer.class);
        toTest.add(Long.class);
        toTest.add(long.class);
        toTest.add(float.class);
        toTest.add(double.class);
        toTest.add(Double.class);
        toTest.add(Date.class);
        toTest.add(Time.class);
        toTest.add(Timestamp.class);
        toTest.add(Blob.class);
        toTest.add(Clob.class);
        toTest.add(Array.class);
        toTest.add(AbstractList.class);
        toTest.add(Thread.class);
        toTest.add(Exception.class);
        toTest.add(CollectionQuery.class);
        toTest.add(DatabaseService.class);
        toTest.add(TwitterStreamer.class);
    }

    @Test
    public void convertToSQLTypeTest() {
        assertEquals("VARCHAR(20)", SQLTypeConverter.convertToSQLType(toTest.get(0)));
        assertEquals("NUMERIC", SQLTypeConverter.convertToSQLType(toTest.get(1)));
        assertEquals("BOOLEAN", SQLTypeConverter.convertToSQLType(toTest.get(2)));
        assertEquals("TINYINT", SQLTypeConverter.convertToSQLType(toTest.get(3)));
        assertEquals("SHORTINT", SQLTypeConverter.convertToSQLType(toTest.get(4)));
        assertEquals("INTEGER", SQLTypeConverter.convertToSQLType(toTest.get(5)));
        assertEquals("INTEGER", SQLTypeConverter.convertToSQLType(toTest.get(6)));
        assertEquals("BIGINT", SQLTypeConverter.convertToSQLType(toTest.get(7)));
        assertEquals("BIGINT", SQLTypeConverter.convertToSQLType(toTest.get(8)));
        assertEquals("REAL", SQLTypeConverter.convertToSQLType(toTest.get(9)));
        assertEquals("DOUBLE", SQLTypeConverter.convertToSQLType(toTest.get(10)));
        assertEquals("DOUBLE", SQLTypeConverter.convertToSQLType(toTest.get(11)));
        assertEquals("DATE", SQLTypeConverter.convertToSQLType(toTest.get(12)));
        assertEquals("TIME", SQLTypeConverter.convertToSQLType(toTest.get(13)));
        assertEquals("TIMESTAMP", SQLTypeConverter.convertToSQLType(toTest.get(14)));
        assertEquals("BLOB", SQLTypeConverter.convertToSQLType(toTest.get(15)));
        assertEquals("CLOB", SQLTypeConverter.convertToSQLType(toTest.get(16)));
        assertEquals("ARRAY", SQLTypeConverter.convertToSQLType(toTest.get(17)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(18)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(19)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(20)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(21)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(22)));
        assertEquals("SQL doesn't support your Class", SQLTypeConverter.convertToSQLType(toTest.get(23)));
    }
}
