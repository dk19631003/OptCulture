package org.mq.marketer.campaign.controller.campaign;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSCampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.SmsQueue;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SmsQueueDao;
import org.mq.marketer.campaign.dao.SmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class ViewSMSCampaign extends GenericForwardComposer {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	private SMSCampaignsDao smsCampaignsDao;
	private SMSCampaignsDaoForDML smsCampaignsDaoForDML;
	private SMSCampaignScheduleDao smsCampaignScheduleDao;
	private SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML;
	private SMSCampaignReportDao smsCampaignReportDao;
	
	private Users currentUser;
	private Session session;
	//private MyRendererListener rendererListener = new MyRendererListener();
	

	
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	
	private Listbox smsCampListlbId;
	private Popup copySMSCampaignPopupId;
	private Window testSMSWinId;
	private Button sendTestSmsBtnId;
	private Textbox mblNumTxtBoxId;
	private Button smsCampCopyBtnId;
	private Textbox copySmsCampaignNameTbId;
	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	private CouponsDao couponsDao;
	private int itemsSize,totalSize;
	//private Label msgLblId;
	
	private Paging campListPaging,campListPaging1;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	
	
	private String fromDateStr;
	private String toDateStr;
	//private String campaignName;
	private Textbox searchByCmpgnNameTbId;
	private Div searchByCmpgnCreationDateDivId;
	private Div searchByCmpgnStatusDivId;
	private Div searchByCmpgnNameDivId;
	private Div resetAnchDivId;
	private Include viewCampIncId;
	private Label resetAnchId;
	private Listbox srchLbId;
	
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private String fromDateString;
	private String toDateString;
	private Listbox smsCampStatusLb;
	
	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	
	private Session sessionScopeForTimeZone;
	
	public ViewSMSCampaign(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Campaigns","",style,true);
		
		
		this.smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		this.smsCampaignsDaoForDML = (SMSCampaignsDaoForDML)SpringUtil.getBean("smsCampaignsDaoForDML");
		this.smsCampaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
		this.smsCampaignScheduleDaoForDML = (SMSCampaignScheduleDaoForDML)SpringUtil.getBean("smsCampaignScheduleDaoForDML");

		this.smsCampaignReportDao = (SMSCampaignReportDao)SpringUtil.getBean("smsCampaignReportDao");
		this.userId = GetUser.getUserId();
		this.session = Sessions.getCurrent();
		this.smsCampStatusLb =  (Listbox)Executions.getCurrent().getAttribute("smsCampStatusLb");
		this.captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)SpringUtil.getBean("captiwayToSMSApiGateway");
		currentUser = GetUser.getUserObj();
		 userCampaignCategoriesDao  =(UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		 orgSMSkeywordsDao = (OrgSMSkeywordsDao)SpringUtil.getBean("orgSMSkeywordsDao");
		 couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		  
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			logger.debug("----just entered---");
			
			sessionScopeForTimeZone = Sessions.getCurrent();
			//smsCampListlbId.setItemRenderer(rendererListener);
			
			//set Multiple(true) to the listModelList for checkmark (zk6ce )
			/*ListModelList tempListModel = new ListModelList(getSmsCampaigns());
			tempListModel.setMultiple(true);*/
			//smsCampListlbId.setModel(new ListModelList(getSmsCampaigns()));
			//smsCampListlbId.setModel(tempListModel);
			 itemsSize=0;
			 totalSize = 0;
			 int pSize;
			 totalSize = smsCampaignsDao.getCount(currentUser.getUserId(), "All");
			 try
			 {
			 if(smsCampStatusLb.getSelectedItem().getLabel().equalsIgnoreCase("All"))
			 {
				 pSize = totalSize;
			 }
			 else
			 {
				 pSize = getStatusCount();
			 }
			 }
			 catch(NullPointerException ne)
			 {
				 pSize=totalSize ; 
			 }
			 logger.info("totalsize is ::::"+totalSize);
			 
			 
			 campListPaging.addEventListener("onPaging", this); 
			 campListPaging.setAttribute("onPaging", "topPaging");
			 //campListPaging.setActivePage(0);
			 campListPaging.setTotalSize(pSize);
			 
			 campListPaging1.setTotalSize(pSize);
			 campListPaging1.setAttribute("onPaging", "bottomPaging");
			 campListPaging1.addEventListener("onPaging", this);
			
			 copySMSCampaignPopupId.addEventListener("onOpen", this);
			 redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			 	
			
			
			
			
			
			
			
		} catch (Exception e) {
			logger.error("** Exception : ",e);
		}
	}
	public int getStatusCount()
	{
		 List<SMSCampaigns> smsCampList = getSmsCampaigns(0, totalSize);
		
		 int itemSize=0;
		 //if(smsCampList == null) return ;
		 if(smsCampList != null && smsCampList.size() > 0) {
			 for (SMSCampaigns smsCampaign : smsCampList) {
				 String status = getCampaignStatus(smsCampaign);
					
					 int index = smsCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						itemSize ++; 
					}
			 }
		 }
		 logger.info("status size is"+itemSize);
		 return itemSize;
	}
	@Override
	public void onEvent(Event event) throws Exception {
		logger.debug("------just entered---in onEvent");
		
		super.onEvent(event);
		
		try{
		Object obj = event.getTarget();
		SMSCampaigns smsCampaign ;
		int index = 0;
		if(obj instanceof Image){
			Image img = (Image)obj;
			String imgAttr = (String)img.getAttribute("addEvent");
			
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			smsCampListlbId = (Listbox)li.getParent();
			index = li.getIndex();
			smsCampaign = (SMSCampaigns)li.getValue();
			if(imgAttr.equalsIgnoreCase("sendTestSMS")){
				
				if(currentUser!=null && currentUser.getSubscriptionEnable() && smsCampaign.getCategory() ==null
						&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				testSMSWinId$msgLblId.setValue("");
				testSMSWinId$mblNumTxtBoxId.setValue("");
				
				if(smsCampaign == null){
					logger.error("** Exception : SMS Campaign object is null when send test sms link is clicked**");
					return;
				}
				testSMSWinId.setAttribute("SmsCampaign", smsCampaign);
				testSMSWinId.doModal();
			}
		/* APP-3855	else if(imgAttr.equalsIgnoreCase("sendAgain")) {
				
				
				Set<MailingList> mailingLists = smsCampaign.getMailingLists();
			
				if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA) &&
						smsCampaign.getMessageType().equals(Constants.SMS_TYPE_PROMOTIONAL) && (smsCampaign.getTemplateRegisteredId()==null
						||smsCampaign.getTemplateRegisteredId().isEmpty())) {
							MessageUtil.setMessage("Promotional message content needs to be approved. Please contact OC Support.", "color:red");
							return;
				}
				if(currentUser!=null && currentUser.getSubscriptionEnable() && smsCampaign.getCategory() ==null
						&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
								
				if(mailingLists == null || mailingLists.size() <= 0) {
					
					MessageUtil.setMessage("Please complete the SMS campaign creation process.","color:red","TOP");
					return;
					
				}
								
				
				String appName = PropertyUtil.getPropertyValueFromDB("ApplicationName");
				
				
				
				//need to check with the credits
				
				int available = 0;
				try {
					SubscriptionDetails subDetails = new SubscriptionDetails();
					available = subDetails.getSMSStatus(Calendar.getInstance());
				} catch (Exception e1) {
					logger.error("** Exception : Problem while getting the subscription details", e1);
					return;
				}
				
				if(available <= 0) {
					MessageUtil.setMessage("SMS cannot be sent as you have " +
							"reached your SMS credits limit or your account has expired.", 
							"color:red", "TOP");
					return;
				}
				
				if(Constants.SMS_TYPE_2_WAY.equalsIgnoreCase(smsCampaign.getMessageType())) {
					if(IsUsingExpKeyword(smsCampaign.getMessageContent())) {
						
						return;
					}
				}
				if(invalidPromoCodes(smsCampaign)) return;
				
				//giving credits info/error popup
				String msgContent=smsCampaign.getMessageContent();
				
				String msg = "";
				List<SMSCampaignSchedule> scheduleList = null;
				SMSCampaignScheduleDao smsCampaignScheduleDao = null;
				SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML = null;
				//
				try{
					
					smsCampaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
					smsCampaignScheduleDaoForDML = (SMSCampaignScheduleDaoForDML)SpringUtil.getBean("smsCampaignScheduleDaoForDML");
					
					scheduleList = smsCampaignScheduleDao.findActiveOrDraftSMSCampaignSchedules(smsCampaign.getSmsCampaignId());
					
					if(scheduleList != null && scheduleList.size() > 0){
						
						Calendar currentCal = Calendar.getInstance();
						int expiredSchedules = 0;
						int activeSchedules = 0;
						
						for(SMSCampaignSchedule campaignSchedule : scheduleList){
							
							if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
								expiredSchedules ++; 
							}
							else if(campaignSchedule.getStatus() == 2 ){
								activeSchedules ++;
							}
						}
						
						
						
						logger.info("Expiry Count is "+expiredSchedules +"\t Active Count "+activeSchedules);
						if(expiredSchedules >0 && activeSchedules >0 ){
							msg = "Note : This action will also activate "+(scheduleList.size() - expiredSchedules)+" schedule(s) and expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
						}
						else if(expiredSchedules <=0 && activeSchedules >0 ){
							msg = "Note : This action will also activate "+(scheduleList.size() - expiredSchedules)+" schedule(s) in this campaign.";
						}
						else if(expiredSchedules >0 && activeSchedules <=0 ){
							msg = "Note : This action will also expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
						}
						
						
					}
					
					
					
					
				}catch(Exception e){
					logger.error(" Exception >>>>>>>> ",e);
				}
				
				//
				
				try {
					
					
					int confirm = Messagebox.show("Are you sure you want to send the campaign now? \n"+msg,
							appName, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					
					int confirm = Messagebox.show("Are you sure you want to send the campaign now?",
							appName, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					if(confirm == 1){
						
						MessageUtil.clearMessage();
						int expiredSchedules = 0,activeSchedules=0;
						
						try{
							
							if(scheduleList != null && scheduleList.size() > 0){
								
								Calendar currentCal = Calendar.getInstance();
								
								for(SMSCampaignSchedule campaignSchedule : scheduleList){
									
									if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
										campaignSchedule.setStatus((byte)7);
										expiredSchedules ++; 
									}
									else if(campaignSchedule.getStatus() == 2 ){
										campaignSchedule.setStatus((byte)0);
										activeSchedules ++;
									}

									
								}
								
								
								
								
								//smsCampaignScheduleDao.saveByCollection(scheduleList);
								smsCampaignScheduleDaoForDML.saveByCollection(scheduleList);

							}
							
							
						}catch(Exception e){
							logger.error("Exception >>> ",e);
						}
						
						logger.info("Number of Expired Campaigns :"+ expiredSchedules+" & Active Campaings :"+activeSchedules );
						
						
						
						
						
						
						
						ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
						CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						
						long smsCount = 0;
						
						String sentType = smsCampaign.getListType();
						if(sentType.startsWith("Total")) { 
							Set<MailingList> mlists = smsCampaign.getMailingLists();
							String ml_ids_str = "";
							
							for(MailingList mailingList:mlists) {
								
								if(ml_ids_str.length() > 0) ml_ids_str+=",";
								ml_ids_str += mailingList.getListId();
							}
							
							if(logger.isDebugEnabled()) {
								logger.debug(" Got Mailing lists :"+ml_ids_str);
							}
							smsCount = contactsDao.getMobileCount(mlists, smsCampaign.isEnableEntireList());
					
					
						}else {
							SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
							MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
							String segRuleIds = sentType.split(""+Constants.DELIMETER_COLON)[1];
							List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
							
							if(segmenRules == null) {
								
								MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
								
								return ;
								
								
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
								
								String qry =  SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlsbit);
								
								if(qry == null) {
									MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
									continue;
								}
								
								if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
									
									//logger.debug("=====got true =====");
									String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
									
									if(csCampIds != null ) {
										//logger.debug("=====got campIds =====");
										String crIDs = Constants.STRING_NILL;
										//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
										List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
										if(campList != null) {
											for (Object[] crArr : campList) {
												
												if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
												crIDs += ((Long)crArr[0]).longValue();
												
											}
										}
										
										qry = qry.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
									}
								}
								qry = qry.replace("<MOBILEOPTIN>", !(smsCampaign.isEnableEntireList()) ? " AND c.mobile_opt_in=1" : "");
								logger.info(" Generated Query :"+qry);
								if(tempQry.length() > 0) tempQry += " UNION ";
								
								tempQry += qry;
//								contactsDao.insertSegmentedContacts(tempQry);
								
								
								
								
							}//for
							
							
							smsCount = contactsDao.getSegmentedContactsCount(tempQry);
							
							
							
							
							//##################################
						}//else
						
						
						if(available < smsCount) {
							
							Messagebox.show("SMS cannot be sent as configured contacts count" +
									" exceeds the SMS credits limit.Please contact admin to increase your SMS credits", appName, Messagebox.OK, 
									Messagebox.EXCLAMATION);
							
							return;
							
						}
						
						//Check Promo expired/Paused and avialble count

						Set<String> subjectSymbolSet = Utility.getPhSet(smsCampaign.getMessageContent());
						Set<String> ccPhSet = Utility.getBarcodePhset(smsCampaign.getMessageContent());
						if(ccPhSet != null){
							subjectSymbolSet.addAll(ccPhSet);
						}
						
						CouponsDao couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
						CouponCodesDao couponCodesDao = (CouponCodesDao)SpringUtil.getBean("couponCodesDao");
						String couponIds = "";
						
					
						if(subjectSymbolSet != null && subjectSymbolSet.size() > 0) {
									
							for(String phString : subjectSymbolSet){
								String[] phStr = phString.split("_");
								logger.info("ph str is "+phStr);
								if(!phStr.equals("updatePreferenceLink")){
									
									couponIds += couponIds.trim().length() > 0 ? ","+phStr[1] :phStr[1];
								}
								
							}
						}
						if(!couponIds.isEmpty()){
							
							List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIds,currentUser.getUserOrganization().getUserOrgId());
							
							
							//Long availPromoCount = 0l;
							
							if(coupList != null && coupList.size() >0){
								String coupNameStr = "";
								
								for (Coupons eachPromoObj : coupList) {
									logger.debug("eachPromoObj.getAvailable() is  ::"+eachPromoObj.getAvailable());
									
									if(!eachPromoObj.getAutoIncrCheck()){
										long count = couponCodesDao.findIssuedCoupCodeByCoup(eachPromoObj.getCouponId());
										logger.debug("count is "+count);
										long availCCount = eachPromoObj.getTotalQty() - count;
										
										if(availCCount < smsCount) {
											MessageUtil.setMessage("SMS cannot be sent as the configured discount code "+eachPromoObj.getCouponName()+" available discount code count " +
													"\n exceeds the campaign send limit.Please increase discount code limit.", "color:red;");
											return;
										}
									}
									
									if(eachPromoObj.getStatus().equals(Constants.COUP_STATUS_PAUSED) || 
											eachPromoObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)  ){
										coupNameStr+= coupNameStr.trim().length()== 0 ? eachPromoObj.getCouponName() :","+eachPromoObj.getCouponName();
										
									}
								}
								
								if(coupNameStr.trim().length() > 0){
									MessageUtil.setMessage("The discount code "+coupNameStr+ " \n used in this sms campaign has either expired or in paused status."
											+ "\n Please change the status of this discount code. ", "color:red;");
									return ;
								}
								
							}
						}
						
						ArrayList<String> msgContentLst = null;
						if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())) {
							SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
							List<SMSSettings> smsSettings = smsSettingsDao.findByUser(userId);
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
								
								msgContent= messageHeader != null ? messageHeader+"\n"+msgContent : msgContent;
								//if(smsCampaign.isEnableEntireList() && optOutSettings != null){
								if(smsCampaign.getMessageType() != null && !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)
										&&smsCampaign.isEnableEntireList() && optOutSettings != null) {
									if(!currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
									msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
											("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
													: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
									}else {
										msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
												("\n" +optOutSettings.getKeyword())
														: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
									}
								}
							}
						}
						if (msgContent != null && msgContent.contains("|^")) {

							msgContentLst = splitSMSMessage(msgContent, 160);

						} else {

							msgContentLst = splitSMSMessage(msgContent, 170);
						}
						
						//logger.debug("User available limit is :"+userAvailableEmlCount + " UserId:"+userId);
						if(available == -1) {
							
							 MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
						 	 return;
						}
						if(msgContentLst!=null && smsCount > (available * msgContentLst.size())) {
							 MessageUtil.setMessage("Insufficient credits: You have "+available+" SMS credits while the campaign needs "+(available * msgContentLst
										.size())+" credits \n"
												+ "as the character count in the SMS exceeds 160 characters \n"
												+ "and total recipients count is "+smsCount+".", "color:red", "TOP");
							 return;
						}
						if(smsCount > available) {
							 MessageUtil.setMessage("Configured to " + smsCount + " mobile numbers. \n"
							 		+ "Your available SMS credits limit is " + available+ ".\n " +
								 		"Please contact support@optculture.com to request additional credits.", "color:red", "TOP");
							 return;
						}
						if(available<=(smsCount * msgContentLst.size())){
							MessageUtil.setMessage("You have "+available+" SMS credit(s).\n"
									+ "This campaign uses "+(smsCount * msgContentLst.size())+" credit(s)\n"
											+ "as the character count in the SMS exceeds 160 characters and\n "
											+ "total recipients count is "+smsCount+".", "color:red");
							return;
						}else{
							MessageUtil.setMessage("You have "+available+" SMS credit(s).\n"
									+ "This campaign uses "+(smsCount * msgContentLst.size())+" credit(s) and \n"
											+ "total recipients count is "+smsCount+".", "color:blue");
						}
							
						
						
						SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule();
						//smsCampSchedule.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
						smsCampSchedule.setSmsCampaignId(smsCampaign.getSmsCampaignId());
						smsCampSchedule.setUserId(currentUser.getUserId());
						smsCampSchedule.setScheduledDate(Calendar.getInstance());
						smsCampSchedule.setStatus((byte)0);
						//smsCampaignScheduleDao.saveOrUpdate(smsCampSchedule);
						smsCampaignScheduleDaoForDML.saveOrUpdate(smsCampSchedule);
						
						Messagebox.show("SMS will be sent in a moment.", "Information", Messagebox.OK, Messagebox.INFORMATION);
						logger.debug("sent now is submitted for the SMS campaign"+smsCampaign.getSmsCampaignName());
						
						String status = getCampaignStatus(smsCampaign);
						Listitem  item = (Listitem)img.getParent().getParent().getParent();
						//Listcell statusLbl = (Listcell)item.getChildren().get(5);
						Listcell statusLbl = (Listcell)item.getChildren().get(3);
						statusLbl.setLabel(status == null || status.length() == 0 ? smsCampaign.getStatus() : status);
						
						redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
						smsCampaign.setStatus(status);
						//smsCampaignsDao.saveOrUpdate(smsCampaign);
						smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				
				
			}*/else if(imgAttr.equalsIgnoreCase("Edit")){
				session.setAttribute("smsCampaign", smsCampaign);
				session.setAttribute("fromPage", "campaign/ViewSMSCampaign");
				String draftStatus = smsCampaign.getDraftStatus();
				if(draftStatus != null) {
					if(draftStatus.equalsIgnoreCase(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE)){
						session.setAttribute("editSmsCampaign", "edit");
						Redirect.goTo(PageListEnum.SMS_CAMP_SETTINGS);
					}
					else{
						session.setAttribute("editSmsCampaign", "view");
						session.setAttribute("smsDraftStatus", draftStatus);
						Redirect.goTo(PageListEnum.SMS_CAMP_SETTINGS);
					}
				}
				
			}else if(imgAttr.equalsIgnoreCase("Copy")){
				
			}else if(imgAttr.equalsIgnoreCase("Delete")){
				
				List<SMSCampaigns> clist = new ArrayList();
				String res = "";
				String msg = "Are you sure you want to delete selected SMS campaign?";
				String status = smsCampaign.getStatus();
				if(status.equalsIgnoreCase(Constants.CAMP_STATUS_ACTIVE) || 
						status.equalsIgnoreCase(Constants.CAMP_STATUS_RUNNING)){
					msg = "Are you sure do you want to delete the '"+status.toLowerCase()+"' SMS campaign?";
				}
				try {
					int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == 1){
						clist.add(smsCampaign);
						res = deleteSmsCampaigns(clist);
						if(res.equals("success")){
							MessageUtil.setMessage("Selected SMS campaign deleted successfully.","color:blue","TOP");
							smsCampListlbId.removeItemAt(index);
						}
						if(res.equals("prolem")){
							MessageUtil.setMessage("Problem experienced while deleting the SMS campaign. Please try again later.","color:red","TOP");
						}
					}//if
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
			}//else if
			else if(imgAttr.equalsIgnoreCase("Report")) {
				try {
					 
					smsCampaign =  (SMSCampaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
					PageUtil.clearHeader();
					MessageUtil.clearMessage();
					Long userId = GetUser.getUserId();
					long reportCount = smsCampaignReportDao.getReportCountByCampaign(smsCampaign.getSmsCampaignName(), userId);
					if(reportCount < 1){
						MessageUtil.setMessage("No records exist for the SMS : "+smsCampaign.getSmsCampaignName(),"color:red", "TOP");
					}else{
						sessionScope.put("smsCampaign",smsCampaign);
						sessionScope.put("smsCampreport","true");
						Redirect.goTo(PageListEnum.CAMPAIGN_SMS_REPORT);
					}
				} catch (Exception e) {
				    logger.error("** Exception **" + e);
				}
				
				
			}//else if
		}//if
		else if (event.getTarget() instanceof Popup) {
			try {
				OpenEvent openEvent = (OpenEvent)event;
				Image img = (Image)openEvent.getReference();
				logger.info(img.getParent().getParent().getParent());
				logger.info("" +((Listitem)img.getParent().getParent().getParent()).getValue());
				smsCampaign =  (SMSCampaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
				
				if(currentUser!=null && currentUser.getSubscriptionEnable() && smsCampaign.getCategory() ==null
						&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				copySMSCampaignPopupId.setAttribute("smsCampaign",smsCampaign);
				copySmsCampaignNameTbId.setValue("Copy of " + smsCampaign.getSmsCampaignName());
			} catch (Exception e) {
			}
		}
		else if(event.getTarget() instanceof Paging) {
			
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			if(paging.getAttribute("onPaging").equals("topPaging")) {
				
				this.campListPaging1.setActivePage(desiredPage);
				
			}else if(paging.getAttribute("onPaging").equals("bottomPaging")) {
				
				this.campListPaging.setActivePage(desiredPage);
				
			}//else if
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
			
		}//else if
		}catch (Exception e) {
			logger.error("** Exception ",e);
		}
	}//onevent()
	
	/**
	 * This method is to show the message if SPC is enabled and if the SMSCampaign doesn't have category
	 * @param smsCampaign
	 * @return if SPC is enable and SMSCampaign without category
	 * */
	
	private void ifSPCenable(SMSCampaigns smsCampaign){
		
		if(currentUser!=null && currentUser.getSubscriptionEnable() && smsCampaign.getCategory() ==null
				&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)){
			MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
				"\n Please specify campaign category for this communication and try again.", "color:red;");
			return;
		}
	}
	
	public void redraw(int startIndex, int size) {
		 
		try{
			
			MessageUtil.clearMessage();
			logger.debug("-- just entered redraw() --");
			int count =  smsCampListlbId.getItemCount();
			
			for(; count>0; count--) {
				smsCampListlbId.removeItemAt(count-1);
			}
			
			//System.gc();
			 
			List<SMSCampaigns> campList = null;
			List<SMSCampaigns> campaignList = new ArrayList<SMSCampaigns>();
			 
			String value = srchLbId.getSelectedItem().getValue().toString();
			List<SMSCampaigns> finalList = new ArrayList<SMSCampaigns>();
			if(value.equals(SEARCH_BY_NAME)) {
			    
				campList =  smsCampaignsDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
			
			else if(value.equals(SEARCH_BY_DATE)) {
				
				campList =  smsCampaignsDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
      
			else if(value.equals(SEARCH_BY_STATUS)) {
				String status = getSelectedStatus();
				if(!status.equals("All")) {
					int sizeTotal = smsCampaignsDao.getCount(currentUser.getUserId(), "All");
					campList =  smsCampaignsDao.getCampaignsByStatus(currentUser.getUserId(), 0, sizeTotal,orderby_colName,desc_Asc);
					List<SMSCampaigns> allSmsCampList=new ArrayList<SMSCampaigns>();
					if(campList != null) {
						//finalList = campList;
							for (SMSCampaigns campaigns : campList) {
								String campStatus = getCampaignStatus(campaigns);
								
								
									if(campStatus.equalsIgnoreCase(status) ){
										campaigns.setStatus(campStatus);
										allSmsCampList.add(campaigns);
									}
															
								
							 
							}
							if(allSmsCampList.size() > 0){
								 for(int i=startIndex;i<size+startIndex&&i<allSmsCampList.size();i++)
								 {
									logger.info("setting :LLLLLLLL"+allSmsCampList.get(i).getCategory());
									finalList.add(allSmsCampList.get(i));
									
								 }
								//finalList = allSmsCampList;
							}
					}
					campListPaging.setTotalSize(allSmsCampList.size());
				    //campListPaging.setAttribute("onPaging", "topPaging");
					//campListPaging.addEventListener("onPaging", this); 
					campListPaging1.setTotalSize(allSmsCampList.size());
				}//if
				else{
				
					List<SMSCampaigns> tempList = smsCampaignsDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
					for (SMSCampaigns campaigns : tempList) {
						String campStatus = getCampaignStatus(campaigns);
						campaigns.setStatus(campStatus);
						finalList.add(campaigns);
					}
				}
				
			}
			
			 TimeZone clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
			 if(finalList != null && finalList.size() > 0) {
				 
				 Listitem item;
				 Listcell lc;
				 for (SMSCampaigns smsCampaign : finalList) {
					
					item = new Listitem();
					
					item.setValue(smsCampaign);
					item.setHeight("30px");
					
					lc = new Listcell();
					lc.setParent(item);
					
					lc = new Listcell(smsCampaign.getSmsCampaignName());
					lc.setTooltiptext(smsCampaign.getSmsCampaignName());
					lc.setParent(item);
					
					String listName = Constants.STRING_NILL;
					SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
					if(smsCampaign.getListType() != null && !smsCampaign.getListType().equalsIgnoreCase("Total")){
						listName = "Segment of ("+smsCampaign.getListNames()+")";
						String segRuleIds = smsCampaign.getListType().split(""+Constants.DELIMETER_COLON)[1];
						List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
						if(segmenRules!=null) listName = "Segment of ("+getListNames(smsCampaign)+")";
						else listName="--";
						
					}else if (smsCampaign.getListType() == null || smsCampaign.getListType().equalsIgnoreCase("Total")){
						listName = getListNames(smsCampaign);
						
					}
					
					lc = new Listcell(listName);
					lc.setTooltiptext(listName);
					/*lc = new Listcell(smsCampaign.getListNames());
					lc.setTooltiptext(smsCampaign.getListNames());*/
					lc.setParent(item);
					
					
					String status = getCampaignStatus(smsCampaign);
					
					
					 int index = smsCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						logger.info(getSelectedStatus());
						itemsSize ++; 
						
					}
					
					if(smsCampStatusLb.getSelectedItem() != null && 
							!smsCampStatusLb.getSelectedItem().getLabel().equals("All") && !smsCampStatusLb.getSelectedItem().getLabel().equals(status)) continue;
									
					lc = new Listcell(status);
					lc.setParent(item);
					
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- STARTS
					lc = new Listcell();
					lc.setLabel(smsCampaign.getScheduledOccurrence() != null ? smsCampaign.getScheduledOccurrence() : "--");
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(smsCampaign.getScheduledDates() != null ? smsCampaign.getScheduledDates() : "--");
					lc.setParent(item);
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- ENDS
					
					
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(smsCampaign.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					logger.info("creation date: "+smsCampaign.getCreatedDate()+"");
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(smsCampaign.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(item);
					
					/*
					logger.info("calendar : "+ smsCampaign.getModifiedDate());
					lc = new Listcell(smsCampaign.getModifiedDate()+"");
					lc.setParent(item);
					*/
					
					/*String status = getCampaignStatus(smsCampaign);
					
					
					 int index = smsCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						logger.info(getSelectedStatus());
						itemsSize ++; 
						
					}
					
					if(smsCampStatusLb.getSelectedItem() != null && 
							!smsCampStatusLb.getSelectedItem().getLabel().equals("All") && !smsCampStatusLb.getSelectedItem().getLabel().equals(status)) continue;
									
					lc = new Listcell(status);
					lc.setParent(item);*/
					
					
					
					
					lc = new Listcell();
					
					Hbox hbox = new Hbox();
					hbox.setAlign("center");
					hbox.setStyle("cursor:pointer;margin-right:5px;");
					
					Image img = new Image();
					img.setSrc("/img/email_page.gif");
					img.setTooltiptext("Send Test SMS");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("addEvent", "sendTestSMS");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					/* APP -3855 img = new Image();
					img.setSrc("/img/email_go.gif");
					img.setTooltiptext("Send Again");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("addEvent", "sendAgain");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
*/					
					img = new Image();
					img.setSrc("/img/email_edit.gif");
					img.setTooltiptext("Edit");
					img.setStyle("cursor:pointer;margin-right:3px;");
					img.setAttribute("addEvent", "Edit");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/copy.gif");
					img.setTooltiptext("Copy");
					img.setStyle("cursor:pointer;");
					img.setPopup(copySMSCampaignPopupId);
					img.setAttribute("addEvent", "Copy");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/action_delete.gif");
					img.setTooltiptext("Delete");
					img.setStyle("cursor:pointer;");
					img.setAttribute("addEvent", "Delete");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/theme/home/reports_icon.png");
					img.setTooltiptext("Reports");
					img.setStyle("cursor:pointer;");
					img.setAttribute("addEvent", "Report");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					hbox.setParent(lc);
					lc.setParent(item);
					
					item.setHeight("30px");
					item.setParent(smsCampListlbId);
				}//if
				
				copySMSCampaignPopupId.addEventListener("onOpen", this);	
					
				}
			/* int index = smsCampStatusLb.getSelectedIndex();
			 if(!(index <= 0)){
				 if( campListPaging.getPageSize() == itemsSize ){
					 campListPaging.setPageSize(itemsSize);
					 //campListPaging1.setPageSize(itemsSize);
					 }
				 else{
					 if(index ==1 || index == 2){
						 campListPaging.setTotalSize(itemsSize);
						 //campListPaging1.setTotalSize(itemsSize);
						 itemsSize=0;
					 }
					 campListPaging.setPageSize(campListPaging.getPageSize());
					 //campListPaging1.setPageSize(campListPaging.getPageSize());
				 }
			 }*/
			
			
			
			
		}
		catch(Exception e){
			logger.error("Exception ", e);
		}
		
		//System.gc();
		 
		 
		/*List<SMSCampaigns> smsCampList=null;
		 if(!getSelectedStatus().equalsIgnoreCase("All"))
		 {
		 List<SMSCampaigns> dsmsCampList = getSmsCampaigns(0, totalSize);
		 smsCampList = new ArrayList<SMSCampaigns>();
		 List<SMSCampaigns> allSmsCampList=new ArrayList<SMSCampaigns>();
		 for (int i=0;i<dsmsCampList.size();i++) {
			 String status = getCampaignStatus(dsmsCampList.get(i));
			 int index = smsCampStatusLb.getSelectedIndex();
			 if(!(index <= 0))
			if(status.equalsIgnoreCase(getSelectedStatus()) 
					//&& !getSelectedStatus().equalsIgnoreCase("All")
					){
				allSmsCampList.add(dsmsCampList.get(i));
				logger.info("setting value is"+dsmsCampList.get(i).getSmsCampaignName());
			}
			
		 }
		// logger.info("first and last "+startIndex+"  "+size);
		 
		// if(!source.equalsIgnoreCase("doaftercompose"))size+=Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		 for(int i=startIndex;i<size+startIndex&&i<allSmsCampList.size();i++)
		 {
			 logger.info("getting value is"+allSmsCampList.get(i).getSmsCampaignName());
			 smsCampList.add(allSmsCampList.get(i));
			 //smsCampList.add(allSmsCampList.get(i));
		 }
		 
		 }
		 else
		 {
			 smsCampList = getSmsCampaigns(startIndex, size);
		 }
		 
		 if(smsCampList.size()==0) return;
		 */
		

		 
	 }//redraw
	
	
	 public String getCampaignStatus(SMSCampaigns smsCampaign) {
		    TimeZone clientTimeZone = (TimeZone)sessionScopeForTimeZone.getAttribute("clientTimeZone");
			String status = smsCampaign.getStatus();
			
			SMSCampaignScheduleDao smsCampaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
			
			List<SMSCampaignSchedule> scheduleList = smsCampaignScheduleDao.getBySmsCampaignId(smsCampaign.getSmsCampaignId());
			
			byte activeCount = 0;
			byte sentCount = 0;
			byte subscriptionCount = 0;
			byte draftCount = 0;
			
			if(scheduleList == null || scheduleList.size() == 0) {
						return status;
			}
			
			
			Calendar startCal =null;
			Calendar endCal = null;
			//boolean draftStatus = campaign.getStatus().equalsIgnoreCase("Draft") ;
			
			SMSCampaignSchedule latestCampaignSchedule = null;
			
			
			
			
			for (SMSCampaignSchedule smsCampaignSchedule : scheduleList) {
					
				if(latestCampaignSchedule == null) latestCampaignSchedule = smsCampaignSchedule;

				if(smsCampaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
					latestCampaignSchedule = smsCampaignSchedule;
				}
				
				//logger.info("latestCampaignSchedule======"+latestCampaignSchedule);
				
					//if campSchedule status is sent no need to do anything
					/*if(smsCampaignSchedule.getStatus() == 1) {
						sentCount++;
						continue;
					}
					
					//if submit and campSchedule status is draft changing it to active
					if((smsCampaignSchedule.getStatus() == 0 )) {
						activeCount++;
					}
					
					if(smsCampaignSchedule.getStatus() == 2 ) {
						draftCount++;
					}
				 
					if(smsCampaignSchedule.getStatus() == 3 ) {
						subscriptionCount++;
					}*/
					
					
					//2.4.7. related -- place 1 STARTS 
					if((smsCampaignSchedule.getStatus() != 0 )) {
						continue;
					}
					
					
					if(startCal== null && endCal== null){
						startCal = smsCampaignSchedule.getScheduledDate();
						endCal = smsCampaignSchedule.getScheduledDate();
						
						
					}
					
					
					if(endCal != null && endCal.before(smsCampaignSchedule.getScheduledDate())){
				    	endCal = smsCampaignSchedule.getScheduledDate();
				    }
					if(startCal.after(smsCampaignSchedule.getScheduledDate())){
				    	startCal = smsCampaignSchedule.getScheduledDate();
				    }
					
					
					//2.4.7. related -- place 1 ENDS
					
				 
			}//for
			
			
			//multiple wala 22nd jan 2016 --starts
			/*try{
				
				for (SMSCampaignSchedule smsCampaignSchedule : scheduleList) {
					if(smsCampaignSchedule.getStatus() == 2 && smsCampaignSchedule.getScheduledDate().before(Calendar.getInstance())){
						smsCampaignSchedule.setStatus((byte)7);
					}
				}
				
				smsCampaignScheduleDao.saveByCollection(scheduleList);
				
				
			}catch(Exception e){
				logger.error("Exception >>>>>>>> ",e);
			}*/
			//multiple wala 22nd jan 2016 --ends
			
			
			
			//2.4.7. related -- place 2 STARTS
			
			String scheduledOnOrBetweenDates = null;
			
			if(startCal != null && endCal !=null){
				if(!startCal.equals(endCal)){
			
				//logger.info("Active count==>>"+activeCount + "Sent count==>>"+sentCount + "Start Calender"+startCal + "End Date" + endCal);
					smsCampaign.setScheduledOccurrence("Recurring");
				scheduledOnOrBetweenDates = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_STDATE, clientTimeZone)+" - "+
						MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_STDATE, clientTimeZone);
				smsCampaign.setScheduledDates(scheduledOnOrBetweenDates);
				
			 }else{
				 smsCampaign.setScheduledOccurrence("One-Time");
				 smsCampaign.setScheduledDates(MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			 }/*else{
				 System.out.println("---3----");
				 campaign.setScheduledOccurrence("--");
				 campaign.setScheduledDates("--");
				 logger.info("Campaign Name==="+campaign.getCampaignName());
			 }*/
			}
			
			
			//2.4.7. related -- place 2 ENDS
			 
			/* if(activeCount >0 && sentCount > 0){
				
				status = Constants.CAMP_STATUS_RUNNING;
			}
			else if(activeCount == 0 && sentCount > 0) {
				status = Constants.CAMP_STATUS_SENT;
			}else if(activeCount >0 && sentCount == 0){

				status = Constants.CAMP_STATUS_ACTIVE ;
			}else if(sentCount > 0 && subscriptionCount > 0) {
				
				status = Constants.CAMP_STATUS_SUSPEND;
			}else if(draftCount >0 ) {
				status = Constants.CAMP_STATUS_DRAFT;
			}
			 
			 logger.info("status>>>>>>>>>>>>>>"+status);*/
			 
			 
			// if(latestCampaignSchedule != null){
				 if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
					 return latestCampaignSchedule.getStatusStr();
				 else if(latestCampaignSchedule.getStatus() >= 3)
					 return "Schedule Failure";
				 else
					 return "Draft";

			// }
			 
			 
			 
			// return status;
			 
			 
		 }
		 
	
	
	
 public String getSelectedStatus() {
		 
		 
		 int index = smsCampStatusLb.getSelectedIndex();
			
			String status = "All";
			if (index != -1)
				status = smsCampStatusLb.getSelectedItem().getLabel();
		 
		 
			return status;
			
	 }
	
	
	/**
	 * this method returns the list of SMS Campaigns based on the status selected
	 * @return List<SMSCampaigns>
	 */
	
	public List<SMSCampaigns> getSmsCampaigns(int firstResult, int maxResult){
		
		
		logger.debug("-- Just Entered -- ");
		
		List<SMSCampaigns> smsCampList = null;
		
		//int index = smsCampStatusLb.getSelectedIndex();
		
		String status = "All";
		/*if (index != -1)
			status = smsCampStatusLb.getSelectedItem().getLabel();*/
		
		smsCampList = smsCampaignsDao.getSmsCampaignsByStatus(userId, status, firstResult, maxResult );
		
		
		
		
		TimeZone clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		
		for (SMSCampaigns smsCampaign : smsCampList) {
			
			try{
				smsCampaign.setModifiedDate(new MyCalendar(smsCampaign.getModifiedDate(),
						clientTimeZone,MyCalendar.FORMAT_DATETIME_STDATE));
				//TODO need to append 'segmented' if this campaign associated mailing list is segmented
				if(smsCampaign.getListType() != null && !smsCampaign.getListType().equalsIgnoreCase("Total")) {
					logger.info("the campaign is segmented......"+smsCampaign.getListNames()+"(segmented)");
					smsCampaign.setListNames("Segment of("+getListNames(smsCampaign)+")");
					
				} else if (smsCampaign.getListType() == null || smsCampaign.getListType().equalsIgnoreCase("Total")) {
					smsCampaign.setListNames(getListNames(smsCampaign));
				}
				
			} catch (Exception e) {
				
				logger.error(" ** Exception : ",(Throwable)e);
			}
		} // for
		logger.debug("SMS Campaign list size :" + smsCampList.size());
		return smsCampList;
		
		
		
	}
	
	
	public String getListNames(SMSCampaigns smsCampaign) {
		String listNames = "";
		
		Set<MailingList> mailingLists= smsCampaign.getMailingLists();
		if(mailingLists.size()>0) {
			for(MailingList ml : mailingLists) {
				
				if(listNames.length() > 0) {
					listNames +=","+ml.getListName();
				}else {
					listNames += ml.getListName();
				}
			} // for
		}else {
			
			listNames = "--";
		}
		return listNames;
	}
	
	
	
	Bandbox smsActionsBandBoxId;
	public void onSelect$smsCampListlbId() {
		
		if(smsCampListlbId.getSelectedCount() == 0){
			
			smsActionsBandBoxId.setDisabled(true);
			smsActionsBandBoxId.setButtonVisible(false);
		}else if(smsCampListlbId.getSelectedCount() > 0) {
			
			smsActionsBandBoxId.setDisabled(false);
			smsActionsBandBoxId.setButtonVisible(true);
		}
		
		
		
	}//onSelect$campListlbId()
	
	
	
	
	/**
	 * this method sends the Test SMS
	 */
	private Label testSMSWinId$msgLblId;
	private Textbox testSMSWinId$mblNumTxtBoxId;

	public void onClick$sendTestSmsBtnId$testSMSWinId() {
		try{
			logger.debug("----just entered----"+testSMSWinId);
			String[] mblNumArr = null;
			long mblNumber = 0;
			ArrayList<String> msgContentLst = null;
			
			SMSCampaigns smsCampaign = (SMSCampaigns)testSMSWinId.getAttribute("SmsCampaign");
			
			//SMSCampaigns smsCampaign = (SMSCampaigns)smsCampListlbId.getSelectedItem().getValue();
			String msgContent = "Test SMS"+"\n";
			String sendingMsg = smsCampaign.getMessageContent();
			String senderId = smsCampaign.getSenderId();
			//String mblNum = ((Textbox)testSMSWinId.getFellowIfAny("mblNumTxtBoxId")).getValue();
			String mblNum = testSMSWinId$mblNumTxtBoxId.getText();
			//testSMSWinId$msgLblId = (Label)testSMSWinId.getFellowIfAny("msgLblId");
			byte sizeOverOption = smsCampaign.getMessageSizeOption();
			String templateRegisteredId=smsCampaign.getTemplateRegisteredId();
			
			/*
			 * if(smsCampaign.getMessageType() == 1){
				placeHolders = msgContent.contains("|^GEN_") | msgContent.contains("|^CF_");
				msgContentLst = splitSMSMessage(msgContent, 160);
			}
			else if(smsCampaign.getMessageType() == 2) {
				msgContent = Utility.HexToString(msgContent);
				placeHolders = msgContent.contains("|^GEN_") | msgContent.contains("|^CF_");
				msgContentLst = splitSMSMessage(msgContent, 70);
			}
			 */
			
			/*if(smsCampaign.getMessageType() == 1){
				msgContentLst = splitSMSMessage(msgContent, 160);
			}
			else if(smsCampaign.getMessageType() == 2) {
				logger.info("just entered for language option=====>"+msgContent);
				msgContent = Utility.HexToString(msgContent);
				logger.info("just entered for language option after con=====>"+msgContent);
				msgContentLst = splitSMSMessage(msgContent, 70);
			}*/
			
			//msgContentLst = splitSMSMessage(smsCampaign.getMessageContent(),160);
			
			String tempMsgType = smsCampaign.getMessageType()+"";
			//Added for UAE
			UserOrganization userOrganization = currentUser.getUserOrganization();
			if(userOrganization == null){
				MessageUtil.setMessage("User does not belong to any organization.", "color:red;","top");
				return ;
			}
			/****validate mobile numbers********/
			int mobCnt =0;
			int charCount =0;
			int usedCount =0;
			if(mblNum != null || !mblNum.trim().equals("")){
				logger.info("---just entered---");
				if(mblNum.contains(",")){
					
					mblNumArr = mblNum.split(",");
					mblNum = "";
					mobCnt = mblNumArr.length;
					for(String mobileNum : mblNumArr) {
						/**
						 * Here we are validating with regular expression &
						 * number of digits should be greater than MINIMUM_NO_DIGITS
						 * &
						 * should be LESSTHAN MAX_NO_DIGITS
						 */
						
						/*if((Utility.phoneParse(mobileNum, userOrganization) != null) && 
								( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits()))) {
							
							if(!mobileNum.startsWith(currentUser.getCountryCarrier().toString()) &&
									( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits())) ){
								mobileNum = currentUser.getCountryCarrier().toString()+mobileNum;
							}
							
							mblNumber = Long.parseLong(mobileNum);
							if(mblNum.length()>0)	mblNum += ",";
							mblNum += mobileNum;
						}
						else{
							MessageUtil.setMessage("Please enter valid number.", "color:red;","top");
							testSMSWinId$mblNumTxtBoxId.setText("");
							return;
						}*/
						if(Utility.phoneParse(mobileNum, userOrganization) != null){
							if(userOrganization.isRequireMobileValidation()){
								if( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits())) {
							
							if(!mobileNum.startsWith(currentUser.getCountryCarrier().toString()) &&
									( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits())) ){
								mobileNum = currentUser.getCountryCarrier().toString()+mobileNum;
							}
						 }
						}
							mblNumber = Long.parseLong(mobileNum);
							if(mblNum.length()>0)	mblNum += ",";
							mblNum += mobileNum;
						}
						else{
							MessageUtil.setMessage("Please enter valid number.", "color:red;","top");
							testSMSWinId$mblNumTxtBoxId.setText("");
							return;
						}
						
					}
				}else{
					logger.debug("Utility.validateUserPhoneNum(mblNum) ........:"+Utility.validateUserPhoneNum(mblNum));
					logger.debug(" (mblNum.length() >= userOrganization.getMinNumberOfDigits()) && (mblNum.length() <= userOrganization.getMaxNumberOfDigits())"+  ((mblNum.length() >= userOrganization.getMinNumberOfDigits()) && (mblNum.length() <= userOrganization.getMaxNumberOfDigits())));
					if(Utility.phoneParse(mblNum, userOrganization) != null){ 
						if(userOrganization.isRequireMobileValidation()){
							if( (mblNum.length() >= userOrganization.getMinNumberOfDigits()) && (mblNum.length() <= userOrganization.getMaxNumberOfDigits())){
						
						
							if(!mblNum.startsWith(currentUser.getCountryCarrier().toString()) && 
									( (mblNum.length() >= userOrganization.getMinNumberOfDigits()) && (mblNum.length() <= userOrganization.getMaxNumberOfDigits()))) {
								
								mblNum = currentUser.getCountryCarrier().toString() + mblNum;
							}
					      }
						}
						mblNumber = Long.parseLong(mblNum);
					}
					else {
						
						MessageUtil.setMessage("Please enter valid number.", "color:red;","top");
						testSMSWinId$mblNumTxtBoxId.setText("");
						return;
					}
					
					mobCnt = 1;
				}
				
				
				if(currentUser.getSmsCount()==0){
					
					MessageUtil.setMessage("SMS credits are not available. Please renew the account.", "color:red;","top");
					
					return ; 
					
				}
				
				UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)SpringUtil.getBean("userSMSGatewayDao");
				OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)SpringUtil.getBean("OCSMSGatewayDao");
				/*UserSMSGateway userSmsGateway = userSmsGatewayDao.findByUserId(currentUser.getUserId(),
						SMSStatusCodes.defaultSMSTypeMap.get(currentUser.getCountryType()));*/
				String accountType = SMSStatusCodes.countryCampValueMap.get(currentUser.getCountryType()).get(tempMsgType);
				if(SMSStatusCodes.optInMap.get(currentUser.getCountryType())) {
					
					 accountType = !smsCampaign.isEnableEntireList() ? 
							 Constants.SMS_ACCOUNT_TYPE_OPTIN : accountType;
					logger.info("accountTypeaccountType"+accountType);
				}
				UserSMSGateway userSmsGateway = userSmsGatewayDao.findByUserId(userId, accountType);
				if(userSmsGateway == null) {
					
					logger.debug("Error while getting userSmsGateway details...");
					return;
					
				}
				OCSMSGateway ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
				if(ocgateway == null) {
					
					logger.debug("Error while getting ocgateway details...");
					MessageUtil.setMessage("No SMS set up for your your account.Please contact admin.","color:red","TOP");
					return ;
					
				}
				
				//adding header and footer for test msg content (harshi)
				String messageHeader = Constants.STRING_NILL;
				logger.info("before add headers");
				if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())) {
					logger.info("entered add headers");
					SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
					List<SMSSettings> smsSettings = smsSettingsDao.findByUser(userId);
					if(smsSettings != null) {

						SMSSettings optinSettings = null;
						SMSSettings optOutSettings = null;
						SMSSettings helpSettings = null;
						
						for (SMSSettings eachSMSSetting : smsSettings) {

							if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
							else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
							else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;

						}
						if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
						else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
						else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
						
						sendingMsg = messageHeader != null ? msgContent+messageHeader +"\n"+sendingMsg : sendingMsg;
						//if(smsCampaign.isEnableEntireList() && optOutSettings != null) {
							if(smsCampaign.getMessageType() != null && !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)
									&&smsCampaign.isEnableEntireList() && optOutSettings != null) {
								if(!currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
							sendingMsg = sendingMsg+ (optOutSettings.getKeyword() != null ?
									("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
											: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
								}else {
									
									sendingMsg = sendingMsg+ (optOutSettings.getKeyword() != null ?
											("\n" + optOutSettings.getKeyword())
													: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
									
								}

						}

					}
				}

				
				
				logger.info("sendingMsg after--- "+sendingMsg);
				charCount = sendingMsg.length();
			    usedCount =1;
			    if(charCount>160) usedCount=(charCount/160) + 1;
				//sendingMsg = StringEscapeUtils.escapeHtml(sendingMsg);
				logger.info("mobile numbers are===>"+mblNum);
				/*****send test SMS based on the selected sending option********/
				if(sizeOverOption == 1){
					for (int i = 0; i <msgContentLst.size(); i++) {
						sendingMsg = msgContentLst.get(i);
						
						if(msgContentLst.size()>1)  { 
							sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
						}
						
						//logger.info(sendingMsg+""+msgLblId);
						/*SMSCountryApi.sendSingleSMS(mblNum, 
										sendingMsg, smsCampaign.getSenderId(), "tempMsgType");*/
						//sendingMsg = StringEscapeUtils.escapeHtml(sendingMsg);

						captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, ""+mblNum,
								tempMsgType,  "9848495956", ""+mblNum, "1", senderId,templateRegisteredId);
					} // for each
				}else if(sizeOverOption == 2) {
					
					sendingMsg = msgContentLst.get(0);
					//SMSCountryApi.sendSingleSMS(mblNum, sendingMsg, smsCampaign.getSenderId(), "tempMsgType");
					//sendingMsg = StringEscapeUtils.escapeHtml(sendingMsg);

					captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, ""+mblNum,
							tempMsgType,  "9848495956", ""+mblNum, "1", senderId,templateRegisteredId);
					
					
				}else if(sizeOverOption == 4) {
					
					if(msgContentLst.size()>1){
						testSMSWinId$msgLblId.setStyle("color:green;font-family:verdana;font-size:11px;");
						testSMSWinId$msgLblId.setValue("according to the sending option selected,the SMS cant be send.");
					}else{
						
						sendingMsg = msgContentLst.get(0);
						
						//SMSCountryApi.sendSingleSMS(mblNum, sendingMsg, smsCampaign.getSenderId(), "tempMsgType");
						//sendingMsg = StringEscapeUtils.escapeHtml(sendingMsg);

						captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, ""+mblNum,
								tempMsgType,  "9848495956", ""+mblNum, "1", senderId,templateRegisteredId);
						
					}//else
				}else if(sizeOverOption == 8){
					
					/* SMSCountryApi.sendSingleSMS(mblNum, msgContent
								, smsCampaign.getSenderId(), "tempMsgType");*/
					
					//msgContent = StringEscapeUtils.escapeHtml(msgContent);
					captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, ""+mblNum,
							tempMsgType,  "9848495956", ""+mblNum, "1", senderId,templateRegisteredId);
					
				}//else if
				/*msgLblId.setStyle("color:green;font-family:verdana;font-size:11px;");
				msgLblId.setValue("Test SMS will be sent in a moment");*/
				
				/**
			     * Update the Used SMS count
			     */
			    try{
			     //UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			    UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			     //usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), mobCnt);
			    
			    usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), usedCount*mobCnt);
			    }catch(Exception exception){
			     logger.error("Exception while updating the Used SMS count",exception);
			    }
			    
			    
				String msgType=smsCampaign.getMessageType();
				
				if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){
				
					if(!((String) msgType).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)){
						
						MessageUtil.setMessage("If mobile number is in NDNC list, test SMS won't be sent. ","color:blue;");
					}
				}
	
				/**
				 * Update the SMS Queue
				 */
				SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
				smsQueueHelper.updateSMSQueue(mblNum, msgContent ,Constants.SMS_MSG_TYPE_TEST, currentUser,senderId );
				
				testSMSWinId.setVisible(false);
				MessageUtil.setMessage("Test SMS will be sent in a moment.", "color:blue", "TOP");
				
				
			}else{
				testSMSWinId$msgLblId.setStyle("color:red;font-family:verdana;font-size:11px;");
				testSMSWinId$msgLblId.setValue("provide Mobile number");
			}
			}catch(NumberFormatException e) {
				testSMSWinId$msgLblId.setStyle("color:red;font-family:verdana;font-size:11px;");
				testSMSWinId$msgLblId.setValue("Please provide valid mobile number");
				logger.error("Exception while parsing the mobile number");
			}
			catch(Exception e) {
				logger.error("Exception ::", e);
			}
	}
	
	/**
	 * 
	 * @param mblNum
	 * @param sendingMsg
	 * @param messageType
	 * @param smsCampaign
	 */
	private void updateSMSQueue(String mblNum,String msgContent , String messageType , SMSCampaigns smsCampaign) {
		logger.debug(">>>>>>> Started ViewSMSCampaign :: updateSMSQueue <<<<<<< ");

		if(mblNum == null){
			logger.error("Error While Updating SMS Queue as the no Mobile Number exist's");
			return;
		}

		List<SmsQueue> smsQueues = new ArrayList<SmsQueue>();
		String []mobArray = mblNum.split(",");

		//SmsQueueDao smsQueueDao = null;
		SmsQueueDaoForDML smsQueueDaoForDML = null;

		try{
		//	smsQueueDao = (SmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_QUEUE_DAO);
			smsQueueDaoForDML = (SmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_QUEUE_DAO_ForDML);
		}
		catch(Exception exception){
			logger.error("Error While creating SmsQueueDao Object....:",exception);
		}

		for(String mobileNumber : mobArray){

			if(smsQueueDaoForDML != null){
				SmsQueue smsQueue = new SmsQueue();
				smsQueue.setMessage(msgContent);
				smsQueue.setMsgType(messageType);
				smsQueue.setToMobilePhone(mobileNumber);
				smsQueue.setUser(currentUser);
				smsQueue.setSentDate(MyCalendar.getNewCalendar());
				smsQueue.setStatus(Constants.CAMP_STATUS_ACTIVE);
				//smsQueue.setSmsCampaigns(smsCampaign);
				//Add to list
				smsQueues.add(smsQueue);
			}
		}//for each
		/**
		 * Storing to DB 
		 */

		if(smsQueueDaoForDML != null){
			//smsQueueDao.saveByCollection(smsQueues);
			smsQueueDaoForDML.saveByCollection(smsQueues);

		}

		logger.debug(">>>>>>> Completed ViewSMSCampaign :: updateSMSQueue <<<<<<< ");
	}//updateSMSQueue

	/**
	 * this splits the given SMS 
	 * @param msgContent
	 * @param size
	 * @return List of message tokens
	 */
	private ArrayList<String> splitSMSMessage(String msgContent, int size) {
		
		try {
			ArrayList<String> retList = new ArrayList<String>();
			
			int skipCounter=0;
			
			do {
				skipCounter++;
				
				if(msgContent.length() > size-10) {
					
					int endInd = msgContent.indexOf(' ', size-20);
					
					if(endInd==-1 || endInd > size-5) {
						endInd = msgContent.lastIndexOf(' ', size-20);
					}
					
					if(endInd==-1) {
						logger.info("No Spaces in the Given Token");
						break;
					}

					/*
					 * While splitting, if it is inside the Place holder
					 * then find out for a new space before/after from that position for split
					 */  
					
					
					if(msgContent.lastIndexOf("|^", endInd) != -1 && 
							msgContent.indexOf("^|", endInd) != -1 &&
							(endInd > msgContent.lastIndexOf("|^", endInd)) && 
							(endInd < msgContent.indexOf("^|", endInd)) )  {
						
						
						int phStInd = msgContent.lastIndexOf("|^", endInd);
						int phEnInd = msgContent.indexOf("^|", endInd);
						
						int tempEndInd=endInd;
						
						if(msgContent.substring(phStInd+2, endInd).indexOf("^|")==-1) {
							endInd = msgContent.lastIndexOf(' ', phStInd);
						}
						
						if(endInd==-1 && (msgContent.substring(tempEndInd, phEnInd).indexOf("|^")==-1)) {
							endInd = msgContent.indexOf(' ', phEnInd+1);
						}
						if(endInd==-1)	{
							break;
						}
						
					} // if
					
					String tempStr = msgContent.substring(0,endInd);
					retList.add(tempStr);
					
					msgContent = msgContent.substring(endInd+1);
					
					
				} //if
				
				if(skipCounter>40) {
					break;
				}
			} while(msgContent.length() > size-10);
			
			if(msgContent.length() > 0) {
				retList.add(msgContent);
			}
			
			return retList;
			
		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",e);
			return null;
		}
		
	}
	
	/**
	 * this method allows to copy the selected SMS Campaign
	 */
	public void onClick$smsCampCopyBtnId(){
		try{
			MessageUtil.clearMessage();
			SMSCampaigns smsCampaign = (SMSCampaigns)copySMSCampaignPopupId.getAttribute("smsCampaign");
			
			String copySMSCampName = copySmsCampaignNameTbId.getValue();
			if(smsCampaign == null){
				logger.debug("selected SMS Campaign is null");
				return;
			}
			if(copySMSCampName == null || !(Utility.condense(copySMSCampName).length()>0)){
				Messagebox.show("Provide SMS campaign name. Name cannot be left empty.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			if(!Utility.validateName(copySMSCampName)) {
				Messagebox.show("Provide valid SMS campaign name. Name should not contain any special characters.",
						"Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			if(smsCampaignsDao.checkName(copySMSCampName, userId)){
				Messagebox.show("SMS campaign name already exists. Please provide another name.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			logger.debug("SMS Campaign is======>"+smsCampaign.getSmsCampaignName());
			
			SMSCampaigns newCampaign = smsCampaign.getCopy();
			String oldName = smsCampaign.getSmsCampaignName();
			newCampaign.setSmsCampaignName(copySMSCampName);
			//smsCampaignsDao.saveOrUpdate(newCampaign);
			smsCampaignsDaoForDML.saveOrUpdate(newCampaign);
			
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show("Copy of '" + oldName + "' SMS is created with '" + copySMSCampName 
					+ "' name. \n You have to reschedule the SMS by clicking on edit SMS.", 
					"Information", Messagebox.OK, Messagebox.INFORMATION);
			
			try {
				if(self.getParent() instanceof Include) {
					Include currentInclude = (Include)self.getParent();
					currentInclude.setSrc("/zul/Empty.zul");
					currentInclude.setSrc("/zul/campaign/SMSCampaignList.zul");
				}
			} catch (Exception e) {
				logger.error("** Exception : ",e);
			}
			
			
			
		}catch (Exception e) {
			logger.debug("**Exception while preparing the copy of the SMS Campaign",e);
		}
	}
	
	/**
	 * this method deletes the selected single SMS campaign
	 */
	
	/*public void onClick$dltMultipleCampLinkId() {
			deleteMultiple(smsCampListlbId);
	}*/
	
	
	/**
	 * this method deletes the multiple Selected SMS Campaigns 
	 * @param smsCampListlbId
	 */
	public void onClick$delSelectedId()  {
		
		int count = smsCampListlbId.getSelectedCount();
		if(logger.isDebugEnabled()) 
			logger.debug("Number of selected campaigns to delete :" + count);
		if(count == 0) {
			Messagebox.show("Select the SMS to delete.", "Information" , 
					Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		String msg = "Are you sure, you want to delete the selected campaign?";
		boolean found = false;
		Set<Listitem> selList = smsCampListlbId.getSelectedItems();
		SMSCampaigns smsCampaign;
		
		/****make a list of SMS Campaigns to be get deleted********/
		List<SMSCampaigns> campaignsList = new ArrayList<SMSCampaigns>();
		
		for (Listitem li : selList) {
			smsCampaign = (SMSCampaigns)li.getValue();
			campaignsList.add(smsCampaign);
			if(smsCampaign.getStatus().equals(Constants.CAMP_STATUS_ACTIVE) || 
					smsCampaign.getStatus().equals(Constants.CAMP_STATUS_RUNNING) ) {
				found = true;
			}
			
		}
		if(found) {
			msg = "Some of the selected campaigns are 'Active/Running', do you want to continue?";
		}
		try {
			int confirm=Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			 
			/******send the selected list for actual deletion ********/
			//String retFlag = deleteSmsCampaigns(campaignsList);
			
			if(confirm== Messagebox.OK){
			
			if(deleteSmsCampaigns(campaignsList).equals("success")) {
				
				MessageUtil.setMessage("Selected SMS campaigns have been deleted successfully. ", "color:green;", "top");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
				
			}//if
			
          }//confirm if
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		/*String retFlag = deleteSmsCampaigns(campaignsList);
		if(retFlag.equalsIgnoreCase("success")){
			MessageUtil.setMessage("Selected SMS campaigns are deleted successfully. ", "color:green;", "top");
		}*/
		
		/*for (Listitem li : selList) {
			li.setVisible(false);
		}*/
		smsCampListlbId.clearSelection();
	}
	
	
	 private Listbox pageSizeLbId;
	 public void onSelect$pageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(smsCampListlbId.getItemCount() == 0 ) {
					
					logger.debug("No SMS Campaigns found for this user...");
					return;
				}
				changeRows(pageSizeLbId.getSelectedItem().getLabel(),campListPaging);
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
		 
		 
		 
	 }
	
	
	 public void changeRows(String selStr, Paging campListPaging) throws Exception {
			try {
				if(campListPaging!=null){
					int pNo = Integer.parseInt(selStr);
					campListPaging.setPageSize(pNo);
					campListPaging1.setPageSize(pNo);
					redraw(0,pNo);
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
		}
/*	
	public void deleteMultiple(Listbox smsCampListlbId) {
			try {
			
			int count = smsCampListlbId.getSelectedCount();
			if(logger.isDebugEnabled()) 
				logger.debug("Number of selected campaigns to delete :" + count);
			if(count == 0) {
				Messagebox.show("Select emails to delete.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			String msg = "Are you sure, you want to delete the selected emails?";
			boolean found = false;
			Set<Listitem> selList = smsCampListlbId.getSelectedItems();
			SMSCampaigns smsCampaign;
			
			*//****make a list of SMS Campaigns to be get deleted********//*
			List<SMSCampaigns> campaignsList = new ArrayList<SMSCampaigns>();
			
			for (Listitem li : selList) {
				smsCampaign = (SMSCampaigns)li.getValue();
				campaignsList.add(smsCampaign);
				if(smsCampaign.getStatus().equals(Constants.CAMP_STATUS_ACTIVE) || 
						smsCampaign.getStatus().equals(Constants.CAMP_STATUS_RUNNING) ) {
					found = true;
				}
				
			}
			if(found) {
				msg = "Some of the selected campaigns are 'Active/Running', do you want to continue?";
			}
			
			if(Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)
					== Messagebox.CANCEL) {
				return;
			}
			*//******send the selected list for actual deletion ********//*
			//String retFlag = deleteSmsCampaigns(campaignsList);
			if(deleteSmsCampaigns(campaignsList).equals("success")) {
				
				MessageUtil.setMessage("Selected SMS campaigns have been deleted successfully. ", "color:green;", "top");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
				
			}//if
			
			String retFlag = deleteSmsCampaigns(campaignsList);
			if(retFlag.equalsIgnoreCase("success")){
				MessageUtil.setMessage("Selected SMS campaigns are deleted successfully. ", "color:green;", "top");
			}
			
			for (Listitem li : selList) {
				li.setVisible(false);
			}
			smsCampListlbId.clearSelection();
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
		}
	}*/
	
	/**
	 * this method deletes the selected SMS campaign(s) from DB
	 * @param smsCampList
	 * @return String var to specify the success or failure of deletion
	 */
	public String deleteSmsCampaigns(List<SMSCampaigns> smsCampList){
		logger.debug("----just entered for deletion----");
		try{
			String smsCampaignIds = "";
			Map<String,String> smsCampMap = new HashMap<String, String>();
			for (SMSCampaigns smsCampaign : smsCampList) {
				smsCampaignIds = smsCampaignIds + (smsCampaignIds.length() > 0 ? "," : "")+smsCampaign.getSmsCampaignId();
				smsCampMap.put(""+smsCampaign.getSmsCampaignId(), smsCampaign.getSmsCampaignName());
				
			}
			
			//TODO need to check whether this campaign is associated with any other program/Event trigger...
			
			AutoProgramComponentsDao autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
			EventTriggerDao eventTriggerDao = (EventTriggerDao)SpringUtil.getBean("eventTriggerDao");
			
			
			List<String> progconfiguredIds = autoProgramComponentsDao.findConfiguredSMSComponents(smsCampaignIds);
			List<String> eventTriggerConfiguredIds = eventTriggerDao.findConfiguredSMSTriggers(smsCampaignIds); 
			
			String notDelList = "";
			for (String smsCampId : progconfiguredIds) {
				
				notDelList +=  notDelList.equals("")?smsCampMap.get(smsCampId):","+smsCampMap.get(smsCampId);
				
			}//for
			
			for(String etCampId : eventTriggerConfiguredIds) {
				
				if(notDelList.equals("")) notDelList += smsCampMap.get(etCampId);
				else if(!notDelList.equals("") && !notDelList.contains(smsCampMap.get(etCampId))) {
					notDelList += ","+smsCampMap.get(etCampId);
					
				}//else if
				
			}//for
			
			
			
			String idsArray[] = smsCampaignIds.split(",");
			String delIdStr = "";//will be hold the campaigns that to be deleted
			String delCampIdStr = "";
			for (String id : idsArray) {
				if(!progconfiguredIds.contains(id) && !eventTriggerConfiguredIds.contains(id)){
					delIdStr += delIdStr.equals("")?id: "," + id; 
				}
			} //for
			if(delIdStr.length() > 0 && notDelList.length() > 0) {
				//means the selected campaigns have configured to some programs or event triggers....
				try {
					int confirm = Messagebox.show("The following SMS campaigns are configured to Programs / Event Triggers and they cannot be " +
							"deleted: " + notDelList + ". Do you want to continue deleting others?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					if(confirm != 1){
						return "problem";
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				
				
			}//if
			
			
			if(delIdStr.length()==0 && notDelList.length()>0) {
				MessageUtil.setMessage("The following SMS campaigns are configured to the Program/Event Trigger, " +
						"and they cannot be deleted.  [ " + notDelList + " ]", "color:red;", "TOP");
				return "problem";
			} //if
			
			
			if(delIdStr.length()>0) {
				
				//TODO need to delete the ids of notDelList from delIdStr
				
				logger.debug("-----about to delete the campaign ids=====>"+delIdStr);
				
				
				//smsCampaignScheduleDao.deleteByCampaignId(delIdStr);
				smsCampaignScheduleDaoForDML.deleteByCampaignId(delIdStr);
				//smsCampaignsDao.deleteBySmsCampaignId(delIdStr);
				smsCampaignsDaoForDML.deleteBySmsCampaignId(delIdStr);
				//smsCampaignsDao.deleteByCampaignIdFromIntermediateTable(delIdStr);
				smsCampaignsDaoForDML.deleteByCampaignIdFromIntermediateTable(delIdStr);
				
				
			} //if
			
			if(notDelList.length()>0){
				MessageUtil.setMessage("Some SMS campaigns are deleted. " +
						"[ " + notDelList + " ] SMS campaigns configured to the Programs/Event Triggers have not been deleted."
						, "color:maroon;", "TOP");
			}/*else{
				MessageUtil.setMessage("Selected campaigns are deleted.", "color:blue;", "TOP");
			}*/
			
			
			if(logger.isDebugEnabled()) logger.debug("SMS Campaigns ids to delete the schedule Objs : " + smsCampaignIds);
			/*campaignScheduleDao.deleteByCampaignId(campaignIds);
			campaignsDao.deleteByCollection(campaignsList);*/
			if(logger.isDebugEnabled()) logger.debug("-- Exit -- ");
			return "success";
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("** Exception while deleting the SMS campains ",e);
			return "problem";
		}
	}
	
	
	
	
	/*private class MyRendererListener implements EventListener,ListitemRenderer {
		
		public MyRendererListener(){
			super();
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			logger.debug("------just entered---in onEvent");
			try{
			Object obj = event.getTarget();
			SMSCampaigns smsCampaign ;
			int index = 0;
			if(obj instanceof Image){
				Image img = (Image)obj;
				String imgAttr = (String)img.getAttribute("addEvent");
				
				Listitem li = (Listitem)img.getParent().getParent().getParent();
				smsCampListlbId = (Listbox)li.getParent();
				index = li.getIndex();
				smsCampaign = (SMSCampaigns)li.getValue();
				if(imgAttr.equalsIgnoreCase("sendTestSMS")){
					if(smsCampaign == null){
						logger.error("** Exception : SMS Campaign object is null when send test sms link is clicked**");
						return;
					}
					testSMSWinId.doModal();
				}
				else if(imgAttr.equalsIgnoreCase("sendAgain")){
					Set<MailingList> mailingLists = smsCampaign.getMailingLists();
					String ml_ids_str = "";
					String appName = PropertyUtil.getPropertyValueFromDB("ApplicationName");
					
					try {
						int confirm = Messagebox.show("Are you sure you want to send the campaign now?",
								appName, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						
						if(confirm == 1){
							
							for(MailingList mailingList:mailingLists) {
								
								if(ml_ids_str.length() > 0) ml_ids_str+=",";
								ml_ids_str += mailingList.getListId();
							}
							SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule();
							//smsCampSchedule.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
							smsCampSchedule.setSmsCampaignId(smsCampaign.getSmsCampaignId());
							smsCampSchedule.setScheduledDate(Calendar.getInstance());
							smsCampSchedule.setStatus((byte)0);
							smsCampaignScheduleDao.saveOrUpdate(smsCampSchedule);
							Messagebox.show("SMS will be sent in a moment.", "Information", Messagebox.OK, Messagebox.INFORMATION);
							logger.debug("send now is submitted for the SMS campaign"+smsCampaign.getSmsCampaignName());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
					
					
					
					
				}else if(imgAttr.equalsIgnoreCase("Edit")){
					session.setAttribute("smsCampaign", smsCampaign);
					session.setAttribute("fromPage", "campaign/ViewSMSCampaign");
					String draftStatus = smsCampaign.getDraftStatus();
					if(draftStatus.equalsIgnoreCase(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE)){
						session.setAttribute("editSmsCampaign", "edit");
						Redirect.goTo("sms/SmsCampSettings");
					}
					else{
						session.setAttribute("editSmsCampaign", "view");
						session.setAttribute("smsDraftStatus", draftStatus);
						Redirect.goTo("sms/SmsCampSettings");
					}
					
					
				}else if(imgAttr.equalsIgnoreCase("Copy")){
					
				}else if(imgAttr.equalsIgnoreCase("Delete")){
					
					List<SMSCampaigns> clist = new ArrayList();
					String res = "";
					String msg = "Are you sure you want to delete selected campaign?";
					String status = smsCampaign.getStatus();
					if(status.equalsIgnoreCase(Constants.CAMP_STATUS_ACTIVE) || 
							status.equalsIgnoreCase(Constants.CAMP_STATUS_RUNNING)){
						msg = "Are you sure do you want to delete the '"+status.toLowerCase()+"' SMS campaign?";
					}
					try {
						int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm == 1){
							clist.add(smsCampaign);
							res = deleteSmsCampaigns(clist);
							if(res.equals("success")){
								MessageUtil.setMessage("Selected campaign deleted successfully.","color:blue","TOP");
								smsCampListlbId.removeItemAt(index);
							}
							if(res.equals("prolem")){
								MessageUtil.setMessage("Problem experienced while deleting the campaign. Try again later.","color:red","TOP");
							}
						}//if
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
				}//else if
				else if(imgAttr.equalsIgnoreCase("Report")) {
					try {
						 
						smsCampaign =  (SMSCampaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
						PageUtil.clearHeader();
						MessageUtil.clearMessage();
						Long userId = GetUser.getUserId();
						long reportCount = smsCampaignReportDao.getReportCountByCampaign(smsCampaign.getSmsCampaignName(), userId);
						if(reportCount < 1){
							MessageUtil.setMessage("No records exist for the email : "+smsCampaign.getSmsCampaignName(),"color:red", "TOP");
						}else{
							sessionScope.put("smsCampaign",smsCampaign);
							sessionScope.put("smsCampreport","true");
							Redirect.goTo("campaign/SMSCampReports");
						}
					} catch (Exception e) {
					    logger.error("** Exception **" + e);
					}
					
					
				}//else if
			}//if
			else if (event.getTarget() instanceof Popup) {
				try {
					OpenEvent openEvent = (OpenEvent)event;
					Image img = (Image)openEvent.getReference();
					logger.info(img.getParent().getParent().getParent());
					logger.info(((Listitem)img.getParent().getParent().getParent()).getValue());
					smsCampaign =  (SMSCampaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
					 copySMSCampaignPopupId.setAttribute("smsCampaign",smsCampaign);
					copySmsCampaignNameTbId.setValue("Copy of " + smsCampaign.getSmsCampaignName());
				} catch (Exception e) {
				}
			}
			else if(event.getTarget() instanceof Paging) {
				
				Paging paging = (Paging)event.getTarget();
				
				int desiredPage = paging.getActivePage();
				
				if(paging.getAttribute("onPaging").equals("topPaging")) {
					
					this.campListPaging1.setActivePage(desiredPage);
					
				}else if(paging.getAttribute("onPaging").equals("bottomPaging")) {
					
					this.campListPaging.setActivePage(desiredPage);
					
				}//else if
				
				
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
				
				
			}//else if
			}catch (Exception e) {
				logger.error("** Exception ",e);
			}
		}//onevent()
		
		@Override
		public void render(Listitem item, Object obj,int arg2) throws Exception {
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			
			if(obj instanceof SMSCampaigns){
				SMSCampaigns smsCampaign = (SMSCampaigns)obj;
				item.setValue(smsCampaign);
				item.setHeight("30px");
				
				Listcell lc = new Listcell();
				lc.setParent(item);
				
				lc = new Listcell(smsCampaign.getSmsCampaignName());
				lc.setParent(item);
				
				lc = new Listcell(smsCampaign.getListNames());
				lc.setParent(item);
				logger.info("calendar : "+ smsCampaign.getCreatedDate());
				lc = new Listcell(MyCalendar.calendarToString(smsCampaign.getCreatedDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				lc.setParent(item);
				
				lc = new Listcell(smsCampaign.getStatus());
				lc.setParent(item);
				
				lc = new Listcell();
				
				Hbox hbox = new Hbox();
				hbox.setAlign("center");
				hbox.setStyle("cursor:pointer;margin-right:5px;");
				
				Image img = new Image();
				img.setSrc("/img/email_page.gif");
				img.setTooltiptext("Send Test SMS");
				img.setStyle("cursor:pointer;margin-right:5px;");
				img.setAttribute("addEvent", "sendTestSMS");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/email_go.gif");
				img.setTooltiptext("Send Again");
				img.setStyle("cursor:pointer;margin-right:5px;");
				img.setAttribute("addEvent", "sendAgain");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/email_edit.gif");
				img.setTooltiptext("Edit");
				img.setStyle("cursor:pointer;margin-right:3px;");
				img.setAttribute("addEvent", "Edit");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/copy.gif");
				img.setTooltiptext("Copy");
				img.setStyle("cursor:pointer;");
				img.setPopup(copySMSCampaignPopupId);
				img.setAttribute("addEvent", "Copy");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/action_delete.gif");
				img.setTooltiptext("Delete");
				img.setStyle("cursor:pointer;");
				img.setAttribute("addEvent", "Delete");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image();
				img.setSrc("/img/theme/home/reports_icon.png");
				img.setTooltiptext("Reports");
				img.setStyle("cursor:pointer;");
				img.setAttribute("addEvent", "Report");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				hbox.setParent(lc);
				lc.setParent(item);
				
			}//if
			
			copySMSCampaignPopupId.addEventListener("onOpen", this);	
		}//render
	
	}//MyRendererListener
*/	
	
	public void onClick$cancelsendTestSmsBtnId$testSMSWinId() {
		
		logger.info("-----just entered-----to cancel");
		((Label)testSMSWinId.getFellow("msgLblId")).setValue("");
		 ((Textbox)testSMSWinId.getFellow("mblNumTxtBoxId")).setValue("");
		testSMSWinId.setVisible(false);
		
	}
	
	private boolean IsUsingExpKeyword(String messageContent) {
		
		boolean expStatus = false;
		List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgEXPSMSKeyWords(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		

		if(keywordsList != null) {
			boolean isFound = false;
			String pendingKeywords = Constants.STRING_NILL;
			String expKeywords = Constants.STRING_NILL;
			for (OrgSMSkeywords OrgSMSkeyword : keywordsList) {
				isFound = false;
				String shortCode = OrgSMSkeyword.getShortCode();
				boolean isPlus = (shortCode.length() > 10);
				String pattern = "\\b"+OrgSMSkeyword.getKeyword()+"\\b\\s+\\bto\\b\\s+"+(isPlus ? "[^\\s\\w]*" : "" )+"\\b"+OrgSMSkeyword.getShortCode();//OrgSMSkeyword.getKeyword()+" to "+(isPlus ? "+" : "" )+OrgSMSkeyword.getShortCode();
				int options = 0;
				options |= 128; 	//This option is for Case insensitive
				options |= 32;
				Pattern p = Pattern.compile(pattern, options);
				Matcher m = p.matcher(messageContent);
				
				while (m.find()) {
					
					isFound = true;
				}
				
				if(isFound) {
					if(Constants.KEYWORD_STATUS_PENDING.equalsIgnoreCase(OrgSMSkeyword.getStatus())) {
						
						if(!pendingKeywords.isEmpty()) pendingKeywords += Constants.DELIMETER_COMMA+" ";
						
						pendingKeywords += "'"+OrgSMSkeyword.getKeyword()+"'";
						
					}else if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(OrgSMSkeyword.getStatus())) {
						if(!expKeywords.isEmpty()) expKeywords += Constants.DELIMETER_COMMA+" ";
						
						expKeywords += "'"+OrgSMSkeyword.getKeyword()+"'";
					}
				}
				
			}//for
			if(!pendingKeywords.isEmpty() || !expKeywords.isEmpty()) {
				
				String Mesg =  "Keyword(s) "+ (!pendingKeywords.isEmpty() ? pendingKeywords : (!expKeywords.isEmpty() ? expKeywords : "")) + 
				" used in SMS Campaign is not Active, SMS Campaign can not be sent";
				MessageUtil.setMessage(Mesg,"color:red", "TOP");
				expStatus = true;
				
			}//if
			
		}
		
		/*for(OrgSMSkeywords keyword : keywordsList) {
			
			
			String expKeword = keyword.getKeyword() + " to " +keyword.getShortCode();
			if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(keyword.getStatus())) {
				if(messageContent.contains(expKeword)){
					MessageUtil.setMessage("Keyword "+ keyword.getKeyword() + " used in SMS Campaign has Expired, SMS Campaign can not be sent","color:red", "TOP");
					expStatus = true;
				}
			}
			
			if(Constants.KEYWORD_STATUS_PENDING.equalsIgnoreCase(keyword.getStatus())) {
				if(messageContent.contains(expKeword)){
					MessageUtil.setMessage("Keyword "+ keyword.getKeyword() + " used in SMS Campaign is not Active, SMS Campaign can not be sent","color:red", "TOP");
					expStatus = true;
				}
			}
		}*/
		
		// TODO Auto-generated method stub
		return expStatus;
	}
	/*public void onOpen$copySMSCampaignPopupId(){
		
				if(smsCampListlbId.getSelectedItem() != null) {
		            SMSCampaigns smsCampaign =  (SMSCampaigns)smsCampListlbId.getSelectedItem().getValue();
		            copySMSCampaignPopupId.setAttribute("smsCampaign",smsCampaign);
		            copySmsCampaignNameTbId.setValue("Copy of " + smsCampaign.getSmsCampaignName());
	            //Messagebox.show(campaign.getCampaignName());
				}   
	     
	}*/
	
	public boolean invalidPromoCodes(SMSCampaigns smsCampaign) {
		boolean isValid = false;
		
		String messageContent = smsCampaign.getMessageContent();

						Set<String> couponPhSet = findCoupPlaceholders(messageContent.trim());
						logger.debug("couponPhSet :"+couponPhSet);
						String couponIdStr = "";
						for(String ph : couponPhSet){
							
							if(ph.startsWith("CC_")){
								
								String[] phStr = ph.split("_");
								if(phStr.length > 2){
									
									MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
									return true;
								}
								
								couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
								Long couponId = null;
								try{
								couponId = Long.parseLong(phStr[1]);
								}catch(Exception e){
									MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
									return true;
								}
								
								if(couponId != null){
									
									CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
									Long orgId = GetUser.getUserObj().getUserOrganization().getUserOrgId();
									
									List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
									if(couponsList == null){
										MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
										return true;
									}
								}
							}
							
						}




						if(!couponIdStr.trim().isEmpty()){
							logger.debug("couponIdStr :"+couponIdStr);
							List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, GetUser.getUserObj().getUserOrganization().getUserOrgId());
							
							if(inValidCoupList != null){
								
								String inValidCoupNames = "";
								if(inValidCoupList != null && inValidCoupList.size() >0) {
									
									for (Coupons coupons : inValidCoupList) {
										inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
									}
									MessageUtil.setMessage(	"The discount code "+inValidCoupNames+" used in this campaign has either expired or in paused status. " +
											" \n Please change the status of this discount codes.",
											"color:red", "TOP");
									return true;
								}
								
							}
							
						}


		return isValid;
	}
	
	public static Set<String> findCoupPlaceholders(String content) {
		//logger.debug("entered... findcouponplaceholder..");
		content = content.replace("|^", "[").replace("^|", "]");
		String cfpattern = "\\[([^\\[]*?)\\]";
		
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);
		//logger.debug("after matcher....");
		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
				
				ph = m.group(1); //.toUpperCase()
				//logger.info("Ph holder :" + ph);

				
				 if(ph.startsWith("CC_")) {
					 totalPhSet.add(ph);
				 }
			}
				
		}catch (Exception e) {
			// TODO: handle exception
			return totalPhSet;
		}
		
		return totalPhSet;
	}
	
	
	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_NAME)) {
			searchByCmpgnNameDivId.setVisible(true);
			searchByCmpgnNameTbId.setText(Constants.STRING_NILL);
			searchByCmpgnNameTbId.setFocus(true);
			
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(false);
			smsCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByCmpgnStatusDivId.setVisible(false);
			smsCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_STATUS)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(true);
			smsCampStatusLb.setSelectedIndex(0);
			return;
		}
		
	}//onSelect$srchLbId()
	
	public void onClick$filterBtnId() {
		smsActionsBandBoxId.setDisabled(true);
    	int pSize = 0;
    	
    	String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
   	 	     boolean status = validateSetCampaignName();
   	 	     if(status == false) return;
   	 	     pSize = smsCampaignsDao.getCountByCampaignName(searchByCmpgnNameTbId.getValue().trim(), currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----name----list size="+totalSize);
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
			 boolean status = validateSetCreationDate();
			 if(status == false) return;
			 pSize = smsCampaignsDao.getCountByCreationDate(fromDateStr, toDateStr, currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----Creation Date----list size="+pSize);
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
    		  pSize = smsCampaignsDao.getCount(currentUser.getUserId(), smsCampStatusLb.getSelectedItem().getLabel());
			
			 logger.info("----onClick$submintBtnId()----Status is----list size="+pSize);
	    }
    	
        logger.info("----filter button pressed and totalSize = "+totalSize);
        
		setSizeOfPageAndCallRedraw(pSize);
       
	}//onClick$filterBtnId()
	
	
    public String orderby_colName="modifiedDate",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
    public void onClick$sortbyCampaigName() {
    			//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    			orderby_colName = "smsCampaignName";
    			desc2ascasc2desc();	
    			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    }
    public void onClick$sortbyCreadteDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "createdDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
    public void onClick$sortbyModifiedDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "modifiedDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
	
	
	private boolean validateSetCampaignName() {
		if(searchByCmpgnNameTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter Campaign Name.",
					"color:red", "TOP");
			searchByCmpgnNameTbId.setFocus(true);
			return false;
		}
		return true;
	}//validateSetCampaignName()
	
    private boolean validateSetCreationDate() {
		
		if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null ){
			MessageUtil.setMessage("Please enter the required dates.",
					"color:red", "TOP");
			
			
			return false;
		}
		
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		logger.debug("client From :" + tempClientFromCal + ", client To :"
				+ tempClientToCal);

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate
		serverFromDateCal.set(
				Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(
				Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE)
						- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(
				Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE)
						- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red", "TOP");
			return false;
		}
		
		fromDateStr = serverFromDateCal.toString();
		toDateStr = serverToDateCal.toString();
		
		
		return true;
		
	}//validateSetCreationDate()
    
     private void setSizeOfPageAndCallRedraw(int pSize) {
    	
    	logger.debug("page size is    "+pSize);
    	campListPaging.setTotalSize(pSize);
	    //campListPaging.setAttribute("onPaging", "topPaging");
		//campListPaging.addEventListener("onPaging", this); 
		 
		campListPaging.setActivePage(0);
		campListPaging1.setActivePage(0);
		campListPaging1.setTotalSize(pSize);
		//campListPaging1.setAttribute("onPaging", "bottomPaging");
		//campListPaging1.addEventListener("onPaging", this);

		redraw(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		//copyEmailPopupId.addEventListener("onOpen", this);	
    }
     
     public void onClick$resetAnchId() {
 		
     	int pSize;
     	smsActionsBandBoxId.setDisabled(true);
     	srchLbId.setSelectedIndex(0);
     	smsCampStatusLb.setSelectedIndex(0);
    
     	
     	searchByCmpgnNameDivId.setVisible(false);
 		searchByCmpgnCreationDateDivId.setVisible(false);
 		searchByCmpgnStatusDivId.setVisible(true);
     	
 		pSize = smsCampaignsDao.getCount(currentUser.getUserId(), "All");
 		logger.info("----onClick$resetAnchId()----Status is----list size="+totalSize);
 		
 		orderby_colName="modifiedDate";
		desc_Asc="desc";
		pageSizeLbId.setSelectedIndex(1);
 		setSizeOfPageAndCallRedraw(pSize);
     	
 		
 	}
}//class
