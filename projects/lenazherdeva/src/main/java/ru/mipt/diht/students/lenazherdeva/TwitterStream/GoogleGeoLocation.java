package ru.mipt.diht.students.lenazherdeva.twitterStream;
 /* Created by admin on 02.10.2015.*/

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GoogleGeoLocation {
    private static final double EARTH_RADIUS = 6371;
    private static GeocodingResult[] result;
    private double radius;

    public GoogleGeoLocation(String place) throws Exception {
        if (!place.equals("nearby")) {
            String apiKey = "AIzaSyCosxTMyTkHW5qujPwS-nFD8DdGtxnkgnk";
            GeoApiContext context = new GeoApiContext()
                    .setApiKey(apiKey);
            result = GeocodingApi.geocode(context, place).await();
            radius = calculateRadius();
        }
    }

    /*private String getKeyFromProperties() throws IOException {
        Properties prop = new Properties();
        try {
            InputStream input = new FileInputStream("twitter4j.properties");
            prop.load(input);
        } catch (FileNotFoundException e) {
            System.err.println("Can't find the file : " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("Can't read the file : " + e.getMessage());
            throw e;
        }
        return prop.getProperty("googleApiKey");
    }*/

    public static LatLng getLocation() {
        return result[0].geometry.location;
    }

    public final double getRadius() {
        return radius;
    }

    private double calculateRadius() {
        double phi1 = Math.toRadians(result[0].geometry.bounds.northeast.lat);
        double phi2 = Math.toRadians(result[0].geometry.bounds.southwest.lat);
        double dPhi = phi1 - phi2;
        double lambd1 = Math.toRadians(result[0].geometry.bounds.northeast.lng);
        double lambd2 = Math.toRadians(result[0].geometry.bounds.southwest.lng);
        double dLambda = lambd1 - lambd2;

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance / 2;
    }
    public final double[][] getBounds() {
        Bounds bounds = result[0].geometry.bounds;
        double[][] boundsArr = {{bounds.southwest.lng, bounds.southwest.lat},
                {bounds.northeast.lng, bounds.northeast.lat}};
        return boundsArr;
    }
}
