package ru.mipt.diht.students.pitovsky.collectionquery.tests;

import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.min;
import static ru.mipt.diht.students.pitovsky.collectionquery.tests.CollectionQueryTest.Student.student;
import static ru.mipt.diht.students.pitovsky.collectionquery.Sources.list;
import static ru.mipt.diht.students.pitovsky.collectionquery.impl.FromStmt.from;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.collectionquery.impl.CollectionQueryExecuteException;

public class CollectionQueryTest extends TestCase {

    @Test
    public void testWhole() {
        Iterable<Statistics> statistics = null;
        try {
            statistics = from(list(
                    student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                    student("petroff", LocalDate.parse("1999-05-08"), "497"),
                    student("testoff", LocalDate.parse("1987-05-08"), "497"),
                    student("sidorov", LocalDate.parse("1996-08-06"), "494"),
                    student("ivanov", LocalDate.parse("1988-08-06"), "493"),
                    student("nobody", LocalDate.parse("1979-05-05"), "497"),
                    student("testman", LocalDate.parse("1987-04-06"), "494"),
                    student("someone", LocalDate.parse("1989-05-06"), "493")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), min(Student::age))
                        .where(s -> s.age() > 20)
                        .groupBy(Student::getGroup)
                        .having(s-> s.getCount() > 1)
                        .orderBy((s1, s2) -> s1.getGroup().compareTo(s2.getGroup()))
                    .execute();
            /*
                    student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                    student("ivanov", LocalDate.parse("1986-08-06"), "494")))
                    .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Student::getGroup), desc(count(Student::getGroup)))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();*/
        } catch (CollectionQueryExecuteException e) {
            fail(e.getMessage());
        }
        assertEquals("[Statistics{group='493', count=2, age=26}, Statistics{group='497', count=2, age=28}]",
                statistics.toString());
    }


    public static class Student {
        private final String name;

        private final LocalDate dateOfBirth;

        private final String group;

        public String getName() {
            return name;
        }

        public Student(String name, LocalDate dateOfBirth, String group) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.group = group;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public String getGroup() {
            return group;
        }

        public long age() {
            return ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now());
        }

        @Override
        public String toString() {
            return "Student " + name + ", " + group + " (" + dateOfBirth + ")";
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

    }


    public static class Statistics {

        private final String group;
        private final Long count;
        private final Long age;

        public String getGroup() {
            return group;
        }

        public Long getCount() {
            return count;
        }

        public Long getAge() {
            return age;
        }

        public Statistics(String group, Long count, Long age) {
            this.group = group;
            this.count = count;
            this.age = age;
        }

        public Statistics(String group) {
            this.group = group;
            this.count = 0L;
            this.age = 18L;
        }

        @Override
        public String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }
    }
}
