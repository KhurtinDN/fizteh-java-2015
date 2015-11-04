package ru.mipt.diht.students.ale3otik.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.GeoLocationResolver;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by alex on 04.11.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest extends TestCase {
    private final String URLIPinfoAdress = "http://ipinfo.io/json";
    private final String URLGoogleAPIAdress = "http://maps.googleapis.com/maps/api/geocode/json";

    private final String DOLGOPRUDNYY = "Dolgoprudnyy";
    private final String DOLGOPRUDNYY_JSON = "/DolgoprudnyyIpInfo.json";

    private final String LONDON = "London";
    private final String LONDON_JSON = "/LondonGoogleApiData.json";

    private final Double LondonLatitude = 51.5073509;
    private final Double LondonLongitude = -0.1277583;
    private final Double LondonRadius = 23.539304731202712;

    private URL dummyGeoDataURL;
    private URL dummyNearbyLocationURL;

    @Before
    public void testPreparation() throws Exception {
        dummyGeoDataURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLGoogleAPIAdress
                        + "?sensor=false"
                        + "&address="
                        + LONDON
        ).thenReturn(dummyGeoDataURL);
        dummyNearbyLocationURL = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(URLIPinfoAdress).thenReturn(dummyNearbyLocationURL);
    }

    @Test
    public void testGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(DOLGOPRUDNYY_JSON);
        PowerMockito.when(dummyNearbyLocationURL.openStream()).thenReturn(inputStream);
        String location = GeoLocationResolver.getNameOfCurrentLocation();
        assertEquals(DOLGOPRUDNYY, location);
    }

    @Test
    public void testGetGeoLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(LONDON_JSON);
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationInfo location = GeoLocationResolver.getGeoLocation(LONDON);

        assertEquals(LondonLatitude, location.getLocation().getLatitude());
        assertEquals(LondonLongitude, location.getLocation().getLongitude());
        assertEquals(LondonRadius, location.getRadius());
    }
}
