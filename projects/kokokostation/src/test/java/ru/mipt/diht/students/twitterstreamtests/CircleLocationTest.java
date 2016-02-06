package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.BoxLocation;
import ru.mipt.diht.students.twitterstream.CircleLocation;
import twitter4j.GeoLocation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 28.01.16.
 */
public class CircleLocationTest {
    @Test
    public void testValidCircleLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();
        gcr.geometry.bounds = new Bounds();
        gcr.geometry.bounds.southwest = new LatLng(359, 359);
        gcr.geometry.bounds.northeast = new LatLng(1, 1);

        CircleLocation circleLocation = new CircleLocation();
        BoxLocation boxLocation = new BoxLocation();
        boxLocation.fromGeocodingResult(gcr);

        assertThat(circleLocation.checkIfValid(), is(false));

        circleLocation.fromGeocodingResult(gcr);
        assertThat(circleLocation.checkIfValid(), is(true));
        assertThat(boxLocation.contains(circleLocation.getGeoLocation()), is(true));

        circleLocation.nearby(new GeoLocation(0.0, 0.0));
        assertThat(circleLocation.checkIfValid(), is(true));
    }

    @Test
    public void testInvalidCircleLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();

        CircleLocation circleLocation = new CircleLocation();
        assertThat(circleLocation.checkIfValid(), is(false));

        circleLocation.fromGeocodingResult(gcr);

        assertThat(circleLocation.checkIfValid(), is(false));
    }
}
