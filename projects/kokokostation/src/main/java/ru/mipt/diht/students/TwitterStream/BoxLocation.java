package ru.mipt.diht.students.TwitterStream;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import javafx.util.Pair;
import twitter4j.GeoLocation;

/**
 * Created by mikhail on 28.01.16.
 */
public class BoxLocation implements Location {
    private static final double BOX_RADIUS = 2; //половина стороны квадратика в градусах

    private double[][] box;

    public double[][] getBox() {
        return box;
    }

    @Override
    public void fromGeocodingResult(GeocodingResult gcr) {
        Bounds bounds = gcr.geometry.bounds;

        if (bounds == null) {
            box = null;
        } else {
            box = new double[2][2];

            box[0][0] = bounds.southwest.lat;
            box[0][1] = bounds.southwest.lng;
            box[1][0] = bounds.northeast.lat;
            box[1][1] = bounds.northeast.lng;
        }
    }

    @Override
    public void nearby(Pair<Double, Double> center) {
        if (center == null) {
            box = null;
        } else {

            box = new double[2][2];

            box[0][0] = center.getKey() - BOX_RADIUS;
            box[0][1] = center.getValue() - BOX_RADIUS;
            box[1][0] = center.getKey() + BOX_RADIUS;
            box[1][1] = center.getValue() + BOX_RADIUS;
        }
    }

    @Override
    public boolean checkIfValid() {
        return box != null;
    }

    boolean contains(GeoLocation geoLocation) {
        if (!checkIfValid()) {
            return false;
        }

        double lat = geoLocation.getLatitude(),
                lng = geoLocation.getLongitude();
        return box[0][0] <= lat && lat <= box[1][0] && box[0][1] <= lng && lng <= box[1][1];
    }
}