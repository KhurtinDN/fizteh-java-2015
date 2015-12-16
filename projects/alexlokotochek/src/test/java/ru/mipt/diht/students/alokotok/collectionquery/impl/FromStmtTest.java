package ru.mipt.diht.students.alokotok.collectionquery.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static ru.mipt.diht.students.alokotok.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.alokotok.collectionquery.OrderByConditions.desc;
import static ru.mipt.diht.students.alokotok.collectionquery.impl.FromStmt.*;
import static ru.mipt.diht.students.alokotok.collectionquery.CollectionQuery.Student.student;
import static ru.mipt.diht.students.alokotok.collectionquery.Sources.list;
import ru.mipt.diht.students.alokotok.collectionquery.CollectionQuery.Student;


/**
 * Created by lokotochek on 16.12.15.
 */
public class FromStmtTest {

    @Test
    public void testFrom() throws Exception {

        List<Student> students = new ArrayList<>();
        String name, group;
        for (int i = 0; i < 1000; ++i) {
            name = "Student-" + i;
            group = "" + (10*i + 1);
            students.add(student(name, group));
        }

        Iterable<Student> statistics =
                from(students)
                        .select(Student.class, Student::getName, Student::getGroup)
                        .execute();

        int iterator = 0;
        for (Student s : statistics) {
            assertEquals(students.get(iterator).toString(), s.toString());
            ++iterator;
        }
    }

    @Test
    public void testSelect() throws Exception {

        List<Student> correctStudents;

        Iterable<Student> statistics =
                from(correctStudents = list(
                        student("ivanov", "4"),
                        student("petrov", "5"),
                        student("sidorov", "6"),
                        student("ololov", "7"),
                        student("mememev", "7")))
                        .select(Student.class, Student::getName, Student::getGroup)
                        .execute();

        int iterator = 0;
        for (Student s : statistics) {
            assertEquals(correctStudents.get(iterator).getName(), s.getName());
            assertEquals(correctStudents.get(iterator).getGroup(), s.getGroup());
            ++iterator;
        }
    }

    @Test
    public void testSelectDistinct() throws Exception {

        List<Student> students = new ArrayList<>();
        String name, group = "0";
        for (int i = 0; i < 1000; ++i) {
            name = "Student-" + (i % 10);
            students.add(student(name, group));
        }

        Iterable<Student> statistics =
                from(students)
                    .selectDistinct(Student.class, Student::getName, Student::getGroup)
                    .execute();

        int iterator = 0;
        for (Student s : statistics) {
            assertEquals("Student-" + iterator, s.getName());
            assertEquals("0", s.getGroup());
            ++iterator;
        }
    }

    @Test
    public void testJoin() throws Exception {
        Iterable<Tuple<String, String>> lovers =
                from(list(
                        student("ivanov", "1"),
                        student("petrov", "2"),
                        student("sidorov", "3")))
                .join(list(
                        student("petrova", "2"),
                        student("ignatova", "3")))
                .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                .select(sg->sg.getFirst().getName(), sg->sg.getSecond().getName())
                .execute();
        for (Tuple t : lovers) {
            if (t.getFirst().toString().equals("petrov")) {
                assertEquals("petrova", t.getSecond().toString());
            }
            if (t.getFirst().toString().equals("sidorov")) {
                assertEquals("ignatova", t.getSecond().toString());
            }
        }
    }

    @Test
    public void testBig() throws Exception {
        Iterable<Student> statistics =
        from(list(
            student("ivanov", "4"),
            student("petrov", "5"),
            student("sidorov", "6"),
            student("ololov", "7"),
            student("mememev", "7")))
            .select(Student.class, Student::getName, Student::getGroup)
            .where(rlike(Student::getName, ".*ov"))
            .union()
            .from(list(
                    student("hear", "8"),
                    student("hear", "8"),
                    student("me", "9"),
                    student("roar", "10")))
            .selectDistinct(Student.class, Student::getName, Student::getGroup)
            .orderBy(desc(Student::getName))
            .execute();
    }
}