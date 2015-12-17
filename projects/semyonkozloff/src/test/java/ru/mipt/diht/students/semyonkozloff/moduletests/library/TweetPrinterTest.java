package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.google.common.base.Strings;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.Status;
import twitter4j.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class TweetPrinterTest extends TestCase {

    private StringWriter stringWriter = new StringWriter();
    private TweetPrinter tweetPrinter = new TweetPrinter(stringWriter);

    @Mock
    private Status mockedStatus = mock(Status.class);
    @Mock
    private Status mockedRetweetedStatus = mock(Status.class);
    @Mock
    private User mockedUser = mock(User.class);
    @Mock
    private User mockedRetweetedUser = mock(User.class);

    @Before
    public void setUp() {
        when(mockedStatus.getRetweetedStatus()).
                thenReturn(mockedRetweetedStatus);
        String tweetText = "Now i'm coding Tests, "
                + "tomorrow i will code MiniORM";

        when(mockedStatus.getText()).thenReturn(tweetText);
        when(mockedRetweetedStatus.getText()).thenReturn(tweetText);

        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedRetweetedStatus.getUser()).thenReturn(mockedRetweetedUser);

        when(mockedUser.getName()).thenReturn("Ivan");
        when(mockedRetweetedUser.getName()).thenReturn("John");

        when(mockedStatus.isRetweet()).thenReturn(true);
        when(mockedRetweetedStatus.isRetweet()).thenReturn(false);

        when(mockedStatus.isRetweeted()).thenReturn(false);
        when(mockedRetweetedStatus.isRetweeted()).thenReturn(true);

        when(mockedRetweetedStatus.getRetweetCount()).thenReturn(123);

    }

    @Test
    public void testPrintTweet() throws IOException {
        final int DELIMITER_LENGTH = 160;
        final String DELIMITER =
                Strings.repeat("-", DELIMITER_LENGTH);

        // Test 1
        String expectedText = "@\u001B[34mIvan\u001B[0m:"
                + " retweeted @\u001B[34mJohn\u001B[0m: "
                + "Now i'm coding Tests, "
                + "tomorrow i will code MiniORM"
                + '\n' + DELIMITER + '\n';

        stringWriter.getBuffer().setLength(0); // clearing the buffer
        tweetPrinter.printTweet(mockedStatus);
        String actualText = stringWriter.getBuffer().toString();

        assertThat(actualText, equalTo(expectedText));

        // Test 2
        expectedText = "@\u001B[34mJohn\u001B[0m: "
                + "Now i'm coding Tests, "
                + "tomorrow i will code MiniORM "
                + "(\u001B[32m123 retweets\u001B[0m)"
                + '\n' + DELIMITER + '\n';

        stringWriter.getBuffer().setLength(0);
        tweetPrinter.printTweet(mockedRetweetedStatus);
        actualText = stringWriter.getBuffer().toString();

        assertThat(actualText, equalTo(expectedText));
    }


    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][] {
                {
                        LocalDateTime.now().minusMinutes(1),
                        "[\u001B[36mjust now\u001B[0m]"
                },
                {
                        LocalDateTime.now().minusMinutes(42),
                        "[\u001B[36m42 minutes ago\u001B[0m]"
                },
                {
                        LocalDateTime.now().minusHours(2),
                        "[\u001B[36m2 hours ago\u001B[0m]"
                },
                {
                        LocalDateTime.now().minusDays(1),
                        "[\u001B[36myesterday\u001B[0m]"

                },
                {
                        LocalDateTime.now().minusDays(5),
                        "[\u001B[36m5 days ago\u001B[0m]"
                }
        };
    }

    @Test
    @UseDataProvider("dataProvider")
    public void testPrintTime(LocalDateTime tweetTime, String expectedTime)
            throws IOException {
        Date tweetDate = Date.from(tweetTime.atZone(ZoneId.systemDefault())
                .toInstant());

        stringWriter.getBuffer().setLength(0);
        tweetPrinter.printTime(tweetDate);
        String actualTime = stringWriter.getBuffer().toString();

        assertThat(actualTime, equalTo(expectedTime));
    }


    @Test
    public void testPrintMessage() throws IOException {
        final int N_TESTS = 10;
        final int N_BITS = 130;
        final int STRING_SIZE = 32;

        for (int i = 0; i < N_TESTS; ++i) {
            String randomString = new BigInteger(N_BITS, new SecureRandom())
                    .toString(STRING_SIZE);
            String expectedString = "\u001B[31m" + randomString
                    + "\u001B[0m" + '\n';

            stringWriter.getBuffer().setLength(0);
            tweetPrinter.printMessage(randomString);
            String actualString = stringWriter.getBuffer().toString();

            assertThat(actualString, equalTo(expectedString));
        }
    }
}

