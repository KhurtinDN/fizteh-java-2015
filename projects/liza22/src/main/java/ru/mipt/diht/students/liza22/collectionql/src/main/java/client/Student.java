package client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Student {
    private final String name;

    private final LocalDate dateOfBirth;

    private final String group;

    public final String getName() {
        return name;
    }

    public Student(String name1, LocalDate dateOfBirth1, String group1) {
        this.name = name1;
        this.dateOfBirth = dateOfBirth1;
        this.group = group1;
    }

    public final LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public final String getGroup() {
        return group;
    }

    public final long age() {
        return ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now());
    }

    public static Student student(String name1, LocalDate dateOfBirth1, String group1) {
        return new Student(name1, dateOfBirth1, group1);
    }
}
