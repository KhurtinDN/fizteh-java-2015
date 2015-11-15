package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.Arguments;
import ru.mipt.diht.students.ale3otik.twitter.TwitterStreamLauncher;
import twitter4j.*;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Created by alex on 15.11.15.
 */
public class TwitterStreamLauncherTest extends TestCase {
    private StatusAdapter statusAdapter;
    private Arguments arguments;
    private JCommander jcm;
    private Consumer<String> mockedConsumer;
    private TwitterStreamLauncher twitterStreamLauncher;
    private Status mockedStatus;
    private TwitterStream twitterStreamClient;
    private User mockedUser;

    @BeforeClass
    public void setUp() {
        arguments = new Arguments();
        jcm = new JCommander(arguments);
        jcm.parse("-q","some query","-s", "--hideRetweets");

        mockedUser = mock(User.class);
        when(mockedUser.getScreenName()).thenReturn("alex");
        mockedStatus = mock(Status.class);
        when(mockedStatus.isRetweet()).thenReturn(false);
        when(mockedStatus.getGeoLocation()).thenReturn(null);
        when(mockedStatus.getText()).thenReturn("alex: aadsadad");
        when(mockedStatus.getUser()).thenReturn(mockedUser);
        twitterStreamClient = mock(TwitterStream.class);

        mockedConsumer = mock(Consumer.class);

        twitterStreamLauncher = new TwitterStreamLauncher(twitterStreamClient, mockedConsumer);
        statusAdapter = twitterStreamLauncher.createStatusAdapter(arguments);
    }

    @Test
    public void testStatusListener() {

        statusAdapter.onStatus(mockedStatus);
        verify(mockedStatus, atLeastOnce()).isRetweet();
        verify(mockedConsumer).accept(anyString());
    }

    @Test public void testStreamStart() throws Exception{
        twitterStreamLauncher.streamStart(arguments, "");
//        verify(twitterStreamClient).addListener(any());
        verify(twitterStreamClient).filter(any(FilterQuery.class));
    }
}