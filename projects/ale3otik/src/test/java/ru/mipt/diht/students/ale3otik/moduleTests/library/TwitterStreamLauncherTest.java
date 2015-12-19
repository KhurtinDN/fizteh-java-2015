package ru.mipt.diht.students.ale3otik.moduletests.library;

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
import ru.mipt.diht.students.ale3otik.twitter.TwitterClientArguments;
import ru.mipt.diht.students.ale3otik.twitter.TwitterStreamLauncher;
import ru.mipt.diht.students.ale3otik.twitter.TwitterUtils;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Created by alex on 15.11.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Thread.class, TwitterUtils.class,TwitterClientArguments.class})
public class TwitterStreamLauncherTest {
    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;
    private static GeoLocationInfo londonGeoLocationInfo;
    private static GeoLocationInfo moscowGeoLocationInfo;
    private StatusAdapter statusAdapter;
    private TwitterClientArguments arguments;
    private JCommander jcm;
    private TwitterStreamLauncher twitterStreamLauncher;

    @Mock
    private Consumer<String> mockedConsumer;

    @Mock
    private Status mockedStatus;

    @Mock
    private TwitterStream mockedTwitterStreamClient;

    @Before
    public void setUp() {
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(null);
        Mockito.when(mockedStatus.getText()).thenReturn("alex: some message");

        PowerMockito.mockStatic(TwitterUtils.class);

        londonGeoLocationInfo = new GeoLocationInfo(new GeoLocation(LondonLatitude,LondonLongitude),LondonRadius);
        moscowGeoLocationInfo = new GeoLocationInfo(new GeoLocation(MoscowLatitude,MoscowLongitude),MoscowRadius);

        arguments = PowerMockito.mock(TwitterClientArguments.class);
        TwitterClientArguments myArgs = new TwitterClientArguments();
        jcm = new JCommander(myArgs);
        jcm.parse(new String[]{"-q", "some query", "-s"});

        Mockito.when(arguments.getGeoLocationInfo()).thenReturn(null);

        Mockito.when(arguments.isStream()).thenReturn(myArgs.isStream());
        Mockito.when(arguments.isHideRetweets()).thenReturn(myArgs.isHideRetweets());
        Mockito.when(arguments.getQuery()).thenReturn(myArgs.getQuery());
        Mockito.when(arguments.isHelp()).thenReturn(myArgs.isHelp());
        Mockito.when(arguments.getLimit()).thenReturn(myArgs.getLimit());

        String text = mockedStatus.getText();
        PowerMockito.when(TwitterUtils
                    .getFormattedTweetToPrint(mockedStatus,arguments))
                    .thenReturn(text);

        twitterStreamLauncher =
                new TwitterStreamLauncher(mockedTwitterStreamClient, mockedConsumer, arguments,1);
    }


    @Test
    public void testStatusListenerNotRetweetNullLocationHideRetweets() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(arguments.isHideRetweets()).thenReturn(true);

        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer).accept(mockedStatus.getText());

    }
    @Test
    public void testStatusListenerNotRetweetNullLocation() throws Exception {
        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer, times(1)).accept(mockedStatus.getText());
    }

    @Test
    public void testStatusListenerRetweetNullLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(true);

        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer, times(1)).accept(mockedStatus.getText());
    }
    @Test
    public void testStatusListenerRetweetNullLocationHideRetweets() throws Exception {
        Mockito.when(arguments.isHideRetweets()).thenReturn(true);
        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer,times(1)).accept(anyString());
    }

    @Test
    public void testStatusListenerNullFailedLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(null);
        Mockito.when(arguments.getGeoLocationInfo()).thenReturn(londonGeoLocationInfo);

        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verifyZeroInteractions(mockedConsumer);
    }

    @Test
    public void testStatusListenerFailedLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(moscowGeoLocationInfo.getLocation());

        Mockito.when(arguments.getGeoLocationInfo()).thenReturn(londonGeoLocationInfo);
        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verifyZeroInteractions(mockedConsumer);
    }

    @Test
    public void testStatusListenerSuccesLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(londonGeoLocationInfo.getLocation());

        Mockito.when(arguments.getGeoLocationInfo()).thenReturn(londonGeoLocationInfo);
        statusAdapter = twitterStreamLauncher.createStatusAdapter();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer,times(1)).accept(anyString());
    }

    @Test
    public void testStreamStartFilter() throws Exception {
        twitterStreamLauncher =
                new TwitterStreamLauncher(mockedTwitterStreamClient, mockedConsumer, arguments, 0);
        twitterStreamLauncher.streamStart(new StringBuilder());
        Mockito.verify(mockedTwitterStreamClient).filter(any(FilterQuery.class));
    }

    @Test
    public void testStreamStartSample() throws Exception {
        Mockito.when(arguments.getQuery()).thenReturn("");
        twitterStreamLauncher =
                new TwitterStreamLauncher(mockedTwitterStreamClient, mockedConsumer, arguments, 0);


        twitterStreamLauncher.streamStart(new StringBuilder());
        Mockito.verify(mockedTwitterStreamClient).sample();
    }
}