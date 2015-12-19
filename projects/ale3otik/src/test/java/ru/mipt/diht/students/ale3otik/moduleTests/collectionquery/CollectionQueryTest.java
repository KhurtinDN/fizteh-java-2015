package ru.mipt.diht.students.ale3otik.moduletests.collectionquery;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.collectionquery.impl.CqlException;
import ru.mipt.diht.students.ale3otik.collectionquery.impl.Tuple;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.mipt.diht.students.ale3otik.collectionquery.Aggregates.avg;
import static ru.mipt.diht.students.ale3otik.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.ale3otik.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.ale3otik.collectionquery.OrderByConditions.asc;
import static ru.mipt.diht.students.ale3otik.collectionquery.OrderByConditions.desc;
import static ru.mipt.diht.students.ale3otik.collectionquery.Sources.list;
import static ru.mipt.diht.students.ale3otik.collectionquery.impl.FromStmt.from;
import static ru.mipt.diht.students.ale3otik.moduletests.collectionquery.CollectionQueryTest.Student.student;

public class CollectionQueryTest extends TestCase {

    @Test
    public void testMain() throws CqlException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();
        assertEquals(
                statistics.toString(),
                "[Statistics{group='494', count=1, age=29.0}, Statistics{group='495', count=1, age=29.0}, Statistics{group='all', count=1, age=30.0}]");
//
        Iterable<Tuple<String, String>> mentorsByStudent =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .join(list(new Group("494", "mr.sidorov")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();
        Iterable<Tuple<String, String>> mentorsByStudent2 =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .join(list(new Group("494", "mr.sidorov")))
                        .on(s -> s.getGroup(), g -> g.getGroup())
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();
        assertEquals(mentorsByStudent.toString(), "[Tuple{first=ivanov, second=mr.sidorov}]");
        assertEquals(mentorsByStudent2.toString(), "[Tuple{first=ivanov, second=mr.sidorov}]");
    }


    public static class Student {
        private static LocalDate baseDateTime;

        static {
            baseDateTime = LocalDate.parse("2015-12-19");
        }

        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public String getName() {
            return name;
        }

        public Student(String name, LocalDate dateOfBith, String group) {
            this.name = name;
            this.dateOfBith = dateOfBith;
            this.group = group;
        }

        public LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public String getGroup() {
            return group;
        }

        public long age() {
            return ChronoUnit.YEARS.between(getDateOfBith(), baseDateTime);
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

        @Override
        public String toString() {
            return "{name=" + name + ";date=" + dateOfBith + ";group=" + group + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (!(obj instanceof Student)) {
                return false;
            }
            return Objects.equals(this.toString(), obj.toString());
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

        public String getGroup() {
            return group;
        }

        public Long getCount() {
            return count;
        }

        public Double getAge() {
            return age;
        }

        public Statistics(String group, Long count, Double age) {
            this.group = group;
            this.count = count;
            this.age = age;
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
