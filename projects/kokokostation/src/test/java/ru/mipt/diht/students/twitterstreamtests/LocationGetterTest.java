package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.*;
import twitter4j.GeoLocation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class LocationGetterTest {
    @Test
    public void test() throws Exception {
        GeocodingResult[] gcr = new GeocodingResult[2];
        for (int i = 0; i < 2; i++) {
            gcr[i] = new GeocodingResult();
            gcr[i].geometry = new Geometry();
            gcr[i].geometry.bounds = new Bounds();
        }
        gcr[0].geometry.bounds.southwest = new LatLng(359, 358);
        gcr[0].geometry.bounds.northeast = new LatLng(3, 4);
        gcr[1].geometry.bounds.southwest = new LatLng(0, 0);
        gcr[1].geometry.bounds.northeast = new LatLng(1, 1);

        Geocoding geocodingResultProducer = mock(Geocoding.class);

        Nearby nearby = mock(Nearby.class);

        when(geocodingResultProducer.getGeocodingResult(anyString())).thenReturn(gcr);
        when(nearby.nearby()).thenReturn(new GeoLocation(0.0, 0.0));

        List<BoxLocation> boxLocations = LocationGetter.getLocations(new BoxLocationLocationFactoryFactory().get(),
                "Nowhere", geocodingResultProducer, nearby);

        assertThat(boxLocations.get(0).getBox(), arrayContaining(new double[][]{{359, 358}, {3, 4}}));

        assertThat(boxLocations.get(2).getBox(), arrayContaining(new double[][]{{359.5, 359.5}, {0.5, 0.5}}));

        List<CircleLocation> circleLocations = LocationGetter.getLocations(
                new CircleLocationLocationFactoryFactory().get(),
                "Nowhere", geocodingResultProducer, nearby);

        assertThat(circleLocations.get(1).getGeoLocation(), is(new GeoLocation(0.5, 0.5)));

        assertThat(circleLocations.get(2).getGeoLocation(), is(new GeoLocation(0, 0)));
    }
}