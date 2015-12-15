package ru.mipt.diht.students.maxdankow.miniorm;

import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.Column;
import ru.mipt.diht.students.maxdankow.miniorm.DatabaseService.PrimaryKey;
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
        studentsDB.queryForAll();

        Student studentMattew = studentsDB.queryById("Mattew");

        studentMattew.setGroupId(9991);
        studentMattew.setHasSalary(false);
        studentsDB.update(studentMattew);
        studentsDB.delete("Mike");
        studentsDB.queryById("Mike");

        studentsDB.queryForAll();

    }

    @Test
    public void getTableNameTest() {
        assertEquals("simple", Utils.getTableName(Simple.class));
    }

    @Test
    public void getColumnListTest() {
        List<ItemColumn> expectedList = new ArrayList<>();
        try {
            expectedList.add(new ItemColumn("FIO", "VARCHAR(255)", Student.class.getField("name")));
            expectedList.add(new ItemColumn("group_id", "INTEGER", Student.class.getField("groupId")));
            expectedList.add(new ItemColumn("has_salary", "BOOLEAN", Student.class.getField("hasSalary")));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        List<ItemColumn> actualList = Utils.analyseColumns(Student.class).getKey();
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
    @PrimaryKey
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