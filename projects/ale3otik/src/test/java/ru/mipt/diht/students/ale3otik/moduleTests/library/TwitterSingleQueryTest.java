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
import ru.mipt.diht.students.ale3otik.twitter.TwitterClient;
import ru.mipt.diht.students.ale3otik.twitter.TwitterClientArguments;
import ru.mipt.diht.students.ale3otik.twitter.TwitterSingleQuery;
import ru.mipt.diht.students.ale3otik.twitter.TwitterUtils;
import ru.mipt.diht.students.ale3otik.twitter.exceptions.ConnectionFailedException;
import ru.mipt.diht.students.ale3otik.twitter.structs.GeoLocationInfo;
import twitter4j.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

/**
 * Created by alex on 16.11.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TwitterUtils.class, TwitterClientArguments.class})
public class TwitterSingleQueryTest extends TestCase {
    private TwitterClientArguments arguments;
    private JCommander jcm;
    private static final double LondonLatitude = 51.5073509;
    private static final double LondonLongitude = -0.1277583;
    private static final double LondonRadius = 23.539304731202712;

    private static GeoLocationInfo londonGeoLocationInfo;
    private static java.util.List<Status> statuses;
    private static final String NON_RETWEET = "!@#Catched non-retweet code#@!";
    private static final String RETWEET = "#$%$@Catched retweet code@#$!";

    private static Status retweetStatus;
    private static Status notRetweetStatus;
    private static StringBuilder infoMessage;

    private TwitterSingleQuery twitterSingleQuery;
    @Mock
    Twitter mockedTwitter;
    @Mock
    QueryResult mockedResult;

    @Mock
    TwitterException mockedTwitterNetworkCausedException;

    @Mock
    TwitterException mockedTwitterException;
    private Query myQuery;
    private Query myLocationQuery;

    @Before
    public void setUp() throws Exception {
        infoMessage = new StringBuilder();

        londonGeoLocationInfo = new GeoLocationInfo(new GeoLocation(LondonLatitude, LondonLongitude), LondonRadius);

        myQuery = new Query("some query");

        myLocationQuery = new Query("location test");
        myLocationQuery.geoCode(londonGeoLocationInfo.getLocation(),
                londonGeoLocationInfo.getRadius(), "km");

        statuses = Twitter4jTestUtils.tweetsFromJson("/MIPT.json");

//        PowerMockito.mockStatic(Query.class);
//        PowerMockito.whenNew(Query.class).withArguments("some query").thenReturn(myQuery);

        PowerMockito.mockStatic(TwitterUtils.class);
        Mockito.when(mockedResult.getTweets()).thenReturn(statuses);
        Mockito.when(mockedResult.nextQuery()).thenReturn(null);
        Mockito.when(mockedTwitter.search(any(Query.class))).thenReturn(mockedResult);

        PowerMockito.when(TwitterUtils
                .getFormattedTweetToPrint(any(Status.class), any(TwitterClientArguments.class)))
                .thenReturn("------\nSome tweet");

        for (Status s : statuses) {
            if (s.isRetweet()) {
                retweetStatus = s;
                break;
            }
        }

        for (Status s : statuses) {
            if (!s.isRetweet()) {
                notRetweetStatus = s;
                break;
            }
        }

        Mockito.when(mockedTwitterNetworkCausedException.isCausedByNetworkIssue()).thenReturn(true);
        Mockito.when(mockedTwitterException.isCausedByNetworkIssue()).thenReturn(false);
        twitterSingleQuery = new TwitterSingleQuery(mockedTwitter);
    }

    private void setMockitoTwitterUtils() {
        PowerMockito.when(TwitterUtils
                .getFormattedTweetToPrint(retweetStatus, arguments))
                .thenReturn(RETWEET);

        PowerMockito.when(TwitterUtils
                .getFormattedTweetToPrint(notRetweetStatus, arguments))
                .thenReturn(NON_RETWEET);

    }

    private void createLauncherWithArguments(boolean isGeolocationNeeded, String... args) {
        arguments = PowerMockito.mock(TwitterClientArguments.class);
        TwitterClientArguments myArgs = new TwitterClientArguments();
        jcm = new JCommander(myArgs);
        jcm.parse(args);

        if (isGeolocationNeeded) {
            Mockito.when(arguments.getGeoLocationInfo()).thenReturn(londonGeoLocationInfo);
        } else {
            Mockito.when(arguments.getGeoLocationInfo()).thenReturn(null);
        }
        Mockito.when(arguments.isStream()).thenReturn(myArgs.isStream());
        Mockito.when(arguments.isHideRetweets()).thenReturn(myArgs.isHideRetweets());
        Mockito.when(arguments.getQuery()).thenReturn(myArgs.getQuery());
        Mockito.when(arguments.isHelp()).thenReturn(myArgs.isHelp());
        Mockito.when(arguments.getLimit()).thenReturn(myArgs.getLimit());
    }

    @Test
    public void testQueryHideRetweets() throws Exception {
        createLauncherWithArguments(false, "--hideRetweets", "-q", "some query");
        setMockitoTwitterUtils();
        String result = twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);

        assert (!result.contains(RETWEET));
        assert (result.contains(NON_RETWEET));
    }

    @Test
    public void testQueryNotHideRetweets() throws Exception {
        createLauncherWithArguments(false, "-q", "some query");
        setMockitoTwitterUtils();
        String result = twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);

        assert (result.contains(RETWEET));
        assert (result.contains(NON_RETWEET));
    }

    @Test
    public void testQueryCountRetweets() throws Exception {
        createLauncherWithArguments(false, "-q", "some query", "-l", "1000");
        setMockitoTwitterUtils();
        String result = twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);

        verify(mockedTwitter).search(myQuery);
        verify(mockedResult).nextQuery();
        assert (result.contains(RETWEET));
        assert (result.contains(NON_RETWEET));
    }

    @Test
    public void testQueryGeoLocation() throws Exception {
        createLauncherWithArguments(true, "-q", "location test", "-l", "1000");
        setMockitoTwitterUtils();
        String result = twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);

        verify(mockedTwitter).search(myLocationQuery);
        assert (result.contains(RETWEET));
        assert (result.contains(NON_RETWEET));
    }

    @Test
    public void testEmptyStatuses() throws Exception {
        createLauncherWithArguments(true, "-q", "some query");
        setMockitoTwitterUtils();
        statuses.clear();
        String result = twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);

        assert (result.contains("Ничего не найдено :("));
    }

    @Test(expected = ConnectionFailedException.class)
    public void testNetworkIssueException() throws Exception {
        twitterSingleQuery = new TwitterSingleQuery(mockedTwitter, (x) -> {
        });
        createLauncherWithArguments(true, "-q", "some query");
        setMockitoTwitterUtils();
        Mockito.when(mockedTwitter.search(any(Query.class))).thenThrow(mockedTwitterNetworkCausedException);

        twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);
    }

    @Test(expected = TwitterException.class)
    public void testUnknownException() throws Exception {
        twitterSingleQuery = new TwitterSingleQuery(mockedTwitter, (x) -> {
        });
        createLauncherWithArguments(true, "-q", "some query");
        setMockitoTwitterUtils();
        Mockito.when(mockedTwitter.search(any(Query.class))).thenThrow(mockedTwitterException);

        twitterSingleQuery.getSingleQueryResult(arguments, infoMessage);
    }

}
