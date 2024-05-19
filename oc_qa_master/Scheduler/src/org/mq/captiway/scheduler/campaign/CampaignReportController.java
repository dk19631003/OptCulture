package org.mq.captiway.scheduler.campaign;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.ExternalSMTPEventsProcessor;
import org.mq.captiway.scheduler.campaign.ContactScoreSetting;

import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Clicks;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.EmailClient;
import org.mq.captiway.scheduler.beans.Opens;
import org.mq.captiway.scheduler.beans.TemplateCategory;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.ClicksDao;
import org.mq.captiway.scheduler.dao.ClicksDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.EmailClientDao;
import org.mq.captiway.scheduler.dao.EmailClientDaoForDML;
import org.mq.captiway.scheduler.dao.OpensDao;
import org.mq.captiway.scheduler.dao.OpensDaoForDML;
import org.mq.captiway.scheduler.dao.UnsubscribesDao;
import org.mq.captiway.scheduler.dao.UnsubscribesDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PropertyUtil;
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

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	//private PrintWriter responseWriter;
	private ContactScoreSetting contactScoreSetting;

    public ContactScoreSetting getContactScoreSetting() {
		return contactScoreSetting;
	}
	public void setContactScoreSetting(ContactScoreSetting contactScoreSetting) {
		this.contactScoreSetting = contactScoreSetting;
	}

	private CampaignReportDao campaignReportDao;
    public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
    	this.campaignReportDao = campaignReportDao;
    } 
    public CampaignReportDao getCampaignReportDao() {
    	return this.campaignReportDao;
    }

    private CampaignReportDaoForDML campaignReportDaoForDML;
    
    public CampaignReportDaoForDML getCampaignReportDaoForDML() {
		return campaignReportDaoForDML;
	}
	public void setCampaignReportDaoForDML(
			CampaignReportDaoForDML campaignReportDaoForDML) {
		this.campaignReportDaoForDML = campaignReportDaoForDML;
	}

	private CampaignSentDao campaignSentDao;
    private CampaignSentDaoForDML campaignSentDaoForDML;
    public CampaignSentDaoForDML getCampaignSentDaoForDML() {
		return campaignSentDaoForDML;
	}
	public void setCampaignSentDaoForDML(CampaignSentDaoForDML campaignSentDaoForDML) {
		this.campaignSentDaoForDML = campaignSentDaoForDML;
	}
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
    public OpensDao getOpensDao() {
        return this.opensDao;
    }
    
    private OpensDaoForDML opensDaoForDML;

    public OpensDaoForDML getOpensDaoForDML() {
		return opensDaoForDML;
	}
	public void setOpensDaoForDML(OpensDaoForDML opensDaoForDML) {
		this.opensDaoForDML = opensDaoForDML;
	}

	private EmailClientDao emailClientDao;
	private EmailClientDaoForDML emailClientDaoForDML;
	public EmailClientDaoForDML getEmailClientDaoForDML() {
		return emailClientDaoForDML;
	}
	public void setEmailClientDaoForDML(EmailClientDaoForDML emailClientDaoForDML) {
		this.emailClientDaoForDML = emailClientDaoForDML;
	}
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

    private ClicksDaoForDML clicksDaoForDML;
    
	public ClicksDaoForDML getClicksDaoForDML() {
		return clicksDaoForDML;
	}
	public void setClicksDaoForDML(ClicksDaoForDML clicksDaoForDML) {
		this.clicksDaoForDML = clicksDaoForDML;
	}

	private UnsubscribesDao unsubscribesDao;
	public UnsubscribesDao getUnsubscribesDao() {
		return unsubscribesDao;
	}
	public void setUnsubscribesDao(UnsubscribesDao unsubscribesDao) {
		this.unsubscribesDao = unsubscribesDao;
	}
	
	
	private UnsubscribesDaoForDML unsubscribesDaoForDML;

	
    public UnsubscribesDaoForDML getUnsubscribesDaoForDML() {
		return unsubscribesDaoForDML;
	}
	public void setUnsubscribesDaoForDML(UnsubscribesDaoForDML unsubscribesDaoForDML) {
		this.unsubscribesDaoForDML = unsubscribesDaoForDML;
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
    
	private ExternalSMTPEventsProcessor externalSMTPEventsProcessor;
	
	
	public ExternalSMTPEventsProcessor getExternalSMTPEventsProcessor() {
		return externalSMTPEventsProcessor;
	}

	public void setExternalSMTPEventsProcessor(
			ExternalSMTPEventsProcessor externalSMTPEventsProcessor) {
		this.externalSMTPEventsProcessor = externalSMTPEventsProcessor;
	}

	
	private CampaignReportEventsQueue campaignReportEventsQueue;
	
	
	public CampaignReportEventsQueue getCampaignReportEventsQueue() {
		return campaignReportEventsQueue;
	}
	public void setCampaignReportEventsQueue(
			CampaignReportEventsQueue campaignReportEventsQueue) {
		this.campaignReportEventsQueue = campaignReportEventsQueue;
	}

	private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	static {
		
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
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");       
        
        String requestedAction = request.getParameter("requestedAction");

        if(logger.isInfoEnabled()) logger.info("Requested Action is :  " + requestedAction);
        
        if(requestedAction==null) {
        	return null;
        }
		
        /*if (requestedAction.compareTo("couponcode") == 0) {
        	couponBarCode(request, response);
        	return null;
        } */
        
		if (requestedAction.equalsIgnoreCase("unsubReason")) {
			unsubscribeUpdate(request, response);
		} 
		
		
		//NOTE :This is only for after getting Resubscription hit from the contact. and this request getting from 
		//Webadding the contact through WebForm .and the contact is already exists and it is Unsubscribed 
		else if(requestedAction.equalsIgnoreCase(Constants.CONTACT_RESUBSCRIBE)) {
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
		long sentId;
		try {
			sentId = Long.parseLong(sentIdStr);
		} catch (NumberFormatException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception : Invalid sentId for the requested action - "+requestedAction);
			return null;
		} 
		
		
		if (requestedAction.compareTo("open") == 0) {
            openUpdate(request, response, sentId);
        } 
		else {
//			PrintWriter responseWriter = response.getWriter();
			if (requestedAction.compareTo("click") == 0) {
	            clickUpdate(request, response, sentId);
	        }  
			else if (requestedAction.compareTo("unsubscribe") == 0) {
				Long userId = null;
				try {
					userId = Long.parseLong(request.getParameter("userId"));
				} catch (NumberFormatException e) {
					if(logger.isWarnEnabled()) logger.warn(" userId not found in the querystring");
				}
				requestForUnsubscribe(request, response, sentId, userId);
	        }
			else if (requestedAction.compareTo("webpage") == 0) {
				Long contactId;
				try {
					contactId = Long.parseLong(request.getParameter(Constants.QS_CID));
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()) logger.error("** Exception : Invalid contactId for the requested action - "+
							requestedAction);
					return null;
				} 
	        	processWeblink(request, response, sentId, contactId);
	        }
			
			/*responseWriter.flush();
			responseWriter.close();*/
		}
        	
        if(logger.isDebugEnabled()) logger.debug("Exiting ");          
		
		return null;
		//TODO: better to redirect to the error page, instead of returning null
	}

    public void openUpdate(HttpServletRequest request, HttpServletResponse response, long sentId)
    						throws Exception {
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");       

        try {
        	
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			updateOpens(request, campaignSent);
			updateReport(campaignSent, Constants.CS_TYPE_OPENS);
			campaignSentDaoForDML.clear(campaignSent);

        } catch (Exception e) {
			logger.error("Exception : Problem while updating the opens \n", e);
		}
        
		try {
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the proxy server
			response.setContentType("image/gif");
			File imgFile = new File(PropertyUtil.getPropertyValue("appDirectory") + "/img/transparent.gif");
			FileInputStream inputStream = new FileInputStream(imgFile);  //read the file

		        response.setHeader("Content-Disposition","attachment; filename=transparent.gif");
		        try {
		            int c;
		            while ((c = inputStream.read()) != -1) {
		            response.getWriter().write(c);
		            }
		        } catch (Exception e) {
					// TODO: handle exception
				}finally {
		            if (inputStream != null) {
		                inputStream.close();
		                response.getWriter().close();
		            }
		        }
			
		} catch (Exception e) {
			logger.error("Exception : Problem while sending the open image \n", e);
		}
		
        
        if(logger.isDebugEnabled()) logger.debug("-- Exit --");
    }
    
    public void clickUpdate(HttpServletRequest request, HttpServletResponse response, long sentId) {
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        CampaignSent campaignSent = campaignSentDao.findById(sentId);
    
        String urlStr = request.getParameter(Constants.QS_URL);
        if(logger.isDebugEnabled()) logger.debug("<<<<<<<<<<<<<<< url Str : " + urlStr);
        if(urlStr==null) {
        	if(logger.isDebugEnabled()) logger.debug("URL is null");
        	return;
        } 
        
        /*if (urlStr.contains("http://www.loksatta.org/cms/")) {
        	// TODO: temp code need to remove it.
        	if(logger.isDebugEnabled()) logger.debug("view : "+ request.getParameter("view"));
        	urlStr = urlStr + "&view=" + request.getParameter("view") + "&id=" + request.getParameter("id");
        }*/
        
        try {
        	
        	/** checking for opens, if zero it update*/ 
        	if(campaignSent != null && campaignSent.getOpens() == 0) {
        		updateOpens(request, campaignSent);
        		updateReport(campaignSent, "opens");
        	} //if(campaignSent != null && campaignSent.getOpens() == 0)
        	
			Clicks clicks = new Clicks(campaignSent, urlStr, new Date());
			//clicksDao.saveOrUpdate(clicks);
			clicksDaoForDML.saveOrUpdate(clicks);

			clicksDaoForDML.clear(clicks);
			
			updateReport(campaignSent, Constants.CS_TYPE_CLICKS);
			campaignSentDaoForDML.clear(campaignSent);
        }

        catch (Exception e) {
        	logger.error("Exception : Problem while updating the Clicks \n", e);
		}
        try {
        	 if(urlStr.indexOf("http://") == -1 && (urlStr.indexOf("https://") == -1 )){
					urlStr = "http://" + urlStr.trim();
			}
			if(logger.isDebugEnabled()) logger.debug("Redirecting to the URL:" + urlStr);
			response.sendRedirect(urlStr);
		} catch (IOException e) {
			logger.error("Exception : Problem while redirecting to the url : " + urlStr + "\n",e);
		}finally{
			if(logger.isDebugEnabled()) logger.debug("---------contactScoreSetting update method is Called by clickUpdate---------------");
			
			contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
					Constants.SCORE_EMAIL_CLICK,campaignSent.getCampaignReport().getCampaignName());
			
		}
        if(logger.isDebugEnabled()) logger.debug("-- Exit --");
    }
    
    public void requestForUnsubscribe(HttpServletRequest request, HttpServletResponse response, 
			Long sentId, Long userId) throws Exception {
    	
    	if(logger.isInfoEnabled()) logger.info(" -- just entered --");
    	if(logger.isInfoEnabled()) logger.info("-------->Unsubscribe request for (sentId, userId) :"+sentId+","+userId);
    	
    	 try {
 			CampaignSent campaignSent = campaignSentDao.findById(sentId);
 			if(campaignSent == null) {
 				response.getWriter().print("Invalid Request...");
 				return;
 			}
 			if(logger.isDebugEnabled()) logger.debug("CampaignSentGetoPens is ---"+campaignSent.getOpens());
 			if(campaignSent.getOpens() == 0) {
 				if(logger.isDebugEnabled()) logger.debug("calling updte opens method");
	 			updateOpens(request, campaignSent);
	 			updateReport(campaignSent, Constants.CS_TYPE_OPENS);
	 			campaignSentDaoForDML.clear(campaignSent);
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
    	
    	logger.info("-- just entered--");
    	try {
    		
			Long userId = Long.parseLong(request.getParameter("userId"));
			Long crId = Long.parseLong(request.getParameter("crId"));
			Long sentId = Long.parseLong(request.getParameter("sentId"));
			
			short categoryWeight = Short.parseShort(request.getParameter("weight"));
			
			String reasonStr = request.getParameter("reason");
			String emailId = request.getParameter("emailId");
			
			if(logger.isDebugEnabled()) {
				logger.debug("(userId, crId, sentId, emailId, categoryWeight, Reason) :"+
					userId+","+crId+", "+sentId+", "+emailId+", "+categoryWeight+", "+reasonStr);
			}
			
			List<TemplateCategory> catList = campaignReportManager.updateUnsubscribe(userId, crId, 
					sentId, reasonStr, emailId, categoryWeight);
			if(logger.isDebugEnabled()) logger.debug("Category weight :"+ categoryWeight);
			
			
			
			
			if(categoryWeight == 0) {
				request.setAttribute("message", Constants._RESUBSCRIBE_RESPONSE_MSG);
				contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Active");
			}else {
				request.setAttribute("message", Constants._UNSUBSCRIBE_RESPONSE_MSG);
				
				// Unsubscribe all contacts of this email in all mailing lists for this user.
				contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Unsubscribed");				
				
				//****contactScoreSetting updateScore method  Calling ****
				if(logger.isDebugEnabled()) logger.debug("------contactScoreSetting update method is Called by unSubscribeUpdatae-------------");
				CampaignSent campaignSent = campaignSentDao.findById(sentId);
				contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
						Constants.SCORE_EMAIL_UNSUBSCRIBED,	campaignSent.getCampaignReport().getCampaignName());
				
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
    	catch (NumberFormatException e) {
			logger.error("** Exception: while updating the unsubscribe reason", e);
		}
		
	}
    
  
    
   public void processWeblink(HttpServletRequest request, HttpServletResponse response,
			Long sentId, Long contactId)  throws Exception {
		
	   PrintWriter responseWriter = response.getWriter();
	   try {
		//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------"+contactId);
			response.setContentType("text/html");
			
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------sentId"+sentId);
			if(campaignSent == null) {
				if(logger.isWarnEnabled()) logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				responseWriter.write(ERROR_RESPONSE);
				return;
			}
			
			Contacts contact = contactsDao.findById(contactId);
			CampaignReport campaignReport = campaignSent.getCampaignReport();
		
			
			
			//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------report"+campaignReport);
			if(campaignReport == null) {
				if(logger.isWarnEnabled()) logger.warn(" Campaign Report object not found for sentId :"+sentId);
				responseWriter.write(ERROR_RESPONSE);
				return;
			}
			
			if(campaignSent.getOpens() == 0) {
				openUpdate(request, response, campaignSent.getSentId());
				updateReport(campaignSent, Constants.CS_TYPE_OPENS);
			}
			String htmlContent = campaignReport.getContent();
			
			if(htmlContent == null) {
				responseWriter.write(ERROR_RESPONSE);
				return ;
			}
			
			Long userId = campaignReport.getUser().getUserId();
			
			String placeHoldersStr = campaignReport.getPlaceHoldersStr();
			String replaceStr="";
			StringBuffer sb = new StringBuffer(htmlContent);
			
			/********replace contact's placeholders***************************/
			if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
				String contactPhValStr = campaignSent.getContactPhValStr();
				if(contactPhValStr != null) {
					
					String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
					String keyStr = "";
					String ValStr = "";
					for (String phToken : phTokenArr) {
						
						keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
						ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());
						
						htmlContent = htmlContent.replace(keyStr, ValStr);
			
						if(keyStr.contains(PlaceHolders.CAMPAIGN_PH_UNSUBSCRIBE_LINK)) {
							
							String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
							String value = "<a href="+unsubURL+">unsubscribe</a>";
							htmlContent = htmlContent.replace(keyStr, value);
							//tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
						}
						else if(keyStr.contains(PlaceHolders.CAMPAIGN_PH_WEBPAGE_VERSION_LINK)){
							
							String webpagelink = PropertyUtil.getPropertyValue("weblinkUrl").replace("|^", "[").replace("^|", "]");
							
							String value = "<a style=\"color: inherit; text-decoration: underline; \"  href="+webpagelink+"" +
											" target=\"_blank\">View in web-browser</a>";
							
							htmlContent = htmlContent.replace(keyStr, value);
							//value = "<a href="+webpagelink+">Webpage Link</a>";
						}
						
						
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
			
			
			htmlContent = htmlContent.replace("[sentId]", sentId.toString());
			htmlContent = htmlContent.replace("[cId]", contactId.toString());
			htmlContent = htmlContent.replace("[userId]", userId.longValue()+"" );
			htmlContent = htmlContent.replace("[email]", contact == null ? " your email Id " : contact.getEmailId());
			
			
			
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
		logger.error("Exception ::::" , e);
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
    private void updateOpens(HttpServletRequest request, CampaignSent campaignSent) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("-- Just Entered  in UpdateOpens--");
			
			if (campaignSent != null) {
				
				String userAgent = 	(String) request.getAttribute(Constants.QS_USERAGENT);
				EmailClient client = emailClientCheck(userAgent, campaignSent);
				Long clientId=null;
				 if(client != null) {
					 
					 clientId = client.getEmailClientId();
				 }
				Opens opens = new Opens(campaignSent.getSentId(), new Date(), clientId );
				//opensDao.saveOrUpdate(opens);
				opensDaoForDML.saveOrUpdate(opens);

				opensDaoForDML.clear(opens);
				emailClientDaoForDML.clear(client);
			} // End if(campaignSent != null)
		} catch (Exception e) {
			logger.error("**Exception : error occured while saving the opens ",e);
		} finally { 
			if(logger.isDebugEnabled()) logger.debug("contactScoreSetting update method is Called ");
			contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
					Constants.SCORE_EMAIL_OPEN,	campaignSent.getCampaignReport().getCampaignName());
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
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        
        int updateCount = 0;
		if(campaignSent == null) {
			if(logger.isWarnEnabled()) logger.warn("CampiagnSent Object is null");
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
			//campaignReportDao.updateCampaignReport(campaignReport.getCrId(),  type);
			campaignReportDaoForDML.updateCampaignReport(campaignReport.getCrId(),  type);
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens in CampaignReport \n", e);
		}
		
        if(logger.isDebugEnabled()) logger.debug("-- Exit --");
        return updateCount;
    }

    /**
	 * Searches for the EmailClient
	 * @param request
	 * @param campaignSent
	 * @return if found returns EmailClinet otherwise returns null
	 */
	public EmailClient emailClientCheck(String userAgent,CampaignSent campaignSent){
		EmailClient client = null;
		try {
			if(logger.isDebugEnabled()) logger.debug("User-Agent : " + userAgent );
			
			//TODO: As Email Client list is static for all the request 
			//Need to declare emailClientList as instance Variable
			List<EmailClient> emailClientList  = emailClientDao.findAll();
			
			if(userAgent!=null) {
				String userAgentSearchStr = null;
				
				for (EmailClient emailClient : emailClientList) {
					userAgentSearchStr = emailClient.getUserAgent();
					if(userAgentSearchStr!=null) {
						if(userAgent.contains(userAgentSearchStr)){
							if(logger.isDebugEnabled()) logger.debug("Email client : " + client);
							return emailClient;
						}
					}
				} // for
			} // if

			String toEmailId = campaignSent.getEmailId();
			
			if(toEmailId!=null) {
				String domain = toEmailId.substring(toEmailId.indexOf('@')+1, toEmailId.indexOf('.',toEmailId.indexOf('@')));
				if(logger.isDebugEnabled()) logger.debug("Domain of sent mail : " + domain);
				
				for (EmailClient emailClient : emailClientList) {
					if(emailClient.getEmailClient().toLowerCase().contains(domain.toLowerCase())) {
						return emailClient;
					}
					else if(emailClient.getEmailClient().equals("Others")) {
						client = emailClient;
					}
				} // for
			} // if toEmailId
			emailClientList = null;
		} catch (Exception e) {
			logger.error("Exception : ", (Throwable)e);
		}		
		return client;
	} // emailClientCheck(HttpServletRequest request,CampaignSent campaignSent)
	
	
	
	public void contactResubcription(HttpServletRequest request) {
		
		String tempEmailId = request.getParameter("emailId");
		Long tempUserId = Long.parseLong(request.getParameter("userId"));
		if(logger.isDebugEnabled()) logger.debug(">>emailId>>"+tempEmailId);
		if(logger.isDebugEnabled()) logger.debug(">>tempUserId >>"+tempUserId);
		
		if(tempEmailId == null || tempEmailId.trim().length() == 0) {
			return ;
		}else if(tempUserId == null ){
			return;
		}
		
		//Find the User Object if exist
		
		//Update the Contact Status 
		contactsDaoForDML.updateEmailStatusByUserId(tempEmailId, tempUserId, "Active");
		
		//Remove Contact From Unsubscribe
		//unsubscribesDao.deleteByEmailIdUserId(tempEmailId, tempUserId);
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
			logger.info("couponcode: "+couponcode);
			logger.info("couponcode width: "+request.getParameter("width"));
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
			
			try {
				int c;
				while ((c = bais.read()) != -1) {
					response.getWriter().write(c);
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				if (bais != null) {
					bais.close();
					response.getWriter().close();
				}
			}

		} catch (Exception e) {
			logger.error("Exception : Problem while generating coupon bar code \n",	e);
		}

	}
	
	
	
}
