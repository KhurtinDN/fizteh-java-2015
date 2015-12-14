package ru.mipt.diht.students.simon23rus.TwitterStream;

import twitter4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Double.parseDouble;


/**
 * Created by semenfedotov on 05.12.15.
 */
public class GeoCodeConverter {


    public static String webSource() throws IOException, JSONException {
        URL newUrl = new URL("http://ip-api.com/json");
        URLConnection urlConnecter = newUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                urlConnecter.getInputStream(), "UTF-8"));
        JSONObject givenSource = new JSONObject(in.readLine());
        String mySource;
        mySource = givenSource.getString("city");
        in.close();
        System.out.println(mySource.toString());
        return mySource.toString();
    }


    public static GeoLocation getCoordinates(String place) throws IOException, InterruptedException,
            JSONException {
        //if (place != "Moscow") System.out.println(place);
        if (place.equals("nearby")) {
//            place = webSource();
            place = "Долгопрудный";
        }
        URL getTheLL = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + place + "&apikey="
                + "AIzaSyCSLjuyawVt4lZAlb8t0EwuxKQWvRCaqsY");
        String city;
        //System.out.println(getTheLL);
        try (InputStream yandexIn = getTheLL.openStream()) {
            //System.out.println(getTheLL);
            JSONTokener tokenizer = new JSONTokener(yandexIn);
            JSONObject jsonParse = new JSONObject(tokenizer);
            if (jsonParse.getString("status").equals("OK")) {
                JSONArray places = jsonParse.getJSONArray("results");
                JSONObject myPlace = places.getJSONObject(0);
                JSONObject geometry = myPlace.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                System.out.println(parseDouble(location.getString("lat")));
                System.out.println(parseDouble(location.getString("lng")));
                return new GeoLocation(parseDouble(location.getString("lat")), parseDouble(location.getString("lng")));
            } else {
                System.out.println("bu");
            }
        }
        return new GeoLocation((double) 0, (double) 0);
    }


    static double sqr(double number) {
        return number * number;
    }
    static final double EARTH_DIAMETER_POPOLAM = 6371;
    public static boolean near(GeoLocation first, GeoLocation second, double radius) {
        double firstLatitude = Math.toRadians(first.getLatitude());
        double firstLongtitute = Math.toRadians(first.getLongitude());
        double secondLatitude = Math.toRadians(second.getLatitude());
        double secondLongtitude = Math.toRadians(second.getLongitude());
        double deltaPhi = secondLatitude - firstLatitude;
        double deltaLambda = secondLongtitude - firstLongtitute;

        double distance = 2 * Math.asin(Math.sqrt(sqr(Math.sin(deltaPhi / 2))
                + Math.cos(firstLatitude) * Math.cos(secondLatitude) * sqr(Math.sin(deltaLambda / 2)))) * EARTH_DIAMETER_POPOLAM;
        //System.out.println(distance);
        return distance < radius;
    }
}


