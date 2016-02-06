package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;

/**
 * Created by mikhail on 06.02.16.
 */
public interface Geocoding {
    GeocodingResult[] getGeocodingResult(String location) throws Exception;
}
