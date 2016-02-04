package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.BoxLocation;
import ru.mipt.diht.students.twitterstream.CircleLocation;
import twitter4j.GeoLocation;

import static org.junit.Assert.*;

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

        assertEquals(false, circleLocation.checkIfValid());

        circleLocation.fromGeocodingResult(gcr);
        assertEquals(true, circleLocation.checkIfValid());
        assertEquals(true, boxLocation.contains(circleLocation.getGeoLocation()));

        circleLocation.nearby(new GeoLocation(0.0, 0.0));
        assertEquals(true, circleLocation.checkIfValid());
    }

    @Test
    public void testInvalidCircleLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();

        CircleLocation circleLocation = new CircleLocation();
        assertEquals(false, circleLocation.checkIfValid());

        circleLocation.fromGeocodingResult(gcr);

        assertEquals(false, circleLocation.checkIfValid());
    }
}
