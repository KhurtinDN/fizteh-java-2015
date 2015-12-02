package ru.mipt.diht.students.ale3otik.twitter;

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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by alex on 05.10.15.
 * Used http://habrahabr.ru/post/148986/
 */

public final class GeoLocationResolver {
    static final int MAX_QUANTITY_OF_TRIES = 2;
    static final double EARTH_RADIUS = 6371;
    public static final String RADIUS_UNIT = "km";
    static final String IP_INFO_URL = "http://ipinfo.io/json";
    static final String GOOGLE_API_URL = "http://maps.googleapis.com/maps/api/geocode/json";

    public static double getSphereDist(final GeoLocation location1,
                                       final GeoLocation location2) {

        double latitude1 = Math.toRadians(location1.getLatitude());
        double latitude2 = Math.toRadians(location2.getLatitude());
        double longitude1 = Math.toRadians(location1.getLongitude());
        double longitude2 = Math.toRadians(location2.getLongitude());

        return EARTH_RADIUS
                * Math.acos(Math.sin(latitude1)
                * Math.sin(latitude2)
                + Math.cos(latitude1)
                * Math.cos(latitude2)
                * Math.cos(longitude1 - longitude2));
    }

    public static GeoLocationInfo getGeoLocation(final String geoRequest)
            throws LocationException {
        try {
            Map<String, String> params = new ImmutableMap.Builder<String, String>()
                    .put("sensor", "false")
                    .put("address", geoRequest)
                    .build();

            String url = GOOGLE_API_URL + '?' + encodeParams(params);
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

            // Radius must be different in different regions
            double diameter = getSphereDist(northEastBoundLocation, southEastBoundLocation);

            return new GeoLocationInfo(new GeoLocation(latitude, longitude), diameter / 2);

        } catch (Exception e) {
            throw new LocationException("Unable to detect required location");
        }
    }

    public static String read(final String url) throws IOException, JSONException {
        try (InputStream inputStream = new URL(url).openStream();
             InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            return CharStreams.toString(streamReader);
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
            throws LocationException {

        for (int numTries = 0; numTries < MAX_QUANTITY_OF_TRIES; ++numTries) {
            try {
                final String content = GeoLocationResolver.read(IP_INFO_URL);
                JSONObject locationInfo = new JSONObject(content);
                return locationInfo.getString("city");
            } catch (Exception ignored) {
                // igore exception to increment numTries
            }
        }
        throw new LocationException("Unable to detect current location");
    }
}
