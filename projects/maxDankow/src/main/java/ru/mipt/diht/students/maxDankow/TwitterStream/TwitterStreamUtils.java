package ru.mipt.diht.students.maxDankow.TwitterStream;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import twitter4j.GeoLocation;
import twitter4j.Twitter;

import java.util.Calendar;
import java.util.Date;

public class TwitterStreamUtils {
    private static final long MINUTE_MS = 1000 * 60;
    private static final long HOUR_MS = MINUTE_MS * 60;
    private static final long DAY_MS = HOUR_MS * 24;

    public static String convertTimeToRussianWords(Date anotherDate) {
        Calendar currentTime = Calendar.getInstance();
        Calendar anotherTime = Calendar.getInstance();
        anotherTime.setTime(anotherDate);
        long timeDeltaMs = currentTime.getTime().getTime() - anotherTime.getTime().getTime();
        if (timeDeltaMs < 2 * MINUTE_MS) {
            return "Только что";
        }
        if (timeDeltaMs < HOUR_MS) {
            return "" + timeDeltaMs / MINUTE_MS + " минут назад";
        }
        if (currentTime.get(Calendar.DAY_OF_MONTH) == anotherTime.get(Calendar.DAY_OF_MONTH)) {
            return "" + timeDeltaMs / HOUR_MS + " часов назад";
        }
        currentTime.add(Calendar.DAY_OF_MONTH, -1);
        if (currentTime.before(anotherTime)) {
            return "Вчера";
        }
        return "" + timeDeltaMs / DAY_MS + " дней назад";
    }
    /*public static void findLocation(String placeName) {
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyCltC9cSKnrnqOApw5TQ155nwEBW-ZUt1E");
        GeocodingApiRequest result = GeocodingApi.geocode(context, placeName);
        //GeocodingResult[] locations;
        LatLng coords = null;
        try {
            GeocodingResult[] locations = result.await();
            coords = locations[0].geometry.location;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(coords.lat + " - " + coords.lng);
    }*/

    public static GeoLocation findLocation(Twitter twitter, String placeName){
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyCltC9cSKnrnqOApw5TQ155nwEBW-ZUt1E");
        GeocodingApiRequest geoRequest = GeocodingApi.geocode(context, placeName);
        LatLng coords = null;
        try {
            GeocodingResult[] locations = geoRequest.await();
            coords = locations[0].geometry.location;
        } catch (Exception e) {
            System.err.println("Google geolocation error.");
            System.exit(0);
        }
        return new GeoLocation(coords.lat, coords.lng);
    }

}
