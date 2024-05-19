package org.mq.loyality.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyCalendar extends GregorianCalendar implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FORMAT_DATEONLY = "dd-MM-yyyy";
	public static final String FORMAT_SB_DATEONLY = "yyyyMMdd";
	public static final String FORMAT_SB_DATETIME = "yyyyMMdd HHmmss";
	public static final String FORMAT_DATETIME_STYEAR = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATETIME_STDATE = "dd MMM, yyyy HH:mm aaa";
	//public static final String FORMAT_DATETIME_STDATE_NEW = "MM-dd-yyyy HH:mm:ss";
	
	public static final String FORMAT_SCHEDULE = "MMM dd, yyyy HH:mm";
	public static final String FORMAT_SCHEDULE_TIME = "MMM dd, yyyy HH:mm:ss";
	public static final String FORMAT_YEARTOSEC = "yyyyMMddHHmmss";
	public static final String FORMAT_STDATE = "dd MMM, yyyy";
//	public static final String FORMAT_CONTACTDATE = "MM/dd/yyy HH:mm a";
	public static final String FORMAT_DATEONLY_GENERAL = "MMM dd, yyyy";
	public static final String FORMAT_ONLY_DATE = "MMM dd";
	public static final String FORMAT_YEARTODATE = "yyyy-MM-dd";
	public static final String FORMAT_DATEONLY_COMPLETE_MONTH = "MMMMM dd, yyyy";
	public static final String FORMAT_DATETIME_MIN_STYEAR = "yyyy-MM-dd HH:mm";
	
	public static final String FORMAT_MDATEONLY ="MM-dd-yyyy";
	public static final String FORMAT_MONTHDATE_ONLY ="MM/dd/yyyy";
	public static final String FORMAT_DATE_MONTH_TIME="dd MMM_HH:mm:ss";
	public static final String FORMAT_DATE_FORMAT="MM-dd-yy HH:mm:ss";
	
	
	public static Map<String,String> dateFormatMap = new HashMap<String, String>();
	static{
		dateFormatMap.put("dd/MM/yyyy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("dd-MM-yyyy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM/dd/yyyy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM-dd-yyyy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM/dd/yy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM-dd-yy", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("dd/MM/yyyy HH:mm", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM/dd/yyyy HH:mm", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("MM/dd/yyyy HH:mm:ss", FORMAT_DATETIME_STYEAR);
		dateFormatMap.put("dd-MMM-yy", FORMAT_DATETIME_STYEAR);
	}


	private String printFormat=null;
	
	public MyCalendar() {
	}

	public MyCalendar(Calendar cal) {
		this(cal, cal.getTimeZone(),null);
	}
	
	public MyCalendar(TimeZone tz) {
		this(null, tz,null);
	}

	public MyCalendar(Calendar cal, TimeZone tz) {
		this(cal, tz, null);
	}

	public MyCalendar(Calendar cal, TimeZone tz, String printFormat) {
		if(cal == null) {
			cal = new MyCalendar();
		}
		this.setTimeInMillis(cal.getTimeInMillis());
		if(tz != null) this.setTimeZone(tz);
		this.printFormat = printFormat;
	}

	public static Calendar getNewCalendar(){
		return new MyCalendar();
	}
	
	public void setPrintFormat(String printFormat) {
		this.printFormat = printFormat;
	}
	
	@Override
	public String toString() {
		return calendarToString(this, printFormat);
	}

	/**
	 * Converts calendar into specified format.
	 * @param cal
	 * @param dateformat
	 * @return
	 */
	public static final String calendarToString(Calendar cal, String dateformat) {
		return calendarToString(cal, dateformat, null);
	}
	
	/**
	 * Converts calendar into specified format and TimeZone
	 * @param cal
	 * @param dateformat
	 * @param timeZone
	 * @return String format of the given Calendar
	 */
	public static final String calendarToString(Calendar tempCal, String dateformat, TimeZone timeZone) {
		 
		if(tempCal==null) return "--";
		Calendar cal=tempCal;
		cal=(Calendar) makeCopy(tempCal);
		
		if(timeZone!=null) {
			cal.setTimeZone(timeZone);
		}
		
		Formatter fmt=new Formatter();

	    try {
			if(dateformat==null || dateformat.equals(FORMAT_DATETIME_STYEAR)) {
				fmt.format("%tY-%tm-%td %tH:%tM:%tS", cal, cal, cal, cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_DATETIME_STDATE)) {
				fmt.format("%td %tb, %tY", cal,cal,cal);
			}
			else if(dateformat.equals(FORMAT_SCHEDULE)) {
				fmt.format("%tb %td, %tY %tH:%tM", cal, cal, cal, cal, cal);
			}else if(dateformat.equals(FORMAT_SCHEDULE_TIME)) {
				fmt.format("%tb %td, %tY %tH:%tM:%tS", cal, cal, cal, cal, cal,cal);
			}
			else if(dateformat.equals(FORMAT_YEARTOSEC)) {
				fmt.format("%tY%tm%td%tH%tM%tS", cal, cal, cal, cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_DATEONLY)) {
				fmt.format("%td-%tm-%tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_STDATE)) {
				fmt.format("%td %tb, %tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_DATEONLY_GENERAL)) {//MMM dd, yyyy
				fmt.format("%tb %td,%tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_ONLY_DATE)) {//MMM dd
				fmt.format("%tb %td", cal, cal);
			}
			else if(dateformat.equals(FORMAT_DATEONLY_COMPLETE_MONTH)) {//MMM dd, yyyy
				fmt.format("%tB %td,%tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_YEARTODATE)) {//yyyy-MM-dd
				fmt.format("%tY-%tm-%td", cal, cal, cal);
			} else if (dateformat.equals(FORMAT_SB_DATEONLY)) {   //dd-MM-yyyy";
				fmt.format("%tY%tm%td", cal, cal, cal);
			} 
			else if(dateformat.equals(FORMAT_SB_DATETIME)) {
				fmt.format("%tY%tm%td %tH%tM%tS", cal, cal, cal, cal, cal, cal);
			}else if(dateformat.equals(FORMAT_DATETIME_MIN_STYEAR)) {//yyyy-MM-dd HH:mm
				fmt.format("%tY-%tm-%td %tH:%tM", cal, cal, cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_MDATEONLY)) {
				fmt.format("%tm-%td-%tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_MONTHDATE_ONLY)) {
				fmt.format("%tm/%td/%tY", cal, cal, cal);
			}
			else if(dateformat.equals(FORMAT_DATE_MONTH_TIME)) {
				fmt.format("%td-%tb_%tH:%tM:%tS", cal, cal, cal,cal, cal);
			}else if(dateformat.equals(FORMAT_DATE_FORMAT)) {
					fmt.format("%tm-%td-%tY %tH:%tM:%tS", cal, cal, cal, cal, cal,cal);
				}
			
		} catch (Exception e) {
			
			return "--";
		}
		    
	    return fmt.toString();
	    
	} // calendarToString
	
	/*
	 *  Returns Calendar Object for format .
	 */
	public static Calendar dateString2Calendar(String s){
		Calendar cal=Calendar.getInstance();
	    try {
			
			String dateRegEx = "\\d{4}\\s*-\\s*\\d{2}\\s*-\\s*\\d{2}\\s*\\S*";
			if(s.matches(dateRegEx) ) {
				
				SimpleDateFormat df = new SimpleDateFormat(FORMAT_DATETIME_STYEAR);
				Date d1= df.parse(s);
				cal.setTime(d1);
				return cal;
				
			}
			
			return null;
		} catch (ParseException e) {
			
			try {
				SimpleDateFormat df = new SimpleDateFormat(FORMAT_YEARTODATE);
				Date d1= df.parse(s);
				cal.setTime(d1);
				return cal;
			} catch (ParseException e1) {
				
				return null;
			}
			
		}
	}
// class

public static Object makeCopy(Object obj) {
	
	if(obj==null) return null;
	Object retObj=null;
	try {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		retObj = ois.readObject();
		ois.close();
	}catch(Exception e) {
		
	}
	return retObj;
}
}