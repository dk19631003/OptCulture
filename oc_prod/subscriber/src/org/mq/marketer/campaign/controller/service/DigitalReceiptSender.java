package  org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.ReplacePlaceholderFromStrConetnt;
import org.mq.optculture.exception.BaseDAOException;


public class DigitalReceiptSender extends Thread {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String serverName = PropertyUtil.getPropertyValue("schedulerIp");
	private String subject;
	private String htmlContent;
	//private String status;
	//private String type;
	private String toEmailId;
//	private Date sentDate;
	private Users user;
	private String jsonPhValStr;  
	private String templateName;
	long drJsonObjId;
	
	
	String messageHeader ="";
	String prsnlizeToFld="";
	
	private UsersDao usersDao;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private MessagesDao messagesDao;
	private DRSentDao drSentDao;
	private DRSentDaoForDML drSentDaoForDML;
	private ContactsDao contactsDao;
	private OrganizationStoresDao organizationStoresDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private RetailProSalesDao retailProSalesDao;
	private POSMappingDao posMappingDao;
	private JSONObject jsonMainObj;
	
	
	public DigitalReceiptSender(String subject,String htmlContent, String toEmailId, 
			Users user, UsersDao usersDao,  MessagesDao messagesDao, DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao,
			ContactsDao contactsDao,OrganizationStoresDao organizationStoresDao,ContactsLoyaltyDao contactsLoyaltyDao,RetailProSalesDao retailProSalesDao,POSMappingDao posMappingDao,JSONObject jsonMainObj,DRSentDao drSentDao,long drJsonObjId, String templateName)  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        this.usersDao = usersDao;
        this.messagesDao = messagesDao;
        this.digitalReceiptUserSettingsDao= digitalReceiptUserSettingsDao;
        this.contactsDao=contactsDao;
        this.organizationStoresDao=organizationStoresDao;
        this.contactsLoyaltyDao = contactsLoyaltyDao;
        this.retailProSalesDao = retailProSalesDao;
        this.posMappingDao = posMappingDao;
        this.jsonMainObj =jsonMainObj;
        this.drSentDao = drSentDao;
        this.drSentDaoForDML=drSentDaoForDML;
        this.drJsonObjId = drJsonObjId;
        this.templateName=templateName;
		
	}
	/*public DigitalReceiptSender(String subject,String htmlContent, String toEmailId, 
			Users user, UsersDao usersDao, DRSentDao drSentDao, MessagesDao messagesDao, DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao  )  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        this.usersDao = usersDao;
        this.drSentDao = drSentDao;
        this.messagesDao = messagesDao;
        this.digitalReceiptUserSettingsDao= digitalReceiptUserSettingsDao;
	}
	
	public DigitalReceiptSender(String subject,String htmlContent, String toEmailId, 
			Users user, UsersDao usersDao, DRSentDao drSentDao, MessagesDao messagesDao, DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao ,String jsonPhValStr )  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        this.usersDao = usersDao;
        this.drSentDao = drSentDao;
        this.messagesDao = messagesDao;
        this.digitalReceiptUserSettingsDao= digitalReceiptUserSettingsDao;
		this.jsonPhValStr = jsonPhValStr;
	}*/
	
	/*private void grabConnection() {
	
		final String server = PropertyUtil.getPropertyValueFromDB("SMTPHost");
		final String smtpUN = PropertyUtil.getPropertyValueFromDB("SMTPUserName");
		final String smtpPW = PropertyUtil.getPropertyValueFromDB("SMTPPassword");
		
		int smtpPort = 25;
		try {
			smtpPort = Integer.parseInt(PropertyUtil.getPropertyValueFromDB("SMTPPort"));
		} catch (NumberFormatException e1) {
			smtpPort = 25;
		}
		try {
			con = new Connection(server, smtpPort, smtpUN, smtpPW);
		} 
		catch (ServiceException se) {
			isRunning = false;
            logger.error("SMTP error while connecting to the server"+server, se);
            return;
        } 
        catch (IOException ioe) {
        	isRunning = false;
            logger.error("SMTP error while connecting to the server"+server, ioe);
            return;
        }

	}
*/
	
	public void run() {

		
			String textContent = htmlContent; 
		    String nonDynamicFrmName="";
		    String nonDynamicFrmEmail="";
			String from = user.getEmailId();
			String replyTo = user.getEmailId();
			logger.info("from1======"+from);
			String jobId = Constants.EQ_TYPE_DIGITALRECIEPT;
			
				
				
				nonDynamicFrmEmail = from;
				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
				if(digitalReceiptUserSettings != null) {
					
					//subject
					String recieptSub = digitalReceiptUserSettings.getSubject();
					if(recieptSub != null &&
							recieptSub.trim().length() > 0) {
						
						subject = recieptSub;
					}
					
					//from email
					String recieptEmail = digitalReceiptUserSettings.getFromEmail();
					if(recieptEmail != null && 
							recieptEmail.trim().length() > 0) {
						
						nonDynamicFrmEmail = from = recieptEmail;
						replyTo = recieptEmail;
					}
					
					logger.info("from2======"+from);
					//fromName
					String recieptFromName = digitalReceiptUserSettings.getFromName();
					nonDynamicFrmName = recieptFromName;
					if(recieptFromName != null && 
							recieptFromName.trim().length() > 0) {
						
						nonDynamicFrmEmail = from = recieptFromName + "<" + from + ">";
						//to = recieptFromName + "<" + to + ">";
					}
					logger.info("from3======"+from);
					
					//set personalize field
					/*if(digitalReceiptUserSettings.isPersonalizeTo() == true &&
							digitalReceiptUserSettings.getToName() != null && 
									digitalReceiptUserSettings.getToName().trim().length() > 0)	 {
						Long contactId = emailQueue.getContactId();
						
						if(contactId != null) {
							
							logger.info("no contact has found...");
							ContactsDao contactsDao = (ContactsDao)context.getBean("contactsDao");
							
							contact = contactsDao.findById(contactId);
							if(contact != null) {
								
								if(custTemplate.getToName().equals("firstName")){
									
									prsnlizeToFld =  contact.getFirstName()==null?"":contact.getFirstName();
									
								}else if(custTemplate.getToName().equals("lastName")){
									
									prsnlizeToFld =  contact.getLastName()==null?"":contact.getLastName();
								}else if(custTemplate.getToName().equals("fullName")) {
									
									prsnlizeToFld = contact.getFirstName()==null?"":contact.getFirstName();
									prsnlizeToFld += contact.getLastName()==null?"":" "+contact.getLastName();
									
								}
							}
						}//if contact not null
						*/
						
					}
				
				//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  STARTS
				List<OrganizationStores> organizationStoresList = organizationStoresDao.findByOrganization(user.getUserOrganization().getUserOrgId());
				JSONObject receiptJSONObj = (JSONObject)jsonMainObj.get("Receipt");
				Object storeJsonObj = receiptJSONObj.get("Store");
				String store=null;
				if(storeJsonObj != null && !storeJsonObj.toString().trim().isEmpty()){
					store = storeJsonObj.toString().trim();
				}
				
				OrganizationStores requiredOrganizationStore = null;
				for(OrganizationStores anOrganizationStore : organizationStoresList){
					if(anOrganizationStore.getHomeStoreId() != null && anOrganizationStore.getHomeStoreId().equals(store)){
						requiredOrganizationStore = anOrganizationStore;
					}
				}
				
				if(requiredOrganizationStore != null ){
					
					if(requiredOrganizationStore.getFromEmailId() != null){
						
						if(digitalReceiptUserSettings.isIncludeDynamicFrmEmail())
								from = requiredOrganizationStore.getFromEmailId();
						else
							    from = nonDynamicFrmEmail;
					}else if(requiredOrganizationStore.getFromEmailId() == null){
						//keep previous email id
					}
						
					String tempEmail;
					if(requiredOrganizationStore.getFromName() != null){
						
						if(digitalReceiptUserSettings.isIncludeDynamicFrmName()){
							
							
							if(from.contains("<")){
								tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
							}else{
								tempEmail = from;
							}
							from = requiredOrganizationStore.getFromName() +"<"+ tempEmail+">";
							
						}
						else{
							
							if(from.contains("<")){
								tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
							}else{
								tempEmail = from;
							}
							
							if(nonDynamicFrmName != null){
								from = nonDynamicFrmName +"<"+ tempEmail+">";
							}else{
								from = tempEmail;
							}
								
						}
					}else if(requiredOrganizationStore.getFromName() == null){
						
						if(nonDynamicFrmName != null){
							
							if(from.contains("<")){
								tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
							}else{
								tempEmail = from;
							}
							
							from = nonDynamicFrmName + "<"+ tempEmail+">";
						}
						
					}
						
					if(requiredOrganizationStore.getReplyToEmailId() != null)
						replyTo = requiredOrganizationStore.getReplyToEmailId();
					
				}
				logger.info("BillToEmail::::toEmailId==="+toEmailId+"  replyTo====="+replyTo+"  from4==="+from+"   user.user.getUserId()==="+user.getUserId());
				//toEmailId = to;//--------------------------------2.4.3   replyTo
				//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  ENDS
				
				
				
				//Replace Place Holders from htmlContent 
				List<Object> daoObjList = new ArrayList<Object>();
				daoObjList.add(contactsDao);
				daoObjList.add(usersDao);
				daoObjList.add(organizationStoresDao);
				daoObjList.add(contactsLoyaltyDao);
				daoObjList.add(retailProSalesDao);
				daoObjList.add(posMappingDao);
				Object[] obj1 = { htmlContent, jsonMainObj, user, daoObjList };
				
				JSONObject receiptObj = (JSONObject)jsonMainObj.get(Constants.DR_RECEIPT_OBJ);
				
				
			//	String docSid = (String)receiptObj.get(Constants.DR_DOCSID);
						
				String[] docsidArr = POSFieldsEnum.docSid.getDrKeyField().split(Constants.DELIMETER_DOUBLECOLON);
				String[] storeNumberArr = POSFieldsEnum.storeNumber.getDrKeyField().split(Constants.DELIMETER_DOUBLECOLON);
				
				String docSid =  docsidArr[1];
				
				String docSidStr =(String)receiptObj.get(docSid);
				String storeNumberStr = (String)receiptObj.get(storeNumberArr[1]);
				DRSent drSent = null;
				boolean isSameDocSid = false;
				
				try {
					if(docSidStr != null ){
						drSent = drSentDao.findBy(user.getUserId(), docSidStr, toEmailId);
						if(drSent == null) {
							
							drSent = drSentDao.findBy(user.getUserId(), docSidStr);
							isSameDocSid = (drSent != null);
						}
					}
				} catch (Exception e) {
					logger.error("Exception ",e );
				}
				//TODO RE-PREPARE
				ReplacePlaceholderFromStrConetnt replacePlaceHoderClass = new ReplacePlaceholderFromStrConetnt();
				Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj1, drSent);
				StringBuffer phKeyValStr = null;
				if(obj2[0] != null){
					
					htmlContent =(String)obj2[0];
					textContent = htmlContent;
				}
				if(obj2[1] != null){
					
					phKeyValStr= (StringBuffer) obj2[1];
				}
				logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
				String phValStr =null;
				if(phKeyValStr != null && phKeyValStr.length() > 0){
					
					 phValStr = phKeyValStr.toString();
				}
				boolean updateCount = (drSent != null && !isSameDocSid);		
				if((drSent == null && !isSameDocSid) ||(drSent != null && isSameDocSid)) {
					 
					/*drSent = new DRSent(Constants.DR_STATUS_SUCCESS, toEmailId, Calendar.getInstance(),
							user.getUserId(), drJsonObjId);*/
					drSent = new DRSent(Constants.DR_STATUS_SUBMITTED, toEmailId, Calendar.getInstance(),
							user.getUserId(), drJsonObjId);
					
					 drSent.setDocSid(docSidStr);
					 drSent.setSentCount(1);
				 }
				drSent.setSubject(subject);
				 drSent.setHtmlStr(htmlContent);
				 drSent.setPhValStr(phValStr);
				 drSent.setTemplateName(templateName);
				 if(storeNumberStr != null && !storeNumberStr.trim().isEmpty())drSent.setStoreNumber(storeNumberStr);
			//	DRSent drSent = new DRSent(subject, digitalReceiptUserSettings.getId(), "Sent", toEmailId,Calendar.getInstance(), user.getUserId());
				/* if(drSent == null) {
					 drSent = new DRSent(subject,phValStr, Constants.DR_STATUS_SUCCESS, toEmailId,Calendar.getInstance(), user.getUserId(),drJsonObjId,templateName);
					 drSent.setDocSid(docSidStr);
					 drSent.setSentCount(1);
				 }
				 drSent.setHtmlStr(htmlContent);
				 drSent.setPhValStr(phValStr);*/
				 
			//	DRSent drSent = new DRSent(subject, htmlContent, "Sent", toEmailId,Calendar.getInstance(), user.getUserId(),jsonPhValStr);
			
				
				

			/*	ReplacePlaceholderFromStrConetnt replacePlaceHoderClass = new ReplacePlaceholderFromStrConetnt();
				Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj1);
				StringBuffer phKeyValStr = null;
				if(obj2[0] != null){
					
					htmlContent =(String)obj2[0];
					textContent = htmlContent;
				}
				if(obj2[1] != null){
					
					phKeyValStr= (StringBuffer) obj2[1];
				}
				logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
				String phValStr =null;
				if(phKeyValStr != null && phKeyValStr.length() > 0){
					
					 phValStr =phKeyValStr.toString();
				}
								
				
			//	DRSent drSent = new DRSent(subject, digitalReceiptUserSettings.getId(), "Sent", toEmailId,Calendar.getInstance(), user.getUserId());
				 drSent = new DRSent(subject,phValStr, Constants.DR_STATUS_SUCCESS, toEmailId,Calendar.getInstance(), user.getUserId(),drJsonObjId,templateName);*/
			//	DRSent drSent = new DRSent(subject, htmlContent, "Sent", toEmailId,Calendar.getInstance(), user.getUserId(),jsonPhValStr);
				//drSentDao.saveOrUpdate(drSent);
				drSentDaoForDML.saveOrUpdate(drSent);
				if(updateCount) {
					
					
					try {
						//drSentDao.updateSentCount(drSent.getId());
						drSentDaoForDML.updateSentCount(drSent.getId());
					} catch (BaseDAOException e) {
						// TODO Auto-generated catch block
					logger.error("Exception while updating the sent count", e);
					}
				}
				/*EmailQueue emailQueue = new EmailQueue(subject, htmlContent,
						"DigitalReciept", "Sent", toEmailId,Calendar.getInstance(), user);
		    	emailQueueDao.saveOrUpdate(emailQueue);
		    	emailQueue.setStatus(Constants.EQ_STATUS_SENT);*/
				
				//	drSent.setStatus(Constants.DR_STATUS_SUCCESS);
					
				/*
				try {
					//drSentDao.saveOrUpdate(drSent);
					//emailQueueDao.saveOrUpdate(emailQueue);
					//emailQueueDao.saveByCollection(tempEmailQueList);
					
				} 
				catch (Exception e) {
					logger.error(" ** Exception : while updating the status of EmailQueue" +
							" after scheduling", e);
				}*/
				
				logger.info("html conetent is::"+htmlContent);
				//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
				 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
					try {
						 

						if(user != null) {
							messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
						}
						logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
						ExternalSMTPDigiReceiptSender externalSMTPSender =  new ExternalSMTPDigiReceiptSender();
						htmlContent = htmlContent.replace("[sentId]", drSent.getId()+"");
						textContent = textContent.replace("[sentId]", drSent.getId()+"");
						//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
						externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, toEmailId, replyTo);
					
					} catch (Exception e) {
						
						logger.debug("Exception while sending through sendGridAPI .. returning ",e);
						
					}
				}
					
					
			}
				
		
	}


