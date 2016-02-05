package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import twitter4j.GeoLocation;

/**
 * Created by mikhail on 28.01.16.
 */
public class BoxLocation implements Location {
    private static final double BOX_RADIUS = 0.5; //половина стороны квадратика в градусах

    private double[][] box;

    public double[][] getBox() {
        return box;
    }

    @Override
    public void fromGeocodingResult(GeocodingResult gcr) {
        if (gcr == null) {
            return;
        }

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
    public void nearby(GeoLocation center) {
        box = new double[2][2];

        box[0][0] = center.getLatitude() - BOX_RADIUS;
        box[0][1] = center.getLongitude() - BOX_RADIUS;
        box[1][0] = center.getLatitude() + BOX_RADIUS;
        box[1][1] = center.getLongitude() + BOX_RADIUS;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                box[i][j] = (box[i][j] % 360) + (box[i][j] < 0 ? 360 : 0);
            }
        }
    }

    @Override
    public boolean checkIfValid() {
        return box != null;
    }

    public boolean contains(GeoLocation geoLocation) {
        if (!checkIfValid()) {
            return false;
        }

        double lat = geoLocation.getLatitude(),
                lng = geoLocation.getLongitude();
        return between(box[0][0], box[1][0], lat) && between(box[0][1], box[1][1], lng);
    }

    private boolean between(double left, double right, double dot) {
        if (left <= right) {
            return left <= dot && dot <= right;
        } else {
            return left <= dot || dot <= right;
        }
    }
}