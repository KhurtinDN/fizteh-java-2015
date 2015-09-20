package Twitter;

import java.util.Base64;
import java.net.URLEncoder;
import java.net.URLConnection;

public class Twitter {
    public Twitter(String consumerKey, String consumerSecret) {
        String consumerKeyEncoded = URLEncoder.encode(consumerKey);
        String consumerSecretEncoded = URLEncoder.encode(consumerSecret);
        byte[] encodedBytes = Base64.getEncoder().encode((consumerKeyEncoded + ":" + consumerSecretEncoded).getBytes());
        
        URLConnection connection =
    }
}