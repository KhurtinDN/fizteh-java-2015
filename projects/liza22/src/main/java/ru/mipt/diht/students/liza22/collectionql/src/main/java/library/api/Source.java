package library.api;

import java.util.function.Function;

public interface Source<S> {

    <R> Query<R, S> select(Class<R> resultClass, Function<S, ?>... arguments);

    <R> Query<R, S> selectDistinct(Class<R> resultClass, Function<S, ?>... arguments);
}