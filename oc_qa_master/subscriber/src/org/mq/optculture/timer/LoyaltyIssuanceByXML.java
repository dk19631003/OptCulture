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
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyIssuanceService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.XMLIssuance;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class LoyaltyIssuanceByXML extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);  
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	private final String SftpUserPath = PropertyUtil.getPropertyValueFromDB("SftpUserPath");
	
	public void run() {
		logger.info("Started LoyaltyIssuanceByXML timer...");
		String inboxIssuancePath = PropertyUtil.getPropertyValue("inboxloyaltyissuance");
		String donePath = PropertyUtil.getPropertyValue("doneloyaltyissuance");
		String outboxLoyaltyIssuancePath = PropertyUtil.getPropertyValue("outboxloyaltyissuance");  
		try {
			boolean filesexists =false;
			
			//check any pending enrolment requests exists 
			if(enrolFilesExist()){
				return;
			}
			
			if(inboxFileExist(inboxIssuancePath)){
				logger.info("inbox issuance files exist...");
				File file = new File(inboxIssuancePath);
				File[] xmlFiles  = file.listFiles();
				
				logger.info("List out the inbox issuance files...");
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
						processXmlFile(xmlFile, outboxLoyaltyIssuancePath, inboxIssuancePath, outboxLoyaltyIssuancePath, false, donePath);//(xmlFile);
						renameAndMove(xmlFile.toString());
					}
					else {
						writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml");
					}
				}
			}
			
			List<Users> specificDirExists = SpecificDirExists();
			if(specificDirExists != null && specificDirExists.size()>0) {
				
				filesexists = true;
				//String genericUsername = "ocftpuser";
				for (Users users : specificDirExists) {
					
					
					String username = Utility.getOnlyOrgId(users.getUserName());
					inboxIssuancePath = SftpUserPath+"/"+username+"/opt_sync/inbox/loyalty/issuance";
					donePath = SftpUserPath+"/"+username+"/opt_sync/done/loyalty/issuance";
					outboxLoyaltyIssuancePath = SftpUserPath+"/"+username+"/opt_sync/outbox/loyalty/issuance";
					if(inboxFileExist(inboxIssuancePath)){

						logger.info("inbox issuance files exist...");
						File file = new File(inboxIssuancePath);
						File[] xmlFiles  = file.listFiles();
						
						logger.info("List out the inbox issuance files...");
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
									writeStatusFile("Temporary", xmlFile1.getName(),outboxLoyaltyIssuancePath, username);
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
								renameAndMoveForSpecific(xmlFile.toString(), donePath);//(xmlFile.toString());
								writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml", outboxLoyaltyIssuancePath, username);
								continue;
							}
							
							boolean isXmlFile = checkFileWithXmlExtension(xmlFile.getName());
							if(isXmlFile){
								processXmlFile(xmlFile, outboxLoyaltyIssuancePath, username, username, true, donePath);								renameAndMove(xmlFile.toString());
							}
							else {
								writeFailedMessage("Permanent", "Invalid xml file", xmlFile.getName(),".xml", outboxLoyaltyIssuancePath, username);
							}
						}
					
						
					}
				}
				
			}
			if(filesexists){
				logger.info("loyalty inbox enroll files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in xml issuance file test", e);
			return;
		} finally {
			logger.info("Completed LoyaltyIssuanceByXML timer...");
		}
		return;
	}
	public List<Users> SpecificDirExists() throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		List<Users> specificDirUsers = usersDao.findAnySpeciDirBasedUsersExists();
		return specificDirUsers;
		
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
	
	public void processXmlFile(File xmlfile, String outboxLoyaltyIssuancePath, String owner, String group, boolean isSpecific, String donePath){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		try{
			Object object =  XMLUtil.unMarshal(xmlfile.toString(), XMLIssuance.class);
			XMLIssuance issuanceList = (XMLIssuance)object;
			List<LoyaltyIssuanceRequestObject> issuanceRequestList = issuanceList.getIssuanceRequest();
			logger.info("Total requests in the file : "+issuanceRequestList.size());
			
			
			
			outboxLoyaltyIssuancePath += outboxLoyaltyIssuancePath.endsWith(File.separator) ? "" : File.separator;
			String loyIssuanceCSVFilePath = outboxLoyaltyIssuancePath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			File inboxLoyIssuanceCSVFile = new File(loyIssuanceCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(loyIssuanceCSVFilePath));
			//bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"EnteredAmount\",\"ValueCode\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			
			for(LoyaltyIssuanceRequestObject issuanceRequest : issuanceRequestList) {
				
				String requestJson = new Gson().toJson(issuanceRequest, LoyaltyIssuanceRequestObject.class);
				//LoyaltyTransaction transaction = logTransactionRequest(issuanceRequest, requestJson, "offline");
				
				String pcFlag = issuanceRequest.getHEADERINFO().getPCFLAG();
				String requestId = issuanceRequest.getHEADERINFO().getREQUESTID();
				String docSId = issuanceRequest.getISSUANCEINFO().getDOCSID();
				String userDetail = issuanceRequest.getUSERDETAILS().getUSERNAME()+"__"+issuanceRequest.getUSERDETAILS().getORGANISATION();

				LoyaltyTransaction transaction = null;
				//transaction = findTransactionByRequestId(requestId);
				
				// pc flag removed using request id for unique identifier
				if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
					transaction = findTransactionBy(userDetail,requestId, null);
					if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
						successCount += 1;
						continue;
					}
				}
				
				// pcflag removed
				//transaction = findTransactionByRequestId(requestId);
				
				
				
				String userName = issuanceRequest.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+issuanceRequest.getUSERDETAILS().getORGANISATION();
				//			String pcFlag = jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getPCFLAG();
				
				
				//reverting the changes
				//if DOCSID not there then split the request id(the second token is meant for DOCSID).This is given for v8 plugin
				boolean isInFormat = (requestId != null && !requestId.isEmpty() && requestId.split(OCConstants.TOKEN_UNDERSCORE).length==3);
				
				String CustSID =  issuanceRequest.getISSUANCEINFO().getCUSTOMERID();
				
				boolean isCustSID = (isInFormat && CustSID != null && !CustSID.trim().isEmpty() && CustSID.equalsIgnoreCase(requestId.split(OCConstants.TOKEN_UNDERSCORE)[1]));
				
				if(docSId == null ||( docSId != null && docSId.trim().isEmpty()) && isInFormat && !isCustSID) {
					String POSVersion = PropertyUtil.getPOSVersion(userName);
					if(POSVersion != null && !POSVersion.equalsIgnoreCase(OCConstants.POSVERSION_V8)){
						docSId = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
					}
					
				}
				
				if(docSId != null && !docSId.trim().isEmpty())transaction = findTransactionBy(userDetail, null, docSId);
				if(transaction != null ) {
					
						successCount += 1;
						continue;
				}
				
				//log transaction
				if(transaction == null){
					try{
						transaction = logTransactionRequest(issuanceRequest, requestJson, "offline", docSId);
					}catch(Exception ex){
						logger.error("Exception in logging a Trx ", ex);
					}
				}	
				Date date = transaction.getRequestDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				
								
				LoyaltyIssuanceService issuanceService = (LoyaltyIssuanceService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ISSUANCE_BUSINESS_SERVICE);
				LoyaltyIssuanceResponseObject responseObject = issuanceService.processIssuanceRequest(issuanceRequest, OCConstants.LOYALTY_OFFLINE_MODE, transaction.getId()+"",transDate);
				String responseJson = new Gson().toJson(responseObject, LoyaltyIssuanceResponseObject.class);	
				logger.info("Response = "+responseJson);
				updateTransactionStatus(transaction, responseJson, responseObject);
				
				//RequestId
				sb.append("\"");sb.append(issuanceRequest.getHEADERINFO().getREQUESTID()); sb.append("\""); sb.append(",");
//				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				//Card Number
				String cardNumber = issuanceRequest.getISSUANCEINFO().getCARDNUMBER();
				sb.append("\"");sb.append(cardNumber == null ? "" : cardNumber ); sb.append("\""); sb.append(",");
				
				//Customer Id
				String custmerId = issuanceRequest.getISSUANCEINFO().getCUSTOMERID();
				sb.append("\"");sb.append(custmerId == null ? "" : custmerId ); sb.append("\""); sb.append(",");
				
				//Entered Amount
				String enteredAmount = issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT();
				sb.append("\"");sb.append(enteredAmount == null ? "" : enteredAmount ); sb.append("\""); sb.append(",");
				
				//Value Code
				String valueCode = issuanceRequest.getISSUANCEINFO().getVALUECODE();
				sb.append("\"");sb.append(valueCode == null ? "" : valueCode ); sb.append("\""); sb.append(",");
				
				//status
				sb.append("\"");sb.append(responseObject.getSTATUS().getSTATUS()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(responseObject.getSTATUS().getERRORCODE()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(responseObject.getSTATUS().getMESSAGE()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(issuanceRequest.getUSERDETAILS().getUSERNAME()+"_"+issuanceRequest.getUSERDETAILS().getORGANISATION()); sb.append("\"");sb.append("\r\n"); 
				logger.info("sb buffer = "+sb);
				
				
				if(responseObject.getSTATUS().getERRORCODE().equals("0")){
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
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+loyIssuanceCSVFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Runtime.getRuntime().exec(chOwnCmd);
				
				String chmodCmd = "chmod 777 "+loyIssuanceCSVFilePath;
				Runtime.getRuntime().exec(chmodCmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			
			if(!isSpecific)renameAndMove(xmlfile.toString());
			else renameAndMoveForSpecific(xmlfile.toString(), donePath);
			writeSuccessMessage(xmlfile.getName(), issuanceRequestList.size(), successCount, failureCount,outboxLoyaltyIssuancePath, owner, group);
			
			
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
			logger.error("Exception while processing xml file..", e);
			renameAndMove(xmlfile.toString());
			writeFailedMessage("Permanent", "Server error", xmlfile.getName(),".xml");
		}
		logger.info("Completed processing XML file :"+xmlfile.getName());
	}
	
	public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("loyaltyissuanceschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, enrollxsd);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}
	public boolean renameAndMoveForSpecific(String xmlFile, String donepAth){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donepAth;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove enroll xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	public boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("doneloyaltyissuance");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+xmlFile+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove issuance xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}
	
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension, String outboxLoyaltyPath, String owner){
		logger.info("Started writing failed message of file :"+xmlfileName);		
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
				String chOwnCmd = "chown "+owner+":"+owner+" "+outboxLoyaltyPath+"/"+fileName;
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
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxloyaltyissuance");
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
	public void writeSuccessMessage(String xmlfileName, int totalCount, int successCount, int failureCount, String outboxLoyaltyPath, String owner, String group){
		logger.info("Started writing success message of file :"+xmlfileName);
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
	
	//public LoyaltyTransaction findTransactionByRequestId(String requestId){
	public LoyaltyTransaction findTransactionBy(String userDetail, String requestId, String docSId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			//transaction = loyaltyTransactionDao.findByRequestIdAndType(requestId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction = loyaltyTransactionDao.findByDocSIdRequestIdAndType(userDetail, requestId, docSId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyIssuanceRequestObject requestObject, String jsonRequest, String mode, String docSid) throws Exception{
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			transaction.setDocSID(docSid != null && 
					!docSid.trim().isEmpty() ? docSid.trim() : null);
		
            loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
			throw new Exception("Exception occured while logging transaction....", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponseObject response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			transaction.setCardNumber(response.getISSUANCEINFO().getCARDNUMBER());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private void writeStatusFile(String reasonType, String filename, String path, String owner) {
		writeFailedMessage(reasonType, "Invalid zip file", filename, ".zip", path, owner);
		
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
