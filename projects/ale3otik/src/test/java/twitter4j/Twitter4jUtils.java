package twitter4j;

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.Arguments;
import ru.mipt.diht.students.ale3otik.twitter.TwitterSingleQuery;

import java.util.ArrayList;

/**
 * Created by alex on 08.11.15.
 */

public class Twitter4jUtils extends TestCase{
    @Test
    public void testMakeTweetsJSON() {
        Arguments arguments;
        JCommander jcm;
        String [] args;

        ArrayList<Status> allTweets;
        args = new String[6];
        args[0] = "-q";
        args[1] = "body";
        args[2] = "-l";
        args[3] = "3";
        args[4] = "-p";
        args[5] = "London";

        arguments = new Arguments();
        jcm = new JCommander(arguments);
        jcm.parse(args);
        try {
             allTweets = TwitterSingleQuery.getSingleQueryStatuses(arguments);
        }catch(Exception e){
            return;
        }
        System.out.print(TwitterObjectFactory.getRawJSON(allTweets));
    }
}
