package org.mq.marketer.campaign.controller.social;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialAccountPageSettings;
import org.mq.marketer.campaign.beans.SocialCampaign;
import org.mq.marketer.campaign.beans.SocialCampaignSchedule;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.SocialAccountPageSettingsDao;
import org.mq.marketer.campaign.dao.SocialCampaignDao;
import org.mq.marketer.campaign.dao.SocialCampaignDaoForDML;
import org.mq.marketer.campaign.dao.SocialCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SocialCampaignScheduleDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class createCampaignController extends GenericForwardComposer implements EventListener {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	Div addConnectionsDivId, createCampaignDivId, navDivId;
	Div fbPagesDivId, fbConnectDivId, twConnectDivId, lnConnectDivId,
			fbConnectedDivId, twConnectedDivId, lnConnectedDivId;
	Div step1DivId, step2DivId, step3DivId, step4DivId,postsDivId,selectedSocialProvdrDivId;
	Button continueBtnId, step2NxtBtnId;
	Include settingsIncludeId;
	MyDatebox schDateId;
	Textbox commentTbId, campNameTxtBxId, campDescTxtBxId,postTbId;
	Listbox postTypeLbId,postsLbId;
	Checkbox facebookChkId, twitterChkId, linkedinChkId;
	Radiogroup schRGId;
	Radio postNowRadioId;
	Groupbox addPostGroupboxId;
	Label revCampaignNameLblId, revCampaignDescLblId;
	Grid revPostsGridId;
	A step1AId,step2AId,step3AId,step4AId;

	SocialAccountPageSettingsDao socialAccountPageSettingsDao;
	SocialCampaignDao socialCampaignDao;
	SocialCampaignDaoForDML socialCampaignDaoForDML ;
	SocialCampaignScheduleDao socialCampaignScheduleDao;
	SocialCampaignScheduleDaoForDML socialCampaignScheduleDaoForDML;
	SocialCampaign socialCampaign;
	Set<SocialCampaignSchedule> socialCampScheduleSet;

	public static final String NO_CONNECTIONS = "NO_CONNECTIONS";
	private Window nwSettWinId = null;
	List<SocialAccountPageSettings> socialConnectionlist;
	private Listitem currentEditPostLi = null;
	private SocialCampaign editCampaign;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {


		super.doAfterCompose(comp);

		// connesctionsList = getAvailableAccounts();
		socialAccountPageSettingsDao = (SocialAccountPageSettingsDao) SpringUtil
				.getBean("socialAccountPageSettingsDao");
		socialCampaignDao = (SocialCampaignDao) SpringUtil
				.getBean("socialCampaignDao");
		socialCampaignDaoForDML = (SocialCampaignDaoForDML) SpringUtil
				.getBean("socialCampaignDaoForDML");
		socialCampaignScheduleDao = (SocialCampaignScheduleDao) SpringUtil
				.getBean("socialCampaignScheduleDao");
		socialCampaignScheduleDaoForDML = (SocialCampaignScheduleDaoForDML) SpringUtil
				.getBean("socialCampaignScheduleDaoForDML");
		socialConnectionlist = socialAccountPageSettingsDao
				.findAllByUserName(GetUser.getUserName());
		if (socialConnectionlist == null || socialConnectionlist.size() < 1) {
			Redirect.goTo(PageListEnum.SOCIAL_NETWORK_SETTINGS);
		} else {
			String style = "font-weight:bold;font-size:16px;";
			PageUtil.setHeader("Create Social Campaign", "", style, true);

			// gotoStep1();
			displaySocialAccntSelectedPages();
			
			if(session.getAttribute("SocialCampaignObj") != null) {
				
				editCampaign = (SocialCampaign)session.getAttribute("SocialCampaignObj");
				session.removeAttribute("SocialCampaignObj");
				doEditSettings();
			}
		}
	}

	private void displaySocialAccntSelectedPages() {

		try {

			createCampaignDivId.setVisible(true);

			Div mainNavDivId = (Div) Utility.getComponentById("mainNavDivId");
			Components.removeAllChildren(mainNavDivId);

			navDivId.setParent(mainNavDivId);
			navDivId.setVisible(true);
			mainNavDivId.setVisible(true);

			for (SocialAccountPageSettings socialAccountPageSettings : socialConnectionlist) {

				if (socialAccountPageSettings.getProfilePageType().startsWith(
						"FB")) {

					if (socialAccountPageSettings.getProfilePageType().equals(
							"FB_MAIN")) {

						facebookChkId.setVisible(true);
						facebookChkId.setLabel("Facebook : "
								+ socialAccountPageSettings
										.getProfilePageName());
						facebookChkId.setValue(socialAccountPageSettings
								.getProfilePageId());
					} else if (socialAccountPageSettings.getProfilePageType()
							.equals("FB_PAGE")) {

						Checkbox tempChk = new Checkbox(
								"Facebook Page : "
										+ socialAccountPageSettings
												.getProfilePageName());
						tempChk.setValue(socialAccountPageSettings
								.getProfilePageId());
						tempChk.setStyle("padding:2px;");
						fbPagesDivId.appendChild(tempChk);
					}
				} else if (socialAccountPageSettings.getProfilePageType()
						.startsWith("TWIT")) {

					if (socialAccountPageSettings.getProfilePageType().equals(
							"TWIT_MAIN")) {
						twitterChkId.setVisible(true);
					} else if (socialAccountPageSettings.getProfilePageType()
							.equals("TWIT_PAGE")) {

					}
				} else if (socialAccountPageSettings.getProfilePageType()
						.startsWith("LNKIN")) {

					if (socialAccountPageSettings.getProfilePageType().equals(
							"LNKIN_MAIN")) {
						linkedinChkId.setVisible(true);
					} else if (socialAccountPageSettings.getProfilePageType()
							.equals("LNKIN_PAGE")) {

					}
				}
			}
		} catch (Exception e) {

		}
	}
	
	private void doEditSettings() {


		try {
			logger.info("****** Edit social campaign called ****");
			byte editCampaignDestinations = editCampaign.getProviders();
			String style = "font-weight:bold;font-size:16px;";
			PageUtil.setHeader("Edit Social Campaign : "+editCampaign.getCampaignName(), "", style, true);
			
			// PAGE 1 SETTINGS
			if((editCampaignDestinations & Constants.SOCIAL_ADD_FB) == Constants.SOCIAL_ADD_FB) {
				
				facebookChkId.setChecked(true);
				
				List<Component> compList = fbPagesDivId.getChildren();
				if(editCampaign.getFbPageIds() != null && editCampaign.getFbPageIds().length() > 1) {
					String[] fbPagesArr = editCampaign.getFbPageIds().split(",");
					for (Component component : compList) {
						if(component instanceof Checkbox ) {
							Checkbox pageChk= (Checkbox)component;
							for(int i=0;i<fbPagesArr.length;i++) {
								if(pageChk.getValue().equals(fbPagesArr[i])) {
									pageChk.setChecked(true);
							    }	
							}	 // for
								
								/*tempChkBx= new Checkbox(pageChk.getLabel(), "/images/facebook_icon.png");
								tempChkBx.setStyle("margin-left:20px;");
								tempChkBx.setChecked(true); tempChkBx.setDisabled(true);
								tempChkBx.setParent(selectedSocialProvdrDivId);*/
								
							}
						}  // for
					}  //  if 
				
			}   if((editCampaignDestinations & Constants.SOCIAL_ADD_TWITTER) == Constants.SOCIAL_ADD_TWITTER) {
			
				twitterChkId.setChecked(true);
			}  if((editCampaignDestinations & Constants.SOCIAL_ADD_LINKEDIN) == Constants.SOCIAL_ADD_LINKEDIN) {
				
				linkedinChkId.setChecked(true);				
			}
			
			// PAGE 2 SETTINGS
			campNameTxtBxId.setValue(editCampaign.getCampaignName());
			campNameTxtBxId.setDisabled(true);
			campDescTxtBxId.setValue(editCampaign.getDescription());
			
			// PAGE 3 SETTINGS
			Set<SocialCampaignSchedule> campaignScheduleList = editCampaign.getSocialCampSchedules();
			if(campaignScheduleList != null && campaignScheduleList.size() > 0) {
				
				postsDivId.setVisible(true);
				Listitem li = null;
				Listcell lc = null;
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm");
				
				for (SocialCampaignSchedule socialCampaignSchedule : campaignScheduleList) {
					li = new Listitem();
					li.appendChild(new Listcell(socialCampaignSchedule.getPostType()));
					li.appendChild(new Listcell(socialCampaignSchedule.getCampaignContent() ));
					
					String dateStr = sdf.format(socialCampaignSchedule.getScheduleDate().getTime());
					logger.info(dateStr);
					li.appendChild(new Listcell(dateStr));
					
					/*if(socialCampaignSchedule.getScheduleStatus() == Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW) {
						li.appendChild(new Listcell(Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW));
					} else if(socialCampaignSchedule.getScheduleStatus() == Constants.SOCIAL_SCHEDULE_STATUS_SCHEDULE){	
						String dateStr = sdf.format(socialCampaignSchedule.getScheduleDate().getTime());
						logger.info(dateStr);
						li.appendChild(new Listcell(socialCampaignSchedule.getScheduleStatus()));
						//li.appendChild(new Listcell(dt+""));
					}*/// else {
						li.appendChild(new Listcell(socialCampaignSchedule.getScheduleStatus()));
					//}	
					
					lc = new Listcell();
					
					logger.info("social status "+ socialCampaignSchedule.getScheduleStatus());
					if(!(socialCampaignSchedule.getScheduleStatus().trim().equals(Constants.SOCIAL_SCHEDULE_STATUS_SENT)) &&
							!(socialCampaignSchedule.getScheduleStatus().trim().equals(Constants.SOCIAL_SCHEDULE_STATUS_FAILED))) {
						Image img = new Image("/img/email_edit.gif");
						img.addEventListener("onClick", this);
						img.setStyle("padding:10px;");
						img.setAttribute("edit", li);
						//img.setAttribute("campScheduleObj", socialCampaignSchedule);
						img.setParent(lc);
					}
					
					Image img2 = new Image("/img/action_delete.gif");
					img2.addEventListener("onClick", this);
					img2.setParent(lc);
					img2.setAttribute("delete", li);
					img2.setStyle("padding:10px;");
					lc.setParent(li);
					li.setAttribute("campScheduleObj", socialCampaignSchedule);
					li.setParent(postsLbId);
				}
				
				addPostGroupboxId.setVisible(false);
				
				step1AId.setSclass("req_step_completed");
				step2AId.setSclass("req_step_completed");
				step3AId.setSclass("req_step_completed");
				step4AId.setSclass("req_step_completed");
				
			}			
			
			// PAGE 4 SETTINGS
			
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void onClick$addMoreBtnId() throws Exception {




		try {
			if(validatePostInfo()==false) return;
			
			Listitem li;
			// check is the current post is in edit operation .
			// if yes modify edit post itself and set currentEditpost  back to null;
			if(currentEditPostLi != null) {
				li = currentEditPostLi;
				Components.removeAllChildren(li);
				currentEditPostLi = null;
			} else {
				
				li = new Listitem();
			}
			
			// Check post content for empty.
			if(postTbId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("Please enter campaign post content.", "red", "top");
				return;
			}
			
			li.appendChild(new Listcell(postTypeLbId.getSelectedItem().getLabel()));
			li.appendChild(new Listcell(postTbId.getValue()));
			
			if(schRGId.getSelectedIndex() == 0) {
				li.appendChild(new Listcell());
			} else {
				li.appendChild(new Listcell(schDateId.getText()));
			}	
			
			if(postNowRadioId.isChecked()) {
				li.appendChild(new Listcell(Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW));
			}
			else {  
				li.appendChild(new Listcell(Constants.SOCIAL_SCHEDULE_STATUS_SCHEDULE));
			}
			
			Listcell lc = new Listcell();
			
			Image img = new Image("/img/email_edit.gif");
			img.addEventListener("onClick", this);
			img.setStyle("padding:10px;");
			img.setAttribute("edit", li);
			img.setParent(lc);
			
			Image img2 = new Image("/img/action_delete.gif");
			img2.addEventListener("onClick", this);
			img2.setParent(lc);
			img2.setAttribute("delete", li);
			img2.setStyle("padding:10px;");
			lc.setParent(li);
			
			li.setParent(postsLbId);
			
			addPostGroupboxId.setVisible(true);
			addPostGroupboxId.setVisible(false);
			postsDivId.setVisible(true);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}

	private boolean validatePostInfo() {
		
		return true;
	}
	
	
	Div socialProvidersDivId;
	
	public void onClick$step4AId() {
		logger.debug("On Click Anhor called.");
		
		// config social providers which are selected on page 1.
		//selectedSocialProvdrDivId.appendChild(socialProvidersDivId);
		
		Components.removeAllChildren(selectedSocialProvdrDivId);
		Checkbox tempChkBx;
		
		if(facebookChkId.isChecked()) {
			tempChkBx= new Checkbox(facebookChkId.getLabel(), "/images/facebook_icon.png");
			tempChkBx.setStyle("margin-top:10px;");
			tempChkBx.setChecked(true); tempChkBx.setDisabled(true);
			tempChkBx.setParent(selectedSocialProvdrDivId);
		}
		
		
		List<Component> compList = fbPagesDivId.getChildren();

		for (Component component : compList) {
			
			if(component instanceof Checkbox ) {
				Checkbox pageChk= (Checkbox)component;

				if(pageChk.isChecked()) {
					tempChkBx= new Checkbox(pageChk.getLabel(), "/images/facebook_icon.png");
					tempChkBx.setStyle("margin-left:20px;");
					tempChkBx.setChecked(true); tempChkBx.setDisabled(true);
					tempChkBx.setParent(selectedSocialProvdrDivId);
				}
			}
		}
		
		if(twitterChkId.isChecked()) {
			tempChkBx= new Checkbox(twitterChkId.getLabel(), "/images/twitter_icon.png");
			tempChkBx.setStyle("margin-top:10px;");
			tempChkBx.setChecked(true); tempChkBx.setDisabled(true);
			tempChkBx.setParent(selectedSocialProvdrDivId);
		}
		if(linkedinChkId.isChecked()) {
			tempChkBx= new Checkbox(linkedinChkId.getLabel(), "/images/linkedin_icon.png");
			tempChkBx.setStyle("margin-top:10px;");
			tempChkBx.setChecked(true); tempChkBx.setDisabled(true);
			tempChkBx.setParent(selectedSocialProvdrDivId);
		}
		
		revCampaignNameLblId.setValue(campNameTxtBxId.getValue());
		revCampaignDescLblId.setValue(campDescTxtBxId.getValue());
		
		List<Listitem> itemsList = postsLbId.getItems();

		Rows rows = revPostsGridId.getRows();
		if(rows!=null) {
			Components.removeAllChildren(rows);
		}	
		else {
			rows = new Rows();
			rows.setParent(revPostsGridId);
		}
		
		for (Listitem listitem : itemsList) {
			logger.debug("III");
			Row row = new Row();
			row.setParent(rows);
			
			List<Component> listcellcomp = listitem.getChildren();
			
			// EDIT: add edit schedule
			if(editCampaign != null) {
				row.setAttribute("editCampScheduleRowObj", listitem.getAttribute("campScheduleObj"));
			}	
			for (Component comp : listcellcomp) {
				row.appendChild(new Label( ((Listcell)comp).getLabel()));
			}
		}
		
	}
	
	public void onClick$step1NxtBtnId() {
		
		logger.debug("Step1 Next btn clicked");
		//int countChecked = 0;
		
		boolean countFlag = facebookChkId.isVisible() && facebookChkId.isChecked();
		if(countFlag==false) countFlag = twitterChkId.isVisible() && twitterChkId.isChecked();
		if(countFlag==false) countFlag = linkedinChkId.isVisible() && linkedinChkId.isChecked();
		
		
		if(countFlag==false) {
			List<Component> compList = fbPagesDivId.getChildren();

			for (Component component : compList) {
				if(component instanceof Checkbox) {
					if(((Checkbox) component).isChecked()) {
						countFlag = true;
						break;
					}    
				}
			} // for
		} // outer if
		
		if(!countFlag) {
			MessageUtil.setMessage("Please select at least one page to post.", "red", "top");
			return;
		}
		
		Clients.evalJavaScript("changeStep(2, true);");
		
	}
	
	public void onClick$step2NxtBtnId() {

		if(editCampaign == null) {
			String campaignNameStr = campNameTxtBxId.getValue();
			logger.debug("Camp Name :"+ campaignNameStr);
			if(campaignNameStr.length() < 1) {
				
				MessageUtil.setMessage("Please provide campaign name to continue.", "red", "top");
				return;
			}
			
			if(isCampaignNameExists(campaignNameStr)) {
				
				MessageUtil.setMessage("Campaign name already exists.", "red", "top");
				return;
			}
		}
		
		//createPostDivId.setVisible(true);
		Clients.evalJavaScript("changeStep(3, true);");
	}
	
	private boolean isCampaignNameExists(String campName) {
		try {
			
			SocialCampaign socialCampaign = socialCampaignDao.findCampaignByName(GetUser.getUserId(), campName);
			if(socialCampaign != null) {
				return true;
			} 
			return false;
		} catch(Exception e) {
			logger.debug("Exception : Error occured while checking campaign name with DB names ");
			return false;
		}
	}
	
	public void onClick$step3NxtBtnId()  {

		logger.debug("Just enterd >>>>>>>>"+ postsLbId.getItemCount());
		if(postsLbId.getItemCount() < 1) {
			MessageUtil.setMessage("Please add at least one schedule to the campaign.", "red", "top");
			return;
		}
		
		Clients.evalJavaScript("changeStep(4, true);");
		onClick$step4AId();
	}
	
	
	/*public void onClick$step2AId() {

		
		logger.debug("just enter2");
		onClick$step1NxtBtnId();
	}
	
	public void onClick$step3AId() {
		
		logger.debug("just enter3");
		onClick$step2NxtBtnId();
	}*/
	

	public void onClick$submitBtnId() {



		try {
			logger.debug(" submitBtnId Button Clicked");

			//String str = commentTbId.getValue();

			/*if (str.trim().length() == 0) {
				Messagebox.show("Comment string is empty.");
				return;
			}*/

			if (Messagebox.show("Continue to submit the POST", "Prompt",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
				return;
			}

			SocialCampaignSchedule socialCampaignSchedule;
			int campaignProviders = 0;
			String fbPageIds = "";

			// Add FB settings ...
			if (facebookChkId.isChecked()) {

				campaignProviders += Constants.SOCIAL_ADD_FB;
				fbPageIds = facebookChkId.getValue();

				// socialCampaignSchedule = new
				// SocialCampaignSchedule(socialCampaign.getCampaignId(),
				// str,postTypeLbId.getSelectedItem().getLabel(),"",schDateId.getServerValue());
				
			}

			List<Component> childs = fbPagesDivId.getChildren();

			for (Component child : childs) {
				Checkbox tempChk = (Checkbox) child;
				
				logger.info("******* "+ tempChk.getValue());
				
				if (tempChk.isChecked() == false)
					continue;
				
				// if FbPageIds already has a value , we will append it with a (,) 
				if(fbPageIds.trim().length() > 1) {
					fbPageIds += "," + tempChk.getValue();
				} else {
					fbPageIds = tempChk.getValue();
				}	

			} // for

			/*
			 * UsersConnectionRepository newUcr =
			 * (UsersConnectionRepository)SpringUtil
			 * .getBean("usersConnectionRepository");
			 * logger.debug("UCR="+newUcr);
			 * 
			 * ConnectionRepository connectionRepository =
			 * newUcr.createConnectionRepository(GetUser.getUserName());
			 * Connection<Facebook> fbConn =
			 * connectionRepository.findPrimaryConnection(Facebook.class);
			 * Connection<Twitter> twConn =
			 * connectionRepository.findPrimaryConnection(Twitter.class);
			 * Connection<LinkedIn> lnConn =
			 * connectionRepository.findPrimaryConnection(LinkedIn.class);
			 * 
			 * // POST on Facebook if(fbConn!=null) { Facebook fb =
			 * fbConn.getApi(); if(facebookChkId.isChecked()) {
			 * fb.feedOperations
			 * ().post(fb.userOperations().getUserProfile().getId(), str); }
			 * 
			 * List<Component> childs = fbPagesDivId.getChildren(); for
			 * (Component child : childs) { Checkbox tempChk = (Checkbox)child;
			 * if(tempChk.isChecked()==false) continue;
			 * fb.pageOperations().post(tempChk.getValue(), str); } // for }
			 * else { logger.debug("FB Conn is null"); }
			 */

			// Add Twitter Settings ...
			if (twitterChkId.isChecked()) {
				campaignProviders += Constants.SOCIAL_ADD_TWITTER;
				// tw.timelineOperations().updateStatus(str);
			}

			// Add Linked Settings ..
			if (linkedinChkId.isChecked()) {
				campaignProviders += Constants.SOCIAL_ADD_LINKEDIN;
				// logger.debug("Profile TEST ="+lnAdv.test(ln));
				// lnAdv.updateStatus(ln, str);
				// logger.debug("Profile ="+ln.profileOperations().getUserProfile().getFirstName());
			}

			// <<<<<<<<<< Creating the campaign >>>>>>>>>>.

			logger.debug("<<<<<<<<!------------ Just Entered ------------->>>>>");
			String campNameStr = campNameTxtBxId.getValue();
			String campDescStr = campDescTxtBxId.getValue();

			if (campNameStr.length() == 0) {
				Clients.evalJavaScript("changeStep(2,true);");
				campNameTxtBxId.setFocus(true);
				return;
			}
			
			// EDIT : edit setting for new changes ... 
			if(editCampaign != null) {
				logger.info("*********** fbPageIds" + fbPageIds);
				socialCampaign = editCampaign;
				socialCampaign.setDescription(campDescStr);
				socialCampaign.setProviders((byte) campaignProviders);
				socialCampaign.setFbPageIds(fbPageIds);
			} else {
				socialCampaign = new SocialCampaign(GetUser.getUserId(),
					campNameStr, campDescStr, (byte) campaignProviders,Constants.SOCIAL_CAMP_STATUS_ACTIVE, "", fbPageIds);
			}
			
			//socialCampaignDao.saveOrUpdate(socialCampaign);
			socialCampaignDaoForDML.saveOrUpdate(socialCampaign);

			logger.debug("<<<<<< Social Campaign object created successfully >>>>>");

			// <<<<<<<<<<<<<<< Creating the campaign Schedule >>>>>>>>>>>>> 
			logger.debug("campaign id "+ socialCampaign.getCampaignId());
			Calendar scheduleDate = Calendar.getInstance();
			
			if(schRGId.getSelectedIndex() == 1) {
				scheduleDate = schDateId.getServerValue();
			} 
			
			
			Set<SocialCampaignSchedule> socialCampScheduleSet = new HashSet<SocialCampaignSchedule>();
			Rows rows = revPostsGridId.getRows();
			List<Component> rowList =  rows.getChildren();
			Calendar scheduleCalendar = null;
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm");
			
			for (Component component : rowList) {
				Row row = (Row)component;
				logger.debug("********* row.getChildren().get(0)"+ row.getChildren().get(0));
				
				String postType = ((Label)row.getChildren().get(0)).getValue();
				String postContent = ((Label)row.getChildren().get(1)).getValue();
				String postDate = ((Label)row.getChildren().get(2)).getValue();
				String postSchedule = ((Label)row.getChildren().get(3)).getValue();
				
				logger.debug("posttype :"+ postType + " content "+ postContent + " postSchedule "+ postSchedule);
				String scheduleStatus = "";
				
				if(postSchedule.equals("Post Now")) {
					scheduleCalendar = Calendar.getInstance();
					scheduleStatus = Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW;
				} else {
					scheduleStatus = Constants.SOCIAL_SCHEDULE_STATUS_SCHEDULE;
					scheduleCalendar = Calendar.getInstance();
					Date date = sdf.parse(postDate);
					scheduleCalendar.setTime(date);
				}
				
				// EDIT: edit part
				if(row.getAttribute("editCampScheduleRowObj") != null) {
					socialCampaignSchedule = (SocialCampaignSchedule)row.getAttribute("editCampScheduleRowObj");
					socialCampaignSchedule.setCampaignContent(postContent);
					socialCampaignSchedule.setPostType(postType);
					socialCampaignSchedule.setScheduleDate(scheduleCalendar);
				} else {
					socialCampaignSchedule = new SocialCampaignSchedule(socialCampaign.getCampaignId(), postContent, postType
						, "", scheduleStatus, scheduleCalendar);
				}
				
				//socialCampaignScheduleDao.saveOrUpdate(socialCampaignSchedule);
				socialCampaignScheduleDaoForDML.saveOrUpdate(socialCampaignSchedule);

				socialCampScheduleSet.add(socialCampaignSchedule);
				
				if(socialCampaignSchedule.getScheduleStatus().equals(Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW) ||
						socialCampaignSchedule.getScheduleStatus().equals(Constants.SOCIAL_SCHEDULE_STATUS_SCHEDULE)) {
					socialCampaign.setCampaignStatus(Constants.SOCIAL_CAMP_STATUS_ACTIVE);
				}
			}
			
			
			//socialCampaignScheduleDao.saveOrUpdate(socialCampaignSchedule);
			
			//<<<<<<<<<<< Add social campaign schedule to campaign >>>>>>>>>
			//Set<SocialCampaignSchedule> scheduleSet = new HashSet<SocialCampaignSchedule>();
			//scheduleSet.add(socialCampaignSchedule);
			socialCampaign.setSocialCampSchedules(socialCampScheduleSet);
			//socialCampaignDao.saveOrUpdate(socialCampaign);
			socialCampaignDaoForDML.saveOrUpdate(socialCampaign);

			logger.debug("Social Campaign and schedules created successfully >>>>>>>");
			Redirect.goTo(PageListEnum.SOCIAL_MY_SOCIAL_CAMPAIGNS);

		} catch (Exception e) {
			logger.error(" ** Error in Submit:", e);
			logger.error("Exception ::" , e);
		}

	}
	
	@Override
	public void onEvent(Event event) throws Exception {

		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Image) {
			
			Image img = (Image)event.getTarget();
			logger.debug("image clicked ..");
			
			if(img.getAttribute("edit") != null) {
				try {
					logger.debug(img.getAttribute("edit"));
					addPostGroupboxId.setVisible(true);
					Listitem li = (Listitem)img.getAttribute("edit");
					
					List<Component> compList = li.getChildren();
					
					String postType = ((Listcell)compList.get(0)).getLabel();
					String descStr =  ((Listcell)compList.get(1)).getLabel();
					String scheduleDate =  ((Listcell)compList.get(2)).getLabel();
					String schedule = ((Listcell)compList.get(3)).getLabel();
					
					logger.debug( " postType : "+postType +"  descStr: "+ descStr + "  schedule: "+ schedule + " : scheduleDate :" + scheduleDate);
					
					// sets post type
					List<Listitem> liList = postTypeLbId.getItems();
					for (Listitem listitem : liList) {
						
						if(listitem.getLabel().equals(postType)) {
							postTypeLbId.setSelectedItem(listitem);
							break;
						}
					}
					
					// set post description
					postTbId.setValue(descStr);
					
					// set post schedule
					if(schedule.equals(schRGId.getItemAtIndex(0).getLabel())) {
						schRGId.setSelectedIndex(0);
					} else { //(schedule.equals(schRGId.getItemAtIndex(0).getLabel()))
						
						if(img.getParent().getParent().getAttribute("campScheduleObj") != null) { // Edit mode
							SocialCampaignSchedule campScheduleObj = (SocialCampaignSchedule)img.getParent().getParent().getAttribute("campScheduleObj");
							Calendar cal = campScheduleObj.getScheduleDate();
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm");
							Date date = sdf.parse(scheduleDate);
							cal.setTime(date);
							schDateId.setValue(cal);
							schRGId.setSelectedIndex(1);
						} else {  
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm");
							Date date = sdf.parse(scheduleDate);
							cal.setTime(date);
							logger.info("schedule date is : "+ date);
							schDateId.setValue(cal);
							schRGId.setSelectedIndex(1);
						}
					}
					//  currentEditPostLi value is set to li so that it will 
					//  be saved on save operation.
					currentEditPostLi = li;
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				}
								
			} else if(img.getAttribute("delete") != null) {
				try {
					logger.debug(" Del button clicked : "+img.getAttribute("delete"));
					
					if (Messagebox.show("Do you really want to delete the schedule ", "Prompt",
							Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
						return;
					}
					
					Listitem li = (Listitem)img.getAttribute("delete");
					Listbox lbx = (Listbox)li.getParent();
					lbx.removeItemAt(li.getIndex());
					
					if(li.getAttribute("campScheduleObj") != null) {
						SocialCampaignSchedule socialCampaignSchedule = (SocialCampaignSchedule)li.getAttribute("campScheduleObj");
						//socialCampaignScheduleDao.delete(socialCampaignSchedule);
						socialCampaignScheduleDaoForDML.delete(socialCampaignSchedule);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				}
			}
			
		}
		
		
	}

}