package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import com.google.common.base.Strings;
import com.sun.org.apache.xpath.internal.Arg;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.*;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import static org.mockito.Mockito.*;

/**
 * Created by alex on 16.11.15.
 */
@PowerMockIgnore("javax.crypto.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConsoleUtil.class,
        TwitterArgumentsValidator.class,
        TwitterStreamLauncher.class,
        TwitterSingleQuery.class,
        TwitterClient.class
})
public class TwitterClientTest extends TestCase {

    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static GeoLocationInfo londonGeoLocationInfo;
    private static String helloString = "[1m[35m\nTwitter 0.1 ::: welcome \n\n[0m[0m";
    Arguments arguments;

    TwitterSingleQuery mockedTwitterSingle;
    TwitterStreamLauncher mockedStreamLauncher;


    @Before
    public void setUp() throws Exception {
        mockedStreamLauncher = PowerMockito.mock(TwitterStreamLauncher.class);
        mockedTwitterSingle = PowerMockito.mock(TwitterSingleQuery.class);

        londonGeoLocationInfo = new GeoLocationInfo(new GeoLocation(LondonLatitude,LondonLongitude),LondonRadius);
        PowerMockito.mockStatic(ConsoleUtil.class);
        PowerMockito.mockStatic(TwitterArgumentsValidator.class);

        PowerMockito.whenNew(TwitterStreamLauncher.class).withAnyArguments().thenReturn(mockedStreamLauncher);
    }

    private void createArguments(boolean isGeolocationNeeded,String... args) {
        arguments = new Arguments();
        JCommander jcm = new JCommander(arguments);
        jcm.parse(args);
        if (isGeolocationNeeded) {
            arguments.setGeoLocationInfo(londonGeoLocationInfo);
        }

    }

    @Test
    public void testSingleQueryRun() throws Exception {
        String infoMessage = "Твиты по запросу \"test\":";
        createArguments(false, "-q", "test");

        PowerMockito.when(mockedTwitterSingle.getSingleQueryResult(any(Arguments.class), any(String.class)))
                .thenReturn("single query result");

        PowerMockito.whenNew(TwitterSingleQuery.class).withArguments(any(Twitter.class)).thenReturn(mockedTwitterSingle);

        TwitterClient.run("-q", "test");
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helloString);
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout("single query result");
    }

}
