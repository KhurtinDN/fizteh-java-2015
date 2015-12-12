package ru.mipt.diht.students.maxDankow.TwitterStream;

import com.google.maps.model.Bounds;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.GeolocationUtils;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.TwitterStreamUtils;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterStreamTextUtilsTests {
    @Test
    public void testBuildUserName() {
        assertEquals("@User-Name123", TwitterStreamUtils.buildColorizedUserName("User-Name123"));
    }

    @Test
    public void testColorization() {
        assertEquals("\033[34mBlueText\033[0m",
                TwitterStreamUtils.colorizeText("BlueText", TwitterStreamUtils.TextColor.BLUE));
        assertEquals("\033[0mNo Color\033[0m",
                TwitterStreamUtils.colorizeText("No Color", TwitterStreamUtils.TextColor.CLEAR));
        assertEquals("\033[37m\033[0m",
                TwitterStreamUtils.colorizeText("", TwitterStreamUtils.TextColor.WHITE));
    }

    @Ignore
    @Test
    public void testDateToRussianWords() {
        Calendar now = new GregorianCalendar(2015, 0, 1, 0, 2);
        Calendar fiveMinutesAgo = new GregorianCalendar(2014, 11, 31, 23, 57);
        Calendar Days30Ago = new GregorianCalendar(2014, 11, 1, 0, 10);
        Calendar Days2Ago = new GregorianCalendar(2014, 11, 30, 0, 0);
        Calendar yesterday = new GregorianCalendar(2014, 11, 31, 0, 0);
        assertEquals("5 минут назад",
                TwitterStreamUtils.convertTimeToRussianWords(
                        fiveMinutesAgo.getTime(),now.getTime()));
        assertEquals("30 дней назад", TwitterStreamUtils.convertTimeToRussianWords(Days30Ago.getTime(),now.getTime()));
        assertEquals("2 дней назад", TwitterStreamUtils.convertTimeToRussianWords(Days2Ago.getTime(),now.getTime()));
        assertEquals("Вчера", TwitterStreamUtils.convertTimeToRussianWords(yesterday.getTime(),now.getTime()));
    }

    @Test
    public void testBuildFormattedTweet() {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, false);
        assertEquals("\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
    }
//    @Test
//    public void testBuildFormattedTweetWithDate() {
//        Status mockStatus = mock(Status.class);
//        User mockUser = mock(User.class);
//        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
//        when(mockStatus.getUser()).thenReturn(mockUser);
//        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
//        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, true);
//        assertEquals("[Только что]\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
//    }

    Status mockStatus = mock(Status.class);
    User mockUser = mock(User.class);

    @Before
    public void before() {
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
    }

    @Test
    public void testCheckTweetRetweet() {
        when(mockStatus.isRetweet()).thenReturn(true);
        assertFalse(TwitterStreamUtils.checkTweet(mockStatus, null, true));
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, false));

        when(mockStatus.isRetweet()).thenReturn(false);
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, true));
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, false));
    }

    @Test
    public void testCheckLocation() {
        Geometry mockGeometry = mock(Geometry.class);
        Bounds mockBounds = mock(Bounds.class);
        Place mockPlace = mock(Place.class);
        mockGeometry.bounds = mockBounds;// = new LatLng(32.50, -45.90);
        mockBounds.northeast = new LatLng(32.50, -45.90);
        mockBounds.southwest = new LatLng(32.00, -46.00);
        when(mockStatus.getPlace()).thenReturn(mockPlace);

        when(mockPlace.getBoundingBoxCoordinates()).thenReturn(
                new GeoLocation[][]{{new GeoLocation(32.25, -45.97)}});
        assertTrue(GeolocationUtils.checkLocation(mockPlace, mockGeometry));

        when(mockPlace.getBoundingBoxCoordinates()).thenReturn(
                new GeoLocation[][]{{new GeoLocation(32.25, 45.97)}});
        assertFalse(GeolocationUtils.checkLocation(mockPlace, mockGeometry));

        assertFalse(GeolocationUtils.checkLocation(null, mockGeometry));
    }
}
