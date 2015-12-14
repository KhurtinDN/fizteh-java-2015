package ru.mipt.diht.students.pitovsky.twitterstream;

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

    private static String resolvePlaceString(String queryString) {
        if (queryString.equals(PLACE_NEARBY)) {
            try {
                return getMyCityByIP();
            } catch (IOException e) {
                System.err.println("Failed to calculate your location by IP, " + e.getMessage()
                        + ". Searching tweets from anywhere.");
                return PLACE_ANYWHERE;
            }
        }
        return queryString;
    }

    public static void main(String[] args) {
        JCommanderList jcParams = new JCommanderList();
        JCommander jcommander = new JCommander(jcParams);
        jcommander.setProgramName(args[0]);
        try {
            jcommander.parse(args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            System.out.println("You can use --help to learn more.");
            System.exit(1);
        }

        if (jcParams.isHelp()) {
            System.out.println("This program can print in stdout some tweets,"
                    + "searched at twitter.com and filtered by options:");
            jcommander.usage();
            return;
        }

        TwitterClient client = new TwitterClient(jcParams.isRetweetsHidden(), jcParams.isDebugMode());

        String searchPlace = resolvePlaceString(jcParams.getPlace());

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
                    client.startStreaming(jcParams.getQueryString(), searchLocation);
                } else {
                    client.printTweets(jcParams.getQueryString(), searchLocation, jcParams.getTweetLimit());
                }
                break;
            } catch (TwitterException te) {
                if (jcParams.isDebugMode()) {
                    te.printStackTrace();
                }
                System.err.println("Failed to run TwitterClient: " + te.getMessage() + "Try again? [y/n]");
                char ans = 0;
                try {
                    while (ans <= ' ' && ans >= 0) {
                        ans = (char) System.in.read();
                    }
                } catch (IOException e) {
                    if (jcParams.isDebugMode()) {
                        System.err.println("reading fail: " + e.getMessage());
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
