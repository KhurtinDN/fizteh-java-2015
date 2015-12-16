package ru.mipt.diht.students.alokotok.collectionquery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by lokotochek on 30.11.15.
 */
public class CollectionQuery {

    public static void main(String[] args) {
    }

    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public final String getName() {
            return name;
        }

        public final String getGroup() {
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

        public final LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public final Double age() {
            return (double) ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }

        public static Student student(String name, String group) {
            return new Student(name, group);
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
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
