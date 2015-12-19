import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by Владимир on 19.12.2015.
 */


public class OrderByConditions
{
    public static <T, R extends Comparable<R>> Comparator<T> asc(Function<T, R> expression)
    {
        return (o1, o2) -> expression.apply(o1).compareTo(expression.apply(o2));
    }

    public static <T, R extends Comparable<R>> Comparator<T> desc(Function<T, R> expression)
    {
        return (o1, o2) -> expression.apply(o2).compareTo(expression.apply(o1));
    }
}
