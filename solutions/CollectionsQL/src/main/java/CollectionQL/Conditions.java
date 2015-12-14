package CollectionQL;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by V on 02.12.2015.
 */
public class Conditions<T> {
    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        Predicate<T> p = s -> expression.apply(s).endsWith(regexp);
        return p;
    }
    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
            Predicate<T> p1 = s -> expression.apply(s).startsWith(pattern);
            Predicate<T> p2 = s -> expression.apply(s).endsWith(pattern);
            Predicate<T> p3 = p1.and(p2);
            return p3;
    }
}
