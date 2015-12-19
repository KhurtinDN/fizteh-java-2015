package ru.mipt.diht.students.ale3otik.moduletests.library;

import com.beust.jcommander.JCommander;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.GeoLocationResolver;
import ru.mipt.diht.students.ale3otik.twitter.TwitterClientArguments;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alex on 08.11.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeoLocationResolver.class})
public class ArgumentsProcessTest {
    private TwitterClientArguments arguments;
    private JCommander jcm;
    private String[] args;

    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;
    private GeoLocationInfo moscowInfo;
    private GeoLocationInfo londonInfo;

    @Before
    public void setUp() throws Exception {
        moscowInfo = new GeoLocationInfo(
                new GeoLocation(MoscowLatitude, MoscowLongitude), MoscowRadius);
        londonInfo = new GeoLocationInfo(
                new GeoLocation(LondonLatitude, LondonLongitude), LondonRadius);

        PowerMockito.mockStatic(GeoLocationResolver.class);
        PowerMockito.when(GeoLocationResolver
                .getNameOfCurrentLocation()).thenReturn("Moscow");

        PowerMockito.when(GeoLocationResolver.getGeoLocation("London"))
                .thenReturn(londonInfo);
        PowerMockito.when(GeoLocationResolver.getGeoLocation("Moscow"))
                .thenReturn(moscowInfo);

        PowerMockito.when(GeoLocationResolver.getGeoLocation("InvalidLocation"))
                .thenThrow(new LocationException("Can't find information"));
    }

    @Test
    public void testParser() throws Exception {
        args = new String[]{"-q", "body", "-l", "1000", "-p", "London", "--hideRetweets", "-s", "-h"};
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse(args);

        assertThat(arguments.getLimit(), equalTo(1000));
        assertThat(arguments.getQuery(), equalTo("body"));
        assertThat(arguments.getLocation(), equalTo("London"));
        assertThat(arguments.isHideRetweets(), equalTo(true));
        assertThat(arguments.isStream(), equalTo(true));
        assertThat(arguments.isHelp(), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalEmptyQueryValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse();
        arguments.validate();
    }

    @Test
    public void testLegalEmptyQueryValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s");
        arguments.validate();
    }

    @Test
    public void testArgumentsNormalNearbyValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s", "-p", "nearby");
        arguments.validate();
        assertThat(arguments.getCurLocationName(), equalTo("Moscow"));
        assertThat(arguments.getGeoLocationInfo(), equalTo(moscowInfo));
        assertThat(arguments.getDetectionLocationMessage(), equalTo(""));
    }

    @Test
    public void testArgumentsNormalGeoLocationValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s", "-p", "London");
        arguments.validate();
        assertThat(arguments.getCurLocationName(), equalTo("London"));
        assertThat(arguments.getGeoLocationInfo(), equalTo(londonInfo));
        assertThat(arguments.getDetectionLocationMessage(), equalTo(""));
    }

    @Test
    public void testArgumentsFailedGeoLocationValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s", "-p", "InvalidLocation");
        arguments.validate();
        assertThat(arguments.getCurLocationName(), equalTo("World"));
        assertThat(arguments.getGeoLocationInfo(), equalTo(null));
        assertThat(arguments.getDetectionLocationMessage(),
                equalTo("Невозможно определить запрашиваемое местоположение\n"));
    }
}
