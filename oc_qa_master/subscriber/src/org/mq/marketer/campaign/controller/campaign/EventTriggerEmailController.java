package org.mq.marketer.campaign.controller.campaign;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.TriggerCustomEvent;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.EventTriggerDaoForDML;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.TriggerCustomEventDao;
import org.mq.marketer.campaign.dao.TriggerCustomEventDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;

public class EventTriggerEmailController extends GenericForwardComposer implements EventListener{

	private Toolbarbutton toolBarId;
	private Grid myGrid;
	// private Toolbarbutton addNewTbarId;

	// Custom Event Fields
	private MyDatebox custmEvntDateBxId;
	private Textbox custEvntTbId;
	private Textbox trigCampNameTbId, productTxtBxId;
	private Combobox trigCampFromCbId, trigCampReplyCbId;
	private EventTrigger eventTrigger ;
	private Users user;
	
	private Rows triggerRowsId;
	
	private POSMappingDao posMappingDao;
	
	private Checkbox sendEmailCampaignChkBxId, sendSMSCampaignChkBxId,
			addToOtherListChkBxId, removeFrCampMlChkBxId, triggerStatusChkBxId, dateIsYrIgnoreChkBxId;

	private Grid existingTriggersGrdId;
	private MyRenderer renderer;
	private Tabbox triggerTabbxId;
	private Tab addTriggerTabId, triggersTabId;
	private Grid existingCustomEventsGrdId;

	private ListModelList listModelList;
	private Image urlHelpImgId ;
	private Listbox ETMlListsLBId, contactDateLbId, userWebFormsLbId, contactOptInMediumLbId, urlSetLbId;
	private Listbox addToMlLbId;
	private Listbox campaignsLbId, campOpenedLbId, campClickedLbId,
	productCriteriaLBId, productOptionsLbId, purchaseAmtOptionsLbId;
	private Listbox smsLbId, mintsLbId, hoursLbId, meridianLbId, triggerPerPageLBId;
	private Doublebox purchaseAmountDblBxId, purchaseAmount1DblBxId;
	private Window previewWinId;
	private Div campConfigSettngDivId, sendEmailCampaignDivId, sendSMSCampaignDivId,
			addToOtherListDivId, MoreOptionsDivId, urlSetDivId;
	
	private Users users = GetUser.getUserObj();

	private Paging triggerPagingId;
	String filterQuery=null;
	String filterCountQuery=null;
	String qryPrefix=null;	
	
	LBFilterEventListener eventTriggerLbELObj =null;
	
	private Checkbox ignoreSunChkBoxId,  ignoreMonChkBoxId, ignoreTueChkBoxId, 
		ignoreWedChkBoxId, ignoreThurChkBoxId, ignoreFriChkBoxId, ignoreSatChkBoxId,layaltyIssueChkId,
		layaltyRedeemChkId,layaltyAdjustChkId,giftTopUpChkId,giftRedeemChkId, layaltyDiffIssueChkId;//enableTargetTimeChkBoxId;
	
	private Label targetTimeLblId, targetDayLblId;
	
	private CampaignsDao campaignsDao;

	private MailingListDao mailingListDao;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	
	private Listitem afterListItemId, beforeListItemId, onListItemId, onDayListItemId;

	private SMSCampaignsDao smsCampaignsDao;
	private EventTriggerDao eventTriggerDao;
	private EventTriggerDaoForDML eventTriggerDaoForDML;
	private UserFromEmailIdDao userFromEmailIdDao;
	private FormMappingDao formMappingDao;
	CampaignReportDao campaignReportDao;
	
	private Div productDivId, contactDateDivId, campaignOpenDivId, onPurchaseDivId,
       optinMediumDivId, campaignClickedDivId,dateAddedDivId,amountDivId,loyaltyPointsDivId,giftBalDivId, loyaltyDiffDivId;
	
	
	
	
	private Set<Long> listIdsSet;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	// private static Logger logger =
	// Logger.getLogger(EventTriggerEmailController.class);

	Session sessionScope = Sessions.getCurrent();
	TriggerCustomEventDao triggerCustomEventDao;
	TriggerCustomEventDaoForDML triggerCustomEventDaoForDML;
	TimeZone clientTimeZone = (TimeZone) sessionScope
			.getAttribute("clientTimeZone");

	public EventTriggerEmailController() {

		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		smsCampaignsDao = (SMSCampaignsDao) SpringUtil
				.getBean("smsCampaignsDao");

		triggerCustomEventDao = (TriggerCustomEventDao) SpringUtil
				.getBean("triggerCustomEventDao");
		triggerCustomEventDaoForDML = (TriggerCustomEventDaoForDML) SpringUtil
				.getBean("triggerCustomEventDaoForDML");
		eventTriggerDao = (EventTriggerDao) SpringUtil
				.getBean("eventTriggerDao");
		eventTriggerDaoForDML = (EventTriggerDaoForDML) SpringUtil
				.getBean("eventTriggerDaoForDML");
		userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil
				.getBean("userFromEmailIdDao");
		campaignReportDao = (CampaignReportDao) SpringUtil.getBean("campaignReportDao");
		
		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		formMappingDao = (FormMappingDao)SpringUtil.getBean("formMappingDao");
		
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		
		user = GetUser.getUserObj();
		renderer = new MyRenderer();

		// added for sharing
		listIdsSet = (Set<Long>) sessionScope.getAttribute(Constants.LISTIDS_SET);
		String style="font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Event Trigger", "", style, true);
		

	}

	// Paging triggerViewsPagingId;

	private ListitemRenderer itemRenderer = new MyListItemRenderer();
	private Map<Radio, Div> RADIO_DIV_MAP = new HashMap<Radio, Div>();
	private Listbox categoryLbId;

	// private ListitemRenderer itemRenderer1 = new MyListItemRenderer1();
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {

			super.doAfterCompose(comp);

			int totalSize = eventTriggerDao.findAllCountByUserId(user.getUserId(),true); 
			
			triggerPagingId.setTotalSize(totalSize);
			triggerPerPageLBId.setSelectedIndex(0);
			//triggerPagingId.setActivePage(0);
			//triggerPagingId.addEventListener("onPaging", this);
			
			existingTriggersGrdId.setModel(getTriggersListModel(0, triggerPagingId.getPageSize()));
			//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
			existingTriggersGrdId.setRowRenderer(renderer);
			
			
			// populate all the mailinglists
			getMailingListsModel();
			/*ETMlListsLBId.setModel(getMailingListsModel());
			ETMlListsLBId.setItemRenderer(itemRenderer);
			*/
			//populate campaigns
			campOpenedLbId.setModel(getCampaignsListModel());
			campOpenedLbId.setItemRenderer(itemRenderer);
			if(campOpenedLbId.getItemCount() > 0) campOpenedLbId.setSelectedIndex(0);
			
			campClickedLbId.setModel(getCampaignsListModel());
			campClickedLbId.setItemRenderer(itemRenderer);
			if(campClickedLbId.getItemCount() > 0) campClickedLbId.setSelectedIndex(0);
			
			campaignsLbId.setModel(getCampaignsListModel());
			campaignsLbId.setItemRenderer(itemRenderer);
			if(campaignsLbId.getItemCount() > 0) {
				
				campaignsLbId.setSelectedIndex(0);
				logger.debug("selected item value ::"+campaignsLbId.getSelectedIndex() + campaignsLbId.getSelectedItem().getValue());
				
			}
			// Adde for Campaign category 
			
			if(user.getSubscriptionEnable()){
	     		getCampCategorties();
	     		
	     	}else{
	     		categoryLbId.setDisabled(true);
	     	}
	     	helpImgId.setVisible(categoryLbId.isDisabled());
			
			
			smsLbId.setModel(getSmsCampaignListModel());
			smsLbId.setItemRenderer(itemRenderer);
			if(smsLbId.getItemCount() > 0) smsLbId.setSelectedIndex(0);

			/*contactDateLbId.setModel(getContactDateMppingsModel());
			contactDateLbId.setItemRenderer(itemRenderer);*/
			
			//defaultSetTime();
			timerListBoxId.setSelectedIndex(0);
			Radio[] radioArr = {productRbnId,contactDateRbnId,campaignOpenRbnId,onPurchaseRbnId,
			        optinMediumRbnId,campaignClickedRbnId,dateAddedRbnId,amountRbnId,loyaltyRbnId,giftRbnId, differenceRbnTd};
			
			Div[]	divArr = {productDivId, contactDateDivId, campaignOpenDivId, onPurchaseDivId,
		   	          optinMediumDivId, campaignClickedDivId,dateAddedDivId, amountDivId,loyaltyPointsDivId,giftBalDivId, loyaltyDiffDivId};

			
			for (int i = 0; i < radioArr.length; i++) {
				
				radioArr[i].addEventListener("onCheck", this);
				RADIO_DIV_MAP.put(radioArr[i], divArr[i]);
				
			}
			
			existingTriggersGrdId.setAttribute("defaultOrderBy", "triggerModifiedDate");
			registerEventListner(0, 5);
			//Utility.refreshModel(eventTriggerLbELObj, 0, false);
			existingTriggersGrdId.setRowRenderer(renderer);
			
		} catch (Exception e) {
			logger.error("** Error : Error occured while loading the page . **",e);
		}

	} // doAfterCompose
	
	
	
	private void registerEventListner(int strtIdx, int endIdx){

		filterQuery = "FROM EventTrigger  WHERE users="+users.getUserId().longValue(); 
		logger.info(users.getUserId().longValue());
		filterCountQuery = "SELECT COUNT(id) "+filterQuery;

		qryPrefix="";

		try{
						Map<Integer, Field> objMap = new HashMap<Integer, Field>();
						objMap.put(1, EventTrigger.class.getDeclaredField("triggerName"));
						objMap.put(2, EventTrigger.class.getDeclaredField("triggerCreatedDate"));
						objMap.put(3, EventTrigger.class.getDeclaredField("triggerModifiedDate"));
						//objMap.put(4, EventTrigger.class.getDeclaredField("status"));

						triggerPagingId.setPageSize(Integer.parseInt(triggerPerPageLBId.getSelectedItem().getLabel()));
						
						eventTriggerLbELObj= LBFilterEventListener.grFilterSetup(existingTriggersGrdId, triggerPagingId, filterQuery, filterCountQuery, qryPrefix, objMap);
					//existingTriggersGrdId.setRowRenderer(renderer);
						//Utility.refreshModel(eventTriggerLbELObj, 0, false);
							}catch(Exception e){
								logger.error("Exception :: ",e);
							}
		}
	
	private Image helpImgId;
	private Popup help2;
	public void getCampCategorties(){
	
			List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findCatByUserId(user.getUserId());
			
			//TODO chk with mallika
			categoryLbId.appendChild( new Listitem(Constants.CAMP_CATEGORY_TRANSACTIONAL));
			Listitem campCategory = null;
			if(userCategoriesList == null || userCategoriesList.size() == 0){
				categoryLbId.setDisabled(true);
				
			}
			else{
				
				for (UserCampaignCategories userCampaignCategories : userCategoriesList) {
					campCategory = new Listitem(userCampaignCategories.getCategoryName(),userCampaignCategories);
					campCategory.setParent(categoryLbId);
					
				}
				
				categoryLbId.setDisabled(false);
			}
			helpImgId.setVisible(categoryLbId.isDisabled());
			if(categoryLbId.getItemCount() > 0 ) categoryLbId.setSelectedIndex(0);
		}
	
	
	
	public void getCampaignCategories() {
		
		if(user.getSubscriptionEnable() && campaignList != null && campaignList.size() > 0) {
			String categoryIds = Constants.STRING_NILL;
			for (Campaigns campObj: campaignList) {
				if(campObj.getCategories() == null) continue;
				
				if(!categoryIds.isEmpty()) categoryIds += Constants.DELIMETER_COMMA;
				
				categoryIds += campObj.getCategories().toString();
				
				
				
			}
			if(!categoryIds.isEmpty()) {
				
				
				List<UserCampaignCategories> usercatObjList =	userCampaignCategoriesDao.findById(categoryIds, user.getUserId());
				if(usercatObjList != null) {
					
					
					for (Listitem campItem : campaignsLbId.getItems()) {
						logger.debug("campItem ::"+campItem.getValue());
						if(((Campaigns)campItem.getValue()).getCategories() == null)continue;
						
						for (UserCampaignCategories userCampaignCategories : usercatObjList) {
							
							if(((Campaigns)campItem.getValue()).getCategories().equals(userCampaignCategories.getId())) {
								
								campItem.setAttribute("CategoryName", userCampaignCategories.getCategoryName());
							}
							
						}
						
						
						
					}
					
					
				}//if
				
			}//if
			
			
			
		}
		
		
	
		
		
		
	}//getCampaignCategories
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Paging) {
			
			
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			existingTriggersGrdId.setModel(getTriggersListModel(ofs, (byte) pagingEvent.getPageable().getPageSize()));
			//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
			existingTriggersGrdId.setRowRenderer(renderer);
			
			
			
		} else if (event.getTarget() instanceof Radio) {
			
			Radio eventTargetRadio = (Radio)event.getTarget();
			doToggleRadioBtn(eventTargetRadio);
			
		}
		
		
		
	}
	
	public void doToggleRadioBtn(Radio eventTargetRadio){
		
		String[] radioArr = {"productRbnId","contactDateRbnId","campaignOpenRbnId","onPurchaseRbnId",
                "optinMediumRbnId","campaignClickedRbnId","dateAddedRbnId","amountRbnId","loyaltyRbnId","giftRbnId", "differenceRbnTd"};

		Div[]	divArr = {productDivId, contactDateDivId, campaignOpenDivId, onPurchaseDivId,
   	          optinMediumDivId, campaignClickedDivId,dateAddedDivId,amountDivId,loyaltyPointsDivId,giftBalDivId, loyaltyDiffDivId};

		

		Set<Radio> keySet = RADIO_DIV_MAP.keySet();

		for (Radio radio : keySet) {
		
			if(radio.getId().equals(eventTargetRadio.getId())) {
				
				doTriggerTypeSettings(radio, RADIO_DIV_MAP.get(radio) );
				
			}
			else{
				
				RADIO_DIV_MAP.get(radio).setVisible(false);
			}
	
		}//for


		
		
	}
	
	
	
	public void doTriggerTypeSettings(Radio eventTargetRadio, Div div ) {
		
		div.setVisible(true);
		Listitem afterLi = (Listitem)triggerActionLbId.getFellowIfAny("afterListItemId");
		Listitem beforeLi = (Listitem)triggerActionLbId.getFellowIfAny("beforeListItemId");
		Listitem onLi = (Listitem)triggerActionLbId.getFellowIfAny("onListItemId");
		
		Listitem hoursLi = (Listitem)hoursDaysLbId.getFellowIfAny("hoursListItemId");
		Listitem onDayLi = (Listitem)triggerActionLbId.getFellowIfAny("onDayListItemId");
		onDayLi.setVisible(true);
		afterLi.setVisible(true);
		if( eventTargetRadio.getId().equals("contactDateRbnId" ) || eventTargetRadio.getId().equals("optinMediumRbnId")  ) {
			if( eventTargetRadio.getId().equals("contactDateRbnId" ) ){
				onLi.setVisible(false);
				hoursLi.setVisible(false);
				beforeLi.setVisible(true);
			}else{
				
				beforeLi.setVisible(false);
				hoursLi.setVisible(true);
				onLi.setVisible(true);
			}
				
			//(zk.Widget.$(jq('$onListItemId')[0])).setLabel('On the same date');
			//zk.Widget.$("$onListItemId").setLabel('On the same date');
			//onLi.setLabel('Immediately');
			}/*else if(eventTargetRadio.getId().equals("loyaltyRbnId") || eventTargetRadio.getId().equals("giftRbnId")){
				logger.debug(" ==== loyaltybalance radio button called here..");
				onLi.setVisible(true);
				beforeLi.setVisible(false);
				hoursLi.setVisible(false);
				afterLi.setVisible(false);
				onDayLi.setVisible(true);
			}*/else{
				
				onLi.setVisible(true);
				beforeLi.setVisible(false);
				hoursLi.setVisible(true);
				//(zk.Widget.$(jq('$onListItemId')[0])).setLabel('Immediately');
				
			}
		
		int mkSelectIndex = 0;
		for (Listitem actionLI : triggerActionLbId.getItems()) {
			
			if(actionLI.isVisible()) {
				
				mkSelectIndex = actionLI.getIndex();
				break;
				
			}
			
			
		}
		
		triggerActionLbId.setSelectedIndex(mkSelectIndex);
		onSelect$triggerActionLbId();
		
	}//doTriggerTypeSettings
	
	
	public void onSelect$triggerPerPageLBId() {
		

		try {
			int count = Integer.parseInt(triggerPerPageLBId.getSelectedItem().getLabel());
			
			//existingTriggersGrdId.setModel(getTriggersListModel(0,count));
			//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
			//existingTriggersGrdId.setRowRenderer(renderer);
			
			triggerPagingId.setPageSize(count);
			triggerPagingId.setActivePage(0);
			//registerEventListner(0, triggerPagingId.getPageSize());
			Utility.refreshGridModel(eventTriggerLbELObj, 0, false);

			
			//System.gc();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	
		
		
		
		
		
	}//method
	
	//private Timebox timeBoxId;
	private Listbox timerListBoxId;
	private Div targetDaysDivId;
	private void defaultSetTime() {
		
		for(int i=0; i< 12; i++) {
			Listitem tempItem = new Listitem();
			if(i<10) {
				tempItem.setLabel("0"+i);
			}else {
				tempItem.setLabel(""+i);
			}
			tempItem.setParent(hoursLbId);
		}
		

		TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		Calendar temCal = new  MyCalendar(clientTimeZone);
		Date date = (temCal.getTime());
		//timeBoxId.setValue(date);
		
		
		
		/*Integer[] timeSatmp = new Integer[3];
	
		List<Listitem> mintsListItemList = mintsLbId.getItems();
		
		int currentHourInt = temCal.get(Calendar.HOUR);
		int currentMintint = temCal.get(Calendar.MINUTE);
		int currentMeridian = temCal.get(Calendar.AM_PM);
		
//		boolean setHourfalg = false;
		Listitem tempItem = null;
		
		for (Listitem listitem : mintsListItemList) {
			
			int tempInt  = Integer.parseInt(listitem.getLabel().trim());
			
			if(currentMintint > tempInt) {
				
				
				if(((Listitem)mintsLbId.getLastChild()).getLabel().equals(listitem.getLabel())) {
					mintsLbId.setSelectedIndex(0);//Item(listitem); 
					if(currentHourInt == 12) {
						
						currentHourInt = 1;
					}
					else{
						currentHourInt = 	currentHourInt +1;
					}
					break;
				}
				
				
			
			}else if(currentMintint <= tempInt){
				//else continue;
				mintsLbId.setSelectedItem(listitem); break;
			}
			
		} // for min
		
		List<Listitem> hoursListItemList = hoursLbId.getItems();
		int tempListItemHour = 0;
		
		for (Listitem  listitem : hoursListItemList) {
			tempListItemHour = Integer.parseInt(listitem.getLabel().trim());
			logger.info("tempListItemHour "+tempListItemHour+" current hour "+currentHourInt);
			if(tempListItemHour == currentHourInt) {
				hoursLbId.setSelectedItem(listitem);
				break;
			}
		} // for  hours
		
		meridianLbId.setSelectedIndex(currentMeridian);
		*/
		
		
		
	} // defaultSetTime
	
	public void onSelect$campClickedLbId() {
		
		//populate campaignurls
		int count = getCampaignURLModel();
		
		if(count == 0) {

			 urlSetDivId.setVisible(false);
			//noURLExitLbId.setVisible(true); 	 // warn message.
			return;	
		} else {
			  urlSetLbId.setVisible(true); 
			  urlHelpImgId.setVisible(true); 
			  urlSetDivId.setVisible(true);// list box.
			  //noURLExitLbId.setVisible(false);		// warn Message.
		} 
		
		/*urlSetLbId.setModel(listModel);
		urlSetLbId.setItemRenderer(itemRenderer);*/
		if(urlSetLbId.getItemCount() >  0) {
			urlSetLbId.setSelectedIndex(0);
		}

		
		
		
	}//onSelect$campaignsLbId
	
	
	
	public int getCampaignURLModel() {
		try {
			
			if(campClickedLbId.getItemCount() == 0) return 0;
			List list = campaignReportDao.getUrlListByCampaignId(campClickedLbId.getSelectedItem().getLabel(),GetUser.getUserId());
			if(list != null) {
				Components.removeAllChildren(urlSetLbId);
				for (Object object : list) {
					Listitem urlItem = new Listitem(object.toString(), object.toString());
					urlItem.setParent(urlSetLbId);
					
					
				}
				return list.size();
			}else{
				
				return 0;
			}
			
		} catch (Exception e) {
			logger.error("Exception : No URL records exist in DB ", e);
			return 0;
		}
	}
	
	//can be made this at client side
	List<FormMapping> mappingsList ;
	public void onSelect$contactOptInMediumLbId() {
		
		try {
			if(mappingsList == null && userWebFormsLbId.getItemCount() == 0) {
				
				mappingsList =  formMappingDao.findAllByUserId(user.getUserId());
				if(mappingsList != null && mappingsList.size() > 0) {
					for (FormMapping formMapping : mappingsList) {
						
						Listitem item = new Listitem(formMapping.getFormMappingName(), formMapping);
						item.setParent(userWebFormsLbId);
						
					}//for
					
					
				}//if
				
			}//if
			
			
			userWebFormsLbId.setVisible(userWebFormsLbId.getItemCount() > 0 
					&& contactOptInMediumLbId.getSelectedCount() != -1 
					&& contactOptInMediumLbId.getSelectedIndex()==2);
			
			if(userWebFormsLbId.getItemCount() > 0 && userWebFormsLbId.getSelectedCount() == 0) userWebFormsLbId.setSelectedIndex(0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		
		
	}//onSelect$contactOptInMediumLbId
	
	public void onSelect$purchaseAmtOptionsLbId() {
	
		logger.info("purchaseAmtOptionsLbId "+purchaseAmtOptionsLbId.getSelectedItem().getValue());
		if(purchaseAmtOptionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("between")) {
			
			purchaseAmount1DblBxId.setVisible(true);
			
			
		}else{
			
			purchaseAmount1DblBxId.setVisible(false);
			
		}
	
	
	}//onSelect$purchaseAmtOptionsLbId() 

	
	
	
	
	
	private List<MailingList> mailingLists ;
	public void getMailingListsModel() { // setMultiple(true) set to the
												// ListModelList
		try {
			// List list = mailingListDao.findAllByUser(userIdsSet);
			if(mailingLists == null) {
				mailingLists = mailingListDao.findAllByCurrentUser(user.getUserId());
			}
			
			if(mailingLists != null) {
				Listitem item = null;
				Listcell cell = null;
				for (MailingList mailingList : mailingLists) {
					item = new Listitem();
					item.setValue(mailingList);
					cell = new Listcell();
					cell.setParent(item);
					
					cell = new Listcell(mailingList.getListName());
					cell.setParent(item);
					item.setParent(ETMlListsLBId);
					
					
				}
				
				
			}
			
			/*ListModelList tempListModelList = new ListModelList(mailingLists);

			tempListModelList.setMultiple(true);
			
			
			return tempListModelList;*/
		} catch (Exception e) {
			logger.debug("Exception : No records exist in DB " + e);
			return ;
		}
	}
	private List<Campaigns> campaignList ;
	public ListModel getCampaignsListModel() {
		try {
			if(campaignList == null) {
				
				campaignList = campaignsDao.findByUser(user.getUserId());
				
			}
			
			return new ListModelList(campaignList);
		} catch (Exception e) {
			logger.debug("Exception : No records exist in DB " + e);
			return null;
		}
	}

	private List<SMSCampaigns> smsCampaignList ;
	public ListModel getSmsCampaignListModel() {
		try {
			if(smsCampaignList == null) {
				
				smsCampaignList = smsCampaignsDao.findAllByUser(user.getUserId());
				
			}
			if (smsCampaignList != null) {
				return new ListModelList(smsCampaignList);
			} else
				return null;
		} catch (Exception e) {
			logger.debug("Exception : No records exist in DB " + e);
			return null;
		}
	}

	public void onClick$sendEmailCampaignChkBxId() {

		sendEmailCampaignDivId.setVisible(sendEmailCampaignChkBxId.isChecked());
		
		if (!sendEmailCampaignChkBxId.isChecked()) {
	
			campConfigSettngDivId.setVisible(false);
		}
	
	}

	public void onClick$sendSMSCampaignChkBxId() {

		
		
		sendSMSCampaignDivId.setVisible(sendSMSCampaignChkBxId.isChecked());
					
		
	}

	public void onClick$addToOtherListChkBxId() {

		addToOtherListDivId.setVisible(addToOtherListChkBxId.isChecked());
		
		if (addToOtherListChkBxId.isChecked()) {
		Components.removeAllChildren(addToMlLbId);
		
		if(mailingLists == null) {
			
			mailingLists =  mailingListDao.findAllByCurrentUser(user.getUserId());
		}
		// MailingList obj;
		for (MailingList mlList : mailingLists) {

			Listitem li = new Listitem();
			li.setParent(addToMlLbId);
			li.setLabel(mlList.getListName());
			li.setValue(mlList);
		}
		if (addToMlLbId.getItemCount() > 0)
			addToMlLbId.setSelectedIndex(0);
		}
	}

	public void onClick$campaignPreviewTBtnId() {
		try {
			Campaigns campaign = (Campaigns) campaignsLbId.getSelectedItem().getValue();
					
			
			String campHtmlContentStr = campaign.getHtmlText();
			if (campHtmlContentStr != null && !campHtmlContentStr.isEmpty()) {
				Html html = (Html) previewWinId.getFellowIfAny("html");
				
				if(campHtmlContentStr.contains("href='")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
					
				}
				if(campHtmlContentStr.contains("href=\"")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
				}
				
				html.setContent(campHtmlContentStr);
				previewWinId.setVisible(true);
			}else{
				
				MessageUtil.setMessage("There is No content to display.", "red", "top");
				return;
			}
			
		} catch (Exception e) {
			logger.debug(
					"*** Error : Exception occured while displaying preview .***",
					e);
		}
	}

	public void onClick$campClickPreviewToolBtnId() {
		try {
			Campaigns campaign = (Campaigns) campClickedLbId.getSelectedItem().getValue();
					
			
			String campHtmlContentStr = campaign.getHtmlText();
			if (campHtmlContentStr != null && !campHtmlContentStr.isEmpty()) {
				Html html = (Html) previewWinId.getFellowIfAny("html");
				
				if(campHtmlContentStr.contains("href='")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
					
				}
				if(campHtmlContentStr.contains("href=\"")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
				}
				
				html.setContent(campHtmlContentStr);
				previewWinId.setVisible(true);
			}else {
				
				MessageUtil.setMessage("There is No content to display.", "red", "top");
				return;
				
			}
		} catch (Exception e) {
			logger.debug(
					"*** Error : Exception occured while displaying preview .***",
					e);
		}
	}
	
	public void onClick$campOpenPreviewToolBtnId() {
		try {
			Campaigns campaign = (Campaigns) campOpenedLbId.getSelectedItem().getValue();
					
			
			String campHtmlContentStr = campaign.getHtmlText();
			
			
			if (campHtmlContentStr != null && !campHtmlContentStr.isEmpty()) {
				Html html = (Html) previewWinId.getFellowIfAny("html");
				
				if(campHtmlContentStr.contains("href='")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
					
				}
				if(campHtmlContentStr.contains("href=\"")){
					campHtmlContentStr = campHtmlContentStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
				}
				
				html.setContent(campHtmlContentStr);
				previewWinId.setVisible(true);
			}else {
				
					
				MessageUtil.setMessage("There is No content to display.", "red", "top");
				return;
				
					
				
			}
		} catch (Exception e) {
			logger.debug(
					"*** Error : Exception occured while displaying preview .***",
					e);
		}
	}
	
	public void onClick$smsPreviewTBtnId() {
		try {
			SMSCampaigns smsCampaign  = (SMSCampaigns) smsLbId.getSelectedItem().getValue();
			String campHtmlContentStr = smsCampaign.getMessageContent();
			if (campHtmlContentStr != null && !campHtmlContentStr.isEmpty()) {
				Html html = (Html) previewWinId.getFellowIfAny("html");
				html.setContent(campHtmlContentStr);
				previewWinId.setVisible(true);
			}
		} catch (Exception e) {
			logger.debug(
					"*** Error : Exception occured while displaying preview .***",
					e);
		}
	}

	
	private List<UserFromEmailId> userFromEmailIdList;
	
	public void onClick$configTlbBtnId() {
	
		if(userFromEmailIdList == null) {
			
			userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(user.getUserId());
			trigCampFromCbId.appendItem(user.getEmailId());
			trigCampReplyCbId.appendItem(user.getEmailId());
			
			if (userFromEmailIdList.size() > 0) {
				Comboitem item = null;
				for (Object obj : userFromEmailIdList) {
					
					UserFromEmailId userFromEmailId = (UserFromEmailId) obj;
					trigCampFromCbId.appendItem(userFromEmailId.getEmailId());
					trigCampReplyCbId.appendItem(userFromEmailId.getEmailId());
					
					
				}
			}
			if (!(trigCampFromCbId.getItemCount() > 0)) {
				trigCampFromCbId.appendItem("No emails registered.");
				trigCampReplyCbId.appendItem("No emails registered.");
			}

			if ((trigCampFromCbId.getItemCount() > 0) && trigCampFromCbId.getSelectedIndex() == -1) {
				trigCampFromCbId.setSelectedIndex(0);
			}
			
			if ((trigCampReplyCbId.getItemCount() > 0) && trigCampReplyCbId.getSelectedIndex() == -1) {
				trigCampReplyCbId.setSelectedIndex(0);
			}
			
		}
		
			trigCampNameTbId.setText(fromName != null ? fromName : "");
			trigCampFromCbId.setValue(fromEmail != null ? fromEmail : trigCampFromCbId.getValue());
			trigCampReplyCbId.setValue(replEmail != null ? replEmail : trigCampReplyCbId.getValue());
			campConfigSettngDivId.setVisible(!campConfigSettngDivId.isVisible());
			/*if (!campConfigSettngDivId.isVisible()) {
			
		} else {
			campConfigSettngDivId.setVisible(false);
			
		}*/
	}

	public void onClick$MoreOptionsTlBtnId() {
		
		MoreOptionsDivId.setVisible(!MoreOptionsDivId.isVisible());
		/*if (!MoreOptionsDivId.isVisible()) {
			
		} else {
			MoreOptionsDivId.setVisible(false);
			
		}*/
	}

	private String fromName;
	private String fromEmail;
	private String replEmail;
	public void onClick$sendgOptSaveBtnId() {
		
		
		if(trigCampNameTbId.isValid() && Utility.validateFromName(trigCampNameTbId.getValue())){
			fromName = trigCampNameTbId.getText();
		}
		else{
			MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
			trigCampNameTbId.setFocus(true);
			return;
		}
		
		if(trigCampFromCbId.getValue().indexOf('@')<0) {
			MessageUtil.setMessage("Register a 'From Email' to create an email.",
					"color:red", "TOP");
			return;
		}
		else if(!(trigCampFromCbId.getSelectedIndex()==-1)) {
			fromEmail = trigCampFromCbId.getValue();
		
		}
		else {
			MessageUtil.setMessage("Provide valid 'From Email Address'.", "color:red", "TOP");
			trigCampFromCbId.setFocus(true);
			return;
		}
		
		if(trigCampReplyCbId.getValue().indexOf('@')<0) {
			MessageUtil.setMessage("Register a 'Reply Email' to create an email.",
					"color:red", "TOP");
			return;
		}
		else if(!(trigCampReplyCbId.getSelectedIndex()==-1)) {
			replEmail = trigCampReplyCbId.getValue();
		
		}
		else {
			
			MessageUtil.setMessage("Provide valid 'From Email Address'.", "color:red", "TOP");
			trigCampReplyCbId.setFocus(true);
			return;
		}
		
		
		MessageUtil.setMessage("Trigger Campaign settings saved successfully.", "color:blue;");
		campConfigSettngDivId.setVisible(false);
		
		
	}//
	
	public void onClick$sendOptCancelBtnId() {
		
		trigCampNameTbId.setText(fromName != null ? fromName : "");
		trigCampFromCbId.setValue(fromEmail != null ? fromEmail : trigCampFromCbId.getValue());
		trigCampReplyCbId.setValue(replEmail != null ? replEmail : trigCampReplyCbId.getValue());
		campConfigSettngDivId.setVisible(false);
		
		
		
		
		
	}//onClick$sendOptCancelBtnId
	
	
	/*
	 * public void onClick$sendgOptSaveBtnId() {
	 * 
	 * 
	 * 
	 * try { MessageUtil.clearMessage();
	 * 
	 * if(eventTrigger == null) {
	 * MessageUtil.setMessage("Please save the trigger by clicking on save button."
	 * , "red", "top"); return; }
	 * 
	 * if(trigCampNameTbId.getValue().trim().equals("")) {
	 * MessageUtil.setMessage("Campaign 'From' name cannot be empty.", "red",
	 * "top"); return; }
	 * 
	 * eventTrigger.setSelectedCampaignFromName(trigCampNameTbId.getValue());
	 * eventTrigger.setSelectedCampaignFromEmail(trigCampFromCbId.getValue());
	 * eventTrigger.setSelectedCampaignReplyEmail(trigCampReplyCbId.getValue());
	 * 
	 * eventTriggerDao.saveOrUpdate(eventTrigger);
	 * 
	 * MessageUtil.setMessage("Trigger campaign settings saved successfully.",
	 * "green", "top");
	 * 
	 * } catch(Exception e) { logger.error(
	 * "**Exception : Error occured while saving sending campaign settings **"
	 * ,e); } }
	 */

	//private Include triggerPageIncludeId;

	// *******include the triggerPager page for creaing the new Trigger
	// *******//
	/*public void onClick$addTriggerTabId() {
		triggerPageIncludeId.setSrc("/zul/campaign/triggerPage.zul");
	} */
	// onClick$addTriggerTabId

	/*
	 * public void onClick$addNewTbarId() {
	 * 
	 * try {
	 * 
	 * sessionScope.setAttribute("fromPage","campaign/EventTriggerEmail");
	 * Redirect.goTo("campaign/newEventTrigger"); } catch(Exception e) {
	 * logger.debug
	 * ("*** Exception occured while redirecting to create new event page **");
	 * } }
	 */

	// ***********Custome Event related Methods *******************//

	public void onClick$custmEvntSavBtnId() {

		try {
			MessageUtil.clearMessage();
			String custEventNameStr = custEvntTbId.getValue();

			if (custEventNameStr.equals("")
					|| custmEvntDateBxId.getValue() == null) {
				MessageUtil.setMessage(
						"Custom event name or date cannot be left empty.",
						"red", "top");
				return;
			}

			if (!Utility.validateName(custEventNameStr)) {
				MessageUtil.setMessage("Enter valid custom field name. "
						+ "Special characters are not allowed.", "color:red",
						"TOP");
				return;
			}

			TriggerCustomEvent triggerCustomEvent = triggerCustomEventDao
					.findByEventName(custEventNameStr, user.getUserName());

			Calendar custEvntDateCal = custmEvntDateBxId.getServerValue();

			if (triggerCustomEvent != null) {

				try {
					int confirm = Messagebox
							.show("Custom event with the given name already exists. Do you want to replace it?",
									"Custom Event", Messagebox.OK
											| Messagebox.CANCEL,
									Messagebox.QUESTION);
					if (confirm == Messagebox.OK) {
						triggerCustomEvent.setEventDate(custEvntDateCal);
					} else {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
			} else {
				triggerCustomEvent = new TriggerCustomEvent(custEventNameStr,
						custEvntDateCal, GetUser.getUserName());
			}

			if (triggerCustomEvent.getId() == null) {

				triggerCustomEventDaoForDML.saveOrUpdate(triggerCustomEvent);
				logger.debug("** Trigger custom event saved successfully .!!!!!!!! **");

				listModelList.add(triggerCustomEvent);
				MessageUtil.setMessage("Custom event created successfully.",
						"green", "top");
			} else {

				triggerCustomEventDaoForDML.saveOrUpdate(triggerCustomEvent);
				logger.debug("** Trigger custom event saved successfully .!!!!!!!! **");

				existingCustomEventsGrdId.setModel(getCustomEventsModel());
				MessageUtil.setMessage("Custom event modified successfully.",
						"green", "top");
			}

			/*
			 * triggerCustomEventDao.saveOrUpdate(triggerCustomEvent);
			 * logger.debug
			 * ("** Trigger custom event saved successfully .!!!!!!!! **");
			 */

			existingCustomEventsGrdId.setRowRenderer(renderer);

			/*// update trigger page with newly added one.
			if (Sessions.getCurrent().getAttribute("trigPageCtrlObj") != null) {
				NewEventTriggerController trigObj = (NewEventTriggerController) Sessions
						.getCurrent().getAttribute("trigPageCtrlObj");
				trigObj.refreshCustomTrigEventList();
			}*/
		} catch (Exception e) {
			logger.debug(
					"** Exception : Error occured while fetching saving Custom Event **",
					e);
		}

	}

	public ListModelList getCustomEventsModel() {
		try {
			List<TriggerCustomEvent> list = triggerCustomEventDao
					.findAllByUserId(user.getUserName());
			return new ListModelList(list);
		} catch (Exception e) {
			logger.error(
					"** Exception : Error occured while getting custom events**",
					e);
			return null;
		}
	}

	public ListModel getTriggersListModel(int start, int end) {
		try {

			List<EventTrigger> list = eventTriggerDao.findAllByUserId(user
					.getUserId(), start, end,true);
			/*
			 * ListModelList tempListModelList = new ListModelList(list);
			 * tempListModelList.set
			 */
			return new ListModelList(list);
		} catch (Exception e) {
			logger.error(
					"** Exception : Error occured while getting triggers **", e);
			return null;
		}
	}

	/*public ListModel getContactDateMppingsModel() {
		
		try {
			
			if(contDateMapList == null) {
				contDateMapList = posMappingDao.findContactDateMappings(Constants.POS_MAPPING_TYPE_CONTACTS,user.getUserId());
				
			}
			return new ListModelList(contDateMapList);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
		
	}*/
	
	// for list items
	/*
	 * final class MyListItemRenderer1 implements ListitemRenderer{
	 * 
	 * @Override public void render(Listitem li, Object data, int arg2) throws
	 * Exception { // TODO Auto-generated method stub try { if (data instanceof
	 * MailingList) { MailingList tempMl = (MailingList) data;
	 * li.setValue(tempMl); li.setLabel(tempMl.getListName()); } } catch
	 * (Exception e) {
	 * 
	 * logger.error("** Exception: error occured while rendering the data ",e);
	 * }
	 * 
	 * }}
	 */

	final class MyListItemRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem li, Object data, int arg2) throws Exception {

			try {
				//logger.info("rendere is called");
				if (data instanceof Campaigns) {
					Campaigns campaign = (Campaigns) data;
					li.setValue(campaign);
					li.setLabel(campaign.getCampaignName());
				} else if (data instanceof SMSCampaigns) {

					SMSCampaigns smsCampaign = (SMSCampaigns) data;
					li.setValue(smsCampaign);
					li.setLabel(smsCampaign.getSmsCampaignName());
				}else if (data instanceof POSMapping) {

					POSMapping posMapping = (POSMapping) data;
					li.setValue(posMapping);
					li.setLabel(posMapping.getDisplayLabel());
				}else if (data instanceof FormMapping) {

					FormMapping formMapping = (FormMapping) data;
					li.setValue(formMapping);
					li.setLabel(formMapping.getFormMappingName());
				}

				else if (data instanceof MailingList) {
					MailingList tempMl = (MailingList) data;
					li.setValue(tempMl);
					// li.setLabel(tempMl.getListName());

					Listcell lc = new Listcell();
					lc.setParent(li);

					lc = new Listcell(tempMl.getListName());
					lc.setParent(li);

				} else if (data instanceof String) {
					String tempURLStr = (String) data;
					
					li.setValue(tempURLStr);
					li.setLabel(tempURLStr);
				} else if (data instanceof TriggerCustomEvent) {
					TriggerCustomEvent triggerCustomEvent = (TriggerCustomEvent) data;
					li.setValue(triggerCustomEvent);
					li.setLabel(triggerCustomEvent.getEventName());
				}
			} catch (Exception e) {

				logger.error(
						"** Exception: error occured while rendering the data ",
						e);
			}
		}

	}

	private Doublebox loyaltyBalDbl1BxId,layaltyBal2DblBxId,giftBalDbl1BxId,giftBal2DblBxId, loyaltyDiffDbl1BxId, loyaltyDiffDbl2BxId;
	private Textbox eventTriggerNameTbId;
	private Listbox triggerActionLbId, numbersLbId, hoursDaysLbId,loyaltyContactOptionLbId1,loyaltyContactOptionLbId2,
	giftContactOptionLbId2, loyaltyDiffOptionLbId1, loyaltyDiffOptionLbId2;
	private Radiogroup triggerTypeRGId;
	public void onClick$etSaveBtnId() {
		
		try {
			
			MessageUtil.clearMessage();
			
			
			logger.debug("<< Just Entered .. >>");
			
			String newEventNameStr = eventTriggerNameTbId.getValue().trim();
			boolean isEditMode = (eventTrigger != null);
			if(!isEditMode) {
				
				if(newEventNameStr.trim().equals("")) {
					MessageUtil.setMessage("Please provide trigger name.", "red", "top");
					return;
				}
				
				boolean isExist = eventTriggerDao.findByName(newEventNameStr, user.getUserId().longValue());
				if(isExist) {
					
					MessageUtil.setMessage("Trigger name already exist. Please provide another name for this trigger", "red", "top");
					return;
					
				}//if exist
				
				
			}
			
			
			boolean isListConfigured = (ETMlListsLBId.getItemCount() <= 0);
			
			if(isListConfigured) {
				
				MessageUtil.setMessage("Please have atleast one mailing list.", "red", "top");
				return;
			}
			
			isListConfigured = (ETMlListsLBId.getSelectedCount() == 0);
			if(isListConfigured) {
				
				MessageUtil.setMessage("Please select atleast one mailing list.", "red", "top");
				return;
			}
			//logger.debug("&&&"+triggerTypeGbxId.getSelectedItem().getId());
			Radio selectRadio = null;
			selectRadio = triggerTypeRGId.getSelectedItem();
			
			long optionSettings = 0; 
			
			if(selectRadio == null) {
				MessageUtil.setMessage("Please select the trigger type.", "red", "top");
				return;
			}
			
			//TODO
			/*if(enableTargetTimeChkBoxId.isChecked()) {
				
				if(!isSetAnyTargetDay()) {
					MessageUtil.setMessage("Please select atleast one specific target day.", "red", "top");
					return;
					
				}
				
				
			}//if
*/			
			
			
			if(sendSMSCampaignChkBxId.isChecked()) {
				
				SMSSettingsDao smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
				
				
				
				List<SMSSettings> smsSettings = smsSettingsDao.findByUser(user.getUserId().longValue());
				if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType()) && smsSettings == null) {
					
					MessageUtil.setMessage("No SMS settings found for your user account.\n Please contact Admin to enable SMS features.", "color:red;");
					return ;
					
				}
				
				
				if(smsLbId.getItemCount() == 0) {
					
					MessageUtil.setMessage("Please have at least one SMS campaign for event to trigger.", "red", "top");
 					return;
					
				}
				
			}
			
			//sendCampChkBxId,addToMlChkBxId,removeFrCampMlChkBxId
			//TODO need to group sms,Email options individually
			
			if(!sendEmailCampaignChkBxId.isChecked() && !sendSMSCampaignChkBxId.isChecked()
					&& !addToOtherListChkBxId.isChecked() && !removeFrCampMlChkBxId.isChecked()) {
				
				MessageUtil.setMessage("Please select at least one event to trigger.", "red", "top");
				return;
				
			}//if
			
 			if(!(sendEmailCampaignChkBxId.isChecked() 
 					|| sendSMSCampaignChkBxId.isChecked()) &&
 					(addToOtherListChkBxId.isChecked() || removeFrCampMlChkBxId.isChecked())) { //SMS code added
				MessageUtil.setMessage("Please select at least one campaign sending action for event to trigger.", "red", "top");
				return;
			}
 			
 			
 			if(sendEmailCampaignChkBxId.isChecked()) {
 				if(campaignsLbId.getItemCount() == 0) {
 					MessageUtil.setMessage("Please have at least one Email campaign for event to trigger.", "red", "top");
 					return;
 					
 				}
 				
 				
 				Campaigns selCamp = campaignsLbId.getSelectedItem().getValue();
 				if(selCamp == null) {
 					MessageUtil.setMessage("Please select at least one Email campaign for event to trigger.", "red", "top");
 					return;
 					
 				}
 				
 				if(selCamp.getHtmlText() == null || selCamp.getHtmlText().isEmpty()) {
 					
 					MessageUtil.setMessage("Selected Email campaign for event to trigger is not completed.\n Please complete the campaign creation first.", "red", "top");
 					return;
 					
 				}
 				
 				
 			}//if
		
 			Listitem mlItem = null;
 			String dblOptinLsts = "";
 			if(addToOtherListChkBxId.isChecked()) {
 				mlItem = addToMlLbId.getSelectedItem();
				if(mlItem == null) {
					
					MessageUtil.setMessage("Please select the 'To' Mailing list ", "red", "top");
					return;
					
				}//if
				
 			}
 			
 			
 			Set<Listitem> selItems = ETMlListsLBId.getSelectedItems();
 			for (Listitem listitem : selItems) {
 				MailingList selList = (MailingList)listitem.getValue();
 				if(mlItem != null) {
	 				if((selList).getListId().longValue() == ((MailingList)mlItem.getValue()).getListId().longValue()) {
	 					MessageUtil.setMessage("Selected 'To' Mailing list is configured to Event Trigger.Please select any other list.", "red", "top");
	 					return;
	 					
	 					
	 				}
 				}//if
 				
 				if(selList.getCheckDoubleOptin() == true) {
 					
 					if(!dblOptinLsts.isEmpty()) dblOptinLsts += ", ";
 					
 					dblOptinLsts += selList.getListName();
 					
 					
 				}//if
 				
 				
 				
 			}//for
 			
 			
			// Fetch user options based on the radio label .
			// Edit or New Trigger .. if user click save button again instead of new obj saved 
			// one will be used.
 			Calendar cal = Calendar.getInstance();
			 if(!isEditMode) {
				eventTrigger = new EventTrigger();
				eventTrigger.setUsers(user);
				eventTrigger.setTriggerName(newEventNameStr);
				eventTrigger.setTriggerCreatedDate(cal);
			}
            
			 eventTrigger.setTriggerModifiedDate(cal);
			 //configure mls
			Set<MailingList> mlSet = new HashSet<MailingList>();
			Set<Listitem> selMlItems = ETMlListsLBId.getSelectedItems();
			
			MailingList selectedMl = null;
			for (Listitem selMlItem : selMlItems) {
				
				selectedMl = selMlItem.getValue();
				mlSet.add(selectedMl);
				
				
			}
			eventTrigger.setMailingLists(mlSet);
			
			//for input str of ET
			String inputStr = null; 
			
			//set the type of ET and prepare the inputstr accordingly
			if(selectRadio.getId().equalsIgnoreCase("contactDateRbnId")) {
				
				eventTrigger.setTrType(Constants.ET_TYPE_ON_CONTACT_DATE);
				POSMapping mappedDateFiled  = (POSMapping)contactDateLbId.getSelectedItem().getValue();
				if(mappedDateFiled == null) {//either birthday or anniversary
					
					if(contactDateLbId.getSelectedIndex() == 0) inputStr = "c.birth_day";
					else  inputStr = "c.anniversary_day";
					
				}else{
					
					inputStr = "c."+mappedDateFiled.getCustomFieldName().toLowerCase();
					
					
				}
				
				if(dateIsYrIgnoreChkBxId.isChecked()) {
					
					inputStr += "|date:isYearIgnored";
					
				}
				
			} else if(selectRadio.getId().equalsIgnoreCase("optinMediumRbnId")) {
				
				eventTrigger.setTrType(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM);
				inputStr = "c.optin_medium|string:is|"+contactOptInMediumLbId.getSelectedItem().getValue()
							+(userWebFormsLbId.isVisible() ? userWebFormsLbId.getSelectedItem().getLabel() : "");
				
				
			
			}else if(selectRadio.getId().equalsIgnoreCase("dateAddedRbnId")) {
				
				inputStr = "c.created_date";
				eventTrigger.setTrType(Constants.ET_TYPE_ON_CONTACT_ADDED);
				
			}
			else if(selectRadio.getId().equalsIgnoreCase("campaignClickedRbnId")) {
				
				if(!urlSetDivId.isVisible()) {
					MessageUtil.setMessage("Please select a campaign with URL(s).", "red", "top");
					return;
				}
				if(campClickedLbId.getItemCount() == 0) {
					MessageUtil.setMessage("Please create a campaign first. ", "red", "top");
					return;
				}
				eventTrigger.setTrType(Constants.ET_TYPE_ON_CAMPAIGN_CLICKED);
				
				inputStr = "cs.clicks|number:>|0<AND>cs.campaign_id|number:=|"
					+(((Campaigns)campClickedLbId.getSelectedItem().getValue()).getCampaignId().longValue())+
					"<AND>cl.click_Url|string:is|"+urlSetLbId.getSelectedItem().getLabel();
				
				
			} else if(selectRadio.getId().equalsIgnoreCase("campaignOpenRbnId")) {
				
				if(campOpenedLbId.getItemCount() == 0) {
					MessageUtil.setMessage("Please create a campaign first. ", "red", "top");
					return;
				}
				
				Campaigns selCamp = (Campaigns)campOpenedLbId.getSelectedItem().getValue();
				if(selCamp.getHtmlText() == null || selCamp.getHtmlText().isEmpty() ) {
					
					MessageUtil.setMessage("In trigger type,the selected Campaign creation is not completed.You can not configure it.", "red", "top");
					return;
					
					
				}
				
				eventTrigger.setTrType(Constants.ET_TYPE_ON_CAMPAIGN_OPENED);
				inputStr = "cs.opens|number:>|0<AND>cs.campaign_id|number:=|"+
				(selCamp.getCampaignId().longValue());
				
			}else if(selectRadio.getId().equalsIgnoreCase("productRbnId")) {
				
				String product = productTxtBxId.getText().trim();
				String criteria = productCriteriaLBId.getSelectedItem().getLabel();
				String options = productOptionsLbId.getSelectedItem().getValue(); 
				if(product.isEmpty()) {
					MessageUtil.setMessage("Please give one of your "+criteria+"(s)", "red", "top");
					return;
					
				}
				inputStr = productCriteriaLBId.getSelectedItem().getValue()+"|string:"+options+"|"+product;
				eventTrigger.setTrType(Constants.ET_TYPE_ON_PRODUCT);
				
			}else if(selectRadio.getId().equalsIgnoreCase("amountRbnId")) {
				
				Double amount = purchaseAmountDblBxId.getValue();
				if(amount == null ){
					
					MessageUtil.setMessage("Please specify the purchase amount.", "red", "top");
					return;
					
				}
				Double amount1 = null;
				
				if(purchaseAmount1DblBxId.isVisible()) {
					
					amount1 = purchaseAmount1DblBxId.getValue();
					if(amount1 == null) {
						MessageUtil.setMessage("Please specify the range of purchase amount ", "red", "top");
						return;
						
					}
					if(amount1 <= amount) {
						
						MessageUtil.setMessage("Amount one should be less than the amount two.", "red", "top");
						return;
						
					}
					
					
				}
				
				
				//inputStr = "sum((rs.sales_price+rs.tax)*rs.quantity )|number:"+purchaseAmtOptionsLbId.getSelectedItem().getValue()+"|"+amount.doubleValue()+(amount1 != null ? "|"+amount1.doubleValue() : "");
				inputStr = "sum(rs.sales_price * rs.quantity + rs.tax - if(rs.discount is null,0, rs.discount))|number:"+purchaseAmtOptionsLbId.getSelectedItem().getValue()+"|"+amount.doubleValue()+(amount1 != null ? "|"+amount1.doubleValue() : "");
				eventTrigger.setTrType(Constants.ET_TYPE_ON_PURCHASE_AMOUNT);
				
			}else if(selectRadio.getId().equalsIgnoreCase("onPurchaseRbnId")) {
				
				inputStr = "rs.sales_date";
				eventTrigger.setTrType(Constants.ET_TYPE_ON_PURCHASE);
			}else if(selectRadio.getId().equalsIgnoreCase("loyaltyRbnId")){
				Double loyaltyBal1 = null;
				if(loyaltyBalDbl1BxId.isVisible()){
					loyaltyBal1 = loyaltyBalDbl1BxId.getValue();
					/*if(loyaltyBal1 == null){
						MessageUtil.setMessage("Please specify the loyalaty balance ", "red", "top");
						return;
					}*/
					
				}
				Double loyaltyBal2 = null;
				if(layaltyBal2DblBxId.isVisible()) {
					
					loyaltyBal2 = layaltyBal2DblBxId.getValue();
					if(loyaltyBal1 != null && loyaltyBal2 == null) {
						MessageUtil.setMessage("Loyalty balance value cannot be left empty.", "red", "top");
						layaltyBal2DblBxId.setFocus(true);
						return;
					}
					
					if(loyaltyBal1 == null && loyaltyBal2 != null) {
						MessageUtil.setMessage("Loyalty balance value cannot be left empty.", "red", "top");
						loyaltyBalDbl1BxId.setFocus(true);
						return;
					}
					if(loyaltyBal2 != null && loyaltyBal1 != null) {
						if(loyaltyBal2 <= loyaltyBal1) {

							MessageUtil.setMessage("Loyalty balance one should be less than the Loyalty balance two.", "red", "top");
							return;

						}
					}
					
				}
				if(layaltyIssueChkId.isChecked() == false &&
						layaltyRedeemChkId.isChecked() == false && layaltyAdjustChkId.isChecked() == false){
					MessageUtil.setMessage("Please check any one of loyalty balance consideration.", "red", "top");
					return;
				}
				
				
				
				String  loyaltyOptionTypeStr= "";
				if(loyaltyContactOptionLbId1.getSelectedIndex() == 0){
					loyaltyOptionTypeStr = loyaltyContactOptionLbId1.getSelectedItem().getValue();
				}else {
					loyaltyOptionTypeStr = loyaltyContactOptionLbId1.getSelectedItem().getValue();
				}
				
				if(loyaltyBal1 != null) {
					inputStr = loyaltyOptionTypeStr+"|number:"+loyaltyContactOptionLbId2.getSelectedItem().getValue()+
							"|"+(loyaltyBal1 != null ?loyaltyBal1.doubleValue() : "IS NULL") +(loyaltyBal2 != null ? "|"+loyaltyBal2.doubleValue() : "");

				}
				int trType = Constants.ET_TYPE_ON_LOYALTY;
				//only Issuance check box
				if(layaltyIssueChkId.isChecked()) {
					logger.debug("Issueancebefore trType ::"+trType);
					logger.debug("Loyalty Checked get Value  is  ::"+layaltyIssueChkId.getValue());
					int valueStr = Integer.parseInt(layaltyIssueChkId.getValue().toString());
					trType = trType | valueStr;
					logger.debug("after trType ::"+trType);
				}
				//only Redemption cechk box
				if(layaltyRedeemChkId.isChecked()) {
					logger.debug("Redeemption trType ::"+trType);
					logger.debug("Loyalty redemtion get Value  is  ::"+layaltyRedeemChkId.getValue());
					int valueStr = Integer.parseInt(layaltyRedeemChkId.getValue().toString());
					
					trType = trType | valueStr;
				}
				//only Adjustment check box
				if(layaltyAdjustChkId.isChecked()) {
					logger.debug("Adjust trType ::"+trType);
					logger.debug("Loyalty Adjustment get Value  is  ::"+layaltyAdjustChkId.getValue());
					int valueStr = Integer.parseInt(layaltyAdjustChkId.getValue().toString());
					trType = trType | valueStr;
				}
				logger.debug("final trType is  ::"+trType);
				eventTrigger.setTrType(trType);
			}else if(selectRadio.getId().equalsIgnoreCase("differenceRbnTd")) {
				

				Double loyaltyBal1 = null;
				if(loyaltyDiffDbl1BxId.isVisible()){
					loyaltyBal1 = loyaltyDiffDbl1BxId.getValue();
					/*if(loyaltyBal1 == null){
						MessageUtil.setMessage("Please specify the loyalaty balance ", "red", "top");
						return;
					}*/
					
				}
				Double loyaltyBal2 = null;
				if(loyaltyDiffDbl2BxId.isVisible()) {
					
					loyaltyBal2 = loyaltyDiffDbl2BxId.getValue();
					if(loyaltyBal1 != null && loyaltyBal2 == null) {
						MessageUtil.setMessage("Loyalty Difference value cannot be left empty.", "red", "top");
						loyaltyDiffDbl2BxId.setFocus(true);
						return;
					}
					
					if(loyaltyBal1 == null && loyaltyBal2 != null) {
						MessageUtil.setMessage("Loyalty Difference value cannot be left empty.", "red", "top");
						loyaltyDiffDbl1BxId.setFocus(true);
						return;
					}
					if(loyaltyBal2 != null && loyaltyBal1 != null) {
						if(loyaltyBal2 <= loyaltyBal1) {

							MessageUtil.setMessage("Loyalty Difference one should be less than the Loyalty Difference two.", "red", "top");
							return;

						}
					}
					
				}
				if(layaltyDiffIssueChkId.isChecked() == false ){
					MessageUtil.setMessage("Please check any one of loyalty Difference consideration.", "red", "top");
					return;
				}
				
				
				
				String  loyaltyOptionTypeStr= "";
				if(loyaltyDiffOptionLbId1.getSelectedIndex() == 0){
					loyaltyOptionTypeStr = loyaltyDiffOptionLbId1.getSelectedItem().getValue();
				}else {
					loyaltyOptionTypeStr = loyaltyDiffOptionLbId1.getSelectedItem().getValue();
				}
				
				if(loyaltyBal1 != null) {
					inputStr = loyaltyOptionTypeStr+"|number:"+loyaltyDiffOptionLbId2.getSelectedItem().getValue()+
							"|"+(loyaltyBal1 != null ?loyaltyBal1.doubleValue() : "IS NULL") +(loyaltyBal2 != null ? "|"+loyaltyBal2.doubleValue() : "");

				}
				int trType = Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE;
				//only Issuance check box
				if(layaltyDiffIssueChkId.isChecked()) {
					logger.debug("Issueancebefore trType ::"+trType);
					logger.debug("Loyalty Checked get Value  is  ::"+layaltyDiffIssueChkId.getValue());
					int valueStr = Integer.parseInt(layaltyDiffIssueChkId.getValue().toString());
					trType = trType | valueStr;
					logger.debug("after trType ::"+trType);
				}
				//only Redemption cechk box
				
				logger.debug("final trType is  ::"+trType);
				eventTrigger.setTrType(trType);
			
			}
			else if(selectRadio.getId().equalsIgnoreCase("giftRbnId")){
				Double giftBal1 = null;
				if(giftBalDbl1BxId.isVisible()){
					giftBal1 = giftBalDbl1BxId.getValue();
					/*if(giftBal1 == null){
						MessageUtil.setMessage("Please specify the gift balance ", "red", "top");
						return;
					}*/
				}
				Double giftBal2 = null;
				if(giftBal2DblBxId.isVisible()) {
					
					giftBal2 = giftBal2DblBxId.getValue();
					/*if(giftBal2 == null) {
						MessageUtil.setMessage("Please specify the range of gift balance ", "red", "top");
						return;
						
					}*/
					
					if(giftBal1 != null && giftBal2 == null) {
						MessageUtil.setMessage("Gift balance value cannot be left empty.", "red", "top");
						giftBal2DblBxId.setFocus(true);
						return;
					}
					
					if(giftBal1 == null && giftBal2 != null) {
						MessageUtil.setMessage("Gift balance value cannot be left empty.", "red", "top");
						giftBalDbl1BxId.setFocus(true);
						return;
					}
					
					if(giftBal2 != null && giftBal1 != null) {
						if(giftBal2 <= giftBal1) {

							MessageUtil.setMessage("Gift balance one should be less than the Gift balance two.", "red", "top");
							return;

						}
					}
					
				}
				
				
				if(giftTopUpChkId.isChecked() == false &&
						giftRedeemChkId.isChecked() == false){
					MessageUtil.setMessage("Please check any one of gift balance consideration.", "red", "top");
					return;
				}
				
				
				
				//String  giftOptionTypeStr= "";
				/*if(giftContactOptionLbId1.getSelectedIndex() == 0){
					giftOptionTypeStr = "loyalty.giftamount_balance";
				}*/
				
				if(giftBal1 != null) {
				
					inputStr = "lc.gift_balance|number:"+giftContactOptionLbId2.getSelectedItem().getValue()+
						"|"+(giftBal1 != null ?giftBal1.doubleValue() : "IS NULL") +(giftBal2 != null ? "|"+giftBal2.doubleValue() : "");
				}
				
				int trType = Constants.ET_TYPE_ON_GIFT;
				//only Issuance check box
				if(giftTopUpChkId.isChecked()) {
					logger.debug("Issuancebefore trType ::"+trType);
					logger.debug("Gift Checked get Value  is  ::"+giftTopUpChkId.getValue());
					int valueStr = Integer.parseInt(giftTopUpChkId.getValue().toString());
					trType = trType | valueStr;
					logger.debug("after trType ::"+trType);
				}
				//only Redemption cechk box
				if(giftRedeemChkId.isChecked()) {
					logger.debug("Redeemption trType ::"+trType);
					logger.debug("Gift redemption get Value  is  ::"+giftRedeemChkId.getValue());
					int valueStr = Integer.parseInt(giftRedeemChkId.getValue().toString());
					
					trType = trType | valueStr;
				}
				
				logger.debug("final trType is  ::"+trType);
				eventTrigger.setTrType(trType);
			}
			eventTrigger.setInputStr(inputStr);
			// Capture trigger actions.<this is fine>
			long numberVal = 0l;
			long mins = 0l;
			long minOffset = 0l;
			
			if(triggerActionLbId.getSelectedItem().getLabel().equalsIgnoreCase("AFTER")) {   
				
				numberVal = Long.parseLong((String)numbersLbId.getSelectedItem().getValue());
				mins = Long.parseLong((String)hoursDaysLbId.getSelectedItem().getValue());
				//logger.info("monutes offset val ::"+(numberVal*mins));
				minOffset = (numberVal * mins);
				//eventTrigger.setMinutesOffset(numberVal * mins);
			} else if(triggerActionLbId.getSelectedItem().getLabel().equalsIgnoreCase("BEFORE")) {
				
				numberVal = Long.parseLong((String)numbersLbId.getSelectedItem().getValue());
				mins = Long.parseLong((String)hoursDaysLbId.getSelectedItem().getValue());
				minOffset = (-numberVal * mins);
				//eventTrigger.setMinutesOffset(-numberVal * mins);	// minus value for BEFORE OPTION				
			} 
			
			eventTrigger.setMinutesOffset(minOffset);
			eventTrigger.setTargetTime(null);
			eventTrigger.setTargetDaysFlag(null);
			int trType = eventTrigger.getTrType();
			
			//enableTargetTimeChkBoxId.isChecked() && 
			
			//logger.info("timerListBoxId.isVisible() ::"+timerListBoxId.isVisible());
			if(	(minOffset >= 1440 || minOffset <= -1440 ) || (minOffset == 0l &&  timerListBoxId.isVisible()) ) {
				
				int targetHour = Integer.parseInt((String)timerListBoxId.getSelectedItem().getValue());
				
				
				/*int targetHour = Integer.parseInt((String)hoursLbId.getSelectedItem().getLabel());
				int targetMinutes = Integer.parseInt((String)mintsLbId.getSelectedItem().getLabel());
				if(Integer.parseInt((String)meridianLbId.getSelectedItem().getValue()) == 1 ){
					
					targetHour += 12;
				}*/
				
				
				Date targetdDate = new Date();
				targetdDate.setHours(targetHour);
				
				
				
				//Date targetdDate = timeBoxId.getValue();
				logger.info("Target time for the event trigger======"+targetdDate+" target hour"+targetHour);
			
				eventTrigger.setTargetTime(targetdDate);
				
				if(targetDaysDivId.isVisible()) {
					//logger.info("is target div visible ???????/");
					String targetDay = getTargetDay();
					
					eventTrigger.setTargetDaysFlag(targetDay);
					
				}//if
				
			}//if
			/*else{
				
				eventTrigger.setTargetTime(null);
				eventTrigger.setTargetDaysFlag(null);
				
			}*/
			
			
			// capture when triggered.
			if(sendEmailCampaignChkBxId.isChecked()) {
				
				if(campaignsLbId.getItemCount() == 0) {
					MessageUtil.setMessage("Please create an email campaign for this trigger.", "red", "top");
					return;
				}else if(campaignsLbId.getSelectedItems() == null) {
					MessageUtil.setMessage("Please select an email campaign for this trigger.", "red", "top");
					return;
				}
				
				Campaigns Camp = (Campaigns)campaignsLbId.getSelectedItem().getValue();
				if(Camp.getHtmlText() == null || Camp.getHtmlText().isEmpty() ) {
					
					MessageUtil.setMessage("The selected campaign creation is not completed.You can not configure it for Trigger Action.", "red", "top");
					return;
					
					
				}
				
				
				optionSettings += Constants.ET_SEND_CAMPAIGN_FLAG;
				
				
				
				eventTrigger.setCampaignId(((Campaigns)campaignsLbId.getSelectedItem().getValue()).getCampaignId());
				
				if(fromEmail != null) eventTrigger.setSelectedCampaignFromEmail(fromEmail);
				if(replEmail != null) eventTrigger.setSelectedCampaignReplyEmail(replEmail);
				if(fromName != null) eventTrigger.setSelectedCampaignFromName(fromName);
				
				
			} 
			if(sendSMSCampaignChkBxId.isChecked()) {
				if(smsLbId.getItemCount() == 0) {
					MessageUtil.setMessage("Please create a SMS campaign for this trigger.", "red", "top");
					return;
				}else if(smsLbId.getSelectedItems() == null) {
					MessageUtil.setMessage("Please select a SMS campaign for this trigger.", "red", "top");
					return;
				}
//					logger.info("========"+sendSMSCampaignsLbId.getItemCount());
				optionSettings += Constants.ET_SEND_SMS_CAMPAIGN_FLAG;
				eventTrigger.setSmsId(((SMSCampaigns)smsLbId.getSelectedItem().getValue()).getSmsCampaignId());
				
				
			}
			// Add contacts to Other ML's
			if(addToOtherListChkBxId.isChecked()) {
				optionSettings += Constants.ET_ADD_CONTACTS_TO_ML_FLAG;
				
			/*	Set<MailingList> tempSet = new HashSet<MailingList>();
				Set addToItemsSet  = addToMlLbId.getSelectedItems();
				Iterator it = addToItemsSet.iterator();
				
				while(it.hasNext()) {
					tempSet.add((MailingList)((Listitem)it.next()).getValue());
				}*/
				 mlItem = addToMlLbId.getSelectedItem();
			/*	if(mlItem == null) {
					
					MessageUtil.setMessage("Please select the 'To' Mailing list ", "red", "top");
					return;
					
				}//if
*/				
				eventTrigger.setAddTriggerContactsToMl((MailingList)mlItem.getValue());
				
				// if Contacts exsits in ML 
				/*if(ifContExistsRbxId.getSelectedIndex() != 0) { // 0 for Ignore Contact
					if(ifContExistsRbxId.getSelectedIndex() == 1) { 	// 1 for merge Contact
						optionSettings +=  Constants.ET_MERGE_IF_CONTACTS_EXISTS;
						if(ignoreCustFieldsCbxId.isChecked()) { 	// Ignore cust fields ?
							optionSettings +=  Constants.ET_MERGE_CUSTOMFIELDS;
						} 
					} else if (ifContExistsRbxId.getSelectedIndex() == 2) { 	// 2 for Replace contact
						optionSettings += Constants.ET_OVERWRITE_IF_CONTACTS_EXISTS;
					}
				}*/
			}
			
			//  Added for campaign categories
			if(user.getSubscriptionEnable()){
				
				if(!categoryLbId.isDisabled()) {
					
					if(categoryLbId.getSelectedItem().getLabel().equals("Select Category") || categoryLbId.getSelectedItem().getLabel().isEmpty() ){
						
						MessageUtil.setMessage("Please select campaign category.", "color:red", "TOP");
						return;
						
					}
					
				}
				
				MessageUtil.setMessage("This campaign category will override email campaign setting.", "color:blue", "TOP");	
			}
			Long categoryName =null;
			Listitem catgoryItem = categoryLbId.getSelectedItem();
			
			if(catgoryItem.getIndex() != 0) {
				
				Object catObj = catgoryItem.getValue();
				
				if(catObj instanceof UserCampaignCategories){
					
					UserCampaignCategories userCampaignCategories = (UserCampaignCategories)catObj;
					
					categoryName =userCampaignCategories.getId();
					
				}else{
					 
					categoryName = Constants.CAMP_CATEGORY_TRANSACTIONAL_ID;
				}
						
				
				
			}
		
			eventTrigger.setCampCategory(categoryName);
			
			
			// Remove from Campaign Ml
			if(removeFrCampMlChkBxId.isChecked()) {
				optionSettings += Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG;
			}
			
			// Check if the trigger is active or not.
			if(triggerStatusChkBxId.isChecked()) { 
				optionSettings += Constants.ET_TRIGGER_IS_ACTIVE_FLAG;
			}	
			
			eventTrigger.setOptionsFlag(optionSettings);
			
			boolean isActive = triggerStatusChkBxId.isChecked();
			
			try {
				int confirm =  Messagebox.show(
						"Are you sure you want to create/modify the event trigger with status "+(isActive ? "'Active'" : "'In Active'") +"? ", "Create/Modify Event", Messagebox.OK
						| Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(!dblOptinLsts.isEmpty()) {
					
					confirm =  Messagebox.show("Selected mailing list(s) : "+dblOptinLsts+"  enabled with double-opt in contacts " +
							"may not reach in time. Are you sure you want to continue save?","Create/Modify Event", Messagebox.OK
							| Messagebox.CANCEL, Messagebox.QUESTION );
					
				}
				
				if(confirm != 1) {
					
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
				return;
			}

			
			// Create Event Trigger bean and Save it.
			if(eventTrigger.getId() != null) {
				eventTriggerDao.getHibernateTemplate().merge(eventTrigger);
				//eventTriggerDao.saveOrUpdate(eventTrigger);
				MessageUtil.setMessage("Trigger modified successfully.", "green");

			}  else {
				
				//eventTriggerDao.saveOrUpdate(eventTrigger);
				eventTriggerDaoForDML.saveOrUpdate(eventTrigger);
				isEditMode = true;
				
				
				MessageUtil.setMessage("Trigger saved successfully.", "green");
			}	
			eventTriggerNameTbId.setDisabled(true);
			logger.debug("*********&&& OPTIONS FLAG VAL :" + optionSettings);
			logger.debug("<< Event Trigger Saved Successfully >>");
			//MessageUtil.setMessage("Trigger saved successfully.", "green", "top");
			 
			eventTrigger =null;
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.CAMPAIGN_EVENT_TRIGGER_EMAIL);

		} catch (Exception e) {
			logger.error("** Error : Exception while saving the new Event Trigger. ** ",e);
		}
		
		
		
	}//onClick$etSaveBtnId
	
private Label catregiryLblId;
private Div categoryDivId;

	public void onSelect$campaignsLbId() {

		Campaigns selCamp = campaignsLbId.getSelectedItem().getValue();
			
			if(selCamp.getHtmlText() == null || selCamp.getHtmlText().isEmpty()) {
				
				MessageUtil.setMessage("Selected Email campaign for event to trigger is not completed.\n Please complete the campaign creation first.", "red", "top");
				return;
				
			}
		
	}
	
	private void onSelect$campOpenedLbId() {
		
		
		Campaigns selCamp = campOpenedLbId.getSelectedItem().getValue();
		
		
		if(selCamp.getHtmlText() == null || selCamp.getHtmlText().isEmpty()) {
			
			MessageUtil.setMessage("Selected Email campaign is not completed.\n Please complete the campaign creation first.", "red", "top");
			return;
			
		}
		
		
	}
	
	public String getTargetDay() {
		
		String targetDay = Constants.STRING_NILL;
		
		String comma = Constants.DELIMETER_COMMA;
		
		if(ignoreSunChkBoxId.isChecked()) {
			if(!targetDay.isEmpty()) targetDay += comma;  targetDay += Integer.parseInt((String)ignoreSunChkBoxId.getValue()); 
		}
		if(ignoreMonChkBoxId.isChecked()){
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreMonChkBoxId.getValue()); 
		}
		if(ignoreTueChkBoxId.isChecked()) {
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreTueChkBoxId.getValue()); 
		}
		if(ignoreWedChkBoxId.isChecked()) {
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreWedChkBoxId.getValue()); 
		}
		if(ignoreThurChkBoxId.isChecked()) {
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreThurChkBoxId.getValue()); 
		}
		if(ignoreFriChkBoxId.isChecked()){
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreFriChkBoxId.getValue()); 
		}
		if(ignoreSatChkBoxId.isChecked()) {
			if(!targetDay.isEmpty()) targetDay += comma; targetDay += Integer.parseInt((String)ignoreSatChkBoxId.getValue()); 
		}
		
		return targetDay;
		
	}
	
	public boolean isSetAnyTargetDay() {
		
		boolean isCheckedAtleastOne = false;
		
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreSunChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreMonChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreTueChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreWedChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreThurChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreFriChkBoxId.isChecked());
		isCheckedAtleastOne = (isCheckedAtleastOne || ignoreSatChkBoxId.isChecked());
		
		
		return isCheckedAtleastOne;
		
	}//isSetAnyTargetDay()
	
	public void onClick$cancelBtnId() {
		try {
			
			logger.debug("---just entered---");
			//Tabbox tabbox = (Tabbox)Utility.getComponentById("triggerTabbxId");
			//logger.debug("tabbox val is :"+tabbox);
			//tabbox.setSelectedIndex(0);
			sessionScope.removeAttribute("eventTriggerObj");
			eventTrigger = null;
			triggerTabbxId.setSelectedTab(triggersTabId);
		} catch (Exception e) {
			logger.error("Exception : Error occured while doing",e);
		}
	}//onClick$cancelBtnId
	
	
	
	
public void onSelect$hoursDaysLbId() {
		
		Listitem tempItem = null;
		if(numbersLbId.getItemCount() > 0) Components.removeAllChildren(numbersLbId);
		
		
		
		
		if(hoursDaysLbId.getSelectedItem().getId().equals("hoursListItemId") ) {
			
			for(int i=1; i<=24 ; i++) {
				if(i== 0) {
					tempItem = new Listitem("0(Immediately)" ,""+i);
				}else {
					tempItem = new Listitem(""+i , ""+i);
				}
				tempItem.setParent(numbersLbId);
			}
			
			targetTimeLblId.setVisible(false);
			timerListBoxId.setVisible(false);
			targetDayLblId.setVisible(false);
			targetDaysDivId.setVisible(false);
			
		}else if(hoursDaysLbId.getSelectedItem().getId().equals("daysListItemId") ) {
			
			for(int i=1; i<=31 ; i++) {
				if(i== 0) {
					tempItem = new Listitem("0(Immediately)" ,""+i);
				}else {
					tempItem = new Listitem(""+i , ""+i);
				}
				tempItem.setParent(numbersLbId);
			}
			
			
			targetTimeLblId.setVisible(!onListItemId.isSelected());
			timerListBoxId.setVisible(!onListItemId.isSelected());
			targetDayLblId.setVisible(!onListItemId.isSelected() && !onDayListItemId.isSelected());
			targetDaysDivId.setVisible(!onListItemId.isSelected() && !onDayListItemId.isSelected());
			
			if(timerListBoxId.isVisible()) {
				
				timerListBoxId.setSelectedIndex(0);
				setCheckForAllSelTargetDays("1,2,3,4,5,6,7");
			}

			
		}
		if(numbersLbId.getSelectedItem() == null) {
			numbersLbId.setSelectedIndex( 0);
		}
//		numbersLbId
	} //onSelect$hoursDaysLbId

	
	
	
	
	//private boolean isEditMode;
	private Radio contactDateRbnId, optinMediumRbnId, 
	campaignOpenRbnId, campaignClickedRbnId,dateAddedRbnId, amountRbnId, onPurchaseRbnId, productRbnId,loyaltyRbnId,giftRbnId, differenceRbnTd;
	private List<POSMapping> contDateMapList;
	
	public void onSelect$addTriggerTabId() {
		
		eventTrigger = (EventTrigger)sessionScope.removeAttribute("eventTriggerObj");
		
		eventTriggerNameTbId.setText("");
		eventTriggerNameTbId.setDisabled(false);
		
		triggerStatusChkBxId.setChecked(true);
		
		trigCampNameTbId.setText(Constants.STRING_NILL);
		campConfigSettngDivId.setVisible(false);
		
		//enableTargetTimeChkBoxId.setChecked(false);
		
		//defaultSetTime();
		
		
		
		
		fromEmail = null;
		fromName = null;
		replEmail = null;
		
		
		/*Clients.evalJavaScript("enableTargetTimerComponentsDiv(zk.Widget.$('$enableTargetTimeChkBoxId'),true )");
		*/
		if(contDateMapList == null) {
			
			contDateMapList = posMappingDao.findContactDateMappings(Constants.POS_MAPPING_TYPE_CONTACTS,user.getUserId());
			
			if(contDateMapList != null && contDateMapList.size() > 0) {
				for (POSMapping dateMapping : contDateMapList) {
					Listitem item = new Listitem(dateMapping.getDisplayLabel(), dateMapping);
					item.setParent(contactDateLbId);
				}
				
			}//if
		}//if
		
		ETMlListsLBId.setSelectedIndex(-1);
		/*if(contDateMapList == null) {
			contDateMapList = posMappingDao.findContactDateMappings(Constants.POS_MAPPING_TYPE_CONTACTS,
					user.getUserId());
		}
		if(contDateMapList != null )	{
			
			Listitem conDateMappingItem = null;
			String contactField = null;
			
			for (POSMapping posMapping : contDateMapList) {
				
				conDateMappingItem = new Listitem(posMapping.getDisplayLabel(), posMapping);
				contactDateLbId.appendChild(conDateMappingItem);
				
			}//for
			
		}//if
*/		triggerTypeRGId.setSelectedItem(contactDateRbnId);
		contactDateRbnId.setChecked(true);
		doToggleRadioBtn(contactDateRbnId);
		//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$contactDateRbnId'), true)");
		
		
		//Clients.evalJavaScript("enableAfterBeforeDivComponents( (zk.Widget.$('$triggerActionLbId')).getChildAt("+0+"))");
		
		hoursDaysLbId.setSelectedIndex(1);
		//Clients.evalJavaScript("enableTargetTimer(zk.Widget.$('$daysListItemId'), true)");
		onSelect$hoursDaysLbId();
		
		triggerActionLbId.setSelectedIndex(0);
		onSelect$triggerActionLbId();
		
		
		timerListBoxId.setSelectedIndex(0);
		ignoreSunChkBoxId.setChecked(true);
		ignoreMonChkBoxId.setChecked(true);
		ignoreTueChkBoxId.setChecked(true);
		ignoreWedChkBoxId.setChecked(true);
		ignoreThurChkBoxId.setChecked(true);
		ignoreFriChkBoxId.setChecked(true);
		ignoreSatChkBoxId.setChecked(true);
		
		
		
		sendEmailCampaignChkBxId.setChecked(false);
		sendSMSCampaignChkBxId.setChecked(false);
		addToOtherListChkBxId.setChecked(false);
		removeFrCampMlChkBxId.setChecked(false);
		MoreOptionsDivId.setVisible(false);
		sendEmailCampaignDivId.setVisible(false);
		sendSMSCampaignDivId.setVisible(false);
		addToOtherListDivId.setVisible(false);
		campConfigSettngDivId.setVisible(false);
		
		purchaseAmountDblBxId.setValue(null);
		purchaseAmount1DblBxId.setValue(null);
		purchaseAmtOptionsLbId.setSelectedIndex(0);
		
		productCriteriaLBId.setSelectedIndex(0);
		productOptionsLbId.setSelectedIndex(0);
		productTxtBxId.setText(Constants.STRING_NILL);
		
		contactDateLbId.setSelectedIndex(0);
		contactOptInMediumLbId.setSelectedIndex(0);
		if(userWebFormsLbId.getItemCount()>0) userWebFormsLbId.setSelectedIndex(0);
		userWebFormsLbId.setVisible(false);
		if(campOpenedLbId.getItemCount() > 0){
			campOpenedLbId.setSelectedIndex(0);
			//onSelect$campOpenedLbId();
		}
		if(campClickedLbId.getItemCount() > 0){
			campClickedLbId.setSelectedIndex(0);
			onSelect$campClickedLbId();
		}
		if(campaignsLbId.getItemCount() > 0) campaignsLbId.setSelectedIndex(0);
		if(smsLbId.getItemCount() > 0) smsLbId.setSelectedIndex(0);
		if(addToMlLbId.getItemCount() > 0) addToMlLbId.setSelectedIndex(0);
		
		
		
	}
	
	
	
	public void doEdittriggerSettings() {
		
		try {
			//onSelect$hoursDaysLbId();
			timerListBoxId.setSelectedIndex(0);
			
			EventTrigger editEventTrigger = null;
			eventTrigger = 	(EventTrigger)sessionScope.getAttribute("eventTriggerObj");	
			
			editEventTrigger = eventTrigger;
			
			
			sessionScope.removeAttribute("eventTriggerObj");
				
			
			
			//logger.info("eidt mode ::"+isEditMode+" object "+editEventTrigger);
			
			
			
			eventTriggerNameTbId.setValue(editEventTrigger.getTriggerName());
			eventTriggerNameTbId.setDisabled(true);
			
			campConfigSettngDivId.setVisible(false);
			
			if(ETMlListsLBId.getItemCount() > 0) {
				int index = 0;
				ETMlListsLBId.clearSelection();
				List<MailingList> configuredList = mailingListDao.findAllTriggerLists(editEventTrigger.getId());
					/*Set<MailingList> EtMlSet = editEventTrigger.getMailingLists();
					Iterator<MailingList> mlIt = EtMlSet.iterator();
					*/
					List<Listitem> itemsList = ETMlListsLBId.getItems();
					int i=0;
					for (MailingList etMl : configuredList) {
						
						for (Listitem listitem : itemsList) {
						
							MailingList ml = listitem.getValue();
							
							//logger.info("ml ::"+ml+" etML ::"+etMl);
							if(ml.getListId().longValue() == etMl.getListId().longValue()) {
								ETMlListsLBId.addItemToSelection(listitem);
							}
							
						}//for
						
						
					}
					
				
					/*	while(mlIt.hasNext()) {
							
							MailingList etMl = mlIt.next();
							for (Listitem listitem : itemsList) {
								MailingList ml = listitem.getValue();
								
								//logger.info("ml ::"+ml+" etML ::"+etMl);
								if(ml.getListId().longValue() == etMl.getListId().longValue()) {
									
									ETMlListsLBId.addItemToSelection(listitem);
								}
								
							}//for
							
							
						}//while ml
*/				
				//if(ETMlListsLBId.getSelectedCount() == 0) ETMlListsLBId.setSelectedIndex(0);
			}

			//to get contact date type mapped fields
			//decide about the contact's b'day and anniversary
			/*List<POSMapping> contDateMapList = posMappingDao.findContactDateMappings(Constants.POS_MAPPING_TYPE_CONTACTS,
												user.getUserId());*/
			
			int triggerType = editEventTrigger.getTrType();
			String inputStr = editEventTrigger.getInputStr();
				
			if((triggerType & Constants.ET_TYPE_ON_CONTACT_DATE) == Constants.ET_TYPE_ON_CONTACT_DATE) {
				
				if(contDateMapList == null) {
					
					contDateMapList = posMappingDao.findContactDateMappings(Constants.POS_MAPPING_TYPE_CONTACTS,user.getUserId());
					
					if(contDateMapList != null && contDateMapList.size() > 0) {
						for (POSMapping dateMapping : contDateMapList) {
							Listitem item = new Listitem(dateMapping.getDisplayLabel(), dateMapping);
							item.setParent(contactDateLbId);
						}
						
					}//if
				}//if
				
				triggerTypeRGId.setSelectedItem(contactDateRbnId);	
				
				String[] tokenArr = inputStr.split("\\|");
				String contactField = tokenArr[0].substring(2);
				if(tokenArr.length > 1) {
					
					dateIsYrIgnoreChkBxId.setChecked(true);
					
				}//if
				else{
					dateIsYrIgnoreChkBxId.setChecked(false);
					
				}
				for (Listitem item : contactDateLbId.getItems()) {
					
					POSMapping posMapping = (POSMapping)item.getValue();
					if(posMapping == null) {
						int selIndex = -1;
						if(contactField.equals("birth_day")){
							selIndex = 0;
							
						}
						else if(contactField.equals("anniversary_day")){
							selIndex = 1;
						}
						if(selIndex != -1) {
							
							contactDateLbId.setSelectedIndex(selIndex);
							break;
						}
					}
					else {
						
						if( contactField.toUpperCase().startsWith("UDF") && contactField.toUpperCase().equals(posMapping.getCustomFieldName())){
							contactDateLbId.setSelectedItem(item);
							break;
							
						}//if
						
					}
					
					
				}//for
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$contactDateRbnId'), true)");
				
				contactDateRbnId.setChecked(true);
				doToggleRadioBtn(contactDateRbnId);
			}//if
			else if((triggerType & 
				Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM) == Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM) {//get webforms if any????
					
				triggerTypeRGId.setSelectedItem(optinMediumRbnId);	
				String formName = inputStr.split("\\|")[2];
				
				//logger.info("formName ::"+formName);
				if(formName != null ) {
					
					if(formName.equals(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY)) {
						contactOptInMediumLbId.setSelectedIndex(1);
					}
					else if(formName.equals(Constants.CONTACT_OPTIN_MEDIUM_POS)) {
						contactOptInMediumLbId.setSelectedIndex(0);
					}else if(formName.equals(Constants.CONTACT_OPTIN_MEDIUM_MOBILE)){
						contactOptInMediumLbId.setSelectedIndex(3);
					}
					else {
						
						contactOptInMediumLbId.setSelectedIndex(2);
						onSelect$contactOptInMediumLbId();
						
						formName = formName.substring(formName.indexOf(":")+1);
						for (Listitem formItem : userWebFormsLbId.getItems()) {
							
							if(formName != null && formName.equals(formItem.getLabel())) {
								formItem.setSelected(true);
								break;
								
							}//if
							
							
						}//for
					}
					//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$optinMediumRbnId'), true)");
					optinMediumRbnId.setChecked(true);
					doToggleRadioBtn(optinMediumRbnId);
				}
				
					
				if(userWebFormsLbId.getItemCount() > 0 && userWebFormsLbId.getSelectedCount() == 0) userWebFormsLbId.setSelectedIndex(0);
					
					
			}//if contact's date
			else if((triggerType & 
					Constants.ET_TYPE_ON_CONTACT_ADDED) == Constants.ET_TYPE_ON_CONTACT_ADDED) {
				
				triggerTypeRGId.setSelectedItem(dateAddedRbnId);
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$onPurchaseRbnId'), true)");
				dateAddedRbnId.setChecked(true);
				doToggleRadioBtn(dateAddedRbnId);
				
				
			}
				
			else if((triggerType & 
					Constants.ET_TYPE_ON_CAMPAIGN_OPENED) == Constants.ET_TYPE_ON_CAMPAIGN_OPENED) {
				triggerTypeRGId.setSelectedItem(campaignOpenRbnId);	
				
				String campaignID =  inputStr.split("<AND>")[1].split("\\|")[2];
				List<Listitem> campList = campOpenedLbId.getItems();
				for (Listitem listitem : campList) {
					Campaigns campain = listitem.getValue();
					if(campain.getCampaignId().longValue() == Long.parseLong(campaignID)){
						
						campOpenedLbId.setSelectedItem(listitem);
						break;
					}
					
				}
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$campaignOpenRbnId'), true)");
				campaignOpenRbnId.setChecked(true);
				doToggleRadioBtn(campaignOpenRbnId);
				if(campOpenedLbId.getItemCount() > 0 && campOpenedLbId.getSelectedCount() == 0)campOpenedLbId.setSelectedIndex(0);
				
			}//if
			else if((triggerType & 
					Constants.ET_TYPE_ON_CAMPAIGN_CLICKED) == Constants.ET_TYPE_ON_CAMPAIGN_CLICKED) {
				
				triggerTypeRGId.setSelectedItem(campaignClickedRbnId);
				
				String[] tokenArr = inputStr.split("<AND>");
				String campaignID =  tokenArr[1].split("\\|")[2];
				
				List<Listitem> campList = campClickedLbId.getItems();
				for (Listitem listitem : campList) {
					Campaigns campain = listitem.getValue();
					if(campain.getCampaignId().longValue() == Long.parseLong(campaignID)){
						campClickedLbId.setSelectedItem(listitem);
						onSelect$campClickedLbId();
						if(urlSetLbId.getItemCount() > 0) {
							String url =  tokenArr[2].split("\\|")[2];
							List<Listitem> urlItems = urlSetLbId.getItems();
							for (Listitem urlItem : urlItems) {
								if(url.equalsIgnoreCase((String)urlItem.getValue())) {
									
									urlItem.setSelected(true);
									break;
								}//if
							}
							
						}//if
						//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$campaignClickedRbnId'), true)");
						campaignClickedRbnId.setChecked(true);
						doToggleRadioBtn(campaignClickedRbnId);
						break;
						
						
					}//for
					
				}//for
				
				if(campClickedLbId.getItemCount() > 0 && campClickedLbId.getSelectedCount() == 0)campClickedLbId.setSelectedIndex(0);
			}//if
			else if( (triggerType & Constants.ET_TYPE_ON_PRODUCT) == Constants.ET_TYPE_ON_PRODUCT ) {
					
				String[] tokenArr = inputStr.split("\\|");
				for (Listitem item : productCriteriaLBId.getItems()) {
					if(tokenArr[0].equals((String)item.getValue())) {
						
						item.setSelected(true);
						break;

					}
					
				}
				for (Listitem item : productOptionsLbId.getItems()) {
					if(tokenArr[1].endsWith((String)item.getValue())) {
						
						item.setSelected(true);
						break;

					}
					
				}
				productTxtBxId.setText(tokenArr[2]);
				triggerTypeRGId.setSelectedItem(productRbnId);
				productRbnId.setChecked(true);
				doToggleRadioBtn(productRbnId);
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$productRbnId'), true)");
				
				
				
				
				
			}//if
			else if( (triggerType & Constants.ET_TYPE_ON_PURCHASE) == Constants.ET_TYPE_ON_PURCHASE ) {
				triggerTypeRGId.setSelectedItem(onPurchaseRbnId);
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$onPurchaseRbnId'), true)");
				onPurchaseRbnId.setChecked(true);
				doToggleRadioBtn(onPurchaseRbnId);
				
				
			}//if
			else if( (triggerType & Constants.ET_TYPE_ON_PURCHASE_AMOUNT) == Constants.ET_TYPE_ON_PURCHASE_AMOUNT ) {
				String[] tokenArr = inputStr.split("\\|");
				
				for (Listitem item : purchaseAmtOptionsLbId.getItems()) {
					if(tokenArr[1].endsWith((String)item.getValue())) {
						
						item.setSelected(true);
						onSelect$purchaseAmtOptionsLbId();
						break;

					}
					
				}//for
				purchaseAmountDblBxId.setValue(Double.parseDouble(tokenArr[2]));
				if(purchaseAmount1DblBxId.isVisible()) purchaseAmount1DblBxId.setValue(tokenArr.length > 3 ? Double.parseDouble(tokenArr[3]) : null);
				
				triggerTypeRGId.setSelectedItem(amountRbnId);
				//Clients.evalJavaScript("toggleDivContents(zk.Widget.$('$amountRbnId'), true)");
				
				amountRbnId.setChecked(true);
				doToggleRadioBtn(amountRbnId);
			}else if((triggerType & Constants.ET_TYPE_ON_LOYALTY) == Constants.ET_TYPE_ON_LOYALTY ){
				logger.debug("----8----");
				
				triggerTypeRGId.setSelectedItem(loyaltyRbnId);
				loyaltyRbnId.setChecked(true);
				doToggleRadioBtn(loyaltyRbnId);
				
				logger.debug("Just enter here "+Constants.ET_TYPE_ON_LOYALTY);
				logger.debug("inputStr is  "+inputStr);

				if(inputStr != null) {
					String[] tokenArr = inputStr.split("\\|");

					//set Loyalty contact balance option 1
					String loyaltyContactTypeStr = tokenArr[0];
					if(loyaltyContactTypeStr.equals("lc.amount_balance"))
						loyaltyContactOptionLbId1.setSelectedIndex(1);
					else loyaltyContactOptionLbId1.setSelectedIndex(0);

					//set Loyalty contact balance option 2
					String condStr = tokenArr[1];
					if(condStr.equals("number:<")) {
						loyaltyContactOptionLbId2.setSelectedIndex(1);
					}else if(condStr.equals("number:>")) {
						loyaltyContactOptionLbId2.setSelectedIndex(0);
					}else if(condStr.equals("number:=")) {//
						loyaltyContactOptionLbId2.setSelectedIndex(2);
					}else if(condStr.equals("number:between")) {
						loyaltyContactOptionLbId2.setSelectedIndex(3);
					}
					//else loyaltyContactOptionLbId2.setSelectedIndex(4);

					//set balance points/amount value1
					//				loyaltyBalDbl1BxId.setVisible(false);
					String doublevalStr1 = tokenArr[2];
					loyaltyBalDbl1BxId.setValue(Double.parseDouble(doublevalStr1));
					loyaltyBalDbl1BxId.setVisible(true);
					/*if(!(loyaltyContactOptionLbId2.getSelectedIndex() == 4)){

				}*/

					layaltyBal2DblBxId.setVisible(false);
					if(loyaltyContactOptionLbId2.getSelectedIndex() == 3){

						String doublevalStr2 = tokenArr[3];
						layaltyBal2DblBxId.setValue(Double.parseDouble(doublevalStr2));
						layaltyBal2DblBxId.setVisible(true);
					}
				}
				//set issuance check box
				int value = Integer.parseInt(layaltyIssueChkId.getValue().toString());
				logger.debug("1 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				layaltyIssueChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
				
				//set Redemtion check box
				value = Integer.parseInt(layaltyRedeemChkId.getValue().toString());
				logger.debug("2 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				layaltyRedeemChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
				
				//set Adjustment check box
				value = Integer.parseInt(layaltyAdjustChkId.getValue().toString());
				logger.debug("3 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				layaltyAdjustChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
				
			}else if((triggerType & Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE) == Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE ){
				

				logger.debug("----8----");
				
				triggerTypeRGId.setSelectedItem(differenceRbnTd);
				differenceRbnTd.setChecked(true);
				doToggleRadioBtn(differenceRbnTd);
				
				logger.debug("Just enter here "+Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE);
				logger.debug("inputStr is  "+inputStr);

				if(inputStr != null) {
					String[] tokenArr = inputStr.split("\\|");

					//set Loyalty contact balance option 1
					String loyaltyContactTypeStr = tokenArr[0];
					if(loyaltyContactTypeStr.equals("lc.amount_difference"))
						loyaltyDiffOptionLbId1.setSelectedIndex(1);
					else loyaltyDiffOptionLbId1.setSelectedIndex(0);

					//set Loyalty contact balance option 2
					String condStr = tokenArr[1];
					if(condStr.equals("number:<")) {
						loyaltyDiffOptionLbId2.setSelectedIndex(1);
					}else if(condStr.equals("number:>")) {
						loyaltyDiffOptionLbId2.setSelectedIndex(0);
					}else if(condStr.equals("number:=")) {//
						loyaltyDiffOptionLbId2.setSelectedIndex(2);
					}else if(condStr.equals("number:between")) {
						loyaltyDiffOptionLbId2.setSelectedIndex(3);
					}
					//else loyaltyContactOptionLbId2.setSelectedIndex(4);

					//set balance points/amount value1
					//				loyaltyBalDbl1BxId.setVisible(false);
					String doublevalStr1 = tokenArr[2];
					loyaltyDiffDbl1BxId.setValue(Double.parseDouble(doublevalStr1));
					loyaltyDiffDbl1BxId.setVisible(true);
					/*if(!(loyaltyContactOptionLbId2.getSelectedIndex() == 4)){

				}*/

					loyaltyDiffDbl2BxId.setVisible(false);
					if(loyaltyDiffOptionLbId2.getSelectedIndex() == 3){

						String doublevalStr2 = tokenArr[3];
						loyaltyDiffDbl2BxId.setValue(Double.parseDouble(doublevalStr2));
						loyaltyDiffDbl2BxId.setVisible(true);
					}
				}
				//set issuance check box
				int value = Integer.parseInt(layaltyDiffIssueChkId.getValue().toString());
				logger.debug("1 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				layaltyDiffIssueChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
				
				
				
				
			}
			else if((triggerType & Constants.ET_TYPE_ON_GIFT) == Constants.ET_TYPE_ON_GIFT ){
				logger.debug("----9----");
				
				triggerTypeRGId.setSelectedItem(giftRbnId);
				giftRbnId.setChecked(true);
				doToggleRadioBtn(giftRbnId);
				
				logger.debug("Just enter here "+Constants.ET_TYPE_ON_GIFT);
				logger.debug("inputStr is  "+inputStr);

				if(inputStr != null) {
					
				String[] tokenArr = inputStr.split("\\|");
				
				//set Loyalty contact balance option 1
				//String giftContactTypeStr = tokenArr[0];
				//if(giftContactTypeStr.equals("loyalty.giftamount_balance"))
					//giftContactOptionLbId1.setSelectedIndex(0);
				
				//set Gift contact balance option 2
				String condStr = tokenArr[1];
				if(condStr.equals("number:<")) {
					giftContactOptionLbId2.setSelectedIndex(1);
				}else if(condStr.equals("number:>")) {
					giftContactOptionLbId2.setSelectedIndex(0);
				}else if(condStr.equals("number:=")) {//
					giftContactOptionLbId2.setSelectedIndex(2);
				}else if(condStr.equals("number:between")) {
					giftContactOptionLbId2.setSelectedIndex(3);
				}
				//else loyaltyContactOptionLbId2.setSelectedIndex(4);
				
				//set balance points/amount value1
//				loyaltyBalDbl1BxId.setVisible(false);
				String doublevalStr1 = tokenArr[2];
				giftBalDbl1BxId.setValue(Double.parseDouble(doublevalStr1));
				giftBalDbl1BxId.setVisible(true);
				/*if(!(loyaltyContactOptionLbId2.getSelectedIndex() == 4)){
					
				}*/
				
				giftBal2DblBxId.setVisible(false);
				if(giftContactOptionLbId2.getSelectedIndex() == 3){
					
					String doublevalStr2 = tokenArr[3];
					giftBal2DblBxId.setValue(Double.parseDouble(doublevalStr2));
					giftBal2DblBxId.setVisible(true);
				}
				
				}
				
				//set issuance check box
				int value = Integer.parseInt(giftTopUpChkId.getValue().toString());
				logger.debug("1 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				giftTopUpChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_GIFT_ISSUANCE);
				
				//set Redemption check box
				value = Integer.parseInt(giftRedeemChkId.getValue().toString());
				logger.debug("2 trigger valu is "+triggerType+" ::value is  ::"+value+ " after Operation and type ::"+(triggerType & value));
				giftRedeemChkId.setChecked((triggerType & value) == Constants.ET_TYPE_ON_GIFT_REDEMPTION);
				
			}
			
			// Added for campaign categories
			
			if(!user.getSubscriptionEnable())categoryLbId.setDisabled(true);
			String categoryName ="";
			Long category = editEventTrigger.getCampCategory();
			logger.info("category is"+category);
			if(category != null){
				
			
				if(category.equals(Constants.CAMP_CATEGORY_TRANSACTIONAL_ID)){
					
					categoryName = Constants.CAMP_CATEGORY_TRANSACTIONAL;
					for(Listitem item : categoryLbId.getItems()) {
						 
						 if(item.getLabel().equals(categoryName)) {
							 item.setSelected(true);
							 break;
						 }
					}
				} else{
					
				UserCampaignCategories userCmapObj =userCampaignCategoriesDao.findByCatId(category, user.getUserId());
				
				if(userCmapObj != null){
					 categoryName = userCmapObj.getCategoryName();
					 for(Listitem item : categoryLbId.getItems()) {
						 
						 if(item.getLabel().equals(categoryName)) {
							 item.setSelected(true);
							 break;
						 }
						 
						 
					 }
					 
					}
				 }

			}

			// set trigger the action settings . (Before/After settings)
			Long minOffSet = editEventTrigger.getMinutesOffset();
			Date targetDate = editEventTrigger.getTargetTime();
			
			logger.debug("************minutes offset"+ minOffSet);
			
			if(minOffSet != null) {
				int child = 0;
				targetTimeLblId.setVisible(false);
				int trtype = editEventTrigger.getTrType();
					if(minOffSet > 0) {  // sets After
						
						triggerActionLbId.setSelectedIndex(0);
						child = 0;
						if(minOffSet >= 1440 ) {
							
							
							
						}//if
						
					} else if(minOffSet < 0){ // sets Before
						child = 1;
						triggerActionLbId.setSelectedIndex(1);
						if(minOffSet <= -1440) {
							
							
							
						}//if
						
						
					} else if(minOffSet == 0 )  { // sets ON / immediately
						
						//if on the same day selected target time will be there
						//if immediately is selected no target time will be there
						
						
						child = (targetDate != null ? 3 :2);
						//logger.info("child ::"+child);
						
						triggerActionLbId.setSelectedIndex(child);
					} 
					
					//Clients.evalJavaScript("enableAfterBeforeDivComponents( (zk.Widget.$('$triggerActionLbId')).getChildAt("+child+"))");
//					onSelect$triggerActionLbId();
					doActionLbSettings(true);
					
			}//if
			
			//logger.debug("min offset is :" + minOffSet + " : " + triggerActionLbId.getSelectedIndex() + " : " );
			
			minOffSet = Math.abs(minOffSet);
			long tempNo = 0;
				if(minOffSet > 0) {
					
					if(minOffSet >= 1440) {
						
						tempNo = minOffSet/1440;
						hoursDaysLbId.setSelectedIndex(1);
						
						/*	Iterator it = numbersLbId.getChildren().iterator();
				while(it.hasNext()) {
					Listitem li = (Listitem)it.next();
					if(Long.parseLong(li.getLabel()) == (Math.abs(tempNo))) {
						numbersLbId.setSelectedItem(li);
						break;
					}
				}
						 */								
						
					} else if(minOffSet < 1440 ) {
						
						tempNo = minOffSet/60;
						hoursDaysLbId.setSelectedIndex(0);
						
					}
					
					
					onSelect$hoursDaysLbId();
					List<Listitem> numberItems  = numbersLbId.getItems();
					
					for (Listitem li : numberItems)  {
						
						if(Long.parseLong((String)li.getValue()) == (tempNo)) {
							numbersLbId.setSelectedItem(li);
							break;
						}
						
					}//for each number
					
					//Clients.evalJavaScript("enableTargetTimer((zk.Widget.$('$hoursDaysLbId')).getChildAt("+hoursDaysLbId.getSelectedIndex()+"))");
				
				}
				
			
			
			if(targetDate != null) {
				
				int targetHour = targetDate.getHours(); 
				
				Calendar cal = new MyCalendar(clientTimeZone);
				
				cal.setTime(targetDate);
				
				
				for (Listitem hourItem : timerListBoxId.getItems()) {
					
					if(targetHour == Integer.parseInt(hourItem.getValue().toString())) {
						
						hourItem.setSelected(true);
						break;
					}
					
					
				}
				
				/*Clients.evalJavaScript("enableTargetTimer(zk.Widget.$('$daysListItemId'));");
				
				enableTargetTimeChkBoxId.setChecked(true);
				Clients.evalJavaScript("enableTargetTimerComponentsDiv(zk.Widget.$('$enableTargetTimeChkBoxId'), true)");
				*/
				
				/*int hours = targetDate.getHours();
				int minutes = targetDate.getMinutes();
				
				meridianLbId.setSelectedIndex(hours > 11 ? 1 : 0);

				if(hours > 11) hours = (hours-12);
				hoursLbId.setSelectedIndex(hours);
				
				for (Listitem minuteItem : mintsLbId.getItems()) {
					if(Integer.parseInt(minuteItem.getLabel()) == minutes) {
						minuteItem.setSelected(true); 
						break;
					}//if
					
				}//for
				*/
				
				
				
				
				
			}//if
			
			
			String targetDayFlag = editEventTrigger.getTargetDaysFlag();
			if(targetDayFlag != null) {
				
				setCheckForAllSelTargetDays(targetDayFlag);
			}
			
			
			
			
			
			long optionsFlag = editEventTrigger.getOptionsFlag();
			if((editEventTrigger.getOptionsFlag() & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == Constants.ET_TRIGGER_IS_ACTIVE_FLAG) {
				triggerStatusChkBxId.setChecked(true);
			}else {
				triggerStatusChkBxId.setChecked(false);
				
			}
				
			//if trigger action is send a campaign
			if((optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG) == Constants.ET_SEND_CAMPAIGN_FLAG) {
				sendEmailCampaignChkBxId.setChecked(true);
				onClick$sendEmailCampaignChkBxId();
				List<Listitem> list = campaignsLbId.getItems();
				Iterator<Listitem> it = list.iterator();
				while(it.hasNext()) {
					Listitem li = it.next();
					if( ((Campaigns)li.getValue()).getCampaignId().equals(editEventTrigger.getCampaignId())) {
						campaignsLbId.setSelectedItem(li);
						break;
					}
				}
				
				fromEmail = editEventTrigger.getSelectedCampaignFromEmail();
				fromName = editEventTrigger.getSelectedCampaignFromName();
				replEmail = editEventTrigger.getSelectedCampaignReplyEmail();
				
				
				//TODO need to look into this method
				//Clients.evalJavaScript("displaySendCampaignDiv(zk.Widget.$('$sendEmailCampaignChkBxId'));");
			}//if send campaign
			
			
			if((optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG){
				
				sendSMSCampaignChkBxId.setChecked(true);
				onClick$sendSMSCampaignChkBxId();
				//Clients.evalJavaScript("displaySendSMSDiv(zk.Widget.$(jq('$sendSMSChkBxId')[0]));"); // we are setting the check box to true 
				
				//Above function will enable the SMS options tab and now we need to pre populate with the information from DB.....  
				
				if((editEventTrigger.getSmsId() != null)) {
					
					List<Listitem> list = smsLbId.getItems();
					for (Listitem li : list) {
						
						if( ((SMSCampaigns)li.getValue()).getSmsCampaignId().equals(editEventTrigger.getSmsId()) ) {
							smsLbId.setSelectedItem(li);
							break;
						}
						
					}
				}
				onClick$sendSMSCampaignChkBxId();
					//Clients.evalJavaScript("displaySendSMSDiv(zk.Widget.$('$sendSMSCampaignChkBxId'));");
			}//if  send SMS
			if((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG) {
				MoreOptionsDivId.setVisible(true);
				addToOtherListChkBxId.setChecked(true);
				
				onClick$addToOtherListChkBxId();
				MailingList otherMl =  editEventTrigger.getAddTriggerContactsToMl();
				//logger.info("---4555555555-----");
				if(otherMl != null ) {
					//logger.info("---999999999-----");
					List<Listitem> list = addToMlLbId.getItems();
					MailingList mailingList =null;
					
					for (Listitem mlItem : list) {
						
						mailingList = (MailingList)mlItem.getValue();
						if(mailingList.getListId().longValue() == otherMl.getListId().longValue()){
							mlItem.setSelected(true);
							break;
						}
					}//for each other ml item
					//Clients.evalJavaScript("displayAddToMLDiv(zk.Widget.$('$addToOtherListChkBxId'));");
				}
				
			}//if add to other list
				
			if((editEventTrigger.getOptionsFlag() & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) {
				removeFrCampMlChkBxId.setChecked(true);
				MoreOptionsDivId.setVisible(true);
			}//if remove from the configured list
			
			String fromEmailId = editEventTrigger.getSelectedCampaignFromEmail();
			for(int index=0; index<trigCampFromCbId.getItemCount();index++) {
				
			   if(trigCampFromCbId.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				   trigCampFromCbId.setSelectedIndex(index);
			   }  
			   if(trigCampReplyCbId.getItemAtIndex(index).getLabel().equals(editEventTrigger.getSelectedCampaignReplyEmail())) {
				   trigCampReplyCbId.setSelectedIndex(index);
			   }  
			   
			}//for
			
			trigCampNameTbId.setText(editEventTrigger.getSelectedCampaignFromName() != null
					? editEventTrigger.getSelectedCampaignFromName() : "" );
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
				
			
		
			
			
	}
		
	private Div beforeAfterMsgDivId;
	
	
	
	
	
	public void onSelect$triggerActionLbId() {
		
		
		doActionLbSettings(false);
		
	}
	
	
	public void doActionLbSettings(boolean isEdit) {
		
		Listitem selectedItem = triggerActionLbId.getSelectedItem();
		//logger.info("selectedItem.getId()"+selectedItem.getId());
		
		if(selectedItem.getId().equals("afterListItemId") || selectedItem.getId().equals("beforeListItemId") ) 
		{
			beforeAfterMsgDivId.setVisible(true);
			numbersLbId.setVisible(true);
			hoursDaysLbId.setVisible(true);
			
			targetTimeLblId.setVisible(true);
			timerListBoxId.setVisible(true);
			
			targetDayLblId.setVisible(true);
			targetDaysDivId.setVisible(true);
			
		}else{
			
			beforeAfterMsgDivId.setVisible(false);
			targetDayLblId.setVisible(false);
			targetDaysDivId.setVisible(false);
			
			if(selectedItem.getId().equals("onDayListItemId") ){
				
				targetTimeLblId.setVisible(true);
				timerListBoxId.setVisible(true);
				
				
			}else{
				//alert('true');
				targetTimeLblId.setVisible(false);
				timerListBoxId.setVisible(false);
				
			}
			
			
		}//else
	
		if(!isEdit) { 
			
			hoursDaysLbId.setSelectedIndex(hoursDaysLbId.getItemAtIndex(0).isVisible() ? 0 : 1);
			onSelect$hoursDaysLbId();
			numbersLbId.setSelectedIndex(0);
			
			if(!beforeAfterMsgDivId.isVisible()){
				
			targetTimeLblId.setVisible(!onListItemId.isSelected());
			timerListBoxId.setVisible(!onListItemId.isSelected());
			targetDayLblId.setVisible(!onListItemId.isSelected() && !onDayListItemId.isSelected());
			targetDaysDivId.setVisible(!onListItemId.isSelected() && !onDayListItemId.isSelected());
		}

			if(timerListBoxId.isVisible()) {
				
				timerListBoxId.setSelectedIndex(0);
				setCheckForAllSelTargetDays("1,2,3,4,5,6,7");
			}
			
		}
		
		
	}
	
	
	public void setCheckForAllSelTargetDays(String daysFlag) {
		String[] daysFlagArr = daysFlag.split(",");
		Checkbox[] dayschkArr = {ignoreSunChkBoxId, ignoreMonChkBoxId, ignoreTueChkBoxId,
								ignoreWedChkBoxId, ignoreThurChkBoxId, ignoreFriChkBoxId, ignoreSatChkBoxId};
		
		
		for (Checkbox checkbox : dayschkArr) {
			
			checkbox.setChecked(false);
		}
		
		for (String day : daysFlagArr) {
			
			for (Checkbox checkbox : dayschkArr) {
				
				if(day.equals(checkbox.getValue().toString())){
					
					checkbox.setChecked(true);
					break;
				}
			}
			
		}//for
		
		
		
		
	}//setCheckForAllSelTargetDays
	
	
	public void onSelect$loyaltyContactOptionLbId2(){
		
		if(loyaltyContactOptionLbId2.getSelectedIndex()== 3){
			layaltyBal2DblBxId.setVisible(true);
			loyaltyBalDbl1BxId.setVisible(true);
		}/*else if(loyaltyContactOptionLbId2.getSelectedIndex()== 4){
			layaltyBal2DblBxId.setVisible(false);
			loyaltyBalDbl1BxId.setVisible(false);
		}*/else {
			layaltyBal2DblBxId.setVisible(false);
			loyaltyBalDbl1BxId.setVisible(true);
		}
	}//onSelect$loyaltyContactOptionLbId2
	
	
	public void onSelect$giftContactOptionLbId2(){
		
		if(giftContactOptionLbId2.getSelectedIndex()== 3){
			giftBal2DblBxId.setVisible(true);
			giftBalDbl1BxId.setVisible(true);
		}else {
			giftBal2DblBxId.setVisible(false);
			giftBalDbl1BxId.setVisible(true);
		}
	}//onSelect$giftContactOptionLbId2
public void onSelect$loyaltyDiffOptionLbId2(){
		
		if(loyaltyDiffOptionLbId2.getSelectedIndex()== 3){
			loyaltyDiffDbl2BxId.setVisible(true);
			loyaltyDiffDbl1BxId.setVisible(true);
		}/*else if(loyaltyContactOptionLbId2.getSelectedIndex()== 4){
			layaltyBal2DblBxId.setVisible(false);
			loyaltyBalDbl1BxId.setVisible(false);
		}*/else {
			loyaltyDiffDbl2BxId.setVisible(false);
			loyaltyDiffDbl1BxId.setVisible(true);
		}
	}//onSelect$loyaltyContactOptionLbId2
	
	final class MyRenderer implements RowRenderer, EventListener {
		@Override
		public void render(Row row, Object data, int arg2) throws Exception {
			try {

				if (data instanceof TriggerCustomEvent) {
					TriggerCustomEvent triggerCustomEvent = (TriggerCustomEvent) data;

					row.setParent(triggerRowsId);
					Label label = new Label(triggerCustomEvent.getEventName());
					label.setParent(row);

					Calendar tempCal = null;
					tempCal = triggerCustomEvent.getEventDate();
					tempCal.setTimeZone(clientTimeZone);

					//
					Label label2 = new Label(MyCalendar.calendarToString(
							tempCal, MyCalendar.FORMAT_DATEONLY));

					label2.setParent(row);

					Image delToolBtn = new Image("/img/icons/delete_icon.png");
					delToolBtn.setStyle("cursor:pointer;margin-right:10px;");
					delToolBtn.setTooltiptext("Delete");
					delToolBtn.addEventListener("onClick", MyRenderer.this);
					delToolBtn.setAttribute("triggerCustomEventObj",
							triggerCustomEvent);
					delToolBtn.setParent(row);

					// row.setAttribute("trigCustEventObj", triggerCustomEvent);

					return;
				}

				EventTrigger eventTrigger = (EventTrigger) data;

				Label label = new Label(eventTrigger.getTriggerName());
				label.setParent(row);
				
				Label label0 = new Label(MyCalendar.calendarToString(eventTrigger.getTriggerCreatedDate() ,MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				
				label0.setParent(row);
		

				Label label1 = new Label(MyCalendar.calendarToString(eventTrigger.getTriggerModifiedDate() ,MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
						
				label1.setParent(row);

				/*Label label2 = new Label(eventTrigger.getTriggerType());
				label2.setParent(row);*/

				Label label4 = new Label(
						(eventTrigger.getOptionsFlag() & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == 1 ? "Active"
								: "InActive");
				label4.setParent(row);

				Hbox hbox = new Hbox();

				Image editToolBarBtn = new Image("/img/email_edit.gif");
				editToolBarBtn.setTooltiptext("Edit");
				editToolBarBtn.setAttribute("Label", "EDIT");
				editToolBarBtn.addEventListener("onClick", MyRenderer.this);
				editToolBarBtn
						.setStyle("cursor:pointer;margin-right:15px;");
				editToolBarBtn.setParent(hbox);

				String src =( (eventTrigger.getOptionsFlag() & 1) <= 0) ?   "/img/play_icn.png" : "/img/pause_icn.png";
				String tooltipStr = ( (eventTrigger.getOptionsFlag() & 1) <= 0) ? "Active" : "Pause";
				
				Image img = new Image(src);
				img.setStyle("cursor:pointer;margin-right:15px;");
				img.setTooltiptext(tooltipStr);
				img.setAttribute("Label", "PAUSE");
				img.addEventListener("onClick",MyRenderer.this);
				
				img.setParent(hbox);
				
				
				Image delToolBarBtn = new Image("/img/icons/delete_icon.png");
				delToolBarBtn.setTooltiptext("Delete");
				delToolBarBtn.setAttribute("Label", "DELETE");
				delToolBarBtn.addEventListener("onClick", MyRenderer.this);
				delToolBarBtn.setStyle("cursor:pointer;margin-right:15px;");
				delToolBarBtn.setParent(hbox);

				
				Image reportImg = new Image("/img/theme/home/reports_icon.png");
				reportImg.setTooltiptext("Report");
				reportImg.addEventListener("onClick", MyRenderer.this);
				reportImg.setAttribute("Label", "REPORT");
				reportImg.setStyle("cursor:pointer;");
				reportImg.setParent(hbox);
				
				
				
				
				/*
				 * Toolbarbutton copyToolbarBtn = new Toolbarbutton("Copy");
				 * copyToolbarBtn.addEventListener("onClick",MyRenderer.this);
				 * copyToolbarBtn.setStyle("padding:0px 20px;border: 0px;");
				 * copyToolbarBtn.setParent(div);
				 */

				row.setAttribute("eventTriggerObj", eventTrigger);

				hbox.setParent(row);

			} catch (Exception e) {
				logger.error(
						"** Exception : Error occured while fetch data **", e);
			}
		}

		@Override
		public void onEvent(Event event) throws Exception {

			Image tempToolbrBtn = (Image) event.getTarget();
			// logger.debug("Event Target Label Is : "+
			// tempToolbrBtn.getLabel());

			MessageUtil.clearMessage();

			if (tempToolbrBtn.getAttribute("triggerCustomEventObj") != null) {
				TriggerCustomEvent triggerCustomEvent = (TriggerCustomEvent) tempToolbrBtn
						.getAttribute("triggerCustomEventObj");
				try {
					int confirm = Messagebox.show(
							"Are you sure you want to delete the custom event: "
									+ triggerCustomEvent.getEventName() + "?",
							"Delete Custom Event", Messagebox.OK
									| Messagebox.CANCEL, Messagebox.QUESTION);

					if (confirm == Messagebox.OK) {
						try {
							triggerCustomEventDaoForDML.delete(triggerCustomEvent);
							((Row) tempToolbrBtn.getParent()).setVisible(false);
							MessageUtil.setMessage(
									"Custom event deleted successfully.",
									"green", "top");
							logger.info("Custom Event Deleted Successfully ********");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//logger.info("this is Error information");
							logger.error("Exception ::", e);
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}

				return;
			}
			/*
			 * if(tempToolbrBtn.getAttribute("triggerCustomEventObj") == null) {
			 * return; }
			 */

			Row row = (Row) tempToolbrBtn.getParent().getParent();
			
			List chaildLst = row.getChildren();
			//Label statusLbl  = (Label)chaildLst.get(2);
			Label statusLbl  = (Label)chaildLst.get(3);
			EventTrigger eventTrigger = (EventTrigger) row
					.getAttribute("eventTriggerObj");

			if (((String) tempToolbrBtn.getAttribute("Label"))
					.equalsIgnoreCase("COPY")) {

			} else if (((String) tempToolbrBtn.getAttribute("Label"))
					.equalsIgnoreCase("DELETE")) {

				try {
					int confirm = Messagebox.show(
							"Are you sure you want to delete the trigger: "
									+ eventTrigger.getTriggerName() + "?",
							"Delete Trigger",
							Messagebox.OK | Messagebox.CANCEL,
							Messagebox.QUESTION);

					if (confirm == Messagebox.OK) {
						try {

							//eventTriggerDao.delete(eventTrigger);//TODO 
							eventTriggerDaoForDML.delete(eventTrigger);
							//row.setVisible(false);
							triggerRowsId.removeChild(row);
							int totalSize = eventTriggerDao.findAllCountByUserId(user.getUserId(),true); 
							
							triggerPagingId.setTotalSize(totalSize);
							
							
							
							MessageUtil.setMessage(
									"Trigger deleted successfully.", "green",
									"top");
							logger.info(">> Trigger Deleted Successfully . ");
						} catch (Exception e) {

							logger.error(
									"** Exception : Error occured while deleting the event Trigger obj :"
											+ eventTrigger.getId(), e);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}

			} else if (((String) tempToolbrBtn.getAttribute("Label"))
					.equalsIgnoreCase("EDIT")) {

				//logger.debug("event obj is :" + eventTrigger);
				sessionScope.setAttribute("eventTriggerObj", eventTrigger);
				
				addTriggerTabId.setSelected(true);
				doEdittriggerSettings();
				
				
				
			} else if (((String) tempToolbrBtn.getAttribute("Label"))
					.equalsIgnoreCase("REPORT")) {

				//logger.debug("event obj is :" + eventTrigger);
				sessionScope.setAttribute("eventTriggerObj", eventTrigger);
				//Redirect.goTo(PageListEnum.REPORT_EVENT_TRIGGER_REPORT);
				Redirect.goTo(PageListEnum.REPORT_EVENT_TRIGGER_NEW_ET_REPORTS);
				
				
			}else if (((String) tempToolbrBtn.getAttribute("Label"))
					.equalsIgnoreCase("PAUSE")) {

				//logger.debug("event obj is :" + eventTrigger);

				
				int confirm = Messagebox.show("Are you sure you want to change the status?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
				String src = "";
				String toolTipTxtStr = "";
				String statusStr = "";
				Calendar activeOn=null;
				
				boolean isEnable = ( (eventTrigger.getOptionsFlag() & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == Constants.ET_TRIGGER_IS_ACTIVE_FLAG ); 
				long optionsFlag = eventTrigger.getOptionsFlag();
				if(isEnable) {
					src = "/img/play_icn.png";
					toolTipTxtStr = "Activate";
					/*img.setSrc("/img/play_icn.png");
					img.setTooltiptext("Pause");*/
					//img.setTooltiptext("Active");
					statusStr = "InActive";
					eventTrigger.setOptionsFlag(optionsFlag- Constants.ET_TRIGGER_IS_ACTIVE_FLAG);
					activeOn =null;
					
				}else{
					src = "/img/pause_icn.png";
					toolTipTxtStr = "Pause";
					/*img.setSrc("/img/pause_icn.png");
					img.setTooltiptext("Active");*/
					//img.setTooltiptext("Pause");
					statusStr = "Active";
					eventTrigger.setOptionsFlag(optionsFlag+ Constants.ET_TRIGGER_IS_ACTIVE_FLAG);
					activeOn=Calendar.getInstance();
					//formMapping.setActiveSince(activeOn);
					
				}
				
				tempToolbrBtn.setSrc(src);
				tempToolbrBtn.setTooltiptext(toolTipTxtStr);
				statusLbl.setValue(statusStr);
				eventTrigger.setTriggerModifiedDate(Calendar.getInstance());
				//eventTriggerDao.saveOrUpdate(eventTrigger);
				eventTriggerDaoForDML.saveOrUpdate(eventTrigger);
				MessageUtil.setMessage("Status changed to "+statusStr, "color:blue;");
				
				//webformEditSettinngs();
				
				
			
				
				
			}

		}

	}

}
