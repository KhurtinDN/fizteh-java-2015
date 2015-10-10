package ru.mipt.diht.students.feezboom.StringUtils;

/**
 * Created by avk on 10.10.15.
 */
public class StringUtils {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static String toColor(String string, String color)   {
        String answer;
        switch (color) {
            case "black" :case "Black" :case "BLACK" :
                answer = ANSI_BLACK + string + ANSI_RESET;
                break;
            case "red" :case "Red" :case "RED" :
                answer = ANSI_RED + string + ANSI_RESET;
                break;
            case "green" :case "Green" :case "GREEN" :
                answer = ANSI_GREEN + string + ANSI_RESET;
                break;
            case "yellow" :case "Yellow" :case "YELLOW" :
                answer = ANSI_YELLOW + string + ANSI_RESET;
                break;
            case "blue" :case "Blue" :case "BLUE" :
                answer = ANSI_BLUE + string + ANSI_RESET;
                break;
            case "purple" :case "Purple" :case "PURPLE" :
                answer = ANSI_PURPLE + string + ANSI_RESET;
                break;
            case "cyan" :case "Cyan" :case "CYAN" :
                answer = ANSI_CYAN + string + ANSI_RESET;
                break;
            case "white" :case "White" :case "WHITE" :
                answer = ANSI_WHITE + string + ANSI_RESET;
                break;
            default :
                answer = string;
        }
        return answer;
    }
    private static char getRussianChar(char in) {
        char answer;
        switch (in) {
            case 'a' :case 'A' :
                answer = 'а';
                break;
            case 'b' :case 'B' :
                answer = 'б';
                break;
            case 'c' :case 'C' :
                answer = 'с';
                break;
            case 'D' : case 'd' :
                answer = 'д';
                break;
            case 'e' :case 'E' :
                answer = 'е';
                break;
            case 'f' :case 'F' :
                answer = 'ф';
                break;
            case 'g' :case 'G' :
                answer = 'г';
                break;
            case 'h' :case 'H' :
                answer = 'х';
                break;
            case 'i' :case 'I' :
                answer = 'и';
                break;
            case 'j' :case 'J' :
                answer = 'й';
                break;
            case 'k':case 'K' :
                answer = 'к';
                break;
            case 'l' :case 'L' :
                answer = 'л';
                break;
            case 'm' :case 'M' :
                answer = 'м';
                break;
            case 'n' :case 'N' :
                answer = 'н';
                break;
            case 'o' :case 'O' :
                answer = 'о';
                break;
            case 'p' :case 'P' :
                answer = 'п';
                break;
            case 'r' :case 'R' :
                answer = 'р';
                break;
            case 's' : case 'S' :
                answer = 'с';
                break;
            case 't':case 'T' :
                answer = 'т';
                break;
            case 'u' :case 'U' :
                answer = 'у';
                break;
            case 'v' :case 'V' :
                answer = 'в';
                break;
            case 'y' :case 'Y' :
                answer = 'ы';
                break;
            case 'z' :case 'Z' :
                answer = 'з';
                break;
            case '\'' :
                answer = 'ь';
                break;
            default :
                answer = in;
                break;
        }
        return answer;
    }
    private static String getEnglishChar(char in) {
        String answer = "";
        switch (in) {
            case 'а' :case 'А' :
                answer += "a";
                break;
            case 'б' :case 'Б' :
                answer += "b";
                break;
            case 'в' :case 'В' :
                answer += "v";
                break;
            case 'г' :case 'Г' :
                answer += "g";
                break;
            case 'д' :case 'Д' :
                answer += "d";
                break;
            case 'е' :case 'Е' :
                answer += "e";
                break;
            case 'ё' :case 'Ё' :
                answer += "yo";
                break;
            case 'ж' :case 'Ж' :
                answer += "zh";
                break;
            case 'з' :case 'З' :
                answer += "z";
                break;
            case 'и' :case 'И' :
                answer += "i";
                break;
            case 'й' :case 'Й' :
                answer += "y";
                break;
            case 'к' :case 'К' :
                answer += "k";
                break;
            case 'л' :case 'Л' :
                answer += "l";
                break;
            case 'м' :case 'М' :
                answer += "m";
                break;
            case 'н' :case 'Н' :
                answer += "n";
                break;
            case 'о' :case 'О' :
                answer += "o";
                break;
            case 'п' :case 'П' :
                answer += "p";
                break;
            case 'р' :case 'Р' :
                answer += "r";
                break;
            case 'с' :case 'С' :
                answer += "s";
                break;
            case 'т' :case 'Т' :
                answer += "t";
                break;
            case 'у' :case 'У' :
                answer += "u";
                break;
            case 'ф' :case 'Ф' :
                answer += "f";
                break;
            case 'х' :case 'Х' :
                answer += "kh";
                break;
            case 'ц' :case 'Ц' :
                answer += "ts";
                break;
            case 'ч' :case 'Ч' :
                answer += "ch";
                break;
            case 'ш' :case 'Ш' :
                answer += "sh";
                break;
            case 'щ' :case 'Щ' :
                answer += "shh";
                break;
            case 'ъ' :
                break;
            case 'ы' :case 'Ы' :
                answer += "y";
                break;
            case 'ь' :
                answer += "'";
                break;
            case 'э' :case 'Э' :
                answer += "e";
                break;
            case 'ю' :case 'Ю' :
                answer += "yu";
                break;
            case 'я' :case 'Я' :
                answer += "ya";
                break;
            default :
                answer += in;
        }
        return answer;
    }
    public static String translitToRussian(String input) {
        String answer = "";

        for (int i = 0; i < input.length(); i++) {
            char t = input.charAt(i);
            boolean check = i + 1 < input.length();
            switch (t) {
                case 'c' :case 'C':
                    if (check && input.charAt(i + 1) == 'h') {
                        answer += 'ч';
                        i++;
                    } else {
                        answer += 'ц';
                    }
                    break;
                case 'z': case 'Z' :
                    if (check && input.charAt(i + 1) == 'h') {
                        answer += 'ж';
                        i++;
                    } else {
                        answer += 'з';
                    }
                    break;
                case 's' : case 'S' :
                    if (check) {
                        i++;
                        switch (input.charAt(i)) {
                            case 'h':
                                if (i + 1 < input.length()
                                        && input.charAt(i + 1) == 'h') {
                                    i++;
                                    answer += 'щ';
                                    break;
                                }
                                answer += 'ш';
                                break;
                            default:
                                answer += 'с';
                                i--;
                                break;
                        }
                    } else {
                        answer += 'с';
                    }
                    break;
                case 'j': case 'J' :
                    if (check) {
                        i++;
                        switch (input.charAt(i)) {
                            case 'e':
                                answer += 'э';
                                break;
                            case 'o' :
                                answer += 'ё';
                                break;
                            default:
                                answer += 'й';
                                i--;
                                break;
                        }
                    } else {
                        answer += 'й';
                    }
                    break;
                case 'y' : case 'Y' :
                    if (check) {
                        i++;
                        switch (input.charAt(i)) {
                            case 'a':
                                answer += 'я';
                                break;
                            case 'y' :
                                answer += "ый";
                                break;
                            case 'o' :
                                answer += "йо";
                                break;
                            case 'e' :
                                answer += 'е';
                                break;
                            case 'u':
                                answer += 'ю';
                                break;
                            default:
                                answer += 'ы';
                                i--;
                                break;
                        }
                    } else {
                        answer += getRussianChar(t);
                    }
                    break;
                case 't' :case 'T' :
                    if (check) {
                        i++;
                        if (input.charAt(i) == 's') {
                            answer += 'ц';
                        } else {
                            answer += 'т';
                            i--;
                        }
                    } else {
                        answer += 'т';
                    }
                    break;
                case 'k' : case 'K' :
                    if (check) {
                        i++;
                        if (input.charAt(i) == 'h') {
                            answer += 'х';
                        } else {
                            answer += 'к';
                            i--;
                        }
                    } else {
                        answer += 'к';
                    }
                default :
                    answer += StringUtils.getRussianChar(input.charAt(i));
                    break;
            }
        }
        if (input.charAt(input.length() - 1) == 'y'
                && input.charAt(input.length() - 2) != 'y') {
            answer += 'й';
        }
        return answer;
    }
    public static String russianToTranslit(String input) {
        String answer = "";
        for (int i = 0; i < input.length(); i++) {
            char t = input.charAt(i);
            answer += getEnglishChar(t);
        }
        return answer;
    }

}
