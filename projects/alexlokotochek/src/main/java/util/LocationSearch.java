package util;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import twitter4j.GeoLocation;
import twitter4j.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class LocationSearch {


    public static GeoLocation getGeoCenter(String place) {
        if ("nearby".equals(place)) {
            place = getGeoIP();
        }

        try {
            Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(place).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> geocoderResult = geocoderResponse.getResults();
            if (geocoderResult.size() == 0) {
                String msg = "Google doesn't know where are you ;(";
                throw new APIException(msg);
            }
            double latitude = geocoderResult.get(0).getGeometry().getLocation().getLat().floatValue();
            double longitude = geocoderResult.get(0).getGeometry().getLocation().getLng().floatValue();

            return new GeoLocation(latitude, longitude);

        } catch (Exception ge) {
            System.err.println("Error in Geocoder: " + ge.getMessage());
        }
        return null;
    }

    public static double[][] getGeoBox(String place) {
        if ("nearby".equals(place)) {
            place = getGeoIP();
        }

        try {
            final Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(place).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> geocoderResult = geocoderResponse.getResults();
            LatLngBounds box = geocoderResult.get(0).getGeometry().getBounds();

            LatLng coordN = box.getNortheast();
            LatLng coordS = box.getSouthwest();

            double[][] boxAnswer = {
                {coordS.getLat().doubleValue(), coordS.getLng().doubleValue()},
                {coordN.getLat().doubleValue(), coordN.getLng().doubleValue()}
            };

            return boxAnswer;

        } catch (Exception ge) {
            System.out.println("Error in Geocoder: " + ge.getMessage());
        }
        return null;
    }

    public static String getGeoIP() {
        try {
            URL url = new URL("http://ip-api.com/json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            JSONObject line = new JSONObject(reader.readLine());
            reader.close();
            System.out.println("Your city: " + line.getString("city"));
            return line.getString("city");
        } catch (Exception ipe) {
            System.out.println("Error in ip location: " + ipe.getMessage());
        }
        return null;
    }
}
