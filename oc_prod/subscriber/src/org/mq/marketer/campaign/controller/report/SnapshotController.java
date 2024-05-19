package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.BounceDao;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.FarwardToFriendDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.general.BounceCategories;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
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
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;


@SuppressWarnings("serial")
public class SnapshotController extends GenericForwardComposer{
	
	private Chart plot1;
	private Flashchart mychart;
	//private Label uniqueOpen;
	//private Label bounced;
	//private Label didNotOpen;
	private Label openRate; 
	private Label deliveryRate;
	private Label clickRate;
	private Label totalBounceRate;
	private Label unsubscribedRate;
	private Label spamRate;
	private Label coonfiguredCountId,fowardCountId,sharedCountId;
	
	private Session sessionScope;
	private CampaignReport campaignReport;
	
	private OpensDao opensDao;
	private ClicksDao clicksDao;
	private SuppressedContactsDao suppressedContactsDao;
	private CampReportLists campReportLists;
	private CampReportListsDao campReportListsDao;
	private FarwardToFriendDao farwardToFriendDao;
	private ShareSocialNetworkLinksDao shareSocialNetworkLinksDao;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		
	public SnapshotController() {
		this.sessionScope = Sessions.getCurrent();
		campaignReport = (CampaignReport)sessionScope.getAttribute("campaignReport");
		campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
		suppressedContactsDao = (SuppressedContactsDao)SpringUtil.getBean("suppressedContactsDao");
		farwardToFriendDao = (FarwardToFriendDao)SpringUtil.getBean("farwardToFriendDao");
		shareSocialNetworkLinksDao = (ShareSocialNetworkLinksDao)SpringUtil.getBean("shareSocialNetworkLinksDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		logger.debug(":: before start time in doAfterCompose compose time in millis ::"+System.currentTimeMillis());
		if(campaignReport == null) {
			Redirect.goTo(PageListEnum.RM_HOME);
		}
		CampaignReportDao campaignReportDao = (CampaignReportDao)SpringUtil.getBean(OCConstants.CAMPAIGN_REPORT_DAO);
		campaignReport = campaignReportDao.findById(campaignReport.getCrId());
		if(campaignReport == null) {
			Redirect.goTo(PageListEnum.RM_HOME);
		}
		plot1.setEngine(new JFreeChartEngine());
		this.setPieChartData();
		this.setPlotData();
		logger.debug(":: after start time in doAfterCompose compose time in millis ::"+System.currentTimeMillis());
	}
	
	/*
	public void init(Flashchart mychart,Chart plot1,Label uniqueOpen,Label bounced,
			Label didNotOpen,Label openRate,Label deliveryRate,Label clickRate,
			Label totalBounceRate,Label unsubscribedRate, Label spamRate) {
		
		this.mychart = mychart;
		this.plot1 = plot1;
		plot1.setEngine(new JFreeChartEngine());
		
		
		this.uniqueOpen = uniqueOpen;
		this.bounced = bounced;
		this.didNotOpen = didNotOpen;
		this.openRate = openRate;
		this.deliveryRate = deliveryRate;
		this.clickRate = clickRate;
		this.totalBounceRate = totalBounceRate;
		this.unsubscribedRate = unsubscribedRate;
		this.spamRate = spamRate;
		this.setPieChartData();
		this.setPlotData();
		
		if(campaignReport!=null)
			dateLblId.setValue(campaignReport.getSentDate() + "");
	}
	*/
	
	private Label sentCountId,HardBounceCount,softBounceCount,droppedBounceCount,blockedBounceCount,otherBounceCount,preferenceCountId,suppressCountId,notSentCount,notSentCountId;
	public void setPieChartData() {
		try {
			PieModel model = new SimplePieModel();
			logger.debug(">>>>>>>>>>>>> before setPieChartData time in millis ::"+System.currentTimeMillis());
			
				
				/*logger.debug(">>>>before 1  cal Time >>>"+System.currentTimeMillis());
				campReportLists = campReportListsDao.findByCampReportId(campaignReport.getCrId());
				logger.debug(">>>> after cal Time >>>"+System.currentTimeMillis());
				String listsNames = null;
				String listNameToBesend = "";
				if(campReportLists != null) {
					
					listsNames = campReportLists.getListsName();
					
						
						String[] strArr = listsNames.split(",");
						for (int i = 0; i < strArr.length; i++) {
							
							if(listNameToBesend.length() > 0) listNameToBesend += ",";
							listNameToBesend += "'"+strArr[i]+"'";
						
					}
					
				}*/
				
				long notOpen = campaignReport.getSent() - 
				(campaignReport.getBounces() + campaignReport.getOpens()); 
				model.setValue("Opened", new Double(campaignReport.getOpens()));
				model.setValue("Not Opened", new Double(notOpen));
				model.setValue("Bounced", new Double(campaignReport.getBounces()));
				mychart.setModel(model);
				
				//uniqueOpen.setValue(""+campaignReport.getOpens());
				//bounced.setValue(""+campaignReport.getBounces());
				//didNotOpen.setValue(""+notOpen);
				
				//logger.info("configured::"+campaignReport.getSent());
				//configured
				
				coonfiguredCountId.setValue(""+campaignReport.getConfigured());
				
				//Sent
				sentCountId.setValue(""+campaignReport.getSent());
				long actualSent = campaignReport.getSent() - campaignReport.getBounces();
				//Opens
				openRate.setValue(campaignReport.getOpens()+"("+getPercentage(campaignReport.getOpens(),actualSent)+"%)");
				//Deliveried
				deliveryRate.setValue(actualSent+"("+getPercentage(actualSent,campaignReport.getSent())+"%)");
				//click
				clickRate.setValue(campaignReport.getClicks()+"("+getPercentage(campaignReport.getClicks(),campaignReport.getOpens())
						+"%)");
				//Bounced
				totalBounceRate.setValue(campaignReport.getBounces()+"("+getPercentage(campaignReport.getBounces(),campaignReport.getSent())+"%)");
				//Unsubscribed
				unsubscribedRate.setValue(campaignReport.getUnsubscribes()+"("+getPercentage(campaignReport.getUnsubscribes(),campaignReport.getOpens())
						+"%)");
				//Marked As Spam
				spamRate.setValue("0");
				CampaignSentDao campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
				logger.debug(">>>>before 2 cal Time >>>"+System.currentTimeMillis());
				int spamCount = campaignSentDao.getSpamEmailCount(campaignReport.getCrId());
				logger.debug(">>>>after  cal Time >>>"+System.currentTimeMillis());
				if(spamCount > 0) {
					spamRate.setValue("" + spamCount);
				}
				
				// preference count
				preferenceCountId.setValue(campaignReport.getPreferenceCount()+"("+getPercentage(campaignReport.getPreferenceCount(),campaignReport.getConfigured())+"%)");
				//Supressed 
				suppressCountId.setValue(Constants.STRING_NILL+campaignReport.getSuppressed());
				//not sent(Submitted count)
				int notSentCnt=campaignSentDao.getNotSentCount(campaignReport.getCrId());
				if(notSentCnt>0){
					notSentCountId.setVisible(true);
					notSentCount.setVisible(true);
					notSentCount.setValue(Constants.STRING_NILL+notSentCnt);
				}
				logger.info("notSentCnt--->"+notSentCnt);
				/*String CampRepListNames = getReportListNames(campaignReport.getCrId());
				logger.debug(">>>>before 3  cal Time >>>"+System.currentTimeMillis());
				
				String suppressedTime = MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				int suppCount = suppressedContactsDao.getCountByReport(campaignReport.getUser().getUserId(), CampRepListNames, suppressedTime);
				logger.debug(">>>>after  cal Time >>>"+System.currentTimeMillis());
				
				if(suppCount > 0) {
					//************need to enable comment*************
					ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
					logger.debug(">>>>before 4  cal Time >>>"+System.currentTimeMillis());
					long totalContacts = contactsDao.getCountByReport(listNameToBesend,campaignReport.getUser().getUserId());
					logger.debug(">>>>after   cal Time >>>"+System.currentTimeMillis());
					//************END comment*************
					logger.info("suuppCount ::"+suppCount+" totalCount :: "+totalContacts+ " ");
					suppressCountId.setValue(""+suppCount);
					
				}*/
				
				//yet to be done suppressed count need to calculate before sending itself  
				//long suppressedCount = 
				BounceDao bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
				//long softBounceCnt=0;
				long hardBounceCnt=0;
				long softBounceCnt=0;
				long droppedBounceCnt=0;
				long blockedBounceCnt = 0;
				long otherBounceCt = 0;
				//logger.info();
				logger.debug(">>>>before 5  cal Time >>>"+System.currentTimeMillis());
				List<Object[]> tempBounceList = bounceDao.getCategoryPercentageByCrId(campaignReport.getUser().getUserId(), campaignReport.getCrId() ); 
				logger.debug(">>>>after   cal Time >>>"+System.currentTimeMillis());
				HardBounceCount.setValue("0");
				softBounceCount.setValue("0");
				droppedBounceCount.setValue("0");
				blockedBounceCount.setValue("0");
				otherBounceCount.setValue("0");
				
				if(tempBounceList != null && tempBounceList.size() > 0) {
					
					for (Object[] obj : tempBounceList) {
						
						
						if( ((String)obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
							//mFullLbl.setValue(obj[1] + "");
							softBounceCnt += ((Long)obj[1]).longValue();
							
						} 
						else if( ((String)obj[0]).startsWith(BounceCategories.BLOCKED)) {
							
							blockedBounceCnt += ((Long)obj[1]).longValue();
							
						} 
						else if( ((String)obj[0]).startsWith(BounceCategories.BOUNCED)) {
							//nonExistLbl.setValue(obj[1] + "");
							hardBounceCnt += ((Long)obj[1]).longValue();
							
						} 
						else if( ((String)obj[0]).startsWith(BounceCategories.DROPPED)) {
							//undeliverLbl.setValue(obj[1] + "");
							droppedBounceCnt += ((Long)obj[1]).longValue();
							
						} 
						else if( ((String)obj[0]).startsWith(BounceCategories.OTHERS)) {
							
							otherBounceCt += ((Long)obj[1]).longValue();
							
						} 
						
					}
					if(hardBounceCnt > 0) {
						HardBounceCount.setValue(getPercentage(hardBounceCnt, campaignReport.getSent())+"% ("+hardBounceCnt+" persons)");
					}
					if(softBounceCnt > 0) {
						softBounceCount.setValue(getPercentage(softBounceCnt, campaignReport.getSent())+"% ("+softBounceCnt+" persons)");
					}
					
					if(blockedBounceCnt > 0) {
						blockedBounceCount.setValue(getPercentage(blockedBounceCnt, campaignReport.getSent())+"% ("+blockedBounceCnt+" persons)");
					}
					if(droppedBounceCnt > 0) {
						droppedBounceCount.setValue(getPercentage(droppedBounceCnt, campaignReport.getSent())+"% ("+droppedBounceCnt+" persons)");
					}
					if(otherBounceCt > 0) {
						otherBounceCount.setValue(getPercentage(otherBounceCt, campaignReport.getSent())+"%");
						
					}
				}
				
				//calculate forward count
				
				long forwardCount = farwardToFriendDao.findUniqEmailCount(campaignReport.getUser().getUserId(),campaignReport.getCrId().longValue());
				logger.info("farward count is"+forwardCount);
				
				
				fowardCountId.setValue(""+forwardCount);
				
				//set shared count
				
				long sharedCount = shareSocialNetworkLinksDao.findShareCountByCrId(campaignReport.getCrId().longValue());
				sharedCountId.setValue(""+sharedCount);
				
				//need to calculate suppressed contacts 
				
				
				
				logger.debug(">>>>>>>>>>>>> after setPieChartData time in millis ::"+System.currentTimeMillis());
				
		 } catch (Exception e) {
			logger.error("Exception");
		}
	} // setPieChartData
	
	
	
	public String getReportListNames(Long campRepId) {
		
		String campRepListNames = "";
		
		CampReportLists campRepLists = campReportListsDao.findByCampReportId(campRepId);
		String listNames = campRepLists.getListsName();
		String[] listNameArr = listNames.split(",");
		for (String listName : listNameArr) {
			
			
			if(campRepListNames.length() > 0) campRepListNames += ",";
			campRepListNames += "'"+listName.trim()+"'";
			
			
			
		}//for
		
		
		return campRepListNames;
		
		
	}
	public String getPercentage(long amount,long totalAmount) {
		try {
			
			return Utility.getPercentage(((Long)amount).intValue(), totalAmount, 2);
			/*NumberFormat nf = NumberFormat.getNumberInstance();
		    nf.setMaximumFractionDigits(2);
		    nf.setRoundingMode (RoundingMode.HALF_DOWN);
			return nf.format((amount/totalAmount*100));*/
		} catch (RuntimeException e) {
			logger.error("** Exception ", (Throwable)e);
			return "";
		}
	} // getPercentage
	/**
	 * creates a Plot for opens and clicks in first 24 hours after the sent date.
	 */
	public void setPlotData() {
		try{
			logger.debug(" >> before setPlotData time In millis ::"+System.currentTimeMillis());
			
			//if(logger.isDebugEnabled())logger.debug("-- just entered --");
			if(opensDao == null)
				opensDao = (OpensDao)SpringUtil.getBean("opensDao");
			
			Calendar sentDate = campaignReport.getSentDate();
//			logger.debug(" sentDate :"+sentDate);

			//Calendar tempSentDate = (Calendar)sentDate.clone();
			Calendar tempSentDate=(Calendar)Utility.makeCopy(sentDate);
			
			tempSentDate.set(Calendar.SECOND, tempSentDate.get(Calendar.SECOND)+1);
			String startDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			tempSentDate.set(Calendar.DATE, tempSentDate.get(Calendar.DATE)+1);

			String endDateStr = MyCalendar.calendarToString(tempSentDate, null);
			
			logger.debug(" startDateStr :"+startDateStr+ " AND enddate Str ::"+endDateStr);

			List<Object[]> openRates = 
				opensDao.getOpenRateByCrId(campaignReport.getCrId(), startDateStr, endDateStr) ;
			
			//if(logger.isDebugEnabled()) { logger.debug(" Open rate List size :"+openRates.size()); }

			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			/*if(logger.isDebugEnabled()) { 
				logger.debug(" ClientTimeZone :"+clientTimeZone);
				logger.debug(" ClientCal Before TimeZone setting:"+tempSentDate);
				
			}*/
			
			tempSentDate.setTimeZone(clientTimeZone);
			
			/*if(logger.isDebugEnabled()) { 
				logger.debug(" ClientCal After TimeZone setting:"+tempSentDate.getTime());
				
			}*/
			
			int tempHourShiftInt = tempSentDate.get(Calendar.HOUR_OF_DAY);
			
			if(clicksDao == null) {
				clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			}
			List<Object[]> clickRates = 
				clicksDao.getClickRateByCrId(campaignReport.getCrId(), startDateStr, endDateStr) ;
			//if(logger.isDebugEnabled())logger.debug(" Click rate List size :"+clickRates.size());

			// Do initialisation
			Integer[] x = new Integer[24];
			Integer[] y = new Integer[24];
			Integer[] y1 = new Integer[24];
			//int stHour = sentDate.get(Calendar.HOUR_OF_DAY);
			
			int stHour = sentDate.get(Calendar.HOUR_OF_DAY);
			/*if(logger.isDebugEnabled()) {
				logger.debug(" Hour of the client Time zone :"+tempHourShiftInt+", Server's Hour :"+stHour );
			}*/
			tempHourShiftInt = tempHourShiftInt-stHour;
			
			//if(logger.isDebugEnabled())logger.debug("Time offset : " + tempHourShiftInt );
			
			CategoryModel model = new SimpleCategoryModel();
			
			for(int i=0;i<x.length;i++) {
				// for(int i=1;i<x.length;i++) {
				// x[i] = (stHour+i-1)%24;
				
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
				
				/*if(i==1) {
					x[0]=x[1]-1;
					y[0]=0;
					y1[0]=0;
					model.setValue("Opens", ""+x[0], 0);
					model.setValue("Clicks", ""+x[0], 0);
				}*/
				
				model.setValue("Opens", ""+(24+x[i]+tempHourShiftInt)%24, y[i]);
				model.setValue("Clicks", ""+(24+x[i]+tempHourShiftInt)%24, y1[i]);
				
			}// for i
			
			
			/*for(int i=0;i<x.length;i++){
				logger.debug(x[i]+" = "+y[i]+","+y1[i]);
			}*/
			
			plot1.setModel(model);
			
			logger.debug(" >> after setPlotData time In millis ::"+System.currentTimeMillis());
		} catch(Exception e) {
			logger.debug(" Exception : while generating the line chart ",(Throwable)e);
		}
	} // setPlotData
	
	public void redirectTo(String pageName) {
		try {
			Include detailRepIncId = (Include)Utility.getComponentById("rightId");
			if(pageName.equals("recipientActivityReport"))
				detailRepIncId.setSrc("/zul/report/recipientActivityReport.zul");
			else if(pageName.equals("bounceReport"))
				detailRepIncId.setSrc("/zul/report/bounceReport.zul");
			else if(pageName.equals("seeFullReport"))
				detailRepIncId.setSrc("/zul/report/clickURL.zul");
			else if(pageName.equals("opensClicksOverTime")) 
				detailRepIncId.setSrc("/zul/report/opensClicksOverTime.zul");
		} catch (Exception e) {
			logger.error("Exception : Error occured while redirect the page :",e);
		}
	} // redirectTo
	
	public List<Object[]> getClickReport(){
		try {
			logger.info("Just Entered ...");
			long crId = campaignReport.getCrId();
			if(logger.isDebugEnabled())
				logger.debug("Getting the click report for crId : "+ crId);
			if(clicksDao == null)
				clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			return clicksDao.getClickList(crId,campaignReport.getUrls(),3);
		} catch(Exception e) {
			logger.error("** Exception : Problem while getting the Links info - "+ e + "**");
			return null;
		}
			
	} // getClickReport
	
	public void onClick$seeAllLbId() {
		redirectTo("opensClicksOverTime");
	}
	
	
	public void onClick$recipientActivityToolbarBtnId() {
		redirectTo("recipientActivityReport");
	}
	public void onClick$bounceReportToolbarBtnId() {
		redirectTo("bounceReport");
	}
	public void onClick$fullReportToolbarBtnId() {
		redirectTo("seeFullReport");
	}
}
