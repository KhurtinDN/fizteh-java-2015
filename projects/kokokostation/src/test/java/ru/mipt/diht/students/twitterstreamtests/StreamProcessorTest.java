package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.*;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.mipt.diht.students.twitterstream.StreamProcessor.fits;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 29.01.16.
 */
public class StreamProcessorTest {
    @SuppressWarnings ("unchecked")
    @Test
    public void test() throws Exception {
        GeocodingResult[] gcr = new GeocodingResult[1];
        gcr[0] = new GeocodingResult();
        gcr[0].geometry = new Geometry();
        gcr[0].geometry.bounds = new Bounds();
        gcr[0].geometry.bounds.southwest = new LatLng(359, 358);
        gcr[0].geometry.bounds.northeast = new LatLng(3, 4);

        Geocoding geocodingResultProducer = mock(Geocoding.class);
        when(geocodingResultProducer.getGeocodingResult("Nowhere")).thenReturn(gcr);

        Nearby nearby = mock(Nearby.class);
        when(nearby.nearby()).thenReturn(new GeoLocation(5.0, 5.0));

        List<BoxLocation> boxLocations = LocationGetter.getLocations(new BoxLocationLocationFactoryFactory().get(),
                "Nowhere", geocodingResultProducer, nearby);

        List<BoxLocation> emptyBoxLocations = new ArrayList<>();

        Status tweet = mock(Status.class, RETURNS_DEEP_STUBS);
        when(tweet.getGeoLocation()).thenReturn(new GeoLocation(50.0, 50.0))
                .thenReturn(new GeoLocation(0, 0))
                .thenReturn(null)
                .thenReturn(new GeoLocation(6.0, 6.0))
                .thenReturn(new GeoLocation(5.1, 5.1));
        when(tweet.isRetweet()).thenReturn(true).thenReturn(false);

        assertThat(fits(tweet, boxLocations), is(true));
        assertThat(fits(tweet, emptyBoxLocations), is(true));
        assertThat(fits(tweet, boxLocations), is(false));
        assertThat(fits(tweet, boxLocations), is(true));
        assertThat(fits(tweet, boxLocations), is(false));
        assertThat(fits(tweet, boxLocations), is(false));
        assertThat(fits(tweet, boxLocations), is(true));
    }
}