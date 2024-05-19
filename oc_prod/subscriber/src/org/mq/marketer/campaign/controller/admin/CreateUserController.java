package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ProductsUsed;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersAdditionalContactDetails;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.beans.Vmta;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CountryCodeDao;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.ExternalSMTPSettingsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.ProductsUsedDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.ProductsUsedDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSettingsDaoForDML;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersAdditionalContactDetailsDao;
import org.mq.marketer.campaign.dao.UsersAdditionalContactDetailsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.dao.UsersDomainsDaoForDML;
import org.mq.marketer.campaign.dao.VmtaDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.timer.CheckEmailSmsCreditThread;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class CreateUserController extends GenericForwardComposer implements EventListener {

	String usersParentDirectory = null;
	Textbox userNameTbId;
	// Textbox passwordTbId;
	// Textbox rePasswordTbId;
	Textbox emailIdTbId, numOfStoresTbId, numOfContactsTbId, smsTbId, drTbId, templateId, uaeTemplateId;
	Textbox firstNameTbId;
	Textbox lastNameTbId;
	Textbox compNameTbId;
	Textbox addressOneTbId;
	Textbox addressTwoTbId;
	Textbox cityTbId;
	Textbox stateTbId;
	Textbox countryTbId;
	Textbox pinTbId;
	Textbox phoneTbId, countryCodeTxtBxId;
	Checkbox enabledCbId;
	Checkbox externalEdCbId, plainTextEdCbId, plainHtmlEdCbId, blockEdCbId, mobilePatternChkBxId;


	Intbox totalLimitLbId, totalSmsLimitLbId;
	MyDatebox startDbId;
	MyDatebox expDbId;
	Combobox vmtaCbId, smsGateWayCbId, vmtaAccountNameCbId;
	// Label vmtaStatusLblId;

	Label nameStatusLblId;
	Checkbox submitCbId;

	boolean isValidUserName = true;
	String type;
	Users user;
	boolean isNewUser;

	Textbox contact1NameTbId, contact1NumberTbId, contact1EmailTbId, contact1PositionTbId;
	Textbox contact2NameTbId, contact2NumberTbId, contact2EmailTbId, contact2PositionTbId;
	Textbox contact3NameTbId, contact3NumberTbId, contact3EmailTbId, contact3PositionTbId;

	Textbox numberOfContractedEmailsTbId, numberOfContractedSmsTbId, optDRVersionTbId, optLoyaltyVersionTbId,
			optPromoVersionTbId, optIntelVersionTbId, optSyncVersionTbId;
	Listbox posSystemVersionLbId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private UsersDomainsDao usersDomainsDao;
	private UsersDomainsDaoForDML usersDomainsDaoForDML;
	private ExternalSMTPSettingsDao externalSMTPSettingsDao;
	private UsersAdditionalContactDetailsDao usersAdditionalContactDetailsDao;
	private UsersAdditionalContactDetailsDaoForDML usersAdditionalContactDetailsDaoForDML;
	private ProductsUsedDao productsUsedDao;
	private ProductsUsedDaoForDML productsUsedDaoForDML;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private Properties countryCodes;
	POSMappingDao posMappingDao = null;// 198
	

	String currentPath = Constants.STRING_NILL;
	// Added for user sms settings
	// private Groupbox smsSettingsIdGbId;

	private Checkbox enableSmsChkId, transactionalSMSGateWayChkId, // promotionalSMSGateWayChkId,
			optinSMSGateWayChkId, enableMsdCalChkId, enableKeywordChkId, optinSMSRouteChkId, mobileOptinOnEnrollChkId,
			mobileValidationsChkId;// outboundChkId,
	private Checkbox uaeTransactionalSMSGateWayChkId;
	private Textbox countryCodeTxtId, keywordsTxtboxId, //transSenderIdTxtBxId, app-3701
			optinSenderIdTxtBxId, optinKeywordTxtBxId,
			optoutKeywordTxtBxId, toEmailTxtboxId, caretPosTB, optInMsgTbId, charCountTbId, selectedFileTbId,
			uaeTransSenderIdTxtBxId;
	private Listbox countryLbId, promotionalSMSGateWayLbId, transactionalSMSGateWayLbId, optinSMSGateWayLbId,
			optinSMSUserNameGateWayLbId, genericSMSUserNameGateWayLbId, promotionalSMSUserNameGateWayLbId,
			transactionalSMSUserNameGateWayLbId, USSMSGateWayLbId,CANADASMSGateWayLbId,SASMSGateWayLbId, senderIdLBId,trsenderIdLBId, pakistanSMSGateWayLbId,
			uaeSMSGateWayLbId, uaeSMSUserNameGateWayLbId, USSMSUserNameGateWayLbId,CANADASMSUserNameGateWayLbId,SASMSUserNameGateWayLbId, pakistanSMSUserNameGateWayLbId,
			optinListLBId, fileListlbId, genericSMSGateWayLbId, uaeTransactionalSMSGateWayLbId,
			uaeTransactionalSMSUserNameGateWayLbId;

	private Div enableSmsDivId, indiaDivId, pakistanSMSDivId, transactionalDivId, optinSmsDivId, optinRouteDivId,
			usSMSDivId,canadaSMSDivId,saSMSDivId, uaeSMSDivId, optinMsdCalDivId, enableMsdCallsKeywordsDivId, optinListDivId, filesDivId,
			genericSMSDivId, selectSenderIdDiv,selectTRSenderIdDiv, tempIdRow, uaeTempIdRow, uaeTransactionalDivId;
	// private Combobox msgRecvNumCmbId;
	private Intbox userSmsLimitIntBoxId, minTxtId, maxTxtId;
	private Combobox approveTempCmbBoxId, uaeApproveTempCmbBoxId;
	// private Toolbarbutton tempContPreviewTbId , approveTempTbId, addNumberTbId;
	private Window previewIframeWin;
	private Iframe previewIframeWin$iframeId;

	private Listbox optinMsdCalNumLBId, optinKeywordMsdCalNumLBId;
	private Listhead senderIdListHead;

	private OCSMSGatewayDao ocsmsGatewayDao;
	private TransactionalTemplatesDao transactionalTemplatesDao;
	private TransactionalTemplatesDaoForDML transactionalTemplatesDaoForDML;
	private UserSMSGatewayDao userSMSGatewayDao;
	private UserSMSGatewayDaoForDML userSMSGatewayDaoForDML;
	private CountryCodeDao countryCodeDao;

	private UserSMSSenderIdDao userSMSSenderIdDao;
	private UserSMSSenderIdDaoForDML userSMSSenderIdDaoForDML;
	private SMSSettingsDao smsSettingsDao;
	private SMSSettingsDaoForDML smsSettingsDaoForDML;
	private MailingListDao mailingListDao;
	private CountryReceivingNumbersDao countryReceivingNumbersDao;
	private VmtaDao vmtaDao;

	public static final Map<String, String> countryMap = new HashMap<String, String>();

	// Loyalty Service type radio buttons and it group
	private Radiogroup loyaltyServicetypeLbId;
	private Radio oCRadioId, sBToOCRadioId, sBRadioId;

	public CreateUserController() {

		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
		externalSMTPSettingsDao = (ExternalSMTPSettingsDao) SpringUtil.getBean("externalSMTPSettingsDao");
		usersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		usersDomainsDaoForDML = (UsersDomainsDaoForDML) SpringUtil.getBean("usersDomainsDaoForDML");
		usersAdditionalContactDetailsDao = (UsersAdditionalContactDetailsDao) SpringUtil
				.getBean("usersAdditionalContactDetailsDao");
		usersAdditionalContactDetailsDaoForDML = (UsersAdditionalContactDetailsDaoForDML) SpringUtil
				.getBean("usersAdditionalContactDetailsDaoForDML");
		productsUsedDao = (ProductsUsedDao) SpringUtil.getBean("productsUsedDao");
		productsUsedDaoForDML = (ProductsUsedDaoForDML) SpringUtil.getBean("productsUsedDaoForDML");
		ocsmsGatewayDao = (OCSMSGatewayDao) SpringUtil.getBean("OCSMSGatewayDao");
		transactionalTemplatesDao = (TransactionalTemplatesDao) SpringUtil.getBean("transactionalTemplatesDao");
		transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML) SpringUtil
				.getBean("transactionalTemplatesDaoForDML");
		userSMSGatewayDao = (UserSMSGatewayDao) SpringUtil.getBean("userSMSGatewayDao");
		userSMSGatewayDaoForDML = (UserSMSGatewayDaoForDML) SpringUtil.getBean("userSMSGatewayDaoForDML");
		userSMSSenderIdDao = (UserSMSSenderIdDao) SpringUtil.getBean("userSMSSenderIdDao");
		userSMSSenderIdDaoForDML = (UserSMSSenderIdDaoForDML) SpringUtil.getBean("userSMSSenderIdDaoForDML");
		smsSettingsDao = (SMSSettingsDao) SpringUtil.getBean("smsSettingsDao");
		smsSettingsDaoForDML = (SMSSettingsDaoForDML) SpringUtil.getBean("smsSettingsDaoForDML");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML) SpringUtil.getBean("emailQueueDaoForDML");
		countryReceivingNumbersDao = (CountryReceivingNumbersDao) SpringUtil.getBean("countryReceivingNumbersDao");
		vmtaDao = (VmtaDao) SpringUtil.getBean("vmtaDao");
		user = GetUser.getUserObj();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		countryCodeDao = (CountryCodeDao) SpringUtil.getBean("countryCodeDao");

		posMappingDao = (POSMappingDao) SpringUtil.getBean("posMappingDao");// 198
		

	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		try {
			super.onEvent(event);
			if (event.getTarget() instanceof Textbox) {
				if (event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(Events.ON_RIGHT_CLICK)) {
					MessageUtil.setMessage("Right-click and CTRL+V actions have been disabled.", "color:red;");
					return;
				}
			}
			Object eventObj = event.getTarget();

			if (eventObj instanceof Image) { // Toolbarbutton
				Image tbImg = (Image) eventObj;
				String path = (String) tbImg.getAttribute("path");
				Listitem liItem = (Listitem) tbImg.getParent().getParent().getParent();
				// Listbox lb = (Listbox)liItem.getParent();
				// logger.info("msg..."+tbImg.getTooltiptext());
				if (tbImg.getTooltiptext().equalsIgnoreCase("Delete")) {
					try {
						if (Messagebox.show("Do you want to delete the file?", "Confirmation",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) != Messagebox.OK) {
							return;
						}
						logger.debug("performing delete file operation");
						Hbox hbox = (Hbox) tbImg.getParent();
						File delFile = new File(path);
						delFile.delete();
						hbox.setVisible(false);
						liItem.setVisible(false);
						MessageUtil.setMessage("File deleted successfully.", "color:blue;", "TOP");

					} catch (Exception e) {
						// TODO: handle exception
						MessageUtil.setMessage("Problem encountered while deleting the file.", "color:red;", "TOP");
						logger.error("Exception ::", e);
					}
				} else {
					try {
						if (Messagebox.show("Do you want to download the file?", "Confirmation",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) != Messagebox.OK) {
							return;
						}
						logger.debug("performing downloading file operation");
						Filedownload.save(new File(path), "text/plain");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						MessageUtil.setMessage("Problem encountered while downloading the file.", "color:red;", "TOP");
						logger.error("Exception ::", e);
						e.printStackTrace();
						return;
					}
				}

			} // if Toolbarbutton

		} catch (Exception e) {
			logger.error("problem occured while handling the event ", e);

		}

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			/*
			 * TimeZone timezone =
			 * (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); MyCalendar
			 * cal = new MyCalendar(timezone); //startDbId.setValue(cal.getTime());
			 * MyCalendar cal2 = new MyCalendar(timezone);
			 * 
			 * cal2.set(MyCalendar.MONTH, cal2.get(MyCalendar.MONTH) + 1);
			 * //expDbId.setValue(cal2.getTime());
			 */

			// countryCodeTxtBxId.setDisabled(true);

			optInMsgTbId.setCtrlKeys("^v");
			optInMsgTbId.addEventListener(Events.ON_CTRL_KEY, this);
			optInMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, this);

			// VmtaDao vmtaDao = (VmtaDao)SpringUtil.getBean("vmtaDao");
			// List<Vmta> vmtaList = vmtaDao.findAll();
			countryCodes = countryCodeDao.findAllCountryCodes();
			List<Vmta> vmtaList = vmtaDao.findUniqueVmta();
			logger.debug("Got vmta List of size :" + vmtaList.size());

			Comboitem ci;
			// ci = new Comboitem("--Select VMTA--");
			ci = new Comboitem("Select Email Route");
			ci.setDescription(" ");
			ci.setParent(vmtaCbId);
			vmtaCbId.setSelectedIndex(0);

			/*
			 * Vmta vmta = vmtaDao.findByVmtaName("SendGridAPI"); if(vmta != null ) {
			 * 
			 * ci = new Comboitem("SendGridAPI"); ci.setDescription(vmta.getDescription());
			 * 
			 * if(vmta.getStatus().equalsIgnoreCase("good")) {
			 * ci.setImage("/img/vmta/goodVmta.JPG"); } else
			 * if(vmta.getStatus().equalsIgnoreCase("bad")) {
			 * ci.setImage("/img/vmta/badVmta.JPG"); } else
			 * if(vmta.getStatus().equalsIgnoreCase("average")) {
			 * ci.setImage("/img/vmta/avgVmta.png"); }
			 * 
			 * ci.setValue(vmta.getStatus()); ci.setParent(vmtaCbId);
			 * 
			 * 
			 * 
			 * }
			 */

			for (Vmta aVmta : vmtaList) {

				ci = new Comboitem(aVmta.getVmtaName());
				ci.setValue(aVmta);
				ci.setAttribute("aVmtaObject", aVmta);
				ci.setDescription(aVmta.getDescription());

				if (aVmta.getStatus() != null && aVmta.getStatus().equalsIgnoreCase("good")) {
					ci.setImage("/img/vmta/goodVmta.JPG");
				} else if (aVmta.getStatus() != null && aVmta.getStatus().equalsIgnoreCase("bad")) {
					ci.setImage("/img/vmta/badVmta.JPG");
				} else if (aVmta.getStatus() != null && aVmta.getStatus().equalsIgnoreCase("average")) {
					ci.setImage("/img/vmta/avgVmta.png");
				}

				ci.setParent(vmtaCbId);

			}

			ci = new Comboitem("Select Account");
			ci.setParent(vmtaAccountNameCbId);
			vmtaAccountNameCbId.setSelectedIndex(0);
			vmtaAccountNameCbId.setDisabled(true);

			// type =
			// ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getParameter("type");

			logger.debug("The value of type=" + type);

			user = (Users) sessionScope.get("editUserObj");
			type = (String) Executions.getCurrent().getAttribute("type");
			String vmtaSelStr = null;

			// ******************set default dates for start and end
			// dates************************
			Calendar cal = MyCalendar.getNewCalendar();
			startDbId.setValue(cal);
			logger.debug("ToDate (server) :" + cal);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
			logger.debug("FromDate (server) :" + cal);
			expDbId.setValue(cal);
			// *************************************************

			// added for sms settings

			countryMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_US);
			countryMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_INDIA);
			countryMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_COUNTRY_PAKISTAN);
			countryMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_COUNTRY_UAE);// for uae date 16th june
			countryMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_COUNTRY_CANADA);
			countryMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_COUNTRY_SA);
			countryMap.put(Constants.SMS_COUNTRY_QATAR, Constants.SMS_COUNTRY_QATAR);
			countryMap.put(Constants.SMS_COUNTRY_KUWAIT, Constants.SMS_COUNTRY_KUWAIT);

			Enumeration enm = countryCodes.propertyNames();
			while (enm.hasMoreElements()) {
				String currCountry = (String) enm.nextElement();
				countryMap.put(currCountry, currCountry);
			}

			Set<String> countrySet = countryMap.keySet();
			TreeSet<String> countrySortedSet = new TreeSet<String>();
			countrySortedSet.addAll(countrySet);
			Components.removeAllChildren(countryLbId);

			for (String country : countrySortedSet) {

				Listitem item = new Listitem(country);

				item.setParent(countryLbId);
				item.setValue(countryMap.get(country));

			} // for

			if (countryLbId.getItemCount() > 0) {

				countryLbId.setSelectedIndex(0);
			}

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_INDIA);

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_US);
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_PAKISTAN);
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_UAE); // for uae date 16th june
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_CANADA);
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_SA);//APP-3819
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_QATAR)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_QATAR);
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_KUWAIT)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_KUWAIT);
			} else {// for countries other than above mentioned conditions

				Enumeration enm2 = countryCodes.propertyNames();
				while (enm2.hasMoreElements()) {
					String currCountry = (String) enm2.nextElement();

					if (countryStr.equalsIgnoreCase(currCountry)) {
						countryCodeTxtId.setValue("+" + countryCodes.get(currCountry));
						break;
					}

				}

			}

			if ((type != null && type.equalsIgnoreCase("edit")) && user != null) {
				isNewUser = false;
				// type="edit";
				/**** ZScript code *****/
				// checkFor UserTyep

				// logger.info("editting the user====>"+user.getUserName());
				if (user.getVmta() != null) {
					vmtaAccountNameCbId.setDisabled(false);
				} else {
					vmtaAccountNameCbId.setDisabled(true);
				}
				String style = "font-weight:bold;font-size:15px;color:#313031;"
						+ "font-family:Arial,Helvetica,sans-serif;align:left";

				PageUtil.setHeader("Edit User", Constants.STRING_NILL, style, true);
				defaultCreatUserSetting(user);
				currentPath = usersParentDirectory + "/" + userNameTbId.getText().trim() + "__org__"
						+ compShrtNameTbId.getText().trim() + "/Opt_sync_details/";
				getImages(filesDivId);
				sessionScope.remove("editUserObj");
				Executions.getCurrent().removeAttribute("type");
				// user = GetUser.getUserObj();
				/*
				 * if(user ==null) { logger.debug("got user as null......"); return; }
				 */
				// vmtaSelStr = user.getVmta();
				// getSenderIds(user);

				defaultSMSSettings(user);
				defaultSelectedSenderIds();
				// Set SMSGateway
				/*
				 * if(user.getUserSMSTool() != null &&
				 * user.getUserSMSTool().equals(Constants.USER_SMSTOOL_MVAYOO)){
				 * smsGateWayCbId.setSelectedIndex(2); smsGateWayCbId.setDisabled(true); }else
				 * if(user.getUserSMSTool() != null &&
				 * user.getUserSMSTool().equals(Constants.USER_SMSTOOL_CLICKATELL)){
				 * smsGateWayCbId.setSelectedIndex(1); smsGateWayCbId.setDisabled(true); }else {
				 * smsGateWayCbId.setSelectedIndex(0); }
				 */

				// set CountryCode
				// countryCodeTxtBxId.setValue(user.getCountryCarrier().toString());

			} else if (type == null) {
				sessionScope.remove("editUserObj");
				ci = new Comboitem("--None--");
				ci.setParent(userSenderIdsCbId);
				userSenderIdsCbId.setSelectedIndex(0);
				isNewUser = true;

				String style = "font-weight:bold;font-size:15px;color:#313031;"
						+ "font-family:Arial,Helvetica,sans-serif;align:left";

				PageUtil.setHeader("Create User", Constants.STRING_NILL, style, true);
				mobileValidationsChkId.setChecked(true);
				// smsGateWayCbId.setSelectedIndex(0);
			}

			/*
			 * for (Vmta vmta : vmtaList) {
			 * 
			 * if(vmta.getVmtaName() != null && getVmtaName(). ) ci = new
			 * Comboitem("SendGridAPI"); ci.setDescription(vmta.getDescription());
			 * 
			 * if(vmta.getStatus().equalsIgnoreCase("good")) {
			 * ci.setImage("/img/vmta/goodVmta.JPG"); } else
			 * if(vmta.getStatus().equalsIgnoreCase("bad")) {
			 * ci.setImage("/img/vmta/badVmta.JPG"); } else
			 * if(vmta.getStatus().equalsIgnoreCase("average")) {
			 * ci.setImage("/img/vmta/avgVmta.png"); } ci.setValue(vmta.getStatus());
			 * ci.setParent(vmtaCbId);
			 * 
			 * if(vmtaSelStr != null) { if(ci.getLabel().equalsIgnoreCase(vmtaSelStr)) {
			 * vmtaCbId.setSelectedItem(ci);
			 * vmtaStatusLblId.setValue((String)ci.getValue()); } }else {
			 * 
			 * } }//for
			 */

			/*
			 * if(vmtaSelStr == null || vmtaSelStr.toLowerCase().contains("cwsend") ||
			 * vmtaSelStr.toLowerCase().contains("cwbounce") ||
			 * vmtaSelStr.toLowerCase().contains("pool") ) {
			 * 
			 * vmtaCbId.setSelectedIndex(0); }
			 * 
			 * if(vmtaSelStr != null && vmta != null &&
			 * vmtaSelStr.equals(vmta.getVmtaName()) ) {
			 * 
			 * vmtaCbId.setSelectedIndex(1);
			 * 
			 * }
			 */

			// setting visibility of reset button to false if the request coming from edit
			// user.
			if (user != null) {
				resetBtnid.setVisible(false);
			} else {
				resetBtnid.setVisible(true);
				createUserPath();

			}
			logger.info("after doaftercompose");

		} catch (Exception e) {

			logger.error("Exception  ::", e);
		}

	} // doAfterCompose

	public void createUserPath() {
		currentPath = usersParentDirectory + "/" + sessionScope.get("userName") + "/Opt_sync_details/";
		File finialfile = new File(currentPath);
		if (!finialfile.exists()) {
			finialfile.mkdirs();
		}
		File[] fileList1 = finialfile.listFiles();
		File[] fileList2;
		for (int i = 0; i < fileList1.length; i++) {
			if (fileList1[i].isDirectory()) {
				fileList2 = fileList1[i].listFiles();
				for (int j = 0; j < fileList2.length; j++) {
					fileList2[j].delete();
				}

				fileList1[i].delete();
			}
		}

	}

	public void getImages(Div filesDivId) {

		try {
			logger.debug("--just entered--");
			MessageUtil.clearMessage();

			String optSynName = userNameTbId.getText().trim() + "__org__" + compShrtNameTbId.getText().trim();
			// String filePath = usersParentDirectory + "/" + sessionScope.get("userName") +
			// "/Opt_sync_details/" + optSynName;
			File optSynFile = new File(currentPath);
			// MessageUtil.setMessage("File path"+currentPath+" .","color:blue","TOP");
			if (optSynFile.exists()) {

				Components.removeAllChildren(filesDivId);

				// filesDivId.setVisible(true);
				File[] fileList = optSynFile.listFiles();
				createFileDiv(fileList, optSynName, currentPath);

			}

		} catch (Exception e) {
			logger.error("Exception: Error occured while getting the Images ", e);

		}
	}// getImages()

	public void onUpload$uploadImgBtnId(UploadEvent uploadEvent) {
		/*
		 * numberOfUploadedFiles=0; numberOfUploadedFilesFailed=0;
		 */
		if (compShrtNameTbId.getText().trim().equals("")) {
			MessageUtil.setMessage("Organization Id should not be empty", "color:red", "TOP");
			return;
		}
		if (userNameTbId.getText().trim().equals("")) {
			MessageUtil.setMessage("user name should not be empty", "color:red", "TOP");
			return;
		}

		try {
			// Media[] fileArray = uploadEvent.getMedias();
			Media media = uploadEvent.getMedia();
			// for(Media media:fileArray){
			// logger.info("--------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>name
			// ==="+media.getName());
			upload(media);
			// }

		} catch (Exception e) {
			logger.error("unable to upload the file" + e.getMessage());
		}
	}

	public void upload(Media media) {

		try {
			MessageUtil.clearMessage();
			// logger.debug("----just entered Here---9");
			if (media == null) {
				MessageUtil.setMessage("Select a file to upload.", "color:red", "TOP");
				return;
			}
			String fileName = media.getName();
			logger.debug("upload File Name is ::" + fileName);
			String ext = FilenameUtils.getExtension(media.getName());
			if (ext.equalsIgnoreCase("ini") || ext.equalsIgnoreCase("txt")) {
				uploadFile(media, ext);
			} else {
				MessageUtil.setMessage("Upload file of format .ini/.txt only.", "color:red", "TOP");
				return;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}// upload()

	public void uploadFile(Media media, String ext) throws Exception {

		MessageUtil.clearMessage();
		String fileName = media.getName();
		try {

			String newOptSynName = userNameTbId.getText().trim() + "__org__" + compShrtNameTbId.getText().trim();
			String presentPath = "";
			if ((type != null && type.equalsIgnoreCase("edit")) && user != null) {
				presentPath = currentPath;
			} else {
				presentPath = currentPath + newOptSynName + "/";
			}

			File file = new File(presentPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String filePath = presentPath + fileName;
			File file1 = new File(filePath);
			// logger.debug("fileName is already exist >>> ::"+file.exists());
			if (file1.exists()) {
				// MessageUtil.setMessage("File already exists.","color:blue","TOP");
				MessageUtil.setMessage("File " + fileName + " already exists.", "color:blue", "TOP");// 2.3.11 asana
				return;
			}
			double fileSize = 0;
			if (ext.equalsIgnoreCase("txt"))
				fileSize = Math.ceil((media.getStringData().length())) / 1024;
			else
				fileSize = Math.ceil((media.getByteData().length)) / 1024;
			if (fileSize > 111) {
				MessageUtil.setMessage("File size is " + fileSize + " kb.It should be less than 112kb", "color:blue",
						"TOP");// 2.3.11 asana
				return;
			}

			File[] fileList = file.listFiles();
			int noFiles = 0;
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile())
					noFiles++;
			}
			if (noFiles >= 10) {
				MessageUtil.setMessage("Cannot upload more than 10-files", "color:blue", "TOP");
				return;
			}
			copyDataFromMediaToFile(filePath, media);

			File finialfile = new File(presentPath);
			File[] fileList1 = finialfile.listFiles();
			// File newFile = new File(path+"/"+fileName);
			// Components.removeAllChildren(filesDivId);
			Components.removeAllChildren(fileListlbId);
			if (file.exists()) {
				logger.debug("File list size : " + fileList.length);
				createFileDiv(fileList1, newOptSynName, presentPath);
			}

		} catch (Exception e) {
			logger.error("Exception :: error occured while Uploading the File");
			logger.error("Exception ::", e);
		}

	}

	public boolean copyDataFromMediaToFile(String path, Media m) {
		MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		String ext = FileUtil.getFileNameExtension(path);
		File file = new File(path);
		BufferedReader br = null;
		BufferedWriter bw = null;
		/*
		 * if(!(ext.equalsIgnoreCase("ini")|ext.equalsIgnoreCase("txt"))){
		 * MessageUtil.setMessage("Upload .ini/.txt file only...","color:red","BOTTOM");
		 * return false; }
		 */
		try {
//		if(logger.isDebugEnabled()) logger.debug("reading data from media using getReaderData()");
			br = new BufferedReader((InputStreamReader) m.getReaderData());
			bw = new BufferedWriter(new FileWriter(path));
			String line = "";
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
			return true;
		} catch (Exception e1) {
//		logger.error("** Exception is " + e1.getMessage()+" :trying to read with Media.getStringData() **");
			try {
//			if(logger.isDebugEnabled()) logger.debug("Reading file with Media.getStringData()");
				String data = m.getStringData();
				FileUtils.writeStringToFile(file, data);
				return true;
			} catch (Exception e2) {
//			logger.error("** Exception is " + e2 +" :trying to read as Streams **");
				try {
					FileOutputStream out = new FileOutputStream(file);
					BufferedInputStream in = new BufferedInputStream((FileInputStream) m.getStreamData());
					byte[] buf = new byte[1024];
					int count = 0;
					while ((count = in.read(buf)) >= 0) {
						out.write(buf, 0, count);
					}
					out.flush();
					in.close();
					out.close();
					return true;
				} catch (FileNotFoundException e) {
//				logger.error("** Exception is : File not found **");
				} catch (Exception e3) {
//				logger.error("** Exception is " + e3 +"  so trying to read as bytes **");
					try {
						byte[] data = m.getByteData();
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(data);
						fos.flush();
						fos.close();
						return true;
					} catch (Exception e) {
//					logger.error("** Exception is " + e +" **");
					}
				}
				String message = ".ini/.txt file upload failed," + m.getName()
						+ "\n could not copied reason may be due to network problem or may be very large file";
				Users user = GetUser.getUserObj();
				(new MessageHandler(messagesDao, user.getUserName())).sendMessage("Contact", "uploaded failed", message,
						"Inbox", false, "INFO", user);
				return false;
			}

		}
	} // copyDataFromMediaToFile

	public void createFileDiv(File[] fileList, String optsynName, String presentPath) {
		try {

			String filePath = null;
			String absPath = null;
			String fileName = null;

			Listitem li;
			Listcell lc;

			// MessageUtil.setMessage(""+fileList.length,"color:blue","TOP");
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile()) {
					// logger.debug(fileList[i]+" is a file -----------");
					fileName = fileList[i].getName();
					// filePath = "/UserData/" + sessionScope.get("userName") + "/Opt_sync_details/"
					// + optsynName + "/" + fileName;

					li = new Listitem();

					lc = new Listcell();
					lc.setLabel(fileName);
					lc.setStyle("margin-right:3px;");
					lc.setTooltiptext(fileName);
					lc.setParent(li);

					absPath = presentPath + fileName;

					lc = new Listcell();
					Hbox hbox = new Hbox();

					Image delImg = new Image("/img/icons/download.png");
					delImg.setTooltiptext("Download");
					delImg.setStyle("cursor:pointer;cursor:hand;margin:0px 10px 0px 0px");
					delImg.setAttribute("path", absPath);
					delImg.addEventListener("onClick", this);
					delImg.setParent(hbox);

					Image img = new Image("/img/action_delete.gif");
					img.setTooltiptext("Delete");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("path", absPath);
					img.addEventListener("onClick", this);
					img.setParent(hbox);

					hbox.setParent(lc);
					lc.setParent(li);

					// li.setHeight("3px");
					li.setParent(fileListlbId);
					fileListlbId.setParent(filesDivId);

				} else {
					logger.debug("not a file " + fileList[i].getPath());
				}
			} // for fileList
		} catch (Exception e) {
			logger.error("** Exception in uploadFile **", e);
		}
	}// createImageDiv()

	public void copyOptSynAndDelete() {
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName")
				+ "/Opt_sync_details/" + userNameTbId.getText().trim() + "__org__" + compShrtNameTbId.getText().trim();
		String finaldirectory = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/"
				+ userNameTbId.getText().trim() + "__org__" + compShrtNameTbId.getText().trim() + "/Opt_sync_details/";
		File file = new File(path);
		File dest = new File(finaldirectory);
		if (file.exists()) {
			// File[] fileList = new File[1];
			// fileList = file.listFiles();
			try {
				FileUtils.copyDirectory(file, dest);
				FileUtils.deleteDirectory(file);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void onClick$submitBtnId() {

		logger.debug("---just entered---");

		/*
		 * String errmsg = validateUser(); if(errmsg.trim().length() > 0 ) {
		 * 
		 * errorDivId.setValue(errmsg);
		 * logger.debug("Some errors found:"+errorDivId.getValue()+"===="+errorDivId.
		 * isVisible() ); errorDivId.setVisible(true); return;
		 * 
		 * }//if
		 */
		onCheck$submitCbId();
		copyOptSynAndDelete();
		errorDivId.setValue(Constants.STRING_NILL);

	}// onClick$submitBtnId()
	/*
	 * function validateUser() {
	 * 
	 * errorContent = "";
	 * 
	 * valid = true; var image; validateStr('userNameTbId');
	 * 
	 * var nameStatusLbl = zk.Widget.$(jq('$nameStatusLblId'));
	 * if(nameStatusLbl.getStyle().indexOf('red') >0 ) { valid = false; errorContent
	 * += '<br/> <li> Provide valid User Name or User Name already exist';
	 * zk.Widget.$(jq('$userNameTbId')).setStyle(tbErrorCss); }
	 * 
	 * validatePwd(); validateEmailStr('emailIdTbId'); validateStr('firstNameTbId');
	 * validateStr('lastNameTbId'); validateStr('addressOneTbId');
	 * validateStr('cityTbId'); validateStr('stateTbId');
	 * validateStr('countryTbId'); validateNum('pinTbId')
	 * 
	 * var cbWidget = zk.Widget.$(jq('$enabledCbId')); if(cbWidget.isChecked()) {
	 * validateNum('totalLimitLbId'); var vmtaCb = zk.Widget.$(jq('$vmtaCbId'));
	 * if(vmtaCb.getValue().indexOf("--") == 0) { valid = false; errorContent +=
	 * '<br/> <li> Select a VMTA for enabled user'; }
	 * 
	 * }
	 * 
	 * validateDates(); if(!valid) { document.getElementById('errorDivId').innerHTML
	 * = defErrorContent + errorContent;
	 * document.getElementById('errorDivId').style.display = 'block'; return; } else
	 * { document.getElementById('errorDivId').style.display = 'none'; }
	 * 
	 * 
	 * //alert('Validation done'); var submitCb = zk.Widget.$(jq('$submitCbId'))
	 * submitCb.setChecked(!submitCb.isChecked());
	 * 
	 * submitCb.fire('onCheck',{value:submitCb.value},{toServer:true},0); }
	 */
	/*
	 * function validatePwd() { var pwd = zk.Widget.$(jq('$passwordTbId')); var
	 * repwd = zk.Widget.$(jq('$rePasswordTbId')); if(trim(pwd.getValue()).length ==
	 * 0 || trim(repwd.getValue()).length == 0 ) { valid = false;
	 * pwd.setStyle(tbErrorCss); repwd.setStyle(tbErrorCss); pwd.setValue("");
	 * repwd.setValue(""); return; }else if(pwd.getValue() != repwd.getValue()) {
	 * valid = false; errorContent += '<br/> <li> Two Passwords must match';
	 * pwd.setStyle(tbErrorCss); repwd.setStyle(tbErrorCss); pwd.setValue("");
	 * repwd.setValue(""); return; } else { pwd.setStyle(tbNormalCss);
	 * repwd.setStyle(tbNormalCss); }
	 * 
	 * }
	 */

	private Label errorDivId;

	public String validateUser() {

		try {
			logger.debug("---just entered to validate the user---");
			boolean valid = true;

			String errContent = Constants.STRING_NILL;

			// var tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
			String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
			String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

			if (!validateOrgName(compNameTbId)) {

				valid = false;
				errContent += "\n Enter valid organization name. ";

			} // if

			if (nameStatusLblId.getStyle().indexOf("red") > 0) {
				valid = false;
				userNameTbId.setStyle(tbErrorCss);
				errContent += "Correct the fields which are marked as red";

			} // if
			else {
				userNameTbId.setStyle(tbNormalCss);
			}

			String username = userNameTbId.getValue();
			userNameTbId.setStyle(tbNormalCss);
			if (username.trim().length() == 0) {
				valid = false;
				userNameTbId.setStyle(tbErrorCss);
				errContent += "\n User name cannot be  empty.";
			}

			if (orgNameStatusLblId.getStyle().indexOf("red") > 0) {
				valid = false;
				compShrtNameTbId.setStyle(tbErrorCss);

			} // if
			else {
				compShrtNameTbId.setStyle(tbNormalCss);
			}

			String orgid = compShrtNameTbId.getValue();
			compShrtNameTbId.setStyle(tbNormalCss);

			if (orgid.trim().length() == 0) {
				valid = false;
				compShrtNameTbId.setStyle(tbErrorCss);
				errContent += "\n Organization id cannot be  empty.";
			}

			if (domainNameStatusLblId.getStyle().indexOf("red") > 0) {
				valid = false;
				domainNameTbId.setStyle(tbErrorCss);

			} // if
			else {
				domainNameTbId.setStyle(tbNormalCss);
			}

			String orgunitname = domainNameTbId.getValue();
			domainNameTbId.setStyle(tbNormalCss);
			if (orgunitname.trim().length() == 0) {
				valid = false;
				domainNameTbId.setStyle(tbErrorCss);
				errContent += "\n Organization unit name cannot be  empty.";
			}

			String fname = firstNameTbId.getValue();
			firstNameTbId.setStyle(tbNormalCss);
			if (fname.trim().length() == 0) {
				valid = false;
				firstNameTbId.setStyle(tbErrorCss);
				errContent += "\n First name cannot be  empty.";
			}

			String lname = lastNameTbId.getValue();
			lastNameTbId.setStyle(tbNormalCss);
			if (lname.trim().length() == 0) {
				valid = false;
				lastNameTbId.setStyle(tbErrorCss);
				errContent += "\n Last name cannot be  empty.";
			}

			String city = cityTbId.getValue();
			cityTbId.setStyle(tbNormalCss);
			if (city.trim().length() == 0) {
				valid = false;
				cityTbId.setStyle(tbErrorCss);
				errContent += "\n City cannot be  empty.";
			}

			String state = stateTbId.getValue();
			stateTbId.setStyle(tbNormalCss);
			if (state.trim().length() == 0) {
				valid = false;
				stateTbId.setStyle(tbErrorCss);
				errContent += "\n State cannot be  empty.";
			}

			String country = countryTbId.getValue();
			countryTbId.setStyle(tbNormalCss);
			if (country.trim().length() == 0) {
				valid = false;
				countryTbId.setStyle(tbErrorCss);
				errContent += "\n Country cannot be  empty.";
			}

			String countryTypeStr = countryLbId.getSelectedItem().getLabel().trim();
			logger.info("SMS country " + countryTypeStr);
			if (!mobilePatternChkBxId.isChecked()) {
				if (countryTypeStr != null && countryTypeStr.equalsIgnoreCase("US")
						|| countryTypeStr.equalsIgnoreCase("Pakistan") || countryTypeStr.equalsIgnoreCase("India")) {
					valid = false;
					// mobilePatternChkBxId.setStyle(tbErrorCss);
					errContent += "\n Enable Mobile Pattern needs to be checked";
				}
			}
			/*
			 * String pwd = passwordTbId.getValue(); String repwd =
			 * rePasswordTbId.getValue(); //
			 * logger.debug("the values of passwords are=======>"+pwd+"====="+repwd); if(
			 * pwd.trim().length() == 0 || repwd.trim().length() == 0 ) {
			 * 
			 * valid = false;
			 * 
			 * passwordTbId.setStyle(tbErrorCss); rePasswordTbId.setStyle(tbErrorCss);
			 * passwordTbId.setValue(""); rePasswordTbId.setValue(""); //return;
			 * 
			 * 
			 * }else if(!pwd.equals(repwd)) { valid = false; //errorContent += '<br/> <li>
			 * Two Passwords must match';
			 * 
			 * passwordTbId.setStyle(tbErrorCss); rePasswordTbId.setStyle(tbErrorCss);
			 * passwordTbId.setValue(""); rePasswordTbId.setValue(""); //return; } else {
			 * passwordTbId.setStyle(tbNormalCss); rePasswordTbId.setStyle(tbNormalCss); }
			 */

			/*************** validating email *******************/
			if (!validateEmiail()) {

				valid = false;
				errContent += "\n Enter valid email";

			} // if
			if (!(validateStr(addressOneTbId) && validateStr(cityTbId) && validateStr(compNameTbId)
					&& validateStr(countryTbId) && validateStr(firstNameTbId) && validateStr(lastNameTbId)
					&& validateStr(stateTbId))) {

				valid = false;
				errContent += "\n Please provide the values which are mandatory.";

			} // if

			if (!validateNum(pinTbId)) {

				valid = false;
				errContent += "\n Enter valid Zip Code";

			} // if

			/*
			 * String pin = pinTbId.getValue().trim(); if( (pin.length() > 6) ||
			 * (pin.length() < 5 ) ) { valid = false; errContent +=
			 * "\n Enter only 5 / 6 digit value for the Zip Code";
			 * 
			 * }
			 */

			/*
			 * String value = phoneTbId.getValue().trim();
			 * 
			 * if(value != null && value.length() > 0 ){ String userPhoneRegex = "\\d+";
			 * Pattern phonePattern = Pattern.compile(userPhoneRegex); Matcher m =
			 * phonePattern.matcher(value); String poneMatch = Constants.STRING_NILL; while
			 * (m.find()) { poneMatch += m.group(); } try { value =
			 * ""+Long.parseLong(poneMatch); } catch (NumberFormatException e) { valid =
			 * false; errContent += "Please provide valid Phone Number"; } value =
			 * Utility.validateUserPhoneNum(value) == false ? null : value; if(value == null
			 * || value.trim().length() == 0){ valid = false; errContent +=
			 * "Please provide valid Phone Number"; } }
			 * 
			 * 
			 * if(! validateDates() ) {
			 * 
			 * valid = false; errContent +=
			 * "\n Please provide proper dates: Start date should be less than the end date / expiry date should not be past"
			 * ;
			 * 
			 * 
			 * }//if
			 */

			if (enabledCbId.isChecked()) {

				if (!validateNum(totalLimitLbId)) {

					valid = false;
					errContent += "\n Please provide valid email limit.";
				}

				if (totalLimitLbId.getValue() == null || totalLimitLbId.getValue() == 0) {

					valid = false;
					errContent += "\n Please provide email limit.";
				}
				/*
				 * if(vmtaCbId.getValue().indexOf("--") == 0) { valid = false; errContent +=
				 * "\n Please select vmta."; }
				 */
				// if

				/*
				 * if( !(vmtaCbId.getSelectedIndex() > 0) ||
				 * !(vmtaAccountNameCbId.getSelectedIndex() > 0) ){ valid = false; errContent +=
				 * "\n Please select vmta."; errContent += "\n Please select email route."; }
				 */

			} // if
			if (countryLbId.getSelectedIndex() == -1 || countryLbId.getSelectedItem().getLabel().trim().isEmpty()) {

				// MessageUtil.setMessage("Please select country.", "color:red;");
				valid = false;
				errContent += "\n Please select country of SMS gateway.";
			}

			// Validate SMS Gateway
			/*
			 * if(smsGateWayCbId.getSelectedIndex() < 1){
			 * 
			 * valid = false; errContent += "\n Please select SMS Gateway."; }else
			 * if(countryCodeTxtBxId.getValue().trim().length() == 0){ valid = false;
			 * errContent += "\n Please provide SMS Country Code"; }
			 */

			if (contact1EmailTbId.getValue().length() > 0
					&& !Utility.validateEmail(contact1EmailTbId.getValue().trim())) {
				valid = false;
				errContent += "\n Please provide valid email for the additional contacts1";
			}

			if (contact2EmailTbId.getValue().length() > 0
					&& !Utility.validateEmail(contact2EmailTbId.getValue().trim())) {
				valid = false;
				errContent += "\n Please provide valid email for the additional contacts2";
			}

			if (contact3EmailTbId.getValue().length() > 0
					&& !Utility.validateEmail(contact3EmailTbId.getValue().trim())) {
				valid = false;
				errContent += "\n Please provide valid email for the additional contacts3";
			}

			/*
			 * String valueCont1 = contact1NumberTbId.getValue().trim();
			 * 
			 * if(valueCont1 != null && valueCont1.length() > 0 ){ String userPhoneRegex =
			 * "\\d+"; Pattern phonePattern = Pattern.compile(userPhoneRegex); Matcher m =
			 * phonePattern.matcher(valueCont1); String poneMatch = Constants.STRING_NILL;
			 * Long phoneLong = null; while (m.find()) { poneMatch += m.group(); } try {
			 * phoneLong = Long.parseLong(poneMatch); } catch (NumberFormatException e) {
			 * valid = false; errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact1";
			 * 
			 * } if(phoneLong != null){ valueCont1 =
			 * Utility.validateUserPhoneNum(phoneLong+"") == false ? null : phoneLong+"";
			 * if(valueCont1 == null || valueCont1.trim().length() == 0){ valid = false;
			 * errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact1"; } }
			 * 
			 * }
			 * 
			 * String valueCont2 = contact2NumberTbId.getValue().trim();
			 * 
			 * if(valueCont2 != null && valueCont2.length() > 0 ){ String userPhoneRegex =
			 * "\\d+"; Pattern phonePattern = Pattern.compile(userPhoneRegex); Matcher m =
			 * phonePattern.matcher(valueCont2); String poneMatch = Constants.STRING_NILL;
			 * Long phoneLong = null; while (m.find()) { poneMatch += m.group(); } try {
			 * phoneLong = Long.parseLong(poneMatch); } catch (NumberFormatException e) {
			 * valid = false; errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact2"; }
			 * if(phoneLong != null){
			 * 
			 * // valueCont2 = Utility.phoneParse(""+phoneLong); valueCont2 =
			 * Utility.validateUserPhoneNum(phoneLong+"") == false ? null : phoneLong+"";
			 * if(valueCont2 == null || valueCont2.trim().length() == 0){ valid = false;
			 * errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact2"; } } }
			 * 
			 * String valueCont3 = contact3NumberTbId.getValue().trim();
			 * 
			 * if(valueCont3 != null && valueCont3.length() > 0 ){ String userPhoneRegex =
			 * "\\d+"; Pattern phonePattern = Pattern.compile(userPhoneRegex); Matcher m =
			 * phonePattern.matcher(valueCont3); String poneMatch = Constants.STRING_NILL;
			 * Long phoneLong = null; while (m.find()) { poneMatch += m.group(); } try {
			 * phoneLong = Long.parseLong(poneMatch); } catch (NumberFormatException e) {
			 * valid = false; errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact3"; }
			 * if(phoneLong != null){
			 * 
			 * //valueCont3 = Utility.phoneParse(""+phoneLong); valueCont3 =
			 * Utility.validateUserPhoneNum(phoneLong+"") == false ? null : phoneLong+"";
			 * if(valueCont3 == null || valueCont3.trim().length() == 0){ valid = false;
			 * errContent +=
			 * "\nPlease provide valid Phone Number for the additional contact3"; } } }
			 */

			if (numberOfContractedEmailsTbId.getValue() != null
					&& !numberOfContractedEmailsTbId.getValue().toString().isEmpty()) {
				if (!checkIfNumber(numberOfContractedEmailsTbId.getValue().toString().trim())) {
					valid = false;
					errContent += "\nPlease provide valid number of contracted emails";
				}
			}

			if (numberOfContractedSmsTbId.getValue() != null
					&& !numberOfContractedSmsTbId.getValue().toString().isEmpty()) {
				if (!checkIfNumber(numberOfContractedSmsTbId.getValue().toString().trim())) {
					valid = false;
					errContent += "\nPlease provide valid number of contracted sms";
				}
			}

			if (!valid && errContent.trim().length() > 0) {

				/*
				 * document.getElementById('').innerHTML = defErrorContent + errorContent;
				 * document.getElementById('errorDivId').style.display = 'block';
				 */
				return errContent;
			}

			return Constants.STRING_NILL;

			// var cbWidget = zk.Widget.$(jq('$enabledCbId'));
			/*
			 * if(enabledCbId.isChecked()) { //validateNum(totalLimitLbId); //var vmtaCb =
			 * zk.Widget.$(jq('$vmtaCbId')); if(vmtaCb.getValue().indexOf("--") == 0) {
			 * valid = false; }//if
			 * 
			 * }//if
			 */
		} catch (WrongValueException e) {

			logger.error("Exception  ::", e);
			return null;
		}

	}// validateUser()
	/*
	 * 
	 * function validateNum(id) { var tb = zk.Widget.$(jq('$' + id)); var val =
	 * tb.getValue(); if(val == undefined) { valid = false; tb.setStyle(tbErrorCss);
	 * } else { if(val <= 0) { valid = false; errorContent += '<br/> <li> Number
	 * must be greater than zero'; tb.setStyle(tbErrorCss); return; }
	 * tb.setStyle(tbNormalCss); } }
	 * 
	 */

	public boolean validateNum(Textbox txtbox) {

		// logger.debug("----just entered with the intbox======>"+txtbox);

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";
		String countryTypeStr = countryLbId.getSelectedItem().getLabel().trim();

		try {
			if (txtbox.isValid()) {

				if (Utility.zipValidateMap.containsKey(countryTypeStr)) {

					String zip = txtbox.getValue().trim();
					boolean zipCode = Utility.validateZipCode(zip, countryTypeStr);
					if (!zipCode) {
						txtbox.setStyle(tbErrorCss);
						return false;
					}

				} else {

					String zip = txtbox.getValue().trim();

					if (zip != null && zip.length() > 0) {

						try {

							Long pinLong = Long.parseLong(zip);

						} catch (NumberFormatException e) {
							// MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
							txtbox.setStyle(tbErrorCss);
							return false;
						}

						if (zip.length() > 6 || zip.length() < 5) {

							// Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
							// MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.",
							// "Color:red", "Top");
							txtbox.setStyle(tbErrorCss);
							return false;

						}
					}
				}
				txtbox.setStyle(tbNormalCss);
			}
			/*
			 * if(txtbox.isValid()){ int str = Integer.parseInt(txtbox.getValue().trim());
			 * if( str <= 0) {
			 * 
			 * txtbox.setStyle(tbErrorCss);
			 * 
			 * 
			 * return false;
			 * 
			 * }//if txtbox.setStyle(tbNormalCss); }
			 */

			return true;
		} catch (Exception e) {

			txtbox.setStyle(tbErrorCss);
			logger.error("Exception  ::", e);
			return false;
		}

	}// validateNum(-)

	public boolean validateNum(Intbox txtbox) {

		// logger.debug("----just entered with the intbox======>"+txtbox);

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

		try {
			if (txtbox.isValid()) {
				Integer str = txtbox.getValue();
				if (str != null && str.intValue() <= 0) {

					txtbox.setStyle(tbErrorCss);

					return false;

				} // if
				txtbox.setStyle(tbNormalCss);
			}

			return true;
		} catch (Exception e) {

			txtbox.setStyle(tbErrorCss);
			logger.error("Exception  ::", e);
			return false;
		}

	}// validateNum(-)

	/*
	 * function validateDates() {
	 * 
	 * var startDb = zk.Widget.$(jq('$startDbId')); var expDb =
	 * zk.Widget.$(jq('$expDbId')); var startVal = startDb.getValue(); var expVal =
	 * expDb.getValue()
	 * 
	 * var newStartDate = new Date(startVal.getYear()+1900, startVal.getMonth(),
	 * startVal.getDate()); var newExpDate = new Date(expVal.getYear()+1900,
	 * expVal.getMonth(), expVal.getDate());
	 * 
	 * if((newStartDate > newExpDate )) { errorContent += '<br/> <li> Start date
	 * must be less than expiry date ';
	 * 
	 * zk.Widget.$(jq('$startDbId_img')).setVisible(true);
	 * zk.Widget.$(jq('$expDbId_img')).setVisible(true); valid=false;
	 * 
	 * }else {
	 * 
	 * zk.Widget.$(jq('$startDbId_img')).setVisible(false);
	 * zk.Widget.$(jq('$expDbId_img')).setVisible(false); } }
	 */

	public void onClick$resetBtnid() {
		//

		userNameTbId.setValue(Constants.STRING_NILL);
//		passwordTbId.setValue(Constants.STRING_NILL);
//		rePasswordTbId.setValue(Constants.STRING_NILL);
		emailIdTbId.setValue(Constants.STRING_NILL);
		firstNameTbId.setValue(Constants.STRING_NILL);
		lastNameTbId.setValue(Constants.STRING_NILL);
		compNameTbId.setValue(Constants.STRING_NILL);
		addressOneTbId.setValue(Constants.STRING_NILL);
		addressTwoTbId.setValue(Constants.STRING_NILL);
		cityTbId.setValue(Constants.STRING_NILL);
		stateTbId.setValue(Constants.STRING_NILL);
		countryTbId.setValue(Constants.STRING_NILL);
		pinTbId.setValue(Constants.STRING_NILL);
		phoneTbId.setValue(Constants.STRING_NILL);
		enabledCbId.setValue(Constants.STRING_NILL);
		totalLimitLbId.setValue(null);
		nameStatusLblId.setValue(Constants.STRING_NILL);
		vmtaCbId.setSelectedIndex(0);
		// vmtaStatusLblId.setValue(Constants.STRING_NILL);
		compNameTbId.setValue(Constants.STRING_NILL);
		compShrtNameTbId.setValue(Constants.STRING_NILL);
		orgNameStatusLblId.setValue(Constants.STRING_NILL);
		domainNameTbId.setValue(Constants.STRING_NILL);
		domainNameStatusLblId.setValue(Constants.STRING_NILL);
		errorDivId.setValue(Constants.STRING_NILL);
		emailLblId.setValue(Constants.STRING_NILL);
		firstNameLblId.setValue(Constants.STRING_NILL);
		lastNameLblId.setValue(Constants.STRING_NILL);
		addressOneLblId.setValue(Constants.STRING_NILL);
		CityLblId.setValue(Constants.STRING_NILL);
		stateLblId.setValue(Constants.STRING_NILL);
		cLbId.setValue(Constants.STRING_NILL);
		orgStatusLblId.setValue(Constants.STRING_NILL);
		errorDivId.setStyle("color:#EF2C2C;font-weight:bold;font-size:14px;font-family:verdena;margin-left:8px;");
		errorDivId.setValue(Constants.STRING_NILL);
		sBRadioId.setChecked(true);

		// Added for sms settings

		// clearSMSSettings();

		type = null;
		user = null;
		createUserPath();
		Components.removeAllChildren(fileListlbId);
	}

	private Image startDbId_img, expDbId_img;

	public boolean validateDates() {

		Calendar strtDate = startDbId.getServerValue();
		Calendar endDate = expDbId.getServerValue();

		if (strtDate == null || endDate == null) {
			startDbId_img.setVisible(true);
			expDbId_img.setVisible(true);
			return false;
		}

		if (endDate != null && endDate.before(Calendar.getInstance())) {
			return false;
		}

		if (strtDate.getTimeInMillis() > endDate.getTimeInMillis()) {

			startDbId_img.setVisible(true);
			expDbId_img.setVisible(true);

			return false;

		} else {
			startDbId_img.setVisible(false);
			expDbId_img.setVisible(false);

			return true;
		}
	}// validateDates()

	/*
	 * var cbWidget = zk.Widget.$(jq('$enabledCbId')); if(cbWidget.isChecked()) {
	 * validateNum('totalLimitLbId'); var vmtaCb = zk.Widget.$(jq('$vmtaCbId'));
	 * if(vmtaCb.getValue().indexOf("--") == 0) { valid = false; errorContent +=
	 * '<br/> <li> Select a VMTA for enabled user'; }
	 * 
	 * }
	 */

	/*
	 * function validateStr(id) { var tb = zk.Widget.$(jq('$' + id)); var name =
	 * tb.getValue(); if(trim(name).length == 0) { valid = false;
	 * tb.setStyle(tbErrorCss); } else { tb.setStyle(tbNormalCss); } }
	 */
	public boolean validateOrgName(Textbox orgNameTb) {

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

		if (!Utility.validateFromName(orgNameTb.getText())) {
			orgNameTb.setStyle(tbErrorCss);
			return false;
		}
		orgNameTb.setStyle(tbNormalCss);
		return true;

	}

	public boolean validateStr(Textbox txtBox) {

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

		String str = txtBox.getValue();

		if (str.trim().length() == 0) {

			txtBox.setStyle(tbErrorCss);
			return false;
		} else {

			txtBox.setStyle(tbNormalCss);
			return true;
		}

	}// validateStr

	public boolean validateEmiail() {

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

		String email = emailIdTbId.getValue();
		if (email.trim().length() == 0) {

		} // if

		if (!Utility.validateEmail(email)) {

			logger.debug(" emailString :" + email);
			emailIdTbId.setStyle(tbErrorCss);
			return false;

		} // if

		emailIdTbId.setStyle(tbNormalCss);
		return true;

	}

	/*
	 * function validateStr(id) { var tb = zk.Widget.$(jq('$' + id)); var name =
	 * tb.getValue(); if(trim(name).length == 0) { valid = false;
	 * tb.setStyle(tbErrorCss); } else { tb.setStyle(tbNormalCss); } }
	 * 
	 * function validateEmailStr(id) { var emailtb = zk.Widget.$(jq('$' + id)); var
	 * email = emailtb.getValue(); if(trim(email).length == 0 ||
	 * !validateEmail(email) ) { valid = false; errorContent += '<br/> <li> Provide
	 * valid Email-id'; emailtb.setStyle(tbErrorCss); } else {
	 * emailtb.setStyle(tbNormalCss); } }
	 * 
	 * function validateNum(id) { var tb = zk.Widget.$(jq('$' + id)); var val =
	 * tb.getValue(); if(val == undefined) { valid = false; tb.setStyle(tbErrorCss);
	 * } else { if(val <= 0) { valid = false; errorContent += '<br/> <li> Number
	 * must be greater than zero'; tb.setStyle(tbErrorCss); return; }
	 * tb.setStyle(tbNormalCss); } }
	 */

	public void defaultCreatUserSetting(Users user) throws Exception {

		if (user != null) {
//		    logger.info("in defaultCreateUser==========>"+user.getUserName());	
			userNameTbId.setValue(Utility.getOnlyUserName(user.getUserName()));
//			userNameTbId.setValue(user.getUserName());
			userNameTbId.setDisabled(true);

//			passwordTbId.setValue("********");
//			passwordTbId.setDisabled(true);
//			rePasswordTbId.setValue("********");
//			rePasswordTbId.setDisabled(true);
			emailIdTbId.setValue(user.getEmailId());
			firstNameTbId.setValue(user.getFirstName());
			lastNameTbId.setValue(user.getLastName());
			compNameTbId.setValue(user.getCompanyName());
			addressOneTbId.setValue(user.getAddressOne());

			numOfStoresTbId.setValue(user.getContractStores());
			numOfContactsTbId.setValue(user.getContractContacts());
			smsTbId.setValue(user.getContractSMSAdded());
			drTbId.setValue(user.getContractEReceiptsRestricted());

			// set loyalty radio button
			if (user.getloyaltyServicetype() != null) {
				if (user.getloyaltyServicetype().equals("SB")) {
					sBRadioId.setChecked(true);
					oCRadioId.setDisabled(true);
				} else if (user.getloyaltyServicetype().equals("SBToOC")) {
					sBToOCRadioId.setChecked(true);
					oCRadioId.setDisabled(true);
					sBRadioId.setDisabled(true);
				} else {
					oCRadioId.setChecked(true);
					sBToOCRadioId.setDisabled(true);
					sBRadioId.setDisabled(true);
				}
			}

			if (user.isOptinMobileByDefault())
				mobileOptinOnEnrollChkId.setChecked(true);

			if (user.getAddressTwo() != null) {

				addressTwoTbId.setValue(user.getAddressTwo());

			}
			cityTbId.setValue(user.getCity());
			stateTbId.setValue(user.getState());
			countryTbId.setValue(user.getCountry());

			UserOrganization userOrg = user.getUserOrganization();

			if (userOrg != null) {
				compNameTbId.setValue(userOrg.getOrganizationName());

				compShrtNameTbId.setValue(userOrg.getOrgExternalId());

				for (int i = 0; i < brandingLbId.getItemCount(); i++) {

					if (userOrg.getBranding().equalsIgnoreCase((String) brandingLbId.getItemAtIndex(i).getValue())) {

						brandingLbId.setSelectedIndex(i);
						break;
					}

				}
				String orgType = userOrg.getClientType();
				if (orgType != null) {

					for (int i = 0; i < orgTypeLbId.getItemCount(); i++) {

						if (userOrg.getClientType()
								.equalsIgnoreCase((String) orgTypeLbId.getItemAtIndex(i).getValue())) {

							orgTypeLbId.setSelectedIndex(i);
							break;
						}

					}

					orgTypeLbId.setDisabled(true);
				}

			}
			compNameTbId.setDisabled(true);

			compShrtNameTbId.setDisabled(true);

			brandingLbId.setDisabled(true);

			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());

			// aded for country type

			if (user.getCountryType() != null) {

				for (Listitem item : countryLbId.getItems()) {

					if (item.getLabel().trim() != null && item.getLabel().trim().equals(user.getCountryType())) {

						item.setSelected(true);
						break;
					}

				}

			}

			countryCodeTxtId.setValue("+" + user.getCountryCarrier());

			String countryStr = user.getCountryType();

			if (countryStr != null && !countryStr.isEmpty())
				countryLbId.setDisabled(true);

			// Set<UsersDomains> userDomainSet = user.getUserDomains();
			String userDomainStr = Constants.STRING_NILL;
			for (UsersDomains usersDomains : domainsList) {

				/*
				 * if(usersDomains.getDomainName() != null) { if(userDomainStr.trim().length() >
				 * 0) userDomainStr += ",";
				 * 
				 * if(userDomainStr.length()<=60) userDomainStr += usersDomains.getDomainName();
				 * else break; }
				 */
				if (usersDomains.getDomainName() != null) {
					userDomainStr = usersDomains.getDomainName();
					break;
				}

			}
			domainNameTbId.setValue(userDomainStr);
			domainNameTbId.setDisabled(true);

			try {
				if (user.getPinCode() != null && user.getPinCode().trim().length() > 0) {
					pinTbId.setValue(user.getPinCode());
				}
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}

			phoneTbId.setValue(user.getPhone());
			enabledCbId.setChecked(user.isEnabled());

			if (user.getVmta() != null) {
				// setting the previously saved VMTA.
				List<Component> componentItemList = vmtaCbId.getChildren();
				Comboitem ci = null;

				for (Component aComponent : componentItemList) {
					ci = (Comboitem) aComponent;
					if (ci.getLabel().toString().equalsIgnoreCase(user.getVmta().getVmtaName())) {
						vmtaCbId.setSelectedItem(ci);
						break;
					}
				}
				// setting the previously saved VMTA Account.
				populateVmtaAccountNameCbId();
				List<Component> componentItemListForAcc = vmtaAccountNameCbId.getChildren();
				for (Component aComponent : componentItemListForAcc) {
					ci = (Comboitem) aComponent;
					if (ci.getLabel().toString().equalsIgnoreCase(user.getVmta().getAccountName())) {
						vmtaAccountNameCbId.setSelectedItem(ci);
						break;
					}
				}
			}

			if (user.getEmailCount() != null) {
				Users updatedUser = usersDao.find(user.getUserId());
				totalLimitLbId.setValue(updatedUser.getEmailCount());
			}
			if (user.getSmsCount() != null) {
				userSmsLimitIntBoxId.setValue(user.getSmsCount());
			}

			if (userOrg != null) {
				minTxtId.setValue(userOrg.getMinNumberOfDigits());
				maxTxtId.setValue(userOrg.getMaxNumberOfDigits());
				mobilePatternChkBxId.setChecked(userOrg.isMobilePattern());

			}
			// logger.info("--hai creta--");
			// java.util.Date date=user.getPackageStartDate().getTime();
			// date=new SimpleDateFormat("dd MMM, yyyy").parse(date.toString());
			try {
				logger.debug(user.getPackageExpiryDate().getTime());
				startDbId.setValue(user.getPackageStartDate().getTime());
				expDbId.setValue(user.getPackageExpiryDate().getTime());
				expDbId.setFormat(MyCalendar.FORMAT_STDATE);

				// logger.debug(expDbId.getValue());
			} catch (WrongValueException e) {
				logger.error("Exception  ::", e);
			}

			List<UsersAdditionalContactDetails> addtnlContacts = usersAdditionalContactDetailsDao
					.findByUserId(user.getUserId());

			if (addtnlContacts != null && !addtnlContacts.isEmpty()) {
				if (addtnlContacts.size() > 0) {

					contact1NameTbId.setValue(addtnlContacts.get(0).getName());
					contact1NumberTbId.setValue(addtnlContacts.get(0).getNumber());
					contact1EmailTbId.setValue(addtnlContacts.get(0).getEmail());
					contact1PositionTbId.setValue(addtnlContacts.get(0).getPosition());

					contact1EmailTbId.setAttribute("contDbObj", addtnlContacts.get(0));

				}
				if (addtnlContacts.size() > 1) {

					contact2NameTbId.setValue(addtnlContacts.get(1).getName());
					contact2NumberTbId.setValue(addtnlContacts.get(1).getNumber());
					contact2EmailTbId.setValue(addtnlContacts.get(1).getEmail());
					contact2PositionTbId.setValue(addtnlContacts.get(1).getPosition());

					contact2EmailTbId.setAttribute("contDbObj", addtnlContacts.get(1));
				}
				if (addtnlContacts.size() > 2) {

					contact3NameTbId.setValue(addtnlContacts.get(2).getName());
					contact3NumberTbId.setValue(addtnlContacts.get(2).getNumber());
					contact3EmailTbId.setValue(addtnlContacts.get(2).getEmail());
					contact3PositionTbId.setValue(addtnlContacts.get(2).getPosition());

					contact3EmailTbId.setAttribute("contDbObj", addtnlContacts.get(2));

				}
			}

			ProductsUsed products = productsUsedDao.findByUserId(user.getUserId());

			if (products != null) {
				numberOfContractedEmailsTbId.setValue(products.getNumberOfContractedEmails() != null
						? products.getNumberOfContractedEmails().toString().trim()
						: Constants.STRING_NILL);
				numberOfContractedSmsTbId.setValue(products.getNumberOfContractedSMS() != null
						? products.getNumberOfContractedSMS().toString().trim()
						: Constants.STRING_NILL);
				List<Listitem> versionList = posSystemVersionLbId.getItems();
				for (Listitem version : versionList) {

					if (version.equals(products.getPOSSystemVersion()))
						posSystemVersionLbId.setSelectedItem(version);

				}
				if (posSystemVersionLbId.getSelectedItem() == null)
					posSystemVersionLbId.setSelectedIndex(0);
				// posSystemVersionTbId.setValue(products.getPOSSystemVersion() != null ?
				// products.getPOSSystemVersion().trim() : Constants.STRING_NILL);
				optDRVersionTbId.setValue(
						products.getOptDigitalRecieptVersion() != null ? products.getOptDigitalRecieptVersion().trim()
								: Constants.STRING_NILL);
				optLoyaltyVersionTbId
						.setValue(products.getOptLoyaltyVersion() != null ? products.getOptLoyaltyVersion().trim()
								: Constants.STRING_NILL);
				optPromoVersionTbId
						.setValue(products.getOptPromoVersion() != null ? products.getOptPromoVersion().trim()
								: Constants.STRING_NILL);
				optIntelVersionTbId
						.setValue(products.getOptIntelVersion() != null ? products.getOptIntelVersion().trim()
								: Constants.STRING_NILL);
				optSyncVersionTbId.setValue(products.getOptSyncVersion() != null ? products.getOptSyncVersion().trim()
						: Constants.STRING_NILL);

				posSystemVersionLbId.setAttribute("prodDbObj", products);
			}

			submitBtnId.setLabel("Update");
			cancelBtnId.setVisible(true);

			Byte tempval = user.getFooterEditor();
			if (tempval != null) {

				externalEdCbId.setChecked((tempval & Integer.parseInt(externalEdCbId.getValue().toString())) > 0);
				plainTextEdCbId.setChecked((tempval & Integer.parseInt(plainTextEdCbId.getValue().toString())) > 0);
				plainHtmlEdCbId.setChecked((tempval & Integer.parseInt(plainHtmlEdCbId.getValue().toString())) > 0);
				blockEdCbId.setChecked((tempval & Integer.parseInt(blockEdCbId.getValue().toString())) > 0);

			}

			List<Listitem> versionList = posSystemVersionLbId.getItems();
			for (Listitem version : versionList) {
				if (user.getPOSVersion() != null && user.getPOSVersion().equalsIgnoreCase(version.getLabel())) {
					posSystemVersionLbId.setSelectedItem(version);
				}

			}

		} else {
			logger.debug("UserType is >>>>>>>" + user);
			TimeZone timezone = (TimeZone) Sessions.getCurrent().getAttribute("clientTimeZone");
			MyCalendar cal = new MyCalendar(timezone);
			startDbId.setValue(cal.getTime());
			cal = new MyCalendar(timezone);
			cal.set(MyCalendar.MONTH, cal.get(MyCalendar.MONTH) + 1);
			expDbId.setValue(cal.getTime());
		}
	}

	public void getSenderIds(Users user) {

		logger.info("----In getSenderIds()----");
		List<UserSMSSenderId> tempSenderIds = usersDao.getSenderIds(user.getUserName());
		Comboitem item = null;
		item = new Comboitem("--Select Sender Id--");
		item.setParent(userSenderIdsCbId);
		if (tempSenderIds.size() > 0) {
			for (UserSMSSenderId userSMSSenderId : tempSenderIds) {

				item = new Comboitem(userSMSSenderId.getSenderId());
				item.setValue(userSMSSenderId);
				item.setParent(userSenderIdsCbId);

			} // for
		}
		userSenderIdsCbId.setSelectedIndex(0);
	}// getSenderIds

	private Listbox brandingLbId, orgTypeLbId;
	private Textbox domainNameTbId;
	private boolean isValidDomainName = true;
	private boolean isValidEmailID = true, isValidFirstName = true,isValidlastName = true,
			isValidCity = true, isValidState = true, isValidCountry = true, isValidAddress = true;

	public void onCheck$submitCbId() {
		try {
			String tempPwd = Constants.STRING_NILL;
			logger.debug("-- Just Entered --");
			MessageUtil.clearMessage();

			String userName = userNameTbId.getValue().trim() + "__org__" + compShrtNameTbId.getValue().trim();
			UserOrganization userOrg;

			try {
				if (type == null && user == null) {
					onBlur$compNameTbId();
					if (!isValidCompName) {
						MessageUtil.setMessage(
								"Organization name is empty or it already exists. Please provide a valid name.",
								"color:red", "TOP");
						return;
					}
					onBlur$compShrtNameTbId();
					if (!isValidOrgName) {

						MessageUtil.setMessage(
								"Organization ID is empty or it already exists. Please provide a valid short name.",
								"color:red", "TOP");
						return;
					}

					onBlur$domainNameTbId();
					if (!isValidDomainName) {
						MessageUtil.setMessage(
								"Organization Unit Name is empty or it already exists. Please provide a valid name.",
								"color:red", "TOP");
						return;
					}

					onBlur$userNameTbId();
					if (!isValidUserName) {
						MessageUtil.setMessage("Username is empty or it already exists. Please provide a valid name.",
								"color:red", "TOP");
						return;
					}
					onBlur$emailIdTbId();
					if (!isValidEmailID) {
						MessageUtil.setMessage("Email Id cannot be empty. Please provide valid email Id.", "color:red",
								"TOP");
						return;
					}
					onBlur$firstNameTbId();
					if (!isValidFirstName) {
						MessageUtil.setMessage("First name cannot be empty. Please provide first name.", "color:red",
								"TOP");
						return;
					}
					onBlur$lastNameTbId();
					if (!isValidlastName) {
						MessageUtil.setMessage("Last name cannot be empty. Please provide last name.", "color:red",
								"TOP");
						return;
					}
					onBlur$addressOneTbId();
					if (!isValidAddress) {
						MessageUtil.setMessage("Address cannot be empty. Please provide address.", "color:red", "TOP");
						return;
					}
					onBlur$cityTbId();
					if (!isValidCity) {
						MessageUtil.setMessage("City cannot be empty. Please provide city.", "color:red", "TOP");
						return;
					}
					onBlur$stateTbId();
					if (!isValidState) {
						MessageUtil.setMessage("State cannot be empty. Please provide state.", "color:red", "TOP");
						return;
					}
					onBlur$countryTbId();
					if (!isValidCountry) {
						MessageUtil.setMessage("Country cannot be empty. Please provide country.", "color:red", "TOP");
						return;
					}
					userName = userNameTbId.getValue().trim() + "__org__" + compShrtNameTbId.getValue().trim();

					if (minTxtId.getValue() == null || minTxtId.getValue() <= 0 || maxTxtId.getValue() == null
							|| maxTxtId.getValue() <= 0) {
						MessageUtil.setMessage("Please provide positive number for min and max mobile length for user.",
								"color:red;");
						return;
					}

					if (minTxtId.getValue() > maxTxtId.getValue()) {
						MessageUtil.setMessage("Min mobile length is greater than max.", "color:red;");
						return;
					}
					//UserOrganization userOrg = new UserOrganization();
					userOrg = new UserOrganization();
					userOrg.setCreatedDate(Calendar.getInstance());
					userOrg.setOrganizationName(compNameTbId.getValue());
					userOrg.setOrgExternalId(compShrtNameTbId.getValue().trim());
					userOrg.setBranding((String) brandingLbId.getSelectedItem().getValue());
					userOrg.setClientType((String) orgTypeLbId.getSelectedItem().getValue());
					// set Deafault value
					userOrg.setOrgStatus(OCConstants.STATUS_ACTIVE);
					userOrg.setMinNumberOfDigits(minTxtId.getValue());
					userOrg.setMaxNumberOfDigits(maxTxtId.getValue());
					userOrg.setMobilePattern(mobilePatternChkBxId.isChecked());

					// usersDao.saveOrUpdate(userOrg);
					usersDaoForDML.saveOrUpdate(userOrg);

					UsersDomains usersDomains = new UsersDomains();
					usersDomains.setCreatedDate(Calendar.getInstance());
					usersDomains.setDomainManagerId(GetUser.getUserObj());
					usersDomains.setDomainName(domainNameTbId.getValue().trim());
					usersDomains.setUserOrganization(userOrg);
					usersDomainsDaoForDML.saveOrUpdate(usersDomains);

					Set<UsersDomains> userDominSet = new HashSet<UsersDomains>();
					userDominSet.add(usersDomains);

					user = new Users();
					user.setUserName(userName);
					// String hash = Utility.encryptPassword(userName,
					// passwordTbId.getValue().trim());

					/*
					 * Md5PasswordEncoder md5 = new Md5PasswordEncoder(); String hash =
					 * md5.encodePassword(passwordTbId.getValue().trim(),userName);
					 */

					tempPwd = Long.toHexString(Double.doubleToLongBits(Math.random()));
					logger.info("generated tempPwd " + tempPwd);
					String hash = Utility.encryptPassword(userName, tempPwd);

					user.setTempPwd(tempPwd);
					user.setPassword(hash);
					user.setUsedEmailCount(0);
					user.setUsedSmsCount(0);
					user.setCreatedDate(Calendar.getInstance());
					user.setUserOrganization(userOrg);
					user.setUserDomains(userDominSet);
					user.setAccountType(Constants.USER_ACCOUNT_TYPE_PRIMARY);

				}

				onBlur$emailIdTbId();
				if (!isValidEmailID) {
					MessageUtil.setMessage("Email Id cannot be empty. Please provide valid email Id.", "color:red",
							"TOP");
					return;
				}
				onBlur$firstNameTbId();
				if (!isValidFirstName) {
					MessageUtil.setMessage("First name cannot be empty. Please provide first name.", "color:red",
							"TOP");
					return;
				}
				onBlur$lastNameTbId();
				if (!isValidlastName) {
					MessageUtil.setMessage("Last name cannot be empty. Please provide last name.", "color:red", "TOP");
					return;
				}
				onBlur$addressOneTbId();
				if (!isValidAddress) {
					MessageUtil.setMessage("Address cannot be empty. Please provide address.", "color:red", "TOP");
					return;
				}
				onBlur$cityTbId();
				if (!isValidCity) {
					MessageUtil.setMessage("City cannot be empty. Please provide city.", "color:red", "TOP");
					return;
				}
				onBlur$stateTbId();
				if (!isValidState) {
					MessageUtil.setMessage("State cannot be empty. Please provide state.", "color:red", "TOP");
					return;
				}
				onBlur$countryTbId();
				if (!isValidCountry) {
					MessageUtil.setMessage("Country cannot be empty. Please provide country.", "color:red", "TOP");
					return;
				}
				userName = userNameTbId.getValue().trim() + "__org__" + compShrtNameTbId.getValue().trim();
				// APP-2089
				if (!validateNum(pinTbId)) {
					MessageUtil.setMessage("Please provide appropriate Zip Code.", "color:red;");
					return;
				} // if

				
				if (minTxtId.getValue() == null || minTxtId.getValue() <= 0 || maxTxtId.getValue() == null
						|| maxTxtId.getValue() <= 0) {
					MessageUtil.setMessage("Please provide positive number for min and max mobile length for user.",
							"color:red;");
					return;
				}

				if (minTxtId.getValue() > maxTxtId.getValue()) {
					MessageUtil.setMessage("Min mobile length is greater than max.", "color:red;");
					return;
				}

				String country = countryLbId.getSelectedItem().getLabel().trim();
				if (!mobilePatternChkBxId.isChecked()) {
					if (country != null && country.equalsIgnoreCase(Constants.SMS_COUNTRY_US)
							|| country.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)
							|| country.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)
							|| country.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)
							|| country.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {
						MessageUtil.setMessage("Enable Mobile Pattern needs to be checked.", "color:red;");
						return;
					}
				}

				user.getUserOrganization().setMinNumberOfDigits(minTxtId.getValue());
				user.getUserOrganization().setMaxNumberOfDigits(maxTxtId.getValue());
				user.getUserOrganization().setMobilePattern(mobilePatternChkBxId.isChecked());
				// usersDao.saveOrUpdate(user.getUserOrganization());
				usersDaoForDML.saveOrUpdate(user.getUserOrganization());

				user.setEmailId(emailIdTbId.getValue());
				user.setFirstName(firstNameTbId.getValue());
				user.setLastName(lastNameTbId.getValue());
				user.setCompanyName(compNameTbId.getValue());
				user.setAddressOne(addressOneTbId.getValue());

				user.setContractStores(numOfStoresTbId.getValue());
				user.setContractContacts(numOfContactsTbId.getValue());
				user.setContractSMSAdded(smsTbId.getValue());
				user.setContractEReceiptsRestricted(drTbId.getValue());

				user.setAddressTwo(addressTwoTbId.getValue());
				user.setCity(cityTbId.getValue());
				user.setState(stateTbId.getValue());
				user.setCountry(countryTbId.getValue());
				user.setPinCode(pinTbId.getValue().trim());

				String value = phoneTbId.getValue().trim();

				if (value != null && value.length() > 0) {
					String userPhoneRegex = "\\d+";
					Pattern phonePattern = Pattern.compile(userPhoneRegex);
					Matcher m = phonePattern.matcher(value);
					String poneMatch = Constants.STRING_NILL;
					while (m.find()) {
						poneMatch += m.group();
					}
					try {
						value = "" + Long.parseLong(poneMatch);
					} catch (NumberFormatException e) {
						value = null;
					}
					if (value == null || value.trim().length() == 0) {
						MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
						return;
					}
					/*
					 * //value = Utility.phoneParse(value, userOrg1); value =
					 * Utility.validateUserPhoneNum(value) == false ? null : value;
					 */
					else {
						value = Utility.phoneParseForCreateUser(value, minTxtId.getValue(), maxTxtId.getValue(),
								mobilePatternChkBxId.isChecked());
						if (value == null || value.trim().length() == 0) {
							MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
							return;
						}
					}
					user.setPhone(value);
				}

				user.setEnabled(enabledCbId.isChecked());
				/*
				 * if(user != null) { user.setEmailCount(user.getEmailCount() +
				 * totalLimitLbId.getValue()); }
				 */
				user.setEmailCount(totalLimitLbId.getValue());
				// user.setSmsCount(totalSmsLimitLbId.getValue());

				user.setPackageStartDate(startDbId.getServerValue());
				user.setPackageExpiryDate(expDbId.getServerValue());
				int tempval = 0;
				if (externalEdCbId.isChecked()) {
					tempval = tempval + Integer.parseInt(externalEdCbId.getValue().toString());// ,plainTextEdCbId,plainHtmlEdCbId,blockEdCbId)
				}
				if (plainTextEdCbId.isChecked()) {
					tempval = tempval + Integer.parseInt(plainTextEdCbId.getValue().toString());
				}
				if (plainHtmlEdCbId.isChecked()) {
					tempval = tempval + Integer.parseInt(plainHtmlEdCbId.getValue().toString());
				}
				if (blockEdCbId.isChecked()) {
					tempval = tempval + Integer.parseInt(blockEdCbId.getValue().toString());
				}
				logger.debug("tempval is--->" + tempval);
				user.setFooterEditor((byte) tempval);
				if (vmtaCbId.getSelectedIndex() > 0 && vmtaAccountNameCbId.getSelectedIndex() > 0) {
					user.setVmta((Vmta) vmtaAccountNameCbId.getSelectedItem().getAttribute("aVmtaObject"));
				} else {
					user.setVmta(null);
				}

				String countryTypeStr = countryLbId.getSelectedItem().getLabel().trim();
				user.setCountryType(countryTypeStr);

				String countryCode = countryCodeTxtId.getValue().trim().replace("+", "");

				if (countryCode.length() > 0) {

					// user.setCountryCarrier(Byte.parseByte(countryCode));
					user.setCountryCarrier(Short.parseShort(countryCode));
				} else {
					user.setCountryCarrier(null);
				}

				// set SMS Gateway
				/*
				 * if(smsGateWayCbId.getSelectedIndex() == 1)
				 * user.setUserSMSTool(Constants.USER_SMSTOOL_CLICKATELL); else
				 * if(smsGateWayCbId.getSelectedIndex() == 2)
				 * user.setUserSMSTool(Constants.USER_SMSTOOL_MVAYOO);
				 */

				// set CountryCode
				/*
				 * if(countryCodeTxtBxId.getValue().trim().length() > 0)
				 * user.setCountryCarrier(Byte.parseByte(countryCodeTxtBxId.getValue().trim()));
				 * 
				 * else user.setCountryCarrier(null);
				 */

				if (approveTempCmbBoxId.getSelectedIndex() != 0) {
					Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
					if (combItem != null) {
						TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
						if (trTemplateObj.getStatus() == 1) {
							if (tempIdRow.isVisible() && !templateId.getText().isEmpty())
								trTemplateObj.setTemplateRegisteredId(templateId.getText());
							transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
						}
					}
				}

				if (uaeApproveTempCmbBoxId.getSelectedIndex() != 0) {
					Comboitem combItem = uaeApproveTempCmbBoxId.getSelectedItem();
					if (combItem != null) {
						TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
						if (trTemplateObj.getStatus() == 1) {
							//if (uaeTempIdRow.isVisible() && !uaeTemplateId.getText().isEmpty())
								//trTemplateObj.setTemplateRegisteredId(uaeTemplateId.getText());
							transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
						}
					}
				}

				try {

					SecRolesDao secRolesDao = (SecRolesDao) SpringUtil.getBean("secRolesDao");
					if (type == null) {
						/*
						 * Authorities authority = new Authorities(Constants.ROLE_USER, userName);//from
						 * this page we only create super user
						 * 
						 * usersDao.saveOrUpdate(authority);
						 * 
						 * authority = new Authorities(Constants.ROLE_USER_SUPER, userName);//from this
						 * page we only create super user
						 * 
						 * usersDao.saveOrUpdate(authority);
						 */

						// Assigning Loyalty role(OC or SB_to_OC)
						List<SecRoles> userRolesList = null;
						if (sBToOCRadioId.isChecked() || oCRadioId.isChecked()) {
							userRolesList = new ArrayList<>();
							List<SecRoles> tempList = secRolesDao
									.findRoleByType("'" + Constants.SECROLE_TYPE_ADMIN + "'");
							for (SecRoles role : tempList) {
								if (!role.getName().equals("Super User"))
									userRolesList.add(role);
							}
							userRolesList.add(secRolesDao.findBy(Constants.ROLE_USER_OC_LOYALTY));
							userRolesList.add(secRolesDao.findBy("Promotions_Power_User"));
						} else {
							userRolesList = secRolesDao.findRoleByType("'" + Constants.SECROLE_TYPE_ADMIN + "'");
							userRolesList.add(secRolesDao.findBy(Constants.ROLE_USER_SB_LOYALTY));
						}

						logger.info(
								"----Assigning roles for user at the creating time------********-------CreateUser--------*********----------"
										+ userRolesList);
						Set<SecRoles> secRolesSet = new HashSet<SecRoles>();
						secRolesSet.addAll(userRolesList);
						user.setRoles(secRolesSet);
					} else {

						// if((sBToOCRadioId.isChecked() || oCRadioId.isChecked()) &&
						// (user.getloyaltyServicetype()==null ||
						// user.getloyaltyServicetype().equalsIgnoreCase("SB"))){
						if (sBToOCRadioId.isChecked() || oCRadioId.isChecked()) {
							List<SecRoles> userRolesList;
							// userRolesList =
							// secRolesDao.findRoleByType("'"+Constants.SECROLE_TYPE_ADMIN+"'");
							SecRoles secRoles = secRolesDao.findBy(Constants.ROLE_USER_OC_LOYALTY);
							userRolesList = secRolesDao.findByUserId(user.getUserId());
							Set<SecRoles> rolesSet = new HashSet<SecRoles>();
							boolean isOCUser = false;
							// Set<SecRoles> rolesSet = user.getRoles();
							logger.info(
									"----Assigning roles for user at the creating time------********-------EditUser--------*********----------");
							for (SecRoles sB_Users : userRolesList) {
								if (sB_Users.getName().equalsIgnoreCase(Constants.ROLE_USER_SB_LOYALTY)) {
									continue;
									// rolesSet.remove(sB_Users);
								} else if (sB_Users.getName().equalsIgnoreCase(Constants.ROLE_USER_OC_LOYALTY)) {
									isOCUser = true;
								}
								rolesSet.add(sB_Users);
							}
							if (!isOCUser)
								rolesSet.add(secRoles);
							user.setRoles(rolesSet);

						}

					}
					// Loyalty service type
					user.setloyaltyServicetype(loyaltyServicetypeLbId.getSelectedItem().getValue().toString());
					user.setOptinMobileByDefault(mobileOptinOnEnrollChkId.isChecked());
					try {
						// usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);

						CheckEmailSmsCreditThread checkEmail = new CheckEmailSmsCreditThread();
						checkEmail.checkEmailSmsCredit(user);

					} catch (Exception e) {

						logger.error("Exception for user save or update is ::", e);

					}

					UsersAdditionalContactDetails contact1 = null, contact2 = null, contact3 = null;
					ProductsUsed products = null;

					if (type != null) {
						contact1 = (UsersAdditionalContactDetails) (contact1EmailTbId.getAttribute("contDbObj") != null
								? contact1EmailTbId.getAttribute("contDbObj")
								: new UsersAdditionalContactDetails());
						contact2 = (UsersAdditionalContactDetails) (contact2EmailTbId.getAttribute("contDbObj") != null
								? contact2EmailTbId.getAttribute("contDbObj")
								: new UsersAdditionalContactDetails());
						contact3 = (UsersAdditionalContactDetails) (contact3EmailTbId.getAttribute("contDbObj") != null
								? contact3EmailTbId.getAttribute("contDbObj")
								: new UsersAdditionalContactDetails());

						products = (ProductsUsed) (posSystemVersionLbId.getAttribute("prodDbObj") != null
								? posSystemVersionLbId.getAttribute("prodDbObj")
								: new ProductsUsed());

					}

					int priorityInt = 0;

					List<UsersAdditionalContactDetails> addtnlContactsListToUpdate = new ArrayList<UsersAdditionalContactDetails>();
					if (!contact1NameTbId.getValue().isEmpty() || !contact1NumberTbId.getValue().isEmpty()
							|| !contact1EmailTbId.getValue().isEmpty() || !contact1PositionTbId.getValue().isEmpty()) {
						if (contact1 == null)
							contact1 = new UsersAdditionalContactDetails();
						contact1.setName(contact1NameTbId.getValue().trim().length() == 0 ? null
								: contact1NameTbId.getValue().trim());
						contact1.setEmail(contact1EmailTbId.getValue().trim().length() == 0 ? null
								: contact1EmailTbId.getValue().trim());
						contact1.setPosition(contact1PositionTbId.getValue().trim().length() == 0 ? null
								: contact1PositionTbId.getValue().trim());
						contact1.setPriorityLevel(++priorityInt);
						contact1.setUserId(user.getUserId());

						if (contact1NumberTbId.getValue().trim().length() == 0) {
							contact1.setNumber(null);
						} else {
							String valueCont1 = contact1NumberTbId.getValue().trim();

							if (valueCont1 != null && valueCont1.length() > 0) {
								String userPhoneRegex = "\\d+";
								Pattern phonePattern = Pattern.compile(userPhoneRegex);
								Matcher m = phonePattern.matcher(valueCont1);
								String poneMatch = Constants.STRING_NILL;
								while (m.find()) {
									poneMatch += m.group();
								}
								try {
									valueCont1 = "" + Long.parseLong(poneMatch);
								} catch (NumberFormatException e) {
									valueCont1 = null;
								}
								if (valueCont1 == null || valueCont1.trim().length() == 0) {
									MessageUtil.setMessage(
											"Please provide valid Phone Number for the additional contact1",
											"color:red", "TOP");
									return;
								} else {
									valueCont1 = Utility.phoneParseForCreateUser(valueCont1, minTxtId.getValue(),
											maxTxtId.getValue(), mobilePatternChkBxId.isChecked());
									if (valueCont1 == null || valueCont1.trim().length() == 0) {
										MessageUtil.setMessage(
												"Please provide valid Phone Number for the additional contact1",
												"color:red", "TOP");
										return;
									}
								}
								contact1.setNumber(valueCont1);
							}
						}

						addtnlContactsListToUpdate.add(contact1);
					} else if (contact1EmailTbId.getAttribute("contDbObj") != null) {
						usersAdditionalContactDetailsDaoForDML
								.delete((UsersAdditionalContactDetails) contact1EmailTbId.getAttribute("contDbObj"));
					}

					if (!contact2NameTbId.getValue().isEmpty() || !contact2NumberTbId.getValue().isEmpty()
							|| !contact2EmailTbId.getValue().isEmpty() || !contact2PositionTbId.getValue().isEmpty()) {
						if (contact2 == null)
							contact2 = new UsersAdditionalContactDetails();
						contact2.setName(contact2NameTbId.getValue().trim().length() == 0 ? null
								: contact2NameTbId.getValue().trim());
						contact2.setEmail(contact2EmailTbId.getValue().trim().length() == 0 ? null
								: contact2EmailTbId.getValue().trim());
						contact2.setPosition(contact2PositionTbId.getValue().trim().length() == 0 ? null
								: contact2PositionTbId.getValue().trim());
						contact2.setPriorityLevel(++priorityInt);
						contact2.setUserId(user.getUserId());

						if (contact2NumberTbId.getValue().trim().length() == 0) {
							contact1.setNumber(null);
						} else {
							String valueCont2 = contact2NumberTbId.getValue().trim();

							if (valueCont2 != null && valueCont2.length() > 0) {
								String userPhoneRegex = "\\d+";
								Pattern phonePattern = Pattern.compile(userPhoneRegex);
								Matcher m = phonePattern.matcher(valueCont2);
								String poneMatch = Constants.STRING_NILL;
								while (m.find()) {
									poneMatch += m.group();
								}
								try {
									valueCont2 = "" + Long.parseLong(poneMatch);
								} catch (NumberFormatException e) {
									valueCont2 = null;
								}
								if (valueCont2 == null || valueCont2.trim().length() == 0) {
									MessageUtil.setMessage(
											"Please provide valid Phone Number for the additional contact2.",
											"color:red", "TOP");
									return;
								} else {
									valueCont2 = Utility.phoneParseForCreateUser(valueCont2, minTxtId.getValue(),
											maxTxtId.getValue(), mobilePatternChkBxId.isChecked());
									if (valueCont2 == null || valueCont2.trim().length() == 0) {
										MessageUtil.setMessage(
												"Please provide valid Phone Number for the additional contact2.",
												"color:red", "TOP");
										return;
									}
								}
								contact2.setNumber(valueCont2);
							}
						}

						addtnlContactsListToUpdate.add(contact2);
					} else if (contact2EmailTbId.getAttribute("contDbObj") != null) {
						usersAdditionalContactDetailsDaoForDML
								.delete((UsersAdditionalContactDetails) contact2EmailTbId.getAttribute("contDbObj"));
					}

					if (!contact3NameTbId.getValue().isEmpty() || !contact3NumberTbId.getValue().isEmpty()
							|| !contact3EmailTbId.getValue().isEmpty() || !contact3PositionTbId.getValue().isEmpty()) {
						if (contact3 == null)
							contact3 = new UsersAdditionalContactDetails();
						contact3.setName(contact3NameTbId.getValue().trim().length() == 0 ? null
								: contact3NameTbId.getValue().trim());
						contact3.setNumber(contact3NumberTbId.getValue().trim().length() == 0 ? null
								: contact3NumberTbId.getValue().trim());
						contact3.setEmail(contact3EmailTbId.getValue().trim().length() == 0 ? null
								: contact3EmailTbId.getValue().trim());
						contact3.setPosition(contact3PositionTbId.getValue().trim().length() == 0 ? null
								: contact3PositionTbId.getValue().trim());
						contact3.setPriorityLevel(++priorityInt);
						contact3.setUserId(user.getUserId());

						if (contact3NumberTbId.getValue().trim().length() == 0) {
							contact1.setNumber(null);
						} else {
							String valueCont3 = contact3NumberTbId.getValue().trim();

							if (valueCont3 != null && valueCont3.length() > 0) {
								String userPhoneRegex = "\\d+";
								Pattern phonePattern = Pattern.compile(userPhoneRegex);
								Matcher m = phonePattern.matcher(valueCont3);
								String poneMatch = Constants.STRING_NILL;
								while (m.find()) {
									poneMatch += m.group();
								}
								try {
									valueCont3 = "" + Long.parseLong(poneMatch);
								} catch (NumberFormatException e) {
									valueCont3 = null;
								}
								if (valueCont3 == null || valueCont3.trim().length() == 0) {
									MessageUtil.setMessage(
											"Please provide valid Phone Number for the additional contact3.",
											"color:red", "TOP");
									return;
								} else {
									valueCont3 = Utility.phoneParseForCreateUser(valueCont3, minTxtId.getValue(),
											maxTxtId.getValue(), mobilePatternChkBxId.isChecked());
									if (valueCont3 == null || valueCont3.trim().length() == 0) {
										MessageUtil.setMessage(
												"Please provide valid Phone Number for the additional contact3.",
												"color:red", "TOP");
										return;
									}
								}
								contact3.setNumber(valueCont3);
							}
						}

						addtnlContactsListToUpdate.add(contact3);
					} else if (contact3EmailTbId.getAttribute("contDbObj") != null) {
						usersAdditionalContactDetailsDaoForDML
								.delete((UsersAdditionalContactDetails) contact3EmailTbId.getAttribute("contDbObj"));
					}

					usersAdditionalContactDetailsDaoForDML.saveByCollection(addtnlContactsListToUpdate);

					if (!numberOfContractedEmailsTbId.getValue().isEmpty()
							|| !numberOfContractedSmsTbId.getValue().isEmpty()
							|| !posSystemVersionLbId.getSelectedItem().getLabel().isEmpty()
							|| !optDRVersionTbId.getValue().isEmpty() || !optLoyaltyVersionTbId.getValue().isEmpty()
							|| !optPromoVersionTbId.getValue().isEmpty() || !optIntelVersionTbId.getValue().isEmpty()
							|| !optSyncVersionTbId.getValue().isEmpty()) {
						if (products == null)
							products = new ProductsUsed();
						products.setNumberOfContractedEmails(
								numberOfContractedEmailsTbId.getValue().trim().length() == 0 ? null
										: Long.parseLong(numberOfContractedEmailsTbId.getValue().trim()));
						products.setNumberOfContractedSMS(
								numberOfContractedSmsTbId.getValue().trim().length() == 0 ? null
										: Long.parseLong(numberOfContractedSmsTbId.getValue().trim()));
						products.setPOSSystemVersion(
								posSystemVersionLbId.getSelectedItem().getLabel().length() == 0 ? null
										: posSystemVersionLbId.getSelectedItem().getLabel().trim());
						products.setOptDigitalRecieptVersion(optDRVersionTbId.getValue().trim().length() == 0 ? null
								: optDRVersionTbId.getValue().trim());
						products.setOptLoyaltyVersion(optLoyaltyVersionTbId.getValue().trim().length() == 0 ? null
								: optLoyaltyVersionTbId.getValue().trim());
						products.setOptPromoVersion(optPromoVersionTbId.getValue().trim().length() == 0 ? null
								: optPromoVersionTbId.getValue().trim());
						products.setOptIntelVersion(optIntelVersionTbId.getValue().trim().length() == 0 ? null
								: optIntelVersionTbId.getValue().trim());
						products.setOptSyncVersion(optSyncVersionTbId.getValue().trim().length() == 0 ? null
								: optSyncVersionTbId.getValue().trim());

						products.setUserId(user.getUserId());

						productsUsedDaoForDML.saveOrUpdate(products);

					} else if (posSystemVersionLbId.getAttribute("prodDbObj") != null) {
						productsUsedDaoForDML.delete((ProductsUsed) posSystemVersionLbId.getAttribute("prodDbObj"));
					}

					if (tempIdRow.isVisible() && templateId.getText().isEmpty()) {
						MessageUtil.setMessage("Please provide Template Registration ID.", "color:red;");
						return;
					}

					String msg = Constants.STRING_NILL;
					String confirmStr = Constants.STRING_NILL;

					if (type == null) {
						msg = "User Created Successfully";
						confirmStr = "Created";
						// Clients.evalJavaScript("reset()");
						// user = null;
					} else {
						/*
						 * if(!startDbId.getConstraint().equals("no past")) {
						 * MessageUtil.setMessage("Past dates are not allowed for start date.",
						 * "color:red", "TOP"); startDbId.setConstraint("no past"); }
						 */
						msg = "User updated successfully.";
						confirmStr = "Upted";
					}

					/*
					 * if(isNewUser) {
					 * 
					 * //check And create UserDirecories... GetUser.checkUserFolders(userName);
					 * 
					 * //Set defaultContMapSetting for This User if(orgTypeLbId.getSelectedIndex()
					 * == 0) { Utility.setUserDefaultMapping(user.getUserId()); }
					 * 
					 * String messageToUser =
					 * PropertyUtil.getPropertyValueFromDB("newUserDetailsTemplate"); messageToUser
					 * = messageToUser.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_FNAME,user.
					 * getFirstName()) .replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_USERNAME,
					 * Utility.getOnlyUserName(user.getUserName()))
					 * .replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_PASSWORD, tempPwd)
					 * .replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_ORGID,
					 * Utility.getOnlyOrgId(user.getUserName()));
					 * 
					 * EmailQueue emailQueue = new
					 * EmailQueue("Welcome to OptCulture!",messageToUser,Constants.
					 * EQ_TYPE_NEW_USER_DETAILS,"Active",user.getEmailId(),MyCalendar.getNewCalendar
					 * (),user); //emailQueueDao.saveOrUpdate(emailQueue);
					 * emailQueueDaoForDML.saveOrUpdate(emailQueue);
					 * 
					 * }
					 */
					if (isNewUser) {

						// check And create UserDirecories...
						GetUser.checkUserFolders(userName);

						// Set defaultContMapSetting for This User
						/*
						 * if(orgTypeLbId.getSelectedIndex() == 0) {
						 * Utility.setUserDefaultMapping(user.getUserId()); }
						 */
						List<POSMapping> recordList = null;
						recordList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
								user.getUserId());
						if (recordList == null || recordList.size() == 0) {

							if (orgTypeLbId.getSelectedIndex() == 0) {
								Utility.setUserDefaultMapping(user.getUserId());
							}
						}
						logger.info("tempPwd before replacing:" + tempPwd);
						String messageToUser = PropertyUtil.getPropertyValueFromDB("newUserDetailsTemplate");
						messageToUser = messageToUser
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_FNAME, user.getFirstName())
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_USERNAME,
										Utility.getOnlyUserName(user.getUserName()))
								// .replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_PASSWORD, tempPwd)
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_PASSWORD, user.getTempPwd())
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_ORGID,
										Utility.getOnlyOrgId(user.getUserName()));

						EmailQueue emailQueue = new EmailQueue("Welcome to OptCulture!", messageToUser,
								Constants.EQ_TYPE_NEW_USER_DETAILS, "Active", user.getEmailId(),
								MyCalendar.getNewCalendar(), user);
						// emailQueueDao.saveOrUpdate(emailQueue);
						emailQueueDaoForDML.saveOrUpdate(emailQueue);

					}

					if ((user.getPOSVersion() != null && !user.getPOSVersion()
							.equalsIgnoreCase(posSystemVersionLbId.getSelectedItem().getLabel()))
							|| user.getPOSVersion() == null) {

						user.setPOSVersion(posSystemVersionLbId.getSelectedItem().getLabel());
						PropertyUtil.loadPOSVersionsCache(true, user.getUserName(), user.getPOSVersion());
					}

					// SET SMS SETTINGS
					// session.setAttribute("USEROBJ", user);

					/*
					 * UserSMSGateway userPromotionalSmsGateway = type!= null ?
					 * (UserSMSGateway)promotionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj") :
					 * null; UserSMSGateway userTransactionalSmsGateway = type!= null ?
					 * (UserSMSGateway)transactionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
					 * : null; UserSMSSenderId userSMSSenderIdObj = type!= null ?
					 * (UserSMSSenderId)transSenderIdTxtBxId.getAttribute("SMSSenderID") : null;
					 * UserSMSGateway userOptinSmsGateway = type!= null ?
					 * (UserSMSGateway)optinSMSGateWayLbId.getAttribute("UserSMSGatewayObj") : null;
					 * 
					 * UserSMSSenderId userOptinSMSSenderIdObj = type!= null ?
					 * (UserSMSSenderId)optinSenderIdTxtBxId.getAttribute("SMSSenderID"): null;
					 * 
					 * SMSSettings optinSmsSettings = type!= null ?
					 * (SMSSettings)optinKeywordTxtBxId.getAttribute("SMSSettings") : null;
					 * SMSSettings optoutSmsSettings = type!= null ?
					 * (SMSSettings)optoutKeywordTxtBxId.getAttribute("SMSSettings") :null;
					 */

					String countryType = user.getCountryType();
					if (countryType != null && !countryType.isEmpty())
						countryLbId.setDisabled(true);

					if (enableSmsChkId.isChecked()) {

						enableSmsDivId.setVisible(true);
						user.setEnableSMS(true);

						if (!validateSMSSettings()) {

							return;
						}

						user.setSmsCount(userSmsLimitIntBoxId.getValue());

						// user.setConsiderSMSSettings(smsSettingsChkId.isChecked());

						/*
						 * String countryType = countryLbId.getSelectedItem().getLabel().trim();
						 * user.setCountryType(countryType);
						 * 
						 * if(countryCodeTxtId.getValue().trim().length() > 0){
						 * 
						 * user.setCountryCarrier(Byte.parseByte(countryCodeTxtId.getValue().trim())); }
						 * else { user.setCountryCarrier(null); }
						 */

						List<UserSMSGateway> listTobeSaved = new ArrayList<UserSMSGateway>();
						List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
								Constants.SMS_TYPE_PROMOTIONAL);

						if (countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

							// check for promotional type

							// if(promotionalSMSGateWayChkId.isChecked()){

							OCSMSGateway promoOcsmsGateway = promotionalSMSUserNameGateWayLbId.getSelectedItem()
									.getValue();

							UserSMSGateway userPromotionalSmsGateway = type != null
									? (UserSMSGateway) promotionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) promotionalSMSGateWayLbId.getAttribute("SMSSenderID")
									: null;
							//app-3689
//							if (userPRSMSSenderIdObj == null) {
//								userPRSMSSenderIdObj = new UserSMSSenderId();
//								userPRSMSSenderIdObj.setUserName(user.getUserName());
//								userPRSMSSenderIdObj.setUserId(user.getUserId());
//								userPRSMSSenderIdObj.setSmsType(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
//							}
//							// userPRSMSSenderIdObj.setSenderId(SMSStatusCodes.defaultAccountMap.get(Constants.SMS_COUNTRY_INDIA).split(Constants.ADDR_COL_DELIMETER)[3]);
//							// setting sender Id from user sms gateway instead of default map
//							userPRSMSSenderIdObj.setSenderId(promoOcsmsGateway.getSenderId());
//							// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
//							userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
									
//App-3689									
							List<UserSMSSenderId> retPRList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_PROMOTIONAL);
							String[] senderIdArray = promoOcsmsGateway.getSenderId().split(",");
							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retPRList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

										userPRSMSSenderIdObj = null;

									}
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}

							if (userPromotionalSmsGateway == null) {

								userPromotionalSmsGateway = new UserSMSGateway();
								userPromotionalSmsGateway.setUserId(user.getUserId());
								userPromotionalSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userPromotionalSmsGateway.setAccountType(promoOcsmsGateway.getAccountType());

								userPromotionalSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userPromotionalSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userPromotionalSmsGateway.setGatewayId(promoOcsmsGateway.getId());
							userPromotionalSmsGateway.setModifiedDate(Calendar.getInstance());
							userPromotionalSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userPromotionalSmsGateway);

							// }

							UserSMSGateway userTransactionalSmsGateway = type != null
									? (UserSMSGateway) transactionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userTRSMSSenderIdObj = type != null
									? (UserSMSSenderId) transactionalSMSGateWayLbId.getAttribute("SMSSenderID") //app-3701
									: null;

							if (transactionalSMSGateWayChkId.isChecked()) {

								OCSMSGateway transocsmsGateway = transactionalSMSUserNameGateWayLbId.getSelectedItem()
										.getValue();
								//App-3701									
								List<UserSMSSenderId> retTRList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_TRANSACTIONAL);
								String[] trsenderIdArray = transocsmsGateway.getSenderId().split(",");
								if (userTRSMSSenderIdObj == null) {

									for (String aSenderId : trsenderIdArray) {

										if (userTRSMSSenderIdObj == null) {
											userTRSMSSenderIdObj = new UserSMSSenderId();
											userTRSMSSenderIdObj.setUserName(user.getUserName());
											userTRSMSSenderIdObj.setUserId(user.getUserId());
											userTRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_TRANSACTIONAL);
										}

										userTRSMSSenderIdObj.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userTRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userTRSMSSenderIdObj);

										userTRSMSSenderIdObj = null;
									}
								} else {
									// delete the existing senderId records and only add those selected in the list
									// box.
									for (UserSMSSenderId retListItem : retTRList) {

										userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
									}

									Set<Listitem> selItems = trsenderIdLBId.getSelectedItems();
									if (selItems.size() != 0) {
										for (Listitem listitem : selItems) {

											// if (userTRSMSSenderIdObj == null) {
											userTRSMSSenderIdObj = new UserSMSSenderId();
											userTRSMSSenderIdObj.setUserName(user.getUserName());
											userTRSMSSenderIdObj.setUserId(user.getUserId());
											userTRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_TRANSACTIONAL);
											// }

											userTRSMSSenderIdObj.setSenderId(listitem.getValue());
											// userSMSSenderIdDao.saveOrUpdate(userTRSMSSenderIdObj);
											userSMSSenderIdDaoForDML.saveOrUpdate(userTRSMSSenderIdObj);

											userTRSMSSenderIdObj = null;

										}
									} else if (!selectTRSenderIdDiv.isVisible()) {
										for (String aSenderId : trsenderIdArray) {

											UserSMSSenderId userTRSMSSenderIdObj1 = new UserSMSSenderId();
											userTRSMSSenderIdObj1.setUserName(user.getUserName());
											userTRSMSSenderIdObj1.setUserId(user.getUserId());
											userTRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_TRANSACTIONAL);

											userTRSMSSenderIdObj1.setSenderId(aSenderId.trim());
											// userSMSSenderIdDao.saveOrUpdate(userTRSMSSenderIdObj);
											userSMSSenderIdDaoForDML.saveOrUpdate(userTRSMSSenderIdObj1);

											userTRSMSSenderIdObj = null;
										}
									}
								}
								if (userTransactionalSmsGateway == null) {

									userTransactionalSmsGateway = new UserSMSGateway();
									userTransactionalSmsGateway.setUserId(user.getUserId());
									userTransactionalSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
									userTransactionalSmsGateway.setAccountType(transocsmsGateway.getAccountType());
									userTransactionalSmsGateway.setCreatedDate(Calendar.getInstance());
									userTransactionalSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());

								}

								userTransactionalSmsGateway.setGatewayId(transocsmsGateway.getId());
								userTransactionalSmsGateway.setModifiedDate(Calendar.getInstance());
								userTransactionalSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

								listTobeSaved.add(userTransactionalSmsGateway);

								// added for sender id

								// List<UserSMSSenderId> senderIdList =
								// userSMSSenderIdDao.findByUserId(user.getUserId());
								// UserSMSSenderId userSMSSenderIdObj =
								// (UserSMSSenderId)transSenderIdTxtBxId.getAttribute("SMSSenderID");

								/*
								 * if(senderIdList != null && senderIdList.size() > 0){ for (UserSMSSenderId
								 * userSMSSenderId : senderIdList) {
								 * 
								 * if(transSenderIdTxtBxId.getValue().trim().equals(userSMSSenderId.getSenderId(
								 * ))) { userSMSSenderIdObj = userSMSSenderId; break; }else
								 * if(userSMSSenderId.getSmsType() != null &&
								 * userSMSSenderId.getSmsType().equals(Constants.SMS_TYPE_TRANSACTIONAL)) {
								 * userSMSSenderIdObj = userSMSSenderId; break; } }// for }//if
								 */
								/*if (userSMSSenderIdObj == null) {	app-3701
									userSMSSenderIdObj = new UserSMSSenderId();
									userSMSSenderIdObj.setUserName(user.getUserName());
									userSMSSenderIdObj.setUserId(user.getUserId());
									userSMSSenderIdObj.setSmsType(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
								}
								userSMSSenderIdObj.setSenderId(transSenderIdTxtBxId.getValue().trim());
								// userSMSSenderIdDao.saveOrUpdate(userSMSSenderIdObj);
								userSMSSenderIdDaoForDML.saveOrUpdate(userSMSSenderIdObj);*/

							} else {

								try {
									// TODO if for edit user this is unchecked remove everything from everywhaere

									// delete transactional sender id when it is unchecked(transactional check)

									if (userTRSMSSenderIdObj != null) {

										if (Messagebox.show(
												"Are you sure you want to delete transactional SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userTRSMSSenderIdObj);
											// userSMSGatewayDao.deleteBy(userTRSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userTRSMSSenderIdObj.getId()); //app-3701

										}

									}

									// // delete transactional SMS gateway when it is unchecked(transactional check)
									if (userTransactionalSmsGateway != null) {

										if (Messagebox.show(
												"Are you sure you want to delete transactional SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userTransactionalSmsGateway);
											// userSMSGatewayDao.deleteBy(userTransactionalSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userTransactionalSmsGateway.getId());

										}

									}
								} catch (Exception e) {

									logger.error("Exception", e);

								}

							}

							user.setOptinRoute(
									optinSMSRouteChkId.isChecked() ? Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL : null);

							UserSMSGateway userOptinSmsGateway = type != null
									? (UserSMSGateway) optinSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;

							UserSMSSenderId userOptinSMSSenderIdObj = type != null
									? (UserSMSSenderId) optinSenderIdTxtBxId.getAttribute("SMSSenderID")
									: null;

							SMSSettings optinSmsSettings = type != null
									? (SMSSettings) optinKeywordTxtBxId.getAttribute("SMSSettings")
									: null;
							SMSSettings optoutSmsSettings = type != null
									? (SMSSettings) optoutKeywordTxtBxId.getAttribute("SMSSettings")
									: null;

							if (optinSMSGateWayChkId.isChecked()) {
								OCSMSGateway optinSMSGateway = optinSMSUserNameGateWayLbId.getSelectedItem().getValue();
								if (userOptinSmsGateway == null) {
									userOptinSmsGateway = new UserSMSGateway();

									userOptinSmsGateway.setUserId(user.getUserId());
									userOptinSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
									userOptinSmsGateway.setAccountType(optinSMSGateway.getAccountType());
									userOptinSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
									userOptinSmsGateway.setCreatedDate(Calendar.getInstance());

								}

								userOptinSmsGateway.setGatewayId(optinSMSGateway.getId());
								userOptinSmsGateway.setModifiedDate(Calendar.getInstance());
								userOptinSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

								listTobeSaved.add(userOptinSmsGateway);

								// added for sender id

								if (optinSenderIdTxtBxId.getValue().trim().length() == 0) {
									MessageUtil.setMessage("Please provide a valid sender ID.", "red", "top");
									return;
								}

								// List<UserSMSSenderId> senderIdList =
								// userSMSSenderIdDao.findByUserId(user.getUserId());
								// UserSMSSenderId userSMSSenderIdObj =
								// (UserSMSSenderId)optinSenderIdTxtBxId.getAttribute("SMSSenderID");

								/*
								 * if(senderIdList != null && senderIdList.size() > 0){ for (UserSMSSenderId
								 * userSMSSenderId : senderIdList) {
								 * 
								 * if(optinKeywordTxtBxId.getValue().trim().equals(userSMSSenderId.getSenderId()
								 * )) { userSMSSenderIdObj = userSMSSenderId; break; }else
								 * if(userSMSSenderId.getSmsType() != null &&
								 * userSMSSenderId.getSmsType().equals(Constants.SMS_SENDING_TYPE_OPTIN)) {
								 * userSMSSenderIdObj = userSMSSenderId; break; } }// for }//if
								 */
								if (userOptinSMSSenderIdObj == null) {
									userOptinSMSSenderIdObj = new UserSMSSenderId();
									userOptinSMSSenderIdObj.setUserName(user.getUserName());
									userOptinSMSSenderIdObj.setUserId(user.getUserId());
									userOptinSMSSenderIdObj.setSmsType(Constants.SMS_SENDING_TYPE_OPTIN);
								}
								userOptinSMSSenderIdObj.setSenderId(optinSenderIdTxtBxId.getValue().trim());
								// userSMSSenderIdDao.saveOrUpdate(userOptinSMSSenderIdObj);
								userSMSSenderIdDaoForDML.saveOrUpdate(userOptinSMSSenderIdObj);

								List<SMSSettings> listTosaved = new ArrayList<SMSSettings>();

								// SMSSettings optinSmsSettings =
								// (SMSSettings)optinKeywordTxtBxId.getAttribute("SMSSettings");

								/*
								 * if(user.isConsiderSMSSettings() ){
								 * 
								 * if(optInMsgTbId.getText().isEmpty()){
								 * 
								 * MessageUtil.
								 * setMessage("Opt-in message cant be left empty. Default opt-in message will be reloaded into the message box"
								 * , "color:red;"); //
								 * MessageUtil.setMessage("Please provide message invite opt-ins",
								 * "color:red;");
								 * 
								 * String autoOptinMsg =
								 * PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_SMS_OPTIN_MESSAGE);
								 * optInMsgTbId.setText(autoOptinMsg);
								 * 
								 * }
								 * 
								 * }
								 */

								if (optinSmsSettings == null) {
									optinSmsSettings = new SMSSettings();
									optinSmsSettings.setUserId(user);
									optinSmsSettings.setOrgId(user.getUserOrganization().getUserOrgId());
									optinSmsSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
									optinSmsSettings.setCreatedDate(Calendar.getInstance());
									// getCharCount(autoResContent);

								}

								// optin list

								optinSmsSettings.setModifiedBy(user.getUserId());
								optinSmsSettings.setCreatedBy(user.getUserId());
								optinSmsSettings.setModifiedDate(Calendar.getInstance());

								// optin list

								String mlName = optinListLBId.getSelectedItem().getLabel();
								MailingList mlist = mailingListDao.findByListName(mlName, user.getUserId());
								if (mlist != null) {
									optinSmsSettings.setListId(mlist != null ? mlist.getListId() : null);
								} else {
									MailingList newOptMlist = createOptinMailinList(mlName, user);
									// mailingListDao.saveOrUpdate(newOptMlist);
									optinSmsSettings.setListId(newOptMlist != null ? newOptMlist.getListId() : null);

								}

								// optinSmsSettings.setSenderId(optinSenderIdTxtBxId.getText().trim());

								// added after sender id changed to tarnascation sender id

								if (transactionalSMSGateWayChkId.isChecked()) {

									//optinSmsSettings.setSenderId(transSenderIdTxtBxId.getText().trim());	app-3701
									List<UserSMSSenderId> retTRList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_TRANSACTIONAL);
									if(retTRList.size()!=0)
										optinSmsSettings.setSenderId(retTRList.get(0).getSenderId().trim());

								} else {

									optinSmsSettings.setSenderId(null);

								}

								// add enable msd call
								optinSmsSettings
										.setOptinMissedCalNumber(
												enableMsdCalChkId.isChecked()
														? (optinMsdCalNumLBId.getAttribute("SHORTCODE") == null ? null
																: optinMsdCalNumLBId.getAttribute("SHORTCODE")
																		.toString())
														: null);

								optinSmsSettings
										.setShortCode(enableKeywordChkId.isChecked()
												? (optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE") == null ? null
														: optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE")
																.toString())
												: null);
								optinSmsSettings
										.setKeyword(enableKeywordChkId.isChecked()
												? (optinKeywordTxtBxId.getValue().trim().isEmpty() ? null
														: optinKeywordTxtBxId.getValue().trim())
												: null);

								String autoResContent = optInMsgTbId.getText().trim();

								autoResContent = autoResContent
										.replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD,
												optinSmsSettings.getKeyword() == null ? Constants.STRING_NILL
														: optinSmsSettings.getKeyword())
										.replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER,
												optinSmsSettings.getShortCode() == null ? Constants.STRING_NILL
														: optinSmsSettings.getShortCode())
										.replace(OCConstants.SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER,
												optinSmsSettings.getOptinMissedCalNumber() == null
														? Constants.STRING_NILL
														: optinSmsSettings.getOptinMissedCalNumber());

								/*
								 * if(enableKeywordChkId.isChecked() && enableMsdCalChkId.isChecked() ){
								 * 
								 * autoResContent =
								 * autoResContent.replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD,
								 * optinKeywordTxtBxId.getText().trim()).
								 * replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER,
								 * optinKeywordMsdCalNumTxtBxId.getText().trim()).
								 * replace(OCConstants.SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER,
								 * optinMsdCalNumTxtBxId.getText().trim());
								 * 
								 * }else if(enableKeywordChkId.isChecked()){
								 * 
								 * autoResContent =
								 * autoResContent.replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD,
								 * optinKeywordTxtBxId.getText().trim()).
								 * replace(OCConstants.SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER,
								 * optinKeywordMsdCalNumTxtBxId.getText().trim());
								 * 
								 * }else if(enableMsdCalChkId.isChecked()){
								 * 
								 * autoResContent
								 * =autoResContent.replace(OCConstants.SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER,
								 * optinMsdCalNumTxtBxId.getText().trim());
								 * 
								 * }
								 */

								optinSmsSettings.setAutoResponse(autoResContent);

								/*
								 * if(type!= null){
								 * optinSmsSettings.setAutoResponse(optInMsgTbId.getText().trim() );
								 * //getCharCount(optInMsgTbId.getText().trim()); }
								 */
								// getCharCount(optInMsgTbId.getText().trim());

								listTosaved.add(optinSmsSettings);

								// SMSSettings optoutSmsSettings =
								// (SMSSettings)optoutKeywordTxtBxId.getAttribute("SMSSettings");

								if (optoutSmsSettings == null) {

									optoutSmsSettings = new SMSSettings();
									optoutSmsSettings.setUserId(user);
									optoutSmsSettings.setOrgId(user.getUserOrganization().getUserOrgId());
									optoutSmsSettings.setType(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
									optoutSmsSettings.setCreatedDate(Calendar.getInstance());
									// optoutSmsSettings.setSenderId(optinSenderIdTxtBxId.getText().trim());

								}

								optoutSmsSettings.setSenderId(optinSenderIdTxtBxId.getText().trim());

								optoutSmsSettings.setModifiedBy(user.getUserId());
								optoutSmsSettings.setCreatedBy(user.getUserId());
								optoutSmsSettings.setModifiedDate(Calendar.getInstance());

								optoutSmsSettings
										.setKeyword(enableKeywordChkId.isChecked()
												? (optoutKeywordTxtBxId.getValue().trim().isEmpty() ? null
														: optoutKeywordTxtBxId.getValue().trim())
												: null);
								optoutSmsSettings
										.setShortCode(enableKeywordChkId.isChecked()
												? (optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE") == null ? null
														: optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE")
																.toString())
												: null);
								listTosaved.add(optoutSmsSettings);

								if (listTosaved.size() > 0)
									smsSettingsDaoForDML.saveByCollection(listTosaved);

							} else {
								try {
									// if uncheck delete all entries

									if (userOptinSMSSenderIdObj != null) {
										if (Messagebox.show("Are you sure you want to delete opt-in SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userOptinSMSSenderIdObj);

											// userSMSSenderIdDao.deleteBy(userOptinSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOptinSMSSenderIdObj.getId());

										}
									}

									if (userOptinSmsGateway != null) {
										if (Messagebox.show("Are you sure you want to delete opt-in SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userOptinSmsGateway);

											// userSMSGatewayDao.deleteBy(userOptinSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userOptinSmsGateway.getId());
										}
									}

									if (optinSmsSettings.getUserId().getUserId().longValue() == user.getUserId()
											.longValue()) {
										if (optinSmsSettings != null) {

											if (Messagebox.show("Are you sure you want to delete SMS opt-in settings? ",
													"Confirm", Messagebox.OK | Messagebox.CANCEL,
													Messagebox.QUESTION) == Messagebox.OK) {

												// smsSettingsDao.delete(optinSmsSettings);

												// smsSettingsDao.deleteBy(optinSmsSettings.getSetupId());
												smsSettingsDaoForDML.deleteBy(optinSmsSettings.getSetupId());

											}
										}
									}

									if (optoutSmsSettings.getUserId().getUserId().longValue() == user.getUserId()
											.longValue()) {

										if (optoutSmsSettings != null) {

											if (Messagebox.show(
													"Are you sure you want to delete SMS opt-out settings? ", "Confirm",
													Messagebox.OK | Messagebox.CANCEL,
													Messagebox.QUESTION) == Messagebox.OK) {

												// smsSettingsDao.delete(optoutSmsSettings);

												// smsSettingsDao.deleteBy(optoutSmsSettings.getSetupId());
												smsSettingsDaoForDML.deleteBy(optoutSmsSettings.getSetupId());

											}
										}
									}
								} catch (Exception e) {

									logger.error("Exception", e);

								}

							}

						} else if (countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {

							OCSMSGateway usOutboundOcsmsGateway = USSMSUserNameGateWayLbId.getSelectedItem().getValue();

							UserSMSGateway userUSOutboundSmsGateway = type != null
									? (UserSMSGateway) USSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) USSMSGateWayLbId.getAttribute("SMSSenderID")
									: null;
							/*
							 * if(userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND); }
							 */
							// harshi US
							List<UserSMSSenderId> retOutboundList = userSMSSenderIdDao
									.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_OUTBOUND);
							String[] senderIdArray = usOutboundOcsmsGateway.getSenderId().split(",");
							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retOutboundList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

										userPRSMSSenderIdObj = null;

									}
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_OUTBOUND);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}

							// userPRSMSSenderIdObj.setSenderId(SMSStatusCodes.defaultAccountMap.get(Constants.SMS_COUNTRY_US).split(Constants.ADDR_COL_DELIMETER)[3]);
							/*
							 * if(senderIdArray.length == 1 && userPRSMSSenderIdObj != null){
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * //userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 * userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj); }
							 */

							if (userUSOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userUSOutboundSmsGateway = new UserSMSGateway();
								userUSOutboundSmsGateway.setUserId(user.getUserId());
								userUSOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userUSOutboundSmsGateway.setAccountType(usOutboundOcsmsGateway.getAccountType());

								userUSOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userUSOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userUSOutboundSmsGateway.setGatewayId(usOutboundOcsmsGateway.getId());
							userUSOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userUSOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userUSOutboundSmsGateway);

						}else if(countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {

							OCSMSGateway canadaOutboundOcsmsGateway = CANADASMSUserNameGateWayLbId.getSelectedItem().getValue();

							UserSMSGateway userCANADAOutboundSmsGateway = type != null
									? (UserSMSGateway) CANADASMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) CANADASMSGateWayLbId.getAttribute("SMSSenderID")
									: null;
							
							List<UserSMSSenderId> retOutboundList = userSMSSenderIdDao
									.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_OUTBOUND);
							String[] senderIdArray = canadaOutboundOcsmsGateway.getSenderId().split(",");
							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retOutboundList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

										userPRSMSSenderIdObj = null;

									}
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_OUTBOUND);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}


							if (userCANADAOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userCANADAOutboundSmsGateway = new UserSMSGateway();
								userCANADAOutboundSmsGateway.setUserId(user.getUserId());
								userCANADAOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userCANADAOutboundSmsGateway.setAccountType(canadaOutboundOcsmsGateway.getAccountType());

								userCANADAOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userCANADAOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userCANADAOutboundSmsGateway.setGatewayId(canadaOutboundOcsmsGateway.getId());
							userCANADAOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userCANADAOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userCANADAOutboundSmsGateway);

						} else if(countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {

							OCSMSGateway saOutboundOcsmsGateway = SASMSUserNameGateWayLbId.getSelectedItem().getValue();

							UserSMSGateway userSAOutboundSmsGateway = type != null
									? (UserSMSGateway) SASMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) SASMSGateWayLbId.getAttribute("SMSSenderID")
									: null;
							
							List<UserSMSSenderId> retOutboundList = userSMSSenderIdDao
									.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_OUTBOUND);
							String[] senderIdArray = saOutboundOcsmsGateway.getSenderId().split(",");
							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retOutboundList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_OUTBOUND);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

										userPRSMSSenderIdObj = null;

									}
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_OUTBOUND);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}


							if (userSAOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userSAOutboundSmsGateway = new UserSMSGateway();
								userSAOutboundSmsGateway.setUserId(user.getUserId());
								userSAOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userSAOutboundSmsGateway.setAccountType(saOutboundOcsmsGateway.getAccountType());

								userSAOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userSAOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userSAOutboundSmsGateway.setGatewayId(saOutboundOcsmsGateway.getId());
							userSAOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userSAOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userSAOutboundSmsGateway);

						} else if (countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {

							OCSMSGateway usOutboundOcsmsGateway = pakistanSMSUserNameGateWayLbId.getSelectedItem()
									.getValue();

							UserSMSGateway userUSOutboundSmsGateway = type != null
									? (UserSMSGateway) pakistanSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) pakistanSMSGateWayLbId.getAttribute("SMSSenderID")
									: null;

							String[] senderIdArray = usOutboundOcsmsGateway.getSenderId().split(",");

							// following code(i.e. if block) sets new sender id(single or multiple) in
							// user_sms_sender_id table for the first time, i.e. when user is being created.
							/*
							 * if (userPRSMSSenderIdObj == null) {
							 * 
							 * for (String aSenderId : senderIdArray) {
							 * 
							 * if (userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL); }
							 * 
							 * userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
							 * //userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 * userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
							 * 
							 * userPRSMSSenderIdObj = null;
							 * 
							 * }
							 * 
							 * 
							 * }
							 */

							// harshi pak

							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);

										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
									}

									userPRSMSSenderIdObj = null;
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}

							/*
							 * if(userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL); }
							 */
							// userPRSMSSenderIdObj.setSenderId(SMSStatusCodes.defaultAccountMap.get(Constants.SMS_COUNTRY_US).split(Constants.ADDR_COL_DELIMETER)[3]);

							/*
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 */

							// important note: date july 11th 2016
							// following code(i.e. if block) retains existing thing(modifying existing
							// setting of single sender id with another single sender id)
							// currently we don' have the facility with multiple sender id being switched to
							// another set of multiple sender ids or one single id, if that comes, we have
							// to
							// do from back end.
							/*
							 * if(senderIdArray.length == 1 && userPRSMSSenderIdObj != null){
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * //userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 * userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj); }
							 */

							if (userUSOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userUSOutboundSmsGateway = new UserSMSGateway();
								userUSOutboundSmsGateway.setUserId(user.getUserId());
								userUSOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userUSOutboundSmsGateway.setAccountType(usOutboundOcsmsGateway.getAccountType());

								userUSOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userUSOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userUSOutboundSmsGateway.setGatewayId(usOutboundOcsmsGateway.getId());
							userUSOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userUSOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userUSOutboundSmsGateway);

						} else if (countryType.equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {

							OCSMSGateway usOutboundOcsmsGateway = uaeSMSUserNameGateWayLbId.getSelectedItem()
									.getValue();

							UserSMSGateway userUSOutboundSmsGateway = type != null
									? (UserSMSGateway) uaeSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) uaeSMSGateWayLbId.getAttribute("SMSSenderID")
									: null;

							String[] senderIdArray = usOutboundOcsmsGateway.getSenderId().split(",");
							// List<UserSMSSenderId> retList =
							// userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
							// Constants.SMS_TYPE_PROMOTIONAL);
							// following code(i.e. if block) sets new sender id(single or multiple) in
							// user_sms_sender_id table for the first time, i.e. when user is being created.
							// harshi uae

							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;
								}
							} else {
								// delete the existing senderId records and only add those selected in the list
								// box.
								for (UserSMSSenderId retListItem : retList) {

									userSMSSenderIdDaoForDML.deleteBy(retListItem.getId());
								}

								Set<Listitem> selItems = senderIdLBId.getSelectedItems();
								if (selItems.size() != 0) {
									for (Listitem listitem : selItems) {

										// if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
										// }

										userPRSMSSenderIdObj.setSenderId(listitem.getValue());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

										userPRSMSSenderIdObj = null;

									}
								} else if (!selectSenderIdDiv.isVisible()) {
									for (String aSenderId : senderIdArray) {

										UserSMSSenderId userPRSMSSenderIdObj1 = new UserSMSSenderId();
										userPRSMSSenderIdObj1.setUserName(user.getUserName());
										userPRSMSSenderIdObj1.setUserId(user.getUserId());
										userPRSMSSenderIdObj1.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);

										userPRSMSSenderIdObj1.setSenderId(aSenderId.trim());
										// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
										userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj1);

										userPRSMSSenderIdObj = null;
									}
								}
							}

							/*
							 * if (userPRSMSSenderIdObj == null) {
							 * 
							 * for (String aSenderId : senderIdArray) {
							 * 
							 * if (userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL); }
							 * 
							 * userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
							 * //userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 * userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
							 * 
							 * userPRSMSSenderIdObj = null;
							 * 
							 * }
							 * 
							 * 
							 * }
							 */

							/*
							 * if(userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL); }
							 */
							// userPRSMSSenderIdObj.setSenderId(SMSStatusCodes.defaultAccountMap.get(Constants.SMS_COUNTRY_US).split(Constants.ADDR_COL_DELIMETER)[3]);

							/*
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 */

							// important note: date july 11th 2016
							// following code(i.e. if block) retains existing thing(modifying existing
							// setting of single sender id with another single sender id)
							// currently we don' have the facility with multiple sender id being switched to
							// another set of multiple sender ids or one single id, if that comes, we have
							// to
							// do from back end.
							/*
							 * if(senderIdArray.length == 1 && userPRSMSSenderIdObj != null){
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * //userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 * userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj); }
							 */

							if (userUSOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userUSOutboundSmsGateway = new UserSMSGateway();
								userUSOutboundSmsGateway.setUserId(user.getUserId());
								userUSOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userUSOutboundSmsGateway.setAccountType(usOutboundOcsmsGateway.getAccountType());

								userUSOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userUSOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userUSOutboundSmsGateway.setGatewayId(usOutboundOcsmsGateway.getId());
							userUSOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userUSOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userUSOutboundSmsGateway);

							// check for promotional type

							// if(promotionalSMSGateWayChkId.isChecked()){

							// }

							UserSMSGateway userTransactionalSmsGateway = type != null
									? (UserSMSGateway) uaeTransactionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userSMSSenderIdObj = type != null
									? (UserSMSSenderId) uaeTransSenderIdTxtBxId.getAttribute("SMSSenderID")
									: null;

							if (uaeTransactionalSMSGateWayChkId.isChecked()) {

								OCSMSGateway transocsmsGateway = uaeTransactionalSMSUserNameGateWayLbId
										.getSelectedItem().getValue();

								if (userTransactionalSmsGateway == null) {

									userTransactionalSmsGateway = new UserSMSGateway();
									userTransactionalSmsGateway.setUserId(user.getUserId());
									userTransactionalSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
									userTransactionalSmsGateway.setAccountType(transocsmsGateway.getAccountType());
									userTransactionalSmsGateway.setCreatedDate(Calendar.getInstance());
									userTransactionalSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());

								}

								userTransactionalSmsGateway.setGatewayId(transocsmsGateway.getId());
								userTransactionalSmsGateway.setModifiedDate(Calendar.getInstance());
								userTransactionalSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

								listTobeSaved.add(userTransactionalSmsGateway);

								if (userSMSSenderIdObj == null) {
									userSMSSenderIdObj = new UserSMSSenderId();
									userSMSSenderIdObj.setUserName(user.getUserName());
									userSMSSenderIdObj.setUserId(user.getUserId());
									userSMSSenderIdObj.setSmsType(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
								}
								userSMSSenderIdObj.setSenderId(uaeTransSenderIdTxtBxId.getValue().trim());
								// userSMSSenderIdDao.saveOrUpdate(userSMSSenderIdObj);
								userSMSSenderIdDaoForDML.saveOrUpdate(userSMSSenderIdObj);

							} else {

								try {
									// TODO if for edit user this is unchecked remove everything from everywhaere

									// delete transactional sender id when it is unchecked(transactional check)

									if (userSMSSenderIdObj != null) {

										if (Messagebox.show(
												"Are you sure you want to delete transactional SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userSMSSenderIdObj);
											// userSMSGatewayDao.deleteBy(userSMSSenderIdObj.getId());
											userSMSGatewayDaoForDML.deleteBy(userSMSSenderIdObj.getId());

										}

									}

									// // delete transactional SMS gateway when it is unchecked(transactional check)
									if (userTransactionalSmsGateway != null) {

										if (Messagebox.show(
												"Are you sure you want to delete transactional SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userTransactionalSmsGateway);
											// userSMSGatewayDao.deleteBy(userTransactionalSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userTransactionalSmsGateway.getId());

										}

									}
								} catch (Exception e) {

									logger.error("Exception", e);

								}

							}

							// if(optinSMSGateWayChkId.isChecked()){}else{}

						} else {

							OCSMSGateway usOutboundOcsmsGateway = genericSMSUserNameGateWayLbId.getSelectedItem()
									.getValue();

							UserSMSGateway userUSOutboundSmsGateway = type != null
									? (UserSMSGateway) genericSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
									: null;
							UserSMSSenderId userPRSMSSenderIdObj = type != null
									? (UserSMSSenderId) genericSMSGateWayLbId.getAttribute("SMSSenderID")
									: null;

							String[] senderIdArray = usOutboundOcsmsGateway.getSenderId().split(",");
							// following code(i.e. if block) sets new sender id(single or multiple) in
							// user_sms_sender_id table for the first time, i.e. when user is being created.
							if (userPRSMSSenderIdObj == null) {

								for (String aSenderId : senderIdArray) {

									if (userPRSMSSenderIdObj == null) {
										userPRSMSSenderIdObj = new UserSMSSenderId();
										userPRSMSSenderIdObj.setUserName(user.getUserName());
										userPRSMSSenderIdObj.setUserId(user.getUserId());
										userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL);
									}

									userPRSMSSenderIdObj.setSenderId(aSenderId.trim());
									// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
									userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);

									userPRSMSSenderIdObj = null;

								}

							}

							/*
							 * if(userPRSMSSenderIdObj == null) { userPRSMSSenderIdObj = new
							 * UserSMSSenderId(); userPRSMSSenderIdObj.setUserName(user.getUserName());
							 * userPRSMSSenderIdObj.setUserId(user.getUserId());
							 * userPRSMSSenderIdObj.setSmsType(Constants.SMS_TYPE_PROMOTIONAL); }
							 */
							// userPRSMSSenderIdObj.setSenderId(SMSStatusCodes.defaultAccountMap.get(Constants.SMS_COUNTRY_US).split(Constants.ADDR_COL_DELIMETER)[3]);

							/*
							 * userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
							 * ? usOutboundOcsmsGateway.getSenderId() : null );
							 * 
							 * userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
							 */

							// important note: date july 11th 2016
							// following code(i.e. if block) retains existing thing(modifying existing
							// setting of single sender id with another single sender id)
							// currently we don' have the facility with multiple sender id being switched to
							// another set of multiple sender ids or one single id, if that comes, we have
							// to
							// do from back end.
							if (senderIdArray.length == 1 && userPRSMSSenderIdObj != null) {
								userPRSMSSenderIdObj.setSenderId(usOutboundOcsmsGateway.getSenderId() != null
										? usOutboundOcsmsGateway.getSenderId()
										: null);

								// userSMSSenderIdDao.saveOrUpdate(userPRSMSSenderIdObj);
								userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
							}

							if (userUSOutboundSmsGateway == null) {

								logger.debug("===========2=============");
								userUSOutboundSmsGateway = new UserSMSGateway();
								userUSOutboundSmsGateway.setUserId(user.getUserId());
								userUSOutboundSmsGateway.setOrgId(user.getUserOrganization().getUserOrgId());
								userUSOutboundSmsGateway.setAccountType(usOutboundOcsmsGateway.getAccountType());

								userUSOutboundSmsGateway.setCreatedBy(GetUser.getUserObj().getUserId());
								userUSOutboundSmsGateway.setCreatedDate(Calendar.getInstance());
							}

							userUSOutboundSmsGateway.setGatewayId(usOutboundOcsmsGateway.getId());
							userUSOutboundSmsGateway.setModifiedDate(Calendar.getInstance());
							userUSOutboundSmsGateway.setModifiedBy(GetUser.getUserObj().getUserId());

							listTobeSaved.add(userUSOutboundSmsGateway);

						}
						UserOrganization userOrganization = user.getUserOrganization();
						user.getUserOrganization().setRequireMobileValidation(mobileValidationsChkId.isChecked());
						// userOrganization.setMaxKeywords(0);
						String maxKeywords = keywordsTxtboxId.getValue().trim();
						if (!maxKeywords.isEmpty() || maxKeywords.length() > 0) {
							/*
							 * MessageUtil.setMessage("Please provide keyword limit", "color:red;"); return;
							 * }else{
							 */
							/*
							 * try { Long.parseLong(maxKeywords); } catch (NumberFormatException e) {
							 * 
							 * MessageUtil.setMessage("provide valid keyord limit.", "color:red;"); }
							 */
							userOrganization.setMaxKeywords(Integer.parseInt(maxKeywords));
						}

						/*
						 * String msgRecvNumber = Constants.STRING_NILL;
						 * 
						 * 
						 * if(msgRecvNumCmbId.getItemCount() < 0){
						 * 
						 * MessageUtil.setMessage(" Please add at least one message receiving number.",
						 * "color:red;"); return ;
						 * 
						 * }
						 * 
						 * 
						 * if(msgRecvNumCmbId.getItemCount() > 0){
						 * 
						 * for (Comboitem cItem : msgRecvNumCmbId.getItems()) {
						 * 
						 * if(!msgRecvNumber.isEmpty()) msgRecvNumber += Constants.ADDR_COL_DELIMETER;
						 * 
						 * msgRecvNumber += cItem.getValue(); }
						 * 
						 * }else{
						 * 
						 * msgRecvNumber = msgRecvNumCmbId.getSelectedItem() != null ?
						 * (String)msgRecvNumCmbId.getSelectedItem().getValue() :null; }
						 * userOrganization.setMsgReceivingNumbers(msgRecvNumber);
						 */

						/*
						 * String msgRecvNumber = msgRecvNumCmbId.getSelectedItem().getValue();
						 * 
						 * if(msgRecvNumber.isEmpty() || msgRecvNumber.length() == 0){
						 * MessageUtil.setMessage("Please provide message receiving number",
						 * "color:red;"); return; }else{
						 * 
						 * userOrganization.setMsgReceivingNumbers(msgRecvNumber); }
						 */

						// boolean enableAlerts =enableAlertsChkId.isChecked();

						if (userOrganization.getToEmailId() == null || userOrganization.getToEmailId().isEmpty()) {

							if (user.getEmailId() != null)
								toEmailTxtboxId.setValue(user.getEmailId());

						}
						String toEmailId = toEmailTxtboxId.getValue().trim();

						// userOrganization.setEnableAlerts(enableAlerts);
						// if(enableAlerts)

						userOrganization.setToEmailId(toEmailId);

						logger.debug("===========6=============" + user.getSmsCount());
						// usersDao.saveOrUpdate(userOrganization);
						usersDaoForDML.saveOrUpdate(userOrganization);
						// usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);
						if (listTobeSaved.size() > 0) {

							logger.debug("===========7=============");
							userSMSGatewayDaoForDML.saveByCollection(listTobeSaved);
						}

					} else {
						enableSmsDivId.setVisible(false);

						user.setEnableSMS(enableSmsChkId.isChecked());
						// usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);
						if (type != null) {

							String countryStr = user.getCountryType();

							if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {

								try {
									UserSMSGateway userUSOutboundSmsGateway = type != null
											? (UserSMSGateway) USSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) USSMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userUSOutboundSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete outbound SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSGatewayDao.deleteBy(userUSOutboundSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userUSOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {
										if (Messagebox.show("Are you sure you want to delete outbound SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSSenderIdDao.deleteBy(userOBSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							}else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {

								try {
									UserSMSGateway userCANADAOutboundSmsGateway = type != null
											? (UserSMSGateway) CANADASMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) CANADASMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userCANADAOutboundSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete outbound SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											userSMSGatewayDaoForDML.deleteBy(userCANADAOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {
										if (Messagebox.show("Are you sure you want to delete outbound SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							} else if(countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {

								try {
									UserSMSGateway userSAOutboundSmsGateway = type != null
											? (UserSMSGateway) SASMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) SASMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userSAOutboundSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete outbound SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											userSMSGatewayDaoForDML.deleteBy(userSAOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {
										if (Messagebox.show("Are you sure you want to delete outbound SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {

								try {
									UserSMSGateway userUSOutboundSmsGateway = type != null
											? (UserSMSGateway) pakistanSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) pakistanSMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userUSOutboundSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete BroadCast SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {
											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSGatewayDao.deleteBy(userUSOutboundSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userUSOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {
										if (Messagebox.show("Are you sure you want to delete BroadCast SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSSenderIdDao.deleteBy(userOBSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

								UserSMSGateway userPromotionalSmsGateway = type != null
										? (UserSMSGateway) promotionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
										: null;

								UserSMSSenderId userPRSMSSenderIdObj = type != null
										? (UserSMSSenderId) promotionalSMSGateWayLbId.getAttribute("SMSSenderID")
										: null;

								UserSMSGateway userTransactionalSmsGateway = type != null
										? (UserSMSGateway) transactionalSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
										: null;
								/*UserSMSSenderId userSMSSenderIdObj = type != null
										? (UserSMSSenderId) transSenderIdTxtBxId.getAttribute("SMSSenderID")
										: null;*/ //app-3701
								UserSMSSenderId userSMSSenderIdObj = type != null
										? (UserSMSSenderId) transactionalSMSGateWayLbId.getAttribute("SMSSenderID")
										: null;
								UserSMSGateway userOptinSmsGateway = type != null
										? (UserSMSGateway) optinSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
										: null;

								UserSMSSenderId userOptinSMSSenderIdObj = type != null
										? (UserSMSSenderId) optinSenderIdTxtBxId.getAttribute("SMSSenderID")
										: null;

								SMSSettings optinSmsSettings = type != null
										? (SMSSettings) optinKeywordTxtBxId.getAttribute("SMSSettings")
										: null;
								SMSSettings optoutSmsSettings = type != null
										? (SMSSettings) optoutKeywordTxtBxId.getAttribute("SMSSettings")
										: null;

								try {
									// if uncheck delete all entries
									if (userPromotionalSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete promotional SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userPromotionalSmsGateway);

											// userSMSGatewayDao.deleteBy(userPromotionalSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userPromotionalSmsGateway.getId());

										}

									}

									if (userPRSMSSenderIdObj != null) {

										if (Messagebox.show("Are you sure you want to delete promotional sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userSMSSenderIdObj);

											// userSMSSenderIdDao.deleteBy(userPRSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userPRSMSSenderIdObj.getId());
										}

									}

									if (userSMSSenderIdObj != null) {

										if (Messagebox.show("Are you sure you want to delete transactional sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userSMSSenderIdObj);

											// userSMSSenderIdDao.deleteBy(userSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userSMSSenderIdObj.getId());
										}

									}

									// // delete transactional SMS gateway when it is unchecked(transactional check)

									if (userTransactionalSmsGateway != null) {
										if (Messagebox.show(
												"Are you sure you want to delete transactional SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userTransactionalSmsGateway);
											// userSMSGatewayDao.deleteBy(userTransactionalSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userTransactionalSmsGateway.getId());

										}

									}

									if (userOptinSmsGateway != null) {

										if (Messagebox.show("Are you sure you want to delete opt-in SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userOptinSmsGateway);

											// userSMSGatewayDao.deleteBy(userOptinSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userOptinSmsGateway.getId());

										}
									}

									if (userOptinSMSSenderIdObj != null) {

										if (Messagebox.show("Are you sure you want to delete opt-in sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSSenderIdDao.delete(userOptinSMSSenderIdObj);

											// userSMSSenderIdDao.deleteBy(userOptinSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOptinSMSSenderIdObj.getId());
										}
									}

									if (optinSmsSettings != null) {

										if (optinSmsSettings.getUserId().getUserId().longValue() == user.getUserId()
												.longValue()) {

											if (optinSmsSettings != null) {

												if (Messagebox.show(
														"Are you sure you want to delete SMS opt-in settings? ",
														"Confirm", Messagebox.OK | Messagebox.CANCEL,
														Messagebox.QUESTION) == Messagebox.OK) {

													// smsSettingsDao.delete(optinSmsSettings);

													// smsSettingsDao.deleteBy(optinSmsSettings.getSetupId());
													smsSettingsDaoForDML.deleteBy(optinSmsSettings.getSetupId());
												}
											}
										}

										if (optoutSmsSettings.getUserId().getUserId().longValue() == user.getUserId()
												.longValue()) {

											if (optoutSmsSettings != null) {

												if (Messagebox.show(
														"Are you sure you want to delete SMS opt-out settings? ",
														"Confirm", Messagebox.OK | Messagebox.CANCEL,
														Messagebox.QUESTION) == Messagebox.OK) {

													// smsSettingsDao.delete(optoutSmsSettings);

													// smsSettingsDao.deleteBy(optoutSmsSettings.getSetupId());
													smsSettingsDaoForDML.deleteBy(optoutSmsSettings.getSetupId());
												}
											}

										}
									}

								} catch (Exception e) {

									logger.error("Exception", e);
								}

							} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {

								try {
									UserSMSGateway userUSOutboundSmsGateway = type != null
											? (UserSMSGateway) uaeSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) uaeSMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userUSOutboundSmsGateway != null) {
										if (Messagebox.show("Are you sure you want to delete BroadCast SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSGatewayDao.deleteBy(userUSOutboundSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userUSOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {

										if (Messagebox.show("Are you sure you want to delete BroadCast SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSSenderIdDao.deleteBy(userOBSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							} else {

								try {
									UserSMSGateway userUSOutboundSmsGateway = type != null
											? (UserSMSGateway) genericSMSGateWayLbId.getAttribute("UserSMSGatewayObj")
											: null;

									UserSMSSenderId userOBSMSSenderIdObj = type != null
											? (UserSMSSenderId) genericSMSGateWayLbId.getAttribute("SMSSenderID")
											: null;

									if (userUSOutboundSmsGateway != null) {
										if (Messagebox.show("Are you sure you want to delete BroadCast SMS gateway? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {

											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSGatewayDao.deleteBy(userUSOutboundSmsGateway.getId());
											userSMSGatewayDaoForDML.deleteBy(userUSOutboundSmsGateway.getId());

										}

									}

									if (userOBSMSSenderIdObj != null) {

										if (Messagebox.show("Are you sure you want to delete BroadCast SMS sender ID? ",
												"Confirm", Messagebox.OK | Messagebox.CANCEL,
												Messagebox.QUESTION) == Messagebox.OK) {
											// userSMSGatewayDao.delete(userUSOutboundSmsGateway);

											// userSMSSenderIdDao.deleteBy(userOBSMSSenderIdObj.getId());
											userSMSSenderIdDaoForDML.deleteBy(userOBSMSSenderIdObj.getId());
										}

									}
								} catch (Exception e) {
									logger.error("Exception", e);
								}

							}

						} // if

					} // else

					/*
					 * if(isNewUser) {// need to enable for SMS further
					 * 
					 * 
					 * smsSenderIdGbId.setVisible(true); submitBtnId.setDisabled(true);
					 * resetBtnid.setDisabled(true); }else { smsSenderIdGbId.setVisible(true);
					 * submitBtnId.setDisabled(true);
					 * 
					 * 
					 * }
					 */
					Messagebox.show(msg, "User Details", Messagebox.OK, Messagebox.INFORMATION);
				} catch (Exception e) {
					// MessageUtil.setMessage("Problem while creating user.", "color:red", "TOP");

					logger.error("Exception", e);
					// logger.error("Exception :::",e);

					// logger.error("**Exception : Problem while storing the user" ,e );
				}

				logger.debug("-- Exit --");
			} catch (Exception e) {

				logger.error("Exception  ::", e);
			}

		} catch (Exception e) {

			logger.error("Exception  ::", e);
		}
		session.removeAttribute("Type");
		session.removeAttribute("user");
		Redirect.goTo(PageListEnum.EMPTY);
		// Redirect.goTo(PageListEnum.ADMIN_CREATE_USER);
		Redirect.goTo(PageListEnum.ADMIN_LIST_USERS);

	}

	/*
	 * public void clearSMSSettings(){
	 * 
	 * enableSmsChkId.setChecked(false); enableSmsDivId.setVisible(false);
	 * userSmsLimitIntBoxId.setValue(0); smsSettingsChkId.setChecked(false);
	 * countryLbId.setSelectedIndex(0);
	 * countryCodeTxtId.setText(Constants.STRING_NILL);
	 * if(promotionalSMSGateWayLbId.getItemCount() >
	 * 0)promotionalSMSGateWayLbId.setSelectedIndex(0);
	 * if(promotionalSMSUserNameGateWayLbId.getItemCount() >
	 * 0)promotionalSMSUserNameGateWayLbId.setSelectedIndex(0);
	 * transactionalSMSGateWayChkId.setChecked(false);
	 * transactionalDivId.setVisible(false);
	 * if(transactionalSMSGateWayLbId.getItemCount() >
	 * 0)transactionalSMSGateWayLbId.setSelectedIndex(0);
	 * if(transactionalSMSUserNameGateWayLbId.getItemCount() >
	 * 0)transactionalSMSUserNameGateWayLbId.setSelectedIndex(0);
	 * transSenderIdTxtBxId.setText(Constants.STRING_NILL);
	 * approveTempCmbBoxId.setSelectedIndex(0);
	 * optinSMSGateWayChkId.setChecked(false); optinSmsDivId.setVisible(false);
	 * if(optinSMSGateWayLbId.getItemCount() >
	 * 0)optinSMSGateWayLbId.setSelectedIndex(0);
	 * if(optinSMSUserNameGateWayLbId.getItemCount() >
	 * 0)optinSMSUserNameGateWayLbId.setSelectedIndex(0);
	 * optinSenderIdTxtBxId.setText(Constants.STRING_NILL);
	 * enableMsdCalChkId.setChecked(false); optinMsdCalDivId.setVisible(false);
	 * optinMsdCalNumTxtBxId.setText(Constants.STRING_NILL);
	 * enableKeywordChkId.setChecked(false);
	 * enableMsdCallsKeywordsDivId.setVisible(false);
	 * optinKeywordMsdCalNumTxtBxId.setText(Constants.STRING_NILL);
	 * optinKeywordTxtBxId.setText(Constants.STRING_NILL);
	 * optoutKeywordTxtBxId.setText(Constants.STRING_NILL);
	 * optinListDivId.setVisible(false); if(optinListLBId.getItemCount() >
	 * 0)optinListLBId.setSelectedIndex(0); usSMSDivId.setVisible(false);
	 * if(USSMSGateWayLbId.getItemCount() > 0)USSMSGateWayLbId.setSelectedIndex(0);
	 * if(USSMSUserNameGateWayLbId.getItemCount() >
	 * 0)USSMSUserNameGateWayLbId.setSelectedIndex(0);
	 * keywordsTxtboxId.setText(Constants.STRING_NILL);
	 * if(msgRecvNumCmbId.getItemCount() > 0 )msgRecvNumCmbId.setSelectedIndex(0);
	 * toEmailTxtboxId.setText(Constants.STRING_NILL);
	 * 
	 * }
	 */

	public void onChange$vmtaCbId() throws Exception {
		/*
		 * String status = (String)vmtaCbId.getSelectedItem().getValue();
		 * vmtaStatusLblId.setValue(status);
		 */

		/*
		 * if(((String)vmtaCbId.getSelectedItem().getLabel()).equalsIgnoreCase(
		 * "sendGridAPI")) { UserExternalSMTPSettings userExternalSMTPSetting =
		 * externalSMTPSettingsDao.findByUserId(user.getUserId());
		 * if(userExternalSMTPSetting == null) {
		 * MessageUtil.setMessage("Send Grid account is not configured to this user.",
		 * "red"); return; } }
		 */
		if (vmtaCbId.getSelectedIndex() > 0) {
			vmtaAccountNameCbId.setDisabled(false);
		} else {
			vmtaAccountNameCbId.getChildren().clear();
			Comboitem ci = null;
			ci = new Comboitem("Select Account");
			ci.setParent(vmtaAccountNameCbId);
			vmtaAccountNameCbId.setSelectedItem(ci);
			vmtaAccountNameCbId.setDisabled(true);
			return;
		}

		populateVmtaAccountNameCbId();

	}

	public void onClick$senderIdCloseBtnId() {
		smsSenderIdGbId.setVisible(false);
		/*
		 * Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
		 * xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
		 * xcontents.setSrc("/zul/" + PageListEnum.ADMIN_LIST_USERS + ".zul");
		 */
	}

	private Combobox userSenderIdsCbId;
	private Button addNewSenderIdBtnId, deleteSenderIdBtnId, submitBtnId, resetBtnid, cancelBtnId, senderIdCloseBtnId;
	private Textbox newSenderIdTxtId;
	private Groupbox smsSenderIdGbId;

	public void onClick$addNewSenderIdBtnId() {
		/*
		 * try { String newSenderId = newSenderIdTxtId.getValue(); if(newSenderId ==
		 * null || newSenderId.trim().length() == 0 ) {
		 * 
		 * MessageUtil.setMessage("Sender ID cannot be left empty.",
		 * "color:red;","top"); return; } if(newSenderId.trim().length() > 0) {
		 * 
		 * Comboitem ci; ci = new Comboitem(newSenderId); //ci.setDescription(" ");
		 * if(user==null) {
		 * 
		 * MessageUtil.
		 * setMessage("Please confirm the user creation before adding Sender IDs.",
		 * "color:red;","top"); return; } UserSMSSenderId userSMSSenderId = new
		 * UserSMSSenderId(user.getUserId(), newSenderId, user.getUserName());
		 * usersDao.saveOrUpdate(userSMSSenderId); ci.setValue(userSMSSenderId);
		 * ci.setParent(userSenderIdsCbId); userSenderIdsCbId.setSelectedIndex(0);
		 * 
		 * }//if
		 * 
		 * 
		 * newSenderIdTxtId.setValue("");
		 * MessageUtil.setMessage("Sender ID added succesfully.","color:green;");
		 * 
		 * Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
		 * xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
		 * xcontents.setSrc("/zul/" + PageListEnum.ADMIN_LIST_USERS + ".zul");
		 * 
		 * } catch (Exception e) {
		 * 
		 * logger.error("Exception  ::", e); }
		 */}// onClick$addNewSenderIdBtnId()

	public void onClick$deleteSenderIdBtnId() {
		try {
			logger.info("----just entered to delete the sender id---");
			if (userSenderIdsCbId.getSelectedIndex() != 0) {
				try {
					if (Messagebox.show(
							"Are you sure you want to delete the sender ID - '"
									+ userSenderIdsCbId.getSelectedItem().getLabel() + "'?",
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {

						UserSMSSenderId tempUserSMSSenderId = (UserSMSSenderId) userSenderIdsCbId.getSelectedItem()
								.getValue();

						Long id = tempUserSMSSenderId.getId();
						logger.info("id====>" + id);
						if (id != null) {
							// usersDao.delete(id);
							usersDaoForDML.delete(id);

						} // if

						userSenderIdsCbId.removeItemAt(userSenderIdsCbId.getSelectedIndex());
						userSenderIdsCbId.setSelectedIndex(0);

					} // if
				} catch (Exception e) {

					logger.error("Exception  ::", e);
				}
			} // if
		} catch (Exception e) {

			logger.error("Exception  ::", e);
		}
	}//onClick$deleteSenderIdBtnId
	
	
	
	public void onBlur$userNameTbId() {
		String userName = userNameTbId.getValue().trim();
		if (userName.length() == 0) {
			nameStatusLblId.setStyle("color:red;font-weight:bold;");
			nameStatusLblId.setValue("Please provide User Name");
			isValidUserName = false;
			return;
		}
		userName = Utility.condense(userName + "__org__" + compShrtNameTbId.getValue().trim());

		if (!Utility.validateUserName(userName)) {
			nameStatusLblId.setStyle("color:red;font-weight:bold;");
			nameStatusLblId.setValue(
					"Please provide valid User Name, Special characters and space will not be allowed ( _ is allowed)");
			isValidUserName = false;
			return;
		}

		try {
			UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
			Users user = usersDao.findByUsername(userName);
			if (user == null) {
				nameStatusLblId.setStyle("color:#08658F;font-weight:bold;");
				nameStatusLblId.setValue("Available");
				isValidUserName = true;
			} else {
				nameStatusLblId.setStyle("color:red;font-weight:bold;");
				nameStatusLblId.setValue("Not Available");
				// userNameTbId.setValue(userName);
				isValidUserName = false;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	}

	private boolean isValidOrgName = true;
	private boolean isValidCompName = true;
	private Textbox compShrtNameTbId;
	private Label orgNameStatusLblId, domainNameStatusLblId, orgStatusLblId, emailLblId, firstNameLblId, lastNameLblId,
			CityLblId, stateLblId, cLbId, addressOneLblId, selectedFieldsLblId, selectSenderIdLblId,selectTRSenderIdLblId;

	public void onBlur$compShrtNameTbId() {
		String orgShortName = compShrtNameTbId.getValue().trim();

		if (orgShortName.length() == 0) {
			orgNameStatusLblId.setStyle("color:red;font-weight:bold;");
			orgNameStatusLblId.setValue("Please provide short name for the Organization");
			isValidOrgName = false;

			return;
		}
		orgShortName = Utility.condense(orgShortName);

		if (!Utility.validateUserName(orgShortName)) {
			orgNameStatusLblId.setStyle("color:red;font-weight:bold;");
			orgNameStatusLblId.setValue(
					"Please provide valid Organization Id, Special characters and spaces will not be allowed ( _ is allowed)");
			isValidOrgName = false;
			return;
		}

		try {
			UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
			UserOrganization userorg = usersDao.findByOrgName(orgShortName);
			if (userorg == null) {
				orgNameStatusLblId.setStyle("color:#08658F;font-weight:bold;");
				orgNameStatusLblId.setValue("Available");
				isValidOrgName = true;
			} else {
				orgNameStatusLblId.setStyle("color:red;font-weight:bold;");
				orgNameStatusLblId.setValue("Not Available");
				// compShrtNameTbId.setValue(orgShortName);
				isValidOrgName = false;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	}

	public void onBlur$compNameTbId() {

		String orgName = compNameTbId.getValue().trim();

		if (orgName.length() == 0) {
			orgStatusLblId.setStyle("color:red;font-weight:bold;");
			orgStatusLblId.setValue("Please provide Organization name");
			isValidCompName = false;
			return;
		} else {
			orgStatusLblId.setValue(" ");
			isValidCompName = true;
		}

	}

	public void onBlur$emailIdTbId() {

		String email = emailIdTbId.getValue();
		if (email.trim().length() == 0) {
			emailLblId.setStyle("color:red;font-weight:bold;");
			emailLblId.setValue("Please provide Email id");
			isValidEmailID = false;
		} // if
		else if (!Utility.validateEmail(email)) {
			emailLblId.setStyle("color:red;font-weight:bold;");
			emailLblId.setValue("Please provide valid Email id");
			isValidEmailID = false;

		} else {
			emailLblId.setValue(" ");
			isValidEmailID = true;
		}

	}

	public void onBlur$firstNameTbId() {

		String fname = firstNameTbId.getValue();
		if (fname.trim().length() == 0) {
			firstNameLblId.setStyle("color:red;font-weight:bold;");
			firstNameLblId.setValue("Please provide First Name");
			isValidFirstName = false;
		} else {
			firstNameLblId.setValue(" ");
			isValidFirstName = true;
		}
	}

	public void onBlur$lastNameTbId() {

		String lname = lastNameTbId.getValue();
		if (lname.trim().length() == 0) {
			lastNameLblId.setStyle("color:red;font-weight:bold;");
			lastNameLblId.setValue("Please provide Last Name");
			isValidlastName = false;
		} else {
			lastNameLblId.setValue(" ");
			isValidlastName = true;
		}
	}

	public void onBlur$cityTbId() {

		String city = cityTbId.getValue();
		if (city.trim().length() == 0) {
			CityLblId.setStyle("color:red;font-weight:bold;");
			CityLblId.setValue("Please provide City");
			CityLblId.setVisible(true);
			isValidCity = false;
		} else {
			CityLblId.setVisible(false);
			isValidCity = true;
		}
	}

	public void onBlur$stateTbId() {

		String state = stateTbId.getValue();
		if (state.trim().length() == 0) {
			stateLblId.setStyle("color:red;font-weight:bold;");
			stateLblId.setValue("Please provide State");
			stateLblId.setVisible(true);
			isValidState = false;
		} else {
			stateLblId.setVisible(false);
			isValidState = true;
		}
	}

	public void onBlur$countryTbId() {

		String country = countryTbId.getValue();
		if (country.trim().length() == 0) {
			cLbId.setStyle("color:red;font-weight:bold;");
			cLbId.setValue("Please provide Country");
			cLbId.setVisible(true);
			isValidCountry = false;
		} else {
			cLbId.setVisible(false);
			isValidCountry = true;
		}
	}

	public void onBlur$addressOneTbId() {

		String addOne = addressOneTbId.getValue();
		if (addOne.trim().length() == 0) {
			addressOneLblId.setStyle("color:red;font-weight:bold;");
			addressOneLblId.setValue("Please provide Address");
			addressOneLblId.setVisible(true);
			isValidAddress = false;
		} else {
			addressOneLblId.setVisible(false);
			isValidAddress = true;
		}
	}

	public void onBlur$domainNameTbId() {

		String domainName = domainNameTbId.getValue().trim();
		// String userName =
		// userNameTbId.getValue().trim()+"__org__"+currentUser.getUserOrganization().getOrgExternalId();
		// logger.info("------1-------------");
		domainName = Utility.condense(domainName);

		if (domainName == null || domainName.trim().length() == 0) {
			// logger.info("------2-------------");
			domainNameStatusLblId.setStyle("color:red;font-weight:bold;");
			domainNameStatusLblId.setValue("Please provide Organization Unit Name");
			isValidDomainName = false;
			return;
		}

		if (!Utility.validateNames(domainName)) {
			// logger.info("------3-------------");
			domainNameStatusLblId.setStyle("color:red;font-weight:bold;");
			domainNameStatusLblId.setValue(
					"Please provide Valid Organization Unit Name, Special characters will not be allowed ( _ is allowed)");
			isValidDomainName = false;
			;
			return;
		}

		try {

			UsersDomainsDao usersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
			String examineDomName = domainName;
			if (domainName.contains("'")) {

				examineDomName = domainName.replace("'", "\\'");

			}

			// logger.info("examineDomName====>"+examineDomName);
			int usersDomains = usersDomainsDao.findBydomainName(examineDomName,
					GetUser.getUserObj().getUserOrganization().getUserOrgId());// user name should be unique accross the
																				// organization
			if (usersDomains == 0) {
				// logger.info("------4-------------");
				domainNameStatusLblId.setStyle("color:#08658F;font-weight:bold;");
				domainNameStatusLblId.setValue("Available");
				isValidDomainName = true;

			} else {
				// logger.info("------5-------------");
				domainNameStatusLblId.setStyle("color:red;font-weight:bold;");
				domainNameStatusLblId.setValue("Organization Unit Name is already exist,please provide another name.");
				isValidDomainName = false;

			}

		} catch (Exception e) {
			logger.info("Exception : ");
			logger.error("Exception  ::", e);
		}

	}

	public void onClick$cancelBtnId() {

		Redirect.goTo(PageListEnum.EMPTY);
		// Redirect.goTo(PageListEnum.ADMIN_CREATE_USER);
		Redirect.goTo(PageListEnum.ADMIN_LIST_USERS);

		/*
		 * Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
		 * xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
		 * xcontents.setSrc("/zul/" + PageListEnum.ADMIN_LIST_USERS + ".zul");
		 */

	}

	/*
	 * public void onSelect$smsGateWayCbId(){ if(smsGateWayCbId.getSelectedIndex()
	 * == 0) countryCodeTxtBxId.setValue(""); else
	 * if(smsGateWayCbId.getSelectedIndex() == 1) countryCodeTxtBxId.setValue("1");
	 * else if(smsGateWayCbId.getSelectedIndex() == 2)
	 * countryCodeTxtBxId.setValue("91");
	 * 
	 * }
	 */

	public boolean checkIfNumber(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	// added for sms settings

	public void defaultCreateGatewaySettings() {

		/*
		 * countryMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_US);
		 * countryMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_INDIA);
		 * 
		 * Set<String> countrySet = countryMap.keySet();
		 * Components.removeAllChildren(countryLbId);
		 * 
		 * for (String country : countrySet) {
		 * 
		 * 
		 * Listitem item= new Listitem(country);
		 * 
		 * item.setParent(countryLbId); item.setValue(countryMap.get(country));
		 * 
		 * }//for
		 */
		if (countryLbId.getItemCount() > 0) {

			if (type != null && user.isEnableSMS()) {
				String userCountry = user.getCountryType();
				for (Listitem item : countryLbId.getItems()) {

					if (item.getLabel().equals(userCountry)) {

						item.setSelected(true);
						break;

					} // if

				} // for
			} // if

		} // if

	}

	public void onSelect$countryLbId() {

		try {
			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_INDIA);

				indiaDivId.setVisible(true);
				usSMSDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(false);
				canadaSMSDivId.setVisible(false);
				saSMSDivId.setVisible(false);

				Components.removeAllChildren(promotionalSMSGateWayLbId);

				List<String> promotionalList = ocsmsGatewayDao.findPromotionalGateways(countryStr);
				for (String promotionalStr : promotionalList) {

					Listitem item = new Listitem(promotionalStr);
					item.setParent(promotionalSMSGateWayLbId);

				}

				if (promotionalSMSGateWayLbId.getItemCount() > 0)
					promotionalSMSGateWayLbId.setSelectedIndex(0);
				// *************START*******************
				if (type != null && user.isEnableSMS()) {

					Collection<String> accountTypes = SMSStatusCodes.countryCampValueMap.get(user.getCountryType())
							.values();
					String typeStr = Constants.STRING_NILL;
					for (String type : accountTypes) {

						if (typeStr.contains(type))
							continue;

						if (!typeStr.isEmpty())
							typeStr += Constants.DELIMETER_COMMA;

						typeStr += "'" + type + "'";

					}
					logger.debug("jsdhjsdhsdh" + typeStr);

					List<UserSMSGateway> userSMSGatewayList = userSMSGatewayDao.findAllBy(user.getUserId(), typeStr);

					String ocSMSGatewayIdsStr = Constants.STRING_NILL;
					if (userSMSGatewayList != null) {
						for (UserSMSGateway userSMSGateway : userSMSGatewayList) {

							if (!ocSMSGatewayIdsStr.isEmpty())
								ocSMSGatewayIdsStr += Constants.DELIMETER_COMMA;

							ocSMSGatewayIdsStr += userSMSGateway.getGatewayId().longValue();
						}
					}

					List<OCSMSGateway> ocsmsGatewayList = ocsmsGatewayDao.findByIds(ocSMSGatewayIdsStr);
					if (ocsmsGatewayList != null) {

						for (OCSMSGateway ocsmsGateway : ocsmsGatewayList) {

							if (ocsmsGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
								promotionalSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								if (promotionalSMSGateWayLbId.getItemCount() > 0) {
									for (Listitem item : promotionalSMSGateWayLbId.getItems()) {

										if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

											item.setSelected(true);
											break;
										}

									}
								} // if(promotionalSMSGateWayLbId.getItemCount() > 0 )
							} else if (ocsmsGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {
								transactionalSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);

							} else if (ocsmsGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
								optinSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);

							}
						}

					} // if ocsmsGatewayList != null

					if (userSMSGatewayList != null) {

						// String ocSMSGatewayIdsStr = Constants.STRING_NILL;
						for (UserSMSGateway userSMSGateway : userSMSGatewayList) {

							/*
							 * if(!ocSMSGatewayIdsStr.isEmpty()) ocSMSGatewayIdsStr +=
							 * Constants.DELIMETER_COMMA;
							 * 
							 * ocSMSGatewayIdsStr += userSMSGateway.getId().longValue();
							 */
							if (userSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {

								promotionalSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
								List<UserSMSSenderId> retList = userSMSSenderIdDao
										.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_PROMOTIONAL);
								if (retList != null) {
									UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
									promotionalSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

								}

							} else if (userSMSGateway.getAccountType()
									.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {

								transactionalSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
								transactionalSMSGateWayChkId.setChecked(true);
								onCheck$transactionalSMSGateWayChkId();

							} else if (userSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {

								optinSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
								optinSMSGateWayChkId.setChecked(true);
								onCheck$optinSMSGateWayChkId();

							}
						}

					}

				}

				// *************END******************

				if (promotionalSMSGateWayLbId.getSelectedIndex() != -1)
					onSelect$promotionalSMSGateWayLbId();

				// TODO

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_US);
				usSMSDivId.setVisible(true);
				canadaSMSDivId.setVisible(false);
				indiaDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(false);
				saSMSDivId.setVisible(false);

				Components.removeAllChildren(USSMSGateWayLbId);

				List<String> USoptinList = ocsmsGatewayDao.findGateways(countryStr,
						SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				for (String optinStr : USoptinList) {

					Listitem item = new Listitem(optinStr);
					item.setParent(USSMSGateWayLbId);

				}

				if (USSMSGateWayLbId.getItemCount() > 0) {
					USSMSGateWayLbId.setSelectedIndex(0);

					if (type != null && user.isEnableSMS()) {

						UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(user.getUserId(),
								Constants.SMS_TYPE_OUTBOUND);
						if (userSMSGateway != null) {

							USSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
									Constants.SMS_TYPE_OUTBOUND);
							if (retList != null) {
								UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
								USSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

							}

							OCSMSGateway ocsmsGateway = ocsmsGatewayDao.findById(userSMSGateway.getGatewayId());
							if (ocsmsGateway != null) {
								USSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								for (Listitem item : USSMSGateWayLbId.getItems()) {

									if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

										item.setSelected(true);
										break;
									}

								}

							}

						} // if
					} // if type != null && user.isEnableSMS()

					onSelect$USSMSGateWayLbId();

				}

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_CANADA);
				canadaSMSDivId.setVisible(true);
				usSMSDivId.setVisible(false);
				indiaDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(false);

				Components.removeAllChildren(CANADASMSGateWayLbId);
				logger.info("countryStr :"+countryStr);
				logger.info("defaultSMSTypeMap for Canada :"+SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				List<String> CANADAoptinList = ocsmsGatewayDao.findGateways(countryStr,
						SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				for (String optinStr : CANADAoptinList) {

					Listitem item = new Listitem(optinStr);
					item.setParent(CANADASMSGateWayLbId);

				}

				if (CANADASMSGateWayLbId.getItemCount() > 0) {
					CANADASMSGateWayLbId.setSelectedIndex(0);

					if (type != null && user.isEnableSMS()) {

						UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(user.getUserId(),
								Constants.SMS_TYPE_OUTBOUND);
						if (userSMSGateway != null) {

							CANADASMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
									Constants.SMS_TYPE_OUTBOUND);
							if (retList != null) {
								UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
								CANADASMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

							}

							OCSMSGateway ocsmsGateway = ocsmsGatewayDao.findById(userSMSGateway.getGatewayId());
							if (ocsmsGateway != null) {
								CANADASMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								for (Listitem item : CANADASMSGateWayLbId.getItems()) {

									if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

										item.setSelected(true);
										break;
									}

								}

							}

						} // if
					} // if type != null && user.isEnableSMS()

					onSelect$CANADASMSGateWayLbId();

				}

			} else if(countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_SA);
				saSMSDivId.setVisible(true);
				canadaSMSDivId.setVisible(false);
				usSMSDivId.setVisible(false);
				indiaDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(false);

				Components.removeAllChildren(SASMSGateWayLbId);
				logger.info("countryStr :"+countryStr);
				logger.info("defaultSMSTypeMap for South Africa :"+SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				List<String> SAoptinList = ocsmsGatewayDao.findGateways(countryStr,
						SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				for (String optinStr : SAoptinList) {

					Listitem item = new Listitem(optinStr);
					item.setParent(SASMSGateWayLbId);

				}

				if (SASMSGateWayLbId.getItemCount() > 0) {
					SASMSGateWayLbId.setSelectedIndex(0);

					if (type != null && user.isEnableSMS()) {

						UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(user.getUserId(),
								Constants.SMS_TYPE_OUTBOUND);
						if (userSMSGateway != null) {

							SASMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
									Constants.SMS_TYPE_OUTBOUND);
							if (retList != null) {
								UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
								SASMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

							}

							OCSMSGateway ocsmsGateway = ocsmsGatewayDao.findById(userSMSGateway.getGatewayId());
							if (ocsmsGateway != null) {
								SASMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								for (Listitem item : SASMSGateWayLbId.getItems()) {

									if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

										item.setSelected(true);
										break;
									}

								}

							}

						} // if
					} // if type != null && user.isEnableSMS()

					onSelect$SASMSGateWayLbId();

				}

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {

				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_PAKISTAN);

				indiaDivId.setVisible(false);
				usSMSDivId.setVisible(false);
				canadaSMSDivId.setVisible(false);
				saSMSDivId.setVisible(false);
				pakistanSMSDivId.setVisible(true);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(false);

				Components.removeAllChildren(pakistanSMSGateWayLbId);

				List<String> pakistanGatewayList = ocsmsGatewayDao.findGateways(countryStr,
						SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				for (String eachgateway : pakistanGatewayList) {

					Listitem item = new Listitem(eachgateway);
					item.setParent(pakistanSMSGateWayLbId);

				}

				if (pakistanSMSGateWayLbId.getItemCount() > 0) {
					pakistanSMSGateWayLbId.setSelectedIndex(0);

					if (type != null && user.isEnableSMS()) {

						UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(user.getUserId(),
								Constants.SMS_TYPE_PROMOTIONAL);
						if (userSMSGateway != null) {

							pakistanSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
									Constants.SMS_TYPE_PROMOTIONAL);
							if (retList != null) {
								UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
								pakistanSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

							}

							OCSMSGateway ocsmsGateway = ocsmsGatewayDao.findById(userSMSGateway.getGatewayId());
							if (ocsmsGateway != null) {
								pakistanSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								for (Listitem item : pakistanSMSGateWayLbId.getItems()) {

									if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

										item.setSelected(true);
										break;
									}

								}

							}

						} // if
					} // if type != null && user.isEnableSMS()

					onSelect$pakistanSMSGateWayLbId();

				}

				String gatwayStr = pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim();
			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_UAE);

				indiaDivId.setVisible(false);
				usSMSDivId.setVisible(false);
				canadaSMSDivId.setVisible(false);
				saSMSDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(true);
				genericSMSDivId.setVisible(false);

				Components.removeAllChildren(uaeSMSGateWayLbId);

				List<String> uaeGatewayList = ocsmsGatewayDao.findGateways(countryStr,
						SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
				for (String eachgateway : uaeGatewayList) {

					Listitem item = new Listitem(eachgateway);
					item.setParent(uaeSMSGateWayLbId);

				}

				if (uaeSMSGateWayLbId.getItemCount() > 0)
					uaeSMSGateWayLbId.setSelectedIndex(0);
				// *************START*******************
				if (type != null && user.isEnableSMS()) {

					Collection<String> accountTypes = SMSStatusCodes.countryCampValueMap.get(user.getCountryType())
							.values();
					String typeStr = Constants.STRING_NILL;
					for (String type : accountTypes) {

						if (typeStr.contains(type))
							continue;

						if (!typeStr.isEmpty())
							typeStr += Constants.DELIMETER_COMMA;

						typeStr += "'" + type + "'";

					}
					logger.debug("jsdhjsdhsdh" + typeStr);

					List<UserSMSGateway> userSMSGatewayList = userSMSGatewayDao.findAllBy(user.getUserId(), typeStr);

					String ocSMSGatewayIdsStr = Constants.STRING_NILL;
					if (userSMSGatewayList != null) {
						for (UserSMSGateway userSMSGateway : userSMSGatewayList) {

							if (!ocSMSGatewayIdsStr.isEmpty())
								ocSMSGatewayIdsStr += Constants.DELIMETER_COMMA;

							ocSMSGatewayIdsStr += userSMSGateway.getGatewayId().longValue();
						}
					}

					List<OCSMSGateway> ocsmsGatewayList = ocsmsGatewayDao.findByIds(ocSMSGatewayIdsStr);
					if (ocsmsGatewayList != null) {

						for (OCSMSGateway ocsmsGateway : ocsmsGatewayList) {

							if (ocsmsGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
								uaeSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								if (uaeSMSGateWayLbId.getItemCount() > 0) {
									for (Listitem item : uaeSMSGateWayLbId.getItems()) {

										if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

											item.setSelected(true);
											break;
										}

									}
								} // if(promotionalSMSGateWayLbId.getItemCount() > 0 )
							} else if (ocsmsGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {
								uaeTransactionalSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);

							}
						}

					} // if ocsmsGatewayList != null

					if (userSMSGatewayList != null) {

						// String ocSMSGatewayIdsStr = Constants.STRING_NILL;
						for (UserSMSGateway userSMSGateway : userSMSGatewayList) {

							/*
							 * if(!ocSMSGatewayIdsStr.isEmpty()) ocSMSGatewayIdsStr +=
							 * Constants.DELIMETER_COMMA;
							 * 
							 * ocSMSGatewayIdsStr += userSMSGateway.getId().longValue();
							 */
							if (userSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {

								uaeSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
								List<UserSMSSenderId> retList = userSMSSenderIdDao
										.findSenderIdBySMSType(user.getUserId(), Constants.SMS_TYPE_PROMOTIONAL);
								if (retList != null) {
									UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
									uaeSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

								}

							} else if (userSMSGateway.getAccountType()
									.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {

								uaeTransactionalSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
								uaeTransactionalSMSGateWayChkId.setChecked(true);
								onCheck$uaeTransactionalSMSGateWayChkId();

							}
						}

					}

				}

				// *************END******************

				if (uaeSMSGateWayLbId.getSelectedIndex() != -1)
					onSelect$uaeSMSGateWayLbId();

				/*
				 * if(uaeSMSGateWayLbId.getItemCount() > 0 ){
				 * uaeSMSGateWayLbId.setSelectedIndex(0);
				 * 
				 * if(type != null && user.isEnableSMS()){
				 * 
				 * UserSMSGateway userSMSGateway =
				 * userSMSGatewayDao.findByUserId(user.getUserId(),
				 * Constants.SMS_TYPE_PROMOTIONAL); if(userSMSGateway != null) {
				 * 
				 * uaeSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
				 * List<UserSMSSenderId> retList =
				 * userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
				 * Constants.SMS_TYPE_PROMOTIONAL); if(retList != null) { UserSMSSenderId
				 * userSMSSenderId = (UserSMSSenderId)retList.get(0);
				 * uaeSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);
				 * 
				 * }
				 * 
				 * OCSMSGateway ocsmsGateway =
				 * ocsmsGatewayDao.findById(userSMSGateway.getGatewayId()); if(ocsmsGateway !=
				 * null) { uaeSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
				 * 
				 * //display user's already selected gateway in edit mode for (Listitem item :
				 * uaeSMSGateWayLbId.getItems()) {
				 * 
				 * if(ocsmsGateway.getGatewayName().equals(item.getLabel())){
				 * 
				 * item.setSelected(true); break; }
				 * 
				 * 
				 * } for (Listitem item : uaeSMSUserNameGateWayLbId.getItems()) {
				 * 
				 * if(ocsmsGateway.getGatewayName().equals(item.getLabel())){
				 * 
				 * item.setSelected(true); break; }
				 * 
				 * 
				 * }
				 * 
				 * }
				 * 
				 * }//if }//if type != null && user.isEnableSMS()
				 * 
				 * onSelect$uaeSMSGateWayLbId();
				 * 
				 * 
				 * } String gatwayStr= uaeSMSGateWayLbId.getSelectedItem().getLabel().trim();
				 * if(type != null && user.isEnableSMS()){
				 * 
				 * Collection<String> accountTypes =
				 * SMSStatusCodes.countryCampValueMap.get(user.getCountryType()).values();
				 * String typeStr = Constants.STRING_NILL; for (String type : accountTypes) {
				 * 
				 * if(typeStr.contains(type)) continue;
				 * 
				 * if(!typeStr.isEmpty()) typeStr += Constants.DELIMETER_COMMA;
				 * 
				 * typeStr += "'"+type+"'";
				 * 
				 * } logger.debug("jsdhjsdhsdh"+typeStr);
				 * 
				 * 
				 * List<UserSMSGateway> userSMSGatewayList =
				 * userSMSGatewayDao.findAllBy(user.getUserId(), typeStr);
				 * 
				 * String ocSMSGatewayIdsStr = Constants.STRING_NILL; if(userSMSGatewayList !=
				 * null) { for (UserSMSGateway userSMSGateway : userSMSGatewayList) {
				 * 
				 * if(!ocSMSGatewayIdsStr.isEmpty()) ocSMSGatewayIdsStr +=
				 * Constants.DELIMETER_COMMA;
				 * 
				 * ocSMSGatewayIdsStr += userSMSGateway.getGatewayId().longValue(); } }
				 * 
				 * List<OCSMSGateway> ocsmsGatewayList =
				 * ocsmsGatewayDao.findByIds(ocSMSGatewayIdsStr); if(ocsmsGatewayList != null) {
				 * 
				 * for (OCSMSGateway ocsmsGateway : ocsmsGatewayList) {
				 * 
				 * 
				 * if(ocsmsGateway.getAccountType().equals(Constants.
				 * SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {
				 * uaeTransactionalSMSUserNameGateWayLbId.setAttribute("GatewayObj",
				 * ocsmsGateway);
				 * 
				 * 
				 * } }
				 * 
				 * 
				 * }//if ocsmsGatewayList != null
				 * 
				 * 
				 * 
				 * if(userSMSGatewayList != null) {
				 * logger.info("USERSMSGATEWAYLIST"+userSMSGatewayList);
				 * 
				 * //String ocSMSGatewayIdsStr = Constants.STRING_NILL; for (UserSMSGateway
				 * userSMSGateway : userSMSGatewayList) {
				 * 
				 * if(!ocSMSGatewayIdsStr.isEmpty()) ocSMSGatewayIdsStr +=
				 * Constants.DELIMETER_COMMA;
				 * 
				 * ocSMSGatewayIdsStr += userSMSGateway.getId().longValue();
				 * 
				 * logger.info("USERSMSGATEWAY"+userSMSGateway);
				 * 
				 * logger.info("USERSMSGATEWAYACTYPE"+userSMSGateway.getAccountType());
				 * 
				 * 
				 * if(userSMSGateway.getAccountType().equals(Constants.
				 * SMS_ACCOUNT_TYPE_TRANSACTIONAL)) {
				 * 
				 * uaeTransactionalSMSGateWayLbId.setAttribute("UserSMSGatewayObj",
				 * userSMSGateway); uaeTransactionalSMSGateWayChkId.setChecked(true);
				 * onCheck$uaeTransactionalSMSGateWayChkId();
				 * 
				 * } }
				 * 
				 * 
				 * 
				 * }
				 * 
				 * 
				 * }
				 * 
				 * uaeTransactionalSMSGateWayChkId.setChecked(true);
				 * onCheck$uaeTransactionalSMSGateWayChkId();
				 */

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_QATAR)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_QATAR);

			} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_KUWAIT)) {
				countryCodeTxtId.setValue("+" + Constants.SMS_COUNTRY_CODE_KUWAIT);

			} else {

				// above canada, qatar, kuwait are in general case now, so commented.

				Enumeration enm = countryCodes.propertyNames();
				while (enm.hasMoreElements()) {
					String currCountryCode = (String) enm.nextElement();
					// countryMap.put(currCountryCode.getCountryName() ,
					// currCountryCode.getCountryName());

					if (countryStr.equalsIgnoreCase(currCountryCode)) {
						countryCodeTxtId.setValue("+" + countryCodes.get(currCountryCode));
						break;
					}

				}

				indiaDivId.setVisible(false);
				usSMSDivId.setVisible(false);
				canadaSMSDivId.setVisible(false);
				saSMSDivId.setVisible(false);
				pakistanSMSDivId.setVisible(false);
				uaeSMSDivId.setVisible(false);
				genericSMSDivId.setVisible(true);

				Components.removeAllChildren(genericSMSGateWayLbId);
				Components.removeAllChildren(genericSMSUserNameGateWayLbId);

				List<String> uaeGatewayList = ocsmsGatewayDao.findGateways(countryStr,
						Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
				for (String eachgateway : uaeGatewayList) {

					Listitem item = new Listitem(eachgateway);
					item.setParent(genericSMSGateWayLbId);

				}

				if (genericSMSGateWayLbId.getItemCount() > 0) {
					genericSMSGateWayLbId.setSelectedIndex(0);

					if (type != null && user.isEnableSMS()) {

						UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(user.getUserId(),
								Constants.SMS_TYPE_PROMOTIONAL);
						if (userSMSGateway != null) {

							genericSMSGateWayLbId.setAttribute("UserSMSGatewayObj", userSMSGateway);
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
									Constants.SMS_TYPE_PROMOTIONAL);
							if (retList != null) {
								UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
								genericSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);

							}

							OCSMSGateway ocsmsGateway = ocsmsGatewayDao.findById(userSMSGateway.getGatewayId());
							if (ocsmsGateway != null) {
								genericSMSUserNameGateWayLbId.setAttribute("GatewayObj", ocsmsGateway);
								for (Listitem item : genericSMSUserNameGateWayLbId.getItems()) {

									if (ocsmsGateway.getGatewayName().equals(item.getLabel())) {

										item.setSelected(true);
										break;
									}

								}

							}

						} // if
					} // if type != null && user.isEnableSMS()

					onSelect$genericSMSGateWayLbId();

				}

			}

			/*
			 * else if(countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_US)){
			 * 
			 * countryCodeTxtId.setValue(Constants.SMS_COUNTRY_CODE_US);
			 * usSMSDivId.setVisible(true); indiaDivId.setVisible(false);
			 * 
			 * Components.removeAllChildren(USSMSGateWayLbId);
			 * 
			 * List<String> USoptinList = ocsmsGatewayDao.findGateways(countryStr,
			 * SMSStatusCodes.defaultSMSTypeMap.get(countryStr)); for (String optinStr :
			 * USoptinList) {
			 * 
			 * 
			 * Listitem item= new Listitem(optinStr); item.setParent(USSMSGateWayLbId);
			 * 
			 * 
			 * 
			 * }
			 * 
			 * if(USSMSGateWayLbId.getItemCount() > 0 ){
			 * USSMSGateWayLbId.setSelectedIndex(0); onSelect$USSMSGateWayLbId(); }
			 * 
			 * 
			 * }
			 */

		} catch (WrongValueException e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$pakistanSMSGateWayLbId() {

		try {
			Components.removeAllChildren(pakistanSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) pakistanSMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) pakistanSMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(pakistanSMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (pakistanSMSUserNameGateWayLbId.getItemCount() > 0) {

				pakistanSMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : pakistanSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
			String gatewayStr = pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) pakistanSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatewayStr, accName, Constants.SMS_TYPE_PROMOTIONAL);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$genericSMSGateWayLbId() {

		try {
			Components.removeAllChildren(genericSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = genericSMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) genericSMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) genericSMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(genericSMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (genericSMSUserNameGateWayLbId.getItemCount() > 0) {

				genericSMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : genericSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$uaeSMSGateWayLbId() {

		try {
			Components.removeAllChildren(uaeSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = uaeSMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) uaeSMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) uaeSMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(uaeSMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (uaeSMSUserNameGateWayLbId.getItemCount() > 0) {

				uaeSMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : uaeSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
			String gatewayStr = uaeSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) uaeSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatewayStr, accName, Constants.SMS_TYPE_PROMOTIONAL);

		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$uaeTransactionalSMSGateWayLbId() {

		try {
			Components.removeAllChildren(uaeTransactionalSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = uaeTransactionalSMSGateWayLbId.getSelectedItem().getLabel().trim();

			List<OCSMSGateway> transactionalNamesList = ocsmsGatewayDao.findTransactionalGatewaysUserIds(countryStr,
					gatwayStr);
			for (OCSMSGateway ocsmsGateway : transactionalNamesList) {

				Listitem item = new Listitem(ocsmsGateway.getUserId());
				item.setParent(uaeTransactionalSMSUserNameGateWayLbId);
				item.setValue(ocsmsGateway);

			}
			if (uaeTransactionalSMSUserNameGateWayLbId.getItemCount() > 0) {

				uaeTransactionalSMSUserNameGateWayLbId.setSelectedIndex(0);
				OCSMSGateway ocsmsGateway = (OCSMSGateway) uaeTransactionalSMSUserNameGateWayLbId
						.getAttribute("GatewayObj");
				if (ocsmsGateway != null) {

					for (Listitem item : uaeTransactionalSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					} // for

				} // if

			} // if

		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$USSMSGateWayLbId() {

		try {
			Components.removeAllChildren(USSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = USSMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) USSMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) USSMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(USSMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (USSMSUserNameGateWayLbId.getItemCount() > 0) {

				USSMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : USSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
			String gatewayStr = USSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) USSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatewayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}
	
	public void onSelect$CANADASMSGateWayLbId() {

		try {
			Components.removeAllChildren(CANADASMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = CANADASMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) CANADASMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) CANADASMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(CANADASMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (CANADASMSUserNameGateWayLbId.getItemCount() > 0) {

				CANADASMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : CANADASMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
			String gatewayStr = CANADASMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) CANADASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatewayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}//onSelect$CANADASMSGateWayLbId
	
	public void onSelect$SASMSGateWayLbId() {

		try {
			Components.removeAllChildren(SASMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = SASMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) SASMSGateWayLbId.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) SASMSUserNameGateWayLbId.getAttribute("GatewayObj");

			List<OCSMSGateway> outBoundlList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(countryStr, gatwayStr,
					SMSStatusCodes.defaultSMSTypeMap.get(countryStr));
			for (OCSMSGateway eachOcsmsGateway : outBoundlList) {

				Listitem item = new Listitem(eachOcsmsGateway.getUserId());
				item.setParent(SASMSUserNameGateWayLbId);
				item.setValue(eachOcsmsGateway);

			}

			if (SASMSUserNameGateWayLbId.getItemCount() > 0) {

				SASMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : SASMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}

			}
			String gatewayStr = SASMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) SASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatewayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}//onSelect$SASMSGateWayLbId

	public void getSenderIdsModel(String gatwayStr, String gatewayUserName, String accType) {
		try {
			userSMSSenderIdDao = (UserSMSSenderIdDao) SpringUtil.getBean("userSMSSenderIdDao");
			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			// String gatwayStr= selectListBox.getSelectedItem().getLabel().trim();
			List<OCSMSGateway> gatewayList = ocsmsGatewayDao.findOcSMSGatewaysByAccountName(countryStr, gatwayStr,
					accType, gatewayUserName);
			List<UserSMSSenderId> retList = null;
			if (user != null) {
				retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), accType);
			}
			String commaSeperatedSenderId = Constants.STRING_NILL;
			List<String> existingSenderIds = new ArrayList<String>();
			for (OCSMSGateway eachOcsmsGateway : gatewayList) {
				if (eachOcsmsGateway.getSenderId() != null)
					commaSeperatedSenderId = eachOcsmsGateway.getSenderId();
			}
			if (retList != null && retList.size() != 0) {
				for (UserSMSSenderId userSenderId : retList) {
					existingSenderIds.add(userSenderId.getSenderId());
				}
			}
			String[] senderIdList = commaSeperatedSenderId.split(Constants.DELIMETER_COMMA);
			if (senderIdList.length > 1) {
				selectSenderIdLblId.setVisible(true);
				selectSenderIdDiv.setVisible(true);
			} else {
				selectSenderIdLblId.setVisible(false);
				selectSenderIdDiv.setVisible(false);
			}
			Components.removeAllChildren(senderIdLBId);
			/*
			 * senderIdListHead = new Listhead(); Listheader headerLabel= new Listheader();
			 * Listheader headerWidth= new Listheader();
			 * headerLabel.setLabel("Select SenderId(s)"); headerWidth.setWidth("40px");
			 */
			if (gatewayList != null) {
				Listitem item = null;
				Listcell cell = null;
				for (String senderId : senderIdList) {
					item = new Listitem();
					item.setValue(senderId);
					cell = new Listcell();
					cell.setParent(item);

					cell = new Listcell(senderId);
					cell.setParent(item);
					item.setParent(senderIdLBId);
					if (!existingSenderIds.isEmpty() && existingSenderIds.contains(senderId)
							&& selectSenderIdDiv.isVisible())
						item.setSelected(true);
				}
			}
			/*
			 * headerLabel.setParent(senderIdListHead);
			 * headerWidth.setParent(senderIdListHead);
			 * senderIdListHead.setParent(senderIdLBId);
			 */

		} catch (Exception e) {
			logger.debug("Exception ", e);
			return;
		}
	} //getSenderIdsModel
	//app-3701
	public void getTRSenderIdsModel(String gatwayStr, String gatewayUserName, String accType) {
		try {
			logger.info("---------- entered into getTRSenderIdsModel -----------");
			userSMSSenderIdDao = (UserSMSSenderIdDao) SpringUtil.getBean("userSMSSenderIdDao");
			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			// String gatwayStr= selectListBox.getSelectedItem().getLabel().trim();
			List<OCSMSGateway> gatewayList = ocsmsGatewayDao.findOcSMSGatewaysByAccountName(countryStr, gatwayStr,
					accType, gatewayUserName);
			List<UserSMSSenderId> retList = null;
			if (user != null) {
				retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), accType);
			}
			String commaSeperatedTRSenderId = Constants.STRING_NILL;
			List<String> existingTRSenderIds = new ArrayList<String>();
			for (OCSMSGateway eachOcsmsGateway : gatewayList) {
				if (eachOcsmsGateway.getSenderId() != null)
					commaSeperatedTRSenderId = eachOcsmsGateway.getSenderId();
			}
			if (retList != null && retList.size() != 0) {
				for (UserSMSSenderId userSenderId : retList) {
					existingTRSenderIds.add(userSenderId.getSenderId());
				}
			}
			String[] trsenderIdList = commaSeperatedTRSenderId.split(Constants.DELIMETER_COMMA);
			if (trsenderIdList.length > 1) {
				selectTRSenderIdLblId.setVisible(true);
				selectTRSenderIdDiv.setVisible(true);
			} else {
				selectTRSenderIdLblId.setVisible(false);
				selectTRSenderIdDiv.setVisible(false);
			}
			Components.removeAllChildren(trsenderIdLBId);
			
			if (gatewayList != null) {
				Listitem item = null;
				Listcell cell = null;
				for (String trsenderId : trsenderIdList) {
					item = new Listitem();
					item.setValue(trsenderId);
					cell = new Listcell();
					cell.setParent(item);

					cell = new Listcell(trsenderId);
					cell.setParent(item);
					item.setParent(trsenderIdLBId);
					if (!existingTRSenderIds.isEmpty() && existingTRSenderIds.contains(trsenderId)
							&& selectTRSenderIdDiv.isVisible())
						item.setSelected(true);
				}
			}

		} catch (Exception e) {
			logger.debug("Exception ", e);
			return;
		}
		logger.info("---------- exiting from getTRSenderIdsModel -----------");
	} //getTRSenderIdsModel

	public void onSelect$promotionalSMSGateWayLbId() {

		try {

			Components.removeAllChildren(promotionalSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
			UserSMSGateway userSMSGateway = (UserSMSGateway) promotionalSMSGateWayLbId
					.getAttribute("UserSMSGatewayObj");
			OCSMSGateway ocsmsGateway = (OCSMSGateway) promotionalSMSUserNameGateWayLbId.getAttribute("GatewayObj");
			List<OCSMSGateway> promotionalList = ocsmsGatewayDao.findPromotionalGatewaysUserIds(countryStr, gatwayStr);
			for (OCSMSGateway eachocsmsGateway : promotionalList) {

				Listitem item = new Listitem(eachocsmsGateway.getUserId());
				item.setParent(promotionalSMSUserNameGateWayLbId);
				item.setValue(eachocsmsGateway);

			}

			if (promotionalSMSUserNameGateWayLbId.getItemCount() > 0) {

				promotionalSMSUserNameGateWayLbId.setSelectedIndex(0);
				if (ocsmsGateway != null) {
					for (Listitem item : promotionalSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					}

				}
			}
			
			onSelect$promotionalSMSUserNameGateWayLbId(); //APP-3965
			
			/*
			 * String gatewayStr=
			 * promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim(); String
			 * accName=((OCSMSGateway)promotionalSMSUserNameGateWayLbId.getSelectedItem().
			 * getValue()).getUserId();
			 * getSenderIdsModel(gatewayStr,accName,Constants.SMS_TYPE_PROMOTIONAL);
			 */
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$transactionalSMSGateWayLbId() {

		try {
			Components.removeAllChildren(transactionalSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim();

			List<OCSMSGateway> transactionalNamesList = ocsmsGatewayDao.findTransactionalGatewaysUserIds(countryStr,
					gatwayStr);
			for (OCSMSGateway ocsmsGateway : transactionalNamesList) {

				Listitem item = new Listitem(ocsmsGateway.getUserId());
				item.setParent(transactionalSMSUserNameGateWayLbId);
				item.setValue(ocsmsGateway);

			}
			if (transactionalSMSUserNameGateWayLbId.getItemCount() > 0) {

				transactionalSMSUserNameGateWayLbId.setSelectedIndex(0);
				OCSMSGateway ocsmsGateway = (OCSMSGateway) transactionalSMSUserNameGateWayLbId
						.getAttribute("GatewayObj");
				if (ocsmsGateway != null) {

					for (Listitem item : transactionalSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					} // for

				} // if

			} // if
			
			onSelect$transactionalSMSUserNameGateWayLbId(); // APP-3965

		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$optinSMSGateWayLbId() {

		try {
			Components.removeAllChildren(optinSMSUserNameGateWayLbId);

			String countryStr = countryLbId.getSelectedItem().getLabel().trim();
			String gatwayStr = optinSMSGateWayLbId.getSelectedItem().getLabel().trim();

			List<OCSMSGateway> optinNamesList = ocsmsGatewayDao.findOptinGatewaysUserIds(countryStr, gatwayStr);
			for (OCSMSGateway ocsmsGateway : optinNamesList) {

				Listitem item = new Listitem(ocsmsGateway.getUserId());
				item.setParent(optinSMSUserNameGateWayLbId);
				item.setValue(ocsmsGateway);

			}
			if (optinSMSUserNameGateWayLbId.getItemCount() > 0) {

				optinSMSUserNameGateWayLbId.setSelectedIndex(0);

				OCSMSGateway ocsmsGateway = (OCSMSGateway) optinSMSUserNameGateWayLbId.getAttribute("GatewayObj");
				if (ocsmsGateway != null) {

					for (Listitem item : optinSMSUserNameGateWayLbId.getItems()) {

						if (((OCSMSGateway) item.getValue()).getId().equals(ocsmsGateway.getId())) {

							item.setSelected(true);
							break;
						}

					} // for

				} // if

			}

		} catch (Exception e) {

			logger.error("Exception ", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onCheck$enableSmsChkId() {

		logger.debug("========in ==========");
		defaultCreateGatewaySettings();
		onSelect$countryLbId();

		enableSmsDivId.setVisible(enableSmsChkId.isChecked());

	}// onCheck$enableSmsChkId

	public void onCheck$enableMsdCalChkId() {

		optinMsdCalDivId.setVisible(enableMsdCalChkId.isChecked());
		optinListDivId.setVisible(enableMsdCalChkId.isChecked() || enableKeywordChkId.isChecked());
	}

	public void onCheck$enableKeywordChkId() {

		enableMsdCallsKeywordsDivId.setVisible(enableKeywordChkId.isChecked());
		optinListDivId.setVisible(enableKeywordChkId.isChecked() || enableMsdCalChkId.isChecked());
	}

	public void onCheck$transactionalSMSGateWayChkId() {

		try {
			if (transactionalSMSGateWayChkId.isChecked()) {

				if (transactionalSMSGateWayLbId.getAttribute("IsPopulated") == null) {

					Components.removeAllChildren(transactionalSMSGateWayLbId);
					List<String> transactionalList = ocsmsGatewayDao
							.findTransactionalGateways(countryLbId.getSelectedItem().getLabel());

					for (String transactionalStr : transactionalList) {

						Listitem item = new Listitem(transactionalStr);
						item.setParent(transactionalSMSGateWayLbId);

					}
					transactionalSMSGateWayLbId.setAttribute("IsPOpulated", true);
					if (transactionalSMSGateWayLbId.getItemCount() > 0)
						transactionalSMSGateWayLbId.setSelectedIndex(0);
				}

				OCSMSGateway OCSMSGateway = (OCSMSGateway) transactionalSMSUserNameGateWayLbId
						.getAttribute("GatewayObj");
				if (OCSMSGateway != null) {
					for (Listitem item : transactionalSMSGateWayLbId.getItems()) {

						if (item.getLabel().equals(OCSMSGateway.getGatewayName())) {

							item.setSelected(true);
							break;
						}
					}
				}
				if (transactionalSMSGateWayLbId.getSelectedIndex() != -1)
					onSelect$transactionalSMSGateWayLbId();

				// Users user= (Users)session.getAttribute("USEROBJ");
				List<TransactionalTemplates> templateList = null;
				String senderID = null;
				// set TransactionalTemplates if any
				if (type != null && user != null) {

					List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
							Constants.SMS_TYPE_TRANSACTIONAL);
					if (retList != null) {
						UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
						//transSenderIdTxtBxId.setAttribute("SMSSenderID", userSMSSenderId);
						transactionalSMSGateWayLbId.setAttribute("SMSSenderID", userSMSSenderId);//app-3701
						senderID = userSMSSenderId != null ? userSMSSenderId.getSenderId() : null;
						//transSenderIdTxtBxId.setText(senderID != null ? senderID : Constants.STRING_NILL);	app-3701
					}

					templateList = transactionalTemplatesDao
							.findTemplatesByOrgId(user.getUserOrganization().getUserOrgId());
				}
				Components.removeAllChildren(approveTempCmbBoxId);
				Comboitem combItem = null;
				combItem = new Comboitem("--select--");
				combItem.setParent(approveTempCmbBoxId);

				if (templateList != null && templateList.size() > 0) {

					for (TransactionalTemplates eachObj : templateList) {
						combItem = new Comboitem(eachObj.getTemplateName());
						combItem.setDescription(eachObj.getStatus() == 0 ? "Pending" : "Approved");
						combItem.setValue(eachObj);
						combItem.setParent(approveTempCmbBoxId);
					}
				}

				if (approveTempCmbBoxId.getItemCount() > 0)
					approveTempCmbBoxId.setSelectedIndex(0);

			}

			transactionalDivId.setVisible(transactionalSMSGateWayChkId.isChecked());
			tempIdRow.setVisible(transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim()
					.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE) && approveTempCmbBoxId.getSelectedIndex() != 0);
		} catch (WrongValueException e) {

			logger.error("Exception ", e);
		} catch (Exception e) {

			logger.error("Exception ", e);
		}

	}// onCheck$transactionalSMSGateWayChkId

	public void onCheck$uaeTransactionalSMSGateWayChkId() {

		try {
			if (uaeTransactionalSMSGateWayChkId.isChecked()) {

				if (uaeTransactionalSMSGateWayLbId.getAttribute("IsPopulated") == null) {

					Components.removeAllChildren(uaeTransactionalSMSGateWayLbId);
					List<String> transactionalList = ocsmsGatewayDao
							.findTransactionalGateways(countryLbId.getSelectedItem().getLabel());

					for (String transactionalStr : transactionalList) {

						Listitem item = new Listitem(transactionalStr);
						item.setParent(uaeTransactionalSMSGateWayLbId);

					}
					uaeTransactionalSMSGateWayLbId.setAttribute("IsPOpulated", true);
					if (uaeTransactionalSMSGateWayLbId.getItemCount() > 0)
						uaeTransactionalSMSGateWayLbId.setSelectedIndex(0);
				}

				OCSMSGateway OCSMSGateway = (OCSMSGateway) uaeTransactionalSMSUserNameGateWayLbId
						.getAttribute("GatewayObj");
				if (OCSMSGateway != null) {
					for (Listitem item : uaeTransactionalSMSGateWayLbId.getItems()) {

						if (item.getLabel().equals(OCSMSGateway.getGatewayName())) {

							item.setSelected(true);
							break;
						}
					}
				}
				if (uaeTransactionalSMSGateWayLbId.getSelectedIndex() != -1)
					onSelect$uaeTransactionalSMSGateWayLbId();

				// Users user= (Users)session.getAttribute("USEROBJ");
				List<TransactionalTemplates> templateList = null;
				String senderID = null;
				// set TransactionalTemplates if any
				if (type != null && user != null) {

					List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
							Constants.SMS_TYPE_TRANSACTIONAL);
					if (retList != null) {
						UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
						uaeTransSenderIdTxtBxId.setAttribute("SMSSenderID", userSMSSenderId);
						senderID = userSMSSenderId != null ? userSMSSenderId.getSenderId() : null;
						uaeTransSenderIdTxtBxId.setText(senderID != null ? senderID : Constants.STRING_NILL);
					}

					templateList = transactionalTemplatesDao
							.findTemplatesByOrgId(user.getUserOrganization().getUserOrgId());
				}
				Components.removeAllChildren(uaeApproveTempCmbBoxId);
				Comboitem combItem = null;
				combItem = new Comboitem("--select--");
				combItem.setParent(uaeApproveTempCmbBoxId);

				if (templateList != null && templateList.size() > 0) {

					for (TransactionalTemplates eachObj : templateList) {
						combItem = new Comboitem(eachObj.getTemplateName());
						combItem.setDescription(eachObj.getStatus() == 0 ? "Pending" : "Approved");
						combItem.setValue(eachObj);
						combItem.setParent(uaeApproveTempCmbBoxId);
					}
				}

				if (uaeApproveTempCmbBoxId.getItemCount() > 0)
					uaeApproveTempCmbBoxId.setSelectedIndex(0);

			}

			uaeTransactionalDivId.setVisible(uaeTransactionalSMSGateWayChkId.isChecked());
			uaeTempIdRow.setVisible(uaeTransactionalSMSGateWayLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(
					Constants.USER_SMSTOOL_SYNAPSE) && uaeApproveTempCmbBoxId.getSelectedIndex() != 0);
		} catch (WrongValueException e) {

			logger.error("Exception ", e);
		} catch (Exception e) {

			logger.error("Exception ", e);
		}

	}// onCheck$uaeTransactionalSMSGateWayChkId

	public void onCheck$optinSMSGateWayChkId() {

		try {
			if (optinSMSGateWayChkId.isChecked()) {

				if (optinSMSGateWayLbId.getAttribute("IsPopulated") == null) {

					Components.removeAllChildren(optinSMSGateWayLbId);
					List<String> optinList = ocsmsGatewayDao
							.findOptinGateways(countryLbId.getSelectedItem().getLabel());

					for (String optinStr : optinList) {

						Listitem item = new Listitem(optinStr);
						item.setParent(optinSMSGateWayLbId);

					}
					optinSMSGateWayLbId.setAttribute("IsPOpulated", true);
					if (optinSMSGateWayLbId.getItemCount() > 0)
						optinSMSGateWayLbId.setSelectedIndex(0);

					List<String> optinMsdCalNumList = countryReceivingNumbersDao.getReceivingNumByCountry(
							Constants.SMS_COUNTRY_INDIA, Constants.RECEVING_NUMBER_TYPE_MISSEDCALL);

					List<String> optinMsgRecvlNumList = countryReceivingNumbersDao.getReceivingNumByCountry(
							Constants.SMS_COUNTRY_INDIA, Constants.RECEVING_NUMBER_TYPE_OPTIN);

					if (optinMsdCalNumList.size() > 0) {

						for (String msdCalStr : optinMsdCalNumList) {
							String label = msdCalStr;
							if (msdCalStr.length() > 10)
								label = "+" + label;
							Listitem item = new Listitem(label, msdCalStr);
							item.setParent(optinMsdCalNumLBId);
						}

						if (optinMsdCalNumLBId.getItemCount() > 0)
							optinMsdCalNumLBId.setSelectedIndex(0);
					}

					if (optinMsgRecvlNumList.size() > 0) {
						for (String recvNumStr : optinMsgRecvlNumList) {

							String labelStr = recvNumStr;

							if (recvNumStr.length() > 10)
								labelStr = "+" + labelStr;

							Listitem item = new Listitem(labelStr, recvNumStr);
							item.setParent(optinKeywordMsdCalNumLBId);
						}

						if (optinKeywordMsdCalNumLBId.getItemCount() > 0)
							optinKeywordMsdCalNumLBId.setSelectedIndex(0);
					}

					String autoOptinMsg = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_SMS_OPTIN_MESSAGE);
					optInMsgTbId.setValue(autoOptinMsg);

					if (type != null) {

						List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(),
								Constants.SMS_SENDING_TYPE_OPTIN);
						if (retList != null) {
							UserSMSSenderId userSMSSenderId = (UserSMSSenderId) retList.get(0);
							optinSenderIdTxtBxId.setAttribute("SMSSenderID", userSMSSenderId);
							String senderID = userSMSSenderId != null ? userSMSSenderId.getSenderId() : null;
							optinSenderIdTxtBxId.setText(senderID != null ? senderID : Constants.STRING_NILL);
						}

						List<SMSSettings> retSettingsList = smsSettingsDao
								.findAllByUserOrg(user.getUserOrganization().getUserOrgId());

						// logger.info("sms settings size is"+retSettingsList.size());

						if (retSettingsList != null) {

							List<MailingList> mlist = mailingListDao.findAllByCurrentUser(user.getUserId());

							// logger.info("optin mlist size is"+retSettingsList.size());

							for (SMSSettings smsSettings : retSettingsList) {

								if (smsSettings.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) {
									logger.info("enterd in optin type");

									enableKeywordChkId.setChecked(smsSettings.getKeyword() != null);
									enableMsdCalChkId.setChecked(smsSettings.getOptinMissedCalNumber() != null);

									if (user.getUserId() == smsSettings.getUserId().getUserId()) {
										logger.info("existed obj");
										MessageUtil.setMessage("SMS settings are available for this organization",
												"color:red;");
										return;
									}
									// if(user.isConsiderSMSSettings()) {

									if (smsSettings.getAutoResponse() != null)
										optInMsgTbId.setText(smsSettings.getAutoResponse());// == null ? autoOptinMsg :
																							// smsSettings.getAutoResponse());

									if (enableKeywordChkId.isChecked()) {
										logger.info("enable keyword obj");
										optinKeywordTxtBxId.setAttribute("SMSSettings", smsSettings);
										optinKeywordTxtBxId.setText(smsSettings.getKeyword());

										/*
										 * optinKeywordMsdCalNumTxtBxId.setAttribute("SHORTCODE",smsSettings.
										 * getShortCode());
										 * optinKeywordMsdCalNumTxtBxId.setText("+"+smsSettings.getShortCode());
										 */

										optinKeywordMsdCalNumLBId.setAttribute("SHORTCODE",
												smsSettings.getOptinMissedCalNumber());
										if (optinKeywordMsdCalNumLBId.getItemCount() > 0) {

											for (Listitem item : optinKeywordMsdCalNumLBId.getItems()) {

												if (item.getValue() != null && item.getValue().toString()
														.equalsIgnoreCase(smsSettings.getShortCode())) {

													item.setSelected(true);

												}

											}

										}

										onCheck$enableKeywordChkId();
										optinListDivId.setVisible(true);

									}
									if (enableMsdCalChkId.isChecked()) {
										logger.info("enable msdcall obj");

										/*
										 * optinMsdCalNumTxtBxId.setAttribute("SHORTCODE",smsSettings.
										 * getOptinMissedCalNumber());
										 * optinMsdCalNumTxtBxId.setText("+"+smsSettings.getOptinMissedCalNumber() );
										 */

										optinMsdCalNumLBId.setAttribute("SHORTCODE",
												smsSettings.getOptinMissedCalNumber());
										if (optinMsdCalNumLBId.getItemCount() > 0) {

											for (Listitem item : optinMsdCalNumLBId.getItems()) {

												if (item.getValue() != null && item.getValue().toString()
														.equalsIgnoreCase(smsSettings.getOptinMissedCalNumber())) {

													item.setSelected(true);

												}

											}

										}

										onCheck$enableMsdCalChkId();
										optinListDivId.setVisible(true);
									}

									Long listId = smsSettings.getListId();

									if (listId != null) {
										if (mlist != null && mlist.size() > 0) {
											for (MailingList mailingList : mlist) {

												if (mailingList.getListId() == listId.longValue()) {

													Listitem optItem = new Listitem(mailingList.getListName());

													optItem.setParent(optinListLBId);
													optItem.setSelected(true);
													break;

												}

											}
										} else {

											/*
											 * MailingList optList = new MailingList();
											 * optList.setListName(Constants.SMS_OPTIN_LIST);
											 */
											Listitem item = new Listitem(Constants.SMS_OPTIN_LIST);
											item.setParent(optinListLBId);
											item.setSelected(true);
										}
									}

								} else if (smsSettings.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) {

									optoutKeywordTxtBxId.setText(smsSettings.getKeyword());
									optoutKeywordTxtBxId.setAttribute("SMSSettings", smsSettings);

								}

							} // for e settings

						} // if got setting

					} // if user exists

				} // if the values not populated already

				OCSMSGateway OCSMSGateway = (OCSMSGateway) optinSMSUserNameGateWayLbId.getAttribute("GatewayObj");
				if (OCSMSGateway != null) {
					for (Listitem item : optinSMSGateWayLbId.getItems()) {

						if (item.getLabel().equals(OCSMSGateway.getGatewayName())) {

							item.setSelected(true);
							break;
						}
					}
				}
				if (optinSMSGateWayLbId.getSelectedIndex() != -1)
					onSelect$optinSMSGateWayLbId();

				/*
				 * MailingList optList = new MailingList();
				 * optList.setListName(Constants.SMS_OPTIN_LIST);
				 */
				Listitem item = new Listitem(Constants.SMS_OPTIN_LIST);
				item.setParent(optinListLBId);
				item.setSelected(true);
				if (!optInMsgTbId.getText().trim().isEmpty())
					getCharCount(optInMsgTbId.getText().trim());
			} // if

			optinSmsDivId.setVisible(optinSMSGateWayChkId.isChecked());
		} catch (Exception e) {

			logger.debug("Exception ", e);
		}

	}// onCheck$optinSMSGateWayChkId

	public String getAccType(String accType) {

		String AccountType = Constants.STRING_NILL;

		if (accType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {

			AccountType = Constants.SMS_TYPE_NAME_TRANSACTIONAL;
		} else if (accType.equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) {

			AccountType = Constants.SMS_TYPE_NAME_PROMOTIONAL;
		} else if (accType.equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)) {

			AccountType = Constants.SMS_TYPE_NAME_2_WAY;
		} else if (accType.equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) {

			AccountType = Constants.SMS_TYPE_NAME_OUTBOUND;
		} else if (accType.equalsIgnoreCase(Constants.SMS_SENDING_TYPE_OPTIN)) {

			AccountType = Constants.SMS_TYPE_NAME_OPTIN;
		}

		return AccountType;
	}// getAccType

	public void onSelect$approveTempCmbBoxId() {
		if (approveTempCmbBoxId.getSelectedIndex() == 0)
			return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		// if(trTemplateObj.getStatus() == 1){
		tempIdRow.setVisible(transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim()
				.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE) && approveTempCmbBoxId.getSelectedIndex() != 0);
		templateId.setText(
				trTemplateObj.getTemplateRegisteredId() != null && !trTemplateObj.getTemplateRegisteredId().isEmpty()
						? trTemplateObj.getTemplateRegisteredId()
						: "");
		// }
	}

	public void onClick$approveTempTbId() {
		if (approveTempCmbBoxId.getSelectedIndex() == 0)
			return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		if (trTemplateObj.getStatus() == 0) {
			trTemplateObj.setStatus(1);
			if (tempIdRow.isVisible() && templateId.getText().isEmpty()) {
				MessageUtil.setMessage("Please provide Template Registration ID.", "color:red;");
				return;
			}
			if (tempIdRow.isVisible() && !templateId.getText().isEmpty())
				trTemplateObj.setTemplateRegisteredId(templateId.getText());
			// transactionalTemplatesDao.saveOrUpdate(trTemplateObj);
			transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
		} else if ((trTemplateObj.getStatus() == 1)) {
			if (tempIdRow.isVisible() && templateId.getText().isEmpty()) {
				MessageUtil.setMessage("Please provide Template Registration ID.", "color:red;");
				return;
			}

		} else
			return;
		combItem.setDescription("Approved");
		MessageUtil.setMessage("Template  approved successfully.", "color:green;");
	} // onClick$approveTempTbId

	/*
	 * public void onSelect$uaeApproveTempCmbBoxId() { if
	 * (uaeApproveTempCmbBoxId.getSelectedIndex() == 0) return; Comboitem combItem =
	 * uaeApproveTempCmbBoxId.getSelectedItem(); TransactionalTemplates
	 * trTemplateObj = (TransactionalTemplates) combItem.getValue(); //
	 * if(trTemplateObj.getStatus() == 1){
	 * uaeTempIdRow.setVisible(uaeTransactionalSMSGateWayLbId.getSelectedItem().
	 * getLabel().trim() .equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE) &&
	 * uaeApproveTempCmbBoxId.getSelectedIndex() != 0); uaeTemplateId.setText(
	 * trTemplateObj.getTemplateRegisteredId() != null &&
	 * !trTemplateObj.getTemplateRegisteredId().isEmpty() ?
	 * trTemplateObj.getTemplateRegisteredId() : ""); // } }
	 */

	public void onClick$tempContPreviewTbId() {

		// Users user= (Users)session.getAttribute("USEROBJ");
		// String htmlContent=campaign.getHtmlText();
		if (approveTempCmbBoxId.getSelectedIndex() == 0)
			return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		Utility.showPreview(previewIframeWin$iframeId, user.getUserName(), trTemplateObj.getTemplateContent());
		previewIframeWin.setVisible(true);

	} // onClick$tempContPreviewTbId

	public void onClick$uaeApproveTempTbId() {
		if (uaeApproveTempCmbBoxId.getSelectedIndex() == 0)
			return;
		Comboitem combItem = uaeApproveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		if (trTemplateObj.getStatus() == 0) {
			trTemplateObj.setStatus(1);
			/*
			 * if (uaeTempIdRow.isVisible() && uaeTemplateId.getText().isEmpty()) {
			 * MessageUtil.setMessage("Please provide Template Registration ID.",
			 * "color:red;"); return; }
			 */
			/*
			 * if (uaeTempIdRow.isVisible() && !uaeTemplateId.getText().isEmpty())
			 * trTemplateObj.setTemplateRegisteredId(uaeTemplateId.getText()); //
			 * transactionalTemplatesDao.saveOrUpdate(trTemplateObj);
			 * transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
			 */
		} /*
			 * else if ((trTemplateObj.getStatus() == 1)) { if (uaeTempIdRow.isVisible() &&
			 * uaeTemplateId.getText().isEmpty()) {
			 * MessageUtil.setMessage("Please provide Template Registration ID.",
			 * "color:red;"); return; }
			 * 
			 * } else return;
			 */
		combItem.setDescription("Approved");
		MessageUtil.setMessage("Template  approved successfully.", "color:green;");
	} // onClick$approveTempTbId

	public void onClick$uaeTempContPreviewTbId() {

		// Users user= (Users)session.getAttribute("USEROBJ");
		// String htmlContent=campaign.getHtmlText();
		if (uaeApproveTempCmbBoxId.getSelectedIndex() == 0)
			return;
		Comboitem combItem = uaeApproveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		Utility.showPreview(previewIframeWin$iframeId, user.getUserName(), trTemplateObj.getTemplateContent());
		previewIframeWin.setVisible(true);

	} // onClick$tempContPreviewTbId

	public boolean validateSMSSettings() {

		if (enableSmsChkId.isChecked()) {

			if (userSmsLimitIntBoxId.getValue() == null || userSmsLimitIntBoxId.getValue() == 0) {
				MessageUtil.setMessage("Please provide SMS limit for user.", "color:red;");
				return false;

			}

			if (userSmsLimitIntBoxId.getValue() < 0) {
				MessageUtil.setMessage("Please provide valid SMS limit for user.", "color:red;");
				return false;

			}

			/*
			 * String number = msgRecvNumCmbId.getText(); if(!number.trim().isEmpty()) {
			 * 
			 * Object item = findOptionalComboitem(msgRecvNumCmbId, number); if(item ==
			 * null) {
			 * 
			 * MessageUtil.setMessage("Please confirm the Message receiving number '"
			 * +number+"' by clicking on 'add'", "color:red;"); return false;
			 * 
			 * } }
			 */
			/*
			 * if(countryCodeTxtBxId.getValue().trim().length() == 0){
			 * MessageUtil.setMessage("Country code cannot be empty.", "color:red;"); return
			 * false; }
			 */

			if (countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

				if (promotionalSMSGateWayLbId.getSelectedIndex() == -1
						|| promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select promotional gateway.", "color:red;");
					return false;
				}

				if (promotionalSMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| promotionalSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select promotional account name.", "color:red;");
					return false;
				}

				if (transactionalSMSGateWayChkId.isChecked()) {

					if (transactionalSMSGateWayLbId.getSelectedIndex() == -1
							|| transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select transactional gateway.", "color:red;");
						return false;
					}

					if (transactionalSMSUserNameGateWayLbId.getSelectedIndex() == -1
							|| transactionalSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select transactional account name.", "color:red;");
						return false;
					}

					/*if (transSenderIdTxtBxId.getValue().trim().length() == 0) {
						MessageUtil.setMessage("Please provide a valid transactional sender ID.", "red", "top");
						return false;
					}*/ //app-3701

				} // trans - check

				if (optinSMSGateWayChkId.isChecked()) {

					if (!transactionalSMSGateWayChkId.isChecked()) {

						MessageUtil.setMessage("Opt-in set up can not be done without transactional set up.",
								"color:red;");
						return false;

					}
					if (optinSMSGateWayLbId.getSelectedIndex() == -1
							|| optinSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select opt-in gateway", "color:red;");
						return false;
					}

					if (optinSMSUserNameGateWayLbId.getSelectedIndex() == -1
							|| optinSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select opt-in account name ", "color:red;");
						return false;
					}

					if (optinSenderIdTxtBxId.getValue().trim().length() == 0) {
						MessageUtil.setMessage("Please provide a valid opt-in sender ID.", "red", "top");
						return false;
					}

					// optinSenderIdTxtBxId.setAttribute("SMSSenderID",
					// optinSenderIdTxtBxId.getValue().trim());

					if (!enableMsdCalChkId.isChecked() && !enableKeywordChkId.isChecked()) {

						MessageUtil.setMessage("Please choose one of the opt-in mechanisms provided.", "red", "top");
						return false;
					}

					if (enableMsdCalChkId.isChecked()) {

						if (optinMsdCalNumLBId.getItemCount() == 0) {

							MessageUtil.setMessage(
									"Cannot enable missed cal opt-in as there is no missed call number found",
									"color:red;");
						}

						// cannot enable missed cal opt-in as there is no missed call number found

						if (optinMsdCalNumLBId.getSelectedItem() == null) {

							MessageUtil.setMessage("Please select a missed call number to enable opt-in .", "red",
									"top");
							return false;

						}

						String msgCallNum = optinMsdCalNumLBId.getSelectedItem().getValue();
						/*
						 * msgCallNum = (msgCallNum.startsWith("+") ? msgCallNum.replace("+",
						 * Constants.STRING_NILL) : msgCallNum); try { Long.parseLong(msgCallNum); }
						 * catch (NumberFormatException e) {
						 * 
						 * MessageUtil.setMessage("Please enter valid opt-in missed call number.",
						 * "color:red;"); return false;
						 * 
						 * }
						 */
						optinMsdCalNumLBId.setAttribute("SHORTCODE", msgCallNum);

						boolean isExist = false;

						Object optin = smsSettingsDao
								.findByMissedCallNumber((String) optinMsdCalNumLBId.getAttribute("SHORTCODE"));
						if (optin != null) {
							Long orgID = (optin instanceof SMSSettings ? ((SMSSettings) optin).getOrgId().longValue()
									: (optin instanceof OrgSMSkeywords ? ((OrgSMSkeywords) optin).getOrgId().longValue()
											: null));

							if (orgID != null && ((type != null
									&& user.getUserOrganization().getUserOrgId().longValue() != orgID.longValue()))) {
								isExist = true;
							}
						}

						if (isExist) {

							MessageUtil.setMessage(
									"Please select another opt-in missed call number. \n This number is already assigned to others.",
									"color:red;");
							return false;

						}

					}

					if (enableKeywordChkId.isChecked()) {

						if (optinKeywordMsdCalNumLBId.getItemCount() == 0) {

							MessageUtil.setMessage(
									"Cannot enable keyword opt-in as there is no  receiving number found",
									"color:red;");
						}

						if (optinKeywordMsdCalNumLBId.getSelectedItem() == null) {

							MessageUtil.setMessage("Please select a receiving number for opt-in and opt-out keywords.",
									"red", "top");
							return false;
						}

						String msgRcvNum = optinKeywordMsdCalNumLBId.getSelectedItem().getValue();
						/*
						 * msgRcvNum = msgRcvNum.startsWith("+") ? msgRcvNum.replace("+",
						 * Constants.STRING_NILL) : msgRcvNum; try { Long.parseLong(msgRcvNum); } catch
						 * (NumberFormatException e) {
						 * 
						 * MessageUtil.
						 * setMessage("Please enter valid opt-in/opt-out message receiving number.",
						 * "color:red;"); return false;
						 * 
						 * }
						 */

						optinKeywordMsdCalNumLBId.setAttribute("SHORTCODE", msgRcvNum);
					}

					if (enableKeywordChkId.isChecked()) {
						String value = optinKeywordTxtBxId.getValue().trim();
						if (value.length() == 0) {
							MessageUtil.setMessage("Please provide opt-in keyword.", "red", "top");
							return false;
						}

						String[] keywordRuleArr = SMSStatusCodes.keywordRuleMap
								.get(countryLbId.getSelectedItem().getLabel());
						int minlength = Integer.parseInt(keywordRuleArr[1]);
						int maxlength = Integer.parseInt(keywordRuleArr[2]);

						if (minlength != 0 && maxlength != 0) {
							if (value.length() < minlength) {

								MessageUtil.setMessage(
										"Opt-in keyword's length should be minimum of " + minlength + " characters.",
										"color:red;");
								optinKeywordTxtBxId.setFocus(true);
								return false;

							}

							if (value.length() > maxlength) {

								MessageUtil.setMessage(
										"Opt-in keyword's length should be maximum of " + maxlength + " characters.",
										"color:red;");
								optinKeywordTxtBxId.setFocus(true);
								return false;

							}
						} // else if

						String keywordPattern = keywordRuleArr[0];

						if (!Utility.validateBy(keywordPattern, value)) {
							MessageUtil.setMessage(
									"Provide valid opt-in keyword, special characters are not allowed except "
											+ keywordRuleArr[3],
									"color:red;");
							optinKeywordTxtBxId.setFocus(true);
							return false;

						}

						String optoutKeyword = optoutKeywordTxtBxId.getValue().trim();
						if (optoutKeyword.length() == 0) {
							MessageUtil.setMessage("Please provide opt-out keyword.", "red", "top");
							return false;
						}

						if (minlength != 0 && maxlength != 0) {
							if (optoutKeyword.length() < minlength) {

								MessageUtil.setMessage(
										"Opt-out keyword's length should be of minimum " + minlength + " characters.",
										"color:red;");
								optoutKeywordTxtBxId.setFocus(true);
								return false;

							}

							if (optoutKeyword.length() > maxlength) {

								MessageUtil.setMessage(
										"Opt-out keyword's  length should be of maximum " + maxlength + " characters.",
										"color:red;");
								optoutKeywordTxtBxId.setFocus(true);
								return false;

							}
						} // else if

						if (!Utility.validateBy(keywordPattern, optoutKeyword)) {
							MessageUtil.setMessage(
									"Provide valid opt-out keyword, special characters are not allowed except "
											+ keywordRuleArr[3],
									"color:red;");
							optoutKeywordTxtBxId.setFocus(true);
							return false;

						}

						boolean isExist = false;

						Object optin = smsSettingsDao.findByKeyword(value,
								(String) optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE"));
						if (optin != null) {
							Long orgID = (optin instanceof SMSSettings ? ((SMSSettings) optin).getOrgId().longValue()
									: (optin instanceof OrgSMSkeywords ? ((OrgSMSkeywords) optin).getOrgId().longValue()
											: null));

							if (orgID != null && ((type != null
									&& user.getUserOrganization().getUserOrgId().longValue() != orgID.longValue()))) {
								isExist = true;
							}
						}

						if (isExist) {

							MessageUtil.setMessage(
									"Please provide another opt-in keyword. \n This keyword already exists.",
									"color:red;");
							return false;

						}

						isExist = false;
						Object optout = smsSettingsDao.findByKeyword(optoutKeyword,
								(String) optinKeywordMsdCalNumLBId.getAttribute("SHORTCODE"));
						if (optout != null) {
							Long orgID = (optout instanceof SMSSettings ? ((SMSSettings) optout).getOrgId().longValue()
									: (optout instanceof OrgSMSkeywords
											? ((OrgSMSkeywords) optout).getOrgId().longValue()
											: null));

							if (orgID != null && type != null
									&& user.getUserOrganization().getUserOrgId().longValue() != orgID.longValue()) {
								isExist = true;
							}
						}

						if (isExist) {

							MessageUtil.setMessage(
									"Please provide another opt-out keyword. \n This keyword already exists.",
									"color:red;");
							return false;

						}

						if (value.equalsIgnoreCase(optoutKeyword)) {

							MessageUtil.setMessage(
									" Opt-out and opt-in keywords shouldn't be same. \n This keyword already exists.",
									"color:red;");
							return false;

						}

					}
					if (optInMsgTbId.getText().trim().isEmpty()) {

						MessageUtil.setMessage(
								"Opt-in message cant be left empty. Default opt-in message will be reloaded into the message box",
								"color:red;");
						// MessageUtil.setMessage("Please provide message invite opt-ins",
						// "color:red;");

						String autoOptinMsg = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_SMS_OPTIN_MESSAGE);
						optInMsgTbId.setText(autoOptinMsg);
						getCharCount(autoOptinMsg);

					}
					/*
					 * if(type != null && user.isConsiderSMSSettings() &&
					 * optInMsgTbId.getText().trim().isEmpty()) {
					 * 
					 * 
					 * MessageUtil.
					 * setMessage(" User is enabled with Invite Opt-in program,Please provide opt-in message."
					 * , "color:red;"); return false; }
					 */

				} // opt-in check

			} else if (countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {

				if (USSMSGateWayLbId.getSelectedIndex() == -1
						|| USSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound gateway.", "color:red;");
					return false;
				}

				if (USSMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| USSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound account name.", "color:red;");
					return false;
				}
			} else if (countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {

				if (CANADASMSGateWayLbId.getSelectedIndex() == -1
						|| CANADASMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound gateway.", "color:red;");
					return false;
				}

				if (CANADASMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| CANADASMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound account name.", "color:red;");
					return false;
				}
			} else if (countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {

				if (SASMSGateWayLbId.getSelectedIndex() == -1
						|| SASMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound gateway.", "color:red;");
					return false;
				}

				if (SASMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| SASMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select outbound account name.", "color:red;");
					return false;
				}
			} else if (countryLbId.getSelectedItem().getLabel().trim()
					.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {

				if (pakistanSMSGateWayLbId.getSelectedIndex() == -1
						|| pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select BroadCast gateway.", "color:red;");
					return false;
				}

				if (pakistanSMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| pakistanSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select BroadCast account name.", "color:red;");
					return false;
				}
			} else if (countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
				/*
				 * if( uaeSMSGateWayLbId.getSelectedIndex() == -1 ||
				 * uaeSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()){
				 * 
				 * MessageUtil.setMessage("Please select BroadCast gateway.", "color:red;");
				 * return false; }
				 */

				/*
				 * if( uaeSMSUserNameGateWayLbId.getSelectedIndex() == -1 ||
				 * uaeSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()){
				 * 
				 * MessageUtil.setMessage("Please select BroadCast account name.",
				 * "color:red;"); return false; }
				 */

				if (uaeSMSGateWayLbId.getSelectedIndex() == -1
						|| uaeSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select promotional gateway.", "color:red;");
					return false;
				}

				if (uaeSMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| uaeSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select promotional account name.", "color:red;");
					return false;
				}

				if (uaeTransactionalSMSGateWayChkId.isChecked()) {

					if (uaeTransactionalSMSGateWayLbId.getSelectedIndex() == -1
							|| uaeTransactionalSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select transactional gateway.", "color:red;");
						return false;
					}

					if (uaeTransactionalSMSUserNameGateWayLbId.getSelectedIndex() == -1
							|| uaeTransactionalSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

						MessageUtil.setMessage("Please select transactional account name.", "color:red;");
						return false;
					}

					if (uaeTransSenderIdTxtBxId.getValue().trim().length() == 0) {
						MessageUtil.setMessage("Please provide a valid transactional sender ID.", "red", "top");
						return false;
					}

				} // trans - check

			} else { // for countries other than mentioned ones.

				if (genericSMSGateWayLbId.getSelectedIndex() == -1
						|| genericSMSGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select BroadCast gateway.", "color:red;");
					return false;
				}

				if (genericSMSUserNameGateWayLbId.getSelectedIndex() == -1
						|| genericSMSUserNameGateWayLbId.getSelectedItem().getLabel().trim().isEmpty()) {

					MessageUtil.setMessage("Please select BroadCast account name.", "color:red;");
					return false;
				}

			}

			String maxKeywords = keywordsTxtboxId.getValue().trim();
			if (!maxKeywords.isEmpty() || maxKeywords.length() > 0) {
				/*
				 * MessageUtil.setMessage("Please provide keyword limit", "color:red;"); return;
				 * }else{
				 */
				try {
					Long.parseLong(maxKeywords);
				} catch (NumberFormatException e) {

					MessageUtil.setMessage("Provide valid keyord limit.", "color:red;");
					return false;
				}
			}

			if (toEmailTxtboxId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("Please provide to email address to send keyword's alert mails.", "red", "top");
				return false;
			}

		}

		return true;
	}

	public void defaultSMSSettings(Users user) {
		try {
			if (user != null) {
				if (user.isEnableSMS()) {

					enableSmsChkId.setChecked(true);

					userSmsLimitIntBoxId.setValue(user.getSmsCount());

					// smsSettingsChkId.setChecked(user.isConsiderSMSSettings());

					countryMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_INDIA);
					countryMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_US);
					countryMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_COUNTRY_CANADA);
					countryMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_COUNTRY_SA);
					countryMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_COUNTRY_PAKISTAN);
					countryMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_COUNTRY_UAE);

					Enumeration enm = countryCodes.propertyNames();
					while (enm.hasMoreElements()) {
						String currCountry = (String) enm.nextElement();
						countryMap.put(currCountry, currCountry);
					}

					/*
					 * countryCodeTxtId.setValue("+"+user.getCountryCarrier()); if(countryStr !=
					 * null && !countryStr.isEmpty())countryLbId.setDisabled(true);
					 */

					String countryStr = user.getCountryType();

					if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {

						indiaDivId.setVisible(true);
						usSMSDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(false);
						canadaSMSDivId.setVisible(false);
						saSMSDivId.setVisible(false);
					} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {

						usSMSDivId.setVisible(true);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(false);
						canadaSMSDivId.setVisible(false);
						saSMSDivId.setVisible(false);
					} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {

						canadaSMSDivId.setVisible(true);
						usSMSDivId.setVisible(false);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(false);
					} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_SA)) {

						saSMSDivId.setVisible(true);
						canadaSMSDivId.setVisible(false);
						usSMSDivId.setVisible(false);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(false);
					} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_PAKISTAN)) {

						usSMSDivId.setVisible(false);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(true);
						uaeSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(false);
						canadaSMSDivId.setVisible(false);
						saSMSDivId.setVisible(false);
					} else if (countryStr.equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {

						usSMSDivId.setVisible(false);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(true);
						genericSMSDivId.setVisible(false);
						canadaSMSDivId.setVisible(false);
						saSMSDivId.setVisible(false);
					} else {// if other than mentioned countries

						usSMSDivId.setVisible(false);
						indiaDivId.setVisible(false);
						pakistanSMSDivId.setVisible(false);
						uaeSMSDivId.setVisible(false);
						canadaSMSDivId.setVisible(false);
						genericSMSDivId.setVisible(true);
						saSMSDivId.setVisible(false);

					}

					UserOrganization userOrg = user.getUserOrganization();

					keywordsTxtboxId.setValue("" + userOrg.getMaxKeywords());
					if (userOrg.getToEmailId() == null || userOrg.getToEmailId().isEmpty()) {

						toEmailTxtboxId.setValue(user.getEmailId());
					} else {

						toEmailTxtboxId.setValue(userOrg.getToEmailId());
					}

					// toEmailTxtboxId.setValue(userOrg.getToEmailId());
					// enableAlertsChkId.setChecked(userOrg.isEnableAlerts());
					// onCheck$enableAlertsChkId();
					/*
					 * String receivingNumbers = userOrg.getMsgReceivingNumbers();
					 * if(receivingNumbers != null) { Components.removeAllChildren(msgRecvNumCmbId);
					 * String[] rcvdNumberArr =
					 * receivingNumbers.split(Constants.ADDR_COL_DELIMETER); for (String number:
					 * rcvdNumberArr) { Comboitem item = new Comboitem(number.length() >10 ?
					 * "+"+number : number); item.setValue(number); item.setParent(msgRecvNumCmbId);
					 * }
					 * 
					 * if(msgRecvNumCmbId.getItemCount() > 0) msgRecvNumCmbId.setSelectedIndex(0);
					 * }//if
					 */

					onCheck$enableSmsChkId();

					if (user.getOptinRoute() != null)
						optinSMSRouteChkId.setChecked(true);
					if (user.getUserOrganization().isRequireMobileValidation())
						mobileValidationsChkId.setChecked(true);

				} else {

					if (user.getUserOrganization().getToEmailId() == null
							|| user.getUserOrganization().getToEmailId().isEmpty()) {

						toEmailTxtboxId.setValue(user.getEmailId());
					}
					if (user.getUserOrganization().isRequireMobileValidation())
						mobileValidationsChkId.setChecked(true);

				}

			} // if
		} catch (Exception e) {

			logger.error("Exception ", e);
		}
	}// defaultSMSSettings
	/*
	 * public void onClick$addNumberTbId(){
	 * 
	 * //Users userObj =(Users)session.getAttribute("USEROBJ"); String recvNumber =
	 * msgRecvNumCmbId.getValue().trim();
	 * 
	 * if( recvNumber == null || recvNumber.trim().length() == 0) {
	 * 
	 * MessageUtil.setMessage("Please enter your number.", "color:red;"); return;
	 * 
	 * }//if
	 * 
	 * try { Long.parseLong(recvNumber); } catch (NumberFormatException e) {
	 * 
	 * MessageUtil.setMessage("Please enter valid number.", "color:red;"); return;
	 * 
	 * }
	 * 
	 * Comboitem addItem = findOptionalComboitem(msgRecvNumCmbId, recvNumber);
	 * if(addItem != null) {
	 * 
	 * MessageUtil.setMessage("Number already exist, please enter another number.",
	 * "color:red;"); return;
	 * 
	 * } else{ boolean available = false; if(userObj != null){
	 * 
	 * available = usersDao.findReceivingNumByUserId(user,recvNumber); }
	 * logger.info("availible ====>"+available); if(available == true) {
	 * 
	 * MessageUtil.setMessage("Number already exist for this user.", "color:red;");
	 * return; } addItem = new Comboitem(recvNumber);
	 * addItem.setParent(msgRecvNumCmbId);
	 * 
	 * } if(recvNumber.length() == 10 &&
	 * !recvNumber.startsWith(user.getCountryCarrier().toString())) { recvNumber =
	 * user.getCountryCarrier().toString()+recvNumber; } addItem = new
	 * Comboitem(recvNumber.length() > 10 ? "+"+recvNumber : recvNumber);
	 * addItem.setValue(recvNumber); addItem.setParent(msgRecvNumCmbId);
	 * 
	 * msgRecvNumCmbId.setText(Constants.STRING_NILL);
	 * 
	 * 
	 * } private Comboitem findOptionalComboitem(Combobox cb, String label) { try {
	 * List<Comboitem> items = cb.getItems(); for (Comboitem cbitem : items) {
	 * String number = cbitem.getValue().toString(); if(number.endsWith(label) ||
	 * label.endsWith(number)) return cbitem; } // for
	 * 
	 * return null; } catch (Exception e) { logger.error("Exception",e);
	 * //logger.error("Exception :::",e); return null; } }
	 */

	public MailingList createOptinMailinList(String listName, Users userObj) {
		try {
			MailingList mailingList = null;

			listName = Utility.condense(listName);
			boolean isExists = isMLExist(listName, userObj);
			if (isExists) {
				MessageUtil.setMessage("Name already exists.", "color:red", "TOP");
				return null;
			}

			Calendar cal = MyCalendar.getNewCalendar();

			MailingListDao mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
			MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) SpringUtil
					.getBean("mailingListDaoFOrDML");
			Long mlbit = mailingListDao.getNextAvailableMbit(userObj.getUserId());

			if (mlbit == 0l) {
				MessageUtil.setMessage("You have exceeded limit on maximum number of lists(60). "
						+ "Please delete one or more lists to create a new list.", "red");
				return null;
			}

			mailingList = new MailingList(listName, "Mobile contacts optin list", cal, "Active", cal, cal, false, false,
					null, userObj, false, null, false, null);

			/*
			 * mailingList = new MailingList(listName, newMlDescTbId.getValue(),cal,
			 * "Active", cal,cal,false, false,null,currentUser, optin, optInCustTemplateId,
			 * consent, consentCustTemplateId);
			 */

			mailingList.setMlBit(mlbit);
			mailingList.setListType(Constants.MAILINGLIST_TYPE_OPTIN_LIST);
			mailingListDaoForDML.saveOrUpdate(mailingList);

			return mailingList;
		} catch (Exception e) {
			logger.error("exception", e);
			// logger.error("Exception :::",e);
			return null;
		}
	}

	public boolean isMLExist(String listName, Users userObj) {
		try {
			MailingListDao mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
			MailingList mailingList = mailingListDao.findByListName(listName, userObj.getUserId());
			if (mailingList == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			logger.error(" ** Exception :" + e + " **");
			return false;
		}
	}

	/*
	 * public void onClick$KeywordDelTBId() {
	 * 
	 * String recvNumber = msgRecvNumCmbId.getValue().trim();
	 * 
	 * if( recvNumber != null || recvNumber.trim().length() > 0) {
	 * 
	 * Comboitem delItem = findOptionalComboitem(msgRecvNumCmbId, recvNumber);
	 * if(delItem!=null) {
	 * 
	 * if(delItem.getIndex() == 0){
	 * 
	 * MessageUtil.setMessage("Please select the number to be deleted.",
	 * "color:red;"); return;
	 * 
	 * 
	 * }//if
	 * 
	 * if(delItem.getValue() != null ){
	 * 
	 * try { int confirm =
	 * Messagebox.show("Are you sure you want to delete the selected number?"
	 * ,"Delete Keyword?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
	 * if(confirm != 1) {
	 * 
	 * return ; } } catch (Exception e) { // TODO Auto-generated catch block
	 * logger.error("Exception",e); }
	 * 
	 * 
	 * }
	 * 
	 * msgRecvNumCmbId.removeChild(delItem);
	 * msgRecvNumCmbId.setValue(Constants.STRING_NILL); }
	 * 
	 * }else{
	 * 
	 * MessageUtil.setMessage("Please select the number to be deleted.",
	 * "color:red;"); return;
	 * 
	 * 
	 * }//else
	 * 
	 * }//onClick$KeywordDelTBId
	 */
	public void getCharCount(String msgContent) {
		try {
			int charCount = msgContent.length();
			// logger.info("the length is====>"+charCount);

			if (charCount > 160) {
				// warnLblId.setVisible(true);
				int msgcount = charCount / 160;
				charCountTbId.setValue("" + charCount + "/" + (msgcount + 1));
				/*
				 * charCountTbId.setValue(""+(smsCampaign.getMessageContent().
				 * substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));
				 */
			} // if
			else {
				// warnLblId.setVisible(false);
				charCountTbId.setValue("" + charCount + " / " + 1);
			} // else
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * updates the current cursor position
	 */

	public void onChange$caretPosTB() {
		logger.debug("---just entered----");
	}

	public void onBlur$caretPosTB() {

		logger.debug("-----just entered: onBlur event----");

	}

	/**
	 * gives the char count of SMS
	 * 
	 * @param event
	 */
	public void onChanging$optInMsgTbId(InputEvent event) {
		try {
			getCharCount(event.getValue());

		} catch (Exception e) {
			logger.debug("Exception **", e);
		}
	}// onChanging$SMSMsgTbId(-)

	// added for onBlur events

	public void onBlur$optinKeywordTxtBxId() {

		String optinKeywordStr = optinKeywordTxtBxId.getText().trim().isEmpty() ? Constants.STRING_NILL
				: optinKeywordTxtBxId.getText().trim();

		if (!optinKeywordStr.isEmpty() || optinKeywordStr != null)
			optinKeywordTxtBxId.setText(optinKeywordStr.toUpperCase());

	}

	public void onBlur$optoutKeywordTxtBxId() {

		String optoutKeywordStr = optoutKeywordTxtBxId.getText().trim().isEmpty() ? Constants.STRING_NILL
				: optoutKeywordTxtBxId.getText().trim();

		if (!optoutKeywordStr.isEmpty() || optoutKeywordStr != null)
			optoutKeywordTxtBxId.setText(optoutKeywordStr.toUpperCase());

	}

	private void populateVmtaAccountNameCbId() {

		vmtaAccountNameCbId.getChildren().clear();

		Comboitem ci = null;
		ci = new Comboitem("Select Account");
		ci.setParent(vmtaAccountNameCbId);
		vmtaAccountNameCbId.setSelectedItem(ci);

		Vmta selectedVmtaObject = (Vmta) (vmtaCbId.getSelectedItem().getAttribute("aVmtaObject"));

		List<Vmta> accountListForOneVmta = vmtaDao.findAllVmtaByVmtaName(selectedVmtaObject.getVmtaName());
		for (Vmta vmta : accountListForOneVmta) {
			ci = new Comboitem(vmta.getAccountName());
			ci.setAttribute("aVmtaObject", vmta);
			ci.setParent(vmtaAccountNameCbId);
		}
	}

	public void onSelect$pakistanSMSUserNameGateWayLbId() {

		try {
			String gatwayStr = pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) pakistanSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_PROMOTIONAL);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$uaeSMSUserNameGateWayLbId() {

		try {
			String gatwayStr = uaeSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) uaeSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_PROMOTIONAL);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}

	public void onSelect$USSMSUserNameGateWayLbId() {

		try {
			String gatwayStr = USSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) USSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}
	
	public void onSelect$CANADASMSUserNameGateWayLbId() {

		try {
			String gatwayStr = CANADASMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) CANADASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}//onSelect$CANADASMSUserNameGateWayLbId
	
	public void onSelect$SASMSUserNameGateWayLbId() {

		try {
			String gatwayStr = SASMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) SASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_OUTBOUND);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}//onSelect$SASMSUserNameGateWayLbId

	public void onSelect$promotionalSMSUserNameGateWayLbId() {

		try {
			String gatwayStr = promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) promotionalSMSUserNameGateWayLbId.getSelectedItem().getValue())
					.getUserId();
			getSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_PROMOTIONAL);
		} catch (Exception e) {

			logger.error("Exception", e);
			// logger.error("Exception :::",e);
		}

	}
	//app-3701
	public void onSelect$transactionalSMSUserNameGateWayLbId() {
		logger.info("......onSelect$transactionalSMSUserNameGateWayLbId.......");
		try {
			String gatwayStr = transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
			String accName = ((OCSMSGateway) transactionalSMSUserNameGateWayLbId.getSelectedItem().getValue())
					.getUserId();
			getTRSenderIdsModel(gatwayStr, accName, Constants.SMS_TYPE_TRANSACTIONAL);
		} catch (Exception e) {

			logger.error("Exception", e);
		}

	}//onSelect$transactionalSMSUserNameGateWayLbId

	public void defaultSelectedSenderIds() {

		String gatewayName = Constants.STRING_NILL;
		String accountName = Constants.STRING_NILL;
		String accType = Constants.STRING_NILL;

		if (enableSmsChkId.isChecked()) {
			/*
			 * if(indiaDivId.isVisible()){
			 * gatewayName=promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
			 * accountName=((OCSMSGateway)promotionalSMSUserNameGateWayLbId.getSelectedItem(
			 * ).getValue()).getUserId(); accType=Constants.SMS_TYPE_PROMOTIONAL; }
			 */
			if (usSMSDivId.isVisible()) {
				gatewayName = USSMSGateWayLbId.getSelectedItem().getLabel().trim();
				accountName = ((OCSMSGateway) USSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				accType = Constants.SMS_TYPE_OUTBOUND;
			}
			if (canadaSMSDivId.isVisible()) {
				gatewayName = CANADASMSGateWayLbId.getSelectedItem().getLabel().trim();
				accountName = ((OCSMSGateway) CANADASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				accType = Constants.SMS_TYPE_OUTBOUND;
			}
			if (saSMSDivId.isVisible()) {
				gatewayName = SASMSGateWayLbId.getSelectedItem().getLabel().trim();
				accountName = ((OCSMSGateway) SASMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				accType = Constants.SMS_TYPE_OUTBOUND;
			}
			if (pakistanSMSDivId.isVisible()) {
				gatewayName = pakistanSMSGateWayLbId.getSelectedItem().getLabel().trim();
				accountName = ((OCSMSGateway) pakistanSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				accType = Constants.SMS_TYPE_PROMOTIONAL;
			}
			if (indiaDivId.isVisible()) {
				gatewayName = promotionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
				accountName = ((OCSMSGateway) promotionalSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				accType = Constants.SMS_TYPE_PROMOTIONAL;
			}//APP-3965
			/*
			 * if(uaeSMSDivId.isVisible()){
			 * gatewayName=uaeSMSGateWayLbId.getSelectedItem().getLabel().trim();
			 * accountName=((OCSMSGateway)uaeSMSUserNameGateWayLbId.getSelectedItem().
			 * getValue()).getUserId(); accType=Constants.SMS_TYPE_PROMOTIONAL; }
			 */
			getSenderIdsModel(gatewayName, accountName, accType);
			//app-3701
			if (indiaDivId.isVisible() && transactionalSMSGateWayChkId.isChecked()) {
				logger.info("transactionalSMSGateWayChkId.isChecked() >> true");
				String trgatewayName = Constants.STRING_NILL;
				String traccountName = Constants.STRING_NILL;
				String traccType = Constants.STRING_NILL;
				trgatewayName = transactionalSMSGateWayLbId.getSelectedItem().getLabel().trim();
				traccountName = ((OCSMSGateway) transactionalSMSUserNameGateWayLbId.getSelectedItem().getValue()).getUserId();
				traccType = Constants.SMS_TYPE_TRANSACTIONAL;
				getTRSenderIdsModel(trgatewayName, traccountName, traccType);
			}

		}
	}

}
