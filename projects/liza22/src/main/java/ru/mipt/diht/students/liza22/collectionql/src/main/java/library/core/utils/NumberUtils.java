package library.core.utils;

import java.util.function.BiFunction;

/**
 * Util class to perform math operations with Number objects.
 *
 * Important assumption:
 * A both arguments are supposed to having the same type,
 * e.g. Long and Long, Integer and Integer and so on,
 * because the real type is defined by the first argument only.
 */
public class NumberUtils {

    public static final BiFunction<Number, Number, Number> SUM_NUMBERS = (n1, n2) -> {
        Class<?> numberClazz = n1.getClass();
        if (numberClazz.equals(Integer.class)) {
            return n1.intValue() + n2.intValue();
        } else if (numberClazz.equals(Long.class)) {
            return n1.longValue() + n2.longValue();
        } else if (numberClazz.equals(Double.class)) {
            return n1.doubleValue() + n2.doubleValue();
        } else if (numberClazz.equals(Float.class)) {
            return n1.floatValue() + n2.floatValue();
        } else {
            throw new IllegalArgumentException("Number class = " + numberClazz + " not supported");
        }
    };

    public static final BiFunction<Number, Number, Number> DIV_NUMBERS = (n1, n2) -> {
        Class<?> numberClazz = n1.getClass();
        if (numberClazz.equals(Integer.class)) {
            return n1.intValue() / n2.intValue();
        } else if (numberClazz.equals(Long.class)) {
            return n1.longValue() / n2.longValue();
        } else if (numberClazz.equals(Double.class)) {
            return n1.doubleValue() / n2.doubleValue();
        } else if (numberClazz.equals(Float.class)) {
            return n1.floatValue() / n2.floatValue();
        } else {
            throw new IllegalArgumentException("Number class = " + numberClazz + " not supported");
        }
    };

    public static final BiFunction<Number, Number, Integer> COMPARE_NUMBERS = (n1, n2) -> {
        Class<?> numberClazz = n1.getClass();
        if (numberClazz.equals(Integer.class)) {
            return Integer.compare(n1.intValue(), n2.intValue());
        } else if (numberClazz.equals(Long.class)) {
            return Long.compare(n1.longValue(), n2.longValue());
        } else if (numberClazz.equals(Double.class)) {
            return Double.compare(n1.doubleValue(), n2.doubleValue());
        } else if (numberClazz.equals(Float.class)) {
            return Float.compare(n1.floatValue(), n2.floatValue());
        } else {
            throw new IllegalArgumentException("Number class = " + numberClazz + " not supported");
        }
    };
}
