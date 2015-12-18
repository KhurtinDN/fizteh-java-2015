package config;

import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;

/*
 * Class loads and holds Twitter Access configuration from file resource
 * and provides methods to get {@link twitter4j.conf.Configuration} and {@link twitter4j.auth.AccessToken}
 */
public class TwitterConfig {
    private static final String CONSUMER_KEY_PROP_NAME          = "consumerKey";
    private static final String CONSUMER_SECRET_PROP_NAME       = "consumerSecret";
    private static final String ACCESS_TOKEN_PROP_NAME          = "accessToken";
    private static final String ACCESS_TOKEN_SECRET_PROP_NAME   = "accessTokenSecret";

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public TwitterConfig() {
        init();
    }

    private void init() {
        File cfgFile = new File(Constants.TWITTER_CONFIG_FILE);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile)))) {
            while (in.ready()) {
                String line = in.readLine();
                int indexOfDelimiter = line.indexOf('=');
                if (indexOfDelimiter == -1) {
                    System.err.println("Incorrect line in twitter configuration = '" + line + "'");
                    continue;
                }
                String propName = line.substring(0, indexOfDelimiter);
                String propValue = line.substring(indexOfDelimiter + 1, line.length());
                switch (propName) {
                    case CONSUMER_KEY_PROP_NAME:
                        consumerKey = propValue;
                        break;
                    case CONSUMER_SECRET_PROP_NAME:
                        consumerSecret = propValue;
                        break;
                    case ACCESS_TOKEN_PROP_NAME:
                        accessToken = propValue;
                        break;
                    case ACCESS_TOKEN_SECRET_PROP_NAME:
                        accessTokenSecret = propValue;
                        break;
                    default:
                        System.err.println("Property '" + propName + "' not recognized");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Twitter config file by path = \"" + cfgFile.getAbsolutePath() + "\" not found");
        } catch (IOException e) {
            System.err.println("Problem with reading twitter config file: " + e.getMessage());
        }

        validate();
    }

    private void validate() {
        if (consumerKey == null
                || consumerSecret == null
                || accessToken == null
                || accessTokenSecret == null) {
            throw new IllegalStateException("Twitter configuration file is incorrect");
        }
    }

    public final AccessToken getAccessToken() {
        return new AccessToken(accessToken, accessTokenSecret);
    }

    public final Configuration getConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().
                setOAuthConsumerKey(consumerKey).
                setOAuthConsumerSecret(consumerSecret).
                setOAuthAccessToken(accessToken).
                setOAuthAccessTokenSecret(accessTokenSecret);
        return configurationBuilder.build();
    }

    @Override
    public final String toString() {
        return "TwitterConfig{"
                + "consumerKey='" + consumerKey
                + '\''
                + ", consumerSecret='" + consumerSecret
                + '\''
                + ", accessToken='" + accessToken
                + '\''
                + ", accessTokenSecret='" + accessTokenSecret
                + '\''
                + '}';
    }
}
