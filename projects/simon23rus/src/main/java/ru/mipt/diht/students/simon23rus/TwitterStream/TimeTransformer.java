package ru.mipt.diht.students.simon23rus.TwitterStream;



import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by semenfedotov on 05.12.15.
 */
public class TimeTransformer {

    private static final int SECONDS_IN_YEAR = 60 * 60 * 24 * 365;
    private static final int SECONDS_IN_MONTH = 60 * 60 * 24 * 30;
    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int TEN = 10;
    private static final int FIFTEEN = 15;
    private static final int TWO = 2;
    private static final int ONE = 1;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int TWENTY_FOUR = 24;
    private static boolean isItYesterday;


   public static String correctRussianText(long deltaInSeconds) {
        System.out.println(deltaInSeconds + "delta");
        if (deltaInSeconds < TWO * SECONDS_IN_MINUTE) {
            return "[<Только что>] ";
        }
        else if (deltaInSeconds < SECONDS_IN_HOUR) {
            long minutes = deltaInSeconds / SECONDS_IN_MINUTE;
            if (minutes >= FIFTEEN || minutes <= TEN) {
                if (minutes % TEN == ONE) {
                    return "[<" + minutes + " минуту назад>] ";
                }
                else if (minutes % TEN == TWO || minutes % TEN == THREE || minutes % TEN == FOUR) {
                    return "[<" + minutes + " минуты назад>] ";
                }
                else {
                    return "[<" + minutes + " минут назад>] ";
                }
            }
            else {
                return "[<" + minutes + " минут назад>] ";
            }
        }

        else if (isItYesterday) {
            return "[<вчера>] ";
        }

        else if (deltaInSeconds < TWENTY_FOUR * SECONDS_IN_HOUR) {
            long hours = deltaInSeconds / SECONDS_IN_HOUR;
            if (hours >= FIFTEEN || hours <= TEN) {
                if (hours % TEN == ONE) {
                    return "[<" + hours + " час назад>] ";
                }
                else if (hours % TEN == TWO || hours % TEN == THREE || hours % TEN == FOUR) {
                    return "[<" + hours + " часа назад>] ";
                }
                else {
                    return "[<" + hours + " часов назад>] ";
                }
            }
            else {
                return "[<" + hours + " часов назад>] ";
            }
        }
        else {
            long days = deltaInSeconds / SECONDS_IN_DAY;
            if (days >= FIFTEEN || days <= TEN) {
                if (days % TEN == ONE) {
                    return "[<" + days + " день назад>] ";
                }
                else if (days % TEN == TWO || days % TEN == THREE || days % TEN == FOUR) {
                    return "[<" + days + " дня назад>] ";
                }
                else {
                    return "[<" + days + " дней назад>] ";
                }
            }
            else {
                return "[<" + days + " дней назад>] ";
            }
        }
    }

   public static boolean isItYesterdayTweet(Date tweetDate) {
       Date currentDate1 = Calendar.getInstance().getTime();
       LocalDate currentDate = LocalDate.now();
       LocalDate tweetCreationDate = tweetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
       long result = ChronoUnit.DAYS.between(tweetCreationDate, currentDate);
       if(result == 1) {
           isItYesterday = true;
           return true;
       }
       else {
           isItYesterday = false;
           return false;
       }


   }

}
