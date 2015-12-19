import java.util.stream.Stream;

public interface Query<R> {
    Iterable<R> execute() throws QueryExecuteException, EmptyCollectionException;
    Stream<R> stream() throws QueryExecuteException, EmptyCollectionException;
}