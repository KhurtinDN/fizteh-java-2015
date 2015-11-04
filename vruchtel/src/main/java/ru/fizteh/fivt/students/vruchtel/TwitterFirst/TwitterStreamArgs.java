package ru.fizteh.fivt.students.vruchtel.TwitterFirst;

/**
 * Created by Серафима on 08.10.2015.
 */

import com.beust.jcommander.*;

public class TwitterStreamArgs {
    @Parameter (names = {"--query", "-q"}, description = "query or keyword for stream")
    private String keyword;

    @Parameter (names = {"--place", "-p"}, description = "location or nearby - search for ip")
    private String place = "";

    @Parameter (names = {"--stream", "-s"}, description = "use stream to print tweets")
    private Boolean streamUse = false;//если считается, то будет true

    @Parameter (names = {"--hideRetweets"}, description = "should mask retweets")
    private Boolean hideRetweets = false;

    @Parameter (names = {"--limit", "-l"}, description = "restriction on amount of tweets")
    private Integer limit = Integer.MAX_VALUE;

    @Parameter (names = {"--help", "-h"}, description = "should print help")
    private Boolean helpUse = false;

    public String getKeyword() { return keyword; }

    public String getPlace() {
        return place;
    }

    public Boolean isStreamUse() {
        return streamUse;
    }

    public Boolean isHideRetweets() {
        return hideRetweets;
    }

    public Integer getLimit() {
        return limit;
    }

    public Boolean isHelpUse() {
        return helpUse;
    }

    public Boolean isSetLimit() {
        if(limit != Integer.MAX_VALUE) {
            return true;
        } else return false;
    }

    public Boolean isSetPlace() {
        if(place != "") {
            return true;
        } else return false;
    }


    private Boolean incorrectArguments;

    private JCommander jc;

    //конструктор класса
    TwitterStreamArgs(String args[]) {
        //при обработке аргументов сразу надо отловить все ошибки
        incorrectArguments = false;
        try {
            jc = new JCommander(this, args);
        } catch (com.beust.jcommander.ParameterException exception) {
            incorrectArguments = true;
        }
        if(isStreamUse() && isSetLimit()) {
            incorrectArguments = true;
        }
        if(incorrectArguments) {
            System.out.println("Ошибки в переданных аргументах. Пожалуйста, прочитайте справку.");
        }
        if(incorrectArguments || isHelpUse()) {
            printHelp();
            System.exit(0);
        }

    }

    //печать справки
    static void printHelp(){
        System.out.println("HELP");
        System.out.println("Консольное приложение, выводящее на экран поток твитов по заданным условиям");
        System.out.println("java TwitterStream [--query|-q] [--place|-p] [--stream|-s] [--hideRetweets] "
                + "[--limits|-l] [--help|-h]");
        System.out.println("Параметры");
        System.out.println("\t query - ключевые слова для поиска твитов");
        System.out.println("\t place - искать по заданному региону (например, Moscow), если параметр "
                + "отсутствует или равен nearby, регион определяется по ip");
        System.out.println("\t stream - если параметр задан, то равномерно и непрерывно с задержкой в 1 секунду "
                + "печатается поток твитов");
        System.out.println("\t hideRetweets - скрывать ретвиты");
        System.out.println("\t limit - число, ограничивающее количество твитов, неприменимо для --stream режима");
        System.out.println("\t help - печатает справку");
    }
}
