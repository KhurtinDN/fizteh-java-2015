package ru.mipt.diht.students.pitovsky.collectionquery.tests;

import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.min;
import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.avg;
import static ru.mipt.diht.students.pitovsky.collectionquery.Aggregates.max;
import static ru.mipt.diht.students.pitovsky.collectionquery.tests.CollectionQueryTest.Student.student;
import static ru.mipt.diht.students.pitovsky.collectionquery.Sources.list;
import static ru.mipt.diht.students.pitovsky.collectionquery.impl.FromStmt.from;
import static ru.mipt.diht.students.pitovsky.collectionquery.OrderByConditions.asc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.collectionquery.impl.CollectionQueryExecuteException;
import ru.mipt.diht.students.pitovsky.collectionquery.impl.CollectionQuerySyntaxException;

public class CollectionQueryTest extends TestCase {
    
    /* FIRST:
     * from(list(
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
    @Test
    public void testSubQuery() {
        Iterable<Statistics> statistics = null;
        try {
            statistics = from(from(list(
                            student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                            student("ivanov2", LocalDate.parse("1983-08-06"), "496"),
                            student("petroff", LocalDate.parse("1999-05-08"), "495"),
                            student("petrof2", LocalDate.parse("1982-05-08"), "495"),
                            student("sidorov", LocalDate.parse("1985-06-09"), "497"),
                            student("testoff", LocalDate.parse("1987-05-08"), "497"),
                            student("testof2", LocalDate.parse("1985-05-08"), "497")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), max(Student::age))
                        .where(s -> s.age() > 20)
                        .groupBy(Student::getGroup)
                        .orderBy(asc(Student::getGroup)))
                    .select(Statistics.class, Statistics::getGroup, Statistics::getCount, Statistics::getAge)
                    .where(s -> s.getCount() > 1)
                    .execute();
        } catch (CollectionQueryExecuteException | CollectionQuerySyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals("[Statistics{group='496', count=2, age=32}, Statistics{group='497', count=3, age=30}]",
                statistics.toString());
    }
    
    @Test
    public void testUnion() {
        Iterable<Statistics> statistics = null;
        try {
            statistics = from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                        student("petroff", LocalDate.parse("1999-05-08"), "497"),
                        student("testoff", LocalDate.parse("1987-05-08"), "497"),
                        student("someone", LocalDate.parse("1985-06-07"), "497"),
                        student("sidorov", LocalDate.parse("1996-08-06"), "494"),
                        student("ivanov", LocalDate.parse("1988-08-06"), "493")))
                    .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                    .where(s -> s.age() > 17)
                    .groupBy(Student::getGroup)
                    .orderBy(asc(Student::getGroup))
                .union()
                    .from(list(student("urcoff", LocalDate.parse("1985-07-07"), "494")))
                    .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                .execute();
        } catch (CollectionQueryExecuteException | CollectionQuerySyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals("[Statistics{group='493', count=1, age=27}, Statistics{group='494', count=1, age=19}, "
                + "Statistics{group='496', count=1, age=29}, Statistics{group='497', count=2, age=29}, "
                + "Statistics{group='all', count=1, age=30}]",
                statistics.toString());
    }
    
    @Test
    public void testSimple() {
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
        } catch (CollectionQueryExecuteException | CollectionQuerySyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals("[Statistics{group='493', count=2, age=26}, Statistics{group='497', count=2, age=28}]",
                statistics.toString());
    }
    
    @Test
    public void testWrongSyntax() {
        Iterable<Statistics> statistics = null;
        /*try {
            statistics = from(Sources.set(student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                student("ivanov", LocalDate.parse("1986-08-06"), "494")))
                    .select(Statistics.class, Student::getName)
                    .where(s -> s.age() > 0)
                    .groupBy(Student::age)
                    .execute();
            fail("Exception was not be throwned and get " + statistics.toString());
        } catch (CollectionQueryExecuteException e) { }*/
    }

    @Test
    public void testJoin() {
        Iterable<Statistics> statistics = null;
        try {
            statistics = from(list(
                    student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                    student("petroff", LocalDate.parse("1999-05-08"), "497"),
                    student("testoff", LocalDate.parse("1987-05-08"), "497"),
                    student("sidorov", LocalDate.parse("1991-08-06"), "494"),
                    student("ivanov", LocalDate.parse("1988-08-06"), "493"),
                    student("nobody", LocalDate.parse("1979-05-05"), "497"),
                    student("testman", LocalDate.parse("1987-04-06"), "494"),
                    student("someone", LocalDate.parse("1989-05-06"), "493")))
                    .join(list(new Group("493", "Master3d"), new Group("497", "Master7th")))
                    .on(sg -> sg.getGroup(), sg -> sg.getName())
                        .select(Statistics.class, sg -> sg.first().getGroup(), count(sg -> sg.first().getGroup()),
                                min(sg -> sg.first().age()), sg -> sg.second().getMentor())
                        .where(sg -> sg.first().age() > 20)
                        .groupBy(sg -> sg.first().getGroup())
                        .having(s-> s.getCount() > 1)
                        .orderBy((s1, s2) -> s1.first().getGroup().compareTo(s2.first().getGroup()))
                    .execute();
        } catch (CollectionQueryExecuteException | CollectionQuerySyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals("[Statistics{group='493', count=2, age=26, mentor='Master3d'}, "
                + "Statistics{group='497', count=2, age=28, mentor='Master7th'}]",
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

    public static class Group {
        private final String name;
        private final String mentor;

        public Group(String group, String mentorName) {
            name = group;
            mentor = mentorName;
        }

        public String getName() {
            return name;
        }

        public String getMentor() {
            return mentor;
        }
    }


    public static class Statistics {

        private final String group;
        private final String mentor;
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

        public String getMentor() {
            return mentor;
        }

        public Statistics(String group, Long count, Long age) {
            this.group = group;
            this.count = count;
            this.age = age;
            this.mentor = "none";
        }

        public Statistics(String group, Long count, Long age, String mentor) {
            this.group = group;
            this.count = count;
            this.age = age;
            this.mentor = mentor;
        }

        public Statistics(String group) {
            this.group = group;
            this.count = 0L;
            this.age = 18L;
            this.mentor = "none";
        }

        @Override
        public String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + (mentor.equals("none") ? "" : (", mentor='" + mentor + '\''))
                    + '}';
        }
    }
}
