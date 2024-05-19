package org.mq.marketer.campaign.controller.sms;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SMSSettingsDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class OptinSMSSettingsController extends GenericForwardComposer implements EventListener{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users currentUser;
	private Textbox optInKeywordTxtId, keywordRecvNumTxtId, optinMsdCalNumTxtId,optoutTxtId 
					,optinSenderIdTxtId , transSenderIdTxtBxId  ,keywordLimiTxtId ,msgRecvNumTxtId,toEmailTxtBxId ;
	private Combobox approveTempCmbBoxId ;
	private Window previewIframeWin , createTransTempWinId, createNewOptinMsgWinId;
	private Iframe previewIframeWin$iframeId; 
	private Div optinMsdCalNumDivId,keywordRecvNumDivId ,optinDivId ,optoutDivId, enableOptinDivId;
	private Groupbox optinGBId,transGBId ,keywordsGBId;
	private Textbox createTransTempWinId$transTempNameTxtBxId,createTransTempWinId$transTempContentTxtBxId,createTransTempWinId$transCaretPosTB,
	createTransTempWinId$transTempCharCountTxtBxId,optInMsgTbId,charCountTbId ,createNewOptinMsgWinId$optinCaretPosTB ,createNewOptinMsgWinId$optinContentTxtBxId,
				createNewOptinMsgWinId$optinCharCountTxtBxId,optinListTxtId;
	private Listbox optinListLBId;
	
	private Checkbox enableOptInChkBox,enableManualChkBox ,enableWebFormChkBox ,enablePOSChkBox;
	private A checkTemplateAnchId;
	private TransactionalTemplatesDao  transactionalTemplatesDao;
	private TransactionalTemplatesDaoForDML  transactionalTemplatesDaoForDML;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private SMSSettingsDao smsSettingsDao;
	private SMSSettingsDaoForDML smsSettingsDaoForDML;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	
	private UserSMSGatewayDao userSMSGatewayDao;
	
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	
	private final String SMSSETTINGS ="SMSSettings";
	public OptinSMSSettingsController(){
		
		String style="font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Opt-in Program", "", style, true);
		
		currentUser = GetUser.getUserObj();
		transactionalTemplatesDao = (TransactionalTemplatesDao)SpringUtil.getBean("transactionalTemplatesDao");
		transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML)SpringUtil.getBean("transactionalTemplatesDaoForDML");
		userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		smsSettingsDao= (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		smsSettingsDaoForDML= (SMSSettingsDaoForDML)SpringUtil.getBean("smsSettingsDaoForDML");
		emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		userSMSGatewayDao = (UserSMSGatewayDao)SpringUtil.getBean("userSMSGatewayDao");
	}
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if(event.getTarget() instanceof Textbox) {
			
			if(event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(Events.ON_RIGHT_CLICK) ) {
				MessageUtil.setMessage("Right-click and CTRL+V actions have been disabled.", "color:red;");
				return;
				
				
			}
			
			
		}	
		
		
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		optInMsgTbId.setCtrlKeys("^v");
		optInMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
		optInMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		
		defaultSettings();
		
	}

	public void defaultSettings(){
		
		try {
			List<TransactionalTemplates>  templateList = null;
			// added for opt-in settings
			boolean isOptinSenderIDVisible= false;
			boolean isdisabled = false;
				
				List<UserSMSSenderId> optinSenderIdList = userSMSSenderIdDao.findSenderIdBySMSType(currentUser.getUserId(), Constants.SMS_SENDING_TYPE_OPTIN);
				if(optinSenderIdList != null) {
					//optinGBId.setVisible(true);
					UserSMSSenderId userSMSSenderId = (UserSMSSenderId)optinSenderIdList.get(0);
					String senderID = userSMSSenderId != null ? userSMSSenderId.getSenderId() : null;
					optinSenderIdTxtId.setText(senderID != null ? senderID : Constants.STRING_NILL);
					isOptinSenderIDVisible = true;
				}else{
					isOptinSenderIDVisible =false;
				}
				
				SMSSettings optinSMSSettings = smsSettingsDao.findByUserOrg(currentUser.getUserOrganization().getUserOrgId(),OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				enableOptInChkBox.setAttribute(SMSSETTINGS, optinSMSSettings);
				
				optinDivId.setVisible(optinSMSSettings != null);
				boolean isOptinVisible= false;
				
				if(optinSMSSettings != null){
					
					optInKeywordTxtId.setText(optinSMSSettings.getKeyword() != null ? optinSMSSettings.getKeyword()  : Constants.STRING_NILL );
					
					optinMsdCalNumDivId.setVisible(optinSMSSettings.getOptinMissedCalNumber().trim() != null);
					keywordRecvNumDivId.setVisible(optinSMSSettings.getShortCode().trim() != null);
					if(optinSMSSettings.getOptinMissedCalNumber().trim() != null || optinSMSSettings.getOptinMissedCalNumber().trim().isEmpty()){
						
						optinMsdCalNumDivId.setVisible(true);
						
						optinMsdCalNumTxtId.setText(optinSMSSettings.getOptinMissedCalNumber() != null ?"+"+optinSMSSettings.getOptinMissedCalNumber()  : Constants.STRING_NILL );
						
						
					}
					
					if(optinSMSSettings.getShortCode().trim() != null || optinSMSSettings.getShortCode().trim().isEmpty()){
						
						keywordRecvNumDivId.setVisible(true);
						
						
						keywordRecvNumTxtId.setText(optinSMSSettings.getShortCode() != null ? "+"+optinSMSSettings.getShortCode() :Constants.STRING_NILL);
						
					}
					
					Long listId = optinSMSSettings.getListId();
					
					MailingList mlist = mailingListDao.findById(listId);
					
					optinListTxtId.setText(mlist.getListName());
					
					/*
					Listitem item = new Listitem(mlist.getListName());
					item.setSelected(true);
					
					item.setParent(optinListLBId);*/
					
					
					
					isOptinVisible = true;
					
					
					//for invite optin program
						
					String autoOptinMsg = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_SMS_OPTIN_MESSAGE);
					autoOptinMsg = autoOptinMsg.replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD, optinSMSSettings.getKeyword() == null ? Constants.STRING_NILL : optinSMSSettings.getKeyword()).
							replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER, optinSMSSettings.getShortCode() == null ? Constants.STRING_NILL : optinSMSSettings.getShortCode()).
							replace(OCConstants.SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER, optinSMSSettings.getOptinMissedCalNumber() == null ? Constants.STRING_NILL : optinSMSSettings.getOptinMissedCalNumber());
					
					
					String optinContent = optinSMSSettings.getAutoResponse() != null ? 
							optinSMSSettings.getAutoResponse() : autoOptinMsg;
					
					optInMsgTbId.setText(optinContent);
					getCharCount(optinContent);
					Byte optinMediumVal = null;
					Users owner = optinSMSSettings.getUserId();
					if(currentUser.getUserId().longValue() == owner.getUserId().longValue()) {
						
						optinMediumVal = (owner.isConsiderSMSSettings() && optinSMSSettings.isEnable())
											? optinSMSSettings.getOptInMedium() : null;
						
					}else{
						optinMediumVal = currentUser.isConsiderSMSSettings() ? currentUser.getOptInMedium() : null;
			
						
					}
					 
					if(optinMediumVal != null) {
						
						enableManualChkBox.setChecked((optinMediumVal & Integer.parseInt(enableManualChkBox.getValue().toString()))>0);
						enableWebFormChkBox.setChecked((optinMediumVal & Integer.parseInt(enableWebFormChkBox.getValue().toString()))>0);
						enablePOSChkBox.setChecked((optinMediumVal & Integer.parseInt(enablePOSChkBox.getValue().toString()))>0);
						
					}//if
					
					isdisabled =(currentUser.getUserId().longValue() != owner.getUserId().longValue());
					
				}
				
				
			/*	enableManualChkBox.setDisabled(isdisabled);
				enablePOSChkBox.setDisabled(isdisabled);
				enableWebFormChkBox.setDisabled(isdisabled);
				checkTemplateAnchId.setDisabled(isdisabled);
				*/
				checkTemplateAnchId.setDisabled(isdisabled);
				SMSSettings optoutSMSSettings = smsSettingsDao.findByUserOrg(currentUser.getUserOrganization().getUserOrgId(),OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
				optoutDivId.setVisible(optoutSMSSettings != null);
				boolean idOptoutVisible= false;
				if(optoutSMSSettings != null){
					
					optoutTxtId.setValue(optoutSMSSettings.getKeyword()!= null ? optoutSMSSettings.getKeyword() : Constants.STRING_NILL);
					idOptoutVisible = true;
					
				}
			
			
				
				
				
			// added for Transactional   settings
			String transSenderID = null;
			
			//if(currentUser != null){
				
				List<UserSMSSenderId> transSenderIdList = userSMSSenderIdDao.findSenderIdBySMSType(currentUser.getUserId(), Constants.SMS_TYPE_TRANSACTIONAL);
				transGBId.setVisible(transSenderIdList != null);
				if(transSenderIdList != null) {
					UserSMSSenderId userSMSSenderId = (UserSMSSenderId)transSenderIdList.get(0);
					transSenderID = userSMSSenderId != null ? userSMSSenderId.getSenderId() : null;
					transSenderIdTxtBxId.setText(transSenderID != null ? transSenderID : Constants.STRING_NILL);
				}
				templateList = transactionalTemplatesDao.findTemplatesByUserId(currentUser.getUserOrganization().getUserOrgId());	
			
			transacTempSettings();
			
			if(approveTempCmbBoxId.getItemCount() > 0)approveTempCmbBoxId.setSelectedIndex(0);
			
			
			
			// added for keywords settings
			
			int keywordLimit =(currentUser.getUserOrganization()).getMaxKeywords();
			keywordLimiTxtId.setText(""+keywordLimit);
			
			/*String msgRecvNum = (currentUser.getUserOrganization()).getMsgReceivingNumbers();
			//msgRecvNumTxtId.setText("+91 "+msgRecvNum);
			
			if(msgRecvNum != null) {
				Components.removeAllChildren(msgRecvNumCbId);
				String[] rcvdNumberArr = msgRecvNum.split(Constants.ADDR_COL_DELIMETER); 
				for (String number: rcvdNumberArr) {
					Comboitem item = new Comboitem(number.length() >10 ? "+"+number : number);
					item.setValue(number);
					item.setParent(msgRecvNumCbId);
				}
				
				if(msgRecvNumCbId.getItemCount() > 0) msgRecvNumCbId.setSelectedIndex(0);
			}//if
*/			
			
			
			String toEmaailStr =(currentUser.getUserOrganization()).getToEmailId();
			
			toEmailTxtBxId.setText(toEmaailStr);
			
			// Added for invite opt-ins
			enableOptInChkBox.setChecked(currentUser.isConsiderSMSSettings());
			enableOptinDivId.setVisible(enableOptInChkBox.isChecked());
			
			/*if(enableOptinDivId.isVisible()){
				
				if(optinSMSSettings != null){
					
					
					String optinContent = optinSMSSettings.isEnable() &&  optinSMSSettings.getAutoResponse() != null ? optinSMSSettings.getAutoResponse() : Constants.STRING_NILL;
					
					optInMsgTbId.setText(optinContent);
					getCharCount(optinContent);
					
				}
				
				Byte optinMediumVal = optinSMSSettings.isEnable() ? optinSMSSettings.getOptInMedium() : 0;
				if(optinSMSSettings.isEnable() && optinMediumVal != null) {
					
					enableManualChkBox.setChecked((optinMediumVal & Integer.parseInt(enableManualChkBox.getValue().toString()))>0);
					enableWebFormChkBox.setChecked((optinMediumVal & Integer.parseInt(enableWebFormChkBox.getValue().toString()))>0);
					enablePOSChkBox.setChecked((optinMediumVal & Integer.parseInt(enablePOSChkBox.getValue().toString()))>0);
					
				}//if
				
				if(currentUser.getUserId().longValue() == optinSMSSettings.getUserId().getUserId().longValue()){
					
					enableManualChkBox.setDisabled(false);
					enablePOSChkBox.setDisabled(false);
					enableWebFormChkBox.setDisabled(false);
					checkTemplateAnchId.setDisabled(false);
					
				}else{
					enableManualChkBox.setDisabled(true);
					enablePOSChkBox.setDisabled(true);
					enableWebFormChkBox.setDisabled(true);
					checkTemplateAnchId.setDisabled(true);
					
					
					
				}
				
				
				
			}*/
			
		
			
		} catch (WrongValueException e) {
			//logger.error("Exception :::",e);
			logger.error("Exception",e);
			
		} catch (Exception e) {
			//logger.error("Exception :::",e);
			logger.error("Exception",e);
		}
		
	}
	
	
	public void onClick$tempContPreviewTbId() {
		
		//Users user= (Users)session.getAttribute("USEROBJ");
		//String htmlContent=campaign.getHtmlText();
		if(approveTempCmbBoxId.getSelectedIndex()==0) return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), trTemplateObj.getTemplateContent());
		previewIframeWin.setVisible(true);
		
	} //onClick$tempContPreviewTbId
	
	// Added for add new tRansactional template 
	
	
	public void onClick$addNewTempTBId() {
		logger.debug("just click addNewTempTBId >>> ");
		
		String transContent =Constants.STRING_NILL;
		
		transContent = createTransTempWinId$transTempNameTxtBxId.getValue().trim().isEmpty() || createTransTempWinId$transTempNameTxtBxId.getValue().trim() == null  ? Constants.STRING_NILL : createTransTempWinId$transTempNameTxtBxId.getValue().trim();
		getCharCountOfTransTempConent(transContent);
		
		createTransTempWinId$transTempNameTxtBxId.setValue("");
		createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempContentTxtBxId.setValue("");
		createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempCharCountTxtBxId.setValue("");
		createTransTempWinId$transTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId.doHighlighted();
		//setVisible(true);
		
	}

	public void onClick$transTempCancelBtnId$createTransTempWinId() {
		logger.debug("just click onClick$transTempCancelBtnId$createTransTempWinId >>> ");
		createTransTempWinId$transTempNameTxtBxId.setValue("");
		createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempContentTxtBxId.setValue("");
		createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempCharCountTxtBxId.setValue("");
		createTransTempWinId$transTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId.setVisible(false);
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	public void onClick$transTempSaveBtnId$createTransTempWinId() {
		logger.debug("just click onClick$transTempSaveBtnId$createTransTempWinId >>> ");
		
		createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
		String templateName = createTransTempWinId$transTempNameTxtBxId.getValue().trim();
		String contentStr = createTransTempWinId$transTempContentTxtBxId.getValue().trim();
		if(templateName.trim().length() == 0) {
			createTransTempWinId$transTempNameTxtBxId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please provide Template Name.", "color:red", "TOP");
			return;
		}else if(contentStr.trim().length() == 0) {
			createTransTempWinId$transTempContentTxtBxId.setStyle(ERROR_STYLE);
			
			MessageUtil.setMessage("Please provide Template Content.", "color:red", "TOP");
			return;
		}
		
		logger.debug("transactionalTemplatesDao value is ::"+transactionalTemplatesDao);
		
		boolean isExistFlag = transactionalTemplatesDao.isTemplateNameExistByUserId(currentUser.getUserId(),templateName);
		
		if(isExistFlag){
			MessageUtil.setMessage("Template already exists with the given name.", "color:red", "TOP");
			createTransTempWinId$transTempNameTxtBxId.setStyle(ERROR_STYLE);
			return;
		}
		
		TransactionalTemplates transactTempObj = new TransactionalTemplates();
		transactTempObj.setStatus(0);
		transactTempObj.setTemplateName(templateName);
		transactTempObj.setUserId(currentUser.getUserId());
		transactTempObj.setOrgId(currentUser.getUserOrganization().getUserOrgId());
		transactTempObj.setTemplateContent(contentStr);
		//transactionalTemplatesDao.saveOrUpdate(transactTempObj);
		transactionalTemplatesDaoForDML.saveOrUpdate(transactTempObj);

		
		
		Messagebox.show("An email has been sent to Optculture Admin for template approval. "
				+ "Once approved, \n the template will be automatically activated into your account for use. ", "info", Messagebox.OK, Messagebox.INFORMATION); 
		MessageUtil.setMessage("Template saved successfully.", "color:green;");
		
		
		String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);

		String templateTypeStr = PropertyUtil.getPropertyValueFromDB("TransactionalTemplate");
		if(templateTypeStr != null ) {
		
		//	templateTypeStr = templateTypeStr.replace("<Template content>", contentStr); 
			//APP-3515
			templateTypeStr = templateTypeStr.replace("[Template content]",contentStr).replace("[Username]",
					Utility.getOnlyUserName(currentUser.getUserName())).replace("[orgID]",Utility.getOnlyOrgId(currentUser.getUserName()));
		}else templateTypeStr = contentStr;
		
		EmailQueue emailQueue = new EmailQueue("Transactional Template", templateTypeStr,
				Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", toEmailId,Calendar.getInstance(), currentUser);
    	//emailQueueDao.saveOrUpdate(emailQueue);
    	emailQueueDaoForDML.saveOrUpdate(emailQueue);
    	
    	
    	
    	
    	onClick$transTempCancelBtnId$createTransTempWinId();
    	
    	// set Transactional templates
    	Comboitem comboItemObj = new Comboitem(templateName);
    	comboItemObj.setDescription("Pending");
    	comboItemObj.setValue(transactTempObj);
    	comboItemObj.setParent(approveTempCmbBoxId);
    	//transacTempSettings();
		
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	/*public void onSelect$transTempCmbBxId(){
		logger.debug("Templates selection started"+approveTempCmbBoxId.getSelectedItem().getValue());
		if(approveTempCmbBoxId.getSelectedIndex() == 0) {
			SMSMsgTbId.setValue("");
			getCharCount("");
//			SMSMsgTbId.setReadonly(false);
		}else{
			TransactionalTemplates transacTempObj = (TransactionalTemplates)transTempCmbBxId.getSelectedItem().getValue();
			if(transacTempObj.getStatus() == 0){
				SMSMsgTbId.setValue("");
				getCharCount("");
//				SMSMsgTbId.setReadonly(false);
				MessageUtil.setMessage("Template is not approved", "color:red", "TOP");
				return;
			}
			SMSMsgTbId.setValue(transacTempObj.getTemplateContent());
			getCharCount(transacTempObj.getTemplateContent());
			SMSMsgTbId.setReadonly(true);
			//Clients.evalJavaScript("updateCaretPosition()");
//			onChange$caretPosTB();
//			SMSMsgTbId.setReadonly(true);
		}
	}*/
	
	private void transacTempSettings() {
		
		try {
			Components.removeAllChildren(approveTempCmbBoxId);
			
			//set the TransactionalTemplates
			List<TransactionalTemplates>  templateList = transactionalTemplatesDao.findTemplatesByOrgId(currentUser.getUserOrganization().getUserOrgId());
			Comboitem combItem = null;
			combItem = new Comboitem("--select--");
			combItem.setParent(approveTempCmbBoxId);
			
			if(templateList != null && templateList.size() >0) {
				
				for (TransactionalTemplates eachObj : templateList) {
					combItem = new Comboitem(eachObj.getTemplateName());
					combItem.setDescription(eachObj.getStatus() == 0? "Pending" : "Approved");
					combItem.setValue(eachObj);
					combItem.setParent(approveTempCmbBoxId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception",e);
		}
	}
	
	public void getCharCountOfTransTempConent(String msgContent) {
		try {
				int charCount = msgContent.length();
//				logger.info("the length is====>"+charCount);
				createTransTempWinId$transCaretPosTB.setValue(""+charCount);
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					createTransTempWinId$transTempCharCountTxtBxId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					createTransTempWinId$transTempCharCountTxtBxId.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				logger.debug("Exception while getting the character count",e);
		}//catch
		
	}//getCharCount
	//createTransTempWinId$transCaretPosTB
	public void onChange$transCaretPosTB$createTransTempWinId(){
		logger.debug("-----just entered:123 onChange event----");
		//Clients.evalJavaScript("updateTranTemCaretPosition();");
		//getCharCountOfTransTempConent(createTransTempWinId$transTempContentTxtBxId.getValue());
	}
	
	public void onBlur$transCaretPosTB$createTransTempWinId() {
		
		logger.debug("-----just entered:123 onBlur event----");
		
	}
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$transTempContentTxtBxId$createTransTempWinId(InputEvent event){
		try{
			//getCharCount( event.getValue());
			getCharCountOfTransTempConent(event.getValue());
			
			
		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	
	
	
	// ADDED FOR OPT-IN INVITES
	
	
	public void onCheck$enableOptInChkBox(){
		
		try {
			
			enableOptinDivId.setVisible(enableOptInChkBox.isChecked());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
		
	}
	
	
	/**
	 * updates the current cursor position
	 */
	
	public void onChange$caretPosTB(){
		logger.debug("---just entered----");
	}
	
	
	public void onBlur$caretPosTB() {
		
		logger.debug("-----just entered: onBlur event----");
		
	}
	
	public void onChange$optinCaretPosTB$createNewOptinMsgWinId(){
		logger.debug("-----just entered:123 onChange event----");
		//Clients.evalJavaScript("updateTranTemCaretPosition();");
		//getCharCountOfTransTempConent(createTransTempWinId$transTempContentTxtBxId.getValue());
	}
	
	public void onBlur$optinCaretPosTB$createNewOptinMsgWinId() {
		
		logger.debug("-----just entered:123 onBlur event----");
		
	}
	
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$optinContentTxtBxId$createNewOptinMsgWinId(InputEvent event){
		try{
			//getCharCount( event.getValue());
			getCharCountOfOptinConent(event.getValue());
			
			
		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	
	public void getCharCountOfOptinConent(String msgContent) {
		try {
				int charCount = msgContent.length();
//				logger.info("the length is====>"+charCount);
				createNewOptinMsgWinId$optinCaretPosTB.setValue(""+charCount);
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					createNewOptinMsgWinId$optinCharCountTxtBxId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					createNewOptinMsgWinId$optinCharCountTxtBxId.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				logger.debug("Exception while getting the character count",e);
		}//catch
		
	}//getCharCount
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$optInMsgTbId(InputEvent event){
		try{
			getCharCount(event.getValue());
			
			
		}catch (Exception e) {
			//logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	public void getCharCount(String msgContent) {
		try {
				int charCount = msgContent.length();
				//logger.info("the length is====>"+charCount);
			
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					charCountTbId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					charCountTbId.setValue(""+charCount+" / "+1);
				}//else
		}catch (Exception e) {
			logger.error("Exception",e);
		}
	
	}

	
	public void onClick$checkTemplateAnchId() {
		try {
			logger.debug("just click addNewTempTBId >>> ");
		/*	
			SMSSettings optinSMSSettings = smsSettingsDao.findByUserOrg(currentUser.getUserOrganization().getUserOrgId(),OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			String optinContent =Constants.STRING_NILL;
			
			if(optinSMSSettings != null){
				
				 optinContent = optinSMSSettings.getAutoResponse() != null ? optinSMSSettings.getAutoResponse() : Constants.STRING_NILL;
				
				optInMsgTbId.setText(optinContent);
				getCharCountOfOptinConent(optinContent);
			}
			*/
			
			String optinContent =Constants.STRING_NILL;
			
			optinContent = optInMsgTbId.getValue().trim().isEmpty() || optInMsgTbId.getValue().trim() == null  ? Constants.STRING_NILL : optInMsgTbId.getValue().trim();
			
			getCharCountOfOptinConent(optinContent);
			
					
			createNewOptinMsgWinId$optinContentTxtBxId.setValue(optinContent);
			createNewOptinMsgWinId$optinContentTxtBxId.setStyle(NORMAL_STYLE);
			//createNewOptinMsgWinId$optinCharCountTxtBxId.setValue("");
			createNewOptinMsgWinId$optinCharCountTxtBxId.setStyle(NORMAL_STYLE);
			
			createNewOptinMsgWinId.doHighlighted();
		} catch (WrongValueException e) {
			logger.error("Exception",e);
		} catch (Exception e1) {
			logger.error("Exception",e1);
		}
		
	}//onClick$checkTemplateAnchId

	public void onClick$optinCancelBtnId$createNewOptinMsgWinId() {
		logger.debug("just click onClick$transTempCancelBtnId$createTransTempWinId >>> ");
		
		createNewOptinMsgWinId$optinContentTxtBxId.setValue("");
		createNewOptinMsgWinId$optinContentTxtBxId.setStyle(NORMAL_STYLE);
		createNewOptinMsgWinId$optinCharCountTxtBxId.setValue("");
		createNewOptinMsgWinId$optinCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createNewOptinMsgWinId.setVisible(false);
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	public void onClick$optinSaveBtnId$createNewOptinMsgWinId() {
		
		createNewOptinMsgWinId$optinContentTxtBxId.setStyle(NORMAL_STYLE);
		String contentStr = createNewOptinMsgWinId$optinContentTxtBxId.getValue().trim();
		 if(contentStr.trim().length() == 0) {
			 createNewOptinMsgWinId$optinContentTxtBxId.setStyle(ERROR_STYLE);
			
			MessageUtil.setMessage("Please provide content.", "color:red", "TOP");
			return;
		}
		
		
		Messagebox.show("An email has been sent to Optculture Admin for content approval. "
				+ "Once approved, \n the content will be automatically activate into your account for use. ", "info", Messagebox.OK, Messagebox.INFORMATION); 
		MessageUtil.setMessage("Content saved successfully.", "color:green;");
		
		
		String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
		
		
		
		EmailQueue emailQueue = new EmailQueue(Constants.OPTIN_REQUEST_SUBJECT, contentStr,
				Constants.EQ_TYPE_OPTIN_REQUEST_TYPE, "Active", toEmailId,Calendar.getInstance(), currentUser);
    	//emailQueueDao.saveOrUpdate(emailQueue);
    	emailQueueDaoForDML.saveOrUpdate(emailQueue);
    	
    	
    	
    	onClick$optinCancelBtnId$createNewOptinMsgWinId();
    	
    
		
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	public void onClick$submitBtnId(){
		
		
		try {
			try {
				SMSSettings optinSMSSettings = (SMSSettings)enableOptInChkBox.getAttribute(SMSSETTINGS);
				
				if(optinSMSSettings == null){
					
					MessageUtil.setMessage("User does not have Optin settings to save .", "color:red;");
					return;
					
				}
				//smsSettingsDao.findByUserOrg(currentUser.getUserOrganization().getUserOrgId(),OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				boolean enable = enableOptInChkBox.isChecked();
				
				if(optinSMSSettings != null){
					
				UserSMSGateway transUserSMSGateway = userSMSGatewayDao.findByUserId(optinSMSSettings.getUserId().getUserId(), Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
				
				if(transUserSMSGateway == null){
					
					MessageUtil.setMessage("Opt-in program without transactional setup can not be configured."
							+ " Please contact Admin for Transactional setup", "color:red;");
					return;
					
				}
				
				
				if(enable){
					
					if(!enableManualChkBox.isChecked() &&  !enablePOSChkBox.isChecked() && !enableWebFormChkBox.isChecked()){
						
						MessageUtil.setMessage("Please select at least one opt-in medium", "color:red;");
						return;
						
					}
					
				}
			
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
				if(currentUser.getUserId().longValue() == optinSMSSettings.getUserId().getUserId().longValue()){
				
					optinSMSSettings.setOptInMedium(optinValue);
					optinSMSSettings.setEnable(enable);
					//smsSettingsDao.saveOrUpdate(optinSMSSettings);
					smsSettingsDaoForDML.saveOrUpdate(optinSMSSettings);
					
				}else{
					
					currentUser.setOptInMedium(optinValue);
				}
				
				currentUser.setConsiderSMSSettings(enable);
				//usersDao.saveOrUpdate(currentUser);
				usersDaoForDML.saveOrUpdate(currentUser);
				
				GetUser.getUserObj(true);
					
				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.SMS_OPTIN_SMS_SETTINGS);	
			}
				
				
			MessageUtil.setMessage("Settings saved successfully.", "color:blue;","TOP");
				
			} catch (Exception e) {
				logger.error("Exception",e);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
		
		
		
	}

	
}
