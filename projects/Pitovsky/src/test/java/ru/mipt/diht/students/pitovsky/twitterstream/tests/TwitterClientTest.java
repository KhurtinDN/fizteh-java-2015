package ru.mipt.diht.students.pitovsky.twitterstream.tests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import junit.framework.TestCase;

import org.mockito.Mockito;

import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils;
import ru.mipt.diht.students.pitovsky.twitterstream.ConsoleUtils.TextColor;
import ru.mipt.diht.students.pitovsky.twitterstream.TwitterClient;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterClientTest extends TestCase {
    
    private static Date localToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant());
    }
    
    private Place mockedPlace(GeoLocation[][] placeCoordinates, String name, String countryCode) {
        Place place = Mockito.mock(Place.class);
        Mockito.when(place.getBoundingBoxCoordinates()).thenReturn(placeCoordinates);
        Mockito.when(place.getFullName()).thenReturn(name);
        Mockito.when(place.getCountryCode()).thenReturn(countryCode);
        return place;
    }
    
    private Status mockedStatus(LocalDateTime time, String userName, String text,
            int retweetCount, Place place) {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getScreenName()).thenReturn(userName);
        
        Status status = Mockito.mock(Status.class);
        Mockito.when(status.getUser()).thenReturn(user);
        Mockito.when(status.getCreatedAt()).thenReturn(localToDate(time));
        Mockito.when(status.getPlace()).thenReturn(place);
        Mockito.when(status.getRetweetCount()).thenReturn(new Integer(retweetCount));
        Mockito.when(status.getText()).thenReturn(text);
        Mockito.when(status.isRetweet()).thenReturn(new Boolean(false));
        return status;
    }
    
    private Status mockedRetweetedStatus(LocalDateTime time, String userName, String text,
            int retweetCount, String authorName, Place place) {
        Status retweetedStatus = mockedStatus(time.minusHours(1), authorName, text, 0, place);
        
        Status thisStatus = mockedStatus(time, userName, "<empty text>", retweetCount, place);
        Mockito.when(thisStatus.isRetweet()).thenReturn(new Boolean(true));
        Mockito.when(thisStatus.getRetweetedStatus()).thenReturn(retweetedStatus);
        return thisStatus;
    }
    
    @Test
    public void testConvertDate() {
        Clock clock = Clock.fixed(LocalDateTime.of(2010, 1, 1, 15, 20).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        TwitterClient twitterClient = new TwitterClient(true, true, clock);
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        LocalDateTime testDateTime = currentDateTime.minusMinutes(1);
        
        twitterClient = new TwitterClient(true, true, clock);
        testDateTime = currentDateTime.minusMinutes(45);
        assertEquals("45 минут назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusMinutes(75);
        assertEquals("1 час назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusHours(13);
        assertEquals("13 часов назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusHours(18);
        assertEquals("вчера", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(1);
        assertEquals("вчера", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(1).minusHours(17);
        assertEquals("1 день назад", twitterClient.convertDate(localToDate(testDateTime)));
        testDateTime = currentDateTime.minusDays(44);
        assertEquals("44 дня назад", twitterClient.convertDate(localToDate(testDateTime)));
    }
    
    @Test
    public void testPrintTweets() throws TwitterException {
        PrintStream standartOut = System.out;
        OutputStream clientByteOut = new ByteArrayOutputStream();
        PrintStream clientOut = new PrintStream(clientByteOut, true);
        System.setOut(clientOut);
        Clock clock = Clock.fixed(LocalDateTime.of(2010, 1, 1, 15, 20).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC);
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        
        List<Status> tweetList = new ArrayList<Status>();
        tweetList.add(mockedStatus(currentDateTime.minusMinutes(1), "Someone", "First tweet!", 15, null));
        tweetList.add(mockedRetweetedStatus(currentDateTime.minusMinutes(15), "Another", "First retweet!", 25,
                "Metrofan", mockedPlace(new GeoLocation[][] {
            {new GeoLocation(20, 20), new GeoLocation(40, 20)},
            {new GeoLocation(20, 40), new GeoLocation(40, 40)}
        }, "MoscowSubway", "RU" )));
        
        QueryResult result = Mockito.mock(QueryResult.class);
        Mockito.when(result.nextQuery()).thenReturn(null);
        Mockito.when(result.getTweets()).thenReturn(tweetList);
        
        Twitter twitter = Mockito.mock(Twitter.class);
        Mockito.when(twitter.search(Mockito.any())).thenReturn(result);

        TwitterClient twitterClient = new TwitterClient(false, true, clock, twitter);
        twitterClient.printTweets("none", null, 100);
        
        System.setOut(standartOut);
        String[] tweets = clientByteOut.toString().split("[\n]");
        assertEquals(ConsoleUtils.colorizeString("[" +
            twitterClient.convertDate(localToDate(currentDateTime.minusMinutes(1))) + "]", TextColor.GREEN)
                + ConsoleUtils.colorizeString("@Someone", TextColor.BLUE) + ": First tweet! (15 ретвитов)",
            tweets[0]);
        assertEquals(ConsoleUtils.colorizeString("[" +
            twitterClient.convertDate(localToDate(currentDateTime.minusMinutes(15))) + "]", TextColor.GREEN)
                + ConsoleUtils.colorizeString("@Another", TextColor.BLUE) + " (ретвитнул "
                + ConsoleUtils.colorizeString("@Metrofan", TextColor.BLUE) + "): First retweet! (25 ретвитов)"
                + ConsoleUtils.colorizeString("<MoscowSubway:RU>", TextColor.MAGENTA),
            tweets[1]);
    }
}
