package ru.mipt.diht.students.twitterstreamtests;

import com.google.maps.model.Bounds;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.twitterstream.*;
import twitter4j.*;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.object.HasToString.hasToString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by mikhail on 29.01.16.
 */

public class QueryProcessorTest {
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws TwitterException {
        CharArrayWriter writer = new CharArrayWriter();
        ArgumentInfo argumentInfo = new ArgumentInfo(new String[]{"-q", "test", "-p", "Nowhere", "--nearby"});
        OutputManager outputManager = new OutputManager(argumentInfo, writer);

        GeocodingResult[] gcr = new GeocodingResult[1];
            gcr[0] = new GeocodingResult();
            gcr[0].geometry = new Geometry();
            gcr[0].geometry.bounds = new Bounds();
        gcr[0].geometry.bounds.southwest = new LatLng(359, 358);
        gcr[0].geometry.bounds.northeast = new LatLng(3, 4);

        Function<String, GeocodingResult[]> geocodingResultProducer =
                (Function<String, GeocodingResult[]>) mock(Function.class);
        when(geocodingResultProducer.apply("Nowhere")).thenReturn(gcr);

        Supplier<GeoLocation> nearby = (Supplier<GeoLocation>) mock(Supplier.class);
        when(nearby.get()).thenReturn(new GeoLocation(0.0, 0.0));

        Status tweet = mock(Status.class, RETURNS_DEEP_STUBS);
        when(tweet.getText()).thenReturn("Поел");
        when(tweet.getUser().getName()).thenReturn("Петя").thenReturn("Дима").thenReturn("Коля");
        Date testDate = new Date();
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, -5);
        testDate.setTime(time.getTimeInMillis());
        when(tweet.getCreatedAt()).thenReturn(testDate);
        when(tweet.isRetweet()).thenReturn(false);
        when(tweet.getRetweetCount()).thenReturn(63);
        List<Status> tweets = new ArrayList<>();
        tweets.add(tweet);

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getTweets()).thenReturn(tweets);
        when(queryResult.hasNext()).thenReturn(true).thenReturn(false);

        Query query = new Query("test").geoCode(new GeoLocation(1.0, 1.0), 277.96191332252783,
                String.valueOf(twitter4j.Query.KILOMETERS));
        Query nearbyQuery = new Query("test").geoCode(new GeoLocation(0.0, 0.0), 55.59746332227937,
                String.valueOf(twitter4j.Query.KILOMETERS));

        when(queryResult.nextQuery()).thenReturn(query);

        Twitter twitter = mock(Twitter.class);
        when(twitter.search(any(Query.class))).thenReturn(queryResult);

        QueryProcessor queryProcessor =
                new QueryProcessor(outputManager, argumentInfo, twitter, geocodingResultProducer, nearby);

        queryProcessor.process();

        assertEquals("5 минут назад @\033[34mПетя\033[0m: Поел (63 ретвита)" + System.lineSeparator() +
                        "5 минут назад @\033[34mДима\033[0m: Поел (63 ретвита)" + System.lineSeparator() +
                        "5 минут назад @\033[34mКоля\033[0m: Поел (63 ретвита)" + System.lineSeparator(),
                writer.toString());

        verify(twitter, times(2)).search(argThat(hasToString(query.toString())));
        verify(twitter).search(argThat(hasToString(nearbyQuery.toString())));
    }
}