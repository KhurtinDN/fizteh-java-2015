package ru.mipt.diht.students.ale3otik.moduleTests.library;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.mipt.diht.students.ale3otik.twitter.*;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterException;

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
        TwitterClient.class,
        Arguments.class
})
public class TwitterClientTest extends TestCase {

    private static String helloString = "[1m[35m\nTwitter 0.1 ::: welcome \n\n[0m[0m";
    Arguments arguments;

    TwitterSingleQuery mockedTwitterSingle;
    TwitterStreamLauncher mockedStreamLauncher;

    StringBuilder helpExpected;

    @Before
    public void setUp() throws Exception {
        JCommander jcm = new JCommander(new Arguments());
        jcm.setProgramName("TwitterQueryClient");
        helpExpected = new StringBuilder();
        jcm.usage(helpExpected);

        mockedStreamLauncher = PowerMockito.mock(TwitterStreamLauncher.class);
        mockedTwitterSingle = PowerMockito.mock(TwitterSingleQuery.class);

        PowerMockito.mockStatic(ConsoleUtil.class);
        PowerMockito.mockStatic(TwitterArgumentsValidator.class);
        PowerMockito.whenNew(TwitterStreamLauncher.class).withAnyArguments().thenReturn(mockedStreamLauncher);
    }

    private void createArguments(String... args) {
        arguments = new Arguments();
        JCommander jcm = new JCommander(arguments);
        jcm.parse(args);
        arguments.setCurLocationName("London");
    }

    @Test
    public void testSingleQueryRun() throws Exception {
        createArguments("-q", "test");
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.whenNew(Arguments.class).withAnyArguments().thenReturn(arguments);
        PowerMockito.when(mockedTwitterSingle.getSingleQueryResult(eq(arguments), anyString()))
                .thenReturn("single query result");

        PowerMockito.whenNew(TwitterSingleQuery.class).withArguments(any(Twitter.class)).thenReturn(mockedTwitterSingle);

        TwitterClient.run("-q", "test");
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helloString);
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout("single query result");
    }

    @Test
    public void testHelp() throws Exception {

        TwitterClient.run("-h");
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helloString);
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helpExpected.toString());
        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("Normal exit");
    }

    @Test
    public void testIllegalArguments() throws Exception {

        TwitterClient.run("-q");
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helloString);
        PowerMockito.verifyStatic();
        ConsoleUtil.printIntoStdout(helpExpected.toString());
        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("Invalid arguments presentation exit");
    }

    @Test
    public void testStreamLaunch() throws Exception {
        createArguments("-s");
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.whenNew(Arguments.class).withAnyArguments().thenReturn(arguments);
        TwitterClient.run("-s");
        verify(mockedStreamLauncher).streamStart("Твиты по пустому запросу для \"London\"");
    }

    @Test
    public void testTwitterExceptionHandler() throws Exception {

        PowerMockito.whenNew(TwitterSingleQuery.class)
                .withAnyArguments().
                thenThrow(new TwitterException("test"));

        TwitterClient.run("-q", "test");

        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("Unhandled TwitterException");
        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("test");
    }

    @Test
    public void testConnectionCausedExceptionHandler() throws Exception {
        PowerMockito.whenNew(TwitterSingleQuery.class)
                .withAnyArguments().
                thenThrow(new ConnectionFailedException("test"));

        TwitterClient.run("-q", "test");

        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("ConnectionFailedException");
        PowerMockito.verifyStatic();
        ConsoleUtil.printErrorMessage("test");
    }

}
