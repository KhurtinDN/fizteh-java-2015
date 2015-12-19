package ru.mipt.diht.students.annnvl.TwitterStream;


import org.junit.Test;
import org.junit.runner.RunWith;
import junit.framework.TestCase;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FindPlaceTest extends TestCase {

    static final double LAT_Moscow = 55.755826;
    static final double LONG_Moscow = 37.6173;

    static final double LAT_London = 51.5085300;
    static final double LONG_London = -0.1257400;

    static final double LAT_NewYork = 40.7142700;
    static final double LONG_NewYork = -74.0059700;

    @Test
    public void LocationTest() throws Exception {
        FindPlace location1 = new FindPlace("Moscow");
        assert (LAT_Moscow == Math.rint(location1.getLocation().lat * 1000000.0) / 1000000.0);
        assert (LONG_Moscow == Math.rint(location1.getLocation().lng * 1000000.0) / 1000000.0);

        FindPlace location2= new FindPlace("London");
        assert (LAT_London == Math.rint(location2.getLocation().lat * 1000000.0) / 1000000.0);
        assert (LONG_London == Math.rint(location2.getLocation().lng * 1000000.0) / 1000000.0);

        FindPlace location3= new FindPlace("NewYork");
        assert (LAT_NewYork == Math.rint(location3.getLocation().lat * 1000000.0) / 1000000.0);
        assert (LONG_NewYork == Math.rint(location3.getLocation().lng * 1000000.0) / 1000000.0);
    }
}
