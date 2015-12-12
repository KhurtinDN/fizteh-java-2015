package ru.mipt.diht.students.andreyzharkov.twitterStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Андрей on 09.10.2015.
 */
public class TwitterStream {
    public static void main(String[] args) {
        ArgumentsList programArguments = new ArgumentsList();
        JCommander jcommander;
        try {
            jcommander = new JCommander(programArguments, args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            System.out.println("You can use --help to learn more.");
            System.exit(1);
            return;
        }

        if (programArguments.isHelp()) {
            System.out.println("This program can print in stdout some tweets,"
                    + "searched at twitter.com and filtered by options:");
            jcommander.usage();
            return;
        }

        programArguments.checkLocation();

        TwitterOutputEditor twitterEditor = new TwitterOutputEditor(programArguments);

        if (!programArguments.isStream()) {
            twitterEditor.simpleMode();
        } else {
            twitterEditor.streamMode();
        }
    }

    private static String getUrlSource(String url) throws IOException {
        URL realURL = new URL(url);
        URLConnection connection = realURL.openConnection();
        try (BufferedReader urlReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String inputLine = urlReader.readLine();
            StringBuilder sourceString = new StringBuilder();
            while (inputLine != null) {
                sourceString.append(inputLine);
                inputLine = urlReader.readLine();
            }
            urlReader.close();

            return sourceString.toString();
        }
    }

    private static String getMyLocation() throws IOException {
        //telize site JSON output look like "getgeoip({"parameter":"value","parameter":"value",...})"
        String[] geoSiteSource = getUrlSource("http://www.telize.com/geoip?callback=getgeoip").split("[,\":]+");
        for (int i = 0; i < geoSiteSource.length; ++i) {
            if (geoSiteSource[i].equals("city") && i < geoSiteSource.length - 1) {
                return geoSiteSource[i + 1];
            }
        }
        return "anywhere";
    }

    public static String checkLocation(String queryString) {
        if (queryString.equals("nearby")) {
            try {
                return getMyLocation();
            } catch (IOException e) {
                System.err.println("Failed to calculate your location by IP, " + e.getMessage()
                        + ". Searching tweets from anywhere.");
                return "anywhere";
            }
        }
        return queryString;
    }
}
