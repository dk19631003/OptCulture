package org.mq.marketer.campaign.controller.report;

import java.awt.Button;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;


@SuppressWarnings("serial")
public class DetailedReportController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Label dateLblId,subjectlblId,campNamelblId;
	CampaignReport campaignReport;
	private Include rightId;
	
	public DetailedReportController() {
		PageUtil.setHeader("Email Campaign Reports", null, "", true);
		logger.info("====1 ===="+System.currentTimeMillis());
		 UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		 UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		 /*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_DETAILED_REPORT,GetUser.getUserObj());
		 }*/
		 if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_DETAILED_REPORT,GetUser.getLoginUserObj());
			 }

		  logger.info("====2 ===="+System.currentTimeMillis()); 
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		//cr = (CampaignReport)sessionScope.get("campaignReport");
		campaignReport = getCampaignReport();
		if(campaignReport == null) {
			org.mq.marketer.campaign.general.Redirect.goTo(PageListEnum.RM_HOME);
		}
		String dateStr = MyCalendar.calendarToString(campaignReport.getSentDate(),null,(TimeZone)sessionScope.get("clientTimeZone"));
		dateLblId.setValue(dateStr);
		subjectlblId.setValue(campaignReport.getSubject());
		subjectlblId.setTooltiptext(campaignReport.getSubject());
		
		campNamelblId.setValue(campaignReport.getCampaignName());
		campNamelblId.setTooltiptext(campaignReport.getCampaignName());
	}
	
	public CampaignReport getCampaignReport() {
		campaignReport = (CampaignReport)sessionScope.get("campaignReport");
		return campaignReport;
	} // getCampaignReport
	
	public void changeRightContent(String page, String title, Include rightId) throws Exception { 
		
		PageUtil.setHeader(title, null, "", true);
		rightId.setSrc("zul/report/"+page+".zul");
	}
	
	public void onClick$snapShotToolbarBtnId() {
		try {
			changeRightContent("snapshot","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$snapShotToolbarBtnId
	
	public void onClick$recipientActivityReporttoolbarBtnId() {
		try {
			changeRightContent("recipientActivityReport","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$recipientActivityReporttoolbarBtnId
	
	public void onClick$urlClickedReportToolbarBtnId() {
		try {
			changeRightContent("clickURL","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}//onClick$urlClickedReportToolbarBtnId
	
	
	
	
	public void onClick$opensAndClicksToolbarBtnId() {
		try {
			changeRightContent("opensClicksOverTime","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$opensAndClicksToolbarBtnId
	
	public void onClick$emailClientUsageReportToolbarBtnId() {
		try {
			changeRightContent("clientUsage","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$emailClientUsageReportToolbarBtnId
	public void onClick$bouncereportToolbarBtnId() {
		try {
			changeRightContent("bounceReport","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$bouncereportToolbarBtnId
	
	
	
	public void onClick$suppressedreportToolbarBtnId() {
		
		try {
			changeRightContent("SuppressedContactsReports","Email Campaign Reports",rightId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
		
	}
	
	
	private Window previewWin;
	private Html previewWin$html;
	public void onClick$viewEmailBtnId() {
		String htmlStr = campaignReport.getContent();
		
		if(htmlStr.contains("<div id='rootDivId'>")){
			
			String firstDivStr = htmlStr.substring(htmlStr.indexOf("<div"), htmlStr.indexOf("<div id='rootDivId'>"));
			//delete the content of Having Trouble viewing this email or customize link  div from the html content
			htmlStr = htmlStr.replace(firstDivStr, "");
		}
		
		String openTrackUrl = PropertyUtil.getPropertyValue(OCConstants.PROPS_KEY_OPENTRACKURL).replace("|^", "[").replace("^|", "]");
		//logger.info("openTrackUrl..................." +openTrackUrl);
		
		if(htmlStr.contains(openTrackUrl)){			
			htmlStr = htmlStr.replace(openTrackUrl,PropertyUtil.getPropertyValue("ApplicationUrl") + "img/transparent.gif");
			//logger.info("htmlStr..........."+htmlStr);
		}
		
		
		if(htmlStr.contains("href='")){
			htmlStr = htmlStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\"");
			
		}
		if(htmlStr.contains("href=\"")){
			htmlStr = htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\"");
		}
		
		//Utility.showPreview(previewWin$html, campaignReport.getUser().getUserName(), htmlStr);
		htmlStr = Utility.mergeTagsForPreviewAndTestMail(htmlStr, "previewReport");
		previewWin$html.setContent(htmlStr);
		//Html html = (Html)previewWin$html;
		//html.setContent(htmlStr);
		previewWin.setVisible(true);
	}
	private Button backBtnId;
	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.REPORT_REPORT);
	}
}
