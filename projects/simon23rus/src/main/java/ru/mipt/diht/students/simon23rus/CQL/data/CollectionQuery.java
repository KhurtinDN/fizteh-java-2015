package ru.mipt.diht.students.simon23rus.CQL.data;

import ru.mipt.diht.students.simon23rus.CQL.impl.*;
import static ru.mipt.diht.students.simon23rus.CQL.data.Sources.list;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

//import static ru.mipt.diht.students.simon23rus.data.CollectionQuery.Student.student;


public class CollectionQuery {


//
//    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//
////        Iterable<Tuple<String, String>> mentorsByStudent =
////                FromStmt.from(list(Student.student("ivanov", LocalDate.parse("1985-08-06"), "494")))
////                        .join(list(new Group("494", "mr.sidorov")))
////                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
////                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
////                        .execute();
////        System.out.println(mentorsByStudent);
//    }


    public static class Student {
        private final String name;

        private final LocalDate dateOfBirth;

        private final String group;

        public Student(String name, LocalDate dateOfBirth, String group) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.group = group;
        }

        public  Student(String name, String group) {
            this.name = name;
            this.group = group;
            this.dateOfBirth = LocalDate.now();
        }

        public String getName() {
            return name;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public String getGroup() {
            return group;
        }

        public Double age() {
            return ((double) ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now()));
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }

        @Override
        public String toString() {
            StringBuilder toReturn = new StringBuilder("Students{");
            if(!group.equals(0)) {
                toReturn.append("group=").append(this.group).append(",");
            }
            if(!name.equals(0)) {
                toReturn.append("name=").append(this.name).append(",");
            }
            if(!dateOfBirth.equals(0)) {
                toReturn.append("dateOfBirth=").append(this.dateOfBirth).append(",");
            }
            toReturn.append("}");
            return toReturn.toString();
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

        public Statistics(String group, Long count) {
            this(group, count, 18D);
        }

        public Statistics(String group, Double age) {
            this(group, 1L, age);
        }

        public Statistics(String group) {
            this(group, 1L, 2D);
        }

        public Statistics(Double age) {
            this("sad", 0L, age);
        }

        @Override
        public String toString() {
            StringBuilder toReturn = new StringBuilder("Statistics{");
            if(!group.equals(0)) {
                toReturn.append("group= ").append(this.group).append("");
            }
            if(!count.equals(0)) {
                toReturn.append("count= ").append(this.count).append("");
            }
            if(!age.equals(0)) {
                toReturn.append("age= ").append(this.age).append("");
            }
            toReturn.append("}");
            return toReturn.toString();
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String myGroup, String myMentor) {
            this.group = myGroup;
            this.mentor = myMentor;
        }

        public String getGroup() {
            return group;
        }

        public String getMentor() {
            return mentor;
        }
        public static Group group(String myGroup, String myMentor) {
            return new Group(myGroup, myMentor);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder().append("Student{");
            if (group != null) {
                result.append("group=").append(group).append(",");
            }
            if (mentor != null) {
                result.append(", name=").append(mentor);
            }
            result.append("}\n");
            return result.toString();
        }
    }

}
