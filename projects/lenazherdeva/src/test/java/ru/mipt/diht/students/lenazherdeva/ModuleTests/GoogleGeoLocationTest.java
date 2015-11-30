package ru.mipt.diht.students.lenazherdeva.moduleTests;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.lenazherdeva.twitterStream.GoogleGeoLocation;
import org.junit.Test;

/**
 * Created by admin on 20.11.2015.*/

 @RunWith(MockitoJUnitRunner.class)
    public class GoogleGeoLocationTest extends TestCase {
    static final double LATITUDEMoscow = 55.755826;
    static final double LONGTITUDEMoscow = 37.6173;
    static final double LATITUDELille = 50.62925;
    static final double LONGTITUDELille = 3.057256;

    @Test
    public void LocationTest() throws Exception {
        GoogleGeoLocation Location1;
        Location1 = new GoogleGeoLocation("Moscow");
        assert (LATITUDEMoscow ==
                Math.rint(Location1.getLocation().lat * 1000000.0)
                        / 1000000.0);
        assert (LONGTITUDEMoscow ==
                Math.rint(Location1.getLocation().lng * 1000000.0)
                        / 1000000.0);

        GoogleGeoLocation Location2;
        Location2 = new GoogleGeoLocation("Lille");
        assert (LATITUDELille ==
                Math.rint(Location2.getLocation().lat * 1000000.0)
                        / 1000000.0);
        assert (LONGTITUDELille ==
                Math.rint(Location2.getLocation().lng * 1000000.0)
                        / 1000000.0);
    }
}
