package ru.mipt.diht.students.andreyzharkov.collectionquery;

import ru.mipt.diht.students.andreyzharkov.collectionquery.impl.EmptyCollectionException;
import ru.mipt.diht.students.andreyzharkov.collectionquery.impl.QueryExecuteException;
import ru.mipt.diht.students.andreyzharkov.collectionquery.impl.Tuple;
import ru.mipt.diht.students.andreyzharkov.collectionquery.impl.UnequalUnionClassesException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.mipt.diht.students.andreyzharkov.collectionquery.Aggregates.avg;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.CollectionQuery.Student.student;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.OrderByConditions.asc;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.OrderByConditions.desc;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.Sources.list;
import static ru.mipt.diht.students.andreyzharkov.collectionquery.impl.FromStmt.from;

public class CollectionQuery {

    /**
     * Make this code work!
     *
     * @param args
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static void main(String[] args) throws QueryExecuteException,
            UnequalUnionClassesException, EmptyCollectionException {
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
        System.out.println(statistics);

        Iterable<Tuple<String, String>> mentorsByStudent =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .join(list(new Group("494", "mr.sidorov")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();
        System.out.println(mentorsByStudent);
    }


    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public final String getName() {
            return name;
        }

        public Student(String nme, LocalDate datOfBith, String grop) {
            this.name = nme;
            this.dateOfBith = datOfBith;
            this.group = grop;
        }

        public final LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public final String getGroup() {
            return group;
        }

        public final long age() {
            return ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String grop, String mentr) {
            this.group = grop;
            this.mentor = mentr;
        }

        public final String getGroup() {
            return group;
        }

        public final String getMentor() {
            return mentor;
        }
    }


    public static class Statistics {

        private final String group;
        private final Long count;
        private final Long age;

        public final String getGroup() {
            return group;
        }

        public final Long getCount() {
            return count;
        }

        public final Long getAge() {
            return age;
        }

        public Statistics(String grop, Long cnt, Long ag) {
            this.group = grop;
            this.count = cnt;
            this.age = ag;
        }

        @Override
        public final String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }
    }

}
