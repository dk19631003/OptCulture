package org.mq.marketer.campaign.controller.contacts;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.MailingListDao;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings({ "unchecked", "serial" })
public class UploadContactController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Radiogroup optnRbId, newListoptnRbId;
	private Div existsDivId, newDivId;
	
	private Listbox mListLbId = null;
	private Textbox newMlNameTbId = null;
	private Textbox newMlDescTbId = null;
	/*private Toolbarbutton newListTbbId = null;
	private Toolbarbutton selListTbbId = null;
	*/
	private Tab newListTbbId = null;
	private Tab selListTbbId = null;
	
	private Checkbox purgeCbId = null;
	private Checkbox doubleOptInCbId,parentalConsentCbId;
	private Div confirmOptDiv,confirmParentalConsentDiv ;
	private Div doubleOptLtDiv,ConsentDiv;
	private Div listManagementOptionsDivId = null;
	private Html welcomeHTMl = null;
	private Listbox optInEmailsLbId ,consentEmailsLbId;
	private Image imagePreviewId;
	private A previewBtnId;
	private A editMsgBtnId;
	private MyTemplatesDao myTemplatesDao;
	
	private final String selectStr = "Select Message";
	private final String defaultStr = "Default Message";
	
	boolean purgeStatusFlag = true;
	String fromPage = null;

	Session session;
	private Set<Long> listIdsSet; 
	public  UploadContactController(){
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Add / Import Contacts","",style,true);
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
	}
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multy user acc
	
	Users currentUser = GetUser.getUserObj();
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		getWelcomeTemplateList();
		getParentalConsentTemplateList();
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		  /*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD,currentUser);
		  }*/
		  if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD,GetUser.getLoginUserObj());
		  }
		  
		  session = Sessions.getCurrent();
		  
		  
		  /*
		   * this block displays the defaultmessage by default
		   */
		  
		  
		  Listitem li = null;
		  List<MailingList> mlList = getMailingLists();
		 if(mlList == null){
			   MessageUtil.setMessage("Please create Mailing List before adding contacts.", "color:red");
			   return;
    	  }
		  for (MailingList mailingList : mlList) {
			
			  //if mailing list type is POS we ignore it..
			  if(mailingList.getListType() != null && 
					  (mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_POS) ||
							  mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_OPTIN_LIST)) ) {
				  continue;
			  }
			  li = new Listitem(mailingList.getListName(), mailingList);
			  li.setParent(mListLbId);
			  
			  
		  }
		  
		  
		  if(optInEmailsLbId!=null){
			  
			  optInEmailsLbId.setSelectedIndex(0);
			  //li.setSelected(true);
			// displayHtmlStuff();
			  logger.debug("======>displayed html stuff");
			  
		  }
		 
		  
			 fromPage = (String)session.getAttribute("fromPage");
			 logger.info("fromPage :: "+fromPage);
			 
			 if(fromPage.equalsIgnoreCase("campaign/SMSCampCreateIndex")) {
				 
				 onClick$newListTbbId();
				 
			 }
			 if(optInEmailsLbId.getSelectedIndex() == 0) {
				 imagePreviewId.setStyle("display:none");
					previewBtnId.setVisible(false);
					editMsgBtnId.setVisible(false);
			 }
			 
	}
	
	public void onSelect$mListLbId() {
		
		if(mListLbId.getSelectedCount() == 0) return;
		
		for (Listitem item : mListLbId.getSelectedItems()) {
			
			if( ((MailingList)item.getValue()).getUsers().getUserId().longValue() != currentUser.getUserId().longValue()) {
				
				MessageUtil.setMessage("The selected list has been shared by others.You can not add a contact to shared list", "color:red;");
				return;
			}
			
			
		}//for
		
		
		
		
		
	}
	
	
	
	
	/*
	public void init(Radiogroup optnRbId, Vbox existsVbId, Vbox newVbId, Listbox mListLbId,
			Textbox newMlNameTbId, Textbox newMlDescTbId, 
			Toolbarbutton newListTbbId,Toolbarbutton selListTbbId, Checkbox purgeCbId,
			Div confirmOptDiv,Checkbox doubleOptInCbId,Div doubleOptLtDiv,Html welcomeHTMl,Listbox optInEmailsLbId,Div listManagementOptionsDivId) {
		
		this.optnRbId = optnRbId;
		this.existsVbId = existsVbId;
		this.newVbId = newVbId;
		this.mListLbId = mListLbId;
		this.newMlNameTbId = newMlNameTbId;
		this.newMlDescTbId = newMlDescTbId;
		this.newListTbbId = newListTbbId;
		this.selListTbbId = selListTbbId;
		this.purgeCbId = purgeCbId;
		this.confirmOptDiv = confirmOptDiv;
		this.doubleOptInCbId = doubleOptInCbId;
		this.doubleOptLtDiv = doubleOptLtDiv;
		this.welcomeHTMl = welcomeHTMl;
		this.optInEmailsLbId = optInEmailsLbId;
		this.listManagementOptionsDivId = listManagementOptionsDivId;
		
        
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		  if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD,GetUser.getUserObj());
		  }
		  
		   * this block displays the defaultmessage by default
		   
		  if(optInEmailsLbId!=null){
			  
			  optInEmailsLbId.setSelectedIndex(0);
			  //li.setSelected(true);
			  displayHtmlStuff();
			  logger.debug("======>displayed html stuff");
			  
		  }
	}
	*/
	
	public void onClick$editMsgBtnId() {
		//logger.info("just Call addnew button");
		session.setAttribute("editCustomTemplate", optInEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN);
		session.setAttribute("fromAddNewBtn","contact/edit");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
	//	Redirect.goTo("contact/customMListWelcomeMsg");
	}
	
	
	public void onClick$parentalEditMsgBtnId() {
		//logger.info("just Call addnew button");
		session.setAttribute("editCustomTemplate", consentEmailsLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT);
		session.setAttribute("fromAddNewBtn","contact/edit");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
	//	Redirect.goTo("contact/parentalConsentMsg");
	}
	
	
	
	private Window previewWin;
	private Iframe previewWin$html;
	public void onClick$previewBtnId() {
		String templateContent = "";
		
		//previewWin$html.setContent(templateContent);
		
		CustomTemplates customTemplates = optInEmailsLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			
		templateContent = 	PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
			
		}else {
			
			templateContent = customTemplates.getHtmlText();
			if(templateContent != null && !templateContent.isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates != null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					 return;
				 }
			}
		}
		//previewWin$html.setContent(templateContent);
		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		
		previewWin.setVisible(true);
		
	}//onClick$previewBtnId

	
	
	
	public void onClick$parentalPreviewBtnId() {
		
		String templateContent = "";
		
		//previewWin$html.setContent(templateContent);
		
		CustomTemplates customTemplates = consentEmailsLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			
			templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
			
		}else {
			templateContent = customTemplates.getHtmlText();
			if(templateContent != null && !templateContent.isEmpty()) {
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
		//previewWin$html.setContent(templateContent);
		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		
		previewWin.setVisible(true);
		
		
		
	}//onClick$parentalPreviewBtnId
	

	
	
	
	public void next(){ 
		
		Session session = Sessions.getCurrent();
		int option = 0;
		Set<MailingList> mailingLists = new HashSet<MailingList>();
		session.setAttribute("isPurgeContacts", purgeCbId.isChecked());
		
		if(existsDivId.isVisible()){
			
			Set mlItems = mListLbId.getSelectedItems();
			
			if(mlItems.size()>0){
				MessageUtil.clearMessage();
			}else{
				MessageUtil.setMessage("Select at least one list.","color:red","TOP");
				return;
			}
			
			for(Object obj : mlItems){
				Listitem listItem = (Listitem)obj;
				MailingList mailingList = (MailingList)listItem.getValue();
				if( mailingList.getUsers().getUserId().longValue() != currentUser.getUserId().longValue()) {
					
					MessageUtil.setMessage("One of selected list(s) has been shared by others.You cannot add a contact to shared list : "+mailingList.getListName(), "color:red;");
					return;
				}
				
				mailingLists.add(mailingList);
			}
			
			session.setAttribute("isNewML","false");
			option = optnRbId.getSelectedIndex();
		}else{
			
			String listName = newMlNameTbId.getValue();
			
			if(listName == null || listName.trim().length() == 0){
				MessageUtil.setMessage("Please specify the list name.","color:red","TOP");
				return ;
			}else if(!Utility.validateName(listName)){
				MessageUtil.setMessage("Enter valid list name. Special characters are not allowed.","color:red","TOP");
				return ;
			}else
				MessageUtil.clearMessage();
			
			listName = Utility.condense(listName);
			boolean isExists = isMLExist(listName);
			
			if(isExists){
				MessageUtil.setMessage("Name already exists.","color:red","TOP");
				return;
			}
			
			Calendar cal = MyCalendar.getNewCalendar();
			MailingList mailingList;
			
			try {
				boolean optin = doubleOptInCbId.isChecked() &&  doubleOptLtDiv.isVisible();
				boolean consent = parentalConsentCbId.isChecked() && ConsentDiv.isVisible();
				
				
				Long optInCustTemplateId = null;
				Long consentCustTemplateId = null;
				
					
					if(optin) {
						Listitem optInListitem = optInEmailsLbId.getSelectedItem();
						
						if(optInListitem.getIndex() != 0) {
							CustomTemplates optInCustomTemplates = (CustomTemplates)optInListitem.getValue();
							optInCustTemplateId = optInCustomTemplates.getTemplateId();
						}else{
							MessageUtil.setMessage("Please select Double-Optin message.","color:red","TOP");
							return;
							
						}
					}//if optin
					
					if(consent) {
						Listitem consentListitem1 = consentEmailsLbId.getSelectedItem();
						
						if(consentListitem1.getIndex() != 0) {
							CustomTemplates consentCustomTemplates = (CustomTemplates)consentListitem1.getValue();
								consentCustTemplateId = consentCustomTemplates.getTemplateId();
						}else{
							
							MessageUtil.setMessage("Please select Parental Consent message.","color:red","TOP");
							return;
						}
					
					}
					logger.debug("Custom template Id is :"+consentCustTemplateId);
					
					MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
					
					Long mlbit = mailingListDao.getNextAvailableMbit(currentUser.getUserId());
					
					if(mlbit == 0l){
						MessageUtil.setMessage("You have exceeded limit on maximum number of lists(60). " +
								"Please delete one or more lists to create a new list.", "red");
						return;
					}
					
					mailingList = new MailingList(listName, newMlDescTbId.getValue(),cal,
							"Active", cal,cal,false, false,null,currentUser,
								optin, optInCustTemplateId, consent, consentCustTemplateId);
					
					/*mailingList = new MailingList(listName, newMlDescTbId.getValue(),cal,
							"Active", cal,cal,false, false,null,currentUser,
								optin, optInCustTemplateId, consent, consentCustTemplateId);*/
				
				mailingList.setMlBit(mlbit);
				mailingList.setListSize(0L);
				
				mailingList.setListType(Constants.MAILINGLIST_TYPE_ADDED_MANUALLY);
				if(!posTypeChkBox.isDisabled() && posTypeChkBox.isChecked()){
					
					mailingList.setListType(Constants.MAILINGLIST_TYPE_POS);
				}
				
				
				mailingLists.add(mailingList);
				session.setAttribute("newListName",listName);
				session.setAttribute("isNewML","true");
				
				option = newListoptnRbId.getSelectedIndex();
			} catch (Exception e) {
				logger.error("Exception : "+e);
			}
		}
		
		// check if purge list option is selected or not.
		try {
			if(!purgeCbId.isChecked()) {
				int confirm = Messagebox.show("Purge option is not selected. Only purged " +
						" contacts can be configured to send email. Do you want to continue?",
						"Purge Contacts", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION); 
				if(confirm == Messagebox.NO) {
					return;
				}
			}
		} catch (Exception e1) {
			logger.debug("** Exception: Error while displaying Messagebox ** ."+e1);
		}
		
		session.removeAttribute("AddSingle_Ml");
		session.removeAttribute("uploadFile_Ml");
		if(option == 1){
			session.setAttribute("AddSingle_Ml",mailingLists);
		}else{
			session.setAttribute("uploadFile_Ml",mailingLists);
		}
		PageUtil.setFromPage("/contact/upload.zul");
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD_CONSENT);
	}
	
	/*public Long getNextMbit(){
		Long mbit = null;
		try{
			
		MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		
		mbit = mailingListDao.getNextAvailableMbit(currentUser.getUserId());
		
		return mbit;
		
		}catch(Exception e){
			logger.debug("exception in get next bit ",e);
			return null;
		}
	}*/
	
	
	
	public void manageDivDisplay(String divId) {
		if(divId.equals("templateList")) {
			// Get the purge Status before double opt in check 
			// so that to replace it on double-opt in uncheck.
			if(doubleOptInCbId.isChecked()) {
		
				purgeStatusFlag = purgeCbId.isChecked();
						
			
			}
			purgeCbId.setChecked( (doubleOptInCbId.isChecked() || parentalConsentCbId.isChecked() )?true:purgeStatusFlag);
			purgeCbId.setDisabled(purgeCbId.isDisabled()?false:true);
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			int count = customeTemplatesDao.getCustomTemplateCount(listIdsSet);
			
			doubleOptLtDiv.setVisible(doubleOptInCbId.isChecked());
			if(count<1) {
				return;
			}
			logger.debug("--Just entered--");
			if(doubleOptInCbId.isChecked()) {
				doubleOptLtDiv.setVisible(true);
			} else {
				doubleOptLtDiv.setVisible(false);
				
			}
			
		}else if(divId.equals("ConsentTemplateList")) {
			
			
			if(parentalConsentCbId.isChecked()) {
				
				purgeStatusFlag = purgeCbId.isChecked();
						
			
			}
			purgeCbId.setChecked( (parentalConsentCbId.isChecked() || doubleOptInCbId.isChecked() )?true:purgeStatusFlag);
			purgeCbId.setDisabled(purgeCbId.isDisabled()?false:true);
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			int count = customeTemplatesDao.getCustomTemplateCount(listIdsSet);
			
			ConsentDiv.setVisible(parentalConsentCbId.isChecked());
			if(count<1) {
				return;
			}
			logger.debug("--Just entered--");
			if(parentalConsentCbId.isChecked()) {
				ConsentDiv.setVisible(true);
			} else {
				ConsentDiv.setVisible(false);
				
			}
			
			
		}
		
	}
	
	public void displayHtmlStuff() {
		try {
			
			Listitem listitem = optInEmailsLbId.getSelectedItem();
			//displaying default message
			if(optInEmailsLbId.getSelectedIndex()==0){
				
				 String optinMsgTemplate = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
				 welcomeHTMl.setContent(optinMsgTemplate);
				 return;
			}
			CustomTemplates customTemplates = (CustomTemplates)listitem.getValue();
			if(customTemplates!=null) {
				String htmlStuff = null;
				if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
					htmlStuff = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 htmlStuff = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
				welcomeHTMl.setContent(htmlStuff);
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	public void manageRedirects(String src) {
		Session session = Sessions.getCurrent();
		session.setAttribute("fromAddNewBtn","contact/upload");
		Redirect.goTo(src);
		
		
	}
	
	public void selOption(int option){


		if(option == 1){
			existsDivId.setVisible(false); 
			newDivId.setVisible(true);
			newListTbbId.setStyle("font-weight:bold;font-size:14px;");
			selListTbbId.setStyle("font-weight:normal;font-size:14px;");
			listManagementOptionsDivId.setVisible(true);
			//doubleOptInCbId.setChecked(false);
			confirmOptDiv.setVisible(true);
			confirmParentalConsentDiv.setVisible(false);
			
			doubleOptLtDiv.setVisible(doubleOptInCbId.isChecked());
			ConsentDiv.setVisible(parentalConsentCbId.isChecked());
			
		}else{
			doubleOptLtDiv.setVisible(false);
			ConsentDiv.setVisible(false);
			existsDivId.setVisible(true); 
			newDivId.setVisible(false);
			newListTbbId.setStyle("font-weight:normal;font-size:14px;");
			selListTbbId.setStyle("font-weight:bold;font-size:14px;");
			listManagementOptionsDivId.setVisible(false);
			confirmOptDiv.setVisible(false);
			confirmParentalConsentDiv.setVisible(false);
		}
	}
	
	public boolean isMLExist(String listName){
		try{
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			MailingList mailingList = mailingListDao.findByListName(listName, currentUser.getUserId());
			if(mailingList == null)
				return false;
			else
				return true;
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
			return false;
		}
	}

	private Checkbox posTypeChkBox;
	public List<MailingList> getMailingLists() {
		List mlist = null;
		try{
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			//mlist = mailingListDao.findAllByUser(userIdsSet);
			mlist = mailingListDao.findByIds(listIdsSet);
			
			if(mlist == null){
			   MessageUtil.setMessage("Please create Mailing List before adding contacts.", "color:red");
			   return mlist;
    	  }
			
			MailingList ml = null;
			for (Object object : mlist) {
				
				if(object instanceof MailingList) {
					
					ml = (MailingList)object;
					
//					logger.debug("list type ::"+ml.getListType());
					if(ml.getListType() != null && ml.getListType().trim().equalsIgnoreCase("POS")) {
						
						posTypeChkBox.setDisabled(true);
						break;
						
					}
					
					
				}
				
				
			}
			logger.debug(" - Got cotact lists for the user ");
		}catch(Exception e){
			logger.error(" - ** Exception to get the contact lists - " + e + " **");		
		}
		return mlist;
	}
	
	
	
	public  void getWelcomeTemplateList() {
		List<CustomTemplates> wlist = null;
		try {
			
			
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			wlist = customeTemplatesDao.getTemplatesByType(listIdsSet,"welcomemail");
			
			if(wlist == null){
				logger.error("Wlist is Empty....");
				return;
			}
			
			Components.removeAllChildren(optInEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(optInEmailsLbId);
			if(wlist != null) {
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
	
	
	public  void getParentalConsentTemplateList() {
		List<CustomTemplates> consentlist = null;
		try {
			
			
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			consentlist = customeTemplatesDao.getTemplatesByType(listIdsSet,"parentalConsent");
			
			
			Components.removeAllChildren(consentEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(consentEmailsLbId);
			if(consentlist != null) {
				for (CustomTemplates customTemplates : consentlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(consentEmailsLbId);
					
					
				}
			}
			if(consentEmailsLbId.getItemCount() > 0 ) consentEmailsLbId.setSelectedIndex(0);
			
			if(consentlist != null)
				logger.debug(" - Got consent email lists for the user. size : "+  consentlist.size() );
			else
				logger.debug("consentlist is null");
			
		} catch(Exception e) {
			//e.printStackTrace();
			logger.error(" - ** Exception to get the consent template List - ",e);
			
		}
	}
	
	
	
	
	
	public List getConsentTemplateList() {
		List wlist = null;
		try {
			
			CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			wlist = customeTemplatesDao.getTemplatesByType(listIdsSet,"parentalConsent");
			logger.debug(" - Got welcome lists for the user. size : "+wlist.size());
			return wlist;
		} catch(Exception e) {
			logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			return null;
		}
	}
	public void onClick$selListTbbId() {
		selOption(0);
	}
	
	public void onClick$newListTbbId() {
		selOption(1);
	}
	
	public void onCheck$doubleOptInCbId() {
		manageDivDisplay("templateList");
	}
	
	public void onCheck$parentalConsentCbId() {
		manageDivDisplay("ConsentTemplateList");
	}
	
	
	public void onSelect$optInEmailsLbId() {
		//displayHtmlStuff();
		if(optInEmailsLbId.getSelectedIndex() > 0) {
			imagePreviewId.setStyle("display:initial");
			previewBtnId.setVisible(true);
			editMsgBtnId.setVisible(true);
		}else {
			imagePreviewId.setStyle("display:none");
			previewBtnId.setVisible(false);
			editMsgBtnId.setVisible(false);
		}
	}
	
	public void onClick$addNewBtnId() {
		logger.info("just Call addnew button");
		manageRedirects("contact/promoteCenter");
	}
	
	public void onClick$nextBtnId() {
		/*if(optInEmailsLbId.getSelectedIndex() == 0 && doubleOptInCbId.isChecked()) {
			MessageUtil.setMessage("Select Opt-In Message", "color:red;");
		}*/
		next();
	}
	
}
