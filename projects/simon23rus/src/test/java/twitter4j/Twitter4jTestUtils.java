package twitter4j;

import twitter4j.*;
        import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by semenfedotov on 13.12.15.
 */
public class Twitter4jTestUtils {
    public static List<Status> tweetsFromJson(String resource) {
        try (InputStream inputStream = twitter4j.Twitter4jTestUtils.class.getResourceAsStream(resource)) {
            JSONObject json = new JSONObject(IOUtils.toString(inputStream));
            JSONArray array = json.getJSONArray("statuses");
            List<Status> tweets = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject tweet = array.getJSONObject(i);
                tweets.add(new StatusJSONImpl(tweet));
            }
            return tweets;
        } catch (IOException | JSONException | TwitterException e) {
            throw new RuntimeException(e);
        }
    }
}