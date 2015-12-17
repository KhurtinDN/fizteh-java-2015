package ru.fizteh.fivt.students.bulgakova.TwitterStream;

/**
 * Created by Bulgakova Daria, 496.
 */

import com.beust.jcommander.*;

public class TwitterParser {

    private JCommander jCommander;
    private Boolean checkArgs = false;

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
    //потом надо будет проверить, задано ли ограничение

    @Parameter(names = { "--help", "-h" }, description = "if help is needed")
    private Boolean ifHelp = false;
    public Boolean getIfHelp() {
        return ifHelp;
    }


    TwitterParser(String args[]) {
        try {
            jCommander = new JCommander(this, args);
        } catch (com.beust.jcommander.ParameterException exception) {
          checkArgs = true;
        }

        if((getLimit() > 0) && getIfStream()) {
            checkArgs = true;
        }

        if (checkArgs || getIfHelp()) {
            TwitterOutput.printHelp();
            System.exit(0);
        }

    }

}
