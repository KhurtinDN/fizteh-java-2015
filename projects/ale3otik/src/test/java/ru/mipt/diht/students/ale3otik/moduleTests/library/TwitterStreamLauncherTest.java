package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.Arguments;
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
@PrepareForTest({Thread.class, TwitterUtils.class})
public class TwitterStreamLauncherTest extends TestCase {
    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;
    private static final double MoscowLatitude = 55.755826;
    private static final double MoscowLongitude = 37.6173;
    private static final double MoscowRadius = 34.914661819343884;
    private static GeoLocationInfo londonGeoLocationInfo;
    private static GeoLocationInfo moscowGeoLocationInfo;
    private StatusAdapter statusAdapter;
    private Arguments arguments;
    private JCommander jcm;
    private TwitterStreamLauncher twitterStreamLauncher;

    @Mock
    private Consumer<String> mockedConsumer;

    @Mock
    private Status mockedStatus;

    @Mock
    private TwitterStream mockedTwitterStreamClient;

    public void setUp() {
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(null);
        Mockito.when(mockedStatus.getText()).thenReturn("alex: some message");

        PowerMockito.mockStatic(TwitterUtils.class);

        londonGeoLocationInfo = new GeoLocationInfo(new GeoLocation(LondonLatitude,LondonLongitude),LondonRadius);
        moscowGeoLocationInfo = new GeoLocationInfo(new GeoLocation(MoscowLatitude,MoscowLongitude),MoscowRadius);
    }

    private void createLauncherWithArguments(boolean isGeolocationNeeded,long sleepTime,String... args) {
        arguments = new Arguments();
        jcm = new JCommander(arguments);
        jcm.parse(args);
        if(isGeolocationNeeded){
            arguments.setGeoLocationInfo(londonGeoLocationInfo);
        }
        twitterStreamLauncher = new TwitterStreamLauncher(mockedTwitterStreamClient, mockedConsumer, arguments,sleepTime);
    }

    private void mockTwitterUtils(){
        String text = mockedStatus.getText();
        PowerMockito.when(TwitterUtils
                .getFormattedTweetToPrint(mockedStatus,arguments))
                .thenReturn(text);
    }

    @Test
    public void testStatusListenerNotRetweetNullLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);

        createLauncherWithArguments(false,1,"-q", "some query", "-s", "--hideRetweets");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer).accept(mockedStatus.getText());

        createLauncherWithArguments(false,1,"-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer, times(2)).accept(mockedStatus.getText());
    }

    @Test
    public void testStatusListenerRetweetNullLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(true);

        createLauncherWithArguments(false,1,"-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer,times(1)).accept(mockedStatus.getText());

        createLauncherWithArguments(false,1,"-q", "some query", "-s", "--hideRetweets");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer,times(1)).accept(anyString());
    }

    @Test
    public void testStatusListenerNullFailedLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(null);

        createLauncherWithArguments(true,1, "-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verifyZeroInteractions(mockedConsumer);
    }

    @Test
    public void testStatusListenerFailedLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(moscowGeoLocationInfo.getLocation());

        createLauncherWithArguments(true,1, "-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verifyZeroInteractions(mockedConsumer);
    }

    @Test
    public void testStatusListenerSuccesLocation() throws Exception {
        Mockito.when(mockedStatus.isRetweet()).thenReturn(false);
        Mockito.when(mockedStatus.getGeoLocation()).thenReturn(londonGeoLocationInfo.getLocation());

        createLauncherWithArguments(true,1, "-q", "some query", "-s");
        statusAdapter = twitterStreamLauncher.createStatusAdapter();
        mockTwitterUtils();

        statusAdapter.onStatus(mockedStatus);
        Mockito.verify(mockedConsumer,times(1)).accept(anyString());
    }


    @Test
    public void testStreamStart() throws Exception {
        createLauncherWithArguments(false,0,"-q", "some query", "-s");
        twitterStreamLauncher.streamStart(new StringBuilder());
        Mockito.verify(mockedTwitterStreamClient).filter(any(FilterQuery.class));

        createLauncherWithArguments(false,0,"-s");

        twitterStreamLauncher.streamStart(new StringBuilder());
        Mockito.verify(mockedTwitterStreamClient).sample();
    }
}