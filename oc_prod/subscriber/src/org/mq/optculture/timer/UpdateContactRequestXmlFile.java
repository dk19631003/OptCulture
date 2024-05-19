package org.mq.optculture.timer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.updatecontacts.UpdateContactsBusinessService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactRequests;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

import com.google.gson.Gson;

public class UpdateContactRequestXmlFile extends TimerTask  {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public void run() {
	String inboxFilePathStr=PropertyUtil.getPropertyValue("inboxcontact");
	String donePath = PropertyUtil.getPropertyValue("donecontact");
	try {
		if(inboxFileExist(inboxFilePathStr)){
			File file = new File(inboxFilePathStr);
			File[] files  = file.listFiles();
			
			logger.info("List out the inbox contact files...");
			for(File file2 : files){
				logger.info(file2.getName().toString());
			}
			
			for(File file1 : files){
				File  xmlFile = null;
				if(file1.getName().endsWith(".zip")) {/*
					xmlFile = XMLUtil.unzip(file1.getAbsolutePath(), inboxFilePathStr);
					if(xmlFile == null) {
						logger.info("Invalid zip file..."+file1);
						renameAndMove(file1);
						writeZipFailedMessage("Temporary","Extraction of file failed", file1.getName());
						continue;
					}
					renameAndMove(file1);
				*/
					logger.info("file1.getAbsolutePath() = "+file1.getAbsolutePath());
					xmlFile = XMLUtil.unzip(file1.getAbsolutePath(), inboxFilePathStr);
					
					if(xmlFile == null) {
						logger.info("Unzip falied for this file name  "+file1.getAbsolutePath());
						
						//TODO write a txt file and put them in to outbox as txt
						writeStatusFile("Temporary", file1.getName());
						if(!donePath.endsWith(File.separator) ){
							donePath = donePath+File.separator;
						}
						//Zip Move to done folder
						file1.renameTo(new File(donePath+file1.getName()));
						 
						continue;
						
					}
					if(!donePath.endsWith(File.separator) ){
						donePath = donePath+File.separator;
					}
					//Zip Move to done folder
					file1.renameTo(new File(donePath+file1.getName()));	
				}else {
					xmlFile = file1;
				}
				
				boolean validXmlFile = validateXmlWithXsd(xmlFile.toString());
				
				if(!validXmlFile){
					logger.info("Invalid xml file..."+xmlFile.getAbsolutePath());
					renameAndMove(xmlFile.toString());
					writeFailedMessage("Permanent","Invalid xsd file", xmlFile.getName(),".xml");
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
			logger.info("Update contact inbox  files not exist.");
		}
	}catch (Exception e) {
		logger.error("",e);
	}
	}
	
	

	private void writeStatusFile(String reasonType, String name) {
		writeFailedMessage(reasonType, "Invalid zip file", name, ".zip");
		
	}

	/*private void writeZipFailedMessage(String reason, String errorMessage,
			String zipFileName) {
		logger.info("Started writing failed message of file :"+zipFileName);		
		String outboxContactPath = PropertyUtil.getPropertyValue("outboxcontact");
		FileWriter writer;
		try {
			String fileName = zipFileName.substring(0, zipFileName.indexOf(".zip"))+".txt";
			File file = new File(outboxContactPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason: ");
			str.append(reason);
			str.append("\r\n");
			str.append("Error message: ");
			str.append(errorMessage);
			bw.write(str.toString());
			bw.write("Status : Not Processed");
			bw.newLine();
			bw.write("Reason type: "+reason);
			bw.newLine();
			bw.write("Error message: "+errorMessage);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing failed message of file :"+zipFileName);
		
	}*/


	private void processXmlFile(File xmlFile) {
		logger.info("Started processing XML file :"+xmlFile.getName());
		int successCount = 0;
		int failureCount = 0;
		try{
			Gson gson=new Gson();
			ContactRequests contactRequests =  (ContactRequests) XMLUtil.unMarshal(xmlFile.toString(), ContactRequests.class);
			List<ContactRequest> contactRequestList = contactRequests.getContactRequest();
			logger.info("Total requests in the file : "+contactRequestList.size());
			ContactResponse contactResponse=null;
			//UpdateContactsRequestObject updateContactsRequestObject=new UpdateContactsRequestObject();
			for(ContactRequest  contactRequest : contactRequestList) {
				//updateContactsRequestObject.setUPDATECONTACTREQUEST(UpdateContactRequest);
				UpdateContactsBusinessService updateContactsBusinessService = (UpdateContactsBusinessService)ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);
				contactResponse = (ContactResponse) updateContactsBusinessService.processUpdateContactRequest(contactRequest,false);
				String responseJson = gson.toJson(contactResponse);	
				logger.info("Response = "+responseJson);
				if(contactResponse.getStatus().getErrorCode().equals("0")){
					successCount += 1;
				}
				else{
					failureCount += 1;
				}
			}//for
			renameAndMove(xmlFile.toString());
			writeSuccessMessage(xmlFile.getName(), contactRequestList.size(), successCount, failureCount);
		}catch(Exception e){
			logger.error("Exception while processing xml file..", e);
			renameAndMove(xmlFile.toString());
			writeFailedMessage("Permanent","Server error", xmlFile.getName(),".xml");
		}
		logger.info("Completed processing XML file :"+xmlFile.getName());
	}

	private void writeSuccessMessage(String xmlfileName, int size, int successCount,
			int failureCount) {
		String outboxContactPath = PropertyUtil.getPropertyValue("outboxcontact");
		FileWriter writer;
		try {
			String fileName = xmlfileName.substring(0, xmlfileName.indexOf(".xml"))+".txt";
			File file = new File(outboxContactPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Processed");
			str.append("\r\n");
			str.append("Total Update Contact Requests : ");
			str.append(size);
			str.append("\r\n");
			str.append("Success count : ");
			str.append(successCount);
			str.append("\r\n");
			str.append("Failure count : ");
			str.append(failureCount);
			bw.write(str.toString());
			/*bw.write("Status : Processed");
			bw.newLine();
			bw.write("Total Update Contact Requests : "+size);
			bw.newLine();
			bw.write("Success count : "+successCount);
			bw.newLine();
			bw.write("Failure count : "+failureCount);*/
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing success message of file :"+xmlfileName);
	}

	private boolean checkFileWithXmlExtension(String xmlfile) {
		logger.info("Started checking xml extension of file : "+xmlfile);
		boolean status = false;
		if(xmlfile != null && xmlfile.trim().endsWith(".xml")){
			status = true;
		}
		logger.info("Completed checking xml extension of file : "+xmlfile);
		return status;
	}

	private void writeFailedMessage(String reason,String errorMessage, String xmlfileName,String extension) {/*
		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxContactPath = PropertyUtil.getPropertyValue("outboxcontact");
		FileWriter writer;
		try {
			String fileName = xmlfileName.substring(0, xmlfileName.indexOf(".xml"))+".txt";
			File file = new File(outboxContactPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason: ");
			str.append(reason);
			str.append("\r\n");
			str.append("Error message: ");
			str.append(errorMessage);
			bw.write(str.toString());
			bw.write("Status : Not Processed");
			bw.newLine();
			bw.write("Reason type: "+reason);
			bw.newLine();
			bw.write("Error message: "+errorMessage);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing failed message of file :"+xmlfileName);
	*/

		logger.info("Started writing failed message of file :"+xmlfileName);		
		String outboxContactPath = PropertyUtil.getPropertyValue("outboxcontact");
		FileWriter writer;
		try {
			String fileName = xmlfileName.substring(0, xmlfileName.indexOf(extension))+".txt";
			File file = new File(outboxContactPath+"/"+fileName);
			writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			StringBuffer str = new StringBuffer();
			str.append("Status : Not Processed");
			str.append("\r\n");
			str.append("Reason Type : "+reason);
			str.append("\r\n");
			str.append("Error Message : ");
			str.append(errorMessage);
			bw.write(str.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Exception in while writing failed message.", e);
		}
		logger.info("Completed writing failed message of file :"+xmlfileName);
	
		}

	private boolean renameAndMove(String file) {
		logger.info("Started renaming and moving a file : "+file);
		try{
			String donePath = PropertyUtil.getPropertyValue("donecontact");
			//String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			//Runtime.getRuntime().exec(renameCmd);
			
			//String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			String moveCmd = "mv "+file+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove enroll xml file" ,e);   
			return false;
		}
		logger.info("Completed renaming and moving a file : "+file);
		return true;
	}

	private boolean validateXmlWithXsd(String xmlfile) {
		logger.info("Started schema validation with file : "+xmlfile);
		try {
			String contactxsd = PropertyUtil.getPropertyValue("updatecontactschema");
			return XMLUtil.validateXMLwithSchema(xmlfile, contactxsd);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			logger.info("Completed schema validation with file : "+xmlfile);
		}
	}

	private boolean inboxFileExist(String inboxFilePathStr) {
		logger.info("Started checking files in inbox folder : "+inboxFilePathStr);
		boolean status = false;
		File file = new File(inboxFilePathStr);
		if(file.exists() && file.isDirectory() && file.list().length > 0){
			status = true;
		}
		logger.info("Completed checking files in inbox folder : "+inboxFilePathStr);
		return status;
	}

}
