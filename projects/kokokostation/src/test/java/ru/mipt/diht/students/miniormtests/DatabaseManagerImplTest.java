package ru.mipt.diht.students.miniormtests;

import org.junit.Test;
import ru.mipt.diht.students.miniorm.DatabaseManager;
import ru.mipt.diht.students.miniorm.DatabaseManagerImpl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 05.02.16.
 */
public class DatabaseManagerImplTest {
    @Test
    public void test() throws SQLException, ClassNotFoundException, IOException {
        DatabaseManager databaseManager = new DatabaseManagerImpl();

        databaseManager.executeQuery("CREATE TABLE tuz (width INT PRIMARY KEY, height INT)");
        databaseManager.executeQuery("INSERT INTO tuz (width, height) VALUES (1, 2)");

        ResultSet resultSet = databaseManager.executeQueryWithResults("SELECT height FROM tuz WHERE width = 1");
        resultSet.next();

        assertThat(resultSet.getInt("height"), is(2));

        databaseManager.close();

        Files.delete(FileSystems.getDefault().getPath("test.h2.db"));
    }
}