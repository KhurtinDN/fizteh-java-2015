package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.Column;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class DatabaseServiceTest {
    private List<Student> students = new ArrayList<>();

    @Before
    public void initStudents() {
        students.add(new Student("Peter", 497, false));
        students.add(new Student("Mike", 499, true));
        students.add(new Student("Xander", 499, true));
        students.add(new Student("Lexa", 497, true));
        students.add(new Student("Nick", 495, true));
        students.add(new Student("Mattew", 497, true));
    }

    @Test
    public void test() {
        DatabaseService<Student> studentsDB = new DatabaseService<>(Student.class);
    }

    @Test
    public void createQueryBuilderTest() {
        DatabaseService<Student> studentDatabaseService = new DatabaseService<>(Student.class);
        assertEquals("CREATE TABLE IF NOT EXISTS students (FIO VARCHAR(255), group INTEGER, has_salary BOOLEAN)",
                studentDatabaseService.buildCreateQuery());
    }

    @Table(name = "students")
    private class Student {

        @Column(name = "FIO", type = "VARCHAR(255)")
        private String name;

        @Column(type = "INTEGER")
        private int group;

        @Column(type = "BOOLEAN")
        private boolean hasSalary;

        public Student(String name, int group, boolean hasSalary) {
            this.name = name;
            this.group = group;
            this.hasSalary = hasSalary;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public boolean isHasSalary() {
            return hasSalary;
        }

        public void setHasSalary(boolean hasSalary) {
            this.hasSalary = hasSalary;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", group=" + group +
                    ", hasSalary=" + hasSalary +
                    '}';
        }
    }
}
