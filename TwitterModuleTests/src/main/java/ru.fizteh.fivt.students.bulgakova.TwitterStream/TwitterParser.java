package ru.fizteh.fivt.students.bulgakova.TwitterStream;

/**
 * Created by Bulgakova Daria, 496.
 */

import com.beust.jcommander.*;

public class TwitterParser {

    private JCommander jCommander;
    public Boolean checkArgs = false;

    public Boolean isSomethingGoingToPrint = false;
    public String printedString = "";

    private final static int MAX_TWEETS_COUNT = 2700;

    @Parameter(names = { "--query", "-q" }, description = "query or keywords for stream")
    private String keyword;
    public String getKeyword(){
        return keyword;
    }

    @Parameter(names = { "--stream", "-s" }, description = "if stream mode is needed")
    private Boolean ifStream = false;
    public Boolean getIfStream(){
        return ifStream;
    }

    @Parameter(names = { "hideRetweets" }, description = "if it is necessary to hide retweets")
    private Boolean hideRetweets = false;
    public Boolean getHideRetweets(){
        return hideRetweets;
    }

    @Parameter(names = { "--limit", "-l" }, description = "amount of tweets, only for usual mode")
    private int limit = -42;
    public int getLimit() {
        return limit;
    }
    private Boolean ifLimit = false;
    public Boolean getIfLimit() { return ifLimit; }

    @Parameter(names = { "--help", "-h" }, description = "if help is needed")
    private Boolean ifHelp = false;
    public Boolean getIfHelp() {
        return ifHelp;
    }



    public void ParseArguments(String args[]) throws Exception{
        checkArgs = false;
        try {
            jCommander = new JCommander(this, args);
            if(limit > MAX_TWEETS_COUNT) {
                throw new IllegalArgumentException("Limit is out of range");
            }
        } catch (Exception exception) {
            checkArgs = true;
            throw exception;
        }
    }

    public String printingResultOfSearchingArguments () {
        if(getIfStream() && getIfLimit()) {
            checkArgs = true;
        }
        if(checkArgs) {
            printedString = "Аргументы переданы неверно\n";
        }
        if(checkArgs || getIfHelp()) {
            isSomethingGoingToPrint = true;
            TwitterOutput.printHelp();
        }

        return printedString;
    }

}
