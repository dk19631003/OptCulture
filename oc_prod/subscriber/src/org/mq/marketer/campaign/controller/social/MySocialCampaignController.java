package org.mq.marketer.campaign.controller.social;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialCampaign;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SocialCampaignDao;
import org.mq.marketer.campaign.dao.SocialCampaignDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class MySocialCampaignController extends GenericForwardComposer implements EventListener{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public MySocialCampaignController() {
		// TODO Auto-generated constructor stub
	}
	
	Listbox mySocialCampaignsLstId;
	SocialCampaignDao socialCampaignDao;
	SocialCampaignDaoForDML socialCampaignDaoForDML;
	Users user = GetUser.getUserObj();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		socialCampaignDao = (SocialCampaignDao) SpringUtil.getBean("socialCampaignDao");
		socialCampaignDaoForDML = (SocialCampaignDaoForDML) SpringUtil.getBean("socialCampaignDaoForDML");

		List<SocialCampaign> socialCampList = socialCampaignDao.findByUserId(user.getUserId());
		
		if(socialCampList != null && socialCampList.size() > 0) {
			Listitem li;
			Listcell lc;
			for (SocialCampaign socialCampaign : socialCampList) {
				li = new Listitem();
				//li.setAttribute("socialCampObj", socialCampaign);
				li.appendChild( new Listcell(socialCampaign.getCampaignName()));
				li.appendChild(new Listcell(socialCampaign.getCampaignName()));
				
				// add Provider images.
				lc = new Listcell();
				byte providersConfig = socialCampaign.getProviders();
				
				if((providersConfig & Constants.SOCIAL_ADD_FB) == Constants.SOCIAL_ADD_FB) {
					
					Image img3 = new Image("/images/facebook_icon.png");
					img3.setStyle("padding:5px;");
					img3.setParent(lc);
				}   if((providersConfig & Constants.SOCIAL_ADD_TWITTER) == Constants.SOCIAL_ADD_TWITTER) {
					Image img4 = new Image("/images/twitter_icon.png");
					img4.setStyle("padding:5px;");
					img4.setParent(lc);
				}  if((providersConfig & Constants.SOCIAL_ADD_LINKEDIN) == Constants.SOCIAL_ADD_LINKEDIN) {
					
					Image img5 = new Image("/images/linkedin_icon.png");
					img5.setStyle("padding:5px;");
					img5.setParent(lc);
				}
				lc.setParent(li);
				
				li.appendChild(new Listcell(socialCampaign.getCreatedDate().getTime()+ ""));
				li.appendChild(new Listcell(socialCampaign.getCampaignStatus()));
				
				// add edit options
				
				lc = new Listcell();
				
				Image img = new Image("/img/email_edit.gif");
				img.addEventListener("onClick", this);
				img.setStyle("padding:5px;");
				img.setAttribute("editSocialCampObj", socialCampaign);
				img.setParent(lc);
				
				Image img2 = new Image("/img/action_delete.gif");
				img2.addEventListener("onClick", this);
				img2.setParent(lc);
				img2.setAttribute("deleteSocialCampObj", socialCampaign);
				img2.setStyle("padding:5px;");
				lc.setParent(li);
				
				li.setParent(mySocialCampaignsLstId);
			}
		}
		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Image) {
			Image img = (Image)event.getTarget();
			SocialCampaign socialCampaign = null;
			if(img.getAttribute("editSocialCampObj")!= null) {
				logger.info("edit btn clicked");
				socialCampaign = (SocialCampaign)img.getAttribute("editSocialCampObj");
				session.setAttribute("SocialCampaignObj", img.getAttribute("editSocialCampObj"));
				Redirect.goTo(PageListEnum.SOCIAL_CREATE_CAMPAIGN);
			} else if (img.getAttribute("deleteSocialCampObj") != null) {
				logger.info("delete btn clicked");
				
				socialCampaign = (SocialCampaign)img.getAttribute("deleteSocialCampObj");
				if (Messagebox.show("Do you really want to delete the campaign : "+ socialCampaign.getCampaignName() , "Prompt",
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
					return;
				}
				
				//socialCampaignDao.delete(socialCampaign);
				socialCampaignDaoForDML.delete(socialCampaign);

				Listitem li = (Listitem)img.getParent().getParent();
				Listbox lbox = li.getListbox();
				lbox.removeItemAt(li.getIndex());
			}
		}
		
	}
	
	
}
