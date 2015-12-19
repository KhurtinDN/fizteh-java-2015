/**
 * Created by Владимир on 19.12.2015.
 */

public class QueryExecuteException extends Exception
{
    QueryExecuteException(String message, Throwable reason) {
        super(message, reason);
    }
}
