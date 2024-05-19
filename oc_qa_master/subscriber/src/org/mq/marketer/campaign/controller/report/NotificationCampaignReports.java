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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.beans.NotificationCampReportLists;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.NotificationCampReportListsDao;
import org.mq.marketer.campaign.dao.NotificationCampaignReportDao;
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

@SuppressWarnings("rawtypes")
public class NotificationCampaignReports extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Listbox notificationCampaignListLbId;
	private Label userOrgLblId,userNameLblId;
	private Listbox userOrgLbId,userNameLbId;
	private Grid reportGridId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Textbox searchTbId;
	private Div custExport$chkDivId;
	private Combobox exportCbId;
	//protected CampaignReportDao campaignReportDao = null;
	private int filter;
	private Listitem selectedItem ;
	private Label filterLabelId,msgLblId;

	private NotificationCampaignReportDao notificationCampaignReportDao;
	private NotificationCampReportListsDao notificationCampReportListsDao;
	private Notification notification;
	private Desktop desktopScope = null;
	private Session sessionScope = null;
	private TimeZone clientTimeZone;
	private String userName = null;
	private String userId = null;
	private boolean isAdmin;
	private String fromPage = null;
	private static final String allStr = "--All--";
	private Listbox pageSizeLbId;
	private int pageSize = 0;
	private Window custExport;
	private String searchTextStr = Constants.STRING_NILL;
	
	private Paging campRepListTopPagingId, campRepListBottomPagingId;
	private List<Object[]> retList = null;
	private List<Object[]> retListFailed = null;
	
	
	public NotificationCampaignReports() {

		try {
			notificationCampaignReportDao = (NotificationCampaignReportDao) SpringUtil.getBean("notificationCampaignReportDao");
			notificationCampReportListsDao = (NotificationCampReportListsDao) SpringUtil.getBean("notificationCampReportListsDao");
			desktopScope = Executions.getCurrent().getDesktop();
			sessionScope = Sessions.getCurrent();
			userName = GetUser.getUserName();
			userId = GetUser.getUserObj().getUserId().toString();
			clientTimeZone = (TimeZone) sessionScope.getAttribute("clientTimeZone");
			isAdmin = (Boolean) sessionScope.getAttribute("isAdmin");
			fromPage = (String) sessionScope.getAttribute("fromPage");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}

	}

	private MyRowRenderer renderer = new MyRowRenderer();

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			String style = "font-weight:bold;font-size:15px;color:#313031;"
					+ "font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("Push Notification Campaign Reports", "", style, true);
			notification = (Notification) sessionScope.getAttribute("notification");
			defaultSettings();
			emailFilterLbId.setSelectedIndex(0);
			searchTbId.setValue(Constants.STRING_NILL);
			searchTbId.setVisible(false);
			if(sessionScope.getAttribute("notificationReport")!=null && (boolean)sessionScope.getAttribute("notificationReport")) {
				//notificationCampNamelbId.setValue(notification.getNotificationName());
				emailFilterLbId.setSelectedIndex(1);
				searchTbId.setValue(notification.getNotificationName());
				pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				getReportByCampaignName(notification.getNotificationName(),pageSize,reportGridId,msgLblId);
				searchTbId.setVisible(true);
				sessionScope.setAttribute("notificationReport",false);
			}else {
				pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				getNotificatonCampaignReports(0, pageSize,orderby_colName,desc_Asc);
			}
			try {
				Components.removeAllChildren(custExport$chkDivId);
				  for (int i = 0; i < Constants.notifiactionCampaignHeaderFields.length; i++) {
				  Checkbox tempChk = new
				  Checkbox(Constants.notifiactionCampaignHeaderFields[i]);
				  tempChk.setSclass("custCheck"); tempChk.setParent(custExport$chkDivId);
				  tempChk.setStyle("width:230px"); 
				 }
				
			} catch (Exception e) {
				logger.error("Exception ::"+e);
			}
	     } catch (Exception e) {
	    	 e.printStackTrace();
			logger.error("Exception while rendering the page"+e);
		}
		
	}
	
private void getReportByCampaignName(String notificationName, int pageSizeForReports, Grid reportGridId, Label msgLblId) {
	try {
		MessageUtil.clearMessage();
		this.reportGridId = reportGridId;
		this.msgLblId = msgLblId;
		setTotalSizeToPagingElements();
		List<NotificationCampaignReport> notificationCampReportList = null;
		try {
			//notificationCampReportList = notificationCampaignReportDao.getReportsByNotificationCampaignName(notificationName, Long.parseLong(userId));
			notificationCampReportList = notificationCampaignReportDao.getReportsByCampaignName(notificationName,userId, null,null, true,"--All--", 0, pageSizeForReports,orderby_colName,desc_Asc);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		if(notificationCampReportList == null || notificationCampReportList.size() == 0) {
			MessageUtil.setMessage("No reports exists for this Notification campaign.", "color:red;", "top");
			return;
		}
		msgLblId.setValue("");
		createRows(notificationCampReportList);
	} catch (Exception e) {
		logger.error("Exception ::", e);
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
    			getNotificatonCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
    }
    public void onClick$sortbyNotificationName() {
		orderby_colName = "notificationCampaignName";
		desc2ascasc2desc();
		getNotificatonCampaignReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
}

	public void onClick$resetAnchId() {
		// nemailFilterLbId.setSelectedIndex(0);
		sourceTypeLbId.setSelectedIndex(0);
		if (!emailFilterLbId.isDisabled()) {
			emailFilterLbId.setSelectedIndex(0);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		notificationCampaignListLbId.setVisible(false);
		
		setDateValues();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			userOrgLbId.setSelectedIndex(0);
			onSelect$userOrgLbId();
//			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
		}
		
		orderby_colName="sentDate";
		desc_Asc="desc";
		getNotificatonCampaignReports(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		campRepListTopPagingId.setActivePage(0);
		campRepListBottomPagingId.setActivePage(0);

	}// onclick$resetAnchId()


	
public void onPaging$campRepListTopPagingId(PagingEvent event) {
	
		
		int desiredPage = campRepListTopPagingId.getActivePage();
		campRepListBottomPagingId.setActivePage(desiredPage);
		
		
		
		//PagingEvent pagingEvent = (PagingEvent) event;
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getNotificatonCampaignReports(ofs, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		//redraw(ofs, (byte) event.getPageable().getPageSize());
		
		
	}
	
	public void onPaging$campRepListBottomPagingId(PagingEvent event) {
		
		int desiredPage = campRepListBottomPagingId.getActivePage();
		campRepListTopPagingId.setActivePage(desiredPage);
		
		
		int pSize = event.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		getNotificatonCampaignReports(ofs, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		
		
	}
	
	
	
	private void defaultSettings() {
		try {
         if (isAdmin) {
				setUserOrg();
				userOrgLbId.setVisible(true);
				userOrgLblId.setVisible(true);
				userNameLblId.setVisible(true);
				userNameLbId.setVisible(true);
				userId = null;
				
			} // if(isAdmin)

			reportGridId.setRowRenderer(renderer);

			String isBack = (String) desktopScope.getAttribute("isReportResBack");

			if (isBack != null) {
				if (isBack.equals("true")) {
					Object reportLbIdSelIndexObj = desktopScope.getAttribute("reportLbIdSelIndex");
					if (reportLbIdSelIndexObj != null) {
						int reportLbIdSelIndex = ((Integer) reportLbIdSelIndexObj).intValue();
						notificationCampaignListLbId.setSelectedIndex(reportLbIdSelIndex);
					} else
					notificationCampaignListLbId.setSelectedIndex(0);
					fromDateboxId.setValue((Calendar) desktopScope.getAttribute("fromDate"));
					toDateboxId.setValue((Calendar) desktopScope.getAttribute("toDate"));
				}
			} else {
				setDateValues();
			}

			getCampaignsByUser(userId, (String) sourceTypeLbId.getSelectedItem().getValue());
			int num = notificationCampaignListLbId.getItemCount();
			if (num > 0) {
				notificationCampaignListLbId.setSelectedIndex(0);
			} else {
				return;
			}
			// getCampaignReports();

		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}

	
	private void setUserOrg() {

		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		List<UserOrganization> orgList	= usersDao.findAllOrganizations();
		
		if(orgList == null) {
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
			notificationCampaignListLbId.setDisabled(true);
			searchTbId.setVisible(false);
			onClick$getBitweenDatesBtnId();
			
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); //getPrimaryUsersByOrg((Long)userOrgLbId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			emailFilterLbId.setDisabled(true);
			notificationCampaignListLbId.setDisabled(true);
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
			userNameLbId.setSelectedIndex(0);
		}
		emailFilterLbId.setDisabled(true);
		notificationCampaignListLbId.setDisabled(true);
		searchTbId.setVisible(false);
		onClick$getBitweenDatesBtnId();
	}
	
	public void onSelect$userNameLbId() {
		
		try {
			if(userNameLbId.getSelectedIndex() == 0){
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				emailFilterLbId.setDisabled(true);
				notificationCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
				notificationCampaignListLbId.setVisible(false);
			}
			else {
				emailFilterLbId.setDisabled(false);
				sourceTypeLbId.setSelectedIndex(0);
				emailFilterLbId.setSelectedIndex(0);
				searchTbId.setVisible(false);
				searchTbId.setValue("");
				notificationCampaignListLbId.setVisible(false);
				getCampaignsByUser(userNameLbId.getSelectedItem().getValue(),sourceTypeLbId.getSelectedItem().getValue().toString());
				
				if(notificationCampaignListLbId.getItemCount() > 0)	notificationCampaignListLbId.setSelectedIndex(0);
				
				notificationCampaignListLbId.setVisible(false);
			}
			
			setDateValues();
			onClick$getBitweenDatesBtnId();
			
		} catch (Exception e) {
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
						else if(notificationCampaignListLbId.isVisible()) {
							
							if(notificationCampaignListLbId.getItemCount() > 0)	emailName = selectedItem.getLabel();
						}
					
				}
				
			}
			
			if(!sourceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("--All--")) {
				
				sourceType = sourceTypeLbId.getSelectedItem().getValue();
				
			}
			
			
			if(serverToDateCal.compareTo(serverFromDateCal) < 0){
				MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
				return totalSize;
			}
			
			
			totalSize = notificationCampaignReportDao.getReportCount(userId, emailName, isLike, sourceType,
							serverFromDateCal.toString(), serverToDateCal.toString() );
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
			
			Calendar fromDateCal = getFromDateCal();
			Calendar toDateCal = getToDateCal();
			String query =  getCampaignReportsCsv(fromDateCal,toDateCal, isAdmin);
			if( query == null)
            return;			
			JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			
			long totalCount = jdbcResultsetHandler.totalRecordsSize();
			if(totalCount == 0) {
				MessageUtil.setMessage("Campaign reports are not available for selected time-period", "red");
				return;
			}
			ResultSet resultSet = jdbcResultsetHandler.getResultSet();
			
			StringBuffer crIdList = new StringBuffer();
			while(resultSet.next())
			{
				if(crIdList.length() > 0) crIdList.append(Constants.DELIMETER_COMMA);
				
				crIdList.append(resultSet.getLong("notification_cr_id")); 
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
					filePath = usersParentDirectory + "/" + userName + "/List/download/Notification_Camp_Report_" + System.currentTimeMillis() + "." + ext;
					
					
					File file = new File(filePath);
					BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));	
					
					sb = new StringBuffer();
										
					StringBuffer header = new StringBuffer();
					StringBuffer fields = new StringBuffer("select id,email_id,first_name,last_name,email_status,address_one,address_two,subscription_type ");
				     for(int i=0;i<indexes.length;i++) {
							
						if(indexes[i]==0) {
								if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
								
							header.append("\"").append(Constants.notifiactionCampaignHeaderFields[i]).append("\"");
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
						Long clks=resultSet.getLong("clicks");
						exportresResultSet.moveToInsertRow();
						exportresResultSet.updateString("id", pk.toString());
						Long Sent = resultSet.getLong("sent");  
					    Long deliveredCount = findDeliveredCount(resultSet.getLong("notification_cr_id"));
					    Long failedCount = findFailedCount(resultSet.getLong("notification_cr_id"));
					    String Clicks=Utility.getPercentage(clks,deliveredCount, 2);
					   pk++;
					 //The name are not valid for update string, was done as it is. So did not modify.
						String arr[]= {"email_id","first_name","last_name","email_status","address_one","address_two","subscription_type"};
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
							exportresResultSet.updateString(arr[i], resultSet.getString("notification_campaign_name"));
							i++;
					}
					if(indexes[2]==0)	{
						Long cridStr= resultSet.getLong("notification_cr_id");
					exportresResultSet.updateString(arr[i],getNotificationCampRepLists(cridStr));
					i++;
					}
					if(indexes[3]==0)	{
						exportresResultSet.updateString(arr[i], Sent.toString());
						i++;
					}
					if(indexes[4] == 0){
						exportresResultSet.updateString(arr[i],Clicks);
						i++;
					}
					if(indexes[5] == 0){
						exportresResultSet.updateString(arr[i],deliveredCount.toString());
						i++;
					}
					if(indexes[6]==0)	{
						exportresResultSet.updateString(arr[i],failedCount.toString());
						i++;
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
				
			
	//private Toolbarbutton consolidateMetricsToolbarBtnId;
			

		/**
		 * retrieves campaign report list name based on cam report id
		 * @param campRepId
		 * @return
		 */
		public String getCampRepLists(Long campRepId) {
			
			//long starttime = System.currentTimeMillis();
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
			String query = null;
			try{
				if(serverToDateCal.compareTo(serverFromDateCal) < 0){
					MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
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
					    		checkCon += " AND  notification_campaign_name like '%"+ searchTextStr +"%' ";
					    	}
							
						}else {
							if(notificationCampaignListLbId.getItemCount() > 0) {
								/*Listitem selectedItem = notificationCampaignListLbId.getSelectedItem();*/
								if(selectedItem == null) {
									MessageUtil.setMessage("Please select campaign.", "color:red", "TOP");
									return query;
								}
								MessageUtil.clearMessage();
								/*campReportList = campaignReportDao.getReportsByCampaignName(
										selectedItem.getLabel(), userId, serverFromDateCal.toString(),
										serverToDateCal.toString(), false, tempSourceType, startIndex, count);*/
								if(selectedItem.getLabel() != null) {
						    		checkCon += " AND notification_campaign_name ='" + selectedItem.getLabel() + "'" ;
						    	}
							
							}
						}
					}/*else{
						
						campReportList = campaignReportDao.findAsAdmin(serverFromDateCal.toString(),serverToDateCal.toString(),tempSourceType, startIndex, count,userId);
						
					}*/
					
					
		    	if(tempSourceType!=null && !tempSourceType.equalsIgnoreCase("--All--")){
		    		checkCon +=" AND source_type like '"+tempSourceType+"%'";
		    	}
		    	if(isListChecked) {
		    		checkCon += " AND notification_campaign_name like     '%"+searchTbId.getValue()+"%'" ;
		    	}
		 		if(serverFromDateCal != null && serverToDateCal != null) {
		 			checkCon += " AND sent_date between '" + serverFromDateCal + "' AND '" + serverToDateCal + "' ";
		 		}
		 		String queryStr = " select sent_date,notification_campaign_name,notification_cr_id,sent,clicks,bounces,source_type FROM notification_campaign_report WHERE " + checkCon + " AND status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY sent_date desc";
				return queryStr;
			}catch(Exception e){
				logger.error("** Exception :"+e);
			}
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
			
		  //long starttime = System.currentTimeMillis();
			Long objLong = null;
			
			/*if(categoriesList == null)
			{*/
			List<Object[]> bounceCatList = null;
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
		

	
	
	
	
	
	
	public void onSelect$notificationCampaignListLbId() {


		

	}

	
	public void setSearchDefaultContent() {
		if (searchTbId.getValue() != null
				&& searchTbId.getValue().trim().equals("Notification Name..."))
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
		// userId = (Long)userListLbId.getSelectedItem().getValue();
		if(userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			if(userOrgLbId.getSelectedIndex() == 0 || userOrgLbId.getSelectedIndex() == 0) {
				emailFilterLbId.setDisabled(true);
				notificationCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);
			}else{
				
				getNotificationCampaignsBySource((String) userNameLbId.getSelectedItem().getValue(),sourceType);
			}
		}
		else {
			
			getNotificationCampaignsBySource(userId,sourceType);
		
		}
	}

	public void getNotificationCampaignsBySource(String userId, String sourceType) {

		List<String> notificationCampList = notificationCampaignReportDao.getNotificationCampaignList(
				userId, sourceType);
		List<String> totCampListFinal = new ArrayList<String>();
		totCampListFinal.add("-- All --");
		totCampListFinal.add("-- Search --");

		totCampListFinal.addAll(notificationCampList);
		ListModel strset = new SimpleListModel(totCampListFinal);
		notificationCampaignListLbId.setModel(strset);
		notificationCampaignListLbId.setSelectedIndex(0);
		notificationCampaignListLbId.setDisabled(false);
		searchTbId.setVisible(false);

	}

	/**
	 * This is an onChange event for users Listbox <br/>
	 * This method populates the Campaigns Listbox based on selected user
	 * 
	 * @param userIdobj
	 */
	public void getCampaignsByUser(Object userIdobj, String sourceType)
			throws Exception {
		try {
			MessageUtil.clearMessage();
			List<String> campList = null;
			if (userIdobj == null) {
				emailFilterLbId.setDisabled(true);
				notificationCampaignListLbId.setDisabled(true);
				searchTbId.setVisible(false);

			} else {
				userId = (String) userIdobj;
				campList = notificationCampaignReportDao.getNotificationCampaignList(userId,sourceType);
				List totCampListFinal = new ArrayList();
				/*
				 * totCampListFinal.add("-- All --");
				 * totCampListFinal.add("-- Search --");
				 */
				totCampListFinal.addAll(campList);
				ListModel strset = new SimpleListModel(totCampListFinal);
				emailFilterLbId.setDisabled(false);
				notificationCampaignListLbId.setDisabled(false);
				notificationCampaignListLbId.setModel(strset);
				emailFilterLbId.setSelectedIndex(0);

				if (notificationCampaignListLbId.getItemCount() > 0)
					notificationCampaignListLbId.setSelectedIndex(0);
				searchTbId.setValue("");
				searchTbId.setVisible(false);
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
		
		getNotificatonCampaignReports(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()),orderby_colName,desc_Asc);
		campRepListTopPagingId.setActivePage(0);
		campRepListBottomPagingId.setActivePage(0);
	}

	public void setTotalSizeToPagingElements() {

		String userId = null;
		String campaignName = null;
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
				if (searchTbId.getValue().trim()!=null && !searchTbId.getValue().trim().isEmpty())
					campaignName = searchTbId.getValue();
				else if (notificationCampaignListLbId.isVisible()) {
					if (notificationCampaignListLbId.getItemCount() > 0)	campaignName = notificationCampaignListLbId.getSelectedItem().getLabel();
				}

			}

		}
		else {
			userId = userNameLbId.getSelectedItem().getValue();
		}
		if (!sourceTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("--All--")) {
			sourceType = sourceTypeLbId.getSelectedItem().getValue();
		}

		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate
		serverFromDateCal.set(Calendar.HOUR_OF_DAY,serverFromDateCal.get(Calendar.HOUR_OF_DAY)- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE,serverFromDateCal.get(Calendar.MINUTE)- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		serverToDateCal.set(Calendar.HOUR_OF_DAY,23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE,59 + serverToDateCal.get(Calendar.MINUTE)- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red", "TOP");
			return;
		}

		desktopScope.setAttribute("reportLbIdSelIndex",notificationCampaignListLbId.getSelectedIndex());
		desktopScope.setAttribute("fromDate", serverFromDateCal);
		desktopScope.setAttribute("toDate", serverToDateCal);

		int	totalSize = notificationCampaignReportDao.getReportCount(userId, campaignName,	isLike, sourceType, serverFromDateCal.toString(),serverToDateCal.toString());

		campRepListTopPagingId.setTotalSize(totalSize);
		campRepListBottomPagingId.setTotalSize(totalSize);

	}

	public void getNotificatonCampaignReports(int startIndex, int count,String orderby_colName,String desc_Asc) {
		MessageUtil.clearMessage();

		setTotalSizeToPagingElements();

		try {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar serverToDateCal = toDateboxId.getServerValue();

			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();

			// change the time for startDate and endDate in order to consider
			// right from the
			// starting time of startDate to ending time of endDate
			serverFromDateCal.set(Calendar.HOUR_OF_DAY,serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE,serverFromDateCal.get(Calendar.MINUTE) - tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			serverToDateCal.set(Calendar.HOUR_OF_DAY, 23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(Calendar.MINUTE,59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
				MessageUtil.setMessage("'To' date must be later than 'From' date.","color:red", "TOP");
				return;
			}

			// setting the parameters in desktopScope when page loads from back
			desktopScope.setAttribute("reportLbIdSelIndex",notificationCampaignListLbId.getSelectedIndex());
			desktopScope.setAttribute("fromDate", serverFromDateCal);
			desktopScope.setAttribute("toDate", serverToDateCal);

			// If Search option is selected .
			List<NotificationCampaignReport> notificationCampReportList = null;

			

			if(isAdmin) {
				int userIndex = userOrgLbId.getSelectedIndex();
				
				if(userIndex != -1 ) {
						userId = userNameLbId.getSelectedItem().getValue();
				}//if
				
			}
			
			String tempSourceType = null;
			tempSourceType = (String) sourceTypeLbId.getSelectedItem().getValue();
			if (!emailFilterLbId.isDisabled()) {

				if (emailFilterLbId.getSelectedIndex() == 0) {  // if all option for campaign name is selected
					notificationCampReportList = notificationCampaignReportDao.getAllReports(userId, serverFromDateCal.toString(),serverToDateCal.toString(), tempSourceType,	startIndex, count,orderby_colName,desc_Asc);
				} else if (emailFilterLbId.getSelectedIndex() == 1) { // If Search option is selected .

					String searchTextStr = searchTbId.getValue().trim();
					if (searchTextStr.equals("") || searchTextStr.equals("Email Name...")) {
						MessageUtil.setMessage("Please provide text in the search box.","color:red", "TOP");
						return;
					}
					notificationCampReportList = notificationCampaignReportDao
							.getReportsByCampaignName(searchTbId.getValue(),userId, serverFromDateCal.toString(),serverToDateCal.toString(), true,tempSourceType, startIndex, count,orderby_colName,desc_Asc);
				} else {
					if (notificationCampaignListLbId.getItemCount() > 0) {
						Listitem selectedItem = notificationCampaignListLbId.getSelectedItem();
						if (selectedItem == null) {
							MessageUtil.setMessage("Please select campaign.","color:red", "TOP");
							return;
						}
						MessageUtil.clearMessage();
						notificationCampReportList = notificationCampaignReportDao
								.getReportsByCampaignName(selectedItem.getLabel(), userId,serverFromDateCal.toString(),serverToDateCal.toString(), false,
										tempSourceType, startIndex, count,orderby_colName,desc_Asc);

					}
				}
			} else {

				notificationCampReportList = notificationCampaignReportDao.findAllAsAdmin(serverFromDateCal.toString(),serverToDateCal.toString(), tempSourceType, startIndex,
						count, userId,orderby_colName,desc_Asc);

			}

			getCampaignList();
			createRows(notificationCampReportList);

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


	@SuppressWarnings("unused")
	public void setDateValues() {
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		fromDateboxId.setValue(cal);
		Calendar fromDateCal = getFromDateCal();
		Calendar toDateCal = getToDateCal();
	}

	private Listbox emailFilterLbId;

	public void onSelect$emailFilterLbId() {
		if (emailFilterLbId.getSelectedIndex() == 0) {

			notificationCampaignListLbId.setVisible(false);
			searchTbId.setValue("");
			searchTbId.setVisible(false);
		}
		if (emailFilterLbId.getSelectedIndex() == 1) {

			notificationCampaignListLbId.setVisible(false);
			searchTbId.setPlaceholder("Notification Name...");
			searchTbId.setVisible(true);
		} else if (emailFilterLbId.getSelectedIndex() == 2) {
			searchTbId.setValue("");
			searchTbId.setVisible(false);
			if (notificationCampaignListLbId.getItemCount() == 0) {

				MessageUtil.setMessage("No campaigns found.", "color:red");
				return;

			}
			notificationCampaignListLbId.setVisible(true);
		}

	}// onSelect$emailFilterLbId()

	public void onSelect$pageSizeLbId() {

		pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		campRepListTopPagingId.setPageSize(pageSize);
		campRepListBottomPagingId.setPageSize(pageSize);
		getNotificatonCampaignReports(campRepListTopPagingId.getActivePage() * pageSize,campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);

	}

	
	public List categoriesList = null;

	private void createRows(List<NotificationCampaignReport> notificationCampReportList) {

		try {
			
			renderer.setDeliveredNotificationCRList(null);
			renderer.setFailedNotificationCRList(null);
			if(notificationCampReportList != null && notificationCampReportList.size() > 0 ) {
				campRepListTopPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				campRepListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				String cridsStr = Constants.STRING_NILL;
				for (NotificationCampaignReport notificationCampaignReport : notificationCampReportList) {
					
					if(!cridsStr.isEmpty()) cridsStr += ",";
					
					cridsStr += notificationCampaignReport.getNotificationCrId().longValue();
					
					
				}//for
				if(!cridsStr.isEmpty()) {
					List<Object[]> retList = notificationCampaignReportDao.findDeliveredCount(cridsStr);
					if(retList != null) renderer.setDeliveredNotificationCRList(retList);
					
					List<Object[]> retListFailed = notificationCampaignReportDao.findFaliedCount(cridsStr);
					if(retListFailed != null) renderer.setFailedNotificationCRList(retListFailed);
					
				}//if
				
				
				
			}//if
			
			reportGridId.setModel(new SimpleListModel(notificationCampReportList));
			
			
			
			reportGridId.setRowRenderer(renderer);
			//reportGridId.setActivePage(0);
		} catch (Exception e) {
			logger.error("Exception while creating the rows", e);
		}

	}// createRows

	
private String getNotificationCampRepLists(Long notificationCampRepId) {
			
			String listsName = null;
	        NotificationCampReportLists notificationcampReport=notificationCampReportListsDao.findByNotificationCampReportId(notificationCampRepId);
	        
	        if(notificationcampReport==null)return "--";
	        if(notificationcampReport!=null) {
	    		
				listsName = notificationcampReport.getListsName();
				
			}
	        
			return listsName;
			
			
		}
public String getListNames(Notification notification) {
	String listNames = "";
	
	Set<MailingList> mailingLists= notification.getMailingLists();
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
		private List<Object[]> deliveredNotificationCRList = null;
		private List<Object[]> failedNotificationCRList = null;
		
		
		public  void setDeliveredNotificationCRList(List<Object[]> deliveredNotificationCRList) {
			
			this.deliveredNotificationCRList = deliveredNotificationCRList;
		}
		
		public  void setFailedNotificationCRList(List<Object[]> failedNotificationCRList) {
			
			this.failedNotificationCRList = failedNotificationCRList;
		}
		public MyRowRenderer() {
			super();
		}

		@Override
		public void render(Row row, Object obj, int arg2) {

			try {
				NotificationCampaignReport notificationCampReport = null;
				long sent = 0;
				Calendar tempCal;
				Toolbarbutton tb = null;
				Label lbl;
				if (obj instanceof NotificationCampaignReport) {

					notificationCampReport = (NotificationCampaignReport) obj;
					sent = notificationCampReport.getSent();
					row.setValue(notificationCampReport);
					tempCal = notificationCampReport.getSentDate();

					tb = new Toolbarbutton(MyCalendar.calendarToString(tempCal,
							MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					tb.setStyle("color:blue;");
					tb.setAttribute("notificationCampReportToolbarbutton", notificationCampReport);

					tb.setParent(row);

					// adding event listener to identify onClick and what to do
					// when onClick happens
					tb.addEventListener("onClick", this);

					tb.setParent(row);
					lbl = new Label(notificationCampReport.getNotificationCampaignName());
					lbl.setParent(row);

					lbl = new Label(getNotificationCampRepLists(notificationCampReport.getNotificationCrId()));
					lbl.setParent(row);

					lbl = new Label("" + sent);
					lbl.setParent(row);
					
					int deliveredCount = 0;
					if(deliveredNotificationCRList != null) {
						
						for (Object[] objectArr : deliveredNotificationCRList) {
							
							if(objectArr[0] == null || !(objectArr[0] instanceof NotificationCampaignReport)) continue;
							
							if(((NotificationCampaignReport)objectArr[0]).getNotificationCrId().longValue() == notificationCampReport.getNotificationCrId()) {
								
								deliveredCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : deliveredCount);
								break;
							}
							
							
						}
						
						
					}
					int failedCount =0;
					if(failedNotificationCRList != null) {
						
						for (Object[] objectArr : failedNotificationCRList) {
							
							if(objectArr[0] == null || !(objectArr[0] instanceof NotificationCampaignReport)) continue;
							
							if(((NotificationCampaignReport)objectArr[0]).getNotificationCrId().longValue() == notificationCampReport.getNotificationCrId()) {
								
								failedCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : failedCount);
								break;
							}
							
							
						}
						
						
					}
					
					lbl = new Label(Utility.getPercentage(notificationCampReport.getClicks(),deliveredCount, 2));
					lbl.setParent(row);
					
					lbl = new Label(""+deliveredCount);
					lbl.setParent(row);
					lbl = new Label(""+failedCount);
					lbl.setParent(row);
					/*sourceType = notificationCampReport.getSourceType();
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
					lbl.setParent(row);*/

				}
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}

		}

		@Override
		public void onEvent(Event evt) {
			try {
				Toolbarbutton tb = (Toolbarbutton) evt.getTarget();
				sessionScope.removeAttribute("notificationCampReport");
				NotificationCampaignReport notificationCampReport = (NotificationCampaignReport) tb.getAttribute("notificationCampReportToolbarbutton");
				
				if(notificationCampReport.getSent() == 0) {
					
					MessageUtil.setMessage("Notification detail report cannot be viewed as sent count is :0. ", "color:green;");
					return;
					
					
				}
				
				
				if (notificationCampReport.getSent() != 0
						|| notificationCampReport.getOpens() != 0
						|| notificationCampReport.getClicks() != 0
						|| notificationCampReport.getBounces() != 0
						|| notificationCampReport.getUnsubscribes() != 0) {

					sessionScope.setAttribute("notificationCampReport",notificationCampReport);
					Redirect.goTo(PageListEnum.REPORT_DETAILED_NOTIFICATION_REPORTS);

				}
			} catch (RuntimeException e) {
				logger.error("Exception **", e);
			}

		}// onEvent()

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
	public Long findDeliveredCount(Long RepotsCrID) {
		

		long deliveredCount = 0;
	    if(retList != null) {
	    	
	    		
	    		for (Object[] objectArr : retList) {
				
				if(objectArr[0] == null || !(objectArr[0] instanceof NotificationCampaignReport)) continue;
				
				if(((NotificationCampaignReport)objectArr[0]).getNotificationCrId().longValue() ==  RepotsCrID) {
					deliveredCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : deliveredCount);
					return deliveredCount;
				}
				
			}
			
			
		}
		return deliveredCount;
		
	}
	
public Long findFailedCount(Long RepotsCrID) {
		long failedCount = 0;
	    if(retListFailed != null) {
	    		for (Object[] objectArr : retListFailed) {
				if(objectArr[0] == null || !(objectArr[0] instanceof NotificationCampaignReport)) continue;
				if(((NotificationCampaignReport)objectArr[0]).getNotificationCrId().longValue() ==  RepotsCrID) {
					failedCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : failedCount);
					return failedCount;
				}
			}
		}
		return failedCount;
		
	}

	public void getCampaignList() {
		
			int count =(int) campRepListTopPagingId.getPageSize() * pageSize;
			MessageUtil.clearMessage();


			try {
				Calendar serverFromDateCal = fromDateboxId.getServerValue();
				Calendar serverToDateCal = toDateboxId.getServerValue();

				Calendar tempClientFromCal = fromDateboxId.getClientValue();
				Calendar tempClientToCal = toDateboxId.getClientValue();

				// change the time for startDate and endDate in order to consider
				// right from the
				// starting time of startDate to ending time of endDate
				serverFromDateCal.set(Calendar.HOUR_OF_DAY,serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
				serverFromDateCal.set(Calendar.MINUTE,serverFromDateCal.get(Calendar.MINUTE)- tempClientFromCal.get(Calendar.MINUTE));
				serverFromDateCal.set(Calendar.SECOND, 0);

				serverToDateCal.set(Calendar.HOUR_OF_DAY,23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)	- tempClientToCal.get(Calendar.HOUR_OF_DAY));
				serverToDateCal.set(Calendar.MINUTE,59 + serverToDateCal.get(Calendar.MINUTE)- tempClientToCal.get(Calendar.MINUTE));
				serverToDateCal.set(Calendar.SECOND, 59);

				if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
					MessageUtil.setMessage("'To' date must be later than 'From' date.","color:red", "TOP");
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
				desktopScope.setAttribute("reportLbIdSelIndex",	notificationCampaignListLbId.getSelectedIndex());
				desktopScope.setAttribute("fromDate", serverFromDateCal);
				desktopScope.setAttribute("toDate", serverToDateCal);

				// If Search option is selected .
				List<NotificationCampaignReport> notificationCampReportList = null;

				

				if(isAdmin) {
					int userIndex = userOrgLbId.getSelectedIndex();
					if(userIndex != -1 ) {
							userId = userNameLbId.getSelectedItem().getValue();
					}//if
					
				}
				
				String tempSourceType = null;

				

				tempSourceType = (String) sourceTypeLbId.getSelectedItem().getValue();
				if (!emailFilterLbId.isDisabled()) {

					if (emailFilterLbId.getSelectedIndex() == 0) {  // if all option for campaign name is selected

						notificationCampReportList = notificationCampaignReportDao.getAllReports(
								userId, serverFromDateCal.toString(),
								serverToDateCal.toString(), tempSourceType,
								0, count,orderby_colName,desc_Asc);

					} else if (emailFilterLbId.getSelectedIndex() == 1) { // If Search option is selected .

						String searchTextStr = searchTbId.getValue().trim();
						if (searchTextStr.equals("") || searchTextStr.equals("Email Name...")) {
							MessageUtil.setMessage("Please provide text in the search box.","color:red", "TOP");
							return;
						}
						notificationCampReportList = notificationCampaignReportDao
								.getReportsByCampaignName(searchTbId.getValue(),userId, serverFromDateCal.toString(),serverToDateCal.toString(), true,
										tempSourceType, 0, count,orderby_colName,desc_Asc);
					} else {
						if (notificationCampaignListLbId.getItemCount() > 0) {
							Listitem selectedItem = notificationCampaignListLbId.getSelectedItem();
							if (selectedItem == null) {
								MessageUtil.setMessage("Please select campaign.","color:red", "TOP");
								return;
							}
							MessageUtil.clearMessage();
							notificationCampReportList = notificationCampaignReportDao
									.getReportsByCampaignName(selectedItem.getLabel(), userId,serverFromDateCal.toString(),serverToDateCal.toString(), false,
											tempSourceType, 0, count,orderby_colName,desc_Asc);

						}
					}
				}
				if(notificationCampReportList != null && notificationCampReportList.size() > 0 ) {
					campRepListTopPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					campRepListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					String cridsStr = Constants.STRING_NILL;
					for (NotificationCampaignReport notificationCampReport : notificationCampReportList) {
						if(!cridsStr.isEmpty()) cridsStr += ",";
						cridsStr += notificationCampReport.getNotificationCrId().longValue();
					}//for
					if(!cridsStr.isEmpty()) {
						List<Object[]> retList = notificationCampaignReportDao.findDeliveredCount(cridsStr);
						if(retList != null) this.retList=retList; 
						List<Object[]> retListFailed = notificationCampaignReportDao.findFaliedCount(cridsStr);
						if(retList != null) this.retListFailed=retListFailed; 
					}//if
				}//if
			}
			catch(Exception e) {
				
			}
	}	

}
