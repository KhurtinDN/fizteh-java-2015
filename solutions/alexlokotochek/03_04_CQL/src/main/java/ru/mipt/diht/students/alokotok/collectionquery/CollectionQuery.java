package ru.mipt.diht.students.alokotok.collectionquery;

import ru.mipt.diht.students.alokotok.collectionquery.impl.Tuple;


import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.mipt.diht.students.alokotok.collectionquery.CollectionQuery.Student.student;
import static ru.mipt.diht.students.alokotok.collectionquery.Sources.list;
import static ru.mipt.diht.students.alokotok.collectionquery.impl.FromStmt.from;

import static ru.mipt.diht.students.alokotok.collectionquery.Conditions.rlike;
import static ru.mipt.diht.students.alokotok.collectionquery.OrderByConditions.desc;

/**
 * Created by lokotochek on 30.11.15.
 */
public class CollectionQuery {

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            InstantiationException {

//        Iterable<Student> statistics =
//                from(list(
//                        student("ivanov", "4"),
//                        student("petrov", "5"),
//                        student("sidorov", "6"),
//                        student("ololov", "7"),
//                        student("mememev", "7")))
//                        .select(Student.class, Student::getName, Student::getGroup)
//                        .where(rlike(Student::getName, ".*ov"))
//                .union()
//                .from(list(
//                        student("hear", "8"),
//                        student("hear", "8"),
//                        student("me", "9"),
//                        student("roar", "10")))
//                        .selectDistinct(Student.class, Student::getName, Student::getGroup)
//                        .orderBy(desc(Student::getName))
//                .execute();
//
//        statistics.forEach(System.out::print);



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
        lovers.forEach(System.out::print);

    }

    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public String getName() {
            return name;
        }

        public String getGroup() {
            return group;
        }

        public Student(String name, LocalDate dateOfBith, String group) {
            this.name = name;
            this.dateOfBith = dateOfBith;
            this.group = group;
        }

        public Student(String name, String group) {
            this.name = name;
            this.dateOfBith = null;
            this.group = group;
        }

        public LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public Double age() {
            return (double) ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }

        public static Student student(String name, String group) {
            return new Student(name, group);
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

        @Override
        public String toString() {
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

    public static class Statistics {

        private final String group;
        private final Integer count;
        private final Double age;

        public String getGroup() {
            return group;
        }

        public Integer getCount() {
            return count;
        }

        public Double getAge() {
            return age;
        }

        public Statistics(String group, Integer count) {
            this.group = group;
            this.count = count;
            this.age = null;
        }

        public Statistics(String group, Integer count, Double age) {
            this.group = group;
            this.count = count;
            this.age = age;
        }

        public Statistics(String group) {
            this.group = group;
            this.count = null;
            this.age = null;
        }

        @Override
        public String toString() {
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