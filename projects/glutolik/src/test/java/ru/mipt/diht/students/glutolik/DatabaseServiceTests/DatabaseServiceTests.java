package ru.mipt.diht.students.glutolik.DatabaseServiceTests;

import org.junit.Before;
import org.junit.Test;

import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.Column;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.PrimaryKey;
import ru.mipt.diht.students.glutolik.MiniORM.DatabaseService.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by glutolik on 19.12.15.
 */
public class DatabaseServiceTests {
    private List<Student> students = new ArrayList<>();

    @Before
    public final void initStudents() {
        students.add(new Student("Peter", 497, false));
        students.add(new Student("Mike", 499, true));
        students.add(new Student("Xander", 499, true));
        students.add(new Student("Lexa", 497, true));
        students.add(new Student("Nick", 495, true));
        students.add(new Student("Mattew", 497, true));
    }

    @Test
    public final void sequenceTest() {
        DatabaseService<Student> studentsDB = new DatabaseService<>(Student.class);
        studentsDB.dropTable();
        studentsDB.createTable();
        for (Student student : students) {
            studentsDB.insert(student);
        }
        assertEquals(students, studentsDB.queryForAll());

        Student studentMattew = studentsDB.queryById("Mattew");
        assertEquals(new Student("Mattew", 497, true), studentsDB.queryById("Mattew"));

        studentMattew.setGroupId(9991);
        studentMattew.setHasSalary(false);
        studentsDB.update(studentMattew);
        assertEquals(new Student("Mattew", 9991, false), studentsDB.queryById("Mattew"));

        studentsDB.delete("Mike");
        assertEquals(null, studentsDB.queryById("Mike"));
        studentsDB.queryForAll();
    }

}

@Table(name = "SiMpLe")
class Simple {
    @Column(type = "INTEGER")
    private int number;
}

@Table
class DoublePrimaryKey {
    @Column(type = "INTEGER")
    @PrimaryKey
    private int firstKey;

    @Column(type = "INTEGER")
    @PrimaryKey
    private int secondKey;

}

@Table
class Student {

    @Column(name = "FIO", type = "VARCHAR(255)")
    @PrimaryKey
    private String name;

    @Column(type = "INTEGER")
    private int groupId;

    @Column(type = "BOOLEAN")
    private boolean hasSalary;

    Student() {
        name = null;
        groupId = 0;
        hasSalary = false;
    }

    Student(String newName, int newGroupId, boolean newHasSalary) {
        this.name = newName;
        this.groupId = newGroupId;
        this.hasSalary = newHasSalary;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int newGroupId) {
        this.groupId = newGroupId;
    }

    public boolean isHasSalary() {
        return hasSalary;
    }

    public void setHasSalary(boolean newHasSalary) {
        this.hasSalary = newHasSalary;
    }

    @Override
    public String toString() {
        return "Student{"
                + "name='" + name + '\''
                + ", groupId=" + groupId
                + ", hasSalary=" + hasSalary
                + '}';
    }

    // Из-за CheckStyle =(
    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Student other = (Student) obj;
        return this.name.equals(other.name)
                && this.groupId == other.groupId
                && this.hasSalary == other.hasSalary;
    }
}

