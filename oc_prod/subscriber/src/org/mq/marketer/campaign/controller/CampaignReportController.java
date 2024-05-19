package org.mq.marketer.campaign.controller;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.beans.ShareSocialNetworkLinks;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.campaign.CampaignReportManager;
import org.mq.marketer.campaign.controller.service.UpdateEmailReports;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.EmailClientDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;



public class CampaignReportController extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	static final String DIV_TEMPLATE = PropertyUtil.getPropertyValue("divTemplate");

	public static Queue<Opens> opensQueue ;
	public static Queue<Clicks> clicksQueue ;


	//private static final Logger logger = LogManager.getLogger(CampaignReportController.class);
	//private PrintWriter responseWriter;
	/*private ContactScoreSetting contactScoreSetting;

    public ContactScoreSetting getContactScoreSetting() {
		return contactScoreSetting;
	}
	public void setContactScoreSetting(ContactScoreSetting contactScoreSetting) {
		this.contactScoreSetting = contactScoreSetting;
	}*/

	private CampaignReportDaoForDML campaignReportDaoForDML;
	public CampaignReportDaoForDML getCampaignReportDaoForDML() {
		return campaignReportDaoForDML;
	}
	public void setCampaignReportDaoForDML(
			CampaignReportDaoForDML campaignReportDaoForDML) {
		this.campaignReportDaoForDML = campaignReportDaoForDML;
	}

	private CampaignReportDao campaignReportDao;
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	} 
	public CampaignReportDao getCampaignReportDao() {
		return this.campaignReportDao;
	}

	private CampaignSentDao campaignSentDao;
	public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
		this.campaignSentDao = campaignSentDao;
	} 
	public CampaignSentDao getCampaignSentDao() {
		return this.campaignSentDao;
	}
	
	
	

	private OpensDao opensDao;
	public void setOpensDao(OpensDao opensDao) {
		this.opensDao = opensDao;
	} 
	
	
	private CampaignSentDaoForDML campaignSentDaoForDML;
	public CampaignSentDaoForDML getCampaignSentDaoForDML() {
		return campaignSentDaoForDML;
	}
	public void setCampaignSentDaoForDML(CampaignSentDaoForDML campaignSentDaoForDML) {
		this.campaignSentDaoForDML = campaignSentDaoForDML;
	}
	public OpensDao getOpensDao() {
		return this.opensDao;
	}

	private EmailClientDao emailClientDao;
	public EmailClientDao getEmailClientDao() {
		return emailClientDao;
	}
	public void setEmailClientDao(EmailClientDao emailClientDao) {
		this.emailClientDao = emailClientDao;
	}

	private ClicksDao clicksDao;
	public void setClicksDao(ClicksDao clicksDao) {
		this.clicksDao = clicksDao;
	} 
	public ClicksDao getClicksDao() {
		return this.clicksDao;
	}

	private UnsubscribesDao unsubscribesDao;
	private UnsubscribesDaoForDML unsubscribesDaoForDML;
	public UnsubscribesDaoForDML getUnsubscribesDaoForDML() {
		return unsubscribesDaoForDML;
	}
	public void setUnsubscribesDaoForDML(UnsubscribesDaoForDML unsubscribesDaoForDML) {
		this.unsubscribesDaoForDML = unsubscribesDaoForDML;
	}
	public UnsubscribesDao getUnsubscribesDao() {
		return unsubscribesDao;
	}
	public void setUnsubscribesDao(UnsubscribesDao unsubscribesDao) {
		this.unsubscribesDao = unsubscribesDao;
	}

	private ContactsDao contactsDao;
	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	} 
	public ContactsDao getContactsDao() {
		return this.contactsDao;
	}
	
	private ContactsDaoForDML contactsDaoForDML;
	

	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}
	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	private CampaignReportManager campaignReportManager;
	public CampaignReportManager getCampaignReportManager() {
		return campaignReportManager;
	}
	public void setCampaignReportManager(CampaignReportManager campaignReportManager) {
		this.campaignReportManager = campaignReportManager;
	}
	private CampaignsDao campaignsDao;
	/*private EmailOpenedQueue emailOpenedQueue;


	public EmailOpenedQueue getEmailOpenedQueue() {
		return emailOpenedQueue;
	}
	public void setEmailOpenedQueue(EmailOpenedQueue emailOpenedQueue) {
		this.emailOpenedQueue = emailOpenedQueue;
	}


	private EmailClickedQueue emailClickedQueue;


	public EmailClickedQueue getEmailClickedQueue() {
		return emailClickedQueue;
	}
	public void setEmailClickedQueue(EmailClickedQueue emailClickedQueue) {
		this.emailClickedQueue = emailClickedQueue;
	}
	 */

	public CampaignsDao getCampaignsDao() {
		return campaignsDao;
	}
	public void setCampaignsDao(CampaignsDao campaignsDao) {
		this.campaignsDao = campaignsDao;
	}

	private UpdateEmailReports updateEmailReports;


	public UpdateEmailReports getUpdateEmailReports() {
		return updateEmailReports;
	}
	public void setUpdateEmailReports(UpdateEmailReports updateEmailReports) {
		this.updateEmailReports = updateEmailReports;
	}

	private ShareSocialNetworkLinksDao shareSocialNetworkLinksDao;
	private ShareSocialNetworkLinksDaoForDML shareSocialNetworkLinksDaoForDML;
	public ShareSocialNetworkLinksDaoForDML getShareSocialNetworkLinksDaoForDML() {
		return shareSocialNetworkLinksDaoForDML;
	}
	public void setShareSocialNetworkLinksDaoForDML(
			ShareSocialNetworkLinksDaoForDML shareSocialNetworkLinksDaoForDML) {
		this.shareSocialNetworkLinksDaoForDML = shareSocialNetworkLinksDaoForDML;
	}
	public ShareSocialNetworkLinksDao getShareSocialNetworkLinksDao() {
		return shareSocialNetworkLinksDao;
	}
	public void setShareSocialNetworkLinksDao(
			ShareSocialNetworkLinksDao shareSocialNetworkLinksDao) {
		this.shareSocialNetworkLinksDao = shareSocialNetworkLinksDao;
	}






	private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	static {

		opensQueue =  new LinkedList<Opens>();
		clicksQueue =  new LinkedList<Clicks>();

		genFieldContMap.put("email", "EmailId");
		genFieldContMap.put("firstName", "FirstName");
		genFieldContMap.put("lastName", "LastName");
		genFieldContMap.put("addressOne", "AddressOne");
		genFieldContMap.put("addressTwo", "AddressTwo");
		genFieldContMap.put("city", "City");
		genFieldContMap.put("state", "State");
		genFieldContMap.put("country", "Country");
		genFieldContMap.put("pin", "Zip");
		genFieldContMap.put("phone", "MobilePhone");
		genFieldContMap.put("gender", "Gender");
		genFieldContMap.put("birthday", "BirthDay");
		genFieldContMap.put("anniversary", "Anniversary");
		genFieldContMap.put("zip", "Zip");
		genFieldContMap.put("mobile", "MobilePhone");




	}




	private static final String ERROR_RESPONSE = 
			"<div style='font-size:15px;color:blue;font-family:verdena;" +
					"font-weight:bold;margin-top:50px'>The Web Page is expired</div>";






	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");       

		String requestedAction = request.getParameter("requestedAction");
		String action = request.getParameter("action");

		// if(logger.isInfoEnabled()) logger.info("Requested Action is :  " + requestedAction);

		if(requestedAction==null && action==null) {
			return null;
		}

		if ((requestedAction != null && requestedAction.compareTo("couponcode") == 0) || 
				(action != null && action.compareTo("couponcode") == 0)) {
			logger.info("coupon barcode request...");
			couponBarCode(request, response);
			return null;
		}

		if ((requestedAction != null && requestedAction.equalsIgnoreCase("unsubReason")) || 
				(action != null && action.equalsIgnoreCase("unsubReason"))) {
			unsubscribeUpdate(request, response);
		} 

		//NOTE :This is only for after getting Resubscription hit from the contact. and this request getting from 
		//Webadding the contact through WebForm .and the contact is already exists and it is Unsubscribed 
		if((requestedAction != null && requestedAction.equalsIgnoreCase(Constants.CONTACT_RESUBSCRIBE)) ||
				(action != null && action.equalsIgnoreCase(Constants.CONTACT_RESUBSCRIBE))) {
			contactResubcription(request);
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><body><table><tr><td>Thank you very much for your interest to receive the emails from this user.</td></tr></table></body></html>");
			
			pw.flush();
			pw.close();
			
			//response.sendRedirect("http://localhost:8080/Scheduler/view/resubscribe.html");
			return null;
		}

		String sentIdStr = request.getParameter(Constants.QS_SENTID);
		Long sentId = null;
		try {
			if(requestedAction != null) {
				sentId = Long.parseLong(sentIdStr);
			}
			else if(action != null) {
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(sentIdStr));
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId for the requested action - "+requestedAction);
			return null;
		} 



		if ((requestedAction != null && requestedAction.compareTo("open") == 0) ||
				(action != null && action.compareTo("open") == 0)) {

			openUpdate(request, response, sentId, false);

			//return null;

		} 
		else {
			//			PrintWriter responseWriter = response.getWriter();
			if ((requestedAction != null && requestedAction.compareTo("click") == 0) || 
					(action != null && action.compareTo("click") == 0)) {


				clickUpdate(request, response, sentId);
			}  
			else if ((requestedAction != null && requestedAction.compareTo("unsubscribe") == 0) ||
					(action != null && action.compareTo("unsubscribe") == 0)) {
				String userIdStr = request.getParameter("userId");
				Long userId = null;
				try {
					if(requestedAction != null) {
						userId = Long.parseLong(userIdStr);
					}
					else if(action != null) {
						userId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(userIdStr));
					}
				} catch (Exception e) {
					logger.warn(" userId not found in the querystring");
				}
				requestForUnsubscribe(request, response, sentId, userId);
			}
			else if ((requestedAction != null && requestedAction.compareTo("webpage") == 0) ||
					(action != null && action.compareTo("webpage") == 0)) {
				String contactIdStr = request.getParameter(Constants.QS_CID);
				Long contactId = null;
				try {
					if(requestedAction != null) {
						contactId = Long.parseLong(contactIdStr);
					}
					else if(action != null) {
						contactId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(contactIdStr));
					}
				} catch (Exception e) {
					logger.error("** Exception : Invalid contactId for the requested action - "+
							requestedAction);
					return null;
				} 
				processWeblink(request, response, sentId, contactId);
			}
			else if((requestedAction != null && requestedAction.equalsIgnoreCase(Constants.TWEET_ON_TWITTER)) ||
					(action != null && action.equalsIgnoreCase(Constants.TWEET_ON_TWITTER))) {

				try {
					//TODO
					String requestCmpelteUrl = request.getRequestURL().toString()+"?"+request.getQueryString();
					requestCmpelteUrl = requestCmpelteUrl.replace("tweetOnTwitter", "shareLink");
					logger.info("afetr replceing  complete url is ::"+requestCmpelteUrl);

					String urlEncodedData = URLEncoder.encode(requestCmpelteUrl,"UTF-8");

					String tweetUrl =  "https://twitter.com/share?url="+urlEncodedData;
					logger.info("afetr replceing  complete url is ::"+tweetUrl);


					Long crId = Long.parseLong(request.getParameter("crId"));

					ShareSocialNetworkLinks shareSocilaNetLinkObj = new ShareSocialNetworkLinks(sentId, crId, "Twitter", Calendar.getInstance());
					//shareSocialNetworkLinksDao.saveOrUpdate(shareSocilaNetLinkObj);
					shareSocialNetworkLinksDaoForDML.saveOrUpdate(shareSocilaNetLinkObj);
					response.sendRedirect(tweetUrl);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}finally {
					return null;
				}

			}
			else if((requestedAction != null && requestedAction.equalsIgnoreCase(Constants.SHARE_ON_FB)) ||
					(action != null && action.equalsIgnoreCase(Constants.SHARE_ON_FB))) {
				try {

					String requestCmpelteUrl = request.getRequestURL().toString()+"?"+request.getQueryString();
					requestCmpelteUrl = requestCmpelteUrl.replace("sharedOnFb", "shareLink");
					logger.info("2 afetr replceing  complete url is ::"+requestCmpelteUrl);

					String urlEncodedData = URLEncoder.encode(requestCmpelteUrl,"UTF-8");

					String tweetUrl =  "http://www.facebook.com/sharer/sharer.php?u="+urlEncodedData;
					logger.info("3 afetr replceing  complete url is ::"+tweetUrl);
					Long crId = Long.parseLong(request.getParameter("crId"));
					ShareSocialNetworkLinks shareSocilaNetLinkObj = new ShareSocialNetworkLinks(sentId, crId, "Facebook", Calendar.getInstance());
					//shareSocialNetworkLinksDao.saveOrUpdate(shareSocilaNetLinkObj);
					shareSocialNetworkLinksDaoForDML.saveOrUpdate(shareSocilaNetLinkObj);

					//tweetUrl = StringEscapeUtils.escapeHtml(tweetUrl);
					response.sendRedirect(tweetUrl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}finally {
					return null;
				}

			}else if((requestedAction != null && requestedAction.equalsIgnoreCase("shareLink")) ||
					(action != null && action.equalsIgnoreCase("shareLink"))){
				logger.info(">>>>>>>>>>>>>>>>>>>> calling here dummy");
				CampaignSent campaignSent = campaignSentDao.findById(sentId);
				//logger.debug("---------------just entered in processWebLink--------sentId"+sentId);
				response.setContentType("text/html");
				PrintWriter pw = response.getWriter();
				if(campaignSent == null) {
					logger.debug(" CampaignSent is not found for the sentId :"+sentId);

					pw.write(ERROR_RESPONSE);

				}else{
					CampaignReport campaignReport = campaignSent.getCampaignReport();
					String htmlContent = campaignReport.getContent();

					Long campainId =  campaignSent.getCampaignId();
					Campaigns campaign = campaignsDao.findByCampaignId(campainId);

					// TODO for the campaigns already sent
					boolean oldCampSentFlag = false;
					if(htmlContent.contains(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL))) {
						htmlContent = htmlContent.replace(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL), PropertyUtil.getPropertyValue(Constants.NEW_TRACK_URL));
						oldCampSentFlag = true;
					}
					
					String webLinkText = campaign.getWebLinkText();
					String webLinkUrlText = campaign.getWebLinkUrlText();

					String weblinkUrl =  PropertyUtil.getPropertyValue("weblinkUrl");
					weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
					String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");

					htmlContent = htmlContent.replace(webUrl, "");

					//String firstDivStr = htmlContent.substring(htmlContent.indexOf("<div"), htmlContent.indexOf("<div id='rootDivId'>"));
					//delete the content of Having Trouble viewing this email or customize link  div from the html content
					//htmlContent = htmlContent.replace(firstDivStr, "");
					
					//delete the content of Unsubscribe link div from the html content
					String unSubUrl = PropertyUtil.getPropertyValue("unSubscribeUrl");
					unSubUrl =unSubUrl.replace("|^", "[").replace("^|", "]");
					if(oldCampSentFlag) {
						unSubUrl = unSubUrl.replace("[userId]", campaignReport.getUser().getUserId().toString());
					}
					else {
						unSubUrl = unSubUrl.replace("[userId]", EncryptDecryptUrlParameters.encrypt(campaignReport.getUser().getUserId().toString()));
					}
					
					String unsubDiv = PropertyUtil.getPropertyValue("unsubFooterText");
					unsubDiv = unsubDiv.replace("|^unsubUrl^|", unSubUrl);

					htmlContent =htmlContent.replace(unsubDiv, "");
					
					
					// remove update subscription link  and unsubscribe
					String upadteUnsubDiv = PropertyUtil.getPropertyValue("updateUnsubFooterText");
					upadteUnsubDiv = upadteUnsubDiv.replace("|^unsubUrl^|", unSubUrl);
					
					htmlContent = htmlContent.replace(upadteUnsubDiv, "");
											
					String updateSubsLink =  PropertyUtil.getPropertyValue("updateSubscriptionLink");
					updateSubsLink =updateSubsLink.replace("|^", "[").replace("^|", "]");
//									
					String updateUnsubDiv = PropertyUtil.getPropertyValue("updateSubHTMLTxt");
					updateUnsubDiv = updateUnsubDiv.replace("|^updateSubSUrl^|", updateSubsLink);
					
					
					htmlContent = htmlContent.replace(updateUnsubDiv, "");

					if(htmlContent.contains("href='")){
						htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
						
					}
					if(htmlContent.contains("href=\"")){
						htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
					}

					//contact home store and last purchase place holders replacement logic
					String placeHoldersStr = campaignReport.getPlaceHoldersStr();
					if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
						String contactPhValStr = campaignSent.getContactPhValStr();
						if(contactPhValStr != null) {

							String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
							String keyStr = "";
							String ValStr = "";
							for (String phToken : phTokenArr) {
								keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
								if(!( keyStr.equalsIgnoreCase("[GEN_ContactHomeStore]") ) && ! (keyStr.equalsIgnoreCase("[GEN_ContactLastPurchasedStore]")) &&  !( keyStr.startsWith("[CC_"))  ){
									continue;
								}
								ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());

								htmlContent = htmlContent.replace(keyStr, ValStr);

							}	

						}
					}
					htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
					
					pw.write(htmlContent);
				}
				pw.flush();
				pw.close();
				return null;
			} 
			else if((requestedAction != null && requestedAction.compareTo("farward") == 0) ||
					(action != null && action.compareTo("farward") == 0)){

				farwardUpdate(request, response, sentId);
			}
			else if ((requestedAction != null && requestedAction.compareTo("updateSubscrption") == 0) ||
					(action != null && action.compareTo("updateSubscrption") == 0)) {
				String contactIdStr = request.getParameter(Constants.QS_CID);
				Long contactId = null;
				try {
					if(requestedAction != null) {
						contactId = Long.parseLong(contactIdStr);
					}
					else if(action != null) {
						contactId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(contactIdStr));
					}
				} catch (Exception e) {
					logger.error("** Exception : Invalid contactId for the requested action - "+
							requestedAction);
					return null;
				} 

				processUpdateSubscriptionlink(request, response, sentId, contactId);
			}


			/*responseWriter.flush();
			responseWriter.close();*/
		}

		//if(logger.isDebugEnabled()) logger.debug("Exiting ");          
		if(opensQueue.size() >= 200 || clicksQueue.size() >= 200 ) {

			if(!updateEmailReports.isRunning()) {
				logger.debug("processor is not running , try to ping it....");
				updateEmailReports.run();
				return null;
			}


		}




		return null;
		//TODO: better to redirect to the error page, instead of returning null
	}





	public void openUpdate(HttpServletRequest request, HttpServletResponse response, Long sentId, boolean isWebPage)
			throws Exception {
		// if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");       

		try {

			/*CampaignSent campaignSent = campaignSentDao.findById(sentId);
			updateOpens(request, campaignSent);
			updateReport(campaignSent, Constants.CS_TYPE_OPENS);
			campaignSentDao.clear(campaignSent);
			 */

			updateOpens(request, sentId);
			if(isWebPage) return;

		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens \n", e);
		}

		try {
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the proxy server
			response.setContentType("image/gif");
			File imgFile = new File(PropertyUtil.getPropertyValue("appDirectory") + "/img/transparent.gif");
			//logger.info("imgFil isexxist ::"+imgFile.exists()+" path is::"+imgFile.getPath());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			try {
				byte b[] = new byte[8];
				int count;
				while((count=in.read(b)) != -1) {
					out.write(b,0,count);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}finally {
				out.flush();
				out.close();
				in.close();
			}


			/*File imgFile = new File(PropertyUtil.getPropertyValue("openImgUrl") + "/img/transparent.gif");

			logger.info("imgFil isexxist ::"+imgFile.exists()+" path is::"+imgFile.getPath());
			FileInputStream inputStream = new FileInputStream(imgFile);  //read the file

		        response.setHeader("Content-Disposition","attachment; filename=transparent.gif");
		        try {
		            int c;
		            while ((c = inputStream.read()) != -1) {
		            response.getWriter().write(c);
		            }
		        } catch (Exception e) {
		        	logger.error("Exception : Problem while sending the open image \n", e);
				}finally {
		            if (inputStream != null) {
		                inputStream.close();
		                response.getWriter().close();
		            }
		        }*/

		} catch (Exception e) {
			logger.error("Exception : Problem while sending the open image \n", e);
		}
		//if(logger.isDebugEnabled()) logger.debug("-- Exit --");
	}

	public void clickUpdate(HttpServletRequest request, HttpServletResponse response, Long sentId) {
		// if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
		// CampaignSent campaignSent = campaignSentDao.findById(sentId);

		String urlStr = request.getParameter(Constants.QS_URL);
		//logger.debug("<<<<<<<<<<<<<<< url Str : " + urlStr);

		if(urlStr==null) {
			if(logger.isDebugEnabled()) logger.debug("URL is null");
			return;
		} 

		/*if (urlStr.contains("http://www.loksatta.org/cms/")) {
        	// TODO: temp code need to remove it.
        	logger.debug("view : "+ request.getParameter("view"));
        	urlStr = urlStr + "&view=" + request.getParameter("view") + "&id=" + request.getParameter("id");
        }*/

		try {

			if(urlStr.contains("&amp;")) {

				urlStr = urlStr.replace("&amp;", "&");
			}
			/** checking for opens, if zero it update*/ 
			/*if(campaignSent != null && campaignSent.getOpens() == 0) {
        		updateOpens(request, campaignSent);
        		updateReport(campaignSent, "opens");
        	} *///if(campaignSent != null && campaignSent.getOpens() == 0)

			String userAgent = 	request.getHeader("User-Agent");
			Clicks clicks = new Clicks(sentId, urlStr, Calendar.getInstance(), userAgent);
			synchronized (clicksQueue) {

				clicksQueue.add(clicks);
			}
			/*clicksDao.saveOrUpdate(clicks);
			clicksDao.clear(clicks);*/

			/*updateReport(campaignSent, Constants.CS_TYPE_CLICKS);
			campaignSentDao.clear(campaignSent);*/
		}

		catch (Exception e) {
			logger.error("Exception : Problem while updating the Clicks \n", e);
		}
		try {
			if(urlStr.indexOf("http://") == -1 && (urlStr.indexOf("https://") == -1 )){
				urlStr = "http://" + urlStr.trim();
			}
			logger.debug("Redirecting to the URL:" + urlStr);
			response.sendRedirect(urlStr);
		} catch (IOException e) {
			logger.error("Exception : Problem while redirecting to the url : " + urlStr + "\n",e);
		}finally{
			/*logger.debug("---------contactScoreSetting update method is Called by clickUpdate---------------");

			contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
					Constants.SCORE_EMAIL_CLICK,campaignSent.getCampaignReport().getCampaignName());*/

		}
		// if(logger.isDebugEnabled()) logger.debug("-- Exit --");
	}

	public void requestForUnsubscribe(HttpServletRequest request, HttpServletResponse response, 
			Long sentId, Long userId) throws Exception {

		//logger.info(" -- just entered --");
		//logger.info("-------->Unsubscribe request for (sentId, userId) :"+sentId+","+userId);

		try {
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			if(campaignSent == null) {
				response.getWriter().print("Invalid Request...");
				return;
			}
			//logger.debug("CampaignSentGetoPens is ---"+campaignSent.getOpens());

			if(campaignSent.getOpens() == 0) {
				//logger.debug("calling updte opens method");
				updateOpens(request, sentId);
				/*updateReport(campaignSent, Constants.CS_TYPE_OPENS);
	 			campaignSentDao.clear(campaignSent);*/
			}
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens \n", e);
		}


		Map<Object, Object> resultMap = campaignReportManager.getStatusAndCategories(sentId, userId);

		if(resultMap.get(Constants._STATUS).equals(Constants.CS_STATUS_UNSUBSCRIBED)) {
			request.setAttribute("message", Constants._UNSUBSCRIBE_ALREADY_MSG);
		}
		else {
			request.setAttribute("message", Constants._UNSUBSCRIBE_REQUEST_MSG);
		}

		request.setAttribute(Constants._CATEGORIES, resultMap.get(Constants._CATEGORIES));
		request.setAttribute(Constants.QS_CRID, resultMap.get(Constants.QS_CRID));
		request.setAttribute(Constants.QS_EMAIL, resultMap.get(Constants.QS_EMAIL));

		if(userId == null) {
			userId = (Long)resultMap.get(Constants.QS_USERID);
		}
		request.setAttribute(Constants.QS_USERID, userId);
		request.setAttribute(Constants.QS_SENTID, sentId);

		RequestDispatcher reqDispatcher = getServletContext().
				getRequestDispatcher("/view/unsubscribe.jsp");

		reqDispatcher.forward(request, response);

	}


	/**
	 * Unsubscribe update controller 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void unsubscribeUpdate(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException{

		//logger.info("-- just entered--");
		try {

			Long userId = Long.parseLong(request.getParameter("userId"));
			Long crId = Long.parseLong(request.getParameter("crId"));
			Long sentId = Long.parseLong(request.getParameter("sentId"));

			short categoryWeight = Short.parseShort(request.getParameter("weight"));

			String reasonStr = request.getParameter("reason");
			String emailId = request.getParameter("emailId");

			/*if(logger.isDebugEnabled()) {
				logger.debug("(userId, crId, sentId, emailId, categoryWeight, Reason) :"+
					userId+","+crId+", "+sentId+", "+emailId+", "+categoryWeight+", "+reasonStr);
			}*/

			List<TemplateCategory> catList = campaignReportManager.updateUnsubscribe(userId, crId, 
					sentId, reasonStr, emailId, categoryWeight);
			//logger.debug("Category weight :"+ categoryWeight);




			if(categoryWeight == 0) {
				request.setAttribute("message", Constants._RESUBSCRIBE_RESPONSE_MSG);
				contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Active");
			}else {
				request.setAttribute("message", Constants._UNSUBSCRIBE_RESPONSE_MSG);

				// Unsubscribe all contacts of this email in all mailing lists for this user.
				contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Unsubscribed");				

				//****contactScoreSetting updateScore method  Calling ****
				/*logger.debug("------contactScoreSetting update method is Called by unSubscribeUpdatae-------------");
				CampaignSent campaignSent = campaignSentDao.findById(sentId);
				contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
						Constants.SCORE_EMAIL_UNSUBSCRIBED,	campaignSent.getCampaignReport().getCampaignName());*/

			}

			request.setAttribute(Constants._CATEGORIES, catList);
			request.setAttribute(Constants.QS_CRID, crId);
			request.setAttribute(Constants.QS_EMAIL, emailId);

			request.setAttribute(Constants.QS_USERID, userId);
			request.setAttribute(Constants.QS_SENTID, sentId);

			RequestDispatcher reqDispatcher = getServletContext().
					getRequestDispatcher("/view/unsubscribe.jsp");

			reqDispatcher.forward(request, response);
		}
		catch (Exception e) {
			logger.error("** Exception: while updating the unsubscribe reason", e);
		}

	}



	public void processWeblink(HttpServletRequest request, HttpServletResponse response,
			Long sentId, Long contactId)  throws Exception {
		//logger.info("----2----");
		PrintWriter responseWriter = response.getWriter();
		try {
			//logger.debug("---------------just entered in processWebLink--------"+contactId);
			response.setContentType("text/html");

			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			//logger.debug("---------------just entered in processWebLink--------sentId"+sentId);
			if(campaignSent == null) {
				logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				responseWriter.write(ERROR_RESPONSE);
				return;
			}

			Contacts contact = contactsDao.findById(contactId);
			CampaignReport campaignReport = campaignSent.getCampaignReport();



			//logger.debug("---------------just entered in processWebLink--------report"+campaignReport);
			if(campaignReport == null) {
				logger.warn(" Campaign Report object not found for sentId :"+sentId);
				responseWriter.write(ERROR_RESPONSE);
				return;
			}

			//if(campaignSent.getOpens() == 0) {
			openUpdate(request, response, campaignSent.getSentId(), true);
			/*updateReport(campaignSent, Constants.CS_TYPE_OPENS);*/
			//}
			String htmlContent = campaignReport.getContent();


			if(htmlContent == null) {
				responseWriter.write(ERROR_RESPONSE);
				return ;
			}
			htmlContent = htmlContent.replaceFirst("<font (.*?)<a[^>]*>(.*?)</a></font>", "");
			String openTrackURl = PropertyUtil.getPropertyValue("OpenTrackUrl").replace("|^", "[").replace("^|", "]");
			htmlContent = htmlContent.replace(openTrackURl, PropertyUtil.getPropertyValue("subscriberUrl") + "img/transparent.gif");

			Long userId = campaignReport.getUser().getUserId();
			htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlContent = htmlContent.replace("[cId]", EncryptDecryptUrlParameters.encrypt(contactId.toString()));
			htmlContent = htmlContent.replace("[crId]", campaignReport.getCrId().toString());
			htmlContent = htmlContent.replace("[email]", contact == null ? " your email Id " : contact.getEmailId());

			String placeHoldersStr = campaignReport.getPlaceHoldersStr();
			String replaceStr="";
			StringBuffer sb = new StringBuffer(htmlContent);

			/********replace contact's placeholders***************************/
			if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
				String contactPhValStr = campaignSent.getContactPhValStr();
				logger.info("contactPlhValStr is "+contactPhValStr);
				if(contactPhValStr != null) {

					String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
					String keyStr = "";
					String ValStr = "";
					for (String phToken : phTokenArr) {

						keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
						ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());

						if(keyStr.equals(Constants.CAMPAIGN_PH_UNSUBSCRIBE_LINK)) {

							String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
							String value = "<a href="+unsubURL+ " target=\"_blank\">unsubscribe</a>";
							htmlContent = htmlContent.replace(keyStr, value);
							continue;
							//tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
						}
						else if(keyStr.equals(Constants.CAMPAIGN_PH_WEBPAGE_VERSION_LINK)){

							String webpagelink = PropertyUtil.getPropertyValue("weblinkUrl").replace("|^", "[").replace("^|", "]");

							String value = "<a style=\"color: inherit; text-decoration: underline; \"  href="+webpagelink+"" +
									" target=\"_blank\">View in web-browser</a>";

							htmlContent = htmlContent.replace(keyStr, value);
							continue;
							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}else if(keyStr.equals(Constants.CAMPAIGN_PH_SHARE_TWEET_LINK)){

							String webpagelink = PropertyUtil.getPropertyValue("shareTweetLinkUrl").replace("|^", "[").replace("^|", "]");

							String value = "<a style=\"color: blue; text-decoration: underline; \"  href="+webpagelink+"" +
									" target=\"_blank\">Share on Twitter</a>";

							htmlContent = htmlContent.replace(keyStr, value);
							continue;
							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}else if(keyStr.equals(Constants.CAMPAIGN_PH_SHARE_FACEBOOK_LINK)){

							String webpagelink = PropertyUtil.getPropertyValue("shareFBLinkUrl").replace("|^", "[").replace("^|", "]");

							String value = "<a style=\"color: blue; text-decoration: underline; \"  href="+webpagelink+"" +
									" target=\"_blank\">Share on Facebook</a>";

							htmlContent = htmlContent.replace(keyStr, value);
							continue;
							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}else if(keyStr.contains(Constants.CAMPAIGN_PH_FORWRADFRIEND_LINK)) {

							String farwardFriendLink = PropertyUtil.getPropertyValue("forwardToFriendUrl").replace("|^", "[").replace("^|", "]");

							String value = "<a href="+farwardFriendLink+" target=\"_blank\">Forward to Friend</a>";
							htmlContent = htmlContent.replace(keyStr, value);
							continue;
							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}
						// to add update reference center
						else if(keyStr.contains(Constants.CAMPAIGN_PH__UPDATE_PREFERENCE_LINK)) {


							String updateSubscrptionLink = PropertyUtil.getPropertyValue("updateSubscriptionLink").replace("|^", "[").replace("^|", "]");
							String value = "<a href="+updateSubscrptionLink+ " target=\"_blank\">Subscriber Preference Link</a>";

							logger.debug("upadte link is"+updateSubscrptionLink);

							logger.debug("value  is"+value);
							htmlContent = htmlContent.replace(keyStr, value);

							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}
						htmlContent = htmlContent.replace(keyStr, ValStr);



					}//for

				}

				if(contactPhValStr == null ) {
					String[] placeHolders = placeHoldersStr.split("\\|\\|");

					Method[] contactClassMethods = Contacts.class.getDeclaredMethods();

					Method conMethod = null;
					Set<String> methoNameStr = new HashSet<String>();
					Object tempObj = null;
					String getterMethodStr = "";
					boolean found = false;
					for (int i = 0; i < placeHolders.length; i++) {
						found = false;
						for (int j = 0; j < contactClassMethods.length; j++) {

							getterMethodStr = genFieldContMap.get(placeHolders[i]) != null ? "get"+genFieldContMap.get(placeHolders[i]) : null;
							if(getterMethodStr != null) {
								if(contactClassMethods[j].getName().equalsIgnoreCase(getterMethodStr)){
									conMethod = contactClassMethods[j];
									break;
								}//if
							}//if
						}
						if(conMethod == null) {
							continue;
						}

						if(!methoNameStr.contains(conMethod.getName())) {
							methoNameStr.add(conMethod.getName());
						}else {

							found = true;

						}

						if(found == true) continue;
						tempObj = conMethod.invoke(contact, (Object[])null);
						if(tempObj instanceof Calendar) {

							Calendar cal = (Calendar)tempObj;
							tempObj = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE);


						}

						String replceStr = tempObj != null ? tempObj.toString() : "";


						htmlContent = htmlContent.replace("[GEN_"+placeHolders[i]+"]", replceStr);

					}
				}

			}


			htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlContent = htmlContent.replace("[cId]", EncryptDecryptUrlParameters.encrypt(contactId.toString()));
			htmlContent = htmlContent.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.longValue()+"" ));
			htmlContent = htmlContent.replace("[email]", campaignSent.getEmailId());
			htmlContent = htmlContent.replace("[crId]", campaignReport.getCrId().toString());



			/*****************************************************************/

			if(contact == null) {
				Pattern p = Pattern.compile("\\|\\^.*?\\^\\|");
				Matcher m = p.matcher(htmlContent);
				while(m.find()) {
					m.appendReplacement(sb, replaceStr);
				}
				m.appendTail(sb);
				responseWriter.write(sb.toString());
				return;

			}
			if(placeHoldersStr == null) {
				responseWriter.write(htmlContent);
				return;
			}


			contactsDaoForDML.clear(contact);
			campaignReportDaoForDML.clear(campaignReport);
			campaignSentDaoForDML.clear(campaignSent);

			responseWriter.write(htmlContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}finally{

			responseWriter.flush();
			responseWriter.close();

		}
	}

	/**
	 * Check the emailClient and inserts a record in the Opens table 
	 * for the specified sentId
	 * @param sentId - sent id from CampaignSent 
	 */
	private void updateOpens(HttpServletRequest request, Long sentId) {
		try {
			/*if (logger.isDebugEnabled())
				logger.debug("-- Just Entered  in UpdateOpens--");
			 */
			//if (campaignSent != null) {

			synchronized (opensQueue) {

				String userAgent = 	request.getHeader("User-Agent"); // (String) request.getAttribute(Constants.QS_USERAGENT);

				//Opens opens = new Opens(sentId, Calendar.getInstance(), clientType,clientOs,clientUa);
				Opens opens = new Opens(sentId, Calendar.getInstance(), userAgent);
				//	updateOpenUADetails(opens, userAgent);

				/*EmailClient clientType = emailTypeCheck(userAgent);
					EmailClient clientOs = emailOsFCheck(userAgent);
					EmailClient clientUa = emailUaFCheck(userAgent);*/
				//opens.setDevice("computer...");
				opensQueue.offer(opens);

			}
			/*opensDao.saveOrUpdate(opens);
				opensDao.clear(opens);*/
			//emailClientDao.clear(client);
			//} // End if(campaignSent != null)
		} catch (Exception e) {
			logger.error("**Exception : error occured while saving the opens ",e);
		} finally { 
			/*logger.debug("contactScoreSetting update method is Called ");
			contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
					Constants.SCORE_EMAIL_OPEN,	campaignSent.getCampaignReport().getCampaignName());*/
		}

	}// End updateOpens(long sentId)


	/**
	 * Updates the CampaignSent and CampaignReport tables for the specified type
	 * @param campaignSent - CampaignSent Object
	 * @param type - Specifies for which type to update the report 
	 * 			(Opens, Clicks, Bounces, Unsubscribes ...)
	 * @return
	 */
	public int updateReport(CampaignSent campaignSent, String type) {
		//if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    

		int updateCount = 0;
		if(campaignSent == null) {
			logger.warn("CampiagnSent Object is null");
			return updateCount;
		}
		try {
			//updateCount = campaignSentDao.updateCampaignSent(campaignSent.getSentId(), type);
			updateCount = campaignSentDaoForDML.updateCampaignSent(campaignSent.getSentId(), type);
			if(updateCount == 0 ) return updateCount;
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens in CampaignSent \n", e);
		}

		CampaignReport campaignReport = campaignSent.getCampaignReport();
		try {
			if(!campaignReport.getStatus().equals(Constants.CR_STATUS_SENDING)) {

				//campaignReportDao.updateCampaignReport(campaignReport.getCrId(),type);
				campaignReportDaoForDML.updateCampaignReport(campaignReport.getCrId(), type);
			}
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens in CampaignReport \n", e);
		}

		// if(logger.isDebugEnabled()) logger.debug("-- Exit --");
		return updateCount;
	}


	/**
	 * Searches for the EmailClient
	 * @param request
	 * @param campaignSent
	 * @return if found returns EmailClinet otherwise returns null
	 */
	/*public EmailClient emailClientCheck(String userAgent){
		EmailClient client = null;

		try {
			//logger.debug("User-Agent : " + userAgent );

			//TODO: As Email Client list is static for all the request 
			//Need to declare emailClientList as instance Variable

			if(userAgent != null) {

				String userAgentSearchStr = null;

				for (EmailClient emailClient : emailClientList) {
					userAgentSearchStr = emailClient.getUserAgent();

					if(userAgentSearchStr!=null) {

						if(userAgent.contains(userAgentSearchStr)) {
							//logger.debug("Email client : " + client);
							return emailClient;
						}
					}
				} // for

			} // if
			else {

				return null;



			}//else


			String toEmailId = campaignSent.getEmailId();

			if(toEmailId!=null) {
				String domain = toEmailId.substring(toEmailId.indexOf('@')+1, toEmailId.indexOf('.',toEmailId.indexOf('@')));
				logger.debug("Domain of sent mail : " + domain);

				for (EmailClient emailClient : emailClientList) {
					if(emailClient.getEmailClient().toLowerCase().contains(domain.toLowerCase())) {
						return emailClient;
					}
					else if(emailClient.getEmailClient().equals("Others")) {
						client = emailClient;
					}
				} // for
			} // if toEmailId

			//emailClientList = null;
		} catch (Exception e) {
			logger.error("Exception : ", (Throwable)e);
		}		
		return client;
	} // emailClientCheck(HttpServletRequest request,CampaignSent campaignSent)

	 */

	public void contactResubcription(HttpServletRequest request) {

		String tempEmailId = null;
		Long tempUserId = null;
		try {
			if(request.getParameter("requestedAction") != null) {
				tempEmailId = request.getParameter("emailId");
				tempUserId = Long.parseLong(request.getParameter("userId"));
			}
			else if(request.getParameter("action") != null) {
				tempEmailId = EncryptDecryptUrlParameters.decrypt(request.getParameter("emailId"));
				tempUserId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(request.getParameter("userId")));
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		//logger.debug(">>emailId>>"+tempEmailId);
		//logger.debug(">>tempUserId >>"+tempUserId);

		if(tempEmailId == null || tempEmailId.trim().length() == 0) {
			return ;
		}else if(tempUserId == null ){
			return;
		}

		//Find the User Object if exist

		//Update the Contact Status 
		contactsDaoForDML.updateEmailStatusByUserId(tempEmailId, tempUserId, "Active");

		//Remove Contact From Unsubscribe
		unsubscribesDaoForDML.deleteByEmailIdUserId(tempEmailId, tempUserId);

	}

	public void couponBarCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("-- Just Entered couponBarCode--");

		try {
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the
			// proxy server
			response.setContentType("image/png");

			BitMatrix bitMatrix = null;

			String couponcode = (String)request.getParameter("code");
			String widthStr = request.getParameter("width").trim(); 
			int width = Integer.parseInt(widthStr);
			String heightStr = request.getParameter("height").trim();
			int height = Integer.parseInt(heightStr);

			String type = request.getParameter("type");

			if(!Constants.barcodeTypes.contains(type)){
				return;
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();


			if(type.equals(Constants.COUP_BARCODE_QR)){
				bitMatrix = new QRCodeWriter().encode(couponcode, BarcodeFormat.QR_CODE, width, height,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_AZTEC)){
				bitMatrix = new AztecWriter().encode(couponcode, BarcodeFormat.AZTEC, width, height);
			}
			else if(type.equals(Constants.COUP_BARCODE_LINEAR)){
				bitMatrix = new Code128Writer().encode(couponcode, BarcodeFormat.CODE_128, width, height,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_DATAMATRIX)){
				bitMatrix = new DataMatrixWriter().encode(couponcode, BarcodeFormat.DATA_MATRIX, width, height,null);
			}

			if(bitMatrix == null){
				return;
			}
			MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());

			try {
				byte b[] = new byte[8];
				int count;
				while((count=bais.read(b)) != -1) {
					out.write(b,0,count);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}finally {
				out.flush();
				out.close();
				bais.close();
			}


		} catch (Exception e) {
			logger.error("Exception : Problem while generating coupon bar code \n",	e);
		}

	}

	public void farwardUpdate(HttpServletRequest request, HttpServletResponse response, Long sentId){

		try {
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			try {
				if(campaignSent == null) {
					response.getWriter().print("Invalid Request...");
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}


			request.setAttribute(Constants.QS_SENTID, campaignSent);

			RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/view/farward.jsp");

			reqDispatcher.forward(request, response);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}



	}
	// Added for update subscrption link
	public void processUpdateSubscriptionlink(HttpServletRequest request, HttpServletResponse response,
			Long sentId, Long contactId)  throws Exception{
		//logger.info("-- just entered--");
		try {

			//Long userId = Long.parseLong(request.getParameter("userId"));
			//Long crId = Long.parseLong(request.getParameter("crId"));


			//String emailId = request.getParameter("emailId");

			//request.setAttribute(Constants.QS_CRID, crId);
			//request.setAttribute(Constants.QS_EMAIL, emailId);

			//	request.setAttribute(Constants.QS_USERID, userId);
			request.setAttribute(Constants.QS_SENTID, sentId);
			request.setAttribute(Constants.QS_CID, contactId);

			Contacts contact = contactsDao.findById(contactId);
			if(contact == null) return;
			Users user =contact.getUsers();
			if(!user.getSubscriptionEnable()){
				RequestDispatcher reqDispatcher = getServletContext().
						getRequestDispatcher("/view/updateSubsError.jsp");

				reqDispatcher.forward(request, response);			
			}

			RequestDispatcher reqDispatcher = getServletContext().
					getRequestDispatcher("/zul/updateSubscriptions.zul");

			reqDispatcher.forward(request, response);
		}
		catch (Exception e) {
			logger.error("** Exception: while updating the subscription details", e);
		}


	}



}
