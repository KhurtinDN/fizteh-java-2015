package ru.mipt.diht.students.ale3otik.moduletests.library;

import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 15.11.15.
 */
public class GeoLocationInfoTest {
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;

    @Test
    public void testGeoLocationClassMethods() {
        GeoLocationInfo moscowInfo = new GeoLocationInfo(
                new GeoLocation(MoscowLatitude, MoscowLongitude), MoscowRadius);
        GeoLocationInfo moscowInfo2 = new GeoLocationInfo(
                new GeoLocation(MoscowLatitude, MoscowLongitude), MoscowRadius);

        assertThat(moscowInfo.getRadius(), equalTo(MoscowRadius));
        assertThat(moscowInfo.getLocation(), equalTo(new GeoLocation(MoscowLatitude, MoscowLongitude)));

        assertThat(moscowInfo.hashCode(), equalTo(moscowInfo2.hashCode()));
        assertThat(moscowInfo.equals(moscowInfo2), equalTo(true));
        assertThat(moscowInfo.equals(null), equalTo(false));
        assertThat(moscowInfo.equals(new ArrayList()), equalTo(false));
    }
}
