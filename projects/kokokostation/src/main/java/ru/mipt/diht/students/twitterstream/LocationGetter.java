package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

import java.util.ArrayList;
import java.util.List;
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
            if (newElement.checkIfValid()) {
                result.add(newElement);
            }
        }

        if (nearby != null) {
            GeoLocation nearbyProduction = nearby.get();
            if (nearbyProduction != null) {
                result.add(factory.nearby(nearbyProduction));
            }
        }

        return result;
    }
}
