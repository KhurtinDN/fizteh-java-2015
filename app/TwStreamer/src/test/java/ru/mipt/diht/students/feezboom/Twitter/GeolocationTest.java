package ru.mipt.diht.students.feezboom.Twitter;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * * Created by avk on 19.12.15.
 **/
public class GeolocationTest extends TestCase {

    @Test
    public void testGetPlaceString() throws Exception {
        String city = FindGeolocation.getPlaceString();
        //assertEquals(city, "долгопрудный");
        //assertEquals(FindGeolocation.getPlaceStringAlternative(), "долгопрудный");
    }

    @Test
    public void testGeoUtils() {

    }
}
