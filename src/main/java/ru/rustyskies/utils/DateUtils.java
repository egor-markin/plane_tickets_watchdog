package ru.rustyskies.utils;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Egor Markin
 * @since 17.08.2009
 */
public class DateUtils {

    public static final String TIME_LONG_FORMAT = "HH:mm:ss";
    public static final String TIME_SHORT_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    
    public static final SimpleDateFormat defaultTimeLongFormat = new SimpleDateFormat(TIME_LONG_FORMAT);
    public static final SimpleDateFormat defaultTimeShortFormat = new SimpleDateFormat(TIME_SHORT_FORMAT);
    public static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat(DATE_FORMAT);
    
    public enum Season {
    	WINTER(0, "Winter"),
    	SPRING(1, "Spring"),
    	SUMMER(2, "Summer"),
    	AUTUMN(3, "Autumn");
    	
    	private final int index;
    	private final String title;
    	
    	Season(int index, String title) {
    		this.index = index;
    		this.title = title;
    	}

		public int getIndex() {
			return index;
		}
		public String getTitle() {
			return title;
		}
		
		public static Season getSeasonByIndex(int index) {
			Season[] seasons = Season.values();
			for (Season season : seasons) {
				if (season.index == index) {
					return season;
				}
			}
			return null;
		}
    }
    
    /** Returns Date object. Months number starts with 1 */
    public static Date getDate(int year, int month, int dayOfMonth) {
        return getDate(year, month, dayOfMonth, 0, 0, 0);
    }

    public static Time getTime(int hourOfDay, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, hourOfDay, minute, second);
        return new Time(calendar.getTimeInMillis());
    }

    /** Returns Date object. Months number starts with 1 */
    public static Date getDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute, second);
        return new Date(calendar.getTimeInMillis());
    }
    
    public static Date getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Date(calendar.getTimeInMillis());
    }
    
    public static Time getTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new Time(calendar.getTimeInMillis());
    }
    
    public static Date getDate(Date date, Time time) {
    	return new Date(date.getTime() + time.getTime() + Calendar.getInstance().getTimeZone().getRawOffset());
    }
    
    public static int getDateField(Date date, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
    	return calendar.get(field);
    }
    
    public static Time getCurrentTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        return new Time(calendar.getTimeInMillis());
    }
    
    public static String getCurrentDayAsString() {
    	return getDateAsString(getCurrentDay());
    }
    
    public static Date getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Date(calendar.getTimeInMillis());
    }
    
    public static Date getCurrentDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        return new Date(calendar.getTimeInMillis());
    }
    
    public static String getCurrentDateAsString() {
    	return getDateTimeAsString(getCurrentDate());
    }
    
    public static Date getMonth(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
        return new Date(calendar.getTimeInMillis());
    }
    public static Date getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getMonth(calendar);
    }
    
    public static Date getCurrentMonth() {
        return getMonth(new GregorianCalendar());
    }
    
    public static String getCurrentDateTimeAsString(boolean reversed) {
    	Date currentDate = getCurrentDate();
    	if (reversed) {
            return (new SimpleDateFormat("yyyyMMddHHmmss")).format(currentDate);
    	} else {
            return (new SimpleDateFormat("ddMMyyyyHHmmss")).format(currentDate);
    	}
    }

    public static String getTimeAsString(Time aTime, String format) {
    	return getTimeAsString(aTime, new SimpleDateFormat(format));
    }
    
    public static String getTimeAsString(Time aTime, SimpleDateFormat format) {
        if (aTime == null) {
            return "";
        }
        return format.format(aTime.getTime());
    }
    
    public static String getTimeAsLongString(Time aTime) {
    	return getTimeAsString(aTime, defaultTimeLongFormat);
    }

    public static String getTimeAsShortString(Time aTime) {
    	return getTimeAsString(aTime, defaultTimeShortFormat);
    }

    public static String getDateAsString(Date aDate) {
    	if (aDate != null) {
    		return defaultDateFormat.format(aDate.getTime());
    	} else {
    		return null;
    	}
    }
    
    public static String getDateTimeAsString(Date aDate) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT + " " + TIME_LONG_FORMAT);
        return format.format(aDate);
    }

    public static Time getTime(String text, SimpleDateFormat format) throws ParseException {
    	return new Time(format.parse(text).getTime());
    }
    
    public static Date getDate(String text) throws ParseException {
    	return new Date(defaultDateFormat.parse(text).getTime());
    }
    
    public static Date getRandomDate() {
    	return getRandomDate(1700, 2100);
    }
    
    public static Date getRandomDate(int minYear, int maxYear) {
    	Random random = new Random();
    	
    	int year = minYear + Math.abs(random.nextInt()) % (maxYear - minYear);
    	int month = Math.abs(random.nextInt()) % 11;
    	int dayOfMonth = Math.abs(random.nextInt()) % 30;
    	
    	return getDate(year, month, dayOfMonth);
    }
    
    public static Time getRandomTime() {
    	return getRandomTime(0, 23);
    }
    
    public static Time getRandomTime(int minHour, int maxHour) {
    	Random random = new Random();
    	
    	int hourOfDay = minHour + Math.abs(random.nextInt()) % (maxHour - minHour);
    	int minute = Math.abs(random.nextInt()) % 59;
    	int second = Math.abs(random.nextInt()) % 59;
    	
    	return getTime(hourOfDay, minute, second);
    }
    
    public static double getSecondsInHours(double seconds, double precision) {
    	double factor = Math.pow(10, precision);
    	return Math.round((seconds / 3600.0) * factor) / factor;
    }
    
    public static String getMinutesAsFormatedTime(long minutes) {
    	return getSecondsAsFormatedTime(minutes * 60);
    }
    
    public static String getSecondsAsFormatedTime(long seconds) {
    	long days = seconds / (60 * 60 * 24);
    	seconds -= days * (60 * 60 * 24);
    	long hours = seconds / (60 * 60);
    	seconds -= hours * (60 * 60);
    	long minutes = seconds / 60;
    	seconds -= minutes * 60;
    	
    	StringBuilder builder = new StringBuilder();
    	if (days > 0) {
    		builder.append(days).append(".");
    	}
    	builder.append(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    	return builder.toString();
    }
    
    
    /** Difference in seconds between specified moments of time */
    public static int getTimeDifference(Time start, Time end) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(start);
    	int t1 = c1.get(Calendar.HOUR_OF_DAY) * 3600 + c1.get(Calendar.MINUTE) * 60 + c1.get(Calendar.SECOND);
    	
    	Calendar c2 = Calendar.getInstance();
    	c2.setTime(end);
    	int t2 = c2.get(Calendar.HOUR_OF_DAY) * 3600 + c2.get(Calendar.MINUTE) * 60 + c2.get(Calendar.SECOND);
    	
    	if (t1 > t2) {
    		return 86400 - t1 + t2;
    	} else {
    		return t2 - t1;
    	}
    }
    
    /** Difference in seconds between specified dates */
    public static long getDateDifference(Date date1, Date date2) {
    	return (date1.getTime() - date2.getTime()) / 1000;
    }
    
    /** Difference in seconds between current time and specified date */
    public static long getDateDifference(Date date) {
    	return (getCurrentDate().getTime() - date.getTime()) / 1000;
    }
    
    public static int getAmountDaysInMonth(int year, int month) {
		Calendar calendar = new GregorianCalendar(year, month - 1, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
	/** Returns list of date objects for specified year/month */
    public static List<Date> getDaysList(int year, int month) {
		int numberOfDays = getAmountDaysInMonth(year, month);

		ArrayList<Date> list = new ArrayList<Date>();
		for (int i = 0; i < numberOfDays; i++) {
			list.add(getDate(year, month, i + 1));
		}
		return list;		
	}
    
    public static int getDayOfWeek(Date date) {
    	Calendar calendar = new GregorianCalendar();
    	calendar.setTimeInMillis(date.getTime());
    	return calendar.get(Calendar.DAY_OF_WEEK);
    }
    
    public static String getDayOfWeekString(Date date) {
    	int dayOfWeek = getDayOfWeek(date);
		return getDayOfWeekString(dayOfWeek);
    }
    
    public static String getDayOfWeekString(int dayOfWeek) {
		String[] weekdays = new DateFormatSymbols(Locale.US).getWeekdays();
		return weekdays[dayOfWeek];
    }
    
    /** Returns english month name, January has index 1*/
    public static String getMonthName(int month) {
		String[] months = new DateFormatSymbols(Locale.US).getMonths();
		return months[month];
    }
    
    
    /** Returns string with "year monthName" format, month is 1-based */
    public static String getYearMonthName(int year, int month) {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(year).append(" ").append(getMonthName(month - 1));
    	return buffer.toString();
    }
    
    /** Returns string with "YYYYMM" format, month is 1-based */
    public static String getYearMonthCode(int year, int month) {
    	return String.format("%04d%02d", year, month);
    }
    
    /** Returns string with "YYYYMM" format */
    public static String getYearMonthCode(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
    	return getYearMonthCode(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }
    
    /** Returns date from string with "YYYYMM" format */
    public static Date getDateFromYearMonthCode(String code) throws ParseException {
    	return getMonth(new Date((new SimpleDateFormat("yyyyMM")).parse(code).getTime()));
    }
    
    public static Season getSeason(Date date) {
    	Season season = null;
    	
    	int month = getDateField(date, Calendar.MONTH);
    	switch (month) {
    		case Calendar.DECEMBER:
	    	case Calendar.JANUARY:
	    	case Calendar.FEBRUARY:
	    		season = Season.WINTER;
	    		break;
	    	case Calendar.MARCH:
	    	case Calendar.APRIL:
	    	case Calendar.MAY:
	    		season = Season.SPRING;
	    		break;
	    	case Calendar.JUNE:
	    	case Calendar.JULY:
	    	case Calendar.AUGUST:
	    		season = Season.SUMMER;
	    		break;
	    	case Calendar.SEPTEMBER:
	    	case Calendar.OCTOBER:
	    	case Calendar.NOVEMBER:
	    		season = Season.AUTUMN;
	    		break;
    	}
    	return season;
    }
    
    public static Date getPreviousDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return new Date(calendar.getTimeInMillis());
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date modifyDateField(Date date, int field, int value) {
  		Calendar calendar = Calendar.getInstance();
  		calendar.setTime(date);
      	calendar.add(field, value);
      	return new Date(calendar.getTimeInMillis());
      }    
    
    public static void main(String[] args) {
    	System.out.println(getPreviousDay(getCurrentDate()));
    }
    
}
