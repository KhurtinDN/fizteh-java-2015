package ru.mipt.diht.students.ale3otik.moduletests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.TwitterClientArguments;
import ru.mipt.diht.students.ale3otik.twitter.GeoLocationResolver;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.LocationException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;

/**
 * Created by alex on 08.11.15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeoLocationResolver.class})
public class ArgumentsProcessTest extends TestCase {
    private TwitterClientArguments arguments;
    private JCommander jcm;
    private String[] args;

    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;
    private static GeoLocationInfo moscowInfo;
    private static GeoLocationInfo londonInfo;

    @Before
    public void setUp() throws Exception {
        args = new String[9];
        args[0] = "-q";
        args[1] = "body";
        args[2] = "-l";
        args[3] = "1000";
        args[4] = "-p";
        args[5] = "London";
        args[6] = "--hideRetweets";
        args[7] = "-s";
        args[8] = "-h";

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
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse(args);

        assertEquals(arguments.getLimit(), 1000);
        assertEquals(arguments.getQuery(), "body");
        assertEquals(arguments.getLocation(), "London");
        assertEquals(arguments.isHideRetweets(), true);
        assertEquals(arguments.isStream(), true);
        assertEquals(arguments.isHelp(), true);
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
        assertEquals(arguments.getCurLocationName(), "Moscow");
        assertEquals(arguments.getGeoLocationInfo(), moscowInfo);
        assertEquals(arguments.getDetectionLocationMessage(), "");
    }

    @Test
    public void testArgumentsNormalGeoLocationValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s", "-p", "London");
        arguments.validate();
        assertEquals(arguments.getCurLocationName(), "London");
        assertEquals(arguments.getGeoLocationInfo(), londonInfo);
        assertEquals(arguments.getDetectionLocationMessage(), "");
    }

    @Test
    public void testArgumentsFailedGeoLocationValidation() {
        arguments = new TwitterClientArguments();
        jcm = new JCommander(arguments);
        jcm.parse("-s", "-p", "InvalidLocation");
        arguments.validate();
        assertEquals(arguments.getCurLocationName(), "World");
        assertEquals(arguments.getGeoLocationInfo(), null);
        assertEquals(arguments.getDetectionLocationMessage(),
                "Невозможно определить запрашиваемое местоположение\n");
    }
}
