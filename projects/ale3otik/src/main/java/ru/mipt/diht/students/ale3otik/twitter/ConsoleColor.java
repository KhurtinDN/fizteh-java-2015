package ru.mipt.diht.students.ale3otik.twitter;

/**
 * Created by alex on 10.10.15.
 */
public final class ConsoleColor {

    public enum Param { blue, purple, bold }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BOLD = "\033[1m";

    public static String getParamsEscape(Param[] params) {
        String fullEscapesStirng = "";
        for (Param p : params) {
            switch (p) {
                case blue:
                    fullEscapesStirng += ANSI_BLUE;
                    break;
                case purple:
                    fullEscapesStirng += ANSI_PURPLE;
                    break;
                case bold:
                    fullEscapesStirng += ANSI_BOLD;
                default:
            }
        }
        return fullEscapesStirng;
    }

    public static String getResetEscape() {
        return ANSI_RESET;
    }

    public static void printFigureText(String text, Param[] params) {
        String specialEscapes = getParamsEscape(params);
        System.out.print(specialEscapes + text + getResetEscape());
    }
}
