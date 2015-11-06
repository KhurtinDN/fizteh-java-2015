package ru.mipt.diht.students.maxDankow.TwitterStream;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import ru.mipt.diht.students.maxDankow.TwitterStream.utils.TwitterStreamUtils;
import twitter4j.Status;
import twitter4j.User;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterStreamTextUtilsTests {
    @Test
    public void testBuildUserName() {
        assertEquals("@User-Name123", TwitterStreamUtils.buildUserName("User-Name123"));
    }
    @Test
    public void testColorization() {
        assertEquals("\033[34mBlueText\033[0m",
                TwitterStreamUtils.colorizeText("BlueText", TwitterStreamUtils.TextColor.BLUE));
        assertEquals("\033[0mNo Color\033[0m",
                TwitterStreamUtils.colorizeText("No Color", TwitterStreamUtils.TextColor.CLEAR));
        assertEquals("\033[37m\033[0m",
                TwitterStreamUtils.colorizeText("", TwitterStreamUtils.TextColor.WHITE));
    }
    @Ignore
    @Test
    public void testDateToRussianWords() {
//        TwitterStreamUtils.convertTimeToRussianWords(new Date())
    }
    @Test
    public void testBuildFormattedTweet() {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, false);
        assertEquals("\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
    }
    @Test
    public void testBuildFormattedTweetWithDate() {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);
        when(mockStatus.getText()).thenReturn("This is a test tweet. #test01 #Java");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Real-UserNAME");
        String result = TwitterStreamUtils.buildFormattedTweet(mockStatus, true);
        assertEquals("[Только что]\033[34m@Real-UserNAME\033[0m: This is a test tweet. #test01 #Java", result);
    }
}
