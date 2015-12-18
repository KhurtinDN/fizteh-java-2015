package ru.mipt.diht.students.maxdankow.sqlcollections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Student {
    private final String name;

    private final LocalDate dateOfBith;

    private final String group;

    public final String getName() {
        return name;
    }

    public Student(String name, LocalDate dateOfBith, String group) {
        this.name = name;
        this.dateOfBith = dateOfBith;
        this.group = group;
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
