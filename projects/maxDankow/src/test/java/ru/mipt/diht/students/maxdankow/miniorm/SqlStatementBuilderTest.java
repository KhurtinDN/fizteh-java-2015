package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlStatementBuilderTest {
    DatabaseService<Student> studentDBS;
    SqlStatementBuilder<Student> statementBuilder;

    @Before
    public final void initStatementBuilder() {
        studentDBS = new DatabaseService<>(Student.class);
        statementBuilder = studentDBS.getStatementBuilder();
    }

    @Test
    public final void createQueryBuilderTest() {
        assertEquals("CREATE TABLE IF NOT EXISTS student (FIO VARCHAR(255) NOT NULL, group_id INTEGER, has_salary BOOLEAN)",
                statementBuilder.buildCreate());
    }

    @Test
    public final void updateQueryBuilderTest() {
        Student student = new Student("Peter", 999, true);
        assertEquals("UPDATE student SET FIO='Peter', group_id=999, has_salary=true WHERE FIO='Peter'",
                statementBuilder.buildUpdate(student));
    }

    @Test
    public final void insertQueryBuilderTest() {
        Student student = new Student("Alex", 123, false);
        assertEquals("INSERT INTO student VALUES ('Alex', 123, false)",
                statementBuilder.buildInsert(student));
    }
}
