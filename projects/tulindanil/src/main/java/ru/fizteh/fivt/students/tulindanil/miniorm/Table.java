package ru.fizteh.fivt.students.tulindanil.miniorm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tulindanil on 15.12.15.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}

