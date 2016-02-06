package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.BoxLocation;
import twitter4j.GeoLocation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by mikhail on 28.01.16.
 */
public class BoxLocationTest {
    @Test
    public void testValidBoxLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();
        gcr.geometry.bounds = new Bounds();
        gcr.geometry.bounds.southwest = new LatLng(359, 358);
        gcr.geometry.bounds.northeast = new LatLng(3, 4);

        BoxLocation boxLocation = new BoxLocation();

        assertThat(boxLocation.checkIfValid(), is(false));

        boxLocation.fromGeocodingResult(gcr);
        assertThat(boxLocation.checkIfValid(), is(true));

        double[][] box = boxLocation.getBox();
        assertThat(box, arrayContaining(new double[][]{{359, 358}, {3, 4}}));

        assertThat(boxLocation.contains(new GeoLocation(0, 0)), is(true));
        assertThat(boxLocation.contains(new GeoLocation(4, 0)), is(false));

        boxLocation.nearby(new GeoLocation(0.0, 0.0));
        assertThat(boxLocation.checkIfValid(), is(true));

        assertThat(boxLocation.contains(new GeoLocation(0, 0.3)), is(true));
        assertThat(boxLocation.contains(new GeoLocation(0.6, 0)), is(false));
    }

    @Test
    public void testInvalidBoxLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();

        BoxLocation boxLocation = new BoxLocation();

        assertThat(boxLocation.checkIfValid(), is(false));

        boxLocation.fromGeocodingResult(gcr);

        assertThat(boxLocation.checkIfValid(), is(false));

        assertThat(boxLocation.contains(new GeoLocation(0.0, 0.0)), is(false));
    }
}
