package ru.mipt.diht.students.tveritinova.moduletests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.tveritinova.TwitterStream.Location;

public class LocationTest extends TestCase{

    @Test
    public void testLocation() {

        Location l1 = new Location("Moscow");

        assertEquals(55.755825,
                Math.rint(l1.getGeoLocation().getLatitude() * 1000000.0)
                        / 1000000.0);
        assertEquals(37.617298,
                Math.rint(l1.getGeoLocation().getLongitude() * 1000000.0)
                / 1000000.0);

        Location l2 = new Location("London");

        assertEquals(51.507351,
                Math.rint(l2.getGeoLocation().getLatitude() * 1000000.0)
                / 1000000.0);
        assertEquals(-0.127758,
                Math.rint(l2.getGeoLocation().getLongitude() * 1000000.0)
                / 1000000.0);
    }
}
