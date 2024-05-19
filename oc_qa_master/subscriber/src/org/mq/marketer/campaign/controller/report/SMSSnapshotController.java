package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampReportLists;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSCampReportListsDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSClicksDao;
import org.mq.marketer.campaign.dao.SMSOpensDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
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

public class SMSSnapshotController extends GenericForwardComposer{
	
	private Session sessionScope;
	private SMSCampaignReport smsCampaignReport;
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
	
	private SMSCampReportListsDao smsCampReportListsDao;
	private SMSOpensDao smsOpensDao;
	private SMSClicksDao smsClicksDao;
	private SMSBouncesDao smsBouncesDao;
	private SMSCampaignSentDao smsCampaignSentDao;
	private Label sentCountId,receivedId,configuredCountId,suppressCountId;// optOutsCountId//,;
					//openedId
	
	public SMSSnapshotController() {
		
		this.sessionScope = Sessions.getCurrent();
		smsCampaignReport = (SMSCampaignReport)sessionScope.getAttribute("smsCampaignReport");
		
		smsBouncesDao = (SMSBouncesDao)SpringUtil.getBean("smsBouncesDao");
		smsClicksDao = (SMSClicksDao)SpringUtil.getBean("smsClicksDao");
		smsOpensDao = (SMSOpensDao)SpringUtil.getBean("smsOpensDao");
		smsCampaignSentDao = (SMSCampaignSentDao)SpringUtil.getBean("smsCampaignSentDao");
		smsCampReportListsDao = (SMSCampReportListsDao)SpringUtil.getBean("smsCampReportListsDao");
		
	}
	
	@Override
	public  void doAfterCompose(Component comp) {
		try{
			super.doAfterCompose(comp);
			
			if(smsCampaignReport == null) {
				Redirect.goTo(PageListEnum.RM_HOME);
			}
			
			SMSCampaignReportDao smsCampaignReportDao = (SMSCampaignReportDao)SpringUtil.getBean(OCConstants.SMS_CAMPAIGNREPORT_DAO);
			smsCampaignReport = smsCampaignReportDao.findById(smsCampaignReport.getSmsCrId());
			if(smsCampaignReport == null) {
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
		
			
			//long notOpen = smsCampaignReport.getSent() - (smsCampaignReport.getBounces() + smsCampaignReport.getOpens()); 
			
			
			sentCountId.setValue(""+smsCampaignReport.getSent());
			suppressCountId.setValue(""+smsCampaignReport.getSuppressedCount());
			configuredCountId.setValue(smsCampaignReport.getConfigured()+"");
			if(smsCampaignReport.getUser().getSubscriptionEnable()){
				prefCount.setVisible(true);
				preferenceCountId.setValue(smsCampaignReport.getPreferenceCount()+"("+getPercentage(smsCampaignReport.getPreferenceCount(),smsCampaignReport.getConfigured())+"%)");
			}
			int deliveredCount = 0;
			int receivedCount = 0 ;
			
			
			//deliveredId.setValue(actualSent+"("+getPercentage(actualSent,smsCampaignReport.getSent())+"%)");
			//toDO need to fiond out delivered, received, opt-outs
			List<Object[]> retSentList = smsCampaignSentDao.getCategoryPercentageByCrId(smsCampaignReport.getSmsCrId());  
			
			if(retSentList != null && retSentList.size() > 0) {
				
				for (Object[] obj : retSentList) {
					
					
					if( ((String)obj[0]).startsWith(SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT)) {
		
						deliveredCount += ((Long)obj[1]).longValue();
						
					} 
					else if( ((String)obj[0]).startsWith(SMSStatusCodes.CLICKATELL_STATUS_RECEIVED)) {
						
						receivedCount += ((Long)obj[1]).longValue();
						
					} 
										
					
				}//for
				
			
			}//if
			
			/*if(deliveredCount > 0) {
				deliveredId.setValue(deliveredCount+" ("+getPercentage(deliveredCount, smsCampaignReport.getSent())+" % )");
				
				
			}*/
			if(receivedCount > 0) {
				
				receivedId.setValue(receivedCount+" ("+getPercentage(receivedCount, smsCampaignReport.getSent())+" % )");
			}
			
			clickedId.setValue(smsCampaignReport.getClicks()+" ("+getPercentage(smsCampaignReport.getClicks(), receivedCount)+" % )");
			/*int totalBounceCount = smsBouncesDao.getAllCountByRepId(smsCampaignReport.getSmsCrId());
			
			
			if(totalBounceCount > 0) {
				
				bouncedId.setValue(totalBounceCount+"("+getPercentage(totalBounceCount,smsCampaignReport.getSent())+"%)");
			}*/
			
			//preferenceCountId.setValue(smsCampaignReport.getPreferenceCount()+"("+getPercentage(smsCampaignReport.getPreferenceCount(),smsCampaignReport.getConfigured())+"%)");
			
			/*long totalContacts = smsCampaignReport.getConfigured();
			configuredCountId.setValue(""+totalContacts);*/
			
			Label statusLabels[] = {statusLblId1,statusLblId2,statusLblId3};
			Label statusLabelValues[] = {statusLblValueId1,statusLblValueId2,statusLblValueId3};
			
			List<Object[]> reciptCatList = smsCampaignSentDao.getAllSentCategories(smsCampaignReport.getSmsCrId());
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
		  List<Object[]> undeliverCatList = smsBouncesDao.getAllBounceCategories(smsCampaignReport.getSmsCrId());
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
			//model.setValue("Clicks", new Double(smsCampaignReport.getClicks()));
			model.setValue("Bounced", new Double(smsCampaignReport.getBounces()));
			
			mychart.setModel(model);
		
	 } catch (Exception e) {
		logger.error("Exception :::",e);
	 }
	}
	
	
/*public String getReportListNames(Long campRepId) {
		
		String campRepListNames = "";
		
		SMSCampReportLists campRepLists = smsCampReportListsDao.findByCampReportId(campRepId);
		String listNames = campRepLists.getListsName();
		String[] listNameArr = listNames.split(",");
		for (String listName : listNameArr) {
			
			
			if(campRepListNames.length() > 0) campRepListNames += ",";
			campRepListNames += "'"+listName.trim()+"'";
			
			
			
		}//for
		
		
		return campRepListNames;
		
		
	}*/
	
	
	
	/**
	 * creates a Plot for opens and clicks in first 24 hours after the sent date.
	 */
	public void setPlotData() {
		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			Calendar sentDate = smsCampaignReport.getSentDate();
			logger.debug(" sentDate :"+sentDate);

			
			Calendar tempSentDate=(Calendar)Utility.makeCopy(sentDate);
			
			tempSentDate.set(Calendar.SECOND, tempSentDate.get(Calendar.SECOND)+1);
			String startDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			tempSentDate.set(Calendar.DATE, tempSentDate.get(Calendar.DATE)+1);

			String endDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			logger.debug(" startDateStr :"+startDateStr);
			logger.debug("endDateStr :"+endDateStr);

			List<Object[]> openRates = 
				smsOpensDao.getOpenRateByCrId(smsCampaignReport.getSmsCrId(), startDateStr, endDateStr) ;
			
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
				smsClicksDao.getClickRateByCrId(smsCampaignReport.getSmsCrId(), startDateStr, endDateStr) ;
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
