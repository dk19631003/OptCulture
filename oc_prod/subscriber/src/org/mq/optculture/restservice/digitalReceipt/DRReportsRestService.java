package org.mq.optculture.restservice.digitalReceipt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.DRSMSChannelSent;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.DRSMSChannelSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.digitalReceipt.DRReportRequest;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.DRReportRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

public class DRReportsRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) {
		String requestedAction = request.getParameter("requestedAction");
		//Long sentId = Long.parseLong(request.getParameter(Constants.DR_SENTID));
		Long sentId=null;
		if (request.getParameter(Constants.DR_SENTID) != null) {
			try {
				sentId = Long.parseLong(request.getParameter(Constants.DR_SENTID));
			} catch (Exception e) {
				try {
					sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(request.getParameter(Constants.DR_SENTID)));
				} catch (Exception e1) {
					logger.error("Exception :"+e1);
				} //app-3769
				logger.info("Decrypted SentId ..................:" + sentId);
			} 
		}

		if(requestedAction==null) {
			return null;
		}
		DRReportRequest drReportRequest = new DRReportRequest();
		drReportRequest.setRequestedAction(requestedAction);
		drReportRequest.setSentId(sentId);


		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.DR_REPORT_BUSINESS_SERVICE);

		if (OCConstants.DR_REPORT_ACTION_TYPE_OPEN.equals(drReportRequest.getRequestedAction()) ||
				OCConstants.DR_REPORT_ACTION_TYPE_OPEN_SMS.equals(drReportRequest.getRequestedAction())) {

			try {
				baseService.processRequest(drReportRequest);

				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setDateHeader("Expires", 0); // prevents caching at the proxy server
				response.setContentType("image/gif");
				File imgFile = new File(PropertyUtil.getPropertyValue(Constants.APP_DIRECTORY) + "/img/digitransparent.gif");
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				try {
					byte b[] = new byte[8];
					int count;
					while((count=in.read(b)) != -1) {
						out.write(b,0,count);
					}

				} catch (Exception e) {
					logger.error("Exception ::", e);
				}finally {
					out.flush();
					out.close();
					in.close();
				}
			} catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
			}
		}
		else if(OCConstants.DR_REPORT_ACTION_TYPE_CLICK.equals(drReportRequest.getRequestedAction()) ||
				OCConstants.DR_REPORT_ACTION_TYPE_CLICK_SMS.equals(drReportRequest.getRequestedAction())){
			try {
				String urlStr = request.getParameter(Constants.DR_URL);
				urlStr = Utility.decodeUrl(urlStr);
				if(urlStr == null) {
					return null;
				}
				drReportRequest.setUrl(urlStr);
				baseService.processRequest(drReportRequest);

				response.sendRedirect(drReportRequest.getUrl());
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}else if(OCConstants.REPORT_ACTION_TYPE_PDF.equals(drReportRequest.getRequestedAction()) ||
				OCConstants.REPORT_ACTION_TYPE_PDF_SMS.equals(drReportRequest.getRequestedAction()) ||
				OCConstants.REPORT_ACTION_TYPE_PDF_WA.equals(drReportRequest.getRequestedAction())){
			logger.info("===========inside pdf generation block=========");
			
			final String USER_PARENT_DIR=PropertyUtil.getPropertyValue("usersParentDirectory");
			String PDF_CMD= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
		  //    String PDF_TAKE_TIME=PropertyUtil.getPropertyValueFromDB("pdfTimeTaken");
			    File pdfFile =null;
			    File htmlFile = null;
			   
			    FileInputStream fileInputStream = null ;
			    OutputStream responseOutputStream=null;
			
			 try {
			long sId=drReportRequest.getSentId();
			Users user=null;
			String htmlContent ="";
			UsersDao userDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			DRSentDao drSentDao = (DRSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
            DRSent drSent = drSentDao.findById(sId);
            if(OCConstants.REPORT_ACTION_TYPE_PDF_SMS.equals(drReportRequest.getRequestedAction())) {
            	DRSMSSentDao drSmsSentDao = (DRSMSSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
                DRSMSSent drSmsSent = drSmsSentDao.findById(sId);
                if(drSmsSent==null)return null;
                user=userDao.findByUserId(drSmsSent.getUserId());
                htmlContent=drSmsSent.getHtmlStr();
            }else if(OCConstants.REPORT_ACTION_TYPE_PDF_WA.equals(drReportRequest.getRequestedAction())) {//OPS-391
            	DRSMSChannelSentDao drSmsChannelSentDao = (DRSMSChannelSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_Channel_SENT_DAO);
                DRSMSChannelSent drSmsChannelSent = drSmsChannelSentDao.findById(sId);
                if(drSmsChannelSent==null)return null;
                user=userDao.findByUserId(drSmsChannelSent.getUserId());
                htmlContent=drSmsChannelSent.getHtmlContent();
            }else {
            	user=userDao.findByUserId(drSent.getUserId());
            	htmlContent=drSent.getHtmlStr();
            }
			    Calendar cal=Calendar.getInstance();
			    String pdfFileName="DR_PDF_"+cal.getTimeInMillis()+"_"+sId+".pdf";
			    String htfileName="DR_HTML_"+cal.getTimeInMillis()+"_"+sId+".html";
			  //  String imgFileName="BAR_CODE_"+cal.getTimeInMillis()+"_"+drsent.getDocSid()+".png";
			    
			 //  logger.info("User Name "+user.getUserName());
				String accName=user.getUserName();
				GetUser.checkUserFolders(accName);
			    String htmlPath= USER_PARENT_DIR+File.separator+accName+File.separator+OCConstants.DR+File.separator+OCConstants.HTML+File.separator+htfileName;
			    String pdfPath= USER_PARENT_DIR+File.separator+accName+File.separator+OCConstants.DR+File.separator+OCConstants.PDF+File.separator+pdfFileName;
			 //   String imgPath=USER_PARENT_DIR+File.separator+user.getUserName()+File.separator+OCConstants.DR+File.separator+OCConstants.DR_BARCODE+File.separator+imgFileName;
			    logger.info("Html Path "+htmlPath);
			    logger.info("pdf path "+pdfPath);
			 //   logger.info("img path "+imgPath);
			    File myHtmlTemp = new File(htmlPath);
				File parentDir = myHtmlTemp.getParentFile();
				 if(!parentDir.exists()) {
					 parentDir.mkdir();
					}
				  File myPdfTemp = new File(pdfPath);
					File pdfParentDir = myPdfTemp.getParentFile();
					 if(!pdfParentDir.exists()) {
						 pdfParentDir.mkdir();
						}
				 Document doc = Jsoup.parse(htmlContent);
				 Elements elements = doc.select("body");
				 htmlContent = elements.toString();
				 //logger.info("htmlContent Body..........."+htmlContent);
            //generating html file
            if(htmlContent!=null){
				String patternToReplace = "(<img style=\"float:left;margin-right:5px;\" height=\"13\" width=\"19\" alt=\"IUS\" \\S*>)";
				htmlContent = htmlContent.replaceAll(patternToReplace,"");
            	 String newHtmlContent=	 htmlContent.replace("To Download as a PDF", " ").replace("click here", " ");
            	htmlFile =new File(htmlPath);
		    /* BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
		     bw.write(newHtmlContent);
		     bw.close();*/
            	FileOutputStream fos = new FileOutputStream(htmlFile);
 		       OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_16);
 		       BufferedWriter writer = new BufferedWriter(osw);

 		      writer.write(newHtmlContent);
 		      writer.close();
      		 //generate pdf
            logger.info(" pdf gereration is started");
            try {
	            ProcessBuilder pb = new ProcessBuilder(PDF_CMD+"bin/phantomjs",PDF_CMD+"htmltoImage/htmltoImage.js", htmlPath, pdfPath);
	            Process p = pb.start();
				int exitVal = p.waitFor(); // do a wait here to prevent it running for ever
				if (exitVal != 0) {
					logger.error("EXIT-STATUS - " + p.toString());
				}
				p.destroy();
            }catch (Exception e) {
            	logger.info("Exception ::"+e);
			}
			
         	 logger.info(" pdf gereration is done");
         	//writing generated file to response
         	// logger.info("Time  "+PDF_TAKE_TIME);
         	/* Thread th=Thread.currentThread();
       		  th.join(Integer.parseInt(PDF_TAKE_TIME));*/
        	  logger.info(" pdf writing to response is started");
         	 response.setContentType("application/pdf");
			  response.setHeader("Content-Disposition", "attachment; filename="+pdfFileName);
			    pdfFile= new File(pdfPath);
			    fileInputStream = new FileInputStream(pdfFile);
				 responseOutputStream = response.getOutputStream();
			  int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				
		    	  }
				logger.info(" pdf writing to response is done");
              	if(htmlFile.exists())
         		htmlFile.delete();
    		   if(pdfFile.exists())
    			pdfFile.delete();
    		   logger.info(" generated html and pdf files are deleted ");
            }
            return null;
			} catch ( Exception  e) {
				 
				logger.error("Exception" ,e);
			
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
		}else if (OCConstants.DR_REPORT_ACTION_TYPE_WEBPAGE.equals(drReportRequest.getRequestedAction())) {
			
			try {
				BaseResponseObject baseResponseObj = baseService.processRequest(drReportRequest);
				PrintWriter responseWriter = response.getWriter();
				//response.addHeader("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >", "<link rel=\"icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >");
				response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
				response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
				response.setContentType("text/html");
				
				if(baseResponseObj == null || baseResponseObj.getResponseObject() == null) {
					responseWriter.write("E-Receipt/Purchase details are not available for this Transaction.");
					return null;
				}
				responseWriter.write(baseResponseObj.getResponseObject().toString());
			} catch (BaseServiceException e) {
				logger.error("Exception ::" , e);
			} catch (IOException e) {
				logger.error("Exception ::" , e);
			}
			
		}else if(OCConstants.DR_REPORT_ACTION_TYPE_BARCODE.equals(drReportRequest.getRequestedAction())){
			
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setDateHeader("Expires", 0); // prevents caching at the
			// proxy server
			response.setContentType("image/png");
			
/*			if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(campaignReportResponse.getStatus())) {
				return null;
			}
			
			
*/

			String receiptNumber = (request.getParameter(OCConstants.DR_RECEIPTID));
			int width = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeWidth"));//get width from db application properties table
			int height = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeHeight"));
           try{
			BitMatrix bitMatrix = new Code128Writer().encode(receiptNumber.toString(), BarcodeFormat.CODE_128, width, height,null);
          
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
			//Create Barcode in UserData for PDF Download Purpose
			DRSentDao drSentDao = (DRSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
			DRSentDaoForDML drSentDaoForDML = (DRSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SENT_DAO_ForDML);
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
			
			DRSent drSent =drSentDao.findById(sentId);
			
			Users user = usersDao.find(drSent.getUserId());
			
			//HardCoded access from constants file
			String accName=user.getUserName() ; //AccountHelper.getAccountNameById(drSent.getAccountId());
			 GetUser.checkUserFolders(user.getUserName());
			String bclnImg = accName+File.separator+
					OCConstants.DR+File.separator+OCConstants.DR_BARCODE+File.separator+sentId+".png";
			String BACODE_PATH = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
			
			String BACODE_URL = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
			logger.info("BACODE_PATH :"+BACODE_PATH);
			logger.info("BACODE_URL :"+BACODE_URL);
			File myTemplateFile = new File(BACODE_PATH);
			File parentDir = myTemplateFile.getParentFile();
			 if(!parentDir.exists()) {
				 parentDir.mkdir();
				}
			if(!myTemplateFile.exists()) {
				
				MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
	            		new File(BACODE_PATH)));	
			}
			
			//String barcodeImgtag = "<img id=\""+idAttr+"\" src=\""+ccPreviewUrl+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" />";
			String drBarCodeUrl = PropertyUtil.getPropertyValue("DRBarCodeUrl");
			drBarCodeUrl = drBarCodeUrl.replace("|^", "[").replace("^|", "]");
			drBarCodeUrl = drBarCodeUrl.replace("[sentId]", sentId.longValue()+Constants.STRING_NILL);
			drBarCodeUrl = drBarCodeUrl.replace("[DRBCreceiptNumber]", receiptNumber+Constants.STRING_NILL);
			
			drBarCodeUrl = drBarCodeUrl.replace("[DRBCdocumentNumber]", drSent.getDocSid()+Constants.STRING_NILL);
			//htmlStr = htmlStr.replace(imgtag, barcodeImgtag);
		
			String htmlContent = drSent.getHtmlStr();
			
			logger.info("Actual drBarCodeUrl to be Replaced :"+drBarCodeUrl);
          
			htmlContent = htmlContent.replace(drBarCodeUrl, BACODE_URL);
			
			drSent.setHtmlStr(htmlContent);
			
			drSentDaoForDML.saveOrUpdate(drSent);
			logger.info("Saved drSentDaoForDML");
		
           } catch (Exception e) {
      			logger.error("Exception :: ",e);
      		}
			
			
		}else if(OCConstants.DR_REPORT_ACTION_TYPE_DR_SMS.equals(drReportRequest.getRequestedAction())) {

			
			try {
				BaseResponseObject baseResponseObj = baseService.processRequest(drReportRequest);
				PrintWriter responseWriter = response.getWriter();
				//response.addHeader("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >", "<link rel=\"icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >");
				response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
				response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
				response.setContentType("text/html");
				
				if(baseResponseObj == null || baseResponseObj.getResponseObject() == null) {
					responseWriter.write("eReceipt/Purchase details are not available for this transaction.");
					return null;
				}
				responseWriter.write(baseResponseObj.getResponseObject().toString());
			} catch (BaseServiceException e) {
				logger.error("Exception ::" , e);
			} catch (IOException e) {
				logger.error("Exception ::" , e);
			}
			
		
		}else if(OCConstants.DR_REPORT_ACTION_TYPE_WA.equals(drReportRequest.getRequestedAction())) {

			
			try {
				BaseResponseObject baseResponseObj = baseService.processRequest(drReportRequest);
				PrintWriter responseWriter = response.getWriter();
				//response.addHeader("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >", "<link rel=\"icon\" type=\"image/x-icon\" href=\"http://qcapp.optculture.com/subscriber/img/favicon.ico\" >");
				response.addHeader("Link" , "<https://qcapp.optculture.com/favicon.ico>; rel=\"icon\"");
				response.addHeader("Link", "<https://qcapp.optculture.com/favicon.ico>; rel=\"shortcut icon\"");
				response.setContentType("text/html");
				
				if(baseResponseObj == null || baseResponseObj.getResponseObject() == null) {
					responseWriter.write("eReceipt/Purchase details are not available for this transaction.");
					return null;
				}
				responseWriter.write(baseResponseObj.getResponseObject().toString());
			} catch (BaseServiceException e) {
				logger.error("Exception ::" , e);
			} catch (IOException e) {
				logger.error("Exception ::" , e);
			}
			
		
		}
		return null;
	}

}
