package ru.mipt.diht.students.lenazherdeva.miniORM.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by admin on 18.12.2015.
 */


@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
