package org.mq.optculture.timer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.RequestToOC;
import org.mq.optculture.model.DR.DigitalReceiptResponse;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class GeneralRequestOfflineService extends TimerTask {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private  Map<String,String> pathRequestMappings = new HashMap<>();
	private  Map<String,String> pathServiceMappings = new HashMap<>();
	
	private final String owner = PropertyUtil.getPropertyValueFromDB("owner");
	private final String group = PropertyUtil.getPropertyValueFromDB("group");
	private final String SftpUserPath = PropertyUtil.getPropertyValueFromDB("SftpUserPath");
	
	//private  String inboxPath,donePath, outboxPath;

	public void run() {
		
		//pathRequestMappings.put("/processReceipt.mqrm", "org.mq.optculture.mo");
		pathServiceMappings.put("/processReceipt.mqrm", OCConstants.PROCESS_DR_BUSINESS_SERVICE);
		
		logger.info("Started LoyaltyEnrollByXML timer...");
		String inboxPath = PropertyUtil.getPropertyValue("inboxgeneral");
		String outboxPath = PropertyUtil.getPropertyValue("outboxgeneral");
		String donePath = PropertyUtil.getPropertyValue("donegeneral");
		try {
			if(inboxFileExist(inboxPath)){
				logger.info("inbox enroll files exist...");
				File file = new File(inboxPath);
				File[] xmlFiles  = file.listFiles();
				
				logger.info("List out the inbox enroll files...");
				for(File file2 : xmlFiles){
					logger.info(file2.getName().toString());
				}
				
				for(File xmlFile1 : xmlFiles){
					
					//TODO changes
					File xmlFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						
						xmlFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxPath);
						
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
					}*/
					
					processXmlFile(xmlFile);
					renameAndMove(xmlFile.toString());
					
				}
			}
			else{
				logger.info("loyalty inbox enroll files not exist.");
			}
			// Separate SFTP directory
			List<Users> specificDirExists = SpecificDirExists();
			if(specificDirExists != null && specificDirExists.size()>0) {
				for (Users users : specificDirExists) {
					String username = Utility.getOnlyOrgId(users.getUserName());
					inboxPath = SftpUserPath+"/"+username+"/opt_sync/inbox/proc_in";
					donePath = SftpUserPath+"/"+username+"/opt_sync/done/proc_in";
					outboxPath = SftpUserPath+"/"+username+"/opt_sync/outbox/proc_in";
			if(inboxFileExist(inboxPath)){
				logger.info("inbox enroll files exist...");
				File file = new File(inboxPath);
				File[] xmlFiles  = file.listFiles();
				
				logger.info("List out the inbox enroll files...");
				for(File file2 : xmlFiles){
					logger.info(file2.getName().toString());
				}
				
				for(File xmlFile1 : xmlFiles){
					
					//TODO changes
					File xmlFile  = null;
					if(xmlFile1.getName().endsWith(".zip")) {
						
						xmlFile = XMLUtil.unzip(xmlFile1.getAbsolutePath(), inboxPath);
						
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
					}*/
					
					processXmlFile(xmlFile);
					renameAndMove(xmlFile.toString());
					
				}
			}
			else{
				logger.info("loyalty inbox enroll files not exist.");
			}
		}
			}
		} catch (Exception e) {
			logger.error("Exception in xml enrolment file test", e);
			return;
		} finally {
			logger.info("Completed LoyaltyEnrollByXML timer...");
		}
		return;
	}
	
	public List<Users> SpecificDirExists() throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		List<Users> specificDirUsers = usersDao.findAnySpeciDirBasedUsersExists();
		return specificDirUsers;
		
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
	
	private void writeStatusFile(String reasonType, String filename) {
		writeFailedMessage(reasonType, "Invalid zip file", filename, ".zip");
		
	}
	public void writeFailedMessage(String reasonType, String errorMsg, String xmlfileName, String extension){
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxgeneral");
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
		String outboxLoyaltyPath = PropertyUtil.getPropertyValue("outboxgeneral");
		FileWriter writer;
		String fileName = xmlfileName.substring(0, xmlfileName.indexOf("."))+".txt";
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
	
	public boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("doneloyaltyenroll");
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
	
	/*public boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String enrollxsd = PropertyUtil.getPropertyValue("loyaltyenrollschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, enrollxsd);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}*/
	
	public boolean checkFileWithXmlExtension(String xmlfile) {
		logger.info("Started checking xml extension of file : "+xmlfile);
		boolean status = false;
		if(xmlfile != null && xmlfile.trim().endsWith(".xml")){
			status = true;
		}
		logger.info("Completed checking xml extension of file : "+xmlfile);
		return status;
	}
	
	public GeneralRequestOfflineService ()
	{
		//pathRequestMappings.put("/sendDigitalReceiptOPT.mqrm", "org.mq.optculture.json.digitalReceipt.DRRequestJson");
		//pathRequestMappings.put("/sendDigitalReceiptOPT.mqrm", "org.mq.optculture.model.digitalReceipt.SendDRRequest");
		
		
		//pathServiceMappings.put("/processReceipt.mqrm", "org.mq.optculture.business.digitalReceipt.ProcessDigitalReceiptService");
		
		
		
	}
	
	
	public static void main(String[] args) throws Exception{/*
		
		String filePath = "/home/proumya/sample.xml";
		String jsoncontent = "";
		String REQUESTSOURCE = null;
		String REQUEST_URL = null;
		String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
		System.out.println(content);
		if(filePath.endsWith(".xml")) {
			
			String reqstr ="<REQUESTSTRING>";
			String reqstr1 ="<REQUESTJSON>";
			String reqSource = "<REQUESTSOURCE>";
			String requesturl = "<REQUESTURL>";
			if(content.indexOf(reqstr ) != -1){
				
				jsoncontent = content.substring(content.indexOf(reqstr)+reqstr.length(), content.indexOf("</REQUESTSTRING>"));
			}else if(content.indexOf(reqstr1 ) != -1){
				jsoncontent = content.substring(content.indexOf(reqstr1)+reqstr1.length(), content.indexOf("</REQUESTJSON>"));
				
			}
			REQUESTSOURCE = content.substring(content.indexOf(reqSource)+reqSource.length(), content.indexOf("</REQUESTSOURCE>"));
			REQUEST_URL = content.substring(content.indexOf(requesturl)+requesturl.length(), content.indexOf("</REQUESTURL>"));
		}else{
			
			//get all the details like format, url, source etc.
			Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\",?").matcher(content);
			String key = null;
			String value = null;
			while (m.find()) {
				
				key = m.group(1);
				value = m.group(2);
				System.out.println(key +":"+ value);
				if(key.equals("Head")){
					System.out.println(key);
					System.out.println(value);
					Matcher mhead = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\",?").matcher(value);
					
					while (mhead.find()) {
						
						key = mhead.group(1);
						value = mhead.group(2);
					}
					if(key.equals("requestSource")){
						REQUESTSOURCE = value;
					}else if(key.equals("requestEndPoint")) {
						REQUEST_URL = value;
						
					}
					if(REQUESTSOURCE != null && REQUEST_URL != null) break;
				}
				if(REQUESTSOURCE != null && REQUEST_URL != null) break;
			}
			
		}
		
		Gson gson = new Gson();
		PrismBasedDRRequest prismBasedDRRequest = gson.fromJson(content, PrismBasedDRRequest.class);
		System.out.println(prismBasedDRRequest);
		
		
		
		System.out.println("REQUESTSOURCE :  "+REQUESTSOURCE +"REQUEST_URL: "+REQUEST_URL);
	*/
		 int maxPrice = 0;
	        int minPrice = Integer.MAX_VALUE;
	        int[] prices = {7,6,4,3,1};
	        for(int price : prices){
	            minPrice = Math.min(price,minPrice);
	            maxPrice = Math.max(maxPrice,price - minPrice);
	        }	
	System.out.println(maxPrice);
	
	
	}
	
	public void processFiles() {/*
		try
		{
		if(XMLUtil.inboxFileExist(inboxPath)){
			logger.info("inbox DR files exist...");
			File file = new File(inboxPath);
			File[] xmlFiles  = file.listFiles();
			
			logger.info("List out the inbox DR files...");
			for(File file2 : xmlFiles){
				if(!file2.getName().endsWith(".xml")){
					try {
						boolean isdeleted = file2.delete();
						logger.debug("isdeleted ???"+isdeleted);
						continue;
					} catch (Exception e) {
						logger.error("Exception while deleting other than xml file", e);
					}
				}
				String dos2UnixCmd = "dos2unix "+inboxPath+File.separator+
						file2.getName()+" "+inboxPath+File.separator+file2.getName();
				logger.info("dos2UnixCmd..."+dos2UnixCmd);
				Runtime.getRuntime().exec(dos2UnixCmd);
			}
			 xmlFiles  = file.listFiles();
			 for (File xmlFile : xmlFiles) {
				 processXmlFile(xmlFile);
			 }
		}	
		}catch (Exception e) {
			logger.error("Exception in the xml file ", e);
			return;
		} finally {
			logger.info("Completed requests  timer...");
			
		}
		
	*/}
	
	
	private void processXmlFile(File xmlfile){
		logger.info("Started processing XML file :"+xmlfile.getName());
		int successCount = 0;
		int failureCount = 0;
		try
		{
			Object object = null;
			RequestToOC RequestToOCObj = null;
			String REQUEST_URL = null;
			String REQUEST_JSON = null;
			String REQUESTSOURCE = null;
			try {
				object = XMLUtil.unMarshal(xmlfile.toString(), RequestToOC.class);
							
				
				RequestToOCObj = (RequestToOC)object;
				REQUEST_URL  = RequestToOCObj.getREQUESTURL();
				REQUEST_JSON = RequestToOCObj.getREQUESTJSON();
				REQUESTSOURCE = RequestToOCObj.getREQUESTSOURCE();
				if(REQUEST_JSON == null) {
					REQUEST_JSON = RequestToOCObj.getREQUESTSTRING();
				}
			} catch (Exception e) {
				
				String content = new Scanner(new File(xmlfile.toString())).useDelimiter("\\Z").next();
				//System.out.println(content);
				if(xmlfile.toString().endsWith(".xml")) {
					
					String reqstr ="<REQUESTSTRING>";
					String reqstr1 ="<REQUESTJSON>";
					String reqSource = "<REQUESTSOURCE>";
					String requesturl = "<REQUESTURL>";
					if(content.indexOf(reqstr ) != -1){
						
						REQUEST_JSON = content.substring(content.indexOf(reqstr)+reqstr.length(), content.indexOf("</REQUESTSTRING>"));
					}else if(content.indexOf(reqstr1 ) != -1){
						REQUEST_JSON = content.substring(content.indexOf(reqstr1)+reqstr1.length(), content.indexOf("</REQUESTJSON>"));
						
					}
					REQUESTSOURCE = content.substring(content.indexOf(reqSource)+reqSource.length(), content.indexOf("</REQUESTSOURCE>"));
					REQUEST_URL = content.substring(content.indexOf(requesturl)+requesturl.length(), content.indexOf("</REQUESTURL>"));
				}else if(xmlfile.getName().endsWith(".txt") || xmlfile.getName().endsWith(".json"))  {
					REQUEST_JSON = content;
					//get all the details like format, url, source etc.
					Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\",?").matcher(REQUEST_JSON);
					String key = null;
					String value = null;
					while (m.find()) {
						
						key = m.group(1);
						value = m.group(2);
						
						if(key.equals("requestSource")){
							REQUESTSOURCE = value;
						}else if(key.equals("requestEndPoint")) {
							REQUEST_URL = value;
							
						}
						if(REQUESTSOURCE != null && REQUEST_URL != null) break;
					}
					
				}
				
			}
			
			BaseRequestObject baseRequest = new BaseRequestObject();
			baseRequest.setJsonValue(REQUEST_JSON);
			baseRequest.setSource(REQUESTSOURCE);
			baseRequest.setMode(OCConstants.DR_OFFLINE_MODE);
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(pathServiceMappings.get(REQUEST_URL));
			
			BaseResponseObject response = baseService.processRequest(baseRequest);
			if(response != null) {
				if(response instanceof DigitalReceiptResponse) {
					DigitalReceiptResponse drResponse = (DigitalReceiptResponse)response;
					StringBuffer sb =  new StringBuffer();
					//RequestId
					sb.append("\"");sb.append("---"); sb.append("\""); sb.append(",");
//					bw.write("\"Request ID\",\"Card Number\",\"Customer Id\",\"Email Id\",\"Mobile\",\"status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
					//Doc Sid
					/*String docSid = drRequestObj.getReceipt().getDocSID();
					sb.append("\"");sb.append(docSid == null ? "" : docSid ); sb.append("\""); sb.append(",");
					//Cust Sid
					String custSid = drRequestObj.getReceipt().getBillToCustSID();
					sb.append("\"");sb.append(custSid == null ? "" : custSid ); sb.append("\""); sb.append(",");
					//Email Id
					String emailId = drRequestObj.getReceipt().getBillToEMail();
					sb.append("\"");sb.append(emailId == null ? "" : emailId ); sb.append("\""); sb.append(",");*/
					//status
					sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getSTATUS()); sb.append("\""); sb.append(",");
					//Error Code
					sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getERRORCODE()); sb.append("\""); sb.append(",");
					//Error Mesage
					sb.append("\"");sb.append(drResponse.getRESPONSEINFO().getSTATUS().getMESSAGE()); sb.append("\""); sb.append(",");
					//User_Org
					//sb.append("\"");sb.append(sendDrRequest.getUserName()+"_"+sendDrRequest.getUserOrg()); sb.append("\"");sb.append("\r\n"); 
					logger.info("sb buffer = "+sb);
					
					
					if(drResponse.getRESPONSEINFO().getSTATUS().getERRORCODE().equals("0")){
						successCount += 1;
					}
					else{
						failureCount += 1;
					}
					String outboxDigitalReceiptPath = PropertyUtil.getPropertyValue("outboxgeneral"); //TODO
					
					outboxDigitalReceiptPath += outboxDigitalReceiptPath.endsWith(File.separator) ? "" : File.separator;
					String digiReceiptCSVFilePath = outboxDigitalReceiptPath+xmlfile.getName().replace(FilenameUtils.getExtension(xmlfile.getName()), "csv");
					
					if(failureCount > 0){
						BufferedWriter bw = new BufferedWriter(new FileWriter(digiReceiptCSVFilePath));
						bw.write("\"Request ID\",\"Doc Sid\",\"Customer Id\",\"Email Id\",\"Status\",\"Error Code\",\"Error Message\",\"User_Org\" \r\n");
						bw.write(sb.toString());
						bw.flush();
						bw.close();
					}
					
					sb = null;
					//System.gc();
					
					renameAndMove(xmlfile.toString());
					writeSuccessMessage(xmlfile.getName(), 1, successCount, failureCount);
					
				}
				
			}
			
		}catch(Exception e)
		{
			
			logger.error("Exception====", e);
		}
		}
		
	
	
	
	
	
	
	
}
