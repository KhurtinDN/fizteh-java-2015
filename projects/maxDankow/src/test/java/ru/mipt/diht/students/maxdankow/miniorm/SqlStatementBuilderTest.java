package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlStatementBuilderTest {
    DatabaseService<Student> studentDBS;
    SqlStatementBuilder<Student> statementBuilder;

    @Before
    public void initStatementBuilder() {
        studentDBS = new DatabaseService<>(Student.class);
        statementBuilder = studentDBS.getStatementBuilder();
    }

    @Test
    public void createQueryBuilderTest() {
        assertEquals("CREATE TABLE IF NOT EXISTS students (FIO VARCHAR(255) NOT NULL, group_id INTEGER, has_salary BOOLEAN)",
                statementBuilder.buildCreate());
    }

    @Test
    public void updateQueryBuilderTest() {
        Student student = new Student("Peter", 999, true);
        assertEquals("UPDATE students SET FIO='Peter', group_id=999, has_salary=true WHERE FIO='Peter'",
                statementBuilder.buildUpdate(student));
    }

    @Test
    public void insertQueryBuilderTest() {
        Student student = new Student("Alex", 123, false);
        assertEquals("INSERT INTO students VALUES ('Alex', 123, false)",
                statementBuilder.buildInsert(student));
    }
}
