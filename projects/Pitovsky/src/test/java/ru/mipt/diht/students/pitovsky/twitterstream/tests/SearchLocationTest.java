package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import org.junit.Test;
import junit.framework.TestCase;
import ru.mipt.diht.students.pitovsky.twitterstream.SearchLocation;
import ru.mipt.diht.students.pitovsky.twitterstream.SearchLocationException;
import twitter4j.GeoLocation;
import twitter4j.PagableResponseList;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
    
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
        Place smallPlace = mock(Place.class);
        when(smallPlace.getBoundingBoxCoordinates()).thenReturn(new GeoLocation[][] {
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
        placeList = new PlaceList();
        try {
            SearchLocation searchLocation = new SearchLocation(placeList);
            fail("No exception throwed");
        } catch (SearchLocationException e) {
            assertEquals("too few places in placelist", e.getMessage());
        }
    }
}
