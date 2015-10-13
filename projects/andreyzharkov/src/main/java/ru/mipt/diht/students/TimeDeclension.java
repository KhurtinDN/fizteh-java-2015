package ru.mipt.diht.students;

/**
 * Created by Андрей on 11.10.2015.
 */
public class TimeDeclension {
    public static final String timeInRightForm(String timeUnion, int count) {
        switch (count % 10) {
            case 1:
                if (count / 10 != 1) {
                    switch (timeUnion) {
                        case "DAY":
                            return "день";
                        case "HOUR":
                            return "час";
                        case "MINUTE":
                            return "минуту";
                        default:
                            return "Wrong Arguments";
                    }
                }
            case 2:
            case 3:
            case 4:
                if (count / 10 != 1) {
                    switch (timeUnion) {
                        case "DAY":
                            return "дня";
                        case "HOUR":
                            return "часа";
                        case "MINUTE":
                            return "минуты";
                        default:
                            return "Wrong Arguments";
                    }
                }
            default:
                switch (timeUnion) {
                    case "DAY":
                        return "дней";
                    case "HOUR":
                        return "часов";
                    case "MINUTE":
                        return "минут";
                    default:
                        return "Wrong Arguments";
                }
        }
    }
}
