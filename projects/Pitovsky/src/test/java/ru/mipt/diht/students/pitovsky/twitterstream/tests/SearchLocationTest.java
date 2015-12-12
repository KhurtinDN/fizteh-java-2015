package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import ru.mipt.diht.students.pitovsky.twitterstream.SearchLocation;
import ru.mipt.diht.students.pitovsky.twitterstream.SearchLocationException;

import org.junit.Test;
import junit.framework.TestCase;
import org.mockito.Mockito;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;

import java.util.ArrayList;

public class SearchLocationTest extends TestCase {
    
    public class PlaceList extends ArrayList<Place> implements ResponseList<Place> {
        
        public PlaceList() {
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
    public final void testSearchLocation() {
        ResponseList<Place> placeList = new PlaceList();
        Place smallPlace = Mockito.mock(Place.class);
        Mockito.when(smallPlace.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
            {new GeoLocation(0, 0), new GeoLocation(2, 0)},
            {new GeoLocation(0, 2), new GeoLocation(2, 2)}
        } );
        placeList.add(smallPlace);
        try {
            SearchLocation searchLocation = new SearchLocation(placeList);
            assertEquals(1, searchLocation.getCenter().getLatitude(), 0.01);
            assertEquals(1, searchLocation.getCenter().getLongitude(), 0.01);
            assertEquals(157, searchLocation.getRadius(), 15);
        } catch (SearchLocationException e) {
            fail(e.getMessage());
        }
        
        Place bigPlace = Mockito.mock(Place.class);
        Mockito.when(bigPlace.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
            {new GeoLocation(20, 20), new GeoLocation(40, 20)},
            {new GeoLocation(20, 40), new GeoLocation(40, 40)}
        } );
        placeList.add(bigPlace);
        try {
            SearchLocation searchLocation = new SearchLocation(placeList); //it must resolve the biggest place
            assertEquals(30, searchLocation.getCenter().getLatitude(), 0.01);
            assertEquals(30, searchLocation.getCenter().getLongitude(), 0.01);
        } catch (SearchLocationException e) {
            fail(e.getMessage());
        }
        
        placeList = new PlaceList();
        try {
            new SearchLocation(placeList);
            fail("No exception throwed");
        } catch (SearchLocationException e) {
            assertEquals("too few places in placelist", e.getMessage());
        }
    }
}
