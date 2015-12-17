package ru.mipt.diht.students.andreyzharkov.miniORM;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.Column;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.PrimaryKey;
import ru.mipt.diht.students.andreyzharkov.miniORM.annotations.Table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 17.12.2015.
 */
public class DatabaseServiceTest {
    List<TestedClass> input;
    DatabaseService<TestedClass> dataBaseService;

    @Before
    public void setUp() throws DatabaseServiceException {
        dataBaseService = new DatabaseService<>(TestedClass.class);
        if (!dataBaseService.isTableCreated()) {
            dataBaseService.createTable();
        }
        input = new ArrayList<>();
        List<Integer> baseForInput = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            baseForInput.add(i);
        }
        baseForInput.forEach(element -> {
            input.add(new TestedClass(element));
        });
        for (TestedClass element : input) {
            dataBaseService.insert(element);
        }
    }

    @After
    public void tryToDrop() throws DatabaseServiceException {
        if (dataBaseService.isTableCreated()) {
            dataBaseService.dropTable();
        }

    }

    @Test(expected = DatabaseServiceException.class)
    public void testDoubleInsert() throws Exception {
        dataBaseService.insert(input.get(0));
        dataBaseService.insert(input.get(0));
    }

    @Test
    public void testQueryById() throws Exception {
        TestedClass elem = dataBaseService.queryById(1);
        assertThat(elem, equalTo(input.get(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueryByBadKey() throws Exception {
        TestedClass elem = dataBaseService.queryById(1d);
    }

    @Test
    public void testQueryForAll() throws Exception {
        List<TestedClass> newList = dataBaseService.queryForAll();
        assertThat(newList, equalTo(input));
    }

    @Test
    public void testDrop() throws Exception {
        dataBaseService.dropTable();
        assertThat(dataBaseService.isTableCreated(), is(false));
    }

    @Test
    public void testDelete() throws Exception {
        dataBaseService.delete(input.get(0));
        List<TestedClass> newList = dataBaseService.queryForAll();
        assertThat(newList, equalTo(input.subList(1, 10)));
    }

    @Test
    public void testInsert() throws Exception {
        dataBaseService.delete(input.get(0));
        dataBaseService.insert(input.get(0));
        List<TestedClass> newList = dataBaseService.queryForAll();
        assertThat(newList, equalTo(input));
    }

    @Test
    public void testUpdate() throws Exception {
        input.get(0).name = "Test";
        dataBaseService.update(input.get(0));
        List<TestedClass> newList = dataBaseService.queryForAll();
        assertThat(newList, equalTo(input));
    }

    @Table
    public static class Student {
        @PrimaryKey
        @Column(name = "name")
        public final String name;
        @Column(name = "age")
        public Integer age;
        @Column(name = "stud_group")
        public final Integer group;

        public final String getName() {
            return name;
        }

        public Student(String name, int age, int group) {
            this.name = name;
            this.age = age;
            this.group = group;
        }

        public Student() {
            this.name = "";
            this.age = 0;
            this.group = 0;
        }

        public final int getAge() {
            return age;
        }

        public final void setAge(int newAge) {
            age = newAge;
        }

        public final int getGroup() {
            return group;
        }

        public static Student student(String name, int age, int group) {
            return new Student(name, age, group);
        }

        @Override
        public final String toString() {
            return "Student{" + name + "|" + age + "|" + group + "}";
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Student)) {
                return false;
            }
            Student st = (Student) obj;
            return (st.name.equals(name) && age == st.age && group == st.group);
        }
    }

    @Table
    public static class TestedClass {
        @Column
        @PrimaryKey
        public Integer id;

        @Column
        public Short shortNumber;

        @Column
        public Byte byteNumber;

        @Column
        public String name;

        @Column
        public Long longNumber;

        @Column
        public Double doubleNumber;

        @Column
        Float floatNumber;

        @Column
        public Character c;

        public TestedClass(Integer id, Short shortNumber, Byte byteNumber, String name, Long longNumber, Character c,
                           Double doubleNumber, Float floatNumber) {
            this.id = id;
            this.shortNumber = shortNumber;
            this.byteNumber = byteNumber;
            this.name = name;
            this.c = c;
            this.longNumber = longNumber;
            this.doubleNumber = doubleNumber;
            this.floatNumber = floatNumber;
        }

        public TestedClass(Integer i) {
            this(i, i.shortValue(), i.byteValue(), i.toString(),
                    i.longValue(), i.toString().charAt(0), i.doubleValue(), i.floatValue());
        }

        public TestedClass() {
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestedClass)) {
                return false;
            }
            TestedClass another = (TestedClass) obj;
            return id.equals(another.id) && shortNumber.equals(another.shortNumber)
                    && byteNumber.equals(another.byteNumber) && name.equals(another.name)
                    && longNumber.equals(another.longNumber);
        }
    }

}
