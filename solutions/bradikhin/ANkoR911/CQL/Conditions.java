import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Владимир on 19.12.2015.
 */
public class Conditions<T>
{
    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp)
    {
        return element -> expression.apply(element).matches(regexp);
    }

    public static <T> Predicate<T> like(Function<T, String> expression, String pattern)
    {
        throw new UnsupportedOperationException();
    }
}