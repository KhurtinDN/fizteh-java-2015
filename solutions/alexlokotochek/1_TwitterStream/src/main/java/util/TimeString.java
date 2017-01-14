package util;

import twitter4j.Status;
import java.util.Calendar;

public class TimeString {

    public static String timeOfTweet(Status status){
        Calendar cal = Calendar.getInstance();
        cal.setTime(status.getCreatedAt());
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(calNow.getTime());

        int minutes = cal.get(Calendar.MINUTE);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int days = cal.get(Calendar.DAY_OF_YEAR);

        int minutesNow = calNow.get(Calendar.MINUTE);
        int hoursNow = calNow.get(Calendar.HOUR_OF_DAY);
        int daysNow = calNow.get(Calendar.DAY_OF_YEAR);

        String tweetTime;

        if (days != daysNow){
            if (daysNow - days == 1)
                return "Вчера";
            else
                tweetTime = formDays(daysNow-days);
        }else{
            if (hours < hoursNow - 1) {
                tweetTime = formHours(hoursNow-hours);
            }else{
                if (hours == hoursNow-1) {
                    if (minutes <= minutesNow)
                        tweetTime = formHours(1);
                    else
                        if (minutes - minutesNow <= 2)
                            return "Только что";
                        else
                            tweetTime = formMinutes(minutes - minutesNow);
                }else{
                    if (minutesNow - minutes <= 2)
                        return "Только что";
                    else
                        tweetTime = formMinutes(minutesNow - minutes);
                }
            }
        }
        return tweetTime + " назад";
    }

    public static String formDays (int days) {
        if (days % 100 >= 11 && days % 100 <= 19)
            return days + " дней";
        if (days % 10 == 1)
            return days + " день";
        if (days % 10 >= 2 && days % 10 <= 4)
            return days + " дня";
        return days + " дней";
    }

    public static String formHours (int hours) {
        if (hours % 100 >= 11 && hours % 100 <= 19)
            return hours + " часов";
        if (hours % 10 == 1)
            return hours + " час";
        if (hours % 10 >= 2 && hours % 10 <= 4)
            return hours + " часа";
        return hours + " часов";
    }

    public static String formMinutes (int minutes) {
        if (minutes % 100 >= 11 && minutes % 100 <= 19)
            return minutes + " минут";
        if (minutes % 10 == 1)
            return minutes + " минута";
        if (minutes % 10 >= 2 && minutes % 10 <= 4)
            return minutes + " минуты";
        return minutes + " минут";
    }


}
