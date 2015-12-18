package core.handling;

import model.Tweet;

/**
 * Tweet handler interface.
 */
public interface TweetHandler {

    /**
     * Handles tweet by any way.
     * @param tweet obtained tweet
     */
    void handle(Tweet tweet);
}
