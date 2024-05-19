package org.mq.marketer.campaign.controller.contacts;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListEditingController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private int configuredCfMaxFieldIndex=0;
//	private Grid custFieldsGbId;
	private Textbox listNameTbId,descTbId;
//	private Column dateFormatColumnId;
	private Checkbox optInChkBox, parentalConsentChkBox, welcomeChkBox;
	private Listbox optInEmailsLbId, parentalConsentEmailLbId, welcomeEmailsLbId;
	private Div doubleOptLtDiv, parentalOptLtDiv, welcomeMailDiv;
	private A editWelcomeMsgBtnId,previewemailBtnId,parentalEditMsgBtnId,parentalPreviewBtnId; 
   	private A editMsgBtnId;
   	private A previewBtnId;
   	private Image welcomePreviewemailIconId,optInPreviewIconId,parentalPreviewIconId;
	private UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
	private UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	private Session session;
   	Users currentUser = GetUser.getUserObj();
   	CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
   	MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
   	private MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
   	
   	private Set<Long> listIdsSet; 
	/*private static String contctGenFieldStr = PropertyUtil.getPropertyValue("defaultContactMapFieldList");
	private static String[] defaultFieldArray = StringUtils.split(contctGenFieldStr, ','); 
	private static String[] udfSetStr ={"UDF1" ,"UDF2" ,"UDF3","UDF4", "UDF5","UDF6","UDF7","UDF8",
											"UDF9","UDF10",	"UDF11","UDF12","UDF13","UDF14","UDF15"};
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";*/
	
	//private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	//private static Map<String, String> optFiledDataType = new HashMap<String, String>();
	//private static Map<String, String> contactFieldMap = new HashMap<String, String>();
	
	/*static{
		genFieldContMap.put("Email" , "Email");
		genFieldContMap.put("FirstName" , "First Name");
		genFieldContMap.put("LastName" , "Last Name");
		genFieldContMap.put("Street" , "Street");
		genFieldContMap.put("City" , "City");
		genFieldContMap.put("State" , "State");
		genFieldContMap.put("Country" , "Country");
		genFieldContMap.put("ZIP" , "ZIP");
		genFieldContMap.put("MobilePhone" , "Mobile");
		genFieldContMap.put("CustomerID" , "RetailPro ID" );
		genFieldContMap.put("Gender" , "Gender");
		genFieldContMap.put("HomeStore" , "Home Store");
		genFieldContMap.put("BirthDay" , "BirthDay");
		genFieldContMap.put("Anniversary" , "Anniversary");
		
		//String","Date","Number","Double","Boolean"
		optFiledDataType.put("Email" , "String");
		optFiledDataType.put("First Name" , "String");
		optFiledDataType.put("Last Name" , "String");
		optFiledDataType.put("Street" , "String");
		optFiledDataType.put("City" , "String");
		optFiledDataType.put("State" , "String");
		optFiledDataType.put("Country" , "String");
		optFiledDataType.put("ZIP" , "Number");
		optFiledDataType.put("Mobile" , "Number");
		optFiledDataType.put("RetailPro ID" , "String" );
		optFiledDataType.put("Gender" , "String");
		optFiledDataType.put("Home Store" , "String");
		optFiledDataType.put("BirthDay" , "Date");
		optFiledDataType.put("Anniversary" , "Date");
		
		
		
		contactFieldMap.put("Email", "emailId");
		contactFieldMap.put("First Name", "firstName");
		contactFieldMap.put("Last Name", "lastName");
		contactFieldMap.put("Street", "addressOne");
		contactFieldMap.put("Address Two", "addressTwo");
		contactFieldMap.put("City", "city");
		contactFieldMap.put("State", "state");
		contactFieldMap.put("Country", "country");
		contactFieldMap.put("ZIP", "pin");
		contactFieldMap.put("Mobile", "phone");
		contactFieldMap.put("RetailPro ID", "externalId" );
		contactFieldMap.put("Addressunit ID", "hpId" );
		contactFieldMap.put("Gender", "gender");
		contactFieldMap.put("BirthDay", "birthDay");
		contactFieldMap.put("Anniversary", "anniversary");
		contactFieldMap.put("Home Store", "homeStore");
		
		
	}*/
	private final String selectStr = "Select Message";
	private final String defaultStr = "Default Message";
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("List Settings","",style,true);
		session = Sessions.getCurrent();
		
		
		/*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getLoginUserObj());
		}
		/*MailingList ml = sessionScope.get("mailingList");
		if(ml != null){
			listNameTbId.setValue(ml.getListName());
			descTbId.setValue(ml.getDescription());
		}*/
		
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		getWelcomeTemplateList();
		getParentalConsentTemplateList();
		getAutoWelcomeTemplateList();
		logger.debug("selected List Name is >>>>>>>>>> ::"+ml.getListId());
		if(ml != null) {
			listNameTbId.setValue(ml.getListName());
			listNameTbId.setDisabled(true);
			descTbId.setValue(ml.getDescription() == null ? "":ml.getDescription());
			if(ml.getCheckDoubleOptin()) {
				optInChkBox.setChecked(true);
				logger.debug("customtemplate====>"+ml.getCustTemplateId());
				onCheck$optInChkBox();
				
				
			}
			//logger.debug("customtemplate====>"+ml.isCheckParentalConsent());
			if(ml.isCheckParentalConsent()) {
				
				parentalConsentChkBox.setChecked(true);
				//logger.debug("Parental Consent customtemplate====>"+ml.getConsentCutomTempId());
				onCheck$parentalConsentChkBox();
				
				
			}
			
			if(ml.isCheckWelcomeMsg()) {
				
				welcomeChkBox.setChecked(true);
				//logger.debug("Parental Consent customtemplate====>"+ml.getConsentCutomTempId());
				onCheck$welcomeChkBox();				
				
			}
			
		}
		
		/*try {
			rendererList();
		} catch (Exception e) {
			logger.error("Exception :: error getting from the rendererList method",e);
		}*/
	}
	
	public void onCheck$parentalConsentChkBox() {
		
		parentalOptLtDiv.setVisible(!parentalOptLtDiv.isVisible());
		
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		if(ml != null ){ 
			if(ml.isCheckParentalConsent()){
				
				if(ml.getConsentCutomTempId() != null){
					CustomTemplates customTemplate = customeTemplatesDao.findCustTemplateById(ml.getConsentCutomTempId());
					
					//logger.info("got the custom template =====>"+customTemplate.getTemplateName());
					if(customTemplate != null) {
						
						for(Listitem item:parentalConsentEmailLbId.getItems()) {
							
							CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
							
							if(curItemTemp == null) continue;
							
							if(curItemTemp.getTemplateId().longValue() == ml.getConsentCutomTempId().longValue()){
								item.setSelected(true);
								break;
							}
						}//for
					}//if
				}else{
					
					parentalConsentEmailLbId.getItemAtIndex(0).setLabel(defaultStr);
					parentalConsentEmailLbId.getItemAtIndex(0).setSelected(true);
				}
			}
		}//if
		onSelect$parentalConsentEmailLbId();
		
	}
	
	public void onCheck$optInChkBox(){
		
		doubleOptLtDiv.setVisible(!doubleOptLtDiv.isVisible());
		
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		//logger.info("got the custom template =====>"+ ml.getCustTemplateId());
		//changeDefaultValueForCheckBoxItems(ml);
		if(ml != null ){ 
			if(ml.getCheckDoubleOptin()) {
				
				if(ml.getCustTemplateId() != null){
					CustomTemplates customTemplate = customeTemplatesDao.findCustTemplateById(ml.getCustTemplateId());
					
					//logger.info("got the custom template =====>"+customTemplate.getTemplateName());
					if(customTemplate != null) {
						
						for(Listitem item:optInEmailsLbId.getItems()) {
							
							CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
							
							if(curItemTemp == null) continue;
							
							if(curItemTemp.getTemplateId().longValue() == ml.getCustTemplateId().longValue()){
								item.setSelected(true);
								break;
							}
						}//for
					}//if
				}else{
					
					optInEmailsLbId.getItemAtIndex(0).setLabel(defaultStr);
					optInEmailsLbId.getItemAtIndex(0).setSelected(true);
				}
			}
		}//if
		onSelect$optInEmailsLbId();
	}
	
	public void onCheck$welcomeChkBox() {
		
		welcomeMailDiv.setVisible(!welcomeMailDiv.isVisible());
		
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		//logger.info("got the custom template =====>"+ ml.getWelcomeCustTempId());
		
		if(ml != null ){ 
			if(ml.isCheckWelcomeMsg()){
				
				if(ml.getWelcomeCustTempId() != null){
					CustomTemplates customTemplate = customeTemplatesDao.findCustTemplateById(ml.getWelcomeCustTempId());
					
					//logger.info("got the custom template =====>"+customTemplate.getTemplateName());
					if(customTemplate != null) {
						
						for(Listitem item:welcomeEmailsLbId.getItems()) {
							
							CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
							
							if(curItemTemp == null) continue;
							
							if(curItemTemp.getTemplateId().longValue() == ml.getWelcomeCustTempId().longValue()){
								item.setSelected(true);
								break;
							}
						}//for
					}//if
				}else{
					
					welcomeEmailsLbId.getItemAtIndex(0).setLabel(defaultStr);
					welcomeEmailsLbId.getItemAtIndex(0).setSelected(true);
				}
			}
		}//if
		
		onSelect$welcomeEmailsLbId();
	}
	
	/*public void rendererList() throws Exception {
		List<MLCustomFields> conCfList;
				conCfList = getCustomFields();
				
				if(conCfList!=null && conCfList.size()>0) {
					
					for (MLCustomFields mlCustomFields : conCfList) {
						if(mlCustomFields.getFieldIndex() > configuredCfMaxFieldIndex) {
							configuredCfMaxFieldIndex = mlCustomFields.getFieldIndex(); 
						}
					} // for
					
					try {
						addCFEntryRule(conCfList);
					} catch (Exception e) {
						logger.error("Exception :: error getting from the addCFEntryRule method",e);
					}
				}
	} // rendererList
	
*/	
	
	

	
	
	
	
	
	
	/*public List<MLCustomFields> getCustomFields() {
			
			MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			return mlCustomFieldsDao.findAllByList(ml);
	}
	*/
	
	/*public List<POSMapping> getCustomFields() {
		
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		
		POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		return posMappingDao .findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", GetUser.getUserId());
	}*/
	
	/**
	 * Add Custom Field Rule
	 */
	/*public void addCFEntryRule(List<MLCustomFields> mlcfLst)throws Exception {
		try {

			MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			
			if(mlcfLst == null ) {

				Row row = new Row();
				MLCustomFields mlCustomFields = new MLCustomFields("","String",ml,"");
				addRule(row, mlCustomFields);
				row.setParent(custFieldsGbId.getRows());
			}
			else {
				for (MLCustomFields mlCustomFields : mlcfLst) {
					Row row = new Row();
					addRule(row, mlCustomFields);
					row.setParent(custFieldsGbId.getRows());
				} // for
			}
			
			custFieldsGbId.invalidate();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			logger.error(" - ** Exception : Problem adding the Custom Field entry rule--" + e + " **");
		}
	}*/
	
	
//	List<String> existDBField = new ArrayList<String>();
	
	
	
//	 DelEventListener delEventListener = new DelEventListener();
	/**
	 * 
	 * @param row
	 * @param mlcf
	 */
	 
	 ////////////////////////////////////
	 
	 	//DataType
	 			/*private Listbox createDataTypeListBox() {
	 				
	 				Listbox dataTypelb = new Listbox();
	 				
	 				Listitem tempItem = new Listitem("String");
	 				tempItem.setParent(dataTypelb);
	 				
	 				//String","Date","Number","Double","Boolean"
	 				tempItem = new Listitem("Date");
	 				tempItem.setParent(dataTypelb);
	 				
	 				tempItem = new Listitem("Number");
	 				tempItem.setParent(dataTypelb);
	 				
	 				tempItem = new Listitem("Double");
	 				tempItem.setParent(dataTypelb);
	 				
	 				tempItem = new Listitem("Boolean");
	 				tempItem.setParent(dataTypelb);
	 				dataTypelb.setMold("select");
	 				return dataTypelb;
	 				
	 			} //createDataTypeListBox()

	 			
	 			//DateFormat
	 			private Listbox createDateFormatListbox() {

	 				Listbox dateFormatListBx = new Listbox();
	 				
	 				Listitem tempItem = new Listitem("dd/MM/yyyy");
	 				tempItem.setParent(dateFormatListBx);
	 				
	 				//String","Date","Number","Double","Boolean"
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
	 				
	 				dateFormatListBx.setMold("select");
	 				return dateFormatListBx;
	 			} //createDateFormatListbox()
	 			
	 			

	 			//create ContactGenField ListBox
	 			private Listbox createContactPosMappingListbox() {
	 				
	 				Listbox dateFormatListBx = new Listbox();
	 				Listitem tempItem = null;
	 				
	 				//genralFieldList
	 				for (int i=0; i< defaultFieldArray.length; i++) {
//	 						tempStr = (String)contactGenFieldList.get(i);
	 					tempItem = new Listitem(defaultFieldArray[i]);
	 					tempItem.setParent(dateFormatListBx);
	 				}
	 				
	 				//UDF FieldList
	 				for(int i=0; i<udfSetStr.length; i++) { 
	 					tempItem = new Listitem(udfSetStr[i]);
	 					tempItem.setParent(dateFormatListBx);
	 				}
	 				dateFormatListBx.setMold("select");
	 				return dateFormatListBx;
	 				
	 			}// createContactPosMappingListbox()
	 			*/
	 
	 
	/*public void addRule(Row row, MLCustomFields mlcf) {
		try {
			Image img;

			Textbox tb = new Textbox(mlcf.getCustFieldName());
				tb.setInplace(true);
				tb.setWidth("95%");
			tb.setParent(row);
			
			Listbox lb = new Listbox();
				lb.setMold("select");
				lb.setWidth("40%");
				lb.appendItem("String", "String");
				lb.appendItem("Date", "Date");
				lb.appendItem("Number", "Number");
				lb.appendItem("Double", "Double");
				lb.appendItem("Boolean", "Boolean");
				lb.addEventListener("onClick", delEventListener);
			lb.setParent(row);
			
			//TODO need to write perfect code
			String tempVal = mlcf.getDataType();
			//logger.info("DATATYPE="+tempVal);

			for (int i = 0; i < lb.getItemCount(); i++) {
				if(tempVal.equals(lb.getItemAtIndex(i).getLabel())) {
					lb.setSelectedIndex(i);
					break;
				}
			} // for
			
			Listbox dateFormatLisBox = new Listbox();
			dateFormatLisBox.setMold("select");
			Listitem tempItem = new Listitem("dd/MM/yyyy");
			tempItem.setParent(dateFormatLisBox);
			
			tempItem = new Listitem("dd-MM-yyyy");
			tempItem.setParent(dateFormatLisBox);
			
			tempItem = new Listitem("MM/dd/yyyy");
			tempItem.setParent(dateFormatLisBox);
			
			tempItem = new Listitem("dd/MM/yyyy HH:mm");
			tempItem.setParent(dateFormatLisBox);
			
			tempItem = new Listitem("MM/dd/yyyy HH:mm");
			tempItem.setParent(dateFormatLisBox);
			tempItem = new Listitem("MM/dd/yyyy HH:mm:ss");
			tempItem.setParent(dateFormatLisBox);
			
			
			
			
			
			
			
			logger.info("mlcf.getFormat() is ::"+mlcf.getFormat());
			if(mlcf.getFormat() != null && mlcf.getFormat().trim().length() >0) {
				List tempItems = dateFormatLisBox.getChildren();
				for (Object object : tempItems) {
					Listitem temItem = (Listitem)object;
					if( mlcf.getFormat().trim().equals(temItem.getLabel())) {
						dateFormatLisBox.setSelectedItem(temItem);
						dateFormatLisBox.setVisible(true);
						break;
					}
				}
			}else {
				dateFormatLisBox.setSelectedIndex(0);
				dateFormatLisBox.setVisible(false);
			}
			
			dateFormatLisBox.setParent(row);
			
			
			logger.info("mlcf.getDefaultValue() is ::"+mlcf.getDefaultValue());
			tb = new Textbox(mlcf.getDefaultValue());
				tb.setInplace(true);
				tb.setWidth("95%");
			tb.setParent(row);

			img = new Image("/img/icons/delete_icon.png");
			img.setTooltiptext("Delete");
			img.setAttribute("mlcfObj", mlcf);
			img.setStyle("cursor:pointer;");
			img.addEventListener("onClick", delEventListener);
			img.setParent(row);
		
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	*/
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multy user acc
	public  void getWelcomeTemplateList() {
		List<CustomTemplates> wlist = null;
		try {
			
			
			
			wlist = customeTemplatesDao.getTemplatesByType(listIdsSet, "welcomemail");
			
			
			Components.removeAllChildren(optInEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(optInEmailsLbId);
			if(wlist != null){
				
				for (CustomTemplates customTemplates : wlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(optInEmailsLbId);
					
					
				}
			}
			if(optInEmailsLbId.getItemCount() > 0) optInEmailsLbId.setSelectedIndex(0);
			
			
			logger.debug(" - Got welcome lists for the user. size : "+wlist.size());
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			
		}
	}
	
	private void changeDefaultValueForCheckBoxItems(MailingList posMaplingList) {
		if(posMaplingList != null && posMaplingList.getCheckDoubleOptin()) {
			try {
				List<Listitem> optInEmailList = optInEmailsLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Optin Message")) {
						list.setLabel("Default Message");
						editMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Default Message List - " + e + " **");
			}
		}
	}
	private void changeDefaultValueForCheckBoxItemsForWlcome(MailingList posMaplingList) {
		if(posMaplingList != null && posMaplingList.isCheckWelcomeMsg()) {
			try {
				List<Listitem> optInEmailList = welcomeEmailsLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Welcome Email Message")) {
						list.setLabel("Default Message");
						editWelcomeMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			}
		}
	}
	
	private void changeDefaultValueForCheckBoxItemsForParentalConsent(MailingList posMaplingList) {
		if(posMaplingList != null && posMaplingList.isCheckWelcomeMsg()) {
			try {
				List<Listitem> optInEmailList = parentalConsentEmailLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Consent Email Template")) {
						list.setLabel("Default Message");
						parentalEditMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			}
		}
	}
	
	public boolean validateSelectedTemplate() {
		boolean returnValue = false;
		
		if(optInChkBox.isChecked()){
			if(optInEmailsLbId.getSelectedItem().getLabel().equals(selectStr) ) {
				if(optInEmailsLbId.getItems().size() == 1 ) {
					MessageUtil.setMessage("Please create at least one Double Opt-In message under Auto Emails.", "color:red","TOP");
					returnValue = true;
					
				}else{
					
					MessageUtil.setMessage("Please Select Double Opt-In message.", "color:red","TOP");
					returnValue = true;
				}
				
			}
			
			
		}
		if(welcomeChkBox.isChecked() && welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
			if(welcomeEmailsLbId.getItems().size() == 1) {
				MessageUtil.setMessage("Please create at least one Welcome message under Auto Emails.", "color:red","TOP");
				returnValue = true;
			}else  {
				MessageUtil.setMessage("Please Select Welcome message.", "color:red","TOP");
				returnValue = true;
			}
		}
		return returnValue;
	}
	
	public  void getAutoWelcomeTemplateList() {
		List<CustomTemplates> wlist = null;
		try {
			
			
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			wlist = customeTemplatesDao.getTemplatesByType(listIdsSet,"webformWelcomeEmail");
			
			
			Components.removeAllChildren(welcomeEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(welcomeEmailsLbId);
			
			if(wlist != null) {
			
				for (CustomTemplates customTemplates : wlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(welcomeEmailsLbId);
					
					
				}
			}
			if(welcomeEmailsLbId.getItemCount() > 0) welcomeEmailsLbId.setSelectedIndex(0);
			
			
			logger.debug(" - Got welcome lists for the user. size : "+wlist.size());
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			
		}
	}
	
	
	public  void getParentalConsentTemplateList() {
		List<CustomTemplates> consentlist = null;
		try {
			
			
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			consentlist = customeTemplatesDao.getTemplatesByType(listIdsSet,"parentalConsent");
			
			
			Components.removeAllChildren(parentalConsentEmailLbId);

			
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(parentalConsentEmailLbId);
			
			
			if(consentlist != null) {
				for (CustomTemplates customTemplates : consentlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(parentalConsentEmailLbId);
					
					
				}
			
			}
			if(parentalConsentEmailLbId.getItemCount() > 0 ) parentalConsentEmailLbId.setSelectedIndex(0);
			
			
			logger.debug(" - Got consent email lists for the user. size : "+consentlist.size());
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");
			
		}
	}
	
	public void onClick$addNewCustFieldTBId() {
	try {
		Redirect.goTo(PageListEnum.ADMIN_LIST_SETTINGS);
		
	} catch (Exception e) {
		logger.error("Exception :: error getting from the addCFEntryRule method",e);
	}
}
	
	
	
	/*public void onClick$addNewCustFieldTBId() {
		try {
			Row row = new Row();
			addRule(row, null);
			row.setParent(custFieldsGbId.getRows());
			
		} catch (Exception e) {
			logger.error("Exception :: error getting from the addCFEntryRule method",e);
		}
	}*/
	
	/****click the back button  redirect to myList.zul*****/
	public void onClick$backBtn() {
		try {
			//		Utility.getComponentById("xcontents").setSrc("zul/contact/myLists.zul");
			Redirect.goTo(PageListEnum.CONTACT_LIST_VIEW);
		} catch (Exception e) {
			logger.error("Exception :: error getting while redirecting the page",e);
		}
	}
	/****if we click the submit button  redirect to myList.zul*****/
	public void onClick$submitBtn() {
		try {
			if(validateSelectedTemplate()) {
				return;
			}
			updateList(listNameTbId.getValue(),descTbId.getValue());
		
		} catch (Exception e) {
			logger.error("Exception :: error getting from updateList method ",e);
		}
	}


	public void onClick$editMsgBtnId() {
		//logger.info("just Call addnew button");
		logger.info("custom template value "+optInEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", optInEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN);
		session.setAttribute("fromAddNewBtn","contact/edit");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		//Redirect.goTo("contact/customMListWelcomeMsg");
	}
	
	public void onClick$editWelcomeMsgBtnId() {
		//logger.info("just Call addnew button");
		logger.info("custom template value "+optInEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", welcomeEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL);
		session.setAttribute("fromAddNewBtn","contact/edit");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		//Redirect.goTo("contact/customMListWelcomeMsg");
	}
	public void onClick$parentalEditMsgBtnId() {
		//logger.info("just Call addnew button");
		session.setAttribute("editCustomTemplate", parentalConsentEmailLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT);
		session.setAttribute("fromAddNewBtn","contact/edit");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
	//	Redirect.goTo("contact/parentalConsentMsg");
	}
	
	
	 public void onSelect$optInEmailsLbId() {
		 
		 if(optInEmailsLbId.getSelectedIndex() == 0 && 
				 optInEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
			 
			 editMsgBtnId.setVisible(false);
			 previewBtnId.setVisible(true);
				optInPreviewIconId.setVisible(true);
			 
		 } else if(optInEmailsLbId.getSelectedIndex() == 0 && 
				 optInEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
			 
			 editMsgBtnId.setVisible(false);
			 previewBtnId.setVisible(false);
				optInPreviewIconId.setVisible(false);
			 
		 }else{
			 editMsgBtnId.setVisible(true);
			 previewBtnId.setVisible(true);
				optInPreviewIconId.setVisible(true);
		 }
		 /*if(optInEmailsLbId.getItemAtIndex(0).getLabel().equalsIgnoreCase("Select Optin Message")) {
				if(optInEmailsLbId.getSelectedIndex() <= 0) {
					editMsgBtnId.setVisible(false);
					previewBtnId.setVisible(false);
					optInPreviewIconId.setVisible(false);
				}else {
					optInPreviewIconId.setVisible(true);
					editMsgBtnId.setVisible(true);
					previewBtnId.setVisible(true);
					
				}
			}else if(optInEmailsLbId.getItemAtIndex(0).getLabel().equalsIgnoreCase("Default Message")) {
				if(optInEmailsLbId.getSelectedIndex() <= 0) {
					editMsgBtnId.setVisible(false);
				}else {
					optInPreviewIconId.setVisible(true);
					editMsgBtnId.setVisible(true);
					previewBtnId.setVisible(true);
					
				}
			}*/
	 }
	
	private Window previewWin;
	private  Iframe previewWin$html;
	public void onClick$previewBtnId() {
		String templateContent = "";
		
		//previewWin$html.setContent(templateContent);
		
		CustomTemplates customTemplates = optInEmailsLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			
		templateContent = 	PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
			
		}else {
			templateContent = customTemplates.getHtmlText();
			if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					 return;
				 }
			}
		}
	//	previewWin$html.setContent(templateContent);
		
		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		previewWin.setVisible(true);
		
	}//onClick$previewBtnId

	
	public void onClick$previewemailBtnId() {
		String templateContent = "";
		
		//previewWin$html.setContent(templateContent);
		
		CustomTemplates customTemplates = welcomeEmailsLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			
		templateContent = 	PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
			
		}else {
			if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType()!=null && customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					 return;
				 }
			}
		}
	//	previewWin$html.setContent(templateContent);
		
		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		
		previewWin.setVisible(true);
		
	}//onClick$previewBtnId
	
	
	
	public void onClick$parentalPreviewBtnId() {
		
		String templateContent = "";
		
		//previewWin$html.setContent(templateContent);
		
		CustomTemplates customTemplates = parentalConsentEmailLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
		}else {
			templateContent = customTemplates.getHtmlText();
			if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					 return;
				 }
			}
		}
	//	previewWin$html.setContent(templateContent);
		
		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		
		previewWin.setVisible(true);
		
		
		
	}//onClick$parentalPreviewBtnId
	
	
	public void updateList(String name, String desc) throws Exception {
		try{
			MessageUtil.clearMessage();
			
			if(name==null || name.trim().equals("")) {
				MessageUtil.setMessage("Mailing list name cannot be empty.", "color:red", "TOP");
				return;
			}
			
			
			
			boolean isMlModified=false;
			
			MailingList mlInSession = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			MailingListDaoForDML mailingListDaoForDML=(MailingListDaoForDML) SpringUtil.getBean(OCConstants.MAILINGLIST_DAO_FOR_DML);
			MailingList ml = mailingListDao.findById(mlInSession.getListId());
			
			String oldName = ml.getListName();
			
			if(!ml.getListName().trim().equals(name.trim())) {
				isMlModified=true;
				ml.setListName(name);
			}
			
			String mlDesc = ml.getDescription() == null ? "":ml.getDescription();
			if(!mlDesc.trim().equals(desc.trim())) {
				isMlModified=true;
				ml.setDescription(desc);
			}
			
			//TODO need to set opt-in and parental consent settings
			//************************************************************
			if(optInChkBox.isChecked()) {
				
				ml.setCheckDoubleOptin(true);
				if(optInEmailsLbId.getSelectedIndex() != 0) {
					isMlModified=true;
					ml.setCustTemplateId(( (CustomTemplates)optInEmailsLbId.getSelectedItem().getValue()).getTemplateId());
				
				}else {
					isMlModified=true;
					ml.setCustTemplateId(null);
				}
				
			}//if
			else{
				
				ml.setCheckDoubleOptin(false);
				isMlModified=true;
				ml.setCustTemplateId(null);
				
			}
			
			
			
			if(welcomeChkBox.isChecked()) {
				
				ml.setCheckWelcomeMsg(true);
				if(welcomeEmailsLbId.getSelectedIndex() != 0) {
					isMlModified=true;
					ml.setWelcomeCustTempId(( (CustomTemplates)welcomeEmailsLbId.getSelectedItem().getValue()).getTemplateId());
				
				}else {
					isMlModified=true;
					ml.setWelcomeCustTempId(null);
				}
				
			}//if
			else{
				
				ml.setCheckWelcomeMsg(false);
				isMlModified=true;
				ml.setWelcomeCustTempId(null);
				
			}
			
			if(parentalConsentChkBox.isChecked()) {
				
				ml.setCheckParentalConsent(true);
				if(parentalConsentEmailLbId.getSelectedIndex() != 0) {
					isMlModified=true;
					ml.setConsentCutomTempId(( (CustomTemplates)parentalConsentEmailLbId.getSelectedItem().getValue()).getTemplateId());
				
				}else {
					isMlModified=true;
					ml.setConsentCutomTempId(null);
				}
				
			}//if
			else{
				
				ml.setCheckParentalConsent(false);
				isMlModified=true;
				ml.setConsentCutomTempId(null);
				
			}
			
			// Modify if Mailing list object if modified
			if(isMlModified) {
				ml.setLastModifiedDate(MyCalendar.getNewCalendar());
				mailingListDaoForDML.saveOrUpdate(ml);
			}
			//*******************************************************
			
			
			MessageUtil.setMessage("List settings updated successfully.", "color:blue", "TOP");
			Long userId = GetUser.getUserId();
			
			/*if(userActivitiesDao != null && userId != null) {
				userActivitiesDao.addToActivityList(ActivityEnum.CONTS_EDIT_ML_p1mlName, GetUser.getUserObj(), ml.getListName());
			}*/
			if(userActivitiesDaoForDML != null && userId != null) {
				userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_EDIT_ML_p1mlName, GetUser.getLoginUserObj(), ml.getListName());
			}
			
			logger.info(" - contact list '" + oldName + "' settings are edited ");
			
			
			//*****************************************************************
			logger.debug(">>>>>>>>>>>>>>>>>>  CF Update <<<<<<<<<<<<<<<<");
			
			
			
//			List<MLCustomFields> cfmodifiedList= new ArrayList<MLCustomFields>();

			//TODO need to modify the code for updating the custom fields

		/*	if(true) return;
			// Set the Custom fields data
//			MLCustomFields tempCfObj;
			POSMapping mapObj;
			
			for (Component rowComp : lst) {
				Row row = (Row)rowComp;
			
				if(!row.isVisible()) continue;
				
				List<Component> comps = row.getChildren();
				
				//logger.debug(" TB VAl="+ ((Textbox)comps.get(0)).getValue() +"  "+row.isVisible());
				if(!row.isVisible()) continue;
				
				//TODO need to chk code  here
				mapObj = (POSMapping)row.getAttribute("POS_MAP_OBJECT");
				
				if(mapObj == null) {
					mapObj = new POSMapping();
					mapObj.setUserId(ml.getUsers().getUserId());
					mapObj.setMappingType(Constants.POS_MAPPING_TYPE_CONTACTS);
					
					
				}
				
				String dateFormat = "";
				if(((Listbox)comps.get(1)).getSelectedItem().getValue().equals("Date")) {
					dateFormat = (String)((Listbox)comps.get(2)).getSelectedItem().getLabel();
				}
				
				
				logger.info("dateFormat str is"+dateFormat);
				
				
				
				boolean retFlag = updateCFandSayIsNew( ((Textbox)comps.get(0)).getValue(), 
									(String)((Listbox)comps.get(1)).getSelectedItem().getValue(), 
									((Textbox)comps.get(3)).getValue(), dateFormat, "" , mapObj);
				
				if(retFlag) {
					cfmodifiedList.add(tempCfObj);
				}
			} // for row
*/			
			//logger.info("Modified List="+cfmodifiedList);
//			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			
			/*// Modify if Mailing list object if modified
			if(isMlModified) {
				ml.setLastModifiedDate(MyCalendar.getNewCalendar());
				mailingListDao.saveOrUpdate(ml);
			}
			//*******************************************************
			
			
			MessageUtil.setMessage("List settings updated successfully.", "color:blue", "TOP");
			Long userId = GetUser.getUserId();
			
			if(userActivitiesDao != null && userId != null) {
				userActivitiesDao.addToActivityList(ActivityEnum.CONTS_EDIT_ML_p1mlName, GetUser.getUserObj(), ml.getListName());
			}
			
			logger.info(" - contact list '" + oldName + "' settings are edited ");*/
			
			
		//	Include xcontents = Utility.getXcontents();
		//	xcontents.invalidate();
		}catch (Exception e) {
			logger.error(" - ** Exception : Problem in updating the list settings --" , e );
			MessageUtil.setMessage("Problem encountered while updating the list settings.", "color:red", "TOP");
		}
	}
	
	 public void onSelect$welcomeEmailsLbId() {
		 
		 if(welcomeEmailsLbId.getSelectedIndex() == 0 && 
				 welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
			 
			 editWelcomeMsgBtnId.setVisible(false);
			 welcomePreviewemailIconId.setVisible(true);
			 previewemailBtnId.setVisible(true);
			 
			 
		 }else if(welcomeEmailsLbId.getSelectedIndex() == 0 && 
				 welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
			 
			 editWelcomeMsgBtnId.setVisible(false);
			 welcomePreviewemailIconId.setVisible(false);
			 previewemailBtnId.setVisible(false);
			 
			 
		 }else{
			 editWelcomeMsgBtnId.setVisible(true);
			 welcomePreviewemailIconId.setVisible(true);
			 previewemailBtnId.setVisible(true);
		 }
	 }
	 
	 public void onSelect$parentalConsentEmailLbId() {
			
			 if(parentalConsentEmailLbId.getSelectedIndex() == 0 && 
					 parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
				 
				 parentalEditMsgBtnId.setVisible(false);
				 parentalPreviewBtnId.setVisible(true);
				parentalPreviewIconId.setVisible(true);
				 
				 
			 }else if(parentalConsentEmailLbId.getSelectedIndex() == 0 && 
					 parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
				 
				 parentalEditMsgBtnId.setVisible(false);
				 parentalPreviewBtnId.setVisible(false);
				parentalPreviewIconId.setVisible(false);
				 
				 
			 }
	}
	/*
	private boolean updateCFandSayIsNew(String cfName, String dataType, String defValue, 
										String dateFormat, 	String posAtrrStr, POSMapping tempCfObj) {
		boolean flag=false;
		try {
			
			//TODO
			if(tempCfObj == null) {
				tempCfObj = new POSMapping();
				tempCfObj.setMappingType(Constants.POS_MAPPING_TYPE_CONTACTS);
//				tempCfObj.setUserId(userId);
				tempCfObj.setPosAttribute(posAtrrStr);
			}
			
			
			
			if(dataType.equals("Date")) {
				dataType = dataType+"("+dateFormat+")";
			}
			
			if(!cfName.trim().equalsIgnoreCase( (tempCfObj.getCustomFieldName()==null? "": tempCfObj.getCustomFieldName()) )) {
				flag=true; 
				tempCfObj.setCustomFieldName(cfName);
			}
			
			if(!dataType.trim().equalsIgnoreCase( (tempCfObj.getDataType()==null? "": tempCfObj.getDataType()) )) {
				flag=true; 
				tempCfObj.setDataType(dataType);
			}
			
			if(!defValue.trim().equalsIgnoreCase( (tempCfObj.getDisplayLabel()==null? "": tempCfObj.getDisplayLabel()) )) {
				flag=true;
				tempCfObj.setDisplayLabel(defValue);
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
*/
	/*private boolean updateCFandSayIsNew(String cfName, String dataType, String defValue, String dateFormat,  MLCustomFields tempCfObj) {
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
*/	
	
	
	/*public void rendererList() throws Exception {
		List<POSMapping> conCfList;
		conCfList = getCustomFields();
		
		if(conCfList == null || conCfList.size() ==0) {
			//TODO for Generate default mapping
			defaultGenFieldMapp();
			return;
		}else  {
			try {
				addCFEntryRule(conCfList);
			} catch (Exception e) {
				logger.error("Exception :: error getting from the addCFEntryRule method",e);
			}
		}
	}*/
	
	
	/*private void defaultGenFieldMapp() {
		
		
		Set<String> set =  genFieldContMap.keySet();
		Row row = null;
		for (String keyStr : set) {
			
			String optCulFiled = genFieldContMap.get(keyStr);
			row = new Row();
			
//			CSV Colomn Label
			Textbox posAttrTextBx = new Textbox(optCulFiled);
			posAttrTextBx.setParent(row);
			
//			Custom Field
			Listbox optFiledLstBx = null;
			
			optFiledLstBx =  createContactPosMappingListbox();
			optFiledLstBx.setParent(row);
			optFiledLstBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			optFiledLstBx.addEventListener("onSelect", this);
			
			
			List optMapChildItemList = optFiledLstBx.getChildren();
			Listitem tempItem= null;
			
			for (Object object : optMapChildItemList) {
				tempItem = (Listitem)object;
				if(tempItem.getLabel().equals(optCulFiled)){ 
					optFiledLstBx.setSelectedItem(tempItem);
				}
			}
			optFiledLstBx.setParent(row);
			
			
//			Data Type
			String dataTypeStr = optFiledDataType.get(optCulFiled);
			logger.info("datatype ::::"+dataTypeStr);
			
			
			Div dataTypeDiv = new Div();
			
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setParent(dataTypeDiv);
			
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(dataTypeDiv);
			dateFormatListBx.setSelectedIndex(0);
			
			List ChildItemList =  dataTypelb.getChildren();
			
			for (Object object : ChildItemList) {
				
				Listitem listItem = (Listitem)object;
//				logger.info("listItem.getLabel() ::::"+listItem.getLabel());
				
				if(listItem.getLabel().toString().equals(dataTypeStr)) {
					dataTypelb.setSelectedItem(listItem);
				}
			} // for
			
			if(dataTypeStr.equals("Date")) {
				dateFormatListBx.setVisible(true);
			}else {
				dateFormatListBx.setVisible(false);
			}
			
			dataTypelb.addEventListener("onSelect", this);
			dataTypeDiv.setParent(row);
			
			dataTypelb.setDisabled(true);
			
			// Optional Value
			Div optDiv=new Div();
			optDiv.setParent(row);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);	
			
			
			
			
//			Display Label
			Textbox dispLabelTextBx = new Textbox(optCulFiled);
			dispLabelTextBx.setParent(row);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			if(optCulFiled.equals("RetailPro ID")) priIntBx.setValue(1);
			else if(optCulFiled.equals("Email")) priIntBx.setValue(2);
			else if(optCulFiled.equals("Mobile")) priIntBx.setValue(3);
			
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			priIntBx.setParent(row);
			
			Div div=new Div();
			div.setParent(row);

			//Delete Action
			Image delImg = new Image();
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);
			
			delImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
			row.setParent(custFieldsGbId.getRows());
			
			
		} // for
		
	} // defaultGenFieldMapp
*/	
	/*Map<String , POSMapping> existDBFieldMap = new HashMap<String , POSMapping>();
	private void addCFEntryRule(List<POSMapping> mlcfLst)throws Exception {
		try {

			MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			
			if(mlcfLst == null ) {

				Row row = new Row();
				MLCustomFields mlCustomFields = new MLCustomFields("","String",ml,"");
				POSMapping posMapObj = new POSMapping("", "", "String", "Contacts", "", ml.getUsers().getUserId());
				addRule(row, posMapObj);
				row.setParent(custFieldsGbId.getRows());
			}
			else {
			}
			for (POSMapping posMapObj : mlcfLst) {
				existDBFieldMap.put(posMapObj.getCustomFieldName(), posMapObj);
//					if(!posMapObj.getCustomFieldName().startsWith("UDF")) continue;
				
				Row row = new Row();
				addRule(row, posMapObj);
				row.setParent(custFieldsGbId.getRows());
			} // for
			
			custFieldsGbId.invalidate();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			logger.error(" - ** Exception : Problem adding the Custom Field entry rule--" + e + " **");
		}
	} // addCFEntryRule
	*/
	
/*
 	private void addRule(Row row, POSMapping posMapping) {
			
			row.setAttribute("POS_MAP_OBJECT", posMapping);
//			POS Attribute
			Textbox posAttrTextBx = new Textbox(posMapping == null ? "" :posMapping.getPosAttribute());
			posAttrTextBx.setParent(row);
			

			
//			Custom Field
			Listbox posMappingListBx = null;
//			logger.debug("posMapping.getMappingType() is :: "+posMapping.getMappingType());
			
			posMappingListBx =  createContactPosMappingListbox();
			posMappingListBx.setParent(row);
			
			
			posMappingListBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			posMappingListBx.addEventListener("onSelect", this);
			
			if(posMapping == null) {
				posMappingListBx.setSelectedIndex(0);
				
			}else  {
				
				List posMappingChilItemList = posMappingListBx.getChildren();
				Listitem tempItem= null;
				for (Object object : posMappingChilItemList) {
					tempItem = (Listitem)object;
					if(tempItem.getLabel().equals(posMapping.getCustomFieldName())){
						posMappingListBx.setSelectedItem(tempItem);
					}
				}
			}
			
			
			
			Div dataTypeDiv = new Div();
//			dataTypeDiv.setWidth("350px");
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setParent(dataTypeDiv);
			
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(dataTypeDiv);
			dateFormatListBx.setSelectedIndex(0);
			dateFormatListBx.setVisible(false);
			
			 if(posMapping == null) {
				 dataTypelb.setSelectedIndex(0);
			 }else  {
				 
//			Data Type
				 String dataTypeStr = posMapping.getDataType();
				 
				 List ChildItemList =  dataTypelb.getChildren();
				 
				 for (Object object : ChildItemList) {
					 
					 Listitem listItem = (Listitem)object;
					 
					 if(dataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
						 dataTypelb.setSelectedItem(listItem);
						 dateFormatListBx.setVisible(true);
						 
						 List dateFormatList = dateFormatListBx.getChildren();
						 dataTypeStr =  dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
						 
						 for (Object obj : dateFormatList) {
							 
							 Listitem tempListItem = (Listitem)obj;
							 if(tempListItem.getLabel().equals(dataTypeStr)) {
								 dateFormatListBx.setSelectedItem(tempListItem);
								 break;
							 }
						 }
						 
						 
					 }else if(listItem.getLabel().equals(dataTypeStr)) {
						 dataTypelb.setSelectedItem(listItem);
						 break;
					 }
					 
				 } // for
			 }
			
			if(!posMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
				
				dataTypelb.setDisabled(true);
			}
			
			
			dataTypelb.addEventListener("onSelect", this);
			dataTypeDiv.setParent(row);
			
			// Optional Value
			
			Div optDiv=new Div();
			optDiv.setParent(row);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			if(posMapping != null && posMapping.getOptionalValues()!=null) {
				String optValues[] = posMapping.getOptionalValues().split(Constants.ADDR_COL_DELIMETER);
				for (String optVal : optValues) {
					cb.appendItem(optVal);
				}
			}
			if(cb.getItemCount()>0) cb.setSelectedIndex(0);
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
//			Display Label
			Textbox dispLabelTextBx = new Textbox(posMapping == null ? "":posMapping.getDisplayLabel());
			dispLabelTextBx.setParent(row);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			if(posMapping!= null && posMapping.getUniquePriority()!=null) priIntBx.setValue(posMapping.getUniquePriority());
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			priIntBx.setParent(row);
			
			Div div=new Div();
			div.setParent(row);

			//Delete Action
			Image delImg = new Image();
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);
			
				
			row.setAttribute("referenceId", posMapping);
			
			delImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
			row.setParent(custFieldsGbId.getRows());
			
 	} // addRule
*/
} // class

 class DelEventListener implements EventListener {
	 
	 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 @Override
	public void onEvent(Event evt) throws Exception {
		Object obj = evt.getTarget();
		try {
			if(obj instanceof Image) {
				Image img = (Image)obj;
				MLCustomFields mlcfObj = (MLCustomFields)img.getAttribute("mlcfObj");
				
				if(mlcfObj.getFieldIndex()<=0) { // If it is the new Row
					img.getParent().setVisible(false);
					return;
				}
				
				if (Messagebox.show("This will delete all the data in the custom field column: "+ mlcfObj.getCustFieldName() +" permanently. "+"\n Do you want to continue?", 
	        		  "Prompt", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
					return;
				}
	          

		        MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
				mlCustomFieldsDao.removeCustomFieldAndClearTheData(mlcfObj);
				
				
				//*********************** Set the Mailing list custom field Flag ********************
				
				MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
				int mlCfCount = mlCustomFieldsDao.findAllByList(ml).size();
				
				boolean isMlModified=false;
				if(mlCfCount>0 && ml.isCustField()==false) {
					isMlModified=true;
					ml.setCustField(true);
				}
				else if(mlCfCount==0 && ml.isCustField()==true) {
					isMlModified=true;
					ml.setCustField(false);
				}
				
				// Modify if Mailing list object if modified
				if(isMlModified) {
					MailingListDaoForDML mailingListDao = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
					ml.setLastModifiedDate(MyCalendar.getNewCalendar());
					mailingListDao.saveOrUpdate(ml);
				}
				//************************
				
				img.getParent().setVisible(false);
				Messagebox.show("List custom field deleted successfully.", "Information", Messagebox.OK, Messagebox.INFORMATION);
			} // If image type
			else if(obj instanceof Listbox)	 {
				
				Listbox listbox = (Listbox)obj;
				Listbox dateFormatLBox = (Listbox)listbox.getParent().getChildren().get(2);
				
				if(listbox.getSelectedItem().getLabel().trim().equals("Date")) {
					dateFormatLBox.setVisible(true);
				}else dateFormatLBox.setVisible(false);
				
			} //if Listbox type
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			
		}
	}

}
