package util;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import org.junit.Test;
import twitter4j.GeoLocation;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by lokotochek on 13.12.15.
 */
public class LocationSearchTest {

    @Test
    public void testGetGeoCenter() throws Exception {
        String place = "Moscow";
        GeoLocation moscowLocation = LocationSearch.getGeoCenter(place);
        String correctLocation = "GeoLocation{latitude=55.75582504272461, longitude=37.6172981262207}";
        assertEquals(correctLocation, moscowLocation.toString());
    }

    @Test
    public void testGetGeoBox() throws Exception {
        String place = "Moscow";
        double[][] moscowBox = LocationSearch.getGeoBox(place);
        double[][] correctBox = {
                {55.48992699999999, 37.3193288},
                {56.009657, 37.9456611} };
        assertArrayEquals(correctBox, moscowBox);
    }

}