package org.mq.marketer.campaign.controller.campaign;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.SupportTicket;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.controller.SupportController;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
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

@SuppressWarnings({ "unchecked", "serial" })
public class ViewCampaign extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Listbox campListlbId;
	private Listbox campStatusLb;
	private Listbox srchLbId;
	private Session sessionScope;
	private Window testMailWinId;
	private Popup copyEmailPopupId;
	private Textbox copyEmailNameTbId;
	private Paging campListPaging,campListPaging1;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private CampaignScheduleDao campaignScheduleDao;
	private UserActivitiesDao   userActivitiesDao;
	private UserActivitiesDaoForDML   userActivitiesDaoForDML;

	
	//these fields are added for different search criteria for filtering out email-campaigns-----starts
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
	//----ends
	
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private Users currentUser;
	private int itemsSize,totalSize;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private String beeJsonContent;
	
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	//private ListitemRenderer renderer = new MyRenderer();
	
	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	
	private static  String name;
	private static String status;
	
	private Window previewWinCampaign;
	private Iframe previewWinCampaign$html;
	
	public ViewCampaign() {
		
	  
	  String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	  PageUtil.setHeader("Email Campaigns","",style,true);
	  
	  if(logger.isDebugEnabled())logger.debug("entered ViewCampaigns");
	  sessionScope = Sessions.getCurrent();
	  //campStatusLb = (Listbox)Executions.getCurrent().getAttribute("campStatusLb");
	  campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
	  campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
	  campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
	  sessionScope.removeAttribute("campaign");
	  sessionScope.removeAttribute("editCampaign");
	  currentUser = GetUser.getUserObj();
	  if(logger.isDebugEnabled())logger.debug("exited ViewCampaigns");
	  
	  	Div navigationDivId =(Div)Utility.getComponentById("navigationDivId");
		navigationDivId.setVisible(false);
	  
	  UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
	  UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	  /*if(userActivitiesDao != null) {
      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_CAMPAIGNS_LIST,GetUser.getUserObj());
	  }*/
	  if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_CAMPAIGNS_LIST,GetUser.getLoginUserObj());
		  }
	  userCampaignCategoriesDao  =(UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
	  
	}
	
	//public void init(Listbox campListlbId) {
	 public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		 super.doAfterCompose(comp);
		 
		 logger.debug("<<<<<<<< campListid" + campListlbId);
		 
		 itemsSize=0;
		 totalSize = 0;
		 int pSize;
		 
		 totalSize = campaignsDao.getCount(currentUser.getUserId(), "All");
		 
		 
		 
		 
		 logger.info("----doAfter....called and currentUser.getUserId() = "+currentUser.getUserId());
		 logger.info("----doAfter....called and totalSize = "+totalSize);
		 /*try
		 {
			 if(campStatusLb.getSelectedItem().getLabel().equalsIgnoreCase("All"))
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
*/
		 pSize=totalSize;
		 
		 campListPaging.addEventListener("onPaging", this); 
		 campListPaging1.addEventListener("onPaging", this); 
		 campListPaging.setAttribute("onPaging", "topPaging");
		 campListPaging1.setAttribute("onPaging", "bottomPaging");
		 /*campListPaging.setTotalSize(pSize);
		 campListPaging.addEventListener("onPaging", this); 
		 
		 campListPaging.setActivePage(0);
		 campListPaging1.setActivePage(0);
		 campListPaging1.setTotalSize(pSize);
		 campListPaging1.addEventListener("onPaging", this);

		 redraw(0, campListPaging.getPageSize());
		 copyEmailPopupId.addEventListener("onOpen", this);	*/
		 copyEmailPopupId.addEventListener("onOpen", this);
		 setSizeOfPageAndCallRedraw(pSize);
	 }
	 public int getStatusCount()
		{
		 	int itemSize= 0;
			try {
				String status = getSelectedStatus();
				List<Campaigns> campList  =  campaignsDao.getCampaignsByStatus(currentUser.getUserId(), 0, totalSize,orderby_colName,desc_Asc);
				 
				 logger.debug("total size is  "+totalSize);
				if(campList != null) {
					itemSize=campList.size();
					int otherItemCnt = 0;
					for (Campaigns campaigns : campList) {
						String campStatus = getCampaignStatus(campaigns);
						if(!status.equals("All")) {
						
							if(campStatus.equalsIgnoreCase(status) ){
								otherItemCnt++;
							}
													
						}//if
					 
					}
					
					logger.debug("itemsize based on camp     "+itemSize);
					if(otherItemCnt > 0) itemSize = otherItemCnt;
					logger.debug("item size after others   "+itemsSize);
				}//if
				

 
 
 
/* List<Campaigns> campList = getCampaigns(0, totalSize);
				
				 int itemSize=0;
				 //if(smsCampList == null) return ;
				 if(campList != null && campList.size() > 0) {
					 
					 for (Campaigns campaign : campList) {
						 
						 String status = campaign.getStatus();
						 
						 if(!getSelectedStatus().equalsIgnoreCase("Draft"))
						 {
							if(status != null && ! status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT)){
								
								status = getCampaignStatus(campaign);
								
								int index = campStatusLb.getSelectedIndex();
								
								if(!(index <= 0))
								if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
									itemSize ++; 
								}
							 
							}
						 }
						 else 
						 {
								if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
									itemSize ++; 
								}
						}
					 }
				 }
				 logger.info("status size is"+itemSize);*/
				 logger.info("status size is"+itemSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.debug("Excption ", e);
			}
			return itemSize;
		}
	 
	 public String getSelectedStatus() {
		 
		 
		 int index = campStatusLb.getSelectedIndex();
			
			String status = "All";
			if (index != -1)
				status = campStatusLb.getSelectedItem().getValue();
		 
		 
			return status;
			
	 }
	 private Listbox pageSizeLbId;
	 public void onSelect$pageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(campListlbId.getItemCount() == 0 ) {
					
					logger.debug("No Email Campaigns found for this user...");
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
	 
	 
	 public void redraw(int startIndex, int size) {
		 
		 try {
			MessageUtil.clearMessage();
			logger.debug("-- just entered redraw--");
			int count =  campListlbId.getItemCount();
			
			for(; count>0; count--) {
				campListlbId.removeItemAt(count-1);
			}
			
			//System.gc();
			 
			 List<Campaigns> campList = null;
			 List<Campaigns> campaignList = new ArrayList<Campaigns>();
			 
			 String value = srchLbId.getSelectedItem().getValue().toString();
			 List<Campaigns> finalList = new ArrayList<Campaigns>();
			if(value.equals(SEARCH_BY_NAME)) {
			
				name =searchByCmpgnNameTbId.getValue().trim();
				if(name!=null){
				campList =  campaignsDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				fromDateStr=null;toDateStr=null;	
				}
				/*else if(fromDateStr!=null && toDateStr!=null)
					campList =  campaignsDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);*/			
				else
					campList =  campaignsDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				for (Campaigns campaigns : campList) {
					String campStatus = getCampaignStatus(campaigns);
					campaigns.setStatus(campStatus);
					finalList.add(campaigns);
				}
				if(campList != null) finalList = campList;
			}
			
			else if(value.equals(SEARCH_BY_DATE)) {
				
				if(fromDateStr!=null && toDateStr!=null ){
				campList =  campaignsDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);			
				   name=null;
				}
				/*else if(name!=null)
					campList =  campaignsDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);*/
				else
				campList =  campaignsDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				
			
				for (Campaigns campaigns : campList) {
					String campStatus = getCampaignStatus(campaigns);
					campaigns.setStatus(campStatus);
					finalList.add(campaigns);
				}
				if(campList != null) finalList = campList;
			}
      
			else if(value.equals(SEARCH_BY_STATUS)) {
				String status = getSelectedStatus();
				if(!status.equals("All")) {
					campList =  campaignsDao.getCampaignsByStatus(currentUser.getUserId(), 0, totalSize,orderby_colName,desc_Asc);
					List<Campaigns> allSmsCampList=new ArrayList<Campaigns>();
					if(campList != null) {
						//finalList = campList;
							for (Campaigns campaigns : campList) {
								String campStatus = getCampaignStatus(campaigns);
								
								
									if(campStatus.equalsIgnoreCase(status) ){
										campaigns.setStatus(campStatus);
										allSmsCampList.add(campaigns);
									}
															
								
							 
							}
							if(allSmsCampList.size() > 0){
								 for(int i=startIndex;i<size+startIndex&&i<allSmsCampList.size();i++)
								 {
						//			logger.info("setting :LLLLLLLL"+allSmsCampList.get(i).getCategoryName());
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
				/*else if(fromDateStr!=null && toDateStr!=null){
					campList =  campaignsDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);			
				name=null;
				for (Campaigns campaigns : campList) {
					String campStatus = getCampaignStatus(campaigns);
					campaigns.setStatus(campStatus);
					finalList.add(campaigns);
				}
				}
				else if(name!=null){
					campList =  campaignsDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
					fromDateStr=null;toDateStr=null;	
					for (Campaigns campaigns : campList) {
						String campStatus = getCampaignStatus(campaigns);
						campaigns.setStatus(campStatus);
						finalList.add(campaigns);
					}
				}*/
				else{
				
					List<Campaigns> tempList = campaignsDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
					for (Campaigns campaigns : tempList) {
						String campStatus = getCampaignStatus(campaigns);
						campaigns.setStatus(campStatus);
						finalList.add(campaigns);
					}
				}
				
			}
			 
			 
			/* if(!getSelectedStatus().equalsIgnoreCase("All"))
			 {
				 //List<Campaigns> dsmsCampList = getCampaigns(0, totalSize);
				 
				 campList = new ArrayList<Campaigns>();
				 
				 List<Campaigns> allSmsCampList=new ArrayList<Campaigns>();
				 
				 for (int i=0;i<dsmsCampList.size();i++) {
					 
					 String status = dsmsCampList.get(i).getStatus();
					 
					 if(status != null && ! status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT)){
						 
							status = getCampaignStatus(dsmsCampList.get(i));
							int index = campStatusLb.getSelectedIndex();
							if(!(index <= 0))
								if(status.equalsIgnoreCase(getSelectedStatus()) ){
									allSmsCampList.add(dsmsCampList.get(i));
								}
					}
					else 
					{
							if(status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT))
								if(status.equalsIgnoreCase(getSelectedStatus()) ){
									allSmsCampList.add(dsmsCampList.get(i));
								}

					}
						
				 }
				 logger.info("size is::::"+size+"startindex"+startIndex);
				 for(int i=startIndex;i<size+startIndex&&i<allSmsCampList.size();i++)
				 {
					logger.info("setting :LLLLLLLL"+allSmsCampList.get(i).getCategoryName());
					 campList.add(allSmsCampList.get(i));
					
				 }
			 
			 }
			 else
			 {
				 campList = getCampaigns(startIndex, size);
			 }
			 */
			 
			 
			// if(campList == null) return;
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			 if(finalList != null && finalList.size() > 0) {
				 
				 Listitem li;
				 Listcell lc;
				 for (Campaigns campaign : finalList) {
					
					li = new Listitem();
					
					lc = new Listcell();
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(campaign.getCampaignName());
					lc.setStyle("padding-left:10px;");
					lc.setTooltiptext(campaign.getCampaignName());
					lc.setParent(li);
					
					
				
				//code for display of segment name and its respective list name
					String listNames = Constants.STRING_NILL;
					SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
					if(campaign.getListsType() != null && !campaign.getListsType().equalsIgnoreCase("Total")) {
						logger.info("the campaign is segmented......"+campaign.getListNames()+"(segmented)");
						String segRuleIds = campaign.getListsType().split(""+Constants.DELIMETER_COLON)[1];
						List<SegmentRules> segmentRules = segmentRulesDao.findById(segRuleIds);
						String segName = Constants.STRING_NILL;
						if(segmentRules !=null) {
							
							for(SegmentRules seg: segmentRules) {
								if(segmentRules!=null) segName +=  seg.getSegRuleName()+",";
								else segName="--";
						}
					}
					logger.info("segName==>"+segName);
							lc = new Listcell();
							lc.setTooltiptext(segName);
							lc.setLabel(segName);
							lc.setParent(li);
					 if(segmentRules!=null) listNames = ""+getListNames(campaign)+"";
					
							else listNames="--";
					} 
					else {
						lc = new Listcell();
						lc.setLabel("--");
						lc.setParent(li);
					}
					if (campaign.getListsType() == null || campaign.getListsType().equalsIgnoreCase("Total")) {
							listNames = getListNames(campaign);
						}
					lc=new Listcell();
					lc.setTooltiptext(listNames);
					lc.setLabel(listNames);
					lc.setParent(li);
					
				
					lc = new Listcell();
					//No need to check if the campaign status is in Draft ,y bcz after scheduling the campaign 
					// campaign status may changed to Draft
					String status = campaign.getStatus();
					if(status != null && ! status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT)){
						status = getCampaignStatus(campaign);
						
						 int index = campStatusLb.getSelectedIndex();
						 if(!(index <= 0))
						if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
							logger.info(getSelectedStatus());
							itemsSize ++; 
						}
						
					}
					if(campStatusLb.getSelectedItem() != null && 
							!campStatusLb.getSelectedItem().getLabel().equals("All") && !campStatusLb.getSelectedItem().getLabel().equals(status)) continue;
					
					lc.setLabel(status);
					lc.setParent(li);
					
					if(!status.equalsIgnoreCase("Active")) {
						campaign.setScheduledDates(null);
						campaign.setScheduledOccurrence(null);
					}
					
					lc = new Listcell();
					lc.setLabel(campaign.getScheduledOccurrence() != null ? campaign.getScheduledOccurrence() : "--");
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(campaign.getScheduledDates() != null ? campaign.getScheduledDates() : "--");
					lc.setParent(li);
					
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(campaign.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
//					logger.info("creation date: "+campaign.getCreatedDate()+"");
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(campaign.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(li);
					
					/*lc = new Listcell();
					//No need to check if the campaign status is in Draft ,y bcz after scheduling the campaign 
					// campaign status may changed to Draft
					String status = campaign.getStatus();
					if(status != null && ! status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT)){
						status = getCampaignStatus(campaign);
						
						 int index = campStatusLb.getSelectedIndex();
						 if(!(index <= 0))
						if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
							System.out.println(getSelectedStatus());
							itemsSize ++; 
						}
						
					}
					if(campStatusLb.getSelectedItem() != null && 
							!campStatusLb.getSelectedItem().getLabel().equals("All") && !campStatusLb.getSelectedItem().getLabel().equals(status)) continue;
					
					lc.setLabel(status);
					lc.setParent(li);*/
					
					lc = new Listcell();
					Hbox hbox = new Hbox();
					
						Image preview = new Image("img/theme/preview_icon.png");
						preview.setTooltiptext("Preview");
						preview.setStyle("cursor:pointer;margin-right:5px;");
						preview.addEventListener("onClick", this);
						preview.setAttribute("type", "previewPage");
						preview.setParent(hbox);
					
						Image img = new Image("/img/email_page.gif");
						img.setTooltiptext("Send Test Mail");
						img.setStyle("cursor:pointer;margin-right:5px;");
						img.addEventListener("onClick", this);
						img.setAttribute("type", "emailPage");
						img.setParent(hbox);
									
						Image editImg = new Image("/img/email_edit.gif");
						editImg.setTooltiptext("Edit");
						editImg.setStyle("cursor:pointer;margin-right:5px;");
						editImg.addEventListener("onClick", this);
						editImg.setAttribute("type", "emailEdit");
						editImg.setParent(hbox);
						
						Image copyImg = new Image("/img/copy.gif");
						copyImg.setTooltiptext("Copy");
						copyImg.setStyle("cursor:pointer;");
						//copyImg.addEventListener("onClick", this);
						copyImg.setPopup("copyEmailPopupId");
						copyImg.setAttribute("type", "emailCopy");
						copyImg.setParent(hbox);
						
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setTooltiptext("Delete");
						delImg.setStyle("cursor:pointer;");
						delImg.addEventListener("onClick", this);
						delImg.setAttribute("type", "emailDelete");
						delImg.setParent(hbox);
						
//						Image sendAgainImg = new Image("/img/email_go.gif");
//						sendAgainImg.setTooltiptext("Send Again");
//						sendAgainImg.setStyle("cursor:pointer;margin-right:5px;");
//						sendAgainImg.addEventListener("onClick", this);
//						sendAgainImg.setAttribute("type", "emailGo");
//						sendAgainImg.setParent(hbox);
						
						Image reportImg = new Image("/img/theme/home/reports_icon.png");
						reportImg.setTooltiptext("Report");
						reportImg.setStyle("cursor:pointer;");
						reportImg.addEventListener("onClick", this);
						reportImg.setAttribute("type", "emailReport");
						reportImg.setParent(hbox);
						
						
					hbox.setParent(lc);	
					lc.setStyle("float: left;padding-left: 9px;");
					lc.setParent(li);
					
					li.setHeight("30px");
					li.setParent(campListlbId);
					li.setValue(campaign);
				}
			 }
			 logger.info("---after making all rows -------inside redraw()");
			 /*int index = campStatusLb.getSelectedIndex();
			 if(!(index <= 0)){
				 
				 
				 if( campListPaging.getPageSize() == itemsSize ){
					 campListPaging.setPageSize(itemsSize);
					 campListPaging1.setPageSize(itemsSize);
					 }
				 else{
					 if(index == 1 || index == 2){
						 campListPaging.setTotalSize(itemsSize);
						 campListPaging1.setTotalSize(itemsSize);
						 itemsSize=0;
					 }
					 campListPaging.setPageSize(campListPaging.getPageSize());
					 campListPaging1.setPageSize(campListPaging.getPageSize());
					 
				 }
				 
			 }*/
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("WrongValueException ", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	 }//redraw

	 public String getCampaignStatus(Campaigns campaign) {
		 
		String status = campaign.getStatus();
		
		CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
		
		List<CampaignSchedule> scheduleList = campaignScheduleDao.findAllByCampaignId(campaign.getCampaignId(), campaign.getUsers().getUserId());
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		
		byte activeCount = 0;
		byte sentCount = 0;
		byte subscriptionCount = 0;
		byte draftCount = 0;
		
		
		if(scheduleList == null || scheduleList.size() == 0) {
		//	logger.info("==scheduleList size==" + scheduleList.size());
		//	if(status.equalsIgnoreCase(Constants.CAMP_STATUS_DRAFT)){
				/*
				 campaign.setScheduledOccurrence("--");
				 campaign.setScheduledDates("--");
		//	}
*/			return status;
		}
		
		//boolean draftStatus = campaign.getStatus().equalsIgnoreCase("Draft") ;
		//logger.info("scheduleList.size()="+scheduleList.size()+"   scheduleList="+scheduleList);
		Calendar startCal =null;
		Calendar endCal = null;
		
		
		//logger.info("Schedule List status###" + ((CampaignSchedule)(scheduleList.get(0))).getStatus());
		CampaignSchedule latestCampaignSchedule = null;
		
		for (CampaignSchedule campaignSchedule : scheduleList) {
			if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;
			
			if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
				latestCampaignSchedule = campaignSchedule;
			}
	
			if(campaignSchedule.getStatus() != 0 && campaignSchedule.getStatus() != 3) continue;
			
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
			
			
			
			/*if(campaignSchedule.getStatus() == 0 && startCal== null && endCal== null){
				
				startCal = campaignSchedule.getScheduledDate();
				endCal = campaignSchedule.getScheduledDate();
				System.out.println("in date ----"+startCal+endCal);
			}
			
			if(campaignSchedule.getStatus() == 0){
			    if(startCal != null && startCal.after(campaignSchedule.getScheduledDate())){
			    	startCal = campaignSchedule.getScheduledDate();
			    }
			    if(endCal != null && endCal.before(campaignSchedule.getScheduledDate())){
			    	endCal = campaignSchedule.getScheduledDate();
			    }
			    System.out.println("in date 2 ----"+startCal+endCal);
			}
				//if campSchedule status is sent no need to do anything
				if(campaignSchedule.getStatus() == 1) {
					sentCount++;
					continue;
				}
				
				//if submit and campSchedule status is draft changing it to active
				if(campaignSchedule.getStatus() == 0 ) {
					activeCount++;
				}
				if(campaignSchedule.getStatus() == 2 ) {
					draftCount++;
				}
			 
				if(campaignSchedule.getStatus() == 3 ) {
					subscriptionCount++;
				}
		*/	 
		}//for
		 
		//TODO need to finalize all the status in combination
		/*if(activeCount >0 && sentCount > 0){
			
			status = Constants.CAMP_STATUS_RUNNING;
		}
		else if(activeCount == 0 && sentCount > 0 && draftCount==0) {
			status = Constants.CAMP_STATUS_SENT;
		}else if((activeCount >0 && sentCount == 0) || (activeCount >0 && sentCount > 0)){

			status = Constants.CAMP_STATUS_ACTIVE ;
		}else if(sentCount > 0 && subscriptionCount > 0) {
			
			status = Constants.CAMP_STATUS_SUSPEND;
		}else if(draftCount >0 ) {
			status = Constants.CAMP_STATUS_DRAFT;
		}*/
		 
		String scheduledOnOrBetweenDates = null;
		
		if(startCal != null && endCal !=null){
			if(!startCal.equals(endCal)){
		
			//logger.info("Active count==>>"+activeCount + "Sent count==>>"+sentCount + "Start Calender"+startCal + "End Date" + endCal);
			campaign.setScheduledOccurrence("Recurring");
			scheduledOnOrBetweenDates = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_STDATE, clientTimeZone)+" - "+
					MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_STDATE, clientTimeZone);
			campaign.setScheduledDates(scheduledOnOrBetweenDates);
			
		 }else{
			 campaign.setScheduledOccurrence("One-Time");
					campaign.setScheduledDates(MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
		 }/*else{
			 System.out.println("---3----");
			 campaign.setScheduledOccurrence("--");
			 campaign.setScheduledDates("--");
			 logger.info("Campaign Name==="+campaign.getCampaignName());
		 }*/
		}
		// campaign.setStatus(status);
		//logger.info("===latestCampaignSchedule=="+latestCampaignSchedule.getStatusStr());
		
		
		if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
			return latestCampaignSchedule.getStatusStr();
		else if(latestCampaignSchedule.getStatus() == 3)
	        return "Paused";
		else if(latestCampaignSchedule.getStatus() > 3)
			return "Schedule Failure";
		else
			return "Draft";
		
	 }
	 
	 
	 
	public List<Campaigns> getCampaigns(List<Campaigns> prepareList){
		
		try {
			/*logger.debug("-- Just Entered -- ");
			logger.info("-- Just Entered getCampaigns()--");
			int index = campStatusLb.getSelectedIndex();
			
			String status = "All";
			if (index != -1)
				status = campStatusLb.getSelectedItem().getLabel();
			List<Campaigns> campList =null;
			 campList =  campaignsDao.getCampaignsByStatus(currentUser.getUserId(), status, firstResult, maxResult);
			
			*/
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			
			for (Campaigns campaign : prepareList) {
				
				try{
					campaign.setModifiedDate(new MyCalendar(campaign.getModifiedDate(),
							clientTimeZone,MyCalendar.FORMAT_DATETIME_STDATE));
					campaign.setCreatedDate(new MyCalendar(campaign.getCreatedDate(),
							clientTimeZone,MyCalendar.FORMAT_DATETIME_STDATE));
					//TODO need to append 'segmented' if this campaign associated mailing list is segmented
					if(campaign.getListsType() != null && !campaign.getListsType().equalsIgnoreCase("Total")) {
						logger.info("the campaign is segmented......"+campaign.getListNames()+"(segmented)");
						campaign.setListNames("Segment of("+getListNames(campaign)+")");
						
					} else if (campaign.getListsType() == null || campaign.getListsType().equalsIgnoreCase("Total")) {
						campaign.setListNames(getListNames(campaign));
					}
					
				} catch (Exception e) {
					
					logger.error(" ** Exception : ",(Throwable)e);
				}
			} // for
			logger.debug("Campaign list size :" + prepareList.size());
			return prepareList;
		} catch (Exception e) {
			logger.error("** Exception : " ,(Throwable)e);
			return null;
		}
	}
	
	
	public String getListNames(Campaigns campaign) {
		String listNames = "";
		
		Set<MailingList> mailingLists= campaign.getMailingLists();
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
	
	public void sendNow(Image img){

		try {
			MessageUtil.clearMessage();
			
			ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			
//			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao)
													SpringUtil.getBean("campaignScheduleDao");
			CampaignScheduleDaoForDML campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)
					SpringUtil.getBean("campaignScheduleDaoForDML");
			
			Campaigns campaign =  (Campaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
			if(campaign==null){
				logger.error("** Exception : campaign object is null when send now link is clicked**");
				return;
			}
			
			if(currentUser.getSubscriptionEnable() && campaign.getCategories() ==null){
				MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
					"\n Please specify campaign category for this communication and try again.", "color:red;");
				return;
			}
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
			
			if(logger.isDebugEnabled())logger.debug("got Campaign obj" + campaign.getCampaignName());
			
			
			
			
			// added for campaign category checking.
			/*if(!currentUser.getSubscriptionEnable()){
				
				MessageUtil.setMessage("You have not  enabled subscriber preference center setting. \n" +
						" To continue, please go to the subscriber preference center setting and enable  settings.", "color:red");
				return;
			}
			*/
			/*if(currentUser.getSubscriptionEnable() && campaign.getCategories() == null ){
				
				MessageUtil.setMessage("You have currently enabled subscriber preference center setting. \n To continue, please go to the 1st step and select a campaign category first.", "color:red");
				return;
				
			}
			
			Long categoryId = campaign.getCategories();
			if(categoryId != null){
				
				if(!currentUser.getSubscriptionEnable()){
					
					MessageUtil.setMessage("You have not  enabled subscriber preference center setting. \n" +
							" To continue, please go to the subscriber preference center setting and enable  settings.", "color:red");
					return;
				}
				UserCampaignCategories  userCmapObj = null;
				
					  userCmapObj =userCampaignCategoriesDao.findById(categoryId.toString());
				
				 
				 if(currentUser .getSubscriptionEnable() && !userCmapObj.getIsVisible()){
					 
					 MessageUtil.setMessage("This  category  "+userCmapObj.getCategoryName()+" is invisible  in settings, " +
					 		"\n please make as visible  or select another category.", "color:red;");
					 return;
				 }
			}
			*/
			beeJsonContent = campaign.getJsonContent();
			if((beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)))&& campaign.getEditorType().equalsIgnoreCase("beeEditor")){ 

					MessageUtil.setMessage("Email content cannot be empty", "color:red");
					return;
			
			}
			
			if(campaign.isCustomizeFooter()) {
				if(!campaign.getHtmlText().contains("|^GEN_unsubscribeLink^|")) {
					MessageUtil.setMessage("Looks like the Unsubscribe Link merge-tag is missing in your email content. "
							+ "Please note that default sender address will be added to email-footer at the time of sending to be compliant with email sending laws.", "color:blue");
				}
			}
		 
			String draftStatus = campaign.getDraftStatus();
			if(!(draftStatus.equalsIgnoreCase("complete") || draftStatus.equalsIgnoreCase("CampFinal"))){
				MessageUtil.setMessage("Please complete the email creation process.", "color:red", "TOP");
				return;
			}
			String appName = PropertyUtil.getPropertyValueFromDB("ApplicationName");
			
			int available = 0;
			try {
				SubscriptionDetails subDetails = new SubscriptionDetails();
				available = subDetails.getEmailStatus(Calendar.getInstance());
			} catch (Exception e1) {
				logger.error("** Exception : Problem while getting the subscription details", e1);
				return;
			}
			
			if(currentUser.getPackageExpiryDate() == null || currentUser.getPackageExpiryDate().before(Calendar.getInstance())){
				MessageUtil.setMessage("Schedule date cannot be after your package expiry date.","color:red", "TOP");
				return;
			}

			if(available <=0) {
				MessageUtil.setMessage("Email campaign can't be sent at the moment as your account is low on email credits. Please contact Support team.", 
						"color:red", "TOP");
				return;
			}
			
			List<CampaignSchedule> campaignSchedules = campaignScheduleDao.findActiveOrDraftCampaignSchedules(campaign.getCampaignId());
			String msg ="";
			if(campaignSchedules != null && campaignSchedules.size()>0){
				Calendar currentCal = Calendar.getInstance();
				int expiredSchedules = 0;
				int activeSchedules = 0;
				for(CampaignSchedule campaignSchedule : campaignSchedules){
					
					if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
					//	campaignSchedule.setStatus((byte)6);
						expiredSchedules ++; 
						//campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)6);
					}
					else if(campaignSchedule.getStatus() == 2 ){
					//	campaignSchedule.setStatus((byte)0);
						//campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)0);
						activeSchedules ++;
					}
				}
				logger.info("Expiry Count is "+expiredSchedules +"\t Active Count "+activeSchedules);
				if(expiredSchedules >0 && activeSchedules >0 ){
					msg = "Note : This action will also activate "+(campaignSchedules.size() - expiredSchedules)+" schedule(s) and expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
				}
				else if(expiredSchedules <=0 && activeSchedules >0 ){
					msg = "Note : This action will also activate "+(campaignSchedules.size() - expiredSchedules)+" schedule(s) in this campaign.";
				}
				else if(expiredSchedules >0 && activeSchedules <=0 ){
					msg = "Note : This action will also expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
				}
			
			}
			
			
			try {
				
				long emailCount = 0;
				String sentType = campaign.getListsType();
				if(sentType.startsWith("Total")) { 
					Set<MailingList> mlists = campaign.getMailingLists();
					if(mlists.size()==0) {
						
						MessageUtil.setMessage("Configured list(s) no longer exist. You might have deleted it.", "color:red;");
						
						return ;
						
						
					}//if
					String ml_ids_str = "";
					
					for(MailingList mailingList:mlists) {
						
						if(ml_ids_str.length() > 0) ml_ids_str+=",";
						ml_ids_str += mailingList.getListId();
					}
					
					if(logger.isDebugEnabled()) {
						logger.debug(" Got Mailing lists :"+ml_ids_str);
					}
					emailCount = contactsDao.getEmailCount(mlists,true);
			
			
				}else {
					SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
					MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
					String segRuleIds = sentType.split(""+Constants.DELIMETER_COLON)[1];
					List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
					
					if(segmenRules == null) {
						
						MessageUtil.setMessage("Configured segment(s) no longer exist. You might have deleted it.", "color:red;");
						
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
						
						String qry =  SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);
						
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
						
						logger.info(" Generated Query :"+qry);
						if(tempQry.length() > 0) tempQry += " UNION ";
						
						tempQry += qry;
//						contactsDao.insertSegmentedContacts(tempQry);
						
						
						
						
					}//for
					
					
					emailCount = contactsDao.getSegmentedContactsCount(tempQry);
					
					
					
					
					//##################################
				}//else
			int confirm = Messagebox.show("Are you sure you want to send the campaign now? \n"+msg,
					appName, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if(confirm == 1) {
				//Added for 2.3.11
				Calendar currentCal = Calendar.getInstance();
				int expiredSchedules = 0,activeSchedules=0;
				for(CampaignSchedule campaignSchedule : campaignSchedules){
					if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
						campaignSchedule.setStatus((byte)6);
						logger.info("1");
						expiredSchedules ++; 
						//campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)6);
						campaignScheduleDaoForDML.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)6);
						
					}
					else if(campaignSchedule.getStatus() == 2 ){
						campaignSchedule.setStatus((byte)0);
						logger.info("2");
						//campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)0);
						campaignScheduleDaoForDML.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)0);
						activeSchedules ++;
					}

					/*if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
						campaignSchedule.setStatus((byte)6);
						expiredSchedules ++ ;
						logger.info("Exipring old schedules"+campaignSchedule.getCsId());
						campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)6);
					}
					else if(campaignSchedule.getStatus() == 2 && ! campaignSchedule.getScheduledDate().before(currentCal) ){
						campaignSchedule.setStatus((byte)0);
						logger.info("Activating future schedules"+campaignSchedule.getCsId());
						activeSchedules ++;
						campaignScheduleDao.updateCampaignStatus(campaignSchedule.getCsId(), campaign.getCampaignId(), (byte)0);
					}*/
				}
				logger.info("Number of Expired Campaigns :"+ expiredSchedules+" & Active Campaings :"+activeSchedules );
				
				/*long emailCount = 0;
					String sentType = campaign.getListsType();
					if(sentType.startsWith("Total")) { 
						Set<MailingList> mlists = campaign.getMailingLists();
						String ml_ids_str = "";
						
						for(MailingList mailingList:mlists) {
							
							if(ml_ids_str.length() > 0) ml_ids_str+=",";
							ml_ids_str += mailingList.getListId();
						}
						
						if(logger.isDebugEnabled()) {
							logger.debug(" Got Mailing lists :"+ml_ids_str);
						}
						emailCount = contactsDao.getEmailCount(mlists,true);
				
				
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
							
							String qry =  SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_EMAIL,mlsbit);
							
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
							
							logger.info(" Generated Query :"+qry);
							if(tempQry.length() > 0) tempQry += " UNION ";
							
							tempQry += qry;
//							contactsDao.insertSegmentedContacts(tempQry);
							
							
							
							
						}//for
						
						
						emailCount = contactsDao.getSegmentedContactsCount(tempQry);
						
						
						
						
						//##################################
					}//else
					*/
					
                    if (available < emailCount) {
                        try {
                            int result = currentUser.getEmailCount() - currentUser.getUsedEmailCount();
                            if (Math.signum(result) == -1 || Math.signum(result) == 0) {
                                Messagebox.show(
                                    "Email cannot be sent as configured contacts count"
                                        + " exceeds the email send limit.Please contact admin to increase your credits",
                                    appName, Messagebox.OK, Messagebox.EXCLAMATION);
                                SupportController support = new SupportController();
                                SupportTicket ticket = new SupportTicket();
                                ticket.setClientName(currentUser.getFirstName());
                                ticket.setContactEmail(currentUser.getEmailId());
                                ticket.setContactEmail(currentUser.getPhone());
                                ticket.setDescription("test mail");
                                ticket.setType((byte) 3);
                                ticket.setProductAreaType((byte) 1);
                                support.sendMailToSupport(ticket);
                                return;
                            }
                        }catch (Exception e) {
                           logger.error( " ==Exception== " +e);
                           return;
                        }
                    }
					
					
					
					Set<String> subjectSymbolSet = Utility.getPhSet(campaign.getHtmlText());
					Set<String> ccPhSet = Utility.getBarcodePhset(campaign.getHtmlText());
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

					Calendar scheduleTime = Calendar.getInstance();
					
					if(!couponIds.isEmpty()){
						
						List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIds,currentUser.getUserOrganization().getUserOrgId());
						
						
						//logger.debug("promo size is  :: "+coupList.size());
						//Long availPromoCount = 0l;
						
						if(coupList != null && coupList.size() >0){
							String coupNameStr = "";
							
							for (Coupons eachPromoObj : coupList) {
								logger.debug("eachPromoObj.getAvailable() is  ::"+eachPromoObj.getAvailable());
								
								if(!eachPromoObj.getAutoIncrCheck()){
									long count = couponCodesDao.findIssuedCoupCodeByCoup(eachPromoObj.getCouponId());
									logger.debug("count is "+count);
									long availCCount = eachPromoObj.getTotalQty() - count;
									
									if(availCCount < emailCount) {
										MessageUtil.setMessage("Email cannot be sent as configured  "+eachPromoObj.getCouponName()+" available discount codes count" +
												" \n exceeds the email send limit.Please  increase discount codes issued limit.", "color:red;");
										return;
									}
								}
								
								if(eachPromoObj.getStatus().equals(Constants.COUP_STATUS_PAUSED) || 
										eachPromoObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)  ){
									coupNameStr+= coupNameStr.trim().length()== 0 ? eachPromoObj.getCouponName() :","+eachPromoObj.getCouponName();
									
								}
							}
							
							if(coupNameStr.trim().length() > 0){
								MessageUtil.setMessage("The discount code "+coupNameStr+ " \n used in this campaign has either expired or in paused status."
										+ "\n Please change the status of this discount code. ", "color:red;");
								return ;
							}
							
						}
						
					}
										
					CampaignSchedule cs = new CampaignSchedule();
					cs.setCsId(campaignScheduleDao.getCurrentId());
					cs.setCampaignId(campaign.getCampaignId());
					cs.setScheduledDate(scheduleTime);
					cs.setUser(currentUser);
					cs.setStatus((byte)0);
					
					//campaignScheduleDao.saveOrUpdate(cs);
					campaignScheduleDaoForDML.saveOrUpdate(cs);
					
					Messagebox.show("Mails will be sent in a moment.", "Information", Messagebox.OK, Messagebox.INFORMATION);
					
					if(logger.isDebugEnabled()) {
						logger.debug("Send Now is submitted for the campaign " + 
								campaign.getCampaignName());
					}
					
					String status = getCampaignStatus(campaign);
					Listitem  item = (Listitem)img.getParent().getParent().getParent();
					Listcell statusLbl = (Listcell)item.getChildren().get(3);
					statusLbl.setLabel(status == null || status.length() == 0 ? campaign.getStatus() : status);
					
					campaign.setStatus(Constants.CAMP_STATUS_ACTIVE);
					campaign.setDraftStatus("complete");
					campaign.setStatusChangedOn(Calendar.getInstance());
					logger.info("====>>>"+ campaign.getAddrsType());
					//campaignsDao.saveOrUpdate(campaign);
					campaignsDaoForDML.saveOrUpdate(campaign);
					redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					campListPaging.setActivePage(0);
					campListPaging1.setActivePage(0);
					campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					campListPaging1.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			}
			} catch (Exception e) {
				logger.error("** Exception : ", e);
			}
		} catch (Exception e) {
			logger.error("** Exceptoin : " + e);
		}
	}

	/*public void sendTestMail(String emailId,Campaigns campaign) {
		try {
			//MessageUtil.setMessage("Test mail will be sent in a moment.", "color:blue", "top");
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			EmailQueue testEmailQueue = new EmailQueue(campaign, Constants.EQ_TYPE_TESTMAIL,"Active",emailId,MyCalendar.getNewCalendar(),campaign.getUsers()); 
			try{
				testEmailQueue.setMessage(campaign.getHtmlText());
				emailQueueDao.saveOrUpdate(testEmailQueue);
			}catch(Exception e1){
				logger.error("** Exception : Error while saving the Test Email into queue "+ e1 + " **");
			}
		} catch (Exception e) {
			logger.error("** Exceptoin : " + e);
		}
	}*/
	
	
	
	
	public void edit(Image img) {
		
		Campaigns campaign =  (Campaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
		
		/*if(campaign.getEditorType()!=null && 
				campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)) {
			MessageUtil.setMessage("External email type can not be edited as of now.", "color:red", "top");
			return;
		} // if
*/		
		String editorType = campaign.getEditorType();
		if(campaign!=null && editorType!=null) { 
			sessionScope.setAttribute("editorType",editorType);
		}
		
		sessionScope.setAttribute("campaign",campaign);
		sessionScope.setAttribute("fromPage", "campaign/View");
		String draftStatus = campaign.getDraftStatus();
		if(draftStatus.equalsIgnoreCase("complete")){
			sessionScope.setAttribute("editCampaign","edit");
			Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
		}
		/*else if(draftStatus.equalsIgnoreCase("CampTextMsg")){
			session.setAttribute("editCampaign","view");
			Redirect.goTo("/campaign/CampTextMsg");
		}*/
		else{
			sessionScope.setAttribute("editCampaign","view");
			Redirect.goTo("campaign/" + draftStatus);
		}
	}

	//public void copyEmail(Popup copyEmailPopupId, String copyEmailNameStr, Listbox campListlbId) {
	public void onClick$copyBtnId() {
		try {
			MessageUtil.clearMessage();
			

			int available = 0;
			try {
				SubscriptionDetails subDetails = new SubscriptionDetails();
				available = subDetails.getEmailStatus(Calendar.getInstance());
			} catch (Exception e1) {
				logger.error("** Exception : Problem while getting the subscription details", e1);
				return;
			}
			
			if(available <=0) {
				MessageUtil.setMessage("Email cannot be copied as you have \n" +
						"reached your Email credits limit or your account has expired.", 
						"color:red", "TOP");
				return;
			}
			
			
			Campaigns campaign = (Campaigns)copyEmailPopupId.getAttribute("campaign");
			
			if(campaign == null){
				logger.debug("Selected campaign is null");
				return;
			}
			
			if(copyEmailNameTbId.getValue().trim().equals("")) {
				Messagebox.show("Provide valid email name.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			String copyEmailNameStr = Utility.condense(copyEmailNameTbId.getValue());
			
			/*if(!Utility.validateName(copyEmailNameStr)) {
				Messagebox.show("Provide valid email name. Name should not contain any special characters.",
						"Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}*/
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
			//Users user = GetUser.getUserObj();
			Users user = campaign.getUsers();
			if(campaignsDao.checkName(copyEmailNameStr, user.getUserId())) {
				Messagebox.show("Email name already exists. Please provide another name.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			logger.debug("Selected campaign :" + campaign.getCampaignName());
			
			try {
				Campaigns newCampaign = campaign.getCopy();
				String oldName = campaign.getCampaignName();
				newCampaign.setCampaignName(copyEmailNameStr);
				logger.info("====>>>"+ campaign.getAddrsType());
				//campaignsDao.saveOrUpdate(newCampaign);
				
				campaignsDaoForDML.saveOrUpdate(newCampaign);
				
				
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show("Copy of '" + oldName + "' email is created with the \n'" + copyEmailNameStr 
						+ "' name. \n You have to reschedule the email by clicking on edit campaign.", 
						"Information", Messagebox.OK, Messagebox.INFORMATION);
				
				
				try {
					if(self.getParent() instanceof Include) {
						Include currentInclude = (Include)self.getParent();
						currentInclude.setSrc("/zul/Empty.zul");
						currentInclude.setSrc("/zul/campaign/CampaignList.zul");
					}
				} catch (Exception e) {
					logger.error("** Exception : ",e);
				}
			} catch (Exception e) {
				logger.error("** Exception : Problem while creating copy of campaign" , e);
			}
			campListlbId.invalidate();
		} catch (Exception e) {
			logger.error("** Exception :" , e);
		}
	}
	
	public void delete(Image img) {
		try {
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			campStatusLb = (Listbox)li.getParent();
			int index = li.getIndex();
			Campaigns campaign =  (Campaigns)li.getValue();
			List<Campaigns> clist = new ArrayList();
			String res = "";
			String msg = "Are you sure you want to delete selected campaign?";
			String status = campaign.getStatus();
			if(status.equals(Constants.CAMP_STATUS_ACTIVE) || 
					status.equals(Constants.CAMP_STATUS_RUNNING) ) {
				msg = "Are you sure you want to delete the '" + status.toLowerCase() + "' campaign?";
			}
			int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == 1){
				clist.add(campaign);
				res = deleteCampaigns(clist);
				if(res.equals("success")){
					MessageUtil.setMessage("Selected campaign deleted successfully.","color:blue","TOP");
					campStatusLb.removeItemAt(index);
				}
				/*if(res.equals("problem")){
					MessageUtil.setMessage("Problem in deleting the campaign. Try again later.","color:red","TOP");
				}*/
			}
		} catch (Exception e) {
			logger.error("** Exceptoin : " + e);
		}
	}

	Bandbox campActionsBandBoxId;
	public void onSelect$campListlbId() {
		
		if(campListlbId.getSelectedCount() == 0){
			
			campActionsBandBoxId.setDisabled(true);
			campActionsBandBoxId.setButtonVisible(false);
		}else if(campListlbId.getSelectedCount() > 0) {
			
			campActionsBandBoxId.setDisabled(false);
			campActionsBandBoxId.setButtonVisible(true);
		}
		
		
		
	}//onSelect$campListlbId()
	
	
	public String deleteCampaigns(List<Campaigns> campaignsList){
		
		if(logger.isDebugEnabled()) logger.debug("-- Just Entered -- ");
		CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
		CampaignScheduleDao campaignScheduleDao  = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
		CampaignScheduleDaoForDML campaignScheduleDaoForDML  = (CampaignScheduleDaoForDML)SpringUtil.getBean("campaignScheduleDaoForDML");
		try{
			String campaignIds = "";
			Map<String,String> campMap = new HashMap<String, String>();
			for (Campaigns campaign:campaignsList) {
				campaignIds = campaignIds + ( campaignIds.length() > 0 ? "," : "" ) + campaign.getCampaignId();
				campMap.put(""+campaign.getCampaignId(), campaign.getCampaignName());
				
				
			}
			
			//TODO need to check whether this campaign is associated with any other program/Event trigger...
			
			AutoProgramComponentsDao autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
			EventTriggerDao eventTriggerDao = (EventTriggerDao)SpringUtil.getBean("eventTriggerDao");
			
			
			List<String> progconfiguredIds = autoProgramComponentsDao.findConfiguredComponents(campaignIds);
			List<String> eventTriggerConfiguredIds = eventTriggerDao.findConfiguredTriggers(campaignIds); 
			
			String notDelList = "";
			for (String campId : progconfiguredIds) {
				
				notDelList +=  notDelList.equals("")?campMap.get(campId):","+campMap.get(campId);
				
			}//for
			
			for(String etCampId : eventTriggerConfiguredIds) {
				
				if(notDelList.equals("")) notDelList += campMap.get(etCampId);
				else if(!notDelList.equals("") && !notDelList.contains(campMap.get(etCampId))) {
					notDelList += ","+campMap.get(etCampId);
					
				}//else if
				
			}//for
			
			
			
			String idsArray[] = campaignIds.split(",");
			String delIdStr = "";//will be hold the campaigns that to be deleted
			String delCampIdStr = "";
			for (String id : idsArray) {
				if(!progconfiguredIds.contains(id) && !eventTriggerConfiguredIds.contains(id)){
					delIdStr += delIdStr.equals("")?id: "," + id; 
				}
			} //for
			if(delIdStr.length() > 0 && notDelList.length() > 0) {
				//means the selected campaigns have configured to some programs or event triggers....
				int confirm = Messagebox.show("Some campaigns cannot be deleted \n since they are used in auto-communications or event triggers."
						+ notDelList + ". Do you want to continue deleting others?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm != 1){
					return "problem";
				}
				
				
			}//if
			
			
			if(delIdStr.length()==0 && notDelList.length()>0) {
				MessageUtil.setMessage("Some campaigns cannot be deleted \n since they are used in auto-communications or event triggers,\n " +
						"so they cannot be deleted.  [ " + notDelList + " ]", "color:red;", "TOP");
				return "problem";
			} //if
			
			
			if(delIdStr.length()>0) {
				
				//TODO need to delete the ids of notDelList from delIdStr
				
				logger.debug("-----about to delete the campaign ids=====>"+delIdStr);
				
				
				
				UserCampaignExpirationDao userCampaignExpirationDao = null;
				UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML = null;
				try {
					userCampaignExpirationDao = (UserCampaignExpirationDao)ServiceLocator.
											getInstance().getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
					userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)ServiceLocator.
							getInstance().getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				} 
				
				userCampaignExpirationDaoForDML.deleteBy(delIdStr);
				//campaignScheduleDao.deleteByCampaignId(delIdStr);
				campaignScheduleDaoForDML.deleteByCampaignId(delIdStr);
				
				//campaignsDao.deleteByCampaignId(delIdStr);
				campaignsDaoForDML.deleteByCampaignId(delIdStr);
				//campaignsDao.deleteByCampaignIdFromIntermediateTable(delIdStr);
				campaignsDaoForDML.deleteByCampaignIdFromIntermediateTable(delIdStr);
				
				
			} //if
			
			if(notDelList.length()>0){
				MessageUtil.setMessage("Some campaigns are deleted. \n" +
						"[ " + notDelList + " ] Campaigns configured to the Programs/Event Triggers have not been deleted."
						, "color:maroon;", "TOP");
			}/*else{
				MessageUtil.setMessage("Selected campaigns are deleted.", "color:blue;", "TOP");
			}*/
			
			
			if(logger.isDebugEnabled()) logger.debug("Campaigns ids to delete the schedule Objs : " + campaignIds);
			/*campaignScheduleDao.deleteByCampaignId(campaignIds);
			campaignsDao.deleteByCollection(campaignsList);*/
			if(logger.isDebugEnabled()) logger.debug("-- Exit -- ");
			return "success";
		}
		catch(Exception e){
		    logger.error("** Exception **" + e);
			return "problem";
		}
		
	}//deleteCampaigns
	
	//public void deleteMultiple(Listbox campListlbId) {
	public void onClick$delSelectedId()  {
		try {
			int count = campListlbId.getSelectedCount();
			if(logger.isDebugEnabled()) 
				logger.debug("Number of selected campaigns to delete :" + count);
			if(count == 0) {
				Messagebox.show("Select the emails to delete.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			String msg = "Are you sure, you want to delete the selected emails?";
			boolean found = false;
			Set<Listitem> selList = campListlbId.getSelectedItems();
			Campaigns campaign;
			List<Campaigns> campaignsList = new ArrayList<Campaigns>();
			
			for (Listitem li : selList) {
				campaign = (Campaigns)li.getValue();
				campaignsList.add(campaign);
				if(campaign.getStatus().equals(Constants.CAMP_STATUS_ACTIVE) || 
						campaign.getStatus().equals(Constants.CAMP_STATUS_RUNNING) ) {
					found = true;
				}
				
			}
			if(found) {
				msg = "Some of the selected campaigns are 'Active/Running', \n do you want to continue?";
			}
			try {
				int confirm =Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						
				if(confirm == Messagebox.OK){
				if(deleteCampaigns(campaignsList).equals("success")){
					
					Include xcontents = Utility.getXcontents();
					xcontents.invalidate();
					
				}
				
				}
				else {
					campActionsBandBoxId.setDisabled(true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}	
			/*for (Listitem li : selList) {
				li.setVisible(false);
			}*/
			campListlbId.clearSelection();
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
		}
	}

	public void report(Image img){
		try {
			CampaignReportDao campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
			Campaigns campaign =  (Campaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
			//PageUtil.clearHeader();
			MessageUtil.clearMessage();
			Long userId = GetUser.getUserId();
			long reportCount = campaignReportDao.getReportCountByCampaign(campaign.getCampaignName(), userId);
			if(reportCount < 1){
				MessageUtil.setMessage("No records exist for the email : "+campaign.getCampaignName(),"color:red", "TOP");
			}else{
				sessionScope.setAttribute("campaign",campaign);
				sessionScope.setAttribute("campreport","true");
				Redirect.goTo(PageListEnum.CAMPAIGN_REPORT);
			}
		} catch (Exception e) {
		    logger.error("** Exception **" + e);
		}
	}
	
	
	Campaigns campaign;
	
	void doPopUp(Image img){
		try {
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			campListlbId = (Listbox)li.getParent();
			campListlbId.setSelectedIndex(li.getIndex());
			campaign =  (Campaigns)li.getValue();
			if(campaign==null){
				logger.error("** Exception : campaign object is null when send test mail link is clicked**");
				return;
			}
			String htmlText = campaign.getHtmlText();
			
			if(currentUser.getSubscriptionEnable() && campaign.getCategories() ==null){
				MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
					"\n Please specify campaign category for this communication and try again.", "color:red;");
				return;
			}
			if(htmlText==null){
				MessageUtil.setMessage("Complete the email creation process first. Select template for the campaign to be sent.", "color:red", "TOP");
				return;
				
			}
			
			/*if(!draftStatus.equalsIgnoreCase("CampLayout")||
					draft){
				MessageUtil.setMessage("Complete the email creation process first.", "color:red", "TOP");
				return;
			}*/
			
			testMailWinId.doModal();
		} catch (SuspendNotAllowedException e) {
			logger.error("***** Exception : ",e);
		} catch(Exception e) {
			logger.error("***** Exception : ",e);
		}
	}
	
	private Textbox testMailWinId$emailIdTbId;
	public void onClick$sendTestMailBtnId$testMailWinId() {
		 String emailId = testMailWinId$emailIdTbId.getValue();
		if(sendTestMail(emailId)){
		testMailWinId$emailIdTbId.setValue("");
		}
		
	}
	
	
	public void onClick$cancelSendTestMailBtnId$testMailWinId() {
		
		testMailWinId$msgLblId.setValue("");
		testMailWinId$emailIdTbId.setValue("");
		testMailWinId.setVisible(false);
		
	}
	
	private Label testMailWinId$msgLblId;
	boolean sendTestMail(String emailId){
		try {
			//Label msgLblId = (Label)testMailWinId.getFellow("msgLblId");
			if(campaign==null){
				logger.error("Exception :Campaign was null");
			}
			
			
			
			if(emailId != null && emailId.trim().length() > 0) {
				

				
				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
				
				MessageUtil.clearMessage();
				
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
				
				//Check whether user is expired or not
				if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
					testMailWinId.setVisible(false);
					logger.debug("Current User, with userId:: "+currentUser.getUserId()+" has been expired, hence cannot send test mail");
					MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
					return false;
				}
				
				String[] emailArr = null;
				
				emailArr = emailId.split(",");
				if(emailArr.length == 0 || emailId.endsWith(","))
				{
					testMailWinId$msgLblId.setValue("Invalid Email Id:");
					return false;
				}
				for (String email : emailArr) {
					
					if(!Utility.validateEmail(email.trim())){
						testMailWinId$msgLblId.setValue("Invalid Email Id:'"+email+"'");
						return false;
					}
					else {
						
						//Testing valid domain email addresses - APP-308
					
						String res = PurgeList.checkForValidDomainByEmailId(email);
						
						if(!res.equalsIgnoreCase("Active")) {
							testMailWinId$msgLblId.setValue("Invalid Email Id:'"+email+"'");
							return false;
						}
						
					}
					//Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					
					
				}//for
				
				
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(campaign.getHtmlText(), currentUser);
				if(isValidCouponAndBarcode != null){
					testMailWinId$msgLblId.setValue("");
					testMailWinId$emailIdTbId.setValue("");
					testMailWinId.setVisible(false);
					return false;
				}
				
				for (String email : emailArr) {
					Utility.sendInstantMail(campaign, campaign.getSubject(), campaign.getHtmlText(),
							Constants.EQ_TYPE_TESTMAIL, email, null );
					
			//		Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					/*String postData = "UserRequest=enable";
					URL url = new URL("http://localhost:8080/Scheduler/simpleMailSender.mqrm");
					
					HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
					
					urlconnection.setRequestMethod("POST");
					urlconnection.setRequestProperty("Content-Type","text/html");
					urlconnection.setDoOutput(true);
					
					OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
					out.write(postData);
					out.flush();
					out.close();*/
					
					
				}//for
				
				/*if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getUserObj(), campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
				}
				
				
				testMailWinId.setVisible(false);
				MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "TOP");
				
				
			}else {
				testMailWinId$msgLblId.setValue("Invalid Email Id");
				testMailWinId$msgLblId.setVisible(true);
			}
			return true;
		}
		catch(Exception e) {
			logger.error("** Exception : " + e);
			return false;
		}
		
	}
	
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		logger.debug("event====");
		if(event.getTarget() instanceof Image) {
			Image currentImage = (Image)event.getTarget();
			String campaignImageType = (String)currentImage.getAttribute("type");
			if(campaignImageType.equalsIgnoreCase("previewPage")) {
				previewPage(currentImage);
			} else if(campaignImageType.equalsIgnoreCase("emailPage")) {
				doPopUp(currentImage);
			} else if (campaignImageType.equalsIgnoreCase("emailEdit")) { 
				edit(currentImage);
			} else if (campaignImageType.equalsIgnoreCase("emailCopy")) {
				
			} else if (campaignImageType.equalsIgnoreCase("emailDelete")) { 
				delete(currentImage);
			} else if (campaignImageType.equalsIgnoreCase("emailGo")) { 
				sendNow(currentImage);
			} else if (campaignImageType.equalsIgnoreCase("emailReport")) {
				report(currentImage);
			}
		} else if (event.getTarget() instanceof Popup) {
			try {
				OpenEvent openEvent = (OpenEvent)event;
				Image img = (Image)openEvent.getReference();
				logger.info(img.getParent().getParent().getParent());
				logger.info("" +((Listitem)img.getParent().getParent().getParent()).getValue());
				Campaigns campaign =  (Campaigns)((Listitem)img.getParent().getParent().getParent()).getValue();
				
				if(currentUser!=null && currentUser.getSubscriptionEnable() && campaign.getCategories() ==null){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				copyEmailPopupId.setAttribute("campaign",campaign);
				copyEmailNameTbId.setValue("Copy of " + campaign.getCampaignName());
			} catch (Exception e) {
			
			
			}
			
		}else if(event.getTarget() instanceof Paging) {
			//event.stopPropagation();
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			logger.info("inside onEvent() for paging, desiredPage = "+desiredPage);
			
			if(paging.getAttribute("onPaging").equals("topPaging")) {
				
				this.campListPaging1.setActivePage(desiredPage);
				
			}else if(paging.getAttribute("onPaging").equals("bottomPaging")) {
				
				this.campListPaging.setActivePage(desiredPage);
				
			}//else if
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			logger.info("inside onEvent() for paging, pSize = "+pSize);
			int ofs = desiredPage * pSize;
			logger.info("inside onEvent() for paging, (1st arg)ofs = "+ofs+" and 2nd arg of redraw() = "+(byte) pagingEvent.getPageable().getPageSize());
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
			
		}//else if
		
	}
	
	private void previewPage(Image currentImage) {
		Listitem li = (Listitem)currentImage.getParent().getParent().getParent();
		campListlbId = (Listbox)li.getParent();
		campListlbId.setSelectedIndex(li.getIndex());
		campaign =  (Campaigns)li.getValue();
		if(campaign==null){
			logger.error("** Exception : no preview");
			return;
		}
		String htmlText = campaign.getHtmlText();
		if(htmlText!=null && !htmlText.isEmpty()) {
			htmlText = Utility.mergeTagsForPreviewAndTestMail(htmlText, "preview");
			Utility.showPreview(previewWinCampaign$html, currentUser.getUserName(), htmlText);
			previewWinCampaign.setVisible(true);
			previewWinCampaign.doHighlighted();
		}else {
			MessageUtil.setMessage("No Template found for preview.", "color:red", "TOP");
			previewWinCampaign.setVisible(false);
		}
	}
	
	

	private boolean validateSetCreationDate() {
		
		if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null ){
			MessageUtil.setMessage("Please specify the dates.",
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
		
	}
	private boolean validateSetCampaignName() {
		if(searchByCmpgnNameTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter email name.",
					"color:red", "TOP");
			searchByCmpgnNameTbId.setFocus(true);
			return false;
		}
		return true;
	}
	
	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_NAME)) {
			searchByCmpgnNameDivId.setVisible(true);
			searchByCmpgnNameTbId.setText(Constants.STRING_NILL);
			searchByCmpgnNameTbId.setFocus(true);
			
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(false);
			campStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByCmpgnStatusDivId.setVisible(false);
			campStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_STATUS)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(true);
			campStatusLb.setSelectedIndex(0);
			return;
		}
		
	}
	
    public void onClick$filterBtnId() {
    	campActionsBandBoxId.setDisabled(true);
    	int pSize = 0;
    	
    	String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
   	 	     boolean status = validateSetCampaignName();
   	 	     if(status == false) return;
			 totalSize = campaignsDao.getCountByCampaignName(searchByCmpgnNameTbId.getValue().trim(), currentUser.getUserId());
			 pSize = totalSize;
			 logger.info("----onClick$submintBtnId()----name----list size="+totalSize);
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
			 boolean status = validateSetCreationDate();
			 if(status == false) return;
			 totalSize = campaignsDao.getCountByCreationDate(fromDateStr, toDateStr,currentUser.getUserId());
			 pSize = totalSize;
			 logger.info("----onClick$submintBtnId()----Creation Date----list size="+totalSize);
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
			 totalSize = campaignsDao.getCount(currentUser.getUserId(), "All");
			 if(campStatusLb.getSelectedItem().getLabel().equalsIgnoreCase("All"))
			 {
				 pSize = totalSize;
			 }
			 else
			 {
				 pSize = getStatusCount();
			 }
			 logger.info("----onClick$submintBtnId()----Status is----list size="+totalSize);
	    }
    	
        logger.info("----filter button pressed and totalSize = "+totalSize);
        
		 setSizeOfPageAndCallRedraw(pSize);
       
	}
    
    public String orderby_colName="modifiedDate",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
    public void onClick$sortbyemailName() {
    			//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    			orderby_colName = "campaignName";
    			desc2ascasc2desc();	
    			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    }
    public void onClick$sortbycreatedDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "createdDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
    public void onClick$sortbylastModifiedDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "modifiedDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
    
    public void onClick$resetAnchId() {
		
    	int pSize;
    	campActionsBandBoxId.setDisabled(true);
    	srchLbId.setSelectedIndex(0);
    	campStatusLb.setSelectedIndex(0);
   
    	
    	searchByCmpgnNameDivId.setVisible(false);
		searchByCmpgnCreationDateDivId.setVisible(false);
		searchByCmpgnStatusDivId.setVisible(true);
    	
    	totalSize = campaignsDao.getCount(currentUser.getUserId(), "All");
		logger.info("----onClick$resetAnchId()----Status is----list size="+totalSize);
		
		pSize = totalSize;
		
		orderby_colName="modifiedDate";
		desc_Asc="desc";
		pageSizeLbId.setSelectedIndex(1);
		setSizeOfPageAndCallRedraw(pSize);
    	
		
	}
    
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
}
