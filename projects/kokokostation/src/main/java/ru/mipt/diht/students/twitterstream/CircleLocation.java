package ru.mipt.diht.students.twitterstream;

import com.google.maps.model.GeocodingResult;
import javafx.util.Pair;
import twitter4j.GeoLocation;

/**
 * Created by mikhail on 28.01.16.
 */
public class CircleLocation implements Location {
    private static final double EARTH_RADIUS = 6371;

    private GeoLocation geoLocation;
    private double radius;

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public double getRadius() {
        return radius;
    }

    private void fromBox(double[][] box) {
        if (box != null) {
            double nla = box[1][0],
                    nln = box[1][1],
                    sla = box[0][0],
                    sln = box[0][1];

            double latitude = (nla + sla) / 2,
                    longitude = (nln + sln) / 2,
                    longitudeRadius = (Math.toRadians(nln - sln)
                            * (EARTH_RADIUS * Math.cos(Math.toRadians(latitude)))) / 2,
                    latitudeRadius = Math.toRadians(nla - sla) * EARTH_RADIUS / 2;

            geoLocation = new GeoLocation(latitude, longitude);
            radius = (longitudeRadius + latitudeRadius) / 2;
        }
    }

    @Override
    public void fromGeocodingResult(GeocodingResult gcr) {
        fromBox(new BoxLocationLocationFactoryFactory().get().fromGeocodingResult(gcr).getBox());
    }

    @Override
    public void nearby(GeoLocation center) {
        fromBox(new BoxLocationLocationFactoryFactory().get().nearby(center).getBox());
    }

    @Override
    public boolean checkIfValid() {
        return radius != 0;
    }
}
