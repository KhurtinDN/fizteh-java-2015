package utils;

public final class TextUtils {
    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_BLUE = "\u001B[34m";

    // to prevent instantiating
    // this class must be used as static only
    private TextUtils() {}

    public static String coloredText(String text, String color) {
        return color +
                text +
                COLOR_RESET;
    }
}
