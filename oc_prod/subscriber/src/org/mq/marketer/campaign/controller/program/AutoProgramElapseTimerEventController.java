package org.mq.marketer.campaign.controller.program;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class AutoProgramElapseTimerEventController extends GenericForwardComposer{
	
	private Session session;
	private String[] elapseDays = {"1","2","3","4","5","6","7","8","9","10"};
	private Listbox elapseDaysLbId,hoursDaysLbId;
	public AutoProgramElapseTimerEventController() {
		
		//session = Sessions.getCurrent();
		/*for(int i=0; i<10; i++) {
			elpsaDays[i] = ""+i;
			
			
		}
		logger.info("elpsaDays ====>"+elpsaDays);*/
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.setAttribute("MY_COMPOSER", this);
		//elapseDaysLbId.setModel(new ListModelList(elapseDays));
		//elapseDaysLbId.setSelectedIndex(0);
		
	}
	
	
	public void setSelectionOfListItem(Long minOffset) {
		
		logger.debug("-----just entered to set the selection of configured properties-----");
		if(minOffset == null) {
			
			elapseDaysLbId.setSelectedIndex(0);
			hoursDaysLbId.setSelectedIndex(0);
			
		}
		
		if(minOffset != null) {
			//logger.info("minOffiss===>"+minOffset);
			long offset = minOffset.longValue();
			String minOff = null;
			if(offset >= 1440) {
				offset = offset/1440;
				hoursDaysLbId.setSelectedIndex(1);
				
			}else if(offset >= 0 && offset < 1440 ) {
				offset = offset/60;
				hoursDaysLbId.setSelectedIndex(0);
			}
			//logger.info("the off set is====>"+offset);
			for (int i = 0; i < elapseDaysLbId.getItemCount(); i++) {
				//logger.info("----just entered---"+i);
				minOff =(String)elapseDaysLbId.getItemAtIndex(i).getValue();
				if(minOff.equals(""+offset)) {
					//logger.info("-----minOff matched-----");
					elapseDaysLbId.setSelectedIndex(i);
					break;
				}//if
				else continue;
				
				
				
			}//for
		
		}	
		
	}
	
	
	public void setSelectionOfListItem(String numOfDays) {
		//logger.info("numOfDays====>"+numOfDays);
		String minOff = null;
		if(numOfDays.equalsIgnoreCase("Elapse Timer")) {
			elapseDaysLbId.setSelectedIndex(0);
			hoursDaysLbId.setSelectedIndex(0);
		}
		else {
			if(numOfDays.contains("Day(s)")){
				logger.info("====yes contains days=====");
				numOfDays = numOfDays.replace(("Day(s)").trim(), "");
				hoursDaysLbId.setSelectedIndex(1);
			}
			else {
				logger.info("====yes contains hours====");
				numOfDays = numOfDays.replace(("Hour(s)").trim(), "");
				hoursDaysLbId.setSelectedIndex(0);
			}
			
			numOfDays = numOfDays.replace(("Wait For").trim(), "");
			
			logger.info("numOfDays=====>"+numOfDays);
			for (int i = 0; i < elapseDaysLbId.getItemCount(); i++) {
				minOff =(String)elapseDaysLbId.getItemAtIndex(i).getValue();
				
				if(minOff.equals(numOfDays.trim())) {
					
					elapseDaysLbId.setSelectedIndex(i);
					break;
				}//if
				else continue;
			}//for
			//minOff = minOff.trim();
			//long numVal = Long.parseLong(minOff);
			
			/*for (int i = 0; i < elapseDays.length; i++) {
				
				if(elapseDays[i].equals(numOfDays.trim())) {
					logger.info("elapseDays are====>"+elapseDays[i]);
					elapseDaysLbId.setSelectedIndex(i);
					break;
				}//if
				else continue;
			}//for
*/			
			
		}//else
		
	}
	public Long getLongObject() {
		
		long numberVal = 0l;
		long mins = 0l;
		
		numberVal = Long.parseLong((String)elapseDaysLbId.getSelectedItem().getValue());
		mins = Long.parseLong((String)hoursDaysLbId.getSelectedItem().getValue());
		Long numOfDays = new Long(numberVal * mins);
		return numOfDays;
		
	}
	
	
}
