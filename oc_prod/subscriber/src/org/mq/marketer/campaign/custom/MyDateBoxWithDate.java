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
import org.zkoss.zul.Datebox;

public class MyDateBoxWithDate extends Datebox {
	
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
	
	public MyDateBoxWithDate() {
		super();
		tz = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	}
	
	@Override
	public void setValue(Date date) throws WrongValueException {
		logger.info("setvalue date"+date);
		if(date==null) {
			logger.info("setvalue is null"+date);
			super.setValue(null);
			return;
		}
		this.myDate=date;
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

	public Date getValue(){
		return this.myDate;
	}

	public Calendar getServerValue() {
		try {
			Date date = (Date)Utility.makeCopy(myDate);
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
		Date date = (Date)Utility.makeCopy(myDate);
		Calendar cal = MyCalendar.getNewCalendar();
		cal.setTimeZone(tz);
		cal.setTime(date);
		return cal;
	}
  
	public Date onChangeDateValue(){
		return super.getValue();
	}

}
