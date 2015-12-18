package ru.fizteh.fivt.students.popova.CollectionQl2;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by V on 19.12.2015.
 */
public class Student {
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
        return ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
    }

    public static Student student(String name, LocalDate dateOfBith, String group) {
        return new Student(name, dateOfBith, group);
    }

    @Override
    public String toString()
    {
        return this.name + " " + this.dateOfBith.toString() + " " + this.group.toString();
    }

}

class Statistics {

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

    public Statistics(String group) {
        this.group = group;
        this.count = Long.MAX_VALUE;
        this.age = Long.MAX_VALUE;
    }

    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(getClass() != o.getClass()){
            return false;
        }
        Statistics st = (Statistics) o;
        return (o == this);
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getGroup());
        return builder.hashCode();
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
