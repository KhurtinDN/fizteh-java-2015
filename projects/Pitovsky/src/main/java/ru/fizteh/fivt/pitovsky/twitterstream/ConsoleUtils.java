package ru.fizteh.fivt.pitovsky.twitterstream;

public class ConsoleUtils {
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
        TextColor(int clr) {
            color = clr;
        }
    }

    private static final char ESCAPE = (char) 27;

    private static String setClr(TextColor tcolor) {
        return "" + ESCAPE + "[" + tcolor.color + "m";
    }
    private static String setStClr() {
        return setClr(TextColor.STANDART);
    }

    public static String colorizeString(String string, TextColor tcolor) {
        return setClr(tcolor) + string + setStClr();
    }
}
