package ru.mipt.diht.students.andreyzharkov.twitterStream;

/**
 * Created by Андрей on 11.10.2015.
 */
public class TimeDeclension {
    private static final int TEN = 10;

    public static String timeInRightForm(TwitterOutputEditor.Time timeUnion, int count) {
        switch (count % TEN) {
            case 1:
                if (count / TEN % TEN != 1) {
                    switch (timeUnion) {
                        case DAY:
                            return "день";
                        case HOUR:
                            return "час";
                        case MINUTE:
                            return "минуту";
                        default:
                            return "Wrong Arguments";
                    }
                }
            case 2:
            case 2 + 1://Because 3 and 4 are magic numbers
            case 2 + 2:
                if (count / TEN % TEN != 1) {
                    switch (timeUnion) {
                        case DAY:
                            return "дня";
                        case HOUR:
                            return "часа";
                        case MINUTE:
                            return "минуты";
                        default:
                            return "Wrong Arguments";
                    }
                }
            default:
                switch (timeUnion) {
                    case DAY:
                        return "дней";
                    case HOUR:
                        return "часов";
                    case MINUTE:
                        return "минут";
                    default:
                        return "Wrong Arguments";
                }
        }
    }
}
