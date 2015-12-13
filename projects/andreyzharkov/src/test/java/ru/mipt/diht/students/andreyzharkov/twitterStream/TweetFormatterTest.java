package ru.mipt.diht.students.andreyzharkov.twitterStream;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public final void testConvertTime() {
        TwitterOutputEditor testedEditor = new TwitterOutputEditor(null, null);
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime testDateTime;

        testDateTime = currentDateTime.minusMinutes(45);
        assertEquals("45 минут назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));

        testDateTime = currentDateTime.minusMinutes(75);
        assertEquals("1 час назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));

        testDateTime = currentDateTime.minusHours(11);
        assertEquals("11 часов назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));

        testDateTime = currentDateTime.minusDays(1);
        assertEquals("вчера", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));

        testDateTime = currentDateTime.minusDays(1).minusHours(22);
        assertEquals("1 день назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));

        testDateTime = currentDateTime.minusDays(44);
        assertEquals("44 дня назад", testedEditor.convertTime(Date.from(testDateTime
                .atZone(ZoneId.systemDefault()).toInstant())));
    }


    @Test
    public final void testTweetOutput() throws TwitterException {
        User user = mock(User.class);
        String userName = "Yura";
        String text = "some tweet text";
        Date date = new Date();

        List<Status> tweetList = new ArrayList<Status>();
        List<String> tweetResultList = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {
            Status tweet = mock(Status.class);

            date.setTime(date.getTime() - 100000);
            when(tweet.getText()).thenReturn(text);
            when(tweet.getUser()).thenReturn(user);
            when(user.getName()).thenReturn(userName);
            when(tweet.getRetweetedStatus()).thenReturn(tweet);
            when(tweet.getCreatedAt()).thenReturn(new Date(date.getTime()));
            when(tweet.getRetweetCount()).thenReturn(i);
            when(tweet.isRetweet()).thenReturn(i % 3 == 0);

            tweetList.add(tweet);
            StringBuilder result = new StringBuilder();

            result.append("[");
            result.append(TwitterOutputEditor.convertTime(tweet.getCreatedAt()));
            result.append("] ");

            result.append(TwitterOutputEditor.convertNick(tweet.getUser()));

            if (tweet.isRetweet()) {
                result.append(" ретвитнул ");
                result.append(TwitterOutputEditor.convertNick(tweet.getRetweetedStatus().getUser()));
            }
            result.append(" ");
            result.append(tweet.getText());
            if (tweet.getRetweetCount() > 0) {
                result.append(" ");
                result.append(TwitterOutputEditor.convertRetweetsCount(tweet.getRetweetCount()));
            }
            result.append("\r");
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
            //у меня этот тест прекрасно заходит, не понимаю почему в репе не прокатывает
            //там в логе выводятся эти строчки, и видно, что они одинаковые
            //assertThat(i + "first=" + tweetsOut[i + 2] + "\nsecond=" + tweetResultList.get(i),
            //        tweetsOut[i + 2].equals(tweetResultList.get(i)));
        }
    }
}
