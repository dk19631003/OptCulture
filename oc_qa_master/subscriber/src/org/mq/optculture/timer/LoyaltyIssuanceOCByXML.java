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
import java.util.TimeZone;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.loyalty.LoyaltyIssuanceOCService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.XMLOCIssuance;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class LoyaltyIssuanceOCByXML extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public void run() {
		logger.info("Started OCLoyaltyIssuanceByXML timer...");
		String inboxIssuancePath = PropertyUtil.getPropertyValue("ocinboxloyaltyissuance");
		String donePath = PropertyUtil.getPropertyValue("ocdoneloyaltyissuance");
		
		try {
			
			//check any pending enrolment requests exists 
			if(enrolFilesExist()){
				return;
			}
			
			if(inboxFileExist(inboxIssuancePath)){
				logger.info("inbox oc issuance files exist...");
				File file = new File(inboxIssuancePath);
				File[] xmlFiles  = file.listFiles();
				
				logger.info("List out the oc inbox issuance files...");
				for(File file2 : xmlFiles){
					logger.info(file2.getName().toString());
				}
				
				for(File xmlFile1 : xmlFiles){

					//TODO changes
					File xmlFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						logger.info("xmlFile1.getAbsolutePath() = "+xmlFile1.getAbsolutePath());
						xmlFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxIssuancePath);
						
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
				logger.info("loyalty inbox oc issuance files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in xml oc issuance file test", e);
			return;
		} finally {
			logger.info("Completed OCLoyaltyIssuanceByXML timer...");
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
			Object object =  XMLUtil.unMarshal(xmlfile.toString(), XMLOCIssuance.class);
			XMLOCIssuance issuanceList = (XMLOCIssuance)object;
			List<LoyaltyIssuanceRequest> issuanceRequestList = issuanceList.getIssuanceRequest();
			logger.info("Total requests in the file : "+issuanceRequestList.size());
			
			String outboxLoyaltyIssuancePath = PropertyUtil.getPropertyValue("ocoutboxloyaltyissuance"); //TODO
			
			outboxLoyaltyIssuancePath += outboxLoyaltyIssuancePath.endsWith(File.separator) ? "" : File.separator;
			String loyIssuanceCSVFilePath = outboxLoyaltyIssuancePath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			File inboxLoyIssuanceCSVFile = new File(loyIssuanceCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(loyIssuanceCSVFilePath));
			//bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"EnteredAmount\",\"ValueCode\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			
			for(LoyaltyIssuanceRequest issuanceRequest : issuanceRequestList) {
				
				String requestJson = new Gson().toJson(issuanceRequest, LoyaltyIssuanceRequest.class);
				//LoyaltyTransaction transaction = logTransactionRequest(issuanceRequest, requestJson, "offline");
				
				String pcFlag = issuanceRequest.getHeader().getPcFlag();
				String requestId = issuanceRequest.getHeader().getRequestId();
				LoyaltyTransaction transaction = null;
				//transaction = findTransactionByRequestId(requestId);
				if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
					transaction = findTransactionByRequestId(requestId);
					if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
						successCount += 1;
						continue;
					}
				}
				
				//log transaction
				if(transaction == null){
					transaction = logTransactionRequest(issuanceRequest, requestJson, "offline");
				}	
								
				LoyaltyIssuanceOCService issuanceService = (LoyaltyIssuanceOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
				
				LoyaltyTransactionParent tranParent = createNewTransaction();
				Date date = tranParent.getCreatedDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				
				LoyaltyIssuanceResponse responseObject = issuanceService.processIssuanceRequest(issuanceRequest, 
						OCConstants.LOYALTY_OFFLINE_MODE, ""+tranParent.getTransactionId(), transDate,null);
				String responseJson = new Gson().toJson(responseObject, LoyaltyIssuanceResponse.class);	
				logger.info("Response = "+responseJson);
				updateTransactionStatus(transaction, responseJson, responseObject);
				String userName = issuanceRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
						issuanceRequest.getUser().getOrganizationId();
				updateTransaction(tranParent, responseObject, userName);
				
				//RequestId
				sb.append("\"");sb.append(issuanceRequest.getHeader().getRequestId()); sb.append("\""); sb.append(",");
//				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				//Card Number
				String cardNumber = null;//issuanceRequest.getISSUANCEINFO().getCARDNUMBER();
				if (responseObject.getMembership() != null && responseObject.getMembership().getCardNumber() != null && 
						!responseObject.getMembership().getCardNumber().trim().isEmpty() ){
					cardNumber = responseObject.getMembership().getCardNumber();
				} else {
					cardNumber = responseObject.getMembership().getPhoneNumber();
				}
				sb.append("\"");sb.append(cardNumber == null ? "" : cardNumber ); sb.append("\""); sb.append(",");
				
				//Customer Id
				String custmerId = null; //issuanceRequest.getISSUANCEINFO().getCUSTOMERID();
				if( responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().get(0) != null ) {
					custmerId = responseObject.getMatchedCustomers().get(0).getCustomerId();
				}
				sb.append("\"");sb.append(custmerId == null ? "" : custmerId ); sb.append("\""); sb.append(",");
				
				//Entered Amount
				String enteredAmount = issuanceRequest.getAmount().getEnteredValue();
				sb.append("\"");sb.append(enteredAmount == null ? "" : enteredAmount ); sb.append("\""); sb.append(",");
				
				//Value Code
				String valueCode = issuanceRequest.getAmount().getValueCode();
				sb.append("\"");sb.append(valueCode == null ? "" : valueCode ); sb.append("\""); sb.append(",");
				
				//status
				sb.append("\"");sb.append(responseObject.getStatus().getStatus()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(responseObject.getStatus().getErrorCode()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(responseObject.getStatus().getMessage()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(issuanceRequest.getUser().getUserName()+"_"+issuanceRequest.getUser().getOrganizationId()); sb.append("\"");sb.append("\r\n"); 
				logger.info("sb buffer = "+sb);
				
				if(responseObject.getStatus().getErrorCode().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
			}//for
			if(failureCount > 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(loyIssuanceCSVFilePath));
				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"EnteredAmount\",\"ValueCode\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			}
			
			sb = null;
			//System.gc();
			
			
			renameAndMove(xmlfile.toString());
			writeSuccessMessage(xmlfile.getName(), issuanceRequestList.size(), successCount, failureCount);
			
			//prepare emailqueue Object for Support team
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
						+ "<br />Loyalty Issuance failed count is :"+failureCount+""
								+ "<br /> Please refer  "+inboxLoyIssuanceCSVFile.getName()+" in FTP loyalty outbox("+outboxLoyaltyIssuancePath+") folder. </body></html>");
				EmailQueue emailQueue = new EmailQueue("OptSync Loyalty Issuance failed",messageStrBuff.toString(),Constants.OPT_SYN_LOYALTY_ISSUANCE,"Active",supportEmail,MyCalendar.getNewCalendar(),user);
				
				emailQueueDao.saveOrUpdate(emailQueue);
				
			} */// Preparing email queue Object
			
		}catch(Exception e){
			logger.error("Exception while processing oc issuance xml file..", e);
			renameAndMove(xmlfile.toString());
			writeFailedMessage("Permanent", "Server error", xmlfile.getName(),".xml");
		}
		logger.info("Completed processing oc issuance XML file :"+xmlfile.getName());
	}
	
	public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("ocloyaltyissuanceschema");
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
			String donePath = PropertyUtil.getPropertyValue("ocdoneloyaltyissuance");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove oc enroll xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("ocoutboxloyaltyissuance");
		FileWriter writer;
		try {
			String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extension))+".txt";
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
		logger.info("Completed writing failed message of file :"+xmlfileName);
	}
	
	public void writeSuccessMessage(String xmlfileName, int totalCount, int successCount, int failureCount){
		logger.info("Started writing success message of file :"+xmlfileName);
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("ocoutboxloyaltyissuance");
		FileWriter writer;
		try {
			String fileName = xmlfileName.substring(0, xmlfileName.indexOf(".xml"))+".txt";
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
	
	public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestId(requestId, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyIssuanceRequest requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHeader().getPcFlag()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			if (response != null && response.getMembership() != null && response.getMembership().getCardNumber() != null && 
					!response.getMembership().getCardNumber().trim().isEmpty()) {
				transaction.setCardNumber(response.getMembership().getCardNumber());
			} else {
				transaction.setCardNumber(response.getMembership().getPhoneNumber());
			}
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
		
		String inboxEnrolPath = PropertyUtil.getPropertyValue("ocinboxloyaltyenroll");
		File file = new File(inboxEnrolPath);
		if(file != null && file.isDirectory()){
			File[] xmlFiles  = file.listFiles();
			if(xmlFiles != null && xmlFiles.length > 0){
				return true;
			}
		}
		
		return false;
	}
	
	private LoyaltyTransactionParent createNewTransaction(){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyIssuanceResponse issuanceResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(issuanceResponse.getStatus() != null) {
				trans.setStatus(issuanceResponse.getStatus().getStatus());
				trans.setErrorMessage(issuanceResponse.getStatus().getMessage());
			}
			if(issuanceResponse.getHeader() != null){
				trans.setRequestId(issuanceResponse.getHeader().getRequestId());
				trans.setRequestDate(issuanceResponse.getHeader().getTransactionDate());
			}
			if(issuanceResponse.getMembership() != null) {
					trans.setMembershipNumber(issuanceResponse.getMembership().getCardNumber());
					trans.setMobilePhone(issuanceResponse.getMembership().getPhoneNumber());
			}
			if(issuanceResponse.getMatchedCustomers() != null) {
				//trans.setMobilePhone(enrollResponse.getMatchedCustomers().getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
}
