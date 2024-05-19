package org.mq.marketer.campaign.controller.program;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class AutoProgramSMSCampaignActivityController  extends GenericForwardComposer{

	//private Users user;
	private SMSCampaignsDao smsCampaignsDao;
	private Session session;
	private Listbox userSMSCampaignsLbId;
	public AutoProgramSMSCampaignActivityController() {
		
		smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		session = Sessions.getCurrent();
		
	}
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		session.setAttribute("MY_COMPOSER", this);
		
	}
	
	public List<SMSCampaigns> getSmsCampaigns(){
		
		List<SMSCampaigns> smsCampList = smsCampaignsDao.findAllByUser(GetUser.getUserId());
		return smsCampList;
		
		
	}
	
	public SMSCampaigns getSmsCampaignObject() {
		
		
		SMSCampaigns smsCampaign = (SMSCampaigns)userSMSCampaignsLbId.getSelectedItem().getValue();
		if(smsCampaign == null) {
			
			MessageUtil.setMessage("Please select the SMS campaign.", "color:red");
			return null;
		}
		return smsCampaign;
		
	}
	
	public boolean setSelectionOfListItem(String smsCampName) {
		
		logger.debug("---just entered to set the SMS campaign------------");
		
		List items = userSMSCampaignsLbId.getItems();
		if(items.size() == 1) {
			MessageUtil.setMessage("You have no SMS campaigns. Please create at least one SMS campaign.", "color:red");
			return false;
			
		}
		Listitem item = null;
		
		for(int i=1; i<items.size(); i++) {
			
			item = (Listitem)items.get(i);
			if(item.getLabel().equalsIgnoreCase(smsCampName)) {
				
				item.setSelected(true);
				break;
				
			}else {
				
				userSMSCampaignsLbId.setSelectedIndex(0);
				continue;
			}
			
		}//for
		/*for (Object object : items) {
			item = (Listitem)object;
			if(item.getLabel().equals(smsCampName)) {
				
				item.setSelected(true);
				
			}else {
				
				userSMSCampaignsLbId.setSelectedIndex(0);
				continue;
			}
			
		}
		*/
		return true;
	}//setSelectionOfListItem

}
