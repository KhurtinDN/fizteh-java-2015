package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 05.10.15.
 * Used http://habrahabr.ru/post/148986/
 */

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.GeoLocation;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import javafx.util.Pair;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;

final class GeoLocationResolver {
    static final int MAX_QUANTITY_OF_TRIES = 2;
    static final double EARTH_RADIUS = 6371;
    static final String RADIUS_UNIT = "km";

    public static double getSphereDist(double latitude1, double longitude1,
                                       double latitude2, double longitude2) {
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude1 = Math.toRadians(longitude1);
        longitude2 = Math.toRadians(longitude2);
        return EARTH_RADIUS
                * Math.acos(Math.sin(latitude1)
                * Math.sin(latitude2)
                + Math.cos(latitude1)
                * Math.cos(latitude2)
                * Math.cos(longitude1 - longitude2));
    }


    public static Pair<GeoLocation, Double> getGeoLocation(final String geoRequest)
            throws IOException, JSONException {
        final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("sensor", "false");
        params.put("address", geoRequest);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GeoLocationResolver.read(url);

        JSONObject location = response.getJSONArray("results").getJSONObject(0);
        location = location.getJSONObject("geometry");
        JSONObject northEastBound = location.getJSONObject("bounds");
        JSONObject southWestBound = location.getJSONObject("bounds");
        location = location.getJSONObject("location");
        northEastBound = northEastBound.getJSONObject("northeast");
        southWestBound = southWestBound.getJSONObject("southwest");
        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("lng");
        double northEastBoundLatitude = northEastBound.getDouble("lat");
        double northEastBoundLongitude = northEastBound.getDouble("lng");
        double southWestBoundLatitude = southWestBound.getDouble("lat");
        double southWestBoundLongitude = southWestBound.getDouble("lng");

        /*
        * Radius must be different in different regions
        */
        double approximatedRadius = getSphereDist(
                northEastBoundLatitude, northEastBoundLongitude,
                southWestBoundLatitude, southWestBoundLongitude
        );
        approximatedRadius /= 2;

        return new Pair((new GeoLocation(latitude, longitude)), new Double(approximatedRadius));
    }

    public static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject read(final String url) throws IOException, JSONException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = readAll(rd);
            final JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    private static String encodeParams(final Map<String, String> params) {
        final String paramsUrl = Joiner.on('&').join(
                Iterables.transform(params.entrySet()
                        , new Function<Map.Entry<String, String>, String>() {

                    @Override
                    public String apply(final Map.Entry<String, String> input) {
                        try {
                            final StringBuffer buffer = new StringBuffer();
                            buffer.append(input.getKey());
                            buffer.append('=');
                            buffer.append(URLEncoder.encode(input.getValue(), "utf-8"));
                            return buffer.toString();
                        } catch (final UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
        return paramsUrl;
    }

    public static String getNameOfCurrentLocation()
            throws MalformedURLException, LocationException {
        int numberOfTries = 0;

        do {
            URL currentIP = new URL("http://ipinfo.io/json");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    currentIP.openStream()))) {

                String currentInfo;
                StringBuilder responseBuilder = new StringBuilder();
                while ((currentInfo = in.readLine()) != null) {
                    responseBuilder.append(currentInfo);
                }

                JSONObject locationInfo =
                        new JSONObject(responseBuilder.toString());

                return locationInfo.getString("city");
            } catch (IOException | JSONException e) {
                ++numberOfTries;
            }
        }
        while (numberOfTries < MAX_QUANTITY_OF_TRIES);
        throw new LocationException();
    }
}
