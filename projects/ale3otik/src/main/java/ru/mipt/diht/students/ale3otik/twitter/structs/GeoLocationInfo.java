package ru.mipt.diht.students.ale3otik.twitter.structs;

import twitter4j.GeoLocation;

/**
 * Created by alex on 10.10.15.
 */
public final class GeoLocationInfo extends Object {
    private static final int HASH_PRIME = 1000000007;
    private static final int HASH_SHIFT = 1000000;

    private final GeoLocation location;
    private final double radius;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        GeoLocationInfo second = (GeoLocationInfo) obj;

        return this.location.equals(second.getLocation()) && (this.radius == second.getRadius());
    }

    @Override
    public int hashCode() {
        return this.getLocation().hashCode() + HASH_PRIME * (int) (HASH_SHIFT * this.radius);
    }
}
