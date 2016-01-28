package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

/**
 * Created by mikhail on 28.01.16.
 */
public interface Location {
    void fromGeocodingResult(GeocodingResult gcr);

    void nearby(GeoLocation center);

    boolean checkIfValid();
}
