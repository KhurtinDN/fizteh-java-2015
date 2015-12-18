package ru.mipt.diht.students.lenazherdeva.CQL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static ru.mipt.diht.students.lenazherdeva.CQL.Conditions.*;
import static ru.mipt.diht.students.lenazherdeva.CQL.Sources.list;
import static ru.mipt.diht.students.lenazherdeva.CQL.OrderByConditions.*;
import static ru.mipt.diht.students.lenazherdeva.CQL.Aggregates.*;
import static ru.mipt.diht.students.lenazherdeva.CQL.CQL.Student.student;
import static ru.mipt.diht.students.lenazherdeva.CQL.impl.FromStmt.*;


/**
 * Created by admin on 16.11.2015.
**/
public class CQL {
    @SuppressWarnings("checkstyle:magicnumber")

    public static void main(String[] args) throws Exception {
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
                        .execute();
        System.out.println(statistics);

    }

    public static class Student {
        private final String name;
        private final LocalDate dateOfBirth;
        private final String group;

        public final String getName() {
            return name;
        }

        public Student(String inpName, LocalDate dateOfBith, String inpGroup) {
            this.name = inpName;
            this.dateOfBirth = dateOfBith;
            this.group = inpGroup;
        }

        public Student(String namee, String groupp) {
            this.name = namee;
            this.dateOfBirth = null;
            this.group = groupp;
        }

        public  final LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public final String getGroup() {
            return group;
        }

        public final Double age() {
            return (double) ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now());
        }

        public static Student student(String name, LocalDate dateOfBirth, String group) {
            return new Student(name, dateOfBirth, group);
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String groupp, String mentorr) {
            this.group = groupp;
            this.mentor = mentorr;
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
        private final Integer count;
        private final Double age;

        public final String getGroup() {
            return group;
        }

        public final Integer getCount() {
            return count;
        }


        public Statistics(String inpGroup, Integer inpCount, Double inpAge) {
            this.group = inpGroup;
            this.count = inpCount;
            this.age = inpAge;
        }

        @Override
        public final  String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }
    }
}
