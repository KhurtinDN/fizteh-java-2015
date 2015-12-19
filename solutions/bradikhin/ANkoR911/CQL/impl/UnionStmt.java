/**
 * Created by Владимир on 19.12.2015.
 */

public class UnionStmt<R>
{
    private Iterable<R> previous;

    UnionStmt(Iterable<R> iterable)
    {
        previous = iterable;
    }

    public final <T> FromStmt<T> from(Iterable<T> list)
    {
        return new FromStmt<>(list, previous);
    }
}