package ru.fizteh.fivt.students.vruchtel.collectionsql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Student {
    private final String name;
    private final LocalDate dateOfBirth;
    private final String group;

    public Student(String _name, LocalDate _dateOfBirth, String _group) {
        name = _name;
        dateOfBirth = _dateOfBirth;
        group = _group;
    }

    public Student(String _name,  String _group) {
        name = _name;
        dateOfBirth = null;
        group = _group;
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
        return (double) ChronoUnit.YEARS.between(getDateOfBirth(), LocalDateTime.now());
    }

    public static Student student(String name, LocalDate dateOfBirth, String group) {
        return new Student(name, dateOfBirth, group);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append("Student{");
        if (group != null) {
            result.append("group = '").append(group).append('\'');
        }
        if (name != null) {
            result.append(", name = ").append(name);
        }
        if (dateOfBirth != null) {
            result.append(", age = ").append(dateOfBirth);
        }
        result.append("}\n");
        return result.toString();
    }
}