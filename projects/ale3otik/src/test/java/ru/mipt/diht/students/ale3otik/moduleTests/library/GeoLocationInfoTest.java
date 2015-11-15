package ru.mipt.diht.students.ale3otik.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

/**
 * Created by alex on 15.11.15.
 */
public class GeoLocationInfoTest extends TestCase {
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;

    @Test
    public void testGeoLocationClassMethods(){
        GeoLocationInfo moscowInfo = new GeoLocationInfo(
                new GeoLocation(MoscowLatitude, MoscowLongitude), MoscowRadius);

        assertEquals(moscowInfo.getRadius(),MoscowRadius);
        assertEquals(moscowInfo.getLocation(),new GeoLocation(MoscowLatitude, MoscowLongitude));

    }

}
