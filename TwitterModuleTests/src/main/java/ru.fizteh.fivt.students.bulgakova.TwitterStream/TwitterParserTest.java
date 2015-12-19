package ru.fizteh.fivt.students.bulgakova.TwitterStream;

import com.beust.jcommander.JCommander;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.equalTo;


public class TwitterParserTest {

    private static String[] arguments1;
    private static String[] arguments2;
    private static String[] arguments3;
    private static String[] arguments4;
    private static TwitterParser twitterParserArgs1;
    private static TwitterParser twitterParserArgs2;
    private static TwitterParser twitterParserArgs3;
    private static TwitterOutput twitterOutput;
    private static String printedString;

    @BeforeClass
    public static void SetUpArguments() {
        arguments1 = new String[]{"-q", "hello", "-s", "-l", "10", "-h"};
        arguments2 = new String[]{"--query", "hello", "--stream", "--hideRetweets", "--limit", "10", "--help"};
        arguments3 = new String[]{};
        arguments4 = new String[]{"-l", "100000000"};

        twitterParserArgs1 = new TwitterParser();
        twitterParserArgs2 = new TwitterParser();
        twitterParserArgs3 = new TwitterParser();
    }

    @Test
    public void GettersTest() {

        JCommander jc1 = new JCommander(twitterParserArgs1, arguments1);

        Assert.assertThat("hello", equalTo(twitterParserArgs1.getKeyword()));
        Assert.assertThat(true, equalTo(twitterParserArgs1.getIfStream()));
        Assert.assertThat(false, equalTo(twitterParserArgs1.getHideRetweets()));
        Assert.assertThat(10, equalTo(twitterParserArgs1.getLimit()));
        Assert.assertThat(true, equalTo(twitterParserArgs1.getIfHelp()));
        Assert.assertThat(true, equalTo(twitterParserArgs1.getIfLimit()));


        JCommander jc2 = new JCommander(twitterParserArgs2, arguments2);

        Assert.assertThat("hello", equalTo(twitterParserArgs2.getKeyword()));
        Assert.assertThat(true, equalTo(twitterParserArgs2.getIfStream()));
        Assert.assertThat(true, equalTo(twitterParserArgs2.getHideRetweets()));
        Assert.assertThat(10, equalTo(twitterParserArgs2.getLimit()));
        Assert.assertThat(true, equalTo(twitterParserArgs2.getIfHelp()));
        Assert.assertThat(true, equalTo(twitterParserArgs2.getIfLimit()));


        JCommander jc3 = new JCommander(twitterParserArgs3, arguments3);

        Assert.assertThat(false, equalTo(twitterParserArgs3.getIfStream()));
        Assert.assertThat(false, equalTo(twitterParserArgs3.getHideRetweets()));
        Assert.assertThat(false, equalTo(twitterParserArgs3.getIfHelp()));
        Assert.assertThat(false, equalTo(twitterParserArgs3.getIfLimit()));
    }


    @Test
    public void PrintingResultOfSearchingArgumentsTest() {
        printedString = twitterParserArgs1.printingResultOfSearchingArguments();
        Assert.assertThat(true, equalTo(twitterParserArgs1.checkArgs));
        Assert.assertThat(true, equalTo(printedString.contains("неверно")));
        Assert.assertThat(true, equalTo(printedString.contains("О ПРОГРАММЕ")));

        printedString = twitterParserArgs3.printingResultOfSearchingArguments();
        Assert.assertThat(false, equalTo(twitterParserArgs3.checkArgs));
        Assert.assertThat(false, equalTo(printedString.contains("О ПРОГРАММЕ")));
    }

    @Test
    public void ParseArgumentsTest() throws Exception{
        try {
            twitterParserArgs1.ParseArguments(arguments1);
            Assert.assertThat(false, equalTo(twitterParserArgs1.checkArgs));
            twitterParserArgs1.ParseArguments(arguments4);
        }
        catch (Exception e) {
            Assert.assertThat("Limit is out of range", equalTo(e.getMessage()));
        }
    }

    @Before
    public void SetUpDataToPrintHelpTest() {
        twitterOutput.printHelp();
    }

    @After
    public void CleanDataAfterPrintHelpTest() {
        twitterParserArgs1.printedString = "";
        twitterParserArgs1.isSomethingGoingToPrint = false;
    }

    @Test
    public void PrintHelpTest() {
        Assert.assertThat(true, equalTo(twitterParserArgs1.isSomethingGoingToPrint));
        Assert.assertThat(true, equalTo(twitterParserArgs1.printedString.contains("О ПРОГРАММЕ")));
        Assert.assertThat(false, equalTo(twitterParserArgs1.isSomethingGoingToPrint));
        Assert.assertThat(false, equalTo(twitterParserArgs1.printedString.contains("О ПРОГРАММЕ")));
    }

}
