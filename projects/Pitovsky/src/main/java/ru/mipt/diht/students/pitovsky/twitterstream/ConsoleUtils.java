package ru.mipt.diht.students.pitovsky.twitterstream;

public class ConsoleUtils {
    private static final char ESCAPE = (char) 27;

    public static enum TextColor {
        STANDART (0),
        BLACK (30),
        RED (31),
        GREEN (32),
        YELLOW (33),
        BLUE (34),
        MAGENTA (35),
        CYAN (36),
        WHITE (37);

        private int color;

        private String start() {
            return "" + ESCAPE + "[" + color + "m";
        }

        private String stop() {
            return STANDART.start();
        }

        TextColor(int clr) {
            color = clr;
        }
    }

    public static String colorizeString(String string, TextColor tcolor) {
        return tcolor.start() + string + tcolor.stop();
    }
}
