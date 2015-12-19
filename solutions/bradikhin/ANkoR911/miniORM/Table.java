/**
 * Created by Владимир on 19.12.2015.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Table
{
    String name() default "";
}