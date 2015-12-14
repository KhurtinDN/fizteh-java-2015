import java.util.List;
import java.util.function.Function;

/**
 * Created by V on 29.11.2015.
 */
public interface Aggregator<T, S> extends Function<T,S> {
    S apply(List<T> values);
}
