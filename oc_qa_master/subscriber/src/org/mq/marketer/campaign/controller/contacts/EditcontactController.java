package org.mq.marketer.campaign.controller.contacts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

@SuppressWarnings({"serial","unchecked"})
public class EditcontactController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Contacts  contact;
	ContactParentalConsent contactParentalConsent;
 	Textbox firstNameTbId;
	Textbox lastNameTbId;
	Textbox addOneTbId;
	Textbox addTwoTbId;
	Textbox cityTbId;
	Textbox stateTbId;
	Textbox countryTbId,rProTbId,homeStoreTbId;
	Textbox pinIbId;
	Textbox phoneLbId;
	Div custFieldDivId;
	Div cfDivId;
	Textbox emailIdTbId;
	private Combobox gendarListBxId;
//	private Button editBtnId;
	private Button updateBtnId;
	private MyDatebox birthdayDateBxId,anniversaryDateBxId;
	
	private Session session = Sessions.getCurrent();
	ContactsDao contactsDao;
	ContactsDaoForDML contactsDaoForDML;
	MLCustomFieldsDao mlCustomFieldsDao;
	CustomFieldDataDao cfDataDao;
	
	private EventTriggerEventsObservable eventTriggerEventsObservable;
	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerDao eventTriggerDao;
	private List<EventTrigger> activatedTriggersList;
	
	String userName;
	List custTBList = null;
	private String editContact;
	private String tempEmailId;
	private Users user;
//	private Long phoneNum;
//	MailingListDao mailingListDao;
	//private boolean isDisable;
	
	MailingList posMList = null;
	POSMappingDao posMappingDao;
	Map<String, Object> genFieldContMap = new HashMap<String, Object>();
	
	public EditcontactController() {
		userName = GetUser.getUserName();
		user = GetUser.getUserObj();
		mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		cfDataDao = (CustomFieldDataDao)SpringUtil.getBean("cfDataDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		this.contactsDao=(ContactsDao)SpringUtil.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		
		contact = ((Contacts)session.getAttribute("emailId"));
		contactParentalConsent = (ContactParentalConsent)session.getAttribute("isUnderAge");
		
		if(contact != null) {
			
			editContact = (String)session.getAttribute("editcontact");
			
			
			logger.info("==>>"+contact.getEmailId());
			logger.info(contact.getMobileStatus());
			emailIdTbId.setValue(contact.getEmailId() != null ?contact.getEmailId():"");
			tempEmailId = emailIdTbId.getValue();
			firstNameTbId.setValue(contact.getFirstName() != null?contact.getFirstName():"");
			lastNameTbId.setValue(contact.getLastName() != null ? contact.getLastName():"");
			addOneTbId.setValue(contact.getAddressOne() != null? contact.getAddressOne():"");
			addTwoTbId.setValue(contact.getAddressTwo() !=null ?contact.getAddressTwo():" ");
			cityTbId.setValue(contact.getCity() !=null?contact.getCity():"");
			countryTbId.setValue(contact.getCountry() != null?contact.getCountry():"");
			stateTbId.setValue(contact.getState() != null ?contact.getState():"");
			
//			pinIbId.setValue(contact.getPin());
			/*if((contact.getPhone()) != null ){
				phoneLbId.setValue(contact.getPhone());
				phoneNum =contact.getPhone();
			}*/
			//ZIP
			if((contact.getZip()) != null ){
				pinIbId.setValue(contact.getZip());
			}
			//Mobile Phone
			if((contact.getMobilePhone()) != null ){
				phoneLbId.setValue(contact.getMobilePhone());
//				phoneNum =phoneLbId.getValue();
			}
			
			
			//Gendar
			String[] splitArray= null;
			List<POSMapping> contMapList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
					user.getUserId());
			
			for (POSMapping map : contMapList) {
				if (map.getCustomFieldName().equals("Gender")) {
					if(map.getOptionalValues()!=null) {
						splitArray = map.getOptionalValues().split(Constants.ADDR_COL_DELIMETER);
					}
				}
			}
			
			if (splitArray != null) {
				
				for(String optionalValue : splitArray) {
					Comboitem gender = new Comboitem(optionalValue);
					gender.setParent(gendarListBxId);
				}
			}
			
			if (contact.getGender() == null) {
				gendarListBxId.setSelectedIndex(0);
			} else {
				Comboitem gender = new Comboitem(contact.getGender());
				for(Comboitem item : gendarListBxId.getItems()) {
					if(contact.getGender().equalsIgnoreCase(item.getLabel())) { 
						gendarListBxId.setSelectedItem(item); 
					}
				}
				if(gendarListBxId.getSelectedItem()==null) {
					gender.setParent(gendarListBxId);
					gendarListBxId.setSelectedItem(gender);
				}
			}			
			
			Calendar tempCal= Calendar.getInstance();
			//Birhday
			if(!(contact.getBirthDay() == null)) {
				/*
				tempDate = (contact.getBirthDay()).getTime();
				birthdayDateBxId.setValue(tempDate); */
				tempCal= Calendar.getInstance();
				tempCal.set(Calendar.YEAR, contact.getBirthDay().get(Calendar.YEAR));
				tempCal.set(Calendar.MONTH, contact.getBirthDay().get(Calendar.MONTH));
				tempCal.set(Calendar.DATE, contact.getBirthDay().get(Calendar.DATE));
				tempCal.set(Calendar.HOUR_OF_DAY, contact.getBirthDay().get(Calendar.HOUR_OF_DAY));
				tempCal.set(Calendar.MINUTE, contact.getBirthDay().get(Calendar.MINUTE));
				tempCal.set(Calendar.SECOND, contact.getBirthDay().get(Calendar.SECOND));
				birthdayDateBxId.setValue(tempCal);
			 }
			
			//Anniversary
			if(!(contact.getAnniversary() == null)) {
				
				/*tempDate = (contact.getAnniversary()).getTime();
				anniversaryDateBxId.setValue(tempDate); */
				tempCal= Calendar.getInstance();
				tempCal.set(Calendar.YEAR, contact.getAnniversary().get(Calendar.YEAR));
				tempCal.set(Calendar.MONTH, contact.getAnniversary().get(Calendar.MONTH));
				tempCal.set(Calendar.DATE, contact.getAnniversary().get(Calendar.DATE));
				tempCal.set(Calendar.HOUR_OF_DAY, contact.getAnniversary().get(Calendar.HOUR_OF_DAY));
				tempCal.set(Calendar.MINUTE, contact.getAnniversary().get(Calendar.MINUTE));
				tempCal.set(Calendar.SECOND, contact.getAnniversary().get(Calendar.SECOND));
				anniversaryDateBxId.setValue(tempCal); 
			 }
			
			//Retail ProId
			if(contact.getExternalId() != null) {
				rProTbId.setValue(contact.getExternalId());
			}
			
			//HomeStore
			if(contact.getHomeStore() != null) {
				homeStoreTbId.setValue(contact.getHomeStore());
			}
			
			
			
			/*Long userIdLong = null;
			if(contact.getUsers().getParentUser() != null) {
				userIdLong = contact.getUsers().getParentUser().getUserId();
			}else {
				userIdLong = contact.getUsers().getUserId();
			}*/
			
			Long userIdLong = contact.getUsers().getUserId();
			
			List<POSMapping> mappingContactList = posMappingDao.findByType("'Contacts'", userIdLong);
			
			logger.debug("mappingContactList.size() is ::"+mappingContactList.size());
			if(mappingContactList != null && mappingContactList.size()  > 0 )  {
				setPOSCustomFiledData(mappingContactList);
			}
			
			
			/*MailingList posMailingList = (MailingList)contact.getMailingList();
			
			if(posMailingList.getListType() != null &&  posMailingList.getListType().equals("POS")) {
				// set pos type custom field data
				setPOSCustomFiledData(posMailingList);
			}
			else {
				getCustFields();
			}*/
			 
			
			/*if(editContact != null) {
				
				disableContactFields(false);*/
				
				/*if(editContact.equalsIgnoreCase("view")) {
					
					disableContactFields(true);
					
					
					
				}else if(editContact.equalsIgnoreCase("edit")) {
					
					disableContactFields(false);
					
				}*/
//			} // if
			
			
			
		}
		
	}
	
	
	private void disableContactFields(boolean isDisable) {
		
		updateBtnId.setVisible(!isDisable);
//		editBtnId.setVisible(isDisable);
		
		
		emailIdTbId.setReadonly(isDisable);//setValue(contact.getEmailId() != null ?contact.getEmailId():"");
		firstNameTbId.setReadonly(isDisable);//setValue(contact.getFirstName() != null?contact.getFirstName():"");
		lastNameTbId.setReadonly(isDisable);//setValue(contact.getLastName() != null ? contact.getLastName():"");
		addOneTbId.setReadonly(isDisable);//setValue(contact.getAddressOne() != null? contact.getAddressOne():"");
		addTwoTbId.setReadonly(isDisable);//setValue(contact.getAddressTwo() !=null ?contact.getAddressTwo():" ");
		cityTbId.setReadonly(isDisable);//setValue(contact.getCity() !=null?contact.getCity():"");
		countryTbId.setReadonly(isDisable);//setValue(contact.getCountry() != null?contact.getCountry():"");
		stateTbId.setReadonly(isDisable);//setValue(contact.getState() != null ?contact.getState():"");
		pinIbId.setReadonly(isDisable);//setValue(contact.getPin());
		phoneLbId.setReadonly(isDisable);//Value(contact.getPhone());
		
		gendarListBxId.setDisabled(isDisable);
		birthdayDateBxId.setDisabled(isDisable);
		anniversaryDateBxId.setDisabled(isDisable);
		
		if(custTBList != null && custTBList.size() > 0 ) {
			Datebox cfDb;
			Textbox cftb;
			for(Object obj : custTBList) {
				if(obj instanceof Datebox) {
					
					cfDb = (Datebox)obj;
					cfDb.setReadonly(isDisable);
					
				}else {
					cftb = (Textbox)obj;
					cftb.setReadonly(isDisable);
				}
			
			}//for
			
			
		}//for
		if(contactUdfHashTable!= null && contactUdfHashTable.size() > 0) {
			
			for(int i=1; i<= contactUdfHashTable.size(); i++) {
				
				if(!contactUdfHashTable.containsKey("UDF"+i)) continue;
				
				if(contactUdfHashTable.get("UDF"+i) instanceof Textbox){
					Textbox tempTextbox = (Textbox)contactUdfHashTable.get("UDF"+i);
					tempTextbox.setReadonly(isDisable);
					
				}else if(contactUdfHashTable.get("UDF"+i) instanceof Decimalbox){
					Decimalbox tempDecbox = (Decimalbox)contactUdfHashTable.get("UDF"+i);
					tempDecbox.setReadonly(isDisable);
					
				}else if(contactUdfHashTable.get("UDF"+i) instanceof Datebox){
					Datebox tempDatebox = (Datebox)contactUdfHashTable.get("UDF"+i);
					tempDatebox.setReadonly(isDisable);
					
				}
				
			}
			
		} //POS TYPE
		
		
	}
	
	/*public void onClick$editBtnId() {
		
		disableContactFields(false);
		
		
	}*/
	
	
	/*public void getCustFields() {

		try {
			MailingList ml = (MailingList)session.getAttribute("mailingList");
			if( ml == null || !ml.isCustField() ){
				logger.debug("mailing lists not a custfield");
				return;
			}

			logger.debug("mailing lists not a custfield"+ml.isCustField());
			logger.debug("getting the custom feilds for the mailing list : " + ml.getListName());
			
			List custList = null;
			custList = mlCustomFieldsDao.findAllByList(ml);
			logger.debug("Total custom fileds : " + custList.size());
			
			if(custList.size() <= 0){
				return;
			}
			
			MLCustomFields mlcf = null;

			String cflistStr = "";
			List cfIdList = new ArrayList();

			Label cfNameLbl = null;
			Label cfTypeLbl = null;
			Textbox cfValTb = null;
			Label colenLbl;
			HtmlNativeComponent htmlTable = new HtmlNativeComponent("table");
			htmlTable.setDynamicProperty("width", "100%");
			HtmlNativeComponent htmlTr;
			HtmlNativeComponent htmlTd;

			custTBList = new ArrayList();
			for(Object obj:custList) {
				mlcf = (MLCustomFields)obj;
				
				htmlTr = new HtmlNativeComponent("tr");
				htmlTr.setDynamicProperty("style", "line-height:30px;");
				
				htmlTd = new HtmlNativeComponent("td");
				htmlTd.setDynamicProperty("width", "50%");
				
				cfNameLbl = new Label(mlcf.getCustFieldName());
				cfNameLbl.setParent(htmlTd);

				cfTypeLbl = new Label(" (" + mlcf.getDataType() + ")");
				cfTypeLbl.setStyle("font-size:10px;color:#333;");
				cfTypeLbl.setParent(htmlTd);
				
				
				htmlTd.setParent(htmlTr);
				
				
				htmlTd = new HtmlNativeComponent("td");
				htmlTd.setDynamicProperty("width", "10px");
				
				colenLbl = new Label(Constants.DELIMETER_COLON + "");
				colenLbl.setParent(htmlTd);
				htmlTd.setParent(htmlTr);
				
				htmlTd = new HtmlNativeComponent("td");
				//htmlTd.setDynamicProperty("width", "10px");
				htmlTd.setParent(htmlTr);
				
				if(mlcf.getDataType().equalsIgnoreCase("Date")) {
					
					logger.debug("----just entered---");
					Datebox custDateDb = new MyDatebox();
					SimpleDateFormat format = null;
					if(mlcf.getFormat() != null) {
					 format = new SimpleDateFormat(mlcf.getFormat());
					}
					else{
						format = new SimpleDateFormat("dd/MM/yyyy");
					}
					custDateDb.setFormat(format.toPattern());
					//custDateDb.setValue(new Date(mlcf.getDefaultValue()));
					custDateDb.setAttribute("datatype", mlcf.getDataType());
					custDateDb.setAttribute("cfName", mlcf.getCustFieldName());
					custDateDb.setId("cust" + mlcf.getFieldIndex());
					
					custTBList.add(custDateDb);
					custDateDb.setParent(htmlTd);
					
				}
				
				else{
					cfValTb = new Textbox();
					cfValTb.setCols(25);
					cfValTb.setId("cust" + mlcf.getFieldIndex());
					cfValTb.setMaxlength(50);
					cfValTb.setAttribute("datatype", mlcf.getDataType());
					cfValTb.setAttribute("cfName", mlcf.getCustFieldName());
					
					custTBList.add(cfValTb);
					
					cfValTb.setParent(htmlTd);
				}
				
				htmlTd.setParent(htmlTr);
				
				htmlTr.setParent(htmlTable);
				
				
				cflistStr = cflistStr + "cust" + mlcf.getFieldIndex() + ",";
				cfIdList.add("cust" + mlcf.getFieldIndex());
			} // for(Object obj:custList)
			
			htmlTable.setParent(cfDivId);
			cflistStr =  cflistStr.substring(0, cflistStr.length()-1);
			logger.debug("getting values of : " + cflistStr);
			List list = cfDataDao.getCustomFieldData(cflistStr, contact);
			logger.debug("Got custom field list of size : " + list.size());
			if(list.size()>0){
				Textbox cftb = null;
				Datebox cfDb = null;
				if(custList.size()>1){
					Object[] cfvalues = (Object[])list.get(0);
					logger.debug("String length " + cfvalues.length);
					logger.debug("Displaying values");
					
					for(int i=0;i<cfIdList.size();i++) {
						logger.debug("Setting value fot the cust field : " + (String)cfIdList.get(i) + "       value :  " + cfvalues[i]);
						//cftb = (Textbox)Utility.getComponentById((String)cfIdList.get(i));
						if(custTBList.get(i) instanceof Datebox) {
							
							cfDb = (Datebox)custTBList.get(i);
							if(cfvalues[i] != null && ((String)cfvalues[i]).trim().length()>0) {
								DateFormat format = new SimpleDateFormat(cfDb.getFormat());
								Date date = format.parse((String)cfvalues[i]);
								cfDb.setValue(date);
								
							}
							//cfDb.setDisabled(isDisable);
						}else{
							cftb = (Textbox)custTBList.get(i);
							cftb.setValue((String)cfvalues[i]);
							//cftb.setDisabled(isDisable);
						}
					}
				}else if(custList.size() == 1) {
					
					logger.debug("Setting value fot the cust field : " + (String)cfIdList.get(0) + "   and     value :  " + list.get(0));
					if(custTBList.get(0) instanceof Datebox) {
						cfDb = (Datebox)custTBList.get(0);
						if(list.get(0) != null) {
							DateFormat format = new SimpleDateFormat(cfDb.getFormat());
							Date date = format.parse((String)list.get(0));
							cfDb.setValue(date);
							
						}
						//cfDb.setDisabled(isDisable);
					}else{
						cftb = (Textbox)custTBList.get(0);
						cftb.setValue((String)list.get(0));
						//cftb.setDisabled(isDisable);
					}
					
				}
			}
			custFieldDivId.setVisible(true);
			
			
		} catch (Exception e) {
			logger.error("** Exception : Problem while getting custom field info - " , e );
		}
	}*/
	
	Hashtable<String , Object > contactUdfHashTable = null; 
	public void setPOSCustomFiledData(List<POSMapping> mappingContactList) {
		contactUdfHashTable = new Hashtable<String, Object>();
		
		try {
			
			//Set UDF values
			int udfIdx = 0;
			String udfCustFieldStr = "";
			for (POSMapping posMapping : mappingContactList) {
				
				udfCustFieldStr = posMapping.getCustomFieldName();
				if(!udfCustFieldStr.trim().startsWith("UDF")) {
					continue;
				}
				
				udfIdx = Integer.parseInt(udfCustFieldStr.substring("UDF".length()));
				String posUDFValue = "";
				if(getUdfContactObj(udfIdx) != null){
					posUDFValue = getUdfContactObj(udfIdx);
				}
					
				setPOSCustFieldUI(posMapping , posUDFValue);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		
		
	} //setPOSCustomFiledData
	
	
	private void setPOSCustFieldUI(POSMapping posMapping, String contactUdfValue) {
		try {
		//		logger.debug("contactUdfValue ::"+contactUdfValue);
						
				HtmlNativeComponent htmlTable = new HtmlNativeComponent("table");
				htmlTable.setDynamicProperty("width", "100%");
				HtmlNativeComponent htmlTr = new HtmlNativeComponent("tr");
				htmlTr.setDynamicProperty("style", "line-height:30px;");
				
				HtmlNativeComponent htmlTd = new HtmlNativeComponent("td");
				htmlTd.setDynamicProperty("width", "50%");
				
				String udfDispalyStr = posMapping.getDisplayLabel();
				
				/*if(posMapping.getDataType().startsWith("Date")) udfDispalyStr =udfDispalyStr+"(Date)";
				else udfDispalyStr = udfDispalyStr+posMapping.getDataType();*/
				
				Label udfDisplayLbl = new Label(udfDispalyStr);
				udfDisplayLbl.setParent(htmlTd);
				htmlTd.setParent(htmlTr);
				
				
				htmlTd = new HtmlNativeComponent("td");
				htmlTd.setDynamicProperty("width", "10px");
				
				Label colenLbl = new Label(Constants.DELIMETER_COLON + "");
				colenLbl.setParent(htmlTd);
				htmlTd.setParent(htmlTr);
				
				htmlTd = new HtmlNativeComponent("td");
				htmlTd.setParent(htmlTr);
				
				if(posMapping.getDataType().startsWith("Date")) {
					String dateFormatStr = posMapping.getDataType();
					dateFormatStr = dateFormatStr.substring(dateFormatStr.indexOf("(")+1, dateFormatStr.indexOf(")"));
					Datebox udfDateBox = new Datebox(); 
					udfDateBox.setFormat(dateFormatStr);
		//			Calendar tempCal = Calendar.getInstance();
					if(contactUdfValue != null && contactUdfValue.trim().length() >0) {
						logger.info("contactUdfValue is ::"+contactUdfValue);
						try {
							SimpleDateFormat sdfSource = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
							Date date = sdfSource.parse(contactUdfValue);
							SimpleDateFormat sdfDestination = new SimpleDateFormat();
							sdfDestination.format(date);
//							logger.info(sdfDestination.format(date));
							udfDateBox.setValue(date);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
						}
					}
					udfDateBox.setParent(htmlTd);
					contactUdfHashTable.put(posMapping.getCustomFieldName(), udfDateBox);
				} else if(posMapping.getDataType().contains("Number")) {
					
					Decimalbox udfdecimalBox = new Decimalbox();
					if(contactUdfValue != null || contactUdfValue.trim().length() > 0) {
						logger.info("contactUdfValue is ::"+contactUdfValue);
						try {
							udfdecimalBox.setValue(contactUdfValue);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
						}
					}
					udfdecimalBox.setParent(htmlTd);
					contactUdfHashTable.put(posMapping.getCustomFieldName(), udfdecimalBox);
				} else {
					Textbox udfTextBox = new Textbox();
					if(contactUdfValue != null || contactUdfValue.trim().length() > 0) {
						udfTextBox.setValue(contactUdfValue);
					}
					udfTextBox.setParent(htmlTd);
					contactUdfHashTable.put(posMapping.getCustomFieldName(), udfTextBox);
				}
				
				htmlTd.setParent(htmlTr);
				htmlTr.setParent(htmlTable);	
				htmlTable.setParent(cfDivId);
				custFieldDivId.setVisible(true);
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	} // setPOSCustFieldUI
	
	
	private String getUdfContactObj(int udfIdx) throws Exception{
		
		String udfValue= "";
			switch(udfIdx) {
			case 1:  return  udfValue = contact.getUdf1();
			case 2:  return  udfValue = contact.getUdf2();
			case 3:  return  udfValue = contact.getUdf3();
			case 4:  return  udfValue = contact.getUdf4();
			case 5:  return  udfValue = contact.getUdf5();
			case 6:  return  udfValue = contact.getUdf6();
			case 7:  return  udfValue = contact.getUdf7();
			case 8:  return  udfValue = contact.getUdf8();
			case 9:  return  udfValue = contact.getUdf9();
			case 10:  return  udfValue = contact.getUdf10();
			case 11:  return  udfValue = contact.getUdf11();
			case 12:  return  udfValue = contact.getUdf12();
			case 13:  return  udfValue = contact.getUdf13();
			case 14:  return  udfValue = contact.getUdf14();
			case 15:  return  udfValue = contact.getUdf15();
			
			}
			return udfValue ;
		
		
	} // getUdfContactObj
	
	public void onClick$updateBtnId(){
		try {
			update();
		} catch (Exception e) {
			logger.error("Exception :: error getting the from the update method ",e);
		}
	}
	
	public void update() throws Exception { 
		try{
			MessageUtil.clearMessage();
           String emailCheck=contact.getEmailId();
           String mobileCheck=contact.getMobilePhone();
         
			String emailId = null;
			boolean isEnableEventTrigger = false;
			
			emailId = emailIdTbId.getValue();
		    String	phoneCheck=phoneLbId.getValue();
		    try {
			if((!emailCheck.equalsIgnoreCase(emailId)) || (!mobileCheck.equalsIgnoreCase(phoneCheck)))
			{
				contact.setModifiedDate(Calendar.getInstance());
			}
		    }
		    catch (Exception e2) {
				// TODO Auto-generated catch block
				logger.error("Exception, this block executed::", e2);
			}
			
			phoneLbId.setStyle("border:1px solid #7F9DB9;");
			emailIdTbId.setStyle("border:1px solid #7F9DB9;");
			pinIbId.setStyle("border:1px solid #7F9DB9;");
			
			if(phoneLbId.getValue().trim().length() == 0 && emailId.trim().length() == 0) {
				MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
				phoneLbId.setStyle("border:1px solid #DD7870;");
				emailIdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
			
			boolean isValid = true;
			
			isValid = Utility.validateEmail(emailId);
//			logger.info("======"+isValid);
			if(emailId.trim().length() >0  && !isValid ){
				MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
				emailIdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
			
			if(phoneLbId.getValue().trim().length() > 0) {
				UserOrganization organization = user!=null ? user.getUserOrganization(): null;
				String phone = Utility.phoneParse(phoneLbId.getValue(),organization);
				if(phone == null || phone.trim().length() == 0) {
					MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
					phoneLbId.setStyle("border:1px solid #DD7870;");
					return;
				}
				/*//contact.setPhone(Long.parseLong(phone));
				//TODO need to set lastSMSdate and mobile optin values to null if it is 
				//enabled with smsDouble-optin and medium is matched  
				
				Users currentUser = contact.getUsers();
				
				performMobileOptIn(contact, currentUser, phone);
				contact.setMobilePhone(phone);
				*/
				
			}/*else {
				contact.setMobilePhone(null);
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			}*/
			
			
			
			
			//Pin number validation
			String countryType = user.getCountryType();
			String value = pinIbId.getValue().trim();
			if(pinIbId.getValue() != null && pinIbId.getValue().trim().length() > 0) {
				
				if(Utility.zipValidateMap.containsKey(countryType)){
					
					boolean zipCode = Utility.validateZipCode(value, countryType);
					 
					 if(!zipCode){
						 
							MessageUtil.setMessage("Please enter valid zip code.","color:red;");
							return ;
							
						}
					
				}else{
					
					if(value != null && value.length() > 0){
						
						try{
							
							Long pinLong = Long.parseLong(value);
							
			      } catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
							return ;
			      }
						
					if(value.length() > 6 || value.length() < 5) {
						
						//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
							return ;
							
						}
					}
				}
				
				/*try {
					String pinStr = ""+Integer.parseInt(pinIbId.getValue());
					if(pinStr.trim().length() >6 || pinStr.trim().length() <5) {
						MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
						pinIbId.setStyle("border:1px solid #DD7870;");
						return;
					}
				} catch (Exception e) {
					MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
					pinIbId.setStyle("border:1px solid #DD7870;");
					logger.error("Exception ::", e);
					return;
				}*/
					
			}
			
			//check combination of Contact exists
			/*if(!(contact.getEmailId().equals(emailId) 
					&& contact.getPhone() == phoneLbId.getValue())) {
			}*/
				
			/*Users users = null;
			if(contact.getUsers().getParentUser() != null) {
				users = contact.getUsers().getParentUser();
			}else {
				users = contact.getUsers();
			}*/
			Users users = contact.getUsers();
			TreeMap<String, List<String>> treeMap = 
					Utility.getPriorityMap(users.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, null);
			logger.info("treeMap ::: "+treeMap);
			
			if(treeMap == null || treeMap.size() == 0) {
				logger.debug("User not having the contacts priority..");
				MessageUtil.setMessage("User does not have the contacts priority.", "color:red", "TOP");
				return ;
			}
			
			
			boolean isUnderAge = false;
			Contacts newContactObj = setContFiled(new Contacts());
			newContactObj.setUsers(users);
			
			boolean purgeFlag = false;
			boolean emailFlag = contact.getPurged();
			String emailStatus = contact.getEmailStatus();
			//Check existency contcats in DB
			Contacts dbContactObj = contactsDao.findContactByUniqPriority(treeMap,newContactObj,users.getUserId());
			Contacts dbContactObjCheck = contactsDao.findContactByUniqPriority(treeMap,newContactObj,users.getUserId());
			
			
			if(dbContactObj != null && 
					dbContactObj.getContactId().longValue() != contact.getContactId().longValue()) {
				
				if(contactParentalConsent != null) {
					//need to merge all the data to the new contact and delete this under age contact
					//update this contact's cid to consent object
					dbContactObj = Utility.mergeContacts(newContactObj, dbContactObj);
					if(newContactObj.getBirthDay()==null) {
						dbContactObj.setBirthDay(null);
					}
					if(newContactObj.getAnniversary()==null) {
						dbContactObj.setAnniversary(null);
					}
					contactParentalConsent.setContactId(dbContactObj.getContactId());
					isUnderAge = true;
					
					//this is require because if the dbContactObj is already deleted and to assign a mlList.
					long underAgeConMlBits = contact.getMlBits();
					//Set<MailingList> underAgeConMlSet = contact.getMlSet();
					contactsDaoForDML.delete(contact);
					contact = dbContactObj;
					
					//need this reasign as 'contact' reference has modified
					emailFlag = contact.getPurged();
					emailStatus = contact.getEmailStatus();
					
					if(contact.getMlBits().longValue() == 0l) {
						//if deleted earlier
						//Set<MailingList> newMlset = new HashSet<MailingList>();
						//newMlset.addAll(underAgeConMlSet);
						//contact.setMlSet(newMlset);
						contact.setMlBits(underAgeConMlBits);
						contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
						emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
						emailFlag = false;
						purgeFlag = true;
						
						if(checkEvenTriggerEnabled(users)) {
							
							isEnableEventTrigger = true;	
						}
						
						
						
						
						
					}
					
				}//if
				else{
					
					if(dbContactObj.getMlBits().longValue() == 0l) {
						
						//deleted dummy contact and no longer useful,so delete it perminantly
						//and the data coming is latest so continue with other merging operations below
						contactsDaoForDML.delete(dbContactObj);
						
						
						
					}
					else{
					
						//with entered data having two different objects here so return without performing any merge
						MessageUtil.setMessage("Contact already exists, " +
								"so contact cannot be updated.", "color:red", "TOP");
						return ;
					}
				}
				
			}
			
			
			//String mobileStatus = contact.getMobileStatus();
			if((contactParentalConsent != null && newContactObj.getEmailId() == null) || 
					(contact.getEmailId() == null  && newContactObj.getEmailId() == null )) {
				
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
				
			}
			
			if((contact.getEmailId() != null && newContactObj.getEmailId() != null &&
					! contact.getEmailId().equalsIgnoreCase(newContactObj.getEmailId())
					)||(contact.getEmailId() == null && newContactObj.getEmailId() != null) ) {
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
			}
			
			

			//************perform mobile optin***********************//   
			//perform mobile status
			
			
			try {
				if(user.isEnableSMS() && user.isConsiderSMSSettings())   {
					logger.info("-------------------------user is enabled with sms--------");
					performMobileOptIn(newContactObj, false, contact);
					
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				logger.error("Exception, this block executed::", e2);
			}
			
			if(!isUnderAge) {
				contact = Utility.mergeContacts(newContactObj, contact);
				
				if(newContactObj.getBirthDay()==null) {
					contact.setBirthDay(null);
				}
				if(newContactObj.getAnniversary()==null) {
					contact.setAnniversary(null);
				}
				//APP-1285
				/*if(contact.getMobilePhone() == null) {
					
					contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}*/
				
			}
			//APP-2597
			if(contact.getMobilePhone() != null && newContactObj.getMobilePhone()!=null && !newContactObj.getMobilePhone().isEmpty() && 
					contact.getMobilePhone().equals(newContactObj.getMobilePhone()))
				newContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE); //because we are using newContactObj as final mobile status. 
				
				
			//logger.debug("got contacts ::"+contact.getMobileStatus());
			contact.setPurged(emailFlag);
			contact.setEmailStatus(emailStatus);
			//contact.setMobileStatus(mobileStatus);
			contact.setMobilePhone(newContactObj.getMobilePhone());
			//APP-1285
				if(contact.getMobilePhone() == null) {
					
					contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			logger.info("Mobile Status==>"+newContactObj.getMobileStatus());
			if(newContactObj.getMobileStatus()!=null)
			contact.setMobileStatus(newContactObj.getMobileStatus());
			if(purgeFlag) {
				
				PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
				purgeList.checkForValidDomainByEmailId(contact);
				
			}
			
			
			int confirm;
			try {
				confirm = Messagebox.show("Are you sure you want to save the contact?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					try {
						
						try {
//							contactsDao.saveOrUpdate(contact);
							logger.info("Mobile Status==>"+contact.getMobileStatus());
							//Modified Date 
							if(Utility.isModifiedContact(contact, dbContactObjCheck)){
								logger.info("entered Modified date");
							contact.setModifiedDate(Calendar.getInstance());
							}
							contactsDaoForDML.saveOrUpdate(contact);
							LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, contact.getMobilePhone());
							LoyaltyProgramHelper.updateLoyaltyEmailId(contact, contact.getEmailId());
							if(isEnableEventTrigger) {
								
								//find is this contact exist in the trigger configured list or not
								
								eventTriggerEventsObservable.notifyToObserver(activatedTriggersList,
										contact.getContactId(), contact.getContactId(), users.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);
								
							}//if
							

							
							
							
							
						} catch (Exception e) {
							
							logger.error(">>> Exception occured while updating the contact..");
							MessageUtil.setMessage("Contact update failed. ","color:RED","TOP");
						}
						
						if(contactParentalConsent != null) {
							
							contactParentalConsent.setContactEmail(contact.getEmailId());
							contactParentalConsent.setChildFirstName(contact.getFirstName());
							contactParentalConsent.setChildDOB(contact.getBirthDay());
							ContactParentalConsentDao contactParentalConsentDao = (ContactParentalConsentDao)SpringUtil.getBean("contactParentalConsentDao");
							ContactParentalConsentDaoForDML contactParentalConsentDaoForDML = (ContactParentalConsentDaoForDML)SpringUtil.getBean("contactParentalConsentDaoForDML");
							
							//contactParentalConsentDao.saveOrUpdate(contactParentalConsent);
							contactParentalConsentDaoForDML.saveOrUpdate(contactParentalConsent);
							
							/*if(contact.getMailingList().isCheckWelcomeMsg() && !contact.getMailingList().getCheckDoubleOptin() ) {
								 
								 sendWelcomeEmail(contact, contact.getMailingList().getWelcomeCustTempId(), GetUser.getUserObj());
								 
							}*/
							MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
							List<MailingList> conMlList = mailingListDao.findByContactBit(contact.getUsers().getUserId(), contact.getMlBits()); 
							//Iterator<MailingList> mlIt = contact.getMlSet().iterator();
							Iterator<MailingList> mlIter = conMlList.iterator();
							MailingList tempMl=null;
							while(mlIter.hasNext()) {
								tempMl = mlIter.next();
								if(tempMl.isCheckWelcomeMsg() && !tempMl.getCheckDoubleOptin() ) {
									 sendWelcomeEmail(contact, tempMl.getWelcomeCustTempId(), GetUser.getUserObj());
									 
								}
							}
							
						}//if
						
						session.removeAttribute("isUnderAge");
						logger.debug("email saved successfully ");
						logger.info("Mobile status2===> "+contact.getMobileStatus());
						MessageUtil.setMessage("Contact updated successfully.","color:blue","TOP");
						Redirect.goToPreviousPage();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
					
					
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e1);
			}
			
			
			
			
		
		}catch(Exception e){
			logger.error("Exception : ", e);
		}
	}
	
	private boolean checkEvenTriggerEnabled(Users currentUser) {
		try{
		if(eventTriggerDao == null) {
			
			eventTriggerEventsObservable = (EventTriggerEventsObservable)SpringUtil.getBean("eventTriggerEventsObservable");
			eventTriggerEventsObserver = (EventTriggerEventsObserver)SpringUtil.getBean("eventTriggerEventsObserver");
			
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			eventTriggerDao = (EventTriggerDao)SpringUtil.getBean("eventTriggerDao");
			
			
			
		}
		
		 activatedTriggersList = eventTriggerDao.findAllUserAutoRespondTriggers(currentUser.getUserId(), 
					
					Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED);

		boolean  isActivatedTrigger = (activatedTriggersList != null && activatedTriggersList.size() > 0);
		
		 return isActivatedTrigger;
		}catch(Exception e) {
			
			return false;
		}
		
		
	}
	
	
	
	
	private Contacts setUDFContactObj(Contacts contact, int udfIdx, String udfValue) {
		
		if(udfValue == null || udfValue.trim().length() == 0 ) return contact;
		logger.info("udfIdx "+udfIdx+" >>> udfValue is  ::"+udfValue);
		switch(udfIdx) {
		case 1:	contact. setUdf1(udfValue); return contact;
		case 2:	contact. setUdf2(udfValue); return  contact;
		case 3: contact.setUdf3(udfValue); return  contact;
		case 4: contact.setUdf4(udfValue);return  contact;
		case 5: contact.setUdf5(udfValue);return  contact;
		case 6: contact.setUdf6(udfValue);return  contact;
		case 7: contact.setUdf7(udfValue);return  contact;
		case 8: contact.setUdf8(udfValue);return  contact;
		case 9: contact.setUdf9(udfValue);return  contact;
		case 10: contact.setUdf10(udfValue);return  contact;
		case 11: contact.setUdf11(udfValue);return  contact;
		case 12: contact.setUdf12(udfValue);return  contact;
		case 13: contact.setUdf13(udfValue);return  contact;
		case 14: contact.setUdf14(udfValue);return  contact;
		case 15: contact.setUdf15(udfValue);return  contact;
		
		}
		return contact;
	}
	
	
	
	public void onClick$backBtnId() {
		try {
			MessageUtil.clearMessage();
			session.removeAttribute("editcontact");
			Redirect.goToPreviousPage();
			//Redirect.goTo("/contact/contacts");
		} catch (Exception e) {
			logger.error("Exception :: error getting while redirecting the page",e);
		}

	}

	public void sendWelcomeEmail(Contacts contact,  Long templateId, Users user) {
		
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao"); 
		EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");

		//to send the loyalty related email
		 CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(templateId != null) {
			  
			  custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			  if(custTemplate != null) {
				  if(custTemplate != null && custTemplate.getHtmlText()!= null) {
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null) message = myTemplates.getContent();
				  }
			  }
		  }
		  //logger.debug("-----------email----------"+tempContact.getEmailId());
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(templateId,Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, MyCalendar.getNewCalendar(), " Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
			
		
		
	}//sendWelcomeEmail
	
	private Contacts setContFiled(Contacts contact){
		
		if(rProTbId.getValue().trim().length() != 0) contact.setExternalId(rProTbId.getValue().trim());
		if(homeStoreTbId.getValue().trim().length() != 0) contact.setHomeStore(homeStoreTbId.getValue().trim());
		
		
		if(addOneTbId.getValue().trim().length() != 0) 	contact.setAddressOne(addOneTbId.getValue().trim());
		if(addTwoTbId.getValue().trim().length() != 0) contact.setAddressTwo(addTwoTbId.getValue().trim());
		
		if(cityTbId.getValue().trim().length() != 0) contact.setCity(cityTbId.getValue().trim());
		if(countryTbId.getValue().trim().length() != 0)contact.setCountry(countryTbId.getValue().trim());
		if(stateTbId.getValue().trim().length() != 0) 	contact.setState(stateTbId.getValue().trim());
		if(firstNameTbId.getValue().trim().length() != 0) contact.setFirstName(firstNameTbId.getValue().trim());
		if(lastNameTbId.getValue().trim().length() != 0)	contact.setLastName(lastNameTbId.getValue().trim());
		if(emailIdTbId.getValue().trim().length() != 0)	contact.setEmailId(emailIdTbId.getValue().trim());
		 
		if(pinIbId.getValue().trim().length() > 0) 	contact.setZip(""+pinIbId.getValue().trim());
		
		if(phoneLbId.getValue().trim().length() > 0) 	{
			UserOrganization organization = user!=null ? user.getUserOrganization(): null;
			String phoneStr= Utility.phoneParse(phoneLbId.getValue().trim(),organization);
			if(phoneStr != null){
			contact.setMobilePhone(phoneStr);
			//contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//APP-2597
			}
		}
			
		
		//Gender
		if(gendarListBxId.getSelectedIndex() > 0) {
			contact.setGender(gendarListBxId.getSelectedItem().getLabel());
		}
		
		
		//Birthday
		if(birthdayDateBxId.getValue()!=null) {
			MyCalendar tempCal= (MyCalendar)birthdayDateBxId.getClientValue();
			contact.setBirthDay(tempCal);
		}
		//Anniversary
		if( anniversaryDateBxId.getValue()!=null) {
			MyCalendar tempCal= (MyCalendar)anniversaryDateBxId.getClientValue();
			contact.setAnniversary(tempCal);
		}
		
		if(contactUdfHashTable!= null && contactUdfHashTable.size() >0) {
			logger.info("contactUdfHashTable ::: "+contactUdfHashTable);
			Set<String> udfKeySet = contactUdfHashTable.keySet();
			
			for (String eachKey : udfKeySet) {
				
				int i = Integer.parseInt(eachKey.substring("UDF".length()));
				if(!contactUdfHashTable.containsKey("UDF"+i)) continue;
				
				if(contactUdfHashTable.get("UDF"+i) instanceof Textbox) {
					Textbox tempTextbox = (Textbox)contactUdfHashTable.get("UDF"+i);
					if(tempTextbox.getValue().trim().length() == 0) continue;
					logger.info(i+"tempTextbox getValue is  ::"+tempTextbox.getValue());
					
					setUDFContactObj(contact, i, tempTextbox.getValue());
					
				}else if(contactUdfHashTable.get("UDF"+i) instanceof Decimalbox){
					Decimalbox tempDecbox = (Decimalbox)contactUdfHashTable.get("UDF"+i);
					if(tempDecbox.getValue() == null) continue;
					setUDFContactObj(contact, i, ""+tempDecbox.getValue());
					
				}else if(contactUdfHashTable.get("UDF"+i) instanceof Datebox){
					Datebox tempDatebox = (Datebox)contactUdfHashTable.get("UDF"+i);
					if(tempDatebox.getValue() == null) continue;
					
					Calendar tempCal =Calendar.getInstance();
					logger.info("tempDatebox getValue is  ::"+tempDatebox.getValue());
					tempCal.setTime(tempDatebox.getValue());
					setUDFContactObj(contact, i, MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_DATETIME_STYEAR));
				}
				
			} // for
			
			/*for(int i=0; i<= contactUdfHashTable.size(); i++) {
				logger.info("i Value is ::: "+i);
				
			}*/
		
		}
		
		return contact;
	} // setContFiled
	
	
@SuppressWarnings("unused")
public void performMobileOptIn(Contacts inputContactObj, boolean isNew, Contacts existingContact) throws BaseServiceException{
		
	SMSSettingsDao smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
	//SMSSettings smsSettings = smsSettingsDao.findByUser(existingContact.getUsers().getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
	SMSSettings smsSettings = null;
	Users user = inputContactObj.getUsers();
	if(SMSStatusCodes.smsProgramlookupOverUserMap.get(user.getCountryType())) smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
	else  smsSettings = smsSettingsDao.findByOrg(user.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
	
	
	if(smsSettings == null) {
		
		String noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
								"SMS may not be sent to the mobile contacts.";
	
	
		Messages messages = new Messages("Contact" ,"Mobile contacts may not reachable" ,noSMSComplaincyMsg ,
				Calendar.getInstance(),"Inbox",false ,"Info", existingContact.getUsers()); 
		
		MessagesDao messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
		
		//messagesDao.saveOrUpdate(messages);
		messagesDaoForDML .saveOrUpdate(messages);
		return ;
	
	}
	
	OCSMSGateway ocGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), 
			SMSStatusCodes.defaultSMSTypeMap.get(smsSettings.getUserId().getCountryType()));
	
	if(ocGateway == null) {
		
			
		String noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
								"SMS may not be sent to the mobile contacts.";
	
	
		Messages messages = new Messages("Contact" ,"Mobile contacts may not reachable" ,noSMSComplaincyMsg ,
				Calendar.getInstance(),"Inbox",false ,"Info", existingContact.getUsers()); 
		
		MessagesDao messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
		
		//messagesDao.saveOrUpdate(messages);
		messagesDaoForDML.saveOrUpdate(messages);
		
		
		contact.setMobileOptin(false);
		contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
		return;
		
		
	}
	
	
	
		String optinMedium = null;
		
		if(inputContactObj.getMobilePhone() != null && !inputContactObj.getMobilePhone().trim().isEmpty()) {
			
			//if
			boolean isDifferentMobile = false;
			String mobile = inputContactObj.getMobilePhone();
			
			if(existingContact != null) {
				
				String conMobile = existingContact.getMobilePhone();
				optinMedium = existingContact.getOptinMedium();
			//to identify whether entered one is same as previous mobile
				if(conMobile != null ) {
					
					if(!mobile.equals(conMobile) ) {
						
						if( (mobile.length() < conMobile.length() && !conMobile.endsWith(mobile) ) ||
								(conMobile.length() < mobile.length() && !mobile.endsWith(conMobile)) || (mobile.length() == conMobile.length())) {
							
							isDifferentMobile = true;
							
						}//if
						
						
					}//if
					
				}//if
			
				else{
					existingContact.setMobilePhone(inputContactObj.getMobilePhone());
					isDifferentMobile = true;
					
				}
				
			}//if
			else{
				
				optinMedium = inputContactObj.getOptinMedium();
				
				
			}
			//contact.setPhone(mPhoneIBoxId.getValue());
			Users currentUser = GetUser.getUserObj();//inputContactObj.getUsers();
			boolean canProceed = false;
			//do only when the existing phone number is not same with the entered
			byte optin = 0;
			if(optinMedium != null) {
				
				if(optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
					optin = 1;
				}
				else if(optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
					optin = 2;
				}
				else if(optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
					optin = 4;
				}
				
			}//if
			
			Users contactOwner = contact.getUsers();
			Byte userOptinVal =	smsSettings.getOptInMedium();
			
			userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
					contactOwner.getOptInMedium() : userOptinVal;
			
			if(smsSettings.isEnable() && 
					userOptinVal != null && 
					(userOptinVal.byteValue() & optin ) > 0  ) {
										
				
				if( (existingContact != null && 
						(existingContact.getLastSMSDate() == null && existingContact.isMobileOptin() != true) ||
						( isDifferentMobile) )  ) {//this is if the mobile is changed or if it has not sent msg earlier while adding
					
						existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
						existingContact.setLastSMSDate(Calendar.getInstance());
						existingContact.setMobileOptin(false);
						canProceed = true;
						
					
					
				}
				if(canProceed || isNew) {	
					
					/*inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
					inputContactObj.setMobileOptin(false);
					*/
					
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)SpringUtil.getBean("captiwayToSMSApiGateway");
					
					if(!ocGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
						
						logger.debug("low credits with clickatell");
						return;
					}
					
					if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
						
						UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
						UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
						
						String msgContent = smsSettings.getAutoResponse();
						if(msgContent != null) {
							if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())){
								
								msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
							}
							//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader() + " "+ msgContent;
						}
						
						//clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobile, msgContent);
						/*String mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocGateway, PropertyUtil.
												getPropertyValueFromDB(Constants.SMS_SENDERID), mobile, msgContent);
						*/
						String mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocGateway, 
								smsSettings.getSenderId(), mobile, msgContent, smsSettings.getUserId());
						if(mobileStatus == null) {
							
							mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
						}
						if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
							
							contactsDaoForDML.updatemobileStatus(mobile, mobileStatus, currentUser);
							
						}
						
						if(canProceed) {
							
							existingContact.setMobileStatus(mobileStatus);
						}
						if(isNew) {
							
							inputContactObj.setMobileStatus(mobileStatus);
						}
						
						
						
						/*if(currentUser.getParentUser() != null) {
							currentUser = currentUser.getParentUser();
						}*/
						/*currentUser.setUsedSmsCount((currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount())+1);
						usersDao.saveOrUpdate(currentUser);*/
						
						//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
						usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
						
						/**
						 * Update SmsQueue
						 */
						SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
						smsQueueHelper.updateSMSQueue(mobile, msgContent, Constants.SMS_MSG_TYPE_OPTIN, currentUser,smsSettings.getSenderId());
					}else {
						logger.debug("low credits with user...");
						return;
						
					}
					
				}//if
			}//if
			else {
				
				if(existingContact != null ) {
					
					if(existingContact.getMobilePhone() != null && isDifferentMobile){
						
						existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
						existingContact.setMobileOptin(false);
						
					}
					
				}//if existing contact
				else {
					
					if(inputContactObj.getMobilePhone() != null ){
						
						inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
						inputContactObj.setMobileOptin(false);
						
					}
					
					
					
				}//if is new contact
				
			}//else
			
		}
		
		
		//logger.debug("got contacts ::"+existingContact.getMobileStatus());
		
		
	}//performMobileOptIn
	
	
	//this is the previous code before such merge logic 
	/*public void performMobileOptIn(Contacts contact, Users currentUser, String mobile) {
	
	SMSSettingsDao smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
	SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId());
	//do only when the existing phone number is not same with the entered
	byte optin = 0;
	if(contact.getOptinMedium() != null) {
		
		if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
			optin = 1;
		}
		else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
			optin = 2;
		}
		else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
			optin = 4;
		}
	}
	
	
	boolean isDifferentMobile = false;
	String conMobile = contact.getMobilePhone();
	//to identify whether entered one is same as previous mobile
	if(conMobile != null ) {
		if(mobile.length() != conMobile.length() ) {
			
			if( (mobile.length() < conMobile.length() && !conMobile.endsWith(mobile) ) ||
					(conMobile.length() < mobile.length() && !mobile.endsWith(conMobile))) {
				
				isDifferentMobile = true;
				
			}
			
			
		}
		
	}
	
	if(smsSettings.isEnableOptInMessage() &&					
			smsSettings.getOptInMedium() != null && 
			smsSettings.getOptInMedium().byteValue() >= optin) {
		//TODO after the above todo done consider only one among these two conditions on contact
		if( (contact.getLastSMSDate() == null && contact.isMobileOptin() != true) ||
				(contact.getLastSMSDate() != null && isDifferentMobile) ) {
		
			contact.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
			contact.setLastSMSDate(Calendar.getInstance());
			//contact.setMobileOptin(true);
			
			ClickaTellApi clickaTellApi = (ClickaTellApi)SpringUtil.getBean("clickaTellApi");
			
			if(!clickaTellApi.getBalance(1)) {
				
				logger.debug("low credits with clickatell");
				return;
			}
			
			if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
				
				UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
				
				String msgContent = smsSettings.getWelcomeMessage();
				if(msgContent != null) {
					
					msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
				}
				
				clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobile, msgContent);
				if(currentUser.getParentUser() != null) {
					currentUser = currentUser.getParentUser();
				}
				currentUser.setUsedSmsCount(currentUser.getUsedSmsCount()+1);
				usersDao.saveOrUpdate(currentUser);
			}else {
				logger.debug("low credits with user...");
				return;
				
			}
			
		}//if
	}//if
	
	else{
		
		if(contact.getMobileStatus() == null){
			
			contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
			
		}
		contact.setMobileOptin(false);
		
		
	}
	
	
	
	
	
}
*/
	
	
}
