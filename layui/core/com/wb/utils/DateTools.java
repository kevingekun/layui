package com.wb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.wb.exceptions.BusinessException;

public class DateTools extends DateUtils {
		  public static final String DF_YYYYMMDD = "yyyyMMdd";

		  public static String getSystemYearMonthDay()
		  {
		    Date currentDate = new Date();

		    return getYYYYMMDD(currentDate);
		  }

		  public static String getYYYYMMDD(Date date)
		  {
		    return formatDate(date, "yyyyMMdd");
		  }

		  public static String formatDate(Date date, String format)
		  {
		    SimpleDateFormat aateFormat = new SimpleDateFormat(format);

		    return aateFormat.format(date);
		  }

		  public static boolean isDate(String str)
		  {
		    return isDate(str, "yyyyMMdd");
		  }

		  public static boolean isDate(String str, String format)
		  {
		    if (StringUtils.isBlank(str)) {
		      return false;
		    }

		    int pos = 0;

		    Calendar cal = Calendar.getInstance();

		    pos = format.indexOf("yyyy");
		    String yyyy = str.substring(pos, pos + 4);
		    cal.set(1, Integer.parseInt(yyyy));

		    pos = format.indexOf("MM");
		    String mm = str.substring(pos, pos + 2);
		    cal.set(2, Integer.parseInt(mm) - 1);

		    pos = format.indexOf("dd");
		    String dd = "";

		    if (pos != -1) {
		      dd = str.substring(pos, pos + 2);
		      cal.set(5, Integer.parseInt(dd));
		    }

		    String tmp = null;
		    pos = format.indexOf("HH");
		    if (pos != -1) {
		      tmp = str.substring(pos, pos + 2);
		      cal.set(11, Integer.parseInt(tmp));
		    }

		    pos = format.indexOf("mm");
		    if (pos != -1) {
		      tmp = str.substring(pos, pos + 2);
		      cal.set(12, Integer.parseInt(tmp));
		    }

		    pos = format.indexOf("ss");
		    if (pos != -1) {
		      tmp = str.substring(pos, pos + 2);
		      cal.set(13, Integer.parseInt(tmp));
		    }

		    SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		    Date date = cal.getTime();
		    tmp = dateFormat.format(date);

		    return (tmp.equals(str));
		  }

		  public static String getPreviousDayFromDate(Date currDate)
		  {
		    if (currDate == null)
		      return null;

		    Calendar cal = Calendar.getInstance();
		    cal.setTime(currDate);
		    cal.add(5, -1);
		    return formatDate(cal.getTime(), "yyyyMMdd");
		  }

		  @SuppressWarnings("deprecation")
		public static int jianYueJinNian(Date curDate, Date oldDate)
		  {
		    if ((curDate == null) || (oldDate == null))
		      return -1;

		    int year = curDate.getYear() - oldDate.getYear();

			int month = curDate.getMonth() - oldDate.getMonth();

		    if (month > 0)
		      ++year;

		    return year;
		  }


		  public static boolean before(String dateStr1, String dateStr2, String format)
		  {
		    SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		    Date date1 = null;
		    Date date2 = null;
		    try
		    {
		      date1 = dateFormat.parse(dateStr1);
		      date2 = dateFormat.parse(dateStr2);
		    }
		    catch (ParseException e) {
		      e.printStackTrace();
		      throw new BusinessException(String.format("杞崲鏃堕棿鏍煎紡閿欒锛宒ateStr1=%s,dateStr2=%s,format=%s", new Object[] { dateStr1, dateStr2, 
		        format }));
		    }

		    return before(date1, date2);
		  }

		  public static boolean before(Date date1, Date date2)
		  {
		    return date1.before(date2);
		  }

		  public static Date processDate(Date oldDate, int type, int count)
		  {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(oldDate);
		    cal.add(type, count);
		    return cal.getTime();
		  }

		  public static String parseMonth(int months)
		  {
		    StringBuffer result = new StringBuffer();

		    int year = months / 12;
		    int month = months % 12;

		    if (year < 10)
		      result.append("0");
		    result.append(year);

		    if (month < 10)
		      result.append("0");
		    result.append(month);

		    return result.toString();
		  }

		  public static int getPreviousMonth(String currentMonth)
		  {
		    Calendar cal = Calendar.getInstance();
		    cal.set(2, Integer.valueOf(Integer.parseInt(currentMonth) - 1).intValue());
		    cal.setTime(processDate(cal.getTime(), 2, -1));
		    return (cal.get(2) + 1);
		  }

		  public static String formatyyyyMMdd(String yyyyMMdd)
		  {
		    if (!(StringTools.hasText(yyyyMMdd)))
		      return "";

		    return formatDate(parseDate(yyyyMMdd, "yyyyMMdd"), "yyyy-MM-dd HH:mm:ss");
		  }

		  public static String getMonthsBetween2Date(Date fromDate, Date toDate)
		  {
		    if ((fromDate == null) || (toDate == null))
		      return null;
		    int times = 1;
		    if (!(fromDate.after(toDate))) {
		      Calendar calfrom = Calendar.getInstance();
		      Calendar calto = Calendar.getInstance();
		      calfrom.setTime(fromDate);
		      calfrom.set(5, 1);
		      calto.setTime(toDate);
		      calto.set(5, 1);
		      while (calfrom.before(calto)) {
		        calfrom.add(2, 1);
		        ++times;
		      }
		    }
		    else {
		      return "0";
		    }
		    return new Integer(times).toString();
		  }

		  public static String getFullMonthsBetween2Date(Date fromDate, Date toDate)
		  {
		    String result = null;

		    int months = Integer.parseInt(getMonthsBetween2Date(fromDate, toDate));

		    result = String.valueOf(months - 1);

		    return result;
		  }

		  public static Integer getDaysBetweenTwoDates(Date from, Date end) {
		    if ((from == null) || (end == null))
		      return null;

		    long fromL = from.getTime();
		    long endL = end.getTime();
		    double diff = (endL - fromL) / 86400000L;
		    return new Integer(new Double(Math.ceil(diff)).intValue());
		  }

		  public static String getSystemYear()
		  {
		    Date currentDate = new Date();
		    SimpleDateFormat format = new SimpleDateFormat("yyyy");
		    return format.format(currentDate);
		  }

		  public static String getSystemYearAndMonth()
		  {
		    Date currentDate = new Date();
		    SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		    return format.format(currentDate);
		  }

		  public static Date parseDate(String dateStr, String pattern)
		  {
		    try
		    {
		      return parseDate(dateStr, new String[] { pattern });
		    } catch (ParseException e) {
		    }
		    return null;
		  }

		  public static Date getDateOfWeek(Date date, int dayOfWeek)
		  {
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(truncate(date, 5));
		    calendar.set(7, dayOfWeek);
		    return calendar.getTime();
		  }

		  public static Date getDateOfMonth(Date date, int dayOfMonth)
		  {
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(truncate(date, 5));
		    calendar.set(5, dayOfMonth);
		    return calendar.getTime();
		  }

		  public static String format(Date date, String pattern)
		  {
		    return new SimpleDateFormat(pattern).format(date);
		  }

		  public static Date wrapDate(Date date, String fieldToken, String wrapValue)
		  {
		    return DateWrapper.wrapDate(date, fieldToken, wrapValue);
		  }

		  public static Date wrapDate(Date date, String wrapString)
		  {
		    if (StringUtils.isEmpty(wrapString)) {
		      return date;
		    }

		    StringTokenizer tokenizer = new StringTokenizer(wrapString, DateWrapper.getTokens(), true);
		    while (tokenizer.hasMoreTokens()) {
		      String fieldToken = tokenizer.nextToken();
		      String wrapValue = tokenizer.nextToken();
		      date = wrapDate(date, fieldToken, wrapValue);
		    }
		    return date;
		  }

		  public static void main(String[] args)
		  {
		    System.out.println(getMonthsBetween2Date(parseDate("20050101", "yyyyMMdd"), parseDate("20060101", "yyyyMMdd")));
		  }

		  private static class DateWrapper
		  {
		    private static Map<String, DateTools.TokenField> fieldTokens = new HashMap();
		    private static String tokens = "";

		    static
		    {
		      addToken(DateTools.TokenField.TOKEN_YEAR);
		      addToken(DateTools.TokenField.TOKEN_MONTH);
		      addToken(DateTools.TokenField.TOKEN_DATE);
		      addToken(DateTools.TokenField.TOKEN_HOUR);
		      addToken(DateTools.TokenField.TOKEN_MINUTE);
		      addToken(DateTools.TokenField.TOKEN_SECOND);
		      addToken(DateTools.TokenField.TOKEN_MILLISECOND);
		    }

		    private static void addToken(DateTools.TokenField tokenField)
		    {
		      fieldTokens.put(tokenField.getToken(), tokenField);
		      if (tokens.indexOf(tokenField.getToken()) < 0)
		        tokens += tokenField.getToken();
		    }

		    public static String getTokens()
		    {
		      return tokens;
		    }

		    static Date wrapDate(Date date, String fieldToken, String wrapValue) {
		      DateTools.TokenField tokenField = (DateTools.TokenField)fieldTokens.get(fieldToken);
		      if (tokenField == null)
		        throw new IllegalArgumentException("Token [" + fieldToken + "] unsupported!");

		      boolean add = (wrapValue.startsWith("+")) || (wrapValue.startsWith("-"));
		      if (wrapValue.startsWith("+"))
		        wrapValue = wrapValue.substring(1);

		      if (!(NumberUtils.isNumber(wrapValue))) {
		        throw new IllegalArgumentException("[" + wrapValue + "] is not a number!");
		      }

		      if (date == null)
		        date = new Date();

		      Calendar calendar = Calendar.getInstance();
		      calendar.setTime(date);
		      if (add) {
		        calendar.add(DateTools.TokenField.access$0(tokenField), Integer.parseInt(wrapValue));
		      }
		      else
		        calendar.set(DateTools.TokenField.access$0(tokenField), Integer.parseInt(wrapValue));

		      return calendar.getTime();
		    }
		  }

		  private static class TokenField
		  {
		    private final String token;
		    private final int field;
		    static TokenField TOKEN_YEAR = new TokenField("Y", 1);
		    static TokenField TOKEN_MONTH = new TokenField("M", 2);
		    static TokenField TOKEN_DATE = new TokenField("D", 5);
		    static TokenField TOKEN_HOUR = new TokenField("h", 10);
		    static TokenField TOKEN_MINUTE = new TokenField("m", 12);
		    static TokenField TOKEN_SECOND = new TokenField("s", 13);
		    static TokenField TOKEN_MILLISECOND = new TokenField("S", 14);

		    private TokenField(String token, int field)
		    {
		      this.token = token;
		      this.field = field;
		    }

		    public static int access$0(TokenField tokenField) {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getField() {
		      return this.field;
		    }

		    public String getToken() {
		      return this.token;
		    }
		  }
		}
