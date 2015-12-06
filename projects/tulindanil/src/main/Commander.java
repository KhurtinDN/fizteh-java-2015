package main;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Commander {
    @Parameter
    public List<String> parameters;

    @Parameter(names = {"--query", "-q"}, description = "Запрос")
    public String query;

    @Parameter(names = {"--place", "-p"}, description = "Искать по заданному региону. Если значение равно nearby или параметр отсутствует - искать по ip")
    public String place;

    @Parameter(names = {"--stream", "-s"}, description = "если параметр задан, то приложение должно равномерно и непрерывно с задержкой в 1 секунду печать твиты на экран.")
    public boolean stream = false;

    @Parameter(names = "--hideRetweets", description = "если параметр задан, нужно фильтровать ретвиты")
    public boolean hideRetweets = false;

    @Parameter(names = {"--limit", "-l"}, description = "выводить только твитов. Не применимо для --stream режима")
    public int limit = 0;

    @Parameter(names = {"--help", "-h"}, description = "печатает справку")
    public boolean help;

    public Commander() {
        parameters = new ArrayList<>();
    }
}