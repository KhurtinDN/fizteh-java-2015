package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.Math.abs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.mipt.diht.students.semyonkozloff
        .moduletests.library.LocationSearch.*;

@RunWith(DataProviderRunner.class)
public class LocationSearchTest extends TestCase {

    @DataProvider
    public static Object[][] locationsDataProvider() throws Exception {
        return new Object[][] { // (c) max-dankow
                {
                        "Moscow",
                        new LatLng(55.48992699999999, 37.3193288),
                        new LatLng(56.009657, 37.9456611)
                },
                {
                        "Рыбинск",
                        new LatLng(58.001714, 38.6495059),
                        new LatLng(58.1202071, 38.974287)
                }
        };
    }

    @Test
    @UseDataProvider("locationsDataProvider")
    public void testFindLocation(String location,
                                 LatLng expectedSouthwestPoint,
                                 LatLng expectedNortheastPoint)
            throws Exception {
        Geometry geometry = findLocation(location);

        LatLng southwest = geometry.bounds.southwest;
        LatLng northeast = geometry.bounds.northeast;
        final double EPSILON = 0.1;

        assertTrue(abs(southwest.lat - expectedSouthwestPoint.lat) < EPSILON
                && abs(southwest.lng - expectedSouthwestPoint.lng) < EPSILON
                && abs(northeast.lat - expectedNortheastPoint.lat) < EPSILON
                && abs(northeast.lng - expectedNortheastPoint.lng) < EPSILON);


    }

    @Test(expected = Exception.class)
    public void testFindUnknownLocation() throws Exception {
        findLocation("tratatatatunknownlocation");
    }

    @DataProvider
    public static Object[][] coordinatesDistanceDataProvider() {
        return new Object[][] {
                {
                        new LatLng(54.814445, 43.660755),
                        new LatLng(55.242637, 34.608021),
                        572.58
                },
                {
                        new LatLng(55.731827, 39.133918),
                        new LatLng(55.789031, 39.435015),
                        19.87
                },
                {
                        new LatLng(55.732780, 37.656361),
                        new LatLng(55.755610, 37.542214),
                        7.58
                }
        };
    }

    @Test
    @UseDataProvider("coordinatesDistanceDataProvider")
    public void testComputeCoordinatesDistance(LatLng a, LatLng b,
                                               double expectedDistance) {
        double computedDistance = computeCoordinatesDistance(a, b);
        double epsilon = 0.01 * computedDistance;

        assertTrue(abs(computedDistance - expectedDistance) < epsilon);
    }

}
