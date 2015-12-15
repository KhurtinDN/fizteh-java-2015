package twitter4j;

import twitter4j.*;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glutolik on 15.12.15.
 */
public class Twitter4jTests {
    public static List<Status> tweetsFromJson(String resource) {
        try (InputStream inputStream = Twitter4jTests.class.getResourceAsStream(resource)) {
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
