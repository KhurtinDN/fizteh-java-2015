package ru.fizteh.fivt.students.vruchtel.moduletests.library;

import com.beust.jcommander.JCommander;
import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by Серафима on 30.11.2015.
 */
public class TwitterStreamArgsTest {
    @BeforeClass
    public static void SetUpArguments() {
        arguments1 = new String[]{"-q", "hello", "-p", "Moscow", "-s", "-l", "10", "-h"};
        arguments2 = new String[]{"--query", "hello", "--place", "Moscow",
                "--stream", "--hideRetweets", "--limit", "10", "--help"};
        arguments3 = new String[]{};
        arguments4 = new String[]{"-l", "100000000"};

        twitterStreamArgs1 = new TwitterStreamArgs();
        twitterStreamArgs2 = new TwitterStreamArgs();
        twitterStreamArgs3 = new TwitterStreamArgs();
    }

    @Test
    public void GettersTest() {
        JCommander jc1 = new JCommander(twitterStreamArgs1, arguments1);

        Assert.assertThat("hello", equalTo(twitterStreamArgs1.getKeyword()));
        Assert.assertThat("Moscow", equalTo(twitterStreamArgs1.getPlace()));
        Assert.assertThat(true, equalTo(twitterStreamArgs1.isStreamUse()));
        Assert.assertThat(false, equalTo(twitterStreamArgs1.isHideRetweets()));
        Assert.assertThat(10, equalTo(twitterStreamArgs1.getLimit()));
        Assert.assertThat(true, equalTo(twitterStreamArgs1.isHelpUse()));
        Assert.assertThat(true, equalTo(twitterStreamArgs1.isSetLimit()));
        Assert.assertThat(true, equalTo(twitterStreamArgs1.isSetPlace()));

        JCommander jc2 = new JCommander(twitterStreamArgs2, arguments2);

        Assert.assertThat("hello", equalTo(twitterStreamArgs2.getKeyword()));
        Assert.assertThat("Moscow", equalTo(twitterStreamArgs2.getPlace()));
        Assert.assertThat(true, equalTo(twitterStreamArgs2.isStreamUse()));
        Assert.assertThat(true, equalTo(twitterStreamArgs2.isHideRetweets()));
        Assert.assertThat(10, equalTo(twitterStreamArgs2.getLimit()));
        Assert.assertThat(true, equalTo(twitterStreamArgs2.isHelpUse()));
        Assert.assertThat(true, equalTo(twitterStreamArgs2.isSetLimit()));
        Assert.assertThat(true, equalTo(twitterStreamArgs2.isSetPlace()));

        JCommander jc3 = new JCommander(twitterStreamArgs3, arguments3);

        Assert.assertThat(false, equalTo(twitterStreamArgs3.isStreamUse()));
        Assert.assertThat(false, equalTo(twitterStreamArgs3.isHideRetweets()));
        Assert.assertThat(false, equalTo(twitterStreamArgs3.isHelpUse()));
        Assert.assertThat(false, equalTo(twitterStreamArgs3.isSetLimit()));
        Assert.assertThat(false, equalTo(twitterStreamArgs3.isSetPlace()));
    }

    @Before
    public void SetUpDataToPrintHelpTest() {
        twitterStreamArgs1.printHelp();
    }

    @After
    public void CleanDataAfterPrintHelpTest() {
        twitterStreamArgs1.printedString = "";
        twitterStreamArgs1.isSomethingGoingToPrint = false;
    }

    @Test
    public void PrintHelpTest() {
        //проверим что после выполнения функции printHelp() строчка printedString содержит подстроку
        //HELP
        //а переменная isSomethingGoingToPrint = true;
        Assert.assertThat(true, equalTo(twitterStreamArgs1.isSomethingGoingToPrint));
        Assert.assertThat(true, equalTo(twitterStreamArgs1.printedString.contains("HELP")));

        Assert.assertThat(false, equalTo(twitterStreamArgs2.isSomethingGoingToPrint));
        Assert.assertThat(false, equalTo(twitterStreamArgs2.printedString.contains("HELP")));
    }

    @Test
    public void PrintingResultOfSearchingArgumentsTest() {
        printedString = twitterStreamArgs1.printingResultOfSearchingArguments();
        Assert.assertThat(true, equalTo(twitterStreamArgs1.incorrectArguments));
        Assert.assertThat(true, equalTo(printedString.contains("Ошибки")));
        Assert.assertThat(true, equalTo(printedString.contains("HELP")));

        printedString = twitterStreamArgs3.printingResultOfSearchingArguments();
        Assert.assertThat(false, equalTo(twitterStreamArgs3.incorrectArguments));
        Assert.assertThat(false, equalTo(printedString.contains("HELP")));
    }

    @Test
    public void ParseArgumentsTest() throws Exception{
        try {
            twitterStreamArgs1.ParseArguments(arguments1);
            Assert.assertThat(false, equalTo(twitterStreamArgs1.incorrectArguments));
            twitterStreamArgs1.ParseArguments(arguments4);
        }
        catch (Exception e) {
            Assert.assertThat("Limit is out of range", equalTo(e.getMessage()));
        }
    }

    private static String[] arguments1;
    private static String[] arguments2;
    private static String[] arguments3;
    private static String[] arguments4;
    private static TwitterStreamArgs twitterStreamArgs1;
    private static TwitterStreamArgs twitterStreamArgs2;
    private static TwitterStreamArgs twitterStreamArgs3;
    private static String printedString;
}
