package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.Column;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
        for (Student student : students) {
            studentsDB.insert(student);
        }
        try {
            List<Student> sqlStudents = studentsDB.queryForAll();
            System.err.println(sqlStudents);
//            assert false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
//        studentsDB.dropTable();
    }

    @Test
    public void createQueryBuilderTest() {
        DatabaseService<Student> studentDatabaseService = new DatabaseService<>(Student.class);
        assertEquals("CREATE TABLE IF NOT EXISTS students (FIO VARCHAR(255), group_id INTEGER, has_salary BOOLEAN)",
                studentDatabaseService.buildCreateStatement());
    }

    @Test
    public void insertQueryBuilderTest() {
        DatabaseService<Student> studentDatabaseService = new DatabaseService<>(Student.class);
        Student student = new Student("Alex", 123, false);
        assertEquals("INSERT INTO students VALUES ('Alex', 123, false)",
                studentDatabaseService.buildInsertStatement(student));
    }

    @Test
    public void getTableNameTest() {
        DatabaseService<Simple> simpleDatabaseService = new DatabaseService<>(Simple.class);
        assertEquals("simple", simpleDatabaseService.getTableName());
    }

    @Test
    public void getColumnListTest() {
        DatabaseService<Student> studentDatabaseService = new DatabaseService<>(Student.class);
        List<ItemColumn> expectedList = new ArrayList<>();
        try {
            expectedList.add(new ItemColumn("FIO", "VARCHAR(255)", Student.class.getField("name")));
            expectedList.add(new ItemColumn("group_id", "INTEGER", Student.class.getField("groupId")));
            expectedList.add(new ItemColumn("has_salary", "BOOLEAN", Student.class.getField("hasSalary")));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        List<ItemColumn> actualList = studentDatabaseService.getColumnList();
        // Т.к. порядок не ганантирован, то проверяем на равенство без его учета.
        assertTrue(expectedList.containsAll(actualList)
                && actualList.containsAll(expectedList));
    }

//    @Test(expected = IllegalStateException.class)
//    public void doubleDropTest() {
//        DatabaseService<Simple> simpleDS = new DatabaseService<>(Simple.class);
//        simpleDS.dropTable();
//        simpleDS.dropTable();
//    }


    @Table
    private class Simple {
        @Column(type = "INTEGER")
        public int number;
    }
}

@Table(name = "students")
class Student {

    @Column(name = "FIO", type = "VARCHAR(255)")
    public String name;

    @Column(type = "INTEGER")
    public int groupId;

    @Column(type = "BOOLEAN")
    public boolean hasSalary;

    public Student() {
        name = null;
        groupId = 0;
        hasSalary = false;
    }

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