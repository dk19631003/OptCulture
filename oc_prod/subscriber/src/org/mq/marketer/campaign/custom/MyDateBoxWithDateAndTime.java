package org.mq.marketer.campaign.custom;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;

public class MyDateBoxWithDateAndTime extends Datebox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeZone tz ;
	private int clientOffsetInt;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Date myDate;
	//private int clientHour; //TODO need to consider client elapsed time
	//private int clientMinute; //TODO need to consider client elapsed time
	
	public MyDateBoxWithDateAndTime() {
		super();
		tz = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	}
	
	
/*	@Override
	public Date getValue() throws WrongValueException {
		// TODO Auto-generated method stub
		Date date = super.getValue();
		clientOffsetInt = tz.getOffset(date.getTime());
		logger.info("getValue Date before tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
		date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
		logger.info("getValue Date after tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
		return date;
	}*/

	@Override
	public void setValue(Date date) throws WrongValueException {
		logger.info("setvalue date"+date);
		if(date==null) {
			logger.info("setvalue is null"+date);
			super.setValue(null);
			return;
		}
		super.setValue(date);		
	}

	public void setValue(Calendar cal) throws WrongValueException {
		logger.info("setValue cal.getTime() :"+cal.getTime());
		logger.info("cal.getTimeZone()  :"+cal.getTimeZone());
		super.setTimeZone(cal.getTimeZone());
		Date date = (Date)Utility.makeCopy(cal.getTime());
		Calendar clienCalender = MyCalendar.getNewCalendar();
		clienCalender.setTime(date);
		setValue(clienCalender.getTime());
	}
	
	public Calendar getServerValue() {
		try {
			Date date = (Date)Utility.makeCopy(super.getValue());
			//Date date = (Date)Utility.makeCopy(myDate);
			clientOffsetInt = tz.getOffset(date.getTime());
			//date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
			Calendar cal = MyCalendar.getNewCalendar();
			cal.setTime(date);
			return cal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}

	
	
	public Calendar getClientValue() {
		Date date = (Date)Utility.makeCopy(super.getValue());
		Calendar cal = MyCalendar.getNewCalendar();
		cal.setTimeZone(tz);
		cal.setTime(date);
		return cal;
	}

	
	public void setValueforprtDtBxId(Calendar cal) throws WrongValueException {
		Date date =cal.getTime();
		Calendar serverCal = Calendar.getInstance();
		logger.info("cal.getTime() :"+cal.getTime());
		if(date==null) {
			logger.info("if date is null :"+date);
			super.setValue(null);
			return;
		}
		clientOffsetInt = tz.getOffset(date.getTime());
		//if(client time-zone is earlier than server time-zone) 
		logger.info("clientcal.getTimeZone()  :"+cal.getTimeZone());
		logger.info("serverCal.getTimeZone()  :"+serverCal.getTimeZone());

		int diffInTimeZones= compare(serverCal.getTimeZone(),cal.getTimeZone());
		logger.info("compare(cal.getTimeZone(),serverCal.getTimeZone())  :"+diffInTimeZones);
		if(diffInTimeZones<0)
			date.setTime(date.getTime()+(date.getTimezoneOffset()*60*1000)+clientOffsetInt);
		//if(diffInTimeZones>0)date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
		logger.info("just before super.setValue(date) :"+date);
		super.setValue(date);
	}

	public int compare(TimeZone serverTZ, TimeZone clientTZ) 
	{    return clientTZ.getRawOffset() - serverTZ.getRawOffset();
	}

	
    //original	
	public void setValue2(Calendar cal) throws WrongValueException {
		super.setTimeZone(cal.getTimeZone());

		Date date = (Date)Utility.makeCopy(cal.getTime());

		Calendar clienCalender = MyCalendar.getNewCalendar();

		clienCalender.setTime(date);

		setValue(clienCalender.getTime());
	}

	
	//orginal just renamed
	public Calendar getServerValue2() {
		try {
			Date date = (Date)Utility.makeCopy(super.getValue());
			clientOffsetInt = tz.getOffset(date.getTime());
			date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
			Calendar cal = MyCalendar.getNewCalendar();
			cal.setTime(date);
			return cal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
}
