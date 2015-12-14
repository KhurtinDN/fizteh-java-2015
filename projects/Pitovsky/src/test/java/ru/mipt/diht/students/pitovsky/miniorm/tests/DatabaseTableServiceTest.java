package ru.mipt.diht.students.pitovsky.miniorm.tests;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.miniorm.Column;
import ru.mipt.diht.students.pitovsky.miniorm.DatabaseTableService;
import ru.mipt.diht.students.pitovsky.miniorm.PrimaryKey;
import ru.mipt.diht.students.pitovsky.miniorm.Table;

public class DatabaseTableServiceTest extends TestCase {

    @Table
    public static class Student {
        @Column
        @PrimaryKey
        private String name;
        @Column(name="studgroup")
        private String group;
        @Column
        private Integer age;

        public Student() {}

        private Student(String name, String group, int age) {
            this.name = name;
            this.group = group;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "Student{" + name + "|" + group + "|" + age + "}";
        }
    }

    @Test
    public void testTableCreation() {
        try {
            DatabaseTableService<Student> service = new DatabaseTableService<>(Student.class);
            service.createTable();
            service.dropTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed: " + e.getMessage());
        }
    }

    @Test
    public void testRowOperations() {
        try {
            DatabaseTableService<Student> service = new DatabaseTableService<>(Student.class);
            service.createTable();
            service.insert(new Student("ivanov", "497", 18));
            service.insert(new Student("petrov", "497", 18));
            service.insert(new Student("sidorov", "497", 19));
            service.update(new Student("petrov", "496", 18));
            service.delete(new Student("sidorov", "498", 30));
            
            assertNull(service.queryById("sidorov"));
            assertEquals("[Student{ivanov|497|18}, Student{petrov|496|18}]", service.queryForAll().toString());
            service.dropTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed: " + e.getMessage());
        }
    }
}
