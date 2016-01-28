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

        Assert.assertEquals(false, boxLocation.checkIfValid());

        boxLocation.fromGeocodingResult(gcr);
        Assert.assertEquals(true, boxLocation.checkIfValid());
        double[][] box = boxLocation.getBox();
        Assert.assertArrayEquals(new double[]{-1, -2, 3, 4},
                new double[]{box[0][0], box[0][1], box[1][0], box[1][1]}, 0);

        Assert.assertEquals(true, boxLocation.contains(new GeoLocation(0, 0)));
        Assert.assertEquals(false, boxLocation.contains(new GeoLocation(4, 0)));

        boxLocation.nearby(new GeoLocation(0.0, 0.0));
        Assert.assertEquals(true, boxLocation.checkIfValid());

        Assert.assertEquals(true, boxLocation.contains(new GeoLocation(0, 1)));
        Assert.assertEquals(false, boxLocation.contains(new GeoLocation(2.1, 0)));
    }

    @Test
    public void testInvalidBoxLocation() {
        GeocodingResult gcr = new GeocodingResult();
        gcr.geometry = new Geometry();

        BoxLocation boxLocation = new BoxLocation();

        Assert.assertEquals(false, boxLocation.checkIfValid());

        boxLocation.fromGeocodingResult(gcr);

        Assert.assertEquals(false, boxLocation.checkIfValid());

        Assert.assertEquals(false, boxLocation.contains(new GeoLocation(0.0, 0.0)));
    }
}
