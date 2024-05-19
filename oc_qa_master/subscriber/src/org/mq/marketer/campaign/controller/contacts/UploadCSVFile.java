package org.mq.marketer.campaign.controller.contacts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zkplus.spring.SpringUtil;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import au.com.bytecode.opencsv.CSVReader;


@SuppressWarnings("unchecked")
public class UploadCSVFile implements Runnable, ApplicationContextAware{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}


	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}


	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private CustomFieldDataDao cfDataDao;
	private CustomFieldDataDaoForDML cfDataDaoForDML;

	public CustomFieldDataDaoForDML getCfDataDaoForDML() {
		return cfDataDaoForDML;
	}


	public void setCfDataDaoForDML(CustomFieldDataDaoForDML cfDataDaoForDML) {
		this.cfDataDaoForDML = cfDataDaoForDML;
	}


	private MessagesDao messagesDao;
	private MLCustomFieldsDao mlCustomFieldsDao;
	private MLCustomFieldsDaoForDML mlCustomFieldsDaoForDML;

	public MLCustomFieldsDaoForDML getMlCustomFieldsDaoForDML() {
		return mlCustomFieldsDaoForDML;
	}


	public void setMlCustomFieldsDaoForDML(
			MLCustomFieldsDaoForDML mlCustomFieldsDaoForDML) {
		this.mlCustomFieldsDaoForDML = mlCustomFieldsDaoForDML;
	}


	public MailingListDaoForDML getMailingListDaoForDML() {
		return mailingListDaoForDML;
	}


	public void setMailingListDaoForDML(MailingListDaoForDML mailingListDaoForDML) {
		this.mailingListDaoForDML = mailingListDaoForDML;
	}


	private SuppressedContactsDao suppressedContactsDao;
	private SuppressedContactsDaoForDML suppressedContactsDaoForDML;
	public SuppressedContactsDaoForDML getSuppressedContactsDaoForDML() {
		return suppressedContactsDaoForDML;
	}


	public void setSuppressedContactsDaoForDML(
			SuppressedContactsDaoForDML suppressedContactsDaoForDML) {
		this.suppressedContactsDaoForDML = suppressedContactsDaoForDML;
	}


	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	
	
	public SMSSuppressedContactsDao getSmsSuppressedContactsDao() {
		return smsSuppressedContactsDao;
	}


	public void setSmsSuppressedContactsDao(SMSSuppressedContactsDao smsSuppressedContactsDao) {
		this.smsSuppressedContactsDao = smsSuppressedContactsDao;
	}
	private SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;

	public SMSSuppressedContactsDaoForDML getSmsSuppressedContactsDaoForDML() {
		return smsSuppressedContactsDaoForDML;
	}


	public void setSmsSuppressedContactsDaoForDML(
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML) {
		this.smsSuppressedContactsDaoForDML = smsSuppressedContactsDaoForDML;
	}


	public boolean isRunning = false;
	public Queue<Object[]> uploadQueue = new LinkedList();

	Object[] pollObj;
	
	private ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		
		this.context = context;
	}
	

	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	
	public SuppressedContactsDao getSuppressedContactsDao() {
		return suppressedContactsDao;
	}

	public void setSuppressedContactsDao(SuppressedContactsDao suppressedContactsDao) {
		this.suppressedContactsDao = suppressedContactsDao;
	}

	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	public CustomFieldDataDao getCfDataDao() {
		return cfDataDao;
	}

	public void setCfDataDao(CustomFieldDataDao cfDataDao) {
		this.cfDataDao = cfDataDao;
	}

	public MessagesDao getMessagesDao() {
		return messagesDao;
	}

	public void setMessagesDao(MessagesDao messagesDao) {
		this.messagesDao = messagesDao;
	}

	public MLCustomFieldsDao getMlCustomFieldsDao() {
		return mlCustomFieldsDao;
	}

	public void setMlCustomFieldsDao(MLCustomFieldsDao mlCustomFieldsDao) {
		this.mlCustomFieldsDao = mlCustomFieldsDao;
	}

	public void run(){
		try{
			
			logger.debug("---just Entered---");
			
			isRunning = true;
			
			while(pollQueue()) {
				logger.debug("looping ...");
				logger.debug("pollObj length : " + pollObj.length);
				if(pollObj.length == 3) {
					uploadSuppressList();
				} 
				else if(pollObj.length == 4) {
					uploadSuppressSMSList();
					if(logger.isInfoEnabled()) logger.info("Mobile NUmbers successfully");
				}else {
					uploadContacts();
					if(logger.isInfoEnabled()) logger.info("emails stored successfully");
				}
				pollObj = null;
				//System.gc();
			} //while
			
			isRunning = false;
		}catch (Exception ex) {
			logger.error(" ** Exception : "+ ex +" **");
		}
	
	}
	
	private void uploadContacts() {

		if(pollObj==null) {
			return;
		}
		
		Users user =  (Users)pollObj[0];
		boolean isNew = ((Boolean)pollObj[1]).booleanValue();
		Set mailingLists = (Set)pollObj[2];
		String filePath = (String)pollObj[3];
		List fieldList = (List)pollObj[4];
		Map cfMap = (Map)pollObj[5];
		PurgeList purgeList = (PurgeList)pollObj[6];
		Boolean isPurgeContacts = (Boolean)pollObj[7];
		Map genFieldMap = (Map)pollObj[8];
		
		List<Long> listIds = null;
		try {
			
			String userName = user.getUserName();
			
			String key,dataType,cfName,value,defValue ="";
			MLCustomFields mlCustomFields;
			String fileName = filePath.substring(filePath.lastIndexOf('/')+1);
			
			if(logger.isInfoEnabled()) logger.info("File Name : " + fileName );
			if(logger.isDebugEnabled())logger.debug(" isNewML :"+isNew);
			if(isNew){
				MailingList mailingList =(MailingList) mailingLists.iterator().next();
				try{
					mailingListDaoForDML.saveOrUpdate(mailingList);
				}catch(Exception e){
					logger.error(" ** Exception :"+ e +" **");
					String message = "list upload is failed, mailing list can not be created with the name :"+mailingList.getListName();
					(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed",message,"Inbox",false,"INFO", user);
					return ;
				}
				if(cfMap != null){
					Set keys = cfMap.keySet();
					for(Object keyObj:keys){
						try{
							key = (String)keyObj;
							value = (String)cfMap.get(key);
							String[] temp = StringUtils.split(value,":");
							cfName = temp[1];
							dataType = temp[2];
							
							if(temp.length >3)
								defValue = temp[3];
							
							int index = 0;
							if(key.toLowerCase().startsWith("custom field")) {
								index = Integer.parseInt(key.substring("custom field".length()));
							} else {
								index = Integer.parseInt(key.substring(key.length()-1));
							}	
							
							
							logger.info("<<<<<<<<<<<<<<<<<<<<<<< Key"+ key + " index :" + index );
							
							
							if(dataType.equals("Date")) {
								//here only  defValue is DateFormat
								DateFormat dateFormat = new SimpleDateFormat(defValue);
								String defaultDteValue = dateFormat.format(Calendar.getInstance().getTime());
								mlCustomFields = new MLCustomFields(key, index, cfName, dataType, mailingList, defaultDteValue);
								//set the date format
								mlCustomFields.setFormat(defValue);
							}else{
								
								mlCustomFields = new MLCustomFields(key, index, cfName, dataType, mailingList, defValue);
							}
							//mlCustomFieldsDao.saveOrUpdate(mlCustomFields);
							mlCustomFieldsDaoForDML.saveOrUpdate(mlCustomFields);
						}catch(DataIntegrityViolationException die){
							logger.error(" ** Exception :"+ die +" **" );
							removeMlData(mailingList);
							String message = "list upload is failed, Same custom field already selected for the mailinglist :"+mailingList.getListName();
							(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed",message,"Inbox",false,"INFO", user);
							return ;
						}catch(Exception e){
							logger.error(" ** Exception :"+ e +" **" );
							removeMlData(mailingList);
							String message = fileName + ": list upload is failed, Same custom field already selected for the mailinglist :"+mailingList.getListName();
							(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed",message,"Inbox",false,"INFO", user);
							return ;
						}
						
					}
				}
			}
			if(logger.isInfoEnabled()) logger.info("Processing the following file : " + filePath);
			
			listIds = new ArrayList<Long>();
			
			for(Object obj:mailingLists) {
				
				MailingList mailingList = (MailingList)obj;
				listIds.add(mailingList.getListId());
				
				if(logger.isDebugEnabled()) logger.debug("List name : " + mailingList.getListName());
				
				List resultList = null;
				try{
					char delimiter = ',';
					ContactListUploader  cloNew = new ContactListUploader();
					cloNew.setApplicationContext(context);
					resultList = cloNew.uploadContactList(contactsDao,contactsDaoForDML, cfDataDao,cfDataDaoForDML,mailingList,filePath,delimiter,fieldList,cfMap,genFieldMap, user);
				}catch (Exception e) {
					logger.error("** Exception :"+e+" **");
				}
				
				if(resultList != null) {
					if(logger.isDebugEnabled()) logger.debug(" resultList size :"+resultList.size());
					String countsStr = (String)resultList.get(0);
					String[] counts = countsStr.split(":");
					int upload = Integer.parseInt(counts[0]);
					if(upload == 0 && isNew){
						(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed",fileName +" : list uploaded failed, found 0 valid contacts","Inbox",false,"INFO", user);
						return ;
					}else{
						storeMessages(resultList,mailingList.getListName(),fileName,user);
					}
				}
			}
			
		} catch (NumberFormatException e) {
			logger.error(" ** NumberFormatException : "+ e +" **");
		} catch (Exception e) {
			logger.error(" ** Exception : "+ e +" **");
		}
		
		try {
			if(isPurgeContacts && listIds!=null) {
				purgeList.addAndStartPurging(user.getUserId(),listIds);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e);
		}
	}
	
	private void uploadSuppressList() {
		if(logger.isInfoEnabled()) logger.info("--Just Entered--");
		
		if(pollObj==null) {
			return;
		}	
		
		
		CSVReader reader = null;
		try {
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsDaoForDML");
			StringBuffer invalidSB = new StringBuffer();
			List result = new ArrayList();
			String[] nextLine;
			int totLines =0;
			int invalidCount = 0;
			String email = "";
			int uploadedCount = 0;
			SuppressedContacts suppressedContacts=null;
			char delimiter=',';
			
			Users user = (Users)pollObj[0];
			String path = (String)pollObj[1];
			String fileName = (String)pollObj[2];
			logger.debug("user : " + user.getUserName() + " Path : " + path);
			
			reader = new CSVReader(new FileReader(path),delimiter);
			
			StringBuffer emailSB = new StringBuffer();
			int updatedCount  =0 ;
			while ((nextLine = reader.readNext()) != null) {
				totLines++;
				int lineTokenCount = nextLine.length;
				if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
			    	if(logger.isDebugEnabled()) logger.debug("Empty line");
			    	invalidSB.append(totLines + "  \r\n   ");
					invalidCount++;
			    	continue;
			    }
				
				email = nextLine[0];
				logger.debug("Email Name : " + email + " User Id : " + user.getUserId());
				if(!Utility.validateEmail(email.trim())) {
					if(logger.isDebugEnabled()) logger.debug("Invalid Email: " + email);
					invalidSB.append(totLines + "  " + email + "\r\n   ");
					invalidCount++;
					continue;
				}
				
				try {
					
					if(emailSB.length() > 0) emailSB.append(",");
					
					emailSB.append("'"+email+"'");
					updatedCount++;
					
					suppressedContacts = new SuppressedContacts(user, email, Constants.SUPPTYPE_MAP.get("SUPP_TYPE_USERADDED"));
					suppressedContacts.setSuppressedtime(Calendar.getInstance());
					suppressedContactsDaoForDML.saveOrUpdate(suppressedContacts);
					uploadedCount++;
					if(updatedCount >= 100){
						
						contactsDaoForDML.updateEmailStatusByStatus(emailSB.toString(), user.getUserId(), Constants.CONT_STATUS_SUPPRESSED, Constants.CONT_STATUS_ACTIVE);
						updatedCount = 0;
						emailSB = new StringBuffer();
					}
					logger.info("Contact added to suppressed_contacts table...");
					
					logger.info("Contacts status changed to suppressed in contacts table...");
					
					//}		
					
				} catch (Exception e) {
					if(logger.isDebugEnabled()) logger.error("Existing email : " + e.getMessage());
				}
				suppressedContacts = null;
				//if(totLines%1000==0) {  System.gc(); }
			}	
			
			if(updatedCount > 0){
				logger.info("contactsDaoForDML....."+contactsDaoForDML);
	
				contactsDaoForDML.updateEmailStatusByStatus(emailSB.toString(), user.getUserId(), Constants.CONT_STATUS_SUPPRESSED, Constants.CONT_STATUS_ACTIVE);
			}
			result.add(uploadedCount + ":" + invalidCount + ":" + totLines);
			result.add(invalidSB);
			storeMessages(result,null,fileName,user);
			try {
				File f = new File(path);
				f.delete();
				logger.debug("Deleting file, f.exists() is :"+f.exists());
			} catch(Exception e) {
				logger.error("** Exception : Error while Deleting the file .",(Throwable)e);
			}	
		} catch (FileNotFoundException e) {
			if(logger.isDebugEnabled()) logger.debug("CSV file not found : ", (Throwable)e);
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception : ", (Throwable)e);
		} finally{
			try {
				if(reader!=null) reader.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public void removeMlData(MailingList mailingList){
		mlCustomFieldsDao.removeByList(mailingList);
		mailingListDaoForDML.delete(mailingList);
	}
	
	
	/**
	 *  This method is for entering message in captiway
	 *  This method is called for both contacts upload and suppress upload
	 *  for suppress upload listName and fileName will be null
	 *  
	 * 
	 */
	public void storeMessages(List result, String listName, String fileName,Users user){
		if(logger.isDebugEnabled()) logger.debug(" -- just entered --");
		StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
		msgStuff.append(fileName);
		String userName = user.getUserName();
		
		if(result != null && result.get(0)!=null){
			try{
				String countsStr = (String)result.get(0);
				String[] counts = countsStr.split(":");
				int exist=0, invalid=0;
				// For suppress Contacts only.
				if(listName==null){
					try{
						msgStuff.append("\n Status : Suppress list uploaded successfully");
						msgStuff.append("\n Uploaded Emails Count : ");
						msgStuff.append(counts[0]);
						msgStuff.append("\n Invalid Emails Count : ");
						msgStuff.append(counts[1]);
						msgStuff.append("\n Total Count : ");
						msgStuff.append(counts[2]);
						
						invalid = Integer.parseInt(counts[1]);
						if(invalid!=0){
							msgStuff.append("\n Invalid (");	msgStuff.append(invalid);	msgStuff.append("):");
							msgStuff.append("\n    ");
							msgStuff.append(result.get(1)); 
						}
					} catch(Exception e) {
						logger.error("** error --",(Throwable)e);
					}
					(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Suppress List Uploaded successfully",msgStuff.toString(),"Inbox",false,"INFO", user);
					return;
				}
				
				msgStuff.append("\n List name : ");
				msgStuff.append(listName);
				msgStuff.append("\n Status : list uploaded successfully");
				msgStuff.append("\n Total Lines : ");
				msgStuff.append(counts[1]);
				msgStuff.append("\n Uploaded Contacts : ");
				msgStuff.append(counts[0]);
				try{
					exist = Integer.parseInt(counts[2]);
					invalid = Integer.parseInt(counts[3]);
				}catch (Exception e) {
				}
				if(exist!=0){
					msgStuff.append("\n Existed(");	msgStuff.append(exist);	msgStuff.append("):");
					msgStuff.append("\n    "); msgStuff.append(result.get(1)); 
				}
				if(invalid!=0){
					msgStuff.append("\n Invalid (");	msgStuff.append(invalid);	msgStuff.append("):");
					msgStuff.append("\n    "); msgStuff.append(result.get(2)); 
				}
				(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded successfully",msgStuff.toString(),"Inbox",false,"INFO", user);
			}catch(Exception e){
				logger.error("** error --"+e.getMessage()+" **");
				if(listName==null) {
					(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed"," Problem while uploading the Suppress list ","Inbox",false,"INFO", user);	
				} else {
					(new MessageHandler(messagesDao,userName)).sendMessage("Contact","Uploaded failed"," Problem while uploading the contact list ","Inbox",false,"INFO", user);
				}
			}	
	}
}	
	
/*	public MailingList createNewList(String listName,String desc,Boolean checkDoubleOptin,Users user){
		try{
			Calendar cal = MyCalendar.getNewCalendar();
			MailingList mailingList = new MailingList(listName,desc,cal,"Active",cal,cal,true,false,null,user,checkDoubleOptin);
			mailingListDao.saveOrUpdate(mailingList);
			return mailingList;
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
			return null;
		}
	}*/

	
	boolean pollQueue(){
		logger.debug("---1---");
		pollObj = uploadQueue.poll();
		if(pollObj!=null) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	
	private void uploadSuppressSMSList() {
		if(logger.isInfoEnabled()) logger.info("--Just Entered--");
		
		if(pollObj==null) {
			return;
		}	
		
		
		CSVReader reader = null;
		try {
			
			StringBuffer invalidSB = new StringBuffer();
			List result = new ArrayList();
			String[] nextLine;
			int totLines =0;
			int totRecords=0;
			int invalidCount = 0;
			String mobile = "";
			int uploaded = 0;
			SMSSuppressedContacts smsSuppressedContacts=null;
			char delimiter=',';
			String mobileStr = Constants.STRING_NILL;
			//List<String> mobileList = new ArrayList<String>(); 
			Map<String,Boolean> mobileUpdatedOrAddedMap = new HashMap<String,Boolean>();
			String validatedMobile;
			
			List<SMSSuppressedContacts> finalList = new ArrayList<SMSSuppressedContacts>();
			
			Users user = (Users)pollObj[0];
			String path = (String)pollObj[1];
			String fileName = (String)pollObj[2];
			logger.debug("user : " + user.getUserName() + " Path : " + path);
			
			
			String countryCarrier  = user.getCountryCarrier().toString();
			
			reader = new CSVReader(new FileReader(path),delimiter);
			while ((nextLine = reader.readNext()) != null) {
				totLines++;
				int lineTokenCount = nextLine.length;
				if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
			    	if(logger.isDebugEnabled()) logger.debug("Empty line");
			    	invalidSB.append(totLines + "  \r\n   ");
					invalidCount++;
			    	continue;
			    }
				
				mobile = nextLine[0];
				logger.debug("Mobile Number : " + mobile + " User Id : " + user.getUserId());
				/*if(!Utility.validateUserPhoneNum(mobile.trim())) {////////////////////////////////////////////////HERE HERE?????????????????????
					if(logger.isDebugEnabled()) logger.debug("Invalid Number: " + mobile);
					invalidSB.append(totLines + "  " + mobile + "\r\n   ");
					invalidCount++;
					continue;
				}*/
				validatedMobile = Utility.phoneParse(mobile.trim(),user.getUserOrganization());
				
				if(validatedMobile == null){
					if(logger.isDebugEnabled()) logger.debug("Invalid Number: " + mobile);
					invalidSB.append(totLines + "  " + mobile + "\r\n   ");
					invalidCount++;
					continue;
				}
				
				mobile = validatedMobile;
				
				/*
				if(Constants.SMS_COUNTRY_CODE_INDIA.equals(countryCarrier)){
					if(mobile.length() == 12){
						
						mobile = mobile.substring(2); // removing 91
					}
				}else if(Constants.SMS_COUNTRY_CODE_PAKISTAN.equals(countryCarrier)){
					if(mobile.length() == 12){
						mobile = mobile.substring(2); // removing 92
					}
				}else if(Constants.SMS_COUNTRY_CODE_UAE.equals(countryCarrier)){
					if(mobile.length() == 10 || mobile.length() == 11 || mobile.length() == 12){ //UAE phone numbers can be of 7,8,9 digits without carrier
						mobile = mobile.substring(3); // removing 971
					}
				}else if(Constants.SMS_COUNTRY_CODE_US.equals(countryCarrier)){
					
				}*/
				
				
				if(mobile.startsWith(user.getCountryCarrier()+Constants.STRING_NILL) && 
						mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {
					
					//logger.info("mobile====="+mobile+" mobile.length()===="+mobile.length());
					//logger.info("user.getCountryCarrier().length()======"+(user.getCountryCarrier()+"").length());
					
					//mobile = mobile.substring(user.getCountryCarrier()+"".length() - 1);
					mobile = mobile.substring((user.getCountryCarrier()+"").length());
					
					
				}
				
				
				

				if(!mobileStr.isEmpty()) 
					mobileStr += "|";
				
				mobileStr += mobile.trim();
				mobileUpdatedOrAddedMap.put(mobile, false);
				//mobileList.add(mobile);
				
				/*try {
					smsSuppressedContacts = new SMSSuppressedContacts(user, mobile, Constants.SMS_SUPP_TYPE_USERADDED);
					smsSuppressedContacts.setSuppressedtime(Calendar.getInstance());
					suppressedContactsDao.saveOrUpdate(smsSuppressedContacts);
					uploaded++;
				} catch (Exception e) {
					if(logger.isDebugEnabled()) logger.error("Existing Number : " + e.getMessage());
				}
				smsSuppressedContacts = null;
				if(totLines%1000==0) {  System.gc(); }*/
			}	
			
			//logger.info("mobileStr======="+mobileStr);
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsByMultipleMobiles(user.getUserId(), mobileStr);
			
			//logger.info("retList.size()======="+retList.size());
			//System.out.println(mobileUpdatedOrAddedMap.keySet());
			boolean withCountryCarrier = false;
			if(retList != null && retList.size() > 0 ){
				for(SMSSuppressedContacts aSmsSuppressedContact : retList){
					String suppMobile = aSmsSuppressedContact.getMobile();
					withCountryCarrier = false;
					//logger.info("suppMobile>>>>>>>1>>>>"+suppMobile);
					if(suppMobile.startsWith(user.getCountryCarrier()+Constants.STRING_NILL) ) {
						
						//suppMobile = suppMobile.substring(user.getCountryCarrier()+"".length() - 1);
						suppMobile = suppMobile.substring((user.getCountryCarrier()+"").length());
						withCountryCarrier = true;
						//logger.info("suppMobile>>>>>>>2>>>>"+suppMobile);
						
					}
					
					if(mobileUpdatedOrAddedMap.keySet().contains(suppMobile)){
						
						//prefixing with country carrier
						//aSmsSuppressedContact.setMobile(user.getCountryCarrier()+Constants.STRING_NILL+suppMobile);
						//mobileUpdatedOrAddedMap.put(aSmsSuppressedContact.getMobile(), true);
						if(withCountryCarrier == false){
							mobileUpdatedOrAddedMap.put(suppMobile, false);
							aSmsSuppressedContact.setMobile(user.getCountryCarrier()+Constants.STRING_NILL+suppMobile);
							finalList.add(aSmsSuppressedContact);
							//logger.info("suppMobile>>>>>>>3>>>>"+suppMobile);
						}
						else{
							
							mobileUpdatedOrAddedMap.put(suppMobile, true);
							//logger.info("suppMobile>>>>>>>4>>>>"+suppMobile);
						}
						
					} 
					
				}
			}
			
			
			for(String mobileNumber : mobileUpdatedOrAddedMap.keySet()){
				//logger.info("suppMobile>>>>>>>5>>>>"+mobileNumber);
				if(mobileUpdatedOrAddedMap.get(mobileNumber) == false){// i.e. new mobile number
					
					
					//logger.info("suppMobile>>>>>>>6>>>>"+mobileNumber);
					totRecords++;
					smsSuppressedContacts = new SMSSuppressedContacts();
					
					smsSuppressedContacts.setMobile(user.getCountryCarrier()+Constants.STRING_NILL+mobileNumber);
					
					smsSuppressedContacts.setUser(user);
					smsSuppressedContacts.setType(Constants.SMS_SUPP_TYPE_USERADDED);
					smsSuppressedContacts.setSuppressedtime(Calendar.getInstance());
					
					
					
					//suppressedContactsDao.saveOrUpdate(smsSuppressedContacts);
					
					finalList.add(smsSuppressedContacts);
					uploaded++;
					
					//smsSuppressedContacts = null;
					
					//if(totRecords%1000==0) {  System.gc(); }
				}
				
					 //APP-2557
					contactsDaoForDML.updatemobileStatus (mobileNumber,Constants.CONT_STATUS_SUPPRESSED, user);
				
			}
			
			//smsSuppressedContactsDao.saveByCollection(finalList);
			smsSuppressedContactsDaoForDML.saveByCollection(finalList);
			result.add(uploaded + ":" + invalidCount + ":" + totLines);
			result.add(invalidSB);
			storeMessages(result,null,fileName,user);
			try {
				File f = new File(path);
				f.delete();
				logger.debug("Deleting file, f.exists() is :"+f.exists());
			} catch(Exception e) {
				logger.error("** Exception : Error while Deleting the file .",(Throwable)e);
			}	
		} catch (FileNotFoundException e) {
			if(logger.isDebugEnabled()) logger.debug("CSV file not found : ", (Throwable)e);
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception : ", (Throwable)e);
		} finally{
			try {
				if(reader!=null) reader.close();
			} catch (Exception e) {
			}
		}
	}
	

}
