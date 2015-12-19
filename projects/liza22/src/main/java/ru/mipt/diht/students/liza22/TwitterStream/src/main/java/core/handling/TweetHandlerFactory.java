package core.handling;

import core.handling.impl.PrintResultOfQueryTweetsHandler;
import core.handling.impl.PrintStreamOfTweetsHandler;
import model.Mode;

public final class TweetHandlerFactory {

    private TweetHandlerFactory() { }

    /**
     * Gets tweets handler depending on the working mode.
     * @param mode working mode of application
     * @return tweet handler implementation
     */
    public static TweetHandler getHandler(Mode mode) {
        switch (mode) {
            case STREAM:
                return new PrintStreamOfTweetsHandler(System.out);
            case QUERY:
                return new PrintResultOfQueryTweetsHandler(System.out);
            default:
                throw new IllegalArgumentException("Mode = " + mode + " is not supported");
        }
    }
}
