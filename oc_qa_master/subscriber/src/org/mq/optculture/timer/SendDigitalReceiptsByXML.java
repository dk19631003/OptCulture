package org.mq.optculture.timer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.general.XML;
import org.mq.optculture.business.digitalReceipt.SendDRBusinessService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.digitalReceipt.CreditCardDR;
import org.mq.optculture.model.digitalReceipt.DRBody;
import org.mq.optculture.model.digitalReceipt.DRCOD;
import org.mq.optculture.model.digitalReceipt.DRCash;
import org.mq.optculture.model.digitalReceipt.DRCharge;
import org.mq.optculture.model.digitalReceipt.DRCheck;
import org.mq.optculture.model.digitalReceipt.DRCreditCard;
import org.mq.optculture.model.digitalReceipt.DRDebitCard;
import org.mq.optculture.model.digitalReceipt.DRDeposit;
import org.mq.optculture.model.digitalReceipt.DRFC;
import org.mq.optculture.model.digitalReceipt.DRFCCheck;
import org.mq.optculture.model.digitalReceipt.DRGift;
import org.mq.optculture.model.digitalReceipt.DRGiftCard;
import org.mq.optculture.model.digitalReceipt.DRItem;
import org.mq.optculture.model.digitalReceipt.DRPayments;
import org.mq.optculture.model.digitalReceipt.DRReceipt;
import org.mq.optculture.model.digitalReceipt.DRRequest;
import org.mq.optculture.model.digitalReceipt.DRStoreCredit;
import org.mq.optculture.model.digitalReceipt.DRTravelerCheck;
import org.mq.optculture.model.digitalReceipt.DRUserDetails;
import org.mq.optculture.model.digitalReceipt.DebitCardDR;
import org.mq.optculture.model.digitalReceipt.GiftCardDR;
import org.mq.optculture.model.digitalReceipt.GiftDR;
import org.mq.optculture.model.digitalReceipt.Items;
import org.mq.optculture.model.digitalReceipt.PaymentsDR;
import org.mq.optculture.model.digitalReceipt.SendDRRequest;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;
import org.mq.optculture.model.digitalReceipt.StoreCreditDR;
import org.mq.optculture.model.digitalReceipt.XMLDigitalReceipts;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class SendDigitalReceiptsByXML extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	private final String SftpUserPath = PropertyUtil.getPropertyValueFromDB("SftpUserPath");
	
	public void run() {
		logger.info("Started SendDigitalReceiptsByXML timer...");
		String inboxDRPath = PropertyUtil.getPropertyValue("inboxdigitalreceipt");
		String donePath = PropertyUtil.getPropertyValue("donedigitalreceipt");
		String outboxDRPath = PropertyUtil.getPropertyValue("outboxdigitalreceipt");
		try {
			boolean filesexists =false;
			if(inboxFileExist(inboxDRPath)){
				filesexists =true;
				logger.info("inbox DR files exist...");
				File file = new File(inboxDRPath);
				File[] xmlFiles  = file.listFiles();
				int NumOfUserThreads =  Integer.parseInt(PropertyUtil.getPropertyValueFromDB(OCConstants.DR_EXTRACTION_THREADS_KEY));
				ExecutorService executor = Executors.newFixedThreadPool(NumOfUserThreads);
				if(xmlFiles != null && xmlFiles.length != 0) {
				for (File xmlFile1 : xmlFiles) {	
					 Runnable worker = new SendDRByXmlMultiThreadService( xmlFile1,  
							 inboxDRPath, outboxDRPath, donePath,  owner, group, false);
			         executor.execute(worker);
				}
				}
				executor.shutdown();			
				while (!executor.isTerminated()) {
				
				}				
				
			}
			
			List<Users> specificDirExists = SpecificDirExists();
			if(specificDirExists != null && specificDirExists.size()>0) {
				filesexists = true;
				//String genericUsername = "ocftpuser";
				
				for (Users users : specificDirExists) {
					String username = Utility.getOnlyOrgId(users.getUserName());
					inboxDRPath = SftpUserPath+"/"+username+"/opt_sync/inbox/digital_receipt";
					donePath = SftpUserPath+"/"+username+"/opt_sync/done/digital_receipt";
					outboxDRPath = SftpUserPath+"/"+username+"/opt_sync/outbox/digital_receipt";
					File file = new File(inboxDRPath);
					File[] xmlFiles  = file.listFiles();
					int dbThreads = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(OCConstants.DR_EXTRACTION_THREADS_KEY));
					int NumOfUserThreads =  specificDirExists.size()>=dbThreads ? dbThreads :specificDirExists.size()  ;//
					ExecutorService executor = Executors.newFixedThreadPool(NumOfUserThreads);
					if(xmlFiles != null && xmlFiles.length != 0) {
					for (File xmlFile1 : xmlFiles) {	
						 Runnable worker = new SendDRByXmlMultiThreadService( xmlFile1,  
								 inboxDRPath, outboxDRPath, donePath,  username, username, true);
				         executor.execute(worker);
					}
					}
					executor.shutdown();			
					while (!executor.isTerminated()) {
					
					}			
					
				}
				
			}
			
			if(!filesexists){
				logger.info("Digital Receipt inbox files not exist.");
			}
		} catch (Exception e) {
			logger.error("Exception in Digital Receipts xml file test", e);
			return;
		} finally {
			logger.info("Completed SendDigitalReceiptsByXML timer...");
		}
		return;
	}
	
	public List<Users> SpecificDirExists() throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		List<Users> specificDirUsers = usersDao.findAnySpeciDirBasedUsersExists();
		return specificDirUsers;
		
	}
	
	/*public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extension))+".txt";
		try {
			File file = new File(outboxDRPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason type: "+reasonType);
			str.append("\r\n");
			str.append("Error message: ");
			str.append(errorMsg);
			bw.write(str.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		
		try {
			String chOwnCmd = "chown "+owner+":"+group+" "+outboxDRPath+"/"+fileName;
			logger.debug("chaging the file permissions =="+ chOwnCmd);
			Runtime.getRuntime().exec(chOwnCmd);
			
			String chmodCmd = "chmod 777 "+outboxDRPath+"/"+fileName;
			Runtime.getRuntime().exec(chmodCmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in while writing failed message.", e);
		}
		
		logger.info("Completed writing failed message of file :"+xmlfileName);
	}*/
	
	public void writeSuccessMessage(String xmlfileName, int totalCount, int successCount, int failureCount){
		logger.info("Started writing success message of file :"+xmlfileName);
		String outboxDRPath = PropertyUtil.getPropertyValue("outboxdigitalreceipt");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf(".xml"))+".txt";
		try {
			File file = new File(outboxDRPath+"/"+fileName);
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
			String chOwnCmd = "chown "+owner+":"+group+" "+outboxDRPath+"/"+fileName;
			logger.debug("chaging the file permissions =="+ chOwnCmd);
			Runtime.getRuntime().exec(chOwnCmd);
			
			String chmodCmd = "chmod 777 "+outboxDRPath+"/"+fileName;
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
			String donePath = PropertyUtil.getPropertyValue("donedigitalreceipt");
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
	
	public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String digitalReceiptxsd = PropertyUtil.getPropertyValue("digitalreceiptschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, digitalReceiptxsd);
		} catch (Exception e) {
			logger.error("Exception ############", e);
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
	
	public void processXmlFile(File xmlfile){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		try{
			
			Object object =  XMLUtil.unMarshal(xmlfile.toString(), XMLDigitalReceipts.class);
			XMLDigitalReceipts digiList = (XMLDigitalReceipts)object;
						
			List<DRBody> drRequestList = digiList.getDrrequest();
			logger.info("Total requests in the file : "+drRequestList.size());
			
			String outboxDigitalReceiptPath = PropertyUtil.getPropertyValue("outboxdigitalreceipt"); //TODO
			
			outboxDigitalReceiptPath += outboxDigitalReceiptPath.endsWith(File.separator) ? "" : File.separator;
			String digiReceiptCSVFilePath = outboxDigitalReceiptPath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
			
			File inboxdigiReceiptCSVFile = new File(digiReceiptCSVFilePath);
			//BufferedWriter bw = new BufferedWriter(new FileWriter(digiReceiptCSVFilePath));
			//bw.write("\"Request ID\",\"Doc Sid\",\"Customer Id\",\"Email Id\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
			StringBuffer sb =  new StringBuffer();
			
			int i = 0;
			for(DRBody drRequest : drRequestList) {
				
				i=i+1;
				logger.info("Started processing request "+ i +" >>>>");
				Gson gson = new Gson();
				Items items = drRequest.getItems();
				CreditCardDR creditCards = drRequest.getCreditCards();
				
				// Added after New DR Schema
				GiftDR clsGiftCertificate = drRequest.getClsGiftCertificate();
				 GiftCardDR clsGiftCard = drRequest.getClsGiftCard();
				 DebitCardDR clsDebitcard = drRequest.getClsDebitcard();
				 StoreCreditDR clsStoreCredit = drRequest.getClsStoreCredit();
			
				
				//cash
				DRCash cash = drRequest.getCash();
				String drCash = gson.toJson(cash, DRCash.class);
				
				//receipt
				DRReceipt receipt = drRequest.getReceipt();
				String drReceipt = gson.toJson(receipt, DRReceipt.class);
				
				//user details
				DRUserDetails userDetails = drRequest.getUserDetails();
				String drUserDetails = gson.toJson(userDetails, DRUserDetails.class);

				List<DRItem> listOfItems = null;;
				List<DRCreditCard> listOfCards = null;
				
				if(items != null){
					listOfItems = items.getItems();
				}
				
				if(creditCards != null){
					listOfCards = creditCards.getCreditCards();
				}
				
				// added after new DR Schema
				
				// Deposit
				DRDeposit deposit =drRequest.getDeposit();
				String drDeposit = gson.toJson(deposit, DRDeposit.class);
				
				// FC
				DRFC fc = drRequest.getFC();
				String drFC = gson.toJson(fc, DRFC.class);
				
				// COD
				DRCOD cod = drRequest.getCOD();
				String drCOD = gson.toJson(cod, DRCOD.class);
				
				//Check
				DRCheck check = drRequest.getCheck();
				String drCheck = gson.toJson(check, DRCheck.class);
				
				List<DRGift> listOfGifts = null;
				List<DRGiftCard> listOfGiftCards = null;
				List<DRDebitCard> listOfDebitCards = null;
				List<DRStoreCredit> listOfStoreCredit = null;
				
				// Gift
				
				if(clsGiftCertificate != null){
					listOfGifts = clsGiftCertificate.getClsGiftCertificate();
				}
				
				// GiftCard
				
				if(clsGiftCard != null){
					listOfGiftCards = clsGiftCard.getClsGiftCard();
				}
				
				// Debit Card
				if(clsDebitcard != null){
					
					listOfDebitCards = clsDebitcard.getClsDebitcard();
				}
				
				// Store Credit
				
				if(clsStoreCredit != null){
					
					listOfStoreCredit = clsStoreCredit.getClsStoreCredit();
				}
				
				// Charge
				DRCharge charge = drRequest.getCharge();
				String drCharge = gson.toJson(charge, DRCharge.class);
				
				// Addeed after 3 rearely userd tender types
				 PaymentsDR clspPaymentsDR = drRequest.getClsPayments();
				 
				 List<DRPayments> listOfpayments = null;
				 
				 // Payments
				 if(clspPaymentsDR != null){
					 
					 listOfpayments = clspPaymentsDR.getClsPayments();
				 }
				
				 // TravelerCheck
				 
				DRTravelerCheck	 travelerCheck	 = drRequest.getTravelerCheck();
				String travelCheck = gson.toJson(travelerCheck, DRTravelerCheck.class);
				
				// FCCheck
				
				DRFCCheck FCCheck = drRequest.getFCCheck();
				String fcCheck = gson.toJson(FCCheck, DRFCCheck.class);
			
				
				
				DRRequest drRequestObj = prepareDRRequestObject(listOfItems, listOfCards, cash, receipt, userDetails ,deposit , fc , cod ,check ,
																listOfGifts ,listOfGiftCards , listOfDebitCards, listOfStoreCredit , charge, listOfpayments , travelerCheck , FCCheck);
				String drRequestJson = gson.toJson(drRequestObj);
				logger.info("dr request = "+drRequestJson);
				
				SendDRBusinessService drService = (SendDRBusinessService)ServiceLocator.getInstance().getServiceById(OCConstants.SEND_DR_BUSINESS_SERVICE);
				
				SendDRRequest sendDrRequest = new SendDRRequest();
				sendDrRequest.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
				sendDrRequest.setJsonValue(drRequestJson);
				sendDrRequest.setUserName(drRequest.getUserDetails().getUserName());
				sendDrRequest.setUserOrg(drRequest.getUserDetails().getOrganisation());
				
				SendDRResponse drResponse = drService.processSendDRRequest(sendDrRequest, OCConstants.DR_OFFLINE_MODE);
				logger.info("status message = "+drResponse.getRESPONSEINFO().getSTATUS().getMESSAGE());
				
				//RequestId
				sb.append("\"");sb.append("---"); sb.append("\""); sb.append(",");
//				bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				//Doc Sid
				String docSid = receipt.getDocSID();
				sb.append("\"");sb.append(docSid == null ? "" : docSid ); sb.append("\""); sb.append(",");
				//Cust Sid
				String custSid = receipt.getBillToCustSID();
				sb.append("\"");sb.append(custSid == null ? "" : custSid ); sb.append("\""); sb.append(",");
				//Email Id
				String emailId = receipt.getBillToEMail();
				sb.append("\"");sb.append(emailId == null ? "" : emailId ); sb.append("\""); sb.append(",");
				//status
				sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getSTATUS()); sb.append("\""); sb.append(",");
				//Error Code
				sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getERRORCODE()); sb.append("\""); sb.append(",");
				//Error Mesage
				sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getMESSAGE()); sb.append("\""); sb.append(",");
				//User_Org
				sb.append("\"");sb.append(sendDrRequest.getUserName()+"_"+sendDrRequest.getUserOrg()); sb.append("\"");sb.append("\r\n"); 
				logger.info("sb buffer = "+sb);
				
				
				if(drResponse.getRESPONSEINFO().getSTATUS().getERRORCODE().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
				
				logger.info("Completed processing request "+ i +" >>>>");
			}//for
			if(failureCount > 0){
				BufferedWriter bw = new BufferedWriter(new FileWriter(digiReceiptCSVFilePath));
				bw.write("\"Request ID\",\"Doc Sid\",\"Customer Id\",\"Email Id\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			}
			
			sb = null;
			//System.gc();
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+digiReceiptCSVFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Runtime.getRuntime().exec(chOwnCmd);
				
				String chmodCmd = "chmod 777 "+digiReceiptCSVFilePath;
				Runtime.getRuntime().exec(chmodCmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			renameAndMove(xmlfile.toString());
			writeSuccessMessage(xmlfile.getName(), drRequestList.size(), successCount, failureCount);
			
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
						+ "<br />Digital Receipt failed count is :"+failureCount+""
								+ "<br /> Please refer  "+inboxdigiReceiptCSVFile.getName()+" in FTP Digital Receipt outbox("+outboxDigitalReceiptPath+") folder. </body></html>");
				EmailQueue emailQueue = new EmailQueue("OptSync Digital Receipt failed",messageStrBuff.toString(),Constants.OPT_SYN_DIGITAL_RECEIPT,"Active",supportEmail,MyCalendar.getNewCalendar(),user);
				
				emailQueueDao.saveOrUpdate(emailQueue);
				
			} */// Preparing email queue Object
			
		}catch(Exception e){
			logger.error("Exception while processing xml file..", e);
			renameAndMove(xmlfile.toString());
			//writeFailedMessage("Permanent", "Server error", xmlfile.getName(),".xml");
		}
		logger.info("Completed processing XML file :"+xmlfile.getName());
	}//End of method

	/*private String readFileContentIntoString(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}*/
	// ,deposit , fc , COD ,check ,listOfGifts ,listOfGiftCards , listOfDebitCards, listOfStoreCredit , charge
	private DRRequest prepareDRRequestObject(List<DRItem> items, List<DRCreditCard> creditCard, DRCash cash, DRReceipt receipt, DRUserDetails userDetails ,	DRDeposit deposit ,
					DRFC fc , DRCOD cod ,DRCheck check ,List<DRGift> gift , List<DRGiftCard> giftCard , List<DRDebitCard> debitCard,List<DRStoreCredit> storeCredit , DRCharge charge,
					List<DRPayments> payments , DRTravelerCheck travelerCheck , DRFCCheck fcCheck ){
		
		DRRequest drRequest = new DRRequest();
		
		if(cash.getAmount() != null && !cash.getAmount().isEmpty())drRequest.setCash(cash);
		if(creditCard != null && creditCard.size() != 0)drRequest.setCreditCard(creditCard);
		if(items != null && items.size() != 0)drRequest.setItems(items);
		drRequest.setReceipt(receipt);
		drRequest.setUserDetails(userDetails);
		if(deposit.getAmount() != null && !deposit.getAmount().isEmpty())drRequest.setDeposit(deposit);
		if(fc.getAmount() != null && !fc.getAmount().isEmpty())drRequest.setFc(fc);
		if(cod.getAmount() != null && !cod.getAmount().isEmpty())drRequest.setCod(cod);
		if(check.getAmount() != null && !check.getAmount().isEmpty())drRequest.setCheck(check);
		if(gift != null && gift.size() != 0)drRequest.setGift(gift);
		if(giftCard != null && giftCard.size() != 0)drRequest.setGiftCard(giftCard);
		if(debitCard != null && debitCard.size() != 0)drRequest.setDebitCard(debitCard);
		if(storeCredit != null && storeCredit.size() != 0)drRequest.setStoreCredit(storeCredit);
		if(charge.getAmount() != null && !charge.getAmount().isEmpty())drRequest.setCharge(charge);
		if(payments != null && payments.size() != 0)drRequest.setPayments(payments);
		if(travelerCheck.getAmount() != null && !travelerCheck.getAmount().isEmpty())drRequest.setTravelerCheck(travelerCheck);
		if(fcCheck.getAmount() != null && !fcCheck.getAmount().isEmpty())drRequest.setFCCheck(fcCheck);
		
		/*drRequest.setCash(cash);
		drRequest.setCreditCard(creditCard);
		drRequest.setItems(items);
		drRequest.setReceipt(receipt);
		drRequest.setUserDetails(userDetails);
		drRequest.setDeposit(deposit);
		drRequest.setFc(fc);
		drRequest.setCod(cod);
		drRequest.setCheck(check);
		drRequest.setGift(gift);
		drRequest.setGiftCard(giftCard);
		drRequest.setDebitCard(debitCard);
		drRequest.setStoreCredit(storeCredit);
		drRequest.setCharge(charge);
		drRequest.setPayments(payments);
		drRequest.setTravelerCheck(travelerCheck);
		drRequest.setFCCheck(fcCheck);*/
	
		return drRequest;
	}
	
	private void writeStatusFile(String reasonType, String filename) {
		//writeFailedMessage(reasonType, "Invalid zip file", filename, ".zip");
		
	}
}
