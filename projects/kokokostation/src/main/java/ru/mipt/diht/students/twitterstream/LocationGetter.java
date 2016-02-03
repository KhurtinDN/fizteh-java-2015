package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import javafx.util.Pair;
import twitter4j.GeoLocation;
import twitter4j.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

interface LocationFactoryFactory<T extends Location> {
    LocationFactory<T> get();
}

/**
 * Created by mikhail on 16.12.15.
 */
class LocationFactory<T extends Location> {
    private final Class<T> type;

    LocationFactory(Class<T> type) {
        this.type = type;
    }

    T fromGeocodingResult(GeocodingResult gcr) {
        T location = null;
        try {
            location = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert location != null;
        location.fromGeocodingResult(gcr);
        return location;
    }

    T nearby(GeoLocation center) {
        T location = null;
        try {
            location = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert location != null;
        location.nearby(center);
        return location;
    }
}

class BoxLocationLocationFactoryFactory implements LocationFactoryFactory<BoxLocation> {
    @Override
    public LocationFactory<BoxLocation> get() {
        return new LocationFactory<>(BoxLocation.class);
    }
}

class CircleLocationLocationFactoryFactory implements LocationFactoryFactory<CircleLocation> {
    @Override
    public LocationFactory<CircleLocation> get() {
        return new LocationFactory<>(CircleLocation.class);
    }
}

public class LocationGetter {
    public static <T extends Location> List<T> getLocations(LocationFactory<T> factory, String location,
                                                            boolean nearby) {
        List<T> result = new ArrayList<>();

        try {
            GeocodingResult[] results = GoogleGeocoding.getGeocodingResult(location);

            for (GeocodingResult geocodingResult : results) {
                T newElement = factory.fromGeocodingResult(geocodingResult);
                if (newElement != null) {
                    result.add(newElement);
                }
            }
        } catch (Exception e) {
            System.err.println("GoogleGeocoding can't process a location: " + e.getMessage());
        }

        try {
            if (nearby) {
                result.add(factory.nearby(Nearby.nearby()));
            }
        } catch (IOException | JSONException e) {
            System.err.println("Nearby can't find your location: " + e.getMessage());
        }

        return result;
    }
}
