package ru.mipt.diht.students.IrinaMudrova.Twitter.library;



import ru.mipt.diht.students.IrinaMudrova.Twitter.library.exceptions.PlaceNotFoundException;
import org.junit.*;

import static org.junit.Assert.*;

public class YandexPlacesTest {

    @Test
    public void testCalculations() throws PlaceNotFoundException {
        YandexPlaces places = new YandexPlaces();
        places.setPlaceQueryByGeoCodeXML(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<ymaps>\n"
                        + "<lowerCorner>83.50535 53.183755</lowerCorner>\n"
                        + "<upperCorner>83.864954 53.452658</upperCorner>\n"
                        + "<pos>83.779875 53.348053</pos>\n"
                        + "</ymaps>\n");
        double coord[] = places.calcCoord(),
                bnds[][] = places.calcBounds(),
                radius = places.calcRadiusKm();
        assertTrue(new Double(83) < coord[0] && coord[0] < new Double(84));
        assertTrue(new Double(53) < coord[1] && coord[1] < new Double(54));
        //System.err.println(radius);
        assertTrue(new Double(10) < radius && radius < new Double(300));
        assertTrue(bnds[0][0] < coord[0] && bnds[0][1] < coord[1]);
        assertTrue(bnds[1][0] > coord[0] && bnds[1][1] > coord[1]);
    }
}
