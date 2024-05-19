package org.mq.marketer.campaign.controller.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class DetailedNotificationCampaignReportController  extends GenericForwardComposer{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Include rightId;
	private Session sessionScope;
	private Label notificationCampNameLblId,sentDateLblId,includePromotionsLblIdNotification,previewWin$headderContentId,previewWin$bodyContentDisplayId;
	private NotificationCampaignReport cr;
	private Window previewWin;
	private Users currentUser;
	private static String USER_DATA_URL;
	private static String IMAGES_URL;
	private Image previewWin$imgLogo,previewWin$img;
	
	private CouponsDao couponsDao;
	private  static final String noPromotionStr="No Promotions";
	
	public DetailedNotificationCampaignReportController() {
		currentUser = GetUser.getUserObj();
		sessionScope = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Push Notification Campaign Reports", "", style, true);
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		IMAGES_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		USER_DATA_URL = IMAGES_URL+"UserData/";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);  
			cr = (NotificationCampaignReport)sessionScope.getAttribute("notificationCampReport");
			if(cr == null) {
				Redirect.goTo(PageListEnum.RM_HOME);
			}
			String notificationCampaignName = cr.getNotificationCampaignName();
			notificationCampNameLblId.setValue(notificationCampaignName);
			
			String dateStr = MyCalendar.calendarToString(cr.getSentDate(),null,(TimeZone)sessionScope.getAttribute("clientTimeZone"));
			sentDateLblId.setValue(dateStr);
			
			String content = cr.getNotificationContent();
			
			Set<String> totalPhSet = getCouponPhSet(content);
			
			String couponIdStr=Constants.STRING_NILL;
			
			for (String phStr : totalPhSet) {
				
				
				if(phStr.startsWith("CC_")) {
					
					
					String[] strArr = phStr.split("_");
					
					if(!couponIdStr.isEmpty()) couponIdStr += Constants.DELIMETER_COMMA;
					
					couponIdStr += strArr[1].trim();
				}
				
				
				
			}// for
			List<Coupons> couponList = null;
			String coupNameStr=Constants.STRING_NILL;
			
			if(! couponIdStr.isEmpty()){
				
				 couponList = couponsDao.findById(couponIdStr);
			}else{
				coupNameStr +=noPromotionStr;
			}
			
			
			if(couponList != null && couponList.size() > 0){
				
				for (Coupons coupons : couponList) {
					
					if(!coupNameStr.isEmpty()) coupNameStr += Constants.DELIMETER_COMMA+Constants.DELIMETER_SPACE;
					
					coupNameStr+=coupons.getCouponName();
				}
			}
			
			includePromotionsLblIdNotification.setValue(coupNameStr);
			includePromotionsLblIdNotification.setTooltiptext(coupNameStr);
			
		}catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	}
	
	public Set<String> getCouponPhSet(String content){
		
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		Set<String> phSet = new HashSet<String>();
		
		String ph = null;
		
			try {
				while(m.find()) {
					ph = m.group(1);
					if(ph.startsWith("CC_")) {
						phSet.add(ph);
					}
				}
			} catch (Exception e) {
				logger.error("Exception",e);
				//logger.error("Exception :::",e);
			}
		return phSet;
	}

	public void onClick$snapShtTbBtnId(){
		
		changeRightContent("NotificationSnapShot","Notification Campaign Reports",rightId);
	}
	
	public  void onClick$rcpntActvtyTbBtnId() {
		changeRightContent("NotificationRecipientActivityReport","Notification Campaign Reports",rightId);
	}
	
	public void onClick$suppressedreportToolbarBtnId() {
		changeRightContent("NotificationSuppressContact","Notification Campaign Reports",rightId);
	}
	
	public void onClick$undeliveredTbBtnId() {
		changeRightContent("NotificationUndeliveredReport","Notification Campaign Reports",rightId);
	}
	
	
	public void onClick$urlClickedReportToolbarBtnId(){
		
		changeRightContent("notificationClickURL","Notification Campaign Reports",rightId);
	}
	
	public void onClick$clicksToolbarBtnId() {

		changeRightContent("notificationClicksOverTime","Notification Campaign Reports",rightId);
	}
	
	public void changeRightContent(String page, String title, Include rightId) {
		PageUtil.setHeader(title, null, "", true);
		rightId.setSrc("zul/report/"+page+".zul");
	}
	
	

	public void onClick$viewNotificationBtnId() {
		
		if(cr != null) {
			//StringBuilder sb = new StringBuilder();
			
			/*
			 * sb.append(cr.getNotificationHeaderContent()).append("<img src="+USER_DATA_URL
			 * +currentUser.getUserName()+"/Notification/logoImage/"+cr.
			 * getNotificationCampaignName()+"/"+cr.getNotificationLogoImage()+">")
			 * .append("</br>").append("<img src="+USER_DATA_URL+currentUser.getUserName()+
			 * "/Notification/bannerImage/"+cr.getNotificationCampaignName()+"/"+cr.
			 * getNotificationBannerImage()+">").append(cr.getNotificationContent()); Html
			 * html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
			 * html.setContent(sb.toString());
			 */
			previewWin.doHighlighted();
			previewWin$imgLogo.setSrc(USER_DATA_URL+currentUser.getUserName()+"/Notification/logoImage/"+cr.getNotificationCampaignName()+"/"+cr.getNotificationLogoImage());
			previewWin$img.setSrc(USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+cr.getNotificationCampaignName()+"/"+cr.getNotificationBannerImage());
			previewWin$headderContentId.setValue(cr.getNotificationHeaderContent());
			previewWin$bodyContentDisplayId.setValue(cr.getNotificationContent());
			previewWin.setVisible(true);
	   	}
		
		
		
	}
	

	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.NOTIFICATION_REPORT);
	}
	
	
	
	
	
}
