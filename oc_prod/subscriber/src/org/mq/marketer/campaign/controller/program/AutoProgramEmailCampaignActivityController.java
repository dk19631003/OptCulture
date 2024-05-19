package org.mq.marketer.campaign.controller.program;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class AutoProgramEmailCampaignActivityController extends GenericForwardComposer{
	
	private CampaignsDao campaignsDao;
	private Session session;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	public AutoProgramEmailCampaignActivityController() {
		session = Sessions.getCurrent();
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.setAttribute("MY_COMPOSER", this);
		
	}
	
	
	public List<Campaigns> getCampaigns(){
		
		List<Campaigns> campList = campaignsDao.findByUser(GetUser.getUserId());
		return campList;
		
		
	}
	
	private Listbox userEmailCampaignsLbId;
	public Campaigns getCampaignObject() {
		
		
		Campaigns campaign = (Campaigns)userEmailCampaignsLbId.getSelectedItem().getValue();
		if(campaign == null) {
			
			MessageUtil.setMessage("Please select an email campaign.", "color:red");
			return null;
		}
		return campaign;
		
	}
	
	public boolean setSelectionOfListItem(String campName) {
		logger.debug("-----just entered to select the campaign configured------");
		List items = userEmailCampaignsLbId.getItems();
		if(items.size() == 1) {
			MessageUtil.setMessage("You have no email campaigns. Please create at least one email campaign.", "color:red");
			return false;
			
		}
		Listitem item = null;
		/*for (Object object : items) {
			item = (Listitem)object;
			if(item.getLabel().equals(campName)) {
				
				item.setSelected(true);
				
			}else {
				
				userEmailCampaignsLbId.setSelectedIndex(0);
				continue;
			}
			
			
		}//for
*/		for(int i=1; i<items.size(); i++) {
	
			item = (Listitem)items.get(i);
			if(item.getLabel().equalsIgnoreCase(campName)) {
				
				item.setSelected(true);
				break;
				
			}else {
				
				userEmailCampaignsLbId.setSelectedIndex(0);
				continue;
			}
	
		}//for
		
		return true;
	}// setSelectionOfListItem
	
}//class
