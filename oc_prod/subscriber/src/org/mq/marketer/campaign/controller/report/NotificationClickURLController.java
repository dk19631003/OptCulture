package org.mq.marketer.campaign.controller.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.NotificationCampaignSentDao;
import org.mq.marketer.campaign.dao.NotificationClicksDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Column;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

@SuppressWarnings("serial")
public class NotificationClickURLController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Desktop desktop = null;
	NotificationClicksDao notificationClicksDao;
	NotificationCampaignReport notificationCampaignReport; 
	
	
	private Grid viewClickUrlsGridId;
	private Footer uniqueClicksLBFId, totClicksLBFId;
	
	public static String notificationUlickUrlColName = ""; 
	//private NotificationCampaignReport notificationCampaignReport;
	private NotificationCampaignSentDao notificationCampaignSentDao;
	private TimeZone clientTimeZone = null;
           
	public NotificationClickURLController() {
		
		//clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
		//campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("notificationCampReport");
		notificationClicksDao = (NotificationClicksDao)SpringUtil.getBean("notificationClicksDao");
		notificationCampaignReport = (NotificationCampaignReport)Sessions.getCurrent().getAttribute("notificationCampReport");
		//this.notificationCampaignSentDao = (NotificationCampaignSentDao)SpringUtil.getBean("notificationCampaignSentDao");
		//clientTimeZone = (TimeZone)(Sessions.getCurrent().getAttribute("clientTimeZone"));
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		List<Object[]> objlist = getNotificationClickReport();
		
		List<UrlNotification> urlList = new ArrayList<UrlNotification>();
		if(objlist!=null && !objlist.isEmpty()) {
			for(Object[] eachObj : objlist){
				urlList.add(new UrlNotification(eachObj[0].toString(), Integer.parseInt(eachObj[1].toString().trim()), Integer.parseInt(eachObj[2].toString().trim())));
			}
		ListModel urlModel = new ListModelList(urlList);
		RowRenderer rowRenderer = new MyRowRenderer();
		viewClickUrlsGridId.setModel(urlModel); 		
		viewClickUrlsGridId.setRowRenderer(rowRenderer);
		
		setFooter(uniqueClicksLBFId,totClicksLBFId);
		}
	}
	
	
	public void onSort$urlNameColId(SortEvent event){
		
		notificationUlickUrlColName = "urlNameColId";
		
	 }
	
	public void onSort$uniqueClickColId(SortEvent event){
		
		notificationUlickUrlColName = "uniqueClickColId";
		
	 }
	public void onSort$totalClickColId(SortEvent event){
		
		notificationUlickUrlColName = "totalClickColId";
		
	 }
	
	public List<Object[]> getNotificationClickReport() {
		
		logger.info("Just Entered ...");
		try {
			
			if(notificationCampaignReport == null)
				notificationCampaignReport = (NotificationCampaignReport)desktop.getAttribute("notificationCampaignReport");
			if(notificationCampaignReport!=null) {
				long crId = notificationCampaignReport.getNotificationCrId();
				String  urls = notificationCampaignReport.getNotificationUrl();
				
				logger.info("--Getting the  Url Click report for : "+urls +" of crId -"+crId);
				return notificationClicksDao.getClickList(crId,urls,urls.length());
			}
			return null;
		} 
		catch(Exception e) {
			logger.error("** Exception : Problem while getting the Links info - "+ e + "**");
			return null;
		}
   }
	
	public void setFooter(Footer uniqueClicksLBFId,Footer totClicksLBFId) {
		
		if(notificationCampaignReport == null)
			notificationCampaignReport = (NotificationCampaignReport)desktop.getAttribute("notificationCampaignReport");
		
		long crId = notificationCampaignReport.getNotificationCrId();
		//uniqueClicksLBFId.setLabel(""+clicksDao.getTotClickCount(crId,"unique"));
		totClicksLBFId.setLabel(""+notificationClicksDao.getTotClickCount(crId,"total"));
	}
	
	
	private class MyRowRenderer implements RowRenderer {

		Label lbl;
		@Override
		public void render(Row row, Object data, int arg2) throws Exception {
			UrlNotification item = (UrlNotification) data;
			lbl = new Label(item.getUrlname());
			lbl.setTooltiptext(item.getUrlname());
			lbl.setParent(row);
			
			new Label(item.getUnique()+"").setParent(row);
			new Label(item.getTotal()+"").setParent(row);
			
		}
		
	}
	
}

  class UrlNotification implements Comparable {
	
	private String urlname;
	private int unique;
	private int total;

	public UrlNotification(String urlname, int unique, int total) {
		super();
		this.urlname = urlname;
		this.unique = unique;
		this.total = total;
	}

	public String getUrlname() {
		return urlname;
	}
	public void setUrlname(String urlname) {
		this.urlname = urlname;
	}
	public int getUnique() {
		return unique;
	}
	public void setUnique(int unique) {
		this.unique = unique;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	
	public int compareTo(Object obj) throws ClassCastException {
		if (!(obj instanceof UrlNotification))
			throw new ClassCastException("Error msg here.");
		UrlNotification Itemobj = (UrlNotification) obj;
		
		if(NotificationClickURLController.notificationUlickUrlColName.equals("uniqueClickColId")){
			return new Integer(this.unique).compareTo(Itemobj.getUnique());
		}
		else if(NotificationClickURLController.notificationUlickUrlColName.equals("totalClickColId")){
			return new Integer(this.total).compareTo(Itemobj.getTotal());	
		}
		else if(NotificationClickURLController.notificationUlickUrlColName.equals("urlNameColId")){
			return this.getUrlname().compareTo(Itemobj.getUrlname());	
		}
		else return 0;
		
	}
	
	
}






