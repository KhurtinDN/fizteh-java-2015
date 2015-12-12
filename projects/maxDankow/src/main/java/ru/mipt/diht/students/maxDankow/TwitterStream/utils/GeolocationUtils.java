package ru.mipt.diht.students.maxDankow.TwitterStream.utils;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import twitter4j.GeoLocation;
import twitter4j.Place;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GeolocationUtils {
    protected static final String GOOGLE_API_KEY = readGoogleAPIKey();

    // Читает из .properties файла ключ для google API(в рабочей директории)
    private static String readGoogleAPIKey() {
        Properties properties = new Properties();
        try (FileInputStream propertiesFile = new FileInputStream("googlegeoapi.properties")) {
            properties.load(propertiesFile);
            return properties.getProperty("key");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // По названию местности получает ее прямоугольные координаты.
    public static Geometry getLocationBoxCoordinates(String placeName) {
        GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_API_KEY);
        GeocodingApiRequest geoRequest = GeocodingApi.geocode(context, placeName);
        try {
            GeocodingResult[] locations = geoRequest.await();
            return locations[0].geometry;
        } catch (Exception e) {
            System.err.println("Ошибка при обращении к Google Geocoding API.");
            return null;
        }
    }

    // Проверяет принадлежность точки на Земле указаной местности.
    public static boolean checkLocation(Place place, Geometry searchLocationGeometry) {
        if (searchLocationGeometry == null) {
            return true;
        }
        if (place == null) {
            return false;
        }
        // Берем среднее значение координат заданной области.
        double latitudeSum = 0, longitudeSum = 0;
        GeoLocation[][] geoLocations = place.getBoundingBoxCoordinates();

        for (int i = 0; i < geoLocations[0].length; ++i) {
            latitudeSum += geoLocations[0][i].getLatitude();
            longitudeSum += geoLocations[0][i].getLongitude();
        }
        double placeLat = latitudeSum / geoLocations[0].length;
        double placeLng = longitudeSum / geoLocations[0].length;

        return (placeLat > searchLocationGeometry.bounds.southwest.lat
                && placeLat < searchLocationGeometry.bounds.northeast.lat
                && placeLng > searchLocationGeometry.bounds.southwest.lng
                && placeLng < searchLocationGeometry.bounds.northeast.lng);
    }
}
