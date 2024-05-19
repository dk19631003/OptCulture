package org.mq.marketer.campaign.controller.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;

public class SmsCampSetupController extends GenericForwardComposer  implements EventListener{

	private MailingListDao mailingListDao;
	private CouponsDao couponsDao;
	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	private OrgSMSkeywordsDaoForDML orgSMSkeywordsDaoForDML;
	private UrlShortCodeMappingDao urlShortCodeMappingDao; 
	private UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML; 
	private SMSSettingsDao smsSettingsDao;
	private List<SMSSettings> smsSettings;
	private Session sessionScope;
	private Radio entireListRId;
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public static final Object TB_ACTION_RESEND = null;
	//String senderId = "";
	private Users currUser;
	private Listbox insertMergetagsLbId, insertCouponLbId,keywordlbId,keywordPageSizeLbId,insertBarcodeLbId,createTransTempWinId$insertBarcodeLbId_createNewTx;
	private Textbox insertKeywordTbId;
	//private Label availLblId;
	private Combobox receivingNumCbId;
	private Label keywordAvailabilityStatusLblId, headerLblId;
	private Session session;
	Desktop desktopScope = null;
	private Button saveNewKeywordBtnId, addNewKeywordBtnId;
	//final String editButton =  "Update Keyword";
	private Paging keywordListPaging;
	
	private Div addNewKeyordDivId;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private CountryReceivingNumbersDao countryReceivingNumbersDao;
	
	private MyDatebox fromDateboxId,toDateboxId;
	private Textbox senderIdTbId, shortCodeTbId, SMSMsgKeywordTbId, charCountTbId, caretPosTB, linkURLTxtBoxId;
	private Textbox createTransTempWinId$transTempNameTxtBxId,createTransTempWinId$transTempContentTxtBxId,createTransTempWinId$transCaretPosTB,
	createTransTempWinId$transTempCharCountTxtBxId;
	public SmsCampSetupController() {
		
		session = Sessions.getCurrent();
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		orgSMSkeywordsDao = (OrgSMSkeywordsDao)SpringUtil.getBean("orgSMSkeywordsDao");
		orgSMSkeywordsDaoForDML = (OrgSMSkeywordsDaoForDML)SpringUtil.getBean("orgSMSkeywordsDaoForDML");
		urlShortCodeMappingDao = (UrlShortCodeMappingDao)SpringUtil.getBean("urlShortCodeMappingDao");
		urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML)SpringUtil.getBean("urlShortCodeMappingDaoForDML");
		countryReceivingNumbersDao = (CountryReceivingNumbersDao ) SpringUtil.getBean("countryReceivingNumbersDao");
		
		desktopScope = Executions.getCurrent().getDesktop();
		currUser = GetUser.getUserObj();
		//senderId = PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID);
		smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		this.userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		smsSettings = smsSettingsDao.findByUser(currUser.getUserId());
		sessionScope = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Keywords", "", style, true);
	}
	
	
	
	private final String DEFAULT_AUTO_RESPONSE = "Enter your Auto-response message for this keyword"; 
	private final String AVAILABLE = "Available";
	private final String EXIST = "Already Exists";
	private final String ADD_NEW = "Add New Keyword";
	private final String ATTRIBUTENAME = "editObject";
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		/*senderIdTbId.setValue(senderId);
		senderIdTbId.setDisabled(true);
		shortCodeTbId.setValue(senderId);
		shortCodeTbId.setDisabled(true);*/
		keywordListPaging.setDetailed(true);
		
		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		
		// Set Editor PH values.
		Set<MailingList> set = new HashSet<MailingList>();
		set.addAll(mailingListDao.findByIds(listIdsSet));
		
		getPlaceHolderList(set);
		
		
		getCouponsIfAny();
		//getKeywordsIfAny();
		
		//insertKeywordTbId.setSelectedIndex(0);
		
	//	SMSMsgKeywordTbId.setCtrlKeys("^v");
	//	SMSMsgKeywordTbId.addEventListener(Events.ON_CTRL_KEY, this);
	//	SMSMsgKeywordTbId.addEventListener(Events.ON_RIGHT_CLICK, this);
		int totalSize = orgSMSkeywordsDao.getUserOrgSMSKeyWordsCount(currUser.getUserOrganization().getUserOrgId());
		logger.debug("total size is "+totalSize);
		keywordListPaging.setTotalSize(totalSize);
			
		redraw(0,(byte)10);
		fromDateboxId.setValue(Calendar.getInstance());
		Calendar toDate = Calendar.getInstance();
		toDate.add(Calendar.DAY_OF_MONTH, 180);
		toDateboxId.setValue(toDate);
		saveNewKeywordBtnId.setAttribute("editButtonMode","Save");
		setReceivingNumbers();
		keywordListPaging.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
		});
		
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
			
			if(event.getTarget() instanceof Image){
				
				Image img = (Image) event.getTarget();
				String imgAttr = (String)img.getAttribute("addEvent");
				
				if(imgAttr.equalsIgnoreCase("Edit")){
					
					keywordAvailabilityStatusLblId.setVisible(false);
					Listitem editItem = (Listitem)img.getParent().getParent().getParent();
					OrgSMSkeywords editItemObj = (OrgSMSkeywords)editItem.getValue();
					//saveNewKeywordBtnId.setAttribute("editButtonMode",editButton);
					addNewKeywordBtnId.setAttribute(ATTRIBUTENAME, editItemObj);
					addNewKeyordDivId.setVisible(true);
					
					insertKeywordTbId.setText(editItemObj.getKeyword());
					//insertKeywordTbId.setDisabled(saveNewKeywordBtnId.getAttribute("editButtonMode").toString().equalsIgnoreCase(editButton));
					insertKeywordTbId.setDisabled(true);
					
					String shortCode = editItemObj.getShortCode();
					
					/*if(SMSStatusCodes.setCountryCode.get(currUser.getCountryType())) {
						if(!(shortCode.startsWith(currUser.getCountryCarrier().toString())) && shortCode.length() == 10) shortCode = "+" + currUser.getCountryCarrier() + shortCode;
						else shortCode = "+" + shortCode;
					}*/
					List<Component> receivingNums = receivingNumCbId.getChildren();
					
					for(Component receivingNum : receivingNums) {
						if(((Comboitem) receivingNum).getValue().toString().equalsIgnoreCase(shortCode)) {
							receivingNumCbId.setSelectedItem(((Comboitem) receivingNum));
						}
						
					}
					//receivingNumCbId.setValue(shortCode);
					TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
					
					fromDateboxId.setValue(new MyCalendar(editItemObj.getStartFrom(), clientTimeZone));
					toDateboxId.setValue(new MyCalendar(editItemObj.getValidUpto(), clientTimeZone));
					saveNewKeywordBtnId.setLabel(saveNewKeywordBtnId.getAttribute("editButtonMode").toString());
					
					String autoResponse = editItemObj.getAutoResponse();
					SMSMsgKeywordTbId.setText(autoResponse != null ? autoResponse : DEFAULT_AUTO_RESPONSE);
					if(autoResponse != null) getCharCount(autoResponse);
				}
				
				else if(imgAttr.equalsIgnoreCase("Delete")){
					
					Listitem delItem = (Listitem)img.getParent().getParent().getParent();
					//keywordlbId = (Listbox)delItem.getParent();
					int index = delItem.getIndex();
					
					//Listitem delItem = keywordlbId.getSelectedItem();
					
					
					//if(delItem!=null) {
							
							try {
								int confirm = Messagebox.show("Are you sure you want to delete the selected keyword?","Delete Keyword?",
										Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != 1) {
									
									return ;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception :::",e);
							}
							
							//orgSMSkeywordsDao.deleteById(((OrgSMSkeywords)delItem.getValue()).getKeywordId());
							orgSMSkeywordsDaoForDML.deleteById(((OrgSMSkeywords)delItem.getValue()).getKeywordId());
							
						//}
						
						keywordlbId.removeItemAt(index);
						keywordListPaging.setTotalSize(keywordListPaging.getTotalSize()-1);
						keywordListPaging.setActivePage(0);
						int pNo = Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel());
						keywordListPaging.setPageSize(pNo);
						redraw(0,(byte)pNo);
						addNewKeyordDivId.setVisible(false);

				}
				// to do here for delete and edit images onclick
				
				else if(imgAttr.equalsIgnoreCase("Report")) {
					
					try {
						Listitem editItem = (Listitem)img.getParent().getParent().getParent();
						OrgSMSkeywords reportObj = (OrgSMSkeywords)editItem.getValue();
						PageUtil.clearHeader();
						MessageUtil.clearMessage();
						//Long userId = GetUser.getUserId();
						/*long reportCount = orgSMSkeywordsDao.getReportCountByCampaign(reportObj.getKeyword(),reportObj.getShortCode());
						if(reportCount < 1){
							MessageUtil.setMessage("No records exist for the keyword : "+reportObj.getKeyword(),"color:red", "TOP");
						}else{*/
							/*session.setAttribute("keywordReport",reportObj.getKeyword());
							session.setAttribute("keywordReportReceiveingNumber",reportObj.getShortCode());
							*/
						session.setAttribute("keywordReport",reportObj);
						session.setAttribute("fromPage","sms/smsCampSetup");
						
							/*desktopScope.setAttribute("keywordReport","STOP");
							desktopScope.setAttribute("keywordReportReceiveingNumber","888555");*/
							//desktopScope.setAttribute("fromKeyword",true);
							Redirect.goTo(PageListEnum.REPORT_SMS_KEYWORD_RESPONSE_REPORTS);
					//	}
					} catch (Exception e) {
					    logger.error("** Exception **" + e);
					}
					
				}//else if
		 }
		
		
	}
	
	public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {
		
		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			List<String> placeHoldersList = new ArrayList<String>(); 
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			
			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			
			
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));			
			}
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			
			Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);
			
			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			
				
				if(contactsUDFList != null) {
					
					//2.prepare merge tag and add to placeHoldersList
					//format : display lable :: |^GEN_<UDF>^|
					for (POSMapping posMapping : contactsUDFList) {
						
						String udfStr;
						if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {
						
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
										Constants.DELIMETER_DOUBLECOLON +
										Constants.CF_START_TAG + Constants.UDF_TOKEN +
										posMapping.getCustomFieldName()  +Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN+ Constants.CF_END_TAG ;
						
						
						}
						else {
							 udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
					
						
						}
						placeHoldersList.add(udfStr);
					}//for
					
					
					
				}//if
			//END
			
			
			
			for (MailingList mailingList : mlistSet) {
				if(!mailingList.isCustField())  continue;
				
				List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
				String custField ;
				for (MLCustomFields customField : mlcust) {
					custField = Constants.CF_TOKEN + customField.getCustFieldName() 
								+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
								Constants.CF_TOKEN + 
								customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;
					
					if(placeHoldersList.contains(custField)) continue;
					placeHoldersList.add(custField);
				}
				
			} // for
			
			Listitem item = null;
			for (String placeHolder : placeHoldersList) {
				
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				
				if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
						placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
						placeHolder.startsWith("Forward To Friend")){
					continue;
				}
				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
				logger.debug("key ::"+key+" value ::"+value);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
					logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
				}
				
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.insert(value.lastIndexOf("^"), posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
				}
				
				item =  new Listitem(key,value.toString());
				item.setParent(insertMergetagsLbId);
				
			} // for
			
			
		/*	// Populate js variable 'phArr' with the place holders for all Editors
			
			Clients.evalJavaScript("var phArr = [];");
			int jsInd=0;
			for (String placeHolder : placeHoldersList) {
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("phArr["+ (jsInd++) +"]=\""+placeHolder+"\";");
			} // for
			*/
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		}
	}
	
	public void  getCouponsIfAny() {
	
	List<Coupons> couponsCampList = couponsDao.findCouponsByOrgId(currUser.getUserOrganization().getUserOrgId());
//	List<Coupons> couponsCampList = couponsDao.findCouponsByUserId(currUser.getUserId());
	if(couponsCampList == null || couponsCampList.size() == 0) {
		
		logger.debug("got no coupons for this org");
		return;
	}
	
	Listitem item  = null;
	
	for (Coupons coupons : couponsCampList) {
		
		item = new Listitem(coupons.getCouponName(), coupons);
		item.setParent(insertCouponLbId);
		
	}
	
	for(Coupons coupon :couponsCampList){
		
		item = new Listitem(coupon.getCouponName(), coupon);
		Listitem itemBarcode = new Listitem(coupon.getCouponName(), coupon);
		if(coupon.getEnableBarcode()){
			item.setParent(insertBarcodeLbId);
			itemBarcode.setParent(createTransTempWinId$insertBarcodeLbId_createNewTx);
		}
	}
	
}//getCouponsIfAny

/*public void  getKeywordsIfAny() {
	
	
	
	List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());
	if(keywordsList == null || keywordsList.size() == 0) {
		
		logger.debug("got no keywords for this org");
		return;
	}
	
	Comboitem item  = null;
	
	for (OrgSMSkeywords keyword : keywordsList) {
		
		item = new Comboitem(keyword.getKeyword());
		item.setValue(keyword);
		item.setParent(insertKeywordTbId);
		
		
	}
	insertKeywordTbId.setSelectedItem(insertKeywordTbId.getItemAtIndex(0));
	
}//getCouponsIfAny
*/	
	
/*public void onSelect$insertKeywordTbId() {
	
	int index = insertKeywordTbId.getSelectedIndex();
	
	
	
	if(index == 0) {
		return;
		
	}
	
	availLblId.setValue("");
	String selKeyword = insertKeywordTbId.getValue().trim();
	
	
	if(selKeyword.length() > 0 ) {
		
		if(selKeyword.equalsIgnoreCase(ADD_NEW.trim())) {
		
			SMSMsgKeywordTbId.setValue(DEFAULT_AUTO_RESPONSE);
			charCountTbId.setValue("0");
			return;
			
		}
	
	}
	
	
	Comboitem item = findOptionalComboitem(insertKeywordTbId, selKeyword);
	
	if(item != null) {
		
		OrgSMSkeywords keyWord = item.getValue();
		if(keyWord == null) {
			
			if(newKeywrds.containsKey(selKeyword)) {
				
				SMSMsgKeywordTbId.setValue(newKeywrds.get(selKeyword) == null ||
						 newKeywrds.get(selKeyword).trim().length() == 0  ? DEFAULT_AUTO_RESPONSE : newKeywrds.get(selKeyword) );
				
				getCharCount(newKeywrds.get(selKeyword));
			}
			
			
		}
		else{
			
			String keyWordAutoResponse = keyWord.getAutoResponse();
			
			if(keyWordAutoResponse == null || keyWordAutoResponse.length() == 0) {
				
				
				SMSMsgKeywordTbId.setValue(DEFAULT_AUTO_RESPONSE);
				charCountTbId.setValue("0");
				
			}
			else {
				
				SMSMsgKeywordTbId.setValue(keyWordAutoResponse);
				getCharCount(keyWordAutoResponse);
				
			}
			
			
		}//if keyword
		
	}else{
		
		
		
		
		
	}
	
	
	OrgSMSkeywords keyWord = (OrgSMSkeywords)insertKeywordTbId.getSelectedItem().getValue();
	String keyWordAutoResponse = keyWord.getAutoResponse();
	
	if(keyWordAutoResponse == null || keyWordAutoResponse.length() == 0) {
		
		
		SMSMsgKeywordTbId.setValue(DEFAULT_AUTO_RESPONSE);
		
		
	}
	else {
		
		SMSMsgKeywordTbId.setValue(keyWordAutoResponse);
		getCharCount(keyWordAutoResponse);
		
	}
	
	
}//onSelect$insertKeywordTbId()
*/
	
public void onSelect$insertBarcodeLbId(){
		
		if(insertBarcodeLbId.getSelectedIndex() <= 0){
			return;
		}
		
		Coupons selCoupon = insertBarcodeLbId.getSelectedItem().getValue();
		
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
		
		String replaceStr = appShortUrl+Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		
		insertText(replaceStr);
		
		insertBarcodeLbId.setSelectedIndex(0);
	}
	public void onSelect$insertBarcodeLbId_createNewTx$createTransTempWinId(){
		if(createTransTempWinId$insertBarcodeLbId_createNewTx.getSelectedIndex() <= 0){
			return;
		}
		Coupons selCoupon = createTransTempWinId$insertBarcodeLbId_createNewTx.getSelectedItem().getValue();
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
		String replaceStr = appShortUrl+Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		insertTextForAddTemplatePopup(replaceStr);
		createTransTempWinId$insertBarcodeLbId_createNewTx.setSelectedIndex(0);
	}
	
	public void insertTextForAddTemplatePopup(String  value) {
		logger.info("insertText");
		String cp = createTransTempWinId$transCaretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
		cp = "0";
		}
		try{
		int caretPos = Integer.parseInt(cp);
		if (caretPos != -1) {
		String currentValue = createTransTempWinId$transTempContentTxtBxId.getValue();
		int charCount = currentValue.length();
		String msg=addingHeaderFoorter(currentValue, charCount);
		String newValue = "";
		if(currentValue != null && currentValue.length() >0 ){
			newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
		}else newValue = value;
			int ccount = value.length()+msg.length();
		
		createTransTempWinId$transTempContentTxtBxId.setValue(newValue);
		if(ccount>160) {
			int msgcount = ccount/160;
			createTransTempWinId$transTempCharCountTxtBxId.setValue(""+ccount+" / "+(msgcount+1));
		}else {
			createTransTempWinId$transTempCharCountTxtBxId.setValue(""+ccount+" / "+1);
		}
			createTransTempWinId$transTempContentTxtBxId.focus();
		}
		}catch(Exception e) {
		logger.error("Exception ::" , e);
		}
	}
	
	public String addingHeaderFoorter(String msgContent,int charCount){
        if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType()) && smsSettings != null) {
            
            logger.info("msgContent.length()"+msgContent.length());
            SMSSettings optinSettings = null;
              SMSSettings optOutSettings = null;
              SMSSettings helpSettings = null;
              
              String messageHeader = Constants.STRING_NILL;
              for (SMSSettings eachSMSSetting : smsSettings) {
                  
                  if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
                  else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
                  else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
                  
              }
            if(optinSettings != null && messageHeader.isEmpty()){
                messageHeader = optinSettings.getMessageHeader();
                msgContent = messageHeader + "\n" + msgContent;
            }
            else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
            else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
            
            //charCount = charCount + (messageHeader != null ? messageHeader.length()+1 : 0);//TODO set perfection
            
            //headerLblId.setValue(messageHeader);
            //String optoutKeyWord = smsSettings.getOptoutKeyword();
            if(entireListRId.isChecked() && optOutSettings != null) {
                //charCount = charCount + 1+(optOutLblId.getValue().length());//("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
                msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
                        ("\n"+"Reply " + optOutSettings.getKeyword() + " 2 Optout")
                                : "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));

            }
            //charCount = msgContent.length();
            
            if(smsSettings==null && entireListRId.isChecked()) {
                msgContent = msgContent+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent");

            }
            charCount = msgContent.length();
            logger.info("charCount-----***"+charCount);
        }
        return msgContent;
    }

public void onSelect$insertCouponLbId() {
	
	
	if(insertCouponLbId.getSelectedIndex() <= 0) {
		
		logger.debug("selected 0");
		return ;
		
	}
	
	Coupons selCoupon = insertCouponLbId.getSelectedItem().getValue();
	
	//changes start 2.5.3.0
	if(selCoupon.getStatus()!=null&&(selCoupon.getStatus().equals("Paused")||selCoupon.getStatus().equals("Expired"))){
		MessageUtil.setMessage("Discount code "+'"'+selCoupon.getCouponName()+'"'+" has "+selCoupon.getStatus().toLowerCase()+". Please activate it to use it in the campaign.", "color:red;");
		insertKeywordTbId.setFocus(true);
		insertCouponLbId.setSelectedIndex(0);
		return;
			}
	
		
	//changes end 2.5.3.0
		
	String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
						+"_"+selCoupon.getCouponName()+Constants.CF_END_TAG;
	
	insertText(replaceStr);
	
	insertCouponLbId.setSelectedIndex(0);
	
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

public void onSelect$insertMergetagsLbId() {
	
	if(insertMergetagsLbId.getSelectedIndex() == 0) {
		
		return;
	}
	
	
	insertText((String)insertMergetagsLbId.getSelectedItem().getValue());
	
	
}









//Sorting Hard Coded

	


public String orderby_colName="createdDate",desc_Asc="desc"; 

public void desc2ascasc2desc()
{
	if(desc_Asc=="desc")
		desc_Asc="asc";
	else
		desc_Asc="desc";

}


public void desecnding_Order_DateTpe(){
  	 firstclickonDeliveredTime=true;
  	firstclickonDeliveredTime1=true;
   }

public void onClick$sortbyKeyword() {
	orderby_colName = "keyword";
	desc2ascasc2desc();
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	desecnding_Order_DateTpe();
	
}
public void onClick$sortbyCreatedDate() {
	orderby_colName = "createdDate";
	desc2ascasc2desc();
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	desecnding_Order_DateTpe();
}public void onClick$sortbyReceivingNumber() {
	orderby_colName = "shortCode";
	desc2ascasc2desc();
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	desecnding_Order_DateTpe();
}
public void onClick$sortbyAutoResponse() {
	orderby_colName = "autoResponse";
	desc2ascasc2desc();
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	desecnding_Order_DateTpe();
}
public boolean firstclickonDeliveredTime=true;
public void onClick$sortbyValidFrom() {
	orderby_colName = "startFrom";
	if(firstclickonDeliveredTime)
	{
		desc_Asc="asc";
		firstclickonDeliveredTime=false;
	}
	desc2ascasc2desc();
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	firstclickonDeliveredTime1=true;
}
public boolean firstclickonDeliveredTime1=true;
public void onClick$sortbyValidTo() {
	orderby_colName = "validUpto";
	if(firstclickonDeliveredTime1)
	{
		desc_Asc="asc";
		firstclickonDeliveredTime1=false;
	}
	desc2ascasc2desc();
	
	redraw(0,(byte)Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel()));
	firstclickonDeliveredTime=true;
}















public void onClick$addNewCouponTBId() {
	
	//Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
	Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONS);
	
	
}



public void onClick$insertLinkBtnId() {
	
	logger.debug("---URL need to be Short Coded---");
	
	String enteredURL = linkURLTxtBoxId.getValue().trim();
	
	if(enteredURL.equals("") || enteredURL.equalsIgnoreCase("http://") ||
			(enteredURL.equals("Use Url Shortener".trim())) ) {
		MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
		logger.error("Exception : Link URl field is empty .");
		return;
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
				logger.error("given Short code is already exists in DB.....",e);
				continue;
				
			}catch (ConstraintViolationException e) {
				// TODO: handle exception
				logger.error("given Short code is already exists in DB.....",e);
				continue;
			}
			
			
			
		}//for
		
	}//if
	
	//should insert the link short code in the message content
	
	
	insertText(insetedUrl);
	linkURLTxtBoxId.setValue("Use Url Shortener");
	
}//onClick$insertLinkBtnId$insertLinkWinId


/*public void onClick$insertKeywordTbId() {
	
	if(insertKeywordTbId.getValue().trim().equalsIgnoreCase(ADD_NEW)){
		
		insertKeywordTbId.setValue("");
	}
	
	
}//onClick$insertKeywordTbId
*/
public void onClick$linkURLTxtBoxId() {
	
	if(linkURLTxtBoxId.getValue().trim().equals("Use Url Shortener")) {
		
		linkURLTxtBoxId.setValue("");
		
	}
	
}

public void insertText(String  value){
	logger.info("insertText");
	//String value = item.getValue();
	
	String cp = caretPosTB.getValue();
	if (cp == null || cp.length() == 0) {
	cp = "0";
	}
	try{
	int caretPos = Integer.parseInt(cp);
	if (caretPos != -1) {
	String currentValue = SMSMsgKeywordTbId.getValue();
	
	String newValue = "";
	if(currentValue.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE))  newValue = value;
	else  newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
	
	SMSMsgKeywordTbId.setValue(newValue);
	
	int charCount = SMSMsgKeywordTbId.getValue().length();
	//logger.info("the length is====>"+charCount);
	if(charCount>160) {
	int msgcount = charCount/160;
	charCountTbId.setValue(""+charCount+" / "+(msgcount+1));
	
	}//if
	else {
	charCountTbId.setValue(""+charCount+" / "+1);
	}//else
	
	//sessionScope.put("messageContent", SMSMsgTbId.getValue());
	//personalizationTagsWinId.setVisible(false);
	SMSMsgKeywordTbId.focus();
	
	}
	}catch(Exception e) {
	logger.error("Exception :::",e);
	}
	}

/**
 * gives the char count of SMS
 * @param event
 */
public void onChanging$SMSMsgKeywordTbId(InputEvent event){
	
	logger.debug("On changing calling");
	try{
		getCharCount( event.getValue());
		
		
	}catch (Exception e) {
		logger.debug("Exception **",e);
	}
}//onChanging$SMSMsgTbId(-)

public void onFocus$SMSMsgKeywordTbId(){
	
	String defaultText = SMSMsgKeywordTbId.getText();
	
	logger.debug("defaULT TExT is "+defaultText);
	
	if(!defaultText.isEmpty() && defaultText.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE)) {
		SMSMsgKeywordTbId.setText("");
		getCharCount(SMSMsgKeywordTbId.getText());
	}
}

public void onBlur$SMSMsgKeywordTbId(){
	
	String defaultText = SMSMsgKeywordTbId.getText();
	if(defaultText.isEmpty() || defaultText.length() == 0) {
		SMSMsgKeywordTbId.setText(DEFAULT_AUTO_RESPONSE);
		getCharCount(SMSMsgKeywordTbId.getText());
	}
}//


/**
 * caluculates the actual character count of 
 * the SMS campaign and sets this value to the SMS msg related  textbox
 * @param msgContent specifies the actual msg content.
 */
public void getCharCount(String msgContent) {
	try {
		
		
		
		
		headerLblId.setValue("");
		headerLblId.setVisible(false);
		
		int charCount = msgContent != null && !(msgContent.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE)) ? msgContent.length() : 0;
		logger.info("char count the length is====>"+charCount);
		if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())) {
			if(smsSettings != null) {
				
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
				else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				
				headerLblId.setValue(messageHeader);
				headerLblId.setVisible(true);
						
				
				charCount = charCount + (messageHeader != null ? messageHeader.length() : 0);//TODO set perfection
				logger.info("char count the length after header is====>"+charCount);
			}
		}
		if(charCount>160) {
			//warnLblId.setVisible(true);
			int msgcount = charCount/160;
			charCountTbId.setValue(""+charCount+"/"+(msgcount+1));
			/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
					substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
		}//if
		else {
			//warnLblId.setVisible(false);
			charCountTbId.setValue("" +charCount+ " / " +1);
		}//else
	
		
	} catch (Exception e) {
			logger.debug("Exception while getting the character count",e);
	}//catch
	
}//getCharCount


Map<String, String> newKeywrds = new HashMap<String, String>();

/*public void onClick$addNewKeywordTBId() {
	
	String selKeyword = insertKeywordTbId.getValue().trim();
	
	
	
	if( selKeyword == null || selKeyword.trim().length() == 0) {
		
		MessageUtil.setMessage("Please enter your keyword.", "color:red;");
		insertKeywordTbId.setFocus(true);
		return;
		
	}//if
	
	if(selKeyword.length() > 0 ) {
		
		if(selKeyword.equalsIgnoreCase(ADD_NEW.trim())) {
		
			MessageUtil.setMessage("Please enter your keyword.", "color:red;");
			insertKeywordTbId.setFocus(true);
			return;
			
		}
		else if(selKeyword.length() < 5){
			
			MessageUtil.setMessage("Keyword length should be of minimum 5 characters.", "color:red;");
			insertKeywordTbId.setFocus(true);
			return;
			
			
		}//else if
		
		if(selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_STOP) || 
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_STOPALL) ||
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_UNSUBSCRIBE) || 
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_END) || 
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_QUIT) || 
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_CANCEL) || 
				selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_HELP) ) {
			
			MessageUtil.setMessage("Please provide another keyword. \n Keywords should not be same as system defined keywords.", "color:red;");
			return ;
			
			
			
		}
		
		
		
		
		
		
		
	}//if
	
	
	
	Comboitem addItem = findOptionalComboitem(insertKeywordTbId, selKeyword);
	logger.info("add item is=====>"+addItem);
	
	
	if(addItem != null) {
		
		availLblId.setStyle("color:red");
		availLblId.setValue(EXIST);
	
	}
	else{
		
		boolean available = orgSMSkeywordsDao.findByKeyword(selKeyword);
		
		logger.info("availible ====>"+available);
		if(available == true) {
		
			availLblId.setStyle("color:red");
			availLblId.setValue(EXIST);
			return;
			
		}else{
			availLblId.setValue("");
		}
		else{
			
			availLblId.setStyle("color:#43A0BA;");
			availLblId.setValue(AVAILABLE);
			
		}
		try {
			int confirm = Messagebox.show("Keyword is available. Are you sure you want to add this keyword?","Add Keyword?",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1) {
				
				return ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
		
		
		
		
		String responseContent = SMSMsgKeywordTbId.getText().trim();
		
		responseContent = (responseContent.length() == 0 || responseContent.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE) ) ? null : responseContent;
		
		OrgSMSkeywords orgSmSkeyword = new OrgSMSkeywords(currUser.getUserOrganization().getUserOrgId(), Calendar.getInstance(), selKeyword,
				Constants.SMS_ORG_KEYWORD_STATUS_PENDING_APPROVAL, responseContent);
	
	
		OrgSMSkeywords orgSmSkeyword = new OrgSMSkeywords(currUser.getUserOrganization().getUserOrgId(), Calendar.getInstance(), selKeyword,
				Constants.SMS_ORG_KEYWORD_STATUS_PENDING_APPROVAL, responseContent);
		
		
		orgSMSkeywordsDao.saveOrUpdate(orgSmSkeyword);
		
		
		MessageUtil.setMessage("Keyword is added successfully and it will be confirmed accordingly.", "color:green;");
		
		//newKeywrds.put(selKeyword, responseContent);
		addItem = new Comboitem(selKeyword);
		addItem.setValue(orgSmSkeyword);
		addItem.setParent(insertKeywordTbId);
	
	}
	
	
}*/

/*private Comboitem findOptionalComboitem(Combobox cb, String label) {
	try {
		List<Comboitem> items = cb.getItems();
		for (Comboitem cbitem : items) {
			if(cbitem.getLabel().equals(label)) return cbitem;
		} // for
		
		return null;
	} catch (Exception e) {
		logger.error("Exception :::",e);
		return null;
	}
}*/


/*public void onClick$KeywordDelTBId() {
	
	
	if(insertKeywordTbId.getValue()!=null && insertKeywordTbId.getValue().trim().length() > 0) {
		String label = insertKeywordTbId.getValue().trim();
	
		Comboitem delItem = findOptionalComboitem(insertKeywordTbId, label);
		if(delItem!=null) {
			
			if(delItem.getIndex() == 0){
				
				MessageUtil.setMessage("Please select the keyword to be deleted.", "color:red;");
				return;
				
				
			}//if
			
			if(delItem.getValue() != null && delItem.getValue() instanceof OrgSMSkeywords ){
				
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the selected keyword?","Delete Keyword?",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						
						return ;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception :::",e);
				}
				
				orgSMSkeywordsDao.delete((OrgSMSkeywords)delItem.getValue());
				
			}
			
			insertKeywordTbId.removeChild(delItem);
			insertKeywordTbId.setValue("");
		}else{
			
			insertKeywordTbId.setValue("");
			insertKeywordTbId.setSelectedItem(insertKeywordTbId.getItemAtIndex(0));
			SMSMsgKeywordTbId.setValue(DEFAULT_AUTO_RESPONSE);
			if(newKeywrds.containsKey(label)) newKeywrds.remove(label);
		}
		
		availLblId.setValue("");
		insertKeywordTbId.setSelectedItem(insertKeywordTbId.getItemAtIndex(0));
		SMSMsgKeywordTbId.setValue(DEFAULT_AUTO_RESPONSE);
	}
	else{
			
		MessageUtil.setMessage("Please select the keyword to be deleted.", "color:red;");
		return;
			
		
	}//else
		
}*/


/*public void onClick$submitBtnId(){
	
	try {
		if(newKeywrds.size() > 0) {
			OrgSMSkeywords orgSmSkeyword = null;
			Set<String> keys = newKeywrds.keySet();
			
			
			List<OrgSMSkeywords> listTobeSaved = new ArrayList<OrgSMSkeywords>();
			for(String keywordEntry : keys) {
				
				logger.info("response====>"+newKeywrds.get(keywordEntry));
				
				Comboitem item = findOptionalComboitem(insertKeywordTbId, keywordEntry);
				
				if(item.getValue() == null) {
					orgSmSkeyword = new OrgSMSkeywords(currUser.getUserOrganization().getUserOrgId(), Calendar.getInstance(), keywordEntry,
					Constants.SMS_ORG_KEYWORD_STATUS_PENDING_APPROVAL, newKeywrds.get(keywordEntry));
				}else{
					
					orgSmSkeyword = item.getValue();
					orgSmSkeyword.setAutoResponse(newKeywrds.get(keywordEntry));
					
				}
				
				listTobeSaved.add(orgSmSkeyword);
				
			}
			
			orgSMSkeywordsDao.saveByCollection(listTobeSaved);
			listTobeSaved.clear();
			newKeywrds.clear();
			
			MessageUtil.setMessage("Keywords are added / modified successfully.\n" +
					" New keywords will be confirmed accordingly.", "color:green;");
			
			
		}else{
			
			MessageUtil.setMessage("No new keywords are added.", "color:green;");
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception :::",e);
	}
	
	
}*/


/*public void onClick$acceptChangesAnchId() {
	
	String keyword = insertKeywordTbId.getValue().trim();
	
	boolean keywordExists = findKeyword(keywordlbId, keyword);
	
	if(editItem != null) {
		
		if(editItem.getIndex() == 0){
			
			MessageUtil.setMessage("Please select the keyword for which you would like to change auto-response message.", "color:red;");
			return;
			
			
		}//if
		
		if(editItem.getValue() != null && editItem.getValue() instanceof OrgSMSkeywords ){
			
			try {
				int confirm = Messagebox.show("Are you sure you want to apply the changes to the keyword's auto-response?","Edit Response?",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) {
					
					return ;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::",e);
			}
			
			//orgSMSkeywordsDao.delete((OrgSMSkeywords)delItem.getValue());
			
			String responseContent = SMSMsgKeywordTbId.getText().trim();
			
			responseContent = (responseContent.length() == 0 || responseContent.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE) ) ? null : responseContent;
			
			OrgSMSkeywords editKeyword = editItem.getValue();
			
			editKeyword.setAutoResponse(responseContent);
			orgSMSkeywordsDao.saveOrUpdate(editKeyword);
			
			MessageUtil.setMessage("Keyword response modified successfully.", "color:green;");
			
			availLblId.setValue("");
			//newKeywrds.put(keyword, responseContent);
			
		}
	
		String responseContent = SMSMsgKeywordTbId.getText().trim();
		
		responseContent = (responseContent.length() == 0 || responseContent.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE) ) ? null : responseContent;
		
		
		newKeywrds.put(keyword, responseContent);
	
	}
	
}
*/
//added new for sms

	public void onClick$addNewKeywordBtnId() {
		
		int maxKeywordLimit = currUser.getUserOrganization().getMaxKeywords();
		addNewKeywordBtnId.removeAttribute(ATTRIBUTENAME);
		if(maxKeywordLimit > keywordListPaging.getTotalSize()) {
			keywordAvailabilityStatusLblId.setValue(maxKeywordLimit-keywordListPaging.getTotalSize() + " keywords available out of "+ maxKeywordLimit);
			addNewKeyordDivId.setVisible(true);
			keywordAvailabilityStatusLblId.setVisible(addNewKeyordDivId.isVisible());
		}
		else {
			
			MessageUtil.setMessage(Constants.MAX_KEYWORD_LIMIT_REACHED, "color:red;");
			addNewKeyordDivId.setVisible(false);
			//return;
		}
		
		reset();
		/*insertKeywordTbId.setDisabled(false);
		saveNewKeywordBtnId.setAttribute("editButtonMode","Save");
		saveNewKeywordBtnId.setLabel(saveNewKeywordBtnId.getAttribute("editButtonMode").toString());*/
		/*int maxKeywordLimit = currUser.getUserOrganization().getMaxKeywords();
		int usedKeywordsNumber = orgSMSkeywordsDao.findAllKeywordCountByOrg(currUser.getUserOrganization().getUserOrgId());
		logger.debug("used number is "+usedKeywordsNumber);
		if(maxKeywordLimit > usedKeywordsNumber) {
			
			addNewKeyordDivId.setVisible(true);
		}
		else {
			
			MessageUtil.setMessage(Constants.MAX_KEYWORD_LIMIT_REACHED, "color:red;");
		}*/
		
		
	}
	
	
	public void redraw(int startIndex, int endIndex) {
		 
		 MessageUtil.clearMessage();
		logger.debug("-- just entered --");
		int count =  keywordlbId.getItemCount();
		
		for(; count>0; count--) {
			keywordlbId.removeItemAt(count-1);
		}
		
		//System.gc();
		 
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		 // here check number of keywords total then we can restrict number of keywords for a user
		List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId(), startIndex,endIndex,orderby_colName,desc_Asc);
		if(keywordsList == null || keywordsList.size() == 0) {
			
			logger.debug("got no keywords for this org");
			return;
		}
		// if(keywordsList != null && keywordsList.size() > 0) {
		
			 Listitem item;
			 Listcell lc;
			 for (OrgSMSkeywords keyword : keywordsList) {
				
				item = new Listitem();
				item.setValue(keyword);
				
				lc = new Listcell(keyword.getKeyword());
				lc.setParent(item);
				
				lc = new Listcell(MyCalendar.calendarToString(keyword.getCreatedDate(), MyCalendar.FORMAT_STDATE, clientTimeZone));
				lc.setParent(item);
				
				String shortCode = keyword.getShortCode();
				if(SMSStatusCodes.setCountryCode.get(currUser.getCountryType())) {
					if(!(shortCode.startsWith(currUser.getCountryCarrier().toString())) && shortCode.length() == 10) shortCode = "+" + currUser.getCountryCarrier() + shortCode;
					else if(shortCode.length() < 10) shortCode = shortCode;
					else shortCode = "+"  + shortCode;
				}
				lc = new Listcell(shortCode);
				lc.setParent(item);
				
				lc = new Listcell();
				Label lbl = new Label(keyword.getAutoResponse());
				lbl.setMaxlength(20);
				lbl.setTooltiptext(keyword.getAutoResponse());
				lbl.setParent(lc);
				lc.setParent(item);
				
				lc = new Listcell(keyword.getStatus());
				lc.setParent(item);
				
				lc = new Listcell(MyCalendar.calendarToString(keyword.getStartFrom(), MyCalendar.FORMAT_STDATE, clientTimeZone));
				lc.setParent(item);
				
				lc = new Listcell(MyCalendar.calendarToString(keyword.getValidUpto(), MyCalendar.FORMAT_STDATE, clientTimeZone));
				lc.setParent(item);
							
				lc = new Listcell();
				Hbox hbox = new Hbox();
				hbox.setAlign("center");
				hbox.setStyle("cursor:pointer;margin-right:5px;");
				
				Image img = new Image();
			
				img = new Image();
				img.setSrc("/img/email_edit.gif");
				img.setTooltiptext("Edit");
				img.setStyle("cursor:pointer;margin-right:7px;");
				img.setAttribute("addEvent", "Edit");
				img.setTooltiptext("Edit");
				img.addEventListener("onClick", this);
				img.setParent(hbox);

				
				img = new Image();
				img.setSrc("/img/action_delete.gif");
				img.setTooltiptext("Delete");
				img.setStyle("cursor:pointer;margin-right:7px;");
				img.setAttribute("addEvent", "Delete");
				img.setTooltiptext("Delete");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/theme/home/reports_icon.png");
				img.setTooltiptext("Reports");
				img.setStyle("cursor:pointer;");
				img.setAttribute("addEvent", "Report");
				img.setTooltiptext("Report");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				hbox.setParent(lc);
				lc.setParent(item);
				
				item.setHeight("30px");
				item.setParent(keywordlbId);
			}//if
				
			//}
		 
		 
	 }//redraw
	
	public boolean validate() throws Exception{
		
		
		String msg=SMSMsgKeywordTbId.getValue();
		logger.info("asd value"+msg);
		if(!Utility.validateName(msg))
		{
			if( Messagebox.show("There are some special characters in this message. Do you want to continue?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
			
				return false;
			}	
			//MessageUtil.setMessage("Special characters are there in your message content", "top");
		}
	
	if(!insertKeywordTbId.isDisabled()) {
			
			String selKeyword = insertKeywordTbId.getText().trim();
			if( selKeyword == null || selKeyword.trim().length() == 0) {
				
				MessageUtil.setMessage("Please enter your keyword.", "color:red;");
				insertKeywordTbId.setFocus(true);
				return false;
				
			}//if
			
			String[] keywordRuleArr = SMSStatusCodes.keywordRuleMap.get(currUser.getCountryType());
			int minlength = Integer.parseInt(keywordRuleArr[1]);	
			int maxlength =  Integer.parseInt(keywordRuleArr[2]);
			
		   if(minlength != 0 && maxlength != 0 ){
			   if( selKeyword.length() < minlength){
				
				MessageUtil.setMessage("Keyword length should be minimum of "+minlength+" characters.", "color:red;");
				insertKeywordTbId.setFocus(true);
				return false;
				
			   }
			   
			   if(selKeyword.length() > maxlength){
				   
				   MessageUtil.setMessage("Keyword length should be maximum of "+maxlength+" characters.", "color:red;");
					insertKeywordTbId.setFocus(true);
					return false;
					
			   }
			}//else if
			
		   
		   String keywordPattern = keywordRuleArr[0];
		   
		  if(! Utility.validateBy(keywordPattern, selKeyword)) {
			  Messagebox.show("Provide valid keyword, special characters are not allowed except "+keywordRuleArr[3], "Keywords", Messagebox.OK, Messagebox.ERROR);
				insertKeywordTbId.setFocus(true);
				return false;
			  
			  
		  }
		   
			if(selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_STOP) || 
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_STOPALL) ||
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_UNSUBSCRIBE) || 
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_END) || 
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_QUIT) || 
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_CANCEL) || 
					selKeyword.equalsIgnoreCase(Constants.SMS_KEYWORD_HELP) ) {
				
				MessageUtil.setMessage("Please provide another keyword. \n Keywords should not be same as system defined keywords.", "color:red;");
				return false ;
				
				
				
			}
			if(receivingNumCbId.getItemCount() == 0) {
				MessageUtil.setMessage(Constants.NO_RECV_NUM, "color:red;");
				return false ;
				
			}
			if(receivingNumCbId.getSelectedItem() == null ){
				
				MessageUtil.setMessage("Please select a keyword receiving number.", "color:red;");
				return false ;
				
			}
			
			boolean keywordExists = findKeyword(selKeyword, receivingNumCbId.getSelectedItem().getValue().toString());
			logger.info("exists  item =====>"+keywordExists);
			
			
			if(keywordExists) {
				//MessageUtil.setMessage("The given keyword is already exists. Please provide another keyword.", "color:red;");
				return false ;
				/*availLblId.setStyle("color:red");
				availLblId.setValue(EXIST);
			*/
			}
			
			
			
		}//for keyword text
		
		String responseContent = SMSMsgKeywordTbId.getText().trim();
		if(responseContent.length() == 0 || responseContent.equalsIgnoreCase(DEFAULT_AUTO_RESPONSE) ) {
			MessageUtil.setMessage("Please provide auto response text.", "color:red;");
			return false ;
		}
		return true;
	}
	
	
	public void onClick$saveNewKeywordBtnId() throws Exception {
			
			
		/*String shortCode = receivingNumCbId.getText().trim();
		String responseContent = SMSMsgKeywordTbId.getText().trim();
		Calendar serverFromDateCal = getFromDateCal();
		Calendar serverToDateCal = getToDateCal();
		
		if(shortCode.isEmpty() || shortCode.length() <=0) {
			MessageUtil.setMessage(Constants.NO_RECV_NUM, "color:red;");
			return;
		}*/
		try {
			if(!validate() ) return;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception with validate ");
		}
		
		
		if(!validPeriod()) return;
		
	
		OrgSMSkeywords orgSmSkeyword = (OrgSMSkeywords)addNewKeywordBtnId.getAttribute(ATTRIBUTENAME);
		//Calendar validityCompDate = Calendar.getInstance();
		
		String selKeyword = insertKeywordTbId.getText().trim();
		String responseContent = SMSMsgKeywordTbId.getText().trim();
		String shortCode = receivingNumCbId.getSelectedItem().getValue().toString();
		Calendar serverFromDateCal = null;
		Calendar serverToDateCal = null;
		try {
			serverFromDateCal = getFromDateCal();
			serverToDateCal = getToDateCal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception with getcalendar ");
		}
		
		List<UserSMSSenderId> retSenderId = userSMSSenderIdDao.findSenderIdBySMSType(currUser.getUserId(), SMSStatusCodes.defaultSMSTypeMap.get(currUser.getCountryType()));
		
		String account = SMSStatusCodes.defaultAccountMap.get(currUser.getCountryType());
		String[] accountArr = account.split(Constants.ADDR_COL_DELIMETER);
		String senderId = retSenderId != null ? retSenderId.get(0).getSenderId() : accountArr[3];
		
		if(orgSmSkeyword == null) {
			
			 orgSmSkeyword = new OrgSMSkeywords(currUser.getUserOrganization().getUserOrgId(), Calendar.getInstance(), selKeyword,
					Constants.KEYWORD_STATUS_PENDING, currUser);
			
		}
		else {
			if(!orgSmSkeyword.getShortCode().equalsIgnoreCase(shortCode)) {
				
				Messagebox.show("SMS Campaigns containing this keyword will be affected on change of Message Receiving Number.", "Keywords", Messagebox.OK, Messagebox.INFORMATION);
			}
		}
		
		try {
			int confirm = Messagebox.show("Are you sure you want to save the keyword?","Create/Update Keyword?",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1) {
				
				return ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception with message box ");
		}
		
		if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(orgSmSkeyword.getStatus())) {
			
			MyDatebox tempbox = new MyDatebox();
			tempbox.setValue(orgSmSkeyword.getValidUpto());
			Calendar serverExtendedDate = tempbox.getServerValue();
			/*Calendar clientExtendedDate = tempbox.getClientValue();
			
			serverExtendedDate.set(Calendar.HOUR_OF_DAY, 23 + serverExtendedDate.get(Calendar.HOUR_OF_DAY)- clientExtendedDate.get(Calendar.HOUR_OF_DAY));
			serverExtendedDate.set(Calendar.MINUTE, 59 + serverExtendedDate.get(Calendar.MINUTE)	- clientExtendedDate.get(Calendar.MINUTE));
			serverExtendedDate.set(Calendar.SECOND, 59);*/
			
			if(serverToDateCal.after(serverExtendedDate)) {
				
				orgSmSkeyword.setValidUpto(serverToDateCal);
				//orgSmSkeyword.setStatus(Constants.SMS_ORG_KEYWORD_STATUS_PENDING_APPROVAL);
				orgSmSkeyword.setCreatedDate(Calendar.getInstance());
				orgSmSkeyword.setUser(currUser);
			}
			else {
				
				Calendar expDate = orgSmSkeyword.getValidUpto();
				
				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				
				String timezoneDiffrenceMinutes = currUser.getClientTimeZone();
				int timezoneDiffrenceMinutesInt = 0;
				
				if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				
				timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
				
				expDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				
				String date =	MyCalendar.calendarToString(orgSmSkeyword.getValidUpto(), MyCalendar.FORMAT_STDATE);

				MessageUtil.setMessage("This is expired keyword,please change 'To' date after " +date +"  to extend validity.", "color:red;");
				return;
				
			}
			
		}
		else {
			orgSmSkeyword.setValidUpto(orgSmSkeyword.getValidUpto());
			//validityCompDate = orgSmSkeyword.getCreatedDate();
		}
		
		
		Calendar activeFrom = getFromDateCal();
		
		MyDatebox tempbox = new MyDatebox();
		tempbox.setValue(Calendar.getInstance());
		Calendar today = tempbox.getServerValue();
		
		
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE,0);
		today.set(Calendar.SECOND, 0);
		
		activeFrom.set(Calendar.HOUR_OF_DAY, 0);
		//activeFrom.set(Calendar.MINUTE,1);
		activeFrom.set(Calendar.MINUTE,0);
		activeFrom.set(Calendar.SECOND, 0);
		
		//SimpleDateFormat format = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
		
	//	today.setTime(format.parse(MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTODATE)));
		logger.debug(" today is "+ today);
		logger.debug(" activeFrom is "+ activeFrom);
		
	//	activeFrom.setTime(format.parse(MyCalendar.calendarToString(serverFromDateCal, MyCalendar.FORMAT_YEARTODATE)));
		
		logger.debug("Active from "+ MyCalendar.calendarToString(activeFrom, MyCalendar.FORMAT_DATETIME_STDATE) );
		
		int diffDays =  (int) ((activeFrom.getTime().getTime()/ (1000*60*60*24)) - (today.getTime().getTime()/ (1000*60*60*24) ) );
		//int diffDays =  (int) ((today.getTime().getTime()/ (1000*60*60*24) ) - (activeFrom.getTime().getTime()/ (1000*60*60*24)));
		
		logger.debug("days diff is  "+ diffDays );
		
		if(diffDays == 0) {
			orgSmSkeyword.setStatus(Constants.KEYWORD_STATUS_ACTIVE);
			logger.debug("Active from");
		}
		else if(diffDays > 0){
			orgSmSkeyword.setStatus(Constants.KEYWORD_STATUS_PENDING);
			logger.debug("pending from " );
		}
		
		orgSmSkeyword.setModifiedBy(currUser.getUserId());
		orgSmSkeyword.setModifiedDate(Calendar.getInstance());
		orgSmSkeyword.setStartFrom(serverFromDateCal);
		orgSmSkeyword.setValidUpto(serverToDateCal);
		orgSmSkeyword.setShortCode(shortCode);
		orgSmSkeyword.setAutoResponse(responseContent);
		orgSmSkeyword.setSenderId(senderId);
		
		//orgSMSkeywordsDao.saveOrUpdate(orgSmSkeyword);
		orgSMSkeywordsDaoForDML.saveOrUpdate(orgSmSkeyword);
		int totalSize = orgSMSkeywordsDao.getUserOrgSMSKeyWordsCount(currUser.getUserOrganization().getUserOrgId());
		
		logger.debug("total size is after update"+totalSize);
		keywordListPaging.setTotalSize(totalSize);
		int pNo = Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel());
		keywordListPaging.setPageSize(pNo);
		keywordListPaging.setActivePage(0);
		redraw(0,(byte)pNo);
		addNewKeyordDivId.setVisible(false);
		keywordAvailabilityStatusLblId.setVisible(addNewKeyordDivId.isVisible());
		
	
	}
	
	private Boolean findKeyword(String keyword, String shortCode) {
		try {
			
			boolean available = false;
			boolean editable = false; 
			List<Object[]> promoKeywordsList= orgSMSkeywordsDao.findExistencePromoKeyword(keyword,shortCode);
			List<Object[]> otherKeywordsList= orgSMSkeywordsDao.findExistenceOtherKeyword(keyword,shortCode);
			
			boolean nonPromo = false;
			for(Object promoKeywords : promoKeywordsList) {
				
						logger.debug("keyword org id "+ (((OrgSMSkeywords) promoKeywords).getOrgId()));
						logger.debug("exp status  "+ ((OrgSMSkeywords) promoKeywords).getStatus());
						if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(((OrgSMSkeywords) promoKeywords).getStatus())) {
							if(currUser.getUserOrganization().getUserOrgId().toString().equalsIgnoreCase((((OrgSMSkeywords) promoKeywords).getOrgId()).toString())) {
								
								editable = true;
								available = true;
							}
							else if(!currUser.getUserOrganization().getUserOrgId().toString().equalsIgnoreCase((((OrgSMSkeywords) promoKeywords).getOrgId()).toString())) {
								available = false;
								editable = false;
							}
						}
						else {
							if(!currUser.getUserOrganization().getUserOrgId().toString().equalsIgnoreCase((((OrgSMSkeywords) promoKeywords).getOrgId()).toString())) {
								MessageUtil.setMessage("The given keyword already exists in another organization. Please provide another keyword.", "color:red;");
								available = true;
								editable = false;
								break;
							}
							else if(currUser.getUserOrganization().getUserOrgId().toString().equalsIgnoreCase((((OrgSMSkeywords) promoKeywords).getOrgId()).toString())) {
								MessageUtil.setMessage("The given keyword already exists in this organization. Please provide another keyword.", "color:red;");
								available = true;
								break;
							}
						}
		  }
		if(editable) MessageUtil.setMessage("The given keyword has expired, Please edit it to extend your keyword's validity.", "color:red;");
		 for(Object otherKeywords : otherKeywordsList) {
			 
						logger.debug("sms settings keyword "+((SMSSettings) otherKeywords).getKeyword());
						
						if(otherKeywords != null) {
							logger.debug("in smssettings  ");
							MessageUtil.setMessage("The given keyword already exists. Please provide another keyword.", "color:red;");
							available = true;
							break;
						}
		}
			
			return available;
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
	}
	
   public Calendar getFromDateCal() throws Exception {
		
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		/*Calendar tempClientFromCal = fromDateboxId.getClientValue();
		
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, serverFromDateCal.get(Calendar.HOUR_OF_DAY)- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, serverFromDateCal.get(Calendar.MINUTE)- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);*/

		return serverFromDateCal;
	}
	
	public Calendar getToDateCal() throws Exception {
		
		Calendar serverToDateCal = toDateboxId.getServerValue();
		/*Calendar tempClientToCal = toDateboxId.getClientValue();
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 59 + serverToDateCal.get(Calendar.MINUTE)	- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);*/

		return serverToDateCal;
		
	}
	
	public boolean validPeriod() throws Exception {
		
		
		Calendar fromCal = getFromDateCal();
		Calendar toCal = getToDateCal();
		
		MyDatebox serverCreatedDatebox = new MyDatebox();
		serverCreatedDatebox.setValue(Calendar.getInstance());
		Calendar serverCreatedDate = serverCreatedDatebox.getServerValue();
		
		
		boolean isValid = true;
		
		
		OrgSMSkeywords orgSmSkeyword = (OrgSMSkeywords)addNewKeywordBtnId.getAttribute(ATTRIBUTENAME);
		if(orgSmSkeyword != null) {
				MyDatebox tempbox = new MyDatebox();
				tempbox.setValue(orgSmSkeyword.getStartFrom());
				Calendar startsFromDate = tempbox.getServerValue();
				
				if((fromCal.getTime().getTime() - startsFromDate.getTime().getTime() ) / (1000*60*60*24) != 0) {
					
					int fromDateDiff = (int) ((fromCal.getTime().getTime() - serverCreatedDate.getTime().getTime() ) / (1000*60*60*24));
					
					logger.debug("difference of days is edit  "+fromDateDiff +" created on"+ serverCreatedDate);
					if(fromDateDiff < 0) {
						MessageUtil.setMessage("Keyword can not be activated from past date.", "color:red;");
						isValid = false;
					} 
				}
		}
		else {
			
			int fromDateDiff = (int) ((fromCal.getTime().getTime() - serverCreatedDate.getTime().getTime() ) / (1000*60*60*24));
			
			logger.debug("difference of days is  new "+fromDateDiff);
			
			if(fromDateDiff < 0) {
				MessageUtil.setMessage("Keyword can not be activated from past date.", "color:red;");
				isValid = false;
			} 
		}
		
				
		int diffDays =  (int) ((toCal.getTime().getTime() - serverCreatedDate.getTime().getTime())/ (1000*60*60*24) );
		logger.debug(" difference in days  "+diffDays);
		
		int toFromDateDiff = (int) ((toCal.getTime().getTime() - fromCal.getTime().getTime())/ (1000*60*60*24) );
		
		logger.debug("to days  "+(toCal.getTime().getTime()/ (1000*60*60*24)));
		logger.debug("from days  "+(fromCal.getTime().getTime()/ (1000*60*60*24)));
		
		logger.debug(" to time difference in days  "+toFromDateDiff);
		
		if(toFromDateDiff < 0) {
			
			MessageUtil.setMessage(Constants.EXP_DATE_BEFORE_START, "color:red;");
			isValid = false;
		}
		else if(diffDays > 180 ) {
			MessageUtil.setMessage(Constants.TO_DATE_MORE_THAN_SIX_MONTHS, "color:red;");
			isValid = false;
		}    
		return isValid;
	}
	
	public void reset() {
		
		insertKeywordTbId.setText("");
		insertKeywordTbId.setDisabled(false);
		fromDateboxId.setValue(Calendar.getInstance());
		Calendar toDate = Calendar.getInstance();
		toDate.add(Calendar.DAY_OF_MONTH, 180);
		toDateboxId.setValue(toDate);
		
		SMSMsgKeywordTbId.setText(DEFAULT_AUTO_RESPONSE);
		getCharCount(SMSMsgKeywordTbId.getText());
		
		if(receivingNumCbId.getItemCount() > 0) receivingNumCbId.setSelectedIndex(0);
		//setReceivingNumbers();
	}

	public void onBlur$linkURLTxtBoxId(){
		String defaultText = linkURLTxtBoxId.getText();
		if(defaultText.isEmpty() || defaultText.length() == 0) {
			linkURLTxtBoxId.setText("Use Url Shortener");
		}
	}//
	
	public void setReceivingNumbers() {
		
		Components.removeAllChildren(receivingNumCbId);
		
		/*//String msgReceivingNumbers = currUser.getUserOrganization().getMsgReceivingNumbers();
		
		if(msgReceivingNumbers == null) return ;
		
		String receivingNumbers[] =  msgReceivingNumbers.split(Constants.ADDR_COL_DELIMETER);
		Comboitem ci;
		//boolean isRecvNum = false;
		for(String recvNum : receivingNumbers) {
			
			if(!recvNum.isEmpty()) {
				
				ci = new Comboitem();
				ci.setValue(recvNum);
				
				if(SMSStatusCodes.setCountryCode.get(currUser.getCountryType())) {		
					if(!(recvNum.startsWith(currUser.getCountryCarrier().toString())) && recvNum.length() == 10) recvNum = "+" + currUser.getCountryCarrier() + recvNum;
					else recvNum = "+" + recvNum;
				}
				//recvNum = "+" + recvNum;
				ci.setLabel(recvNum);
				ci.setParent(receivingNumCbId);
			}
		}*/
		//String msgReceivingNumbers = currUser.getUserOrganization().getMsgReceivingNumbers();
		
				List<String> msgReceivingNumbers = countryReceivingNumbersDao.getReceivingNumByCountry(currUser.getCountryType(), Constants.SMS_TYPE_PROMOTIONAL);
				
				if(msgReceivingNumbers == null) return ;
				
				Comboitem ci;
				//boolean isRecvNum = false;
				for(String recvNum : msgReceivingNumbers) {
					
					if(!recvNum.isEmpty()) {
						
						ci = new Comboitem();
						ci.setValue(recvNum);
						
						if(SMSStatusCodes.setCountryCode.get(currUser.getCountryType())) {		
							if(!(recvNum.startsWith(currUser.getCountryCarrier().toString())) && 
									(recvNum.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
									&& recvNum.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) recvNum = "+" + currUser.getCountryCarrier() + recvNum;
							else if(recvNum.length() < 10) recvNum = recvNum;
							else recvNum = "+" + recvNum;
						}
						//recvNum = "+" + recvNum;
						ci.setLabel(recvNum);
						ci.setParent(receivingNumCbId);
					}
				}
		
		if(receivingNumCbId.getItemCount() > 0) { 
			receivingNumCbId.setSelectedIndex(0);
		}
		
	}
	
	public void onSelect$keywordPageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(keywordlbId.getItemCount() == 0 ) {
					
					logger.debug("No reports found for this user...");
					return;
				}
				
				if(keywordListPaging!=null){
					int pNo = Integer.parseInt(keywordPageSizeLbId.getSelectedItem().getLabel());
					keywordListPaging.setPageSize(pNo);
					//campListPaging1.setPageSize(pNo);
					redraw(0, (byte)pNo);
				}

			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
	 }
	
	public void onBlur$insertKeywordTbId() {
		
		String selKeyword = insertKeywordTbId.getText().trim();
		if( selKeyword != null || selKeyword.trim().length() > 0) {
			insertKeywordTbId.setText(insertKeywordTbId.getText().toUpperCase());
			
		}//if
	}
	
	public void onClick$checkAvailTbId() {
		
		String[] keywordRuleArr = SMSStatusCodes.keywordRuleMap.get(currUser.getCountryType());
		String selKeyword = insertKeywordTbId.getText().trim();
		if( selKeyword == null || selKeyword.trim().length() == 0) {
			
			MessageUtil.setMessage("Please enter your keyword.", "color:red;");
			insertKeywordTbId.setFocus(true);
			
		}//if
		else {
			 String keywordPattern = keywordRuleArr[0];
			 int minlength = Integer.parseInt(keywordRuleArr[1]);	
			 int maxlength =  Integer.parseInt(keywordRuleArr[2]);
				
			   if(minlength != 0 && maxlength != 0 ){
				   if( selKeyword.length() < minlength){
					
					MessageUtil.setMessage("Keyword length should be minimum of "+minlength+" characters.", "color:red;");
					insertKeywordTbId.setFocus(true);
					return ;
					
				   }
				   
				   if(selKeyword.length() > maxlength){
					   
					   MessageUtil.setMessage("Keyword length should be maximum of "+maxlength+" characters.", "color:red;");
						insertKeywordTbId.setFocus(true);
						return ;
						
				   }
			  } 
			  if(! Utility.validateBy(keywordPattern, selKeyword)) {
				  
				  Messagebox.show("Provide valid keyword, special characters are not allowed except "+keywordRuleArr[3], "Keywords", Messagebox.OK, Messagebox.ERROR);
					insertKeywordTbId.setFocus(true);
					return ;
			  }
			boolean keywordExists = findKeyword(selKeyword, receivingNumCbId.getSelectedItem().getValue().toString());
			logger.info("exists  item =====>"+keywordExists);
			
			if(!keywordExists) {
				MessageUtil.setMessage("Keyword is available.", "color:green;");
			}
		}
	}
}//SmsCampSetupController
