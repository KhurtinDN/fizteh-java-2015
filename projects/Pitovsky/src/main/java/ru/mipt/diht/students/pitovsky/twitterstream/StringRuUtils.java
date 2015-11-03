package ru.mipt.diht.students.pitovsky.twitterstream;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringRuUtils {
    public enum NounEnding {
        NOMINATIVE (0),
        ACCUSATIVE (1),
        GENITIVE (2),
        DATIVE (3),
        PREPOSITIONAL (4),
        INSTRUMENTAL (5),
        NOMINATIVE_MANY (6),
        ACCUSATIVE_MANY (7),
        GENITIVE_MANY (8),
        DATIVE_MANY (9),
        PREPOSITIONAL_MANY (10),
        INSTRUMENTAL_MANY (11);

        private int number;

        NounEnding(int nmb) {
            number = nmb;
        }
    }

    private static final int NUM_DEC = 10;
    private static final int NUM_SECOND_ENDING = 5; //start for new numeral endings in ru lang

    private static final Map<String, String[]> NOUN_ENDINGS_MAP
            = Collections.unmodifiableMap(new HashMap<String, String[]>() { {
        put("минута", new String[] {"минута", "минуты", "минуту", "минуте", "минуте", "минутой",
                                    "минуты", "минут", "минуты", "минутам", "минутах", "минутами"});
        put("час", new String[] {"час", "часа", "час", "часу", "часе", "часом",
                                 "часы", "часов", "часы", "часам", "часах", "часами"});
        put("день", new String[] {"день", "дня", "день", "дню", "дне", "днем",
                                  "дни", "дней", "дни", "дням", "днях", "днями"});
        put("ретвит", new String[] {"ретвит", "ретвита", "ретвит", "ретвиту", "ретвите", "ретвитом",
                                    "ретвиты", "ретвитов", "ретвиты", "ретвитам", "ретвитах", "ретвитами"});
    } });

    public static String getChangedNoun(String word, NounEnding ending) {
        return NOUN_ENDINGS_MAP.get(word)[ending.number];
    }

    public static String getNumeralWord(String word, int value) {
        if (value / NUM_DEC != 1 && value % NUM_DEC == 1) {
            return getChangedNoun(word, NounEnding.NOMINATIVE); //like 1, 21, 31...
        }
        if (value / NUM_DEC != 1 && value % NUM_DEC > 1
                && value % NUM_DEC < NUM_SECOND_ENDING) {
            return getChangedNoun(word, NounEnding.ACCUSATIVE);
        }
        return getChangedNoun(word, NounEnding.ACCUSATIVE_MANY);
    }

    public static String getNumeralsAgo(String word, int value) {
        return value + " " + getNumeralWord(word, value) + " назад";
    }
}
