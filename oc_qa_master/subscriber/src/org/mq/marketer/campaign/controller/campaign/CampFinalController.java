package org.mq.marketer.campaign.controller.campaign;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserCampaignExpiration;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDateBoxWithDate;
import org.mq.marketer.campaign.custom.MyDateBoxWithDateAndTime;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDaoForDML;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.MessageboxDlg;

@SuppressWarnings("serial")
public class CampFinalController extends GenericForwardComposer {
    
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private static String TB_ACTION_DELETE = "DELTE";
	private static String TB_ACTION_EDIT = "EDIT";
	private static String TB_ACTION_RESEND = "RESEND";
	private static String TB_ACTION = "TOOLBUTTON ACTION";
	private final String ATTRIBUTE_SOURCE = "Source";
	
	private static byte MAX_RESEND_LEVEL = 2;
	
	
	
	private Rows schedGrdRowsId;
	private Row currentRow;
	
	//private MyDatebox prtDtBxId;
	private MyDateBoxWithDate prtDtBxId;
	//private MyDatebox startDtBxId;
	private MyDateBoxWithDateAndTime startDtBxId;
	//private MyDatebox endDtBxId;
	private MyDateBoxWithDateAndTime endDtBxId;
	//private MyDatebox resendOptionDbId;
	private MyDateBoxWithDateAndTime resendOptionDbId;
	private Listbox frqLbId;
	private Listbox resendOptionLbId; 
	private Window resendOptionWinId;
	private Label errMsgLblId;
	private Div listNamesDivId;
	private Label recipentsSourceLblId;
	private Window resendEditorWinId;
	private Include resendEditorWinId$resendIncId;
	private Button sendNowButtonId;
	private Label schErrorLblId;
	private Button okBtnId;
	private Textbox testEmailTbId;
	
	private Grid schedGrdId;
	private CampaignScheduleDao campaignScheduleDao;
	private CampaignScheduleDaoForDML campaignScheduleDaoForDML;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private UserActivitiesDao userActivitiesDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	private Image editCampImgId;
	
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private SegmentRulesDao segmentRulesDao;
	private MailingListDao mailingListDao;
	private ContactsDao contactsDao;
	
//	private Combobox hoursCombBxId,mintsCombBxTd;
	private Listbox hoursListBxId,mintsListBxTd;
	
	private List<CampaignSchedule> campScheduleList;
	private Map<Long, Rows> rowsMap = new HashMap<Long, Rows>();
	private Map<Long, Row> rowMap = new HashMap<Long, Row>();
	private Campaigns campaign;
	private boolean editCampaign;
	private Div	frqDivId;
	//private Div frqDtDivId;
	private Div prtDtDivId;
	private Label cCategoryName;
	
	//Added for 2.3.11
	private UserCampaignExpirationDao userCampaignExpirationDao;
	private UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML;
	private final String LATEST_SCH_ON = "ENDCAL";
	private final String START_SCH_ON = "STARTCAL";
	private final String FREEQUENCY = "FREQ";
	private Button frqBtnId;
	private boolean persistentCamp = true;

	//Added for 2.3.11
	private Window viewAllArchivedSchedulesWinId;
	private Listbox viewAllArchivedSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllArchivedSchedulesWinId;
	private A viewAllArchivedSchedAnchId;
	private A viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId;
	private Listbox sentCampaingsListlbId;
	private Div campaignSentDivId;
	private Label viewAllArchivedSchedulesWinId$noRecordsArchivedLbId,campaignSentLbId;

	//Added 2.3.11

	private Listbox activeCampaingsListlbId;
	private Div campaignActiveTillDivId;
	private Label campActiveTillDateLbId,numOfTimeCampActiveLbId;
	private A viewAllActiveSchedAnchId;
	private Window viewAllActiveSchedulesWinId,viewIncludedCouponsWinId;
	private Bandbox viewAllActiveSchedulesWinId$campActionsBandBoxId;
	private Listbox viewAllActiveSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllActiveSchedulesWinId;
	private Bandbox campActionsBandBoxId$viewAllActiveSchedulesWinId;
	private Label viewAllActiveSchedulesWinId$bulkDeleteLbId;	
	private Label viewAllActiveSchedulesWinId$noRecordsActiveLbId ;
	private A viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId;
	private int activeSchCount = 100;
	private int sentSchCount = 100;
	
	
	//added in 2.4.6
	private Label listOfIncludedPromoCodes;
	private Div includedPromoCodesDivId;
	
	private MyListener listener = new MyListener();
	
	public CampFinalController() {
		
		campScheduleList = new ArrayList<CampaignSchedule>();
		campaign = (Campaigns)Sessions.getCurrent().getAttribute("campaign");
		campaignScheduleDao = (CampaignScheduleDao)
										SpringUtil.getBean("campaignScheduleDao");
		campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)
				SpringUtil.getBean("campaignScheduleDaoForDML");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
		
		
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		try {
			userCampaignExpirationDao = (UserCampaignExpirationDao)ServiceLocator.
									getInstance().getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		} 
		
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Email (Step 6 of 6)","",style,true);

		Utility.breadCrumbFrom(6);
		
		userActivitiesDao = (UserActivitiesDao) SpringUtil.getBean("userActivitiesDao");
		/*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_FINAL,GetUser.getUserObj());
		}*/
		
		userActivitiesDaoForDML = (UserActivitiesDaoForDML) SpringUtil.getBean("userActivitiesDaoForDML");
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_FINAL,GetUser.getLoginUserObj());
		}
		
		session = Sessions.getCurrent();
		
		
		
	}
	
	Users currentUser = GetUser.getUserObj();
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug(">>>>>>> Started  doAfterCompose :: ");
		logger.debug(">>>>>>>> isEdit :"+session.getAttribute("editCampaign"));
		
		editCampaign = (String)session.getAttribute("editCampaign") == null ? false:
			(((String)session.getAttribute("editCampaign")).equals("edit") ? true:false);
		
		campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
		
		
		if(campScheduleList != null && campScheduleList.size() > 0){
			logger.info("campScheduleList Size is  ::"+campScheduleList.size());
			//Sent Schedule
			List<CampaignSchedule> archivedCampSchedule = getArchivedCampScheduleList(campScheduleList);
			redrawSent(0, sentSchCount);
			if(archivedCampSchedule != null && archivedCampSchedule.size() > 0){
				logger.info(MyCalendar.calendarToString(archivedCampSchedule.get(archivedCampSchedule.size()-1).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				/*drawArchivedDiv(archivedCampSchedule.get(archivedCampSchedule.size()-1));
				drawSentListBox(archivedCampSchedule.get(0));*/
				drawArchivedDiv(archivedCampSchedule.get(0));
				drawSentListBox(archivedCampSchedule.get(archivedCampSchedule.size()-1));
			}
			else{
				campaignSentDivId.setVisible(false);
			}
			//Active Scheudle
			List<CampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(campScheduleList);
			if(activeCampScheduleList.size() > 0){
				logger.info("Drawing Active Schedules ::::::"+activeCampScheduleList.size());
				persistentCamp = true;
			//	createRowUpComingListBox(activeCampScheduleList.get(activeCampScheduleList.size()-1), 0, true);
				//createDivUpComingCampaigns(true,activeCampScheduleList.get(activeCampScheduleList.size()-1));
				
				
				createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
				createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
			}
			else{
				persistentCamp = false;
				createDivUpComingCampaigns(false,null);
			}
			//loadSchedule();
		}
		else{
			logger.info("campScheduleList Size is  Null or empty");
			persistentCamp = false;
			createDivUpComingCampaigns(false,null);
		}
		/*if(editCampaign) {
			
			logger.debug("campaign id ::"+campaign.getCampaignId());
		}*/
		resendOptionLbId = (Listbox)resendOptionWinId.getFellow("resendOptionLbId");
		//resendOptionDbId = (MyDatebox)resendOptionWinId.getFellow("resendOptionDbId");
		resendOptionDbId = (MyDateBoxWithDateAndTime)resendOptionWinId.getFellow("resendOptionDbId");
		errMsgLblId = (Label)resendOptionWinId.getFellow("errMsgLblId");
		TimeZone clientTimeZone = (TimeZone) sessionScope.get("clientTimeZone");
		Calendar clientCal = Calendar.getInstance(clientTimeZone);
		MyCalendar currentCal = new MyCalendar(clientCal,clientTimeZone);
		currentCal.set(MyCalendar.MINUTE, currentCal.get(MyCalendar.MINUTE) + 15);
		logger.info("currentCal.getNewCalendar()"+currentCal.getNewCalendar());
		logger.info("currentCal.getTime()"+currentCal.getTime());
		logger.info("prtDtBxId"+prtDtBxId.getValue());
		//int diffInTimeZones= compare(serverCal.getTimeZone(),cal.getTimeZone());
		//prtDtBxId.setValueforprtDtBxId(currentCal);
		//prtDtBxId.setValueForPrtDtBxId(currentCal);
		prtDtBxId.setValue(currentCal);
		startDtBxId.setValue(currentCal);
		endDtBxId.setValue(new MyCalendar(currentCal));
		resendOptionDbId.setValue(new MyCalendar(currentCal));
		setMlistLinks();
		
		/**
		 *this code is for detail
		 */
		 Rows rows=(Rows)schedGrdId.getFellow("schedGrdRowsId");
			List list = rows.getChildren();
			
			for(Object obj:list){
				Row row=(Row)obj;
				CampaignSchedule campShcedule=(CampaignSchedule)row.getValue();
				
				List<Object[]> childList=campaignScheduleDao.getAllChidren(campShcedule.getCsId(),campShcedule.getCampaignId());
				if(childList!=null) {
					logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
					Detail detail=(Detail)row.getChildren().get(0);
					detail.setStyle("display:block;");
					detail.addEventListener("onOpen", listener);
					
				} 

			}
			
			/******deafult setting of Hours *********/
			defaultSetTime();
			
			// user subscrption settings..
			Long categoryId= campaign.getCategories();
			UserCampaignCategories userCmapObj  = null;
			if(categoryId != null){
				
				userCmapObj =userCampaignCategoriesDao.findById(categoryId.toString());
			}
			if(currentUser.getSubscriptionEnable() &&(userCmapObj != null && userCmapObj.getIsVisible())){
				
					cCategoryName.setValue(campaign.getCategoryName());
				
			}else cCategoryName.setValue("");
			
			populateListOfIncludedPromoCodesLabel();
			logger.debug("<<<<< Completed doAfterCompose .");
			
	}


	/**
	 * getArchivedCampScheduleList
	 * @param campScheduleList2
	 * @return archiveCampSchedule
	 */
	private List<CampaignSchedule> getArchivedCampScheduleList(List<CampaignSchedule> campScheduleList2) {
//		logger.debug(">>>>>>> Started  getArchivedCampScheduleList :: ");
		List<CampaignSchedule> archiveCampSchedule = new ArrayList<CampaignSchedule>();
		for (CampaignSchedule campaignSchedule : campScheduleList2) {
			if(!(campaignSchedule.getStatus() == 0)  && !( campaignSchedule.getStatus() == 2 ) && !( campaignSchedule.getStatus() == 3 )) {
				logger.info(campaignSchedule.getDateStrByTimeZone(MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				archiveCampSchedule.add(campaignSchedule);
			}
		}
//		logger.debug("<<<<< Completed getArchivedCampScheduleList .");
		return archiveCampSchedule;

	}//getArchivedCampScheduleList




	private void defaultSetTime() {
		
		for(int i=0; i< 24; i++) {
			Listitem tempItem = new Listitem();
			if(i<10) {
				tempItem.setLabel("0"+i);
			}else {
				tempItem.setLabel(""+i);
			}
			tempItem.setParent(hoursListBxId);
		}
		

		/*TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		Calendar temCal = new  MyCalendar(clientTimeZone);
		*///Date date = (temCal.getTime());
		
		TimeZone clientTimeZone = (TimeZone) sessionScope.get("clientTimeZone");
	        Calendar clientCal = Calendar.getInstance(clientTimeZone);
		MyCalendar temCal = new MyCalendar(clientCal,clientTimeZone);
		
		
		String[] timeSatmp = new String[2];
		timeSatmp[0] = ""+temCal.get(Calendar.HOUR_OF_DAY);
		timeSatmp[1] = ""+temCal.get(Calendar.MINUTE);
		//logger.debug("current hour is ====="+ timeSatmp[0]);
		//logger.debug("current Mintues is ====="+ timeSatmp[1]);
		
		List<Listitem> mintsListItemList = mintsListBxTd.getItems();
		int currentMintint = Integer.parseInt(timeSatmp[1].trim());
		int currentHourInt = Integer.parseInt(timeSatmp[0].trim());
//		boolean setHourfalg = false;
//		Listitem tempItem = null;
		
		for (Listitem listitem : mintsListItemList) {
			int tempInt  = Integer.parseInt(listitem.getLabel().trim());
			
			/*if(currentMintint == tempInt) {
				mintsListBxTd.setSelectedItem(listitem);
			}*/
			
			if(currentMintint > tempInt) {
				
				
				if(((Listitem)mintsListBxTd.getLastChild()).getLabel().equals(listitem.getLabel())) {
					mintsListBxTd.setSelectedIndex(0);//Item(listitem); 
					if(currentHourInt == 23) {
						currentHourInt = 00;
					}
					else{
						currentHourInt = 	currentHourInt +1;
					}
					break;
//					setHourfalg = true;
				}
				continue;
				
				
				/*tempItem = listitem;
				if(((Listitem)mintsListBxTd.getLastChild()).getLabel().equals(listitem.getLabel())) {
					mintsListBxTd.setSelectedItem(listitem); 
				}else continue;*/
			}else if(currentMintint <= tempInt){
				//else continue;
				mintsListBxTd.setSelectedItem(listitem); break;
			}
			
		} // for min
		
		List<Listitem> hoursListItemList = hoursListBxId.getItems();
		int tempListItemHour = 0;
		
		for (Listitem  listitem : hoursListItemList) {
			tempListItemHour = Integer.parseInt(listitem.getLabel().trim());
			if(tempListItemHour == currentHourInt) {
				hoursListBxId.setSelectedItem(listitem);
			}
		} // for  hours
		
		
		
		
		/*if(temCal.get(Calendar.AM_PM) == 0) {
			AMPMListBxTd.setSelectedIndex(0);
		}else AMPMListBxTd.setSelectedIndex(1);*/
		
	} // defaultSetTime
	
	/**
	 * added as a part of conversion to GenericForwardComposer
	 * @return
	 */

	public Campaigns getCampaign() {
		
		//Campaigns campaign = (Campaigns)session.getAttribute("campaign");
		
		if(campaign == null){
			
			//Redirect.goTo(org.mq.marketer.campaign.general.PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(org.mq.marketer.campaign.general.PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}
		
		if( campaign.getCategories() != null ){
			
			Long catId= campaign.getCategories();
			UserCampaignCategories userCmapObj =userCampaignCategoriesDao.findByCatId(catId, currentUser.getUserId());
			if(userCmapObj != null){
				
				String categoryName =userCmapObj.getCategoryName();
				campaign.setCategoryName(categoryName);
				
			}
			
			
		}
		
		
		return campaign;
		
	}//getCampaign()
	
	public void onClick$backBtnId() {
		
		
		PageUtil.goToPreviousPage();
		
	}//onClick$backBtnId()
	
	//private Html html;
	private Window previewWin,previewIframeWin,viewSegRuleWinId;
	private Label viewSegRuleWinId$segRuleLblId;
	public void onClick$textPreviewImgId() {
		
		
		if(campaign != null && campaign.getTextMessage() != null && !campaign.getTextMessage().trim().isEmpty()) {
			
			Html html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
			String textMsg="<html><body><pre>"+campaign.getTextMessage() +"</pre></body></html>";
			html.setContent(textMsg);
			previewWin.setVisible(true);
			
		}else{
			
			MessageUtil.setMessage("No text content is available for this campaign.", "color:blue.");
			
		}
		
	}
	
	private  Iframe previewIframeWin$iframeId;
	public void onClick$htmlPreviewImgId() {

			if(campaign != null && campaign.getHtmlText() != null && !campaign.getHtmlText().trim().isEmpty()) {
				
				/*Html html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
				html.setContent(campaign.getHtmlText());*/
				// Iframe  html = ( Iframe )previewIframeWin.getFellow("contentDivId").getFellow("html");
				
				 String htmlContent=campaign.getHtmlText();
				 
				 if(htmlContent.contains("href='")){
					 htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");	
					}
				if(htmlContent.contains("href=\"")){
					htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
					}
				 Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
				 previewIframeWin.setVisible(true);
				
			}else{
				
				MessageUtil.setMessage("No html content is available for this campaign.", "color:blue.");
				
			}

		}
	
	/**
	 * creates the campaignSchedule object with the specified date </BR>
	 * if with the same date no any schedule is existed.
	 * @param selectedDtCal
	 * @param parentId
	 * @param criteria
	 * @return CampaignSchedule object
	 */
	private CampaignSchedule addDate(Calendar selectedDtCal, 
						CampaignSchedule parentCampSchedule, byte criteria) {	
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered---------");
		}
		selectedDtCal.set(Calendar.SECOND,0);
		selectedDtCal.set(Calendar.MILLISECOND,0);

		Calendar tempCal = Calendar.getInstance();
		tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		
		tempCal.setTime(selectedDtCal.getTime());
		logger.info("######==========>"+tempCal.get(Calendar.HOUR_OF_DAY));
		Long csId = campaignScheduleDao.getCurrentId();
		
		if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>> Generated Id :"+csId);
		}
		
		CampaignSchedule campSchedule = new CampaignSchedule(csId, tempCal, criteria);
		
		if(parentCampSchedule != null) {
			
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>> parent Id :"+parentCampSchedule.getCsId());
			}
			
			campSchedule.setParentId(parentCampSchedule.getCsId());
			campSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
		} 
		else {
			campSchedule.setResendLevel((byte)0);
		}// else if parentCampSchedule
		
		if(campScheduleList.contains(campSchedule)) {
			return null;
		}

		campSchedule.setCampaignId(campaign.getCampaignId());
		campSchedule.setStatus((byte)0);
		campSchedule.setUser(GetUser.getUserObj());
		
		campScheduleList.add(campSchedule);
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- before returning---------");
		}
		return campSchedule;
		
	}//addDate
	
	/**
	 * Creates the list of CampaignSchedule objects with the given frequency and if any</BR>
	 * date is already scheduled ignores that CampaignSchedule.
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List of CampaignSchedule objects
	 */
	private List<CampaignSchedule> addDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered---------");
		}
		
		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
//			MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
			return null;
		}
		
		List<CampaignSchedule> csList = new ArrayList<CampaignSchedule>();
		
		CampaignSchedule startDtCS = new CampaignSchedule(campaignScheduleDao.getCurrentId(), 
				startDtCal, campaign.getCampaignId(), (byte)0, (byte)0 );
		startDtCS.setResendLevel((byte)0);
		startDtCS.setUser(GetUser.getUserObj());
		
		// add the starting date in the schedule final list 
		// if it doesn't contain with the same date
		
		int count = activeCampaingsListlbId.getItemCount();
		boolean isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false);
		//createDivUpComingCampaigns(true,startDtCS);
		logger.info("campSchedule "+startDtCS.getCsId()+": isCreditOrExipry :"+isCreditOrExipry);
		if(isCreditOrExipry == false){
			logger.error("Your Credit Limits are Exipred please contact support");
			return null;
		}
		/*else{
			createDivUpComingCampaigns(true,startDtCS);
			logger.info("**campSchedule "+startDtCS.getCsId()+": isCreditOrExipry :"+isCreditOrExipry);
		}*/
		
		if( !campScheduleList.contains(startDtCS) ) {
			campScheduleList.add(startDtCS);
			csList.add(startDtCS);
		}
		
		CampaignSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());
		
		nextDate.set(frequency, startDtCal.get(frequency)+step);
		
		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {
			
			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());
			
			nextDtCS = new CampaignSchedule(campaignScheduleDao.getCurrentId(), 
					nextDateTemp, campaign.getCampaignId(), (byte)0, (byte)0);
			
			 count = activeCampaingsListlbId.getItemCount();
			 isCreditOrExipry   = createRowUpComingListBox(nextDtCS,count,false);
			 
			logger.info("campSchedule "+nextDtCS.getCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				break;
			}
			else{
				//createDivUpComingCampaigns(true,nextDtCS);
				logger.info("**campSchedule "+nextDtCS.getCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			}
			
			nextDtCS.setResendLevel((byte)0);
			nextDtCS.setUser(GetUser.getUserObj());
			
			if( !campScheduleList.contains(nextDtCS) ) {
				csList.add(nextDtCS);
				campScheduleList.add(nextDtCS);
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);
			
			
		}// while
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting---------");
		}
		return csList;
		
	}//addDates
	
	private void removeCampScheduleFromList(CampaignSchedule campSchedule) {
		
		try {
			
			campScheduleList.remove(campSchedule);
			
			CampaignSchedule tempCS;
			for (Iterator<CampaignSchedule> iterator = campScheduleList.listIterator();
														iterator.hasNext();) {
				tempCS = iterator.next();
				
				if(tempCS.getParentId() != null && 
						(tempCS.getParentId().intValue() == campSchedule.getCsId().intValue())) {
					
					removeCampScheduleFromList(tempCS);
				}
				
			}
		} 
		catch (Exception e) {}
		
	}// removeCampScheduleFromList()
	
	/*private void loadSchedule() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("----------- just entered---------");
		}
			
		if(campScheduleList == null || campScheduleList.size() == 0) return;
		
		if(logger.isDebugEnabled()) {
			
			logger.debug(" No of the schedules loaded from DB :"+campScheduleList.size());
		}
		
		for(CampaignSchedule campaignSchedule: campScheduleList) {
			createRow(campaignSchedule, null,true);
		} // for each
		
		Row tempRow;
		
		for(CampaignSchedule campaignSchedule: campScheduleList) {
			
			tempRow = rowMap.get(campaignSchedule.getCsId());
			if(campaignSchedule.getParentId() == null) {
				tempRow.setParent(schedGrdRowsId);
			}
			else {
				tempRow.setParent(rowsMap.get(campaignSchedule.getParentId()));
			}
			
		} // for each

	}// loadSchedule()
*/	public Long getEmailCount(String segmentStr, Set <MailingList> mlset){
		long emailCount=0;
		long totalCount = contactsDao.getAllEmailCount(mlset);
		long unpurgedCount = contactsDao.getAllUnpurgedCount(mlset);
		if(segmentStr.startsWith("Segment")) {
			
			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
			
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				
				return null;
				
				
			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {
				
				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}
				
				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				//ClickHouse changes
				if(!currentUser.isEnableClickHouseDBFlag())
					segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);
				else
					segmentStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);
				
				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
					
					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {
								
								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();
								
							}
						}
						
						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}
				
				
				logger.info(" Generated Query :"+segmentStr);
				if(!currentUser.isEnableClickHouseDBFlag()) {					
					if(tempQry.length() > 0) tempQry += " UNION ";
				}else {
					if(tempQry.length() > 0) tempQry += " UNION ALL ";
				}
				
				tempQry += segmentStr;
//				contactsDao.insertSegmentedContacts(tempQry);
				
				
				
				
			}//for
			
			if(!currentUser.isEnableClickHouseDBFlag())
				emailCount = contactsDao.getSegmentedContactsCount(tempQry);
			else
				emailCount = contactsDao.getSegmentedContactsCountFromCH(tempQry);
			
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}		 
			
			if(emailCount == 0) {
				
				MessageUtil.setMessage( "Your segment returned 0 active unique contacts of "+ totalCount + " available contacts.","color:blue", "TOP");
				//return null;
				//MessageUtil.setMessage("Your segment returned 0 active unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				//return false;
			}
			
		
		}//if segment
		else {
			emailCount = contactsDao.getUniqueCount(mlset,"Active");
			
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}
			
			if(emailCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 active unique contacts of "+ totalCount + " available contacts. ", "color:blue", "TOP");
				//return null;
			}
		}
		
		
		return emailCount;
	}
	
	/**
	 * This method save's campaign to persistent storage.
	 * @param draftStatus
	 */
	private void saveCampaign(boolean draftStatus){
		Boolean flagInsufficientFund = draftStatus;
		logger.debug(">>>>>>> Started  saveCampaign   :: ");
		schErrorLblId.setValue("");
		Calendar currentCal = Calendar.getInstance();
		String segmentStr = campaign.getListsType();
		boolean insufficientFunds = false;

		Set <MailingList> mlset = campaign.getMailingLists();
		if(segmentStr != null && mlset == null || mlset.size() == 0) {
			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it." +
					"\n Please configure at least one mailing list / segment.", "color:red;");
			return;
		}
		else if(segmentStr == null && mlset == null || mlset.size() == 0){

			MessageUtil.setMessage("Campaign is not configured to any other mailing list."
					+ "\n Please configure at least one mailing list.", "color:red;");
			return;
		}
		
		/* method to check if email credit score is negative or not*/
		Long emailCount =getEmailCount(segmentStr ,mlset);
		if(currentUser != null && !flagInsufficientFund){
            SubscriptionDetails subDetails = new SubscriptionDetails();
            int userAvailableEmlCount = subDetails.getEmailStatus(Calendar.getInstance());
            Double result = (double) Math.signum(userAvailableEmlCount);
            if(result.equals(-1.0) || result.equals(0.0)) {
            	Messagebox.show("This schedule(s) will be in \"Paused\" status due to insufficient email credits balance.\n" +
            	"Please contact Support team to add email credits to your account.\n "+
            	"Note: Schedule(s) will be automatically activated once credits are added.", "Warning", Messagebox.OK, Messagebox.INFORMATION);
                flagInsufficientFund = true;
                insufficientFunds = true;
                Calendar now = Calendar.getInstance();
                campScheduleList.stream().forEach(campSchedule ->{
                	if(campSchedule.getScheduledDate().after(now)) {
                		campSchedule.setStatus((byte) 3);
                	}
                });
                /*SupportController support = new SupportController();
                SupportTicket ticket = new SupportTicket();
                ticket.setClientName(currentUser.getFirstName());
                ticket.setContactName(currentUser.getFirstName());
                ticket.setContactEmail(currentUser.getEmailId());
                ticket.setContactPhone(currentUser.getPhone());
                ticket.setProductArea(Constants.STRING_NILL);
                ticket.setDescription("available email credit limit is low");
                ticket.setType((byte) 3);
                ticket.setProductAreaType((byte) 1);
                support.sendMailToSupport(ticket);*/
                logger.info("mail to support team.");
            }
        }
		
		
		// Added for  campagn category
		/*if(!currentUser.getSubscriptionEnable()){

			MessageUtil.setMessage("You have not  enabled subscriber preference center setting. \n" +
					" To continue, please go to the subscriber preference center setting and enable  settings.", "color:red");
			return;
		}*/

		if(currentUser.getSubscriptionEnable() && campaign.getCategories() == null ){

			MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
					"\n To continue, please go to the 1st step and select a campaign category first.", "color:red");
			return;

		}

		Long categoryId= campaign.getCategories();
		UserCampaignCategories userCmapObj = null;
		if(categoryId != null){

			userCmapObj = userCampaignCategoriesDao.findById(categoryId.toString());
			//logger.debug("userCmapObj is "+userCmapObj);logger.debug("currentUser is "+currentUser);
			if(currentUser.getSubscriptionEnable()  && !userCmapObj.getIsVisible()){

				MessageUtil.setMessage("Please set the campaign category.", "color:red;");
				return;
			}
		}
		//Check if any Promos are Expiry/Pause in the campaign
		try {
			if(campaign.getHtmlText() == null || campaign.getHtmlText().isEmpty() ||
				(campaign.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && !(campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_HTML)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||campaign.getJsonContent().contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)))){
				MessageUtil.setMessage("Email campaign content cannot be empty.", "color:red;");
				return;
			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(campaign.getHtmlText(), currentUser);
			if(isValidCouponAndBarcode != null){
				return;
			}
			Set<String> subjectSymbolSet = Utility.getPhSet(campaign.getHtmlText());
			Set<String> ccPhSet = Utility.getBarcodePhset(campaign.getHtmlText());
			if(ccPhSet != null){
				subjectSymbolSet.addAll(ccPhSet);
			}

			CouponsDao couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
			String couponIds = "";


			/*if(subjectSymbolSet != null && subjectSymbolSet.size() > 0) {

				for(String phString : subjectSymbolSet){
					String[] phStr = phString.split("_");
					logger.info("ph str is "+phStr);
					if(!phStr.equals("updatePreferenceLink")){

						couponIds += couponIds.trim().length() > 0 ? ","+phStr[1] :phStr[1];
					}

				}
			}*/
			if(subjectSymbolSet != null && subjectSymbolSet.size() > 0) {

				for(String phString : subjectSymbolSet){
					if(!phString.startsWith("CC_"))continue;
					String[] phStr = phString.split("_");
					logger.info("ph str is "+phStr);
					

					couponIds += couponIds.trim().length() > 0 ? ","+phStr[1] :phStr[1];

				}
			}


			//
			campaign.setDraftStatus("complete");
		//	APP-3501 
			int activeCount = 0;
			int sentCount = 0;
			byte expiredCount = 0;

			UserCampaignExpiration triggeredEmail = getTriggeredAlertEmail();
			Calendar expiredOn = null;
			if(triggeredEmail != null){

				expiredOn = triggeredEmail.getSentOn();
				if(expiredOn != null)
				expiredOn.add(Calendar.DAY_OF_YEAR, 7);

			}

			int expiredSchedules = 0 ;
			List<String> expriedScheduleDates = new ArrayList<String>();
			List<CampaignSchedule> activeList = new ArrayList<CampaignSchedule>();
			
			for(CampaignSchedule campSchedule:campScheduleList) {

				//if campSchedule status is sent no need to do anything
				if(campSchedule.getStatus() == 1) {
					sentCount++;
					logger.info("Campaign Status is Sent No Need to do Anything.");
					continue;
				}

				//if draft and campSchedule status is active changing it to draft
				if(draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
					campSchedule.setStatus((byte)2);
					if(expiredOn != null && campSchedule.equals(expiredOn)){

						//find the schedule which is last but one in the periodical schedules
						triggeredEmail.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_DRAFT);
						triggeredEmail.setModifiedDate(Calendar.getInstance());
						userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

					}
					continue;
				}

				//if submit and campSchedule status is draft changing it to active
				boolean isDraft =false;
				if(!draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
					//campSchedule.setStatus((byte)0);
					//activeCount++;
					//TODO if status is draft schedule date is before current date make status as expired.

					if( campSchedule.getStatus() == 2 && campSchedule.getScheduledDate().before(currentCal)){
						logger.info("Setting Status as Expired");
						isDraft = true;
						campSchedule.setStatus((byte)6);
						expiredSchedules ++;
						logger.info("expiredSchedules after"+expiredSchedules);
						//expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATEONLY));
						expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STYEAR));

						expiredCount++;
					}
					else{
						logger.info("Setting Status as Active");
						campSchedule.setStatus((byte)0);
						activeCount++;
						activeList.add(campSchedule);
					}

					if(campSchedule.getStatus() == 2 && expiredOn != null && campSchedule.equals(expiredOn)){

						//find the schedule which is last but one in the periodical schedules
						Calendar now = Calendar.getInstance();
						int onOrAfter = now.compareTo(campSchedule.getScheduledDate()) ;
						String status = onOrAfter <= 0 ? OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE : 
							OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_EXPIRED;
						triggeredEmail.setStatus(status);
						triggeredEmail.setModifiedDate(Calendar.getInstance());
						userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

					}
				}
				boolean flag = ((campSchedule.getStatus() == 0) && campSchedule.getScheduledDate().before(currentCal)); 

				// checks whether the scheduled date is less than current date.
				if(flag  || (!flag && isDraft)){
					if(campSchedule.getStatus() == 0 &&
							campSchedule.getScheduledDate().before(currentCal)) {
						logger.info("campSchedule.getScheduledDate() :::::;"+MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_SCHEDULE_TIME));
						logger.info("currentCalcurrentCal :::"+MyCalendar.calendarToString(currentCal, MyCalendar.FORMAT_SCHEDULE_TIME));
						schErrorLblId.setValue("Schedule dates can not be past dates");
						/*MessageUtil.setMessage("Schedule dates cannot be past dates.", 
							"color:red", "TOP");*/
						return;
					}
				}

				if(campSchedule.getStatus() != 1 && campSchedule.getCrId() == null  
						&& couponIds != null && couponIds.trim().length() >0){
					String scheduleDate =MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
					List<Coupons> coupList = couponsDao.isValidCoupScheduledInCamp(couponIds,currentUser.getUserOrganization().getUserOrgId(),scheduleDate);
					if(coupList != null && coupList.size() >0){
						MessageUtil.setMessage(" Campaign was not sent as discount code expired on date:"+scheduleDate, 
								"color:red", "TOP");
						return;
					}
				}
				/*
				if (finalScheduleCal == null)finalScheduleCal =  campSchedule.getScheduledDate();
				else if(finalScheduleCal.before(campSchedule.getScheduledDate())) finalScheduleCal = campSchedule.getScheduledDate();*/

			} // for campSchedule

			long totalCount = 0l;
			int tempActiveCountstr =1;
			if(activeCount > 0){

				if(emailCount!=null)totalCount =(emailCount.longValue()) * activeCount;
				tempActiveCountstr = activeCount;
			}else{
				if(emailCount!=null)totalCount = emailCount.longValue();
				tempActiveCountstr=1;
			}
			CouponCodesDao couponCodesDao = (CouponCodesDao)SpringUtil.getBean("couponCodesDao");
			//	if(totalCount > 0){


			if(!couponIds.isEmpty()){

				List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIds,currentUser.getUserOrganization().getUserOrgId());


				//logger.debug("promo size is  :: "+coupList.size());
				//Long availPromoCount = 0l;

				if(coupList != null && coupList.size() >0){

					for (Coupons eachPromoObj : coupList) {
						logger.debug("eachPromoObj.getAvailable() is  ::"+eachPromoObj.getAvailable());

						if(!eachPromoObj.getAutoIncrCheck()){
							long count = couponCodesDao.findIssuedCoupCodeByCoup(eachPromoObj.getCouponId());
							logger.debug("count is "+count);
							long availCCount = eachPromoObj.getTotalQty() - count;

							if(availCCount < emailCount) {
								MessageUtil.setMessage("Email cannot be sent as configured  "+eachPromoObj.getCouponName()+" available promo count" +
										" \n exceeds the email send limit.Please  increase Discount codes issued limit.", "color:red;");
								return;
							}
						}

					}

				}

			}

			
			if(activeCount == 0 && !(draftStatus) && !insufficientFunds) {

				try {
					if( Messagebox.show("There are no active schedules. " +
							"Do you want to continue?", "Confirm", 
							Messagebox.OK|Messagebox.CANCEL, Messagebox.QUESTION)
							== Messagebox.CANCEL) {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
			}

			if(draftStatus) {
				campaign.setStatus(Constants.CAMP_STATUS_DRAFT);
				campaign.setDraftStatus("CampFinal");
				campaign.setStatusChangedOn(Calendar.getInstance());
				if(triggeredEmail != null) {
					triggeredEmail.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_DRAFT);
					triggeredEmail.setModifiedDate(Calendar.getInstance());
					userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);
				}


			}
			else if(activeCount >0 && sentCount > 0){
				campaign.setStatus(Constants.CAMP_STATUS_RUNNING);
			}
			else if(activeCount == 0 && sentCount > 0) {
				campaign.setStatus(Constants.CAMP_STATUS_SENT);
			}else if(activeCount >0 && sentCount == 0){
				campaign.setStatus(Constants.CAMP_STATUS_ACTIVE);
				campaign.setStatusChangedOn(Calendar.getInstance());
			}
			
			if(flagInsufficientFund && insufficientFunds){
                campaign.setStatus(Constants.CAMP_STATUS_INSUFFICIENT_FUND);
            }
			/*if (emailCount == null) {
				//campaignsDao.saveOrUpdate(campaign);
				campaignsDaoForDML.saveOrUpdate(campaign);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}*/
			//campaignsDao.saveOrUpdate(campaign);
			campaignsDaoForDML.saveOrUpdate(campaign);

			logger.info("#########active count is##########" +activeCount);

			//logger.debug("delete count ::"+deleteCount);

			// check if credits  sufficient or not
			//}
			/*if(currentUser != null){

				SubscriptionDetails subDetails = new SubscriptionDetails();
				int userAvailableEmlCount = subDetails.getEmailStatus(Calendar.getInstance());

				logger.debug("User available limit is :"+userAvailableEmlCount + " UserId:"+currentUser.getUserId() +" totalCount"+totalCount);
				if(userAvailableEmlCount == -1) {
					//MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
					return ;
				}	 
				else if(totalCount > userAvailableEmlCount) {
					MessageUtil.setMessage(tempActiveCountstr + " active schedule(s) found configured to " + totalCount + " email addresses. Your available email credit limit is " + userAvailableEmlCount +".\n " +
							"Please contact support@optculture.com to request additional credits.", "color:red", "TOP");
					return ;
				}

			}*/


			//int deleteCount = campaignScheduleDao.deleteByCampaignId(campaign.getCampaignId());
			int deleteCount = campaignScheduleDaoForDML.deleteByCampaignId(campaign.getCampaignId());

			/*if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
				MessageUtil.setMessage(activeCount+" schedule(s) between "+expriedScheduleDates.get(0)+" "
						+ "to "+expriedScheduleDates.get(expriedScheduleDates.size()-1)+""
								+ " have been activated\n while "+expiredSchedules+" "
										+ "unsent, past schedule(s) between "+expriedScheduleDates.get(0)
										+" & "+expriedScheduleDates.get(expriedScheduleDates.size()-1)+" have expired.",  "color:blue");
			}*/

	
			
			if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
				MessageUtil.setMessage(getRequiredMessage(expriedScheduleDates, activeList, expiredSchedules, activeCount),  "color:blue");
			}
			
			
			campaignScheduleDaoForDML.saveByCollection(campScheduleList);

			if(!draftStatus ){
				//Added for 2.3.11
				Calendar endCal = (Calendar)frqBtnId.getAttribute(LATEST_SCH_ON);
				Calendar startCal = (Calendar)frqBtnId.getAttribute(START_SCH_ON);
				//String frequency = (String)frqBtnId.getAttribute(FREEQUENCY);

				if( endCal != null) {
					logger.debug("is there triggered email??????"+endCal.get(Calendar.DATE));

					triggerExpirationAlertEmail(endCal, startCal);
				}
			}
			//end
			userActivitiesDao = (UserActivitiesDao) SpringUtil.getBean("userActivitiesDao");
			userActivitiesDaoForDML = (UserActivitiesDaoForDML) SpringUtil.getBean("userActivitiesDaoForDML");
			/*if(userActivitiesDao != null) {
				ActivityEnum activityType = (draftStatus) ? ActivityEnum.CAMP_CRE_SUBMIT_p1campaignName : ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName;
				userActivitiesDao.addToActivityList(activityType, GetUser.getUserObj(),campaign.getCampaignName());
			}*/
			if(userActivitiesDaoForDML!= null) {
				ActivityEnum activityType = (!draftStatus) ? ActivityEnum.CAMP_CRE_SUBMIT_p1campaignName : ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName;
				userActivitiesDaoForDML.addToActivityList(activityType, GetUser.getLoginUserObj(),campaign.getCampaignName());
			}
		} 
		catch (Exception e) {
			logger.error("** Exception : while saving the campaign schedule objects", e);

		}

		logger.debug("-- Exit --");
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
		Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);

	}// saveCampaign

	/*private String getRequiredMessage(List<String> expriedScheduleDates, List<CampaignSchedule> passList, int expiredSchedules, byte activeCount){
		
		StringBuffer reqdMSgStrBfr = new StringBuffer();
		
		if(expiredSchedules == 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated\nwhile" +
					" 1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules == 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile"+
					" 1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
			
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
			
		}else if(expiredSchedules == 0 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated");
			
		}else if(expiredSchedules == 0 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated ");
			
		}else if(expiredSchedules == 1 && activeCount == 0){
			
			reqdMSgStrBfr.append("1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 0){
			
			reqdMSgStrBfr.append(expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
		}*/
//App-3501
	private String getRequiredMessage(List<String> expriedScheduleDates, List<CampaignSchedule> passList, int expiredSchedules, int activeCount){
		StringBuffer reqdMSgStrBfr = new StringBuffer();
		
		if(expiredSchedules == 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated\nwhile" +
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules == 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "
			+MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile"+
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+
			" has been activated\nwhile "+expiredSchedules+" unsent, past schedules between "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount > 1){
			logger.info("expiredSchedules and expriedScheduleDates "+expiredSchedules,expriedScheduleDates);
			/*reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");*/
			
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules == 0 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated");
			
		}else if(expiredSchedules == 0 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated ");
			
		}else if(expiredSchedules == 1 && activeCount == 0){
			
			reqdMSgStrBfr.append("1 unsent, past schedule on "+  MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 0){
			
			reqdMSgStrBfr.append(expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");
		}
		
		
		logger.info("reqdMSg >>> "+reqdMSgStrBfr);
		
		return reqdMSgStrBfr.toString();
	}
	private UserCampaignExpiration getTriggeredAlertEmail(){

		if(userCampaignExpirationDao == null) {

			try {
				userCampaignExpirationDao = (UserCampaignExpirationDao)ServiceLocator.
						getInstance().getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
				userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)ServiceLocator.
						getInstance().getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
		}

		List<UserCampaignExpiration> existingTriggerList = userCampaignExpirationDao.
				findBy(campaign.getUsers().getUserId(), campaign.getCampaignId());

		UserCampaignExpiration userCampaignExpiration = null;

		if(existingTriggerList != null && existingTriggerList.size() > 0){

			userCampaignExpiration = existingTriggerList.get(0);

		}

		return userCampaignExpiration;
	}

	private void triggerExpirationAlertEmail(Calendar endCal, Calendar startCal) {

		try {
			//trigger alert mail on campaign expiration
			logger.debug("in triggerExpirationAlertEmail=========");
			UserCampaignExpiration userCampaignExpiration = getTriggeredAlertEmail();
			Calendar expireOn = Calendar.getInstance();
			expireOn.setTime(endCal.getTime());
			expireOn.add(Calendar.DAY_OF_YEAR, -7);
			long differencenDays = 0;

			Calendar now = Calendar.getInstance();
			differencenDays = ((endCal.getTimeInMillis()/(1000 * 60 * 60 * 24))-(now.getTimeInMillis()/(1000 * 60 * 60 * 24))) ;

			if(userCampaignExpiration == null && differencenDays >= 30) {

				logger.debug("in triggerExpirationAlertEmail=========null");
				//String msg = prepareAlertMailContent();

				userCampaignExpiration = new UserCampaignExpiration(Calendar.getInstance(),
						Calendar.getInstance(),startCal, endCal,  expireOn, campaign.getUsers().getUserId(),
						campaign.getCampaignId(), OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE,
						OCConstants.USER_ALERT_EMAIL_TYPE_CAMP_EXPIRATION );


			}else{
				logger.debug("in triggerExpirationAlertEmail=========!null");
				/*Calendar now = Calendar.getInstance();
				if(now.after(expireOn)){
					//TODO

				}*/
				userCampaignExpiration.setModifiedDate(now);
				userCampaignExpiration.setSentOn(expireOn);
				userCampaignExpiration.setEndDate(endCal);
				//userCampaignExpiration.setFreequency(freequency);

			}
			logger.debug("in triggerExpirationAlertEmail=========saving");
			userCampaignExpirationDaoForDML.saveOrUpdate(userCampaignExpiration);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Expect",e);
		}


	}

	
	private void setMlistLinks() {
		if(campaign == null) return;
		 
		
		
		if(campaign.getListsType().startsWith("Segment:"))
		{
			String segments=campaign.getListsType();
			segments=segments.replace("Segment:", "");
			Hbox mlHbox = new Hbox();
			mlHbox.setSpacing("10px");
			List<SegmentRules> segmentRules=segmentRulesDao.findById(segments);
			A mlLink;
			if(segmentRules!=null){
			for(SegmentRules segmentRule:segmentRules)
			{
				mlLink = new A(segmentRule.getSegRuleName());
				mlLink.setAttribute(ATTRIBUTE_SOURCE, segmentRule);
				mlLink.setStyle("display:inline-block;");
				mlLink.addEventListener("onClick", new MyListener());
				mlLink.setParent(mlHbox);
				
			}
			}
			mlHbox.setParent(listNamesDivId);
			recipentsSourceLblId.setValue(" segment(s)");
		}
		else	
		{
		Hbox mlHbox = new Hbox();
		mlHbox.setSpacing("10px");
		Set<MailingList> mlset = campaign.getMailingLists();
		A mlLink;
		for (MailingList mailingList : mlset) {
			mlLink = new A(mailingList.getListName());
			mlLink.setAttribute(ATTRIBUTE_SOURCE, mailingList);
			mlLink.setStyle("display:inline-block;");
			mlLink.addEventListener("onClick", new MyListener());
			mlLink.setParent(mlHbox);
		}
		mlHbox.setParent(listNamesDivId);
		}
		
	}
	/******************ZK methods*************/
	
	public void onClick$prtDtBtnId() {
		
		TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		//Calendar temCal = new  MyCalendar(clientTimeZone);
//		Calendar cal = Calendar.getInstance();
		schErrorLblId.setValue("");
		try {	// logger.info("prtDtBxId.getValue()  :"+prtDtBxId.getValue());
			    logger.info("prtDtBxId.getServerValue()  :"+prtDtBxId.getServerValue());
			    logger.info("prtDtBxId.getClientValue()  :"+prtDtBxId.getClientValue());
			 if(prtDtBxId.getValue() == null) {
				 schErrorLblId.setValue(" Please select the date");
				 return;
			 }
				


			 MyCalendar tempCal= (MyCalendar)prtDtBxId.getClientValue();
			 //MyCalendar tempCal= (MyCalendar)prtDtBxId.getServerValue();
			 //logger.info("tempCal.getTimeInMillis()  :"+tempCal.getTimeInMillis());
			 tempCal.setTimeZone(clientTimeZone);

			 tempCal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hoursListBxId.getSelectedItem().getLabel().trim()));
			 tempCal.set(Calendar.MINUTE, Integer.parseInt(mintsListBxTd.getSelectedItem().getLabel().trim()));
			 //logger.info("get Hour :" +tempCal.get(Calendar.HOUR_OF_DAY));
			 //cal = prtDtBxId.getServerValue();
			 //logger.info("tempCal.getTimeInMillis()  ::"+tempCal.getTimeInMillis());
			 addDateToGrid(tempCal);
		} 
		catch (Exception e) {
			logger.warn("Exception : Date is not selected" , e);
//			MessageUtil.setMessage("Please select the date.", "color:red", "TOP");
			schErrorLblId.setValue("Please select the date");
			return;
		}
		
	}
	
	
	
	
	public void onClick$sendNowButtonId() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		logger.info("Time Zone is ::"+cal.getTimeZone());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+ 5);
		addDateToGrid(cal);
	}
	
	private void addDateToGrid(Calendar cal) {

		Calendar currCal = Calendar.getInstance();
		currCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		
		if(cal == null || cal.compareTo(currCal) < 0){
			schErrorLblId.setValue(" Schedule date/time should be later than current date/time.");
//			MessageUtil.setMessage("Date should be future date.", "color:red", "TOP");
			return;
		}
		/*Commented for 2.3.11
		 * CampaignSchedule campSchedule = addDate(cal, null,(byte)0);
		
		if(campSchedule == null) {
			schErrorLblId.setValue("Date already scheduled");
//			MessageUtil.setMessage("Date already scheduled.", "color:red", "TOP");
			return;
		}*/
		
		
		//New For Same date + time schedule should not be allowed in every scenario 
		
		CampaignSchedule campSchedule1 = addDateCheck(cal, null,(byte)0);

		if(campSchedule1 == null) {
			//schErrorLblId.setValue("Date already scheduled");
			//MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
			return;
		}
		
		
		int confirm = 0;
		boolean isSameDay = false;
		List<String> dates = new ArrayList<String>();
		for (CampaignSchedule campSched : campScheduleList) {
			
			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 
			
			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					||  campSched.getStatus() == 2 )){
				dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
			}
		}
		
		for (CampaignSchedule campSched : campScheduleList) {

			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 
			
			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					 || campSched.getStatus() == 2 )){
				
				confirm = Messagebox.show("Schedule(s) already exist on "+ MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
						+ " for following time:"
						+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
						"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				
				isSameDay = true; 
				break;
			}	
		}
		
		if(confirm == 2){
			logger.debug("Message box cancel is clicked.");
			return;
		}
		
		if(!isSameDay ||(confirm == 1 && isSameDay)) {

			CampaignSchedule campSchedule = addDate(cal, null,(byte)0);

			if(campSchedule == null) {
				//schErrorLblId.setValue("Date already scheduled");
				MessageUtil.setMessage("Date already scheduled.", "color:red", "TOP");
				return;
			}

			//createRow(campSchedule, schedGrdRowsId,false);
			//TODO Create ListBox First element
			int count = activeCampaingsListlbId.getItemCount();
			boolean isCreditOrExipry = createRowUpComingListBox(campSchedule,count,false);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				return;
			}
			//newlyAddedSchedule.add(campSchedule);
			persistentCamp = false;
			createDivUpComingCampaigns(true,campSchedule);
			MessageUtil.setMessage("You have added 1 schedule to be sent on "+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+".\n All your active schedules can be viewed by clicking on \n \'View All Upcoming Schedules\' link.", "color:blue");
		}
	}
	
	
	private CampaignSchedule addDateCheck(Calendar selectedDtCal, 
			CampaignSchedule parentCampSchedule, byte criteria) {
		
		selectedDtCal.set(Calendar.SECOND,0);
		selectedDtCal.set(Calendar.MILLISECOND,0);

		Calendar tempCal = Calendar.getInstance();
		tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		
		tempCal.setTime(selectedDtCal.getTime());
		logger.info("######==========>"+tempCal.get(Calendar.HOUR_OF_DAY));
		Long csId = campaignScheduleDao.getCurrentId();
		
		
		CampaignSchedule campSchedule = new CampaignSchedule(csId, tempCal, criteria);
		
		if(parentCampSchedule != null) {
			campSchedule.setParentId(parentCampSchedule.getCsId());
			campSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
		} 
		else {
			campSchedule.setResendLevel((byte)0);
		}// else if parentCampSchedule
		
		if(campScheduleList.contains(campSchedule)) {
			MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
			return null;
		}



		int available = 0;
		try {
			SubscriptionDetails subDetails = new SubscriptionDetails();
			available = subDetails.getEmailStatus(campSchedule.getScheduledDate());
		} catch (Exception e1) {
			logger.error("** Exception : Problem while getting the subscription details", e1);
			return null;
		}
		/*if(available <=0) {
			MessageUtil.setMessage("Email cannot be scheduled as you have \n " +
					"reached your email credits limit or your account has expired.", 
					"color:red", "TOP");
			return null;
		}*/


		if(campSchedule != null && campSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
			return null;
		}
		
		
		
		campSchedule.setCampaignId(campaign.getCampaignId());
		campSchedule.setStatus((byte)0);
		campSchedule.setUser(GetUser.getUserObj());
		
	//	campScheduleList.add(campSchedule);
		
		
		return campSchedule;
	
	}



	
	private void createDivUpComingCampaigns(boolean flag, CampaignSchedule schedule) {
		logger.debug(">>>>>>> Started  createDivUpComingCampaigns :: ");
		
		
		List<CampaignSchedule> activeOrDraftCampScheduleList = getActiveCampScheduleList(campScheduleList);
		
		CampaignSchedule lastSchedule = getLastSchedule(activeOrDraftCampScheduleList);
		
		if(lastSchedule != null){
			campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
			campActiveTillDateLbId.setAttribute("setLbDate",lastSchedule.getScheduledDate());
			campaignActiveTillDivId.setVisible(true);
			String size = (campScheduleList != null && campScheduleList.size() > 0) ? getSize(campScheduleList) : 1+"";  
			//int allActiveSchedules = getAcitveScheduleSize(campScheduleList);
			
			numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
			numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
			logger.info("persistentCamp Date .....: "+MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
			
		}
		else{
			campaignActiveTillDivId.setVisible(false);
		}
		
		
		/*if(activeOrDraftCampScheduleList.size() > 0){
			
			if(persistentCamp){
			campActiveTillDateLbId.setValue(MyCalendar.calendarToString(activeOrDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
			campActiveTillDateLbId.setAttribute("setLbDate",activeOrDraftCampScheduleList.get(0).getScheduledDate());
			//logger.info("persistentCamp Date .....: "+MyCalendar.calendarToString(activeOrDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
			}
			else{
				
				try {
					Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
				//	logger.info(".........:"+lblDate);
					if(lblDate != null ){
						//Calendar cal = MyCalendar.string2Calendar(lblDate);
					//	logger.info("calcal.....:"+lblDate);
						if(schedule != null && lblDate.before(schedule.getScheduledDate())){
							//logger.info("cal.after(schedule) time ::"+MyCalendar.calendarToString(schedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))); //schedule.getDateStrByTimeZone(MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							campActiveTillDateLbId.setValue(Constants.STRING_NILL);
							campActiveTillDateLbId.setValue(MyCalendar.calendarToString(schedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							campActiveTillDateLbId.setAttribute("setLbDate",schedule.getScheduledDate());
							
						}
						else{
							//logger.info("cal time ::"+MyCalendar.calendarToString(lblDate,MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lblDate,MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							campActiveTillDateLbId.setAttribute("setLbDate",lblDate);
						}
						
					}
					else{
						//logger.info("Setting Camp Schedule first time ::"+MyCalendar.calendarToString(schedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						campActiveTillDateLbId.setValue(MyCalendar.calendarToString(schedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						campActiveTillDateLbId.setAttribute("setLbDate",schedule.getScheduledDate());
					}
					//return true;
				} catch (Exception e) {
					logger.error("Exception :",e);
				}
				
				
					}
			campActiveTillDateLbId.setStyle(OCConstants.FONT_BOLD);

			String size = (campScheduleList != null && campScheduleList.size() > 0) ? getSize(campScheduleList) : 1+"";  
			//int allActiveSchedules = getAcitveScheduleSize(campScheduleList);
			
			numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
			numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
			campaignActiveTillDivId.setVisible(flag);
		}
		campaignActiveTillDivId.setVisible(flag);*/
		logger.debug("<<<<< Completed createDivUpComingCampaigns .");
		
		/*for (CampaignSchedule campaignSchedule : activeOrDraftCampScheduleList) {
			if(campaignSchedule.getStatus() == 2){
			//	campaignActiveTillDivId.setVisible(false);
			}
		}*/
	}

	private CampaignSchedule getLastSchedule(List<CampaignSchedule> scheduleList) {

		CampaignSchedule latestCampaignSchedule = null;

		for (CampaignSchedule campaignSchedule : scheduleList) {
			if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;

			if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
				latestCampaignSchedule = campaignSchedule;
			}
		}


		return latestCampaignSchedule;
	}


	private int getAcitveScheduleSize(	List<CampaignSchedule> campaignScheduleList2) {
		int allActiveSchedules =0;
		for(CampaignSchedule schedule2 : campaignScheduleList2){
			if(true){
				int available = 0;
				allActiveSchedules ++;
				try {
					SubscriptionDetails subDetails = new SubscriptionDetails();
					available = subDetails.getEmailStatus(schedule2.getScheduledDate());
				} catch (Exception e1) {
					logger.error("** Exception : Problem while getting the subscription details", e1);
					
					//return false;
				}
				if(available <=0) {
					logger.info("Email cannot be scheduled as you have \n " +
							"reached your email credits limit or your account has expired.", 
							"color:red", "TOP");
					allActiveSchedules --;
				}
			}

			if(schedule2.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
				logger.info("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
				allActiveSchedules --;
			}
		}
		return (campaignScheduleList2.size() + allActiveSchedules);
	}


	public void onClick$frqBtnId() {

		try {
			schErrorLblId.setValue("");
			Calendar startCal = null;
			Calendar endCal = null;

			try {
				startCal = startDtBxId.getServerValue();
				endCal = endDtBxId.getServerValue();
			} 
			catch (Exception e) {
				logger.warn("Exception : startDate / endDate is not selected" , e);
				schErrorLblId.setValue(" Please select the dates");
				//				MessageUtil.setMessage("Please select the dates.", "color:red", "TOP");
				return;
			}

			if(startCal == null || endCal == null) {
				//				MessageUtil.setMessage("Please select the dates.", "color:red", "TOP");
				schErrorLblId.setValue("Please select the dates");
				return;
			}
			if(startCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("Start schedule date/time should be later than current date/time.");
				//				MessageUtil.setMessage("Start date should be future date.", "color:red", "TOP");
				return;
			}
			if(	endCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("End schedule date/time should be later than current date/time.");
				//				MessageUtil.setMessage("End date should be future date.", "color:red", "TOP");
				return;
			}
			if(endCal.compareTo(startCal) < 0) {
				schErrorLblId.setValue("End schedule date should be later than start schedule date.");
				//				MessageUtil.setMessage("End date should be later than start date.", "color:red", "TOP");
				return;
			}

			if(startDtBxId.getServerValue().compareTo( endDtBxId.getServerValue()) == 0) {
				schErrorLblId.setValue("Start and end schedules cannot be same.");
				//				MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
				return ;
			}

			//TODO Same Day Check

			CampaignSchedule campaignSchedule1 = addDateCheck(startCal, null, (byte)0);

			if(campaignSchedule1 == null){
				//MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
				return ;
			}
			int confirm = 0;
			boolean isSameDay = false;
			List<String> dates = new ArrayList<String>();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
			List<String> nowSch = getSchedulesDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
					Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
					((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
			
			List<CampaignSchedule> activeDraftCampScheduleList =  getActiveCampScheduleList(campScheduleList);
			
			for(String now : nowSch){
				for (CampaignSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					//logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
					}
				}
			}
		
			for(String now : nowSch){
				for (CampaignSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
				//	logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						//dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));

						/*if(campScheduleList != null && campScheduleList.size()>0){*/
						if(activeDraftCampScheduleList != null && activeDraftCampScheduleList.size() > 0){
							confirm = Messagebox.show("Schedule(s) already exist on  "+MyCalendar.calendarToString(activeDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
									+" to " +  MyCalendar.calendarToString(activeDraftCampScheduleList.get(activeDraftCampScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
									+ " for following time: "
									+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
									"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						}
						isSameDay = true; 
						break;
						//}
					}
				}
				if(isSameDay) break;
			}
			logger.info("completed for isSameDay .......:"+isSameDay);
			if(confirm == 2){
				logger.info("returning");
				return;
			}

			

			if(campScheduleList != null && (!isSameDay ||(confirm == 1 && isSameDay))) {
				
				List<CampaignSchedule> campaignScheduleList = 
						addDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
								Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
								((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
				
				if(campaignScheduleList !=null && campaignScheduleList.size() >0){
					createDivUpComingCampaigns(true,campaignScheduleList.get(campaignScheduleList.size()-1));
					MessageUtil.setMessage("You have added "+campaignScheduleList.size()+" schedules to be sent between "
							+ ""+MyCalendar.calendarToString(campaignScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+" to \n"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+".\n All your active schedules can be viewed by clicking on \n  \'View All Upcoming Schedules\' link.", "color:blue");


					frqBtnId.setAttribute(LATEST_SCH_ON, endCal);
					frqBtnId.setAttribute(START_SCH_ON, startCal);
					frqBtnId.setAttribute(FREEQUENCY, frqLbId.getSelectedItem().getLabel());
				}
				else{
					MessageUtil.setMessage("Please select valid date.", "color:red");
					logger.error("Invalid date...........");
				}
			}
		} 
		catch (NumberFormatException e) {
			logger.error("Exception ::", e);
		}
	}
	
	/**
	 * getSchedulesDates
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List<String>
	 */
	private List<String> getSchedulesDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {

		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered getSchedulesDates---------");
		}

		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
			//			MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
			return null;
		}

		List<String> csList = new ArrayList<String>();

		CampaignSchedule startDtCS = new CampaignSchedule(campaignScheduleDao.getCurrentId(), 
				startDtCal, campaign.getCampaignId(), (byte)0, (byte)0 );
		startDtCS.setResendLevel((byte)0);
		startDtCS.setUser(GetUser.getUserObj());

		// add the starting date in the schedule final list 
		// if it doesn't contain with the same date

		if( !campScheduleList.contains(startDtCS) ) {
			//campScheduleList.add(startDtCS);
			csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
		}

		CampaignSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());

		nextDate.set(frequency, startDtCal.get(frequency)+step);

		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {

			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());

			nextDtCS = new CampaignSchedule(campaignScheduleDao.getCurrentId(), 
					nextDateTemp, campaign.getCampaignId(), (byte)0, (byte)0);

			nextDtCS.setResendLevel((byte)0);
			nextDtCS.setUser(GetUser.getUserObj());

			if( !campScheduleList.contains(nextDtCS) ) {
				csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
				//campScheduleList.add(nextDtCS);
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);


		}// while

		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting getSchedulesDates ---------");
		}
		return csList;
	}//getSchedulesDates

	public void onClick$resendBtnId$resendOptionWinId() {
		
		schErrorLblId.setValue("");
		byte criteria = Byte.parseByte(
				(String)resendOptionLbId.getSelectedItem().getValue());
		
		CampaignSchedule campSchedule = (CampaignSchedule)currentRow.getValue();
		Calendar resendTempCal = resendOptionDbId.getServerValue();
		
		if (campSchedule.getScheduledDate().getTimeInMillis() +(60*60*1000) > 
		 										resendTempCal.getTimeInMillis()) {
			
			errMsgLblId.setValue(
					" Resend date must be at least one hour after the actual schedule date");
			errMsgLblId.setVisible(true);
			return;
		}
		
		CampaignSchedule newCampSchedule = addDate(resendTempCal, campSchedule, criteria);
		
		if(newCampSchedule == null) {
			errMsgLblId.setValue("Selected date already exist");
			errMsgLblId.setVisible(true);
			return;
		}
		
		errMsgLblId.setVisible(false);
		resendOptionWinId.setVisible(false);
		/*if(newCampSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())){
			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
			return;
		}*/
		int available = 0;
		try {
			SubscriptionDetails subDetails = new SubscriptionDetails();
			available = subDetails.getEmailStatus(newCampSchedule.getScheduledDate());
		} catch (Exception e1) {
			logger.error("** Exception : Problem while getting the subscription details", e1);
			return;
		}
		
		if(available <=0) {
			MessageUtil.setMessage("Email cannot be scheduled as you have \n " +
					"reached your email credits limit or your account has expired.", 
					"color:red", "TOP");
			return;
		}
		//createRow(newCampSchedule, rowsMap.get(campSchedule.getCsId()),false);
		Detail detail = (Detail)((Rows)rowsMap.get(campSchedule.getCsId())).getParent().getParent(); 
		((Grid)((Rows)rowsMap.get(campSchedule.getCsId())).getParent()).setVisible(true);
		detail.setStyle("display:block;");
		detail.setOpen(true);
	}
	
	public void onClick$saveAsDrftBtnId() {
		//Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
		long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
		if(campScheduleList.size() == 0 || activeCount == 0) {
			saveCampaign(true);
		}else {
			//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules first.", "color:red");
			MessageUtil.setMessage(" A campaign with upcoming schedule/s \n cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
			return;
		}
	}
	
	public Calendar getCurrentTimePlus3Huors() {
		Calendar currentTimePlus3hours = Calendar.getInstance();
		currentTimePlus3hours.add(Calendar.HOUR, +3);
		return currentTimePlus3hours;
	}
	
	public void onClick$saveBtnId(){
		
	/*if(Utility.validateHtmlSize(campaign.getHtmlText())) {
		Messagebox.show("It looks like the content of your email is more " +
				"than the recommended size per the email-" + 
				"sending best practices. " + "To avoid email landing " + 
				"in recipient's spam folder, please redesign " 
							+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		org.zkoss.zul.Messagebox.Button confirm= Messagebox.show(" Do you want to save and schedule \n the campaign? \n Confirm that there are no open \n tabs/browsers with unfinished work.", "Confirm",
				new Messagebox.Button[] {Messagebox.Button.NO, Messagebox.Button.YES },
				new String[] {"Wait for now","Save"},
				Messagebox.INFORMATION, null, null); 
		if(confirm==null || !confirm.equals(Messagebox.Button.YES)) return;
		
		long size =	Utility.validateHtmlSize(campaign.getHtmlText());
		
		if(campaign.isCustomizeFooter()) {
			if(!campaign.getHtmlText().contains(Constants.FOOTER_UNSUBSCRIBE_LINK)) {
				MessageUtil.setMessage("Looks like the Unsubscribe Link merge-tag is missing in your email content. "
						+ "Please note that default sender address will be added to email-footer at the time of sending to be compliant with email sending laws.", "color:blue");
			}
		}
		
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		if(campScheduleList.size() == 0) {
			schErrorLblId.setValue("Select at least one schedule");
//			MessageUtil.setMessage("Select at least one schedule.", "color:red", "TOP");
			return;
		}
		schErrorLblId.setValue("");
		saveCampaign(false);
	}
	
/**
 * 
 * Sends Test Email from Page 6 of campaign creation.
 * 
 */	
	
	/*public void onClick$okBtnId(){
		try {
			String emailId = testEmailTbId.getValue();
			
			logger.debug("Test mailId is: "+emailId);
			
			if(!Utility.validateEmail(emailId)) {
				
				Messagebox.show("Please enter a valid email address.", "Captiway", Messagebox.OK, Messagebox.ERROR);
				return;
			} else {

				MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "top");
				EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
				EmailQueue testEmailQueue = new EmailQueue(campaign, Constants.EQ_TYPE_TESTMAIL,"Active",emailId,MyCalendar.getNewCalendar(),campaign.getUsers()); 
				testEmailQueue.setMessage(campaign.getHtmlText());
				emailQueueDao.saveOrUpdate(testEmailQueue);
				testEmailTbId.setValue("Email Address...");
			
			} 
		}catch (Exception e) {
			logger.error("** Exception : Error occured while sending test email.", e);
		}
		
	}*/
	
	public void onClick$testEmailTbId() {
		if(testEmailTbId.getValue().equals("Email Address...")) {
			
			testEmailTbId.setValue("");
			
		}
		
		
		
	}
	
	// changes here
	
	public void onClick$okBtnId(){

		/*if(Utility.validateHtmlSize(campaign.getHtmlText())) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/

		long size =	Utility.validateHtmlSize(campaign.getHtmlText());
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		String emailId=testEmailTbId.getValue();
		 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
				MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
				testEmailTbId.setValue("Email Address...");
			}
		 else if(sendTestMail()) {
			
			testEmailTbId.setValue("Email Address...");
		}
			
		
	}
	
		
	public boolean sendTestMail() {
		
		if(campaign==null){
			logger.error("Exception :Campaign was null");
			return false;
		}
		
		String emailId = testEmailTbId.getValue();
		try {
			
			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
			
			MessageUtil.clearMessage();
			
			
			String[] emailArr = null;
			
				emailArr = emailId.split(",");
				for (String email : emailArr) {
					
					if(!Utility.validateEmail(email.trim())) {
						
						Messagebox.show("Please enter a valid email address.", "Error", Messagebox.OK, Messagebox.ERROR);
						return false;
					}else {
						//Testing for invalid email domains-APP-308
						String result = PurgeList.checkForValidDomainByEmailId(email);
						if(!result.equalsIgnoreCase("Active")) {
							Messagebox.show("Please enter email addresses with valid email domains.'" +email+"' is invalid.","Error",Messagebox.OK,Messagebox.ERROR);
							return false;
						}
					}
					
					
				}//for
				
				//Check whether user is expired or not
				if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
					logger.debug("Current User, with userId:: "+currentUser.getUserId()+" has been expired, hence cannot send test mail");
//					MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
					return false;
				}
				
				if( campaign.getHtmlText()==null ||  campaign.getHtmlText().isEmpty()) {
					Messagebox.show("Email content cannot be empty.", "Error", Messagebox.OK, Messagebox.ERROR);
					return false;
				}
				
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(campaign.getHtmlText(), currentUser);
				if(isValidCouponAndBarcode != null){
					return false;
				}
				
				for (String email : emailArr) {
				
					Utility.sendInstantMail(campaign, campaign.getSubject(), campaign.getHtmlText(),
							Constants.EQ_TYPE_TESTMAIL, email, null );
					
				}
				
				Messagebox.show("Test mail will be sent in a moment.", "Information", 
						Messagebox.OK, Messagebox.INFORMATION);
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	
	
		return true;
		
	}

	
	
/*	Commented for 2.3.11
 * private boolean createRow(CampaignSchedule campaignSchedule, Rows rows,boolean loadDBSch) {

		if (logger.isDebugEnabled()) {
			logger.debug("------------- just entered----------");
		}

		if(!loadDBSch){
			
			int available = 0;
			try {
				SubscriptionDetails subDetails = new SubscriptionDetails();
				available = subDetails.getEmailStatus(campaignSchedule.getScheduledDate());
			} catch (Exception e1) {
				logger.error("** Exception : Problem while getting the subscription details", e1);
				return false;
			}
			if(available <=0) {
				MessageUtil.setMessage("Email cannot be scheduled as you have \n " +
						"reached your email credits limit or your account has expired.", 
						"color:red", "TOP");
				return false;
			}
		}
		
		if(campaignSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
			return;
		}
		
		try {
			
			Row row = new Row();
			row.setValue(campaignSchedule);
			
			if(rows != null) {
				row.setParent(rows);
			}
			else {
				rowMap.put(campaignSchedule.getCsId(), row);
			}
			
			if(campaignSchedule.getResendLevel() < MAX_RESEND_LEVEL) {
				
				Detail detail = new Detail();
				detail.setOpen(false);
				detail.setParent(row);	
				detail.setStyle("display:none;");
				detail.addEventListener("onOpen", listener);
				
				Grid grid = new Grid();
				Columns cols = new Columns();
				cols.setParent(grid);
				grid.setVisible(false);
				grid.setParent(detail);
				int padding = 3;
				if(campaignSchedule.getResendLevel()+1 < MAX_RESEND_LEVEL){
					
					Column col = new Column();
					col.setWidth("3%");
					col.setParent(cols);
					padding = 0;
				}
			
				Column col = new Column("Date");
				col.setWidth((27 + padding) + "%");
				col.setParent(cols);
	
				col = new Column("Status");
				col.setWidth("18%");
				col.setParent(cols);
				
				col = new Column("Resend criteria");
				col.setWidth("17%");
				col.setParent(cols);
	
				col = new Column("Actions");
				col.setWidth("35%");
				col.setParent(cols);
				
				Rows tempRows = new org.zkoss.zul.Rows();
				tempRows.setParent(grid);
	
				rowsMap.put(campaignSchedule.getCsId(), tempRows);
			
			}// if resend level

			Label tempLabel = new Label(campaignSchedule.getDateStrByTimeZone(null,
									(TimeZone) sessionScope.get("clientTimeZone")));
			tempLabel.setParent(row);
			
			tempLabel = new Label(campaignSchedule.getStatusStr());
			tempLabel.setParent(row);

			
			*//**
			 * criteria will be 0 for root schedules for
			 * re send schedules only criteria will be > 0
			 *
			 **//*
			if(campaignSchedule.getParentId() != null && campaignSchedule.getCriteria() > 0) {
				
				tempLabel = new Label();
				if (campaignSchedule.getCriteria() == 1) {
					tempLabel.setValue("Not opens");
				}
				else if (campaignSchedule.getCriteria() == 2) {
					tempLabel.setValue("Not clicked");
				}
				tempLabel.setParent(row);
				
			}// if campaignSchedule.getParent()
			
			Hbox hbox = new Hbox();
			hbox.setParent(row);
			
			Toolbarbutton tbButton;
				
			if(campaignSchedule.getResendLevel() < MAX_RESEND_LEVEL) {
				
				tbButton = new Toolbarbutton("Add/Resend");
				tbButton.setImage("/img/icons/add_icon.png");
				tbButton.setTooltiptext("Resend the email after some days");
				tbButton.setAttribute(TB_ACTION, TB_ACTION_RESEND);
				tbButton.addEventListener("onClick", listener);
				tbButton.setParent(hbox);
			}

			if(campaignSchedule.getParentId() != null){
				
				tbButton = new Toolbarbutton("Edit content");
				tbButton.setTooltiptext("Use different content for this schedule");
				tbButton.setImage("/img/icons/small_edit_icon.png");
				tbButton.setAttribute(TB_ACTION, TB_ACTION_EDIT);
				tbButton.addEventListener("onClick", listener);
				
				tbButton.setParent(hbox);
			}

			*//** * Delete toolbar button** *//*
			tbButton = new Toolbarbutton("Delete");
			tbButton.setTooltiptext("Delete from schedule");
			tbButton.setImage("/img/action_delete.gif");
			tbButton.setAttribute(TB_ACTION, TB_ACTION_DELETE);
			tbButton.addEventListener("onClick", listener);
			
			tbButton.setParent(hbox);

			if(rows != null) {
				rows.invalidate();
			}
			
		} catch (Exception e) {
			
			logger.error("** Exception : while creating a row ", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("------------- before exit----------");
		}
		
		return true;

	}// createRow()
*/
	private class MyListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {

			String action = (String)event.getTarget().getAttribute(
					CampFinalController.TB_ACTION);
			Rows rows;
			CampaignSchedule campSchedule;

			if(event.getTarget() instanceof Image) {
				logger.info("Deleting on click of Image Event");
				Image img = (Image)event.getTarget();
				String imageEventName = img.getAttribute("delete").toString();
				Listitem li = (Listitem)img.getParent().getParent().getParent();

				campSchedule = (CampaignSchedule)li.getValue();
				if("campScheduleDelete".equals(imageEventName)){
					/**
					 *  Deletes the row from the rows and removes corresponding
					 *  schedule object from the list when user clicks on delete link 
					 */
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {
						try {
							logger.info(" >>> 1 >>>>>"+campSchedule.getCsId());
							
							
							// Added 2.3.11 
							//find the triggered email & then compare the 
							//scheduleDate with sent on if it is matched then reduce the time of triggered email
							
							
							UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
							if(triggeredAlert != null){
								
								Calendar expireOn = triggeredAlert.getSentOn();
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campSchedule.getScheduledDate().equals(expireOn)){
									
									//find the schedule which is last but one in the periodical schedules
									//just mark the schedule as delete one
									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDaoForDML.saveOrUpdate(triggeredAlert);
								}
							}
							//campaignScheduleDao.deleteCampSchById(campSchedule.getCsId());
							campaignScheduleDaoForDML.deleteCampSchById(campSchedule.getCsId());

							/*rows = (Rows)currentRow.getParent();
							rows.removeChild(currentRow);

							if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
								Detail detail = (Detail)(rows.getParent().getParent()); 
								rows.getParent().setVisible(false);
								detail.setOpen(false);
								detail.setStyle("display:none");
							}*/

							//rows.invalidate();
							Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
							if(campSchedule.getScheduledDate().equals(lblDate)){
								logger.info("Removing attribute");
								campActiveTillDateLbId.removeAttribute("setLbDate");
							}
							removeCampScheduleFromList(campSchedule);

							campaign.setStatus(getCampaignStatus(campaign));
							campaign.setStatusChangedOn(Calendar.getInstance());
							//campaignsDao.saveOrUpdate(campaign);
							campaignsDaoForDML.saveOrUpdate(campaign);
							rowsMap.remove(campSchedule.getCsId());
							rowMap.remove(campSchedule.getCsId());
							//TODO arrange campShcedList, Clear ListBoxes
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");
							
							int count = activeCampaingsListlbId.getItemCount();
							for(; count>0; count--) {
								activeCampaingsListlbId.removeItemAt(count-1);
							}
							count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
							for(; count>0; count--) {
								viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
							}
							if(campScheduleList != null && campScheduleList.size() > 0){

								List<CampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(campScheduleList);
								if(activeCampScheduleList.size() > 0){
									createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
									createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
									noOfTimeRedraw = 0;
									activeSchCount = 100;
									redraw(0, activeSchCount);
								}
								else{
									createDivUpComingCampaigns(false,null);
								}
								//redraw(0, activeSchCount);
							}
							else{
								createDivUpComingCampaigns(false,null);
							}

							/*
							
							if(campScheduleList != null && campScheduleList.size() > 0){

								List<CampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(campScheduleList);
								if(activeCampScheduleList.size() > 0){
									createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
									createDivUpComingCampaigns(true);
								}
								else{
									createDivUpComingCampaigns(false);
								}
								redraw(0, activeSchCount);
							}
							else{
								createDivUpComingCampaigns(false);
							}
*/
						} catch (Exception e) {
							logger.error("Exception ::", e);
						}
					}
				}
			}
			else if( event.getTarget() instanceof A) {

				Object srcObject = event.getTarget().getAttribute(ATTRIBUTE_SOURCE);
				if(srcObject instanceof SegmentRules) {

					SegmentRules segmentRule = (SegmentRules)srcObject;
					String str= segmentRule.getSegRuleToView();
					viewSegRuleWinId$segRuleLblId.setValue(str);
					viewSegRuleWinId.setVisible(true);


				}else if(srcObject instanceof MailingList) {

					MailingList mailingList=(MailingList) srcObject;
					Sessions.getCurrent().setAttribute("mailingList",mailingList);

					if(Sessions.getCurrent().getAttribute("viewType") != null )
						Sessions.getCurrent().removeAttribute("viewType");

					Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);


				}
			}
			else if( event.getTarget() instanceof Toolbarbutton && action != null) {/*

				Commented for 2.3.11
				currentRow = (Row) event.getTarget().getParent().getParent();
				campSchedule = (CampaignSchedule)currentRow.getValue();

				if(action.equals(CampFinalController.TB_ACTION_RESEND)) {

					// if clicks on 'Add Resend'
					errMsgLblId.setVisible(false);
					resendOptionWinId.setVisible(true);

				}
				else if(action.equals(CampFinalController.TB_ACTION_DELETE)) {

					//if clicks on Delete the schedule
					*//**
					 *  Deletes the row from the rows and removes corresponding
					 *  schedule object from the list when user clicks on delete link 
					 *//*
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {

						try {

							//find the triggeredemail & then compare the 
							//scheduleDate with sent on if it is matched then reduce the time of triggered email
							
							
							UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
							if(triggeredAlert != null){
								
								Calendar expireOn = triggeredAlert.getSentOn();
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campSchedule.getScheduledDate().equals(expireOn)){
									
									//find the schedule which is last but one in the periodical schedules
									//just mark the schedule as delete one
									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDao.saveOrUpdate(triggeredAlert);
								}
							}
							logger.info(" >>> 1 >>>>>"+campSchedule.getCsId());
							campaignScheduleDao.deleteCampSchById(campSchedule.getCsId());

							rows = (Rows)currentRow.getParent();
							rows.removeChild(currentRow);

							if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
								Detail detail = (Detail)(rows.getParent().getParent()); 
								rows.getParent().setVisible(false);
								detail.setOpen(false);
								detail.setStyle("display:none");
							}

							//rows.invalidate();
							removeCampScheduleFromList(campSchedule);

							campaign.setStatus(getCampaignStatus());
							campaignsDao.saveOrUpdate(campaign);


							rowsMap.remove(campSchedule.getCsId());
							rowMap.remove(campSchedule.getCsId());
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");

						} catch (Exception e) {
							logger.error("Exception ::", e);
						}

					}



				}
				else if(action.equals(CampFinalController.TB_ACTION_EDIT)) {

					// if clicks on Edit the content of 'Re send schedule'
					resendEditorWinId.setVisible(true);

					HttpServletRequest request = (HttpServletRequest)
							Executions.getCurrent().getNativeRequest();
					request.removeAttribute("campSchedule");
					request.setAttribute("campSchedule", campSchedule);
					request.setAttribute("source", "schedule");
					resendEditorWinId$resendIncId.setSrc("zul/Empty.zul");

					resendEditorWinId$resendIncId.setSrc(
							"/zul/campaign/plainEditor.zul");

				}


			*/}// if toolbarbutton event

			else if(event.getTarget() instanceof Detail) {/*

				Commented for 2.3.11
				Detail detail = (Detail) event.getTarget();
				Grid subGrid = (Grid)detail.getFirstChild();
				rows = subGrid.getRows();

				if(rows.getChildren().size() == 0) {
					rows.getParent().setVisible(false);
				}else {
					List list = rows.getChildren();

					for(Object obj:list){
						Row row=(Row)obj;
						CampaignSchedule campShcedule=(CampaignSchedule)row.getValue();

						List<Object[]> childList=campaignScheduleDao.getAllChidren(campShcedule.getCsId(),campShcedule.getCampaignId());
						if(childList!=null) {
							logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
							detail=row.getDetailChild();
							detail.setStyle("display:block;");
							detail.addEventListener("onOpen", listener);
							//Grid grid=(Grid)detail.getChildren().get(0);

							//((Detail)row.getChildren().get(0)).setOpen(true);//detail.setStyle("display:block;");


						} //if
					}//for

					rows.getParent().setVisible(true);
				}//else
			*/}//else if
			
			else if(event.getTarget() instanceof Label){
				
				logger.info("included promo code clicked>>>>>>>>>>>>>>>>");
				Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
			}
		}//onEvent
		
		public String getCampaignStatus(Campaigns campaign) {
			


			//String status = campaign.getStatus();

		//	CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");

			//	List<CampaignSchedule> scheduleList = campaignScheduleDao.findAllByCampaignId(campaign.getCampaignId(), campaign.getUsers().getUserId());
			//TimeZone clientTimeZone = (TimeZone) sessionScope.get("clientTimeZone");



			if(campScheduleList == null || campScheduleList.size() == 0) {
				logger.debug("All Active or Draft Schedules are deleted.");
				return Constants.CAMP_STATUS_DRAFT;
			}

			//boolean draftStatus = campaign.getStatus().equalsIgnoreCase("Draft") ;
			//logger.info("scheduleList.size()="+scheduleList.size()+"   scheduleList="+scheduleList);
			Calendar startCal =null;
			Calendar endCal = null;


			//logger.info("Schedule List status###" + ((CampaignSchedule)(scheduleList.get(0))).getStatus());
			CampaignSchedule latestCampaignSchedule = null;

			for (CampaignSchedule campaignSchedule : campScheduleList) {
				if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;

				if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
					latestCampaignSchedule = campaignSchedule;
				}

				if(campaignSchedule.getStatus() != 0 ) continue;

				if(startCal== null && endCal== null){
					startCal = campaignSchedule.getScheduledDate();
					endCal = campaignSchedule.getScheduledDate();


				}

				if(endCal != null && endCal.before(campaignSchedule.getScheduledDate())){
					endCal = campaignSchedule.getScheduledDate();
				}
				if(startCal.after(campaignSchedule.getScheduledDate())){
					startCal = campaignSchedule.getScheduledDate();
				}
			}//for



			if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
				return latestCampaignSchedule.getStatusStr();
			else if(latestCampaignSchedule.getStatus() >= 3)
				return "Schedule Failure";
			else
				return "Draft";

		}
		
		
		
		
		
		
	}// class EventListener
	
	
	public void onClick$editCampListImgId() throws Exception {
		logger.info("testing...");
		session.setAttribute("editCampaign","edit");
		Redirect.goTo(PageListEnum.CAMPAIGN_MLIST);
	}
	
	public void onCheck$sendPeriodicallyId() throws Exception {
		frqDivId.setVisible(!frqDivId.isVisible());
		//frqDtDivId.setVisible(!frqDtDivId.isVisible());
		prtDtDivId.setVisible(!prtDtDivId.isVisible());
	}//onCheck$sendPeriodicallyId()
	
	public void onCheck$prtDateRadioId() throws Exception {
		
		
		frqDivId.setVisible(!frqDivId.isVisible());
		//frqDtDivId.setVisible(!frqDtDivId.isVisible());
		prtDtDivId.setVisible(!prtDtDivId.isVisible());
		
	}//onCheck$prtDateRadioId()
	
	
	
	public void onClick$editTextImgId() throws Exception {
		Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
	}
	
	public void onClick$editCampHtmlImgId() throws Exception {
		String editorType = campaign.getEditorType();
		session.setAttribute("editorType",editorType);
		session.setAttribute("editCampaign","edit");
		if(editorType.equalsIgnoreCase("blockEditor")){
			Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
		}else if(editorType.equalsIgnoreCase("plainTextEditor") || 
		 editorType.equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)){
			Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
		}else if(editorType.equalsIgnoreCase("beeEditor")){
			Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
		}else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
			Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
		} 
	}
	
	public void onClick$subjectImgEditId() throws Exception {
		session.setAttribute("editCampaign","edit");
		Redirect.goTo(PageListEnum.CAMPAIGN_SETTINGS);
	}
	
	// changes here
	public void onBlur$testEmailTbId() throws Exception {
		 
		 String mail=testEmailTbId.getValue();
		 //logger.debug("here in on blur method mail id "+mail);
		 if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("Email Address...");
			 
		 }
	 }

	//Added For 2.3.11
	/**
	 * createRowUpComingListBox
	 * @param campaignSchedule
	 * @param count
	 * @param loadDBSch
	 * @return
	 */
	private boolean createRowUpComingListBox(CampaignSchedule campaignSchedule,int count, boolean loadDBSch){


		logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");
		try {
			//			logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");			
			if(!loadDBSch){
				int available = 0;
				try {
					SubscriptionDetails subDetails = new SubscriptionDetails();
					available = subDetails.getEmailStatus(campaignSchedule.getScheduledDate());
				} catch (Exception e1) {
					logger.error("** Exception : Problem while getting the subscription details", e1);
					return false;
				}
				/*if(available <=0) {
					MessageUtil.setMessage("Email cannot be scheduled as you have \n " +
							"reached your email credits limit or your account has expired.", 
							"color:red", "TOP");
					return false;
				}*/
			}

			if(campaignSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
				MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
				return false;
			}

			Listitem li = new Listitem();
			Listcell lc = new Listcell();

			if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus() || 3 == campaignSchedule.getStatus()){

				//Schedule Date
				li.setValue(campaignSchedule);
				lc.setParent(li);
				lc = new Listcell();
				lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				//logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				lc.setParent(li);

				//Status
				lc = new Listcell();
				if(campaignSchedule.getStatusStr().equalsIgnoreCase("Insufficient Credits / Subscription Expired")) {
					lc.setLabel("Paused");
				}else {
					lc.setLabel(campaignSchedule.getStatusStr());
				}
				lc.setParent(li);

				//Delete
				lc = new Listcell();
				Hbox hbox = new Hbox();
				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;");
				delImg.setAttribute("delete", "campScheduleDelete");
				delImg.addEventListener("onClick", new MyListener());
				delImg.setParent(hbox);

				hbox.setParent(lc);	
				lc.setParent(li);

				li.setHeight("30px");


				if(count == 0){
					li.setSelected(true);
					li.setParent(activeCampaingsListlbId);
					logger.info("setting first element");
				}
				else{
					logger.info("Set window as parent or hide");

					try{
						//Listbox listbox = (Listbox) activeCampaingsListlbId.getChildren();
						//Listitem listitem = listbox.getItemAtIndex(0);

						CampaignSchedule campSchedChkDate =   (CampaignSchedule) ((Listitem)activeCampaingsListlbId.getSelectedItem()).getValue();

						//			CampaignSchedule campSchedChkDate = (CampaignSchedule) listItem;
						logger.info("When list is not null ::"+MyCalendar.calendarToString(campSchedChkDate.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
						logger.info("campaignSchedule ::"+MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
						if(campaignSchedule.getScheduledDate().before(campSchedChkDate.getScheduledDate())){
							logger.debug("true");


							int count1 =  activeCampaingsListlbId.getItemCount();
							for(; count1>0; count1--) {
								activeCampaingsListlbId.removeItemAt(count-1);
							}

							if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus() || 3 == campaignSchedule.getStatus()){
								li = new Listitem();
								li.setSelected(true);
								//Schedule Date
								li.setValue(campaignSchedule);
								lc.setParent(li);
								lc = new Listcell();
								lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								//logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								lc.setParent(li);

								//Status
								lc = new Listcell();
								if(campaignSchedule.getStatusStr().equalsIgnoreCase("Insufficient Credits / Subscription Expired")) {
									lc.setLabel("Paused");
								}else {
									lc.setLabel(campaignSchedule.getStatusStr());
								}
								lc.setParent(li);

								//Delete
								lc = new Listcell();
								hbox = new Hbox();
								delImg = new Image("/img/action_delete.gif");
								delImg.setTooltiptext("Delete");
								delImg.setStyle("cursor:pointer;");
								delImg.setAttribute("delete", "campScheduleDelete");
								delImg.addEventListener("onClick", new MyListener());
								delImg.setParent(hbox);

								hbox.setParent(lc);	
								lc.setParent(li);

								li.setHeight("30px");


								li.setParent(activeCampaingsListlbId);

								li.setParent(activeCampaingsListlbId);

								logger.info("Set window as parent or hide");
							}
						}
					}
					catch(Exception e){
						logger.info("Exception ::",e);
					}
				}
			}
			logger.debug("<<<<< Completed createRowUpComingListBox .");
			return true;
		} catch (Exception e) {
			logger.error("Exception ..........:",e);
			logger.debug("<<<<< Completed createRowUpComingListBox .");
			return false;
		}

	}//createRowUpComingListBox
	
	
	/**
	 * This method return's active or draft campScheuleList.
	 * @param campScheduleList2
	 * @return activeOrDraftList
	 */
	private List<CampaignSchedule> getActiveCampScheduleList(List<CampaignSchedule> campScheduleList2) {
		//		logger.debug(">>>>>>> Started  getActiveCampScheduleList :: ");
		List<CampaignSchedule> activeOrDraftList = new ArrayList<CampaignSchedule>();
		for (CampaignSchedule campaignSchedule : campScheduleList2) {
			if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 || campaignSchedule.getStatus() == 3) {
				activeOrDraftList.add(campaignSchedule);
			}
		}
		//		logger.debug("<<<<< Completed getActiveCampScheduleList .");
		return activeOrDraftList;
	}
	
	/**
	 * This method returns size for active or draft campaign's.
	 * @param campScheduleList2
	 * @return size
	 */
	private String getSize(List<CampaignSchedule> campScheduleList2) {
		//logger.debug(">>>>>>> Started  getSize :: ");
		int activeCount = 0;
		for (CampaignSchedule campaignSchedule : campScheduleList2) {
			if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 || campaignSchedule.getStatus() == 3) {
				activeCount++;
			}
		}
		//logger.debug("<<<<< Completed getSize .");
		return activeCount+"";
	}//getSize
	
	
	/**
	 * This method handles on click on viewAllActiveSchedAnchId
	 */
	public void onClick$viewAllActiveSchedAnchId(){
		//logger.debug(">>>>>>> Started  onClick$viewAllActiveSchedAnchId :: ");
		try {
			viewAllActiveSchedulesWinId.setVisible(true);
			viewAllActiveSchedulesWinId.doHighlighted();
			viewAllActiveSchedulesWinId.setVisible(true);
			viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
			int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			noOfTimeRedraw = 0;
			activeSchCount = 100;
			redraw(0,activeSchCount);
			viewAllActiveSchedulesWinId.setStyle("scroll:auto;");
			viewAllActiveSchedulesWinId.setStyle("overflow:auto;");
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		//logger.debug("<<<<< Completed onClick$viewAllActiveSchedAnchId .");
	}//onClick$viewAllActiveSchedAnchId
	
	/**
	 * onClick$submitBtnId$viewAllActiveSchedulesWinId
	 */
	public void onClick$submitBtnId$viewAllActiveSchedulesWinId(){
		logger.debug(">>>>>>> Started  onClick$submitBtnId$viewAllActiveSchedulesWinId :: ");
		int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
		for(; count>0; count--) {
			viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
		}
		viewAllActiveSchedulesWinId.setClosable(false);
		viewAllActiveSchedulesWinId.setVisible(false);
		viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
		persistentCamp = true;
		noOfTimeRedraw = 0; 
		
		logger.debug("<<<<< Completed onClick$submitBtnId$viewAllActiveSchedulesWinId .");
	}//onClick$submitBtnId$viewAllActiveSchedulesWinId
	
	
	/**
	 * This method helps to redraw and populate email campaigns.
	 * @param startIndex
	 * @param size
	 */
	int noOfTimeRedraw=0;
	boolean reverseFlag = true;
	List<CampaignSchedule> campaignScheduleList = null;
	public void redraw(int startIndex, int size) {
		//	logger.debug(">>>>>>> Started  redraw :: ");
		logger.info("Redraw form "+startIndex +"--"+size);
		try {
			MessageUtil.clearMessage();
			//System.gc();
			viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(true);
			//campaignScheduleList  = campScheduleList;
			campaignScheduleList  = getActiveCampScheduleList(campScheduleList);


			if(campaignScheduleList == null || campaignScheduleList.size()<=0){
				logger.info("NO more campaigns");
				viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
				viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				return;
			}

			Collections.sort(campaignScheduleList);

			
			if(campaignScheduleList != null && campaignScheduleList.size() >0){
				//		logger.info("Start DAte"+campaignScheduleList.get(0).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
				//	logger.info("End Date"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				if(startIndex == 0){
					//					logger.info("No of time redraw ::"+noOfTimeRedraw);
					noOfTimeRedraw ++;
					Listitem li;
					Listcell lc;
					int index =0;
					//for (int i = 0; i <= size; i++) { // 0 -100
					for (int i = 0; i < campaignScheduleList.size(); i++) { 
						//logger.info("Started loop"+i);
						if(i == 101){
							logger.info(i+"..Camp list reach size break......"+size);
							break;
						}
						else{
							index=i;
							//	logger.info("in else"+i);
							li = new Listitem();
							lc = new Listcell();
							//Active and Draft Schedules
							//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
							//Schedule Date
							li.setValue(campaignScheduleList.get(i));
							lc.setParent(li);
							lc = new Listcell();
							logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
							lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));

							lc.setParent(li);

							//Status
							lc = new Listcell();
							if(campaignScheduleList.get(i).getStatusStr().equalsIgnoreCase("Insufficient Credits / Subscription Expired")) {
								lc.setLabel("Paused");
							}else {
								lc.setLabel(campaignScheduleList.get(i).getStatusStr());
							}
							lc.setParent(li);

							//Delete
							lc = new Listcell();
							Hbox hbox = new Hbox();
							Image delImg = new Image("/img/action_delete.gif");
							delImg.setTooltiptext("Delete");
							delImg.setStyle("cursor:pointer;");
							delImg.setAttribute("delete", "campScheduleDelete");
							delImg.addEventListener("onClick", new MyListener());
							delImg.setParent(hbox);

							hbox.setParent(lc);	
							lc.setParent(li);

							li.setHeight("30px");
							li.setParent(viewAllActiveSchedulesWinId$campListlbId);

							//}//internal if
						}
						logger.debug("Completed for loop till size  ::"+index);
					}//for loop

				}//first time
				else if(startIndex !=0 ){


					//					logger.info("No of time redraw ::"+noOfTimeRedraw);
					noOfTimeRedraw ++;
					Listitem li;
					Listcell lc;
					logger.info(startIndex+" Start Index & Size ::"+size);
					logger.info("startIndex == campaignScheduleList.size()"+(startIndex == campaignScheduleList.size()));
					for (int i =  startIndex+1; i <= size; i++) {
						//	logger.info("value for index :"+i+" : list size :: "+campaignScheduleList.size());
						if( campaignScheduleList.size() <= i){
							logger.info("NO more campaigns");
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
							viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
							logger.info("Camp list reach size break......");
							break;
						}

						li = new Listitem();
						lc = new Listcell();
						//Active and Draft Schedules
						//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
						viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
						//Schedule Date

						li.setValue(campaignScheduleList.get(i));
						lc.setParent(li);
						lc = new Listcell();
						/*logger.info("::"+i);*/

						logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setParent(li);

						//Status
						lc = new Listcell();
						if(campaignScheduleList.get(i).getStatusStr().equalsIgnoreCase("Insufficient Credits / Subscription Expired")) {
							lc.setLabel("Paused");
						}else {
							lc.setLabel(campaignScheduleList.get(i).getStatusStr());
						}
						lc.setParent(li);

						//Delete
						lc = new Listcell();
						Hbox hbox = new Hbox();
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setTooltiptext("Delete");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("delete", "campScheduleDelete");
						delImg.addEventListener("onClick", new MyListener());
						delImg.setParent(hbox);

						hbox.setParent(lc);	
						lc.setParent(li);

						li.setHeight("30px");
						li.setParent(viewAllActiveSchedulesWinId$campListlbId);

						//}//internal if
					}//for loop


				}//more than one time
				else {
					logger.info("NO more campaigns");
					viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
					viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				}

			}

		} catch (WrongValueException e) {
			logger.error("WrongValueException ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		//	logger.debug("<<<<< Completed redraw .");
	}//redraw


	private List<CampaignSchedule> sentCampaignScheduleList = null;
	/**
	 * redrawSent
	 * @param startIndex
	 * @param size
	 */
	boolean redrawFlag = true;
	public void redrawSent(int startIndex,int size){
		logger.debug(">>>>>>> Started  redrawSent :: ");
		try{
			sentCampaignScheduleList = getArchivedCampScheduleList(campScheduleList);

			if(sentCampaignScheduleList == null ){
				viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
				viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
				logger.error("No Campaing's existing.........");
				return;
			}
			
			if(redrawFlag){
				logger.info("reversing list");
				Collections.reverse(sentCampaignScheduleList);
			}
		
		

			if(sentCampaignScheduleList != null && sentCampaignScheduleList.size()>0){	
				if(startIndex == 0){
					Listitem li;
					Listcell lc;
					for(int i=0;i<=sentCampaignScheduleList.size()-1;i++ ){

						if(i == size){
							logger.info(i+"..Camp list reach size break......"+size);
							break;
						}
						else{
							li = new Listitem();
							lc = new Listcell();
							viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
							viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
							viewAllArchivedSchedAnchId.setVisible(true);
							li.setValue(sentCampaignScheduleList.get(i));
							lc.setParent(li);
							//Date
							lc = new Listcell();
							lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							lc.setParent(li);
							//Status
							lc = new Listcell();
							lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
							lc.setParent(li);

							li.setHeight("30px");
							li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
						}
					}

				}


				else if(startIndex != 0 ){
					Listitem li;
					Listcell lc;
					for(int i =  startIndex; i <= size; i++){
						if( sentCampaignScheduleList.size() <= i){
							logger.info("NO more campaigns");
							viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
							viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
							logger.info("Camp list reach size break......");
							break;
						}

						li = new Listitem();
						lc = new Listcell();
						li = new Listitem();
						lc = new Listcell();
						viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
						viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
						viewAllArchivedSchedAnchId.setVisible(true);
						li.setValue(sentCampaignScheduleList.get(i));
						lc.setParent(li);
						//Date
						lc = new Listcell();
						lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setParent(li);
						//Status
						lc = new Listcell();
						lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
						lc.setParent(li);

						li.setHeight("30px");
						li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
					}

				}
			}
			else{
				viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
				viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
				logger.error("No Campaing's existing.........");
			}
		}
		catch(Exception exception){
			logger.error("Exception",exception);
		}
		logger.debug("<<<<< Completed redrawSent .");
	}//redrawSent
	
	/**
	 * drawSentListBox
	 * @param campaignSchedule
	 */
	private void drawSentListBox(CampaignSchedule campaignSchedule) {
		logger.debug(">>>>>>> Started  drawSentListBox :: ");
		Listitem li = new Listitem();
		Listcell lc = new Listcell();
		viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
		viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
		viewAllArchivedSchedAnchId.setVisible(true);
		li.setValue(campaignSchedule);
		lc.setParent(li);
		//Date
		lc = new Listcell();
		lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
		lc.setParent(li);
		//Status
		lc = new Listcell();
		lc.setLabel(campaignSchedule.getStatusStr());
		lc.setParent(li);

		li.setHeight("30px");
		logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
		li.setParent(sentCampaingsListlbId);
		logger.debug("<<<<< Completed drawSentListBox .");
	}//drawSentListBox
	
	
	/**
	 * onSelect$campListlbId$viewAllActiveSchedulesWinId
	 */
	public void onSelect$campListlbId$viewAllActiveSchedulesWinId() {
		//		logger.debug(">>>>>>> Started  campListlbId$viewAllActiveSchedulesWinId:: ");
		if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() == 0){
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(false);
		}else if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() > 0) {
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(false);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(true);
		}
		//		logger.debug("<<<<< Completed onSelect$campListlbId$viewAllActiveSchedulesWinId .");
	}//onSelect$campListlbId$viewAllActiveSchedulesWinId
	
	/**
	 * onClick$delSelectedId$viewAllActiveSchedulesWinId
	 */
	public void onClick$delSelectedId$viewAllActiveSchedulesWinId()  {
		//		logger.debug(">>>>>>> Started  onClick$delSelectedId$viewAllActiveSchedulesWinId :: ");
		try {
			int count = viewAllActiveSchedulesWinId$campListlbId.getSelectedCount();
			if(logger.isDebugEnabled()) 
				logger.debug("Number of selected campaigns schedules to delete :" + count);
			if(count == 0) {
				Messagebox.show("Please select at least one schedule to delete.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			String msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
			boolean found = false;
			Set<Listitem> selList = viewAllActiveSchedulesWinId$campListlbId.getSelectedItems();
			logger.info("Selected list of items are ..:"+viewAllActiveSchedulesWinId$campListlbId.getSelectedCount());
			CampaignSchedule campaignSchedule;
			List<CampaignSchedule> campaignSchedules = new ArrayList<CampaignSchedule>();
			for (Listitem li : selList) {
				campaignSchedule = (CampaignSchedule)li.getValue();
				campaignSchedules.add(campaignSchedule);
				
				if(campaignSchedule.getStatus() == OCConstants.ACTIVE_EMAIL_STATUS || 
						campaignSchedule.getStatus() == OCConstants.DRAFT_EMAIL_STATUS) {
					found = true;
				}
			}
			if(found) {
				//msg = "Some of the selected campaigns are 'Active/Running', \n do you want to continue?";
				msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
			}
			try {
				int confirm =Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

				if(confirm == Messagebox.OK){
					if(deleteCampaignsSchedules(campaignSchedules).equals("success")){
						campaign.setStatus(getCampaignStatus(campaign));
						campaign.setStatusChangedOn(Calendar.getInstance());
						//campaignsDao.saveOrUpdate(campaign);
						campaignsDaoForDML.saveOrUpdate(campaign);
						MessageUtil.setMessage(count+" schedule(s) deleted successfully.", "green", "TOP");
					}
				}
				else {
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
				}
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}	
			viewAllActiveSchedulesWinId$campListlbId.clearSelection();
			logger.info("Item count ::"+viewAllActiveSchedulesWinId$campListlbId.getItemCount());
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
		}
		//		logger.debug("<<<<< Completed onClick$delSelectedId$viewAllActiveSchedulesWinId .");
	}//onClick$delSelectedId$viewAllActiveSchedulesWinId
	
	private String getCampaignStatus(Campaigns campaign) {

		if(campScheduleList == null || campScheduleList.size() == 0) {
			logger.debug("All Active or Draft Schedules are deleted.");
			return Constants.CAMP_STATUS_DRAFT;
		}
		
		CampaignSchedule latestCampaignSchedule = null;

		for (CampaignSchedule campaignSchedule : campScheduleList) {
			if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;

			if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
				latestCampaignSchedule = campaignSchedule;
			}
		}//for

		if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
			return latestCampaignSchedule.getStatusStr();
		else if(latestCampaignSchedule.getStatus() >= 3)
			return "Schedule Failure";
		else
			return "Draft";

	}

	/**
	 * This method helps to delete campaignScheduleList
	 * @param campaignSchedules
	 * @return message
	 */
	private String deleteCampaignsSchedules(List<CampaignSchedule> campaignSchedules) {
		//		logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: ");
		try{
			logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: "+campaignSchedules.size());
			//campaignScheduleDao.deleteByCollection(campaignSchedules);
			campaignScheduleDaoForDML.deleteByCollection(campaignSchedules);

			//Performance Problem to DB hits in Loop, Need to Fixed.

			for (CampaignSchedule campaignSchedule : campaignSchedules) {

				Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
				if(campaignSchedule.getScheduledDate().equals(lblDate)){
					logger.info("Removing attribute");
					campActiveTillDateLbId.removeAttribute("setLbDate");
				}
				removeCampScheduleFromList(campaignSchedule);
				rowsMap.remove(campaignSchedule.getCsId());
				rowMap.remove(campaignSchedule.getCsId());
				//TODO arrange campShcedList, Clear ListBoxes

			}// For Loop

			// Redrawing Lists
			int count = activeCampaingsListlbId.getItemCount();
			for(; count>0; count--) {
				activeCampaingsListlbId.removeItemAt(count-1);
			}
			count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			if(campScheduleList != null && campScheduleList.size() > 0){

				List<CampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(campScheduleList);
				if(activeCampScheduleList.size() > 0){
					createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
					createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
					noOfTimeRedraw = 0;
					activeSchCount = 100;
					redraw(0, activeSchCount);
				}
				else{
					createDivUpComingCampaigns(false,null);
				}
				//redraw(0, activeSchCount);
			}
			else{
				createDivUpComingCampaigns(false,null);
			}

			// Triggered Email

			UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
			for (CampaignSchedule campaignSchedule : campaignSchedules) {
				if(triggeredAlert != null){
					Calendar expireOn = triggeredAlert.getSentOn();
					if(expireOn !=null){
						expireOn.add(Calendar.DAY_OF_YEAR, 7);
						if(campaignSchedule.getScheduledDate().equals(expireOn)){

							//find the schedule which is last but one in the periodical schedules
							//just mark the schedule as delete one
							logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
							triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
							triggeredAlert.setModifiedDate(Calendar.getInstance());
							userCampaignExpirationDaoForDML.saveOrUpdate(triggeredAlert);
						}
					}
				}
			}


			//			logger.debug("<<<<< Completed deleteCampaignsSchedules .");
			return "success";
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
			//			logger.debug("<<<<< Completed deleteCampaignsSchedules .");
			return null;
		}
	}//deleteCampaignsSchedules

	/**
	 * This method handles the on click view more event
	 */
	public void onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId(){
		//		logger.debug(">>>>>>> Started  onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId :: ");
		noOfTimeRedraw = noOfTimeRedraw + 1;
		redraw(activeSchCount,activeSchCount+100);
		activeSchCount = activeSchCount + 100;
		logger.info("now records are " + activeSchCount);
		//		logger.debug("<<<<< Completed onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId .");

	}//onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId
	
	
	/**
	 * This method handles on click on viewAllArchivedSchedAnchId
	 */
	public void onClick$viewAllArchivedSchedAnchId(){
		//		logger.debug(">>>>>>> Started  onClick$viewAllArchivedSchedAnchId :: ");
		try {
			viewAllArchivedSchedulesWinId.setVisible(true);
			viewAllArchivedSchedulesWinId.doHighlighted();
			viewAllArchivedSchedulesWinId.setVisible(true);
			sentSchCount = 100;
			int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			redrawSent(0,sentSchCount);
			viewAllArchivedSchedulesWinId.setStyle("scroll:auto;");
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		//		logger.debug("<<<<< Completed onClick$viewAllArchivedSchedAnchId .");
	}//onClick$viewAllArchivedSchedAnchId
	
	/**
	 * onClick$submitBtnId$viewAllArchivedSchedulesWinId
	 */
	public void onClick$submitBtnId$viewAllArchivedSchedulesWinId(){
	//	logger.debug(">>>>>>> Started  onClick$submitBtnId$viewAllArchivedSchedulesWinId:: ");
		int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
		for(; count>0; count--) {
			viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
		}
		viewAllArchivedSchedulesWinId.setClosable(false);
		viewAllArchivedSchedulesWinId.setVisible(false);
	//	logger.debug("<<<<< Completed onClick$submitBtnId$viewAllArchivedSchedulesWinId .");
	}//onClick$submitBtnId$viewAllArchivedSchedulesWinId

	/**
	 * onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId
	 */
	public void onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId(){
		//		logger.debug(">>>>>>> Started  onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId :: ");
		redrawSent (sentSchCount,sentSchCount+100);
		sentSchCount = sentSchCount + 100;
		logger.info("now records are " + sentSchCount);
		//		logger.debug("<<<<< Completed onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId .");
	}//onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId
	
	/**
	 * 
	 * @param startIndex
	 * @param size
	 */
	public void redrawSent1(int startIndex, int size) {
	//	logger.debug(">>>>>>> Started  redrawSent :: ");
		logger.info("Redrawing sent with start : "+startIndex+"\t End index :"+size);
		//TimeZone timeZone = (TimeZone) sessionScope.get("clientTimeZone");
		List<CampaignSchedule> campaignScheduleSentList =campaignScheduleDao.findAllSentSchedules(startIndex,size,campaign.getCampaignId());

		if(campaignScheduleSentList != null && campaignScheduleSentList.size() >0){
			logger.info("Archived CampSched List size ..:"+campaignScheduleSentList.size());
			logger.info(MyCalendar.calendarToString(campaignScheduleSentList.get(campaignScheduleSentList.size()-1).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
			Listitem li;
			Listcell lc;
	//		logger.info("before for loop"+campaignScheduleList.size());
			/*for (int i = (campaignScheduleList.size()-1);i >=0; i--) {*/
			for (int i = 0;i <= campaignScheduleSentList.size()-1; i++) {
				li = new Listitem();
				lc = new Listcell();
				//For Not Active and Draft Schedules
				if(!(0 == campaignScheduleSentList.get(i).getStatus()) && !( 2 == campaignScheduleSentList.get(i).getStatus())){
					//Insufficient Credits / Subscription Expired,Insufficient Promo-codes,Promo-code Expired / Paused
				//	logger.info("..................."+campaignScheduleList.get(i).getStatus());
					viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
					viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
					viewAllArchivedSchedAnchId.setVisible(true);
					li.setValue(campaignScheduleSentList.get(i));
					lc.setParent(li);
					//Date
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(campaignScheduleSentList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
					lc.setParent(li);
					//Status
					lc = new Listcell();
					lc.setLabel(campaignScheduleSentList.get(i).getStatusStr());
					lc.setParent(li);

					li.setHeight("30px");
					int count =  sentCampaingsListlbId.getItemCount();
				//	logger.info("countcount :"+count);
					if(count == 0){
						logger.info(MyCalendar.calendarToString(campaignScheduleSentList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
						li.setParent(sentCampaingsListlbId);
					}else{
						li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
					}
				}//if

			}//for
			logger.info("Completed For Loop");
			
		}//if list is not null
		else{
			viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
			viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
			logger.error("No Campaing's existing.........");
		}
//		logger.debug("<<<<< Completed redrawSent .");
	}//redrawSent

	/**
	 * This method draw Archived Schedules
	 * @param campaignSchedule
	 */
	private void drawArchivedDiv(CampaignSchedule campaignSchedule){
//		logger.debug(">>>>>>> Started  drawArchivedDiv :: ");
		if(!(0 == campaignSchedule.getStatus()) && !( 2 == campaignSchedule.getStatus())){
			//	logger.info("campaignSentDivId.setVisible(true)");
			campaignSentDivId.setVisible(true);
			campaignSentLbId.setValue(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			campaignSentLbId.setStyle(OCConstants.FONT_BOLD);
		}
		else{
			campaignSentDivId.setVisible(false);
		}
//		logger.debug("<<<<< Completed drawArchivedDiv .");
	}//drawArchivedDiv
	
	
	
	//added in 2.4.6
	private void populateListOfIncludedPromoCodesLabel(){
		
		
		Set<String> phSet = new HashSet<String>();
		Set<String> phSetForPlainText = new HashSet<String>(); 
		
		if(campaign.getHtmlText() != null)
			phSet = Utility.getPhSet(campaign.getHtmlText());
		
		
		if(campaign.getTextMessage() != null)
			phSetForPlainText = Utility.getPhSet(campaign.getTextMessage());
		
			
		phSet.addAll(phSetForPlainText);
		
		if(phSet.size() == 0){
			Label label1 = new Label("Discount code(s) used in campaign:   ");
			label1.setParent(includedPromoCodesDivId);
			Label label = new Label("--");
			label.setParent(includedPromoCodesDivId);
			return;
		}
		
		
		
		String couponIdStr = "";
		logger.info("coupon ph set: "+phSet);
		
		for(String ph : phSet){
			if(ph.startsWith("CC_")){
				
				String[] phStr = ph.split("_");
				
				couponIdStr += couponIdStr.trim().length() <= 0 ? phStr[1] : ","+phStr[1];
			}
			
		}//for
		
		if(couponIdStr.trim().length() == 0){
			Label label1 = new Label("Discount code(s) used in campaign:   ");
			label1.setParent(includedPromoCodesDivId);
			Label label = new Label("--");
			label.setParent(includedPromoCodesDivId);
			
			return;
		}else{
			CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
			Long orgId = currentUser.getUserOrganization().getUserOrgId();
			
			List<Coupons> listOfCoupons = couponsDao.findCouponsByCoupIdsAndOrgId(couponIdStr, orgId);
			
			Label label1 = new Label("Discount code(s) used in campaign:   ");
			label1.setParent(includedPromoCodesDivId);
			int numberOfCoupons=0;
			if(listOfCoupons != null && listOfCoupons.size() >0) {
				for (Coupons coupons : listOfCoupons) {
					
					numberOfCoupons++;
					Label label = new Label(coupons.getCouponName());
					label.setStyle("cursor:pointer;color:blue;text-decoration: underline");
					label.addEventListener("onClick", new MyListener());
					label.setParent(includedPromoCodesDivId);
					

					if( numberOfCoupons < listOfCoupons.size() ){
						
						Label label2 = new Label(", ");
						label2.setParent(includedPromoCodesDivId);
					}
					
					
				}
				
			}
			
		}
		
	
	}
	
 	public void onChange$prtDtBxId() {
 		logger.info("call back");
          logger.info("prtDtBxId.browserDate()    :"+prtDtBxId.onChangeDateValue());
          logger.info("prtDtBxId                  :"+prtDtBxId.getValue());
          logger.info("prtDtBxId.getServerValue   :"+prtDtBxId.getServerValue());
          logger.info("prtDtBxId.getClientValue   :"+prtDtBxId.getClientValue());
  		  prtDtBxId.setValue(prtDtBxId.onChangeDateValue());
 	}
}//EOF
