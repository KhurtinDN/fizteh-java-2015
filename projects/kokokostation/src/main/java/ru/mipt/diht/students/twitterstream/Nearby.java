package ru.mipt.diht.students.twitterstream;

import javafx.util.Pair;
import twitter4j.GeoLocation;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by mikhail on 28.01.16.
 */
public class Nearby {
    private static final String QUERY = "http://ip-api.com/json",
            LATITUDE_KEY = "lat",
            LONGITUDE_KEY = "lon";

    public static GeoLocation nearby() {
        try (InputStream inputStream = new URL(QUERY).openStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;

            while ((cp = bufferedReader.read()) != -1) {
                stringBuilder.append((char) cp);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            return new GeoLocation(Double.parseDouble(jsonObject.getString(LATITUDE_KEY)),
                    Double.parseDouble(jsonObject.getString(LONGITUDE_KEY)));
        } catch (JSONException | IOException e) {
            System.err.println("Nearby can't find your location: " + e.getMessage());

            return null;
        }
    }
}
