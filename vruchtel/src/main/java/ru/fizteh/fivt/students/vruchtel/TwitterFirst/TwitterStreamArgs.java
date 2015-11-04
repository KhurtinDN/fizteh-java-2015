package ru.fizteh.fivt.students.vruchtel.TwitterFirst;

/**
 * Created by �������� on 08.10.2015.
 */

import com.beust.jcommander.*;

public class TwitterStreamArgs {
    @Parameter (names = {"--query", "-q"}, description = "query or keyword for stream")
    private String keyword;

    @Parameter (names = {"--place", "-p"}, description = "location or nearby - search for ip")
    private String place = "";

    @Parameter (names = {"--stream", "-s"}, description = "use stream to print tweets")
    private Boolean streamUse = false;//���� ���������, �� ����� true

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

    //����������� ������
    TwitterStreamArgs(String args[]) {
        //��� ��������� ���������� ����� ���� �������� ��� ������
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
            System.out.println("������ � ���������� ����������. ����������, ���������� �������.");
        }
        if(incorrectArguments || isHelpUse()) {
            printHelp();
            System.exit(0);
        }

    }

    //������ �������
    static void printHelp(){
        System.out.println("HELP");
        System.out.println("���������� ����������, ��������� �� ����� ����� ������ �� �������� ��������");
        System.out.println("java TwitterStream [--query|-q] [--place|-p] [--stream|-s] [--hideRetweets] "
                + "[--limits|-l] [--help|-h]");
        System.out.println("���������");
        System.out.println("\t query - �������� ����� ��� ������ ������");
        System.out.println("\t place - ������ �� ��������� ������� (��������, Moscow), ���� �������� "
                + "����������� ��� ����� nearby, ������ ������������ �� ip");
        System.out.println("\t stream - ���� �������� �����, �� ���������� � ���������� � ��������� � 1 ������� "
                + "���������� ����� ������");
        System.out.println("\t hideRetweets - �������� �������");
        System.out.println("\t limit - �����, �������������� ���������� ������, ����������� ��� --stream ������");
        System.out.println("\t help - �������� �������");
    }
}
