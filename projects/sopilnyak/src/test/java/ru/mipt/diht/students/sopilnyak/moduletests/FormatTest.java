package ru.mipt.diht.students.sopilnyak.moduletests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import ru.mipt.diht.students.sopilnyak.moduletests.library.Format;

import twitter4j.Status;
import twitter4j.User;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class FormatTest {

    @Mock
    Status status;

    @Mock
    Status status1;

    @Mock
    User user;

    @Mock
    User user1;

    @Test
    public void testTweet() {
        doReturn("Владимир Путин").when(user).getScreenName();
        doReturn(false).when(status).isRetweet();
        doReturn(user).when(status).getUser();
        doReturn("123").when(status).getText();
        doReturn(false).when(status).isRetweeted();
        assertEquals("@\u001B[34mВладимир Путин\u001B[0m: 123", Format.formatTweet(status));
    }

    @Test
    public void testRetweets() {
        doReturn("Владимир Путин").when(user).getScreenName();
        doReturn(false).when(status).isRetweet();
        doReturn(user).when(status).getUser();
        doReturn("123").when(status).getText();
        doReturn(true).when(status).isRetweeted();
        doReturn(6).when(status).getRetweetCount();
        assertEquals("@\u001B[34mВладимир Путин\u001B[0m: 123 (6 ретвитов)", Format.formatTweet(status));

    }

    @Test
    public void testIsRetweet() {
        doReturn("Дмитрий Медведев").when(user).getScreenName();
        doReturn(user).when(status).getUser();
        doReturn(true).when(status).isRetweet();

        doReturn("Владимир Путин").when(user1).getScreenName();
        doReturn(user1).when(status1).getUser();
        doReturn(false).when(status1).isRetweet();

        doReturn("132").when(status).getText();
        doReturn(status1).when(status).getRetweetedStatus();
        assertEquals("@\u001B[34mДмитрий Медведев\u001B[0m ретвитнул @\u001B[34mВладимир Путин\u001B[0m: 132",
                Format.formatTweet(status));

    }

}
