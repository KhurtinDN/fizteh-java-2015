package main.java.ru.fizteh.fivt.pitovsky;

import java.util.Calendar;
import java.util.Date;

public class StringUtils {
	public static enum textColor {
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
		private textColor(int clr) {
			color = clr;
		}
	}
	
	private final static char ESCAPE = (char)27;
	
	public static String setClr(textColor tcolor) {
		return "" + ESCAPE + "[" + tcolor.color + "m";
	}
	public static String setStClr() {
		return setClr(textColor.STANDART);
	}
	
	private final static int MINUTE = 60*1000;
	private final static int HOUR = 60*MINUTE;
	private final static int DAY = 24*HOUR;
	public static String ConvertDate(Date date) {
		Calendar curCal = Calendar.getInstance();
		Calendar tweetCal = Calendar.getInstance();
		tweetCal.setTime(date);
		if (tweetCal.compareTo(curCal) > 0) {
			return "еще не опубликовано";
		}
		curCal.roll(Calendar.MINUTE, -2);
		if (tweetCal.compareTo(curCal) > 0) {
			return "только что";
		}
		curCal = Calendar.getInstance();
		curCal.roll(Calendar.HOUR, -1);
		if (tweetCal.compareTo(curCal) > 0) {
			int minutes = (int)((HOUR + curCal.getTimeInMillis() - tweetCal.getTimeInMillis()) / MINUTE);
			if (minutes / 10 != 1 && minutes % 10 == 1) { //like 1, 21, 31...
				return minutes + " минуту назад";
			}
			if (minutes / 10 != 1 && minutes % 10 > 1 && minutes % 10 < 5) {
				return minutes + " минуты назад";
			}
			return minutes + " минут назад";
		}
		curCal = Calendar.getInstance();
		if (curCal.get(Calendar.DAY_OF_YEAR) == tweetCal.get(Calendar.DAY_OF_YEAR) && 
				curCal.get(Calendar.YEAR) == tweetCal.get(Calendar.YEAR)) {
			int hours = curCal.get(Calendar.HOUR_OF_DAY) - tweetCal.get(Calendar.HOUR_OF_DAY);
			if (hours / 10 != 1 && hours % 10 == 1) {
				return hours + " час назад";
			}
			if (hours / 10 != 1 && hours % 10 > 1 && hours % 10 < 5) {
				return hours + " часа назад";
			}
			return hours + " часов назад";
		}
		curCal = Calendar.getInstance();
		curCal.roll(Calendar.DATE, -1);
		if (curCal.get(Calendar.DAY_OF_YEAR) == tweetCal.get(Calendar.DAY_OF_YEAR) &&
				curCal.get(Calendar.YEAR) == tweetCal.get(Calendar.YEAR)) {
			return "вчера";
		}
		curCal = Calendar.getInstance();
		int days = (int)((curCal.getTimeInMillis() - tweetCal.getTimeInMillis()) / DAY) + 1; //because day before yesterday it is 2 days ago
		if (days / 10 != 1 && days % 10 == 1) {
			return days + " день назад";
		}
		if (days / 10 != 1 && days % 10 > 1 && days % 10 < 5) {
			return days + " дня назад";
		}
		return days + " дней назад";
	}
}