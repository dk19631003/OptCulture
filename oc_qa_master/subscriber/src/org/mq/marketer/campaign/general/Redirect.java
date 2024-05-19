package org.mq.marketer.campaign.general;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;

public class Redirect{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static final String UNAUTHORIZED_PAGE="unAuthorized";
	
	public static void goTo(PageListEnum srcEnum){
		goTo(srcEnum.getPagePath());
	}

	public static void goTo(String src){
		try {
			
			
			logger.debug("Requested Page: "+src);
			
			if(!src.equalsIgnoreCase(UNAUTHORIZED_PAGE)) {
				
				PageListEnum plEnum = PageListEnum.getEnumByPagePath(src);
				if(plEnum==null || plEnum.getPageRightEnum()==null) {
					logger.info("PageListEnum / Right is not found :"+src);
					return; // TODO need to handle
				}
				
				Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
	 			//logger.debug("userRoleSet ::"+userRoleSet);
	 			
				if(userRoleSet.contains(plEnum.getPageRightEnum().name())==false) {
					logger.info("No Access...");
					src=UNAUTHORIZED_PAGE;
					//return;
				}
			}
			
			Bandbox userFilterBandBox = (Bandbox)Sessions.getCurrent().getAttribute("userFilter"); 
			Div userLableDiv = (Div)Sessions.getCurrent().getAttribute("userlabelDiv");
			
			if(src.equals("contact/myLists") || src.equals("contact/viewSegments") || src.equals("contact/upload") || src.equals("contact/singleViewContact")) {
				
				userFilterBandBox.setVisible(true);
				userLableDiv.setVisible(true);
				
			}else{
				
				userFilterBandBox.setVisible(false);
				userLableDiv.setVisible(false);
			}
			
			
			
			
			String prevPage = (String)Sessions.getCurrent().getAttribute("currentPage");
			Executions.getCurrent().getDesktop().setBookmark(src);
			String from = (String)Sessions.getCurrent().getAttribute("currentPage");
			if(from !=null){
				Sessions.getCurrent().setAttribute("fromPage", from);
			}
			
			
			Div navigationDivId =(Div)Utility.getComponentById("navigationDivId");
			navigationDivId.setVisible(false);
			
			Div mainNavDivId =(Div)Utility.getComponentById("mainNavDivId");
			mainNavDivId.setVisible(false);
			
			Div ltyNavigationDivId =(Div)Utility.getComponentById("ltyNavigationDivId");
			ltyNavigationDivId.setVisible(false);

			Sessions.getCurrent().setAttribute("currentPage", src);
			MessageUtil.clearMessage();
			logger.debug("prevPage :"+prevPage+" Src :"+src);
			if(prevPage!=null && !prevPage.equalsIgnoreCase(src)) 
				PageUtil.clearHeader();
			
			Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
			xcontents = (Include)Utility.getComponentById("xcontents");
			Sessions.getCurrent().setAttribute("xcontents", xcontents);

			Div xcontentsDivId = (Div)Sessions.getCurrent().getAttribute("xcontentsDivId");
			xcontentsDivId = (Div)Utility.getComponentById("xcontentsDivId");
			Sessions.getCurrent().setAttribute("xcontentsDivId", xcontentsDivId);

			Include ycontents = (Include)Sessions.getCurrent().getAttribute("ycontents");
			ycontents = (Include)Utility.getComponentById("ycontents");
			Sessions.getCurrent().setAttribute("ycontents", ycontents);

			Div ycontentsDivId = (Div)Sessions.getCurrent().getAttribute("ycontentsDivId");
			ycontentsDivId = (Div)Utility.getComponentById("ycontentsDivId");
			Sessions.getCurrent().setAttribute("ycontentsDivId", ycontentsDivId);

			logger.debug(" src : "+ src);
			if(src.contains("mqs") || (src.contains("Editor") && !src.contains("plainEditor") ) || src.contains("TextMsg") || src.contains("uploadHTML")){
				xcontentsDivId.setVisible(false);
				xcontents.setSrc("/zul/Empty.zul");
				ycontentsDivId.setVisible(true);
				ycontents.setSrc("/zul/"+src+".zul");
			}else{
				xcontentsDivId.setVisible(true);
				xcontents.setSrc("/zul/"+src+".zul");
				ycontentsDivId.setVisible(false);
				ycontents.setSrc("/zul/Empty.zul");
			}
			
			if(prevPage!=null) { 
				PageUtil.setFromPage(prevPage); 
			}
			
			Clients.evalJavaScript("parent.window.scrollTo(0,0)");
			logger.debug(" Page loaded successfully");
		} catch (Exception e) {
			logger.error("**  Exception : ", e);
		}
	}

	public static void goToPreviousPage() {
		try{
			String fromPage = (String)Sessions.getCurrent().getAttribute("fromPage");
			if(fromPage != null){
				MessageUtil.clearMessage();
				goTo(fromPage);
			}else{
				goTo("RMHome");
			}
		}catch(Exception e){
			logger.error(" ** Exception :"+e+" **");
		}
	}
}
