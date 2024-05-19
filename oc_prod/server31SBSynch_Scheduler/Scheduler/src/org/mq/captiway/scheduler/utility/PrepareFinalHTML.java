package org.mq.captiway.scheduler.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.captiway.scheduler.beans.Address;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.EmailContent;
import org.mq.captiway.scheduler.beans.Users;


public class PrepareFinalHTML {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	static final String JS_SCRIPT_PATTERN = "<script.*?>.*?</script>";
	static final String URL_SYSTEM_DATA_PATTERN = "url\\(.*?(SystemData/Templates/(.*?/(.*?)))[\\)]";
	static final String IMG_PATTERN = PropertyUtil.getPropertyValue("HiddenImgPattern");
	static final String TMCE_URL_PATTERN = "<div[^>]*?name=\"TMCEeditableDiv\"[^>]*?>";
	static final String BG_IMG_PATTERN = "background\\s*?=\\s*?[\"](.*?)[\"]";

	
	static final String HTTP_TOKEN = "http://";
	
	static final String IMAGES_URL = PropertyUtil.getPropertyValue("imagesUrl");
	static final String SUBSCRIBER_URL = PropertyUtil.getPropertyValue("subscriberUrl");
	//static final String _URL = PropertyUtil.getPropertyValue("subscriberUrl");
	
	static final String APP_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String SCHEDULER_URL = PropertyUtil.getPropertyValue("schedulerUrl");
	static final String USER_PARENT_DIRECTORY = PropertyUtil.getPropertyValue("usersParentDirectory");
	static final String DIV_TEMPLATE = PropertyUtil.getPropertyValue("divTemplate");
	static final String FOOTER_TEXT = PropertyUtil.getPropertyValue("footerText");
	static final String AUTO_EMAIL_FOOTER_TEXT =  PropertyUtil.getPropertyValue("autoEMailFooterText");
	static final String OPTIN_FOOTER_TEXT = PropertyUtil.getPropertyValue("doubleOptinFooterText");
    static final String LINK_PATTERN = PropertyUtil.getPropertyValue("LinkPattern");
	static final String CLICK_TRACK_URL = PropertyUtil.getPropertyValue("ClickTrackUrl");
	static final String AUTO_EMAIL_CLICK_TRACK_URL = PropertyUtil.getPropertyValue("AutoEmailClickTrackUrl");
	
	static final String UNSUBSCRIBE_URL = PropertyUtil.getPropertyValue("unSubscribeUrl");
	static final String OPEN_TRACK_URL = PropertyUtil.getPropertyValue("OpenTrackUrl");
	static final String AUTO_EMAIL_OPEN_TRACK_URL = PropertyUtil.getPropertyValue("AutoEmailOpenTrackUrl");
	
	static final String UPDATE_SUBS_FOOTER_TEXT = PropertyUtil.getPropertyValue("updateSubsFooterText");
	
	static final String UPDATE_SUBSCIRIPTION_URL = PropertyUtil.getPropertyValue("updateSubscriptionLink");
	static final String UPDATE_SUBSCIRIPTION_TEXT = PropertyUtil.getPropertyValue("updateSubHTMLTxt");
	
	
	static final String SENDER_FOOTER_TEXT=PropertyUtil.getPropertyValue("senderFooterText");
	

	static final String SYS_DATA_URL = SUBSCRIBER_URL + "SystemData/Templates/";
	static final String USER_DATA_URL = IMAGES_URL+"/UserData/";
	
	
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	
	
	/**
	 * Prepares final html string for test mail.
	 * @param campaign
	 * @param htmlStuff
	 * @return String
	 */
	public static String prepareStuff(Campaigns campaign, String htmlStuff, 
							EmailContent emailContent, boolean isTestMail, String userDomainStr) {
		String TestUnSubscribeUrl = PropertyUtil.getPropertyValue("testUnSubscribeUrl");
		if(htmlStuff == null && isTestMail && emailContent == null) {
			if(logger.isWarnEnabled()) logger.warn(" Input content is null for test mail");
			return null;
		}
		
		StringBuffer contentSb = null;
		
		if(isTestMail) {
			contentSb = new StringBuffer(htmlStuff);
		}
		else if(emailContent != null) {
			
			if(emailContent.getHtmlContent() == null) {
				if(logger.isWarnEnabled()) logger.warn(" Html content is null in the EmailContent object");
				return null;
			}
			contentSb = new StringBuffer(emailContent.getHtmlContent());
		}
		else {
			contentSb = new StringBuffer(campaign.getHtmlText());
		}
		
		StringBuffer sb = new StringBuffer();
		
		String replaceStr;
		String userName = campaign.getUsers().getUserName();
		int options = 0;
		options |= 128; 	//This option is for Case insensitive
		options |= 32; 		//This option is for Dot matches newline
		String copyToStr = USER_PARENT_DIRECTORY + "/" + userName + "/Email/" + campaign.getCampaignName() + "/";
		
		if(logger.isDebugEnabled()) {
			logger.debug(" UserName of the campaign -"+campaign.getCampaignName()+" : "+userName);
		}
		
		/**
		 *   removes scripts tags 
		 */
		Pattern r = Pattern.compile(JS_SCRIPT_PATTERN, options);
		Matcher m = r.matcher(contentSb);
		while (m.find()) {
			replaceStr = "";
			if(logger.isDebugEnabled()) logger.debug("Replaced String1 : " + replaceStr);
			m.appendReplacement(sb, replaceStr);
		}
		m.appendTail(sb);
		
		if(logger.isDebugEnabled())  logger.debug("--------Script tags are striped----------");

		
		/**
		 * remove TMCE related stuff
		 */
		contentSb = sb;
		sb = new StringBuffer();
		r = Pattern.compile(TMCE_URL_PATTERN, Pattern.CASE_INSENSITIVE);
		m = r.matcher(contentSb);
		while (m.find()) {
			replaceStr = "<div name=\"TMCEeditableDiv\" >";
			if(logger.isDebugEnabled()) logger.debug ("Replaced String2 :" + replaceStr);
			m.appendReplacement(sb, replaceStr);
		}
		m.appendTail(sb);
		if(logger.isDebugEnabled())  logger.debug("------editor stuff is replaced---------");

		
		/**
		 *  handles SystemData images in background urls
		 */
		contentSb = sb;
		sb = new StringBuffer();
		r = Pattern.compile(URL_SYSTEM_DATA_PATTERN, Pattern.CASE_INSENSITIVE);
		m = r.matcher(contentSb);
		while (m.find()) {
			replaceStr = "url(" + Utility.encodeSpace(SYS_DATA_URL +  m. group(2)) + ")";
			if(logger.isDebugEnabled()) logger.debug("Replaced String3 : " + replaceStr);
			m.appendReplacement(sb, replaceStr);
		}
		m.appendTail(sb);
		if(logger.isDebugEnabled()) logger.debug("---------SystemData Images are replaced---------");


		/**
		 *  handles image URLs
		 */
		contentSb = sb;
		sb = new StringBuffer();
		r = Pattern.compile(IMG_PATTERN, Pattern.CASE_INSENSITIVE);
		m = r.matcher(contentSb);
		while (m.find()) {
			String url = m.group(2);
			replaceStr = url;
			if(!url.contains(HTTP_TOKEN)){
				if(url.contains("UserData/" + userName + "/Gallery")){
					String fromPath = url.substring(url.indexOf(userName), url.length());
					if(logger.isDebugEnabled()) logger.debug("from Path : " +  fromPath);
					
					String[] tokenArr = fromPath.split("/");
					String img = tokenArr[tokenArr.length -1];
					replaceStr = USER_DATA_URL + img;
					String from = USER_PARENT_DIRECTORY + "/" + fromPath ;
					if(logger.isDebugEnabled()) logger.debug(" from :" + from);
					try {
						FileUtil.copyFile(new File(Utility.unescape(from)), 
								new File(Utility.unescape(copyToStr)));
					} catch (IOException e) {
						if(logger.isWarnEnabled()) logger.warn("Unable to copy the image form : " + from + "\n", e);
					}
				}
				else if(url.contains("SystemData")) {
					replaceStr = SUBSCRIBER_URL + url.substring(url.indexOf("SystemData"), url.length());
				}
				else if(url.contains("tinymce")) {
					replaceStr = SUBSCRIBER_URL + url.substring(url.indexOf("js/tinymce"), url.length());
				}
				if(logger.isDebugEnabled()) logger.debug("Replaced String4 : " + Utility.encodeSpace(replaceStr));
				replaceStr = m.group().replace(m.group(2), Utility.encodeSpace(replaceStr));
				m.appendReplacement(sb, replaceStr);
				
			}
			if(url.startsWith(APP_URL)){
				if(url.contains("UserData/" + userName + "/Gallery")){
					replaceStr=m.group(2).replace(APP_URL, ImageServer_Url);
					if(logger.isDebugEnabled()) logger.debug("after replacing : " +  replaceStr);
				}
				replaceStr = m.group().replace(m.group(2), Utility.encodeSpace(replaceStr));
				m.appendReplacement(sb, replaceStr);
			}
			
		}
		m.appendTail(sb);
		if(logger.isDebugEnabled()) logger.debug("------- Image URLs are replaced --------");
		
		
		
		
		/**
		 * handles for replacing oldApplicationURL with NewApplicationURL
		 */
		/*contentSb = sb;
		sb = new StringBuffer();
		r = Pattern.compile(IMG_PATTERN, Pattern.CASE_INSENSITIVE);
		m = r.matcher(contentSb);
		while (m.find()) {
			String url = m.group(2);
			replaceStr = url;
			if(url.startsWith(APP_URL)){
				if(url.contains("UserData/" + userName + "/Gallery")){
					replaceStr=m.group(2).replace(APP_URL, ImageServer_Url);
					if(logger.isDebugEnabled()) logger.debug("after replacing : " +  replaceStr);
				}
				replaceStr = m.group().replace(m.group(2), Utility.encodeSpace(replaceStr));
				m.appendReplacement(sb, replaceStr);
			}
			
		}*/
		/*m.appendTail(sb);
		if(logger.isDebugEnabled()) logger.debug("------- OLdApplicationURL is replaced with  NewApplicationURL --------");
*/
		
		
		
		
		
		
		
		
		/**
		 *  handles background images in content
		 */
		contentSb = sb;
		sb = new StringBuffer();
		r = Pattern.compile(BG_IMG_PATTERN, options);
		m = r.matcher(contentSb);
		while (m.find()) {
			String orgUrl = m.group();
			String url = m.group(1);
			if(!url.contains("http://")){
				String appIp =  PropertyUtil.getPropertyValue("subscriberIp");
				url = "background=\"" + appIp + url + "\"";
			}
			else {
				url = orgUrl;
			}
			if(logger.isDebugEnabled()) logger.debug ("Replaced String5 :" + url);
			m.appendReplacement(sb, url);
		}
		m.appendTail(sb);
		if(logger.isDebugEnabled()) logger.debug("-------- backgound attributes are replaced ---------");
		
		
		if(isTestMail==false) {
			/**
			 *  converts links with click track 
			 */
			contentSb = sb;
			sb = new StringBuffer();
		    r = Pattern.compile(LINK_PATTERN, options);
		    m = r.matcher(contentSb);
		    String anchorUrl;
	        while (m.find()) {
				anchorUrl = m.group(2).trim();
				if(logger.isDebugEnabled()) logger.debug("Anchor Tag : " + anchorUrl);
				try{
		            if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
		            	continue;
		            }
		            	
		            if(anchorUrl.indexOf("/Scheduler/updateReport.mqrm?requestedAction=") != -1 ) {
		            	if(anchorUrl.indexOf("&amp;url=") != -1) {
		            		anchorUrl = anchorUrl.substring(anchorUrl.indexOf("&amp;url=")+9).trim();
		            	}
		            	else {
		            		continue;
		            	}
		            }
		            
	            	if(!anchorUrl.startsWith(HTTP_TOKEN) && !(anchorUrl.startsWith("https://"))) {
	            		
	            			anchorUrl = HTTP_TOKEN + anchorUrl;
	            	}
	            	
	            	if(logger.isDebugEnabled()) logger.debug("URL is: " + anchorUrl);
	            	anchorUrl = Utility.encodeUrl(anchorUrl);
	            	
	            	//Start Google Analytics code
	            	if(campaign.isGoogleAnalytics()){
	            	
	            		String decodeUrl = Utility.decodeUrl(anchorUrl);
	            		URI redirectUrl = new URI(decodeUrl);
	            		String redirectUrlQryStr = redirectUrl.getQuery();
	            		String gaStr = "utm_source="+campaign.getCampaignName().trim()+
								"&utm_medium=email&utm_campaign="+campaign.getGoogleAnalyticsCampTitle();
	            		if(redirectUrlQryStr == null || redirectUrlQryStr.length() <= 0){
	            			redirectUrlQryStr = gaStr;
	            		}
	            		else{
	            			redirectUrlQryStr += "&"+gaStr;
	            		}
	            		redirectUrl.setQuery(redirectUrlQryStr);
	            		anchorUrl = Utility.encodeUrl(redirectUrl.toString());
	            		logger.info("anchorUrl after GA string addition :"+anchorUrl);
	            	}
	            	//End Google Analytics code
	            	
					replaceStr = CLICK_TRACK_URL.replace(PlaceHolders.PH_CLICK_URL, anchorUrl);;
					replaceStr = m.group().replace(m.group(2), replaceStr);
		            if(logger.isDebugEnabled()) logger.debug("Replaced String6 " + replaceStr);
	                m.appendReplacement(sb, replaceStr);
				}catch (Exception e) {
					logger.error("** Exception : Problem while encoding the URL " + e);
				}
				
	        } //while
		    m.appendTail(sb);
		    if(logger.isDebugEnabled()) logger.debug("------ URLs are converted --------");
		} // if(isTestMail==false)
		
		//Add Permission reminder
		
		if(campaign.isPermissionRemainderFlag()){
			//TODO need to add the new zul source url
			String permissionRemStr = isTestMail ? TestUnSubscribeUrl : UNSUBSCRIBE_URL;
			String permRemText = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, 
											campaign.getPermissionRemainderText() +
											"<br/> You may <a href='"+(permissionRemStr)+"'>unsubscribe</a>" +
											" if you do not wish to receive our mails");
			sb.insert(0, permRemText);
		}

		//Add webpageclick here
		if(campaign.isWebLinkFlag()) {
			
			String webLinkText = campaign.getWebLinkText();
			String webLinkUrlText = campaign.getWebLinkUrlText();
			String weblinkUrl ="#";
			
			if(isTestMail==false) { 
				weblinkUrl = PropertyUtil.getPropertyValue("weblinkUrl");
				if(logger.isDebugEnabled()) logger.debug("web url before: " + weblinkUrl);
				weblinkUrl = weblinkUrl.replace(PlaceHolders.PH_CAMPAIGN_ID, campaign.getCampaignId().toString());
				if(logger.isDebugEnabled()) logger.debug("web url after: " + weblinkUrl);
			}else{
				
				weblinkUrl = PropertyUtil.getPropertyValue("parentalWeblinkUrl");
			}
			
			String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
			sb.insert(0,webUrl);
			
		}
		if(logger.isDebugEnabled()) logger.debug(" addWebLink end --");

		/**
		 * Adds Footer
		 */
		String senderAddress = "";
		String orgAddress = "";
		String[] orgUnitAndName;
		Users user = campaign.getUsers();
		
		if(!isTestMail) {
			
			//senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,getSenderAddress(campaign, false));
			orgUnitAndName = getOrgAndSenderAddress(campaign, false, "");
			//orgAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgUnitAndName[0]);
			//senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgUnitAndName[1]);
			orgAddress = orgUnitAndName[0];
			senderAddress = orgUnitAndName[1];
		}else {
			
			
			
						//senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,getSenderAddress(campaign.getUsers(), false));
						// need to pass campaign instead user
												
						if(campaign.getAddrsType() != null && !campaign.getAddrsType().isEmpty()) {
							
							if(campaign.getAddrsType().startsWith(Constants.CAMP_ADDRESS_TYPE_USER) || 
								 campaign.getAddrsType().startsWith(Constants.CAMP_ADDRESS_TYPE_STORE ) ) 
						   {
							
								logger.debug("inside condition addrs_type not empty or null");
								orgUnitAndName = getOrgAndSenderAddress(campaign, false, "");
								orgAddress = orgUnitAndName[0];
								senderAddress = orgUnitAndName[1];
								
								logger.info("address is "+ orgUnitAndName[1]);
							}
							else
								logger.info("address type is null cant take store or user address");
							
						}else {
							String includeStr = campaign.getIncludeBeforeStr();
							
							logger.debug("inside condition addrs_type  empty or null");
							if(includeStr != null && includeStr.trim().length() > 0 ) {
								
								includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
								
								
								
							}//if
							orgUnitAndName = getOrgAndSenderAddress(user, false, "");
							
							if(includeStr != null) orgAddress = includeStr;
							else orgAddress = orgUnitAndName[0];
							senderAddress = orgUnitAndName[1];
						}
						//orgAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgUnitAndName[0]);
						//senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgUnitAndName[1]);
						
						
						
		}
		//logger.debug("##############footer address######################## "+senderAddress);
		
		String footerTextStr="";
		String updateSusText="";
		
		//Added for Update subscription link
		
		if(!user.getSubscriptionEnable() || campaign.getCategories() == null ){
			
			
			
			
			footerTextStr = FOOTER_TEXT.replace(PlaceHolders.PH_SENDER_ADDR,senderAddress);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_ORGANIZATION_UNITANDNAME, orgAddress);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_SENDER_EMAIL, campaign.getFromEmail());
			
			if(isTestMail) {
				String openTrackImg = SCHEDULER_URL + "/img/transparent.gif";
				footerTextStr = footerTextStr.replace(PlaceHolders.PH_OPEN_TRACK_URL, openTrackImg);
			}
			else {
				footerTextStr = footerTextStr.replace(PlaceHolders.PH_OPEN_TRACK_URL, OPEN_TRACK_URL);
			}
			//TODO need to add the new zul source url
			String unsubUrl = isTestMail ? TestUnSubscribeUrl : UNSUBSCRIBE_URL;
			sb.append(footerTextStr.replace(PlaceHolders.PH_UNSUBSCRIBE_URL, (unsubUrl)));	
		}
		else{
			
			
			
			updateSusText = UPDATE_SUBS_FOOTER_TEXT.replace(PlaceHolders.PH_SENDER_ADDR,senderAddress);
			updateSusText = updateSusText.replace(PlaceHolders.PH_ORGANIZATION_UNITANDNAME, orgAddress);
			updateSusText = updateSusText.replace(PlaceHolders.PH_SENDER_EMAIL, campaign.getFromEmail());
			
			if(isTestMail) {
				String openTrackImg = SCHEDULER_URL + "/img/transparent.gif";
				updateSusText = updateSusText.replace(PlaceHolders.PH_OPEN_TRACK_URL, openTrackImg);
			}
			else {
				updateSusText = updateSusText.replace(PlaceHolders.PH_OPEN_TRACK_URL, OPEN_TRACK_URL);
			}
			updateSusText = updateSusText.replace(PlaceHolders.PH_UPDATE_SUBSCIRIPTION_URL, (isTestMail?"#":UPDATE_SUBSCIRIPTION_URL));
			//TODO need to add the new zul source url
			String unsubUrl = isTestMail ? TestUnSubscribeUrl : UNSUBSCRIBE_URL;
			sb.append(footerTextStr.replace(PlaceHolders.PH_UNSUBSCRIBE_URL, (unsubUrl)));	
			
			//sb.append(updateSusText.replace(PlaceHolders.PH_UNSUBSCRIBE_URL, (isTestMail?"#":UNSUBSCRIBE_URL)));
			
			
		}
		
		
		/*if(user.getSubscriptionEnable()){
			
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_UPDATE_SUBSCIRIPTION_TEXT, UPDATE_SUBSCIRIPTION_TEXT);
			
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_UPDATE_SUBSCIRIPTION_URL, UPDATE_SUBSCIRIPTION_URL);
		}else{
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_UPDATE_SUBSCIRIPTION_TEXT, "");
		}
		
		sb.append(footerTextStr.replace(PlaceHolders.PH_UNSUBSCRIBE_URL, (isTestMail?"#":UNSUBSCRIBE_URL)));	*/
		
		
		if(isTestMail) {
			
			String testStr = "";
			if(IsBarcodeExist(sb.toString())){
				
				testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
					"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
					"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
					"This is a test email for your preview. Some links may not work. Promotions/ Barcode is indicative only.<br/></span></DIV>";
			}
			else{
				
				testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
					"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
					"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
					"This is a test email for your preview. Some links may not work.<br/></span></DIV>";
			}
			sb.insert(0, testStr);
		}
		//wrap the conent in HTML Tag
		//sb.insert(0, "<HTML><HEAD></HEAD><BODY>");
		//sb.append("</BODY></HTML>");
		//************* for mobile templates************
		
		String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
		//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
		sb.insert(0, mobileHeadStr);
		sb.append("</body></html>");
		
		//*********************
		if(logger.isDebugEnabled()) logger.debug("------ Footer added------");
		String encUserId = null;
		try {
			encUserId = EncryptDecryptUrlParameters.encrypt(campaign.getUsers().getUserId()+Constants.STRING_NILL);
		} catch (Exception e) {
			logger.error("Exception ::::", e);
		}
		String htmlContent = sb.toString().replace(PlaceHolders.PH_USER_ID, encUserId);
		
		//htmlContent = htmlContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
		
		htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
		
		htmlContent = replaceCouponImgSrcWithMqrm(htmlContent, isTestMail);
		
		//end coupon image code
		
		//logger.debug("htmlcontent in prepare stuff*****************"+htmlContent);
		return htmlContent;
	}
	
	
	/**
	 * Prepares the final html for the campaign to send
	 * @param campaign
	 * @param isTestMail
	 * @return
	 */
	public static String prepareStuff(Campaigns campaign) {
		
		if(campaign.getHtmlText() == null) {
			if(logger.isWarnEnabled()) logger.warn(" *********** Input content is null");
			return null;
		}
		
	//	if(logger.isInfoEnabled()) logger.info(">>>>>>>> just for testing");
	//	if(logger.isInfoEnabled()) logger.info(">>>>Preparing content for campaign :"+campaign.getCampaignName());
		return prepareStuff(campaign, null, null, false, "");
		
	}
	
	/**
	 * prepares the final content for double opt-in mails to be sent
	 * @param customTemplate
	 * @return 
	 */
	public static String prepareDoubleOptInStuff(CustomTemplates customTemplate, boolean isTestMail, String contentStr, Users user, String userDomainStr) {
		
		/*if(customTemplate == null && contentStr != null) {
			
			StringBuffer contentSb = null;
			
			contentSb = new StringBuffer(customTemplate.getHtmlText());
			
			
			StringBuffer sb = new StringBuffer(customTemplate.getHtmlText());
			
			//TODO need to prepare stuff with default mail footer address and 
			
			return null;
		*/
		
			
			StringBuffer contentSb = null;
			StringBuffer sb = null;
			
			//these are serves as default web link url
			/*String webLinkText = PropertyUtil.getPropertyValue("optInWebLinktext");
			String webLinkUrlText = PropertyUtil.getPropertyValue("optInWebLinkUrltext");
			String weblinkUrl ="#";
			 if(isTestMail==false) { 
					weblinkUrl = PropertyUtil.getPropertyValue("optInWeblinkUrl");
					
				}*/
			
			//in case of default custom template user address should be taken as sender address
		
			
			String[] orgUnitAndName = getOrgAndSenderAddress(user, false, userDomainStr);
			//String senderAddressStr = getSenderAddress(user, false);
			String senderAddressStr = orgUnitAndName[1];
			String orgAddressStr = orgUnitAndName[0];
			//in case of default custom template user email should be taken as from email
			String fromEmail = user.getEmailId();
			
			if(customTemplate != null) {
				contentSb = new StringBuffer(customTemplate.getHtmlText());
				sb = new StringBuffer(customTemplate.getHtmlText());
				
				//Add Permission reminder
				
				
				if(customTemplate.isPermissionRemainderFlag()){
					
					String permRemText = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, 
													customTemplate.getPermissionRemainderText()
													);
					sb.insert(0, permRemText);
				}
				
				//Add webpage
				/*if(customTemplate.isWebLinkFlag()) {
					
					 webLinkText = customTemplate.getWebLinkText();
					 webLinkUrlText = customTemplate.getWebLinkUrlText();
					 
					
						
					 String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
					sb.insert(0,webUrl);
							
					 
					 String webLinkText = PropertyUtil.getPropertyValue("optInWebLinktext");
						String webLinkUrlText = PropertyUtil.getPropertyValue("optInWebLinkUrltext");
						String weblinkUrl ="#";
					 
					 
				}*/
				
				//add sender address
				//senderAddressStr = getSenderAddress(customTemplate, false);
				
				orgUnitAndName = getOrgAndSenderAddress(customTemplate, false, "");
				orgAddressStr = orgUnitAndName[0];
				senderAddressStr = orgUnitAndName[1];
				
				
				
				
				//from email
				fromEmail = customTemplate.getFromEmail();
				
				
			}else {
				
				contentSb = new StringBuffer(contentStr);
				sb = new StringBuffer(contentStr);
				
				
				
				/* String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
					sb.insert(0,webUrl);
					*/		
				
			}
			
			
			
			if(logger.isDebugEnabled()) logger.debug(" addWebLink end --");
			

			/**
			 * Adds Footer
			 */
			//String senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,senderAddressStr);
			//String orgAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgAddressStr);
			String footerTextStr = OPTIN_FOOTER_TEXT.replace(PlaceHolders.PH_SENDER_ADDR,senderAddressStr);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_ORGANIZATION_UNITANDNAME, orgAddressStr);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_SENDER_EMAIL, fromEmail);
			
			
			sb.append(footerTextStr);
			if(isTestMail) {
				
				String testStr = "";
				if(IsBarcodeExist(sb.toString())){
					testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
						"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
						"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
						"THIS IS A TEST EMAIL ONLY.PERSONALIZATION AND SYSTEM GENERATED LINKS WILL NOT WORK.<br/>BARCODE IS INDICATIVE ONLY AND NOT THE ACTUAL CODE.<br/></span></DIV>";
				}
				else{
				   testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
						"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
						"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
						"THIS IS A TEST EMAIL ONLY.PERSONALIZATION AND SYSTEM GENERATED LINKS WILL NOT WORK.<br/></span></DIV>";
				}
				sb.insert(0, testStr);
			}
			// commented after responsive  in auto emails
			/*
			//wrap the conent in HTML Tag
			sb.insert(0, "<HTML><HEAD></HEAD><BODY>");
			//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth]{width:100% !important; height:auto !important;} *[class=headerblock] {width:100% !important; height:auto !important;}	*[class=headerblock] img{width:100% !important; height:auto !important;}	*[class=prodblock]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock] img{width:100% !important; height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			//sb.insert(0, mobileHeadStr);
			sb.append("</BODY></HTML>");
			*/
			
			// Added after responsive header code  in Double Opt-in( auto emails)
			
			String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			sb.insert(0, mobileHeadStr);
			sb.append("</body></html>");
			
			
			
			if(logger.isDebugEnabled()) logger.debug("------ Footer added------");
			String htmlContent = sb.toString().replace(PlaceHolders.PH_USER_ID, user.getUserId().toString());//not needed
			
			htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
			
			htmlContent = replaceCouponImgSrcWithMqrm(htmlContent, isTestMail);
			
			return htmlContent;
		
		
		
	}//prepareDoubleOptInStuff
	
public static String prepareParentalOptInStuff(CustomTemplates customTemplate, boolean isTestMail, String contentStr, Users user, String userDomainStr) {
			
			StringBuffer contentSb = null;
			
			//these are serves as default web link url
			String webLinkText = PropertyUtil.getPropertyValue("optInWebLinktext");
			String webLinkUrlText = PropertyUtil.getPropertyValue("optInWebLinkUrltext");
			String weblinkUrl ="#";
			
			 if(isTestMail==false) { 
				 
					weblinkUrl = PropertyUtil.getPropertyValue("parentalWeblinkUrl");
					
			}
			
			//in case of default custom template user address should be taken as sender address
			//String senderAddressStr = getSenderAddress(user, false);
			 
			 String[] orgUnitAndName = getOrgAndSenderAddress(user, false, userDomainStr);
			 String senderAddressStr = orgUnitAndName[1];
			 String orgAddressStr = orgUnitAndName[0];
			
			//in case of default custom template user email should be taken as from email
			String fromEmail = user.getEmailId();
			
			contentSb = new StringBuffer(contentStr);
			//StringBuffer sb =   new StringBuffer(contentStr);
			
			// Added for autoEmails clicks and opens
			StringBuffer sb =  new StringBuffer();
			
			String replaceStr;
			int options = 0;
			options |= 128; 	//This option is for Case insensitive
			options |= 32; 		//This option is for Dot matches newline
			

			
			
			/**
			 *  converts links with click track 
			 */
			
			Pattern  r = Pattern.compile(LINK_PATTERN, options);
			Matcher  m = r.matcher(contentSb);
		    String anchorUrl;
	        while (m.find()) {
				anchorUrl = m.group(2).trim();
				logger.info("Anchor Tag : " + anchorUrl);
				try{
		            if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
		            	continue;
		            }
		            	
		            if(anchorUrl.indexOf("/susbscriber/updateAutoEmailReport.mqrm?action=") != -1 ) {
		            	if(anchorUrl.indexOf("&amp;url=") != -1) {
		            		anchorUrl = anchorUrl.substring(anchorUrl.indexOf("&amp;url=")+9).trim();
		            	}
		            	else {
		            		continue;
		            	}
		            }
		            
	            	if(!anchorUrl.startsWith(HTTP_TOKEN) && !(anchorUrl.startsWith("https://"))) {
	            		
	            			anchorUrl = HTTP_TOKEN + anchorUrl;
	            	}
	            	
	            	anchorUrl = Utility.encodeUrl(anchorUrl);
					replaceStr = AUTO_EMAIL_CLICK_TRACK_URL.replace(Constants.AUTO_EMAIL_CLICK_TRACK_URL, anchorUrl);;
					replaceStr = m.group().replace(m.group(2), replaceStr);
	                m.appendReplacement(sb, replaceStr);
				}catch (Exception e) {
					logger.info("** Exception : Problem while encoding the URL " + e);
				}
				
	        } //while
		    m.appendTail(sb);
		    
		    //end autoEmails clicks and opens
			
			if(customTemplate != null) {
				
				//Add Permission reminder
				
				
				if(customTemplate.isPermissionRemainderFlag()){
					
					String permRemText = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, 
													customTemplate.getPermissionRemainderText()
													);
					sb.insert(0, permRemText);
				}
				
				//Add webpage
				if(customTemplate.isWebLinkFlag()) {
					
					
					 
					 webLinkText = customTemplate.getWebLinkText();
					 webLinkUrlText = customTemplate.getWebLinkUrlText();
					
						
					 String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
					sb.insert(0,webUrl);
					 
					 
				}
				
				//add sender address
				//senderAddressStr = getSenderAddress(customTemplate, false);
				 orgUnitAndName = getOrgAndSenderAddress(customTemplate, false, "");
				 senderAddressStr = orgUnitAndName[1];
				 orgAddressStr = orgUnitAndName[0];
				
				//from email
				fromEmail = customTemplate.getFromEmail();
				
				
			}else {
				
				 String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
					sb.insert(0,webUrl);
							
				
			}
			
				
			
			if(logger.isDebugEnabled()) logger.debug(" addWebLink end --");
			

			/**
			 * Adds Footer
			 */
			//String senderAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,senderAddressStr);
			//String orgAddress = DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,orgAddressStr);
			String footerTextStr = OPTIN_FOOTER_TEXT.replace(PlaceHolders.PH_SENDER_ADDR,senderAddressStr);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_ORGANIZATION_UNITANDNAME, orgAddressStr);
			footerTextStr = footerTextStr.replace(PlaceHolders.PH_SENDER_EMAIL, fromEmail);
			
			sb.append(footerTextStr);
			if(isTestMail) {
				String testStr = "";
				if(IsBarcodeExist(sb.toString())){
					
					testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
						"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
						"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
						"THIS IS A TEST EMAIL ONLY.PERSONALIZATION AND SYSTEM GENERATED LINKS WILL NOT WORK.<br/>BARCODE IS INDICATIVE ONLY AND NOT THE ACTUAL CODE.<br/></span></DIV>";
				}
				else{
				    testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
						"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
						"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
						"THIS IS A TEST EMAIL ONLY.PERSONALIZATION AND SYSTEM GENERATED LINKS WILL NOT WORK.<br/></span></DIV>";
				}
				sb.insert(0, testStr);
			}
			
			// commented after responsive  in auto emails
			/*
			//wrap the conent in HTML Tag
			sb.insert(0, "<HTML><HEAD></HEAD><BODY>");
			//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth]{width:100% !important; height:auto !important;} *[class=headerblock] {width:100% !important; height:auto !important;}	*[class=headerblock] img{width:100% !important; height:auto !important;}	*[class=prodblock]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock] img{width:100% !important; height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			//sb.insert(0, mobileHeadStr);
			sb.append("</BODY></HTML>");
			*/
			
			// Added after responsive header code in Parental consent( auto emails)

			String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			sb.insert(0, mobileHeadStr);
			

			if(logger.isDebugEnabled()) logger.debug("------ Footer added------");
			String htmlContent = sb.toString().replace(PlaceHolders.PH_USER_ID, user.getUserId().toString());//not needed
			
			htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
			
			htmlContent = replaceCouponImgSrcWithMqrm(htmlContent, isTestMail);
			
			
			
			String openTrackUrl = Constants.AUTO_EMAIL_OPEN_TRACK_URL;
			   // String openTrackImg = SUBSCRIBER_URL+ "img/digitransparent.gif";
			    footerTextStr =AUTO_EMAIL_FOOTER_TEXT.replace(openTrackUrl, AUTO_EMAIL_OPEN_TRACK_URL);
				
				//sb.append(footerTextStr.replace(footerTextStr, openTrackImg));
			    htmlContent += footerTextStr;
			    htmlContent += "</body></html>";
			    logger.info("htmlcontent :: ",htmlContent);
			    
			return htmlContent;
		
		
		
	}//prepareDoubleOptInStuff
	
	
	
	
	/**
	 *  Sender address
	 */
	/*public static String getSenderAddress(Object obj, boolean isPrepared){
		logger.debug("Just entered");
		String usraddr = "";
		
		
		
		if(obj != null && obj instanceof Campaigns) {
			
			Campaigns campaign = (Campaigns)obj;
			Address address = campaign.getAddress();
			
			String includeStr = campaign.getIncludeBeforeStr();
			
			
			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, " | ");
				usraddr = usraddr +includeStr+ " | ";
				
				
			}//if
			
			if(campaign.isAddressFlag()){
				
				if(address.getDinamicAddrstr() != null ) {
					
					usraddr = usraddr + address.getDinamicAddrstr();
				}
				else {
					if(address.getAddressOne()!=null && !address.getAddressOne().trim().equals("")){
						usraddr = usraddr +  address.getAddressOne(); 
					}
					if(address.getAddressTwo()!=null && !address.getAddressTwo().trim().equals("")){
						usraddr = usraddr + " | " + address.getAddressTwo(); 
					}
					if(address.getCity()!=null && !address.getCity().trim().equals("")){
						usraddr = usraddr + " | " + address.getCity(); 
					}
					if(address.getState()!=null && !address.getState().trim().equals("")){
						usraddr = usraddr + " | " + address.getState(); 
					}
					if(address.getCountry()!=null && !address.getCountry().trim().equals("")){
						usraddr = usraddr + " | " + address.getCountry(); 
					}
					if(address.getPin()!=null && address.getPin()!=0){
						usraddr = usraddr + " | " + address.getPin(); 
					}
					if(address.getPhone()!=null && !address.getPhone().trim().equals("")){
						usraddr = usraddr + " | " + address.getPhone(); 
					}
					
					if(usraddr.length()>2)
						usraddr = usraddr.substring(2, usraddr.length()).trim();
					
					logger.debug("addr : " + usraddr);
				}
			}
		
		}
		
		else if(obj != null && obj instanceof CustomTemplates) {
			
			CustomTemplates customTemplates = (CustomTemplates)obj;
			Address address = customTemplates.getAddress();
			
			String includeStr = customTemplates.getIncludeBeforeStr();
			
			
			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, " | ");
				usraddr = usraddr +includeStr+ " | ";
				
				
			}//if
			
			
			
			if(customTemplates.isAddressFlag() ){
				if(address.getAddressOne()!=null && !address.getAddressOne().trim().equals("")){
					usraddr = usraddr +  address.getAddressOne(); 
				}
				if(address.getAddressTwo()!=null && !address.getAddressTwo().trim().equals("")){
					usraddr = usraddr + " | " + address.getAddressTwo(); 
				}
				if(address.getCity()!=null && !address.getCity().trim().equals("")){
					usraddr = usraddr + " | " + address.getCity(); 
				}
				if(address.getState()!=null && !address.getState().trim().equals("")){
					usraddr = usraddr + " | " + address.getState(); 
				}
				if(address.getCountry()!=null && !address.getCountry().trim().equals("")){
					usraddr = usraddr + " | " + address.getCountry(); 
				}
				if(address.getPin()!=null && address.getPin()!=0){
					usraddr = usraddr + " | " + address.getPin(); 
				}
				if(address.getPhone()!=null && !address.getPhone().trim().equals("")){
					usraddr = usraddr + " | " + address.getPhone(); 
				}
				
				if(usraddr.length()>2)
					usraddr = usraddr.substring(2, usraddr.length()).trim();
				
				logger.debug("addr : " + usraddr);
			}
			
			
		} else if(obj != null && obj instanceof Users) {
			
			Users user = (Users)obj;
			
			
			String includeStr = user.getUserOrganization().getOrganizationName()+" | "+user.getUserDomainStr();

			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, " | ");
				usraddr = usraddr +includeStr+ " | ";
				
				
			}//if
			
			
			if(user.getAddressOne()!=null && !user.getAddressOne().trim().equals("")){
				usraddr = usraddr  + user.getAddressOne(); 
			}
			if(user.getAddressTwo()!=null && !user.getAddressTwo().trim().equals("")){
				usraddr = usraddr + " | " + user.getAddressTwo(); 
			}
			if(user.getCity()!=null && !user.getCity().trim().equals("")){
				usraddr = usraddr + " | " + user.getCity(); 
			}
			if(user.getState()!=null && !user.getState().trim().equals("")){
				usraddr = usraddr + " | " + user.getState(); 
			}
			if(user.getCountry()!=null && !user.getCountry().trim().equals("")){
				usraddr = usraddr + " | " + user.getCountry(); 
			}
			if(user.getPinCode()!=null && !user.getPinCode().trim().equals("")){
				usraddr = usraddr + " | " + user.getPinCode(); 
			}
			if(user.getPhone()!=null && !user.getPhone().trim().equals("")){
				usraddr = usraddr + " | " + user.getPhone(); 
			}
			
			
			if(usraddr.length()>2)
				usraddr = usraddr.substring(2, usraddr.length()).trim();
			
			logger.debug("addr : " + usraddr);
			
			
			
			
		}
		
		logger.debug("Exiting");
		return usraddr;
	}*/
	

	
	public static String[] getOrgAndSenderAddress(Object obj, boolean isPrepared, String userDomainStr){
		if(logger.isDebugEnabled()) logger.debug("Just entered");
		String[] usraddr = {"",""};
		
		
		
		if(obj != null && obj instanceof Campaigns) {
			
			Campaigns campaign = (Campaigns)obj;
			Address address = campaign.getAddress();
			
			String includeStr = campaign.getIncludeBeforeStr();
			
			
			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				usraddr[0] = usraddr[0]+includeStr;
				
				
			}//if
			
			if(campaign.isAddressFlag()){
				
				if(address.getDinamicAddrstr() != null ) {
					
					usraddr[1] = usraddr[1] + address.getDinamicAddrstr();
				}
				else {
					if(address.getAddressOne()!=null && !address.getAddressOne().trim().equals("")){
						usraddr[1] = usraddr[1] +  address.getAddressOne(); 
					}
					if(address.getAddressTwo()!=null && !address.getAddressTwo().trim().equals("")){
						usraddr[1] = usraddr[1] + ", " + address.getAddressTwo(); 
					}
					if(address.getCity()!=null && !address.getCity().trim().equals("")){
						usraddr[1] = usraddr[1] + ", " + address.getCity(); 
					}
					if(address.getState()!=null && !address.getState().trim().equals("")){
						usraddr[1] = usraddr[1] + ", " + address.getState(); 
					}
					if(address.getCountry()!=null && !address.getCountry().trim().equals("")){
						usraddr[1] = usraddr[1] + ", " + address.getCountry(); 
					}
					if(address.getPin()!=null && !address.getPin().trim().equals("") && !address.getPin().trim().equals("0")){
						usraddr[1] = usraddr[1] + ", " + address.getPin(); 
					}
					if(address.getPhone()!=null && !address.getPhone().trim().equals("")){
						usraddr[1] = usraddr[1] + " | Phone: " + address.getPhone(); 
					}
					usraddr[1] = usraddr[1];
					/*if(usraddr.length()>2)
						usraddr = usraddr.substring(2, usraddr.length()).trim();*/
					
					if(logger.isDebugEnabled()) logger.debug("Orgaddr : " + usraddr[0]+"Senderaddr : "+usraddr[1]);
				}
			}
		
		}
		
		else if(obj != null && obj instanceof CustomTemplates) {
			
			CustomTemplates customTemplates = (CustomTemplates)obj;
			Address address = customTemplates.getAddress();
			
			String includeStr = customTemplates.getIncludeBeforeStr();
			
			
			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				usraddr[0] = usraddr[0] +includeStr;
				
				
			}//if
			
			
			
			if(customTemplates.isAddressFlag() ){
				if(address.getAddressOne()!=null && !address.getAddressOne().trim().equals("")){
					usraddr[1] = usraddr[1] +  address.getAddressOne(); 
				}
				if(address.getAddressTwo()!=null && !address.getAddressTwo().trim().equals("")){
					usraddr[1] = usraddr[1] + ", " + address.getAddressTwo(); 
				}
				if(address.getCity()!=null && !address.getCity().trim().equals("")){
					usraddr[1] = usraddr[1] + ", " + address.getCity(); 
				}
				if(address.getState()!=null && !address.getState().trim().equals("")){
					usraddr[1] = usraddr[1] + ", " + address.getState(); 
				}
				if(address.getCountry()!=null && !address.getCountry().trim().equals("")){
					usraddr[1] = usraddr[1] + ", " + address.getCountry(); 
				}
				if(address.getPin() !=null && !address.getPin().trim().equals("") && !address.getPin().equals("0")){
					usraddr[1] = usraddr[1] + ", " + address.getPin(); 
				}
				if(address.getPhone()!=null && !address.getPhone().trim().equals("")){
					usraddr[1] = usraddr[1] + " | Phone: " + address.getPhone(); 
				}
				
				usraddr[1] = usraddr[1];
				/*if(usraddr.length()>2)
					usraddr = usraddr.substring(2, usraddr.length()).trim();*/
				
				if(logger.isDebugEnabled()) logger.debug("Orgaddr : " + usraddr[0]+"Senderaddr : "+usraddr[1]);
			}
			
			
		} else if(obj != null && obj instanceof Users) {
			
			Users user = (Users)obj;
			
			
			String includeStr = user.getUserOrganization().getOrganizationName()+(!userDomainStr.isEmpty() ? (", "+userDomainStr):"") ;

			if(includeStr != null && includeStr.trim().length() > 0 && !isPrepared) {
				
				includeStr = includeStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				usraddr[0] = usraddr[0] +includeStr;
			}//if
			if(user.getAddressOne()!=null && !user.getAddressOne().trim().equals("")){
				usraddr[1] = usraddr[1]  + user.getAddressOne(); 
			}
			if(user.getAddressTwo()!=null && !user.getAddressTwo().trim().equals("")){
				usraddr[1] = usraddr[1] + ", " + user.getAddressTwo(); 
			}
			if(user.getCity()!=null && !user.getCity().trim().equals("")){
				usraddr[1] = usraddr[1] + ", " + user.getCity(); 
			}
			if(user.getState()!=null && !user.getState().trim().equals("")){
				usraddr[1] = usraddr[1] + ", " + user.getState(); 
			}
			if(user.getCountry()!=null && !user.getCountry().trim().equals("")){
				usraddr[1] = usraddr[1] + ", " + user.getCountry(); 
			}
			if(user.getPinCode()!=null && !user.getPinCode().trim().equals("")){
				usraddr[1] = usraddr[1] + ", " + user.getPinCode(); 
			}
			if(user.getPhone()!=null && !user.getPhone().trim().equals("")){
				usraddr[1] = usraddr[1] + " | Phone: " + user.getPhone(); 
			}
			
			usraddr[1] = usraddr[1];
			/*if(usraddr.length()>2)
				usraddr = usraddr.substring(2, usraddr.length()).trim();*/
			
			if(logger.isDebugEnabled()) logger.debug("Orgaddr : " + usraddr[0]+"Senderaddr : "+usraddr[1]);
			
			
			
			
		}
		
		if(logger.isDebugEnabled()) logger.debug("Exiting");
		return usraddr;
	}

	public static String replaceCouponImgSrcWithMqrm(String htmlContent, boolean isTestMail){
		try{
		String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
		String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
		String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
		String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
		String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
		String stylePattern = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";
		
		Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(htmlContent);
		
		
		while(matcher.find()) {

			String imgtag = null;
			//String srcAttr = null;
			String idAttr = null;
			String wAttr = null;
			String hAttr = null;
			String styleAttr = null;
			
				imgtag = matcher.group();
				
				/*Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
				Matcher srcm = srcp.matcher(imgtag);*/
				
				Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
				Matcher idm = idp.matcher(imgtag);
				
				Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
				Matcher widthm = widthp.matcher(imgtag);
				
				Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
				Matcher heightm = heightp.matcher(imgtag);
				
				Pattern stylep = Pattern.compile(stylePattern, Pattern.CASE_INSENSITIVE);
				Matcher stylem = stylep.matcher(imgtag);
				
				/*while(srcm.find()){
					srcAttr = srcm.group(1);
				}*/
				while(idm.find()){
					idAttr = idm.group(1);
				}
				while(widthm.find()){
					wAttr = widthm.group(1);
				}
				while(heightm.find()){
					hAttr = heightm.group(1);
				}
				
				//style="width: 120px; height: 80px;"
				if(wAttr == null || hAttr == null){
					
					while(stylem.find()){
						styleAttr = stylem.group(1);
						
						logger.info("styleAttr :"+styleAttr);
						String whAttr[] = styleAttr.split(";");
						//for width part
						String wstyle[] = whAttr[0].trim().split(":");
						wAttr = wstyle[1].replace("px", "").trim();

						//for height part
						String hstyle[] = whAttr[1].trim().split(":");
						hAttr = hstyle[1].replace("px", "").trim();
						
					}
				}
				
				wAttr = wAttr.trim();
				hAttr = hAttr.trim();
				
				String ccPhTokens[] = idAttr.split("_");
				
				String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
				String COUPON_CODE_URL = "";
				String testBarcode = "Test-"+ccPhTokens[2];
				String mqrmImgtag = "";
				
				if(isTestMail){
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("CouponCodeUrl").replace("|^code^|",testBarcode)
							.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
							.replace("|^type^|", ccPhTokens[3]);
					
					mqrmImgtag = "<img id=\""+idAttr+"\" src=\""+COUPON_CODE_URL+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" alt=\""+testBarcode+"\" />";
					
					logger.info("PrepareStuff Testmail: ");
					logger.info("code= "+testBarcode+" width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);
				}
				else{
					
				COUPON_CODE_URL = PropertyUtil.getPropertyValue("CouponCodeUrl").replace("|^code^|","["+phStr+"]")
						.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
						.replace("|^type^|", ccPhTokens[3]);
					
					mqrmImgtag = "<img id=\""+idAttr+"\" src=\""+COUPON_CODE_URL+"\" width=\""+wAttr+"\" height=\""+hAttr+"\"  alt=\"["+phStr+"]\" />";
				
					logger.info("PrepareStuff campaign: ");
					logger.info("code= "+phStr+" width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);
				}
				//String bcstr = "<img width='"+width+"' height= '"+height+"' src= '"+COUPON_CODE_URL+"' />";
				
				//String mqrmImgtag = "<img id=\""+idAttr+"\" src=\""+COUPON_CODE_URL+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" />";
				
				
				htmlContent = htmlContent.replace(imgtag, mqrmImgtag);
			//	logger.info("htmlContent with barcode = "+htmlContent);
				
		} 
			
	}catch(Exception e){
		logger.error("Exception ::::", e);
		logger.error("** Exception in replacing coupon img tag with barcode mqrm **");
		return htmlContent;
	}
		return htmlContent;
	}
	
	
	private static boolean IsBarcodeExist(String htmlContent){
		
		boolean existFlag = false;
		
		try {			
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
						
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlContent);
			
			while(matcher.find()) {
				existFlag = true;
				break;
			}
			
		}catch(Exception e){
			logger.error("Exception in IsBarcodeExist method ", e);
		}
		
		return existFlag;
	}
	
	
	
}
