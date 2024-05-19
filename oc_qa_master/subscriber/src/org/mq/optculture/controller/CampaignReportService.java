package org.mq.optculture.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.campaign.CampaignReportRequest;
import org.mq.optculture.model.campaign.CampaignReportResponse;
import org.mq.optculture.model.campaign.EmailPdfResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
import com.google.zxing.client.j2se.MatrixToImageWriter;

public class CampaignReportService  extends AbstractController{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("Calling rest service to record unsubscribe...");
		String requestedAction = request.getParameter("requestedAction");
		String action = request.getParameter("action");
		String updatedAction = request.getParameter("updatedAction");

		

		if(requestedAction==null && action==null && updatedAction == null ) {
			return null;
		}

		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		CampaignReportRequest campaignReportRequest = new CampaignReportRequest();

		if(requestedAction != null) {
			campaignReportRequest.setActionType(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION);
			campaignReportRequest.setAction(requestedAction);
			
		}
		else if (action != null){
			campaignReportRequest.setActionType(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION);
			campaignReportRequest.setAction(action);
		}
				
		
		if(requestedAction != null || action != null) {
		
		campaignReportRequest.setSentId(request.getParameter("sentId"));
		campaignReportRequest.setUrl(request.getParameter("url"));
		campaignReportRequest.setUserId(request.getParameter("userId"));
		campaignReportRequest.setcId(request.getParameter("cId"));
		campaignReportRequest.setCrId(request.getParameter("crId"));
		campaignReportRequest.setType(request.getParameter("type"));
		campaignReportRequest.setCode(request.getParameter("code"));
		campaignReportRequest.setWidth(request.getParameter("width"));
		campaignReportRequest.setHeight(request.getParameter("height"));
		campaignReportRequest.setUserAgent(request.getHeader("User-Agent"));
		campaignReportRequest.setEmailId(request.getParameter("emailId"));
		campaignReportRequest.setWeight(request.getParameter("weight"));
		campaignReportRequest.setReason(request.getParameter("reason"));
		campaignReportRequest.setRequestUrl(request.getRequestURL().toString());
		campaignReportRequest.setQueryString(request.getQueryString());
		campaignReportRequest.setSource(request.getParameter("CampaignSource"));
		campaignReportRequest.setCampaignId(request.getParameter("campaignId"));
		campaignReportRequest.setUnSubscribeReqType(request.getParameter(Constants.UNSUBSCRIBE_REQUEST_TYPE));
		logger.info("received request : "+request.getParameterNames());
		
		}
				logger.info("campaignId , "+request.getParameter("campaignId")+" action : "+action);
		
		if(updatedAction != null) {
		String email = "";
		Long campaignId = 0L;
		Long reportId = 0L;
		Long userId = 0L;
		Long contactId = 0L;
		
		logger.info("received request : "+request.getParameter("hash"));
		String hash = request.getParameter("hash");
		if(hash!=null && !hash.isEmpty()){
			//redirect to 3.0 with params , crId, there, cId there userId,there emailId, there campId. added here.
			logger.info("entered in the 3.0 flow. with campaign Id : "+ request.getParameter("campaignId")+", "+hash);
			
			// only for subreq
			byte[] decodedBytes = Base64.decodeBase64(hash);
			String decodedString = new String(decodedBytes);
			

	String[] paramList = decodedString.split("&");
		
		//https://qcapp.optculture.com/updateReport.mqrm?updateAction=unsubscribe?
		//emailId=|^emailId^|&
		//campId=|^campId^|&
		//crId=|^crId^|&
		//userId=|^userId^|
	logger.info("params : "+paramList);
		
		for(String param : paramList ) {
			
			String orginalParams[]  = param.split("=");
			
			if(orginalParams[0].contains("email")) {
			 email = orginalParams[1];
			 logger.debug("email : "+email);
			}else if(orginalParams[0].contains("campId")) {
				campaignId  = Long.valueOf(orginalParams[1]);
				 logger.debug("campaignId : "+campaignId);
			}else if(orginalParams[0].contains("crId")) {
				reportId  = Long.valueOf(orginalParams[1]);
				 logger.debug("reportId : "+reportId);

			}else if(orginalParams[0].contains("userId")) {
				userId  = Long.valueOf(orginalParams[1]);
				 logger.debug("userId : "+userId);

			}else if(orginalParams[0].contains("cId")) {
				 contactId = Long.valueOf(orginalParams[1]);
				 logger.debug("cId : "+contactId);

			}
		}
		
			campaignReportRequest.setcId(String.valueOf(contactId));
			campaignReportRequest.setCrId(String.valueOf(reportId));
			campaignReportRequest.setEmailId(email);
			campaignReportRequest.setUserId(String.valueOf(userId));
			campaignReportRequest.setCampaignId(String.valueOf(campaignId));
			campaignReportRequest.setAction("unsubscribe");
			campaignReportRequest.setSource("Platform");
			campaignReportRequest.setQueryString("hash");
			campaignReportRequest.setActionType("updatedAction");
			campaignReportRequest.setUnSubscribeReqType(request.getParameter(Constants.UNSUBSCRIBE_REQUEST_TYPE));

			
			logger.info("entered in the 3.0 flow. after decrypting campaign Id : "+ campaignId+" Parameter  : "+request.getParameter("campaignSource")+
					"attribute : "+request.getAttribute("campaignSource"));

		}
	
		}

		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.CAMPAIGN_REPORT_BUSINESS_SERVICE);
		campaignReportResponse = (CampaignReportResponse) baseService.processRequest(campaignReportRequest);
		
		logger.info("after executing the method : "+campaignReportResponse.getResponseType());
		
		if(OCConstants.RESPONSE_TYPE_OPEN_IMAGE.equalsIgnoreCase(campaignReportResponse.getResponseType())) {
			try {
				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setDateHeader("Expires", 0); // prevents caching at the proxy server
				response.setContentType("image/gif");
				File imgFile = new File(PropertyUtil.getPropertyValue("appDirectory") + "/img/transparent.gif");
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
			}catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
			}

		}
		else if(OCConstants.RESPONSE_TYPE_CLICK_REDIRECT.equalsIgnoreCase(campaignReportResponse.getResponseType())) {
			try {
				if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
					return null;
				}
				response.sendRedirect(campaignReportResponse.getUrlStr());
				
			}catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
			}

		}
		else if(OCConstants.RESPONSE_TYPE_WEB_LINK.equalsIgnoreCase(campaignReportResponse.getResponseType())) {
			
			PrintWriter responseWriter = response.getWriter();
			//response.addHeader("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >", "<link rel=\"icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >");
			response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
			response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
			response.setContentType("text/html");
			
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				responseWriter.write(campaignReportResponse.getErrorResponse());
				return null;
			}
			responseWriter.write(campaignReportResponse.getHtmlContent());
		}
		else if(OCConstants.RESPONSE_TYPE_RESUBSCRIBE.equalsIgnoreCase(campaignReportResponse.getResponseType())) {
			
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				return null;
			}
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head><link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://qcapp.optculture.com/favicon.ico\" ><link rel=\"icon\" type=\"image/x-icon\" href=\"https://qcapp.optculture.com/favicon.ico\" ></head><body><table><tr><td>Thank you very much for your interest to receive the emails from this user.</td></tr></table></body></html>");
			
			pw.flush();
			pw.close();
			
		}
		else if(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_REQ.equalsIgnoreCase(campaignReportResponse.getResponseType()) || campaignReportResponse.getResponseType().equalsIgnoreCase("PlatformUnsubscribe")
				|| campaignReportRequest.getActionType().equalsIgnoreCase("updatedAction")) {
			
			
			logger.info("will be redirecting to unsubscribe.jsp"+campaignReportResponse.getCampaignId());

		
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				response.getWriter().print("Invalid Request...");
				return null;
			}
		
			logger.info("Added Params to unsubscribe.jsp");


			// userId,- done  cId -done , crId- done , email- done, campid- done
			request.setAttribute(Constants.QS_USERID, campaignReportResponse.getUserId());
			request.setAttribute(Constants.QS_SENTID, campaignReportResponse.getSentId());
			request.setAttribute(Constants.QS_EMAIL, campaignReportResponse.getEmailId());
			
			request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE, Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB);
			if(campaignReportRequest.getActionType().equalsIgnoreCase("updatedAction") || campaignReportResponse.getResponseType().equalsIgnoreCase("PlatformUnsubscribe")) {
			
			request.setAttribute(Constants.QS_CID,campaignReportResponse.getcId());
			request.setAttribute(Constants.QS_CRID,campaignReportResponse.getCrId());
			request.setAttribute("campaignId", campaignReportResponse.getCampaignId());
			request.setAttribute("CampaignSource", "Platform");
			//request.setAttribute("action","EmailCommUnsubscribe");
			
			}
			RequestDispatcher reqDispatcher = getServletContext().
					getRequestDispatcher(OCConstants.REDIRECT_UNSUB_LINK);

			response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
			response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
			reqDispatcher.forward(request, response);
		}
		
		else if(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_UPDATE.equalsIgnoreCase(campaignReportResponse.getResponseType())) {
			
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				response.getWriter().print("Invalid Request...");
				return null;
			}
			if(campaignReportRequest.getSource().equalsIgnoreCase("Platform")) {
				
				logger.info("Completed updated Action. subscribtion is success");
				return null;
				
			}
			//logger.info("########################"+request.getParameter("unsubReqType"));
			
			if(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE.equals(request.getParameter("unsubReqType"))){
				request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE);
			}else if(Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB.equals(request.getParameter("unsubReqType"))){ 
				request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE);
			}
			request.setAttribute(Constants.QS_EMAIL, campaignReportResponse.getEmailId());
			request.setAttribute(Constants.QS_USERID, campaignReportRequest.getUserId());
			request.setAttribute(Constants.QS_SENTID, campaignReportRequest.getSentId());
			RequestDispatcher reqDispatcher = getServletContext().
					getRequestDispatcher(OCConstants.REDIRECT_UNSUB_LINK);

			reqDispatcher.forward(request, response);
		}		
		
		else if(OCConstants.RESPONSE_TYPE_FARWARD_UPDATE.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				response.getWriter().print("Invalid Request...");
				return null;
			}
			request.setAttribute(Constants.QS_SENTID, campaignReportResponse.getCampaignSent());

			RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/view/farward.jsp");

			reqDispatcher.forward(request, response);
		}
		
		else if(OCConstants.RESPONSE_TYPE_UPDATE_SUBSCRPTION.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				return null;
			}
			
			if(!campaignReportResponse.getUser().getSubscriptionEnable()){
				RequestDispatcher reqDispatcher = getServletContext().
						getRequestDispatcher(OCConstants.REDIRECT_UPDATE_SUBS_ERROR_LINK);

				reqDispatcher.forward(request, response);			
			}
			request.setAttribute(Constants.QS_SENTID, campaignReportResponse.getSentId());
			request.setAttribute(Constants.QS_CID, campaignReportResponse.getcId());
			  
			RequestDispatcher reqDispatcher = getServletContext().
					getRequestDispatcher(OCConstants.REDIRECT_UPDATE_SUBS_LINK);
			
			response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
			response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
			reqDispatcher.forward(request, response);
		}
		
		else if(OCConstants.RESPONSE_TYPE_REDIRECT_SHARE.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			response.sendRedirect(campaignReportResponse.getUrlStr());
		}
		
		else if(OCConstants.RESPONSE_TYPE_SHARE_LINK.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				pw.write(campaignReportResponse.getErrorResponse());
				return null;
			}
			pw.write(campaignReportResponse.getHtmlContent());
		}
		
		else if(OCConstants.RESPONSE_TYPE_BARCODE.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the
			// proxy server
			response.setContentType("image/png");
			
			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				return null;
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(campaignReportResponse.getBitMatrix(), "png", baos);
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
			
		}else if(OCConstants.REPORT_ACTION_TYPE_PDF.equalsIgnoreCase(campaignReportResponse.getResponseType())){
			FileInputStream fileInputStream = null;
			OutputStream responseOutputStream=null;
			try {
			EmailPdfResponseObject emailPdfResponseObject = (EmailPdfResponseObject)campaignReportResponse.getResponseObject();
			response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename="+emailPdfResponseObject.getPdfName());
            File htmlFile = new File(emailPdfResponseObject.getHtmlFilePath());
            File pdfFile = new File(emailPdfResponseObject.getPdfFilePath());
            fileInputStream = new FileInputStream(pdfFile);
            responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
              responseOutputStream.write(bytes);
            }
            if(htmlFile.exists())
              htmlFile.delete();
            if(pdfFile.exists())
              pdfFile.delete();
			}catch (Exception e) {
				logger.error("email to pdf campiagn "+e);
			}
            finally {
                try {
				if(fileInputStream!=null && responseOutputStream !=null){
                    responseOutputStream.flush();
                    responseOutputStream.close();
                    fileInputStream.close();
                  }
                } catch (IOException e) {       
                  logger.error("Exception ",e);
                  return null;
                }
              }
		}
		
		return null;
	}
}
