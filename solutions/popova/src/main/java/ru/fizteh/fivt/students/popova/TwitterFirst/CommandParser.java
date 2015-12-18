package ru.fizteh.fivt.students.popova.TwitterFirst;

import com.beust.jcommander.Parameter;


/**
 * Created by V on 08.10.2015.
 */

public class CommandParser {

    @Parameter(names = {"-q", "--query"}, description = "����� ��� ������" )
        private String queryWord = null;
    @Parameter(names = {"--stream", "-s"}, description = "����� ������ � ���������")
        private boolean Stream = false;

    @Parameter(names = {"--limit", "-l"},
            description = " ����� ��������� ���������� ������")
    private int number = -1;

    @Parameter(names = {"--help", "-h"}, description = "������ �������")
    private boolean help = false;

    public boolean giveHelp() {
        return help;
    }
    public  boolean IsLimited(){
        return(number != -1);
    }
    public String QueryWord(){
        return queryWord;
    }
    public int Limit(){
        return number;
    }
    public boolean IsStream(){
        return Stream;
    }
}
