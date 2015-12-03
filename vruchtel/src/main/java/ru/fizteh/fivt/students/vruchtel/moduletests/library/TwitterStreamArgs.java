package ru.fizteh.fivt.students.vruchtel.moduletests.library;

/**
 * Created by Серафима on 24.11.2015.
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
    private Integer limit = MAX_TWEETS_COUNT;

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
        if(limit != MAX_TWEETS_COUNT) {
            return true;
        } else return false;
    }

    public Boolean isSetPlace() {
        if(place != "") {
            return true;
        } else return false;
    }


    public Boolean incorrectArguments = false;
    private JCommander jc;

    public Boolean isSomethingGoingToPrint = false;
    public String printedString = "";

    private final static int MAX_TWEETS_COUNT = 2700;

    //разобрать аргументы
    public void ParseArguments(String args[]) throws Exception{
        //при обработке аргументов сразу надо отловить все ошибки
        incorrectArguments = false;
        try {
            jc = new JCommander(this, args);
            if(limit > MAX_TWEETS_COUNT) {
                throw new IllegalArgumentException("Limit is out of range");
            }
        } catch (Exception exception) {
            incorrectArguments = true;
            throw exception;
        }
    }

    //если что-то выводится на экран, то программа должна завершаться; необходимо учесть этот факт
    public String printingResultOfSearchingArguments () {
        if(isStreamUse() && isSetLimit()) {
            incorrectArguments = true;
        }
        if(incorrectArguments) {
            printedString = "Ошибки в переданных аргументах. Пожалуйста, прочитайте справку.\n";
        }
        if(incorrectArguments || isHelpUse()) {
            isSomethingGoingToPrint = true;
            printHelp();
        }

        return printedString;
    }

    //печать справки
    public void printHelp() {
        isSomethingGoingToPrint = true;

        printedString += "HELP\n";
        printedString += "Консольное приложение, выводящее на экран поток твитов по заданным условиям\n";
        printedString += "java TwitterStream [--query|-q] [--place|-p] [--stream|-s] [--hideRetweets] ";
        printedString += "[--limits|-l] [--help|-h]\n";
        printedString += "Параметры\n";
        printedString += "\t query - ключевые слова для поиска твитов\n";
        printedString += "\t place - искать по заданному региону (например, Moscow), если параметр ";
        printedString += "отсутствует или равен nearby, регион определяется по ip\n";
        printedString +=  "\t stream - если параметр задан, то равномерно и непрерывно с ";
        printedString += "задержкой в 1 секунду печатается поток твитов\n";
        printedString += "\t hideRetweets - скрывать ретвиты\n";
        printedString += "\t limit - число, ограничивающее количество твитов, неприменимо для --stream ";
        printedString += "режима\n";
        printedString += "\t help - печатает справку\n";
    }
}
