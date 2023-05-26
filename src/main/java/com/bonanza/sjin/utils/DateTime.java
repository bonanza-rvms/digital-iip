package com.bonanza.sjin.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTime {
	private static final String[][] FORMAT_TABLE = new String[][]{{"mmm", "MMM"}, {"eee", "EEE"}, {"g", "G"},
		{"hh24", "HH"}, {"ms", "SSS"}, {"mm", "MM"}, {"mi", "mm"}, {"ttt", "SSS"}, {"ww", "w"}, {"w", "W"}};
private static Map<Locale, DateTime> _instance = new ConcurrentHashMap<Locale, DateTime>();
private Map<String, BlockingQueue<DateFormat>> _format = new ConcurrentHashMap<String, BlockingQueue<DateFormat>>();
private Locale _locale = null;
private int _nYear = -1;
private int _nMonth = -1;
private int _nDay = -1;
private boolean _bUserTransDate = false;

public static final DateTime getInstance() {
	return getInstance(Locale.getDefault());
}

public static final DateTime getInstance(Locale locale) {
	DateTime result = (DateTime) _instance.get(locale);
	if (result == null) {
		synchronized (DateTime.class) {
			result = new DateTime(locale);
			_instance.put(locale, result);
		}
	}

	return result;
}

public Calendar getCalendar(String input) throws IllegalArgumentException {
	if (input == null) {
		throw new IllegalArgumentException(input);
	} else {
		String strInput = input.trim();
		Calendar calendar = Calendar.getInstance();
		switch (strInput.length()) {
			case 6 :
				String strTmp = this.getDate("yyyymmdd") + strInput;
				calendar.set(Integer.parseInt(strTmp.substring(0, 4)), Integer.parseInt(strTmp.substring(4, 6)) - 1,
						Integer.parseInt(strTmp.substring(6, 8)), Integer.parseInt(strTmp.substring(8, 10)),
						Integer.parseInt(strTmp.substring(10, 12)), Integer.parseInt(strTmp.substring(12)));
				break;
			case 8 :
				calendar.set(Integer.parseInt(strInput.substring(0, 4)),
						Integer.parseInt(strInput.substring(4, 6)) - 1, Integer.parseInt(strInput.substring(6)));
				break;
			case 14 :
				calendar.set(Integer.parseInt(strInput.substring(0, 4)),
						Integer.parseInt(strInput.substring(4, 6)) - 1, Integer.parseInt(strInput.substring(6, 8)),
						Integer.parseInt(strInput.substring(8, 10)), Integer.parseInt(strInput.substring(10, 12)),
						Integer.parseInt(strInput.substring(12)));
				break;
			default :
				throw new IllegalArgumentException(input);
		}

		return calendar;
	}
}

protected DateTime(Locale l) {
	this._locale = l;
	String strTransationDate = System.getProperty("transationDate");
	if (strTransationDate != null && strTransationDate.length() == 8) {
		this._nYear = Integer.parseInt(strTransationDate.substring(0, 4));
		this._nMonth = Integer.parseInt(strTransationDate.substring(4, 6)) - 1;
		this._nDay = Integer.parseInt(strTransationDate.substring(6));
		this._bUserTransDate = this._nYear >= 0 && this._nMonth >= 0 && this._nDay >= 0;
	}
}

private Locale _getLocale() {
	return this._locale;
}

private String _getDateFormatString(String pattern) {
	String strFormat = pattern.toLowerCase();
	String strChecker = strFormat;
	String[][] var7 = FORMAT_TABLE;
	int var6 = FORMAT_TABLE.length;

	for (int var5 = 0; var5 < var6; ++var5) {
		String[] format = var7[var5];
		if (strChecker.indexOf(format[0]) != -1) {
			strFormat = StrUtil.replace(strFormat, format[0], format[1], false);
			strChecker = StrUtil.replace(strChecker, format[0], " ", false);
		}
	}

	return strFormat;
}

private DateFormat _getDateFormat(String key, String pattern) {
	if (pattern == null) {
		return null;
	} else {
		BlockingQueue<DateFormat> queue = this.getFormatQueue(key);
		DateFormat result = null;
		if (queue != null) {
			result = (DateFormat) queue.poll();
		}

		if (result == null) {
			try {
				result = new SimpleDateFormat(this._getDateFormatString(pattern), this._getLocale());
			} catch (Throwable var6) {
				log.debug(var6 + ":" + pattern + ":" + this._getDateFormatString(pattern));
				throw new RuntimeException(var6);
			}
		}

		return (DateFormat) result;
	}
}

private void _addDateFormat(String key, DateFormat format) {
	BlockingQueue<DateFormat> queue = this.getFormatQueue(key);
	if (queue != null) {
		try {
			queue.add(format);
		} catch (Exception var5) {
			;
		}

	}
}

private final BlockingQueue<DateFormat> getFormatQueue(String key) {
	BlockingQueue<DateFormat> result = this._format.get(key);
	if (result == null) {
		synchronized (this) {
			if (this._format.size() == 99) {
				Exception exception = new Exception("To many create DateFormat");
				exception.printStackTrace();
				exception.printStackTrace(System.out);
			} else if (this._format.size() > 100) {
				return null;
			}

			result = new ArrayBlockingQueue<DateFormat>(1000);
			this._format.put(key, result);
		}
	}

	return (BlockingQueue<DateFormat>) result;
}

public Calendar getTransactionDay() {
	Calendar calendar = Calendar.getInstance();
	if (this._bUserTransDate) {
		calendar.set(this._nYear, this._nMonth, this._nDay);
	}

	return calendar;
}

public String getSysDate(String format) {
	return this.getDate(Calendar.getInstance().getTime(), format);
}

public String getSysDate(String format, char c, int i) {
	return this.getDate(Calendar.getInstance().getTime(), format, c, i);
}

public String getDate(String format) {
	return this.getDate(this.getTransactionDay().getTime(), format);
}

public String getDate(String format, char c, int i) {
	return this.getDate(this.getTransactionDay().getTime(), format, c, i);
}

public String getDate(String inputDate, String format) {
	try {
		return this.getDate(this.getCalendar(inputDate).getTime(), format);
	} catch (IllegalArgumentException var4) {
		return "";
	}
}

public String getDate(long date, String format) {
	return this.getDate(new Date(date), format);
}

public String getDate(String inputDate, String format, char c, int i) {
	try {
		return this.getDate(this.getCalendar(inputDate).getTime(), format, c, i);
	} catch (IllegalArgumentException var6) {
		return "";
	}
}

public String getDate(long date, String format, char c, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(date);
	switch (c) {
		case 'D' :
			calendar.add(5, i);
			break;
		case 'H' :
			calendar.add(10, i);
			break;
		case 'I' :
			calendar.add(12, i);
			break;
		case 'M' :
			calendar.add(2, i);
			break;
		case 'S' :
			calendar.add(13, i);
			break;
		case 'W' :
			calendar.add(4, i);
			break;
		case 'Y' :
			calendar.add(1, i);
	}

	return this.getDate(calendar.getTime(), format);
}

public long getDate(long date, char c, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(date);
	switch (c) {
		case 'D' :
			calendar.add(5, i);
			break;
		case 'H' :
			calendar.add(10, i);
			break;
		case 'I' :
			calendar.add(12, i);
			break;
		case 'M' :
			calendar.add(2, i);
			break;
		case 'S' :
			calendar.add(13, i);
			break;
		case 'W' :
			calendar.add(4, i);
			break;
		case 'Y' :
			calendar.add(1, i);
	}

	return calendar.getTime().getTime();
}

public String getDate(Date date, String format, char c, int i) {
	return this.getDate(date.getTime(), format, c, i);
}

public int getDayBetween(String fromDate, String toDate) {
	return this.getDayBetween(this.getCalendar(fromDate).getTime(), this.getCalendar(toDate).getTime());
}

public int getDayBetween(Date fromDate, Date toDate) {
	Calendar fromCal = Calendar.getInstance();
	Calendar toCal = Calendar.getInstance();
	fromCal.setTime(fromDate);
	toCal.setTime(toDate);
	return this.getDayBetween(fromCal, toCal);
}

public int getDayBetween(Calendar fromDate, Calendar toDate) {
	Calendar tmpCal = Calendar.getInstance();
	int nFromYear = fromDate.get(1);
	int nToYear = toDate.get(1);
	int nFromDate = fromDate.get(6);
	int nToDate = toDate.get(6);
	int nCheckDate = 0;
	int i;
	if (nFromYear < nToYear) {
		for (i = nFromYear; i < nToYear; ++i) {
			tmpCal.set(i, 11, 31);
			nCheckDate += tmpCal.get(6);
		}

		return nCheckDate + nToDate - nFromDate;
	} else {
		for (i = nToYear; i < nFromYear; ++i) {
			tmpCal.set(i, 11, 31);
			nCheckDate += tmpCal.get(6);
		}

		return (nCheckDate + nFromDate - nToDate) * -1;
	}
}

public String getMiliBetween(String before, String after) {
	Calendar calendar = Calendar.getInstance();
	calendar.set(Integer.parseInt(before.substring(0, 4)),
			Integer.parseInt(before.substring(4, 6)) - 1, Integer.parseInt(before.substring(6, 8)),
			Integer.parseInt(before.substring(8, 10)), Integer.parseInt(before.substring(10, 12)),
			Integer.parseInt(before.substring(12, 14)));
	calendar.setTimeInMillis(Long.parseLong(before.substring(14)));
	
	Calendar calendar2 = Calendar.getInstance();
	calendar2.set(Integer.parseInt(after.substring(0, 4)),
			Integer.parseInt(after.substring(4, 6)) - 1, Integer.parseInt(after.substring(6, 8)),
			Integer.parseInt(after.substring(8, 10)), Integer.parseInt(after.substring(10, 12)),
			Integer.parseInt(after.substring(12, 14)));
	calendar2.setTimeInMillis(Long.parseLong(after.substring(14)));
	
	int diffHour = calendar2.get(Calendar.HOUR_OF_DAY) - calendar.get(Calendar.HOUR_OF_DAY);
	int diffMinute = calendar2.get(Calendar.MINUTE) - calendar.get(Calendar.MINUTE);
	int diffSecond = calendar2.get(Calendar.SECOND) - calendar.get(Calendar.SECOND);
	int diffMillisecond = calendar2.get(Calendar.MILLISECOND) - calendar.get(Calendar.MILLISECOND);
	
	return diffHour+":"+diffMinute+":"+diffSecond+"."+diffMillisecond;
	
}

public String getTimeBetween(String hhmmss1, String hhmmss2) {
	int ss = this.getSecondBetween(hhmmss1, hhmmss2);
	int mm = ss / 60;
	ss %= 60;
	int hh = mm / 60;
	mm %= 60;
	return String.valueOf(hh) + mm + ss;
}

private int getSecondBetween(String hhmmss1, String hhmmss2) {
	return Math.abs(this.toSecond(hhmmss2) - this.toSecond(hhmmss1));
}

private int toSecond(String hhmmss) {
	try {
		int hh = Integer.parseInt(hhmmss.substring(0, 2));
		int mm = Integer.parseInt(hhmmss.substring(2, 4));
		int ss = Integer.parseInt(hhmmss.substring(4, 6));
		return (hh * 60 + mm) * 60 + ss;
	} catch (Exception var5) {
		var5.printStackTrace(System.out);
		return -1;
	}
}

public String getDate(Date date, String format) {
	String strKey = format.toLowerCase();
	DateFormat dateFromat = null;

	String var6;
	try {
		dateFromat = this._getDateFormat(strKey, format);
		var6 = dateFromat != null ? dateFromat.format(date) : "";
	} finally {
		if (dateFromat != null) {
			this._addDateFormat(strKey, dateFromat);
		}

	}

	return var6;
}

public boolean isLastDay(String format) {
	return this.isLastDay(this.getCalendar(format).getTimeInMillis());
}

public boolean isLastDay(Date date) {
	return this.isLastDay(date.getTime());
}

public boolean isLastDay(long date) {
	String strMM = this.getDate(date, "MM");
	String strMM2 = this.getDate(date, "MM", 'D', 1);
	return !strMM.equals(strMM2);
}
}
