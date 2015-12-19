import java.util.stream.Stream;

/**
 * Created by Владимир on 19.12.2015.
 */


public interface Query<R> {

    Iterable<R> execute() throws QueryExecuteException, EmptyCollectionException;

    Stream<R> stream() throws QueryExecuteException, EmptyCollectionException;
}
