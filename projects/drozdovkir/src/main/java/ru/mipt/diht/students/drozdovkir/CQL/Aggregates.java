import ru.fizteh.fivt.students.drozdovkir.collectionquery.EmptyCollectionException;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Aggregates {

    public interface Agregator<C, T> extends Function<C, T> {
        T apply(Collection<C> collection) throws EmptyCollectionException;
    }

    public static <C, T extends Comparable<T>> Agregator<C, T> max(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) throws EmptyCollectionException {
                if (collection.isEmpty()) {
                    throw new EmptyCollectionException("Method max was called for empty collection.");
                }
                return Collections.max(collection.stream().map(expression).collect(Collectors.toList()));
            }

            @Override
            public T apply(C c) {
                return expression.apply(c);
            }
        };
    }


    public static <C, T extends Comparable<T>> Agregator<C, T> min(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) throws EmptyCollectionException {
                if (collection.isEmpty()) {
                    throw new EmptyCollectionException("Method min was called for empty collection.");
                }
                return Collections.min(collection.stream().map(expression).collect(Collectors.toList()));
            }

            @Override
            public T apply(C c) {
                return expression.apply(c);
            }
        };
    }


    public static <C, T extends Comparable<T>> Agregator<C, Long> count(Function<C, T> expression) {
        return new Agregator<C, Long>() {
            @Override
            public Long apply(Collection<C> collection) {
                if (collection.isEmpty()) {
                    return 0L;
                }
                return collection.stream().map(expression).count();
            }

            @Override
            public Long apply(C c) {
                if (expression.apply(c) != null) {
                    return 1L;
                }
                return 0L;
            }
        };
    }


    public static <C, T extends Number> Agregator<C, T> avg(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) throws EmptyCollectionException {
                if (collection.isEmpty()) {
                    throw new EmptyCollectionException("Method avg was called for empty collection.");
                }
                T example = expression.apply(collection.iterator().next());
                if (example instanceof Integer) {
                    int sum = 0;
                    for (C elem : collection) {
                        sum += (Integer) expression.apply(elem);
                    }
                    return (T) Integer.valueOf(sum / collection.size());
                }
                if (example instanceof Long) {
                    int sum = 0;
                    for (C elem : collection) {
                        sum += (Long) expression.apply(elem);
                    }
                    return (T) Long.valueOf(sum / collection.size());
                }
                if (example instanceof Double || example instanceof Float) {
                    double sum = 0;
                    for (C elem : collection) {
                        sum += (Long) expression.apply(elem);
                    }
                    return (T) Double.valueOf(sum / collection.size());
                }
                throw new ClassCastException("Class type is not detected!");
            }

            @Override
            public T apply(C c) {
                return expression.apply(c);
            }
        };
    }
}