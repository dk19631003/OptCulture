package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.dao.FormMappingDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class WebformsController extends GenericForwardComposer implements EventListener{
	private Rows webformsRowsId;
	private Session session;
	private Grid webformsGridId;
	private Paging webformsPagingId; 
	private Users currentUser;
	private TimeZone clientTimeZone;
	private FormMapping formMapping;
	private String type;
	private Listbox webformsPerPageLBId,subsListLbId,ltySubsListLbId,ltyProgramLbId,pickFromCardSetLbId, urlFormsToMlLbId,subsListFeedBackId;
	private Tabbox webformsTabBoxId;
	private Textbox formNameTbId,webformUrlTbId,successTbId,errorTbId,existSubsciberTbId,parenatConsentTbId;
	private Checkbox parentalConsentCbId,parentalConsentChkBox,mobileOptinCbId,sendFeedBackMailOnsuccess,sendFeedbackSmsOnsuccess,issueRewardCheckBoxId,sendSimpleSignMailOnsuccess,sendSimpleSignSmsOnsuccess,doIssuePointsId;
	private A editListAnchId,ltyEditListAnchId,editListFeedBackId;
	private Div parentalOptLtDiv,urlDivId,mappingsDivId,redirectUrlsDivId,configureDivId,parentalEmailDivId,urlAnchDivId,issueRewardDivId,childparentconsentDivId,buttonBackAndRedirect;
	private Listbox parentalConsentEmailLbId,feedbackEmailLbId,feedbackSmsLbId,issueRewardTypeListId,SimpleSignEmailLbId,SimpleSignSmsLbId;
	private Window previewWin; 
	private Iframe previewWin$html;
	private Textbox ActionUrlTbId,issueRewardTypeValueTextId;
	private Rows mappingRowsId;
	private String selectedFormName;
	private int count=0;
	private Image previewImageId,previewFeedbackImageId,previewFeedbackSmsImageId,previewSimpleSignImageId,previewSimpleSignSmsImageId;
	private A parentalPreviewBtnId;
	private A parentalEditMsgBtnId;
	private A feedbackPreviewBtnId,feedbackSmsPreviewBtnId,SimpleSignPreviewBtnId,SimpleSignSmsPreviewBtnId;
	private A feedbackEditMsgBtnId,feedbackSmsEditMsgBtnId,SimpleSignEditMsgBtnId,SimpleSignSmsEditMsgBtnId;

	private FormMappingDao formMappingDao;
	private FormMappingDaoForDML formMappingDaoForDML;
	private MailingListDao mailingListDao;
	private CustomTemplatesDao customeTemplatesDao ;
	private LoyaltyProgramDao loyaltyProgramDao;
	private LoyaltyCardSetDao loyaltyCardSetDao;
	private MyTemplatesDao myTemplatesDao;
	private AutoSMSDao autoSMSDao;
	private Label parenatConsentLabelId;

	private Vector<String> submitFieldVec = new Vector<String>();
	private Map<String, String> inputFieldWithTypeMap;
	private List<String> urlFormNameList = new ArrayList<String>();
	static final String APP_URL = PropertyUtil.getPropertyValue("webFormActionUrl");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Div cardselectionDiv;



	public WebformsController(){
		session = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		loyaltyProgramDao = (LoyaltyProgramDao)SpringUtil.getBean("loyaltyProgramDao");
		loyaltyCardSetDao = (LoyaltyCardSetDao)SpringUtil.getBean("loyaltyCardSetDao");
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		autoSMSDao = (AutoSMSDao)SpringUtil.getBean(OCConstants.AUTO_SMS_DAO);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Web-forms","",style,true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			type = (String)session.removeAttribute("formMappingType");
			formMapping = (FormMapping)session.removeAttribute("formMapping");
			formMappingDao =	(FormMappingDao)SpringUtil.getBean("formMappingDao");
			formMappingDaoForDML =	(FormMappingDaoForDML)SpringUtil.getBean("formMappingDaoForDML");
			mailingListDao =(MailingListDao)SpringUtil.getBean("mailingListDao");

			int totalSize = formMappingDao.findAllUserId(currentUser.getUserId()); 

			simpleSignUpRadioId.setChecked(true);
			simpleSignUpDivId.setVisible(true);

			webformsPagingId.setTotalSize(totalSize);
			webformsPagingId.setActivePage(0);
			webformsPagingId.addEventListener("onPaging", this);
			redrawForms(0,webformsPagingId.getPageSize());


			populateSubscribersList(subsListLbId);
			populateLtyProgList();
			populateSubscribersList(ltySubsListLbId);
			populateSubscribersList(subsListFeedBackId);
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}//doAfterCompose


	private void populateSubscribersList(Listbox listBoxId) {
		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		List<MailingList> mlList =  mailingListDao.findByIds(listIdsSet);

		Listitem mlItem = null;
		if(mlList != null && mlList.size() > 0) {

			for (MailingList mailingList : mlList) {

				mlItem =  new Listitem(mailingList.getListName());
				mlItem.setValue(mailingList);
				mlItem.setParent(listBoxId);
			}//for
		}//if
	}


	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);

		if(event.getTarget() instanceof Image){

			Image img = (Image)event.getTarget();
			Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
			FormMapping formMapping = (FormMapping)tempRow.getValue();
			List chaildLst = tempRow.getChildren();
			Label statusLbl  = (Label)chaildLst.get(3);
			Label activatedOnLb2=(Label)chaildLst.get(2);
			String evtType = (String)img.getAttribute("type");


			if(evtType.equalsIgnoreCase("userEdit")) {

				session.setAttribute("formMappingType", "edit");
				session.setAttribute("formMapping", formMapping);
				webformsTabBoxId.setSelectedIndex(1);
				webformEditSettinngs();

			} else if(evtType.equalsIgnoreCase("userPause")){

				int confirm = Messagebox.show("Are you sure you want to change the status?", "Prompt", 
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
				String src = "";
				String toolTipTxtStr = "";
				String statusStr = "";
				Calendar activeOn=null;

				if(formMapping.isEnable()) {
					src = "/img/play_icn.png";
					toolTipTxtStr = "Activate";
					statusStr = "InActive";
					formMapping.setEnable(false);
					activeOn =null;

				}else{
					src = "/img/pause_icn.png";
					toolTipTxtStr = "Pause";
					statusStr = "Active";
					formMapping.setEnable(true);
					activeOn=Calendar.getInstance();

				}

				img.setSrc(src);
				img.setTooltiptext(toolTipTxtStr);
				statusLbl.setValue(statusStr);
				String actvieFrom = MyCalendar.calendarToString(activeOn, MyCalendar.FORMAT_STDATE );
				activatedOnLb2.setValue(""+actvieFrom);
				formMapping.setActiveSince(activeOn);
				formMappingDaoForDML.saveOrUpdate(formMapping);
				MessageUtil.setMessage("Status changed to "+statusStr, "color:blue;");

			}
			else if(img.getAttribute("type").equals("userDelete")) {

				try {

					int confirm = Messagebox.show("Confirm to delete the selected form.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {


						try {
							formMappingDaoForDML .delete(formMapping);
							MessageUtil.setMessage("Form deleted successfully.","color:blue","TOP");
							webformsRowsId.removeChild(tempRow);
							int totalSize = formMappingDao.findAllUserId(currentUser.getUserId()); 

							webformsPagingId.setTotalSize(totalSize);

						} catch (Exception e) {
							logger.error("Exception ::", e);
						}
					}
				}


				catch (Exception e) {
					logger.error("Exception ::", e);
				}
				return;

			}

		}
		else if(event.getTarget() instanceof Paging) {


			Paging paging = (Paging)event.getTarget();

			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redrawForms(ofs, (byte) pagingEvent.getPageable().getPageSize());




		} if (event.getTarget() instanceof Listbox) {

			logger.debug("select action perfomed on listbox...");
			Listbox lb = (Listbox)event.getTarget();

			if(lb.getSelectedIndex() == 0) {
				return;
			}
			logger.debug("select action perfomed on listbox..."+lb.getSelectedItem().getValue());

			Row row = (Row)((Div)lb.getParent()).getParent();

			Div div = (Div)lb.getParent();

			if(lb.getSelectedItem().getValue().equals("Anniversary") || 
					lb.getSelectedItem().getValue().equals("BirthDay")) {

				if(div != null) {

					if(div.getChildren().size() > 1) {

						Listbox tempbx = (Listbox)div.getChildren().get(2);
						Label lbl = (Label)div.getChildren().get(1);
						tempbx.setVisible(true);
						lbl.setVisible(true);
						return;
					}

					Listbox listbx = createDateFormatListbox();

					//listbx.setMold("select");
					//listbx.setSelectedIndex(0);

					Label lbl = new Label("Date Format :");
					div.appendChild(lbl);
					div.appendChild(listbx);

				} else {
					logger.info("Unable to find the div");
				}
			} else if(lb.getSelectedItem().getValue().toString().toLowerCase().contains("udf")){
				
				String type = (String)lb.getSelectedItem().getAttribute("type");
				logger.info("type =========="+type);
				if(type != null && type.toLowerCase().contains("date")) {
					
					if(div != null) {

						if(div.getChildren().size() > 1) {

							Listbox tempbx = (Listbox)div.getChildren().get(2);
							Label lbl = (Label)div.getChildren().get(1);
							tempbx.setVisible(true);
							lbl.setVisible(true);
							return;
						}

						Listbox listbx = createDateFormatListbox();

						//listbx.setMold("select");
						//listbx.setSelectedIndex(0);

						Label lbl = new Label("Date Format :");
						div.appendChild(lbl);
						div.appendChild(listbx);

					} else {
						logger.info("Unable to find the div");
					}

				}else{
					if(div.getChildren().size() > 1) {

						Listbox tempbx = (Listbox)div.getChildren().get(2);
						Label lbl = (Label)div.getChildren().get(1);
						tempbx.setVisible(false);
						lbl.setVisible(false);
					}

					
				}
				
				
				
			}
			else {

				if(div.getChildren().size() > 1) {

					Listbox tempbx = (Listbox)div.getChildren().get(2);
					Label lbl = (Label)div.getChildren().get(1);
					tempbx.setVisible(false);
					lbl.setVisible(false);
				}
			}
		}


	}//onEvent()


	public void redrawForms(int StartIndex,int endIndex){

		List<FormMapping> formList=formMappingDao.findByUserId(currentUser.getUserId(),StartIndex,endIndex,orderby_colName,desc_Asc);
		Components.removeAllChildren(webformsRowsId);

		if(formList == null || formList.isEmpty() || formList.size() == 0) return;

		for (FormMapping formMapping : formList) {

			Row row = new Row();

			row.setParent(webformsRowsId);
			String activefrom = null;
			if(formMapping.isEnable()){
				activefrom= MyCalendar.calendarToString(formMapping.getActiveSince(), MyCalendar.FORMAT_STDATE );
			}

			row.appendChild(new Label(formMapping.getFormMappingName()));
			row.appendChild(new Label(formMapping.getURL()));
			row.appendChild(new Label(activefrom != null? activefrom:""));
			row.appendChild(new Label(formMapping.isEnable()?"Active":"InActive"));
			Hbox hbox = new Hbox();

			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit");
			editImg.setStyle("cursor:pointer;margin-right:5px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", "userEdit");
			editImg.setParent(hbox);


			String src = formMapping.isEnable() ? "/img/pause_icn.png" : "/img/play_icn.png";
			String tooltipStr = formMapping.isEnable() ? "Pause" : "Active";

			Image img = new Image(src);
			img.setStyle("margin-right:10px;cursor:pointer;");
			img.setTooltiptext(tooltipStr);
			img.setAttribute("type", "userPause");
			img.addEventListener("onClick",this);

			img.setParent(hbox);

			Image delImg = new Image("/img/action_delete.gif");
			delImg.setTooltiptext("Delete");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setAttribute("type", "userDelete");
			delImg.setParent(hbox);

			row.setValue(formMapping);

			hbox.setParent(row);
		}
	}//redraw

	public void onSelect$webformsPerPageLBId() {
		try {
			int n =Integer.parseInt(webformsPerPageLBId.getSelectedItem().getLabel().trim());
			webformsPagingId.setPageSize(n);
			redrawForms(0, n);
			int count = webformsGridId.getPageCount();
			for(; count>0; count--) {
				webformsPerPageLBId.removeItemAt(count-1);
			}

			//System.gc();

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//onSelect$storesPerPageLBId()

	public void onSelect$webformsTabBoxId() {

		//	logger.info("----just entered in tabbox selction ----");
		session.removeAttribute("formMapping");
		selectedFormName = null;
		MessageUtil.clearMessage();
		gotoStep(1);
		formNameTbId.setValue("");
		formNameTbId.setDisabled(false);
		//webformUrlTbId.setValue("http://");
        webformUrlTbId.setValue("https://");
		webformUrlTbId.setDisabled(false);
		urlAnchDivId.setVisible(false);
		subsListLbId.setSelectedIndex(0);
		editListAnchId.setVisible(false);
		simpleSignUpRadioId.setChecked(true);
		ltySignUpDivId.setVisible(false);
		simpleSignUpDivId.setVisible(true);
		autoSelectCardRadioId.setChecked(true);
		pickCardSetRadioId.setChecked(false);
		pickFromCardSetLbId.setSelectedIndex(0);
		pickFromCardSetLbId.setVisible(false);
		ltyProgramLbId.setSelectedIndex(0);
		ltySubsListLbId.setSelectedIndex(0);
		ltyEditListAnchId.setVisible(false);
		
		parentalOptLtDiv.setVisible(false);
		parentalConsentCbId.setChecked(false);
		successTbId.setValue("");
		errorTbId.setValue("");
		existSubsciberTbId.setValue("");
		parenatConsentTbId.setValue("");
//		parentalConsentChkBox.setVisible(false);
		parentalEmailDivId.setVisible(false);
		feedBackFormDivId.setVisible(false);
		sendFeedBackMailOnsuccess.setChecked(false);
		sendFeedbackSmsOnsuccess.setChecked(false);
		subsListFeedBackId.setSelectedIndex(0);
		editListFeedBackId.setVisible(false);
		issueRewardCheckBoxId.setChecked(false);
		issueRewardTypeListId.setVisible(false);
		issueRewardTypeValueTextId.setVisible(false);
		issueRewardTypeListId.setSelectedIndex(0);
		issueRewardTypeValueTextId.setValue(Constants.STRING_NILL);
		sendSimpleSignMailOnsuccess.setChecked(false);
		sendSimpleSignSmsOnsuccess.setChecked(false);
		
		onCheck$sendFeedBackMailOnsuccess();
		onCheck$sendFeedbackSmsOnsuccess();
		onCheck$sendSimpleSignMailOnsuccess();
		onCheck$sendSimpleSignSmsOnsuccess();
	}//onSelect$webformsTabBoxId()
	
	//SORTING Hard coded
public String orderby_colName="activeSince",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
	
	
	public void onClick$sortbyFormName() {
		orderby_colName = "formMappingName";
		desc2ascasc2desc();
		redrawForms(0,webformsPagingId.getPageSize());
		
	}public void onClick$sortbyFormURL() {
		orderby_colName = "URL";
		desc2ascasc2desc();
		redrawForms(0,webformsPagingId.getPageSize());
		
	}
	public void onClick$sortbyActivatedOn() {
		orderby_colName = "activeSince";
		desc2ascasc2desc();
		redrawForms(0,webformsPagingId.getPageSize());
		
	}

	public void onClick$webformsTabId(){
		int totalSize = formMappingDao.findAllUserId(currentUser.getUserId()); 

		webformsPagingId.setTotalSize(totalSize);
		webformsPagingId.setActivePage(0);
		webformsPagingId.addEventListener("onPaging", this);
		redrawForms(0,webformsPagingId.getPageSize());
		
	}
	
	public void onClick$urlEditAnchId() {
		if(webformUrlTbId.isDisabled()){
			webformUrlTbId.setDisabled(false);
			urlAnchDivId.setVisible(false);
		}
	}
	
	public void onClick$urlSubmitBtnId() throws Exception{
		
		if(!validateForms()){
			return;
		}

		if(!formSettings()){
			return;
		}
		
		availableCards();
		
	//	gotoStep(2);

	}//onclick$urlSubmitBtnId()

	public boolean validateForms() {
		String formName = formNameTbId.getValue().trim();
		String url=webformUrlTbId.getValue().trim();


		if(formName.trim().length() == 0) {

			MessageUtil.setMessage("Please provide form name. Form name cannot be left empty.", "color:red;");
			return false;

		}

		if(url.trim().length() == 0){
			MessageUtil.setMessage("Please provide Web-form Url. Web-form Url cannot be left empty.", "color:red;");
			return false;

		}

		if(url.equalsIgnoreCase("http://") || (!url.startsWith("http://") && !url.startsWith("https://")  ) ) {
			MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
			logger.error("Exception : Page URl field is empty .");
			return false;
		}

		if(simpleSignUpRadioId.isChecked()) {

			if(subsListLbId.getSelectedIndex()<=0){
				MessageUtil.setMessage("Please select a list.", "color:red;");
				return false;

			}
			
			if(parentalConsentCbId.isChecked() && parentalConsentChkBox.isChecked() && parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
				if(parentalConsentEmailLbId.getItems().size() == 1) {
					MessageUtil.setMessage("Please create at least one Parental Consent message under Auto Emails.", "color:red","TOP");
					return false;
				}else  {
					MessageUtil.setMessage("Please Select Parental Consent message.", "color:red","TOP");
					return false;
				}
			}
			
			if(sendSimpleSignMailOnsuccess.isChecked() && SimpleSignEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
				if(SimpleSignEmailLbId.getItems().size() == 1) {
					MessageUtil.setMessage("Please create at least one welcome message under Auto Emails.", "color:red","TOP");
					return false;
				}else  {
					MessageUtil.setMessage("Please Select welcome message.", "color:red","TOP");
					return false;
				}
			}
			
			if(sendSimpleSignSmsOnsuccess.isChecked() && SimpleSignSmsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectStr)) {
				if(SimpleSignSmsLbId.getItems().size() == 1) {
					MessageUtil.setMessage("Please create at least one welcome message under Auto SMS.", "color:red","TOP");
					return false;
				}else  {
					MessageUtil.setMessage("Please Select welcome SMS.", "color:red","TOP");
					return false;
				}
			}
			
			
		}
		if(ltyEnrollSignUpRadioId.isChecked()) {

			if(ltySubsListLbId.getSelectedIndex()<=0){
				MessageUtil.setMessage("Please select a list.", "color:red;");
				return false;

			}

			if(ltyProgramLbId.getSelectedIndex()<=0){
				MessageUtil.setMessage("Please select a program.", "color:red;");
				return false;

			}
			LoyaltyProgram prgmObj = (LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue();
			if(!OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(prgmObj.getStatus())){
				MessageUtil.setMessage("This program(" + prgmObj.getProgramName() + ") is suspended.Please select an active program from the drop down.", "color:red;");
				return false;
			}

			if(autoSelectCardRadioId.isChecked() && !prgmObj.getMembershipType().contentEquals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				List<LoyaltyCardSet> activeCardSets = loyaltyCardSetDao.findActiveByProgramId(((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());
				if(activeCardSets == null){
					MessageUtil.setMessage("No active virtual card-sets found for the configured loyalty program.", "color:red;");
					return false;
				}
			}
			
			if(pickCardSetRadioId.isChecked()){
				if(pickFromCardSetLbId.getSelectedIndex() <= 0){
					MessageUtil.setMessage("Please select a card-set.", "color:red;");
					return false;

				}
				LoyaltyCardSet cardSetObj = (LoyaltyCardSet) pickFromCardSetLbId.getSelectedItem().getValue();
				if(!OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equalsIgnoreCase(cardSetObj.getStatus())){
					MessageUtil.setMessage("This card-set(" + cardSetObj.getCardSetName() + ") is suspended.Please select an active card-set from the drop down.", "color:red;");
					return false;
				}
			}
			
			if(ltySubsListLbId.getSelectedIndex()<=0){
				MessageUtil.setMessage("Please select a list.", "color:red;");
				return false;

			}

		}if(feedBackformRadioId.isChecked()) {
			
			if(sendFeedBackMailOnsuccess.isChecked() && feedbackEmailLbId.getSelectedIndex() == 0) {
				MessageUtil.setMessage("Please select mailing Template.", "color:red;");
				return false;
			}
			if(sendFeedbackSmsOnsuccess.isChecked() && feedbackSmsLbId.getSelectedIndex() == 0) {
				MessageUtil.setMessage("Please select sms Template.", "color:red;");
				return false;
			}
			if(subsListFeedBackId.getSelectedIndex()<=0){
				MessageUtil.setMessage("Please select a list.", "color:red;");
				return false;

			}
		}

		//special characters handling??????????????????;
		FormMapping formMapping=(FormMapping)session.getAttribute("formMapping");
		if(formMapping == null) {

			boolean isExist=formMappingDao.findAllByFormName(currentUser.getUserId(),formName);
			if(isExist) {
				MessageUtil.setMessage("Form name already in use. Please provide another form name.", "color:red;");
				return false;
			}
			isExist=formMappingDao.findAllByUrl(url);
			if(isExist ){
				MessageUtil.setMessage("URL already configured. Please provide another URL.", "color:red;");
				return false;
			}

		}
		return true;


	}//validateForms()

	public void onSelect$subsListLbId(){
		if(subsListLbId.getSelectedIndex() > 0) {
			editListAnchId.setVisible(true);
		}
		else {
			editListAnchId.setVisible(false);
		}
	}
	
	public void onSelect$ltySubsListLbId(){
		if(ltySubsListLbId.getSelectedIndex() > 0) {
			ltyEditListAnchId.setVisible(true);
		}
		else {
			ltyEditListAnchId.setVisible(false);
		}
	}
	
	public void onClick$editListAnchId() {
		try{
			MailingList mailingList = (MailingList)subsListLbId.getSelectedItem().getValue();
			MessageUtil.clearMessage();
			if(mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_POS)){
				Redirect.goTo(PageListEnum.ADMIN_POS_SETTINGS);
			}
			else {
				Sessions.getCurrent().setAttribute("mailingList", mailingList);
				logger.debug("Redirecting to edit settings for the list - " + mailingList.getListName());
				Redirect.goTo(PageListEnum.CONTACT_LIST_EDIT);
			}
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
	}
	
	public void onClick$ltyEditListAnchId() {
		try{
			MailingList mailingList = (MailingList)ltySubsListLbId.getSelectedItem().getValue();
			MessageUtil.clearMessage();
			if(mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_POS)){
				Redirect.goTo(PageListEnum.ADMIN_POS_SETTINGS);
			}
			else {
				Sessions.getCurrent().setAttribute("mailingList", mailingList);
				logger.debug("Redirecting to edit settings for the list - " + mailingList.getListName());
				Redirect.goTo(PageListEnum.CONTACT_LIST_EDIT);
			}
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
	}
	
	
	public void onClick$editListFeedBackId() {
		try{
			MailingList mailingList = (MailingList)subsListFeedBackId.getSelectedItem().getValue();
			MessageUtil.clearMessage();
			if(mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_POS)){
				Redirect.goTo(PageListEnum.ADMIN_POS_SETTINGS);
			}
			else {
				Sessions.getCurrent().setAttribute("mailingList", mailingList);
				logger.debug("Redirecting to edit settings for the list - " + mailingList.getListName());
				Redirect.goTo(PageListEnum.CONTACT_LIST_EDIT);
			}
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
	}

	public void onClick$createListAnchId() {
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD);

	}
	
	public void onClick$ltyCreateListAnchId() {
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD);

	}

	public void onClick$createListFeedBackId() {
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD);

	}
	
	public void onCheck$parentalConsentCbId(){

		parentalEmailDivId.setVisible(parentalConsentCbId.isChecked());
//		parentalConsentChkBox.setVisible(parentalConsentCbId.isChecked());
		parentalOptLtDiv.setVisible(parentalConsentCbId.isChecked());
		onCheck$parentalConsentChkBox();
	}

	
	public void  onCheck$parentalConsentChkBox() {
		if(parentalConsentCbId.isChecked()){
			parentalOptLtDiv.setVisible(parentalConsentChkBox.isChecked());
			if(parentalConsentEmailLbId.getSelectedIndex() == 0) {
				 	 previewImageId.setStyle("display:none");
					 parentalPreviewBtnId.setVisible(false);
					 parentalEditMsgBtnId.setVisible(false);
			}
		}
		getParentalConsentTemplateList();

	} //onCheck$parentalConsentCbId
private final String selectStr = "Select Message";
private final String defaultStr = "Default Message";
	public  void getParentalConsentTemplateList() {

		List<CustomTemplates> consentlist = null;
		try {
			consentlist = customeTemplatesDao.findAllByUser(currentUser.getUserId(),"parentalConsent");

			Components.removeAllChildren(parentalConsentEmailLbId);

			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(parentalConsentEmailLbId);

			if(consentlist != null && consentlist.size() >0) {

				for (CustomTemplates customTemplates : consentlist) {

					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(parentalConsentEmailLbId);

				}
			}
			if(parentalConsentEmailLbId.getItemCount() > 0 ) parentalConsentEmailLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");

		}
	} // getParentalConsentTemplateLi

	
	public  void getFeedBackFormTemplateList() {

		List<CustomTemplates> consentlist = null;
		try {
			consentlist = customeTemplatesDao.findAllByUser(currentUser.getUserId(),"feedbackform");
			Components.removeAllChildren(feedbackEmailLbId);

			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(feedbackEmailLbId);

			if(consentlist != null && consentlist.size() >0) {

				for (CustomTemplates customTemplates : consentlist) {

					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(feedbackEmailLbId);

				}
			}
			if(feedbackEmailLbId.getItemCount() > 0 ) feedbackEmailLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");

		}
	}
	
	
	public  void getWelcomeFormTemplateList() {

		List<CustomTemplates> consentlist = null;
		try {
			consentlist = customeTemplatesDao.findAllByUser(currentUser.getUserId(),"webformWelcomeEmail");
			Components.removeAllChildren(SimpleSignEmailLbId);

			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(SimpleSignEmailLbId);

			if(consentlist != null && consentlist.size() >0) {

				for (CustomTemplates customTemplates : consentlist) {

					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(SimpleSignEmailLbId);

				}
			}
			if(SimpleSignEmailLbId.getItemCount() > 0 ) SimpleSignEmailLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");

		}
	}
	
	
	
	
	

	public void onClick$parentalPreviewBtnId() {

		String templateContent = "";

		CustomTemplates customTemplates = parentalConsentEmailLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
		}else {
			if(customTemplates!=null && customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.","color:red;");
					 return;
				 }
			}
		}

		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		previewWin.setVisible(true);

	}//onClick$welcomePreviewBtnId()
	
	
	public void onClick$SimpleSignPreviewBtnId() {
		String templateContent = "";

		CustomTemplates customTemplates = SimpleSignEmailLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			//templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
		}else {
			if(customTemplates!=null && customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.","color:red;");
					 return;
				 }
			}
		}

		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		previewWin.setVisible(true);
	}
	
	public void onClick$feedbackPreviewBtnId() {
		String templateContent = "";

		CustomTemplates customTemplates = feedbackEmailLbId.getSelectedItem().getValue();
		if(customTemplates == null) {
			//templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
		}else {
			if(customTemplates!=null && customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				templateContent = customTemplates.getHtmlText();
			}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
				 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				 if(myTemplates!=null) {
					 templateContent = myTemplates.getContent();
				 }else {
					 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.","color:red;");
					 return;
				 }
			}
		}

		Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
		previewWin.setVisible(true);
	}
	

	public void onClick$parentalEditMsgBtnId() {
		//logger.info("just Call addnew button");
		logger.info("custom template value "+parentalConsentEmailLbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", parentalConsentEmailLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT);
		session.setAttribute("fromAddNewBtn","contact/webform");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.	CONTACT_MANAGE_AUTO_EMAILS_BEE);
		//Redirect.goTo("contact/customMListWelcomeMsg");
	}//onClick$welcomeEditMsgBtnId()
	
	
	public void onClick$feedbackEditMsgBtnId() {
		logger.info("custom template value "+feedbackEmailLbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", feedbackEmailLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM);
		session.setAttribute("fromAddNewBtn","contact/webform");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.	CONTACT_MANAGE_AUTO_EMAILS_BEE);
	}
	
	
	public void onClick$SimpleSignEditMsgBtnId() {
		logger.info("custom template value "+SimpleSignEmailLbId.getSelectedItem().getValue());
		session.setAttribute("editCustomTemplate", SimpleSignEmailLbId.getSelectedItem().getValue());
		session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL);
		session.setAttribute("fromAddNewBtn","contact/webform");
		session.setAttribute("Mode", "edit");
		Redirect.goTo(PageListEnum.	CONTACT_MANAGE_AUTO_EMAILS_BEE);
	}

	private void gotoStep(int step) {

		urlDivId.setVisible(step==1);
		mappingsDivId.setVisible(step==2);
		redirectUrlsDivId.setVisible(step==2);
		buttonBackAndRedirect.setVisible(step==2);
		configureDivId.setVisible(step==3);
		if(step == 2 && feedBackformRadioId.isChecked()) {
			issueRewardDivId.setVisible(true);
			parenatConsentTbId.setVisible(false);
			parenatConsentLabelId.setVisible(false);
		}else {
			issueRewardDivId.setVisible(false);
			parenatConsentTbId.setVisible(true);
			parenatConsentLabelId.setVisible(true);
		}
	} // gotoStep
	public void onClick$redirectBackmitBtnId(){
		MessageUtil.clearMessage();
		gotoStep(1);
	}

	public boolean  formSettings(){

		try {
			MessageUtil.clearMessage();
			String enteredURL = webformUrlTbId.getValue();//to read the form of given url

			//to fetch the html code in form
			String orgHtml = fetchHtml(enteredURL);
			//	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>HTML="+orgHtml);
			if(orgHtml == null){
				//MessageUtil.setMessage("", "color:red");
				return false;
			}

			//******************** Removing the commented html code *****************************

			Pattern r = Pattern.compile("<!--.*?-->", Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(orgHtml);
			StringBuffer sb = new StringBuffer();

			while (m.find()) {
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);

			String outHtml = sb.toString();

			//*********************************  defgdg *******************************************8

			String formPattrnStr = "<\\s*?form(.*?)>(.*?)<\\s*?/\\s*?form\\s*?>";

			// Search pattern with 4 groups : 1:2 in name:type AND 3:4 in type:name. 
			// If name:type is found, type:name is null and vice versa.  
			String namePatternStr = ".*?(?:name\\s*?=\\s*?['\"](.+?)['\"].*?type\\s*?=\\s*?['\"](.+?)['\"].*?|type\\s*?=\\s*?['\"](.+?)['\"].*?name\\s*?=\\s*?['\"](.+?)['\"].*?|name\\s*?=\\s*?['\"](.+?)['\"])";
			String inputPatternStr = "<\\s*?(?:(input|select|textarea))(\\s.*?)>";
			String selectPatternStr = ".*?name\\s*?=\\s*?['\"](.+?)['\"]";


			r = Pattern.compile(formPattrnStr, Pattern.CASE_INSENSITIVE);
			m = r.matcher(outHtml);
			boolean found = false;
			submitFieldVec.clear();

			// TODO need to change to while loop for multi FORM tag page.
			// Iterate Each input field.

			inputFieldWithTypeMap = new LinkedHashMap<String, String>();
			int totalFormsCount=0;
			urlFormNameList.clear();
			String noFormNameStr = "NoName-";
			int noFormNameCount = 0;
			// form loop
			while(m.find()) {

				found = true;
				totalFormsCount++;

				String formPropersStr = m.group(1).trim();
				Pattern namePattern = Pattern.compile("name\\s*?=\\s*?['\"](.+?)['\"]", Pattern.CASE_INSENSITIVE);
				Matcher nameMatcher = namePattern.matcher(formPropersStr);
				String formNameStr=null;
				if(nameMatcher.find()) {

					formNameStr = nameMatcher.group(1);
				}

				if(formNameStr == null ) {
					formNameStr = noFormNameStr+(++noFormNameCount);
				}

				urlFormNameList.add(formNameStr);
				//	logger.debug("urlFormNameList="+urlFormNameList);

				//	logger.debug("Match found  >>>>> " + m.group(2).trim());
				String formInnerHtml = m.group(2).trim();
				//	logger.debug("sys out :"+formInnerHtml);			

				Pattern inputR1 = Pattern.compile(inputPatternStr, Pattern.CASE_INSENSITIVE);
				Matcher inputM1 = inputR1.matcher(formInnerHtml);

				// input/select Loop
				while(inputM1.find()) {
					if(inputM1.group(1).equalsIgnoreCase("input") || inputM1.group(1).equalsIgnoreCase("textarea")) {
						Pattern r1 = Pattern.compile(namePatternStr, Pattern.CASE_INSENSITIVE);
						Matcher m1 = r1.matcher(inputM1.group(2));

						while(m1.find()) {
							if(m1.group(1) != null) {

								if(m1.group(2).trim().equalsIgnoreCase("hidden")) {
									// continue;
								} else if (m1.group(2).trim().equalsIgnoreCase("image")) {
									submitFieldVec.add(formNameStr+"::"+m1.group(1)); continue;
								} else if (m1.group(2).trim().equalsIgnoreCase("submit")) {
									submitFieldVec.add(formNameStr+"::"+m1.group(1)); continue;
								} else if (m1.group(2).trim().equalsIgnoreCase("reset") || 
										m1.group(2).trim().equalsIgnoreCase("password")) {
									continue;
								}

								inputFieldWithTypeMap.put(formNameStr+"::"+m1.group(1), m1.group(2));
							} 
							else if(m1.group(3) != null) {

								if(m1.group(3).trim().equalsIgnoreCase("hidden")) {
									// continue;
								} else if (m1.group(3).trim().equalsIgnoreCase("image")) {
									submitFieldVec.add(formNameStr+"::"+m1.group(4)); continue;
								} else if (m1.group(3).trim().equalsIgnoreCase("submit")) {
									submitFieldVec.add(formNameStr+"::"+m1.group(4)); continue;
								} else if (m1.group(3).trim().equalsIgnoreCase("reset")  || 
										m1.group(3).trim().equalsIgnoreCase("password")) {
									continue;
								}

								inputFieldWithTypeMap.put(formNameStr+"::"+m1.group(4), m1.group(3));
							}
							else if(m1.group(5) != null) {
								inputFieldWithTypeMap.put(formNameStr+"::"+m1.group(5), "text");		// default type for input element.
							}
						}  // while



					} // if
					else if(inputM1.group(1).equalsIgnoreCase("select")) {
						// Search for select input fields.
						Pattern r1 = Pattern.compile(selectPatternStr, Pattern.CASE_INSENSITIVE);
						Matcher m1 = r1.matcher(inputM1.group(2));

						while(m1.find()) {

							if(m1.group(1) != null) {
								inputFieldWithTypeMap.put(formNameStr+"::"+m1.group(1), "select");
							} 
						}
					} // else

				}// while

				//	logger.debug("map obje"+inputFieldWithTypeMap);
				if(submitFieldVec.size() == 0) {

					Pattern r3 =  Pattern.compile(inputPatternStr, Pattern.CASE_INSENSITIVE);
					Matcher m3 =  r3.matcher(formInnerHtml);

					if(m3.find()) {
						submitFieldVec.add(formNameStr+"::"+"submit"); 
					}
				}

			} // while

			if(!found) {

				logger.debug("************** No Form tags found *****************");
				MessageUtil.setMessage("No form tags found in the given URL.", "red", "top");
				return false;
			} 

			if(urlFormNameList.size() < 1) {

				MessageUtil.setMessage("No form(s) found in the given URL with name attribute.", "red", "top");
				return false ;
			}

			//	logger.debug("Hash Map contents : "+ inputFieldWithTypeMap);

			Components.removeAllChildren(urlFormsToMlLbId);
			if(urlFormNameList.size() > 0) {


				for (String formNameStr : urlFormNameList) {

					Listitem listItem = new Listitem(formNameStr);
					listItem.setParent(urlFormsToMlLbId);
				}
			}


			FormMapping formMapping = (FormMapping)session.getAttribute("formMapping");
			if(formMapping != null) {
				String selFormName = formMapping.getFormName();

				for(Listitem item : urlFormsToMlLbId.getItems()) {

					if(item.getLabel().equals(selFormName)) {
						item.setSelected(true);
						break;
					}


				}

				if(urlFormsToMlLbId.getSelectedCount() == 0 && urlFormsToMlLbId.getItemCount() > 0 ) {

					urlFormsToMlLbId.setSelectedIndex(0);
				}



				formMappings();
				setSelectionOfInputFields(formMapping.getInputFieldMapping());


			}
			else{
				if(urlFormsToMlLbId.getItemCount() > 0){

					urlFormsToMlLbId.setSelectedIndex(0);
					inputSettings();
					//formMappings();
				}
			}
			//formMappings();
		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
			return false;
		}

		return true;
	}//formSettings()

	public void onSelect$urlFormsToMlLbId() {

		//formMappings();
	inputSettings();

	}


	private String fetchHtml(String urlStr) {
		try {
			try {
				StringBuffer pageSb = new StringBuffer();
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				conn.connect();
				logger.debug("Connection opened to the specified url :" + urlStr);
				DataInputStream in = new DataInputStream (conn.getInputStream ()) ;
				BufferedReader d = new BufferedReader(new InputStreamReader(in));
				logger.debug("Reader obj created");

				String lineStr=null;
				//while(d.ready()) {

				while( (lineStr = d.readLine()) != null ) {
					pageSb.append(lineStr+" ");
				} // while

				logger.debug("Read the data from the URL, data lenght:" + pageSb.length());
				in.close();
				d.close();
				//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>" + pageSb.toString());

				//String content = HTMLUtility.getBodyContentOnly(pageSb.toString(),urlStr);
				String content = pageSb.toString(); 

				if(content == null) {
					Messagebox.show("Invalid HTML: Unable to fetch HTML content from the specified URL.",
							"Error", Messagebox.OK, Messagebox.ERROR);
					return null;
				}
				return content;

			} catch (MalformedURLException e) {
				Messagebox.show("Malformed URL: Unable to fetch the web-page from the specified URL.",
						"Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("MalformedURLException : ", e);
			} catch (IOException e) {
				Messagebox.show("Network problem experienced while fetching the page. "
						, "Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("IOException : ", e);
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
		return null;

	}//fetchHtml

	public void formMappings(){

		MailingList mailingList = null;
		if( !ltyCardVerificationRadioId.isChecked()) {
			mailingList =(MailingList)subsListLbId.getSelectedItem().getValue();

		}
		MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		Set<String> inputMapKeySet = inputFieldWithTypeMap.keySet();

		//	logger.debug("key set is"+inputMapKeySet);

		String formNameStr = urlFormsToMlLbId.getSelectedItem().getLabel();

		selectedFormName = formNameStr;
		if(selectedFormName == null || selectedFormName.trim().isEmpty()) return;

		count = 0;

		// Added for web-form selected fields display
		String formMapStr="";
		HashMap<String, List> inputMapSettingHM = null;
		FormMapping formMapping = (FormMapping)session.getAttribute("formMapping");
		if(formMapping != null){
			formMapStr=formMapping.getInputFieldMapping();
		}

		if(formMapStr != null) {
			String[] tagMapStrArr = formMapStr.split("\\|");
			String[] tempStr = null;

			if(tagMapStrArr.length >0) {
				inputMapSettingHM = new HashMap<String, List>();

				for(int i = 0; i< tagMapStrArr.length; i++) {

					tempStr = tagMapStrArr[i].trim().split("_:_");


					List list = new ArrayList();

					if(tempStr.length == 3) {

						list.add(tempStr[1]);
						list.add(tempStr[2]);
					} else if (tempStr.length == 2) {

						list.add(tempStr[1]);
					}	
					inputMapSettingHM.put(tempStr[0], list);
				}
			}
		} else {

			logger.error("Exception : Error occured while getting Input Field Mapping **");
			return;
		}




		Components.removeAllChildren(mappingRowsId);


		logger.debug("formMapping obj  is"+formMapping);
		for (String inputKey : inputMapKeySet) {	
			try {


				if(!inputKey.startsWith(selectedFormName+"::")) continue; // Skip other form elements
				count++;

				inputKey = inputKey.substring(selectedFormName.length()+2);

				if( formMapping != null && inputMapSettingHM.containsKey(inputKey)){


					logger.debug("input key  is"+inputKey);
					Row row = new Row();

					Label label = new Label(inputKey);
					//label.setId("fieldLabelId"+count);
					label.setParent(row);


					Listbox lb = new Listbox();
					//lb.setId("fieldLBId"+count);
					lb.setMold("select");
					lb.addEventListener("onSelect", this);

//					String defaultFieldStr = PropertyUtil.getPropertyValue("defaultFieldList");
//					String[] defaultFieldArray = defaultFieldStr.split(",");

					Listitem li = null;


					//		MailingList  mailingList =subsListLbId.getSelectedItem().getValue();

					//		logger.info("<<<<<<<<< list type : "+ mailingList.getListType());

					POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao"); 

					List<POSMapping> posMappingsList = posMappingDao.findByType("'Contacts'", GetUser.getUserId());
					if(posMappingsList.size() == 0 ) {

						MessageUtil.setMessage("No list settings found for your account.", "color:red;");
						return;

					}


					logger.debug("posMappingsList size::"+posMappingsList.size());
					li = new Listitem("--select--");
					li.setSelected(true);
					li.setParent(lb);

					for (POSMapping posMapping : posMappingsList) {

						Listitem cfListItem = new Listitem();  
						cfListItem.setLabel(posMapping.getCustomFieldName());
						if((posMapping.getCustomFieldName().startsWith("UDF"))){
							cfListItem.setLabel(posMapping.getDisplayLabel());
							cfListItem.setValue(posMapping.getCustomFieldName());
							cfListItem.setAttribute("type", posMapping.getDataType());
						}else{

							cfListItem.setValue(Utility.genFieldContMap.get(posMapping.getCustomFieldName()));
						}
						cfListItem.setParent(lb);

					}

					li = new Listitem("BirthDay_Year", "BirthDay_Year");
					li.setParent(lb);

					li = new Listitem("BirthDay_Month", "BirthDay_Month");
					li.setParent(lb);

					li = new Listitem("BirthDay_Day", "BirthDay_Day");
					li.setParent(lb);

					if(simpleSignUpRadioId.isChecked() && parentalConsentCbId.isChecked() &&  parentalConsentChkBox.isChecked()) {

						li = new Listitem("Parent Email", "ParentEmail");
						li.setParent(lb);
					}

					if(ltyEnrollSignUpRadioId.isChecked()) {
						li = new Listitem("Loyalty Enroll", "LoyaltyEnroll");
						li.setParent(lb);
						li = new Listitem("Store Location","StoreLocation");
						li.setParent(lb);
						
						//to bypass optin
						if(mobileOptinCbId.isChecked()){
						li = new Listitem("Loyalty mobile optin", "Loyalty_mobile_optin");
						li.setParent(lb);
						}
						
						if(doIssuePointsId.isChecked()){
							li = new Listitem("Loyalty Points", "loyaltyPoints");
							li.setParent(lb);
							
							li = new Listitem("Loyalty Currency", "loyaltyCurrency");
							li.setParent(lb);
							
							li = new Listitem("Invoice Number", "invoiceNumber");
							li.setParent(lb);
							
							li = new Listitem("Invoice Amount", "invoiceAmount");
							li.setParent(lb);
							
						}
						
						
						
					}
					if(ltyCardVerificationRadioId.isChecked()) {
						li= new Listitem(" Card Number","CardNumber");
						li.setParent(lb);

						li= new Listitem(" Card Pin","CardPin");
						li.setParent(lb);
					}
					if(feedBackformRadioId.isChecked()) {
						for (int i = 1; i <= 20; i++) {
							li= new Listitem(" Rating "+i+"","Rating"+i+"");
							li.setParent(lb);
						}
						li= new Listitem(" customerNo","customerNo");
						li.setParent(lb);
						li= new Listitem(" DOCSID","DOCSID");
						li.setParent(lb);
						li= new Listitem(" Feedback","FeedBackMessage");
						li.setParent(lb);
						li= new Listitem(" Store","feedBackStore");
						li.setParent(lb);
					}
					if(mailingList != null){

						if(mailingList.isCustField()) { // Add Custom Fields. 
							List<MLCustomFields> mlFieldsList = mlCFDao.findAllByList(mailingList);
							for (MLCustomFields customFields : mlFieldsList) {
								Listitem cfListItem = new Listitem();  
								cfListItem.setLabel("CF_"+customFields.getCustFieldName());
								cfListItem.setValue("CF_"+ 
										customFields.getCustFieldName() + "_" + customFields.getDataType() + "_" +customFields.getFieldIndex());
								cfListItem.setParent(lb);
							}
						}
					}

					Div div = new Div();
					//div.setId("div"+ count);
					div.setParent(row);

					lb.setParent(div);


					// Add delete button 
					Image img = new Image("/img/icons/delete_icon.png");
					img.setParent(row);
					img.addEventListener("onClick", new EventListener() {
						@Override
						public void onEvent(Event evt) throws Exception {
							Image tempImg = (Image)evt.getTarget();
							if(tempImg != null) {
								tempImg.getParent().setVisible(false);
							}
						}
					});


					row.setParent(mappingRowsId);




				}

			} catch (Exception e) {
				logger.error("Exception ::", e);
			}


		}//for each


	}//formMappings()


	public void inputSettings(){

		MailingList mailingList = null;
		if( !ltyCardVerificationRadioId.isChecked()) {
			mailingList =(MailingList)subsListLbId.getSelectedItem().getValue();

		}
		MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		Set<String> inputMapKeySet = inputFieldWithTypeMap.keySet();

		logger.debug("key set is"+inputMapKeySet);

		String formNameStr = urlFormsToMlLbId.getSelectedItem().getLabel();

		selectedFormName = formNameStr;
		if(selectedFormName == null || selectedFormName.trim().isEmpty()) {
			return;
		}
		count = 0;

		Components.removeAllChildren(mappingRowsId);

		for (String inputKey : inputMapKeySet) {	
			try {

				if(!inputKey.startsWith(selectedFormName+"::")) continue; // Skip other form elements
				count++;

				inputKey = inputKey.substring(selectedFormName.length()+2);

				//	logger.debug("input key  is"+inputKey);
				Row row = new Row();

				Label label = new Label(inputKey);
				//label.setId("fieldLabelId"+count);
				label.setParent(row);


				Listbox lb = new Listbox();
				//lb.setId("fieldLBId"+count);
				lb.setMold("select");
				lb.addEventListener("onSelect", this);

//				String defaultFieldStr = PropertyUtil.getPropertyValue("defaultFieldList");
//				String[] defaultFieldArray = defaultFieldStr.split(",");

				Listitem li = null;


				//		MailingList  mailingList =subsListLbId.getSelectedItem().getValue();

				//		logger.info("<<<<<<<<< list type : "+ mailingList.getListType());

				POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao"); 

				List<POSMapping> posMappingsList = posMappingDao.findByType("'Contacts'", GetUser.getUserId());
				if(posMappingsList.size() == 0 ) {

					MessageUtil.setMessage("No list settings found for your account.", "color:red;");
					return;

				}


				logger.debug("posMappingsList size::"+posMappingsList.size());
				li = new Listitem("--select--");
				li.setSelected(true);
				li.setParent(lb);

				for (POSMapping posMapping : posMappingsList) {

					Listitem cfListItem = new Listitem();  
					cfListItem.setLabel(posMapping.getCustomFieldName());
					if((posMapping.getCustomFieldName().startsWith("UDF"))){
						cfListItem.setLabel(posMapping.getDisplayLabel());
						cfListItem.setValue(posMapping.getCustomFieldName());
						cfListItem.setAttribute("type", posMapping.getDataType());
					}else{

						cfListItem.setValue(Utility.genFieldContMap.get(posMapping.getCustomFieldName()));
					}
					cfListItem.setParent(lb);

				}

				li = new Listitem("BirthDay_Year", "BirthDay_Year");
				li.setParent(lb);

				li = new Listitem("BirthDay_Month", "BirthDay_Month");
				li.setParent(lb);

				li = new Listitem("BirthDay_Day", "BirthDay_Day");
				li.setParent(lb);

				if(simpleSignUpRadioId.isChecked() && parentalConsentCbId.isChecked() &&  parentalConsentChkBox.isChecked()) {

					li = new Listitem("Parent Email", "ParentEmail");
					li.setParent(lb);

				}

				if(ltyEnrollSignUpRadioId.isChecked()) {
					li = new Listitem("Loyalty Enroll", "LoyaltyEnroll");
					li.setParent(lb);
					li = new Listitem("Store Location","StoreLocation");
					li.setParent(lb);
					//to bypass optin
					if(mobileOptinCbId.isChecked()){
					li = new Listitem("Loyalty mobile optin", "Loyalty_mobile_optin");
					li.setParent(lb);
					}
					if(doIssuePointsId.isChecked()){
						li = new Listitem("Loyalty Points", "loyaltyPoints");
						li.setParent(lb);
						
						li = new Listitem("Loyalty Currency", "loyaltyCurrency");
						li.setParent(lb);
						
						li = new Listitem("Invoice Number", "invoiceNumber");
						li.setParent(lb);
						
						li = new Listitem("Invoice Amount", "invoiceAmount");
						li.setParent(lb);
						
					}
				}
				if(ltyCardVerificationRadioId.isChecked()) {
					li= new Listitem(" Card Number","CardNumber");
					li.setParent(lb);

					li= new Listitem(" Card Pin","CardPin");
					li.setParent(lb);
				}
				if(feedBackformRadioId.isChecked()) {
					for (int i = 1; i <= 20; i++) {
						li= new Listitem(" Rating "+i+"","Rating"+i+"");
						li.setParent(lb);
					}
					li= new Listitem(" customerNo","customerNo");
					li.setParent(lb);
					li= new Listitem(" DOCSID","DOCSID");
					li.setParent(lb);
					li= new Listitem(" Feedback","FeedBackMessage");
					li.setParent(lb);
					li= new Listitem(" Store","feedBackStore");
					li.setParent(lb);
				}
				
				
				
				
				if(mailingList != null){

					if(mailingList.isCustField()) { // Add Custom Fields. 
						List<MLCustomFields> mlFieldsList = mlCFDao.findAllByList(mailingList);
						for (MLCustomFields customFields : mlFieldsList) {
							Listitem cfListItem = new Listitem();  
							cfListItem.setLabel("CF_"+customFields.getCustFieldName());
							cfListItem.setValue("CF_"+ 
									customFields.getCustFieldName() + "_" + customFields.getDataType() + "_" +customFields.getFieldIndex());
							cfListItem.setParent(lb);
						}
					}
				}

				Div div = new Div();
				//div.setId("div"+ count);
				div.setParent(row);

				lb.setParent(div);


				// Add delete button 
				Image img = new Image("/img/icons/delete_icon.png");
				img.setParent(row);
				img.addEventListener("onClick", new EventListener() {
					@Override
					public void onEvent(Event evt) throws Exception {
						Image tempImg = (Image)evt.getTarget();
						if(tempImg != null) {
							tempImg.getParent().setVisible(false);
						}
					}
				});


				row.setParent(mappingRowsId);

			} catch (Exception e) {
				logger.error("Exception ::", e);
			}

		}
	}

	public void setSelectionOfInputFields(String formMapStr) {

		// Get DB form map config.
		HashMap<String, List> inputMapSettingHM = null;
		MailingList mailingList = subsListLbId.getSelectedItem().getValue();
		if(formMapStr != null) {
			String[] tagMapStrArr = formMapStr.split("\\|");
			String[] tempStr = null;

			if(tagMapStrArr.length >0) {
				inputMapSettingHM = new HashMap<String, List>();

				for(int i = 0; i< tagMapStrArr.length; i++) {

					tempStr = tagMapStrArr[i].trim().split("_:_");


					List list = new ArrayList();

					if(tempStr.length == 3) {

						list.add(tempStr[1]);
						list.add(tempStr[2]);
					} else if (tempStr.length == 2) {

						list.add(tempStr[1]);
					}	
					inputMapSettingHM.put(tempStr[0], list);
				}
			}
		} else {

			logger.error("Exception : Error occured while getting Input Field Mapping **");
			return;
		}

		List rows  = mappingRowsId.getChildren();
		Iterator it = rows.iterator();
		while(it.hasNext()) {

			Row row = (Row)it.next();

			if(inputMapSettingHM.containsKey(((Label)row.getChildren().get(0)).getValue())) {

				List tempList = inputMapSettingHM.get(((Label)row.getChildren().get(0)).getValue());

				//	logger.debug("tempList ..."+ tempList);

				String inputMapValStr = tempList.get(0).toString(); 

				//	logger.debug("inputMapValStr is"+inputMapValStr);
				//List<Component> list = ((Listbox)row.getChildren().get(2)).getChildren();
				List<Component> list = (((Div)row.getChildren().get(1)).getChildren().get(0)).getChildren();
				//logger.info(list);
				for (Component listitemComp : list) {

					Listitem listitem = (Listitem)listitemComp;
					if(inputMapValStr.equalsIgnoreCase(listitem.getValue()+"")) {
						((Listbox)(((Div)row.getChildren().get(1)).getChildren().get(0))).setSelectedItem(listitem);
					}	


				} 


				// set Date format ...

				if (tempList.size() == 2) {

					//	logger.info("---Date format is set ---");
					Div  div = ((Div)row.getChildren().get(1));
					//	String lbNo = div.getId();
					//lbNo = lbNo.substring("div".length());

					Listbox listbx = createDateFormatListbox();

					/*Listitem li = new Listitem("MM-dd-yyyy");
					li.setParent(listbx);

					li = new Listitem("dd-MM-yyyy");
					li.setParent(listbx);

					li = new Listitem("yyyy-MM-dd HH:mm:ss");
					li.setParent(listbx);

					li = new Listitem("yyyy-MM-dd");
					li.setParent(listbx);


					li = new Listitem("MM dd,yy");
					li.setParent(listbx);

					li = new Listitem("MM/dd/yyyy");
					li.setParent(listbx);

					li = new Listitem("MM/dd/yy");
					li.setParent(listbx);


					li = new Listitem("dd/MM/yyyy HH:mm");
					li.setParent(listbx);

					li = new Listitem("MM/dd/yyyy HH:mm");
					li.setParent(listbx);

					li = new Listitem("MM/dd/yyyy HH:mm:ss");
					li.setParent(listbx);
*/


					//listbx.setMold("select");
					//listbx.setSelectedIndex(0);
					//	listbx.setId("dateFormatLbx"+ lbNo);

					List list2 = listbx.getItems();

					Listitem listitem2;

					for (Object object : list2) {

						listitem2 = (Listitem)object;

						if(listitem2.getLabel().equals(tempList.get(1).toString())) {

							logger.info("Field date forMatched...");
							listbx.setSelectedItem(listitem2);
						}
					}

					div.appendChild(new Label("Date Format :"));
					div.appendChild(listbx);

				}

			}



		}

	}//method end


	public void onCheck$sendFeedBackMailOnsuccess() {
		if(sendFeedBackMailOnsuccess.isChecked()) {
			feedbackEmailLbId.setVisible(true);
			if(feedbackEmailLbId.getSelectedIndex() == 0) {
				previewFeedbackImageId.setStyle("display:none");
			 	feedbackPreviewBtnId.setVisible(false);
			 	feedbackEditMsgBtnId.setVisible(false);
		     }
			getFeedBackFormTemplateList();
		}else if(!sendFeedBackMailOnsuccess.isChecked()) {
			feedbackEmailLbId.setVisible(false);
			previewFeedbackImageId.setStyle("display:none");
		 	feedbackPreviewBtnId.setVisible(false);
		 	feedbackEditMsgBtnId.setVisible(false);
		}
		
	}
	
	
	public void onCheck$sendSimpleSignMailOnsuccess() {
		if(sendSimpleSignMailOnsuccess.isChecked()) {
			SimpleSignEmailLbId.setVisible(true);
			if(SimpleSignEmailLbId.getSelectedIndex() == 0) {
				previewSimpleSignImageId.setStyle("display:none");
				SimpleSignPreviewBtnId.setVisible(false);
				SimpleSignEditMsgBtnId.setVisible(false);
		     }
				getWelcomeFormTemplateList();
		}else if(!sendSimpleSignMailOnsuccess.isChecked()) {
				SimpleSignEmailLbId.setVisible(false);
				previewSimpleSignImageId.setStyle("display:none");
				SimpleSignPreviewBtnId.setVisible(false);
				SimpleSignEditMsgBtnId.setVisible(false);
			}
		
	}
	
	public void onCheck$sendFeedbackSmsOnsuccess() {
		if(sendFeedbackSmsOnsuccess.isChecked()) {
			feedbackSmsLbId.setVisible(true);
			if(feedbackSmsLbId.getSelectedIndex() == 0) {
				previewFeedbackSmsImageId.setStyle("display:none");
				feedbackSmsPreviewBtnId.setVisible(false);
				feedbackSmsEditMsgBtnId.setVisible(false);
		     }
			getFeedBackSmsTemplateList();
		}else if(!sendFeedbackSmsOnsuccess.isChecked()) {
			feedbackSmsLbId.setVisible(false);
			previewFeedbackSmsImageId.setStyle("display:none");
			feedbackSmsPreviewBtnId.setVisible(false);
		 	feedbackSmsEditMsgBtnId.setVisible(false);
		}
	}
	
	public void onCheck$sendSimpleSignSmsOnsuccess() {
		if(sendSimpleSignSmsOnsuccess.isChecked()) {
			SimpleSignSmsLbId.setVisible(true);
			if(SimpleSignSmsLbId.getSelectedIndex() == 0) {
				previewSimpleSignSmsImageId.setStyle("display:none");
				SimpleSignSmsPreviewBtnId.setVisible(false);
				SimpleSignSmsEditMsgBtnId.setVisible(false);
		     }
			getWelcomeSmsTemplateList();
		}else if(!sendSimpleSignSmsOnsuccess.isChecked()) {
			SimpleSignSmsLbId.setVisible(false);
			previewSimpleSignSmsImageId.setStyle("display:none");
			SimpleSignSmsPreviewBtnId.setVisible(false);
			SimpleSignSmsEditMsgBtnId.setVisible(false);
		}
	}


	public void onClick$redirectSubmitBtnId(){

		StringBuffer formFieldMapSB = new StringBuffer();

		Set<String> inputMapKeySet = inputFieldWithTypeMap.keySet(); 	// TODO : Code duplication, need to remove inputMap exists.

		Vector tempVector = new Vector();
		List rowList = mappingRowsId.getChildren();
		Row row = null;

		for (Object object : rowList) {

			row = (Row)object;
			Listbox dateFormatLb=null;

			Label label = (Label)row.getChildren().get(0);
			Div div = (Div)row.getChildren().get(1);
			Listbox lb = (Listbox)div.getChildren().get(0);
			if(div.getChildren().size() > 1){
				dateFormatLb = (Listbox)div.getChildren().get(2);
			}

			if(!label.getParent().isVisible()) {
				continue;
			}

			if(lb.getSelectedItem() == null || lb.getSelectedIndex() == 0) {
				continue;
			} 

			if( formFieldMapSB.length() == 0) {
				formFieldMapSB.append(label.getValue() + "_:_" + lb.getSelectedItem().getValue());
				if(dateFormatLb != null && dateFormatLb.isVisible()) {
					formFieldMapSB.append("_:_" +dateFormatLb.getSelectedItem().getLabel());
				}
			} else { 			
				formFieldMapSB.append(" | " + label.getValue() + "_:_" + lb.getSelectedItem().getValue());
				if(dateFormatLb != null && dateFormatLb.isVisible()) {
					formFieldMapSB.append("_:_" +dateFormatLb.getSelectedItem().getLabel());
				}
			}

			if(tempVector.contains(lb.getSelectedItem().getValue())) {
				MessageUtil.setMessage("Cannot configure multiple input elements to the same contact field :"+lb.getSelectedItem().getValue(), "red", "top");
				return;
			}
			else if(lb.getSelectedIndex() > 0){
				tempVector.add(lb.getSelectedItem().getValue());
			}

		}

		if(!feedBackformRadioId.isChecked()) {
			if(!tempVector.contains(Utility.genFieldContMap.get("Email")) && !tempVector.contains("Email") 
					&& !tempVector.contains(Utility.genFieldContMap.get("Mobile")) && !tempVector.contains("Mobile")) {
	
				MessageUtil.setMessage("Either email address or mobile phone is mandatory. Please configure at least one of these fields.", "red", "top");
				return;
			}
		}else if (!tempVector.contains(Utility.genFieldContMap.get("customerNo")) && !tempVector.contains("customerNo")
				&& (!tempVector.contains(Utility.genFieldContMap.get("DOCSID")) && !tempVector.contains("DOCSID")) &&
				!tempVector.contains(Utility.genFieldContMap.get("Email")) && !tempVector.contains("Email") 
				&& !tempVector.contains(Utility.genFieldContMap.get("Mobile")) && !tempVector.contains("Mobile")) {
			
			MessageUtil.setMessage("Either email address , mobile phone / customerNo , DOCSID  is mandatory. Please configure at least one of these fields.", "red", "top");
			return;
		}

		if(simpleSignUpRadioId.isChecked() && parentalConsentCbId.isChecked()) {
			
			if((!tempVector.contains(Utility.genFieldContMap.get("BirthDay")) && !tempVector.contains("BirthDay"))  && !tempVector.contains("BirthDay_Year")){
				MessageUtil.setMessage("Birthday or Birth Year is mandatory. Please configure at least one of these fields.", "red", "top");
				return;
			}
			
			if(parentalConsentChkBox.isChecked() && !tempVector.contains("ParentEmail")) {	

				MessageUtil.setMessage("Parent Email is mandatory. Please configure parent email field.", "red", "top");
				return;
			}
		}
		
		if(ltyEnrollSignUpRadioId.isChecked() && mobileOptinCbId.isChecked()) {
			
			if(mobileOptinCbId.isChecked() && !tempVector.contains("Loyalty_mobile_optin")) {	

				MessageUtil.setMessage("Optin is mandatory. Please configure Optin field.", "red", "top");
				return;
			}
		}
		if(ltyCardVerificationRadioId.isChecked()){
			//Removed for HOF forms
			//if(!tempVector.contains("CardNumber") && !tempVector.contains("CardPin")) {
			if(!tempVector.contains("CardNumber")) {

				MessageUtil.setMessage("Loyalty Card Number is mandatory. Please configure Card Number field.", "red", "top");
				return;
			}
		}
		
		
		if(feedBackformRadioId.isChecked() && sendFeedBackMailOnsuccess.isChecked()) {
			if(sendFeedBackMailOnsuccess.isChecked() && (!tempVector.contains(Utility.genFieldContMap.get("Email")) && !tempVector.contains("Email"))) {	
				MessageUtil.setMessage("Email is mandatory. Please configure email field.", "red", "top");
				return;
			}
			
			if(feedBackformRadioId.isChecked() && !tempVector.contains("MobilePhone")) {	
				MessageUtil.setMessage("Mobile is mandatory. Please configure mobile field.", "red", "top");
				return;
			}
		}

		// If user opted for redirect another url if sucess.
		String redirectTo = successTbId.getValue().trim();

		if(redirectTo != null && redirectTo.length() >0 ){
			if(redirectTo.equalsIgnoreCase("http://") || (!redirectTo.startsWith("http://") && !redirectTo.startsWith("https://")  ) ) {
				MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
				logger.error("Exception : Page URl field is empty .");
				return;
			}

		}


		// If user opted for redirect another url if Failure.
		String failureRedirectTo = errorTbId.getValue().trim();
		if( failureRedirectTo != null && failureRedirectTo.length() >0){
			if(failureRedirectTo.equalsIgnoreCase("http://") || (!failureRedirectTo.startsWith("http://") && !failureRedirectTo.startsWith("https://")  ) ) {
				MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
				logger.error("Exception : Page URl field is empty .");
				return;
			}

		}




		// If user opted for redirect another url if DB Failure.
		String dbfailureRedirectTo = existSubsciberTbId.getValue().trim();
		if(dbfailureRedirectTo != null && dbfailureRedirectTo.length() > 0 )  {
			if(dbfailureRedirectTo.equalsIgnoreCase("http://") || (!dbfailureRedirectTo.startsWith("http://") && !dbfailureRedirectTo.startsWith("https://")  ) ) {
				MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
				logger.error("Exception : Page URl field is empty .");
				return;
			}

		}


		// If user opted for redirect another url if Parental Consent.
		String redirectParentalTo = parenatConsentTbId.getValue().trim();
		if( redirectParentalTo != null && redirectParentalTo.length() >0 ){
			if(redirectParentalTo.equalsIgnoreCase("http://") || (!redirectParentalTo.startsWith("http://") && !redirectParentalTo.startsWith("https://")  ) ) {
				MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
				logger.error("Exception : Page URl field is empty .");
				return;
			}

		}

		if(issueRewardCheckBoxId.isChecked() || issueRewardTypeListId.getSelectedIndex() == 0 || issueRewardTypeValueTextId.getValue().isEmpty()) {
			if( issueRewardTypeListId.getSelectedIndex() == 0 && issueRewardCheckBoxId.isChecked()) {
				MessageUtil.setMessage("Please select issue reward type.", "red", "top");
				return;
			}else if(issueRewardTypeValueTextId.getValue().isEmpty() && issueRewardCheckBoxId.isChecked()) {
				MessageUtil.setMessage("Please provide value for selcted issue reward type.", "red", "top");
				return;
			}
		}
		
		if(ltyEnrollSignUpRadioId.isChecked() && doIssuePointsId.isChecked() ) {
			if(!tempVector.contains("loyaltyCurrency") && !tempVector.contains("loyaltyPoints")) {
				MessageUtil.setMessage("Loyalty Currency/Points is mandatory. Please configure Loyalty Currency/Points field.", "red", "top");
				return;
			}
		}


		/*****to check whether form with same user,url and formname is already exist or not*******/

		// create new form mamppings

		FormMapping formMapping = (FormMapping)session.getAttribute("formMapping");


		String enteredURL = webformUrlTbId.getValue();
		logger.info("form mapaing obj is "+formMapping);

		if(formMapping == null) {

			formMapping = new FormMapping(currentUser,enteredURL, formNameTbId.getValue(), Calendar.getInstance(),OCConstants.FLAG_NO,OCConstants.FLAG_NO,OCConstants.FLAG_NO);

		}

		formMapping.setURL(enteredURL);
		formMapping.setFormName(selectedFormName);
		formMapping.setInputFieldMapping(formFieldMapSB.toString());

		if(simpleSignUpRadioId.isChecked()){
			formMapping.setFormType(Constants.WEBFORM_TYPE_SIGNUP);
		}
		else if(ltyEnrollSignUpRadioId.isChecked()){
			formMapping.setFormType(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP);
		}
		else if(ltyCardVerificationRadioId.isChecked()){
			formMapping.setFormType(Constants.WEBFORM_TYPE_LOYALTY_CARD);
		}else if(feedBackformRadioId.isChecked()){
			formMapping.setFormType(Constants.FEED_BACK_WEB_FORM);
		}
		int signupFormTypeSettingsVal=0;
		if(simpleSignUpRadioId.isChecked()) {

			long mailingList = ((MailingList)subsListLbId.getSelectedItem().getValue()).getListId().longValue();
			formMapping.setListId(mailingList);

			if(parentalConsentCbId.isChecked()) {
				formMapping.setEnableParentalConsent(OCConstants.FLAG_YES);
			} 
			else {
				formMapping.setEnableParentalConsent(OCConstants.FLAG_NO);
			}
			
			if(sendSimpleSignMailOnsuccess.isChecked()){
				formMapping.setCheckSimpleSignUpForEmail(OCConstants.FLAG_YES);
				Long simpleSignUpCustTemplateId = null;
				Listitem simpleSignupListitem = SimpleSignEmailLbId.getSelectedItem();
				if(simpleSignupListitem.getIndex() != 0) {
					CustomTemplates simpleSignUpCustomTemplates = (CustomTemplates)simpleSignupListitem.getValue();
					simpleSignUpCustTemplateId = simpleSignUpCustomTemplates.getTemplateId();
				}
				formMapping.setSimpleSignUpCustTemplateId(simpleSignUpCustTemplateId);
			}else {
				formMapping.setCheckSimpleSignUpForEmail(OCConstants.FLAG_NO);
				formMapping.setSimpleSignUpCustTemplateId(null);
			}
			if(sendSimpleSignSmsOnsuccess.isChecked()){
				formMapping.setCheckSimpleSignUpFormSms(OCConstants.FLAG_YES);
				Long simpleSignUpSmsTemplateId = null;
				Listitem simpleSignUpSMSListitem = SimpleSignSmsLbId.getSelectedItem();
				if(simpleSignUpSMSListitem.getIndex() != 0) {
					try {
						if(!simpleSignUpSMSListitem.getValue().equals(-1L)) {
							AutoSMS autoSMS = (AutoSMS)simpleSignUpSMSListitem.getValue();
							simpleSignUpSmsTemplateId = autoSMS.getAutoSmsId();
							formMapping.setSimpleSignUpSmsTemplateId(simpleSignUpSmsTemplateId);
						}else if(simpleSignUpSMSListitem.getValue().equals(-1L)) {
							formMapping.setSimpleSignUpSmsTemplateId(null);
						}
						
					} catch (Exception e) {
						logger.error("Exception ::"+e);
					}
				}
			}else {
				formMapping.setCheckSimpleSignUpFormSms(OCConstants.FLAG_NO);
				formMapping.setSimpleSignUpSmsTemplateId(null);
			}

			//Parental email Check option
			if(parentalConsentChkBox.isChecked()) {

				Long parentalCustTemplateId = null;
				Listitem parentlListitem = parentalConsentEmailLbId.getSelectedItem();

				if(parentlListitem.getIndex() != 0) {
					CustomTemplates parenatalCustomTemplates = (CustomTemplates)parentlListitem.getValue();
					parentalCustTemplateId = parenatalCustomTemplates.getTemplateId();
				}

				formMapping.setConsentCustTemplateId(parentalCustTemplateId);

			}else {
				formMapping.setConsentCustTemplateId(null);
			}
			formMapping.setCheckParentalEmail(parentalConsentChkBox.isChecked());

		}
		else if(ltyEnrollSignUpRadioId.isChecked()){
			long mailingList = ((MailingList)ltySubsListLbId.getSelectedItem().getValue()).getListId().longValue();
			formMapping.setListId(mailingList);

			formMapping.setConsentCustTemplateId(null);
			formMapping.setCheckParentalEmail(false);
			
			if(((LoyaltyProgram)ltyProgramLbId.getSelectedItem().getValue()).getMembershipType().contentEquals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				if(!tempVector.contains("MobilePhone")) {
					MessageUtil.setMessage("Mobile number is mandatory for mobile based loyalty enrolment.", "red", "top");
					return;
				}
			}
			
			formMapping.setLoyaltyProgramId(((LoyaltyProgram)ltyProgramLbId.getSelectedItem().getValue()).getProgramId().longValue());
			if(autoSelectCardRadioId.isChecked()){
				formMapping.setAutoSelectCard(OCConstants.FLAG_YES);
				formMapping.setLoyaltyCardsetId(null);
			}
			else if(pickCardSetRadioId.isChecked()){
				formMapping.setAutoSelectCard(OCConstants.FLAG_NO);
				formMapping.setLoyaltyCardsetId(((LoyaltyCardSet)pickFromCardSetLbId.getSelectedItem().getValue()).getCardSetId().longValue());
			}
			
			if(doIssuePointsId.isChecked()) {
				formMapping.setDoIssuePoints(OCConstants.FLAG_YES);
			}else {
				formMapping.setDoIssuePoints(OCConstants.FLAG_NO);
			}
			
		}else if(feedBackformRadioId.isChecked()){
			long mailingList = ((MailingList)subsListFeedBackId.getSelectedItem().getValue()).getListId().longValue();
			formMapping.setListId(mailingList);
			
			if(sendFeedBackMailOnsuccess.isChecked()){
				formMapping.setCheckFeedbackFormEmail(OCConstants.FLAG_YES);
				Long feedBackCustTemplateId = null;
				Listitem feedbackListitem = feedbackEmailLbId.getSelectedItem();
				if(feedbackListitem.getIndex() != 0) {
					CustomTemplates feedbackCustomTemplates = (CustomTemplates)feedbackListitem.getValue();
					feedBackCustTemplateId = feedbackCustomTemplates.getTemplateId();
				}
				formMapping.setFeedBackMailCustTemplateId(feedBackCustTemplateId);
			}else {
				formMapping.setCheckFeedbackFormEmail(OCConstants.FLAG_NO);
				formMapping.setFeedBackMailCustTemplateId(null);
			}
			if(sendFeedbackSmsOnsuccess.isChecked()){
				formMapping.setCheckFeedbackFormSms(OCConstants.FLAG_YES);
				Long feedBackSmsTemplateId = null;
				Listitem feedbackListitem = feedbackSmsLbId.getSelectedItem();
				if(feedbackListitem.getIndex() != 0) {
					try {
						if(!feedbackListitem.getValue().equals(-1L)) {
							AutoSMS autoSMS = (AutoSMS)feedbackListitem.getValue();
							feedBackSmsTemplateId = autoSMS.getAutoSmsId();
							formMapping.setFeedBackSmsTemplateId(feedBackSmsTemplateId);
						}else if(feedbackListitem.getValue().equals(-1L)) {
							formMapping.setFeedBackSmsTemplateId(null);
						}
						
					} catch (Exception e) {
						logger.error("Exception ::"+e);
					}
				}
			}else {
				formMapping.setCheckFeedbackFormSms(OCConstants.FLAG_NO);
				formMapping.setFeedBackSmsTemplateId(null);
			}
			
			if(issueRewardCheckBoxId.isChecked() && issueRewardTypeListId.isVisible()) {
				formMapping.setIssueRewardIschecked(OCConstants.FLAG_YES);
				formMapping.setIssueRewardType(issueRewardTypeListId.getSelectedItem().getValue());
				if(issueRewardTypeValueTextId.getValue()!=null && !issueRewardTypeValueTextId.getValue().isEmpty()) {
					Pattern p = Pattern.compile("\\d+");
					Matcher m = p.matcher(issueRewardTypeValueTextId.getValue());  
					boolean b = m.matches();  
					if(b) {
						formMapping.setIssueRewardValue(issueRewardTypeValueTextId.getValue());
					}else {
						MessageUtil.setMessage("Please enter valid "+issueRewardTypeListId.getSelectedItem().getValue(), "red", "top");
						issueRewardTypeValueTextId.setValue(Constants.STRING_NILL);
						logger.error("Exception : issue reward value is empty .");
						return;
					}
					
				}else {
					MessageUtil.setMessage("Please provide "+issueRewardTypeListId.getSelectedItem().getValue(), "red", "top");
					logger.error("Exception : issue reward value is empty .");
					return;	
				}
			}else {
				formMapping.setIssueRewardIschecked(OCConstants.FLAG_NO);
			}
			
		}
		else {
			formMapping.setConsentCustTemplateId(null);
			formMapping.setCheckParentalEmail(false);
			formMapping.setLoyaltyProgramId(null);
			formMapping.setLoyaltyCardsetId(null);

		}

		if(formMapping.isEnable()){
			if(formMapping.getActiveSince() == null) {
				formMapping.setActiveSince(Calendar.getInstance()); 
			}

		}

		redirectTo = redirectTo.length() > 0 ?redirectTo : null;
		formMapping.setHtmlRedirectURL(redirectTo); 

		failureRedirectTo = failureRedirectTo.length() > 0 ?failureRedirectTo : null;
		formMapping.setHtmlRedirectFailureURL(failureRedirectTo); 

		dbfailureRedirectTo = dbfailureRedirectTo.length() > 0 ?dbfailureRedirectTo : null;
		formMapping.setHtmlRedirectDbFailureURL(dbfailureRedirectTo); 

		redirectParentalTo = redirectParentalTo.length() > 0 ?redirectParentalTo : null;
		formMapping.setHtmlRedirectParentalURL(redirectParentalTo); 



		try {
			String saveMsg="";

			int confirm = Messagebox.show("Are you sure you want to save the form mappings?", "Prompt", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == Messagebox.OK) {

				try {
					//formMappingDaoForDML.saveOrUpdate(formMapping);
					FormMapping frm = (FormMapping)session.getAttribute("formMapping");

					if(frm == null) {
						saveMsg = "created"; 
						formMappingDaoForDML.saveOrUpdate(formMapping);
						logger.info("-------"+saveMsg);
					}else if(frm != null){

						saveMsg = "updated";
						formMappingDaoForDML.saveOrUpdate(formMapping);
						logger.info("-------"+saveMsg);

					}	
					MessageUtil.setMessage("Form mappings "+saveMsg +"  successfully.","color:green;");
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
			}
			if( confirm != Messagebox.OK) {
				return;
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		gotoStep(3);
		String actionUrl=""+ APP_URL +"JSBuilder.mqrm?hId="+ formMapping.getId() +
				"";
		ActionUrlTbId.setValue(actionUrl);

	}//onClick$redirectSubmitBtnId()

	
	
	
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
		
		tempItem = new Listitem("yyyy-MM-dd HH:mm:ss");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd/MM");
		tempItem.setParent(dateFormatListBx);
		
		dateFormatListBx.setMold("select");
		dateFormatListBx.setSelectedIndex(0);
		return dateFormatListBx;
	} //createDateFormatListbox()

	
	private void sendSettingsMail(String fmIds, String enteredURL) {
		try {
			EmailQueueDao emailQueueDao=(EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			EmailQueueDaoForDML emailQueueDaoForDML=(EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");

			String messageStr="<pre>&lt;!-- START Capture Code --&gt;\r\n"+
					"&lt;script src=\""+ APP_URL +"JSBuilder.mqrm?fmId="+fmIds.trim()+"\" type=\"text/javascript\" &gt;&lt;/script&gt;\r\n"+
					"&lt;!-- END  Capture Code --&gt;\r\n</pre>";

			String instructStr=" Paste the below code in the HTML("+ enteredURL +") page just before the closing </BODY> tag. </br></br>";

			if(fmIds.contains("_")) {

				instructStr=" Replace the below code with the existing one in HTML("+ enteredURL +"). ( Just before the closing </BODY> tag) </br></br>";
			}
			String branding = GetUser.getUserObj().getUserOrganization().getBranding();
			String brandStr="OptCulture";
			if(branding!=null && branding.equalsIgnoreCase("CAP")) {
				brandStr="Captiway";
			}

			String emailMsg="Hello "+currentUser.getUserName()+" ,<br/><br/> " + instructStr + messageStr + 
					"<br/>"+"Regards<br/>"+brandStr+" Team.";


			EmailQueue emailQueue = new EmailQueue(brandStr+" - Form Mapping Settings", emailMsg, 
					Constants.EQ_TYPE_FEEDBACK, "Active", currentUser.getEmailId(), MyCalendar.getNewCalendar(), currentUser);
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);

		} catch (Exception e) {
			logger.error("** Exception : error occcured while creating the configuration string ** ", e);
		}	

	}//sendSettingsMail()


	public void onClick$exitBtnId() {

		try {
			MessageUtil.clearMessage();
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.CONTACT_WEBFORM);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//

	public void onClick$configureBackmitBtnId() {

		try {
			MessageUtil.clearMessage();
			gotoStep(2);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}


	public void webformEditSettinngs(){
		MessageUtil.clearMessage();
		FormMapping formMapping = (FormMapping)session.getAttribute("formMapping");
		logger.info("form mapaing obj in edit is "+formMapping);


		if(formMapping != null) {

			formNameTbId.setValue(formMapping.getFormMappingName());
			formNameTbId.setDisabled(true);
			webformUrlTbId.setValue(formMapping.getURL());
			webformUrlTbId.setDisabled(true);
			urlAnchDivId.setVisible(true);
			if(formMapping.getHtmlRedirectURL() != null) {
				successTbId.setValue(formMapping.getHtmlRedirectURL());
			}else successTbId.setValue("");


			if(formMapping.getHtmlRedirectFailureURL() != null) {
				errorTbId.setValue(formMapping.getHtmlRedirectFailureURL());
			}else errorTbId.setValue("");

			if(formMapping.getHtmlRedirectDbFailureURL() != null) {
				existSubsciberTbId.setValue(formMapping.getHtmlRedirectDbFailureURL());
			}else existSubsciberTbId.setValue("");

			if(formMapping.getHtmlRedirectParentalURL() != null) {
				parenatConsentTbId.setValue(formMapping.getHtmlRedirectParentalURL());
			}else parenatConsentTbId.setValue("");


			simpleSignUpRadioId.setChecked(formMapping.getFormType().equals(Constants.WEBFORM_TYPE_SIGNUP)) ;
			ltyEnrollSignUpRadioId.setChecked(formMapping.getFormType().equals(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP)) ;
			ltyCardVerificationRadioId.setChecked(formMapping.getFormType().equals(Constants.WEBFORM_TYPE_LOYALTY_CARD));
			feedBackformRadioId.setChecked(formMapping.getFormType().equals(Constants.FEED_BACK_WEB_FORM));
			onCheck$formTypeRGId();

			if(simpleSignUpRadioId.isChecked()) {
				parentalConsentCbId.setChecked(formMapping.getEnableParentalConsent() == OCConstants.FLAG_YES ? true : false);
				parentalConsentChkBox.setChecked(formMapping.isCheckParentalEmail());

				onCheck$parentalConsentCbId();
				onCheck$parentalConsentChkBox();

				for (Listitem item : subsListLbId.getItems()) {

					if(formMapping.getListId() != null) {

						if(item.getValue() != null && ( (MailingList)item.getValue()).getListId().longValue()
								== formMapping.getListId().longValue())  {
							item.setSelected(true);
							break;
						}
					}//if
				}
				if(subsListLbId.getSelectedIndex() > 0) {
					editListAnchId.setVisible(true);
				}
				if(parentalConsentCbId.isChecked() && parentalConsentChkBox.isChecked()) {
					
					for (Listitem item : parentalConsentEmailLbId.getItems()) {
						
						if(formMapping.getConsentCustTemplateId() != null) {
							
							if(item.getValue() != null && ( (CustomTemplates)item.getValue()).getTemplateId().longValue()
									== formMapping.getConsentCustTemplateId().longValue())  {
								item.setSelected(true);
								onSelect$parentalConsentEmailLbId();
								break;
							}
						}//if
						else{
							
							parentalConsentEmailLbId.getItemAtIndex(0).setLabel(defaultStr);
							onSelect$parentalConsentEmailLbId();
						}
					}
				}
				sendSimpleSignMailOnsuccess.setChecked(formMapping.getCheckSimpleSignUpForEmail() == OCConstants.FLAG_YES ? true : false);
				sendSimpleSignSmsOnsuccess.setChecked(formMapping.getCheckSimpleSignUpFormSms() == OCConstants.FLAG_YES ? true : false);
				onCheck$sendSimpleSignMailOnsuccess();
				onCheck$sendSimpleSignSmsOnsuccess();
				
				
				if(sendSimpleSignMailOnsuccess.isChecked()) {
					
					for (Listitem item : SimpleSignEmailLbId.getItems()) {
						
						if(formMapping.getSimpleSignUpCustTemplateId() != null) {
							
							if(item.getValue() != null && ( (CustomTemplates)item.getValue()).getTemplateId().longValue()
									== formMapping.getSimpleSignUpCustTemplateId().longValue())  {
								item.setSelected(true);
								onSelect$SimpleSignEmailLbId();
								break;
							}
						}else{
							onSelect$SimpleSignEmailLbId();
						}
					}
				}
				if(sendSimpleSignSmsOnsuccess.isChecked()) {
					
					for (Listitem item : SimpleSignSmsLbId.getItems()) {
						
						if(formMapping.getSimpleSignUpSmsTemplateId() == null) {
							SimpleSignSmsLbId.setSelectedIndex(1);
							onSelect$SimpleSignSmsLbId();
							break;
						}
						
						if(formMapping.getSimpleSignUpSmsTemplateId() != null) {
							 if(item.getValue() != null && !item.getValue().equals(-1l)  && ((AutoSMS)item.getValue()).getAutoSmsId().longValue() == formMapping.getSimpleSignUpSmsTemplateId().longValue())  {
								item.setSelected(true);
								onSelect$SimpleSignSmsLbId();
								break;
							}
						}else{
							SimpleSignSmsLbId.getItemAtIndex(0).setLabel(defaultStr);
							onSelect$SimpleSignSmsLbId();
						}
					}
				}
				
				
				
				
			}
			else if(ltyEnrollSignUpRadioId.isChecked()){
				for (Listitem li : ltyProgramLbId.getItems()) {
					if(formMapping.getLoyaltyProgramId() != null) {
						if(li.getValue() != null && ((LoyaltyProgram)li.getValue()).getProgramId().longValue() 
								== formMapping.getLoyaltyProgramId().longValue())  {
							li.setSelected(true);
							break;
						}
					}
				}
				onSelect$ltyProgramLbId();
				if(formMapping.getAutoSelectCard() != OCConstants.FLAG_YES) {
					autoSelectCardRadioId.setChecked(false);
					pickCardSetRadioId.setChecked(true);
					pickFromCardSetLbId.setVisible(true);
					for (Listitem li : pickFromCardSetLbId.getItems()) {
						if(formMapping.getLoyaltyCardsetId() != null) {
							if(li.getValue() != null && ((LoyaltyCardSet)li.getValue()).getCardSetId().longValue() 
									== formMapping.getLoyaltyCardsetId().longValue())  {
								li.setSelected(true);
								break;
							}
						}
					}
				}
				else {
					autoSelectCardRadioId.setChecked(true);
					pickCardSetRadioId.setChecked(false);
					pickFromCardSetLbId.setVisible(false);
				}
				
				for (Listitem item : ltySubsListLbId.getItems()) {

					if(formMapping.getListId() != null) {

						if(item.getValue() != null && ( (MailingList)item.getValue()).getListId().longValue()
								== formMapping.getListId().longValue())  {
							item.setSelected(true);
							break;
						}
					}//if
				}
				
				if(ltySubsListLbId.getSelectedIndex() > 0) {
					ltyEditListAnchId.setVisible(true);
				}
				
				//allow optin check
				if(formMapping.getInputFieldMapping().contains("Loyalty_mobile_optin")){
					mobileOptinCbId.setChecked(true);
				}
				if(formMapping.getDoIssuePoints() == OCConstants.FLAG_YES) {
					 doIssuePointsId.setChecked(true);
				}else {
					 doIssuePointsId.setChecked(false);
				}
				
			}else if(feedBackformRadioId.isChecked()) {
				sendFeedBackMailOnsuccess.setChecked(formMapping.getCheckFeedbackFormEmail() == OCConstants.FLAG_YES ? true : false);
				sendFeedbackSmsOnsuccess.setChecked(formMapping.getCheckFeedbackFormSms() == OCConstants.FLAG_YES ? true : false);
				onCheck$sendFeedBackMailOnsuccess();
				onCheck$sendFeedbackSmsOnsuccess();
				
				for (Listitem item : subsListFeedBackId.getItems()) {

					if(formMapping.getListId() != null) {

						if(item.getValue() != null && ( (MailingList)item.getValue()).getListId().longValue()
								== formMapping.getListId().longValue())  {
							item.setSelected(true);
							break;
						}
					}//if
				}
				onSelect$subsListFeedBackId();
				if(sendFeedBackMailOnsuccess.isChecked() && sendFeedBackMailOnsuccess.isChecked()) {
					
					for (Listitem item : feedbackEmailLbId.getItems()) {
						
						if(formMapping.getFeedBackMailCustTemplateId() != null) {
							
							if(item.getValue() != null && ( (CustomTemplates)item.getValue()).getTemplateId().longValue()
									== formMapping.getFeedBackMailCustTemplateId().longValue())  {
								item.setSelected(true);
								onSelect$feedbackEmailLbId();
								break;
							}
						}else{
							onSelect$feedbackEmailLbId();
						}
					}
				}
				if(sendFeedbackSmsOnsuccess.isChecked() && sendFeedbackSmsOnsuccess.isChecked()) {
					
					for (Listitem item : feedbackSmsLbId.getItems()) {
						
						if(formMapping.getFeedBackSmsTemplateId() == null) {
							feedbackSmsLbId.setSelectedIndex(1);
							onSelect$feedbackSmsLbId();
							break;
						}
						
						if(formMapping.getFeedBackSmsTemplateId() != null) {
							 if(item.getValue() != null && !item.getValue().equals(-1l)  && ((AutoSMS)item.getValue()).getAutoSmsId().longValue() == formMapping.getFeedBackSmsTemplateId().longValue())  {
								item.setSelected(true);
								onSelect$feedbackSmsLbId();
								break;
							}
						}else{
							feedbackSmsLbId.getItemAtIndex(0).setLabel(defaultStr);
							onSelect$feedbackSmsLbId();
						}
					}
				}
				if(formMapping.getIssueRewardIschecked()!=' ' && formMapping.getIssueRewardIschecked()== OCConstants.FLAG_YES) {
					issueRewardCheckBoxId.setChecked(true);
				}else {
					issueRewardCheckBoxId.setChecked(false);
				}
				onCheck$issueRewardCheckBoxId();
				if(issueRewardCheckBoxId.isChecked()) {
					if(formMapping.getIssueRewardType().equalsIgnoreCase("Currency")) {
						issueRewardTypeListId.setSelectedIndex(1);
					}else if(formMapping.getIssueRewardType().equalsIgnoreCase("Points")) {
						issueRewardTypeListId.setSelectedIndex(2);
					}else {
						issueRewardTypeListId.setSelectedIndex(0);
					}
					issueRewardTypeValueTextId.setValue(formMapping.getIssueRewardValue().trim());
				}
			}
			setSelectionOfInputFields(formMapping.getInputFieldMapping());
		}
	}

	public void onClick$refreshBtnId(){

		FormMapping formMapping = (FormMapping)session.getAttribute("formMapping");
		inputSettings();
		if(formMapping != null)setSelectionOfInputFields(formMapping.getInputFieldMapping());

	}

	/*======================new=============================================*/


	//For New Screen
	/**
	 * //welcomeEmailChkDiv.setVisible(true); to be commented
	 * Changes in zul 
	 */
	private Radiogroup formTypeRGId,cardSelectionRgId;
	private Radio simpleSignUpRadioId,ltyEnrollSignUpRadioId,ltyCardVerificationRadioId,autoSelectCardRadioId,pickCardSetRadioId,feedBackformRadioId;
	private Div simpleSignUpDivId,ltySignUpDivId,feedBackFormDivId;
	/**
	 * This method is use to handle on check event of Form Type Radio Group
	 */
	public void onCheck$formTypeRGId(){
		logger.info("Radio Group formTypeRGId" );
		if(!(formTypeRGId.getSelectedIndex()== -1)){

			if(simpleSignUpRadioId.isChecked()){
				logger.info("simpleSignUpRadioId");
				ltySignUpDivId.setVisible(false);
				simpleSignUpDivId.setVisible(true);
				feedBackFormDivId.setVisible(false);
			}
			else if(ltyEnrollSignUpRadioId.isChecked()){
				logger.info("lty sign up");
				simpleSignUpDivId.setVisible(false);
				ltySignUpDivId.setVisible(true);
				feedBackFormDivId.setVisible(false);
//				populateLtyProgList();
//				populateSubscribersList(ltySubsListLbId);
			}
			else if(ltyCardVerificationRadioId.isChecked()){
				logger.info("lty card verification");
				simpleSignUpDivId.setVisible(false);
				ltySignUpDivId.setVisible(false);
				feedBackFormDivId.setVisible(false);
			}
			else if(feedBackformRadioId.isChecked()){
				logger.info("feed back form");
				simpleSignUpDivId.setVisible(false);
				ltySignUpDivId.setVisible(false);
				feedBackFormDivId.setVisible(true);
				//onCheck$sendFeedBackMailOnsuccess();
				//onCheck$sendFeedbackSmsOnsuccess();
				
			}
		}//if
	}//onSelect$formTypeRGId
	
	public void onSelect$ltyProgramLbId() {
		if(ltyProgramLbId.getSelectedIndex() > 0) {
			if(((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getMembershipType().contentEquals("Card")) {
				cardselectionDiv.setVisible(true);
				populateCardSetList((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue());
			}else {
				cardselectionDiv.setVisible(false);
			}
		}
	}
	
	private void populateLtyProgList() {
		//List<LoyaltyProgram> prgmList =  loyaltyProgramDao.getAllCardBasedProgramsListByUserId(currentUser.getUserId());
		List<LoyaltyProgram> prgmList = loyaltyProgramDao.getAllProgramsListByUserId(currentUser.getUserId());
		Listitem li = null;
		if(prgmList != null && prgmList.size() > 0) {
			
			for (LoyaltyProgram progObj : prgmList) {
				
				li =  new Listitem(progObj.getProgramName());
				li.setValue(progObj);
				li.setParent(ltyProgramLbId);
			}//for
		}//if
	}

	private void populateCardSetList(LoyaltyProgram progObj) {
		List<LoyaltyCardSet> cardSetList =  loyaltyCardSetDao.findByProgramId(progObj.getProgramId());
		
		Components.removeAllChildren(pickFromCardSetLbId);
		Listitem li = null;
		li = new Listitem("--Select Card-Set--");
		li.setParent(pickFromCardSetLbId);
		if(cardSetList != null && cardSetList.size() > 0) {

			for (LoyaltyCardSet cardSet : cardSetList) {

				li =  new Listitem(cardSet.getCardSetName());
				li.setValue(cardSet);
				li.setParent(pickFromCardSetLbId);
			}//for
		}//if
		pickFromCardSetLbId.setSelectedIndex(0);
	}
	/**
	 * This method is use to handle on check event of Card Selection Radio Group
	 */
	public void onCheck$cardSelectionRgId(){
		logger.info("Radio Group formTypeRGId" );
		if(!(cardSelectionRgId.getSelectedIndex()== -1)){

			if(autoSelectCardRadioId.isChecked()){
				logger.info("autoSelectCardRadioId");
				pickFromCardSetLbId.setVisible(false);
			}
			else if(pickCardSetRadioId.isChecked()){
				logger.info("pickCardSetRadioId");
				pickFromCardSetLbId.setVisible(true);


			}

		}//if


	}//onCheck$cardSelectionRgId
	
	public void availableCards() throws Exception{
		
		if(ltyEnrollSignUpRadioId.isChecked() && !((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getMembershipType().contentEquals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			
			if(autoSelectCardRadioId.isChecked()) {
		
		List<LoyaltyCardSet> activeCardSets = null;
		
		activeCardSets = loyaltyCardSetDao.findActiveByProgramId(((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());
		
		String cardSetIdStr = null;
		for(LoyaltyCardSet cardSet : activeCardSets){
			if(cardSetIdStr == null){
				cardSetIdStr = ""+cardSet.getCardSetId();
			}
			else{
				cardSetIdStr += ","+cardSet.getCardSetId();
			}
		}
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		Long totalInvCards = loyaltyCardsDao.getCardsCountByCardSetId(cardSetIdStr,true,((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());
		logger.info("prgmId in webformsController: getcardscount is  ::"+((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());

	try{
		
		if(totalInvCards > 0){
			
		int confirm = Messagebox.show(" "+ totalInvCards +" inventory cards available in virtual card-set(s).",
				"Information", Messagebox.OK , Messagebox.QUESTION);
	
			if(confirm == Messagebox.OK) {
				gotoStep(2);
			}
		} else {
			MessageUtil.setMessage("No inventory cards available in virtual card-set(s). ", "color:red");
			return;
		}
		
	}catch (Exception e) {
		
		logger.error("Exception::",e);
	}
	
		}else if(pickCardSetRadioId.isChecked()){
			
			LoyaltyCardSet cardSetObj = (LoyaltyCardSet) pickFromCardSetLbId.getSelectedItem().getValue();
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			Long totalInvCardsInCardSet = loyaltyCardsDao.getInvCardsCountByCardSet(cardSetObj.getCardSetId(),true,((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());
			logger.info("prgmId in webformsController is  ::"+((LoyaltyProgram) ltyProgramLbId.getSelectedItem().getValue()).getProgramId());

			try{
				
				if(totalInvCardsInCardSet > 0){
				
				int confirm = Messagebox.show(" "+ totalInvCardsInCardSet +" inventory cards available in "+cardSetObj.getCardSetName()+" card-set.",
						"Information", Messagebox.OK , Messagebox.QUESTION);
			
					if(confirm == Messagebox.OK) {
						gotoStep(2);
					}
				}
				else{
					MessageUtil.setMessage("No inventory cards available in this card-set: "+cardSetObj.getCardSetName()+" ", "color:red");
					return;
				}
				
			}catch(Exception e){
				logger.error("Exception::",e);
			}
			
		}
		
		}else{
			gotoStep(2);
		}
		
	}

	/*======================new=============================================*/
	
	public void onSelect$parentalConsentEmailLbId() {
		if(parentalConsentEmailLbId.getSelectedIndex() > 0) {
			previewImageId.setStyle("display:initial");
			parentalPreviewBtnId.setVisible(true);
			parentalEditMsgBtnId.setVisible(true);
		}else {
			if(parentalConsentEmailLbId.getSelectedItem().getLabel().equals(selectStr)){
				previewImageId.setStyle("display:none");
				parentalPreviewBtnId.setVisible(false);
				parentalEditMsgBtnId.setVisible(false);
			}else{
				previewImageId.setStyle("display:initial");
				parentalPreviewBtnId.setVisible(true);
				parentalEditMsgBtnId.setVisible(false);
			}
		}
	}
	
	public void onSelect$feedbackEmailLbId() {
		if(feedbackEmailLbId.getSelectedIndex() > 0) {
			previewFeedbackImageId.setStyle("display:initial");
			feedbackEditMsgBtnId.setVisible(true);
			feedbackPreviewBtnId.setVisible(true);
		}else {
			if(feedbackEmailLbId.getSelectedItem().getLabel().equals(selectStr)){
				previewFeedbackImageId.setStyle("display:none");
				feedbackEditMsgBtnId.setVisible(false);
				feedbackPreviewBtnId.setVisible(false);
			}else{
				previewFeedbackImageId.setStyle("display:initial");
				feedbackEditMsgBtnId.setVisible(true);
				feedbackPreviewBtnId.setVisible(false);
			}
		}
	}
	
	public void onSelect$SimpleSignEmailLbId() {
		if(SimpleSignEmailLbId.getSelectedIndex() > 0) {
			previewSimpleSignImageId.setStyle("display:initial");
			SimpleSignEditMsgBtnId.setVisible(true);
			SimpleSignPreviewBtnId.setVisible(true);
		}else {
			if(SimpleSignEmailLbId.getSelectedItem().getLabel().equals(selectStr)){
				previewSimpleSignImageId.setStyle("display:none");
				SimpleSignEditMsgBtnId.setVisible(false);
				SimpleSignPreviewBtnId.setVisible(false);
			}else{
				previewSimpleSignImageId.setStyle("display:initial");
				SimpleSignEditMsgBtnId.setVisible(true);
				SimpleSignPreviewBtnId.setVisible(false);
			}
		}
	}
	
	public void onSelect$feedbackSmsLbId() {
		if(feedbackSmsLbId.getSelectedIndex() > 0) {
			previewFeedbackSmsImageId.setStyle("display:initial");
			feedbackSmsEditMsgBtnId.setVisible(true);
			feedbackSmsPreviewBtnId.setVisible(true);
		}else {
			if(feedbackSmsLbId.getSelectedItem().getLabel().equals(selectStr)){
				previewFeedbackSmsImageId.setStyle("display:none");
				feedbackSmsEditMsgBtnId.setVisible(false);
				feedbackSmsPreviewBtnId.setVisible(false);
			}else{
				previewFeedbackSmsImageId.setStyle("display:initial");
				feedbackSmsEditMsgBtnId.setVisible(true);
				feedbackSmsPreviewBtnId.setVisible(false);
			}
		}
	}
	
	public void onSelect$SimpleSignSmsLbId() {
		if(SimpleSignSmsLbId.getSelectedIndex() > 0) {
			previewSimpleSignSmsImageId.setStyle("display:initial");
			SimpleSignSmsEditMsgBtnId.setVisible(true);
			SimpleSignSmsPreviewBtnId.setVisible(true);
		}else {
			if(SimpleSignSmsLbId.getSelectedItem().getLabel().equals(selectStr)){
				previewSimpleSignSmsImageId.setStyle("display:none");
				SimpleSignSmsEditMsgBtnId.setVisible(false);
				SimpleSignSmsPreviewBtnId.setVisible(false);
			}else{
				previewSimpleSignSmsImageId.setStyle("display:initial");
				SimpleSignSmsEditMsgBtnId.setVisible(true);
				SimpleSignSmsPreviewBtnId.setVisible(false);
			}
		}
	}
	
	
	
	
	public  void getFeedBackSmsTemplateList() {

		List<AutoSMS> smsTemplateList = null;
		try {
			smsTemplateList = autoSMSDao.getTemplatesByStatus(currentUser.getUserId(),"feedbackform");
			Components.removeAllChildren(feedbackSmsLbId);

			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(feedbackSmsLbId);
			
			item = new Listitem(defaultStr, new Long(-1));
			item.setParent(feedbackSmsLbId);

			if(smsTemplateList != null && smsTemplateList.size() >0) {

				for (AutoSMS autoSms : smsTemplateList) {

					item = new Listitem(autoSms.getTemplateName(), autoSms);
					item.setParent(feedbackSmsLbId);

				}
			}
			if(feedbackSmsLbId.getItemCount() > 0 ) feedbackSmsLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");

		}
	}
	
	public  void getWelcomeSmsTemplateList() {

		List<AutoSMS> welcomeSmsTemplateList = null;
		try {
			welcomeSmsTemplateList = autoSMSDao.getTemplatesByStatus(currentUser.getUserId(),"welcomeSMS");
			Components.removeAllChildren(SimpleSignSmsLbId);
			Listitem item = null;
			item = new Listitem(selectStr, null);
			item.setParent(SimpleSignSmsLbId);
			
			item = new Listitem(defaultStr, new Long(-1));
			item.setParent(SimpleSignSmsLbId);

			if(welcomeSmsTemplateList != null && welcomeSmsTemplateList.size() >0) {

				for (AutoSMS autoSms : welcomeSmsTemplateList) {

					item = new Listitem(autoSms.getTemplateName(), autoSms);
					item.setParent(SimpleSignSmsLbId);

				}
			}
			if(SimpleSignSmsLbId.getItemCount() > 0 ) SimpleSignSmsLbId.setSelectedIndex(0);

		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");

		}
	}
	
	public void onClick$feedbackSmsPreviewBtnId() {
		logger.debug("---Entered onclick$feedbackSmsPreviewBtnId---");
			previewSMSTemplate(feedbackSmsLbId,OCConstants.FEEDBACK_WEBFORM);
		logger.debug("---Entered onclick$feedbackSmsPreviewBtnId---");
	}
	
	public void onClick$SimpleSignSmsPreviewBtnId() {
		logger.debug("---Entered onclick$SimpleSignSmsPreviewBtnId---");
			previewSMSTemplate(SimpleSignSmsLbId,OCConstants.WELCOMESMS);
		logger.debug("---Entered onclick$SimpleSignSmsPreviewBtnId---");
	}
	
	
	private void previewSMSTemplate(Listbox lbId,String defaultMsgType) {
		logger.info("---Entered previewSMSTemplate-----");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Message")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			String templateContent = "";
			AutoSMS autoSMS = null;
			if(!lbId.getSelectedItem().getValue().equals(-1L)) {
				autoSMS = autoSMSDao.getAutoSmsTemplateById(((AutoSMS)lbId.getSelectedItem().getValue()).getAutoSmsId().longValue());
			}
			if(autoSMS == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB(defaultMsgType);
			}else {
				templateContent = autoSMS.getMessageContent();
			}
			logger.info("templateContent 1----"+templateContent);
			Utility.showPreview(previewWin$html, currentUser.getUserName(), templateContent);
			previewWin.setVisible(true);
		}
		logger.debug("--Exit previewSMSTemplate---");
	}
	
	public void onClick$feedbackSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$feedbackSmsEditMsgBtnId---");
			editSMSTemplate(feedbackSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_WEB_FORMS);
		logger.debug("---Exit onClick$feedbackSmsEditMsgBtnId---");
	}
	
	public void onClick$SimpleSignSmsEditMsgBtnId() {
		logger.debug("---Entered onClick$SimpleSignSmsEditMsgBtnId---");
			editSMSTemplate(SimpleSignSmsLbId,OCConstants.AUTO_SMS_TEMPLATE_TYPE_WELCOME_SMS);
		logger.debug("---Exit onClick$SimpleSignSmsEditMsgBtnId---");
	}
	
	
	public void editSMSTemplate(Listbox lbId,String tempType) {
		logger.info("---Entered editSMSTemplate-----");
		if(lbId.getSelectedItem().getLabel().equalsIgnoreCase("Select Message")) {
			MessageUtil.setMessage("Please select atleast one template", "color:red", "TOP");
		}
		else {
			logger.info("in edit mode--------------------");
			logger.info("Sms template value "+lbId.getSelectedItem().getValue());
			AutoSMS autoSMS = null;
			if(!lbId.getSelectedItem().getValue().equals(-1L)) {
				autoSMS = autoSMSDao.getAutoSmsTemplateById(((AutoSMS)lbId.getSelectedItem().getValue()).getAutoSmsId().longValue());
			}
			session.setAttribute("editSmsTemplate", autoSMS);
			session.setAttribute("SmsMode", "edit");
			session.setAttribute("typeOfSms",tempType);
			session.setAttribute("fromAddNewBtn","loyalty/loyaltyAutoCommunication");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_SMS);
		}
		logger.debug("--Exit editSMSTemplate---");
	}
	
	public void onSelect$subsListFeedBackId() {
		if(subsListFeedBackId.getItems().size() > 1 && subsListFeedBackId.getSelectedIndex() > 0) {
			editListFeedBackId.setVisible(true);
		}else {
			editListFeedBackId.setVisible(false);
		}
	}
	
	public void onCheck$issueRewardCheckBoxId() {
		if(issueRewardCheckBoxId.isChecked()) {
			issueRewardTypeListId.setVisible(true);
			issueRewardTypeValueTextId.setVisible(true);
		}else {
			issueRewardTypeListId.setVisible(false);
			issueRewardTypeValueTextId.setVisible(false);
		}
	}
	
}//EOF
