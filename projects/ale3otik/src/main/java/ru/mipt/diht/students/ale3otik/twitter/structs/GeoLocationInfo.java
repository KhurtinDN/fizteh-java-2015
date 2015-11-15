package ru.mipt.diht.students.ale3otik.twitter.structs;

import twitter4j.GeoLocation;

/**
 * Created by alex on 10.10.15.
 */
public final class GeoLocationInfo {
    private GeoLocation location;
    private double radius;

    public GeoLocationInfo(final GeoLocation newLocation,
                           final double newRadius) {
        radius = newRadius;
        location = newLocation;
    }

    public double getRadius() {
        return radius;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
