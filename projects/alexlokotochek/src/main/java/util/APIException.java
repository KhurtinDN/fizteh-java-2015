package util;
/**
 * Created by lokotochek on 13.12.15.
 */
public class APIException extends Exception {
    public APIException() { }

    public APIException(String message) {
        super(message);
    }

    public APIException(Throwable cause) {
        super(cause);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
