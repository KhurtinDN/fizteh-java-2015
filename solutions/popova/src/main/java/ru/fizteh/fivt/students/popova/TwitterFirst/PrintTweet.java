package ru.fizteh.fivt.students.popova.TwitterFirst;
/**
 * Created by V on 04.11.2015.
 */
import twitter4j.Status;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class PrintTweet {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void print(Status tweet, boolean time, PrintStream out){
        if(time){
            printTime(out, tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if(tweet.getUser() != null){
            out.println(ANSI_BLUE+tweet.getUser().getName()+" ");
        }
        if(tweet.isRetweet()){
            out.println(ANSI_BLACK+"is retweet");
            out.println(ANSI_BLUE+tweet.getRetweetedStatus().getUser().getName()+": ");
            out.println(ANSI_PURPLE+tweet.getText()+"\n");
        }
        else{
            out.println(ANSI_CYAN+tweet.getText()+": ");
            out.println(ANSI_CYAN+tweet.getRetweetCount()+"\n");
        }
    }
    public static void printTime(PrintStream out, LocalDateTime when){
        LocalDateTime CurrentTime = LocalDateTime.now();
        long Minutes = ChronoUnit.MINUTES.between(when, CurrentTime);
        long Hours = ChronoUnit.HOURS.between(when, CurrentTime);
        long Days = ChronoUnit.DAYS.between(when, CurrentTime);
        if(Minutes < 2){
            out.println(ANSI_GREEN+" just now ");
        }
        else if(Hours < 1){
            out.println(ANSI_GREEN+Minutes+" minutes ago ");
        }
        else if(Days < 1){
            out.println(ANSI_GREEN+Hours+" hours ago ");
        }
        else if(Days < 2){
            out.println(ANSI_GREEN+"yesterday ");
        }
        else{
            out.println(ANSI_GREEN+Days+ " days ago");
        }
    }
}
