package org.mq.optculture.timer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.loyalty.LoyaltyRedemptionService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionResponseObject;
import org.mq.optculture.model.loyalty.XMLRedemption;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class LoyaltyRedemptionByXML extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	
	public void run() { 
		logger.info("Started LoyaltyRedemptionByXML timer...");
		String inboxRedemptionPath = PropertyUtil.getPropertyValue("inboxloyaltyredemption");
		String donePath = PropertyUtil.getPropertyValue("doneloyaltyredemption");
		
		try {
			
			//check any pending enrolment requests exists 
			if(enrolFilesExist()){
				return;
			}
			
			if(inboxFileExist(inboxRedemptionPath)){
				logger.info("inbox redemption files exist...");
				File file = new File(inboxRedemptionPath);
				File[] xmlFiles  = file.listFiles(); 
				
				logger.info("List out the inbox redemption files...");
				for(File file2 : xmlFiles){
					logger.info(file2.getName().toString());
				}
				
				for(File xmlFile1 : xmlFiles){

					//TODO changes
					File xmlFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						logger.info("xmlFile1.getAbsolutePath() = "+xmlFile1.getAbsolutePath());
						xmlFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxRedemptionPath);
						
						if(xmlFile == null) {
							logger.info("Unzip falied for this file name  "+xmlFile1.getAbsolutePath());
							
							//TODO write a txt file and put them in to outbox as txt
							writeStatusFile("Temporary", xmlFile1.getName());
							if(!donePath.endsWith(File.separator) ){
								donePath = donePath+File.separator;
							}
							//Zip Move to done folder
							xmlFile1.renameTo(new File(donePath+xmlFile1.getName()));
							 
							continue;
							
						}
						if(!donePath.endsWith(File.separator) ){
							donePath = donePath+File.separator;
						}
						//Zip Move to done folder
						xmlFile1.renameTo(new File(donePath+xmlFile1.getName()));
					
					}else {
						xmlFile = xmlFile1;
					}
					//
					logger.info("before validating xml file = "+xmlFile.toString());
					boolean validXmlFile = validateXmlWithXsd(xmlFile.toString());
					if(!validXmlFile){
						logger.info("Invalid xml file..."+xmlFile);
						renameAndMove(xmlFile.toString());
						writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml");
						continue;
					}
					
					boolean isXmlFile = checkFileWithXmlExtension(xmlFile.getName());
					if(isXmlFile){
						processXmlFile(xmlFile);
						renameAndMove(xmlFile.toString());
					}
					else {
						writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml");
					}
				}
			}
			else{
				logger.info("loyalty inbox redemption files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in xml redemption file test", e);
			return;
		} finally {
			logger.info("Completed LoyaltyRedemptionByXML timer...");
		}
		return;
	}
	
	public boolean inboxFileExist(String inboxPath) throws Exception {
		File file = new File(inboxPath);
		if(file.exists() && file.isDirectory()){
			if(file.list().length > 0){
				return true;
			}
			else return false;
		}
		return false;
	}
	
	public void processXmlFile(File xmlfile){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		try{
			Object object =  XMLUtil.unMarshal(xmlfile.toString(), XMLRedemption.class);
			XMLRedemption redemptionList = (XMLRedemption)object;
			List<LoyaltyRedemptionRequestObject> redemptionRequestList = redemptionList.getRedemptionRequest();
			logger.info("Total requests in the file : "+redemptionRequestList.size());
			
			String outboxLoyaltyRedemptionPath = PropertyUtil.getPropertyValue("outboxloyaltyredemption"); //TODO
			
			outboxLoyaltyRedemptionPath += outboxLoyaltyRedemptionPath.endsWith(File.separator) ? "" : File.separator;
			String loyRedemptionCSVFilePath = outboxLoyaltyRedemptionPath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			File inboxLoyRedemptionCSVFile = new File(loyRedemptionCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(loyIssuanceCSVFilePath));
			//bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"EnteredAmount\",\"ValueCode\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			
			for(LoyaltyRedemptionRequestObject redemptionRequest : redemptionRequestList) {
				
				String requestJson = new Gson().toJson(redemptionRequest, LoyaltyRedemptionRequestObject.class);
				//LoyaltyTransaction transaction = logTransactionRequest(issuanceRequest, requestJson, "offline");
				
				String pcFlag = redemptionRequest.getHEADERINFO().getPCFLAG();
				String requestId = redemptionRequest.getHEADERINFO().getREQUESTID();
				String docSId = redemptionRequest.getREDEMPTIONINFO().getDOCSID();
				String userDetail = redemptionRequest.getUSERDETAILS().getUSERNAME()+"__"+redemptionRequest.getUSERDETAILS().getORGANISATION();

				LoyaltyTransaction transaction = null;
				
				
				String userName = redemptionRequest.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+redemptionRequest.getUSERDETAILS().getORGANISATION();
				//if DOCSID not there then split the request id(the second token is meant for DOCSID).This is given for v8 plugin
				boolean isInFormat = (requestId != null && !requestId.isEmpty() && requestId.split(OCConstants.TOKEN_UNDERSCORE).length==3);
				
				String CustSID =  redemptionRequest.getREDEMPTIONINFO().getCUSTOMERID();
				
				boolean isCustSID = (isInFormat && CustSID != null && !CustSID.trim().isEmpty() && CustSID.equalsIgnoreCase(requestId.split(OCConstants.TOKEN_UNDERSCORE)[1]));
				
				if(docSId == null ||( docSId != null && docSId.trim().isEmpty()) && isInFormat && !isCustSID) {
					String POSVersion = PropertyUtil.getPOSVersion(userName);
					if(POSVersion != null && !POSVersion.equalsIgnoreCase(OCConstants.POSVERSION_V8)){
						docSId = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
						
					}
					
				}
				//transaction = findTransactionByRequestId(requestId);
				
				// pc flag removed using request id for unique identifier
				if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
					transaction = findTransactionBy(userDetail, requestId, null);
					if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
						successCount += 1;
						continue;
					}
				}
				
				// pcflag removed
				//transaction = findTransactionByRequestId(requestId);
				if(docSId != null && !docSId.trim().isEmpty())transaction = findTransactionBy(userDetail,null,docSId);
				if(transaction != null ) {
					
						successCount += 1;
						continue;
				}

				//log transaction
				if(transaction == null){
					try{
						transaction = logTransactionRequest(redemptionRequest, requestJson, "offline", docSId);
					}catch(Exception ex){
					}
				}	
				Date date = transaction.getRequestDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
								
				LoyaltyRedemptionService redemptionService = (LoyaltyRedemptionService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_REDEMPTION_BUSINESS_SERVICE);
				LoyaltyRedemptionResponseObject responseObject = redemptionService.processRedemptionRequest(redemptionRequest, OCConstants.LOYALTY_OFFLINE_MODE, transaction.getId()+"", transDate,null);
						//TODO : IssuanceRequest(redemptionRequest, OCConstants.LOYALTY_OFFLINE_MODE);
				String responseJson = new Gson().toJson(responseObject, LoyaltyRedemptionResponseObject.class);	
				logger.info("Response = "+responseJson);
				updateTransactionStatus(transaction, responseJson, responseObject);
				
				//RequestId
				sb.append("\"");sb.append(redemptionRequest.getHEADERINFO().getREQUESTID()); sb.append("\""); sb.append(",");
//				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				//Card Number
				String cardNumber = redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER();
				sb.append("\"");sb.append(cardNumber == null ? "" : cardNumber ); sb.append("\""); sb.append(",");
				
				//Customer Id
				String custmerId = redemptionRequest.getREDEMPTIONINFO().getCUSTOMERID();
				sb.append("\"");sb.append(custmerId == null ? "" : custmerId ); sb.append("\""); sb.append(",");
				
				//Entered Amount
				String enteredAmount = redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT();
				sb.append("\"");sb.append(enteredAmount == null ? "" : enteredAmount ); sb.append("\""); sb.append(",");
				
				//Value Code
				String valueCode = redemptionRequest.getREDEMPTIONINFO().getVALUECODE();
				sb.append("\"");sb.append(valueCode == null ? "" : valueCode ); sb.append("\""); sb.append(",");
				
				//status
				sb.append("\"");sb.append(responseObject.getSTATUS().getSTATUS()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(responseObject.getSTATUS().getERRORCODE()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(responseObject.getSTATUS().getMESSAGE()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(redemptionRequest.getUSERDETAILS().getUSERNAME()+"_"+redemptionRequest.getUSERDETAILS().getORGANISATION()); sb.append("\"");sb.append("\r\n"); 
				logger.info("sb buffer = "+sb);
				
				
				if(responseObject.getSTATUS().getERRORCODE().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
			}//for
			if(failureCount > 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(loyRedemptionCSVFilePath));
				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"EnteredAmount\",\"ValueCode\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			}
			
			sb = null;
			//System.gc();
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+loyRedemptionCSVFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Runtime.getRuntime().exec(chOwnCmd);
				
				String chmodCmd = "chmod 777 "+loyRedemptionCSVFilePath;
				Runtime.getRuntime().exec(chmodCmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			
			renameAndMove(xmlfile.toString());
			writeSuccessMessage(xmlfile.getName(), redemptionRequestList.size(), successCount, failureCount);
			
		}catch(Exception e){
			logger.error("Exception while processing xml file..", e);
			renameAndMove(xmlfile.toString());
			writeFailedMessage("Permanent", "Server error", xmlfile.getName(),".xml");
		}
		logger.info("Completed processing XML file :"+xmlfile.getName());
	}
	
	public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("loyaltyredemptionschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, enrollxsd);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}
	
	public boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("doneloyaltyredemption");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove redemption xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxloyaltyredemption");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extension))+".txt";
		try {
			File file = new File(outboxLoyaltyPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason Type : "+reasonType);
			str.append("\r\n");
			str.append("Error Message : ");
			str.append(errorMsg);
			bw.write(str.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		//change the user/group and  the permissions to the file
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
		logger.info("Completed writing failed message of file :"+xmlfileName);
	}
	
	public void writeSuccessMessage(String xmlfileName, int totalCount, int successCount, int failureCount){
		logger.info("Started writing success message of file :"+xmlfileName);
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxloyaltyredemption");
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
		//change the user/group and  the permissions to the file
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
	
	public boolean checkFileWithXmlExtension(String xmlfile) {
		logger.info("Started checking xml extension of file : "+xmlfile);
		boolean status = false;
		if(xmlfile != null && xmlfile.trim().endsWith(".xml")){
			status = true;
		}
		logger.info("Completed checking xml extension of file : "+xmlfile);
		return status;
	}
	
	/*public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestIdAndType(requestId, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}*/
	public LoyaltyTransaction findTransactionBy(String userDetail, String requestId, String docSId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByDocSIdRequestIdAndType(userDetail, requestId, docSId, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyRedemptionRequestObject requestObject, String jsonRequest, String mode, String docSid) throws Exception{
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
		
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			transaction.setDocSID(docSid != null && 
					!docSid.trim().isEmpty() ? docSid.trim() : null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
			throw new Exception("Exception occured while logging the transaction...", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionResponseObject response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			transaction.setCardNumber(response.getREDEMPTIONINFO().getCARDNUMBER());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	
	private void writeStatusFile(String reasonType, String filename) {
		writeFailedMessage(reasonType, "Invalid zip file", filename, ".zip");
		
	}
	
	private boolean enrolFilesExist() {
		
		String inboxEnrolPath = PropertyUtil.getPropertyValue("inboxloyaltyenroll");
		File file = new File(inboxEnrolPath);
		if(file != null && file.isDirectory()){
			File[] xmlFiles  = file.listFiles();
			if(xmlFiles != null && xmlFiles.length > 0){
				return true;
			}
		}
		
		return false;
	}
}
