package org.mq.marketer.campaign.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;

/**
 This class can be used to get the properties 
 created date 28-04-2009
 author RM Team
*/

public class PageUtil{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public static boolean setHeader(String header,String rightLbl,String style,boolean visibility){
		try{
			logger.debug("Changing the Page Parameters");
			Label headingLbId = (Label)Utility.getComponentById("homeHeaderLableId");
			Label headingRightId = (Label)Utility.getComponentById("homeHeaderLableRightId");
			headingLbId.setValue(header);
			headingRightId.setValue(rightLbl);
			headingRightId.setVisible(visibility);
			headingLbId.setVisible(visibility);
			headingLbId.setStyle(style);
			logger.debug("Changing the Page Parameters is successfull");
			return true;
		}catch(Exception e){
			logger.error("Exception : Changing the Page Parameters is failed " , e);
			return false;
		}	
	}
	public static void clearHeader(){
		try {
			Label headingLbId = (Label)Utility.getComponentById("homeHeaderLableId");
			Label headingLblRId = (Label)Utility.getComponentById("homeHeaderLableRightId");
			headingLbId.setValue("");
			headingLblRId.setValue("");
			headingLblRId.setVisible(false);
			headingLbId.setVisible(false);
		} catch (Exception e) {
			logger.error("Excpetion : Clearing the Page Parameters is failed " + e);
		}
	}
	
	public static void setFromPage(String fromPage){
		try {
			Sessions.getCurrent().setAttribute("fromPage", fromPage);
			if(fromPage.indexOf('.')>0){
				fromPage = fromPage.substring(0, fromPage.indexOf('.'));
			}
		} catch (Exception e) {
			logger.error(" ** Exception :"+e+" **");
		}
	}
	
	public static void goToPreviousPage(){
		try{
			String fromPage = (String)Sessions.getCurrent().getAttribute("fromPage");
			if(fromPage != null){
				MessageUtil.clearMessage();
				Redirect.goTo(fromPage);
			}else{
				Redirect.goTo(PageListEnum.RM_HOME);
			}
		}catch(Exception e){
			logger.error(" ** Exception :"+e+" **");
		}
	}


}
