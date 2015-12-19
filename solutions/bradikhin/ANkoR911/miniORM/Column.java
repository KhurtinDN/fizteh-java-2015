/**
 * Created by Владимир on 19.12.2015.
 */

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;


@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
    String name() default "";
}