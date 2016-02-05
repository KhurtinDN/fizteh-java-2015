package ru.mipt.diht.students.miniormtests;

import ru.mipt.diht.students.miniorm.DatabaseServiceAnnotations;

import java.io.Writer;

/**
 * Created by mikhail on 05.02.16.
 */
@DatabaseServiceAnnotations.Table (name = "testTable")
public class NormalTestClass {
    public NormalTestClass(Boolean booleanField, Integer integerField) {
        this.booleanField = booleanField;
        this.integerField = integerField;
    }

    public NormalTestClass() {
    }

    public int intField;
    public Writer writerField;

    @DatabaseServiceAnnotations.Column(name = "testIntegerField")
    public Integer integerField;

    @DatabaseServiceAnnotations.PrimaryKey
    @DatabaseServiceAnnotations.Column
    public Boolean booleanField;

    public Double doubleField;
}
