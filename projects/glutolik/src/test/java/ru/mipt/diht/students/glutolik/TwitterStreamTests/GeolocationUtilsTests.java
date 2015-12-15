package ru.mipt.diht.students.glutolik.TwitterStreamTests;


import com.google.maps.model.Bounds;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import ru.mipt.diht.students.glutolik.TwitterStream.GeolocationUtils;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by glutolik on 15.12.15.
 */

public class GeolocationUtilsTests {
    @Test
    public void testGetCoordinates() {
        List<String> cities = new ArrayList<>();
        cities.add("Москва");
        cities.add("Сочи");
        cities.add("Ташкент");
        List<Double[]> coordinates = new ArrayList<>();
        coordinates.add(new Double[]{55.755826, 37.6173});
        coordinates.add(new Double[]{43.602806091308594, 39.734153747558594});
        coordinates.add(new Double[]{-41.3, -110.733333});
        List<GeoLocation> locations = new ArrayList<>();
        locations.add(new GeoLocation(coordinates.get(0)[0], coordinates.get(0)[1]));
        locations.add(new GeoLocation(coordinates.get(1)[0], coordinates.get(1)[1]));
        locations.add(new GeoLocation(coordinates.get(2)[0], coordinates.get(2)[1]));
        assertEquals(locations.get(0), GeolocationUtils.getCoordinates(cities.get(0)));
        assertEquals(locations.get(1), GeolocationUtils.getCoordinates(cities.get(1)));
        assertEquals(locations.get(2), GeolocationUtils.getCoordinates(cities.get(2)));
    }

    Status mockStatus = mock(Status.class);

    @Test
    public void testCheckLocation() {
        Geometry mockGeometry = mock(Geometry.class);
        Bounds mockBounds = mock(Bounds.class);
        Place mockPlace = mock(Place.class);
        mockGeometry.bounds = mockBounds;// = new LatLng(32.50, -45.90);
        mockBounds.northeast = new LatLng(32.50, -45.90);
        mockBounds.southwest = new LatLng(32.00, -46.00);
        when(mockStatus.getPlace()).thenReturn(mockPlace);

        when(mockPlace.getBoundingBoxCoordinates()).thenReturn(
                new GeoLocation[][]{{new GeoLocation(32.25, -45.97)}});
        assertTrue(GeolocationUtils.checkLocation(mockPlace, mockGeometry));

        when(mockPlace.getBoundingBoxCoordinates()).thenReturn(
                new GeoLocation[][]{{new GeoLocation(32.25, 45.97)}});
        assertFalse(GeolocationUtils.checkLocation(mockPlace, mockGeometry));

        assertFalse(GeolocationUtils.checkLocation(null, mockGeometry));
    }
}
