package ru.mipt.diht.students.maxdankow.twitterstream;


import com.google.maps.model.Bounds;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.mipt.diht.students.maxdankow.twitterstream.utils.GeolocationUtils;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class GeolocationUtilsTest {
    @DataProvider
    public static Object[][] getDifferentLocationsProvider() {
        return new Object[][]{
                {
                        "Moscow",
                        new Double[][]{
                                {55.48992699999999, 37.3193288}, {56.009657, 37.9456611}
                        }
                },
                {
                        "Рыбинск",
                        new Double[][]{
                                {58.001714, 38.6495059}, {58.1202071, 38.974287}
                        }
                }
        };
    }

    @Test
    @UseDataProvider("getDifferentLocationsProvider")
    public void getBoxCoorginatesTest(String locationName, Double[][] expectedBox) {
        Bounds coordinates = GeolocationUtils.getLocationBoxCoordinates(locationName).bounds;
        Double[][] actualBox = new Double[][]{
                {coordinates.southwest.lat, coordinates.southwest.lng},
                {coordinates.northeast.lat, coordinates.northeast.lng},

        };
        for (int i = 0; i < expectedBox.length; ++i) {
            for (int j = 0; j < expectedBox[i].length; ++j) {
                assertTrue(Double.compare(expectedBox[i][j], actualBox[i][j]) == 0);
            }
        }
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
