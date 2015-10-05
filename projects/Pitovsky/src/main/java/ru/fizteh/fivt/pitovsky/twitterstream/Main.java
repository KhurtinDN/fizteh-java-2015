package ru.fizteh.fivt.pitovsky.twitterstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.TwitterException;

import com.beust.jcommander.JCommander;

/**
 *
 * @author Peter Pitovsky
 *
 */
public class Main {

    private static String getUrlSource(String url) throws IOException {
        URL realURL = new URL(url);
        URLConnection urlcon = realURL.openConnection();
        BufferedReader urlReader = new BufferedReader(
                new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
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
        String[] wipsource = getUrlSource("http://www.telize.com/geoip?callback=getgeoip").split("[,\":]+");
        for (int i = 0; i < wipsource.length; ++i) {
            if (wipsource[i].equals("city") && i < wipsource.length - 1) {
                return wipsource[i + 1];
            }
        }
        return "anywhere";
    }

    public static void main(String[] args) {
        //String[] argstmp = {"-q", "#Moscow", "-s"};
        JCommanderList jcl = new JCommanderList();
        JCommander jcomm = new JCommander(jcl, args);

        if (jcl.isHelp() || jcl.getQueryString() == null) {
            System.out.println("This program can print in stdout some tweets,"
                    + "searched at twitter.com and filtered by options:");
            jcomm.usage();
            return;
        }

        TwitterClient client = new TwitterClient();

        String searchPlace = jcl.getPlace();
        if (jcl.getPlace().equals("nearby")) {
            try {
                searchPlace = getMyCityByIP();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to calculate your location by IP."
                        + " Searching tweets from anywhere.");
            }
            if (searchPlace.equals("nearby")) {
                searchPlace = "anywhere";
            }
        }

        SearchLocation searchLocation = null;

        while (true) {
            try {
                if (searchLocation == null && !searchPlace.equals("anywhere")) {
                    searchLocation = client.findLocation(searchPlace);
                    if (jcl.isDebugMode()) {
                        if (searchLocation != null) {
                            System.err.println("[debug]: place is (" + searchLocation.getCenter().getLatitude() + ", "
                                    + searchLocation.getCenter().getLongitude() + ") with r = "
                                    + searchLocation.getRadius() + ".");
                        } else {
                            System.err.println("[debug]: cannot find place '" + jcl.getPlace() + "', search anywhere.");
                        }
                    }
                }
                if (jcl.isStream()) {
                    client.startStreaming(jcl.getQueryString(), jcl.isRetweetsHidden(),
                            searchLocation, jcl.isDebugMode());
                } else {
                    client.printTweets(jcl.getQueryString(), jcl.isRetweetsHidden(),
                            searchLocation, jcl.getTweetLimit(), jcl.isDebugMode());
                }
                break;
            } catch (TwitterException te) {
                if (jcl.isDebugMode()) {
                    te.printStackTrace();
                }
                if (jcl.isStream()) {
                    Thread.currentThread().interrupt();
                }
                System.err.println("Failed to run TwitterClient: " + te.getMessage() + "Try again? [y/n]");
                char ans = 0;
                try {
                    while (ans <= ' ') {
                        ans = (char) System.in.read();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ans = 'e';
                }
                if (ans != 'y' && ans != 'Y') {
                    break;
                }
            }
        }
    }
}
