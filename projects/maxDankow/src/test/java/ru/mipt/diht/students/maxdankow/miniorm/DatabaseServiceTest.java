package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


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

    private class Student {
        private String name;
        private int group;
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
