package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 05.10.15.
 * Used http://habrahabr.ru/post/148986/
 */

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.json.JSONException;
import org.json.JSONObject;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;

final class GeoLocationResolver {
    static final int MAX_QUANTITY_OF_TRIES = 2;
    static final double EARTH_RADIUS = 6371;
    static final String RADIUS_UNIT = "km";
    static final String URL_IPINFO = "http://ipinfo.io/json";
    static final String BASE_URL = "http://maps.googleapis.com/maps/api/geocode/json";

    public static double getSphereDist(final GeoLocation location1,
                                       final GeoLocation location2) {

        double latitude1 = location1.getLatitude();
        double longitude1 = location1.getLongitude();
        double latitude2 = location2.getLatitude();
        double longitude2 = location2.getLongitude();

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

        Map<String, String> params = new ImmutableMap.Builder<String, String>()
                .put("sensor", "false")
                .put("address", geoRequest)
                .build();

        String url = BASE_URL + '?' + encodeParams(params);
        JSONObject response = new JSONObject(GeoLocationResolver.read(url));

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

        GeoLocation northEastBoundLocation = new GeoLocation(northEastBoundLatitude, northEastBoundLongitude);
        GeoLocation southEastBoundLocation = new GeoLocation(southWestBoundLatitude, southWestBoundLongitude);

        // Radius can be different in different regions
        double approximatedRadius = getSphereDist(northEastBoundLocation, southEastBoundLocation);
        approximatedRadius /= 2;

        return new GeoLocationInfo((new GeoLocation(latitude, longitude)), approximatedRadius);
    }

    public static String read(final String url) throws IOException, JSONException {
        try (InputStream inputStream = new URL(url).openStream();
             InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            String content = CharStreams.toString(streamReader);
            return content;
        }
    }

    private static String encodeParams(final Map<String, String> params) {
        return params.entrySet().stream().map(param -> {
            try {
                return URLEncoder.encode(param.getKey(), "UTF-8") + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining("&"));
    }

    public static String getNameOfCurrentLocation()
            throws MalformedURLException, LocationException {

        for (int numTries = 0; numTries < MAX_QUANTITY_OF_TRIES; ++numTries) {
            try {
                final String content = read(URL_IPINFO);
                JSONObject locationInfo = new JSONObject(content);
                return locationInfo.getString("city");
            } catch (IOException | JSONException e) {
            }
        }
        throw new LocationException("Unable to detect current location");
    }
}