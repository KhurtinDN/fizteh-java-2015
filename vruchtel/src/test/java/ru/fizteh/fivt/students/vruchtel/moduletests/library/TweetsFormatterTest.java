package ru.fizteh.fivt.students.vruchtel.moduletests.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.User;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Created by Серафима on 30.11.2015.
 */
public class TweetsFormatterTest {

    @Before
    public void SetUpStatuses() {
        status1 = mock(Status.class);
        user1 = mock(User.class);
        when(user1.getScreenName()).thenReturn("John");
        when(status1.getUser()).thenReturn(user1);
        when(status1.getCreatedAt()).thenReturn(new Date());//Только что
        when(status1.isRetweet()).thenReturn(true);//ретвит
        when(status1.getText()).thenReturn("Hello!");


        status2 = mock(Status.class);
        user2 = mock(User.class);
        when(user2.getScreenName()).thenReturn("Mary");
        when(status2.getUser()).thenReturn(user2);
        when(status2.getCreatedAt()).thenReturn(new Date());//Только что
        when(status2.isRetweet()).thenReturn(false);//не ретвит
        when(status2.getText()).thenReturn("Hello!");
        when(status2.isRetweeted()).thenReturn(true);
        when(status2.getRetweetCount()).thenReturn(1);

        status3 = mock(Status.class);
        user3 = mock(User.class);
        when(user3.getScreenName()).thenReturn("Alex");
        when(status3.getUser()).thenReturn(user3);
        when(status3.getCreatedAt()).thenReturn(new Date());//Только что
        when(status3.isRetweet()).thenReturn(false);//не ретвит
        when(status3.getText()).thenReturn("Hello!");
        when(status3.isRetweeted()).thenReturn(false);

        when(status1.getRetweetedStatus()).thenReturn(status3);//ретвитнул @Alex
    }

    @Test
    public void GetTextToPrintTest() {
        Assert.assertThat("[Только что] \u001B[34m@John: \u001B[0mретвитнул \u001B[34m@Alex:" +
                " \u001B[0mHello!", equalTo(tweetsFormatter.getTextToPrint(status1, true)));
        Assert.assertThat("[Только что] \u001B[34m@Mary: \u001B[0mHello! (1 ретвитов)",
                equalTo(tweetsFormatter.getTextToPrint(status2, true)));
        Assert.assertThat("[Только что] \u001B[34m@Alex: \u001B[0mHello!",
                equalTo(tweetsFormatter.getTextToPrint(status3, true)));

        Assert.assertThat("\u001B[34m@John: \u001B[0mретвитнул \u001B[34m@Alex: \u001B[0mHello!",
                equalTo(tweetsFormatter.getTextToPrint(status1, false)));
        Assert.assertThat("\u001B[34m@Mary: \u001B[0mHello! (1 ретвитов)",
                equalTo(tweetsFormatter.getTextToPrint(status2, false)));
        Assert.assertThat("\u001B[34m@Alex: \u001B[0mHello!",
                equalTo(tweetsFormatter.getTextToPrint(status3, false)));
    }

    private static Status status1;
    private static Status status2;
    private static Status status3;

    private static User user1;
    private static User user2;
    private static User user3;

    private static TweetsFormatter tweetsFormatter;
}
