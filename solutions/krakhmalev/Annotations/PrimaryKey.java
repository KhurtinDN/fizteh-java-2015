package ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations;

import  java.lang.annotation.*;


@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
}
