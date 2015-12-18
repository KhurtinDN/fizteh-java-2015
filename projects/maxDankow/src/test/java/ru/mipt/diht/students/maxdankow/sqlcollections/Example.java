package ru.mipt.diht.students.maxdankow.sqlcollections;

public class Example {
    private String name;
    private int value;

    public Example(String newName, int newValue) {
        this.name = newName;
        this.value = newValue;
    }

    public final String getName() {
        return name;
    }

    public final int getValue() {
        return value;
    }
}

