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
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.WACampaign;
import org.mq.marketer.campaign.beans.WACampaignsSchedule;
import org.mq.marketer.campaign.beans.WAQueue;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoProgramComponentsDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.WACampaignScheduleDao;
import org.mq.marketer.campaign.dao.WACampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.WACampaignsDao;
import org.mq.marketer.campaign.dao.WACampaignsDaoForDML;
import org.mq.marketer.campaign.dao.WAQueueDaoForDML;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.WACampaignReportDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.WAQueueHelper;
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

public class ViewWACampaign extends GenericForwardComposer {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static final WACampaign[] waCampList = null;
	private Long userId;
	private WACampaignsDao WACampaignsDao;
	private WACampaignsDaoForDML WACampaignsDaoForDML;
	private WACampaignScheduleDao waCampaignScheduleDao;
	private WACampaignScheduleDaoForDML WACampaignScheduleDaoForDML;
	private WACampaignReportDao WACampaignReportDao;
	
	private Users currentUser;
	private Session session;
	

	
	
	private Listbox waCampListlbId;
	private Popup copyWACampaignPopupId;
	private Window testWAWinId;
	private Button sendTestWaBtnId;
	private Textbox mblNumTxtBoxId;
	private Button waCampCopyBtnId;
	private Textbox copyWaCampaignNameTbId;
	private CouponsDao couponsDao;
	private int itemsSize,totalSize;
	
	private Paging campListPaging,campListPaging1;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	
	
	private String fromDateStr;
	private String toDateStr;
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
	private Listbox waCampStatusLb;
	
	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	
	private Session sessionScopeForTimeZone;
	
	public ViewWACampaign(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("WhatsApp Campaigns","",style,true);
		
		
		this.WACampaignsDao = (WACampaignsDao)SpringUtil.getBean("waCampaignsDao");
		this.WACampaignsDaoForDML = (WACampaignsDaoForDML)SpringUtil.getBean("waCampaignsDaoForDML");
		this.waCampaignScheduleDao = (WACampaignScheduleDao)SpringUtil.getBean("waCampaignScheduleDao");
		this.WACampaignScheduleDaoForDML = (WACampaignScheduleDaoForDML)SpringUtil.getBean("waCampaignScheduleDaoForDML");
       	this.WACampaignReportDao = (WACampaignReportDao)SpringUtil.getBean("waCampaignReportDao");
		this.userId = GetUser.getUserId();
		this.session = Sessions.getCurrent();
		this.waCampStatusLb =  (Listbox)Executions.getCurrent().getAttribute("waCampStatusLb");
		currentUser = GetUser.getUserObj();
		 userCampaignCategoriesDao  =(UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		 couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		  
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			logger.debug("----just entered---");
			
			sessionScopeForTimeZone = Sessions.getCurrent();
			 itemsSize=0;
			 totalSize = 0;
			 int pSize;
			 totalSize = WACampaignsDao.getCount(currentUser.getUserId(), "All");
			 try
			 {
			 if(waCampStatusLb.getSelectedItem().getLabel().equalsIgnoreCase("All"))
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
			
			 copyWACampaignPopupId.addEventListener("onOpen", this);
			 redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			 	
			
			
			
			
			
			
			
		} catch (Exception e) {
			logger.error("** Exception : ",e);
		}
	}
	public int getStatusCount()
	{
		 List<WACampaign> waCampList = getWaCampaigns(0, totalSize);
		
		 int itemSize=0;
		 if(waCampList != null && waCampList.size() > 0) {
			 for (WACampaign waCampaign : waCampList) {
				 String status = getCampaignStatus(waCampaign);
					
					 int index = waCampStatusLb.getSelectedIndex();
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
		WACampaign waCampaign ;
		int index = 0;
		if(obj instanceof Image){
			Image img = (Image)obj;
			String imgAttr = (String)img.getAttribute("addEvent");
			
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			waCampListlbId = (Listbox)li.getParent();
			index = li.getIndex();
			waCampaign = (WACampaign)li.getValue();
			if(imgAttr.equalsIgnoreCase("sendTestWA")){
				
				if(currentUser!=null && currentUser.getSubscriptionEnable()){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				testWAWinId$msgLblId.setValue("");
				testWAWinId$mblNumTxtBoxId.setValue("");
				
				if(waCampaign == null){
					logger.error("** Exception : WA Campaign object is null when send test WA link is clicked**");
					return;
				}
				testWAWinId.setAttribute("WACampaign", waCampaign);
				testWAWinId.doModal();
			}
		else if(imgAttr.equalsIgnoreCase("Edit")){
				session.setAttribute("waCampaign", waCampaign);
				session.setAttribute("fromPage", "campaign/ViewWACampaign");
				String draftStatus = waCampaign.getDraftStatus();
				if(draftStatus != null) {
					if(draftStatus.equalsIgnoreCase(Constants.WA_CAMP_DRAFT_STATUS_STEP_COMPLETE)){
						session.setAttribute("editWACampaign", "edit");
						Redirect.goTo(PageListEnum.WA_CAMP_SETTINGS);
					}
					else{
						session.setAttribute("editWACampaign", "view");
						session.setAttribute("WADraftStatus", draftStatus);
						Redirect.goTo(PageListEnum.WA_CAMP_SETTINGS);
					}
				}
				
			}else if(imgAttr.equalsIgnoreCase("Copy")){
				
			}else if(imgAttr.equalsIgnoreCase("Delete")){
				
				List<WACampaign> clist = new ArrayList();
				String res = "";
				String msg = "Are you sure you want to delete selected WA campaign?";
				String status = waCampaign.getStatus();
				if(status.equalsIgnoreCase(Constants.CAMP_STATUS_ACTIVE) || 
						status.equalsIgnoreCase(Constants.CAMP_STATUS_RUNNING)){
					msg = "Are you sure do you want to delete the '"+status.toLowerCase()+"' WA campaign?";
				}
				try {
					int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == 1){
						clist.add(waCampaign);
						res = deleteWaCampaigns(clist);
						if(res.equals("success")){
							MessageUtil.setMessage("Selected WA campaign deleted successfully.","color:blue","TOP");
							waCampListlbId.removeItemAt(index);
						}
						if(res.equals("prolem")){
							MessageUtil.setMessage("Problem experienced while deleting the WA campaign. Please try again later.","color:red","TOP");
						}
					}//if
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
			}//else if
			else if(imgAttr.equalsIgnoreCase("Report")) {
				try {
					 
					waCampaign =  (WACampaign)((Listitem)img.getParent().getParent().getParent()).getValue();
					PageUtil.clearHeader();
					MessageUtil.clearMessage();
					Long userId = GetUser.getUserId();
					long reportCount = WACampaignReportDao.getReportCountByCampaign(waCampaign.getWaCampaignName(), userId);
					if(reportCount < 1){
						MessageUtil.setMessage("No records exist for the WA : "+waCampaign.getWaCampaignName(),"color:red", "TOP");
					}else{
						sessionScope.put("WaCampaign",waCampaign);
						sessionScope.put("WaCampreport","true");
						Redirect.goTo(PageListEnum.CAMPAIGN_WA_REPORT);
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
				waCampaign =  (WACampaign)((Listitem)img.getParent().getParent().getParent()).getValue();
				
				if(currentUser!=null && currentUser.getSubscriptionEnable()){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				copyWACampaignPopupId.setAttribute("waCampaign",waCampaign);
				copyWaCampaignNameTbId.setValue("Copy of " + waCampaign.getWaCampaignName());
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
	
	
	public void redraw(int startIndex, int size) {
		 
		try{
			
			MessageUtil.clearMessage();
			logger.debug("-- just entered redraw() --");
			int count =  waCampListlbId.getItemCount();
			
			for(; count>0; count--) {
				waCampListlbId.removeItemAt(count-1);
			}
			
			 
			List<WACampaign> campList = null;
			List<WACampaign> campaignList = new ArrayList<WACampaign>();
			 
			String value = srchLbId.getSelectedItem().getValue().toString();
			List<WACampaign> finalList = new ArrayList<WACampaign>();
			if(value.equals(SEARCH_BY_NAME)) {
			    
				campList =  WACampaignsDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
			
			else if(value.equals(SEARCH_BY_DATE)) {
				
				campList =  WACampaignsDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
      
			else if(value.equals(SEARCH_BY_STATUS)) {
				String status = getSelectedStatus();
				if(!status.equals("All")) {
					int sizeTotal = WACampaignsDao.getCount(currentUser.getUserId(), "All");
					campList =  WACampaignsDao.getCampaignsByStatus(currentUser.getUserId(), 0, sizeTotal,orderby_colName,desc_Asc);
					List<WACampaign> allWaCampList=new ArrayList<WACampaign>();
					if(campList != null) {
							for (WACampaign campaigns : campList) {
								String campStatus = getCampaignStatus(campaigns);
								
								
									if(campStatus.equalsIgnoreCase(status) ){
										campaigns.setStatus(campStatus);
										allWaCampList.add(campaigns);
									}
															
								
							 
							}
							if(allWaCampList.size() > 0){
								 for(int i=startIndex;i<size+startIndex&&i<allWaCampList.size();i++)
								 {
									finalList.add(allWaCampList.get(i));
									
								 }
							}
					}
					campListPaging.setTotalSize(allWaCampList.size());
				 	campListPaging1.setTotalSize(allWaCampList.size());
				}//if
				else{
				
					List<WACampaign> tempList = WACampaignsDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
					for (WACampaign campaigns : tempList) {
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
				 for (WACampaign waCampaign : finalList) {
					
					item = new Listitem();
					
					item.setValue(waCampaign);
					item.setHeight("30px");
					
					lc = new Listcell();
					lc.setParent(item);
					
					lc = new Listcell(waCampaign.getWaCampaignName());
					lc.setTooltiptext(waCampaign.getWaCampaignName());
					lc.setParent(item);
					
					String listName = Constants.STRING_NILL;
					SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
					if(waCampaign.getListType() != null && !waCampaign.getListType().equalsIgnoreCase("Total")){
						listName = "Segment of ("+waCampaign.getListNames()+")";
						String segRuleIds = waCampaign.getListType().split(""+Constants.DELIMETER_COLON)[1];
						List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
						if(segmenRules!=null) listName = "Segment of ("+getListNames(waCampaign)+")";
						else listName="--";
						
					}else if (waCampaign.getListType() == null || waCampaign.getListType().equalsIgnoreCase("Total")){
						listName = getListNames(waCampaign);
						
					}
					
					lc = new Listcell(listName);
					lc.setTooltiptext(listName);
					lc.setParent(item);
					
					
					String status = getCampaignStatus(waCampaign);
					
					
					 int index = waCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						logger.info(getSelectedStatus());
						itemsSize ++; 
						
					}
					
					if(waCampStatusLb.getSelectedItem() != null && 
							!waCampStatusLb.getSelectedItem().getLabel().equals("All") && !waCampStatusLb.getSelectedItem().getLabel().equals(status)) continue;
									
					lc = new Listcell(status);
					lc.setParent(item);
					
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- STARTS
					lc = new Listcell();
					lc.setLabel(waCampaign.getScheduledOccurrence() != null ? waCampaign.getScheduledOccurrence() : "--");
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(waCampaign.getScheduledDates() != null ? waCampaign.getScheduledDates() : "--");
					lc.setParent(item);
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- ENDS
					
					
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(waCampaign.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					logger.info("creation date: "+waCampaign.getCreatedDate()+"");
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(waCampaign.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(item);	
					
					
					lc = new Listcell();
					
					Hbox hbox = new Hbox();
					hbox.setAlign("center");
					hbox.setStyle("cursor:pointer;margin-right:5px;");
					
					Image img = new Image();
					img.setSrc("/img/email_page.gif");
					img.setTooltiptext("Send Test WA");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("addEvent", "sendTestWA");
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
					img.setPopup(copyWACampaignPopupId);
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
					item.setParent(waCampListlbId);
				}//if
				
				copyWACampaignPopupId.addEventListener("onOpen", this);	
					
				}
			
		}
		catch(Exception e){
			logger.error("Exception ", e);
		}

		 
	 }//redraw
	
	
	 public String getCampaignStatus(WACampaign campaigns) {
		    TimeZone clientTimeZone = (TimeZone)sessionScopeForTimeZone.getAttribute("clientTimeZone");
			String status = campaigns.getStatus();
			this.waCampaignScheduleDao = (WACampaignScheduleDao)SpringUtil.getBean("waCampaignScheduleDao");

		  	List<WACampaignsSchedule> scheduleList = waCampaignScheduleDao.getByWACampaignId(campaigns.getWaCampaignId());
			
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
			
			WACampaignsSchedule latestCampaignSchedule = null;
			
			
			
			
			for (WACampaignsSchedule waCampaignSchedule : scheduleList) {
					
				if(latestCampaignSchedule == null) latestCampaignSchedule = waCampaignSchedule;

				if(waCampaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
					latestCampaignSchedule = waCampaignSchedule;
				}
					
					//2.4.7. related -- place 1 STARTS 
					if((waCampaignSchedule.getStatus() != 0 )) {
						continue;
					}
					
					
					if(startCal== null && endCal== null){
						startCal = waCampaignSchedule.getScheduledDate();
						endCal = waCampaignSchedule.getScheduledDate();
						
						
					}
					
					
					if(endCal != null && endCal.before(waCampaignSchedule.getScheduledDate())){
				    	endCal = waCampaignSchedule.getScheduledDate();
				    }
					if(startCal.after(waCampaignSchedule.getScheduledDate())){
				    	startCal = waCampaignSchedule.getScheduledDate();
				    }
					
					
					//2.4.7. related -- place 1 ENDS
					
				 
			}//for
			
			
			
			String scheduledOnOrBetweenDates = null;
			
			if(startCal != null && endCal !=null){
				if(!startCal.equals(endCal)){
			
					campaigns.setScheduledOccurrence("Recurring");
				scheduledOnOrBetweenDates = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_STDATE, clientTimeZone)+" - "+
						MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_STDATE, clientTimeZone);
				campaigns.setScheduledDates(scheduledOnOrBetweenDates);
				
			 }else{
				 campaigns.setScheduledOccurrence("One-Time");
				 campaigns.setScheduledDates(MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			 }
			}
			 
			 
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
		 
		 
		 int index = waCampStatusLb.getSelectedIndex();
			
			String status = "All";
			if (index != -1)
				status = waCampStatusLb.getSelectedItem().getLabel();
		 
		 
			return status;
			
	 }
	
	
	/**
	 * this method returns the list of WA Campaigns based on the status selected
	 * @return List<WACampaigns>
	 */
	
	public List<WACampaign> getWaCampaigns(int firstResult, int maxResult){
		
		
		logger.debug("-- Just Entered -- ");
		
		List<WACampaign> waCampList = null;
		
		
		String status = "All";
		
		waCampList = WACampaignsDao.getWACampaignsByStatus(userId, status, firstResult, maxResult );
		
		
		
		
		TimeZone clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		
		for (WACampaign WACampaign : waCampList) {
			
			try{
				WACampaign.setModifiedDate(new MyCalendar(WACampaign.getModifiedDate(),
						clientTimeZone,MyCalendar.FORMAT_DATETIME_STDATE));
				//TODO need to append 'segmented' if this campaign associated mailing list is segmented
				if(WACampaign.getListType() != null && !WACampaign.getListType().equalsIgnoreCase("Total")) {
					logger.info("the campaign is segmented......"+WACampaign.getListNames()+"(segmented)");
					WACampaign.setListNames("Segment of("+getListNames(WACampaign)+")");
					
				} else if (WACampaign.getListType() == null || WACampaign.getListType().equalsIgnoreCase("Total")) {
					WACampaign.setListNames(getListNames(WACampaign));
				}
				
			} catch (Exception e) {
				
				logger.error(" ** Exception : ",(Throwable)e);
			}
		} // for
		return waCampList;
		
		
		
	}
	
	
	public String getListNames(WACampaign wACampaign) {
		String listNames = "";
		
		Set<MailingList> mailingLists= wACampaign.getMailingLists();
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
	
	
	
	Bandbox waActionsBandBoxId;
	public void onSelect$waCampListlbId() {
		
		if(waCampListlbId.getSelectedCount() == 0){
			
			waActionsBandBoxId.setDisabled(true);
			waActionsBandBoxId.setButtonVisible(false);
		}else if(waCampListlbId.getSelectedCount() > 0) {
			
			waActionsBandBoxId.setDisabled(false);
			waActionsBandBoxId.setButtonVisible(true);
		}
		
		
		
	}//onSelect$campListlbId()
	
	
	
	
	/**
	 * this method sends the Test Wa
	 */
	private Label testWAWinId$msgLblId;
	private Textbox testWAWinId$mblNumTxtBoxId;

	public void onClick$sendTestWABtnId$testWAWinId() {
		try{
			logger.debug("----just entered----"+testWAWinId);
			String[] mblNumArr = null;
			long mblNumber = 0;
			ArrayList<String> msgContentLst = null;
			
			WACampaign waCampaign = (WACampaign)testWAWinId.getAttribute("WaCampaign");
			
			String msgContent = "Test WAMsg"+"\n";
			String sendingMsg = waCampaign.getMessageContent();
			String mblNum = testWAWinId$mblNumTxtBoxId.getText();
			
			String tempMsgType = waCampaign.getMessageContent()+"";
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
							testWAWinId$mblNumTxtBoxId.setText("");
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
						testWAWinId$mblNumTxtBoxId.setText("");
						return;
					}
					
					mobCnt = 1;
				}
				
				
				String messageHeader = Constants.STRING_NILL;
				
				
				logger.info("sendingMsg after--- "+sendingMsg);
				charCount = sendingMsg.length();
			    usedCount =1;
			    if(charCount>160) usedCount=(charCount/160) + 1;
				logger.info("mobile numbers are===>"+mblNum);
			    
			 	
				/**
				 * Update the WA Queue
				 */
				WAQueueHelper waQueueHelper = new WAQueueHelper();
				waQueueHelper.updateWAQueue(mblNum, msgContent ,Constants.WA_MSG_TYPE_TEST, currentUser);
				
				testWAWinId.setVisible(false);
				MessageUtil.setMessage("Test WA will be sent in a moment.", "color:blue", "TOP");
				
				
			}else{
				testWAWinId$msgLblId.setStyle("color:red;font-family:verdana;font-size:11px;");
				testWAWinId$msgLblId.setValue("provide Mobile number");
			}
			}catch(NumberFormatException e) {
				testWAWinId$msgLblId.setStyle("color:red;font-family:verdana;font-size:11px;");
				testWAWinId$msgLblId.setValue("Please provide valid mobile number");
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
	 * @param WACampaign
	 */
	private void updateWAQueue(String mblNum,String msgContent , String messageType , WACampaign waCampaign) {
		logger.debug(">>>>>>> Started ViewWACampaign :: updateWAQueue <<<<<<< ");

		if(mblNum == null){
			logger.error("Error While Updating WA Queue as the no Mobile Number exist's");
			return;
		}

		List<WAQueue> waQueues = new ArrayList<WAQueue>();
		String []mobArray = mblNum.split(",");

		WAQueueDaoForDML waQueueDaoForDML = null;

		try{
			waQueueDaoForDML = (WAQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.WA_QUEUE_DAO_ForDML);
		}
		catch(Exception exception){
			logger.error("Error While creating WAQueueDao Object....:",exception);
		}

		for(String mobileNumber : mobArray){

			if(waQueueDaoForDML != null){
				WAQueue waQueue = new WAQueue();
				waQueue.setMessage(msgContent);
				waQueue.setMsgType(messageType);
				waQueue.setToMobilePhone(mobileNumber);
				waQueue.setUser(currentUser);
				waQueue.setSentDate(MyCalendar.getNewCalendar());
				waQueue.setStatus(Constants.CAMP_STATUS_ACTIVE);
				//Add to list
				waQueues.add(waQueue);
			}
		}//for each
		/**
		 * Storing to DB 
		 */

		if(waQueueDaoForDML != null){
			waQueueDaoForDML.saveByCollection(waQueues);

		}

		logger.debug(">>>>>>> Completed ViewWACampaign :: updateWAQueue <<<<<<< ");
	}//updateWAQueue

	public void onClick$waCampCopyBtnId(){
		try{
			MessageUtil.clearMessage();
			WACampaign waCampaign = (WACampaign)copyWACampaignPopupId.getAttribute("waCampaign");
			
			String copyWACampName = copyWaCampaignNameTbId.getValue();
			if(waCampaign == null){
				logger.debug("selected WA Campaign is null");
				return;
			}
			if(copyWACampName == null || !(Utility.condense(copyWACampName).length()>0)){
				Messagebox.show("Provide WA campaign name. Name cannot be left empty.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			if(!Utility.validateName(copyWACampName)) {
				Messagebox.show("Provide valid WA campaign name. Name should not contain any special characters.",
						"Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			if(WACampaignsDao.checkName(copyWACampName, userId)){
				Messagebox.show("WA campaign name already exists. Please provide another name.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			logger.debug("WA Campaign is======>"+waCampaign.getWaCampaignName());
			
			WACampaign newCampaign = waCampaign.getCopy();
			String oldName = newCampaign.getWaCampaignName();
			newCampaign.setWaCampaignName(copyWACampName);
			WACampaignsDaoForDML.saveOrUpdate(newCampaign);
			
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show("Copy of '" + oldName + "'  is created with '" + copyWACampName 
					+ "' name. \n You have to reschedule the WA by clicking on edit WA.", 
					"Information", Messagebox.OK, Messagebox.INFORMATION);
			
			try {
				if(self.getParent() instanceof Include) {
					Include currentInclude = (Include)self.getParent();
					currentInclude.setSrc("/zul/Empty.zul");
					currentInclude.setSrc("/zul/campaign/WACampaignList.zul");
				}
			} catch (Exception e) {
				logger.error("** Exception : ",e);
			}
			
			
			
		}catch (Exception e) {
			logger.debug("**Exception while preparing the copy of the WA Campaign",e);
		}
	}
	
	/**
	 * this method deletes the selected single WA campaign
	 */
	
	
	/**
	 * this method deletes the multiple Selected WA Campaigns 
	 * @param waCampListlbId
	 */
	public void onClick$delSelectedId()  {
		
		int count = waCampListlbId.getSelectedCount();
		if(logger.isDebugEnabled()) 
			logger.debug("Number of selected campaigns to delete :" + count);
		if(count == 0) {
			Messagebox.show("Select the WA to delete.", "Information" , 
					Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		String msg = "Are you sure, you want to delete the selected campaign?";
		boolean found = false;
		Set<Listitem> selList = waCampListlbId.getSelectedItems();
		WACampaign waCampaign;
		
		/****make a list of WA Campaigns to be get deleted********/
		List<WACampaign> campaignsList = new ArrayList<WACampaign>();
		
		for (Listitem li : selList) {
			waCampaign = (WACampaign)li.getValue();
			campaignsList.add(waCampaign);
			if(waCampaign.getStatus().equals(Constants.CAMP_STATUS_ACTIVE) || 
					waCampaign.getStatus().equals(Constants.CAMP_STATUS_RUNNING) ) {
				found = true;
			}
			
		}
		if(found) {
			msg = "Some of the selected campaigns are 'Active/Running', do you want to continue?";
		}
		try {
			int confirm=Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			 
			/******send the selected list for actual deletion ********/
			
			if(confirm== Messagebox.OK){
			
			if(deleteWaCampaigns(campaignsList).equals("success")) {
				
				MessageUtil.setMessage("Selected WA campaigns have been deleted successfully. ", "color:green;", "top");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
				
			}//if
			
          }//confirm if
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		waCampListlbId.clearSelection();
	}
	
	
	 private Listbox pageSizeLbId;
	 public void onSelect$pageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(waCampListlbId.getItemCount() == 0 ) {
					
					logger.debug("No WA Campaigns found for this user...");
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
	
	/**
	 * this method deletes the selected WA campaign(s) from DB
	 * @param WACampList
	 * @return String var to specify the success or failure of deletion
	 */
	public String deleteWaCampaigns(List<WACampaign> waCampList){
		logger.debug("----just entered for deletion----");
		try{
			String waCampaignIds = "";
			Map<String,String> waCampMap = new HashMap<String, String>();
			for (WACampaign waCampaign : waCampList) {
				waCampaignIds = waCampaignIds + (waCampaignIds.length() > 0 ? "," : "")+waCampaign.getWaCampaignId();
				waCampMap.put(""+waCampaign.getWaCampaignId(), waCampaign.getWaCampaignName());
				
			}
			
			//TODO need to check whether this campaign is associated with any other program/Event trigger...
			
			AutoProgramComponentsDao autoProgramComponentsDao = (AutoProgramComponentsDao)SpringUtil.getBean("autoProgramComponentsDao");
			EventTriggerDao eventTriggerDao = (EventTriggerDao)SpringUtil.getBean("eventTriggerDao");
			
			
			List<String> progconfiguredIds = autoProgramComponentsDao.findConfiguredSMSComponents(waCampaignIds);
			List<String> eventTriggerConfiguredIds = eventTriggerDao.findConfiguredSMSTriggers(waCampaignIds); 
			
			String notDelList = "";
			for (String smsCampId : progconfiguredIds) {
				
				notDelList +=  notDelList.equals("")?waCampMap.get(smsCampId):","+waCampMap.get(smsCampId);
				
			}//for
			
			for(String etCampId : eventTriggerConfiguredIds) {
				
				if(notDelList.equals("")) notDelList += waCampMap.get(etCampId);
				else if(!notDelList.equals("") && !notDelList.contains(waCampMap.get(etCampId))) {
					notDelList += ","+waCampMap.get(etCampId);
					
				}//else if
				
			}//for
			
			
			
			String idsArray[] = waCampaignIds.split(",");
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
					int confirm = Messagebox.show("The following WA campaigns are configured to Programs / Event Triggers and they cannot be " +
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
				MessageUtil.setMessage("The following WA campaigns are configured to the Program/Event Trigger, " +
						"and they cannot be deleted.  [ " + notDelList + " ]", "color:red;", "TOP");
				return "problem";
			} //if
			
			
			if(delIdStr.length()>0) {
				
				//TODO need to delete the ids of notDelList from delIdStr
				
				logger.debug("-----about to delete the campaign ids=====>"+delIdStr);
				
				
				WACampaignScheduleDaoForDML.deleteByCampaignId(delIdStr);
				WACampaignsDaoForDML.deleteByWACampaignId(delIdStr);
				WACampaignsDaoForDML.deleteByCampaignIdFromIntermediateTable(delIdStr);
				
				
			} //if
			
			if(notDelList.length()>0){
				MessageUtil.setMessage("Some WA campaigns are deleted. " +
						"[ " + notDelList + " ] WA campaigns configured to the Programs/Event Triggers have not been deleted."
						, "color:maroon;", "TOP");
			}
			
			
			if(logger.isDebugEnabled()) logger.debug("WA Campaigns ids to delete the schedule Objs : " + waCampaignIds);
			if(logger.isDebugEnabled()) logger.debug("-- Exit -- ");
			return "success";
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("** Exception while deleting the WA campains ",e);
			return "problem";
		}
	}
	
	
	public void onClick$cancelsendTestWaBtnId$testWAWinId() {
		
		logger.info("-----just entered-----to cancel");
		((Label)testWAWinId.getFellow("msgLblId")).setValue("");
		 ((Textbox)testWAWinId.getFellow("mblNumTxtBoxId")).setValue("");
		testWAWinId.setVisible(false);
		
	}
	
	public boolean invalidPromoCodes(WACampaign waCampaign) {
		boolean isValid = false;
		
		String messageContent = waCampaign.getMessageContent();

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
		content = content.replace("|^", "[").replace("^|", "]");
		String cfpattern = "\\[([^\\[]*?)\\]";
		
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);
		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
				
				ph = m.group(1); //.toUpperCase()

				
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
			waCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByCmpgnStatusDivId.setVisible(false);
			waCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_STATUS)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(true);
			waCampStatusLb.setSelectedIndex(0);
			return;
		}
		
	}//onSelect$srchLbId()
	
	public void onClick$filterBtnId() {
		waActionsBandBoxId.setDisabled(true);
    	int pSize = 0;
    	
    	String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
   	 	     boolean status = validateSetCampaignName();
   	 	     if(status == false) return;
   	 	     pSize = WACampaignsDao.getCountByCampaignName(searchByCmpgnNameTbId.getValue().trim(), currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----name----list size="+totalSize);
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
			 boolean status = validateSetCreationDate();
			 if(status == false) return;
			 pSize = WACampaignsDao.getCountByCreationDate(fromDateStr, toDateStr, currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----Creation Date----list size="+pSize);
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
    		  pSize = WACampaignsDao.getCount(currentUser.getUserId(), waCampStatusLb.getSelectedItem().getLabel());
			
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
    			orderby_colName = "waCampaignName";
    			desc2ascasc2desc();	
    			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    }
    public void onClick$sortbyCreadteDate() {
		orderby_colName = "createdDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
    public void onClick$sortbyModifiedDate() {
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
	    		 
		campListPaging.setActivePage(0);
		campListPaging1.setActivePage(0);
		campListPaging1.setTotalSize(pSize);
		
		redraw(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    }
     
     public void onClick$resetAnchId() {
 		
     	int pSize;
     	waActionsBandBoxId.setDisabled(true);
     	srchLbId.setSelectedIndex(0);
     	waCampStatusLb.setSelectedIndex(0);
    
     	
     	searchByCmpgnNameDivId.setVisible(false);
 		searchByCmpgnCreationDateDivId.setVisible(false);
 		searchByCmpgnStatusDivId.setVisible(true);
     	
 		pSize = WACampaignsDao.getCount(currentUser.getUserId(), "All");
 		logger.info("----onClick$resetAnchId()----Status is----list size="+totalSize);
 		
 		orderby_colName="modifiedDate";
		desc_Asc="desc";
		pageSizeLbId.setSelectedIndex(1);
 		setSizeOfPageAndCallRedraw(pSize);
     	
 		
 	}
}//class
