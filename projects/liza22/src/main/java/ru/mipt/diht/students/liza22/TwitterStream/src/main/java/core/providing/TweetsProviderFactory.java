package core.providing;

import core.providing.impl.TweetsByQueryProvider;
import core.providing.impl.TweetsStreamProvider;
import model.Mode;

/**
 * Tweets provider factory.
 */
public final class TweetsProviderFactory {

    private TweetsProviderFactory() { }

    /**
     * Gets tweets provider depending on the working mode.
     * @param mode working mode of application
     * @return tweets provider implementation
     */
    public static TweetsProvider getProvider(Mode mode) {
        switch (mode) {
            case STREAM:
                return new TweetsStreamProvider();
            case QUERY:
                return new TweetsByQueryProvider();
            default:
                throw new IllegalArgumentException("Mode = " + mode + " is not supported");
        }
    }
}
