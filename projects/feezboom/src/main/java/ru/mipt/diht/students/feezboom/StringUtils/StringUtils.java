package ru.mipt.diht.students.feezboom.StringUtils;

/**
 ** Created by avk on 10.10.15.
 **/
public class StringUtils {

    public enum Color {
        BLACK {
            public String paint(String input) {
                return "\u001B[30m" + input + ANSI_RESET;
            }
        },
        RED {
            public String paint(String input) {
                return "\u001B[31m" + input + ANSI_RESET;
            }
        },
        GREEN {
            public String paint(String input) {
                return "\u001B[32m" + input + ANSI_RESET;
            }
        },
        YELLOW {
            public String paint(String input) {
                return "\u001B[33m" + input + ANSI_RESET;
            }
        },
        BLUE {
            public String paint(String input) {
                return "\u001B[34m" + input + ANSI_RESET;
            }
        },
        PURPLE {
            public String paint(String input) {
                return "\u001B[35m" + input + ANSI_RESET;
            }
        },
        CYAN {
            public String paint(String input) {
                return "\u001B[36m" + input + ANSI_RESET;
            }
        },
        WHITE {
            public String paint(String input) {
                return "\u001B[37m" + input + ANSI_RESET;
            }
        };
        public abstract String paint(String input);
        public static final String ANSI_RESET = "\u001B[0m";
    }

    public static String getPainted(String string, String color)   {
        String answer;
        switch (color.toLowerCase()) {
            case "black" :
                answer = Color.BLACK.paint(string);
                break;
            case "red" :
                answer = Color.RED.paint(string);
                break;
            case "green" :
                answer = Color.GREEN.paint(string);
                break;
            case "yellow" :
                answer = Color.YELLOW.paint(string);
                break;
            case "blue" :
                answer = Color.BLUE.paint(string);
                break;
            case "purple" :
                answer = Color.PURPLE.paint(string);
                break;
            case "cyan" :
                answer = Color.CYAN.paint(string);
                break;
            case "white" :
                answer = Color.WHITE.paint(string);
                break;
            default :
                answer = string;
        }
        return answer;
    }
    private static char getRussianChar(char in) {
        switch (in) {
            case 'a' :case 'A' :
                return 'а';
            case 'b' :case 'B' :
                return 'б';
            case 'c' :case 'C' :
                return 'с';
            case 'D' : case 'd' :
                return 'д';
            case 'e' :case 'E' :
                return 'е';
            case 'f' :case 'F' :
                return 'ф';
            case 'g' :case 'G' :
                return 'г';
            case 'h' :case 'H' :
                return 'х';
            case 'i' :case 'I' :
                return 'и';
            case 'j' :case 'J' :
                return 'й';
            case 'k':case 'K' :
                return 'к';
            case 'l' :case 'L' :
                return 'л';
            case 'm' :case 'M' :
                return 'м';
            case 'n' :case 'N' :
                return 'н';
            case 'o' :case 'O' :
                return 'о';
            case 'p' :case 'P' :
                return 'п';
            case 'r' :case 'R' :
                return 'р';
            case 's' : case 'S' :
                return 'с';
            case 't':case 'T' :
                return 'т';
            case 'u' :case 'U' :
                return 'у';
            case 'v' :case 'V' :
                return 'в';
            case 'y' :case 'Y' :
                return 'ы';
            case 'z' :case 'Z' :
                return 'з';
            case '\'' :
                return 'ь';
            default :
                return in;
        }
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
