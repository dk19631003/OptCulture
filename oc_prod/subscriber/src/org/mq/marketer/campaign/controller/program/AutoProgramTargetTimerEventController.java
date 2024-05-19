package org.mq.marketer.campaign.controller.program;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;

public class AutoProgramTargetTimerEventController extends GenericForwardComposer{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Session session;
	
	public AutoProgramTargetTimerEventController() {
		
		session = Sessions.getCurrent();
	}
	
	private MyDatebox targettimeDateboxId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			session.setAttribute("MY_COMPOSER", this);
			MyCalendar currentCal = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
			currentCal.set(MyCalendar.MINUTE, currentCal.get(MyCalendar.MINUTE) + 15);
			logger.info("currentCal====>"+currentCal.getTime());
			targettimeDateboxId.setValue(new MyCalendar(currentCal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
	}
	
	public Calendar getTargetDateObject() {
		
		Calendar  targetDate = targettimeDateboxId.getServerValue();
		
		return targetDate;
		
	}
	/*
	 * public Long getLongObject() {
		
		
		Long numOfDays = new Long(Long.parseLong((String)elapseDaysLbId.getSelectedItem().getValue()));
		return numOfDays;
		
	}
	 */
	
	public void setTargetTime(Calendar targetTime) {
		if(targetTime != null) {
			//targetTime.set
			targettimeDateboxId.setConstraint("");
			targettimeDateboxId.setValue(targetTime);
			//targettimeDateboxId.setConstraint("no past");
		}
		else {
			targettimeDateboxId.setValue(Calendar.getInstance());
		}
		
		//targettimeDateboxId.setValue(cal);
		
	}
}
