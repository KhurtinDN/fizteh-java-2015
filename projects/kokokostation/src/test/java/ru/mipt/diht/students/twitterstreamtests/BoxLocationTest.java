package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import ru.mipt.diht.students.twitterstream.BoxLocation;
import twitter4j.GeoLocation;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 28.01.16.
 */
public class BoxLocationTest {
    @Test
    public void testValidBoxLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();
        gcr.geometry.bounds = new Bounds();
        gcr.geometry.bounds.southwest = new LatLng(-1, -2);
        gcr.geometry.bounds.northeast = new LatLng(3, 4);

        BoxLocation boxLocation = new BoxLocation();

        assertEquals(false, boxLocation.checkIfValid());

        boxLocation.fromGeocodingResult(gcr);
        assertEquals(true, boxLocation.checkIfValid());
        double[][] box = boxLocation.getBox();
        assertArrayEquals(new double[]{-1, -2, 3, 4},
                new double[]{box[0][0], box[0][1], box[1][0], box[1][1]}, 0);

        assertEquals(true, boxLocation.contains(new GeoLocation(0, 0)));
        assertEquals(false, boxLocation.contains(new GeoLocation(4, 0)));

        boxLocation.nearby(new GeoLocation(0.0, 0.0));
        assertEquals(true, boxLocation.checkIfValid());

        assertEquals(true, boxLocation.contains(new GeoLocation(0, 1)));
        assertEquals(false, boxLocation.contains(new GeoLocation(2.1, 0)));
    }

    @Test
    public void testInvalidBoxLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();

        BoxLocation boxLocation = new BoxLocation();

        assertEquals(false, boxLocation.checkIfValid());

        boxLocation.fromGeocodingResult(gcr);

        assertEquals(false, boxLocation.checkIfValid());

        assertEquals(false, boxLocation.contains(new GeoLocation(0.0, 0.0)));
    }
}
