package org.mq.marketer.campaign.controller.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Html;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class DetailedWACampaignReportController  extends GenericForwardComposer{
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Include rightId;
	private Session session;
	private Label waCampNameLblId,sentDateLblId,includePromotionsLblId,creditsLblId;
	private WACampaignReport cr;
	private Window previewWin;
	
	
	private CouponsDao couponsDao;
	private  static final String noPromotionStr="No Promotions";
	
	public DetailedWACampaignReportController() {
		
		session = Sessions.getCurrent();
		logger.info("detail wa"+session.getAttribute("waCampaignReport"));
		
		String style = "font-weight:bold;font-size:15px;color:#313031;"
			+ "font-family:Arial,Helvetica,sans-serif;align:left";
	PageUtil.setHeader("WA Campaign Reports", "", style, true);
	couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");

	}
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);  
			logger.info("IN do Ag");

			cr = (WACampaignReport)session.getAttribute("waCampaignReport");
//			logger.info("detail wa"+cr);
			if(cr == null) {
				Redirect.goTo(PageListEnum.RM_HOME);
			}
			String waCampaignName = cr.getWaCampaignName();
			waCampNameLblId.setValue(waCampaignName);
			
			String dateStr = MyCalendar.calendarToString(cr.getSentDate(),null,(TimeZone)session.getAttribute("clientTimeZone"));
			sentDateLblId.setValue(dateStr);
			
			String content = cr.getContent();
			
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
			
			includePromotionsLblId.setValue(coupNameStr);
			includePromotionsLblId.setTooltiptext(coupNameStr);
			
			//creditsLblId.setValue(""+cr.getCreditsCount());
			
			
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
		
		
		changeRightContent("WASnapShot","WA Campaign Reports",rightId);
	}
	
	public  void onClick$rcpntActvtyTbBtnId() {
		changeRightContent("WARecipientActivityReport","WA Campaign Reports",rightId);
	}
	
	public void onClick$opensAndClicksToolbarBtnId() {
		changeRightContent("WAOpensClicks","WA Campaign Reports",rightId);
	}
	
	public void onClick$keyWordUsageReportToolbarBtnId() {
		changeRightContent("WAKeywordReport","WA Campaign Reports",rightId);
	}
	
	public void onClick$bouncereportToolbarBtnId() {
		changeRightContent("WABounceReports","WA Campaign Reports",rightId);
	}
	
	public void onClick$suppressedreportToolbarBtnId() {
		changeRightContent("WASuppressContact","WA Campaign Reports",rightId);
	}
	
	public void onClick$undeliveredTbBtnId() {
		changeRightContent("WAUndeliveredReport","WA Campaign Reports",rightId);
	}
	
	// Added for wa clicks
	
	public void onClick$urlClickedReportToolbarBtnId(){
		
		changeRightContent("WAClickURL","WA Campaign Reports",rightId);
	}
	
	
	public void changeRightContent(String page, String title, Include rightId) {
		logger.info("page"+ page);
		PageUtil.setHeader(title, null, "", true);
		rightId.setSrc("zul/report/"+page+".zul");
	}
	
	

	public void onClick$viewwaBtnId() {
		
		if(cr != null) {
			String msgCnt = cr.getContent();
			Html html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
			html.setContent(msgCnt);
			previewWin.setVisible(true);
	   	}
		
		
		
	}
	

	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.REPORT_WA_CAMPAIGN_REPORTS);
	}
	
	
	
	
	
}
