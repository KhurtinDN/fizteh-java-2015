package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.*;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.Twitter4jTestUtils;

import static org.mockito.Mockito.*;
/**
 * Created by alex on 16.11.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TwitterUtils.class})
public class TwitterSingleQueryTest extends TestCase {
    private Arguments arguments;
    private JCommander jcm;
    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;
    private static GeoLocationInfo londonGeoLocationInfo;
    private static GeoLocationInfo moscowGeoLocationInfo;
    private static java.util.List<Status> statuses;

    @Mock
    Twitter mockedTwitter;

    @Before
    public void setUp(){
        statuses = Twitter4jTestUtils.tweetsFromJson("/DoctorWho.json");

        PowerMockito.mockStatic(TwitterUtils.class);
        PowerMockito.when(TwitterUtils.getFormattedTweetToPrint(any(Status.class),any(Arguments.class)));

        londonGeoLocationInfo = new GeoLocationInfo(new GeoLocation(LondonLatitude,LondonLongitude),LondonRadius);
        moscowGeoLocationInfo = new GeoLocationInfo(new GeoLocation(MoscowLatitude,MoscowLongitude),MoscowRadius);

    }
    private void createLauncherWithArguments(boolean isGeolocationNeeded,String... args) {
        arguments = new Arguments();
        jcm = new JCommander(arguments);
        jcm.parse(args);
        if(isGeolocationNeeded){
            arguments.setGeoLocationInfo(londonGeoLocationInfo);
        }
    }

    @Test
    public void testQueryWithGeoLocation(){

    }
}
