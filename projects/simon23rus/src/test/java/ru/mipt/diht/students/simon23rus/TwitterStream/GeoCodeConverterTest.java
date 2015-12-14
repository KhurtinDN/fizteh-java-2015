package ru.mipt.diht.students.simon23rus.TwitterStream;


import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import twitter4j.GeoLocation;
import twitter4j.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by semenfedotov on 06.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeoCodeConverterTest extends TestCase {

    @Test
    public void getCoordinatesTest() throws InterruptedException, JSONException, IOException {
        List<String> citys = new ArrayList<String>();
        citys.add("Краснодар");
        citys.add("Сочи");
        citys.add("Barcelona");
        citys.add("Deerfield_Beach");
        List<Double[]> coordinates = new ArrayList<Double[]>();
        coordinates.add(new Double[]{45.03926740000001, 38.987221});
        coordinates.add(new Double[]{43.60280789999999, 39.7341543});
        coordinates.add(new Double[]{41.3850639, 2.1734035});
        coordinates.add(new Double[]{26.3184123, -80.09976569999999});
        List<GeoLocation> locations = new ArrayList<GeoLocation>();
        locations.add(new GeoLocation(coordinates.get(0)[0], coordinates.get(0)[1]));
        locations.add(new GeoLocation(coordinates.get(1)[0], coordinates.get(1)[1]));
        locations.add(new GeoLocation(coordinates.get(2)[0], coordinates.get(2)[1]));
        locations.add(new GeoLocation(coordinates.get(3)[0], coordinates.get(3)[1]));
        assertEquals(locations.get(0), GeoCodeConverter.getCoordinates(citys.get(0)));
        assertEquals(locations.get(1), GeoCodeConverter.getCoordinates(citys.get(1)));
        assertEquals(locations.get(2), GeoCodeConverter.getCoordinates(citys.get(2)));
        assertEquals(locations.get(3), GeoCodeConverter.getCoordinates(citys.get(3)));
    }

    @Test
    public void sqrTest() {
        List<Double> dataProvided = new ArrayList<Double>();
        for(double i = 0; i < 10000D; ++i) {
            dataProvided.add(i * i);
            assertEquals(dataProvided.get((int)i), GeoCodeConverter.sqr(i));
        }

    }

    @Test
    public void nearTest() {
        List<String> citys = new ArrayList<String>();
        citys.add("Краснодар");
        citys.add("Сочи");
        citys.add("Долгопрудный");
        citys.add("Khimki");
        citys.add("Москва");
        List<Double[]> coordinates = new ArrayList<Double[]>();
        coordinates.add(new Double[]{45.03926740000001, 38.987221});
        coordinates.add(new Double[]{43.60280789999999, 39.7341543});
        coordinates.add(new Double[]{55.947064, 37.4992755});
        coordinates.add(new Double[]{55.8940553, 37.4439487});
        coordinates.add(new Double[]{55.755826, 37.6173});

        List<GeoLocation> locations = new ArrayList<GeoLocation>();
        locations.add(new GeoLocation(coordinates.get(0)[0], coordinates.get(0)[1]));
        locations.add(new GeoLocation(coordinates.get(1)[0], coordinates.get(1)[1]));
        locations.add(new GeoLocation(coordinates.get(2)[0], coordinates.get(2)[1]));
        locations.add(new GeoLocation(coordinates.get(3)[0], coordinates.get(3)[1]));
        locations.add(new GeoLocation(coordinates.get(4)[0], coordinates.get(4)[1]));

        assertEquals(false, GeoCodeConverter.near(locations.get(0), locations.get(1), 170)); //Krasnodar-Sochi
        assertEquals(true, GeoCodeConverter.near(locations.get(0), locations.get(1), 171));
        assertEquals(true, GeoCodeConverter.near(locations.get(2), locations.get(3), 10));//Dolgoprudniy-Khrimki
    }
}
