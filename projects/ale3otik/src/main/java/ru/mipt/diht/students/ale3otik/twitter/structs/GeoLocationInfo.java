package ru.mipt.diht.students.ale3otik.twitter.structs;

import twitter4j.GeoLocation;

/**
 * Created by alex on 10.10.15.
 */
public class GeoLocationInfo {
    private GeoLocation location;
    private double radius;

    public GeoLocationInfo(final GeoLocation newLocation, final double newRadius) {
        setRadius(newRadius);
        setLocation(newLocation);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(final double newRadius) {
        this.radius = newRadius;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(final GeoLocation newLocation) {
        this.location = newLocation;
    }
}
