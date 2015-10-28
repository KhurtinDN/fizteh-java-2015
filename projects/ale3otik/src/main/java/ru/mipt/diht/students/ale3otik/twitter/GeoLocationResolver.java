package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 05.10.15.
 * Used http://habrahabr.ru/post/148986/
 */

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import org.json.JSONException;
import org.json.JSONObject;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

final class GeoLocationResolver {
    static final int MAX_QUANTITY_OF_TRIES = 2;
    static final double EARTH_RADIUS = 6371;
    static final String RADIUS_UNIT = "km";
    static final String URL_IPINFO = "http://ipinfo.io/json";

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


    public static GeoLocationInfo getGeoLocation(final String geoRequest)
            throws IOException, JSONException {
        final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json";
        final Map<String, String> params = Maps.newHashMap();
        params.put("sensor", "false");
        params.put("address", geoRequest);
        final String url = baseUrl + '?' + encodeParams(params);
        final JSONObject response = GeoLocationResolver.read(url);

        JSONObject result = response.getJSONArray("results").getJSONObject(0);
        JSONObject geometry = result.getJSONObject("geometry");

        JSONObject northEastBound = geometry.getJSONObject("bounds").getJSONObject("northeast");
        JSONObject southWestBound = geometry.getJSONObject("bounds").getJSONObject("southwest");
        JSONObject location = geometry.getJSONObject("location");

        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("lng");
        double northEastBoundLatitude = northEastBound.getDouble("lat");
        double northEastBoundLongitude = northEastBound.getDouble("lng");
        double southWestBoundLatitude = southWestBound.getDouble("lat");
        double southWestBoundLongitude = southWestBound.getDouble("lng");

        /*
        * Radius can be different in different regions
        */
        double approximatedRadius = getSphereDist(
                northEastBoundLatitude, northEastBoundLongitude,
                southWestBoundLatitude, southWestBoundLongitude
        );
        approximatedRadius /= 2;

        return new GeoLocationInfo((new GeoLocation(latitude, longitude)), approximatedRadius);
    }

    public static JSONObject read(final String url) throws IOException, JSONException {
        try (final InputStream inputStream = new URL(url).openStream();
             final InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            final String content = CharStreams.toString(streamReader);
            return new JSONObject(content);
        }
    }

    private static String encodeParams(final Map<String, String> params) {
        final String paramsUrl = Joiner.on('&').join(
                Iterables.transform(params.entrySet()
                        , new Function<Map.Entry<String, String>, String>() {

                    @Override
                    public String apply(final Map.Entry<String, String> input) {
                        try {
                            return input.getKey() + "=" + URLEncoder.encode(input.getValue(), "utf-8");
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
        URL currentIP = new URL(URL_IPINFO);
        while (numberOfTries < MAX_QUANTITY_OF_TRIES) {

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    currentIP.openStream()))) {

                final String content = CharStreams.toString(reader);

                JSONObject locationInfo = new JSONObject(content);

                return locationInfo.getString("city");
            } catch (IOException | JSONException e) {
                ++numberOfTries;
            }
        }
        throw new LocationException("Exception in detecting current location");
    }
}
