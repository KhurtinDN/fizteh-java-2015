package ru.mipt.diht.students.miniormtests;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.miniorm.DatabaseManager;
import ru.mipt.diht.students.miniorm.DatabaseService;
import ru.mipt.diht.students.miniorm.DatabaseServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by mikhail on 05.02.16.
 */
public class DatabaseServiceTest {
    DatabaseManager databaseManager;
    DatabaseService<NormalTestClass> testable;

    @Before
    public void setUp() throws DatabaseServiceException, SQLException {
        databaseManager = mock(DatabaseManager.class);
        testable = new DatabaseService<>(NormalTestClass.class, databaseManager);
    }

    @Test
    public void testQueryById() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("testIntegerField")).thenReturn(7);
        when(resultSet.getBoolean("boolean_field")).thenReturn(true);

        when(databaseManager.executeQueryWithResults("SELECT * FROM testTable WHERE boolean_field = true"))
                .thenReturn(resultSet);

        NormalTestClass result = testable.queryById(true);

        assertEquals(new Integer(7), result.integerField);
        assertEquals(new Boolean(true), result.booleanField);
        assertEquals(null, result.writerField);
    }

    @Test(expected = DatabaseServiceException.class)
    public void testBadQueryById() throws Exception {
        testable.queryById(7);
    }


    @Test
    public void testQueryForAll() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("testIntegerField")).thenReturn(7);
        when(resultSet.getBoolean("boolean_field")).thenReturn(true);
        when(resultSet.next()).thenReturn(true, true, false);

        when(databaseManager.executeQueryWithResults("SELECT * FROM testTable"))
                .thenReturn(resultSet);

        List<NormalTestClass> result = testable.queryForAll();

        assertEquals(result.size(), 2);

        for (NormalTestClass item : result) {
            assertEquals(new Integer(7), item.integerField);
            assertEquals(new Boolean(true), item.booleanField);
            assertEquals(null, item.writerField);
        }
    }

    @Test
    public void testInsert() throws Exception {
        NormalTestClass insertable = new NormalTestClass(true, 15);

        testable.insert(insertable);

        verify(databaseManager).executeQuery(
                "INSERT INTO testTable (testIntegerField, boolean_field) VALUES (15, true)");
    }

    @Test(expected = DatabaseServiceException.class)
    public void testBadInsert() throws Exception {
        testable.insert(new NormalTestClass());
    }

    @Test
    public void testUpdate() throws Exception {
        NormalTestClass updatable = new NormalTestClass(true, 15);

        testable.update(updatable);

        verify(databaseManager).executeQuery(
                "UPDATE testTable SET testIntegerField = 15, boolean_field = true WHERE boolean_field = true");
    }

    @Test(expected = DatabaseServiceException.class)
    public void testBadUpdate() throws Exception {
        testable.update(new NormalTestClass());
    }

    @Test
    public void testDelete() throws Exception {
        testable.delete(true);

        verify(databaseManager).executeQuery(
                "DELETE FROM testTable WHERE boolean_field = true");
    }

    @Test(expected = DatabaseServiceException.class)
    public void testBadDelete() throws Exception {
        testable.delete(7);
    }


    @Test
    public void testCreateTable() throws Exception {
        testable.createTable();

        verify(databaseManager).executeQuery(
                "CREATE TABLE IF NOT EXISTS testTable (testIntegerField INT, boolean_field BOOLEAN PRIMARY KEY)");
    }

    @Test
    public void testDropTable() throws Exception {
        testable.dropTable();

        verify(databaseManager).executeQuery(
                "DROP TABLE IF EXISTS testTable");
    }
}