package ru.fizteh.fivt.students.tulindanil.twitterstream.library;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.fizteh.fivt.students.tulindanil.twitterstream.TwitterStream;
import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.InvalidLocationException;
import ru.fizteh.fivt.students.tulindanil.twitterstream.library.exceptions.LocationDefinitionErrorException;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by tulindanil on 12.10.15.
 */
public class PlaceLocationResolverTest_whole {

    private static String normalGoogleResponce;
    private static String badKeyGoogleResponce;
    private static String noResultsGoogleResponce;
    private static String queryLimitGoogleResponce;

    @BeforeClass
    public static void loadSamplesGoogle() throws Exception {
        try (InputStream inputStream
                     = TwitterStream.class
                .getResourceAsStream("/googleResponce.json")) {
            normalGoogleResponce = IOUtils.toString(inputStream);
        }
        try (InputStream inputStream
                     = TwitterStream.class
                .getResourceAsStream("/googleBadKey.json")) {
            badKeyGoogleResponce = IOUtils.toString(inputStream);
        }
        try (InputStream inputStream
                     = TwitterStream.class
                .getResourceAsStream("/googleZeroResults.json")) {
            noResultsGoogleResponce = IOUtils.toString(inputStream);
        }
        try (InputStream inputStream
                     = TwitterStream.class
                .getResourceAsStream("/googleQueryLimit.json")) {
            queryLimitGoogleResponce = IOUtils.toString(inputStream);
        }
    }

    @Test
    public void testResolvePlaceLocationOnlyGoogle() throws Exception {
        HttpReader httpReader = mock(HttpReader.class);
        when(httpReader.httpGet(anyString())).then(invocation -> {
            String query = (String)invocation.getArguments()[0];
            if (query.contains("google")) {
                return normalGoogleResponce;
            }
            throw new Exception();
        });
        Location resolvedLocation = new PlaceLocationResolver(httpReader)
                .resolvePlaceLocation("Москва");
        assertThat(resolvedLocation.getLatitude(), is(55.755826));
        assertThat(resolvedLocation.getLongitude(), is(37.6173));
        assertThat(resolvedLocation.getName(), is("Москва"));
    }

    @Test(expected = InvalidLocationException.class)
    public void testResolvePlaceLocationNoResult() throws Exception {
        HttpReader httpReader = mock(HttpReader.class);
        when(httpReader.httpGet(anyString())).then(invocation -> {
            String query = (String) invocation.getArguments()[0];
            if (query.contains("google")) {
                return noResultsGoogleResponce;
            }
            throw new Exception();
        });
        new PlaceLocationResolver(httpReader)
                .resolvePlaceLocation("Москва");
    }

    @Test
    public void testResolvePlaceLocationCacheTest() throws Exception {
        HttpReader httpReader = mock(HttpReader.class);
        int attemps = 0;
        when(httpReader.httpGet(anyString())).then(new Answer<Object>() {
            final int[] count = new int[1];

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String query = (String) invocation.getArguments()[0];
                if (query.contains("google")) {
                    if (count[0] == 0) {
                        ++count[0];
                        return normalGoogleResponce;
                    } else {
                        throw new Exception();
                    }
                }
                throw new Exception();
            }
        });
        PlaceLocationResolver geoResolver = new PlaceLocationResolver(httpReader);
        Location resolvedLocation = geoResolver.resolvePlaceLocation("Москва");
        assertThat(resolvedLocation.getLatitude(), is(55.755826));
        assertThat(resolvedLocation.getLongitude(), is(37.6173));
        assertThat(resolvedLocation.getName(), is("Москва"));

        resolvedLocation = geoResolver.resolvePlaceLocation("Москва");
        assertThat(resolvedLocation.getLatitude(), is(55.755826));
        assertThat(resolvedLocation.getLongitude(), is(37.6173));
        assertThat(resolvedLocation.getName(), is("Москва"));
    }

}