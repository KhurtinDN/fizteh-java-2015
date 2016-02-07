package ru.mipt.diht.students.twitterstream;

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
public class NearbyImpl implements Nearby {
    private static final String QUERY = "http://ip-api.com/json";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";

    public GeoLocation nearby() throws Exception {
        try (InputStream inputStream = new URL(QUERY).openStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            String temp;

            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            return new GeoLocation(Double.parseDouble(jsonObject.getString(LATITUDE_KEY)),
                    Double.parseDouble(jsonObject.getString(LONGITUDE_KEY)));
        }
    }
}
