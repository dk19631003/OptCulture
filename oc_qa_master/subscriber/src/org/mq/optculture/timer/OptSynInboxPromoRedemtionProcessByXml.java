package org.mq.optculture.timer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.PromoTrxLog;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.couponcode.CouponCodeRedeemedService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemRequests;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedObj;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class OptSynInboxPromoRedemtionProcessByXml  extends TimerTask {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	
	
	public void run(){
		
		try {
			logger.info("Started OptSynInboxPromoRedemtionProcessByXml timer...");
			promoInboxFilesProcess();
			logger.info("end OptSynInboxPromoRedemtionProcessByXml timer...");
		} catch (Exception e) {
			logger.error("Error : occured while generaing the Marshal and unmarshal the Objects ",e);
		}
	} //run



	
	private void promoInboxFilesProcess(){
		
		
		String inboxPromoPath = PropertyUtil.getPropertyValue("inboxpromo");
		String donePath = PropertyUtil.getPropertyValue("donepromo");
		
		if(inboxPromoPath == null || inboxPromoPath.trim().length() == 0) {
			logger.info("inboxPromoPath is not Existed ::"+inboxPromoPath);
			return;
		}
		logger.info("inboxPromoPath is ::"+inboxPromoPath);
		try {
			if(XMLUtil.inboxFileExist(inboxPromoPath)) {
				
				File file = new File(inboxPromoPath);
				File[] xmlFiles  = file.listFiles();
				
				if(xmlFiles == null || xmlFiles.length == 0){
					logger.info("No files exists for this inbox folder :: "+inboxPromoPath);
					return;
				}
				
				logger.info("List out the inbox issuance files..."+xmlFiles.length);
				
				
				for(File xmlFile1 : xmlFiles){
	//				if(!xmlFile.getName().endsWith(".zip")) continue;
					File generatedFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						
						generatedFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxPromoPath);
						
						if(generatedFile == null) {
							logger.info("Unzip falied for this file name  "+xmlFile1.getAbsolutePath());
							
							//TODO write a txt file and put them in to outbox as txt
							writeStatusFile(xmlFile1.getName());
							
							//Zip Move to done folder
							xmlFile1.renameTo(new File(donePath+File.separator+xmlFile1.getName()));
							 
							continue;
							
						}
						
						//Zip Move to done folder
						xmlFile1.renameTo(new File(donePath+File.separator+xmlFile1.getName()));
					
					}else {
						generatedFile = xmlFile1;
					}
				
					
					boolean validXmlFile = validateXmlWithXsd(generatedFile.toString());
					logger.debug("is Validate XML is  ::"+validXmlFile);
					if(!validXmlFile){
						//logger.info("Invalid xml file..."+xmlFile);
						XMLUtil.renameAndMove(generatedFile.toString());
						writeFailedMessage("Temporary","Invalid xml file", generatedFile.getName(), ".xml");
						continue;
					}
					
					boolean isXmlFile = XMLUtil.checkFileWithXmlExtension(generatedFile.getName());
					if(isXmlFile){
						processXmlFile(generatedFile);
						//renameAndMove(xmlFile.toString());
					}
				}
			}
			else{
				logger.info("promo inbox issuance files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in xml promo file test", e);
			return;
		} finally {
			logger.info("Completed PromoIssuanceByXML timer...");
		}
		return;
	}
	
	
	private boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("promoRedemtionschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, enrollxsd);
		} catch (BaseServiceException e) {
			logger.error("Error occured validation of xml to xsd " , e);
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}
	
	
	private void writeFailedMessage(String reason, String errorMessage, String xmlfileName, String extention){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extention))+".txt";
		try {
			File file = new File(outboxPromoPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason: ");
			str.append(reason);
			str.append("\r\n");
			str.append("Error Mesage: ");
			str.append(errorMessage);
			bw.write(str.toString());
			
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		
		try {
			String chOwnCmd = "chown "+owner+":"+group+" "+outboxPromoPath+"/"+fileName;
			logger.debug("chaging the file permissions =="+ chOwnCmd);
			Runtime.getRuntime().exec(chOwnCmd);
			
			String chmodCmd = "chmod 777 "+outboxPromoPath+"/"+fileName;
			Runtime.getRuntime().exec(chmodCmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing failed message of file :"+xmlfileName);
	}
	
	public void processXmlFile(File xmlfile){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		try{
			//Object object =  XMLUtil.unMarshal(xmlfile.toString(), CouponCodeRedeemRequests.class);
			
			CouponCodeRedeemRequests promoRootObject = (CouponCodeRedeemRequests) XMLUtil.unMarshal(xmlfile.toString(), CouponCodeRedeemRequests.class);
			int redemtionReqSize = promoRootObject.getCouponCodeRedeemReqList().size();
			logger.info("promoRootObject  size is  "+promoRootObject.getCouponCodeRedeemReqList().size());
			CouponCodeRedeemedObj couponRedeemObj = null;
			Gson gson =new Gson();
			String outboxPromoPath = PropertyUtil.getPropertyValue("outboxpromo");
			
			outboxPromoPath += outboxPromoPath.endsWith(File.separator) ? "" : File.separator;
			String promoReportsCSVFilePath = outboxPromoPath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			File inboxPromoReportCSVFile = new File(promoReportsCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(promoReportsCSVFilePath));
			//bw.write("\"Request ID\",\"PromoCode\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			for (int i = 0; i < promoRootObject.getCouponCodeRedeemReqList().size(); i++) {
				
				String requestJson = new Gson().toJson(promoRootObject.getCouponCodeRedeemReqList().get(i), CouponCodeRedeemReq.class);
				
				logTransactionRequest(promoRootObject.getCouponCodeRedeemReqList().get(i), requestJson, OCConstants.LOYALTY_OFFLINE_MODE);
				
				couponRedeemObj = new CouponCodeRedeemedObj();
				couponRedeemObj.setCOUPONCODEREDEEMREQ(promoRootObject.getCouponCodeRedeemReqList().get(i));
				CouponCodeRedeemedService codeRedeemedService = (CouponCodeRedeemedService) ServiceLocator.getInstance().getServiceByName(OCConstants.COUPON_CODE_REDEEMED_BUSINESS_SERVICE);
				CouponCodeRedeemedResponse responceJson = (CouponCodeRedeemedResponse) codeRedeemedService.processRedeemedRequest(couponRedeemObj);
				logger.info("responce Json is ::"+gson.toJson(responceJson));
				//RequestId
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getHEADERINFO().getREQUESTID()); sb.append("\""); sb.append(",");
				//PromoCode
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getCOUPONCODEINFO().getCOUPONCODE()); sb.append("\""); sb.append(",");
				//status
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getSTATUS()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getERRORCODE()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getMESSAGE()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(responceJson.getCOUPONCODEREDEEMRESPONSE().getUSERDETAILS().getORGID()); sb.append("\"");sb.append("\r\n"); 
				
				if(responceJson.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getERRORCODE().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
				
			} //for 
			
			if(failureCount > 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(promoReportsCSVFilePath));
				bw.write("\"Request ID\",\"PromoCode\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			}
			
			sb = null;
			//System.gc();
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+promoReportsCSVFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Runtime.getRuntime().exec(chOwnCmd);
				
				String chmodCmd = "chmod 777 "+promoReportsCSVFilePath;
				Runtime.getRuntime().exec(chmodCmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			
			renameAndMove(xmlfile.toString());
			writeSuccessMessage(xmlfile.getName(), redemtionReqSize, successCount, failureCount);
			
			//prepare emailqueue Object fro Support team
			/*if(failureCount > 0) {
				
				//(String subject,String message, String type, String status,String toEmailId,Calendar sentDate,Users user)
				String supportEmail = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
				if(supportEmail == null || supportEmail.trim().isEmpty()) {
					logger.info("Support email id not found "+supportEmail);
					return;
				}
				UsersDao usersdao = null ;
				EmailQueueDao emailQueueDao = null;
				try {
					usersdao =(UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
					emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(usersdao == null) {
					logger.info("usersdao not found retruning ...");
					return;
				}
				Users user = usersdao.find(1l);//inbox promos redemtion email queue userid hard coded
				if(user == null) {
					logger.info("users  not found retruning ...");
					return;
				}
				StringBuffer messageStrBuff = new StringBuffer("<html><head></head><body>"
						+ "<br />Promo redemtion failed count is :"+failureCount+""
								+ "<br /> Please refer  "+inboxPromoReportCSVFile.getName()+" in FTP promo outbox("+outboxPromoPath+") folder. </body></html>");
				EmailQueue emailQueue = new EmailQueue("OptSyn Promo Redemption failed",messageStrBuff.toString(),Constants.OPT_SYN_PROMO_REDEMTION,"Active",supportEmail,MyCalendar.getNewCalendar(),user);
				
				emailQueueDao.saveOrUpdate(emailQueue);
				
			} */// Preparing email queue Object
			
		
		}catch(Exception e){
			logger.error("Exception while processing xml file..", e);
			XMLUtil.renameAndMove(xmlfile.toString());
			writeFailedMessage("Permanent","Server error", xmlfile.getName(), ".xml");
		}
		logger.info("Completed processing XML file :"+xmlfile.getName());
		XMLUtil.renameAndMove(xmlfile.toString());
	}
	public PromoTrxLog logTransactionRequest(CouponCodeRedeemReq requestObject, String jsonRequest, String mode){
		PromoTrxLogDao PromoTrxLogDao = null;
		PromoTrxLogDaoForDML PromoTrxLogDaoForDML = null;

		PromoTrxLog transaction = null;
		try {
			PromoTrxLogDao = (PromoTrxLogDao)ServiceLocator.getInstance().getDAOByName("PromoTrxLogDao");
			PromoTrxLogDaoForDML = (PromoTrxLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("PromoTrxLogDaoForDML");
			transaction = new PromoTrxLog();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGID());
			transaction.setCustomerId(requestObject.getCOUPONCODEINFO().getCUSTOMERID() != null && !requestObject.getCOUPONCODEINFO().getCUSTOMERID().isEmpty()? requestObject.getCOUPONCODEINFO().getCUSTOMERID().trim() : null);
			transaction.setDocSID(requestObject.getCOUPONCODEINFO().getDOCSID() != null && !requestObject.getCOUPONCODEINFO().getDOCSID().isEmpty()? requestObject.getCOUPONCODEINFO().getDOCSID().trim() : null);
			transaction.setStoreNumber(requestObject.getCOUPONCODEINFO().getSTORENUMBER() != null && !requestObject.getCOUPONCODEINFO().getSTORENUMBER().isEmpty()? requestObject.getCOUPONCODEINFO().getSTORENUMBER().trim() : null);
			//PromoTrxLogDao.saveOrUpdate(transaction);
			PromoTrxLogDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	private boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("donepromo");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove enroll xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	private void writeSuccessMessage(String xmlfileName, int totalCount, int successCount, int failureCount){
		logger.info("Started writing success message of file :"+xmlfileName);
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxpromo");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(".xml"))+".txt";
		try {
			File file = new File(outboxLoyaltyPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Processed");
			str.append("\r\n");
			str.append("Total Requests Count : ");
			str.append(totalCount);
			str.append("\r\n");
			str.append("Success Count : ");
			str.append(successCount);
			str.append("\r\n");
			str.append("Failure Count : ");
			str.append(failureCount);
			bw.write(str.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		try {
			String chOwnCmd = "chown "+owner+":"+group+" "+outboxLoyaltyPath+"/"+fileName;
			logger.debug("chaging the file permissions =="+ chOwnCmd);
			Runtime.getRuntime().exec(chOwnCmd);
			
			String chmodCmd = "chmod 777 "+outboxLoyaltyPath+"/"+fileName;
			Runtime.getRuntime().exec(chmodCmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing success message of file :"+xmlfileName);
	}
	
	
	private void writeStatusFile(String filename) {
		writeFailedMessage("Temporary","is Unzipped File", filename, ".zip");
		
	}

}
