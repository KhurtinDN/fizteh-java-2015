package ru.mipt.diht.students.andreyzharkov.twitterStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import twitter4j.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Андрей on 13.12.2015.
 */
public class TweetFormatterTest extends TestCase {
/*
    @Test
    public final void testConvertTime() {
        TwitterOutputEditor testedEditor = new TwitterOutputEditor(null, null);
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime testDateTime;

        testDateTime = currentDateTime.minusMinutes(45);
        System.out.println("45 минут назад" + testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        assertEquals("45 минут назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusMinutes(75);
        assertEquals("1 час назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusHours(11);
        assertEquals("11 часов назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusHours(18);
        assertEquals("вчера", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusDays(1);
        assertEquals("вчера", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusDays(1).minusHours(17);
        assertEquals("1 день назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
        testDateTime = currentDateTime.minusDays(44);
        assertEquals("44 дня назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
    }
*/

    @Test
    public final void testTweetOutput() throws TwitterException {
        Status tweet = mock(Status.class);
        User user = mock(User.class);
        String userName = "Yura";
        String text = "some tweet text";
        int retweetsCount = 0;
        Date date = new Date(0);
        boolean isRetweet = false;
        when(tweet.getCreatedAt()).thenReturn(date);
        when(tweet.getText()).thenReturn(text);
        when(tweet.getUser()).thenReturn(user);
        when(tweet.getRetweetCount()).thenReturn(retweetsCount);
        when(tweet.isRetweet()).thenReturn(isRetweet);
        when(user.getName()).thenReturn(userName);
        when(tweet.getRetweetedStatus()).thenReturn(tweet);

        List<Status> tweetList = new ArrayList<Status>();
        List<String> tweetResultList = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {
            retweetsCount = 0;
            isRetweet = i % 2 == 0;
            date = new Date(1000 * i);

            tweetList.add(tweet);
            StringBuilder result = new StringBuilder();

            result.append("[");
            result.append(TwitterOutputEditor.convertTime(tweet.getCreatedAt()));
            result.append("] ");

            result.append(TwitterOutputEditor.convertNick(tweet.getUser()));

            if (tweet.isRetweet()) {
                result.append("ретвитнул ");
                result.append(TwitterOutputEditor.convertNick(tweet.getRetweetedStatus().getUser()));
            }
            result.append(" ");
            result.append(tweet.getText());
            if (retweetsCount > 0){
                result.append(" ");
                result.append(TwitterOutputEditor.convertRetweetsCount(tweet.getRetweetCount()));
            }
            result.append(" \r");
            tweetResultList.add(result.toString());
        }

        QueryResult result = Mockito.mock(QueryResult.class);
        Mockito.when(result.nextQuery()).thenReturn(null);
        Mockito.when(result.getTweets()).thenReturn(tweetList);

        Twitter twitter = Mockito.mock(Twitter.class);
        Mockito.when(twitter.search(Mockito.any())).thenReturn(result);
        ArgumentsList programArguments = new ArgumentsList();
        JCommander jcommander = new JCommander(programArguments, "-q", "someQuery");


        PrintStream standartOut = System.out;
        OutputStream clientByteOut = new ByteArrayOutputStream();
        PrintStream clientOut = new PrintStream(clientByteOut, true);
        System.setOut(clientOut);

        TwitterOutputEditor testedEditor = new TwitterOutputEditor(programArguments, twitter);
        testedEditor.simpleMode();

        System.setOut(standartOut);

        String[] tweetsOut = clientByteOut.toString().split("[\n]");
        for (int i = 0; i < tweetList.size(); ++i) {
            //assertEquals(tweetsOut[i + 2], tweetResultList.get(i));
        }
    }
}
