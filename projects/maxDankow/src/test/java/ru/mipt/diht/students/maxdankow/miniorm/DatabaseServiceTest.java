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
        studentsDB.dropTable();
        studentsDB.createTable();
//        studentsDB.dropTable();
    }

    @Test
    public void createQueryBuilderTest() {
        DatabaseService<Student> studentDatabaseService = new DatabaseService<>(Student.class);
        assertEquals("CREATE TABLE IF NOT EXISTS students (FIO VARCHAR(255), group_id INTEGER, has_salary BOOLEAN)",
                studentDatabaseService.createStatementBuilder());
    }

    @Table(name = "students")
    private class Student {

        @Column(name = "FIO", type = "VARCHAR(255)")
        private String name;

        @Column(type = "INTEGER")
        private int groupId;

        @Column(type = "BOOLEAN")
        private boolean hasSalary;

        public Student(String name, int groupId, boolean hasSalary) {
            this.name = name;
            this.groupId = groupId;
            this.hasSalary = hasSalary;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
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
                    ", groupId=" + groupId +
                    ", hasSalary=" + hasSalary +
                    '}';
        }
    }
}
