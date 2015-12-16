package util;

import org.junit.Test;
import twitter4j.Status;
import twitter4j.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Created by lokotochek on 13.12.15.
 */
public class StringEditorTest {

    @Test
    public void testColourName() throws Exception {
        String colouredName = StringEditor.colourName("Lena");
        String correctName = (char)27 + "[34m" + "@" + "Lena" + (char) 27 + "[0m" + ": ";
        assertEquals(correctName, colouredName);
    }

    @Test
    public void testTweetStringToPrint() throws Exception {
        Status mockedStatus = mock(Status.class);
        LocalDateTime createdAtLDT = LocalDateTime.now();
        createdAtLDT = createdAtLDT.minusDays(3);
        Instant instant = createdAtLDT.atZone(ZoneId.systemDefault()).toInstant();
        Date createdAt = Date.from(instant);

        when(mockedStatus.getCreatedAt()).thenReturn(createdAt);

        User mockedUser = mock(User.class);
        when(mockedUser.getScreenName()).thenReturn("Lena");

        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedStatus.getText()).thenReturn("Test tweet");

        String tweetStringResult = StringEditor.tweetStringToPrint(mockedStatus);
        String correctResult = "[3 дня назад] " + StringEditor.colourName("Lena") + "Test tweet";
        assertEquals(correctResult, tweetStringResult);
    }
}