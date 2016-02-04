package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

/**
 * Created by mikhail on 16.12.15.
 */
public class LocationFactory<T extends Location> {
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
