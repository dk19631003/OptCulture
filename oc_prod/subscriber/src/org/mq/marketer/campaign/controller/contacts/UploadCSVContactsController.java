package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.POSMappingDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

public class UploadCSVContactsController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	POSMappingDao posMappingDao = null;
	POSMappingDaoForDML posMappingDaoForDML = null;
	ContactsDao contactsDao = null;
	CustomTemplatesDao customTemplatesDao;
	EmailQueueDao emailQueueDao;
	Contacts contacts;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private MessagesDao messagesDao;
	private static String SELECT_STRING = "--Select --";
	private static String contctGenFieldStr = PropertyUtil.getPropertyValue("defaultContactMapFieldList");
	private static String contctGenFieldStrLoyalty = PropertyUtil.getPropertyValue("defaultLoyaltyMapFieldList");
	private static String[] defaultFieldArray = StringUtils.split(contctGenFieldStr, ',');
	private static String[] defaultFieldArrayLoyalty = StringUtils.split(contctGenFieldStrLoyalty, ',');
	private static String ANNIVERSARY = "Anniversary";
	private static String BIRTHDAY = "BirthDay";
	private static String ZIP = "ZIP";
	private static String MOBILE = "Mobile";
	private static String ERROR_STYLE = "border:1px solid #DD7870;";
	private static String NORMAL_STYLE = "border:1px solid #7F9DB9;";

	Rows contactRowsId;
	Div listMappingDivId, fileUploadDivId, uploadResultDivId;
	private boolean isAdmin;
	private static String[] udfSetStr = { "UDF1", "UDF2", "UDF3", "UDF4", "UDF5", "UDF6", "UDF7", "UDF8", "UDF9",
			"UDF10", "UDF11", "UDF12", "UDF13", "UDF14", "UDF15" };

	private int mobileContacts = 0;
	private String noSMSComplaincyMsg = null;
	private String noSMSCredits = null;
	public Button uploadBtnId,uploadBtnId1;

	// private static Map<String, String> genFieldContMap = new HashMap<String,
	// String>();
	private static Map<String, String> optFiledDataType = new HashMap<String, String>();
	// private static Map<String, String> contactFieldMap = new HashMap<String,
	// String>();

	static {
		/*
		 * genFieldContMap.put("Email" , "Email"); genFieldContMap.put("FirstName" ,
		 * "First Name"); genFieldContMap.put("LastName" , "Last Name");
		 * genFieldContMap.put("Street" , "Street"); genFieldContMap.put("City" ,
		 * "City"); genFieldContMap.put("State" , "State");
		 * genFieldContMap.put("Country" , "Country"); genFieldContMap.put("ZIP" ,
		 * "ZIP"); genFieldContMap.put("MobilePhone" , "Mobile");
		 * genFieldContMap.put("CustomerID" , "Customer ID" );
		 * genFieldContMap.put("Gender" , "Gender"); genFieldContMap.put("HomeStore" ,
		 * "Home Store"); genFieldContMap.put("BirthDay" , "BirthDay");
		 * genFieldContMap.put("Anniversary" , "Anniversary");
		 */

		// String","Date","Number","Double","Boolean"
		optFiledDataType.put("Email", "String");
		optFiledDataType.put("First Name", "String");
		optFiledDataType.put("Last Name", "String");
		optFiledDataType.put("Street", "String");
		optFiledDataType.put("City", "String");
		optFiledDataType.put("State", "String");
		optFiledDataType.put("Country", "String");
		optFiledDataType.put("ZIP", "String");
		optFiledDataType.put("Mobile", "String");
		optFiledDataType.put("Customer ID", "String");
		optFiledDataType.put("Gender", "String");
		optFiledDataType.put("Home Store", "String");
		optFiledDataType.put("BirthDay", "Date");
		optFiledDataType.put("Anniversary", "Date");
		optFiledDataType.put("Created Date", "Date");
		optFiledDataType.put("Subsidiary Number", "String");

		/*
		 * contactFieldMap.put("Email", "emailId"); contactFieldMap.put("First Name",
		 * "firstName"); contactFieldMap.put("Last Name", "lastName");
		 * contactFieldMap.put("Street", "addressOne");
		 * contactFieldMap.put("Address Two", "addressTwo"); contactFieldMap.put("City",
		 * "city"); contactFieldMap.put("State", "state");
		 * contactFieldMap.put("Country", "country"); contactFieldMap.put("ZIP", "zip");
		 * contactFieldMap.put("Mobile", "mobilePhone");
		 * contactFieldMap.put("Customer ID", "externalId" );
		 * contactFieldMap.put("Addressunit ID", "hpId" ); contactFieldMap.put("Gender",
		 * "gender"); contactFieldMap.put("BirthDay", "birthDay");
		 * contactFieldMap.put("Anniversary", "anniversary");
		 * contactFieldMap.put("Home Store", "homeStore");
		 */

	}

	private static final String csvDelemiterStr = "(?:\"\\s*,\\s*\"|^\\s*\"|\\s*\"\\s*$)";
	Session session = null;
	private Set<MailingList> mailingListSet = null;
	Users currentUser = GetUser.getUserObj();
	private Label selectedListsLblId;
	private Textbox selectedFileTbId;
	private String isNew = null;
	//private boolean loyaltyFlag = false;
	/*
	 * private SMSSettingsDao smsSettingsDao; private SMSSettings smsSettings ;
	 */
	// private ClickaTellApi clickaTellApi;
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private UsersDao usersDao;

	public UploadCSVContactsController() {
		session = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Add / Import Contacts", "", style, true);
		listIdsSet = (Set<Long>) session.getAttribute(Constants.LISTIDS_SET);
	}

	private Set<Long> listIdsSet;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML) SpringUtil.getBean("mailingListDaoForDML");
		messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		/*
		 * smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		 * smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(),
		 * OCConstants.);
		 */
		// clickaTellApi = (ClickaTellApi)SpringUtil.getBean("clickaTellApi");
		captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway) SpringUtil.getBean("captiwayToSMSApiGateway");
		mailingListSet = (Set<MailingList>) session.getAttribute("uploadFile_Ml");

		isAdmin = (Boolean) session.getAttribute("isAdmin");
		String listNmaes = "";

		isNew = (String) session.getAttribute("isNewML");
		// save new Mailing List
		if (new Boolean(isNew)) {
			MailingList mailingList = (MailingList) mailingListSet.iterator().next();
			try {

				mailingListDaoForDML.saveOrUpdate(mailingList);
				listIdsSet.add(mailingList.getListId());
				listIdsSet = (Set<Long>) session.setAttribute(Constants.LISTIDS_SET, listIdsSet);

				listNmaes = mailingList.getListName();

			} catch (Exception e) {
				logger.error(" ** Exception :" + e + " **");
				String message = "list upload is failed, mailing list can not be created with the name :"
						+ mailingList.getListName();
				(new MessageHandler(messagesDao, currentUser.getUserName())).sendMessage("Contact", "Uploaded failed",
						message, "Inbox", false, "INFO", currentUser);
				return;
			}
		} else {

			for (MailingList ml : mailingListSet) {

				if (listNmaes.length() > 0)
					listNmaes += ", ";

				listNmaes += ml.getListName();

			}
		}

		// set selected Mailing lists names
		selectedListsLblId.setValue(listNmaes);

		posMappingDao = (POSMappingDao) SpringUtil.getBean("posMappingDao");
		posMappingDaoForDML = (POSMappingDaoForDML) SpringUtil.getBean("posMappingDaoForDML");
		contactsDao = (ContactsDao) SpringUtil.getBean("contactsDao");
		customTemplatesDao = (CustomTemplatesDao) SpringUtil.getBean("customTemplatesDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		
		
		//uploadBtnId.setUpload("true");
		//uploadBtnId.setImage("url(../images/button_green_idle.jpg) repeat-x scroll 0 0 transparent !important");

		// Changes 2.5.4.0
		//String loyaltyServicetype = GetUser.getUserObj().getloyaltyServicetype();
		//loyaltyFlag = loyaltyServicetype != null && !loyaltyServicetype.isEmpty();
		//logger.info(loyaltyFlag);
		//logger.info("GetUser.getUserObj(posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_LOYALTY + "'", currentUser.getUserId())).getloyaltyServicetype()===>"+GetUser.getUserObj().getloyaltyServicetype());

	} // doAfterCompose

	Map<String, POSMapping> existOptCulFiledMap = null;

	private void defaultUserPOSMapSettings(String filePath) {

		List<POSMapping> contMapList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
				currentUser.getUserId());
				contMapList.addAll(	posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_LOYALTY + "'", currentUser.getUserId()));

		if (contMapList == null || contMapList.size() == 0) {
			MessageUtil.setMessage("User has no list settings.", "color:RED", "TOP");
			return;
		}

		Components.removeAllChildren(contactRowsId);

		BufferedReader br;
		String lineStr;
		List<String> fileHeaderList = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(filePath);
			br = new BufferedReader(fileReader);
			// String csvColumnStr = "";
			lineStr = null;
			String[] lineStrTokens;

			// Getting the Headers
			while ((lineStr = br.readLine()) != null) {
				// lineStr += ",\"0\"";
				if (lineStr.trim().length() == 0)
					continue;

				lineStrTokens = parse(lineStr);// lineStr.split(csvDelemiterStr);
				logger.debug("lineStrTokens>>>>" + lineStrTokens);

				if (lineStrTokens.length == 0) {
					continue;
				}

				for (int j = 0; j < lineStrTokens.length; j++) {
					fileHeaderList.add(lineStrTokens[j].trim());
					// csvColumnStr = lineStrTokens[j].trim();

				}
				contacts.setModifiedDate(Calendar.getInstance());
				break;
			} // while

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

		/*
		 * if(contMapList == null || contMapList.size() ==0) { //TODO for Generate
		 * default mapping defaultGenFieldMapp(); return; }
		 */

		// existOptCulFiledLst = new ArrayList<String>();
		if (fileHeaderList == null || fileHeaderList.size() == 0) {
			MessageUtil.setMessage("File does not have the header field set.", "color:RED", "TOP");
			return;
		}

		if (contMapList != null && contMapList.size() > 0) {
			existOptCulFiledMap = new HashMap<String, POSMapping>();

			for (POSMapping posMapping : contMapList) {

				existOptCulFiledMap.put(posMapping.getCustomFieldName(), posMapping);

			}
		}

		for (String eachStr : fileHeaderList) {
			// logger.debug("Eachstr in a File is ::"+eachStr);
			// Row row = new Row();
			// boolean flag = false;
			POSMapping posMapObj = getPosObject(contMapList, eachStr);
			logger.info("eachStr===>" + eachStr);
			logger.info("posMapObj===>" + posMapObj);
			defaultGenFieldMapp(posMapObj, eachStr);
		}
	} // defaultUserPOSMapSettings

	private POSMapping getPosObject(List<POSMapping> contMapList, String eachStr) {

		POSMapping posMapObj = null;
		for (POSMapping posMapping : contMapList) {

			if (posMapping.getPosAttribute().equals(eachStr)) {

				posMapObj = posMapping;

			}

		}
		return posMapObj;

	}

	private void defaultGenFieldMapp(POSMapping posMapObj, String fileHeader) {

		// Set<String> set = genFieldContMap.keySet();
		Row row = null;
		// for (String keyStr : set) {

		// String optCulFiled = genFieldContMap.get(keyStr);
		row = new Row();

		// CSV Colomn Label
		Div csvDiv = new Div();
		csvDiv.setParent(row);

		Textbox posAttrTextBx = new Textbox();
		posAttrTextBx.setValue(posMapObj != null ? posMapObj.getPosAttribute() : fileHeader);
		posAttrTextBx.setDisabled(true);
		posAttrTextBx.setParent(csvDiv);

		Image csvDelValImg = new Image();
		csvDelValImg.setSrc("/img/action_delete.gif");
		csvDelValImg.setStyle("cursor:pointer;");
		csvDelValImg.setStyle("cursor:pointer;margin:0 10px 0 15px;");
		csvDelValImg.addEventListener("onClick", this);
		csvDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		csvDelValImg.setParent(csvDiv);

		csvDelValImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
		row.setParent(contactRowsId);

		// Custom Field
		Div ocAttrDiv = new Div();
		// ocAttrDiv.setParent(row);

		Listbox optFiledLstBx = null;

		optFiledLstBx = createContactPosMappingListbox();
		optFiledLstBx.setParent(ocAttrDiv);
		optFiledLstBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
		optFiledLstBx.addEventListener("onSelect", this);

		List posMappingChilItemList = optFiledLstBx.getChildren();
		Listitem tempItem = null;

		if (posMapObj != null) {

			for (Object object : posMappingChilItemList) {
				tempItem = (Listitem) object;
				if (tempItem.getLabel().equals(posMapObj.getCustomFieldName())||
						tempItem.getLabel().equals(posMapObj.getDisplayLabel())) {
					logger.info("tempItem==>"+tempItem);
					optFiledLstBx.setSelectedItem(tempItem);

				}
			}
		} else {
			optFiledLstBx.setSelectedIndex(32);
		}
		optFiledLstBx.setParent(ocAttrDiv);

		// ocAttribute label

		Label ocAttrLabelId = new Label();
		ocAttrLabelId.setAttribute("CUST_FIELD_LABEL", "CUST_FIELD_LABEL");
		ocAttrLabelId.setStyle("color:blue");
		logger.info("ocAttrLabelId==>" + ocAttrLabelId);
		if (posMapObj != null) {
			logger.info("posMapObj==>" + posMapObj);
			logger.info("posMapObj==>" + posMapObj);
			for (Object object : posMappingChilItemList) {
				tempItem = (Listitem) object;
				logger.info("optFiledLstBx.getSelectedItem()==>"+optFiledLstBx.getSelectedItem());
				logger.info("optFiledLstBx.getSelectedItem().getLabel()==>"+optFiledLstBx.getSelectedItem().getLabel());
				logger.info("posMapObj.getCustomFieldName==>"+posMapObj.getCustomFieldName());
				if (optFiledLstBx.getSelectedItem().getLabel().equals(posMapObj.getCustomFieldName())||
						optFiledLstBx.getSelectedItem().getLabel().equals(posMapObj.getDisplayLabel())) {

					ocAttrLabelId.setValue("Used Field");
				} else {

					ocAttrLabelId.setValue("Unused Field");
				}
			}
		}

		ocAttrLabelId.setVisible(true);
		ocAttrLabelId.setParent(ocAttrDiv);

		ocAttrDiv.setParent(row);

		/*
		 * List optMapChildItemList = optFiledLstBx.getChildren(); Listitem tempItem=
		 * null;
		 * 
		 * for (Object object : optMapChildItemList) { tempItem = (Listitem)object;
		 * if(tempItem.getLabel().equals(optCulFiled)){
		 * optFiledLstBx.setSelectedItem(tempItem); } }
		 */

		// Display Label
		Textbox dispLabelTextBx = new Textbox();
		dispLabelTextBx.setValue(posMapObj != null ? posMapObj.getDisplayLabel() : "");
		if (posMapObj != null)
			dispLabelTextBx.setDisabled(true);
		dispLabelTextBx.setParent(row);

		// Data Type
		// String dataTypeStr = optFiledDataType.get(optCulFiled);
		String dataTypeStr = "";
		if (posMapObj != null) {
			dataTypeStr = posMapObj.getDataType();

		} else {
			dataTypeStr = optFiledDataType.containsKey(fileHeader) == true ? optFiledDataType.get(fileHeader) : "";

		}

		// logger.info("datatype ::::"+dataTypeStr);

		Div dataTypeDiv = new Div();

		Listbox dataTypelb = createDataTypeListBox();
		dataTypelb.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
		dataTypelb.addEventListener("onSelect", this);
		dataTypelb.setParent(dataTypeDiv);

		Listbox dateFormatListBx = createDateFormatListbox();
		dateFormatListBx.setSelectedIndex(0);
		dateFormatListBx.setVisible(false);
		dateFormatListBx.setParent(dataTypeDiv);
		List ChildItemList = dataTypelb.getChildren();

		/*
		 * for (Object object : ChildItemList) {
		 * 
		 * Listitem listItem = (Listitem)object; //
		 * logger.info("listItem.getLabel() ::::"+listItem.getLabel());
		 * 
		 * if(listItem.getLabel().toString().equals(dataTypeStr)) {
		 * dataTypelb.setSelectedItem(listItem); } } // for
		 */

		if (dataTypeStr.length() > 0) {
			for (Object object : ChildItemList) {

				Listitem listItem = (Listitem) object;

				if (dataTypeStr.startsWith("Date") && listItem.getLabel().toString().equals("Date")
						&& dataTypeStr.trim().length() > "Date".length()) {
					dataTypelb.setSelectedItem(listItem);
					dataTypelb.setDisabled(true);

					dateFormatListBx.setVisible(true);

					List dateFormatList = dateFormatListBx.getChildren();
					dataTypeStr = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1, dataTypeStr.indexOf(")"));

					for (Object obj : dateFormatList) {

						Listitem tempListItem = (Listitem) obj;
						if (tempListItem.getLabel().equals(dataTypeStr)) {
							dateFormatListBx.setSelectedItem(tempListItem);
							dateFormatListBx.setDisabled(true);
							break;
						}
					}

				} else if (listItem.getLabel().equals(dataTypeStr) && optFiledLstBx.getSelectedIndex() != 32) {
					dataTypelb.setSelectedItem(listItem);
					dataTypelb.setDisabled(true);
					break;
				}

			} // for

		}
		if (dataTypelb.getSelectedIndex() == -1)
			dataTypelb.setSelectedIndex(0);

		/*
		 * if(dataTypeStr.equals("Date")) { dateFormatListBx.setVisible(true); }else {
		 * dateFormatListBx.setVisible(false); }
		 */

		// dataTypelb.addEventListener("onSelect", this);
		dataTypeDiv.setParent(row);

		// Optional Value
		/*
		 * Div optDiv=new Div(); optDiv.setParent(row);
		 * 
		 * Combobox cb = new Combobox(); cb.setSclass("cb_100w"); cb.setParent(optDiv);
		 * 
		 * if(posMapObj != null && posMapObj.getOptionalValues()!=null) { String
		 * optValues[] =
		 * posMapObj.getOptionalValues().split(Constants.ADDR_COL_DELIMETER); for
		 * (String optVal : optValues) { cb.appendItem(optVal); } }
		 * 
		 * 
		 * 
		 * 
		 * //Optional Value Add Action Image optAddValImg = new Image();
		 * optAddValImg.setSrc("/img/action_add.jpg");
		 * optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		 * optAddValImg.addEventListener("onClick", this);
		 * optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
		 * optAddValImg.setParent(optDiv);
		 * 
		 * //Optional Value Add Action Image optDelValImg = new Image();
		 * optDelValImg.setSrc("/img/action_delete.gif");
		 * optDelValImg.setStyle("cursor:pointer;");
		 * optDelValImg.addEventListener("onClick", this);
		 * optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		 * optDelValImg.setParent(optDiv);
		 */

		// Display Label
		/*
		 * Textbox dispLabelTextBx = new Textbox(); dispLabelTextBx.setValue(posMapObj
		 * != null ?posMapObj.getDisplayLabel() : "");
		 * dispLabelTextBx.setDisabled(true); dispLabelTextBx.setParent(row);
		 */

		// Unique priority
		Intbox priIntBx = new Intbox();
		priIntBx.setMaxlength(1);
		priIntBx.setWidth("30px");
		if (posMapObj != null) {
			priIntBx.setValue(posMapObj.getUniquePriority());
		}
		if (isAdmin) {
			priIntBx.setDisabled(false);
		} else {
			priIntBx.setDisabled(true);
		}

		/*
		 * else {
		 * 
		 * if(optCulFiled.equals("RetailPro ID")) priIntBx.setValue(1); else
		 * if(optCulFiled.equals("Email")) priIntBx.setValue(2); else
		 * if(optCulFiled.equals("Mobile")) priIntBx.setValue(3); }
		 */

		/*
		 * priIntBx.setDisabled(true); if(isAdmin) { priIntBx.setDisabled(false); }
		 */
		priIntBx.setParent(row);

		Div div = new Div();
		div.setParent(row);

		// Delete Action
		/*
		 * Image delImg = new Image(); delImg.setSrc("/images/action_delete.gif");
		 * delImg.setStyle("cursor:pointer;"); delImg.addEventListener("onClick", this);
		 * delImg.setParent(div);
		 * 
		 * delImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
		 * row.setParent(contactRowsId);
		 */

		// } // for

	} // defaultGenFieldMapp

	// posMapBackBtnId,saveContinueBtnId
	public void onClick$addContactMapBtnId() {

		Row tempRow = new Row();

		// CSV Colomn Label

		Div csvDiv = new Div();
		csvDiv.setParent(tempRow);

		Textbox posAttrTextBx = new Textbox();

		posAttrTextBx.setParent(csvDiv);

		Image csvDelValImg = new Image();
		csvDelValImg.setSrc("/img/action_delete.gif");
		csvDelValImg.setStyle("cursor:pointer;");
		csvDelValImg.setStyle("cursor:pointer;margin:0 10px 0 15px;");
		csvDelValImg.addEventListener("onClick", this);
		csvDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		csvDelValImg.setParent(csvDiv);

		csvDelValImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
		tempRow.setParent(contactRowsId);

		// Custom Field
		Div ocAttrDiv = new Div();
		// ocAttrDiv.setParent(row);

		Listbox optFiledLstBx = null;

		optFiledLstBx = createContactPosMappingListbox();
		optFiledLstBx.setParent(ocAttrDiv);
		optFiledLstBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
		optFiledLstBx.addEventListener("onSelect", this);

		optFiledLstBx.setParent(ocAttrDiv);

		// ocAttribute label

		Label ocAttrLabelId = new Label();
		ocAttrLabelId.setAttribute("CUST_FIELD_LABEL", "CUST_FIELD_LABEL");
		ocAttrLabelId.setStyle("color:blue");
		ocAttrLabelId.setVisible(true);
		ocAttrLabelId.setParent(ocAttrDiv);

		ocAttrDiv.setParent(tempRow);

		// Display Label
		Textbox tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);

		// DataType
		Div tempDiv = new Div();

		// Data Type
		Listbox tempListbox = createDataTypeListBox();
		tempListbox.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setSelectedIndex(0);
		tempListbox.setParent(tempDiv);

		// Date Format
		tempListbox = createDateFormatListbox();
		tempListbox.setSelectedIndex(0);
		tempListbox.setVisible(false);
		tempListbox.setParent(tempDiv);

		tempDiv.setParent(tempRow);

		// Unique priority
		Intbox priIntBx = new Intbox();
		priIntBx.setMaxlength(1);
		priIntBx.setWidth("30px");

		priIntBx.setDisabled(true);
		if (isAdmin) {
			priIntBx.setDisabled(false);
		}

		priIntBx.setParent(tempRow);

		tempRow.setParent(contactRowsId);

		/*
		 * //POS Attr Div tempcsvDiv = new Div(); tempcsvDiv.setParent(tempRow);
		 * 
		 * tempTextBox = new Textbox(); //tempTextBox.setDisabled(true);
		 * tempTextBox.setParent(tempcsvDiv);
		 * 
		 * Listbox tempListbox = null;
		 * 
		 * Image tempcsvDelValImg = new Image();
		 * tempcsvDelValImg.setSrc("/img/action_delete.gif");
		 * tempcsvDelValImg.setStyle("cursor:pointer;");
		 * tempcsvDelValImg.setStyle("cursor:pointer;margin:0 10px 0 15px;");
		 * tempcsvDelValImg.addEventListener("onClick", this);
		 * tempcsvDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		 * tempcsvDelValImg.setParent(tempcsvDiv);
		 * 
		 * tempcsvDelValImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
		 * 
		 * tempRow.setParent(contactRowsId);
		 * 
		 * //Custom Field Div tempocAttrDiv = new Div();
		 * 
		 * tempListbox = createContactPosMappingListbox();
		 * tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
		 * tempListbox.addEventListener("onSelect", this);
		 * tempListbox.setSelectedIndex(17); tempListbox.setParent(tempRow);
		 * 
		 * Label ocAttrLabel = new Label(); ocAttrLabel.setAttribute("CUST_FIELD_LABEL",
		 * "CUST_FIELD_LABEL"); ocAttrLabel.setStyle("color:blue");
		 * 
		 * //Display Label tempTextBox = new Textbox(); tempTextBox.setParent(tempRow);
		 * 
		 * //DataType Div tempDiv = new Div();
		 * 
		 * //Data Type tempListbox = createDataTypeListBox();
		 * tempListbox.addEventListener("onSelect", this);
		 * tempListbox.setSelectedIndex(0); tempListbox.setParent(tempDiv);
		 * 
		 * //Date Format tempListbox = createDateFormatListbox();
		 * tempListbox.setSelectedIndex(0); tempListbox.setVisible(false);
		 * tempListbox.setParent(tempDiv);
		 * 
		 * tempDiv.setParent(tempRow);
		 * 
		 * // Optional Value Div optDiv=new Div(); optDiv.setParent(tempRow);
		 * 
		 * Combobox cb = new Combobox(); cb.setSclass("cb_100w"); cb.setParent(optDiv);
		 * 
		 * 
		 * //Optional Value Add Action Image optAddValImg = new Image();
		 * optAddValImg.setSrc("/img/action_add.jpg");
		 * optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		 * optAddValImg.addEventListener("onClick", this);
		 * optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
		 * optAddValImg.setParent(optDiv);
		 * 
		 * //Optional Value Add Action Image optDelValImg = new Image();
		 * optDelValImg.setSrc("/img/action_delete.gif");
		 * optDelValImg.setStyle("cursor:pointer;");
		 * optDelValImg.addEventListener("onClick", this);
		 * optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		 * optDelValImg.setParent(optDiv);
		 * 
		 * //Display Label tempTextBox = new Textbox(); tempTextBox.setParent(tempRow);
		 * 
		 * // Unique priority Intbox priIntBx = new Intbox(); priIntBx.setMaxlength(1);
		 * priIntBx.setWidth("30px");
		 * 
		 * priIntBx.setDisabled(true); if(isAdmin) { priIntBx.setDisabled(false); }
		 * 
		 * priIntBx.setParent(tempRow);
		 * 
		 * Div div=new Div(); div.setParent(tempRow);
		 * 
		 * //Delete Action Image delImg = new Image(); delImg.setAttribute("TYPE",
		 * "CONTACT_MAPPING_DELETE"); delImg.setSrc("/images/action_delete.gif");
		 * delImg.setStyle("cursor:pointer;"); delImg.addEventListener("onClick", this);
		 * delImg.setParent(div);
		 * 
		 * tempRow.setParent(contactRowsId);
		 */

	} // onClick$addContactPosMapBtnId

	// DataType
	private Listbox createDataTypeListBox() {

		Listbox dataTypelb = new Listbox();

		Listitem tempItem = new Listitem("String");
		tempItem.setParent(dataTypelb);

		// String","Date","Number","Double","Boolean"
		tempItem = new Listitem("Date");
		tempItem.setParent(dataTypelb);

		tempItem = new Listitem("Number");
		tempItem.setParent(dataTypelb);

		tempItem = new Listitem("Double");
		tempItem.setParent(dataTypelb);
		dataTypelb.setMold("select");
		return dataTypelb;

	} // createDataTypeListBox()

	// DateFormat
	private Listbox createDateFormatListbox() {

		Listbox dateFormatListBx = new Listbox();

		Listitem tempItem = new Listitem("dd/MM/yyyy");
		tempItem.setParent(dateFormatListBx);

		// String","Date","Number","Double","Boolean"
		tempItem = new Listitem("dd-MM-yyyy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM/dd/yyyy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM-dd-yyyy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM/dd/yy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM-dd-yy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("dd/MM/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM/dd/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("MM/dd/yyyy HH:mm:ss");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("dd-MMMMM-yyyy");
		tempItem.setParent(dateFormatListBx);

		tempItem = new Listitem("dd-MMMMM-yy");
		tempItem.setParent(dateFormatListBx);

		dateFormatListBx.setMold("select");
		return dateFormatListBx;
	} // createDateFormatListbox()

	// create ContactGenField ListBox
	private Listbox createContactPosMappingListbox() {

		Listbox dateFormatListBx = new Listbox();
		Listitem tempItem = null;
		tempItem = new Listitem(SELECT_STRING);
		tempItem.setParent(dateFormatListBx);
		List<POSMapping> LoyaltyMapList= posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_LOYALTY + "'", currentUser.getUserId());
		boolean loyaltyFlag = LoyaltyMapList != null && !LoyaltyMapList.isEmpty();
		// genralFieldList
		logger.info("loyaltyFlag===>"+loyaltyFlag);
		if (loyaltyFlag) {
			for (int i = 0; i < defaultFieldArrayLoyalty.length; i++) {
				// tempStr = (String)contactGenFieldList.get(i);
				tempItem = new Listitem(defaultFieldArrayLoyalty[i]);
				tempItem.setParent(dateFormatListBx);
			}
		} else {
			for (int i = 0; i < defaultFieldArray.length; i++) {
				// tempStr = (String)contactGenFieldList.get(i);
				tempItem = new Listitem(defaultFieldArray[i]);
				tempItem.setParent(dateFormatListBx);
			}
		}
		 
		
		  
		  LoyaltyMapList.addAll( posMappingDao.findByType("'" +
				  Constants.POS_MAPPING_TYPE_CONTACTS + "'", currentUser.getUserId()));
		 
				Map<String,String> udfDataDisplayMap = new HashMap<String,String>();
				for (POSMapping posMapping : LoyaltyMapList) {
					if (posMapping.getCustomFieldName().startsWith("UDF")) {
						if(posMapping.getDisplayLabel()!=null) {
							existOptCulFiledMap.put(posMapping.getDisplayLabel(), posMapping); // additional code to use the udf fields Map
							udfDataDisplayMap.put(posMapping.getCustomFieldName(), posMapping.getDisplayLabel());
						}
					}
				}
				// UDF FieldList
				
				  for (int i = 0; i < udfSetStr.length; i++) {
					  if(udfDataDisplayMap.containsKey(udfSetStr[i])) {
						  tempItem = new Listitem(udfDataDisplayMap.get(udfSetStr[i])); 
						  tempItem.setValue(udfSetStr[i]);
						  tempItem.setParent(dateFormatListBx);
					  }else {
						  tempItem = new Listitem(udfSetStr[i]); 
						  tempItem.setValue(udfSetStr[i]);
						  tempItem.setParent(dateFormatListBx);   
					  }
				}

		/*
		 * tempItem = new Listitem(SELECT_STRING); tempItem.setParent(dateFormatListBx);
		 */

		dateFormatListBx.setMold("select");
		return dateFormatListBx;

	}// createContactPosMappingListbox()

	/*
	 * Map<String, String> csvAndOptFiledMap = null;
	 * 
	 * 
	 */
	String filePath = "";

	public void onClick$saveContinueBtnId() {

		List<String> scopeCustfieldList = new ArrayList<String>();
		List<String> scopePosAttrList = new ArrayList<String>();
		List<String> scopeDispLblList = new ArrayList<String>();

		// TODO validations
		List chaildRowLst = contactRowsId.getChildren();

		logger.info("chaildRowLst" + chaildRowLst.toArray());

		boolean fieldSel = false;
		// boolean isHeder = false;
		for (Object object : chaildRowLst) {
			logger.info("object===>" + object);
			Row temRow = (Row) object;

			// POSMapping posMapping = null;
			List chaildLis = temRow.getChildren();

			// csv File Attribute

			Div csvDiv = (Div) chaildLis.get(0);
			Textbox csvColTxtBx = (Textbox) csvDiv.getChildren().get(0);
			csvColTxtBx.setStyle(NORMAL_STYLE);

			/*
			 * Textbox csvColTxtBx = (Textbox) chaildLis.get(0);
			 * csvColTxtBx.setStyle(NORMAL_STYLE);
			 */

			// Display label
			Textbox displayTextbox = (Textbox) chaildLis.get(2);
			// displayTextbox.setStyle(NORMAL_STYLE);

			// Uniq Prio label

			Intbox uniqPriIntbox = (Intbox) chaildLis.get(4);
			uniqPriIntbox.setStyle(NORMAL_STYLE);

			if (csvColTxtBx.getValue().trim().equals("")) {
				logger.debug("Custom fieldData is eampty");
				csvColTxtBx.setStyle("border:1px solid #DD7870;");

				MessageUtil.setMessage("Provide .csv column label.", "color:red", "TOP");
				return;
			} else if (displayTextbox.getValue().trim().equals("")) {
				displayTextbox.setStyle(ERROR_STYLE);
				logger.debug("Custom fieldData is eampty");
				MessageUtil.setMessage("Provide contact Display Label.", "color:red", "TOP");
				return;
			} else {
				try {
					if (uniqPriIntbox.getValue() != null && uniqPriIntbox.getValue() <= 0) {
						uniqPriIntbox.setStyle(ERROR_STYLE);
						logger.debug("Invalid unique priority eampty");
						MessageUtil.setMessage("Provide valid Unique Priority value.", "color:red", "TOP");
						return;
					}
				} catch (Exception e) {
					uniqPriIntbox.setStyle(ERROR_STYLE);
					logger.debug("Invalid unique priority eampty");
					MessageUtil.setMessage("Provide valid Unique Priority value.", "color:red", "TOP");
					return;
				}
			}
			String csvFileAttr = csvColTxtBx.getValue().toLowerCase();
			logger.info("csvFileAttr===>" + csvFileAttr);

			/*
			 * if(csvFileAttr.contains("email") || csvFileAttr.contains("mobile") ||
			 * csvFileAttr.contains("phone") || csvFileAttr.contains("customer")) {
			 * 
			 * isHeder = true; }
			 */

			// optculture field

			Div optFieldDiv = (Div) chaildLis.get(1);
			Listbox optFieldLstBx = (Listbox) optFieldDiv.getChildren().get(0);

			if (optFieldLstBx.getSelectedItem() == null) {
				MessageUtil.setMessage("Please select the field from List box.", "color:red", "TOP");
				optFieldLstBx.setStyle(ERROR_STYLE);
				return;
			}

			String scopeCustFieldStr = optFieldLstBx.getSelectedItem().getLabel();

			optFieldLstBx.setStyle(NORMAL_STYLE);

			/*
			 * Listbox optFieldLstBx = (Listbox)chaildLis.get(1); String scopeCustFieldStr =
			 * optFieldLstBx.getSelectedItem().getLabel();
			 * 
			 * optFieldLstBx.setStyle(NORMAL_STYLE);
			 */
			if (scopeCustFieldStr.equals(SELECT_STRING)) {
				MessageUtil.setMessage("Please select the OptCulture Attribute field.", "color:red", "TOP");
				optFieldLstBx.setStyle(ERROR_STYLE);
				return;
			}

			if (scopeCustFieldStr.equals("")) {
				MessageUtil.setMessage("Please select the OptCulture Attribute field.", "color:red", "TOP");
				optFieldLstBx.setStyle(ERROR_STYLE);
				return;
			}

			if (scopeCustFieldStr.equals("Email") || scopeCustFieldStr.equals("Mobile")) {
				fieldSel = true;
			}

			String scopePosStr = csvColTxtBx.getValue();
			String scopeDispStr = displayTextbox.getValue();

			if (scopeCustfieldList.contains(scopeCustFieldStr)) {
				optFieldLstBx.setStyle(ERROR_STYLE);
				MessageUtil.setMessage(
						"Optculture field  " + optFieldLstBx.getSelectedItem().getLabel() + " is already mapped.",
						"color:red", "TOP");
				return;
			} else if (scopePosAttrList.contains(scopePosStr)) {
				csvColTxtBx.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("CSV column label  " + csvColTxtBx.getValue() + " is already mapped.",
						"color:red", "TOP");
				return;
			} else if (scopePosAttrList.contains(scopePosStr)) {
				csvColTxtBx.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("CSV column label  " + csvColTxtBx.getValue() + " is already mapped.",
						"color:red", "TOP");
				return;
			} else if (scopeDispLblList.contains(scopeDispStr)) {
				displayTextbox.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Display column label  " + displayTextbox.getValue() + " is already mapped.",
						"color:red", "TOP");
				return;
			}
			scopeCustfieldList.add(scopeCustFieldStr);
			scopePosAttrList.add(scopePosStr);
			scopeDispLblList.add(scopeDispStr);

		}

		/*
		 * if(!isHeder) {
		 * MessageUtil.setMessage("File does not contain proper header fields."
		 * ,"color:red","TOP"); return; }
		 */

		if (!fieldSel) {
			MessageUtil.setMessage("At least email or mobile must be selected.", "color:red", "TOP");
			return;
		}

		try {

			int confirm = Messagebox.show("Are you sure you want to save the new mapping fields?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

			if (confirm == Messagebox.OK) {
				// logger.info("ok");
				List<POSMapping> posMapList = new ArrayList<POSMapping>();

				String csvHeaderFileds = "";
				for (Object object : chaildRowLst) {
					Row tempRow = (Row) object;

					if (tempRow.getAttribute("POS_MAP_OBJECT") != null)
						continue;

					List chaildLis = tempRow.getChildren();

					// custom field

					Div optFieldDiv = (Div) chaildLis.get(1);
					// Listbox custFielListBx = (Listbox)optFieldDiv.getChildren().get(0);
					// Listbox custFielListBx = (Listbox) ((Div)chaildLis.get(1);

					Listbox custFielListBx = (Listbox) (((Div) chaildLis.get(1)).getChildren().get(0));

					if (existOptCulFiledMap != null
							&& existOptCulFiledMap.containsKey(custFielListBx.getSelectedItem().getLabel()))
						continue;

					POSMapping posmapObj = new POSMapping();

					// Set custom Fields
					posmapObj.setCustomFieldName(custFielListBx.getSelectedItem().getLabel());

					// set the UserId
					posmapObj.setUserId(currentUser.getUserId());

					// Set Mapping Type
					posmapObj.setMappingType(Constants.POS_MAPPING_TYPE_CONTACTS);

					// POS Attribute (CSV colomn Label)
					Textbox csvColTxtBx = (Textbox) ((Div) chaildLis.get(0)).getChildren().get(0);
					posmapObj.setPosAttribute(csvColTxtBx.getValue());
					if (csvHeaderFileds.length() > 0) {
						csvHeaderFileds = csvHeaderFileds + ", " + csvColTxtBx.getValue();
					} else {
						csvHeaderFileds = csvHeaderFileds + csvColTxtBx.getValue();
					}
					logger.info("csvHeaderFileds===>" + csvHeaderFileds);

					// Data Type
					Div dateFormatDiv = (Div) chaildLis.get(3);

					Listbox dataTypeListbox = (Listbox) dateFormatDiv.getChildren().get(0);
					String dataTypeStr = dataTypeListbox.getSelectedItem().getLabel();
					if (dataTypeStr.equals("Date")) {
						dataTypeStr = dataTypeStr + "("
								+ ((Listbox) dateFormatDiv.getChildren().get(1)).getSelectedItem().getLabel() + ")";
					}
					posmapObj.setDataType(dataTypeStr);

					// Display Label
					Textbox dispLblTextBx = (Textbox) chaildLis.get(2);
					posmapObj.setDisplayLabel(dispLblTextBx.getValue());

					// Optional Values
					/*
					 * Combobox optCombBox = (Combobox) ((Div) chaildLis.get(4)).getFirstChild();
					 * String optCombStr="";
					 * 
					 * List<Comboitem> cbItemsList = optCombBox.getItems(); for (Comboitem cbItem :
					 * cbItemsList) { if(optCombStr.length()>0) optCombStr +=
					 * Constants.ADDR_COL_DELIMETER ; optCombStr += cbItem.getLabel(); }
					 * if(optCombStr.length()>0) posmapObj.setOptionalValues(optCombStr); else
					 * posmapObj.setOptionalValues(null);
					 */

					posMapList.add(posmapObj);

				}

				if (posMapList.size() > 0) {
					MessageUtil.setMessage(
							csvHeaderFileds + " csv column headers of new POS mapping Fields saved successfully",
							"Color:Blue");
					// posMappingDao.saveByCollection(posMapList);
					posMappingDaoForDML.saveByCollection(posMapList);

				}

				uploadMListsContacts(filePath);

			}

			// TODO show visible of
			/*
			 * fileUploadDivId.setVisible(true); listMappingDivId.setVisible(false);
			 */

		} catch (Exception e) {

			logger.error("Exception::", e);
		}

	} // onClick$saveContinueBtnId
	
	
	public void onUpload$uploadBtnId(UploadEvent uploadEvent){
		logger.info("evt====="+uploadEvent);
		Media media=uploadEvent.getMedia();
		logger.info("meedia"+media);
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName")
		+ "/List/" + ((Media) media).getName();
		logger.info("path===>" + path);
		String ext = FileUtil.getFileNameExtension(path);
		if (ext == null) {
			MessageUtil.setMessage("Upload .csv file only.", "color:red", "TOP");
			return;
		}
		if (!ext.equalsIgnoreCase("csv")) {
			MessageUtil.setMessage("Upload .csv file only.", "color:red", "TOP");
			return;
		}

		String pathString = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + GetUser.getUserName()
			+ "/List/" + media.getName();
		boolean isSuccess = copyDataFromMediaToFile(pathString, media);
		selectedFileTbId.setValue(media.getName());
		logger.info("media.getName()===>" + media.getName());
		selectedFileTbId.setDisabled(true);
		media = null;
		if (!isSuccess) {
			return;
		}

		defaultUserPOSMapSettings(pathString);
		filePath = pathString;
 

			listMappingDivId.setVisible(true);
		
	}
	
	public void onClick$cancelBtnId(){
		
		selectedFileTbId.setValue("");
		selectedFileTbId.setDisabled(false);
		listMappingDivId.setVisible(false);
		
		
	}

	//public void onClick$uploadBtnId() {
		
		//uploadBtnId1.set
		

		/*List<POSMapping> contMapList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
				currentUser.getUserId());
		if (contMapList == null || contMapList.size() == 0) {
			MessageUtil.setMessage("Please create the contact mapping list.", "color:red", "TOP");
			return;
		}

		Media media = Fileupload.get();
		MessageUtil.clearMessage();
		if (media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName")
				+ "/List/" + ((Media) media).getName();
		logger.info("path===>" + path);
		String ext = FileUtil.getFileNameExtension(path);
		if (ext == null) {
			MessageUtil.setMessage("Upload .csv file only.", "color:red", "TOP");
			return;
		}
		if (!ext.equalsIgnoreCase("csv")) {
			MessageUtil.setMessage("Upload .csv file only.", "color:red", "TOP");
			return;
		}

		String pathString = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + GetUser.getUserName()
				+ "/List/" + media.getName();
		boolean isSuccess = copyDataFromMediaToFile(pathString, media);
		selectedFileTbId.setValue(media.getName());
		logger.info("media.getName()===>" + media.getName());
		selectedFileTbId.setDisabled(true);
		media = null;
		if (!isSuccess) {
			return;
		}

		// FInd Header Fields from the Files
		defaultUserPOSMapSettings(pathString);
		filePath = pathString;

		
	
		listMappingDivId.setVisible(true);*/

	//} // onClick$uploadBtnId

	public boolean copyDataFromMediaToFile(String path, Media m) {
		MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		String ext = FileUtil.getFileNameExtension(path);
		File file = new File(path);
		BufferedReader br = null;
		BufferedWriter bw = null;
		if (!ext.equalsIgnoreCase("csv")) {
			MessageUtil.setMessage("Upload .csv file only.", "color:red", "BOTTOM");
			return false;
		}
		try {
			// if(logger.isDebugEnabled()) logger.debug("reading data from media using
			// getReaderData()");
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
			// logger.error("** Exception is " + e1.getMessage()+" :trying to read with
			// Media.getStringData() **");
			try {
				// if(logger.isDebugEnabled()) logger.debug("Reading file with
				// Media.getStringData()");
				String data = m.getStringData();
				FileUtils.writeStringToFile(file, data);
				return true;
			} catch (Exception e2) {
				// logger.error("** Exception is " + e2 +" :trying to read as Streams **");
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
					// logger.error("** Exception is : File not found **");
				} catch (Exception e3) {
					// logger.error("** Exception is " + e3 +" so trying to read as bytes **");
					try {
						byte[] data = m.getByteData();
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(data);
						fos.flush();
						fos.close();
						return true;
					} catch (Exception e) {
						// logger.error("** Exception is " + e +" **");
					}
				}
				String message = "CSV file upload failed," + m.getName()
						+ "\n could not copied reason may be due to network problem or may be very large file";
				Users user = GetUser.getUserObj();
				(new MessageHandler(messagesDao, user.getUserName())).sendMessage("Contact", "uploaded failed", message,
						"Inbox", false, "INFO", user);
				return false;
			}

		}
	} // copyDataFromMediaToFile

	private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");
	private ArrayList<String> allMatches = new ArrayList<String>();
	private Matcher matcher = null;

	public String[] parse(String csvLine) {
		matcher = csvPattern.matcher(csvLine);
		allMatches.clear();
		String match;
		while (matcher.find()) {
			match = matcher.group(1);
			if (match != null) {
				allMatches.add(match);
			} else {
				allMatches.add(matcher.group(2));
			}
		}

		if (allMatches.size() > 0) {
			return allMatches.toArray(new String[allMatches.size()]);
		} else {
			return new String[0];
		}
	}

	private void uploadMListsContacts(String path) {

		try {
			Object[] obj = { currentUser, mailingListSet, contactRowsId, path };

			if (logger.isDebugEnabled())
				logger.debug("Is uploadCSVFile thread running : " + obj);
			UploadContactsThread uploadContactsThread = (UploadContactsThread) SpringUtil
					.getBean("uploadContactsThread");
			logger.debug(" >>> " + uploadContactsThread.isRunning);
			synchronized (uploadContactsThread) {
				uploadContactsThread.uploadQueue.add(obj);
				if (!uploadContactsThread.isRunning) {
					Thread thread = new Thread(uploadContactsThread);
					thread.start();
				}
			}

			listMappingDivId.setVisible(false);
			fileUploadDivId.setVisible(false);

			uploadResultDivId.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

	} // uploadMListsContacts

	public void onClick$closeBtnId() {
		Redirect.goTo(PageListEnum.RM_HOME);
	}

	public void onClick$backBtnId() {
		Redirect.goToPreviousPage();
	} // onClick$BackBtnId

	private Set<String> optinMobileSet = new HashSet<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.zk.ui.event.GenericEventListener#onEvent(org.zkoss.zk.ui.event.
	 * Event)
	 */
	@Override
	public void onEvent(Event event) throws Exception {

		super.onEvent(event);
		boolean isUsed = false;
		if (event.getTarget() instanceof Listbox) {

			Listbox tempListBx = (Listbox) event.getTarget();

			String selectDataType = tempListBx.getSelectedItem().getLabel();

			if (tempListBx.getAttribute("CUST_FIELD_LISTBOX") != null
					&& tempListBx.getAttribute("CUST_FIELD_LISTBOX").equals("CUST_FIELD_LISTBOX")) {

				// logger.info("i am here "+ selectDataType);
				// logger.info("########## selectDateType
				// #######"+((Row)tempListBx.getParent()).getChildren().size());

				POSMapping posMapping = existOptCulFiledMap.get(tempListBx.getSelectedItem().getLabel());

				// logger.info("pos mapping obj is:::"+posMapping);

				// Div ocAttrDiv = (Div)((Row)tempListBx.getParent()).getChildren().get(1);
				Div ocAttrDiv = (Div) ((Row) ((Div) tempListBx.getParent()).getParent()).getChildren().get(1);

				Textbox dispLblTxtBox = (Textbox) ((Row) ((Div) tempListBx.getParent()).getParent()).getChildren()
						.get(2);
				Label ocAttrLabel = (Label) ocAttrDiv.getChildren().get(1);
				Listbox ocAttrLB = (Listbox) ocAttrDiv.getChildren().get(0);

				Div dataTypeDiv = (Div) ((Row) ((Div) tempListBx.getParent()).getParent()).getChildren().get(3);
				Listbox dataTypeListbox = (Listbox) dataTypeDiv.getChildren().get(0);
				Listbox dataformatListbox = (Listbox) dataTypeDiv.getChildren().get(1);

				if (posMapping != null) {
					ocAttrLabel.setValue("Used Field");
					dispLblTxtBox.setValue(posMapping.getDisplayLabel());
					dispLblTxtBox.setDisabled(true);
					// dataTypeListbox.setDisabled(true);
					isUsed = true;

				} else {

					if (ocAttrLB.getSelectedItem().getLabel().equals("--Select --")) {
						ocAttrLabel.setValue("");
						dispLblTxtBox.setValue("");
						dispLblTxtBox.setDisabled(false);
						isUsed = false;
					} else {
						ocAttrLabel.setValue("Unused Field");
						dispLblTxtBox.setValue("");
						dispLblTxtBox.setDisabled(false);
						isUsed = false;
					}
				}
				List ChildItemList = dataTypeListbox.getChildren();
				String dataTypeStr = "";
				if (posMapping != null) {
					dataTypeStr = posMapping.getDataType();

				} else {
					dataTypeStr = dataTypeListbox.getSelectedItem().getLabel();

				}

				logger.info("dataType is====>" + dataTypeStr);

				for (Object object : ChildItemList) {

					Listitem listItem = (Listitem) object;

					if (dataTypeStr.startsWith("Date") && listItem.getLabel().toString().equals("Date")
							&& dataTypeStr.trim().length() > "Date".length()) {
						dataTypeListbox.setSelectedItem(listItem);
						dataTypeListbox.setDisabled(true);

						dataformatListbox.setVisible(true);

						List dateFormatList = dataformatListbox.getChildren();
						dataTypeStr = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1, dataTypeStr.indexOf(")"));

						for (Object obj : dateFormatList) {

							Listitem tempListItem = (Listitem) obj;
							if (tempListItem.getLabel().equals(dataTypeStr)) {
								dataformatListbox.setSelectedItem(tempListItem);
								dataformatListbox.setDisabled(true);
								break;
							}
						}

					} else if (listItem.getLabel().equals(dataTypeStr) && tempListBx.getSelectedIndex() != 32) {
						dataTypeListbox.setSelectedItem(listItem);
						dataTypeListbox.setDisabled(true);
						dataformatListbox.setVisible(false);
						break;
					}

				}
				// for
				/*
				 * if(selectDataType.startsWith(BIRTHDAY) ||
				 * selectDataType.startsWith(ANNIVERSARY) ||
				 * selectDataType.startsWith("Created Date")) {
				 * 
				 * dataTypeListbox.setSelectedIndex(1); dataTypeListbox.setDisabled(true);
				 * dataformatListbox.setVisible(true); dataformatListbox.setDisabled(true);
				 * 
				 * } else
				 */ if (selectDataType.startsWith(ZIP) || selectDataType.startsWith(MOBILE)) {
					dataTypeListbox.setSelectedIndex(0);
					// dataTypeListbox.setDisabled(false);
					dataTypeListbox.setDisabled(true);
					((Listbox) dataTypeDiv.getChildren().get(1)).setVisible(false);
				} else if (selectDataType.startsWith("UDF")) {
					dataTypeListbox.setSelectedIndex(0);
					dataTypeListbox.setDisabled(false);
					// dataTypeListbox.setDisabled(true);
					((Listbox) dataTypeDiv.getChildren().get(0)).setVisible(true);
					dataformatListbox.setVisible(false);
				}

				/*
				 * else { dataTypeListbox.setSelectedIndex(0); //
				 * dataTypeListbox.setDisabled(false); dataTypeListbox.setDisabled(false);
				 * dataformatListbox.setVisible(false);
				 * //((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false); }
				 */

				if (isUsed) {
					// dataTypeListbox.setSelectedIndex(0);
					dataTypeListbox.setDisabled(true);
					((Listbox) dataTypeDiv.getChildren().get(0)).setVisible(true);
					// dataformatListbox.setVisible(false);
				} else {
					dataTypeListbox.setSelectedIndex(0);
					dataTypeListbox.setDisabled(false);
					// dataTypeListbox.setDisabled(true);
					((Listbox) dataTypeDiv.getChildren().get(0)).setVisible(true);
					dataformatListbox.setVisible(false);
				}

			} else if (tempListBx.getAttribute("DATATYPE_LISTBOX") != null
					&& tempListBx.getAttribute("DATATYPE_LISTBOX").equals("DATATYPE_LISTBOX")) {

				// logger.info("tempListBx getAttribute method called here >>>. ");
				// Data type
				Div dataTypeDiv = (Div) tempListBx.getParent();
				// Listbox dataTypeListbox = (Listbox)dataTypeDiv.getChildren().get(0);
				Listbox dataformatListbox = (Listbox) dataTypeDiv.getChildren().get(1);

				/*
				 * Div ocAttrDiv =
				 * (Div)((Row)((Div)tempListBx.getParent()).getParent()).getChildren().get(1);
				 * Label ocAttrLabel =(Label)ocAttrDiv.getChildren().get(1); Listbox
				 * dataTypeListbox = (Listbox)dataTypeDiv.getChildren().get(0);
				 */

				if (selectDataType.startsWith("Date")) {
					dataformatListbox.setVisible(true);
					dataformatListbox.setDisabled(false);
				} else {
					dataformatListbox.setVisible(false);
				}

			}

			if (existOptCulFiledMap != null
					&& existOptCulFiledMap.containsKey(tempListBx.getSelectedItem().getLabel())) {
				POSMapping posMapping = existOptCulFiledMap.get(tempListBx.getSelectedItem().getLabel());
				Intbox uniqPriIntbox = (Intbox) ((Row) ((Div) tempListBx.getParent()).getParent()).getChildren().get(4);

				if (posMapping.getUniquePriority() != null) {
					uniqPriIntbox.setValue(posMapping.getUniquePriority());
				} else {
					uniqPriIntbox.setValue(null);
				}

			}

		} else if (event.getTarget() instanceof Image) {
			Image img = (Image) event.getTarget();

			if (img.getAttribute("TYPE") != null && img.getAttribute("TYPE").equals("CONTACT_MAPPING_DELETE")) {

				Row tempRow = (Row) (img.getParent()).getParent();
				contactRowsId.removeChild(tempRow);
			}

		}

	}

}
