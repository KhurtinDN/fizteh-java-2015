package ru.mipt.diht.students.glutolik.TwitterStreamTests;

import org.junit.Test;
import ru.mipt.diht.students.glutolik.TwitterStream.TwitterStreamUtils;
import twitter4j.Status;
import twitter4j.User;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by glutolik on 15.12.15.
 */
public class TwitterStreamUtilsTests {
    @Test
    public void testPaintName() {
        assertEquals("\033[34m@User-Name123\033[0m", TwitterStreamUtils.paintName("User-Name123", TwitterStreamUtils.Colors.BLUE));
    }

    @Test
    public void testPaint() {
        assertEquals("\033[34mBlueText\033[0m",
                TwitterStreamUtils.paint("BlueText", TwitterStreamUtils.Colors.BLUE));
        assertEquals("\033[0mNo Color\033[0m",
                TwitterStreamUtils.paint("No Color", TwitterStreamUtils.Colors.CLEAR));
        assertEquals("\033[37m\033[0m",
                TwitterStreamUtils.paint("", TwitterStreamUtils.Colors.WHITE));
    }

    @Test
    public void testSpellTime() {
        Calendar thisTime = new GregorianCalendar(2015, 0, 1, 0, 2);
        Calendar fiveMins = new GregorianCalendar(2014, 11, 31, 23, 57);
        Calendar thirtyDays = new GregorianCalendar(2014, 11, 1, 0, 10);
        Calendar yesterday = new GregorianCalendar(2014, 11, 31, 0, 0);
        assertEquals("5 минут назад",
                TwitterStreamUtils.spellTime(
                        fiveMins.getTime(), thisTime.getTime()));
        assertEquals("30 дней назад", TwitterStreamUtils.spellTime(thirtyDays.getTime(), thisTime.getTime()));
        assertEquals("Вчера", TwitterStreamUtils.spellTime(yesterday.getTime(), thisTime.getTime()));
    }

    @Test
    public void testFormate() {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);
        when(mockStatus.getText()).thenReturn("The dream will finally come true!");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockUser.getScreenName()).thenReturn("Glutolik");
        String formatted = TwitterStreamUtils.formate(mockStatus, false);
        assertEquals("\033[34m@Glutolik\033[0m: The dream will finally come true!", formatted);
    }
}

