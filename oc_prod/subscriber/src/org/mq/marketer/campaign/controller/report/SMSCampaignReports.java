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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSCampReportLists;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.report.ReportController.ReportGridRenderer;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.BounceDao;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
//import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.SMSCampReportListsDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.BounceCategories;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
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

public class SMSCampaignReports extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Listbox smsCampaignListLbId;
	private Label userOrgLblId,userNameLblId;
	private Label  msgLblId;
	private Listbox userOrgLbId,userNameLbId;
	private Grid reportGridId;
	private Button getBitweenDatesBtnId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Textbox searchTbId;
	private Div custExport$chkDivId;
	private Combobox exportCbId;
	//private BounceDao bounceDao;
	//private ClicksDao clicksDao ;
	private SMSBouncesDao smsBouncesDao;
	protected CampaignReportDao campaignReportDao = null;
	private int filter;
	private Listbox campaignListLbId;
	Listitem selectedItem ;
	private Listbox userListLbId;

	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReport smsCampaignReport;
	private SMSCampaigns smsCampaign;
	Desktop desktopScope = null;
	Session sessionScope = null;
	private TimeZone clientTimeZone;
	String userName = null;
	String userId = null;
	private boolean isAdmin;
	private String fromPage = null;
	private static final String allStr = "--All--";
	private Calendar fromDateCal, toDateCal ;

	private Listbox pageSizeLbId;
	private int pageSize = 0;
	private Window custExport;
	private String searchTextStr = Constants.STRING_NILL;
	
	private Paging campRepListTopPagingId, campRepListBottomPagingId;
	private List<Object[]> retList = null;
	
	
	public SMSCampaignReports() {

		try {
			logger.debug("----just entered in smsCampaignreport------");
			smsCampaignReportDao = (SMSCampaignReportDao) SpringUtil
					.getBean("smsCampaignReportDao");
			desktopScope = Executions.getCurrent().getDesktop();
			sessionScope = Sessions.getCurrent();
			userName = GetUser.getUserName();
			userId = GetUser.getUserObj().getUserId().toString();
			clientTimeZone = (TimeZone) sessionScope
					.getAttribute("clientTimeZone");

			//clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			//bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
			smsBouncesDao = (SMSBouncesDao)SpringUtil.getBean("smsBouncesDao");
			
			logger.debug("isAdmin :" + sessionScope.getAttribute("isAdmin"));

			isAdmin = (Boolean) sessionScope.getAttribute("isAdmin");

			fromPage = (String) sessionScope.getAttribute("fromPage");
			smsCampaign = (SMSCampaigns) sessionScope
					.getAttribute("smsCampaign");
			logger.debug("smsCampaign is===>" + smsCampaign);
		} catch (Exception e) {
			logger.error("Exception", e);
			logger.error("Exception ::" , e);
		}

	}

	private MyRowRenderer renderer = new MyRowRenderer();

	@Override
	public void doAfterCompose(Component comp) {
		try {

			super.doAfterCompose(comp);

			logger.debug("-----just entered----" + fromPage);

			/*
			 * if(fromPage.equalsIgnoreCase("campaign/ViewSMSCampaigns")){
			 * logger.info("from viewSmsCampaigns");
			 * 
			 * getReportByCampaignName(smsCampaign.getSmsCampaignName(),reportGridId
			 * ,msgLblId); }
			 */

			String style = "font-weight:bold;font-size:15px;color:#313031;"
					+ "font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("SMS Campaign Reports", "", style, true);

			pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
					.getLabel());
			defaultSettings();

		//	getSmsCampaignReports(0, pageSize,orderby_colName,desc_Asc);
			logger.debug("=====done with reports module=====");
			try {
				
				Components.removeAllChildren(custExport$chkDivId);
				for (int i = 0; i < Constants.smsCampaignHeaderFields.length; i++) {
					Checkbox tempChk = new Checkbox(Constants.smsCampaignHeaderFields[i]);
					tempChk.setSclass("custCheck");
					tempChk.setParent(custExport$chkDivId);
					tempChk.setStyle("width:230px");
				} // for
				
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
			
	     } catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception while rendering the page", e);
		}
	}
	
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
    			getSmsCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbySMSName() {
		orderby_colName = "smsCampaignName";
		desc2ascasc2desc();
		getSmsCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
}

	public void onClick$resetAnchId() {
		// need to reset with the default values

		//APP-3939
		 Redirect.goTo(PageListEnum.EMPTY);
	     Redirect.goTo(PageListEnum.REPORT_SMS_CAMPAIGN_REPORTS);
		/*sourceTypeLbId.setSelectedIndex(0);
		if (!emailFilterLbId.isDisabled()) {
			emailFilterLbId.setSelectedIndex(0);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		smsCampaignListLbId.setVisible(false);
		
		setDateValues();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			userOrgLbId.setSelectedIndex(0);
			onSelect$userOrgLbId();
//			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
		}
		
		orderby_colName="sentDate";
		desc_Asc="desc";
		getSmsCampaignReports(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		campRepListTopPagingId.setActivePage(0);
		campRepListBottomPagingId.setActivePage(0);*/

	}// onclick$resetAnchId()

	 
	
public void onPaging$campRepListTopPagingId(PagingEvent event) {
	
		
		int desiredPage = campRepListTopPagingId.getActivePage();
		campRepListBottomPagingId.setActivePage(desiredPage);
		
		
		
		//PagingEvent pagingEvent = (PagingEvent) event;
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getSmsCampaignReports(ofs, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		//redraw(ofs, (byte) event.getPageable().getPageSize());
		
		
	}
	
	public void onPaging$campRepListBottomPagingId(PagingEvent event) {
		
		//logger.info("----just entered ----onPaging$campRepListBottomPagingId()");
		int desiredPage = campRepListBottomPagingId.getActivePage();
		campRepListTopPagingId.setActivePage(desiredPage);
		
		
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getSmsCampaignReports(ofs, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		
		
	}
	
	
	
	private void defaultSettings() {
		try {
			logger.debug("Is Admin :" + isAdmin);
			
         if (isAdmin) {
				logger.debug("is Admin ?" + isAdmin);
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

			reportGridId.setRowRenderer(renderer);

			String isBack = (String) desktopScope
					.getAttribute("isReportResBack");

			if (isBack != null) {

				if (isBack.equals("true")) {
					Object reportLbIdSelIndexObj = desktopScope
							.getAttribute("reportLbIdSelIndex");
					if (reportLbIdSelIndexObj != null) {
						int reportLbIdSelIndex = ((Integer) reportLbIdSelIndexObj)
								.intValue();
						smsCampaignListLbId
								.setSelectedIndex(reportLbIdSelIndex);
					} else
						smsCampaignListLbId.setSelectedIndex(0);
					fromDateboxId.setValue((Calendar) desktopScope
							.getAttribute("fromDate"));
					toDateboxId.setValue((Calendar) desktopScope
							.getAttribute("toDate"));
				}
			} else {

				//setDateValues();
			}

			getCampaignsByUser(userId, (String) sourceTypeLbId
					.getSelectedItem().getValue());
			int num = smsCampaignListLbId.getItemCount();
			logger.debug("number of campaigns" + num);
			if (num > 0) {
				smsCampaignListLbId.setSelectedIndex(0);
			} else {
				return;
			}
			// getCampaignReports();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
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
		Components.removeAllChildren(userNameLbId);
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		if(userOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			emailFilterLbId.setDisabled(true);
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			smsCampaignListLbId.setDisabled(true);
			searchTbId.setVisible(false);
			onClick$getBitweenDatesBtnId();
			
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); //getPrimaryUsersByOrg((Long)userOrgLbId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			smsCampaignListLbId.setDisabled(true);
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
		smsCampaignListLbId.setDisabled(true);
		searchTbId.setVisible(false);
		onClick$getBitweenDatesBtnId();
	}
	
	public void onSelect$userNameLbId() {
		
		try {
			if(userNameLbId.getSelectedIndex() == 0){
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				emailFilterLbId.setDisabled(true);
				smsCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
				smsCampaignListLbId.setVisible(false);
			}
			else {
				emailFilterLbId.setDisabled(false);
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				searchTbId.setVisible(false);
				searchTbId.setValue("");
				smsCampaignListLbId.setVisible(false);
				getCampaignsByUser(userNameLbId.getSelectedItem().getValue(),sourceTypeLbId.getSelectedItem().getValue().toString());
				
				if(smsCampaignListLbId.getItemCount() > 0)	smsCampaignListLbId.setSelectedIndex(0);
				
				smsCampaignListLbId.setVisible(false);
			}
			
//			setDateValues();
			onClick$getBitweenDatesBtnId();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}
	//********************************************************************************************************//
	
		/**
		 * triggers on click event of export csv button
		 */
		
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
						else if(smsCampaignListLbId.isVisible()) {
							
							if(smsCampaignListLbId.getItemCount() > 0)	emailName = selectedItem.getLabel();
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
			
			
			totalSize = smsCampaignReportDao.getReportCount(userId, emailName, isLike, sourceType,
							serverFromDateCal.toString(), serverToDateCal.toString() );
			
			logger.debug("total time taken :: ReportController :: getTotalSize   "
					+ (System.currentTimeMillis() - starttime));
			
			return totalSize;
		}
		/*
		 * retrieves records campaign reports to be exported in file 
		 */
		
		/**
		 * exports data into a  file of  input extension type
		 * @param ext
		 * @throws Exception 
		 */
public void exportCSV(String ext,int[] indexes) throws Exception {
			
			long starttime = System.currentTimeMillis();
			
			fromDateCal = getFromDateCal();
			toDateCal = getToDateCal();
			String query =  getCampaignReportsCsv(fromDateCal,toDateCal, isAdmin);
			smscontain=false;
			smsname=false;
			if( query == null)
            return;			
			logger.info("query is ::::::::: "+query);
			JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			
			long totalCount = jdbcResultsetHandler.totalRecordsSize();
			logger.info("totalCount is ::::::::: "+totalCount);
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
				
				crIdList.append(resultSet.getLong("sms_cr_id")); 
			}
			resultSet.beforeFirst();
			 JdbcResultsetHandler exportJdbcResultsetHandler = null;
				String exportQuery = null;
				String usersParentDirectory = null;
				File downloadDir = null; String filePath = null; StringBuffer sb = null; ResultSet exportresResultSet = null;
				try {
			
					ext = ext.trim();
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
					filePath = usersParentDirectory + "/" + userName + "/List/download/SMS_Camp_Report_" + System.currentTimeMillis() + "." + ext;
					
					
					File file = new File(filePath);
					BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));	
					
					sb = new StringBuffer();
										
					StringBuffer header = new StringBuffer();
					StringBuffer fields = new StringBuffer("select id,email_id,first_name,last_name,email_status,address_one,address_two,subscription_type ");
				     for(int i=0;i<indexes.length;i++) {
							
						if(indexes[i]==0) {
								if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
								
							header.append("\"").append(Constants.smsCampaignHeaderFields[i]).append("\"");
							//urlsOnly=false;
						}
					}// for
					
					
					exportQuery = fields.toString();
					exportQuery += " from tempexportreport where 1=2 ";
					sb.append(header);
					sb.append("\r\n");
						
					bw.write(sb.toString());
					//System.gc();
					
					logger.info("export query is :::: "+exportQuery);	
					
					exportJdbcResultsetHandler = new JdbcResultsetHandler();
					exportJdbcResultsetHandler.executeStmt(exportQuery, true);
					exportresResultSet = exportJdbcResultsetHandler.getResultSet();
					
					
					Long pk=(long) 1;
					
					//double random = 0;
					
					while(resultSet.next())
					{
						int i = 0;
						//random = Math.random();
						exportresResultSet.moveToInsertRow();
						exportresResultSet.updateString("id", pk.toString());
						Long Sent = resultSet.getLong("sent");  
					    Long deliveredCount = findDeliveredCount(resultSet.getLong("sms_cr_id"));
					    
					 	    
					    
						Long boun=resultSet.getLong("bounces");
						Long clks=resultSet.getLong("clicks");
						String Bounces=Utility.getPercentage(boun,Sent,2);
						String Clicks=Utility.getPercentage(clks,deliveredCount, 2);
					   pk++;
					 //The name are not valid for update string, was done as it is. So did not modify.
						String arr[]= {"email_id","first_name","last_name","email_status","address_one","address_two","subscription_type"};
						Map<String,Integer> arr2=new HashMap<String,Integer>(); 
					   if(indexes[0]==0) {
						Calendar cal = null;
						Timestamp t=resultSet.getTimestamp("sent_date");
					
						if(t!=null)
						{
							if(new Date(t.getTime()) != null) {
								cal = Calendar.getInstance();
								cal.setTime(t);
							}
							String sentDate= cal == null ? "" : MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone);
							exportresResultSet.updateString(arr[i], sentDate);
						}
						i++;
					   }
					
					if(indexes[1]==0)	{
							exportresResultSet.updateString(arr[i], resultSet.getString("sms_campaign_name"));
							i++;
					}
					if(indexes[2]==0)	{
						Long cridStr= resultSet.getLong("sms_cr_id");
					exportresResultSet.updateString(arr[i],getSmsCampRepLists(cridStr));
					i++;
					}
					if(indexes[3]==0)	{
						exportresResultSet.updateString(arr[i], resultSet.getString("sent"));
						i++;
					}
					if(indexes[4] == 0){
						exportresResultSet.updateString(arr[i],Clicks);
						i++;
					}
					if(indexes[5]==0)	{
						exportresResultSet.updateString(arr[i],Bounces);
						i++;
					}
					if(indexes[6]==0){
						String sourceType =resultSet.getString("source_type");
						if (sourceType != null) {
							if (sourceType.contains("_")) {
								sourceType= sourceType.substring(0,sourceType.indexOf("_"));
							} 
						} else {
							sourceType="One Off";
						}						
					exportresResultSet.updateString(arr[i], sourceType);
					}
					exportresResultSet.insertRow();
					exportresResultSet.moveToCurrentRow();
					}//while
					
					
					
					OCCSVWriter csvWriter = null;
					try{
						csvWriter = new OCCSVWriter(bw);
						csvWriter.writeAll(exportresResultSet, false, 1);
						
					}
					catch(Exception e){
						logger.info("Exception while writing into file ", e);
					}finally{
						
						bw.flush();
						csvWriter.flush();
						bw.close();
						csvWriter.close();
						
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
				
			
	private Toolbarbutton consolidateMetricsToolbarBtnId;
			

		/**
		 * retrieves campaign report list name based on cam report id
		 * @param campRepId
		 * @return
		 */
		public String getCampRepLists(Long campRepId) {
			
			long starttime = System.currentTimeMillis();
			String listsName = "";
			
			CampReportListsDao campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
			
			CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
			if(tempCampRepLists!=null) {
			
				listsName = tempCampRepLists.getListsName();
				
			}
			
			
			return listsName;
			
		}
		/*
		 * retrieves records campaign reports to be exported in file 
		 */
			public String getCampaignReportsCsv(Calendar serverFromDateCal,Calendar serverToDateCal,boolean isListChecked) {
			
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
		 			checkCon += "user_id IN (" + userId + ") ";
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
					    		checkCon += " AND  sms_campaign_name like '%"+ searchTextStr +"%' ";
					    	}
							
						}else {
							if(smsCampaignListLbId.getItemCount() > 0) {
								/*Listitem selectedItem = smsCampaignListLbId.getSelectedItem();*/
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
						    		checkCon += " AND sms_campaign_name ='" + selectedItem.getLabel() + "'" ;
						    	}
							
							}
						}
					}/*else{
						
						campReportList = campaignReportDao.findAsAdmin(serverFromDateCal.toString(),serverToDateCal.toString(),tempSourceType, startIndex, count,userId);
						
					}*/
				 
					
	        	   logger.info("entering smscontain value"+smscontain);
	        	   logger.info("entering smsname"+smsname);

			 		if(serverFromDateCal.toString() != null && serverToDateCal.toString() != null) {
			 			checkCon += " AND  sent_date between '" + serverFromDateCal.toString() + "' AND '" + serverToDateCal.toString() + "' ";
			 		}
		    	if(tempSourceType!=null && !tempSourceType.equalsIgnoreCase("--All--")){
		    		checkCon +=" AND source_type like '"+tempSourceType+"%'";
		    	}
	           if(isListChecked) {
	        	   logger.info("entering isListChecked"+isListChecked);
		    		checkCon += " AND sms_campaign_name like     '%"+searchTbId.getValue()+"%'" ;
		    	}
		    		
		    	if(smscontain) {
		    		
		        	   logger.info("entering smscontain"+smscontain);
		    		checkCon += " AND sms_campaign_name like     '%"+searchTbId.getValue()+"%'" ;

		    	}
	           
		    	if(smsname) {
		    		
		        	   logger.info("entering smsname"+smsname);

	           		checkCon += " AND sms_campaign_name like     '%"+smsCampaignListLbId.getSelectedItem().getValue()+"%'" ;

		    	}
	           		//checkCon += " AND sms_campaign_name like     '%"+smsCampaignListLbId.getSelectedItem().getValue()+"%'" ;

		    	//}
		 		
		 		if(serverFromDateCal != null && serverToDateCal != null) {
		 			checkCon += " AND sent_date between '" + serverFromDateCal + "' AND '" + serverToDateCal + "' ";
		 		}
		    	
		 		String queryStr = " select sent_date,sms_campaign_name,sms_cr_id,sent,clicks,bounces,source_type FROM sms_campaign_report WHERE " + checkCon + " AND status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY sent_date desc";
		 	
		 		
	 			logger.debug("final qry ::"+queryStr);
				return queryStr;
				
				
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
		public Map<Long, Long[]> getHardBounce(String crIdList) {
			
		  long starttime = System.currentTimeMillis();
			Long objLong = null;
			
			/*if(categoriesList == null)
			{*/
			List<Object[]> bounceCatList = null;
			try {
			
				 bounceCatList = smsBouncesDao.getAllBounceCategories(crIdList);
			
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
				
				
				Long[] bounces = (Long[])retMap.get((Long)obj[1]);
				
				if(bounces == null){
					bounces = computeBouncePerc(obj, null);
					
				}else {
					bounces = computeBouncePerc(obj, bounces);
					
				}
				retMap.put((Long)obj[1], bounces);
			}
				
			return retMap;
			
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

		int indexes[]=new int[chkList.size()];
		//boolean allUrlsFlag=false;
		boolean checked=false;
		
		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} // for
		

		Checkbox tempChk = null;
		
		
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			
			if(tempChk.isChecked()) {
				indexes[i]=0;
				checked=true;
			}
			
		} // for
		
		
		if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {
			
			 //allUrlsFlag=true;
			 checked=true;
		}
		
		if(checked) {
			
			int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){
				try{
					
					exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);
					
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
		

	
	
	
	
	
	
	public void onSelect$smsCampaignListLbId() {


		

	}

	
	public void setSearchDefaultContent() {
		if (searchTbId.getValue() != null
				&& searchTbId.getValue().trim().equals("SMS Name..."))
			searchTbId.setValue("");
		}

	public void onBlur$searchTbId() {

		setSearchDefaultContent();

	}

	public void onFocus$searchTbId() {
		setSearchDefaultContent();

	}

	private Listbox sourceTypeLbId;

	public void onSelect$sourceTypeLbId() {
		MessageUtil.clearMessage();
		
		String sourceType = (String) sourceTypeLbId.getSelectedItem()
				.getValue();
		// getSmsCampaignsBySource((Long)userListLbId.getSelectedItem().getValue(),sourceType);
		logger.debug("sourceType is======>" + sourceType);
		// userId = (Long)userListLbId.getSelectedItem().getValue();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			if(userOrgLbId.getSelectedIndex() == 0 || userOrgLbId.getSelectedIndex() == 0) {
				emailFilterLbId.setDisabled(true);
				smsCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
			}else{
				
				getSmsCampaignsBySource((String) userNameLbId.getSelectedItem().getValue(),sourceType);
			}
		}
		else {
			
			getSmsCampaignsBySource(userId,sourceType);
		
		}
	}

	public void getSmsCampaignsBySource(String userId, String sourceType) {

		List<String> smsCampList = smsCampaignReportDao.getSMSCampaignList(
				userId, sourceType);
		List<String> totCampListFinal = new ArrayList<String>();
		totCampListFinal.add("-- All --");
		totCampListFinal.add("-- Search --");

		totCampListFinal.addAll(smsCampList);
		ListModel strset = new SimpleListModel(totCampListFinal);
		smsCampaignListLbId.setModel(strset);
		smsCampaignListLbId.setSelectedIndex(0);
		smsCampaignListLbId.setDisabled(false);
		searchTbId.setVisible(false);

	}

	/*
	 * public void getCampaignsByUser(Object userIdobj, String sourceType) { try
	 * { MessageUtil.clearMessage(); if(userIdobj == null) {
	 * smsCampaignListLbId.setDisabled(true); searchTbId.setVisible(false);
	 * 
	 * }else{ userId = (Long)userIdobj; List<String> campList =
	 * smsCampaignReportDao.getSMSCampaignList(userId, sourceType);
	 * 
	 * List<String> totCampListFinal = new ArrayList<String>();
	 * 
	 * totCampListFinal.add("-- All --"); totCampListFinal.add("-- Search --");
	 * totCampListFinal.addAll(campList); ListModel strset = new
	 * SimpleListModel(totCampListFinal); smsCampaignListLbId.setModel(strset);
	 * smsCampaignListLbId.setSelectedIndex(0);
	 * smsCampaignListLbId.setDisabled(false); } } catch (Exception e) {
	 * logger.error("Exception : ", e); } } // getCampaignsByUser
	 */
	/**
	 * This is an onChange event for users Listbox <br/>
	 * This method populates the Campaigns Listbox based on selected user
	 * 
	 * @param userIdobj
	 */
	boolean getcampain=false;
	public void getCampaignsByUser(Object userIdobj, String sourceType)
			throws Exception {
		try {
			
			logger.info("entering getcampains by user");
			MessageUtil.clearMessage();
			List<String> campList = null;
			if (userIdobj == null) {
				emailFilterLbId.setDisabled(true);
				smsCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);

			} else {
				userId = (String) userIdobj;
				campList = smsCampaignReportDao.getSMSCampaignList(userId,
						sourceType);
				List totCampListFinal = new ArrayList();
				/*
				 * totCampListFinal.add("-- All --");
				 * totCampListFinal.add("-- Search --");
				 */
				totCampListFinal.addAll(campList);
				ListModel strset = new SimpleListModel(totCampListFinal);
				emailFilterLbId.setDisabled(false);
				smsCampaignListLbId.setDisabled(false);
				smsCampaignListLbId.setModel(strset);
				emailFilterLbId.setSelectedIndex(0);

				if (smsCampaignListLbId.getItemCount() > 0) {
					smsCampaignListLbId.setSelectedIndex(0);
				searchTbId.setValue("");
				searchTbId.setVisible(false);
				}else {
					if (smsCampaignListLbId.getItemCount() == 0) {
					
										getcampain=true;
										MessageUtil.setMessage("No campaigns found.", "color:red");
										return;
								}
				}
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	} // getCampaignsByUser

	public void onClick$getBitweenDatesBtnId() {
		if(isAdmin) {
			int userIndex = userOrgLbId.getSelectedIndex();
			if(userIndex != -1 ) {
				if(userIndex != 0 && userNameLbId.getItemCount() == 1) {
					return;
				}
			}
		}
		
		getSmsCampaignReports(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		campRepListTopPagingId.setActivePage(0);
		campRepListBottomPagingId.setActivePage(0);
	}

	public void setTotalSizeToPagingElements() {

		String userId = null;
		String emailName = null;
		boolean isLike = false;
		// String searchStr = null;
		String sourceType = null;

		if (!emailFilterLbId.isDisabled()) {

			if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
				
				userId = userNameLbId.getSelectedItem().getValue();
				
			}//if
			else {

				userId = this.userId;

			}

			if (emailFilterLbId.getSelectedIndex() != 0) {
				isLike = true;
				if (searchTbId.isVisible())
					emailName = searchTbId.getValue();
				else if (smsCampaignListLbId.isVisible()) {
					if (smsCampaignListLbId.getItemCount() > 0)	emailName = smsCampaignListLbId.getSelectedItem().getLabel();
				}

			}

		}
		else {
			userId = userNameLbId.getSelectedItem().getValue();
		}

		if (!sourceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("--All--")) {

			sourceType = sourceTypeLbId.getSelectedItem().getValue();

		}
		if (this.fromDateboxId.getValue() == null || this.toDateboxId.getValue() == null) {
	            MessageUtil.setMessage("Please select from date and to date", "color:red", "TOP");
	            return;
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
			return;
		}

		/*
		 * if(logger.isDebugEnabled()) {
		 * logger.debug(" Selected Campaign : "+campaignListLbId.
		 * getItemAtIndex(campaignListLbId.getSelectedIndex()).getLabel());
		 * logger.debug(" From Date : "+serverFromDateCal
		 * +"- To Date : "+serverToDateCal); }
		 */

		// setting the parameters in desktopScope when page loads from back
		desktopScope.setAttribute("reportLbIdSelIndex",
				smsCampaignListLbId.getSelectedIndex());
		desktopScope.setAttribute("fromDate", serverFromDateCal);
		desktopScope.setAttribute("toDate", serverToDateCal);

		int totalSize = smsCampaignReportDao.getReportCount(userId, emailName,
				isLike, sourceType, serverFromDateCal.toString(),
				serverToDateCal.toString());

		campRepListTopPagingId.setTotalSize(totalSize);
		campRepListBottomPagingId.setTotalSize(totalSize);

	}

	public void getSmsCampaignReports(int startIndex, int count,String orderby_colName,String desc_Asc) {
		logger.debug("-- just entered --");
		MessageUtil.clearMessage();

		setTotalSizeToPagingElements();

		try {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar serverToDateCal = toDateboxId.getServerValue();

			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();

			logger.debug("client From :" + tempClientFromCal + ", client To :"
					+ tempClientToCal);

			// change the time for startDate and endDate in order to consider
			// right from the
			// starting time of startDate to ending time of endDate
			serverFromDateCal.set(Calendar.HOUR_OF_DAY,
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
				MessageUtil.setMessage(
						"'To' date must be later than 'From' date.",
						"color:red", "TOP");
				return;
			}

			/*
			 * if(logger.isDebugEnabled()) {
			 * logger.debug(" Selected Campaign : "+campaignListLbId.
			 * getItemAtIndex(campaignListLbId.getSelectedIndex()).getLabel());
			 * logger.debug(" From Date : "+serverFromDateCal
			 * +"- To Date : "+serverToDateCal); }
			 */

			// setting the parameters in desktopScope when page loads from back
			desktopScope.setAttribute("reportLbIdSelIndex",
					smsCampaignListLbId.getSelectedIndex());
			desktopScope.setAttribute("fromDate", serverFromDateCal);
			desktopScope.setAttribute("toDate", serverToDateCal);

			// If Search option is selected .
			List<SMSCampaignReport> smsCampReportList = null;

			

			if(isAdmin) {
				int userIndex = userOrgLbId.getSelectedIndex();
				
				if(userIndex != -1 ) {
						userId = userNameLbId.getSelectedItem().getValue();
				}//if
				
			}
			
			String tempSourceType = null;

			

			tempSourceType = (String) sourceTypeLbId.getSelectedItem()
					.getValue();
			if (!emailFilterLbId.isDisabled()) {

				if (emailFilterLbId.getSelectedIndex() == 0) {  // if all option for campaign name is selected

					smsCampReportList = smsCampaignReportDao.getAllReports(
							userId, serverFromDateCal.toString(),
							serverToDateCal.toString(), tempSourceType,
							startIndex, count,orderby_colName,desc_Asc);

				} else if (emailFilterLbId.getSelectedIndex() == 1) { // If Search option is selected .

					if (logger.isDebugEnabled()) {
						logger.debug(" Campaign name to be searched : "
								+ searchTbId.getValue());
					}
					String searchTextStr = searchTbId.getValue().trim();
					if (searchTextStr.equals("")
							|| searchTextStr.equals("Email Name...")) {
						MessageUtil.setMessage(
								"Please provide text in the search box.",
								"color:red", "TOP");
						return;
					}
					smsCampReportList = smsCampaignReportDao
							.getReportsByCampaignName(searchTbId.getValue(),
									userId, serverFromDateCal.toString(),
									serverToDateCal.toString(), true,
									tempSourceType, startIndex, count,orderby_colName,desc_Asc);
				} else {
					if (smsCampaignListLbId.getItemCount() > 0) {
						Listitem selectedItem = smsCampaignListLbId
								.getSelectedItem();
						if (selectedItem == null) {
							MessageUtil.setMessage("Please select campaign.",
									"color:red", "TOP");
							return;
						}
						MessageUtil.clearMessage();
						smsCampReportList = smsCampaignReportDao
								.getReportsByCampaignName(
										selectedItem.getLabel(), userId,
										serverFromDateCal.toString(),
										serverToDateCal.toString(), false,
										tempSourceType, startIndex, count,orderby_colName,desc_Asc);

					}
				}
			} else {

				smsCampReportList = smsCampaignReportDao.findAllAsAdmin(
						serverFromDateCal.toString(),
						serverToDateCal.toString(), tempSourceType, startIndex,
						count, userId,orderby_colName,desc_Asc);

			}

			if (logger.isDebugEnabled()) {
				logger.debug("No of CampaignReports : "
						+ smsCampReportList.size());
			}
			
			getCampaignList();
			createRows(smsCampReportList);

		} catch (Exception e) {
			logger.error("** Exception :", (Throwable) e);
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


	public void setDateValues() {

		Calendar cal = MyCalendar.getNewCalendar();
	//	toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		logger.debug("FromDate (server) :" + cal);
	//	fromDateboxId.setValue(cal);

		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
	}

	private Listbox emailFilterLbId;
	boolean smscontain;
	boolean smsname;

	public void onSelect$emailFilterLbId() {
		if (emailFilterLbId.getSelectedIndex() == 0) {

     	   logger.info("entering index 0");

			smscontain=false;
			smsname=false;
			smsCampaignListLbId.setVisible(false);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		if (emailFilterLbId.getSelectedIndex() == 1) {

	     	logger.info("entering index 1");

			smscontain=true;
			smsname=false;

			smsCampaignListLbId.setVisible(false);
			searchTbId.setValue("SMS Name...");
			searchTbId.setVisible(true);
		} else if (emailFilterLbId.getSelectedIndex() == 2) {
			
	     	logger.info("entering index 2");

			
			smsname=true;
			smscontain=false;

		
			searchTbId.setValue("");
			searchTbId.setVisible(false);
			//smsCampaignListLbId.setVisible(true);
		
			if(getcampain) {
			if (smsCampaignListLbId.getItemCount() == 0) {

				MessageUtil.setMessage("No campaigns found.", "color:red");
				return;

			}}
			getcampain=false;

			smsCampaignListLbId.setVisible(true);
		}
		

	}// onSelect$emailFilterLbId()

	public void onSelect$pageSizeLbId() {

		pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		campRepListTopPagingId.setPageSize(pageSize);
		campRepListBottomPagingId.setPageSize(pageSize);
		getSmsCampaignReports(
				campRepListTopPagingId.getActivePage() * pageSize,
				campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);

	}

	
	public List categoriesList = null;

	private void createRows(List<SMSCampaignReport> smsCampReportList) {

		try {
			
			renderer.setDeliveredSMSCRList(null);
			if(smsCampReportList != null && smsCampReportList.size() > 0 ) {
				campRepListTopPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				campRepListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				String cridsStr = Constants.STRING_NILL;
				for (SMSCampaignReport smsCampaignReport : smsCampReportList) {
					
					if(!cridsStr.isEmpty()) cridsStr += ",";
					
					cridsStr += smsCampaignReport.getSmsCrId().longValue();
					
					
				}//for
				if(!cridsStr.isEmpty()) {
					
					List<Object[]> retList = smsCampaignReportDao.findDeliveredCount(cridsStr);
					logger.debug("retList "+retList.size());
					if(retList != null) renderer.setDeliveredSMSCRList(retList);
					
				}//if
				
				
				
			}//if
			
			reportGridId.setModel(new SimpleListModel(smsCampReportList));
			
			
			
			reportGridId.setRowRenderer(renderer);
			//reportGridId.setActivePage(0);
		} catch (Exception e) {
			logger.error("Exception while creating the rows", e);
			logger.error("Exception ::" , e);
		}

	}// createRows

	/*private String getSmsCampRepLists(Long smsCampRepId) {

		String listsName = "";

		SMSCampReportListsDao smsCampReportListsDao = (SMSCampReportListsDao) SpringUtil
				.getBean("smsCampReportListsDao");

		SMSCampReportLists tempSmsCampRepLists = smsCampReportListsDao
				.findBySmsCampReportId(smsCampRepId);
		if (tempSmsCampRepLists != null) {

			listsName = tempSmsCampRepLists.getListsName();

		}
		return listsName;

	}*/
	
	
private String getSmsCampRepLists(Long smsCampRepId) {
			
	        String listName = "";
	        
	        SMSCampaignScheduleDao smscampaignScheduleDao=(SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
	        
	        
	        SMSCampaignSchedule smscampsh=smscampaignScheduleDao.findBySmsCampRepId(smsCampRepId);
	        
	        if(smscampsh==null)return "--";
			logger.info("smscampsch.getSmsCampaignId()===>"+smscampsh.getSmsCampaignId());
	      
	        SMSCampaignsDao smsCampaignsDao=(SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
	        
	        SMSCampaigns smsCampaign = smsCampaignsDao.findByCampaignId(smscampsh.getSmsCampaignId());
	        
	        if(smsCampaign.getListType() != null && !smsCampaign.getListType().equalsIgnoreCase("Total")){
				listName = "Segment of ("+smsCampaign.getListNames()+")";
				SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
				String segRuleIds = smsCampaign.getListType().split(""+Constants.DELIMETER_COLON)[1];
				
				List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
				if(segmenRules!=null) listName = "Segment of ("+getListNames(smsCampaign)+")";
				else listName="--";
				
			}else if (smsCampaign.getListType() != null || smsCampaign.getListType().equalsIgnoreCase("Total")){
				listName = getListNames(smsCampaign);
				
			}
			return listName;
			
			
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

	
	
	
	
	

	private final class MyRowRenderer implements RowRenderer, EventListener {
		private List<Object[]> deliveredSMSCRList = null;
		public  void setDeliveredSMSCRList(List<Object[]> deliveredSMSCRList) {
			
			this.deliveredSMSCRList = deliveredSMSCRList;
		}
		public MyRowRenderer() {
			super();
		}

		@Override
		public void render(Row row, Object obj, int arg2) {

			try {
				SMSCampaignReport smsCampReport = null;
				long sent = 0;
				Calendar tempCal;
				Toolbarbutton tb = null;
				Label lbl;
				String sourceType = null;
				if (obj instanceof SMSCampaignReport) {

					smsCampReport = (SMSCampaignReport) obj;
					sent = smsCampReport.getSent();
					row.setValue(smsCampReport);
					tempCal = smsCampReport.getSentDate();

					tb = new Toolbarbutton(MyCalendar.calendarToString(tempCal,
							MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					tb.setStyle("color:blue;");
					tb.setAttribute("smsCampaignReport", smsCampReport);

					tb.setParent(row);

					// adding event listener to identify onClick and what to do
					// when onClick happens
					tb.addEventListener("onClick", this);

					tb.setParent(row);
					lbl = new Label(smsCampReport.getSmsCampaignName());
					lbl.setParent(row);

					lbl = new Label(getSmsCampRepLists(smsCampReport.getSmsCrId()));
					lbl.setParent(row);

					lbl = new Label("" + sent);
					lbl.setParent(row);
					
					int deliveredCount = 0;
					if(deliveredSMSCRList != null) {
						
						for (Object[] objectArr : deliveredSMSCRList) {
							
							if(objectArr[0] == null || !(objectArr[0] instanceof SMSCampaignReport)) continue;
							
							if(((SMSCampaignReport)objectArr[0]).getSmsCrId().longValue() == smsCampReport.getSmsCrId()) {
								
								deliveredCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : deliveredCount);
								logger.debug("deliveredCount=====>"+deliveredCount);
								break;
							}
							
							
						}
						
						
					}
					
					lbl = new Label(Utility.getPercentage(smsCampReport.getClicks(),deliveredCount, 2));
					lbl.setParent(row);
					
					/*lbl = new Label(Utility.getPercentage(
							smsCampReport.getOpens(),
							sent - smsCampReport.getBounces(), 2));
					lbl.setParent(row);
					lbl = new Label(Utility.getPercentage(
							smsCampReport.getClicks(),
							smsCampReport.getOpens(), 2));
					lbl.setParent(row);*/
					/*lbl = new Label(Utility.getPercentage(
							smsCampReport.getUnsubscribes(),
							smsCampReport.getOpens(), 2));
					lbl.setParent(row);*/
					lbl = new Label(Utility.getPercentage(
							smsCampReport.getBounces(), sent, 2));
					lbl.setParent(row);
					// setBounceCategories(smsCampReport.getSmsCrId(),row);
					sourceType = smsCampReport.getSourceType();
					if (sourceType != null) {
						if (sourceType.contains("_")) {

							lbl = new Label(sourceType.substring(0,
									sourceType.indexOf("_")));
						} else {

							lbl = new Label(sourceType);
						}
					} else {
						lbl = new Label("One Off");
					}
					lbl.setParent(row);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}

		}

		@Override
		public void onEvent(Event evt) {
			try {
				logger.debug("----just entered to handle the event-----");

				Toolbarbutton tb = (Toolbarbutton) evt.getTarget();
				Session sessionScope = Sessions.getCurrent();
				sessionScope.removeAttribute("smsCampaignReport");
				SMSCampaignReport smsCampReport = (SMSCampaignReport) tb
						.getAttribute("smsCampaignReport");
				
				if(smsCampReport.getSent() == 0) {
					
					MessageUtil.setMessage("SMS detail report cannot be viewed as sent count is :0. ", "color:green;");
					return;
					
					
				}
				
				
				if (smsCampReport.getSent() != 0
						|| smsCampReport.getOpens() != 0
						|| smsCampReport.getClicks() != 0
						|| smsCampReport.getBounces() != 0
						|| smsCampReport.getUnsubscribes() != 0) {

					sessionScope.setAttribute("smsCampaignReport",
							smsCampReport);
					Redirect.goTo(PageListEnum.REPORT_DETAILED_SMS_REPORTS);

				}
			} catch (RuntimeException e) {
				logger.error("Exception **", e);
			}

		}// onEvent()

	}// MyRowRenderersms_cr_id
	
	
public Calendar getFromDateCal()  {
		
		Calendar tempClientFromCal=null;
		Calendar serverFromDateCal=null;
		
		try {
		 serverFromDateCal = fromDateboxId.getServerValue();
		 tempClientFromCal = fromDateboxId.getClientValue();
		
		 
		}catch(Exception e) {
			logger.info("Exception is "+e);
		}
		 
		 
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
	public Long findDeliveredCount(Long RepotsCrID) {
		

		long deliveredCount = 0;
	    if(retList != null) {
	    	
	    		
	    		for (Object[] objectArr : retList) {
				
				if(objectArr[0] == null || !(objectArr[0] instanceof SMSCampaignReport)) continue;
				
				if(((SMSCampaignReport)objectArr[0]).getSmsCrId().longValue() ==  RepotsCrID) {
					deliveredCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : deliveredCount);
					logger.debug("deliveredCount2=====>"+deliveredCount);
					return deliveredCount;
				}
				
			}
			
			
		}
		return deliveredCount;
		
	}

	public void getCampaignList() {
		
			int count =(int) campRepListTopPagingId.getPageSize() * pageSize;
			logger.debug("-- just entered --");
			MessageUtil.clearMessage();


			try {
				Calendar serverFromDateCal = fromDateboxId.getServerValue();
				Calendar serverToDateCal = toDateboxId.getServerValue();

				Calendar tempClientFromCal = fromDateboxId.getClientValue();
				Calendar tempClientToCal = toDateboxId.getClientValue();

				logger.debug("client From :" + tempClientFromCal + ", client To :"
						+ tempClientToCal);

				// change the time for startDate and endDate in order to consider
				// right from the
				// starting time of startDate to ending time of endDate
				serverFromDateCal.set(Calendar.HOUR_OF_DAY,
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
					MessageUtil.setMessage(
							"'To' date must be later than 'From' date.",
							"color:red", "TOP");
					return;
				}

				/*
				 * if(logger.isDebugEnabled()) {
				 * logger.debug(" Selected Campaign : "+campaignListLbId.
				 * getItemAtIndex(campaignListLbId.getSelectedIndex()).getLabel());
				 * logger.debug(" From Date : "+serverFromDateCal
				 * +"- To Date : "+serverToDateCal); }
				 */

				// setting the parameters in desktopScope when page loads from back
				desktopScope.setAttribute("reportLbIdSelIndex",
						smsCampaignListLbId.getSelectedIndex());
				desktopScope.setAttribute("fromDate", serverFromDateCal);
				desktopScope.setAttribute("toDate", serverToDateCal);

				// If Search option is selected .
				List<SMSCampaignReport> smsCampReportList = null;

				

				if(isAdmin) {
					int userIndex = userOrgLbId.getSelectedIndex();
					
					if(userIndex != -1 ) {
							userId = userNameLbId.getSelectedItem().getValue();
					}//if
					
				}
				
				String tempSourceType = null;

				

				tempSourceType = (String) sourceTypeLbId.getSelectedItem()
						.getValue();
				if (!emailFilterLbId.isDisabled()) {

					if (emailFilterLbId.getSelectedIndex() == 0) {  // if all option for campaign name is selected

						smsCampReportList = smsCampaignReportDao.getAllReports(
								userId, serverFromDateCal.toString(),
								serverToDateCal.toString(), tempSourceType,
								0, count,orderby_colName,desc_Asc);

					} else if (emailFilterLbId.getSelectedIndex() == 1) { // If Search option is selected .

						if (logger.isDebugEnabled()) {
							logger.debug(" Campaign name to be searched : "
									+ searchTbId.getValue());
						}
						String searchTextStr = searchTbId.getValue().trim();
						if (searchTextStr.equals("")
								|| searchTextStr.equals("Email Name...")) {
							MessageUtil.setMessage(
									"Please provide text in the search box.",
									"color:red", "TOP");
							return;
						}
						smsCampReportList = smsCampaignReportDao
								.getReportsByCampaignName(searchTbId.getValue(),
										userId, serverFromDateCal.toString(),
										serverToDateCal.toString(), true,
										tempSourceType, 0, count,orderby_colName,desc_Asc);
					} else {
						if (smsCampaignListLbId.getItemCount() > 0) {
							Listitem selectedItem = smsCampaignListLbId
									.getSelectedItem();
							if (selectedItem == null) {
								MessageUtil.setMessage("Please select campaign.",
										"color:red", "TOP");
								return;
							}
							MessageUtil.clearMessage();
							smsCampReportList = smsCampaignReportDao
									.getReportsByCampaignName(
											selectedItem.getLabel(), userId,
											serverFromDateCal.toString(),
											serverToDateCal.toString(), false,
											tempSourceType, 0, count,orderby_colName,desc_Asc);

						}
					}
				}
				if(smsCampReportList != null && smsCampReportList.size() > 0 ) {
					campRepListTopPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					campRepListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					String cridsStr = Constants.STRING_NILL;
					for (SMSCampaignReport smsCampaignReport : smsCampReportList) {
						
						if(!cridsStr.isEmpty()) cridsStr += ",";
						
						cridsStr += smsCampaignReport.getSmsCrId().longValue();
						
						
					}//for
					if(!cridsStr.isEmpty()) {
						
						List<Object[]> retList = smsCampaignReportDao.findDeliveredCount(cridsStr);
						logger.debug("retList "+retList.size());
						if(retList != null) this.retList=retList; 
						
					}//if
					
					
					
				}//if
				
				
			}
			catch(Exception e) {
				
			}
	}	

}
