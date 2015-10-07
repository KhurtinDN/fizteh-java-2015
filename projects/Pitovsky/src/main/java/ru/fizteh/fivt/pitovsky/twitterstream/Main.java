package ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.TwitterException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 *
 * @author Peter Pitovsky
 *
 */
public class Main {

    public static final String PLACE_ANYWHERE = "anywhere";
    public static final String PLACE_NEARBY = "nearby";

    private static String getUrlSource(String url) throws IOException {
        URL realURL = new URL(url);
        URLConnection connection = realURL.openConnection();
        BufferedReader urlReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String inputLine = urlReader.readLine();
        StringBuilder sourceString = new StringBuilder();
        while (inputLine != null) {
            sourceString.append(inputLine);
            inputLine = urlReader.readLine();
        }
        urlReader.close();

        return sourceString.toString();
    }

    private static String getMyCityByIP() throws IOException {
      //telize site JSON output look like "getgeoip({"parameter":"value","parameter":"value",...})"
        String[] geoSiteSource = getUrlSource("http://www.telize.com/geoip?callback=getgeoip").split("[,\":]+");
        for (int i = 0; i < geoSiteSource.length; ++i) {
            if (geoSiteSource[i].equals("city") && i < geoSiteSource.length - 1) {
                return geoSiteSource[i + 1];
            }
        }
        return PLACE_ANYWHERE;
    }

    public static void main(String[] args) {
        //String[] argstmp = {"-q", "#Moscow", "-s"};
        JCommanderList jcParams = new JCommanderList();
        JCommander jcommander;
        try {
            jcommander = new JCommander(jcParams, args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            System.out.println("You can use --help to learn more.");
            return;
        }

        if (jcParams.isHelp()) {
            System.out.println("This program can print in stdout some tweets,"
                    + "searched at twitter.com and filtered by options:");
            jcommander.usage();
            return;
        }

        TwitterClient client = new TwitterClient();

        String searchPlace = jcParams.getPlace();
        if (jcParams.getPlace().equals(PLACE_NEARBY)) {
            try {
                searchPlace = getMyCityByIP();
            } catch (IOException e) {
                if (jcParams.isDebugMode()) {
                    e.printStackTrace();
                }
                System.err.println("Failed to calculate your location by IP, " + e.getMessage()
                        + ". Searching tweets from anywhere.");
            }
            if (searchPlace.equals(PLACE_NEARBY)) {
                searchPlace = PLACE_ANYWHERE;
            }
        }

        SearchLocation searchLocation = null;

        while (true) {
            try {
                if (searchLocation == null && !searchPlace.equals(PLACE_ANYWHERE)) {
                    searchLocation = client.findLocation(searchPlace);
                    if (jcParams.isDebugMode()) {
                        if (searchLocation != null) {
                            System.err.println("[debug]: place is " + searchLocation);
                        } else {
                            System.err.println("[debug]: cannot find place '" + jcParams.getPlace()
                                    + "', search anywhere.");
                        }
                    }
                }
                if (jcParams.isStream()) {
                    client.startStreaming(jcParams.getQueryString(), jcParams.isRetweetsHidden(),
                            searchLocation, jcParams.isDebugMode());
                } else {
                    client.printTweets(jcParams.getQueryString(), jcParams.isRetweetsHidden(),
                            searchLocation, jcParams.getTweetLimit(), jcParams.isDebugMode());
                }
                break;
            } catch (TwitterException te) {
                if (jcParams.isDebugMode()) {
                    te.printStackTrace();
                }
                if (jcParams.isStream()) {
                    Thread.currentThread().interrupt();
                }
                System.err.println("Failed to run TwitterClient: " + te.getMessage() + "Try again? [y/n]");
                char ans = 0;
                try {
                    while (ans <= ' ') {
                        ans = (char) System.in.read();
                    }
                } catch (IOException e) {
                    if (jcParams.isDebugMode()) {
                        e.printStackTrace();
                    }
                    ans = 'e';
                }
                if (ans != 'y' && ans != 'Y') {
                    break;
                }
            }
        }
    }
}
