package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.WABouncesDao;
import org.mq.marketer.campaign.dao.WACampaignReportDao;
import org.mq.marketer.campaign.dao.WACampaignSentDao;
import org.mq.marketer.campaign.dao.WAClicksDao;
import org.mq.marketer.campaign.dao.WAOpensDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.WAStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Flashchart;
import org.zkoss.zul.Label;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;

public class WASnapshotController extends GenericForwardComposer{
	
	private Session sessionScope;
	private WACampaignReport waCampaignReport;
	private Flashchart mychart;
	private Chart plot1;
	
	private Label statusLblId1, statusLblValueId1;
	private Label statusLblId2, statusLblValueId2;
	private Label statusLblId3, statusLblValueId3;
	private Label statusOthersLblId, statusOthersLblValueId; 
	

	private Label undeliveredLblId1, undeliveredLblValueId1;
	private Label undeliveredLblId2, undeliveredLblValueId2;
	private Label undeliveredLblId3, undeliveredLblValueId3;
	private Label undeliveredOthersLblId, undeliveredOthersLblValueId;
	
	private Label clickedId,configLblId,prefCount,preferenceCountId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
//	private WACampReportListsDao waCampReportListsDao;
	private WAOpensDao waOpensDao;
	private WAClicksDao waClicksDao;
	private WABouncesDao waBouncesDao;
	private WACampaignSentDao waCampaignSentDao;
	private Label sentCountId,receivedId,configuredCountId,suppressCountId;// optOutsCountId//,;
					//openedId
	
	public WASnapshotController() {
		
		this.sessionScope = Sessions.getCurrent();
		waCampaignReport = (WACampaignReport)sessionScope.getAttribute("waCampaignReport");
		
		waBouncesDao = (WABouncesDao)SpringUtil.getBean("waBouncesDao");
		waClicksDao = (WAClicksDao)SpringUtil.getBean("waClicksDao");
		waOpensDao = (WAOpensDao)SpringUtil.getBean("waOpensDao");
		waCampaignSentDao = (WACampaignSentDao)SpringUtil.getBean("waCampaignSentDao");
//		waCampReportListsDao = (WACampReportListsDao)SpringUtil.getBean("waCampReportListsDao");
		
	}
	
	@Override
	public  void doAfterCompose(Component comp) {
		try{
			super.doAfterCompose(comp);
			
			if(waCampaignReport == null) {
				logger.info("wa*report null in snapwa");
				Redirect.goTo(PageListEnum.RM_HOME);
			}
			
			WACampaignReportDao waCampaignReportDao = (WACampaignReportDao)SpringUtil.getBean("waCampaignReportDao");
			waCampaignReport = waCampaignReportDao.findById(waCampaignReport.getWaCrId());
			if(waCampaignReport == null) {
				Redirect.goTo(PageListEnum.RM_HOME);
			}
			
			int countryCarrier = GetUser.getUserObj().getCountryCarrier();
			String configText = "Specifies the total unique mobile contacts(after adding " + countryCarrier + "  as prefix) configured.";
			configLblId.setTooltiptext(configText);
			plot1.setEngine(new JFreeChartEngine());
			setPieChartData();
			setPlotData();
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void setPieChartData(){

	try {
		PieModel model = new SimplePieModel();
		
			
			
			
			sentCountId.setValue(""+waCampaignReport.getSent());
			suppressCountId.setValue(""+waCampaignReport.getSuppressedCount());
			configuredCountId.setValue(waCampaignReport.getConfigured()+"");
			if(waCampaignReport.getUser().getSubscriptionEnable()){
				prefCount.setVisible(true);
				preferenceCountId.setValue(waCampaignReport.getPreferenceCount()+"("+getPercentage(waCampaignReport.getPreferenceCount(),waCampaignReport.getConfigured())+"%)");
			}
			int deliveredCount = 0;
			int receivedCount = 0 ;
			
			
			List<Object[]> retSentList = waCampaignSentDao.getCategoryPercentageByCrId(waCampaignReport.getWaCrId());  
			
			if(retSentList != null && retSentList.size() > 0) {
				
				for (Object[] obj : retSentList) {
					
					
					if( ((String)obj[0]).startsWith(WAStatusCodes.WA_STATUS_DELIVERED_TO_RECEPIENT)) {
		
						deliveredCount += ((Long)obj[1]).longValue();
						
					} 
					else if( ((String)obj[0]).startsWith(WAStatusCodes.WA_STATUS_RECEIVED)) {
						
						receivedCount += ((Long)obj[1]).longValue();
						
					} 
										
					
				}//for
				
			
			}//if
			
			
			if(receivedCount > 0) {
				
				receivedId.setValue(receivedCount+" ("+getPercentage(receivedCount, waCampaignReport.getSent())+" % )");
			}
			
			clickedId.setValue(waCampaignReport.getClicks()+" ("+getPercentage(waCampaignReport.getClicks(), receivedCount)+" % )");
			
			
			Label statusLabels[] = {statusLblId1,statusLblId2,statusLblId3};
			Label statusLabelValues[] = {statusLblValueId1,statusLblValueId2,statusLblValueId3};
			
			List<Object[]> reciptCatList = waCampaignSentDao.getAllSentCategories(waCampaignReport.getWaCrId());
			int count = 0; 
			int otherCount = 0;
			for(Object[] category : reciptCatList) {
				if(category[0] != null) {
					if(count<3) {
						
						logger.debug("in if  "+(String) category[0]);
						statusLabels[count].setValue((String) category[0]+": ");
						statusLabelValues[count].setValue(category[1]+"");
					}
					else {
						
						logger.debug("in else  "+(String) category[0]);
						otherCount += (Long)category[1];
					}
				}
				count++;
			}
		  if(reciptCatList.size()>3) {
			  statusOthersLblId.setValue("Others: ");
			  statusOthersLblValueId.setValue(otherCount+"");
		  }
		  
		Label undeliveredLabels[] = {undeliveredLblId1,undeliveredLblId2,undeliveredLblId3};
		Label undeliveredLabelValues[] = {undeliveredLblValueId1,undeliveredLblValueId2,undeliveredLblValueId3};
		  List<Object[]> undeliverCatList = waBouncesDao.getAllBounceCategories(waCampaignReport.getWaCrId());
		  count = 0; 
		  otherCount = 0;
			for(Object[] category : undeliverCatList) {
				if(category[0] != null) {
					if(count<3) {
							
							logger.debug("in if  "+(String) category[0]);
							undeliveredLabels[count].setValue((String) category[0]+": ");
							undeliveredLabelValues[count].setValue(category[1]+"");
					}
					else {
						logger.debug("in else  "+(String) category[0]);
						otherCount += (Long)category[1];
					}
				}
				count++;
			}
		  if(undeliverCatList.size()>3) {
			  undeliveredOthersLblId.setValue("Others: ");
			  undeliveredOthersLblValueId.setValue(otherCount+"");
		  }
			
			
			model.setValue("Delivered", new Double(receivedCount));
			model.setValue("Bounced", new Double(waCampaignReport.getBounces()));
			
			mychart.setModel(model);
		
	 } catch (Exception e) {
		logger.error("Exception :::",e);
	 }
	}
	
	

	
	
	
	/**
	 * creates a Plot for opens and clicks in first 24 hours after the sent date.
	 */
	public void setPlotData() {
		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			Calendar sentDate = waCampaignReport.getSentDate();
			logger.debug(" sentDate :"+sentDate);

			
			Calendar tempSentDate=(Calendar)Utility.makeCopy(sentDate);
			
			tempSentDate.set(Calendar.SECOND, tempSentDate.get(Calendar.SECOND)+1);
			String startDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			tempSentDate.set(Calendar.DATE, tempSentDate.get(Calendar.DATE)+1);

			String endDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			logger.debug(" startDateStr :"+startDateStr);
			logger.debug("endDateStr :"+endDateStr);

			List<Object[]> openRates = 
				waOpensDao.getOpenRateByCrId(waCampaignReport.getWaCrId(), startDateStr, endDateStr) ;
			
			if(logger.isDebugEnabled()) { logger.debug(" Open rate List size :"+openRates.size()); }

			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			if(logger.isDebugEnabled()) { 
				logger.debug(" ClientTimeZone :"+clientTimeZone);
				logger.debug(" ClientCal Before TimeZone setting:"+tempSentDate);
				
			}
			
			tempSentDate.setTimeZone(clientTimeZone);
			
			if(logger.isDebugEnabled()) { 
				logger.debug(" ClientCal After TimeZone setting:"+tempSentDate);
				
			}
			
			int tempHourShiftInt = tempSentDate.get(Calendar.HOUR_OF_DAY);
			
			
			List<Object[]> clickRates = 
				waClicksDao.getClickRateByCrId(waCampaignReport.getWaCrId(), startDateStr, endDateStr) ;
			if(logger.isDebugEnabled())logger.debug(" Click rate List size :"+clickRates.size());

			// Do initialisation
			Integer[] x = new Integer[24];
			Integer[] y = new Integer[24];
			Integer[] y1 = new Integer[24];
			
			
			int stHour = sentDate.get(Calendar.HOUR_OF_DAY);
			if(logger.isDebugEnabled()) {
				logger.debug(" Hour of the client Time zone :"+tempHourShiftInt+", Server's Hour :"+stHour );
			}
			tempHourShiftInt = tempHourShiftInt-stHour;
			
			if(logger.isDebugEnabled())logger.debug("Time offset : " + tempHourShiftInt );
			
			CategoryModel model = new SimpleCategoryModel();
			
			for(int i=0;i<x.length;i++) {
				
				
				x[i] = (stHour+i)%24;
				y[i] = 0;
				y1[i] = 0;
				int temp;
				
				for(Object[] obj:openRates) {
					temp = Integer.parseInt(obj[0].toString());
					if(temp==x[i]) {
						y[i] = Integer.parseInt(obj[1].toString());
						break;
					}
				}// for obj	
				
				for(Object[] obj:clickRates) {
					temp = Integer.parseInt(obj[0].toString());
					if(temp==x[i]) {
						y1[i] = Integer.parseInt(obj[1].toString());
						break;
					}
				}// for obj
				
				
				
				model.setValue("Opens", ""+(24+x[i]+tempHourShiftInt)%24, y[i]);
				model.setValue("Clicks", ""+(24+x[i]+tempHourShiftInt)%24, y1[i]);
				
			}// for i
			
			
			
			
			plot1.setModel(model);
		} catch(Exception e) {
			logger.debug(" Exception : while generating the line chart ",(Throwable)e);
		}
	} // setPlotData
	
	
	
	
	
	
	
	
	
	
	public String getPercentage(long amount,long totalAmount) {
		try {
			
			return Utility.getPercentage(((Long)amount).intValue(), totalAmount, 2);
		} catch (RuntimeException e) {
			logger.error("** Exception ", (Throwable)e);
			return "";
		}
	}
}
