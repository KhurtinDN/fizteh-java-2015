package ru.mipt.diht.students.collectionquerytests;

import javafx.util.Pair;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static ru.mipt.diht.students.collectionquery.Aggregates.avg;
import static ru.mipt.diht.students.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.collectionquery.OrderByConditions.asc;
import static ru.mipt.diht.students.collectionquery.OrderByConditions.desc;
import static ru.mipt.diht.students.collectionquery.Sources.list;
import static ru.mipt.diht.students.collectionquery.impl.FromStmt.from;
import static ru.mipt.diht.students.collectionquerytests.SelectStmtTest.Student.student;

/**
 * Created by mikhail on 03.02.16.
 */
public class SelectStmtTest {
    @Test
    public void dkhurtinTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Iterable<Statistics> statistics =
                from(list(
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
                        .execute();
        assertThat(statistics, hasToString(
                "[Statistics{group='494', count=2, age=29.0}, Statistics{group='all', count=1, age=30.0}]"));

        Iterable<Pair<String, String>> mentorsByStudent =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .join(list(new Group("494", "mr.sidorov")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getKey().getName(), sg -> sg.getValue().getMentor())
                        .execute();
        assertThat(mentorsByStudent, hasToString("[ivanov=mr.sidorov]"));
    }

    @Test
    public void someAnotherTests() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1985-08-06"), "494"),
                        student("petrov", LocalDate.parse("1984-08-06"), "498"),
                        student("sidorov", LocalDate.parse("1996-08-06"), "499"),
                        student("ivan", LocalDate.parse("1983-08-06"), "495")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .union()
                        .from(list(
                                student("ivanov", LocalDate.parse("1985-08-06"), "494"),
                                student("petrov", LocalDate.parse("1986-08-06"), "494")))
                        .selectDistinct(t -> new Statistics(t.getGroup(), 0L, 0.0))
                        .execute();
        assertThat(statistics, hasToString(
                "[Statistics{group='494', count=1, age=30.0}, Statistics{group='498', count=1, age=31.0}, " +
                        "Statistics{group='494', count=0, age=0.0}]"));

        Iterable<String> mentors = from(list(
                student("ivanov", LocalDate.parse("1985-08-06"), "494"),
                student("petrov", LocalDate.parse("1984-08-06"), "498"),
                student("sidorov", LocalDate.parse("1996-08-06"), "499"),
                student("ivan", LocalDate.parse("1983-08-06"), "495")))
                .join(list(
                        new Group("494", "vasya"),
                        new Group("499", "petya"),
                        new Group("491", "dima")))
                .on(t -> Integer.parseInt(t.getGroup()), t -> Integer.parseInt(t.getGroup()))
                .select(t -> t.getValue().getMentor())
                .orderBy(desc(t -> t.getValue().getMentor()))
                .execute();
        assertThat(mentors, hasToString("[vasya, petya]"));

        Iterable<Pair<String, Long>> groups = from(list(
                student("ivanov", LocalDate.parse("1985-08-06"), "494"),
                student("tuzov", LocalDate.parse("1984-08-06"), "494"),
                student("ivanov", LocalDate.parse("1985-08-06"), "1"),
                student("tuzov", LocalDate.parse("1984-08-06"), "1"),
                student("petrov", LocalDate.parse("1984-08-06"), "498"),
                student("petrov", LocalDate.parse("1984-08-06"), "493"),
                student("petrov", LocalDate.parse("1984-08-06"), "493"),
                student("petrov", LocalDate.parse("1984-08-06"), "493"),
                student("sidorov", LocalDate.parse("1996-08-06"), "499"),
                student("ivan", LocalDate.parse("1983-08-06"), "499")))
                .select(Student::getGroup, count(Student::getName))
                .where((Student s) -> !s.getName().equals("ivan"))
                .groupBy(Student::getGroup)
                .having(t -> t.getValue() == 2)
                .orderBy(desc((Student s) -> Integer.parseInt(s.getGroup())))
                .execute();
        assertThat(groups, hasToString("[494=2, 1=2]"));
    }

    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public Student(String name, LocalDate dateOfBith, String group) {
            this.name = name;
            this.dateOfBith = dateOfBith;
            this.group = group;
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

        public String getName() {
            return name;
        }

        public LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public String getGroup() {
            return group;
        }

        public long age() {
            return ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String group, String mentor) {
            this.group = group;
            this.mentor = mentor;
        }

        public String getGroup() {
            return group;
        }

        public String getMentor() {
            return mentor;
        }
    }


    public static class Statistics {

        private final String group;
        private final Long count;
        private final Double age;

        public Statistics(String group, Long count, Double age) {
            this.group = group;
            this.count = count;
            this.age = age;
        }

        public String getGroup() {
            return group;
        }

        public Long getCount() {
            return count;
        }

        public Double getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Statistics) {
                Statistics statistics = (Statistics) obj;
                return group.equals(statistics.group) && count.equals(statistics.count) && age.equals(statistics.age);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return count.intValue();
        }
    }
}
