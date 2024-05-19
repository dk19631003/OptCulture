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
import org.mq.optculture.business.loyalty.LoyaltyReturnTransactionOCService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.model.loyalty.XMLReturn;
import org.mq.optculture.model.ocloyalty.LoyaltyOfflineReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class LoyaltyReturnByXML extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	
	@Override
	public void run() {
		
		
		logger.info("Started LoyaltyreturnByXML timer...");
		String inboxreturnPath = PropertyUtil.getPropertyValue("ocinboxloyaltyreturn");
		String donePath = PropertyUtil.getPropertyValue("ocdoneloyaltyreturn");
		
		try {
			if(inboxFileExist(inboxreturnPath)){
				logger.info("inbox return files exist...");
				File file = new File(inboxreturnPath);
				File[] xmlFiles  = file.listFiles();
				
				logger.info("List out the inbox return files...");
				for(File file2 : xmlFiles){
					logger.info(file2.getName().toString());
				}
				
				for(File xmlFile1 : xmlFiles){
					
					//TODO changes
					File xmlFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						
						xmlFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxreturnPath);
						
						if(xmlFile == null) {
							logger.info("Unzip falied for this file name  "+xmlFile1.getAbsolutePath());
							
							//TODO write a txt file and put them in to outbox as txt
							writeStatusFile("Temporary", xmlFile1.getName());
							
							//Zip Move to done folder
							if(!donePath.endsWith(File.separator) ){
								donePath = donePath+File.separator;
							}
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
					
					
					/*boolean validXmlFile = validateXmlWithXsd(xmlFile.toString());
					if(!validXmlFile){
						logger.info("Invalid xml file..."+xmlFile);
						renameAndMove(xmlFile.toString());
						writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml");
						continue;
					}
					*/
					boolean isXmlFile = checkFileWithXmlExtension(xmlFile.getName());
					if(isXmlFile){
						processXmlFile(xmlFile);
						renameAndMove(xmlFile.toString());
					}
					else{
						writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml");
					}
				}
			}
			else{
				logger.info("loyalty inbox oc return files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in xml oc return file test", e);
			return;
		} finally {
			logger.info("Completed OCLoyaltyreturnByXML timer...");
		}
		return;
	}
	
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("ocoutboxloyaltyreturn");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extension))+".txt";
		try {
			File file = new File(outboxLoyaltyPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason Type: "+reasonType);
			str.append("\r\n");
			str.append("Error Message: ");
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
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("ocoutboxloyaltyreturn");
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
	
	public boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("ocdoneloyaltyreturn");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove oc return xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String returnxsd = PropertyUtil.getPropertyValue("ocloyaltyreturnschema");
			logger.debug("schema :: "+returnxsd);
			return XMLUtil.validateXMLwithSchema(xmlfile, returnxsd);
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
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
	
	public boolean inboxFileExist(String inboxPath) throws Exception {
		logger.info("Started checking files in inbox folder : "+inboxPath);
		boolean status = false;
		File file = new File(inboxPath);
		if(file.exists() && file.isDirectory() && file.list().length > 0){
			status = true;
		}
		logger.info("Completed checking files in inbox folder : "+inboxPath);
		return status;
	}
	
	public LoyaltyReturnTransactionRequest convertOfflineToOnline(LoyaltyOfflineReturnTransactionRequest loyaltyOfflineReturnRequest){
		
		
		LoyaltyReturnTransactionRequest loyaltyReturnTrx = new LoyaltyReturnTransactionRequest();
		loyaltyReturnTrx.setHeader(loyaltyOfflineReturnRequest.getHeader());
		loyaltyReturnTrx.setOriginalReceipt(loyaltyOfflineReturnRequest.getOriginalReceipt());
		loyaltyReturnTrx.setMembership(loyaltyOfflineReturnRequest.getMembership());
		loyaltyReturnTrx.setCustomer(loyaltyOfflineReturnRequest.getCustomer());
		loyaltyReturnTrx.setAmount(loyaltyOfflineReturnRequest.getAmount());
		loyaltyReturnTrx.setCreditRedeemedAmount(loyaltyOfflineReturnRequest.getCreditRedeemedAmount());
		loyaltyReturnTrx.setItems(loyaltyOfflineReturnRequest.getItems().getOCLoyaltyItem());
		loyaltyReturnTrx.setUser(loyaltyOfflineReturnRequest.getUser());
		return loyaltyReturnTrx;
	}
	
	
	
	public void processXmlFile(File xmlfile){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		
		try{
			Object object =  XMLUtil.unMarshal(xmlfile.toString(), XMLReturn.class);
			XMLReturn returnList = (XMLReturn)object;
			List<LoyaltyOfflineReturnTransactionRequest> returnRequestList = returnList.getOCLoayaltyReturnReq();
			
			logger.info("Total requests in the file : "+returnRequestList.size());
			String outboxLoyaltyreturnPath = PropertyUtil.getPropertyValue("ocoutboxloyaltyreturn"); //TODO
			
			outboxLoyaltyreturnPath += outboxLoyaltyreturnPath.endsWith(File.separator) ? "" : File.separator;
			String loyreturnCSVFilePath = outboxLoyaltyreturnPath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			//File inboxLoyreturnCSVFile = new File(loyreturnCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(loyreturnCSVFilePath));
			//bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			
			for(LoyaltyOfflineReturnTransactionRequest returnRequest : returnRequestList) {
				String requestJson = new Gson().toJson(returnRequest, LoyaltyOfflineReturnTransactionRequest.class);
				
				String pcFlag = returnRequest.getHeader().getPcFlag();
				String requestId = returnRequest.getHeader().getRequestId();
				LoyaltyTransaction transaction = null;
				//transaction = findTransactionByRequestId(requestId);
				if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
					transaction = findTransactionByRequestId(requestId);
					if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
						successCount += 1;
						continue;
					}
				}	
				/*else if (transaction != null){//request with requestid was already processed. 
					failureCount += 1;
					continue;
				}*/
				//log transactionF
				if(transaction == null){
					transaction = logTransactionRequest(returnRequest, requestJson, OCConstants.LOYALTY_OFFLINE_MODE);
				}
				//BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_return_BUSINESS_SERVICE);
				LoyaltyReturnTransactionOCService returnService = (LoyaltyReturnTransactionOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_RETURN_TRANSACTION_OC_BUSINESS_SERVICE);
				
				LoyaltyTransactionParent tranParent = createNewTransaction();
				Date date = tranParent.getCreatedDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				
				
				LoyaltyReturnTransactionRequest loyaltyReturnRequest = convertOfflineToOnline(returnRequest);
				
				
				//BaseResponseObject baseResponseObject = baseService.processRequest(requestObject);
				LoyaltyReturnTransactionResponse responseObject = returnService.processReturnTransactionRequest(loyaltyReturnRequest, transaction.getId()+"", transDate, requestJson,  OCConstants.LOYALTY_OFFLINE_MODE,null)	;
				String responseJson = new Gson().toJson(responseObject, LoyaltyReturnTransactionResponse.class);	
				logger.info("Response = "+responseJson);
				updateTransactionStatus(transaction, responseJson, responseObject);
				String userName = returnRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
						returnRequest.getUser().getOrganizationId();
				updateTransaction(tranParent, responseObject, userName);
				//RequestId
				sb.append("\"");sb.append(returnRequest.getHeader().getRequestId()); sb.append("\""); sb.append(",");
//				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				//Card Number
				String cardNumber = "";
				if (responseObject.getMembership() != null && responseObject.getMembership().getCardNumber() != null && 
						!responseObject.getMembership().getCardNumber().trim().isEmpty() ){
					cardNumber = responseObject.getMembership().getCardNumber();
				} else {
					cardNumber = responseObject.getMembership().getPhoneNumber();
				}
				sb.append("\"");sb.append(cardNumber == null ? "" : cardNumber ); sb.append("\""); sb.append(",");
				
				//Customer Id
				String custmerId = null;
				//Email Id
				String emailId = null;
				//Mobile
				String phoneStr = null;
				if( responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().size()>0 && responseObject.getMatchedCustomers().get(0) != null ) {
					custmerId = responseObject.getMatchedCustomers().get(0).getCustomerId();
					emailId = responseObject.getMatchedCustomers().get(0).getEmailAddress();
					phoneStr = responseObject.getMatchedCustomers().get(0).getPhone();
				}
				sb.append("\"");sb.append(custmerId == null ? "" : custmerId ); sb.append("\""); sb.append(",");
				
				
				sb.append("\"");sb.append(emailId == null ? "" : emailId ); sb.append("\""); sb.append(",");
				
				
				sb.append("\"");sb.append(phoneStr == null ? "" : phoneStr ); sb.append("\""); sb.append(",");
				
				//status
				sb.append("\"");sb.append(responseObject.getStatus().getStatus()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(responseObject.getStatus().getErrorCode()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(responseObject.getStatus().getMessage()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(returnRequest.getUser().getUserName()+"_"+returnRequest.getUser().getOrganizationId()); sb.append("\"");sb.append("\r\n"); 
				logger.info("sb buffer = "+sb);
				
				if(responseObject.getStatus().getErrorCode().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
				//bw.write(sb.toString());
				//bw.flush();
				
			}//for
			if(failureCount > 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(loyreturnCSVFilePath));
				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			}
			
			sb = null;
			//System.gc();
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+loyreturnCSVFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Runtime.getRuntime().exec(chOwnCmd);
				
				String chmodCmd = "chmod 777 "+loyreturnCSVFilePath;
				Runtime.getRuntime().exec(chmodCmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			renameAndMove(xmlfile.toString());
			writeSuccessMessage(xmlfile.getName(), returnRequestList.size(), successCount, failureCount);
			
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
					logger.error("Exception ::", e);
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
						+ "<br />Loyalty returnment failed count is :"+failureCount+""
								+ "<br /> Please refer  "+inboxLoyreturnCSVFile.getName()+" in FTP loyalty outbox("+outboxLoyaltyreturnPath+") folder. </body></html>");
				EmailQueue emailQueue = new EmailQueue("OptSync Loyalty returnment failed",messageStrBuff.toString(),Constants.OPT_SYN_LOYALTY_return,"Active",supportEmail,MyCalendar.getNewCalendar(),user);
				
				emailQueueDao.saveOrUpdate(emailQueue);
				
			} */// Preparing email queue Object
			
			
			
		}catch(Exception e){
			logger.error("Exception while processing oc return xml file..", e);
			renameAndMove(xmlfile.toString());
			writeFailedMessage("Permanent", "Server error", xmlfile.getName(),".xml");
		}
		logger.info("Completed processing XML file :"+xmlfile.getName());
	}//End of method

	
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
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyOfflineReturnTransactionRequest requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_RETURN);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyReturnTransactionResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			if (response.getMembership() != null && response.getMembership().getCardNumber() != null &&
					!response.getMembership().getCardNumber().trim().isEmpty()) {
				transaction.setCardNumber(response.getMembership().getCardNumber());
			} else {
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
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
	
	private LoyaltyTransactionParent createNewTransaction(){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_RETURN);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
			
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyReturnTransactionResponse returnResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(returnResponse.getStatus() != null) {
				trans.setStatus(returnResponse.getStatus().getStatus());
				trans.setErrorMessage(returnResponse.getStatus().getMessage());
			}
			if(returnResponse.getHeader() != null){
				trans.setRequestId(returnResponse.getHeader().getRequestId());
				trans.setRequestDate(returnResponse.getHeader().getTransactionDate());
			}
			if(returnResponse.getMembership() != null) {
					trans.setMembershipNumber(returnResponse.getMembership().getCardNumber());
					trans.setMobilePhone(returnResponse.getMembership().getPhoneNumber());
			}
			if(returnResponse.getMatchedCustomers() != null) {
				//trans.setMobilePhone(returnResponse.getMatchedCustomers().getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
}
