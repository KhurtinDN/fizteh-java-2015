package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LocationGetter {
    public static <T extends Location> List<T> getLocations(LocationFactory<T> factory, String location,
                                                            Geocoding geocodingResultProducer,
                                                            Nearby nearby) throws Exception {
        List<T> result = new ArrayList<>();

        GeocodingResult[] results = geocodingResultProducer.getGeocodingResult(location);

        for (GeocodingResult geocodingResult : results) {
            T newElement = factory.fromGeocodingResult(geocodingResult);
            if (newElement.checkIfValid()) {
                result.add(newElement);
            }
        }

        if (nearby != null) {
            GeoLocation nearbyProduction = nearby.nearby();
            if (nearbyProduction != null) {
                result.add(factory.nearby(nearbyProduction));
            }
        }

        return result;
    }
}
