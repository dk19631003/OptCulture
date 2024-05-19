package org.mq.marketer.campaign.controller.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSettingsDaoForDML;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;

public class SMSSettingsController extends GenericForwardComposer implements EventListener {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Textbox shortCodeTbId, senderIdTbId, optInKeywordTbId, optInMsgTbId, linkURLTxtBoxId,
	caretPosTB1,caretPosTB2,caretPosTB3,caretPosTB4, charCountTbId, welcomecharCountTbId, welcomeMsgTbId, optOutKeywordTbId, welcomelinkURLTxtBoxId,
	unsubConfirmMsgTbId, helpKeywordTbId, helpMsgTbId, optoutlinkURLTxtBoxId, optoutcharCountTbId, helplinkURLTxtBoxId, helpcharCountTbId;
	
	private Label optoutHeaderLblId, optinHeaderLblId, helpHeaderLblId, welcomeHeaderLblId;
	
	private Button confirmBtn1Id;
	//private String senderId;
	private Checkbox enableChwelcomeChkBox, enableUnSubswelcomeChkBox, enableOptInChkBox,
	enableWebFormChkBox, enableManualChkBox, enablePOSChkBox;
	
	private Div  fstDivId, secondDivId;
	private Listbox selListLbId;
	
	private Html welcomeMsgTemplateHtmlId, optInMsgTemplateHtmlId, optoutMsgTemplateHtmlId, helpMsgTemplateHtmlId;
	private Div optInMsgTemplateDivId, welcomeMsgTemplateDivId, optOutMsgTemplateDivId, helpMsgTemplateDivId;
	private A createNewListAnchId;
	private Div enableOptinDivId, enableWelcomeDivId, unsubscribDivId;
	
	private SMSSettingsDao smsSettingsDao;
	private SMSSettingsDaoForDML smsSettingsDaoForDML;
	private MailingListDao mailingListDao;
	private UrlShortCodeMappingDao urlShortCodeMappingDao; 
	private UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML; 
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private Users currUser;
	private List<SMSSettings> smsSettings;
	//Set<Long> userIdsSet = GetUser.getUsersSet();
	Set<String> userRoleSet;
	
	public static final String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
	
	public static final String OptinTemplateStr = "<html><body style='margin:10px;font-size:10px;'><p style='font-size:13px;'>For example:<br></br>" +
													"OptCulture:Welcome to our Subscribers Program! Send JOINOC to 888555 to<br></br>"+
													"opt-in. "+"Msg&amp;Data Rates May Apply." +"Get 2 msgs/week." +
													"Rply HELPOC 4 help.</p></body></html>";
	
	public static final String welcomeTemplateStr = "<html><body style='margin:10px;font-size:10px;'><p style='font-size:13px;'>For example:<br></br>" +
													"OptCulture: Thanks 4 subscribing! Update ur contact info:"+appShortUrl+"/u5nt0d.<br></br> " +
													"Msg&amp;Data Rates May Apply." +
													"Get 2 msgs/week." +
													"Rply HELPOC 4 help or STOPOC 2 cancel.</p></body></html>";
	
	public static final String optoutTemplateStr = "<html><body style='margin:10px;font-size:10px;'><p style='font-size:13px;'>For example:<br></br>" +
													"OptCulture: You have opted out of our Subscribers Program. You will not<br></br>" +
													"receive additional messages. For any questions, contact: "+appShortUrl+"u5nt0d.</p></body></html>" ;

	public static final String helpTemplateStr = "<html><body style='margin:10px;font-size:10px;'><p style='font-size:13px;'>For example:<br></br>" +
													"OptCulture: We send store offers &amp;  updates. Contact "+appShortUrl+"u5nt0d or 800-8889996677.<br></br>" +
													"Msg &amp; Data Rates May Apply." +
													" 4 msgs/mo." +
													"Rply STOPOC 2 cancel.</p></body></html>";

	
	
	
	private Session session;
	
	
	public SMSSettingsController() {
		
		session = Sessions.getCurrent();
		//senderId = PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID);
		smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		smsSettingsDaoForDML = (SMSSettingsDaoForDML)SpringUtil.getBean("smsSettingsDaoForDML");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		currUser = GetUser.getUserObj();
		//smsSettings = smsSettingsDao.findByOrg(currUser.getUserOrganization().getUserOrgId());
		smsSettings = smsSettingsDao.findByUser(currUser.getUserId());
		urlShortCodeMappingDao = (UrlShortCodeMappingDao)SpringUtil.getBean("urlShortCodeMappingDao");
		urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML)SpringUtil.getBean("urlShortCodeMappingDaoForDML");
		
		userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Program","",style,true);
		
	}
	
	public String getSenderID() {
		
		String type = SMSStatusCodes.defaultSMSTypeMap.get(currUser.getCountryType());
		if(SMSStatusCodes.optInMap.get(currUser.getCountryType())) type = Constants.SMS_ACCOUNT_TYPE_OPTIN;
		
		List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(currUser.getUserId(), type);
		if(retList == null) return Constants.STRING_NILL;
		
		return retList.get(0).getSenderId();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		shortCodeTbId.setValue(getSenderID());
		shortCodeTbId.setDisabled(true);
		
		
		optInMsgTbId.setCtrlKeys("^v");
		optInMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
		optInMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		
		welcomeMsgTbId.setCtrlKeys("^v");
		welcomeMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
		welcomeMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		
		helpMsgTbId.setCtrlKeys("^v");
		helpMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
		helpMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		
		unsubConfirmMsgTbId.setCtrlKeys("^v");
		unsubConfirmMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
		unsubConfirmMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		
		
		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		List<MailingList> mlList =  mailingListDao.findByIds(listIdsSet);
		
		Listitem mlItem = null;
		if(mlList != null && mlList.size() > 0) {
			
			for (MailingList mailingList : mlList) {
				
				mlItem =  new Listitem(mailingList.getListName());
				mlItem.setValue(mailingList);
				mlItem.setParent(selListLbId);
				
			}//for
			
		}//if
		
		if(smsSettings != null) {
			
			SMSSettings optinSettings = null;
	  		SMSSettings optOutSettings = null;
	  		SMSSettings helpSettings = null;
	  		
	  		for (SMSSettings eachSMSSetting : smsSettings) {
	  			
	  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
	  			
	  		}
			
	  		String messageHeader = Constants.STRING_NILL;
	  		
			
			if(optinSettings != null){
				if(messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				optInKeywordTbId.setValue( optinSettings.getKeyword() != null ? optinSettings.getKeyword() : "" );
				optInMsgTbId.setValue(optinSettings.isEnable() && optinSettings.getAutoResponse() != null ? optinSettings.getAutoResponse() : "" );
				getCharCount(optInMsgTbId.getValue(), charCountTbId, false);
				enableOptInChkBox.setChecked(currUser.isConsiderSMSSettings());
				onCheck$enableOptInChkBox();
				welcomeMsgTbId.setValue( optinSettings.isEnableWelcomeMessage() && optinSettings.getWelcomeMessage() != null ? optinSettings.getWelcomeMessage() : "" );
				getCharCount(welcomeMsgTbId.getValue(), welcomecharCountTbId, false);
				enableChwelcomeChkBox.setChecked(optinSettings.isEnableWelcomeMessage());
				onCheck$enableChwelcomeChkBox();
				Byte optinMediumVal = optinSettings.isEnable() ? optinSettings.getOptInMedium() : 0;
				if(optinSettings.isEnable() && optinMediumVal != null) {
					
					enableManualChkBox.setChecked((optinMediumVal & Integer.parseInt(enableManualChkBox.getValue().toString()))>0);
					enableWebFormChkBox.setChecked((optinMediumVal & Integer.parseInt(enableWebFormChkBox.getValue().toString()))>0);
					enablePOSChkBox.setChecked((optinMediumVal & Integer.parseInt(enablePOSChkBox.getValue().toString()))>0);
					
				}//if
				
				if(optinSettings.getListId() != null) {
					
					for (Listitem mItem : selListLbId.getItems()) {
						
						MailingList mailingList = mItem.getValue();
						if(mailingList == null) continue;
						
						if(mailingList.getListId().longValue() == optinSettings.getListId().longValue()) {
							
							mItem.setSelected(true);
							break;
							
						}//if
						
					}//for
					
				}else {
					
					selListLbId.setSelectedIndex(0);
				}
				
			}
			
			if(optOutSettings != null) {
				if(messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				optOutKeywordTbId.setValue(optOutSettings.getKeyword() != null ? optOutSettings.getKeyword() : "");
				unsubConfirmMsgTbId.setValue(optOutSettings.isEnable() &&
						optOutSettings.getAutoResponse() != null ? optOutSettings.getAutoResponse() : "");
				enableUnSubswelcomeChkBox.setChecked(optOutSettings.isEnable());
				onCheck$enableUnSubswelcomeChkBox();
				getCharCount(unsubConfirmMsgTbId.getValue(),optoutcharCountTbId, false);
				
				
			}
			
			if(helpSettings != null) {
				if(messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				helpKeywordTbId.setValue(helpSettings.getKeyword() != null ? helpSettings.getKeyword() : "");
				helpMsgTbId.setValue(helpSettings.getAutoResponse() != null ? helpSettings.getAutoResponse() : "");
				getCharCount(helpMsgTbId.getValue(),helpcharCountTbId, false);
			}
			
			
			senderIdTbId.setValue(messageHeader );
			optoutHeaderLblId.setValue(messageHeader);
			helpHeaderLblId.setValue(messageHeader);
			optinHeaderLblId.setValue(messageHeader);
			welcomeHeaderLblId.setValue(messageHeader);
			
			
			/*
			 * Byte tempval=user.getFooterEditor();
			if(tempval!=null) {
			
			externalEdCbId.setChecked((tempval & Integer.parseInt(externalEdCbId.getValue().toString()))>0);
			plainTextEdCbId.setChecked((tempval & Integer.parseInt(plainTextEdCbId.getValue().toString()))>0);
			plainHtmlEdCbId.setChecked((tempval & Integer.parseInt(plainHtmlEdCbId.getValue().toString()))>0);
			blockEdCbId.setChecked((tempval & Integer.parseInt(blockEdCbId.getValue().toString()))>0);
			
			}
			 */
			
			
			//TODO list selection
			
			
			
			
		}//if
		
		
		
			//make all the things be edditable only for the super user
			userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
			
			if(userRoleSet != null) {
				createNewListAnchId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_AddImport_Contacts_VIEW));
				
			/*	if(userRoleSet.contains(Constants.ROLE_USER_BASIC)) {
					
					createNewListAnchId.setVisible(false);
				}
				*/
				/*if(userRoleSet.contains(Constants.ROLE_USER_BASIC) || userRoleSet.contains(Constants.ROLE_USER_POWER) ) {
					
					
					optInKeywordTbId.setDisabled(true);
					optInMsgTbId.setDisabled(true);
					enableChwelcomeChkBox.setDisabled(true);
					welcomeMsgTbId.setDisabled(true);
					selListLbId.setDisabled(true);
					optOutKeywordTbId.setDisabled(true);
					unsubConfirmMsgTbId.setDisabled(true);
					enableUnSubswelcomeChkBox.setDisabled(true);
					helpKeywordTbId.setDisabled(true);
					helpMsgTbId.setDisabled(true);
					confirmBtn1Id.setDisabled(true);
					senderIdTbId.setDisabled(true);
					
					
					
				}//if
				
				*/
				
			}//if
			

 			
 			
			
			
			
			
		
	}//
	
	
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
				super.onEvent(event);
				if(event.getTarget() instanceof Textbox) {
					//logger.info("event name  ::"+event.getName());
					if(event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(Events.ON_RIGHT_CLICK) ) {
						MessageUtil.setMessage("Right-click and CTRL+V actions have been disabled.", "color:red;");
						return;
						
						
					}
					
					
				}
			
		}
	
	
	public void onClick$continueBtnId() {
		
		
		
		if( !validateFirstDiv()) return;
		
		
		if(smsSettings != null ) {/*
			
			
			SMSSettings optinSettings = null;
	  		SMSSettings optOutSettings = null;
	  		SMSSettings helpSettings = null;
	  		
	  		for (SMSSettings eachSMSSetting : smsSettings) {
	  			
	  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
	  			
	  		}
			
			optOutKeywordTbId.setValue(optOutSettings.getKeyword() != null ? optOutSettings.getOptoutKeyword() :""  );
			unsubConfirmMsgTbId.setValue(smsSettings.getOptoutKeywordResponse() != null ? smsSettings.getOptoutKeywordResponse() :""  );
			enableUnSubswelcomeChkBox.setChecked(smsSettings.isEnableOptOutMsg());
			helpKeywordTbId.setValue(smsSettings.getHelpKeyword() != null ? smsSettings.getHelpKeyword() :""  );
			helpMsgTbId.setValue(smsSettings.getHelpKeywordResponse() != null ? smsSettings.getHelpKeywordResponse() :""  );
			
			
		*/}//if
		
		
		fstDivId.setVisible(false);
		secondDivId.setVisible(true);
		Clients.evalJavaScript("parent.window.scrollTo(0,0)");
		
	}//
	public boolean validateHeader() {
		
		
		String value = senderIdTbId.getValue().trim(); 
		if(value.isEmpty() ) {
			
			MessageUtil.setMessage("Please provide message header. It should not be empty.", "color:red;");
			return false;
		}
		
		if(!Utility.validateSMSHeader(value)) {
			
			MessageUtil.setMessage("Please provide valid message header. \n Special characters are not allowed.", "color:red;");
			return false;
			
		}
		
		boolean isExist = false;
			
		SMSSettings existedHeader = smsSettingsDao.findByHeader(value, shortCodeTbId.getValue().trim());
		if(existedHeader != null) {
			
			if(currUser.getUserId().longValue() != existedHeader.getUserId().getUserId().longValue()  ){
				isExist = true;
			}
		}
		if(isExist) {
			
			MessageUtil.setMessage("Please provide another message header. \n This Header already exists.", "color:red;");
			return false;
			
			
		}
		
		return true;
	}
	public boolean validateFirstDiv() {
		
		//if(!userRoleSet.contains(Constants.ROLE_USER_SUPER)) return true;
		
		//validate short code
		String value = shortCodeTbId.getValue().trim();
		if(value.isEmpty() ) {
			
			MessageUtil.setMessage("Please provide valid shared short-code. It should not be empty.", "color:red;");
			return false;
		}
		
		//validate message header
		
		if(!validateHeader()) return false;
		//validate opt-in keyword
		value = optInKeywordTbId.getValue().trim();
		if(value.isEmpty() ) {
			
			MessageUtil.setMessage("Please provide valid opt-in keyword. It should not be empty.", "color:red;");
			return false;
		}
		
		String[] keywordRuleArr = SMSStatusCodes.keywordRuleMap.get(currUser.getCountryType());
		int minlength = Integer.parseInt(keywordRuleArr[1]);	
		int maxlength =  Integer.parseInt(keywordRuleArr[2]);
		
	   if(minlength != 0 && maxlength != 0 ){
		   if( value.length() < minlength){
			
			MessageUtil.setMessage("Opt-in keyword's length should be minimum of "+minlength+" characters.", "color:red;");
			optInKeywordTbId.setFocus(true);
			return false;
			
		   }
		   
		   if(value.length() > maxlength){
			   
			   MessageUtil.setMessage("Opt-in keyword's length should be maximum of "+maxlength+" characters.", "color:red;");
			   optInKeywordTbId.setFocus(true);
				return false;
				
		   }
		}//else if
		
	   
	   String keywordPattern = keywordRuleArr[0];
	   
		/*
		 * if(! Utility.validateBy(keywordPattern, value)) { MessageUtil.
		 * setMessage("Provide valid opt-in keyword, special characters are not allowed except "
		 * +keywordRuleArr[3], "color:red;"); optInKeywordTbId.setFocus(true); return
		 * false;
		 * 
		 * 
		 * }
		 */
		
		/*if(value.length() < 3 ) {
			
			MessageUtil.setMessage("Opt-in keyword's length can be minimum of 3 characters.", "color:red;");
			return false;
		}
		if(value.length() > 9 ) {
			
			MessageUtil.setMessage("Opt-in keyword's length can be maximum of 9 characters.", "color:red;");
			return false;
		}*/
		
		if(value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOP) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOPALL) ||
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_UNSUBSCRIBE) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_END) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_QUIT) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_CANCEL) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_HELP) ) {
			
			MessageUtil.setMessage("Please provide another opt-in keyword. \n Keywords should not be same as system defined keywords.", "color:red;");
			return false;
			
			
			
		}
		
		boolean isExist = false;
		Object optin = smsSettingsDao.findByKeyword( value, shortCodeTbId.getValue().trim());
		if(optin != null) {
			Long userID =  (optin instanceof SMSSettings ? 
					((SMSSettings)optin).getUserId().getUserId().longValue() : (optin instanceof OrgSMSkeywords ? 
							((OrgSMSkeywords)optin).getUser().getUserId().longValue() : null));
			
			if(userID != null && currUser.getUserId().longValue() != userID.longValue()){
				isExist = true;
			}
		}
		
		if(isExist) {
			
			MessageUtil.setMessage("Please provide another opt-in keyword. \n This keyword already exists.", "color:red;");
			return false;
			
			
		}
		
		
		//validate Double opt-in msg
		if(enableOptInChkBox.isChecked()) { 
			if( optInMsgTbId.getValue().isEmpty() ) {
			
				MessageUtil.setMessage("Please provide valid Double Opt-in message. It can not be empty as you've enabled it.", "color:red;");
				return false;
				
			}
			if(!enableManualChkBox.isChecked() && 
					!enablePOSChkBox.isChecked() && 
					!enableWebFormChkBox.isChecked()) {
				
				MessageUtil.setMessage("Please select at least one Opt-in medium.", "color:red;");
				return false;
				
				
			}//if
		}
		
		//validate list selection
		
		if(selListLbId.getItemCount() == 1) {
			
			MessageUtil.setMessage("You should have a mailing list to be configured. ", "color:red;");
			return false;
			
			
		}
		
		if(selListLbId.getSelectedIndex() <= 0 ) {
			
			MessageUtil.setMessage("Please select a mailing list to be configured. ", "color:red;");
			return false;
		}
		
		if(enableChwelcomeChkBox.isChecked()) {
			
			if(welcomeMsgTbId.getValue().isEmpty()) {
				
				MessageUtil.setMessage("Please provide welcome message content as you have enabled it. ", "color:red;");
				return false;
				
				
			}
			
		}//if
		
		if(optInKeywordTbId.getValue().equalsIgnoreCase(helpKeywordTbId.getValue())) {
			
			MessageUtil.setMessage("Please provide different keywords for Help and Opt-in.", "color:red;");
			return false;
			
			
		}
		if(optInKeywordTbId.getValue().equalsIgnoreCase(optOutKeywordTbId.getValue())) {
			
			MessageUtil.setMessage("Please provide different keywords for Opt-out and Opt-in.", "color:red;");
			return false;
			
			
		}
		
		return true;
		
	}
	
	public boolean validateSecondDiv() {
		
		//validate OPt-out keyword
		//if(!userRoleSet.contains(Constants.ROLE_USER_SUPER)) return true;
		String value = optOutKeywordTbId.getValue().trim();
		if(value.isEmpty()) {
			
			MessageUtil.setMessage("Please provide valid opt-out keyword. It should not be empty.", "color:red;");
			return false;
		}
		String[] keywordRuleArr = SMSStatusCodes.keywordRuleMap.get(currUser.getCountryType());
		int minlength = Integer.parseInt(keywordRuleArr[1]);	
		int maxlength =  Integer.parseInt(keywordRuleArr[2]);
		
	   if(minlength != 0 && maxlength != 0 ){
		   if( value.length() < minlength){
			
			MessageUtil.setMessage("Opt-out keyword's length should be minimum of "+minlength+" characters.", "color:red;");
			optOutKeywordTbId.setFocus(true);
			return false;
			
		   }
		   
		   if(value.length() > maxlength){
			   
			   MessageUtil.setMessage("Opt-out keyword's  length should be maximum of "+maxlength+" characters.", "color:red;");
			   optOutKeywordTbId.setFocus(true);
				return false;
				
		   }
		}//else if
		
	   
	   String keywordPattern = keywordRuleArr[0];
	   
		/*
		 * if(! Utility.validateBy(keywordPattern, value)) { MessageUtil.
		 * setMessage("Provide valid opt-out keyword, special characters are not allowed except "
		 * +keywordRuleArr[3], "color:red;"); optOutKeywordTbId.setFocus(true); return
		 * false;
		 * 
		 * 
		 * }
		 */
		
		/*if(value.length() < 3) {
			
			MessageUtil.setMessage("Opt-out keyword's length can be minimum of 3 characters.", "color:red;");
			return false;
		}
		
		if(value.length() > 9) {
			
			MessageUtil.setMessage("Opt-out keyword's length can be maximum of 9 characters.", "color:red;");
			return false;
		}*/
		
		if(value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOP) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOPALL) ||
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_UNSUBSCRIBE) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_END) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_QUIT) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_CANCEL) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_HELP) ) {
			
			MessageUtil.setMessage("Please provide another opt-out keyword. \n Keywords should not be same as system defined keywords.", "color:red;");
			return false;
			
			
			
		}
		
		
		
		boolean isExist = false;
		Object optout = smsSettingsDao.findByKeyword(value, shortCodeTbId.getValue().trim());
		if(optout != null) {
			Long userID =  (optout instanceof SMSSettings ? 
					((SMSSettings)optout).getUserId().getUserId().longValue() : (optout instanceof OrgSMSkeywords ? 
							((OrgSMSkeywords)optout).getUser().getUserId().longValue() : null));
			
			if(userID != null && currUser.getUserId().longValue() != userID.longValue()){
				isExist = true;
			}
			
		}
		
		if(isExist) {
			
			MessageUtil.setMessage("Please provide another opt-out keyword. \n This keyword already exists.", "color:red;");
			return false;
			
			
		}
		
		if(enableUnSubswelcomeChkBox.isChecked()) {
			
			if(unsubConfirmMsgTbId.getValue().isEmpty()) {
				
				MessageUtil.setMessage("Please provide opt-out message content as you have enabled it. ", "color:red;");
				return false;
				
				
			}//if
			
		}//if
		
		//validate help keyword
		value = helpKeywordTbId.getValue().trim();
		if(value.isEmpty() ) {
			
			MessageUtil.setMessage("Please provide valid Help keyword. It should not be empty.", "color:red;");
			return false;
		}
		
		
		 if(minlength != 0 && maxlength != 0 ){
		   if( value.length() < minlength){
			
			MessageUtil.setMessage("Help keyword's length should be minimum of "+minlength+" characters.", "color:red;");
			helpKeywordTbId.setFocus(true);
			return false;
			
		   }
		   
		   if(value.length() > maxlength){
			   
			   MessageUtil.setMessage("Help keyword's length should be maximum of "+maxlength+" characters.", "color:red;");
			   helpKeywordTbId.setFocus(true);
				return false;
				
		   }
		}//else if
		 if(! Utility.validateBy(keywordPattern, value)) {
			  MessageUtil.setMessage("Provide valid Help keyword, special characters are not allowed except "+keywordRuleArr[3], "color:red;");
			  helpKeywordTbId.setFocus(true);
				return false;
			  
			  
		  }
		/*if(value.length() < 3 ) {
			
			MessageUtil.setMessage("Help keyword's length can be minimum of 3 characters.", "color:red;");
			return false;
		}
		
		if(value.length() > 9 ) {
			
			MessageUtil.setMessage("Help keyword's length can be maximum of 9 characters.", "color:red;");
			return false;
		}*/
		
		if(value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOP) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_STOPALL) ||
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_UNSUBSCRIBE) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_END) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_QUIT) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_CANCEL) || 
				value.equalsIgnoreCase(Constants.SMS_KEYWORD_HELP) ) {
			
			MessageUtil.setMessage("Please provide another Help keyword. \n Keywords should not be same as system defined keywords.", "color:red;");
			return false;
			
			
			
		}
		
		
		
		
		isExist = false;
		Object help = smsSettingsDao.findByKeyword(value, shortCodeTbId.getValue().trim());
		if(help != null) {
			Long userID =  (help instanceof SMSSettings ? 
					((SMSSettings)help).getUserId().getUserId().longValue() : (help instanceof OrgSMSkeywords ? 
							((OrgSMSkeywords)help).getUser().getUserId().longValue() : null));
			
			if(userID != null && currUser.getUserId().longValue() != userID.longValue()){
				isExist = true;
			}
			
		}
		
		if(isExist) {
			
			MessageUtil.setMessage("Please provide another Help keyword. \n This keyword already exists.", "color:red;");
			return false;
			
			
		}
		
		//validate help msg
		if(helpMsgTbId.getValue().isEmpty() ) {
			
			MessageUtil.setMessage("Please provide valid Help message. It should not be empty.", "color:red;");
			return false;
		}
	
		
		if(helpKeywordTbId.getValue().equalsIgnoreCase(optOutKeywordTbId.getValue())) {
			
			MessageUtil.setMessage("Please provide different keywords for Help and Opt-out.", "color:red;");
			return false;
			
			
		}
		
		
		
		return true;
	}
	
	
	
	public void onClick$checkTemplateAnchId() {
		
		optInMsgTemplateHtmlId.setContent(OptinTemplateStr);
		optInMsgTemplateDivId.setVisible(!optInMsgTemplateDivId.isVisible());
		
	}//onClick$checkTemplateAnchId
	
	public void onClick$checkWelcomeTemplateAnchId() {
		
		welcomeMsgTemplateHtmlId.setContent(welcomeTemplateStr);
		welcomeMsgTemplateDivId.setVisible(!welcomeMsgTemplateDivId.isVisible());
	}//onClick$checkWelcomeTemplateAnchId
	
	public void onClick$createNewListAnchId() {
		
		Redirect.goTo("contact/upload");
		
	}//onClick$createNewListAnchId
	
	public void onClick$checkOptoutTemplateAnchId() {
		
		optoutMsgTemplateHtmlId.setContent(optoutTemplateStr);
		optOutMsgTemplateDivId.setVisible(!optOutMsgTemplateDivId.isVisible());
		
		
	}//onClick$checkOptoutTemplateAnchId
	
	public void onClick$checkhelpTemplateAnchId() {
		
		
		helpMsgTemplateHtmlId.setContent(helpTemplateStr);
		helpMsgTemplateDivId.setVisible(!helpMsgTemplateDivId.isVisible());
		
		
	}//onClick$checkhelpTemplateAnchId
	
	
	public void onClick$confirmBtn1Id() {
		
		if(!validateSecondDiv()) {
			
			return;
		}//if
		try {
			int confirm = Messagebox.show("Are you sure you want to save the settings?","Save Settings?",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1) {
				
				return ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		SMSSettings newSettings = null;
		SMSSettings optinSettings = null;
		SMSSettings optOutSettings = null;
		SMSSettings helpSettings = null;
		SMSSettings newOptinSetting = null;
		SMSSettings newOptoutSettings = null;
		SMSSettings newHelpSettings = null;
		
		List<SMSSettings> saveList = new ArrayList<SMSSettings>();
		List<SMSSettings> newSaveList = new ArrayList<SMSSettings>();
		
		if(smsSettings != null) {
			
	  		for (SMSSettings eachSMSSetting : smsSettings) {
	  			
	  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
	  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
	  			
	  		}
		}
		if(smsSettings == null || optinSettings == null || optOutSettings == null || helpSettings == null) {
			
			newSettings = new SMSSettings();
			newSettings.setUserId(currUser);
			newSettings.setOrgId(currUser.getUserOrganization().getUserOrgId());
			newSettings.setShortCode(shortCodeTbId.getText());
			newSettings.setSenderId(shortCodeTbId.getText());
			newSettings.setMessageHeader(senderIdTbId.getValue());
			newSettings.setCreatedBy(currUser.getUserId());
			newSettings.setModifiedBy(currUser.getUserId());
			newSettings.setModifiedDate(Calendar.getInstance());
			
		}//if
		
		boolean enable = enableOptInChkBox.isChecked();
		Integer optinMediumval=0;
		if(enable) {
			
			if(enableManualChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enableManualChkBox.getValue().toString());//,plainTextEdCbId,plainHtmlEdCbId,blockEdCbId)
			} 
			if(enableWebFormChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enableWebFormChkBox.getValue().toString());
			} 
			if(enablePOSChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enablePOSChkBox.getValue().toString());
			}
			
		}		
		Byte optinValue = optinMediumval > 0 ? optinMediumval.byteValue() : null;
		currUser.setConsiderSMSSettings(enable);
			if(optinSettings != null) {
				
				optinSettings.setKeyword(optInKeywordTbId.getValue());
				optinSettings.setEnable(enable);
				
				
				if(enable)optinSettings.setAutoResponse(optInMsgTbId.getValue());
				else optinSettings.setAutoResponse(null);
				
				optinSettings.setOptInMedium(optinValue );
				optinSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				optinSettings.setSenderId(shortCodeTbId.getText());
				
				optinSettings.setModifiedBy(currUser.getUserId());
				optinSettings.setModifiedDate(Calendar.getInstance());
				
				optinSettings.setMessageHeader(senderIdTbId.getValue());
				
				
			}else{
				newOptinSetting = newSettings.getCopy();
				newOptinSetting.setKeyword(optInKeywordTbId.getValue());
				newOptinSetting.setEnable(enable);
				if(enable)newOptinSetting.setAutoResponse(optInMsgTbId.getValue());
				else newOptinSetting.setAutoResponse(null);
				
				newOptinSetting.setOptInMedium(optinValue);
				newOptinSetting.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				newOptinSetting.setSenderId(shortCodeTbId.getText());
				newOptinSetting.setCreatedBy(currUser.getUserId());
				newOptinSetting.setModifiedBy(currUser.getUserId());
				newOptinSetting.setModifiedDate(Calendar.getInstance());
			}
			
		/*}else{
			
			if(optinSettings != null) {
				
				optinSettings.setKeyword(optInKeywordTbId.getValue());
				optinSettings.setEnable(false);
				optinSettings.setAutoResponse(null);
				optinSettings.setOptInMedium((byte)0 );
				optinSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				optinSettings.setSenderId(shortCodeTbId.getText());
				
			}else{
				newOptinSetting = newSettings;
				newOptinSetting.setKeyword(optInKeywordTbId.getValue());
				newOptinSetting.setEnable(false);
				newOptinSetting.setAutoResponse(null);
				newOptinSetting.setOptInMedium((byte)0 );
				
				newOptinSetting.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				newOptinSetting.setSenderId(shortCodeTbId.getText());
			}
			
			
		}
		*/
		boolean enableWelcomeMSg = enableChwelcomeChkBox.isChecked();
		Long listId = ((MailingList)selListLbId.getSelectedItem().getValue()).getListId();
		if(optinSettings != null){
			optinSettings.setEnableWelcomeMessage(enableWelcomeMSg);
			if(enableWelcomeMSg) optinSettings.setWelcomeMessage(welcomeMsgTbId.getValue());
			else optinSettings.setWelcomeMessage(null);
		}
		else{
			newOptinSetting.setEnableWelcomeMessage(enableWelcomeMSg);
			if(enableWelcomeMSg) newOptinSetting.setWelcomeMessage(welcomeMsgTbId.getValue());
			else newOptinSetting.setWelcomeMessage(null);
			
		}
			
		
		if(optinSettings != null ) optinSettings.setListId(listId);
		else 	newOptinSetting.setListId(listId);
		
		if(optinSettings != null) saveList.add(optinSettings);
		else newSaveList.add(newOptinSetting);
		//TODO for no check for optin
		
		/*if(smsSettings.isEnableOptInMessage()) {
			int optinMediumval=0;
			if(enableManualChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enableManualChkBox.getValue().toString());//,plainTextEdCbId,plainHtmlEdCbId,blockEdCbId)
			} 
			if(enableWebFormChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enableWebFormChkBox.getValue().toString());
			} 
			if(enablePOSChkBox.isChecked()) {
				optinMediumval=optinMediumval+Integer.parseInt(enablePOSChkBox.getValue().toString());
			}
			smsSettings.setOptInMedium((byte)optinMediumval );
		
		}*/
		//logger.debug("optinMediumval is--->"+optinMediumval);
		
		
		boolean enableoptout = enableUnSubswelcomeChkBox.isChecked();
		if(optOutSettings != null) {
			
			optOutSettings.setKeyword(optOutKeywordTbId.getValue());
			optOutSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
			optOutSettings.setSenderId(shortCodeTbId.getText());
			optOutSettings.setEnable(enableoptout);
			if(enableoptout) optOutSettings.setAutoResponse(unsubConfirmMsgTbId.getValue());
			else optOutSettings.setAutoResponse(null);
			optOutSettings.setModifiedBy(currUser.getUserId());
			optOutSettings.setModifiedDate(Calendar.getInstance());
			
			optOutSettings.setMessageHeader(senderIdTbId.getValue());
			
		}else{
			
			newOptoutSettings = newSettings.getCopy();
			newOptoutSettings.setKeyword(optOutKeywordTbId.getValue());
			newOptoutSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
			newOptoutSettings.setSenderId(shortCodeTbId.getText());
			newOptoutSettings.setEnable(enableoptout);
			if(enableoptout) newOptoutSettings.setAutoResponse(unsubConfirmMsgTbId.getValue());
			else newOptoutSettings.setAutoResponse(null);
			
			newOptoutSettings.setCreatedBy(currUser.getUserId());
			newOptoutSettings.setModifiedBy(currUser.getUserId());
			newOptoutSettings.setModifiedDate(Calendar.getInstance());
		}
		
		if(optOutSettings != null) saveList.add(optOutSettings);
		else newSaveList.add(newOptoutSettings);
		
		if(helpSettings != null) {
			
			helpSettings.setKeyword(helpKeywordTbId.getValue());
			helpSettings.setAutoResponse(helpMsgTbId.getValue());
			helpSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP);
			helpSettings.setSenderId(shortCodeTbId.getValue());
			
			
			helpSettings.setModifiedBy(currUser.getUserId());
			helpSettings.setModifiedDate(Calendar.getInstance());
			
			helpSettings.setMessageHeader(senderIdTbId.getValue());
			
		}else{
			
			newHelpSettings = newSettings.getCopy();
			newHelpSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP);
			newHelpSettings.setSenderId(shortCodeTbId.getValue());
			newHelpSettings.setKeyword(helpKeywordTbId.getValue());
			newHelpSettings.setAutoResponse(helpMsgTbId.getValue());
			
			newHelpSettings.setCreatedBy(currUser.getUserId());
			newHelpSettings.setModifiedBy(currUser.getUserId());
			newHelpSettings.setModifiedDate(Calendar.getInstance());
		}
		
		if(helpSettings != null) saveList.add(helpSettings);
		else newSaveList.add(newHelpSettings);
		
		if(saveList.size() > 0) smsSettingsDaoForDML.saveByCollection(saveList);
		if(newSaveList.size() > 0) smsSettingsDaoForDML.saveByCollection(newSaveList);
		
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		//usersDao.saveOrUpdate(currUser);
		usersDaoForDML.saveOrUpdate(currUser);
		
		GetUser.getUserObj(true);
		
		MessageUtil.setMessage("SMS settings saved successfully.", "color:green;");
		
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.SMS_SMS_SETTINGS);
		
	}
	
	public String validateLinkContent(String enteredURL) {
		
		if(enteredURL.equals("") || enteredURL.equalsIgnoreCase("http://") ||
				(enteredURL.equals("Use Url Shortener".trim())) ) {
			MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
			//logger.error("Exception : Link URl field is empty .");
			return null;
		}
		if( !( enteredURL.startsWith("http://") || enteredURL.startsWith("https://")) ) {   //APP-3490
			
			enteredURL = "http://"+enteredURL;
			
		}
		
		String mappingUrl = GetUser.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
		String insetedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
		
		List<StringBuffer> retList =  Utility.getSixDigitURLCode(mappingUrl);
		UrlShortCodeMapping urlShortCodeMapping = null;
		if(retList != null && retList.size() > 0) {
			
			//check whether any returned  shordcode exists in DB
			
			for (StringBuffer shortCode : retList) {
				
				urlShortCodeMapping = new UrlShortCodeMapping("U"+shortCode, enteredURL, "", GetUser.getUserObj().getUserId());
				
				try {
					
					urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);
					
					insetedUrl += "U"+shortCode;
					break;
				}catch (DataIntegrityViolationException e) {
					// TODO: handle exception
					//logger.error("given Short code is already exist in DB.....",e);
					continue;
					
				}catch (ConstraintViolationException e) {
					// TODO: handle exception
					//logger.error("given Short code is already exist in DB.....",e);
					continue;
				}
				
				
				
			}//for
			
		}//if
		
		return insetedUrl;
		
	}
	
	
	public void onClick$insertLinkBtnId() {
		
		//logger.debug("---URL need to be Short Coded---");
		
		String enteredURL = linkURLTxtBoxId.getValue().trim();
		
		 String insertedUrl = validateLinkContent(enteredURL);
		
		//should insert the link short code in the message content
		
		if(insertedUrl == null) return;
		
		insertText(insertedUrl, optInMsgTbId, charCountTbId);
		
		linkURLTxtBoxId.setValue("Use Url Shortener");

		
	}//onClick$insertLinkBtnId$insertLinkWinId

	
	/**
	 * updates the current cursor position
	 */

	public void onChange$caretPosTB(){
		//logger.debug("---just entered----");
	}


	public void onBlur$caretPosTB() {
		
		//logger.debug("-----just entered: onBlur event----");
		
	}
	
	public void insertText(String  value, Textbox targetTB, Textbox targetCountTB){
		logger.info("insertText");
		logger.info("targetTB "+targetTB.getId());
		//String value = item.getValue();
		String cp="";
		if(targetTB.getId().equalsIgnoreCase("welcomeMsgTbId"))cp = caretPosTB1.getValue();
		else if(targetTB.getId().equalsIgnoreCase("optInMsgTbId"))cp = caretPosTB2.getValue();
		else if(targetTB.getId().equalsIgnoreCase("unsubConfirmMsgTbId"))cp = caretPosTB3.getValue();
		else if(targetTB.getId().equalsIgnoreCase("helpMsgTbId"))cp = caretPosTB4.getValue();
		//String cp = caretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
		cp = "0";
		}
		try{
		int caretPos = Integer.parseInt(cp);
		if (caretPos != -1) {
			String currentValue = targetTB.getValue();
			String newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
			
			targetTB.setValue(newValue);
			
			int charCount = targetTB.getValue().length();
			//logger.info("the length is====>"+charCount);
			if(charCount>160) {
			int msgcount = charCount/160;
			targetCountTB.setValue(""+charCount+" / "+(msgcount+1));
			
			}//if
			else {
				targetCountTB.setValue(""+charCount+" / "+1);
			}//else
			
			//sessionScope.put("messageContent", SMSMsgTbId.getValue());
			//personalizationTagsWinId.setVisible(false);
			targetTB.focus();
			
		}
		}catch(Exception e) {
		logger.error("Exception ::" , e);
		}
		}
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$optInMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue(), charCountTbId, true);
			
			
		}catch (Exception e) {
			//logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$helpMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue(), helpcharCountTbId, true);
			
			
		}catch (Exception e) {
			//logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	
	public void onChanging$unsubConfirmMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue(), optoutcharCountTbId, true);
			
			
		}catch (Exception e) {
			//logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$welcomeMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue(), welcomecharCountTbId, true);
			
			
		}catch (Exception e) {
			//logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)

	public void onBlur$senderIdTbId() {
		
		
		if(!validateHeader() ) return;
		getCharCount(optInMsgTbId.getValue(), charCountTbId, true );
		getCharCount(helpMsgTbId.getValue(), helpcharCountTbId, true);
		getCharCount(unsubConfirmMsgTbId.getValue(), optoutcharCountTbId, true);
		getCharCount(welcomeMsgTbId.getValue(), welcomecharCountTbId, true);
		
		String value = senderIdTbId.getValue().trim(); 
		optinHeaderLblId.setValue(value);
		optoutHeaderLblId.setValue(value);
		helpHeaderLblId.setValue(value);
		welcomeHeaderLblId.setValue(value);
		
	}
	
	/**
	 * caluculates the actual character count of 
	 * the SMS campaign and sets this value to the SMS msg related  textbox
	 * @param msgContent specifies the actual msg content.
	 */
	public void getCharCount(String msgContent, Textbox targetTb, boolean isnew) {
		try {
				int charCount =( msgContent != null && !msgContent.isEmpty() ? msgContent.length() : 0);
				//logger.info("the length is====>"+charCount);
				String messageHeader = Constants.STRING_NILL;
				if(smsSettings != null) {
					
					SMSSettings optinSettings = null;
			  		SMSSettings optOutSettings = null;
			  		SMSSettings helpSettings = null;
			  		
			  		for (SMSSettings eachSMSSetting : smsSettings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
			  			
			  		}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				}
				if(messageHeader != null) msgContent=msgContent+" ";
				charCount=msgContent.length();
				/*if(!isnew && smsSettings != null) {
					
					SMSSettings optinSettings = null;
			  		SMSSettings optOutSettings = null;
			  		SMSSettings helpSettings = null;
			  		
			  		String messageHeader = Constants.STRING_NILL;
			  		for (SMSSettings eachSMSSetting : smsSettings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
			  			
			  		}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();*/
				if(!isnew && smsSettings != null){
					charCount = charCount + (messageHeader != null ? messageHeader.length() : 0);//TODO set perfection
					
				}else {
					
					charCount = charCount + (senderIdTbId.getValue().trim().isEmpty() ? 0 : senderIdTbId.getValue().trim().length());
					
				}
				
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					targetTb.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					targetTb.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				//logger.debug("Exception while getting the character count",e);
		}//catch
		
	}//getCharCount
	
	
	
	public void onClick$linkURLTxtBoxId() {
		
		if(linkURLTxtBoxId.getValue().trim().equals("Use Url Shortener")) {
			
			linkURLTxtBoxId.setValue("");
			
		}
		
	}
	
public void onClick$welcomelinkURLTxtBoxId() {
		
		if(welcomelinkURLTxtBoxId.getValue().trim().equals("Use Url Shortener")) {
			
			welcomelinkURLTxtBoxId.setValue("");
			
		}
		
	}
public void onClick$optoutlinkURLTxtBoxId() {
	
	if(optoutlinkURLTxtBoxId.getValue().trim().equals("Use Url Shortener")) {
		
		optoutlinkURLTxtBoxId.setValue("");
		
	}
	
}
public void onClick$helplinkURLTxtBoxId() {
	
	if(helplinkURLTxtBoxId.getValue().trim().equals("Use Url Shortener")) {
		
		helplinkURLTxtBoxId.setValue("");
		
	}
	
}
public void onClick$welcomeinsertLinkBtnId() {
	
	//logger.debug("---URL need to be Short Coded---");
	
	String enteredURL = welcomelinkURLTxtBoxId.getValue().trim();
	
	 String insertedUrl = validateLinkContent(enteredURL);
	
	//should insert the link short code in the message content
	
	if(insertedUrl == null) return;
	
	insertText(insertedUrl, welcomeMsgTbId, welcomecharCountTbId);
	welcomelinkURLTxtBoxId.setValue("Use Url Shortener");
	
}//onClick$insertLinkBtnId$insertLinkWinId
public void onClick$optoutinsertLinkBtnId() {
	
	//logger.debug("---URL need to be Short Coded---");
	
	String enteredURL = optoutlinkURLTxtBoxId.getValue().trim();
	
	 String insertedUrl = validateLinkContent(enteredURL);
	
	//should insert the link short code in the message content
	
	if(insertedUrl == null) return;
	
	insertText(insertedUrl, unsubConfirmMsgTbId, optoutcharCountTbId);
	optoutlinkURLTxtBoxId.setValue("Use Url Shortener");
	
}//onClick$insertLinkBtnId$insertLinkWinId

public void onClick$helpinsertLinkBtnId() {
	
	//logger.debug("---URL need to be Short Coded---");
	
	String enteredURL = helplinkURLTxtBoxId.getValue().trim();
	
	 String insertedUrl = validateLinkContent(enteredURL);
	
	//should insert the link short code in the message content
	
	if(insertedUrl == null) return;
	
	insertText(insertedUrl, helpMsgTbId, helpcharCountTbId);
	helplinkURLTxtBoxId.setValue("Use Url Shortener");
	
}//onClick$insertLinkBtnId$insertLinkWinId

public void onClick$backBtnId() {
	
	fstDivId.setVisible(true);
	secondDivId.setVisible(false);
	
}
	
private Popup optInkeywordHelp,headerHelp, helpkeywordHelp, optOutkeywordHelp;
public void onFocus$optInKeywordTbId() {
	
	//cSubTb.setPopup(help);
	optInkeywordHelp.open(optInKeywordTbId, "end_after");
	
}
public void onFocus$optOutKeywordTbId() {
	
	//cSubTb.setPopup(help);
	optOutkeywordHelp.open(optOutKeywordTbId, "end_after");
	
}
public void onFocus$helpKeywordTbId() {
	
	//cSubTb.setPopup(help);
	helpkeywordHelp.open(helpKeywordTbId, "end_after");
	
}

public void onFocus$senderIdTbId() {
	
	//cSubTb.setPopup(help);
	headerHelp.open(senderIdTbId, "end_after");
	
}


public void onCheck$enableUnSubswelcomeChkBox() {
	
	unsubscribDivId.setVisible(enableUnSubswelcomeChkBox.isChecked());
	
}



public void onCheck$enableOptInChkBox() {
	
	enableOptinDivId.setVisible(enableOptInChkBox.isChecked());
	
}


public void onCheck$enableChwelcomeChkBox() {
	
	enableWelcomeDivId.setVisible(enableChwelcomeChkBox.isChecked());
	
}

// added for onBlur()events

//optInKeywordTbId,optOutKeywordTbId,helpKeywordTbId;

	public void onBlur$optInKeywordTbId(){
		
		String optinKeywordStr = optInKeywordTbId.getText().trim().isEmpty() ? Constants.STRING_NILL :  optInKeywordTbId.getText().trim();
		
		if(!optinKeywordStr.isEmpty() || optinKeywordStr != null)optInKeywordTbId.setText(optinKeywordStr.toUpperCase());
		
		
	}
	public void onBlur$optOutKeywordTbId(){
		
		String optoutKeywordStr = optOutKeywordTbId.getText().trim().isEmpty() ? Constants.STRING_NILL :  optOutKeywordTbId.getText().trim();
		
		if(!optoutKeywordStr.isEmpty() || optoutKeywordStr != null)optOutKeywordTbId.setText(optoutKeywordStr.toUpperCase());
		
		
	}
	public void onBlur$helpKeywordTbId(){
		
		String helpKeywordStr = helpKeywordTbId.getText().trim().isEmpty() ? Constants.STRING_NILL :  helpKeywordTbId.getText().trim();
		
		if(!helpKeywordStr.isEmpty() || helpKeywordStr != null )helpKeywordTbId.setText(helpKeywordStr.toUpperCase());
		
		
	}



}
