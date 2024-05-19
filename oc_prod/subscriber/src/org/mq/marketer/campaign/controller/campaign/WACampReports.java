package org.mq.marketer.campaign.controller.campaign;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.WACampReportLists;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.beans.WACampaign;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.WACampReportListsDao;
import org.mq.marketer.campaign.dao.WACampaignReportDao;
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

public class WACampReports extends GenericForwardComposer{

	private WACampaignReportDao waCampaignReportDao;
	private WACampaignReport waCampaignReport;
	private WACampaign waCampaign;
	Desktop desktopScope ;
	Session sessionScope;
	private TimeZone  clientTimeZone;
	String userName ; 
	Long userId ;
	private boolean isAdmin;
	private String fromPage;
	private Label waCampNamelbId;
	
	private Grid reportGridId;
	private Label msgLblId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Calendar fromDateCal, toDateCal;
	private Listbox campaignListLbId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public WACampReports(){

		try {
			logger.info("----just entered in waCampreports------");
			waCampaignReportDao = (WACampaignReportDao)SpringUtil.getBean("waCampaignReportDao");
			desktopScope = Executions.getCurrent().getDesktop();
			sessionScope = Sessions.getCurrent();
			userName = GetUser.getUserName();
			userId = GetUser.getUserObj().getUserId();
			clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");

			logger.debug("isAdmin :" + sessionScope.getAttribute("isAdmin") );
			
			isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
			
			fromPage=(String)sessionScope.getAttribute("fromPage");
			waCampaign = (WACampaign)sessionScope.getAttribute("WaCampaign");
			logger.info("waCampaign is===>"+waCampaign);
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
				 PageUtil.setHeader("Whatsapp Campaign Reports","",style,true);
			
			Session sessionScope = Sessions.getCurrent();
			sessionScope.removeAttribute("waCampaignReport");
		//	setDateValues();
		//	fromDateCal = getFromDateCal();
		//	toDateCal = getToDateCal();
		//	getReportByCampaignName(waCampaign.getwaCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());
			
		}catch (Exception e) {
			logger.error("Exception ::", e);
		}
	
}

	
	
	public void onClick$backBtnId() {
		
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_WA_CAMPAIGNS);
		Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);
	}
	
	public void getReportByCampaignName(String waCampaignName,Grid reportGridId,Label msgLblId,String fromDate,String toDate){
		try {
			MessageUtil.clearMessage();
			this.reportGridId = reportGridId;
			this.msgLblId = msgLblId;
			//reportGridId.setRowRenderer(new MyRowRenderer());
			List<WACampaignReport> waCampReportList = null;
			try {
				waCampReportList = waCampaignReportDao.getReportsByWaCampaignNameWithInaRange(waCampaignName, userId,fromDate,toDate);
				
			} catch (Exception e) {
				logger.error("Exception :", e);
			}
			if(waCampReportList == null || waCampReportList.size() == 0) {
				//MessageUtil.setMessage("No reports exists for this WA campaign.", "color:red;", "top");
				return;
			}
			msgLblId.setValue("");
			createRows(waCampReportList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	
	
	private void createRows(List<WACampaignReport> waCampReportList){
		
		try {
			//		BounceDao bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
			//		categoriesList = bounceDao.getCategoryPercentageByUser(userId);
			//logger.debug("Got Categories % Info : " + categoriesList);
			MessageUtil.clearMessage();
			MyRowRenderer renderer = new MyRowRenderer();
			renderer.setDeliveredWACRList(null);
			if(waCampReportList != null) {
				
				String cridsStr = Constants.STRING_NILL;
				for (WACampaignReport waCampaignReport : waCampReportList) {
					
					if(!cridsStr.isEmpty()) cridsStr += ",";
					
					cridsStr += waCampaignReport.getWaCrId().longValue();
					
					
				}//for
				if(!cridsStr.isEmpty()) {
					
					List<Object[]> retList = waCampaignReportDao.findDeliveredCount(cridsStr);
					logger.debug("retList "+retList.size());
					if(retList != null) renderer.setDeliveredWACRList(retList);
					
				}//if
				
				
				
			}//if
			
			
			reportGridId.setVisible( (waCampReportList.size() > 0) );
			reportGridId.setModel(new SimpleListModel(waCampReportList));
			reportGridId.setRowRenderer(renderer);
			reportGridId.setActivePage(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}//createRows
	
	
	private String getWaCampRepLists(Long waCampRepId) {
		
		String listsName = "";
		
		WACampReportListsDao waCampReportListsDao = (WACampReportListsDao)SpringUtil.getBean("waCampReportListsDao");
		logger.info("wacampid"+waCampRepId);
		WACampReportLists tempWaCampRepLists = waCampReportListsDao.findBywaCampReportId(waCampRepId);
		if(tempWaCampRepLists!=null) {
		
			listsName = tempWaCampRepLists.getListsName();
			
		}
		return listsName;
		
	}
	

private final class MyRowRenderer implements RowRenderer,EventListener{
	
	private List<Object[]> deliveredWACRList = null;
	public  void setDeliveredWACRList(List<Object[]> deliveredWACRList) {
		
		this.deliveredWACRList = deliveredWACRList;
	}
	public MyRowRenderer() {
		super();
	}
	
	@Override
	public void render(Row row,Object obj,int arg2){
		
		try {
			WACampaignReport waCampReport = null;
			long sent = 0;
			Calendar tempCal;
			Toolbarbutton tb = null;
			Label lbl;
			String sourceType = null;
			if(obj instanceof WACampaignReport) {
				
				waCampReport = (WACampaignReport)obj;
				sent = waCampReport.getSent();
				row.setValue(waCampReport);
				tempCal = waCampReport.getSentDate();
				
				tb = new Toolbarbutton(MyCalendar.calendarToString(tempCal,
						MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				tb.setStyle("color:blue;");
				tb.setAttribute("waCampaignReport", waCampReport);
				
				tb.setParent(row);
				
				
				//adding event listener to identify onClick and what to do when onClick happens
				tb.addEventListener("onClick", this);
				
				tb.setParent(row);
				lbl = new Label(waCampReport.getWaCampaignName());
				lbl.setParent(row);
				
				lbl = new Label(getWaCampRepLists(waCampReport.getWaCrId()));
				lbl.setParent(row);
				
				
				lbl = new Label(""+sent);
				lbl.setParent(row);
				
				int deliveredCount = 0;
				if(deliveredWACRList != null) {
					
					for (Object[] objectArr : deliveredWACRList) {
						
						if(objectArr[0] == null || !(objectArr[0] instanceof WACampaignReport)) continue;
						
						if(((WACampaignReport)objectArr[0]).getWaCrId().longValue() == waCampReport.getWaCrId()) {
							
							deliveredCount = (objectArr[1] != null ? Integer.parseInt(objectArr[1].toString()) : deliveredCount);
							logger.debug("deliveredCount=====>"+deliveredCount);
							break;
						}
						
						
					}
					
					
				}
				
				lbl = new Label(Utility.getPercentage(waCampReport.getClicks(),deliveredCount, 2));
				//lbl = new Label(Utility.getPercentage(waCampReport.getClicks(),sent,2));
				lbl.setParent(row);
				
				/*lbl = new Label(Utility.getPercentage(waCampReport.getOpens(),sent - waCampReport.getBounces(),2));
				lbl.setParent(row);
				lbl = new Label(Utility.getPercentage(waCampReport.getClicks(),waCampReport.getOpens(),2));
				lbl.setParent(row);
				lbl = new Label(Utility.getPercentage(waCampReport.getUnsubscribes(),waCampReport.getOpens(),2));
				lbl.setParent(row);*/
				lbl = new Label(Utility.getPercentage(waCampReport.getBounces(),sent,2));
				lbl.setParent(row);
				
				
				//setBounceCategories(waCampReport.getWaCrId(),row);
				sourceType = waCampReport.getSourceType();
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
			sessionScope.removeAttribute("waCampaignReport");
	WACampaignReport waCampReport = (WACampaignReport)tb.getAttribute("waCampaignReport");
			if(waCampReport.getSent() == 0) {
				
				MessageUtil.setMessage("WA detailed report can not be viewed as sent count is :0. ", "color:green;");
				return;
				
				
			}
			
			if(waCampReport.getSent()!=0 || waCampReport.getOpens()!=0 ||waCampReport.getClicks()!=0 
					|| waCampReport.getBounces()!=0 || waCampReport.getUnsubscribes()!=0) {
				
				sessionScope.setAttribute("waCampaignReport",waCampReport);
				Redirect.goTo(PageListEnum.REPORT_DETAILED_WA_REPORTS);
				
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
	logger.info("wacamp: "+waCampaign +" reportgrid"+reportGridId);
		getReportByCampaignName(waCampaign.getWaCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());
}

public void onClick$resetAnchId() {
	
	
	Redirect.goTo(PageListEnum.EMPTY);
     Redirect.goTo(PageListEnum.CAMPAIGN_WA_REPORT);
	
		/*Rows reportGridIdRows = reportGridId.getRows();
		if (reportGridIdRows != null) {
			reportGridId.removeChild(reportGridIdRows);
		}
		setDateValues();
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		
		
			
		getReportByCampaignName(waCampaign.getWaCampaignName(),reportGridId,msgLblId,fromDateCal.toString(), toDateCal.toString());*/
		
	
}


}

