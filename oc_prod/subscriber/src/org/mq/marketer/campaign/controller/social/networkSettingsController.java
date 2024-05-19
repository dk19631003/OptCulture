package org.mq.marketer.campaign.controller.social;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialAccountPageSettings;
import org.mq.marketer.campaign.beans.UserConnection;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SocialAccountPageSettingsDao;
import org.mq.marketer.campaign.dao.SocialAccountPageSettingsDaoForDML;
import org.mq.marketer.campaign.dao.UserConnectionDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.twitter.api.Twitter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class networkSettingsController extends GenericForwardComposer  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	Div fbAccountsDivId, twAccountsDivId, lnAccountsDivId;
	Listbox fbAccountsLBoxId, twAccountsLBoxId, lnAccountsLBoxId;
	
	Button addFbAccountBtnId, deleteFbAccountBtnId, 
		addTwAccountBtnId, deleteTwAccountBtnId, addLnAccountBtnId, deleteLnAccountBtnId,
		continueBtnId;
	
	createCampaignController ccController;
	List<UserConnection> connesctionsList=null;
	SocialAccountPageSettingsDao socialAccountPageSettingsDao; 
	SocialAccountPageSettingsDaoForDML socialAccountPageSettingsDaoForDML; 
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		String style = "font-weight:bold;font-size:16px;";
		PageUtil.setHeader("Social Networking Accounts", "", style, true);
		
		socialAccountPageSettingsDao = (SocialAccountPageSettingsDao)SpringUtil.getBean("socialAccountPageSettingsDao");
		socialAccountPageSettingsDaoForDML = (SocialAccountPageSettingsDaoForDML)SpringUtil.getBean("socialAccountPageSettingsDaoForDML");
		
		connesctionsList = getAvailableAccounts();
		updateConnectionsStatus();
		setProfilePageSavedPages();		
	}
	
	
	private List<UserConnection> getAvailableAccounts() {
		logger.debug("--just Entered ---");
		UserConnectionDao userConnectionDao = (UserConnectionDao)SpringUtil.getBean("userConnectionDao");
		List<UserConnection> list = userConnectionDao.findByUserName(GetUser.getUserName());
		
		logger.debug("Connections List = "+list);
		
		if(list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	/*
	 *  Checkmarks already saved profile pages/fan pages for the current user.
	 */
	public void setProfilePageSavedPages() {
		try {
			
			List<SocialAccountPageSettings> list = socialAccountPageSettingsDao.findAllByUserName(GetUser.getUserName());
			List<Listitem> liList = fbAccountsLBoxId.getItems();
			
			for (SocialAccountPageSettings socialAccountPageSettings : list) {
				
				if(socialAccountPageSettings.getProfilePageType().startsWith("FB")) {
					
					for(Listitem li : liList) {
						
						if(socialAccountPageSettings.getProfilePageId().equals(li.getValue())) {
							li.setSelected(true);
						}
					}
				} else if(socialAccountPageSettings.getProfilePageType().startsWith("TWIT")) {
					
					twAccountsLBoxId.getItemAtIndex(0).setSelected(true);
					
				} else if(socialAccountPageSettings.getProfilePageType().startsWith("LNKIN")) {
					
					lnAccountsLBoxId.getItemAtIndex(0).setSelected(true);
				}
				
			}
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}	
	}
	
	
	public void onClick$continueBtnId() throws Exception {
		
		List<Listitem> listItemsSet = fbAccountsLBoxId.getItems();
		logger.debug("Listitem set = "+ listItemsSet.size());
		
		if(socialAccountPageSettingsDao == null) {
			logger.debug("*** Exception : Error occured while fetching socialAccountPageSettingsDao returning ... ");
		}
		
		if(listItemsSet.size() > 0) {
			savePageSettingByListAndTitles(fbAccountsLBoxId,"Facebook : ","Facebook Page : ","FB");
		}
		
		if(twAccountsLBoxId.getItems().size() > 0) {
			savePageSettingByListAndTitles(twAccountsLBoxId,"Twitter : ","Twitter Page : ","TWIT");
		}
		
		if(lnAccountsLBoxId.getItems().size() > 0) {
			savePageSettingByListAndTitles(lnAccountsLBoxId,"LinkedIn : ","Facebook Page : ","LNKIN");
		}
		
		
		
		logger.debug("<<<< User Social Account Page setting Saved Successfully >>>>> ");
		
		Redirect.goTo(PageListEnum.SOCIAL_CREATE_CAMPAIGN);
	 }
	
	
	/*
	 *  Saves the User Social Account Pages or fan page settings
	 */
	private boolean savePageSettingByListAndTitles(Listbox providerPagesLbId,String mainPageStr, String subPageStr,String providerStr) {
		try {
			
			List<Listitem> listItemsList = providerPagesLbId.getItems();
			SocialAccountPageSettings socialAccountPage = null;
			
			SocialAccountPageSettings existAccountPage = null;
			for (Listitem listitem : listItemsList) {
				
				logger.debug("list item label "+ listitem.getLabel() + " listItem val : "+ listitem.getValue());
				
				if(listitem.getLabel() == null || listitem.getValue() == null) {
					logger.debug("*** Social Account Page Label or Page value is missing .. returning ...");
					return false;
				}
				
				String profilePageStr = listitem.getLabel();
				
				
				if(profilePageStr.startsWith(mainPageStr)) {
					
					existAccountPage = socialAccountPageSettingsDao.findByDetails(GetUser.getUserName(),
							profilePageStr.substring(mainPageStr.length()-1,profilePageStr.length()).trim(),(String)listitem.getValue());
					
					if(existAccountPage == null) {
						socialAccountPage = new SocialAccountPageSettings(GetUser.getUserName(),profilePageStr.substring(mainPageStr.length(),profilePageStr.length()).trim(),
							(String)listitem.getValue(),providerStr +"_MAIN");
					}
				} else if(listitem.getLabel().startsWith(subPageStr)){
					
					existAccountPage = socialAccountPageSettingsDao.findByDetails(GetUser.getUserName(),
							profilePageStr.substring(subPageStr.length()-1,profilePageStr.length()).trim(),(String)listitem.getValue());
					
					if(existAccountPage == null) {
						socialAccountPage = new SocialAccountPageSettings(GetUser.getUserName(),profilePageStr.substring(subPageStr.length(),profilePageStr.length()).trim(),
							(String)listitem.getValue(),providerStr+"_PAGE");
					}
				}
				
				
				logger.debug("is exist : "+ existAccountPage);
				
				/*if(existAccountPage != null) {
					socialAccountPage.setId(existAccountPage.getId());
				}  */				
				
				if(!listitem.isSelected() && existAccountPage != null) {
					socialAccountPageSettingsDaoForDML.delete(existAccountPage);
				} else if(listitem.isSelected() && existAccountPage == null) {
					socialAccountPageSettingsDaoForDML.saveOrUpdate(socialAccountPage);
				}	
				
			}
			return true;
		} catch(Exception e) {
			
			logger.error("Exception ::" , e);
			return false;
		}
	}

	
	private void updateConnectionsStatus() {
		try {

			if(connesctionsList==null || connesctionsList.isEmpty()) {
				return;
			}
			
			UsersConnectionRepository newUcr = (UsersConnectionRepository)SpringUtil.getBean("usersConnectionRepository");
			ConnectionRepository connectionRepository=newUcr.createConnectionRepository(GetUser.getUserName());
			
			for (UserConnection conn : connesctionsList) {
				
				if(conn.getProviderId().equalsIgnoreCase("facebook")) {
					
					Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
					Facebook fb = connection.getApi();
					
					FacebookProfile fbProfile = fb.userOperations().getUserProfile();
					
					Listitem li = new Listitem("Facebook : "+ fbProfile.getFirstName()+" "+fbProfile.getLastName(), fbProfile.getId());
					li.setParent(fbAccountsLBoxId);
					
					addFbAccountBtnId.setVisible(false);
					deleteFbAccountBtnId.setVisible(true);
					
					List<Account> accList = fb.pageOperations().getAccounts();
					logger.debug("accList ="+accList );
					
					for (Account account : accList) {
						try {
							logger.debug("Name="+ account.getName()+" : Category="+account.getCategory()+	" : Id "+account.getId());
							
							Listitem newLi = new Listitem("Facebook Page : "+account.getName(), account.getId());
							newLi.setSclass("myindent");
							newLi.setParent(fbAccountsLBoxId);

							
						} catch (Exception e) {
							logger.error("Exception ::" , e);
						}
					}
				}
				
				if(conn.getProviderId().equalsIgnoreCase("twitter")) {

					Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
					Twitter tw = connection.getApi();

					Listitem li = new Listitem("Twitter : " + tw.userOperations().getScreenName(), 
							""+tw.userOperations().getProfileId());
					
					li.setParent(twAccountsLBoxId);
					
					
					addTwAccountBtnId.setVisible(false);
					deleteTwAccountBtnId.setVisible(true);
				}
				
				if(conn.getProviderId().equalsIgnoreCase("linkedin")) {

					Connection<LinkedIn> connection = connectionRepository.findPrimaryConnection(LinkedIn.class);
					LinkedIn ln = connection.getApi();
					LinkedInProfile lnPro = ln.profileOperations().getUserProfile();

					Listitem li = new Listitem("LinkedIn : " + lnPro.getFirstName()+" "+lnPro.getLastName(), lnPro.getId());
					
					li.setParent(lnAccountsLBoxId);

					addLnAccountBtnId.setVisible(false);
					deleteLnAccountBtnId.setVisible(true);
				}
				
			} // for 			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void onClick$deleteFbAccountBtnId() {
		List<SocialAccountPageSettings> allFBPagesList = socialAccountPageSettingsDao.findAllPagesByProviderType(
				GetUser.getUserName(),Constants.SOCIAL_FACEBOOK_FB);
		
		Iterator it = allFBPagesList.iterator();
		SocialAccountPageSettings socialAccountPageSettings;
		while(it.hasNext()) {
			socialAccountPageSettings = (SocialAccountPageSettings)it.next();
			socialAccountPageSettingsDaoForDML.delete(socialAccountPageSettings);
		}
	}
	
	public void onClick$deleteTwAccountBtnId() {
		List<SocialAccountPageSettings> allFBPagesList = socialAccountPageSettingsDao.findAllPagesByProviderType(
				GetUser.getUserName(),Constants.SOCIAL_TWITTER_TWIT);
		
		Iterator it = allFBPagesList.iterator();
		SocialAccountPageSettings socialAccountPageSettings;
		while(it.hasNext()) {
			socialAccountPageSettings = (SocialAccountPageSettings)it.next();
			socialAccountPageSettingsDaoForDML.delete(socialAccountPageSettings);
		}
	}

	public void onClick$deleteLnAccountBtnId() {
		List<SocialAccountPageSettings> allFBPagesList = socialAccountPageSettingsDao.findAllPagesByProviderType(
				GetUser.getUserName(),Constants.SOCIAL_LINKEDIN_LNKIN);
		
		Iterator it = allFBPagesList.iterator();
		SocialAccountPageSettings socialAccountPageSettings;
		while(it.hasNext()) {
			socialAccountPageSettings = (SocialAccountPageSettings)it.next();
			socialAccountPageSettingsDaoForDML.delete(socialAccountPageSettings);
		}
	}
}