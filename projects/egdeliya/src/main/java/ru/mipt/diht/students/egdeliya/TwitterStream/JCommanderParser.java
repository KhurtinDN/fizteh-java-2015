package ru.mipt.diht.students.egdeliya.TwitterStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Created by Эгделия on 13.12.2015.
 */
public class JCommanderParser {
    private final JCommander commander;

    @Parameter(names = { "-q", "--query" },
            description = "Ваши ключевые слова",
            required = true)
    private String query = "";

    public final String getQuery() {
        return query;
    }

    public JCommanderParser(String[] args) {
        commander = new JCommander(this, args);
    }

}
