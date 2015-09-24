package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 23.09.15.
 */


import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

class JCommanderParser {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"--stream", "-s"},
            description = "print stream of new tweets")
    private boolean stream = false;

    @Parameter(names = {"--help", "-h"}, description = "print help man")
    private boolean help = false;

    @Parameter(names = {"--query", "-q"}, description = "set query parameters")
    private String query = "";

    /*@Parameter(names = {"--place", "-p"}, description = "set location")
    private boolean help = false;*/

    public boolean isStream() {
        return stream;
    }

    public boolean isHelp() {
        return help;
    }

    public String getQuery() {
        return query;
    }
}
