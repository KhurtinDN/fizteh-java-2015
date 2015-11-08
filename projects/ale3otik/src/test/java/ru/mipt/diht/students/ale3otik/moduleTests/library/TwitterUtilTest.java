package ru.mipt.diht.students.ale3otik.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.mipt.diht.students.ale3otik.twitter.ConsoleUtil;
import ru.mipt.diht.students.ale3otik.twitter.TwitterUtil;

/**
 * Created by alex on 08.11.15.
 */
public class TwitterUtilTest extends TestCase {
    final private static int SEPARATOR_LENGTH = 80;
    final private static String userName = "alex";

    @Test
    public void testGettingSplitLine(){
        StringBuilder separator = new StringBuilder();
        for(int i = 0; i < SEPARATOR_LENGTH; ++i){
            separator.append("-");
        }
        assertEquals(TwitterUtil.getSplitLine(),separator.toString());
    }

    @Test
    public void testGetUserNameStyle() {
        String expected = ConsoleUtil.Style.BLUE.line(ConsoleUtil.Style.BOLD.line("@" + userName));
        assertEquals(expected,TwitterUtil.getUserNameStyle(userName));
    }

//    @Test
//    public void getFormattedTweetToPrint(){
//        Status satus = new Status;
//        satus.get
////        DataObjectFactory.createStatus();
////        DataObjectFactory.getRawJSON(satus);
//
//    }
}