package ru.mipt.diht.students.TwitterStream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
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
import java.util.Vector;

interface LocationFromGeocoding<T> {
    T getLocation(GeocodingResult gcr);

    T nearby() throws IOException, JSONException;
}

/**
 * Created by mikhail on 16.12.15.
 */
class GoogleGeocoding {
    private static final String GOOGLE_PROPERTIES = "google.properties";
    private static final String API_KEY_FIELD = "apiKey";

    public static GeocodingResult[] getGeocodingResult(String location) throws Exception {
        GeoApiContext context = new GeoApiContext().setApiKey(PropertiesHelper.getProperty(
                GOOGLE_PROPERTIES, API_KEY_FIELD));

        return GeocodingApi.geocode(context, location).await();
    }
}

class Nearby {
    private static final String QUERY = "http://ip-api.com/json",
            LATITUDE_KEY = "lat",
            LONGITUDE_KEY = "lon";

    static Pair<Double, Double> nearby() throws IOException, JSONException {
        try (InputStream inputStream = new URL(QUERY).openStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;

            while ((cp = bufferedReader.read()) != -1) {
                stringBuilder.append((char) cp);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            return new Pair<>(Double.parseDouble(jsonObject.getString(LATITUDE_KEY)),
                    Double.parseDouble(jsonObject.getString(LONGITUDE_KEY)));
        }
    }
}

class CircleLocation {
    private static final double EARTH_RADIUS = 6371;

    private GeoLocation geoLocation;
    private double radius;

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public double getRadius() {
        return radius;
    }

    CircleLocation(GeocodingResult gcr) {
        this((new BoxLocation(gcr)).getBox());
    }

    CircleLocation(double[][] box) {
        if (box == null) {
            geoLocation = null;
            radius = 0;
        } else {
            double nla = box[1][0],
                    nln = box[1][1],
                    sla = box[0][0],
                    sln = box[0][1];

            double latitude = (nla + sla) / 2,
                    longitude = (nln + sln) / 2,
                    longitudeRadius = (Math.toRadians(nln - sln)
                            * (EARTH_RADIUS * Math.cos(Math.toRadians(latitude)))) / 2,
                    latitudeRadius = Math.toRadians(nla - sla) * EARTH_RADIUS / 2;

            geoLocation = new GeoLocation(latitude, longitude);
            radius = (longitudeRadius + latitudeRadius) / 2;
        }
    }

    CircleLocation() throws IOException, JSONException {
        this((new BoxLocation()).getBox());
    }

    CircleLocation checkIfValid() {
        if (radius != 0) {
            return this;
        } else {
            return null;
        }
    }
}

class CircleLocationFactory implements LocationFromGeocoding<CircleLocation> {
    public CircleLocation getLocation(GeocodingResult gcr) {
        return (new CircleLocation(gcr)).checkIfValid();
    }

    public CircleLocation nearby() throws IOException, JSONException {
        return (new CircleLocation()).checkIfValid();
    }
}

class BoxLocation {
    private static final double BOX_RADIUS = 2; //половина стороны квадратика в градусах

    private double[][] box;

    public double[][] getBox() {
        return box;
    }

    BoxLocation(GeocodingResult gcr) {
        Bounds bounds = gcr.geometry.bounds;

        if (bounds == null) {
            box = null;
        } else {
            box = new double[2][2];

            box[0][0] = bounds.southwest.lat;
            box[0][1] = bounds.southwest.lng;
            box[1][0] = bounds.northeast.lat;
            box[1][1] = bounds.northeast.lng;
        }
    }

    BoxLocation() throws IOException, JSONException {
        Pair<Double, Double> center = Nearby.nearby();

        if (center == null) {
            box = null;
        } else {

            box = new double[2][2];

            box[0][0] = center.getKey() - BOX_RADIUS;
            box[0][1] = center.getValue() - BOX_RADIUS;
            box[1][0] = center.getKey() + BOX_RADIUS;
            box[1][1] = center.getValue() + BOX_RADIUS;
        }
    }

    BoxLocation checkIfValid() {
        if (box != null) {
            return this;
        } else {
            return null;
        }
    }

    boolean contains(GeoLocation geoLocation) {
        if (checkIfValid() == null) {
            return false;
        }

        double lat = geoLocation.getLatitude(),
                lng = geoLocation.getLongitude();
        return box[0][0] <= lat && lat <= box[1][0] && box[0][1] <= lng && lng <= box[1][1];
    }
}

class BoxLocationFactory implements LocationFromGeocoding<BoxLocation> {
    public BoxLocation getLocation(GeocodingResult gcr) {
        return (new BoxLocation(gcr)).checkIfValid();
    }

    public BoxLocation nearby() throws IOException, JSONException {
        return (new BoxLocation()).checkIfValid();
    }
}

class LocationGetter {
    static <T, S extends LocationFromGeocoding<T>> Vector<T> getLocations(S factory, String location, boolean nearby) {
        Vector<T> result = new Vector<>();

        try {
            GeocodingResult[] results = GoogleGeocoding.getGeocodingResult(location);

            for (GeocodingResult geocodingResult : results) {
                T newElement = factory.getLocation(geocodingResult);
                if (newElement != null) {
                    result.add(newElement);
                }
            }
        } catch (Exception e) {
            System.err.println("GoogleGeocoding can't process a location: " + e.getMessage());
        }

        try {
            if (nearby) {
                result.add(factory.nearby());
            }
        } catch (IOException | JSONException e) {
            System.err.println("Nearby can't find your location: " + e.getMessage());
        }

        return result;
    }
}
