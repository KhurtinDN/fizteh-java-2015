package ru.fizteh.fivt.students.krakhmalev.MiniORM.Annotations;

import java.lang.annotation.*;

@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
