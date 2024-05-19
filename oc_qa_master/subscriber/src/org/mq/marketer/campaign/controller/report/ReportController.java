package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.BounceDao;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.BounceCategories;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;


@SuppressWarnings( { "unchecked", "serial" })
public class ReportController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	protected CampaignReportDao campaignReportDao = null;
	private UserActivitiesDao userActivitiesDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	public List categoriesList = null;
	String userName = null;
	private boolean isAdmin;
	String userId;

	Desktop desktopScope = null;
	Session sessionScope = null;
	private Textbox searchTbId;
	private Listbox campaignListLbId;
	private Listbox consolidatedRepLbId;
//	private Listbox userListLbId;
	private Listbox userOrgLbId,userNameLbId;
	private Listbox sourceTypeLbId,campConsolLbId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Grid reportGridId;
	private Label userOrgLblId,userNameLblId;
	private Label msgLblId,campNamelbId;
	private TimeZone  clientTimeZone;
	private String fromPage=null;
	private Div metricsid;
	private Listbox pageSizeLbId;
	private Combobox exportCbId;
	private ClicksDao clicksDao ;
	private OpensDao opensDao;
	private Listbox emailFilterLbId;
	private Window custExport;
	private static final String allStr = "--All--";
	
	private Div custExport$chkDivId;
	
	private Paging campRepListTopPagingId,campRepListBottomPagingId;
	
	private BounceDao bounceDao;
	private Calendar fromDateCal, toDateCal ;
	private int filter;
	private String searchTextStr = Constants.STRING_NILL;
	Listitem selectedItem ;
	//searchTextStr = searchTbId.getValue().trim()
	public ReportController() {
		
		this.campaignReportDao = (CampaignReportDao)
		SpringUtil.getBean("campaignReportDao");
		clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
		opensDao =(OpensDao)SpringUtil.getBean("opensDao");
		desktopScope = Executions.getCurrent().getDesktop();
		sessionScope = Sessions.getCurrent();
		userName = GetUser.getUserName();
		userId = GetUser.getUserObj().getUserId().toString();
		clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_REPORT,GetUser.getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_REPORT,GetUser.getLoginUserObj());
		}
		logger.debug("isAdmin :" + sessionScope.getAttribute("isAdmin") );
		
		isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		
		fromPage=(String)sessionScope.getAttribute("fromPage");
		
		
	}//ReportController()
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		 String style = "font-weight:bold;font-size:15px;color:#313031;" +
			"font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Email Campaign Reports","",style,true);
	
		
        pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		defaultSettings();
		
		
		getCampaignReports(0,pageSize,orderby_colName,desc_Asc);
		logger.debug("=====done with reports module=====");
		
		try {
			
			Components.removeAllChildren(custExport$chkDivId);
			for (int i = 0; i < Constants.emailCampaignHeaderFields.length; i++) {
				Checkbox tempChk = new Checkbox(Constants.emailCampaignHeaderFields[i]);
				tempChk.setSclass("custCheck");
				tempChk.setParent(custExport$chkDivId);
			} // for
			
			Checkbox tempChk = new Checkbox("URLs");
			tempChk.setSclass("custCheck");
			tempChk.setParent(custExport$chkDivId);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
	}
	
	
	private void defaultSettings() {
		try {
			logger.debug("Is Admin :" + isAdmin);
			
			 //int totalSize = campaignReportDao.getCount(user.getUserName(),getSelectedStatus());
			 
			/* campListPaging.setTotalSize(totalSize);
			 campListPaging.setAttribute("onPaging", "topPaging");
			 campListPaging.addEventListener("onPaging", this); 
			 
			 
			 campListPaging1.setTotalSize(totalSize);
			 campListPaging1.setAttribute("onPaging", "bottomPaging");
			 campListPaging1.addEventListener("onPaging", this);
			 
			*/
			
			
			if(isAdmin) {
				
				setUserOrg();
				userOrgLbId.setVisible(true);
				userOrgLblId.setVisible(true);
				userNameLblId.setVisible(true);
				userNameLbId.setVisible(true);
				userId = null;
				
				//<listitem forEach="${reportsWinId.usersList}" label="${each[1]}" value="${each[0]}">
				
				/*Listitem li;
				List userObj = usersDao.findAllUsers();
				
				Object[] objArr; 
				for (Object object : userObj) {
					if(object instanceof Object[]) {
						objArr = (Object[])object;
						
						li = new Listitem(objArr[1] + "");
						li.setValue(objArr[0]);
						
						li.setParent(userListLbId);
					}
				} // for each userObj
				usersLblId.setVisible(true);
				userListLbId.setVisible(true);
				if(userObj.size() > 0) {
					userListLbId.setSelectedIndex(0);
					userId = (Long)userListLbId.getItemAtIndex(0).getValue();
				}*/
			} // if(isAdmin)
			
			
			reportGridId.setRowRenderer(new ReportGridRenderer());
			
			
			
			String isBack = (String)desktopScope.getAttribute("isReportResBack");
			
			if(isBack != null) {
				//logger.info("-------yes isBack enabled------");
				if (isBack.equals("true")){
					Object reportLbIdSelIndexObj = desktopScope.getAttribute("reportLbIdSelIndex");
					if(reportLbIdSelIndexObj!=null ) {
						int reportLbIdSelIndex = ((Integer)	reportLbIdSelIndexObj).intValue();
						if(campaignListLbId.getItemCount() > 0)	campaignListLbId.setSelectedIndex(reportLbIdSelIndex);
					}
					else{
						
						if(campaignListLbId.getItemCount() > 0)
						campaignListLbId.setSelectedIndex(0) ;
					}
					fromDateboxId.setValue((Calendar)desktopScope.getAttribute("fromDate"));
					toDateboxId.setValue((Calendar)desktopScope.getAttribute("toDate"));	
					
				}
			}
			else {
				//logger.info("-------no isBack enabled------");
				/*Calendar cal = MyCalendar.getNewCalendar();
				toDateboxId.setValue(cal);
				logger.debug("ToDate (server) :"+cal);
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
				logger.debug("FromDate (server) :"+cal);
				fromDateboxId.setValue(cal);*/
				setDateValues();
			}
			
			getCampaignsByUser(userId, (String)sourceTypeLbId.getSelectedItem().getValue());
			int num = campaignListLbId.getItemCount();
			logger.debug("number of campaigns" +num);
			if(num > 0){
				campaignListLbId.setSelectedIndex(0);
			}else{
				return;
			}
			//getCampaignReports();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}
	
	public void setDateValues() {
		
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :"+cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);
		
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		filter = 0;
		searchTextStr = Constants.STRING_NILL;
		selectedItem = null;
	}
	
	private void setUserOrg() {

		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		List<UserOrganization> orgList	= usersDao.findAllOrganizations();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(userOrgLbId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(userOrgLbId);
		} // for
		userOrgLbId.setSelectedIndex(0);
	} 
	
	public void onSelect$userOrgLbId() {
		filter = 0;
		searchTextStr = Constants.STRING_NILL;
		selectedItem = null;
		Components.removeAllChildren(userNameLbId);
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		if(userOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			emailFilterLbId.setDisabled(true);
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			campaignListLbId.setDisabled(true);
			searchTbId.setVisible(false);
			oldGetBitweenDatesBtnId();
			
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); //getPrimaryUsersByOrg((Long)userOrgLbId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			campaignListLbId.setDisabled(true);
			searchTbId.setVisible(false);
			reportGridId.setModel(new SimpleListModel(usersList));
			int totalSize = 0;
			campRepListTopPagingId.setTotalSize(totalSize);
			campRepListBottomPagingId.setTotalSize(totalSize);
//			onClick$getBitweenDatesBtnId();
			return;
		}
		
		String allUserIdForOrgId = Constants.STRING_NILL ;
//		List<Users> allUsersForOrgId = usersDao.getUsersListByOrg((Long)mlUserOrgLbId.getSelectedItem().getValue());
		for (Users users : usersList) {
			
			if(allUserIdForOrgId.trim().length() > 0) allUserIdForOrgId += Constants.DELIMETER_COMMA;
			allUserIdForOrgId += users.getUserId().toString();
			
		}
		
		Listitem tempList = new Listitem(allStr,allUserIdForOrgId);
		tempList.setParent(userNameLbId);
		
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
			
			tempItem = new Listitem(userNameStr,users.getUserId().toString());
			tempItem.setParent(userNameLbId);
			
		} // for
		
		if(userNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+userNameLbId.getItemCount());
			userNameLbId.setSelectedIndex(0);
		}
		emailFilterLbId.setDisabled(true);
		campaignListLbId.setDisabled(true);
		searchTbId.setVisible(false);
		oldGetBitweenDatesBtnId();
	}
	
	public void onSelect$userNameLbId() {
		try {
			filter = 0;
			searchTextStr = Constants.STRING_NILL;
			selectedItem = null;
			if(userNameLbId.getSelectedIndex() == 0){
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				emailFilterLbId.setDisabled(true);
				campaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
				campaignListLbId.setVisible(false);
			}
			else {
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				searchTbId.setVisible(false);
				searchTbId.setValue("");
				campaignListLbId.setVisible(false);
				getCampaignsByUser(userNameLbId.getSelectedItem().getValue(),sourceTypeLbId.getSelectedItem().getValue().toString());
				
				if(campaignListLbId.getItemCount() > 0)	campaignListLbId.setSelectedIndex(0);
				
				consolidatedRepLbId.setVisible(false);
			}
			
//			setDateValues();
			oldGetBitweenDatesBtnId();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}
	
	
	
	public void onSelct$sourceTypeLbId() {
		try {
			getCampaignBySource((String)sourceTypeLbId.getSelectedItem().getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	} // onSelct$sourceTypeLbId()
	
	
	
	public void getCampaignBySource(String sourceType) throws Exception {
		MessageUtil.clearMessage();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			if(userOrgLbId.getSelectedIndex() == 0 || userOrgLbId.getSelectedIndex() == 0) {
				emailFilterLbId.setDisabled(true);
				campaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
			}else{
				
				getCampaignBySource((String) userNameLbId.getSelectedItem().getValue(),sourceType);
			}
		}
		else {
			
			getCampaignBySource(userId,sourceType);
		
		}
	}//getCampaignBySource
	
	public void getCampaignBySource(String userId, String sourceType) {
		
		
		List<String> campList = campaignReportDao.getCampaignList(userId, sourceType);
		List totCampListFinal = new ArrayList();
		totCampListFinal.add("-- All --");
		totCampListFinal.add("-- Search --");
		totCampListFinal.addAll(campList);
		ListModel strset = new SimpleListModel(totCampListFinal);
		campaignListLbId.setDisabled(false);
		campaignListLbId.setModel(strset);
		if(campaignListLbId.getItemCount() > 0) campaignListLbId.setSelectedIndex(0);
		searchTbId.setVisible(false);
		
		
	}
	
	//added newly

	public void onSelect$emailFilterLbId() {
		if(emailFilterLbId.getSelectedIndex() == 0) {
			
			campaignListLbId.setVisible(false);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		if(emailFilterLbId.getSelectedIndex() == 1) {
			
			campaignListLbId.setVisible(false);
			searchTbId.setValue("Email Name...");
			searchTbId.setVisible(true);
		}else if(emailFilterLbId.getSelectedIndex() == 2) {
			searchTbId.setValue("");
			searchTbId.setVisible(false);
			if(campaignListLbId.getItemCount() == 0) {
				
				MessageUtil.setMessage("No campaigns found.", "color:red");
				return;
				
			}
			campaignListLbId.setVisible(true);
		}
		
	}//onSelect$emailFilterLbId()
	
	
public String orderby_colName="sentDate",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
    public void onClick$sortbyDateSent() {
    			orderby_colName = "sentDate";
    			desc2ascasc2desc();	
    			getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyEmailName() {
		orderby_colName = "campaignName";
		desc2ascasc2desc();
		getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
}
  /*  public void onClick$sortbyList() {
		orderby_colName = "crId";
		desc2ascasc2desc();
		getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
}
    public void onClick$sortbySent() {
		orderby_colName = "sourceType";
		desc2ascasc2desc();	
		getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
}
    public void onClick$sortbyOpens() {
    	orderby_colName = "createdDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyClicks() {
    	orderby_colName = "modifiedDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyUnsubscribed() {
    	orderby_colName = "createdDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyMarkedAsSpam() {
    	orderby_colName = "modifiedDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyDropped() {
    	orderby_colName = "campaignName";
    	desc2ascasc2desc();	
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyHardBounces() {
    	orderby_colName = "createdDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbySoftBounces() {
    	orderby_colName = "modifiedDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyBlockedBounces() {
		orderby_colName = "campaignName";
		desc2ascasc2desc();	
		getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyOtherBounces() {
    	orderby_colName = "modifiedDate";
    	desc2ascasc2desc();
    	getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbySourceType() {
		orderby_colName = "sourceType";
		desc2ascasc2desc();	
		getCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }*/
    
	
	
	public void onClick$resetAnchId() {
		//need to reset with the default values
		
		sourceTypeLbId.setSelectedIndex(0);
		if(!emailFilterLbId.isDisabled()){
			emailFilterLbId.setSelectedIndex(0);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		campaignListLbId.setVisible(false);
		/*fromDateboxId.setValue((Calendar)desktopScope.getAttribute("fromDate"));
		toDateboxId.setValue((Calendar)desktopScope.getAttribute("toDate"));	*/
		setDateValues();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			userOrgLbId.setSelectedIndex(0);
			onSelect$userOrgLbId();
//			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			
			
			consolidateMetricsToolbarBtnId.setDisabled(true);
		}
		
		consolidatedRepLbId.setVisible(false);
		consolidateMetricsToolbarBtnId.setLabel("View Consolidated Metrics");
		orderby_colName="sentDate";
		desc_Asc="desc";
		getCampaignReports(0, campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);		
		
		
		
	}//onclick$resetAnchId()
	
	
private int pageSize = 0;	
	
private Toolbarbutton consolidateMetricsToolbarBtnId;
	
	public void getCampaignReports(int startIndex, int count,String orderby_colName,String desc_Asc) {
		logger.debug("-- just entered --");
		MessageUtil.clearMessage();
		
		setTotalSizeToPagingElements();
		try{
			/*Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar serverToDateCal = toDateboxId.getServerValue();
			
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();
			
			logger.debug("client From :"+tempClientFromCal +", client To :"+tempClientToCal);
			
			//change the time for startDate and endDate in order to consider right from the 
			// starting time of startDate to ending time of endDate
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
					serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			
			serverToDateCal.set(Calendar.HOUR_OF_DAY, 
					23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(Calendar.MINUTE, 
					59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);
			
			if(serverToDateCal.compareTo(serverFromDateCal) < 0){
				MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
				return;
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug(" Selected Campaign : "+campaignListLbId.
						getItemAtIndex(campaignListLbId.getSelectedIndex()).getLabel());
				logger.debug(" From Date : "+serverFromDateCal +"- To Date : "+serverToDateCal);
			}
			
			//setting the parameters in desktopScope when page loads from back
			desktopScope.setAttribute("reportLbIdSelIndex",campaignListLbId.getSelectedIndex());
			desktopScope.setAttribute("fromDate",serverFromDateCal);
			desktopScope.setAttribute("toDate",serverToDateCal);
			*/
			
			List<CampaignReport> campReportList = null;
			
			if(isAdmin) {
				int userIndex = userOrgLbId.getSelectedIndex();
				if(userIndex != -1 ) {
					if(userIndex == 0 || userNameLbId.getSelectedIndex() == 0) {
						
						consolidateMetricsToolbarBtnId.setDisabled(true);
						
					}else {
						consolidateMetricsToolbarBtnId.setDisabled(false);
					}
					userId = userNameLbId.getSelectedItem().getValue();
				}
			}
			
			String tempSourceType=null;
			
			/*if(!((String)sourceTypeLbId.getSelectedItem().getValue()).contains("--All--")) {
				tempSourceType = (String)sourceTypeLbId.getSelectedItem().getValue();
			}else{
				tempSourceType = 
			}*/
			
			tempSourceType = (String)sourceTypeLbId.getSelectedItem().getValue();
			if(!emailFilterLbId.isDisabled()) {
			
				if(filter == 0) { // if all option for campaign name is selected
					
						campReportList = campaignReportDao.getAllReports(userId, fromDateCal.toString(),
								toDateCal.toString(),  tempSourceType, startIndex, count,orderby_colName,desc_Asc);
						
					
				} 
				else if(filter == 1) { // If Search option is selected .
	
					if(logger.isDebugEnabled()) {
						logger.debug(" Campaign name to be searched : " + searchTextStr);
					}
					
					/*if(searchTextStr.equals("") || searchTextStr.equals("Email Name...")) {
						MessageUtil.setMessage("Please provide text in the search box.", "color:red", "TOP");
						return;
					}*/
					campReportList = campaignReportDao.getReportsByCampaignName(searchTextStr, userId.toString(),
							fromDateCal.toString(), toDateCal.toString(), true, tempSourceType, startIndex, count,orderby_colName,desc_Asc);
				}else {
					if(campaignListLbId.getItemCount() > 0) {
						
						/*if(selectedItem == null) {
							MessageUtil.setMessage("Please select campaign.", "color:red", "TOP");
							return;
						}*/
						MessageUtil.clearMessage();
						campReportList = campaignReportDao.getReportsByCampaignName(
								selectedItem.getLabel(), userId, fromDateCal.toString(),
								toDateCal.toString(), false, tempSourceType, startIndex, count,orderby_colName,desc_Asc);
					
					}
				}
			}else{
				
				campReportList = campaignReportDao.findAsAdmin(fromDateCal.toString(),toDateCal.toString(),tempSourceType, startIndex, count,userId,orderby_colName,desc_Asc);
				
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("No of CampaignReports : "+campReportList.size());
			}
			/*if(campReportList.size() == 0) {
				MessageUtil.setMessage("No reports exist between the selected dates. ", "color:red;", "top");
				//msgLblId.setValue(" No Reports existed between the selected dates ");
				return;
			}else {
				msgLblId.setValue("");
			}*/
			
				createRows(campReportList);
			
			
		}catch(Exception e){
			logger.error("** Exception :", (Throwable)e);
		}
	
	}
	
	public void onClick$consolidateMetricsToolbarBtnId() {
		try {
			//logger.info("==============");
			if(!consolidatedRepLbId.isVisible()) {
			
				getConsolidatedReport();
				
				consolidateMetricsToolbarBtnId.setLabel("Hide Consolidated Metrics");
				consolidatedRepLbId.setVisible(true);
			}else{
				
				consolidatedRepLbId.setVisible(false);
				consolidateMetricsToolbarBtnId.setLabel("View Consolidated Metrics");
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}
	
	public void setTotalSizeToPagingElements() {
		
		String userId = null;
		String emailName = null;
		boolean isLike = false;
		//String searchStr = null;
		String sourceType = null;
				
		
		
		if(!emailFilterLbId.isDisabled()) {
			
			if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
				
				userId = userNameLbId.getSelectedItem().getValue();
				
			}//if
			else{
				
				userId = this.userId;
				
				
			}
			
			if(filter == 1) { 
				emailName = searchTextStr;
				isLike = true;
			}
				else if(filter == 2) {
					emailName = selectedItem.getLabel();
					isLike = false;
				}
				
			
			
		}
		else {
			userId = userNameLbId.getSelectedItem().getValue();
		}
		
		
		if(!sourceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("--All--")) {
			
			sourceType = sourceTypeLbId.getSelectedItem().getValue();
			
		}
		
		
		
		if(toDateCal.compareTo(fromDateCal) < 0){
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
			return;
		}
		
		/*if(logger.isDebugEnabled()) {
			logger.debug(" Selected Campaign : "+campaignListLbId.
					getItemAtIndex(campaignListLbId.getSelectedIndex()).getLabel());
			logger.debug(" From Date : "+serverFromDateCal +"- To Date : "+serverToDateCal);
		}*/
		
		//setting the parameters in desktopScope when page loads from back
		desktopScope.setAttribute("reportLbIdSelIndex",campaignListLbId.getSelectedIndex());
		desktopScope.setAttribute("fromDate",fromDateCal);
		desktopScope.setAttribute("toDate",toDateCal);
		
		
		int totalSize = campaignReportDao.getReportCount(userId, emailName, isLike, sourceType,
				fromDateCal.toString(), toDateCal.toString() );
		campRepListTopPagingId.setTotalSize(totalSize);
		campRepListBottomPagingId.setTotalSize(totalSize);
		
		
		
	}
	
	
	
	public void onPaging$campRepListTopPagingId(PagingEvent event) {
		
		//logger.info("----just entered ----onPaging$campRepListTopPagingId()");
		//Paging paging = (Paging)event.getTarget();
		
		int desiredPage = campRepListTopPagingId.getActivePage();
		campRepListBottomPagingId.setActivePage(desiredPage);
		
		
		
		//PagingEvent pagingEvent = (PagingEvent) event;
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getCampaignReports(ofs, campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);
		//redraw(ofs, (byte) event.getPageable().getPageSize());
		
		
	}
	
	public void onPaging$campRepListBottomPagingId(PagingEvent event) {
		
		//logger.info("----just entered ----onPaging$campRepListBottomPagingId()");
		int desiredPage = campRepListBottomPagingId.getActivePage();
		campRepListTopPagingId.setActivePage(desiredPage);
		
		
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getCampaignReports(ofs, campRepListBottomPagingId.getPageSize(),orderby_colName,desc_Asc);
		
		
	}
	
	
	
	
	public void getConsolidatedReport() throws Exception {
		try{
			
			logger.debug("---just entered---");
			MessageUtil.clearMessage();
			if(isAdmin) {
				
				int userIndex = userNameLbId.getSelectedIndex();
				if(userIndex != -1 ) {
					userId = userNameLbId.getSelectedItem().getValue();
				}
				
			}
			try{
				for(int i = consolidatedRepLbId.getItemCount(); i > 0 ; i--){
					consolidatedRepLbId.removeItemAt(i-1);
					
				}
			}catch(Exception e){
				
			}
			
			
			Listitem lItem = null;
			Listcell lCell = null;
			Long sent = null;
			List<Object[]> consolReport1 = campaignReportDao.getSentCampaignDetail(userId, null);
			
			if(consolReport1 == null){
				MessageUtil.setMessage("No reports to display.", "color:red", "TOP");
				return;
			}
			
			if(!fromPage.equalsIgnoreCase("admin/dashBoard")){//no need of this condition
			consolidatedRepLbId.setVisible(true);
			List<Object[]> consolReport2 = campaignReportDao.getCampaignsInLast3Months(userId, null);
			Object[] obj = consolReport1.get(0);
			
			
			sent = (Long)obj[0];
			lItem = new Listitem();
			lCell = new Listcell("Over all");
			lCell.setParent(lItem);
			if(sent == null){
				lCell = new Listcell("-");
				return;
			}else if(sent != null) {
				lCell = new Listcell(""+sent);
			}
			
			/*
			 * lbl = new Label(Utility.getPercentage(campaignReport.getOpens(),sent - campaignReport.getBounces(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getClicks(),campaignReport.getOpens(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getUnsubscribes(),campaignReport.getOpens(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getSpams(), sent - campaignReport.getBounces(),2));
			lbl.setParent(row);
			setBounceCate
			 */
			
			
			logger.info(" "+obj[0]+"  "+obj[1]+"  "+obj[2]+"  "+obj[3]+"  "+"  "+obj[4]+"   "+obj[5] );
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[1]),sent-((Long)obj[5]).longValue(),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[2]),((Long)obj[1]).longValue(),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[3]),((Long)obj[1]).longValue(),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[4]),sent-((Long)obj[5]).longValue(),2));
			lCell.setParent(lItem);
			//lbl = new Label(Utility.getPercentage(campaignReport.getSpams(), sent - campaignReport.getBounces(),2));
			//lCell = new Listcell(Utility.getPercentage(((Long)obj[1]),sent,2));
			
			//need to calculate the bouncecategories here itself.
			lItem = setConsolidatedBounceCategories(lItem, "Detail", sent);//over all
			
			
			
			
			lItem.setParent(consolidatedRepLbId);
			obj = consolReport2.get(0);
			if(obj.length>0) {
			
			sent = (Long)obj[0];
			lItem = new Listitem();
			lCell = new Listcell("Last 3 months");
			lCell.setParent(lItem);
			if(sent == null) {
				lCell = new Listcell("-");
				return;
			}else if(sent != null) {
				lCell = new Listcell(""+sent);
				
			}
			lCell.setParent(lItem);
			logger.info(" "+obj[0]+"  "+obj[1]+"  "+obj[2]+"  "+obj[3]+"  "+"  "+obj[4]+"   "+obj[5] );
			lCell = new Listcell(Utility.getPercentage(((Long)obj[1]),sent-((Long)obj[5]),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[2]),((Long)obj[1]),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[3]),((Long)obj[1]),2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long)obj[4]),sent-((Long)obj[5]),2));
			lCell.setParent(lItem);
			lItem = setConsolidatedBounceCategories(lItem, "InDetail", sent);//last 3months
			
			
			
			lItem.setParent(consolidatedRepLbId);
			}
		 }
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
			logger.error("Exception");
		}
		
	}
	
	
	
public Listitem setConsolidatedBounceCategories(Listitem item, String str, long sent ) {
		
		Listcell hardBounced = new Listcell("0");
		Listcell softBounced = new Listcell("0");
		Listcell droppedBounces = new Listcell("0");
		Listcell blockedBounces = new Listcell("0");
		Listcell otherBounces =  new Listcell("0");
		
		long hardBounceCount = 0;
		long softBounceCount = 0;
		
		
		List<Object[]> bounceCategoryList = null;
		
		if(str.equalsIgnoreCase("Detail")) {
			
			
			bounceCategoryList	=  bounceDao.getConsolidatedBounceCatByUser(userId);
			
			
		}else if(str.equalsIgnoreCase("InDetail")) {
			
			
			bounceCategoryList = bounceDao.getConslBounceCatFor3Months(userId);
			
			
		}
		
		//Long objLong = null;
		for (Object object : bounceCategoryList) {
			Object[] obj = (Object[])object;
			//logger.debug("Object[] : " + obj[0] + " | " + obj[1] + " | "  + obj[2]);
			
				
				if( ((String)obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
					//mFullLbl.setValue(obj[1] + "");
					softBounceCount += ((Long)obj[1]).longValue();
					softBounced.setLabel(Utility.getPercentage(softBounceCount, sent, 2)+"");
					
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
					
					
					blockedBounces.setLabel(Utility.getPercentage(((Long)obj[1]), sent, 2)+"");
				} else if( ((String)obj[0]).startsWith(BounceCategories.DROPPED)) {
					
					
					droppedBounces.setLabel(Utility.getPercentage(((Long)obj[1]), sent, 2)+"");
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.BOUNCED)) {
					//nonExistLbl.setValue(obj[1] + "");
					hardBounceCount += ((Long)obj[1]).longValue();
					hardBounced.setLabel(Utility.getPercentage(hardBounceCount, sent, 2)+"");
					
				} 
				
				else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
					
					
					otherBounces.setLabel(Utility.getPercentage(((Long)obj[1]), sent, 2)+"");
				} 
				
				
				
				
			
			
			
		} // for
		
		droppedBounces.setParent(item);
		hardBounced.setParent(item);
		softBounced.setParent(item);
		blockedBounces.setParent(item);
		otherBounces.setParent(item);
		
		
		
		
		
		
		return item;
		
		
		
		
	}//setConsolidatedBounceCategories
	
	
	
	
	
	
	
	
	public void onBlur$searchTbId() {
		if(searchTbId.getValue()!=null && searchTbId.getValue().trim().equals(""))
			searchTbId.setValue("Email Name...");
	}
	
	public void onFocus$searchTbId() {
		if(searchTbId.getValue()!=null && searchTbId.getValue().trim().equals("Email Name..."))
			searchTbId.setValue(null);
	}
	
	
	
	
	/**
	 * This is an onChange event for users Listbox <br/>
	 * This method populates the Campaigns Listbox based on selected user
	 * 
	 * @param userIdobj
	 */
	public void getCampaignsByUser(Object userIdobj, String sourceType)throws Exception {
		try {
			MessageUtil.clearMessage();
			List<String> campList = null;
			if(userIdobj == null) {
				emailFilterLbId.setDisabled(true);
				campaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
				
			}
			else{
				userId = (String)userIdobj;
				campList = campaignReportDao.getCampaignList(userId, sourceType);
				List totCampListFinal = new ArrayList();
				/*totCampListFinal.add("-- All --");
				totCampListFinal.add("-- Search --");*/
				totCampListFinal.addAll(campList);
				ListModel strset = new SimpleListModel(totCampListFinal);
				emailFilterLbId.setDisabled(false);
				campaignListLbId.setDisabled(false);
				campaignListLbId.setModel(strset);
				emailFilterLbId.setSelectedIndex(0);
				
				if(campaignListLbId.getItemCount() > 0)	campaignListLbId.setSelectedIndex(0);
				searchTbId.setValue("");
				searchTbId.setVisible(false);
				consolidateMetricsToolbarBtnId.setDisabled(false);
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	} // getCampaignsByUser
	
	/*public void onSelect$userListLbId() {
		try {
			
			sourceTypeLbId.setSelectedIndex(0);
			emailFilterLbId.setSelectedIndex(0);
			searchTbId.setVisible(false);
			searchTbId.setValue("");
			campaignListLbId.setVisible(false);
			getCampaignsByUser(userListLbId.getSelectedItem().getValue(),sourceTypeLbId.getSelectedItem().getValue().toString());
			
			if(campaignListLbId.getItemCount() > 0)	campaignListLbId.setSelectedIndex(0);
			setDateValues();
			
			consolidatedRepLbId.setVisible(false);
			
			onClick$getBitweenDatesBtnId();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}// onSelect$userListLbId
*/	
	public void onClick$getBitweenDatesBtnId() {
		
		if((emailFilterLbId.getSelectedIndex() == 1) && (searchTbId.getValue().equals("") || searchTbId.getValue().equals("Email Name..."))) { // If Search option is selected .
			
			
				MessageUtil.setMessage("Please provide text in the search box.", "color:red", "TOP");
				return;
			
			
		}else if( emailFilterLbId.getSelectedIndex() == 2 && campaignListLbId.getItemCount() > 0 && campaignListLbId.getSelectedItem() == null ) {
			
					MessageUtil.setMessage("Please select campaign.", "color:red", "TOP");
					return;
				
		}
		filter = emailFilterLbId.getSelectedIndex();
		searchTextStr = searchTbId.getValue();
		selectedItem = campaignListLbId.getSelectedItem();
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		oldGetBitweenDatesBtnId();
	}
		public void oldGetBitweenDatesBtnId() {
		try {
			if(isAdmin) {
				int userIndex = userOrgLbId.getSelectedIndex();
				if(userIndex != -1 ) {
					if(userIndex != 0 && userNameLbId.getItemCount() == 1) {
						return;
					}
				}
			}
			
			consolidatedRepLbId.setVisible(false);
			consolidateMetricsToolbarBtnId.setLabel("View Consolidated Metrics");
			
			getCampaignReports(0, campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
	}
	
	
	public void onSelect$pageSizeLbId() {
		
		pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		campRepListTopPagingId.setPageSize(pageSize);
		campRepListBottomPagingId.setPageSize(pageSize);
		getCampaignReports(campRepListTopPagingId.getActivePage()*pageSize, campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);
		
		
	}
	
	private void createRows(List<CampaignReport> campReportList){
		//MessageUtil.clearMessage();
		
		//categoriesList = bounceDao.getCategoryPercentageByUser(userId);
		StringBuffer crIds = new StringBuffer();//Constants.STRING_NILL;
		if(campReportList != null && campReportList.size() >0) { 
		for (CampaignReport campaignReport : campReportList) {
			
			
			if(crIds.length() > 0) crIds.append(Constants.DELIMETER_COMMA);
			
			crIds.append(campaignReport.getCrId());
		}
		
		categoriesList = bounceDao.getBounceCategories(crIds.toString());
		//categoriesList = bounceDao.getCategoryPercentageByUser(userId,null);
		}
		
		//logger.debug("Got Categories % Info : " + categoriesList);
		//reportGridId.setVisible( (campReportList.size() > 0) );
		
		
		
		reportGridId.setModel(new SimpleListModel(campReportList));
		
		reportGridId.setRowRenderer(new ReportGridRenderer());
		
		//reportGridId.setActivePage(0);
		
	}//createRows
	
	
	/**
	 * ReportGridRenderer is a row renderer class to populate the reports grid.
	 * @author venkateshgoudampalli
	 *
	 */
	public class ReportGridRenderer implements RowRenderer, EventListener {
		long sent = 0;
		Toolbarbutton tb = null;
		Calendar tempCal;
		CampaignReport campaignReport;
		Label lbl;
		String sourceType = null;
		
		@Override
		public void render(Row row, Object crObj, int arg2) throws Exception{
			campaignReport = (CampaignReport)crObj;
			
			sent = campaignReport.getSent();
			row.setValue(campaignReport);
			tempCal = campaignReport.getSentDate();
			
			tb = new Toolbarbutton(MyCalendar.calendarToString(tempCal,
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			tb.setStyle("color:blue;");
			tb.setAttribute("campaignReport", campaignReport);
			
			tb.setParent(row);
			
			
			//adding event listener to identify onClick and what to do when onClick happens
			tb.addEventListener("onClick", this);
			
			tb.setParent(row);
			lbl = new Label(campaignReport.getCampaignName());
			lbl.setParent(row);

			logger.info("campaignReport.getCrId()===>"+campaignReport.getCrId());
            String listName=getCampRepLists(campaignReport.getCrId());
			lbl = new Label(listName);
			lbl.setTooltiptext(listName);
			lbl.setParent(row);
			
			lbl = new Label(""+sent);
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getOpens(),sent - campaignReport.getBounces(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getClicks(),campaignReport.getOpens(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getUnsubscribes(),campaignReport.getOpens(),2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getSpams(), sent - campaignReport.getBounces(),2));
			lbl.setParent(row);
			setBounceCategories(campaignReport.getCrId(),row);
			
			/*lbl = new Label(Utility.getPercentage(campaignReport.getBounces(),sent,2));
			lbl.setParent(row);*/
			
			sourceType = campaignReport.getSourceType();
			if(sourceType != null) {
				if(sourceType.contains("_")){
					lbl = new Label(sourceType.substring(0, sourceType.indexOf("_")));
				}else {
					lbl = new Label(sourceType);
				}
			}else {
				lbl = new Label("One Off");
			}
			lbl.setParent(row);
		}
		
		
	/*	private String getCampRepLists(Long campRepId) {
			
			String listsName = "";
			
			CampReportListsDao campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
			
			CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
			if(tempCampRepLists!=null) {
			
				listsName = tempCampRepLists.getListsName();
				
			}
			return listsName;
			
		}*/
		
		
		private String getCampRepLists(Long campRepId) {

			String listsName = "";
			CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao) SpringUtil.getBean("campaignScheduleDao");
			CampReportListsDao campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
			CampaignSchedule campsch = campaignScheduleDao.findByCampRepId(campRepId);
			CampaignsDao campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
			if (campsch == null)
				return "--";
				Campaigns campaign = campaignsDao.findByCampaignId(campsch.getCampaignId());
				CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
			if (campaign.getListsType() != null && !campaign.getListsType().equalsIgnoreCase("Total")) {
				//listsName = "Segment of (" + getListNames(campaign) + ")";
				listsName = "Segment of ("+tempCampRepLists.getListsName()+ ")";
			}else if (campaign.getListsType() == null || campaign.getListsType().equalsIgnoreCase("Total")) {
				//	listsName = getListNames(campaign);
				if(tempCampRepLists!=null) {
					listsName = tempCampRepLists.getListsName();
				}
			}
			return listsName;

		}
	
	public String getListNames(Campaigns campaign) {
		String listNames = "";
		
		Set<MailingList> mailingLists= campaign.getMailingLists();
		logger.info("mailing lists size===>"+mailingLists.size());
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*private void setBounceCategories(Long crId, Row row) {
			Label mFullLbl = new Label("0");
			Label blockedLbl = new Label("0");
			Label nonExistLbl = new Label("0");
			Label undeliverLbl = new Label("0");
			Label othersLbl = new Label("0");
			Long objLong = null;
			for (Object object : categoriesList) {
				objLong = null;
				Object[] obj = (Object[])object;
				//logger.debug("Object[] : " + obj[0] + " | " + obj[1] + " | "  + obj[2]);
				try {
					objLong = (Long)obj[2];
				} catch (Exception e) {	logger.error(e);}
				
				if(objLong == null) {
					continue;
				}
				//logger.debug("Found : " + objLong + " | " + crId + " | " + (objLong.equals(crId)) + " | " + obj[0] + " | " + obj[1] + " | "  + obj[2]);
				if(!objLong.equals(crId)) {
					continue;
				}
				if( ((String)obj[0]).startsWith(BounceCategories.MAILBOX_FULL)) {
					mFullLbl.setValue(obj[1] + "");
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
					blockedLbl.setValue(obj[1] + "");
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.NON_EXISTENT_ADDRESS)) {
					nonExistLbl.setValue(obj[1] + "");
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.UNDELIVERABLE)) {
					undeliverLbl.setValue(obj[1] + "");
				} 
				else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
					othersLbl.setValue(obj[1] + "");
				} 
				
			} // for
			
			mFullLbl.setParent(row);
			blockedLbl.setParent(row);
			nonExistLbl.setParent(row);
			undeliverLbl.setParent(row);
			othersLbl.setParent(row);
		} //setBounceCategories
*/		
		private void setBounceCategories(Long crId, Row row) {
			
			
			Label hardBouncedLbl = new Label("0");
			Label softBouncedLbl= new Label("0");
			Label droppedLbl = new Label("0");
			//Label mFullLbl = new Label("0");
			Label blockedLbl = new Label("0");
			/*Label nonExistLbl = new Label("0");
			Label undeliverLbl = new Label("0");*/
			Label othersLbl = new Label("0");
			Long objLong = null;
			//long softBounceCount= 0;
			long dropped = 0;
			long hardBounceCount = 0;
			long softBounceCount = 0;
			for (Object object : categoriesList) {
				objLong = null;
				Object[] obj = (Object[])object;
				//logger.debug("Object[] : " + obj[0] + " | " + obj[1] + " | "  + obj[2]);
				try {
					objLong = (Long)obj[2];
				} catch (Exception e) {	logger.error(e);}
				
				if(objLong == null) {
					continue;
				}
				//logger.debug("Found : " + objLong + " | " + crId + " | " + (objLong.equals(crId)) + " | " + obj[0] + " | " + obj[1] + " | "  + obj[2]);
				if(!objLong.equals(crId)) {
					continue;
				}
				
					
					if( ((String)obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
						//mFullLbl.setValue(obj[1] + "");
						softBounceCount += ((Long)obj[1]).longValue();
						softBouncedLbl.setValue(Utility.getPercentage(softBounceCount, sent, 2)+"");
						
					} 
					else if( ((String)obj[0]).startsWith(BounceCategories.DROPPED)) {
						//mFullLbl.setValue(obj[1] + "");
						dropped += ((Long)obj[1]).longValue();
						droppedLbl.setValue(Utility.getPercentage(dropped, sent, 2)+"");
					} 
					else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
						
						
						blockedLbl.setValue(Utility.getPercentage(((Long)obj[1]), sent, 2)+"");
					} 
					else if( ((String)obj[0]).startsWith(BounceCategories.BOUNCED)) {
						//nonExistLbl.setValue(obj[1] + "");
						hardBounceCount += ((Long)obj[1]).longValue();
						hardBouncedLbl.setValue(Utility.getPercentage(hardBounceCount, sent, 2)+"");
						
					} 
					/*else if( ((String)obj[0]).startsWith(BounceCategories.UNDELIVERABLE)) {
						//undeliverLbl.setValue(obj[1] + "");
						hardBounceCount += ((Long)obj[1]).longValue();
						
					} */
					else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
						
						
						othersLbl.setValue(Utility.getPercentage(((Long)obj[1]), sent, 2)+"");
					} 
					
					
					
					
				
				
				
			} // for
			
			droppedLbl.setParent(row);
			hardBouncedLbl.setParent(row);
			softBouncedLbl.setParent(row);
			
			//mFullLbl.setParent(row);
			blockedLbl.setParent(row);
			//nonExistLbl.setParent(row);
			//undeliverLbl.setParent(row);
			othersLbl.setParent(row);
		} //setBounceCategories
		
		

		@Override
		public void onEvent(Event event) throws Exception {
			
			Toolbarbutton tb = (Toolbarbutton)event.getTarget();
			Session sessionScope = Sessions.getCurrent();
			sessionScope.removeAttribute("campaignReport");
			CampaignReport cr = (CampaignReport)tb.getAttribute("campaignReport");
			if(cr.getSent() == 0) {
				
				MessageUtil.setMessage("Detail report cannot be viewed as sent count is :0. ", "color:green;");
				return;
				
				
			}
			if(cr.getSent()!=0 || cr.getOpens()!=0 ||cr.getClicks()!=0 
					|| cr.getBounces()!=0 || cr.getUnsubscribes()!=0) {
				
				sessionScope.setAttribute("campaignReport",cr);
				Redirect.goTo(PageListEnum.REPORT_DETAILED_REPORT);
			}
			
		}
		
	}
	
	
	//********************************************************************************************************//
	
	/**
	 * triggers on click event of export csv button
	 */
	public void onClick$exportBtnId() {
		try {
			if(isAdmin) {
				if(userNameLbId.getSelectedIndex() != 0){
					
					anchorEvent(false);
					
					custExport.setVisible(true);
					custExport.doHighlighted();
				}
				else{
					
					MessageUtil.setMessage("Please select a user", "red");
				}
			}
			else {
				anchorEvent(false);
				
				custExport.setVisible(true);
				custExport.doHighlighted();
			}
			
				
		} catch (Exception e) {
			logger.error("Exception in onClick$exportBtnId  method ***",e);
		}
	}
	
public void onClick$selectAllAnchr$custExport() {
	
	anchorEvent(true);

}

public void onClick$clearAllAnchr$custExport() {
	
	anchorEvent(false);
}

public void anchorEvent(boolean flag) {
	List<Component> chkList = custExport$chkDivId.getChildren();
	Checkbox tempChk = null;
	for (int i = 0; i < chkList.size(); i++) {
		if(!(chkList.get(i) instanceof Checkbox)) continue;
		
		tempChk = (Checkbox)chkList.get(i);
		tempChk.setChecked(flag);
		
	} // for

}



public void onClick$selectFieldBtnId$custExport() {

	custExport.setVisible(false);
	List<Component> chkList = custExport$chkDivId.getChildren();

	int indexes[]=new int[chkList.size()-1];
	boolean allUrlsFlag=false;
	boolean checked=false;
	
	for(int i=0;i<chkList.size()-1;i++) {
		indexes[i]=-1;
	} // for
	

	Checkbox tempChk = null;
	
	
	for (int i = 0; i < chkList.size()-1; i++) {
		if(!(chkList.get(i) instanceof Checkbox)) continue;
		
		tempChk = (Checkbox)chkList.get(i);
		
		if(tempChk.isChecked()) {
			indexes[i]=0;
			checked=true;
		}
		
	} // for
	
	
	if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {
		
		 allUrlsFlag=true;
		 checked=true;
	}
	
	if(checked) {
		
		int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
		if(confirm==1){
			try{
				
				exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes, allUrlsFlag);
				
			}catch(Exception e){
				logger.error("Exception caught :: ",e);
			}
		}
		else{
			custExport.setVisible(true);
		}
		
	}
	else {
		
		MessageUtil.setMessage("Please select atleast one field", "red");
		custExport.setVisible(false);
	}
	
}
	
	
	/**
	 * exports data into a  file of  input extension type
	 * @param ext
	 * @throws Exception 
	 */
	public void exportCSV(String ext,int[] indexes,boolean urls) throws Exception {
		
		long starttime = System.currentTimeMillis();
		String innerSB = null;
		List<CampaignReport> list = null;
		long totalCampaigns = 0;
		boolean urlsOnly=true;
		
		/*******************************************************************************/

		/*Calendar serverFromDateCal = getFromDateCal();
		Calendar serverToDateCal = getToDateCal();
		logger.info("from and to date tostring "+serverFromDateCal.toString()+"   "+serverToDateCal);*/
		/*******************************************************************************/
		
		String query =  getCampaignReportsCsv(fromDateCal,toDateCal, indexes[3] == 0, urls);
		if( query == null)
		{
			return ;
		}
		
		//logger.info("query is ::::::::: "+query);
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query);
		
		long totalCount = jdbcResultsetHandler.totalRecordsSize();
		
		if(totalCount == 0) {
			MessageUtil.setMessage("Campaign reports are not available for selected time-period", "red");
			logger.error("no campaigns");
			return;
		}
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();
		
		StringBuffer crIdList = new StringBuffer();
		while(resultSet.next())
		{
			if(crIdList.length() > 0) crIdList.append(Constants.DELIMETER_COMMA);
			
			crIdList.append(resultSet.getLong("cr_id")); 
		}
		//crIdList.deleteCharAt(crIdList.length()-2);
		resultSet.beforeFirst();
		StringBuffer urlHeaderBuf = null;
		Set<String> headerUrls = null;
		List<Object[]> urlList = null;
		List<Object[]> campUrlList = null;
		if(urls) {
			urlList = clicksDao.getClickedUrls(crIdList); /*serverFromDateCal.toString(), serverToDateCal.toString(),*/
			/*List<String> idleUrls = clicksDao.getNotClickedUrls(crIdList);*/
			campUrlList = clicksDao.getCampUrls(crIdList.toString());
			
			headerUrls = new LinkedHashSet<String>();
			if(campUrlList!=null){
				
				for(Object[] obj : campUrlList){
					if(obj[1]!=null)
					headerUrls.add((String)obj[1]);
				}
				
				urlHeaderBuf = new StringBuffer();
				for(String currUrl: headerUrls) {
						
					if(urlHeaderBuf.length()>0) urlHeaderBuf.append(",");
					
					urlHeaderBuf.append("\"").append(currUrl).append("\"");
				}
				
				
			}
		}
		
		
		
		Map<Long, Long[]> bounceCatMap = getHardBounce(crIdList.toString());
		int urlColumn = 0;
		for(int i=0;i<indexes.length;i++) {
				
				if(indexes[i]==0) {
					urlColumn = urlColumn+1;
					urlsOnly = false;
				}
			}
			
			if( urlsOnly && urlHeaderBuf.length()==0) {
				
				MessageUtil.setMessage("Your campaigns within this time-frame did not contain any URLs. Please change the time-period or include more fields to generate report.", "red");
				return;
			}
			JdbcResultsetHandler exportJdbcResultsetHandler = null;
			String exportQuery = null;
			String usersParentDirectory = null;
			File downloadDir = null; String filePath = null; StringBuffer sb = null; ResultSet exportresResultSet = null;
			try {
		
				ext = ext.trim();
				//String Name = userName;
				usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
				downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );
				if(downloadDir.exists()){
					try {
						FileUtils.deleteDirectory(downloadDir);
					} catch (Exception e) {
						logger.error("Exception caught ",e);
					}
				}
				if(!downloadDir.exists()){
					downloadDir.mkdirs();
				}
				filePath = usersParentDirectory + "/" + userName + "/List/download/Email_Camp_Report_" + System.currentTimeMillis() + "." + ext;
				
				
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));	
				
				sb = new StringBuffer();
									
				StringBuffer header = new StringBuffer();
				StringBuffer fields = new StringBuffer("select id");
				for(int i=0;i<indexes.length;i++) {
						
					if(indexes[i]==0) {
							if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
							
						header.append("\"").append(Constants.emailCampaignHeaderFields[i]).append("\"");
						fields.append(", ").append(Constants.emailCampaignDbFields[i]);
						urlsOnly=false;
					}
				}// for
				if(urls){
					fields.append(", ");fields.append("urls");
					if(urlHeaderBuf !=null  ) {
						
						
						//for (String url : headerUrls) {
						if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
						header.append(urlHeaderBuf.toString());
						
						//}// for
					}
				}
				
				exportQuery = fields.toString();
				exportQuery += " from tempexportreport where 1=2 ";
				sb.append(header);
				//logger.debug("sb==="+sb.toString());
				sb.append("\r\n");
					
				bw.write(sb.toString());
				//System.gc();
				
				
				exportJdbcResultsetHandler = new JdbcResultsetHandler();
				exportJdbcResultsetHandler.executeStmt(exportQuery, true);
				exportresResultSet = exportJdbcResultsetHandler.getResultSet();
				
				logger.info("export query is :::: "+exportQuery);	
				/*Long pk=(long) 1;*/
				int increment = 0;
				double random = 0;
				while(resultSet.next())
				{
					random = Math.random();
					exportresResultSet.moveToInsertRow();
					increment ++;
					/*exportresResultSet.updateString("email_id", "dummyemail"+pk);
					exportresResultSet.updateString("email_status", "dummystauts");
					exportresResultSet.updateLong("cid", pk);
					pk++;*/
					
					long sentCount=resultSet.getLong("sent");
					int openCount=resultSet.getInt("opens");
					int clickCount=resultSet.getInt("clicks");
					int unsubCount=resultSet.getInt("unsubscribes");
					int spamCount=resultSet.getInt("spams");
					int bounceCount=resultSet.getInt("bounces");
					
					Long[] allBounces = bounceCatMap != null ? bounceCatMap.get(resultSet.getLong("cr_id")) : null;
					/*if(allBounces != null) {
						for (int i = 0; i < allBounces.length; i++) {
							
							logger.debug("bounces ===>"+allBounces[i]);
						}
					}*/
					exportresResultSet.updateString("id", userId+""+random+""+System.currentTimeMillis());
					
				if(indexes[0]==0)	{
					exportresResultSet.updateString("domain", resultSet.getString("campaign_name"));
				}
					
				if(indexes[1]==0) {
					Calendar cal = null;
					Timestamp t=resultSet.getTimestamp("sent_date");
					if(t!=null)
					{
						if(new Date(t.getTime()) != null) {
							cal = Calendar.getInstance();
							cal.setTime(t);
						}
						String sentDate= cal == null ? "" : MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone);
						exportresResultSet.updateString("first_name", sentDate);
					}
				}
					
				if(indexes[2]==0)	{
						exportresResultSet.updateString("mobile_status", resultSet.getString("subject"));
				}
				if(indexes[3]==0)	{
					exportresResultSet.updateString("subscription_type", resultSet.getString("list_name"));
				}
				if(indexes[4]==0)	{
					exportresResultSet.updateString("external_id", resultSet.getString("sent"));
				}
				if(indexes[5] == 0){
					exportresResultSet.updateString("udf10", Utility.getPercentage(openCount,sentCount - bounceCount,2));
				}
				if(indexes[6]==0){
					exportresResultSet.updateString("udf11", Utility.getPercentage(clickCount,openCount,2));
				}
				if(indexes[7]==0){
					exportresResultSet.updateString("udf12", Utility.getPercentage(unsubCount,openCount,2));
				}
				if(indexes[8]==0){
					exportresResultSet.updateString("udf13", Utility.getPercentage(spamCount, sentCount - bounceCount,2));
				}
				if(indexes[9]==0){
					exportresResultSet.updateString("udf14", Utility.getPercentage(allBounces != null && (Long)allBounces[0] != null ? allBounces[0] : 0,sentCount, 2));
				}
				if(indexes[10]==0){
					exportresResultSet.updateString("udf15", Utility.getPercentage(allBounces != null && (Long)allBounces[1] != null ? allBounces[1] : 0,sentCount, 2));
				}
				if(indexes[11]==0){
					exportresResultSet.updateString("home_store", Utility.getPercentage(allBounces != null && (Long)allBounces[2] != null ? allBounces[2] : 0,sentCount, 2));
				}
				if(indexes[12]==0){
					exportresResultSet.updateString("zip", Utility.getPercentage(allBounces != null && (Long)allBounces[3] != null ? allBounces[3] : 0,sentCount, 2));
				}
				if(indexes[13]==0){
					exportresResultSet.updateString("mobile_phone", Utility.getPercentage(allBounces != null && (Long)allBounces[4] != null ? allBounces[4] : 0,sentCount, 2));
				}
				if(indexes[14]==0){
					exportresResultSet.updateInt("udf1", openCount);
				}
				/*{"Email Name","Date Sent","Subject ","List(s)","Sent","Opens %","Clicks %","Unsubs %","Marked as Spam %","Dropped %","Hard Bounces %","Soft Bounce %","Blocked Bounce %",
				 "Other Bounce %","Unique Opens ","Unique Clicks ","Unsubs ","Marked as Spam ","Dropped","Hard Bounces ","Soft Bounce","Blocked Bounce","Other Bounce"};
				cr_id, campaign_name, sent_date, content, subject, sent, opens, clicks, unsubscribes, bounces, status, user_id, place_holders_str, source_type, spams, configured, preference_count
				{"company_name","label","subject ","configured_ph_val_str","from_name","to_name","reply_email","status","draft_status","editor_type","schedule_type","web_link_text","web_link_urlText",
				"permission_remainder_text","address_one","address_two","city","state","country","phone","content_type","place_holders_type","addrs_type"};*/
				if(indexes[15]==0){
					exportresResultSet.updateInt("udf2", clickCount);
				}
				if(indexes[16]==0){
					exportresResultSet.updateInt("udf3", unsubCount);
				}
				if(indexes[17]==0){
					exportresResultSet.updateInt("udf4", spamCount);
				}
				if(indexes[18]==0){
					exportresResultSet.updateString("udf5", String.valueOf(allBounces != null && (Long)allBounces[0] != null ? allBounces[0] : 0));
				}
				if(indexes[19]==0){
					exportresResultSet.updateString("udf6", String.valueOf(allBounces != null && (Long)allBounces[1] != null ? allBounces[1] : 0));
				}
				if(indexes[20]==0){
					exportresResultSet.updateString("udf7", String.valueOf(allBounces != null && (Long)allBounces[2] != null ? allBounces[2] : 0));
				}
				if(indexes[21]==0){
					exportresResultSet.updateString("udf8", String.valueOf(allBounces != null && (Long)allBounces[3] != null ? allBounces[3] : 0));
				}
				if(indexes[22]==0){
					exportresResultSet.updateString("udf9", String.valueOf(allBounces != null && (Long)allBounces[4] != null ? allBounces[4] : 0));
				}
				Long crId = resultSet.getLong("cr_id");
				//logger.info("cr_id is ::: "+crId);
				//urlList!=null && urlList.size()>0 && urls
				if(urls) {
				innerSB = Constants.STRING_NILL;
					
				for(String eachUrl : headerUrls)
				{
					String urlValue = Constants.STRING_NILL;
					for(Object[] row: urlList)
					{
						String url = (String) row[1];
						if(url == null)continue;	
						if(crId.equals(row[0]) && eachUrl.equals(url) )
						{
						urlValue = row[2].toString();
						break;
						}
					}
					if(urlValue.length()==0)
					{
						
						
					for(Object[] eachCampaignUrl : campUrlList)
					{
						if(eachCampaignUrl[1]==null)continue;
						if(crId.equals(eachCampaignUrl[0]) && eachUrl.equals(eachCampaignUrl[1]))
						{
							urlValue = "0";
							break;
						}
					}
					}
					
					if(innerSB.length() > 0) innerSB = innerSB.concat(";::;");
					innerSB = innerSB.concat(urlValue.length() != 0 ? urlValue  : "N/A"); 
				}
					
					
					/*for(Object[] row : urlList)
					{
						boolean isCrIdFound = false;
						String urlValue = Constants.STRING_NILL;
						//logger.info("inner cr_id is :::: "+row[0]);
						if(crId.equals(row[0]) )
						{
							isCrIdFound = true;
							String url = (String) row[1];
							if(url == null)continue;
							
							for (String eachUrl : headerUrls) {
								
								if(innerSB.length() > 0) innerSB = innerSB.concat(";::;");
								
								if(eachUrl.equals(url)){
									urlValue = row[2] != null ? row[2].toString():"0";
									break;
								}else{
									urlValue = urlValue.concat("N/A");
								}
							}
							if(innerSB.length() > 0) innerSB = innerSB.concat(";::;");
							innerSB = innerSB.concat(urlValue.length() != 0 ? urlValue  : "N/A"); 
						
						}
						if(isCrIdFound)
						{
							isCrIdFound = false;
						if(innerSB.length() > 0) innerSB = innerSB.concat(";::;");
						innerSB = innerSB.concat(urlValue.length() != 0 ? urlValue  : "N/A"); 
						}
					}
								
				}*/
					//logger.info("url values "+innerSB);
					exportresResultSet.updateString("urls", innerSB);
				}
				//logger.debug("before inserting a row ???"+exportresResultSet.getRow());
				exportresResultSet.insertRow();
				exportresResultSet.moveToCurrentRow();
				}//while
				
				
				
				OCCSVWriter csvWriter = null;
				try{
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(exportresResultSet, false, 1, urlColumn+2);
					
				}
				catch(Exception e){
					logger.info("Exception while writing into file ", e);
				}finally{
					
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
					/*Thread.sleep(5000);*/
					/*exportJdbcResultsetHandler = new JdbcResultsetHandler();
					exportJdbcResultsetHandler.executeUpdate("delete from tempexportreport");*/
					/*exportresResultSet.beforeFirst();
					while(exportresResultSet.next())
					{
						exportresResultSet.deleteRow();
					}*/
				}
				
				
				Filedownload.save(file, "text/csv");
				logger.debug("exited");
				
				logger.debug("total time taken :: ReportController :: exportCSV   "
						+ (System.currentTimeMillis() - starttime));
				
			} catch (Exception e) {
				logger.error("** Exception : " , e);
			}finally{
				exportJdbcResultsetHandler.rollback();
				if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy();
				if(exportJdbcResultsetHandler != null)exportJdbcResultsetHandler.destroy();
				query = null; exportQuery = null; jdbcResultsetHandler = null; exportJdbcResultsetHandler = null;
				userName = null; usersParentDirectory = null;
				downloadDir = null; filePath = null; sb = null;  exportresResultSet = null;
				//System.gc();
			}
			
		
		
	}

	/**
	 * retrieves campaign report list name based on cam report id
	 * @param campRepId
	 * @return
	 */
	/*public String getCampRepLists(Long campRepId) {
		
		long starttime = System.currentTimeMillis();
		String listsName = "";
		
		CampReportListsDao campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
		
		CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
		if(tempCampRepLists!=null) {
		
			listsName = tempCampRepLists.getListsName();
			
		}
		
		
		return listsName;
		
	}*/
	/*
	 * retrieves records campaign reports to be exported in file 
	 */
		public String getCampaignReportsCsv(Calendar serverFromDateCal,Calendar serverToDateCal,boolean isListChecked, boolean isUrlChecked) {
		
		long starttime=System.currentTimeMillis();
		
		String query = null;
		try{
			
			if(serverToDateCal.compareTo(serverFromDateCal) < 0){
				
				MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
				
				logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
						+ (System.currentTimeMillis() - starttime));
				
				return query;
			}
			
			String tempSourceType=null;
			
			
			tempSourceType = (String)sourceTypeLbId.getSelectedItem().getValue();
			String checkCon = "";
			if(userId != null) {
	 			checkCon += " c.user_id IN (" + userId + ") ";
	 		} 
	 		
			if(!emailFilterLbId.isDisabled()) {
			
				if(filter == 0) { // if all option for campaign name is selected
						 		
				 							
				} 
				else if(filter == 1) { // If Search option is selected .
	
					if(logger.isDebugEnabled()) {
						logger.debug(" Campaign name to be searched : " + searchTextStr);
					}
					/*String searchTextStr = searchTbId.getValue().trim();
					if(searchTextStr.equals("") || searchTextStr.equals("Email Name...")) {
						MessageUtil.setMessage("Please provide text in the search box.", "color:red", "TOP");
						
						logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
								+ (System.currentTimeMillis() - starttime));
						return query;
					}*/
					/*campReportList = campaignReportDao.getReportsByCampaignName(searchTbId.getValue(), userId,
							serverFromDateCal.toString(), serverToDateCal.toString(), true, tempSourceType, startIndex, count);*/
					if(searchTextStr != null) {
			    		checkCon += " AND  c.campaign_name like '%"+ StringEscapeUtils.escapeSql(searchTextStr) +"%' ";
			    	}
					
				}else {
					if(campaignListLbId.getItemCount() > 0) {
						/*Listitem selectedItem = campaignListLbId.getSelectedItem();*/
						if(selectedItem == null) {
							MessageUtil.setMessage("Please select campaign.", "color:red", "TOP");
							logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
									+ (System.currentTimeMillis() - starttime));
							return query;
						}
						MessageUtil.clearMessage();
						/*campReportList = campaignReportDao.getReportsByCampaignName(
								selectedItem.getLabel(), userId, serverFromDateCal.toString(),
								serverToDateCal.toString(), false, tempSourceType, startIndex, count);*/
						if(selectedItem.getLabel() != null) {
				    		checkCon += " AND c.campaign_name ='" +StringEscapeUtils.escapeSql(selectedItem.getLabel()) + "'" ;
				    	}
					
					}
				}
			}/*else{
				
				campReportList = campaignReportDao.findAsAdmin(serverFromDateCal.toString(),serverToDateCal.toString(),tempSourceType, startIndex, count,userId);
				
			}*/
			
			
	 		if(serverFromDateCal.toString() != null && serverToDateCal.toString() != null) {
	 			checkCon += " AND  c.sent_date between '" + serverFromDateCal.toString() + "' AND '" + serverToDateCal.toString() + "' ";
	 		}
	 		if(tempSourceType != null && !tempSourceType.equalsIgnoreCase("--All--")) {
	 			checkCon += " AND c.source_type like '"+tempSourceType+"%'  ";
	 		}
	    	String listsSubqry = Constants.STRING_NILL;
	    	String selectClause = Constants.STRING_NILL;
	    	
	    	/*String bouncesQry = ", ( SELECT category, COUNT(b.bounce_id) as catCount," +
	    			" c.cr_id FROM campaign_report c , bounces b " +
	    			"WHERE c.cr_id = b.cr_id " +
	    			"AND "+checkCon+" GROUP BY c.cr_id, b.category) bc";
	    	
	    	checkCon += " AND c.cr_id=bc.cr_id";
	    	selectClause += " ,bc.category, bc.catCount ";
	    	*/
	    	if(isListChecked) {
	    		
	    		listsSubqry += ", campreports_lists cl " ;
	    		checkCon += " AND c.cr_id=cl.cr_id";
	    		selectClause += " ,cl.list_name ";
	    	}
	 		
	 		//if(!isListChecked)	{
 			query = " SELECT c.cr_id, c.sent, c.opens, c.clicks, c.unsubscribes, c.spams, c.bounces, c.sent_date, "
	 					+ "c.campaign_name, c.subject " +selectClause+ " FROM campaign_report c "+
	 							listsSubqry+ " WHERE " + checkCon + " AND c.status ='sent' ORDER BY  c.sent_date DESC";
	 		//}else {
	 			/*query = "select c.cr_id, c.sent, c.opens, c.clicks, c.unsubscribes, c.spams, c.bounces, c.sent_date, "
	 					+ "c.campaign_name, c.subject, cl.list_name FROM campaign_report c left outer join "
	 					+ "campreports_lists cl on c.cr_id=cl.cr_id  WHERE " + checkCon + " c.status ='sent' ORDER BY  c.sent_date DESC";
	 		}*/
 			
 			logger.debug("final qry ::"+query);
			return query;
			
			
		}catch(Exception e){
			logger.error("** Exception :", (Throwable)e);
		}
		
		logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
				+ (System.currentTimeMillis() - starttime));
		
		return query;
	
	}
	public Long[] computeBouncePerc(Object[] obj, Long[] categoryArr) {
		
		long hardBounceCount = 0;
		long softBounceCount = 0;
		long blockedBounceCount = 0;
		long droppedBounceCount = 0;
		long otherBounceCount = 0;
			
		
		if( ((String)obj[0]).startsWith(BounceCategories.NON_EXISTENT_ADDRESS)) {
			
			hardBounceCount += ((Long)obj[1]).longValue();
			
		} 
		else if( ((String)obj[0]).startsWith(BounceCategories.UNDELIVERABLE)) {
			
				hardBounceCount += ((Long)obj[1]).longValue();
				
		} 
		else if( ((String)obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
			//mFullLbl.setValue(obj[1] + "");
			softBounceCount += ((Long)obj[1]).longValue();
			
		} 
		else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
			
			blockedBounceCount = ((Long)obj[1]).longValue();
			
		} else if( ((String)obj[0]).startsWith(BounceCategories.DROPPED)) {
			
			droppedBounceCount = ((Long)obj[1]).longValue();
			
		} 
		else if( ((String)obj[0]).startsWith(BounceCategories.BOUNCED)) {
			//nonExistLbl.setValue(obj[1] + "");
			hardBounceCount += ((Long)obj[1]).longValue();
			
			
		} 
		
		else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
			
			otherBounceCount = ((Long)obj[1]).longValue();
			
		} 	
		
		Long bounces[]= {droppedBounceCount,hardBounceCount,softBounceCount,blockedBounceCount,otherBounceCount};
		if(categoryArr != null) {
			
			for (int i = 0; i < categoryArr.length; i++) {
				
				if(categoryArr[i] > bounces[i]) {
					bounces[i] = categoryArr[i];
				}
				
			}
			
		}
		
		
		return bounces;
}


 	
	/**
	 * retrieves count of hard bounces
	 * @param crId
	 * @param campaignReport
	 * @return
	 */
	public Map<Long, Long[]> getHardBounce(String  crIdList) {
		
	  long starttime = System.currentTimeMillis();
		Long objLong = null;
		
		/*if(categoriesList == null)
		{*/
		List<Object[]> bounceCatList = null;
		try {
			bounceCatList = bounceDao.getBounceCategories(crIdList);
			//categoriesList = bounceDao.getCategoryPercentageByUser(userId,null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception",e1);
		}
		if(bounceCatList == null) return null;
		 Map<Long, Long[]> retMap = new HashMap<Long, Long[]>();
		//}
		for (Object[] object : bounceCatList) {
			objLong = null;
			Object[] obj = (Object[])object;
			
			/*try {
				objLong = (Long)obj[2];
			} catch (Exception e) {	logger.error(e);}
			
			if(objLong == null) {
				continue;
			}
			
			if(!objLong.equals(crId)) {
				continue;
			}*/
			Long[] bounces = (Long[])retMap.get((Long)obj[2]);
			
			if(bounces == null){
				bounces = computeBouncePerc(obj, null);
				
			}else {
				bounces = computeBouncePerc(obj, bounces);
				
			}
			retMap.put((Long)obj[2], bounces);
		}
			/*( ((String)obj[0]).startsWith(BounceCategories.NON_EXISTENT_ADDRESS)) {
					
					hardBounceCount += ((Long)obj[1]).longValue();
					
				} 
			else if( ((String)obj[0]).startsWith(BounceCategories.UNDELIVERABLE)) {
				
					hardBounceCount += ((Long)obj[1]).longValue();
					
				} 
			else if( ((String)obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
				//mFullLbl.setValue(obj[1] + "");
				softBounceCount += ((Long)obj[1]).longValue();
				
			} 
			else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
				
				blockedBounceCount = ((Long)obj[1]).longValue();
				
			} else if( ((String)obj[0]).startsWith(BounceCategories.DROPPED)) {
				
				droppedBounceCount = ((Long)obj[1]).longValue();
				
			} 
			else if( ((String)obj[0]).startsWith(BounceCategories.BOUNCED)) {
				//nonExistLbl.setValue(obj[1] + "");
				hardBounceCount += ((Long)obj[1]).longValue();
				
				
			} 
			
			else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
				
				otherBounceCount = ((Long)obj[1]).longValue();
				
			} 	
				
				
		}
		
		long bounces[]={droppedBounceCount,hardBounceCount,softBounceCount,blockedBounceCount,otherBounceCount};
		*/
		return retMap;
		
	}
	
	public long getTotalSize(Calendar serverFromDateCal,Calendar serverToDateCal) {
		
		long starttime = System.currentTimeMillis();
		String userId = null;
		String emailName = null;
		boolean isLike = false;
		String sourceType = null;
		long totalSize=0;
		
		
		
		
		if(!emailFilterLbId.isDisabled()) {
			
			if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
				
				
				userId = userNameLbId.getSelectedItem().getValue();
				
			}//if
			else{
				
				userId = this.userId;
				
				
			}
			
			if(filter != 0) { 
					isLike = true;
					if(searchTbId.isVisible()) {
						
						emailName = searchTextStr;
					}
					else if(campaignListLbId.isVisible()) {
						
						if(campaignListLbId.getItemCount() > 0)	emailName = selectedItem.getLabel();
					}
				
			}
			
		}
		
		if(!sourceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("--All--")) {
			
			sourceType = sourceTypeLbId.getSelectedItem().getValue();
			
		}
		
		
		if(serverToDateCal.compareTo(serverFromDateCal) < 0){
			
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
			
			logger.debug("total time taken :: ReportController :: getTotalSize   "
					+ (System.currentTimeMillis() - starttime));
			
			return totalSize;
		}
		
		
		totalSize = campaignReportDao.getReportCount(userId, emailName, isLike, sourceType,
						serverFromDateCal.toString(), serverToDateCal.toString() );
		
		logger.debug("total time taken :: ReportController :: getTotalSize   "
				+ (System.currentTimeMillis() - starttime));
		
		return totalSize;
	}
	
	/**
	 * retrieves count for input url
	 * @param urlList
	 * @param objlist
	 * @return
	 */
	public int[] getUrlMappingCount(List<String> campaignUrls,List<String> urlList,List<Object[]> objlist)
	{
		int urlCount[]=new int[urlList.size()];
		for(int i=0;i<urlList.size();i++) {
			urlCount[i]=-1;
		}// for
		
		if(campaignUrls==null) {
			return urlCount;
		}
		else
		{
			// inputs count of clicks for each clicked url
				for(Object[] eachObj : objlist){
					int i=0;
					for(String url : urlList){
						if(url.equals(eachObj[0].toString())){
							urlCount[i]=Integer.parseInt(eachObj[1].toString().trim());
							break;
						}
						i++;
					}
					
				}// for
				
				// inputs 0 for each not clicked url
				for(String  campaignUrl: campaignUrls) {
					campaignUrl = campaignUrl.replaceAll("%26","&");
					campaignUrl = campaignUrl.replaceAll("%3D","=");
					campaignUrl = campaignUrl.replaceAll("%2F","/");
					campaignUrl = campaignUrl.replaceAll("%3A",":");
					campaignUrl = campaignUrl.replace("%3F","?");
					int i=0;
					for(String url : urlList){
						if(url.equals(campaignUrl) && urlCount[i]==-1){
							urlCount[i]=0;
							break;
						}
						i++;
					}// for
					
				}// for
				return urlCount;
		}
	}
	
	public Calendar getFromDateCal()  {
		
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, serverFromDateCal.get(Calendar.HOUR_OF_DAY)- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, serverFromDateCal.get(Calendar.MINUTE)- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		return serverFromDateCal;
	}
	
	public Calendar getToDateCal()  {
		
		Calendar serverToDateCal = toDateboxId.getServerValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 59 + serverToDateCal.get(Calendar.MINUTE)	- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		return serverToDateCal;
		
	}

	
}
