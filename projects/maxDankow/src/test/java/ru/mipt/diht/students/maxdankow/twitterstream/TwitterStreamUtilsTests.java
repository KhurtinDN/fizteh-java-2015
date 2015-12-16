package ru.mipt.diht.students.maxdankow.twitterstream;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.mipt.diht.students.maxdankow.twitterstream.utils.TwitterStreamUtils;
import twitter4j.Status;
import twitter4j.User;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TwitterStreamUtilsTests {

    @DataProvider
    public static Object[][] timesProvider() {
        return new Object[][]{
                {
                        new GregorianCalendar(2015, 0, 1, 0, 2),
                        new GregorianCalendar(2014, 11, 31, 23, 57),
                        "5 минут назад"
                },
                {
                        new GregorianCalendar(2015, 0, 1, 0, 2),
                        new GregorianCalendar(2014, 11, 1, 0, 10),
                        "30 дней назад"
                },
                {
                        new GregorianCalendar(2015, 0, 1, 0, 2),
                        new GregorianCalendar(2014, 11, 30, 0, 0),
                        "2 дней назад"
                },
                {
                        new GregorianCalendar(2015, 0, 1, 0, 2),
                        new GregorianCalendar(2014, 11, 31, 0, 0),
                        "Вчера"
                },
                {
                        new GregorianCalendar(2015, 11, 31, 0, 0, 15),
                        new GregorianCalendar(2015, 11, 31, 0, 0, 20),
                        "Только что"
                }
        };
    }

    @Test
    @UseDataProvider("timesProvider")
    public void dateToRussianWordsTest(GregorianCalendar currentDate,
                                       GregorianCalendar anotherDate,
                                       String expected) {
        String actual = TwitterStreamUtils.convertTimeToRussianWords(anotherDate.getTime(), currentDate.getTime());
        assertThat(actual, equalTo(expected));
    }

    @Test
    public final void testBuildUserName() {
        assertEquals("\033[34m@User-Name123\033[0m", TwitterStreamUtils.buildColorizedUserName("User-Name123", TwitterStreamUtils.TextColor.BLUE));
    }

    @Test
    public final void testColorization() {
        assertEquals("\033[34mBlueText\033[0m",
                TwitterStreamUtils.colorizeText("BlueText", TwitterStreamUtils.TextColor.BLUE));
        assertEquals("\033[0mNo Color\033[0m",
                TwitterStreamUtils.colorizeText("No Color", TwitterStreamUtils.TextColor.CLEAR));
        assertEquals("\033[37m\033[0m",
                TwitterStreamUtils.colorizeText("", TwitterStreamUtils.TextColor.WHITE));
    }

    @Test
    public void testBuildFormattedTweet() {
        Status mockStatus = mock(Status.class);
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        User mockUser = mock(User.class);

        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, false);
        assertEquals("\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
    }

    @Test
    public void testBuildFormattedTweetWithDate() {
        Status mockStatus = mock(Status.class);
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockStatus.getCreatedAt()).thenReturn(new Date());

        User mockUser = mock(User.class);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");

        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, true);
        assertEquals("[Только что]\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
    }

    Status mockStatus = mock(Status.class);
    User mockUser = mock(User.class);

    @Before
    public void before() {
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
    }

    @Test
    public void checkTweetTest() {
        when(mockStatus.isRetweet()).thenReturn(true);
        assertFalse(TwitterStreamUtils.checkTweet(mockStatus, null, true));
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, false));

        when(mockStatus.isRetweet()).thenReturn(false);
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, true));
        assertTrue(TwitterStreamUtils.checkTweet(mockStatus, null, false));
    }

}
