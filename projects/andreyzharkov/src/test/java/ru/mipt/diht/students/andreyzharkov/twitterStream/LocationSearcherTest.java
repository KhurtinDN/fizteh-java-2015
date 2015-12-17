package ru.mipt.diht.students.andreyzharkov.twitterStream;

import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;

import java.util.ArrayList;

/**
 * Created by Андрей on 13.12.2015.
 */
public class LocationSearcherTest extends TestCase{
    public class TestResponseList extends ArrayList<Place> implements ResponseList<Place> {

        public TestResponseList() {
            super();
        }

        @Override
        public int getAccessLevel() {
            return 0;
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return null;
        }

    }

    @Test
    public final void testLocationSearcher() {
        ResponseList<Place> placeList = new TestResponseList();
        Place street = Mockito.mock(Place.class);
        Mockito.when(street.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
                {new GeoLocation(0, 0.5), new GeoLocation(0.5, 0)},
                {new GeoLocation(0, 0.5), new GeoLocation(0.5, 0.5)}
        } );
        placeList.add(street);
        try {
            LocationSearcher searchLocation = new LocationSearcher(placeList);
            assertEquals(0.25, searchLocation.getCenter().getLatitude(), 0.01);
            assertEquals(0.375, searchLocation.getCenter().getLongitude(), 0.01);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Place village = Mockito.mock(Place.class);
        Mockito.when(village.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
                {new GeoLocation(0, 2), new GeoLocation(2, 0)},
                {new GeoLocation(0, 2), new GeoLocation(2, 2)}
        } );
        placeList.add(village);
        try {
            LocationSearcher searchLocation = new LocationSearcher(placeList);
            assertEquals(0.625, searchLocation.getCenter().getLatitude(), 0.01);
            assertEquals(0.93, searchLocation.getCenter().getLongitude(), 0.01);
            assertEquals(140, searchLocation.getRadius(), 20);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Place city = Mockito.mock(Place.class);
        Mockito.when(city.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
                {new GeoLocation(0, 5), new GeoLocation(5, 0)},
                {new GeoLocation(0, 5), new GeoLocation(5, 5)}
        } );
        placeList.add(city);
        try {
            LocationSearcher searchLocation = new LocationSearcher(placeList);
            assertEquals(1.25, searchLocation.getCenter().getLatitude(), 0.01);
            assertEquals(1.87, searchLocation.getCenter().getLongitude(), 0.01);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Place region = Mockito.mock(Place.class);
        Mockito.when(region.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
                {new GeoLocation(20, 20), new GeoLocation(40, 20)},
                {new GeoLocation(20, 40), new GeoLocation(40, 40)}
        } );
        placeList.add(region);
        try {
            LocationSearcher searchLocation = new LocationSearcher(placeList); //it must resolve the biggest place
            assertEquals(8.5, searchLocation.getCenter().getLatitude(), 0.1);
            assertEquals(9, searchLocation.getCenter().getLongitude(), 0.1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        placeList = new TestResponseList();
        try {
            new LocationSearcher(placeList);
            fail("No exception throwed");
        } catch (Exception e) {
            assertEquals("Too few places in placelist.", e.getMessage());
        }
    }
}
