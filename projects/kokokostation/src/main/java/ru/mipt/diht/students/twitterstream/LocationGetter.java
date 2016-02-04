package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import javafx.util.Pair;
import twitter4j.GeoLocation;
import twitter4j.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

public class LocationGetter {
    public static <T extends Location> List<T> getLocations(LocationFactory<T> factory, String location,
                                                            Function<String, GeocodingResult[]> geocodingResultProducer,
                                                            Supplier<GeoLocation> nearby) {
        List<T> result = new ArrayList<>();

        GeocodingResult[] results = geocodingResultProducer.apply(location);

        for (GeocodingResult geocodingResult : results) {
            T newElement = factory.fromGeocodingResult(geocodingResult);
            if (newElement != null) {
                result.add(newElement);
            }
        }

        if (nearby != null) {
            GeoLocation newElement = nearby.get();
            if (newElement != null) {
                result.add(factory.nearby(newElement));
            }
        }

        return result;
    }
}
