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

public class MyDatebox extends Datebox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeZone tz ;
	private int clientOffsetInt;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private int clientHour; //TODO need to consider client elapsed time
	//private int clientMinute; //TODO need to consider client elapsed time
	
	public MyDatebox() {
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
		if(date==null) {
			super.setValue(null);
			return;
		}
		clientOffsetInt = tz.getOffset(date.getTime());
//		logger.info("Date before tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
		date.setTime(date.getTime()+(date.getTimezoneOffset()*60*1000)+clientOffsetInt);
//		logger.info("Date after tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
		//clientHour = date.getHours();
		//clientMinute = date.getMinutes();
		super.setValue(date);
	}

	public void setValue(Calendar cal) throws WrongValueException {
//		MyCalendar mycal = new MyCalendar(cal,tz);
//		Date date = mycal.getTime();
//		logger.info("date :" + cal.getTime());
		
		setValue(cal.getTime());
	}
	
	public Calendar getServerValue() {
		try {
			Date date = (Date)Utility.makeCopy(super.getValue());
			if(date==null) return null;
//			date.setHours(clientHour);
			//date.setMinutes(clientMinute);
			clientOffsetInt = tz.getOffset(date.getTime());
			//	logger.info("getValue Date before tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
			date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
//			logger.info("getValue Date after tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
			Calendar cal = MyCalendar.getNewCalendar();
			cal.setTime(date);
//			logger.info("cal=" + cal);
			return cal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	
	public Calendar getClientValue() {
		Date date = (Date)Utility.makeCopy(super.getValue());
	//	date.setHours(clientHour);
	//	date.setMinutes(clientMinute);
		
/*		clientOffsetInt = tz.getOffset(date.getTime());
		logger.info("getValue Date before tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
		date.setTime(date.getTime()-(date.getTimezoneOffset()*60*1000)-clientOffsetInt);
		logger.info("getValue Date after tz:" + date + "  off=" + clientOffsetInt + "  dateOff=" + date.getTimezoneOffset());
*/		
		Calendar cal = MyCalendar.getNewCalendar();
		cal.setTime(date);
//		logger.info("cal=" + cal);
		return cal;
	}
	
	
	
}
