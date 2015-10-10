package ru.mipt.diht.students.ale3otik.twitter.structs;

import twitter4j.GeoLocation;

/**
 * Created by alex on 10.10.15.
 */
public class GeoLocationInfo {
    private GeoLocation location;
    private double radius;

    public GeoLocationInfo(GeoLocation newLocation, double newRadius) {
        setRadius(newRadius);
        setLocation(newLocation);
    }

    public final double getRadius() {
        return radius;
    }

    public final void setRadius(double newRadius) {
        this.radius = newRadius;
    }

    public final GeoLocation getLocation() {
        return location;
    }

    public final void setLocation(GeoLocation newLocation) {
        this.location = newLocation;
    }
}
