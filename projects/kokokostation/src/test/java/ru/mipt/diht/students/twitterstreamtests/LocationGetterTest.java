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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class LocationGetterTest {
    @SuppressWarnings ("unchecked")
    @Test
    public void test() {
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

        Function<String, GeocodingResult[]> geocodingResultProducer =
                (Function<String, GeocodingResult[]>) mock(Function.class);

        Supplier<GeoLocation> nearby = (Supplier<GeoLocation>) mock(Supplier.class);

        when(geocodingResultProducer.apply(anyString())).thenReturn(gcr);
        when(nearby.get()).thenReturn(new GeoLocation(0.0, 0.0));

        List<BoxLocation> boxLocations = LocationGetter.getLocations(new BoxLocationLocationFactoryFactory().get(),
                "Nowhere", geocodingResultProducer, nearby);

        assertEquals(true, Arrays.deepEquals(boxLocations.get(0).getBox(), new double[][]{new double[]{359, 358},
                new double[]{3, 4}}));

        assertEquals(true, Arrays.deepEquals(boxLocations.get(2).getBox(), new double[][]{new double[]{359.5, 359.5},
                new double[]{0.5, 0.5}}));

        List<CircleLocation> circleLocations = LocationGetter.getLocations(
                new CircleLocationLocationFactoryFactory().get(),
                "Nowhere", geocodingResultProducer, nearby);

        assertEquals(new GeoLocation(0.5, 0.5), circleLocations.get(1).getGeoLocation());

        assertEquals(new GeoLocation(0, 0), circleLocations.get(2).getGeoLocation());
    }
}