package ru.mipt.diht.students.TwitterStream;

import com.google.maps.model.GeocodingResult;
import javafx.util.Pair;

/**
 * Created by mikhail on 28.01.16.
 */
public interface Location {
    void fromGeocodingResult(GeocodingResult gcr);

    void nearby(Pair<Double, Double> center);

    boolean checkIfValid();
}
