package ru.mipt.diht.students.feezboom.MiniORM.Annotations;

/**
 * * Created by avk on 18.12.15.
 **/
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
