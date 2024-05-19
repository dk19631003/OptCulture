package org.mq.marketer.campaign.controller.contacts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

@SuppressWarnings({ "serial","unchecked" })//
public class AddSingleController extends GenericForwardComposer{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Include xcontents = null;
	private Session session;
	private Set mailingLists = null;
//	private Object fieldsObj;
//	private String invalidEmails = "";
//	private int uploadedCount=0;
	

	private Div cfDivId,addCustDivId;
	private Textbox firstNameTbId;
	private Textbox lastNameTbId;
	private Textbox emailTBoxId;
	private A showAllAnchorId;
	
	private Longbox mPhoneIBoxId;
	private Label statusLabelId;
	private Label listNameLabeId;
	private Textbox addOneTBoxId;
	private Textbox addTwoTBoxId;
	private Textbox cityTBoxId;
	private Textbox stateTBoxId;
	private Textbox countryTBoxId;
	private Intbox pinTBoxId;
	private Button submitBtnId,doneBtnId;
	
	private Grid custFieldsGbId;
	private Rows rowsId;
	private MLCustomFieldsDao mlCustomFieldsDao;
	private CustomFieldDataDao cfDataDao;
	private CustomFieldDataDaoForDML cfDataDaoForDML;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private Rows custGridRowsId,custFieldRowsId;
	private Label custFieldLabelId;
	private Toolbarbutton addNewCustFieldTBId;
	private Listbox gendarListBxId;
//	Contacts contact;
//	Desktop desktop = null;
	List custTBList = null;
//	List list ;
//	List listOne = null;
	List custList = null;
//	List custTextBoxIdList = null;
	private String isNew;
	//private Users user;
	private Users currentUser;
	private UserActivitiesDao userActivitiesDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	
	private MyDatebox birthDayDateBoxId,anniversaryDateBoxId;
	int custInputCount=0; 
//	int custInput = 0;
	int customFieldSize = 0;
	int configuredCfMaxFieldIndex=0;
	int mailingListCount=1;
	
	private Set<Long> listIdsSet; 
	public AddSingleController() {
		
		session = Sessions.getCurrent();
		mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		cfDataDao = (CustomFieldDataDao)SpringUtil.getBean("cfDataDao");
		cfDataDaoForDML = (CustomFieldDataDaoForDML)SpringUtil.getBean("cfDataDaoForDML");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		userActivitiesDao =	(UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML =	(UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		
		currentUser = GetUser.getUserObj();
		isNew = (String)session.getAttribute("isNewML");
		this.xcontents = Utility.getXcontents();
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		
	}


	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		String addSingleFList = (String)PropertyUtil.getPropertyValue("addSingleFList");
		
		logger.debug("addSingleFList is::"+addSingleFList);
//		String[] fieldArray = StringUtils.split(addSingleFList, ',');
		
		if(logger.isDebugEnabled()) {
			logger.debug(" isNewML :"+isNew);
		}
		
		
		mailingLists = (Set)session.getAttribute("AddSingle_Ml");
		String listNameStr = "";
		
		for(Object obj:mailingLists) {
			
			MailingList mailingList = (MailingList)obj;
			if(listNameStr.length() < 1) {
				listNameStr = mailingList.getListName();
				continue;
			}
			listNameStr += ", "+mailingList.getListName();
		}
		
		//display ListName
		listNameLabeId.setValue(listNameStr);
		
//		List<String> tempList = new ArrayList<String>();
		
		MailingList mailingList = null;
		
		// coming from Add or Import contact zul
		if(isNew != null && isNew.equals("true")) { 
			addCustDivId.setVisible(true);
			mailingList = (MailingList)mailingLists.iterator().next();
			
			mailingListDaoForDML.saveOrUpdate(mailingList); //list name are save in to the Db
			
			listIdsSet.add(mailingList.getListId());
			session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
			
			
//			fieldArray = StringUtils.split(addSingleFList, ',');
		}
		else if(!isNew.equals("true") && mailingLists.size() == 1) {
			
			logger.debug("is new is not null and selected existing mailing list");
			mailingList = (MailingList)mailingLists.iterator().next();
			
			/*for(int i=0;i<fieldArray.length;i++) {
			
				tempList.add(fieldArray[i]);
			}*/
			if(mailingList.isCustField()) {
				
				List<MLCustomFields> list = mlCustomFieldsDao.findAllByList(mailingList);
				if(logger.isDebugEnabled()) {
				
					logger.debug(" Total Custom Fileds for the already existing list :"+list.size());
				}
				/*for(Object object :list) {
					tempList.add("CF :"+((MLCustomFields)object).getCustFieldName());
				}*/
				
				custFieldLabelId.setVisible(true);
				cfDivId.setVisible(true);
				
				//get the custom for the List
				getCustFields(mailingList);
				
				
//				fieldsObj = tempList;
				if(logger.isDebugEnabled()) logger.debug(" -- end --");
			}
		}	
		/*if(userActivitiesDao != null) {
		    userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_ADDSINGLE, currentUser);
		}*/
		if(userActivitiesDaoForDML != null) {
		    userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_ADDSINGLE, GetUser.getLoginUserObj());
		}
	
		
	}
	
	/*
	public void init( Textbox firstNameTbId,Textbox lastNameTbId,Textbox emailTBoxId, Longbox mPhoneIBoxId,
			Textbox addOneTBoxId, Textbox addTwoTBoxId,Textbox cityTBoxId,Textbox stateTBoxId,Textbox countryTBoxId,
			Intbox pinTBoxId,Button submitBtnId,Button doneBtnId,Div cfDivId ,Label statusLabelId,A showAllAnchorId,
			Label listNameLabeId, Rows custGridRowsId,Label custFieldLabelId,Div addCustDivId,Grid custFieldsGbId,Rows 
			custFieldRowsId,Toolbarbutton addNewCustFieldTBId)  {
		
		try {
			this.cityTBoxId    = cityTBoxId;
			this.pinTBoxId     = pinTBoxId;
			this.cfDivId       = cfDivId;
			this.doneBtnId     = doneBtnId;
			this.stateTBoxId   = stateTBoxId;
			this.submitBtnId   = submitBtnId;
			this.emailTBoxId   = emailTBoxId;
			this.firstNameTbId = firstNameTbId;
			this.lastNameTbId  = lastNameTbId;
			this.mPhoneIBoxId  = mPhoneIBoxId;
			this.addOneTBoxId  = addOneTBoxId;
			this.addTwoTBoxId  = addTwoTBoxId;
			this.countryTBoxId = countryTBoxId;
			this.statusLabelId = statusLabelId;
			this.listNameLabeId = listNameLabeId;
			this.showAllAnchorId = showAllAnchorId;
			this.custGridRowsId = custGridRowsId;
			this.custFieldLabelId = custFieldLabelId;
			this.addCustDivId = addCustDivId;
			this.custFieldsGbId = custFieldsGbId;
			this.custFieldRowsId= custFieldRowsId;
			this.addNewCustFieldTBId= addNewCustFieldTBId;
			String addSingleFList = (String)PropertyUtil.getPropertyValue("addSingleFList");
			
			logger.debug("addSingleFList is::"+addSingleFList);
//			String[] fieldArray = StringUtils.split(addSingleFList, ',');
			
			if(logger.isDebugEnabled()) {
				logger.debug(" isNewML :"+isNew);
			}
			
			if(userActivitiesDao != null) {
			    userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_ADDSINGLE,GetUser.getUserObj());
			}
			
			mailingLists = (Set)session.getAttribute("AddSingle_Ml");
			String listNameStr = "";
			
			for(Object obj:mailingLists) {
				
				MailingList mailingList = (MailingList)obj;
				if(listNameStr.length() < 1) {
					listNameStr = mailingList.getListName();
					continue;
				}
				listNameStr += ", "+mailingList.getListName();
			}
			
			//display ListName
			listNameLabeId.setValue(listNameStr);
			
//			List<String> tempList = new ArrayList<String>();
			
			MailingList mailingList = null;
			
			// coming from Add or Import contact zul
			if(isNew != null && isNew.equals("true")) { 
				addCustDivId.setVisible(true);
				mailingList = (MailingList)mailingLists.iterator().next();
				
				mailingListDao.saveOrUpdate(mailingList); //list name are save in to the Db
				
//				fieldArray = StringUtils.split(addSingleFList, ',');
			}
			else if(!isNew.equals("true") && mailingLists.size() == 1) {
				
				logger.debug("is new is not null and selected existing mailing list");
				mailingList = (MailingList)mailingLists.iterator().next();
				
				for(int i=0;i<fieldArray.length;i++) {
				
					tempList.add(fieldArray[i]);
				}
				if(mailingList.isCustField()) {
					
					List<MLCustomFields> list = mlCustomFieldsDao.findAllByList(mailingList);
					if(logger.isDebugEnabled()) {
					
						logger.debug(" Total Custom Fileds for the already existing list :"+list.size());
					}
					for(Object object :list) {
						tempList.add("CF :"+((MLCustomFields)object).getCustFieldName());
					}
					
					custFieldLabelId.setVisible(true);
					cfDivId.setVisible(true);
					
					//get the custom for the List
					getCustFields(mailingList);
					
					
//					fieldsObj = tempList;
					if(logger.isDebugEnabled()) logger.debug(" -- end --");
				}
			}	
			else {
				logger.debug("Is new null.....from mycontacts");
				
				
				if(mailingLists.size() == 1) {
					
					mailingList = (MailingList)mailingLists.iterator().next(); 
					for(int i=0;i<fieldArray.length;i++) {
							
						tempList.add(fieldArray[i]);
					}
					if(mailingList.isCustField()) {
							
						List list = mlCustomFieldsDao.findAllByList(mailingList);
						if(logger.isDebugEnabled()) {
								
							logger.debug(" Total Custom Fileds for the already existing list :"+list.size());
						}
						for(Object o:list) {
								
							tempList.add("CF :"+((MLCustomFields)o).getCustFieldName());
						}//for
					}//if
//				fieldsObj = tempList;
					if(logger.isDebugEnabled()) logger.debug("this was entered from mylists page");
				}
				
			}
			
			else if(mailingLists.size() == 1 && ((MailingList) mailingLists.iterator().next()).isCustField()) {
				
				// logger.info(" is mailingList is custField");
				custFieldLabelId.setVisible(true);
				cfDivId.setVisible(true);
				
				//get the custom for the List
				getCustFields(mailingList);
			}
		} catch (Exception e) {
			logger.debug("** Exception : Error occured while displaying contact fields **",e);
		}
	} // init

	*/

	public void onClick$backBtnId() {
		try {
			PageUtil.goToPreviousPage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception error occured while redircting the page >>>",e);
		}
	}
	
	public void onClick$submitBtnId() {
		try {
			listName.setLength(0);
			if(addContacts()) {
				String msg = "";
				if(listName.length() >0 && mailingLists.size() > 1) {
					msg = "Contact already exists in "+listName+" and contact saved successfully in remailning list ";
					/*MessageUtil.setMessage("Contact already exists in following mailing list(s) :"+" "+listName, "color:red", "TOP");
					MessageUtil.setMessage("Contact details saved successfully.", "color:blue", "TOP");*/
					
				}else {
					msg = "Contact details saved successfully.";
				}
				
				MessageUtil.setMessage(msg, "color:blue", "TOP");
				
				enterMoreContacts();
			}
		} catch (Exception e) {
			logger.error("Exception ::error occured while adding the contact :::",e);
		}
	} //onClick$submitBtnId
	
	//Save Button
	public void onClick$doneBtnId() {
		try {
			saveAndRedirect();
		} catch (Exception e) {
			logger.error("Exception ::error occured while saving and redircting the page :::",e);
		}
	}
	
	public void onClick$addNewCustFieldTBId() {
		try {
			addCustomField();
		} catch (Exception e) {
			logger.error("Exception ::error occured while adding the custom fields :::",e);
		}
	}
	
	public void onClick$showAllAnchorId() {
		try {
			showAll(rowsId);
		} catch (Exception e) {
			logger.error("Exception ::error occured while showing the all rows in grid :::",e);
		}
		
	}
	
	/**
	 * get Custom field, for given mailingList
	 * @param mailingList
	 */
	public void getCustFields(MailingList mailingList) {
		try {
				logger.debug("<<<<<<<<<<<<Just enter custFields >>>>>>>>>>>>>>>>>>>>>");
				
				if( mailingList == null && !mailingList.isCustField() ) {
					logger.info(" No custom fields are existed for mailing list : "+mailingList);
					return;
				} 
			
				custList = mlCustomFieldsDao.findAllByList(mailingList);
				logger.debug("Total custom fields : " + custList.size());
			
				if(custList.size() < 1) {
					return;
				}
				
				MLCustomFields mlcf = null;

				String cflistStr = "";

				custTBList = new ArrayList();
				Label cfNameLbl = null;
				/*Label cfTypeLbl = null;
				Label colenLbl;*/
				Textbox cfValTb = null;
			
				for(Object obj:custList) {
				
					mlcf = (MLCustomFields)obj;
					Row custFieldRow = new Row();
					
					cfNameLbl = new Label(mlcf.getCustFieldName()+" (" + mlcf.getDataType() + ")"+" "+Constants.DELIMETER_COLON );
					cfNameLbl.setParent(custFieldRow);
					
					cfValTb = new Textbox();
					cfValTb.setCols(25);
					cfValTb.setId("cust" + mlcf.getFieldIndex());
					
					logger.debug(" Custom Field Id : "+cfValTb.getId());
					
					cfValTb.setMaxlength(50);
					cfValTb.setAttribute("datatype", mlcf.getDataType());
					logger.info("Custom Field datatype:"+mlcf.getDataType());
					
					if(mlcf.getDataType().contains("Date")) {
						MyDatebox dateBox = new MyDatebox();
						dateBox.setFormat(mlcf.getFormat());
						dateBox.setAttribute("index", mlcf.getFieldIndex());
//						dateBox.setWidth("72%");
						custTBList.add(dateBox);
						dateBox.setReadonly(true);
						dateBox.setParent(custFieldRow);
						dateBox.setId("customDateBox_"+custInputCount);
					}
					else {
						
						cfValTb.setAttribute("cfName", mlcf.getCustFieldName());
						cfValTb.setAttribute("index", mlcf.getFieldIndex());
						cfValTb.setId("custTextBox_"+custInputCount);
						custTBList.add(cfValTb); 
						cfValTb.setParent(custFieldRow);
					}
					custInputCount++;
					
					A anchorTag = new A();
					anchorTag.setLabel("Hide");
					anchorTag.addEventListener("onClick", this);
					anchorTag.setStyle("color:#2886B9;font-size:12px;font-weight:bold;");
					anchorTag.setParent(custFieldRow);
					custFieldRow.setHeight("35px");
					custFieldRow.setParent(custGridRowsId);
					
					cflistStr = cflistStr + "cust" + mlcf.getFieldIndex() + ",";
				} // forEach
				
				customFieldSize=custTBList.size();

				cflistStr =  cflistStr.substring(0, cflistStr.length()-1);
				logger.debug("getting values of : " + cflistStr);

				
			} catch (Exception e) {
				
				logger.error("** Exception : Problem while getting custom field info - " , e );
			}
		}
	
	//onBlur method calling
	
	//onBlur method calling for emailId exists or not
	private boolean checkEmailName(Textbox emTextbox, Label statusLabelId) {
	
		String phone = null;
	
		try {
			//mPhoneIBoxId
			MessageUtil.clearMessage();
			String emailStr = emTextbox.getValue();
			logger.info("<<--------Just Entered... , Email Id is: "+ emailStr);
			
			ContactsDao contactDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			
			if (emailStr.trim().equals("") && mPhoneIBoxId.getValue() == null) {
//					submitBtnId.setDisabled(true);
				/*statusLabelId.setStyle("color:red");
				statusLabelId.setValue("Please enter a e-mail address");*/
				MessageUtil.setMessage("Please provide either email address or phone number before saving contact.", "color:red", "TOP");
				return false;
	        }
			
			if(emailStr.trim().length() >0 && !Utility.validateEmail(emailStr)) {
//						submitBtnId.setDisabled(true);
					statusLabelId.setStyle("color:red");
					statusLabelId.setValue("Please enter a valid e-mail address");
					return false;
			} 
			/*else {
				submitBtnId.setDisabled(false);
			}*/
			
			boolean flag = false;
			
			if(mPhoneIBoxId.getValue() != null) {
				UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
				phone = Utility.phoneParse(""+mPhoneIBoxId.getValue(),organization);
				if(phone == null || phone.length() == 0 ) {
					MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
					return false;
				}
			}
			
			
			//pin validation
			if(pinTBoxId.getValue() != null) {
				String pinStr = ""+pinTBoxId.getValue();
				
				if(pinStr.length() > 6 || pinStr.length() < 5){
					MessageUtil.setMessage("Invalid Zip Code.", "color:red", "TOP");
					return false;
				}
			}
			
			
			
			
			
			String msg = "";
			
			if(mailingLists.size()==1) {
				MailingList mailingList = (MailingList)mailingLists.iterator().next();
				
//				flag = contactDao.findByEmailId(emailStr,mailingList.getListId());
				//Contacts contact  = contactDao.findContactByValues(emailStr, phone, null,  mailingList.getListId());
				Contacts contact  = contactDao.findContactByValues(emailStr, phone, null, currentUser.getUserId());
				
				logger.debug("Contact already Exists ::"+flag);
				
				if(contact != null) {
					flag = true;
					msg = "contact already exists";
				} else {
					flag = false;
					msg ="Available";
				}
			}	
			
			statusLabelId.setStyle(flag ?"color:red":"color:#023849");
			statusLabelId.setValue(msg);
			
			return (!flag);
				
		} catch (Exception e) {
			logger.error(" ** Exception while validating email Id ** ",e);
			return false;
		}
	} // checkEmailName


	//onClick of  Add new Custom field (Toolbar button)
	// add CustomField for creating of New List, when we click on a Add new custom field Button
	public void addCustomField() {
		
		try {
			MessageUtil.clearMessage();
				
			if(!custFieldsGbId.isVisible()) {
				custFieldsGbId.setVisible(true);
			}
			if(mailingListCount >= 19) {
				addNewCustFieldTBId.setVisible(false);
			}
			mailingListCount++;
			if(isNew !=null && isNew.equalsIgnoreCase("true")) {
					
			    MailingList  mailingList = (MailingList)mailingLists.iterator().next();;
				Row customFieldRow = new Row();
				
				MLCustomFields mlCustomFields = new MLCustomFields("","String",mailingList,"");
				
				//set the chaild field  for a row
				addRule(customFieldRow, mlCustomFields);
				
				customFieldRow.setParent(custFieldRowsId);
				//custFieldRowsId.invalidate();
			} // if
			
		}catch (Exception e) {
			logger.error("** Exception : Error occured while adding custom fields **",e);
		}
	
	} // addCustomField()
	
	/**
	 * set the chaild fields for creating
	 * @param customFieldRow
	 * @param mlcf
	 */
	public void addRule(Row customFieldRow , MLCustomFields mlcf) {
		try {
			
			//creating textbox for custom field name
			Textbox tempTxtbox = new Textbox();
			tempTxtbox.setInplace(true);
			tempTxtbox.setWidth("150px;");
			tempTxtbox.setParent(customFieldRow);
			
			//creating listbox for data types
			Hbox tempDiv= new Hbox();
			//DataType
			Listbox tempListbox = new Listbox();
			tempListbox.setMold("select");
			tempListbox.appendItem("String", "String");
			tempListbox.appendItem("Date", "Date");
			tempListbox.appendItem("Number", "Number");
			tempListbox.appendItem("Double", "Double");
			tempListbox.appendItem("Boolean", "Boolean");
			tempListbox.setWidth("120px");
			tempListbox.setParent(tempDiv);
			
			//Date Format Combobox
			Combobox dateFormatCombBx= new Combobox();
			dateFormatCombBx.setVisible(false);
			Comboitem tempComboitem = new Comboitem("dd/MM/yyyy");
			tempComboitem.setParent(dateFormatCombBx);
			
			tempComboitem = new Comboitem("dd-MM-yyyy");
			tempComboitem.setParent(dateFormatCombBx);
			
			tempComboitem = new Comboitem("dd/MM/yyyy HH:mm");
			tempComboitem.setParent(dateFormatCombBx);
			
			tempComboitem = new Comboitem("MM/dd/yyyy");
			tempComboitem.setParent(dateFormatCombBx);
			
			tempComboitem = new Comboitem("MM-dd-yyyy");
			tempComboitem.setParent(dateFormatCombBx);
			
			tempComboitem = new Comboitem("MM/dd/yyyy HH:mm");
			tempComboitem.setParent(dateFormatCombBx);
			dateFormatCombBx.setSelectedIndex(0);
			dateFormatCombBx.setParent(tempDiv);
			
			tempDiv.setParent(customFieldRow);
			
			tempListbox.setAttribute("getCombobox", dateFormatCombBx);
			tempListbox.addEventListener("onClick", this);
			
			String tempVal = mlcf.getDataType();

			for (int i = 0; i < tempListbox.getItemCount(); i++) {
				if(tempVal.equals(tempListbox.getItemAtIndex(i).getValue())) {
					tempListbox.setSelectedIndex(i);
					break;
				}
			} // for
			
			// creting textbox for Default value
			tempTxtbox = new Textbox();
			tempTxtbox.setInplace(true);
			tempTxtbox.setParent(customFieldRow);
			
			// create delete img for deleting the row
			Image img = new Image("/img/icons/delete_icon.png");
			img.setTooltiptext("Delete");
			img.setAttribute("mlcfObj", mlcf);
			img.setStyle("cursor:pointer;");
			img.addEventListener("onClick", this);
			img.setParent(customFieldRow);
		
		} catch (Exception e) {
			logger.error("Exception:error occured while adding the custom fields, isNew is true "); 
		}
	}
	
	
	/**
	 * onClick of a Save and Add More Contacts button
	 * Validating  the fields and saveContacts() calling.
	 */
	public boolean addContacts() {
		try {
			
			logger.debug("--just Click Save and Add More Contacts button for saving the contacts--");
			MessageUtil.clearMessage();
			
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
			
			//emailId validation	
			if(emailTBoxId.getValue().trim().length() >0  && !Utility.validateEmail(emailTBoxId.getValue())) {
				logger.debug( " emailString :"+emailTBoxId.getValue());
				MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
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
			if(pinTBoxId.getParent().isVisible() && !pinTBoxId.isValid()) {
				MessageUtil.setMessage("Please enter a valid Zip code." , "color:red", "TOP");
				return false;
			}
			
			/*//Gender
			if(genderTxtBoxId.getParent().isVisible() && !genderTxtBoxId.isValid()) {
				MessageUtil.setMessage("Please enter a valid Gender." , "color:red", "TOP");
				return false;
			}*/
			
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
			
			
			
			// Check for email id existance
			if(checkEmailName(emailTBoxId, statusLabelId)==false) return false;
			
				 
			logger.debug("is mailing lis is ::"+isNew);
			
			if(isNew != null && isNew.equalsIgnoreCase("true")) {
				
				if(saveNewMailingList() == false) {
					return false;  
				}
			} // 
					
			//Save the contact in aDB 
			if(saveContacts() == false) {  
				return false;  
			}
			return true;
		
		} catch (Exception e) {
				logger.debug("** Exception: Error occured  while adding the contact .**",e);
				return false;
			}
	} // addContacts()
	
	private boolean saveNewMailingList() {
		

		
		try {
			for(Object obj : mailingLists) {
				
				MailingList mailingList= (MailingList)obj;
				
				List<Component> lst = custFieldRowsId.getChildren();
				
				if(lst.size()>0) {
					
					String cfNameStr;
					String dataTypeStr;
					String defValueStr;
					int count = 0;
			
				// Validate The Custom fields data
					Vector<String> tempVec = new Vector<String>();
					mailingList.setCustField(true);
					mailingListDaoForDML.saveOrUpdate(mailingList);
					
					for (Component rowComp : lst) {
						Row row = (Row)rowComp;
						if(!row.isVisible()) continue;
					
						List<Component> comps = row.getChildren();

						cfNameStr = ((Textbox)comps.get(0)).getValue().trim();
						
						dataTypeStr = ((Listbox)((Hbox)comps.get(1)).getChildren().get(0)).getSelectedItem().getLabel();
						
						
						
						
						defValueStr = ((Textbox)comps.get(2)).getValue();
						
						logger.debug("custom field  is ::"+dataTypeStr +":: the value is ::"+defValueStr);
						
						if(cfNameStr.equals("")) {
							MessageUtil.setMessage("Custom field name cannot be empty.", "color:red", "TOP");
							return false;
						}
						else if(tempVec.contains(cfNameStr.toUpperCase())){
							MessageUtil.setMessage("Custom field name should not be repeated :"+cfNameStr, "color:red", "TOP");
							return false;
						}
						else {
							tempVec.add(cfNameStr.toUpperCase());
							
							if(dataTypeStr.contains("Date")) {
								//Date Format
								String dateFormatStr	= ((Combobox)((Hbox)comps.get(1)).getChildren().get(1)).getSelectedItem().getLabel();
								
								if(!CustomFieldValidator.validateDate(defValueStr, dataTypeStr, dateFormatStr)) {
									MessageUtil.setMessage("Invalid default data for the custom field :" + cfNameStr, "color:red","TOP");
									return false;
								}
							}
							
							else if(!CustomFieldValidator.validate(defValueStr,dataTypeStr)) {
								MessageUtil.setMessage("Invalid default data for the custom field :" + cfNameStr, "color:red","TOP");
								return false;
							}
						}
					
					} // for
					
					List<MLCustomFields> cfmodifiedList= new ArrayList<MLCustomFields>();
					custTBList = new ArrayList();
					// Set the Custom fields data
					MLCustomFields tempCfObj;
					
					for (Component rowComp : lst) {
						Row row =(Row)rowComp;
						if(!row.isVisible()) continue;
						
						List<Component> comps = row.getChildren();
						
						if(!row.isVisible()) continue;
						
						tempCfObj = (MLCustomFields)((Image)comps.get(3)).getAttribute("mlcfObj");
						
						
						cfNameStr = ((Textbox)comps.get(0)).getValue().trim();				//customFieldname
						
						dataTypeStr = ((Listbox)((Hbox)comps.get(1)).getChildren().get(0)).getSelectedItem().getLabel(); //customfield datatype
//						dataTypeStr = ((Listbox)comps.get(1)).getSelectedItem().getLabel(); 
						
						defValueStr = ((Textbox)comps.get(2)).getValue();					//customfield value
						
						logger.debug("custom field  is :: :::"+dataTypeStr +":: the value is ::"+defValueStr);
						String dateFormatterStr="";
						if(dataTypeStr.contains("Date")) {
							//Date Format
							dateFormatterStr	= ((Combobox)((Hbox)comps.get(1)).getChildren().get(1)).getSelectedItem().getLabel();
						}
						
						boolean retFlag = updateCFandSayIsNew(cfNameStr, dataTypeStr ,defValueStr , dateFormatterStr, tempCfObj);
//						comps.get(2).setId("Textbox_id"+count)
						Textbox tbox= new Textbox();
						count++;
						
						
						
						
						tbox.setAttribute("cfName", cfNameStr);             
						tbox.setAttribute("datatype",dataTypeStr);	
						//DateFormat when only the selecting data Type is Date  otherwise its eampty
						tbox.setAttribute("dateFormat",dateFormatterStr);	
						tbox.setAttribute("index", count);				//custom index			
						tbox.setValue(defValueStr);							
						tbox.setParent(row);
						custTBList.add(tbox);
						
						if(retFlag) {
							cfmodifiedList.add(tempCfObj);
						}
					} // forEach row
					
					//logger.info("Modified List="+cfmodifiedList);
					MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
					MLCustomFieldsDaoForDML mlCustomFieldsDaoForDML = (MLCustomFieldsDaoForDML)SpringUtil.getBean("mlCustomFieldsDaoForDML");

					//Modify the Custom fields info
					if(cfmodifiedList.size()>0) {
						logger.info("*******cfmodifiedList.size:"+cfmodifiedList.size());
					//	mlCustomFieldsDao.saveByCollection(cfmodifiedList);
						mlCustomFieldsDaoForDML.saveByCollection(cfmodifiedList);

					}
			
				}//if(lst.size()>0)
			}
			
			return true;
		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
			return false;
		}
		
		
	} // saveNewMailingList
	
	/**
	 * 
	 */
		private boolean updateCFandSayIsNew(String cfName, String dataType, String defValue, String dateFormat ,MLCustomFields tempCfObj) {
			boolean flag=false;
			try {
				if(!cfName.trim().equalsIgnoreCase( (tempCfObj.getCustFieldName()==null? "": tempCfObj.getCustFieldName()) )) {
					flag=true; 
					tempCfObj.setCustFieldName(cfName);
				}
				
				if(!dataType.trim().equalsIgnoreCase( (tempCfObj.getDataType()==null? "": tempCfObj.getDataType()) )) {
					flag=true; 
					tempCfObj.setDataType(dataType);
				}
				
				if(!defValue.trim().equalsIgnoreCase( (tempCfObj.getDefaultValue()==null? "": tempCfObj.getDefaultValue()) )) {
					flag=true;
					tempCfObj.setDefaultValue(defValue);
				}
				if(!dateFormat.trim().equalsIgnoreCase((tempCfObj.getFormat() == null? "": tempCfObj.getFormat()) )) {
					flag=true;
					tempCfObj.setFormat(dateFormat);
				}
				if(tempCfObj.getFieldIndex() <= 0) {
					flag=true;
					configuredCfMaxFieldIndex++;
					tempCfObj.setFieldIndex(configuredCfMaxFieldIndex);
					tempCfObj.setSelectedField("Custom Field" + configuredCfMaxFieldIndex);
				}
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
				return false;
			}
			return flag;
		} // updateCFandSayIsNew
	
		/**
		 * saves single contact added and redirects to View List page
		 */
		public void saveAndRedirect() {
			
			//logger.debug("EMAIL Id is ::" +emailTBoxId.getValue().trim().isEmpty());
			/*if(emailTBoxId.getValue().trim().isEmpty() == true) {
				Redirect.goTo("/contact/myLists");
			}*/
//			else {
			listName.setLength(0);
			
				if(addContacts() == true) {
					String msg = "";
					//logger.debug("%%%%%%%%%%%%%%-----List name is_---"+listName);
					if(listName.length() >0 && mailingLists.size() > 1) {
						msg = "Contact already exists in "+listName+" and contact saved successfully in remaining list ";
						/*MessageUtil.setMessage("Contact already exists in following mailing list(s) :"+" "+listName, "color:red", "TOP");
						MessageUtil.setMessage("Contact details saved successfully.", "color:blue", "TOP");*/
						
					}else {
						msg = "Contact details saved successfully.";
					}
					
					
					MessageUtil.setMessage(msg , "color:green");
				
					Redirect.goTo(PageListEnum.CONTACT_LIST_VIEW);
				}
				
//			}
			
		}
		
		
		/**
		 * Save the contact in a DB
		 * mlCustomFieldsDao.saveByCollection(cfmodifiedList);
		 * 
		 */
		StringBuffer listName= new StringBuffer();	
		private boolean saveContacts() {
			
			logger.info("-- just entered in Save Contacts--");
			
			ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			String emailString = null;
			CustomFieldData cfData;
			Contacts contact;
			
			try{
				boolean isCreated = true;
				
				for(Object mlObj:mailingLists) {
								
					MailingList mailingList = (MailingList)mlObj;
		//						String isNewML = (String)session.getAttribute("isNewML");
					if(logger.isDebugEnabled()) {
						
						logger.debug(" >>>>>>>>>>>>>>>>>>Is MailingList to be created newly  :"+isNew);
					}
						
					
					if(isCreated) {
						
						 if(emailTBoxId.getValue().length() > 0) {
							 emailString  = emailTBoxId.getValue();
						 }
						contact = new Contacts(mailingList,  emailString, Constants.CONT_STATUS_PURGE_PENDING);
						
						//contact centric code
						contact.setUsers(currentUser);
						contact.setMlBits(contact.getMlBits() | mailingList.getMlBit());
						/*Set<MailingList> mlset = contact.getMlSet();
						mlset.add(mailingList);
						contact.setMlSet(mlset);*/
						
						
						
						contact.setExternalId("-1");
						if(addOneTBoxId.getParent().isVisible() && !(addOneTBoxId.getValue().trim().equals(""))) {
								
							contact.setAddressOne(addOneTBoxId.getValue());
						}
						 if(addTwoTBoxId.getParent().isVisible() && !(addTwoTBoxId.getValue().trim().equals(""))) {
							contact.setAddressTwo(addTwoTBoxId.getValue());
						}	
						if(cityTBoxId.getParent().isVisible() && !(cityTBoxId.getValue().trim().equals(""))) {
							contact.setCity(cityTBoxId.getValue());
						}	 
						if(countryTBoxId.getParent().isVisible() && !(countryTBoxId.getValue().trim().equals(""))) {
							contact.setCountry(countryTBoxId.getValue());
						}	 
						if(stateTBoxId.getParent().isVisible() && !(stateTBoxId.getValue().trim().equals(""))) {
							contact.setState(stateTBoxId.getValue());
						}	 
						if(firstNameTbId.getParent().isVisible() && !(firstNameTbId.getValue().trim().equals(""))) {
							contact.setFirstName(firstNameTbId.getValue());
						}	 
						if(lastNameTbId.getParent().isVisible() && !(lastNameTbId.getValue().trim().equals(""))) {
							contact.setLastName(lastNameTbId.getValue());
						}	 
						if(emailTBoxId.getParent().isVisible() && !(emailTBoxId.getValue().trim().equals(""))) {
							contact.setEmailId(emailTBoxId.getValue());
						}	 
						if(pinTBoxId.getParent().isVisible() && pinTBoxId.getValue() != null) {
							contact.setPin(pinTBoxId.getValue());
						}	
						if(mPhoneIBoxId.getParent().isVisible() && mPhoneIBoxId.getValue() != null) {
							contact.setPhone(mPhoneIBoxId.getValue());
							contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
						}else  {
							contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
						}
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
						
						contact.setCreatedDate(Calendar.getInstance());
						contact.setModifiedDate(Calendar.getInstance());
						
				
						
						//added after implementation for contact's optin medium
						contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
						
						if(mailingLists.size()==1) {
							try{
		//									boolean isValid = true;
		//									String emailId = emailTBoxId.getValue();
		
								Textbox cftb = null;
								String cfval = "";
								String datatype;
								int index=0;
								if(custTBList != null) {
									
									if(isNew.equalsIgnoreCase("true")) {
										if(Messagebox.show("The newly created custom field will be added to the mailing list."+"\n Do you want to continue?" +" ",   
												"Save Contact ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) != Messagebox.OK) {
											return false;
										}
										/*Messagebox.show("Are you sure, these selected custom fields are added to mailing list ? " +
												"or if you want to change the custom fields for the same list please come back once ", 
												"Information", Messagebox.OK, Messagebox.INFORMATION);*/
									}
									contactsDaoForDML.saveOrUpdate(contact);
									cfData = new CustomFieldData(contact);
									
									for (Object object : custTBList) {
										try {
											if(object instanceof Datebox) {
												
												Datebox dateBox = new Datebox();
												dateBox = (Datebox)object;
												DateFormat formatter ; 
															
												if(dateBox.getParent().isVisible()==false) {
													continue;
												}
		
												formatter = new SimpleDateFormat(dateBox.getFormat());
												
												if(dateBox.getValue()==null) {
													cfval = "";
													logger.debug("cfVal is...:"+cfval);
													logger.debug("cfval.length() > 0 : " + (cfval.length() > 0));
												}
												else {
													cfval = formatter.format(dateBox.getValue());
													logger.debug("***** cfVal is...:"+cfval);
												}
												datatype = (String)dateBox.getAttribute("datatype");
												index = ((Integer)dateBox.getAttribute("index")).intValue();
																
												logger.debug("cfval.length() > 0 : " + (cfval.length() > 0));
												logger.debug("CFValue : " + cfval + " - Datatype : " + datatype);
												//logger.debug("CustomFieldValidator : " + (CustomFieldValidator.validate(cfval, datatype)));
																
												cfData = storeCfData(cfData,index,cfval);
												//cfDataDao.saveOrUpdate(cfData);
												cfDataDaoForDML.saveOrUpdate(cfData);
			
											} //if(object instanceof Datebox)
											else if(object instanceof Textbox) {
												
												cftb = (Textbox)object;
												if(cftb.getParent().isVisible()==false) {
													continue;
												}
														
												cfval = cftb.getValue();
												if(cfval==null) {
													cfval = "";
												}
		
												datatype = (String)cftb.getAttribute("datatype");
												index = ((Integer)cftb.getAttribute("index")).intValue();
												
												String dateFormatStr = (String)cftb.getAttribute("dateFormat");
												
													
												logger.debug("cfval.length() > 0 : " + (cfval.length() > 0));
												logger.debug("CFValue : " + cfval + " - Datatype : " + datatype);
												//logger.debug("CustomFieldValidator : " + (CustomFieldValidator.validate(cfval, datatype)));
												
												cfData = storeCfData(cfData,index,cfval);
												//cfDataDao.saveOrUpdate(cfData);
												cfDataDaoForDML.saveOrUpdate(cfData);

											}	//else if(object instanceof Textbox)	
										} catch (Exception e) {
											logger.error("** Exception while Saving the customFields**",e);
											return false;
										}
									} //foreach custTBList
									
								} //if(custTBList!= null)	
													
									/*Boolean isPurgeContacts = (Boolean)session.getAttribute("isPurgeContacts");
									isPurgeContacts = (isPurgeContacts==null) ? false : isPurgeContacts;
											
									if(isPurgeContacts) {
										
										PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
										List<Long> tempList = new ArrayList<Long>();
										tempList.add(mailingList.getListId());
										purgeList.addAndStartPurging(tempList);
									}*/
									
									//purging the list
									PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
									List<Long> tempList = new ArrayList<Long>();
									tempList.add(mailingList.getListId());
									purgeList.addAndStartPurging(mailingList.getUsers().getUserId(), tempList);
									
									logger.debug("puging is complted now ");
									
									//set the last modified date to list
									mailingList.setLastModifiedDate(MyCalendar.getNewCalendar());
									mailingListDaoForDML.saveOrUpdate(mailingList);
									
									contactsDaoForDML.saveOrUpdate(contact);
									
									
								}catch(Exception e) {
											
									logger.error("Exception : ", e);
									return false;
								}
							} //if(mailingList.isCustField() && mailingLists.size()==1)
							else{
								try{
									/*Boolean isPurgeContacts = (Boolean)session.getAttribute("isPurgeContacts");
									isPurgeContacts = (isPurgeContacts==null) ? false : isPurgeContacts;
									
									if(isPurgeContacts) {
										
										PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
										List<Long> tempList = new ArrayList<Long>();
										tempList.add(mailingList.getListId());
										purgeList.addAndStartPurging(tempList);
									}*/
									
									String phone = null;
									if(mPhoneIBoxId.getValue() != null) {
										phone = ""+mPhoneIBoxId.getValue();
									}
									Contacts tempContact = contactsDao.findContactByValues(emailString, phone, null,  mailingList.getListId());
									logger.debug("For The List "+mailingList.getListId() +" >>>Contact Obj is Exist:: "+tempContact);
									if(tempContact != null) {
										if(listName.length()<1) {
											listName.append(mailingList.getListName());
//											listName = mailingList.getListName();
											continue;
										}
										listName.append(", "+mailingList.getListName());
//										listName += ", "+mailingList.getListName();
										continue;
									}
									//purging the list
									PurgeList purgeList = (PurgeList)SpringUtil.getBean("purgeList");
									List<Long> tempList = new ArrayList<Long>();
									tempList.add(mailingList.getListId());
									purgeList.addAndStartPurging(mailingList.getUsers().getUserId(), tempList);
									
									//set the last modified date to list
									mailingList.setLastModifiedDate(MyCalendar.getNewCalendar());
									mailingListDaoForDML.saveOrUpdate(mailingList);
									
									contactsDaoForDML.saveOrUpdate(contact);
									logger.debug(emailString + "-uploaded successfully");
									
									/*if(logger.isDebugEnabled()) {
										//MessageUtil.setMessage("Contact saved successfully.", "color:blue", "TOP");
									}*/
								}catch(DataIntegrityViolationException die) {
									
									if(listName.length()<1) {
										listName.append(mailingList.getListName());
//										listName = mailingList.getListName();
										continue;
									}
									listName.append( ", "+mailingList.getListName());
//									listName += ", "+mailingList.getListName();
									
									logger.debug("-----List name is_---"+mailingList.getListName());
									logger.error(" ** Exception :"+ die +" **");
									MessageUtil.setMessage("Contact already exists in following mailing list(s) :"+" "+listName, "color:red", "TOP");
		//									return true;
								}
								catch(Exception e) {
									logger.error(" **  Exception while saving the  contact:"+ e +" **");
									MessageUtil.setMessage("Contact already exists.", "color:red", "TOP");
									return false;
								}
							}
						
							logger.debug("-----List name is_---"+mailingList.getListName());
							
							int emailIdList=1;
							/*if(userActivitiesDao != null && mailingList.getUsers() != null) {
								userActivitiesDao.addToActivityList(ActivityEnum.CONTS_CRE_CONT_p1contactNo_p2mlName, mailingList.getUsers(), 
										emailIdList+"",mailingList.getListName());
							}*/
							if(userActivitiesDaoForDML != null && mailingList.getUsers() != null) {
								userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_CRE_CONT_p1contactNo_p2mlName, mailingList.getUsers(), 
										emailIdList+"",mailingList.getListName());
							}
						}	//if(created)
					} // for(Object mlObj:mailingLists)
				
				
				
				if(logger.isDebugEnabled()) {
					logger.debug(" uploading into all mailing lists is finished ");
				}
			}
			catch(Exception e) {
				
				logger.error(" **  Exception while saving the  contact:"+ e +" **");
				return false;
			}/*finally {
				session.removeAttribute("isNewML");
				session.removeAttribute("AddSingle_Ml");
				//session.getAttribute("mailingList");
			}*/
			
			return true;
			
		}	// saveContacts()
	
	
	
	public CustomFieldData storeCfData(CustomFieldData customFieldData,int index, String cfVal) {
		
		switch(index){
			case 1:{
				customFieldData.setCust1(cfVal);
				return customFieldData;
			}
			case 2:{
				customFieldData.setCust2(cfVal);
				return customFieldData;
			}
			case 3:{
				customFieldData.setCust3(cfVal);
				return customFieldData;
			}
			case 4:{
				customFieldData.setCust4(cfVal);
				return customFieldData;
			}
			case 5:{
				customFieldData.setCust5(cfVal);
				return customFieldData;
			}
			case 6:{
				customFieldData.setCust6(cfVal);
				return customFieldData;
			}
			case 7:{
				customFieldData.setCust7(cfVal);
				return customFieldData;
			}
			case 8:{
				customFieldData.setCust8(cfVal);
				return customFieldData;
			}
			case 9:{
				customFieldData.setCust9(cfVal);
				return customFieldData;
			}
			case 10:{
				customFieldData.setCust10(cfVal);
				return customFieldData;
			}
			case 11:{
				customFieldData.setCust11(cfVal);
				return customFieldData;
			}
			case 12:{
				customFieldData.setCust12(cfVal);
				return customFieldData;
			}
			case 13:{
				customFieldData.setCust13(cfVal);
				return customFieldData;
			}
			case 14:{
				customFieldData.setCust14(cfVal);
				return customFieldData;
			}
			case 15:{
				customFieldData.setCust15(cfVal);
				return customFieldData;
			}
			case 16:{
				customFieldData.setCust16(cfVal);
				return customFieldData;
			}
			case 17:{
				customFieldData.setCust17(cfVal);
				return customFieldData;
			}
			case 18:{
				customFieldData.setCust18(cfVal);
				return customFieldData;
			}
			case 19:{
				customFieldData.setCust19(cfVal);
				return customFieldData;
			}
			case 20:{
				customFieldData.setCust20(cfVal);
				return customFieldData;
			}
		}
		return customFieldData;
	}
	
	/**
	 * reset the contct form
	 */
	public void enterMoreContacts(){
	
	 try {
//			MessageUtil.clearMessage();
			if(logger.isDebugEnabled()) logger.debug("-- just entered --");
			//invalidEmails = "";
			addOneTBoxId.setValue("");
			addTwoTBoxId.setValue("");
			cityTBoxId.setValue("");
			stateTBoxId.setValue("");
			countryTBoxId.setValue("");
			pinTBoxId.setValue(null);
			firstNameTbId.setValue("");
			lastNameTbId.setValue("");
			emailTBoxId.setValue("");
//			submitBtnId.setDisabled(true);
			statusLabelId.setValue("");
			mPhoneIBoxId.setValue(null);
			gendarListBxId.setSelectedIndex(0);
			birthDayDateBoxId.setValue((Date)null);
			anniversaryDateBoxId.setValue((Date)null);
			
			addCustDivId.setVisible(false);
			if(mailingLists.size()==1){
				for(Object obj:mailingLists){
					MailingList mailingList =(MailingList)obj;
					if(mailingList.isCustField() && mailingLists.size()==1) {
			//if(custFieldDivId.isVisible() && mailingLists.size()==1) {
						Textbox tempTbox =null;
						MyDatebox tempDateBox = null;
						for(int i = 0; i< custInputCount; i++) {
					
							if(cfDivId.getFellowIfAny("custTextBox_"+i) != null) {
								tempTbox = (Textbox)cfDivId.getFellowIfAny("custTextBox_"+i);
								tempTbox.setValue("");
							}
							else if(cfDivId.getFellowIfAny("customDateBox_"+i) !=null ) {
								tempDateBox = (MyDatebox)cfDivId.getFellowIfAny("customDateBox_"+i);
								tempDateBox.setValue((Date)null);
							}
					
						} // forEach i
						
						
						if(isNew.equalsIgnoreCase("true")){
							
							session.setAttribute("isNewML","false");
							xcontents.invalidate();
							Redirect.goTo(PageListEnum.CONTACT_ADDSINGLE);
						}
						
				
					} else 
						return;
				}
			}
		} catch (Exception e) {
			logger.debug("Exception while getting  from a reset the contact..:",e);
		}
	}
	
	/**
	 * 
	 * 
	 */
	
	public void showAll(Rows r) {
		
		try {
			List li=(List)r.getChildren();
			
			// Display default fields of Mailing list
			for(Object obj : li) {
				Row row =(Row)obj;
				row.setVisible(true);
			}
			// Display custom fields , if exist.
			showAllAnchorId.setVisible(false);
			
			if(custTBList != null){
				//customFieldSize=custTBList.size();
				custFieldLabelId.setVisible(true);
				cfDivId.setVisible(true);
				
				for(Object obj:custGridRowsId.getChildren()){
								
					Row row= (Row)obj;
					row.setVisible(true);
				}
//				custFieldDivId.setVisible(true);
				customFieldSize=custTBList.size();
				return;
			}	// if(custTBList != null)
		} catch (Exception e) {
			logger.error("Exception while hiding the rows ", e);
		}
	}
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
			try {
				if(event.getTarget() instanceof A) {
					
					if(customFieldSize==1 ){
						custFieldLabelId.setVisible(false);
						cfDivId.setVisible(false);
					}
					customFieldSize--;
					A eventAnchor = (A)event.getTarget();
					Row row = (Row)eventAnchor.getParent();
					row.setVisible(false);
					
					showAllAnchorId.setVisible(true);
				}
				else if(event.getTarget() instanceof Image){
					Image img= (Image)event.getTarget();
					Row row = (Row)img.getParent();
					row.detach();
//					row.setVisible(false);
					addNewCustFieldTBId.setVisible(true);
					mailingListCount--;
					if(mailingListCount==1){
						custFieldsGbId.setVisible(false);
					}
				}
				else if(event.getTarget() instanceof Listbox) {
					Listbox tempListbox = (Listbox)event.getTarget();
					
					if(tempListbox.getSelectedItem().getLabel().trim().equals("Date")) {
					 Combobox  combobox = (Combobox)tempListbox.getAttribute("getCombobox");
					 combobox.setVisible(true);
					}
				}
				
			} catch (Exception e) {
				
				logger.error("Exception while hiding  the custField rows...:",e);
			}	
		}
	}	
	
