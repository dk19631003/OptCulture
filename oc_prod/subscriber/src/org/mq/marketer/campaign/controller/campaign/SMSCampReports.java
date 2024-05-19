package org.mq.marketer.campaign.controller.campaign;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampReportLists;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.SMSCampReportListsDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Toolbarbutton;

public class SMSCampReports extends GenericForwardComposer{

	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReport smsCampaignReport;
	private SMSCampaigns smsCampaign;
	Desktop desktopScope ;
	Session sessionScope;
	private TimeZone  clientTimeZone;
	String userName ; 
	Long userId ;
	private boolean isAdmin;
	private String fromPage;
	private Label smsCampNamelbId;
	
	private Grid reportGridId;
	private Label msgLblId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Calendar fromDateCal, toDateCal;
	private Listbox campaignListLbId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public SMSCampReports(){

		try {
			logger.info("----just entered in smsCampreports------");
			smsCampaignReportDao = (SMSCampaignReportDao)SpringUtil.getBean("smsCampaignReportDao");
			desktopScope = Executions.getCurrent().getDesktop();
			sessionScope = Sessions.getCurrent();
			userName = GetUser.getUserName();
			userId = GetUser.getUserObj().getUserId();
			clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");

			logger.debug("isAdmin :" + sessionScope.getAttribute("isAdmin") );
			
			isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
			
			fromPage=(String)sessionScope.getAttribute("fromPage");
			smsCampaign = (SMSCampaigns)sessionScope.getAttribute("smsCampaign");
			logger.info("smsCampaign is===>"+smsCampaign);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		
	}
	
	
	@Override
	public void doAfterCompose(Component comp) {
		try{
			
			super.doAfterCompose(comp);
			
			logger.info("-----just entered----"+fromPage);
			
			String style = "font-weight:bold;font-size:15px;color:#313031;" +
					"font-family:Arial,Helvetica,sans-serif;align:left";
				 PageUtil.setHeader("SMS Campaign Reports","",style,true);
			
			Session sessionScope = Sessions.getCurrent();
			sessionScope.removeAttribute("smsCampaignReport");
		//	setDateValues();
		//	fromDateCal = getFromDateCal();
		//	toDateCal = getToDateCal();
		//	getReportByCampaignName(smsCampaign.getSmsCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());
			
		}catch (Exception e) {
			logger.error("Exception ::", e);
		}
	
}

	
	
	public void onClick$backBtnId() {
		
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
		Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
	}
	
	public void getReportByCampaignName(String smsCampaignName,Grid reportGridId,Label msgLblId,String fromDate,String toDate){
		try {
			MessageUtil.clearMessage();
			this.reportGridId = reportGridId;
			this.msgLblId = msgLblId;
			//reportGridId.setRowRenderer(new MyRowRenderer());
			List<SMSCampaignReport> smsCampReportList = null;
			try {
				smsCampReportList = smsCampaignReportDao.getReportsBySmsCampaignNameWithInaRange(smsCampaignName, userId,fromDate,toDate);
				
			} catch (Exception e) {
				logger.error("Exception :", e);
			}
			if(smsCampReportList == null || smsCampReportList.size() == 0) {
				//MessageUtil.setMessage("No reports exists for this SMS campaign.", "color:red;", "top");
				return;
			}
			msgLblId.setValue("");
			createRows(smsCampReportList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	
	
	private void createRows(List<SMSCampaignReport> smsCampReportList){
		
		try {
			//		BounceDao bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
			//		categoriesList = bounceDao.getCategoryPercentageByUser(userId);
			//logger.debug("Got Categories % Info : " + categoriesList);
			MessageUtil.clearMessage();
			MyRowRenderer renderer = new MyRowRenderer();
			renderer.setDeliveredSMSCRList(null);
			if(smsCampReportList != null) {
				
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
			
			
			reportGridId.setVisible( (smsCampReportList.size() > 0) );
			reportGridId.setModel(new SimpleListModel(smsCampReportList));
			reportGridId.setRowRenderer(renderer);
			reportGridId.setActivePage(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}//createRows
	
	
	private String getSmsCampRepLists(Long smsCampRepId) {
		
		String listsName = "";
		
		SMSCampReportListsDao smsCampReportListsDao = (SMSCampReportListsDao)SpringUtil.getBean("smsCampReportListsDao");
		
		SMSCampReportLists tempSmsCampRepLists = smsCampReportListsDao.findBySmsCampReportId(smsCampRepId);
		if(tempSmsCampRepLists!=null) {
		
			listsName = tempSmsCampRepLists.getListsName();
			
		}
		return listsName;
		
	}
	

private final class MyRowRenderer implements RowRenderer,EventListener{
	
	private List<Object[]> deliveredSMSCRList = null;
	public  void setDeliveredSMSCRList(List<Object[]> deliveredSMSCRList) {
		
		this.deliveredSMSCRList = deliveredSMSCRList;
	}
	public MyRowRenderer() {
		super();
	}
	
	@Override
	public void render(Row row,Object obj,int arg2){
		
		try {
			SMSCampaignReport smsCampReport = null;
			long sent = 0;
			Calendar tempCal;
			Toolbarbutton tb = null;
			Label lbl;
			String sourceType = null;
			if(obj instanceof SMSCampaignReport) {
				
				smsCampReport = (SMSCampaignReport)obj;
				sent = smsCampReport.getSent();
				row.setValue(smsCampReport);
				tempCal = smsCampReport.getSentDate();
				
				tb = new Toolbarbutton(MyCalendar.calendarToString(tempCal,
						MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				tb.setStyle("color:blue;");
				tb.setAttribute("smsCampaignReport", smsCampReport);
				
				tb.setParent(row);
				
				
				//adding event listener to identify onClick and what to do when onClick happens
				tb.addEventListener("onClick", this);
				
				tb.setParent(row);
				lbl = new Label(smsCampReport.getSmsCampaignName());
				lbl.setParent(row);
				
				lbl = new Label(getSmsCampRepLists(smsCampReport.getSmsCrId()));
				lbl.setParent(row);
				
				
				lbl = new Label(""+sent);
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
				//lbl = new Label(Utility.getPercentage(smsCampReport.getClicks(),sent,2));
				lbl.setParent(row);
				
				/*lbl = new Label(Utility.getPercentage(smsCampReport.getOpens(),sent - smsCampReport.getBounces(),2));
				lbl.setParent(row);
				lbl = new Label(Utility.getPercentage(smsCampReport.getClicks(),smsCampReport.getOpens(),2));
				lbl.setParent(row);
				lbl = new Label(Utility.getPercentage(smsCampReport.getUnsubscribes(),smsCampReport.getOpens(),2));
				lbl.setParent(row);*/
				lbl = new Label(Utility.getPercentage(smsCampReport.getBounces(),sent,2));
				lbl.setParent(row);
				
				
				//setBounceCategories(smsCampReport.getSmsCrId(),row);
				sourceType = smsCampReport.getSourceType();
				if(sourceType != null) {
					if(sourceType.contains("_")) {
						
						lbl = new Label(sourceType.substring(0, sourceType.indexOf("_")));
					}
					else {
						
						lbl = new Label(sourceType);
					}
				}else {
					lbl = new Label("One Off");
				}
				lbl.setParent(row);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}
	
	@Override
	public void onEvent(Event evt) {
		try {
			logger.debug("----just entered to handle the event-----");

			Toolbarbutton tb = (Toolbarbutton)evt.getTarget();
			Session sessionScope = Sessions.getCurrent();
			sessionScope.removeAttribute("smsCampaignReport");
			SMSCampaignReport smsCampReport = (SMSCampaignReport)tb.getAttribute("smsCampaignReport");
			if(smsCampReport.getSent() == 0) {
				
				MessageUtil.setMessage("SMS detailed report can not be viewed as sent count is :0. ", "color:green;");
				return;
				
				
			}
			
			if(smsCampReport.getSent()!=0 || smsCampReport.getOpens()!=0 ||smsCampReport.getClicks()!=0 
					|| smsCampReport.getBounces()!=0 || smsCampReport.getUnsubscribes()!=0) {
				
				sessionScope.setAttribute("smsCampaignReport",smsCampReport);
				Redirect.goTo(PageListEnum.REPORT_DETAILED_SMS_REPORTS);
				
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}//onEvent()
	
}//MyRowRenderer

public void setDateValues() {

	Calendar cal = MyCalendar.getNewCalendar();
	//toDateboxId.setValue(cal);
	logger.debug("ToDate (server) :" + cal);
	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
	logger.debug("FromDate (server) :" + cal);
	//fromDateboxId.setValue(cal);

	fromDateCal = getFromDateCal();
	toDateCal = getToDateCal();
}






public  Calendar getFromDateCal()  {
	
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

	
	
	

public Calendar getToDateCal() {

	Calendar serverToDateCal = toDateboxId.getServerValue();
	Calendar tempClientToCal = toDateboxId.getClientValue();
	serverToDateCal.set(Calendar.HOUR_OF_DAY,
			23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
	serverToDateCal.set(Calendar.MINUTE,
			59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
	serverToDateCal.set(Calendar.SECOND, 59);

	return serverToDateCal;

}

public void onClick$getBitweenDatesBtnId() {
	
	logger.info("entered into onClick of getReports");
	Rows reportGridIdRows = reportGridId.getRows();
	if (reportGridIdRows != null) {
		reportGridId.removeChild(reportGridIdRows);
	}
	if (this.fromDateboxId.getValue() == null || this.toDateboxId.getValue() == null) {
        MessageUtil.setMessage("Please select from date and to date", "color:red", "TOP");
        return;
	}
	 
	fromDateCal = getFromDateCal();
	toDateCal = getToDateCal();
	
		getReportByCampaignName(smsCampaign.getSmsCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());
}

public void onClick$resetAnchId() {
	
	
	Redirect.goTo(PageListEnum.EMPTY);
     Redirect.goTo(PageListEnum.CAMPAIGN_SMS_REPORT);
	
		/*Rows reportGridIdRows = reportGridId.getRows();
		if (reportGridIdRows != null) {
			reportGridId.removeChild(reportGridIdRows);
		}
		setDateValues();
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		
		
			
		getReportByCampaignName(smsCampaign.getSmsCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());*/
		
	
}


}

