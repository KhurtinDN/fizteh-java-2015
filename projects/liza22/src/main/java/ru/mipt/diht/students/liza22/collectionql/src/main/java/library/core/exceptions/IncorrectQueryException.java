package library.core.exceptions;

/**
 * Checked exception to notify about incorrect query syntax.
 */
public class IncorrectQueryException extends Exception {

    public IncorrectQueryException(String description) {
        super(description);
    }
}
