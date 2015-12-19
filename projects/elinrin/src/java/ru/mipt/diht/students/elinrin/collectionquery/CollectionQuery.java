package ru.mipt.diht.students.elinrin.collectionquery;

import ru.mipt.diht.students.elinrin.collectionquery.impl.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.mipt.diht.students.elinrin.collectionquery.Aggregates.avg;
import static ru.mipt.diht.students.elinrin.collectionquery.Aggregates.count;
import static ru.mipt.diht.students.elinrin.collectionquery.CollectionQuery.Student.student;
import static ru.mipt.diht.students.elinrin.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.elinrin.collectionquery.OrderByConditions.asc;
import static ru.mipt.diht.students.elinrin.collectionquery.OrderByConditions.desc;
import static ru.mipt.diht.students.elinrin.collectionquery.Sources.list;
import static ru.mipt.diht.students.elinrin.collectionquery.impl.FromStmt.from;

public class CollectionQuery {
    static final int TWENTY = 20;
    static final int HUNDRED = 100;
    /**
     * Make this code work!
     *
     * @param args
     */
    public static void main(final String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "496"),
                        student("ivanov", LocalDate.parse("1986-08-06"), "496")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > TWENTY))
                        .groupBy(Student::getName)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(count(Statistics::getGroup)))
                        .limit(HUNDRED)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "496")))
                        .selectDistinct(Statistics.class, s -> "all",
                                count(Student::getDateOfBith), avg(Student::age))
                        .groupBy(Student::getGroup)
                        .execute();
        statistics.forEach(System.out::print);

        /*List<Student> ex = new ArrayList<>();
        ex.add(student("ivanov", LocalDate.parse("1986-08-06"), "496"));
        ex.add(student("petrov", LocalDate.parse("1986-08-06"), "495"));
        ex.add(student("sidorov", LocalDate.parse("1986-08-06"), "495"));
        ex.add(student("ivanov", LocalDate.parse("1986-08-06"), "495"));
        ex.sort(asc(Student::getGroup));
        System.out.println(ex);*/

        /*Iterable<Student> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("zvereva", LocalDate.parse("1986-08-06"), "494"),
                        student("zuev", LocalDate.parse("1976-08-06"), "494"),
                        student("petrov", LocalDate.parse("1986-08-06"), "495"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("1986-08-06"), "495"),
                        student("garkavyy", LocalDate.parse("1986-08-06"), "495"),
                        student("ivanov", LocalDate.parse("1989-08-06"), "494")))
                        .select(Student.class, Student::getName, Student::getGroup)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                                student("zvereva", LocalDate.parse("1986-08-06"), "494"),
                                student("ivanov", LocalDate.parse("1986-08-06"), "494")))
                                .select(Student.class, Student::getName, Student::getGroup)
                                .execute();*/
        //Tuple a = new Tuple("1", "2");
        //System.out.println(a.getFirst().getClass());
        //System.out.println(a.getClass());

        Iterable<Tuple<String, String>> mentorsByStudent =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "496"),
                            student("sidorov", LocalDate.parse("1986-08-06"), "497"),
                            student("vasilev", LocalDate.parse("1986-08-06"), "496"),
                            student("petrov", LocalDate.parse("1986-08-06"), "497")))
                        .join(list(Group.group("497", "mr.solovev")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .where(s -> Objects.equals(s.getFirst().getName(), "sidorov"))
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "496"),
                                student("sidorov", LocalDate.parse("1986-08-06"), "497"),
                                student("vasilev", LocalDate.parse("1986-08-06"), "496"),
                                student("petrov", LocalDate.parse("1986-08-06"), "497")))
                        .join(list(Group.group("496", "mr.ilanov")))
                        .on(s -> s.getGroup(), f -> f.getGroup())
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();
        mentorsByStudent.forEach(System.out::print);
    }

    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public final String getName() {
            return name;
        }

        /*public Student(String group) {
            this.name = null;
            this.dateOfBith = null;
            this.group = group;
        }*/

        public Student(final String gottenName, final LocalDate gottenDateOfBith, final String gottenGroup) {
            name = gottenName;
            dateOfBith = gottenDateOfBith;
            group = gottenGroup;
        }

        public Student(final String gottenName, final String gottenGroup) {
            name = gottenName;
            dateOfBith = null;
            group = gottenGroup;
        }

        public final LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public final String getGroup() {
            return group;
        }

        public final Double age() {
            return (double) ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }

        public static Student student(final String name, final LocalDate dateOfBith, final String group) {
            return new Student(name, dateOfBith, group);
        }

        @Override
        public final String toString() {
            StringBuilder result = new StringBuilder().append("Student{");
            if (group != null) {
                result.append("group='").append(group).append('\'');
            }
            if (name != null) {
                result.append(", name=").append(name);
            }
            if (dateOfBith != null) {
                result.append(", age=").append(dateOfBith);
            }
            result.append("}\n");
            return result.toString();
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(final String gottenGroup, final String gottenMentor) {
            group = gottenGroup;
            mentor = gottenMentor;
        }

        public final String getGroup() {
            return group;
        }

        public final String getMentor() {
            return mentor;
        }

        public static Group group(final String ggroup, final String mmentor) {
            return new Group(ggroup, mmentor);
        }

        @Override
        public final String toString() {
            StringBuilder result = new StringBuilder().append("Student{");
            if (group != null) {
                result.append("group='").append(group).append('\'');
            }
            if (mentor != null) {
                result.append(", name=").append(mentor);
            }
            result.append("}\n");
            return result.toString();
        }
    }


    public static class Statistics {

        private final String group;
        private final Integer count;
        private final Double age;

        public final String getGroup() {
            return group;
        }

        public final Integer getCount() {
            return count;
        }

        public final Double getAge() {
            return age;
        }

        public Statistics(final String gottenGroup, final Integer gottenCount) {
            group = gottenGroup;
            count = gottenCount;
            age = null;
        }

        public Statistics(final String gottenGroup, final Integer gottenCount, final Double gottenAge) {
            group = gottenGroup;
            count = gottenCount;
            age = gottenAge;
        }

        public Statistics(final String gottenGroup) {
            group = gottenGroup;
            count = null;
            age = null;
        }

        @Override
        public final String toString() {
            StringBuilder result = new StringBuilder().append("Statistics{");
            if (group != null) {
                result.append("group='").append(group).append('\'');
            }
            if (count != null) {
                result.append(", count=").append(count);
            }
            if (age != null) {
                result.append(", age=").append(age);
            }
            result.append("}\n");
            return result.toString();
        }
    }

}
