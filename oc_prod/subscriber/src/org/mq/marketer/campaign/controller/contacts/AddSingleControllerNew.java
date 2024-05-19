package org.mq.marketer.campaign.controller.contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
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
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

public class AddSingleControllerNew extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	
	private ContactsDao contactDao;
	private ContactsDaoForDML contactsDaoForDML;
//	private MLCustomFieldsDao mlCustomFieldsDao;
//	private CustomFieldDataDao cfDataDao;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private String isNew;
	private Users currentUser;
//	private UserActivitiesDao userActivitiesDao;
//	private Include xcontents = null;
	private Set<MailingList> mailingLists = null;
	private Label listNameLabeId,udfFieldLblId,statusLabelId,statusLabelId1;
	
	private Div udfDivId;
	private Rows rowsId,custGridRowsId;
	private A showAllAnchorId;
	
	private EventTriggerEventsObservable eventTriggerEventsObservable;
	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerDao eventTriggerDao;
	private boolean isActivatedTrigger;
	private List<EventTrigger> activatedTriggersList;
	private Label popupLbId;
	
	public AddSingleControllerNew() {	
		session = Sessions.getCurrent();
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		contactDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
		//****************FOR AUTO RESPONDER TYPE EVENT TRIGGERS*************************
		eventTriggerEventsObservable = (EventTriggerEventsObservable)SpringUtil.getBean("eventTriggerEventsObservable");
		eventTriggerEventsObserver = (EventTriggerEventsObserver)SpringUtil.getBean("eventTriggerEventsObserver");
		
		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
		eventTriggerDao = (EventTriggerDao)SpringUtil.getBean("eventTriggerDao");
		//activatedTriggersList = new ArrayList<EventTrigger>();
		//**************END************************
		currentUser = GetUser.getUserObj();
		isNew = (String)session.getAttribute("isNewML");
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
	}
	
	private Set<Long> listIdsSet; 
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		mailingLists = (Set)session.getAttribute("AddSingle_Ml");
		
		if(mailingLists == null || mailingLists.size() == 0) {
			logger.debug(" no mailing list found for adding the new Contacts ..");
			return;
		}
		
		String listNameStr = "";
		
		for(Object obj:mailingLists) {
			
			MailingList mailingList = (MailingList)obj;
			if(listNameStr.length() < 1) {
				listNameStr = mailingList.getListName();
				continue;
			}
			listNameStr += ", "+mailingList.getListName();
		} // for
		
		//display ListName
		listNameLabeId.setValue(listNameStr);
		logger.info((currentUser.getUserOrganization().getMinNumberOfDigits() == currentUser.getUserOrganization().getMinNumberOfDigits())==true);
		String numOfDigits = ((currentUser.getUserOrganization().getMinNumberOfDigits() == currentUser.getUserOrganization().getMaxNumberOfDigits()))==true 
								? currentUser.getUserOrganization().getMinNumberOfDigits()+"" : currentUser.getUserOrganization().getMinNumberOfDigits()+"-"+currentUser.getUserOrganization().getMaxNumberOfDigits(); 
		popupLbId.setValue("just "+numOfDigits+" digit number, we will add country carrier.");
		//Save the new Mailing list
		if(isNew != null && isNew.equals("true")) { 
//			addCustDivId.setVisible(true);//TODO
			MailingList mailingList = (MailingList)mailingLists.iterator().next();
			
			mailingListDaoForDML.saveOrUpdate(mailingList); 
			
			listIdsSet.add(mailingList.getListId());
			session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
			
		}
	
		
		 activatedTriggersList = eventTriggerDao.findAllUserAutoRespondTriggers(currentUser.getUserId(), 
											
											Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED);
		
		isActivatedTrigger = (activatedTriggersList != null && activatedTriggersList.size() > 0);
		//logger.info("is activated trigger ::"+isActivatedTrigger);
		setDefaulMapField();
		
		
	} // doAfterCompose	
	
	
	POSMappingDao posMappingDao = null;
	private void setDefaulMapField() {
		
		List<POSMapping> contMapList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", currentUser.getUserId());
		
		if(contMapList == null || contMapList.size()== 0) {
			logger.debug("No Mapping list have this user return..");
			return;
		}
		
//		custGridRowsId
		for (POSMapping posMapping : contMapList) {
			
			if (!posMapping.getCustomFieldName().startsWith("UDF")) { continue;	}
			Row udfRow = new Row();
			
			String dataTypeStr = posMapping.getDataType();
			String dateFormat = "";
			
			if(dataTypeStr.startsWith("Date")) {
				dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
				dataTypeStr = "Date";	
			}
			
			//Label with data type
			Label tempLbl = new Label();
			tempLbl.setValue(posMapping.getDisplayLabel()+"("+dataTypeStr+")");
			tempLbl.setParent(udfRow);
			tempLbl.setAttribute("DB_UDF_INDX", posMapping.getCustomFieldName());
			
			
			//Input Value
			if(dataTypeStr.startsWith("Date")) {
				Datebox udfDBox = new Datebox();
				udfDBox.setReadonly(true);
				udfDBox.setParent(udfRow);
				udfDBox.setAttribute("FORMAT", dateFormat);
			}else  {
				Textbox tempTxtBx = new Textbox();
				tempTxtBx.setParent(udfRow);
				tempTxtBx.setAttribute("FORMAT", dataTypeStr);
			}
			
			
			
			A anchorTag = new A();
			anchorTag.setLabel("Hide");
			anchorTag.addEventListener("onClick", this);
			anchorTag.setStyle("color:#2886B9;font-size:12px;font-weight:bold;");
			anchorTag.setParent(udfRow);
			udfRow.setHeight("35px");
			udfRow.setParent(custGridRowsId);
			udfRowSize ++;
			
		}
		
		//set optional values for gender
		String[] splitArray= null;
		for (POSMapping posMapping : contMapList) {
			if (posMapping.getCustomFieldName().equals("Gender")) {
				if(posMapping.getOptionalValues()!=null) {
					splitArray = posMapping.getOptionalValues().split(Constants.ADDR_COL_DELIMETER);
				}
			}
		}
		if (splitArray != null) {
			
			for(String optionalValue : splitArray) {
				Listitem gender = new Listitem(optionalValue);
				gender.setParent(gendarListBxId);
			}
		}
		
	} // setDefaulMapField
	
	
	
	
	public void onClick$showAllAnchorId() {
		try {
//			showAll(rowsId);
			

			List li=(List)rowsId.getChildren();
			
			// Display default fields of Mailing list
			for(Object obj : li) {
				Row row =(Row)obj;
				row.setVisible(true);
			}
			// Display UDF fields , if exist.
			if(custGridRowsId.getChildren() != null && custGridRowsId.getChildren().size() > 0) {
				//customFieldSize=custTBList.size();
				udfFieldLblId.setVisible(true);
				udfDivId.setVisible(true);
				
				for(Object obj:custGridRowsId.getChildren()){
								
					Row row= (Row)obj;
					row.setVisible(true);
					udfRowSize++;
				}
//				custFieldDivId.setVisible(true);
//				udfRowSize=custTBList.size();
				showAllAnchorId.setVisible(false);
				return;
			}	// if(custTBList != null)
		
			
			
		} catch (Exception e) {
			logger.error("Exception ::error occured while showing the all rows in grid :::",e);
		}
		
	}
	
	
	public void onClick$saveBtnId() {
		
		/*if(validationOfCont()== false) return ;
		
		if(validationFields()== false) return ;
		if(saveContactInDb()== false) return;*/
		
		Redirect.goTo(PageListEnum.CONTACT_LIST_VIEW);
		
	} // onClick$saveBtnId
	
	public void onClick$saveAndContBtnId() {
		
		try {
			if(validationOfCont()== false) return ;
			
			if(validationFields()== false) return ;
//		saveContactInDb();
			
			if(saveContactInDb()== false) return ;
			
			//clear the Field Values
			clearFieldValue();
			Redirect.goTo(PageListEnum.EMPTY);
	        Redirect.goTo(PageListEnum.CONTACT_ADDSINGLE_NEW);
	        //if(true)return;
//			firstNameTbId.setFocus(true);
//			Clients.scrollTo(0, 0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ::", e);
		}
	} // onClick$saveAndContBtnId
	
	private void clearFieldValue() {
		retailProTextBxId.setValue("");
		homeStoreTxtBxId.setValue("");
		addOneTBoxId.setValue("");
		addTwoTBoxId.setValue("");
		cityTBoxId.setValue("");
		stateTBoxId.setValue("");
		countryTBoxId.setValue("");
		pinIBoxId.setValue("");
		firstNameTbId.setValue("");
		lastNameTbId.setValue("");
		emailTBoxId.setValue("");
		statusLabelId.setValue("");
		mPhoneIBoxId.setValue("");
		subsidiaryTbId.setValue("");
		gendarListBxId.setSelectedIndex(0);
		birthDayDateBoxId.setValue((Date)null);
		anniversaryDateBoxId.setValue((Date)null);
		
		if(isNew.equalsIgnoreCase("true")){
			isNew = "false";
			/*session.setAttribute("isNewML","false");
			xcontents.invalidate();
			Redirect.goTo("contact/AddSingle");*/
		}
		udfRowSize = 0;
		
		List udfchaildrowList = custGridRowsId.getChildren();
		
		if(udfchaildrowList== null || udfchaildrowList.size() == 0) return ;
		
		Row udfRow = null;
		
		for (Object object : udfchaildrowList) {
			
			udfRow = (Row)object;
			List tempList = udfRow.getChildren();
			String dispLbl = ((Label)tempList.get(0)).getValue();
			
			if(dispLbl.endsWith("(Date)")) {
				Datebox tempDBx = ((Datebox)tempList.get(1));
				tempDBx.setValue(null);
			}else  {
				Textbox tempTxtBx = ((Textbox)tempList.get(1));
				tempTxtBx.setValue("");
			}
			
		} // for
			
	} // clearFieldValue()
	
	
	
	public void onClick$backBtnId() {
		try {
			PageUtil.goToPreviousPage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception error occured while redircting the page >>>",e);
		}
	}
	
	private boolean validationOfCont() {

		
		try {
			//emailId validation	
			String emailStr = emailTBoxId.getValue().trim();
			
			if (emailStr.trim().equals("") && mPhoneIBoxId.getValue().trim().length()  == 0) {
				MessageUtil.setMessage("Please provide either email address or phone number before saving contact.", "color:red", "TOP");
				return false;
			}
		
			if(emailStr.length() >0 && !Utility.validateEmail(emailStr)) {
		//					submitBtnId.setDisabled(true);
					statusLabelId.setStyle("color:red");
					statusLabelId.setValue("Please enter a valid e-mail address");
					return false;
			} 
			//String reg = "[0-9][+-'']";
			/*UserOrganization userOrganization = currentUser.getUserOrganization();
			if(mPhoneIBoxId.getValue().trim().length() > 0  && 
					( (mPhoneIBoxId.getValue().trim().length() >= userOrganization.getMinNumberOfDigits()) && (mPhoneIBoxId.getValue().trim().length() <= userOrganization.getMaxNumberOfDigits()))
					 ) {
				//					submitBtnId.setDisabled(true);
							logger.error("Problem in validating phone number");
							statusLabelId1.setStyle("color:red");
							statusLabelId1.setValue("Please enter a valid 10 digit phone number");
							return false;
					} 
			statusLabelId1.setValue("");*/
			
			String phone = null;
			UserOrganization userOrganization = currentUser != null ? currentUser.getUserOrganization() : null ;
			/*if(mPhoneIBoxId.getValue() != null && mPhoneIBoxId.getValue().trim().length() >0 && mPhoneIBoxId.getValue().trim().length() > 0  && 
					( (mPhoneIBoxId.getValue().trim().length() >= userOrganization.getMinNumberOfDigits()) && (mPhoneIBoxId.getValue().trim().length() <= userOrganization.getMaxNumberOfDigits()))
					 ) {
				phone = Utility.phoneParse(""+mPhoneIBoxId.getValue(), userOrganization);
				if(phone == null || phone.length() == 0 ) {
		//			logger.debug("Invalid Phone Number");
					MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
					return false;
				}
			}
			else{
				String numOfDigits = ((currentUser.getUserOrganization().getMinNumberOfDigits() == currentUser.getUserOrganization().getMaxNumberOfDigits()))==true 
						? currentUser.getUserOrganization().getMinNumberOfDigits()+"" : currentUser.getUserOrganization().getMinNumberOfDigits()+"-"+currentUser.getUserOrganization().getMaxNumberOfDigits(); 

				statusLabelId1.setStyle("color:red");
				statusLabelId1.setValue("Please enter a valid "+numOfDigits+" digit phone number");
				return false;
			
			}*/
			
			if(mPhoneIBoxId.getValue() != null && mPhoneIBoxId.getValue().trim().length() >0){
				
				String countryCarrier  = GetUser.getUserObj().getCountryCarrier()+Constants.STRING_NILL;
				String tempMobileNumber = mPhoneIBoxId.getValue().trim();
			
				phone = Utility.phoneParse(tempMobileNumber, userOrganization);
				logger.info("return value by phone parse"+phone+"userOrganization.getMinNumberOfDigits()..:"+userOrganization.getMinNumberOfDigits()+"userOrganization.getMaxNumberOfDigits()..:"+userOrganization.getMaxNumberOfDigits());
				
				
				if(phone == null || phone.length() == 0 ) {
					logger.debug("Invalid Phone Number"+phone);
					MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
					return false;
				}
				else{
					if(userOrganization.isRequireMobileValidation()){
					if(phone.startsWith(countryCarrier)){

					if((phone.trim().length() == (userOrganization.getMinNumberOfDigits()))
							 && (phone.trim().length() < userOrganization.getMaxNumberOfDigits()+countryCarrier.length())){
						return true;
					}
						if((phone.trim().length() < (userOrganization.getMinNumberOfDigits()+countryCarrier.length())) || 
								((phone.trim().length() > userOrganization.getMaxNumberOfDigits()+countryCarrier.length()))){

							String numOfDigits = ((currentUser.getUserOrganization().getMinNumberOfDigits() == currentUser.getUserOrganization().getMaxNumberOfDigits()))==true 
									? currentUser.getUserOrganization().getMinNumberOfDigits()+"" : currentUser.getUserOrganization().getMinNumberOfDigits()+countryCarrier.length()+"-"+(currentUser.getUserOrganization().getMaxNumberOfDigits()+countryCarrier.length());
							statusLabelId1.setStyle("color:red");
							statusLabelId1.setValue("Please enter a valid "+numOfDigits+" digit phone number");
							return false;
						
						}
					}
					else{
						if((phone.trim().length() < userOrganization.getMinNumberOfDigits()) || 
								(phone.trim().length() > userOrganization.getMaxNumberOfDigits())){

							String numOfDigits = ((currentUser.getUserOrganization().getMinNumberOfDigits() == currentUser.getUserOrganization().getMaxNumberOfDigits()))==true 
									? currentUser.getUserOrganization().getMinNumberOfDigits()+"" : currentUser.getUserOrganization().getMinNumberOfDigits()+"-"+currentUser.getUserOrganization().getMaxNumberOfDigits();
							statusLabelId1.setStyle("color:red");
							statusLabelId1.setValue("Please enter a valid "+numOfDigits+" digit phone number");
							return false;
						}
					}
					}
				}
				
				
			}
			
			//Pin number validation

			String pinStr = ""+pinIBoxId.getValue();
			String countryType = currentUser.getCountryType();
			if(Utility.zipValidateMap.containsKey(countryType)){
				
			if(pinIBoxId.getValue() != null && pinIBoxId.getValue().trim().length() >0) {
				boolean validZip = Utility.validateZipCode(pinStr,countryType);
				
				if(!validZip){
					 MessageUtil.setMessage("Please enter valid zip code.","color:red;");
						return false;
					
				}
				/*if(pinStr.trim().length() >6 || pinStr.trim().length() <5) {
					MessageUtil.setMessage("Please provide valid Zip Code.", "color:red", "TOP");	//Pin is modified to Zip 
					return false;
				}*/
					
			}
			
			}else{
				
				if(pinStr != null && pinStr.length() > 0){
					
					try{
						
						Long pinLong = Long.parseLong(pinStr);
						
		      } catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
						return false;
		      }
					
				if(pinStr.length() > 6 || pinStr.length() < 5) {
					
					//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
						return false;
						
					}
				}
			}
			return true;
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return false;
		}
		
	}
	
	
	
	private Textbox emailTBoxId,firstNameTbId,lastNameTbId,addOneTBoxId,addTwoTBoxId,cityTBoxId,stateTBoxId,countryTBoxId,subsidiaryTbId,retailProTextBxId,homeStoreTxtBxId;
	private Textbox pinIBoxId;
	private Textbox mPhoneIBoxId;
	MyDatebox birthDayDateBoxId,anniversaryDateBoxId;
	
	private boolean validationFields() {
		
		/*******Validation all fields******/
		//Firstname validation
		if(firstNameTbId.getParent().isVisible() && !firstNameTbId.isValid()) {
			MessageUtil.setMessage("Please enter a valid First Name.", "color:red", "TOP");
			return false;
		}
		//Last name
		if(lastNameTbId.getParent().isVisible() && !lastNameTbId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Last Name.", "color:red", "TOP");
			return false;
		}
		
		//AddressOne validation
		if(addOneTBoxId.getParent().isVisible() && !addOneTBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Address Line 1.", "color:red", "TOP");
			return false;
		}
		//AddressTwo validation
		if(addTwoTBoxId.getParent().isVisible() && !addTwoTBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Address Line 2.", "color:red", "TOP");
			return false;
		}
		//city validation
		if(cityTBoxId.getParent().isVisible() && !cityTBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid City.", "color:red", "TOP");
			return false;
		}
		//state
		if(stateTBoxId.getParent().isVisible() && !stateTBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid State.", "color:red", "TOP");
			return false;
		}
		//country
		if(countryTBoxId.getParent().isVisible() && !countryTBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Country.", "color:red", "TOP");
			return false;
		}
		//phone num
		if(mPhoneIBoxId.getParent().isVisible() && !mPhoneIBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Phone number.", "color:red", "TOP");
			return false;
		}
		//pin num
		if(pinIBoxId.getParent().isVisible() && !pinIBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Zip code." , "color:red", "TOP");
			return false;
		}
		//Subsidiary num
		if(subsidiaryTbId.getParent().isVisible() && !subsidiaryTbId.isValid()) {
			MessageUtil.setMessage("Please enter subsidiary number." , "color:red", "TOP");
			return false;
		}
		
		
		//Birthday
		if(birthDayDateBoxId.getParent().isVisible() && !birthDayDateBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid date." , "color:red", "TOP");
			return false;
		}
		//Anniversary
		if(anniversaryDateBoxId.getParent().isVisible() && !anniversaryDateBoxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid date." , "color:red", "TOP");
			return false;
		}
		
		//RetaiProId
		if(retailProTextBxId.getParent().isVisible() && !retailProTextBxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Retailpro Atrribute.", "color:red", "TOP");
			return false;
		}
		
		//Home strore
		if(homeStoreTxtBxId.getParent().isVisible() && !homeStoreTxtBxId.isValid()) {
			MessageUtil.setMessage("Please enter a valid Home Store.", "color:red", "TOP");
			return false;
		}
		
		/*if(pinIBoxId.getValue().trim().length() > 0) {
			try {
				Integer.parseInt(pinIBoxId.getValue().trim());
			}catch (Exception e) {
				 MessageUtil.setMessage("Please enter a valid Zip Code.", "color:red", "TOP");
					return false;
			}
		}*/
		
		List udfchaildrowList = custGridRowsId.getChildren();
		
		if(udfchaildrowList== null || udfchaildrowList.size() == 0) return true;
		
		Row udfRow = null;
		
		for (Object object : udfchaildrowList) {
			
			udfRow = (Row)object;
			
			//ignore if its invisible
			if(udfRow.isVisible() == false) continue;
			
			List tempList = udfRow.getChildren();
			String dispLbl = ((Label)tempList.get(0)).getValue();
			String tempStr ="";
			//String","Date","Number","Double","Boolean"
			if(dispLbl.endsWith("(Date)") || dispLbl.endsWith("(String)")) continue;
			
			if(dispLbl.endsWith("(Number)")) {
				Textbox tempTxtBx = ((Textbox)tempList.get(1));
				tempTxtBx.setStyle(NORMAL_STYLE);
				tempStr = tempTxtBx.getValue();
				if(tempStr.trim().length() == 0) continue;
				
				try {
					Long.parseLong(tempStr.trim());
				} catch (NumberFormatException e) {
					tempTxtBx.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Please provide valid data." , "color:red", "TOP");
					return false;
				}
				
			}else if(dispLbl.endsWith("(Double)")) {
				Textbox tempTxtBx = ((Textbox)tempList.get(1));
				tempTxtBx.setStyle(NORMAL_STYLE);
				tempStr = tempTxtBx.getValue();
				if(tempStr.trim().length() == 0) continue;
				
				try {
					Double.parseDouble(tempStr.trim());
				} catch (NumberFormatException e) {
					tempTxtBx.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Please provide valid data." , "color:red", "TOP");
					return false;
				}
				
			}else if(dispLbl.endsWith("(Boolean)")) {
				
				Textbox tempTxtBx = ((Textbox)tempList.get(1));
				tempTxtBx.setStyle(NORMAL_STYLE);
				tempStr = tempTxtBx.getValue();
				if(tempStr.trim().length() == 0) continue;
				
				if(!(tempStr.trim().toLowerCase().equalsIgnoreCase("true") ||
						tempStr.trim().toLowerCase().equalsIgnoreCase("false") ||
						tempStr.trim().toLowerCase().equalsIgnoreCase("t") ||
						tempStr.trim().toLowerCase().equalsIgnoreCase("f"))){
					
					MessageUtil.setMessage("Please provide valid boolean type data." , "color:red", "TOP");
					return false;
				}
					
			}
		}
		
		
		
		return true;
		
		
		
	} // validationFields
	
	private Listbox gendarListBxId;
	private boolean saveContactInDb() throws Exception{

		try {
			boolean isTrEnableForThisContact = false;
			//boolean isEnableForDateTypeTrigger = false;
			boolean isNewContact = false;
			boolean isAddedToMailingList = false;
			Iterator<MailingList> mlIter = null;
			//		Contacts contObj = new Contacts();
			logger.info("list size is ::"+mailingLists.size());
			/*String mobileStr= null;
					String emailString = null;*/
			Users currentUser = GetUser.getUserObj();
			TreeMap<String, List<String>> treeMap = 
					Utility.getPriorityMap(currentUser.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, null);
			logger.info("treeMap ::: "+treeMap);

			if(treeMap == null || treeMap.size() == 0) {
				logger.debug("User not having the contacts priority..");
				MessageUtil.setMessage("It looks like your List Settings are incomplete. Please contact our support representative.", "color:red", "TOP");
				return false;
			}

			Contacts contactObj  = setContFiled(new Contacts());
			contactObj.setUsers(currentUser);



			//Set<MailingList> mlSet = null;
			long contactBit = 0l;
			//					MailingList mailingList = null;
			boolean purgeFlag = false;

			logger.info("----"+contactObj.getFirstName());
			/*mailingList =(MailingList) mailingLists.iterator().next();
						Users user = mailingList.getUsers().getParentUser() == null ?
								mailingList.getUsers() : mailingList.getUsers().getParentUser();*/

			//TODO Handling Sharing Concept
			Contacts tempContactObj = contactDao.findContactByUniqPriority(treeMap,contactObj, currentUser.getUserId());
			//						Contacts tempContactObj = contactDao.findContactByUniqPriority(treeMap,contactObj,user.getUserId());


			if(tempContactObj  != null) {
				//Not a new Contact
				isNewContact = true;

				//mlSet = tempContactObj.getMlSet();
				contactBit = tempContactObj.getMlBits().longValue();

				String emailStatus = tempContactObj.getEmailStatus();
				boolean emailFlag = tempContactObj.getPurged();
				//String phoneStatus = tempContactObj.getMobileStatus();

				if((tempContactObj.getEmailId() != null && contactObj.getEmailId() != null &&
						! tempContactObj.getEmailId().equalsIgnoreCase(contactObj.getEmailId())
						)||(tempContactObj.getEmailId() == null && contactObj.getEmailId() != null) ) {
					emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
					emailFlag = false;
					purgeFlag = true;
				}

				//if( mlSet.size() == 0 ){
				if( contactBit == 0l ) {//deleted contact ,need to be triggered action

					isTrEnableForThisContact = (isActivatedTrigger && true);
					tempContactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);//even reduce by finding the ET condition as 'addedMannually' more 
					emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
					emailFlag = false;
					purgeFlag = true;

				}


				//************perform mobile optin***********************//   
				//perform mobile status
				boolean isNeedToPerformMO = (contactObj.getMobilePhone()!= null)&&(currentUser.isEnableSMS() && currentUser.isConsiderSMSSettings()); 
				if(isNeedToPerformMO) {
					performMobileOptIn(contactObj, false, tempContactObj);
				}

				//*****************************************************************

				contactObj = Utility.mergeContacts(contactObj, tempContactObj);

				if(contactObj.getMobilePhone() == null) {
					contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}else if(!isNeedToPerformMO){
					contactObj.setMobileOptin(false);
					contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				}
				contactObj.setEmailStatus(emailStatus);
				contactObj.setPurged(emailFlag);
				//contactObj.setMobileStatus(phoneStatus);

				// -------------Existing Contact Modification

			}else { // -------------New Contact Addition
				//mlSet = new HashSet<MailingList>();
				//kjkjk
				purgeFlag = true;
				contactObj.setCreatedDate(Calendar.getInstance());
				contactObj.setModifiedDate(Calendar.getInstance());
				contactObj.setPurged(false);
				contactObj.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				contactObj.setUsers(currentUser);
				contactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
				isTrEnableForThisContact = (isActivatedTrigger && true);
				//perform mobile status
				if(contactObj.getMobilePhone()!= null) {
					if(currentUser.isEnableSMS() && currentUser.isConsiderSMSSettings())performMobileOptIn(contactObj, true, null);
					else {
						contactObj.setMobileOptin(false);
						contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
					//contactObj.setMobilePhone(contactObj.getMobilePhone());

				}else {
					contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			}

			List<MailingList> lastModifiedDatList = new ArrayList<MailingList>();	

			/*Iterator<MailingList> mlsetIter = mlSet.iterator();
						Set<String> conMlSet = new HashSet<String>();
						while(mlsetIter.hasNext()){
							conMlSet.add(mlsetIter.next().getListName());
						}*/

			mlIter = mailingLists.iterator();
			MailingList ml = null;
			// Get the existing mailing list 
			long existingContactBit = contactBit;
			while(mlIter.hasNext()){
				ml = mlIter.next();

				//existing ml.bit
				logger.info("ml"+ml.getListId());
				logger.info("ml.getMlBit().longValue()...:"+ml.getMlBit().longValue()+"contactBit...:"+contactBit+"\n(ml.getMlBit().longValue() & contactBit) > 0...:"+((ml.getMlBit().longValue() & contactBit) > 0));
				if((ml.getMlBit().longValue() & contactBit) > 0) continue;
				//mlSet.add(ml);
				//mlSet.add(ml);
				contactBit = (contactBit | ml.getMlBit().longValue());
				ml.setLastModifiedDate(Calendar.getInstance());
				lastModifiedDatList.add(ml);
			}//While

			//compare
			//contactObj.setMlSet(mlSet);
			contactObj.setMlBits(contactBit);
			logger.info("existingContactBit != contactBit"+existingContactBit != contactBit+"existingContactBit......:"+existingContactBit+"\t contactBit...........:"+contactBit);
			if(existingContactBit != contactBit){
				//Contact is added into this list, need to trigger this job
				isAddedToMailingList = true;
			}

			if(purgeFlag) {
				PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
				purgeList.checkForValidDomainByEmailId(contactObj);

			}

			contactsDaoForDML.saveOrUpdate(contactObj);
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contactObj, contactObj.getMobilePhone());

			//TODO whenever a contact is added to db in particular list we need to modify the list size
			String mailingListIds = getMailingListIds(mlIter);
			logger.info("getMailingListIds"+mailingListIds);
			/*if(!isNewContact || !isAddedToMailingList){
						 UpdateListSizeJob.startUpdateListSizeJob(mlListIds,currentUser.getUserId());
							//listSizeHelper.start();
						}*/
			if(!isNewContact || isAddedToMailingList){
				for(MailingList mailingList : lastModifiedDatList){
					logger.info("Mailing List Id .....:"+mailingList.getListName());
					mailingList.setListSize(mailingList.getListSize() + 1);
				}
			}

			//logger.info("call observer "+isEnableForDateTypeTrigger+" === "+isTrEnableForThisContact);

			if(isTrEnableForThisContact) {

				//find is this contact exist in the trigger configured list or not

				eventTriggerEventsObservable.notifyToObserver(activatedTriggersList,
						contactObj.getContactId(), contactObj.getContactId(), currentUser.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);

			}//if

			//mailingListDao.saveByCollection(lastModifiedDatList);
			mailingListDaoForDML.saveByCollection(lastModifiedDatList);

			//TODO purging here


			MessageUtil.setMessage("Contact saved successfully.", "color:Green", "TOP");

			return true;
		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
			return false;
		} catch (NumberFormatException e) {
			logger.error("Exception ::", e);
			return false;
		}

	} // saveContactInDb()
	
	/**
	 * getMailingListIds
	 * @param mlIter
	 * @return
	 */
	public String getMailingListIds(Iterator<MailingList> mlIter){
		String mailingListIds = Constants.STRING_NILL;
		MailingList ml = null;
		logger.info("while list ids"+mlIter.hasNext());
		while(mlIter.hasNext()){
			ml = mlIter.next();
			logger.info("while list ids"+ml.getListId());
			MailingList mailingList = (MailingList)ml;
			if(Constants.STRING_NILL.equals(mailingListIds)) {
				mailingListIds = mailingList.getListId()+Constants.STRING_NILL;
				continue;
			}
			mailingListIds += ", "+mailingList.getListName();
		} 
		return mailingListIds;
	}//getMailingListIds
	
	private Contacts setContFiled(Contacts contact){
		
		if(retailProTextBxId.getParent().isVisible() && !(retailProTextBxId.getValue().trim().equals(""))) {
			contact.setExternalId(retailProTextBxId.getValue().trim());
		}
		if(homeStoreTxtBxId.getParent().isVisible() && !(homeStoreTxtBxId.getValue().trim().equals(""))) {
			contact.setHomeStore(homeStoreTxtBxId.getValue().trim());
		}
		
		
		if(addOneTBoxId.getParent().isVisible() && !(addOneTBoxId.getValue().trim().equals(""))) {
			contact.setAddressOne(addOneTBoxId.getValue().trim());
		}
		 if(addTwoTBoxId.getParent().isVisible() && !(addTwoTBoxId.getValue().trim().equals(""))) {
			contact.setAddressTwo(addTwoTBoxId.getValue().trim());
		}	
		if(cityTBoxId.getParent().isVisible() && !(cityTBoxId.getValue().trim().equals(""))) {
			contact.setCity(cityTBoxId.getValue().trim());
		}	 
		if(countryTBoxId.getParent().isVisible() && !(countryTBoxId.getValue().trim().equals(""))) {
			contact.setCountry(countryTBoxId.getValue().trim());
		}	 
		if(stateTBoxId.getParent().isVisible() && !(stateTBoxId.getValue().trim().equals(""))) {
			contact.setState(stateTBoxId.getValue().trim());
		}	 
		if(firstNameTbId.getParent().isVisible() && !(firstNameTbId.getValue().trim().equals(""))) {
			contact.setFirstName(firstNameTbId.getValue().trim());
		}	 
		if(lastNameTbId.getParent().isVisible() && !(lastNameTbId.getValue().trim().equals(""))) {
			contact.setLastName(lastNameTbId.getValue().trim());
		}	 
		if(emailTBoxId.getParent().isVisible() && !(emailTBoxId.getValue().trim().equals(""))) {
			contact.setEmailId(emailTBoxId.getValue().trim());
		}	 
		if(pinIBoxId.getParent().isVisible() && pinIBoxId.getValue() != null  && pinIBoxId.getValue().trim().length() > 0) {
			
		//	int zipNumber;
			String zipNumber;
			try {
			//	zipNumber = Integer.parseInt(pinIBoxId.getValue());
				zipNumber = pinIBoxId.getValue();
				contact.setZip(""+zipNumber);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
			
		}
		if(subsidiaryTbId.getParent().isVisible() && !(subsidiaryTbId.getValue().trim().equals(""))) {
			contact.setSubsidiaryNumber(subsidiaryTbId.getValue().trim());
		}	 
		if(mPhoneIBoxId.getParent().isVisible() && mPhoneIBoxId.getValue() !=null && mPhoneIBoxId.getValue().trim().length() > 0) {
			UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
			String tempMobileNumber = mPhoneIBoxId.getValue().trim();
			String phoneStr= Utility.phoneParse(tempMobileNumber,organization);
			
			if(phoneStr != null){
				contact.setMobilePhone(phoneStr);
			}
			
			//contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
		}/*else  {
			if(contact.getMobileStatus() != null)
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
		}*/
		//Gender
		if(gendarListBxId.getParent().isVisible() && gendarListBxId.getSelectedIndex() != 0) {
			contact.setGender(gendarListBxId.getSelectedItem().getLabel());
		}
		
		
		//Birthday
		if(birthDayDateBoxId.getParent().isVisible() && birthDayDateBoxId.getValue()!=null) {
			MyCalendar tempCal= (MyCalendar)birthDayDateBoxId.getClientValue();
			contact.setBirthDay(tempCal);
		}
		//Anniversary
		if(anniversaryDateBoxId.getParent().isVisible() && anniversaryDateBoxId.getValue()!=null) {
			MyCalendar tempCal= (MyCalendar)anniversaryDateBoxId.getClientValue();
			contact.setAnniversary(tempCal);
		}
		
//		contact.setCreatedDate(Calendar.getInstance());TODO
		
		//added after implementation for contact's optin medium
//		contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);//TODO
		
		

		List udfchaildrowList = custGridRowsId.getChildren();
		logger.info(">>>>>>>>>>> &&&&&& "+udfchaildrowList.size());
		if(udfchaildrowList != null && udfchaildrowList.size() > 0) {
			
			Row udfRow = null;
			
			for (Object object : udfchaildrowList) {
				
				
				udfRow = (Row)object;
				//ignore if its invisible
				if(udfRow.isVisible() == false) continue;
				
				List tempList = udfRow.getChildren();
				
				Label tempLabel = (Label)tempList.get(0);
				
				String dispLbl = tempLabel.getValue();
				String tempStr ="";
				
//				logger.info(" display Lable is :"+dispLbl);
				//String","Date","Number","Double","Boolean"
				if(dispLbl.endsWith("(Date)")) {
					Datebox dateBox = (Datebox)tempList.get(1);
					if(dateBox.getValue() == null) continue;
					try {
						
						Date tempDate = dateBox.getValue();
						Calendar tempCal =Calendar.getInstance();
						tempCal.setTime(tempDate);
						tempStr = MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					} catch (WrongValueException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
					
				}else {
					
					Textbox tempTxtBx = ((Textbox)tempList.get(1));
					tempStr = tempTxtBx.getValue().trim();
					if(tempStr.trim().length() == 0) continue;
					
				}
				int index = Integer.parseInt(((String)tempLabel.getAttribute("DB_UDF_INDX")).substring("UDF".length()));
				
				try {
					contact = setConatctCustFields(contact, index, tempStr);
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
			} //for
		}
		
		return contact;
	}
	
	private Contacts setConatctCustFields(Contacts contact , int index, String  udfData) throws Exception {

			 logger.debug("udf index is >>::"+index);
		switch(index){
		case 1:	contact. setUdf1(udfData); return contact;
		case 2:	contact. setUdf2(udfData); return  contact;
		case 3: contact.setUdf3(udfData); return  contact;
		case 4: contact.setUdf4(udfData);return  contact;
		case 5: contact.setUdf5(udfData);return  contact;
		case 6: contact.setUdf6(udfData);return  contact;
		case 7: contact.setUdf7(udfData);return  contact;
		case 8: contact.setUdf8(udfData);return  contact;
		case 9: contact.setUdf9(udfData);return  contact;
		case 10: contact.setUdf10(udfData);return  contact;
		case 11: contact.setUdf11(udfData);return  contact;
		case 12: contact.setUdf12(udfData);return  contact;
		case 13: contact.setUdf13(udfData);return  contact;
		case 14: contact.setUdf14(udfData);return  contact;
		case 15: contact.setUdf15(udfData);return  contact;
		
		}
			
		return contact;
			 
	} // setConatctCustFields
	
	
	
	public void onClick$addNewCustFieldTBId() {
		Redirect.goTo(PageListEnum.ADMIN_LIST_SETTINGS);
	} // onClick$addNewCustFieldTBId
	
	
	@SuppressWarnings("unused")
	public void performMobileOptIn(Contacts inputContactObj, boolean isNew, Contacts existingContact) throws BaseServiceException {
		
		SMSSettingsDao smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		SMSSettings smsSettings = null;
		Users user = inputContactObj.getUsers();
		if(SMSStatusCodes.smsProgramlookupOverUserMap.get(user.getCountryType())) smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		else  smsSettings = smsSettingsDao.findByOrg(user.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			
	//SMSSettings smsSettings = smsSettingsDao.findByUser(inputContactObj.getUsers().getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		if(smsSettings == null) {
			String noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
					"SMS may not be sent to the mobile contacts.";
			
			
			Messages messages = new Messages("Contact" ,"Mobile contacts may not reachable" ,noSMSComplaincyMsg ,
					Calendar.getInstance(),"Inbox",false ,"Info", inputContactObj.getUsers()); 
			
			MessagesDao messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
			MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);

			inputContactObj.setMobileOptin(false);
			inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
				
			if(existingContact != null) {
				
				existingContact.setMobileOptin(false);
				existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
				
			}
			
			return;
		}	
	
	OCSMSGateway ocGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), 
			SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(smsSettings.getUserId().getCountryType()));
	
	if(ocGateway == null) {
		
		String noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
				"SMS may not be sent to the mobile contacts.";
		
		
		Messages messages = new Messages("Contact" ,"Mobile contacts may not reachable" ,noSMSComplaincyMsg ,
				Calendar.getInstance(),"Inbox",false ,"Info", inputContactObj.getUsers()); 
		
		MessagesDao messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
		inputContactObj.setMobileOptin(false);
		inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
			
		if(existingContact != null) {
			
			existingContact.setMobileOptin(false);
			existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
			
		}
		
		return;
		
		
		
	}
	
	String optinMedium = null;
	
	if(inputContactObj.getMobilePhone() != null && !inputContactObj.getMobilePhone().isEmpty()) {
		
		//if
		boolean isDifferentMobile = false;
		String mobile = inputContactObj.getMobilePhone();
		
		if(existingContact != null) {
			
			String conMobile = existingContact.getMobilePhone();
			optinMedium = existingContact.getOptinMedium();
		//to identify whether entered one is same as previous mobile
			if(conMobile != null ) {
				
				if(!mobile.equals(conMobile)) {
					
					if( (mobile.length() < conMobile.length() && !conMobile.endsWith(mobile) ) ||
							(conMobile.length() < mobile.length() && !mobile.endsWith(conMobile)) || mobile.length() == conMobile.length()) {
						
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
		
	/*	SMSSettingsDao smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId());*/
		
		Byte userOptinVal =	smsSettings.getOptInMedium();
		
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(currentUser.getCountryType()) && currentUser.getOptInMedium() != null) ? 
				 currentUser.getOptInMedium() : userOptinVal;
				
		if(smsSettings.isEnable() && 
				userOptinVal != null && 
				(userOptinVal.byteValue() & optin ) > 0 ) {						
			
			if( (existingContact != null && 
					(existingContact.getLastSMSDate() == null && existingContact.isMobileOptin() != true) ||
					(existingContact != null && isDifferentMobile) )  ) {
				
					existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
					existingContact.setLastSMSDate(Calendar.getInstance());
					existingContact.setMobileOptin(false);
					canProceed = true;
					
				
				
			}
			if(canProceed || isNew) {	
				
				//logger.info("is a new contact=====");
				
					
					inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
					inputContactObj.setMobileOptin(false);
				
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)SpringUtil.getBean("captiwayToSMSApiGateway");
				
				if(!ocGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
					
					logger.debug("low credits with clickatell");
					return;
				}
				
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					
					//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
					
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())){
							
							msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
					}
					
					if(!mobile.startsWith(user.getCountryCarrier()+"")){
						mobile =user.getCountryCarrier()+mobile;
					}
					
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
									
					//clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobile, msgContent);
					/*if(currentUser.getParentUser() != null) {
						currentUser = currentUser.getParentUser();
					}*/
					/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() )+1);
					usersDao.saveOrUpdate(currentUser);*/
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
					
					/**
					 * Update SMS Queue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					smsQueueHelper.updateSMSQueue(mobile,msgContent,optinMedium,currentUser,smsSettings.getSenderId());
					
				}else {
					logger.debug("low credits with user...");
					
					return;
					
				}
				
			}//if
		}//if
		else {
			
			if(existingContact != null) {
				
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
	
	
	
	
	
}//performMobileOptIn}//performMobileOptIn
	
	Popup help,help1;
	public void onFocus$mPhoneIBoxId() {
		
		//cSubTb.setPopup(help);
		help.open(mPhoneIBoxId, "end_after");
		
	}
	
	
	int udfRowSize = 0;
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
			try {
				if(event.getTarget() instanceof A) {
					
					if(udfRowSize==1 ){
						udfFieldLblId.setVisible(false);
						udfDivId.setVisible(false);
					}
					udfRowSize--;
					A eventAnchor = (A)event.getTarget();
					Row row = (Row)eventAnchor.getParent();
					row.setVisible(false);
					
					showAllAnchorId.setVisible(true);
				}
			}catch (Exception e) {
			}
	} // onEvent
}
