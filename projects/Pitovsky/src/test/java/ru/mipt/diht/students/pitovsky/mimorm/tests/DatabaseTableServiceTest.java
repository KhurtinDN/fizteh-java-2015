package ru.mipt.diht.students.pitovsky.mimorm.tests;

import org.junit.Test;

import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.miniorm.Column;
import ru.mipt.diht.students.pitovsky.miniorm.DatabaseTableService;
import ru.mipt.diht.students.pitovsky.miniorm.Table;

public class DatabaseTableServiceTest extends TestCase {

    @Table
    private static class Student {
        @Column
        private String name;
        @Column
        private String group;
        @Column
        private Integer age;

        private Student(String name, String group, int age) {
            this.name = name;
            this.group = group;
            this.age = age;
        }
    }

    @Test
    public void testTableCreation() {
        try {
            DatabaseTableService<Student> service = new DatabaseTableService<>(Student.class);
            service.createTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed: " + e.getMessage());
        }
    }
}
