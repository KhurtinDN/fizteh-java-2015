package ru.mipt.diht.students.ale3otik.twitter;

import java.util.function.Consumer;

/**
 * Created by alex on 10.10.15.
 */
public final class ConsoleUtil {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BOLD = "\033[1m";

    public enum Style {
        BLUE(ANSI_BLUE),
        PURPLE(ANSI_PURPLE),
        BOLD(ANSI_BOLD);

        private String escape;

        Style(String escapeCode) {
            this.escape = escapeCode;
        }

        public String line(String inputLine) {
            return this.escape + inputLine + ANSI_RESET;
        }
    }

    public static void printIntoStdout(final String strToPrint, Style... args) {
        String outStr = strToPrint.substring(0);
        setStyle(outStr, args);
        System.out.println(outStr);
    }

    public static String setStyle(String strToStyle, Style... args) {
        for (int i = 0; i < args.length; ++i) {
            strToStyle = args[i].line(strToStyle);
        }
        return strToStyle;
    }

    public static void printErrorMessage(final String strToPrint) {
        System.err.println(strToPrint);
    }

    public static Consumer<String> getStdoutConsumer() {
        return (x) -> ConsoleUtil.printIntoStdout(x);
    }
}
