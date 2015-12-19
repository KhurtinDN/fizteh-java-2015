package ru.mipt.diht.students.ale3otik.moduletests.library;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.GeoLocationResolver;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;

import java.io.InputStream;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 04.11.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, GeoLocationResolver.class})
public class GeoLocationResolverTest {
    private static final String URLIPinfoAdress = "http://ipinfo.io/json";
    private static final String URLGoogleAPIAdress = "http://maps.googleapis.com/maps/api/geocode/json";

    private static final String DOLGOPRUDNYY = "Dolgoprudnyy";
    private static final String DOLGOPRUDNYY_JSON = "/DolgoprudnyyIpInfo.json";
    private static final String INVALID_JSON = "/Invalid.json";


    private static final String LONDON = "London";
    private static final String LONDON_JSON = "/LondonGoogleApiData.json";

    private static final Double LondonLatitude = 51.5073509;
    private static final Double LondonLongitude = -0.1277583;
    private static final Double LondonRadius = 23.539304731202712;

    private URL dummyGeoDataURL;
    private URL dummyNearbyLocationURL;

    @Before
    public void setUp() throws Exception {
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
    public void testSucceedGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(DOLGOPRUDNYY_JSON);
        PowerMockito.when(dummyNearbyLocationURL.openStream()).thenReturn(inputStream);
        String location = GeoLocationResolver.getNameOfCurrentLocation();
        assertThat(DOLGOPRUDNYY, equalTo(location));
    }

    @Test(expected = LocationException.class)
    public void testFailedGetNameOfCurrentLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(INVALID_JSON);
        PowerMockito.when(dummyNearbyLocationURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver.getNameOfCurrentLocation();
    }

    @Test
    public void testSucceedGetGeoLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(LONDON_JSON);
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationInfo location = GeoLocationResolver.getGeoLocation(LONDON);

        assertThat(LondonLatitude, equalTo(location.getLocation().getLatitude()));
        assertThat(LondonLongitude, equalTo(location.getLocation().getLongitude()));
        assertThat(LondonRadius, equalTo(location.getRadius()));
    }

    @Test(expected = LocationException.class)
    public void testFailedGetGeoLocation() throws Exception {
        InputStream inputStream = GeoLocationResolverTest.class.getResourceAsStream(INVALID_JSON);
        PowerMockito.when(dummyGeoDataURL.openStream()).thenReturn(inputStream);
        GeoLocationResolver.getGeoLocation(LONDON);
    }
}
