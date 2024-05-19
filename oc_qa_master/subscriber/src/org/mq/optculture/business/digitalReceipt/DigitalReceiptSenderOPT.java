package  org.mq.optculture.business.digitalReceipt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.service.ExternalSMTPDigiReceiptSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.ReplacePlaceholderFromStrConetnt;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


public class DigitalReceiptSenderOPT extends Thread{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String serverName = PropertyUtil.getPropertyValue("schedulerIp");
	private final String DR_PDF_URL=PropertyUtil.getPropertyValue("DRPdfGenerationUrl");
	 private final String DR_PDF_DIV_TEMPLATE = PropertyUtil.getPropertyValue("DRPDFdivTemplate");
	private String subject;
	private String htmlContent;
	private boolean isTaxExists =false, isBillTo =false, isShipTo =false;
	//private String status;
	//private String type;
	private String toEmailId;
//	private Date sentDate;
	private Users user;
	private String jsonPhValStr;  
	private String templateName;
	long drJsonObjId;
	
	String editorType;
	String messageHeader ="";
	String prsnlizeToFld="";
	
	/*private UsersDao usersDao;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private MessagesDao messagesDao;
	private DRSentDao drSentDao;
	private ContactsDao contactsDao;
	private OrganizationStoresDao organizationStoresDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private RetailProSalesDao retailProSalesDao;
	private POSMappingDao posMappingDao;*/
	private JSONObject jsonMainObj;
	
	private DigitalReceiptUserSettings digitalReceiptUserSettings;
	
	
	
	public DigitalReceiptSenderOPT(String subject,String htmlContent, String toEmailId,String editorType, 
			Users user, JSONObject jsonMainObj, long drJsonObjId, DigitalReceiptUserSettings digitalReceiptUserSettings)  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        /*this.usersDao = usersDao;
        this.messagesDao = messagesDao;
        this.digitalReceiptUserSettingsDao= digitalReceiptUserSettingsDao;
        this.contactsDao=contactsDao;
        this.organizationStoresDao=organizationStoresDao;
        this.contactsLoyaltyDao = contactsLoyaltyDao;
        this.retailProSalesDao = retailProSalesDao;
        this.posMappingDao = posMappingDao;
        this.jsonMainObj =jsonMainObj;
        this.drSentDao = drSentDao;*/
        this.jsonMainObj =jsonMainObj;
        this.drJsonObjId = drJsonObjId;
        this.templateName=digitalReceiptUserSettings.getSelectedTemplateName();
       
        this.digitalReceiptUserSettings = digitalReceiptUserSettings;
        this.editorType=editorType;
       	}
	
	/*public DigitalReceiptSender(String subject,String htmlContent, String toEmailId, 
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
        this.drJsonObjId = drJsonObjId;
        this.templateName=templateName;
		
	}*/
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
		processDR();
	}
				
	
		public void processDR() {
			

			
			
			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = null;
			DRSentDao drSentDao = null;
			DRSentDaoForDML drSentDaoForDML = null;
			OrganizationStoresDao organizationStoresDao = null;
			
			try{
				digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				drSentDao = (DRSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
				drSentDaoForDML = (DRSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SENT_DAO_ForDML);
				organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			}
			catch(Exception e){
				logger.error("Exception while getting daos...");
			}

			
				
				String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting"); 
				Map<String, String> userJSONSettingHM  = null;
				try{
				userJSONSettingHM = getUserJsonSettingMap(userJSONSettings); 
				
				
				String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentLoopPlaceHolders"); 
				String itemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemLoopPlaceHolders");
		    	
		    	String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
				String[] itemsSetPlaceHoldersArr = itemsSetPlaceHolders.split(",");
				
				
				String billToSetPlaceHolders = PropertyUtil.getPropertyValue("DRBillToLoopPlaceHolders");
				String shipToSetPlaceHolders = PropertyUtil.getPropertyValue("DRShipToLoopPlaceHolders");
				
				String[] billToSetPlaceHoldersArr = billToSetPlaceHolders.split(",");
				String[] shipToSetPlaceHoldersArr = shipToSetPlaceHolders.split(",");
				
				
				/*JSONObject receiptJSONObj = (JSONObject)jsonBodyObj.get("Receipt");
				if(receiptJSONObj == null) {
					logger.debug("**** Exception : Receipt Object Not found ***");
					return null;
				}*/
				
				
				boolean soTest = testForSONumber(htmlContent, itemsSetPlaceHoldersArr, userJSONSettingHM, 
						jsonMainObj, paymentSetPlaceHoldersArr);
				
				htmlContent = replaceReceiptPhValues(htmlContent, itemsSetPlaceHoldersArr, userJSONSettingHM, 
						jsonMainObj, paymentSetPlaceHoldersArr,user);
				
				/**
				 *BillTo_ShipTo Saved rows
				 *APP-1224
				 */
				htmlContent = replaceBillTo_ShipToPhValues(htmlContent, userJSONSettingHM, jsonMainObj, billToSetPlaceHoldersArr, shipToSetPlaceHoldersArr);
				htmlContent = htmlContent.replace(billToSetPlaceHoldersArr[0], "").replace(billToSetPlaceHoldersArr[1],"");
				htmlContent = htmlContent.replace(shipToSetPlaceHoldersArr[0], "").replace(shipToSetPlaceHoldersArr[1],"");
				
				htmlContent = htmlContent.replace(paymentSetPlaceHoldersArr[0], "").replace(paymentSetPlaceHoldersArr[4],"");
				htmlContent = htmlContent.replace(itemsSetPlaceHoldersArr[0], "").replace(itemsSetPlaceHoldersArr[5],"");
				}catch(Exception e){
					logger.error("Exception :: ",e);
					return ;
				}
				// Remove ##START## ##END## PH from the template
				
				
		    	
				// Replace All individual place holders place Holders
				htmlContent = replaceIndividualPhValues(userJSONSettingHM, htmlContent, jsonMainObj);
				
				htmlContent = replaceExisting_ExtPrcWithTax_IfAny(jsonMainObj, htmlContent);
				htmlContent = PrepareFinalHtml.replaceImgURL(htmlContent, user.getUserName());
				
				// remove all no-value place holders form template
				htmlContent = removeNoValuePhValues(htmlContent);
				
				
				htmlContent = addDomainNameToImageURLs(htmlContent);
				htmlContent = htmlContent.replace("[TAX_VISIBLE]", isTaxExists ? "" : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				
				htmlContent = htmlContent.replace("[BILLTO_VISIBLE]", isBillTo ? "" : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[SHIPTO_VISIBLE]", isShipTo ? "" : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				
				//Get email from json request..
				//JSONObject receiptObj = (JSONObject)jsonBodyObj.get("Receipt");
				//DRReceipt drReceipt = drJsonRequest.getBody().getReceipt();
				
				
				//preparing html content which handles clicks
				htmlContent = PrepareFinalHtml.prepareStuff(htmlContent,editorType);
				
				
				String textContent = htmlContent; 
				
			
				String from = user.getEmailId();
				String jobId = Constants.EQ_TYPE_DIGITALRECIEPT;
				
				
				String nonDynamicFrmName="";
			    String nonDynamicFrmEmail="";
				String replyTo = user.getEmailId();
				
				String nonDynamicReplyToEmail=replyTo;
				
				logger.info("from1======"+from);
					//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
					
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
							//replyTo = recieptEmail;
						}
						logger.info("from2======"+from);
						//fromName
						
						String recieptFromName = digitalReceiptUserSettings.getFromName();
						nonDynamicFrmName = recieptFromName;
						if(recieptFromName != null && 
								recieptFromName.trim().length() > 0) {
							
							nonDynamicFrmEmail = from = recieptFromName + "<" + from + ">";
						}
						logger.info("from3======"+from);
						
						
						//ReplyTo email
						String recieptReplyToEmail = digitalReceiptUserSettings.getReplyToEmail();
						
						if(recieptReplyToEmail != null && 
								recieptReplyToEmail.trim().length() > 0) {
							
							nonDynamicReplyToEmail = recieptReplyToEmail;
							replyTo = recieptReplyToEmail;
						}
						logger.info("replyTo2======"+replyTo);
						//Reply To Name
						
													
						
						//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  STARTS
						try {
							Long userDomainID = null;
							if(digitalReceiptUserSettings.isIncludeDynamicFrmEmail()){
								UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
								List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());//finding this power user's domain
								if(domainsList != null && !domainsList.isEmpty()) userDomainID = domainsList.get(0).getDomainId();
							}
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
									if(userDomainID != null && digitalReceiptUserSettings.isIncludeDynamicFrmEmail() && anOrganizationStore.getDomainId() != null && 
											anOrganizationStore.getDomainId().longValue() == userDomainID.longValue()){
										requiredOrganizationStore = anOrganizationStore;
										break;
									}
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
									
								/*if(requiredOrganizationStore.getReplyToEmailId() != null)
									replyTo = requiredOrganizationStore.getReplyToEmailId();
								*/
								if(requiredOrganizationStore.getReplyToEmailId() != null)
								{
									if(digitalReceiptUserSettings.isIncludeDynamicReplyToEmail()) {
										replyTo = requiredOrganizationStore.getReplyToEmailId();;
									}else
										replyTo = nonDynamicReplyToEmail;
								}
								
							}
						} catch (Exception e) {
							logger.info("Exception>>>>>>>>>>>>>>"+e);
						}
						
						logger.info("BillToEmail::::toEmailId==="+toEmailId+"  replyTo====="+replyTo+"  from4==="+from+"   user.user.getUserId()==="+user.getUserId());
						//toEmailId = to;//--------------------------------2.4.3   replyTo
						//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  ENDS
						
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
					
					//Replace Place Holders from htmlContent 
					/*List<Object> daoObjList = new ArrayList<Object>();
					daoObjList.add(contactsDao);
					daoObjList.add(usersDao);
					daoObjList.add(organizationStoresDao);
					daoObjList.add(contactsLoyaltyDao);
					daoObjList.add(retailProSalesDao);
					daoObjList.add(posMappingDao);*/
					//Object[] obj1 = { htmlContent, jsonMainObj, user, daoObjList };
					Object[] obj1 = { htmlContent, jsonMainObj, user };
					// Commented after REsending DR

					/*ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
					Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj1);
					StringBuffer phKeyValStr = null;
					if(obj2[0] != null){
						
						htmlContent =(String)obj2[0];
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
					DRSent drSent = new DRSent(subject,phValStr, Constants.DR_STATUS_SUCCESS, toEmailId,Calendar.getInstance(), user.getUserId(),drJsonObjId,templateName);
				//	DRSent drSent = new DRSent(subject, htmlContent, "Sent", toEmailId,Calendar.getInstance(), user.getUserId(),jsonPhValStr);
					drSentDao.saveOrUpdate(drSent);*/
					
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
						ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
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
							
							 phValStr =phKeyValStr.toString();
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
						 /*StringBuffer sb = new StringBuffer();
						 String pdflinkUrl = DR_PDF_URL;
						 pdflinkUrl = pdflinkUrl.replace("|^", "[").replace("^|", "]");
						 String pdfUrl = DR_PDF_DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + pdflinkUrl + "'> click here</a>");
						 Document doc1 = Jsoup.parse(htmlContent);
						 Element bodyelements = doc1.select("body").first().prepend(pdfUrl);;
						 String bodyStr = bodyelements.toString();	
						 sb = new StringBuffer(bodyStr);
						 htmlContent = sb.toString();*/
						 drSent.setHtmlStr(htmlContent);
						 drSent.setPhValStr(phValStr);
						 drSent.setTemplateName(templateName);
						 if(storeNumberStr != null && !storeNumberStr.trim().isEmpty())drSent.setStoreNumber(storeNumberStr);
						
						 /**new parameter in drsent table for the sack of drReports
						  * */
						 drSent.setMyTemplateId(digitalReceiptUserSettings!=null ? digitalReceiptUserSettings.getMyTemplateId() : null);
						 drSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
						 

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
							logger.error("Exception while updating the sent count", e);
							}
						}
					
					
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
							drSent.setHtmlStr(htmlContent);
							drSentDaoForDML.saveOrUpdate(drSent);
							//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
							externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, toEmailId, replyTo);
						
						} catch (Exception e) {
							
							logger.debug("Exception while sending through sendGridAPI .. returning ",e);
							
						}
					}
						
						
				
		}
		
		
		private String GetMergedPaymentPart(String paymentPart, String number, String type, String Amount )
	    {
	        String paymentPartStr = paymentPart.replace("#Payment.Amount#",  Amount )
	                .replace("#Payment.Number#", number)
	                .replace("#Payment.Type#", type);
	        return paymentPartStr;
	    }
		
		
		private String replaceLoopBlock(String loopBlock, Map<String,String> userJSONSettingHM,JSONObject jsonMainObj) {
			try {
				Map<String,String> extPrcWithTaxForItemsMap = new HashMap<String, String>();
				Map<String,String> discWithNoDecItemsMap = new HashMap<String, String>();
				String finalHtmlBlockVal = "";
				Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(loopBlock);
				
				//JSONObject jsonObj = null;

				JSONArray jsonArr = null;
				 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00"); 
				
				if(matcher.find()) {
					
					logger.info("--PLACE HOLDERS FOUND --"+ matcher.group(0));
					logger.info("--1--"+ userJSONSettingHM.get(matcher.group(0)));
					
					if(userJSONSettingHM.get(matcher.group(0)) == null) {
			//			
						logger.info("*** Place holder value is not found in HashMap ***");
						return null;
					}
					
					String[] arrayElement = userJSONSettingHM.get(matcher.group(0)).split(Pattern.quote("."));
					
					// Items from Items.billTomail
					logger.info("arrayElement[0]" + arrayElement[0]);
					jsonArr  = (JSONArray)jsonMainObj.get(arrayElement[0]);
					
				} else {
					
					logger.info("Pattern not found");
					return null;
				}
				
				matcher.reset();
				
				if(jsonArr == null) {
					return null;
				}
				
			//		logger.info("Array size :" + jsonArr.size());
				
				for(int i = 0; i < jsonArr.size(); i++)   // Loop all array elements
				{	
					
					JSONObject jsonObj = (JSONObject)jsonArr.get(i);
					String tempStr = loopBlock;
					//logger.info(" Current jsonobj object from the jsonarray is :"+ jsonObj);
				
					while (matcher.find()) {			// Loop all fields
						String val = "";
						
						String extPrcWithTax_ExtPrc_JsonKey = "";
						String extPrcWithTax_Tax_JsonKey = "";
						boolean processExtPrcWithTax = false;
						String ItemDocItemDisc_JsonKey = "";
						boolean processdiscPercNDC = false;

						
						
						
						String placeHolderStr = matcher.group(0);
						//logger.info(" Found place holders : " +placeHolderStr);
						
						try{
							if("#Item.extPrcWithTax#".equals(placeHolderStr)){
								
								extPrcWithTax_ExtPrc_JsonKey = "Items.ExtPrc";
								
								extPrcWithTax_Tax_JsonKey = "Items.Tax";
								
								String[] extPrcWithTax_ExtPrc_JsonKeyValArray = extPrcWithTax_ExtPrc_JsonKey.split(Pattern.quote("."));
								
								String[] extPrcWithTax_Tax_JsonKeyValArray = extPrcWithTax_Tax_JsonKey.split(Pattern.quote("."));
								
								
								Double valueExtPrcFromJson = (Double) (jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString().isEmpty()  ? 
										 Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString()));
								
								
								Double valueTaxFromJson = (Double) (jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString().isEmpty()  ? 
										 Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString()));
								
								
								
								Double sumOfExtPrcAndTax = (valueExtPrcFromJson + valueTaxFromJson);
								sumOfExtPrcAndTax = Math.round(sumOfExtPrcAndTax*100.0)/100.0;
								
								logger.debug("sumOfExtPrcAndTax >>>>>>>>> "+sumOfExtPrcAndTax);
								
								
								extPrcWithTaxForItemsMap.put("#Item.extPrcWithTax#", decimalFormat.format(sumOfExtPrcAndTax));
								
								processExtPrcWithTax = true;
								
							}if("#Item.DiscountPercent_NDC#".equals(placeHolderStr)){
								ItemDocItemDisc_JsonKey = "Items.DocItemDisc";
								String[] ItemDocItemDisc_JsonKeyValArray = ItemDocItemDisc_JsonKey.split(Pattern.quote("."));
								String discountVal = "";
								String discountJson = jsonObj.get(ItemDocItemDisc_JsonKeyValArray[1]).toString();
								if(discountJson.contains(".")) {
									discountVal = discountJson.substring(0,discountJson.indexOf("."));
								}
								Integer valueDiscPrcFromJson = (Integer) (discountVal.isEmpty()  ? 
										Integer.parseInt("0") : Integer.parseInt(discountVal));
								discWithNoDecItemsMap.put("#Item.DiscountPercent_NDC#", valueDiscPrcFromJson+"");
								processdiscPercNDC = true;
								
							}
						}catch(Exception e){
							logger.error("Exception >>> ",e);
						}
						
						
						
						String jsonKey = userJSONSettingHM.get(placeHolderStr);
						String[] jsonValArr = null;
						if(jsonKey != null) { 
							
						//logger.info("*** JSON key path  is : " + jsonKey);
						// if contains _$$_ multi valued keys .
						if(jsonKey.indexOf("_$$_") != -1) {
							
							//logger.info("******************** Multi values in json Key exist ...");
							//logger.info("*** json Val is : "+ jsonKey);
							
							String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));
							
							for(int j=0;j<jsonKeytokens.length;j++) {
								
								//logger.info("Individual key is  : "+ jsonKeytokens[j]);
								jsonValArr = jsonKeytokens[j].split(Pattern.quote("."));
								//logger.info("key is "+ jsonValArr[1]);
								
								if(jsonObj.get(jsonValArr[1]) == null) {
									
									logger.info("**** Excepted value not found in the json object ***");
									continue;
								}
								
//								logger.info("***^^ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+ " key is "+ jsonValArr[1]);
								
								try {
									
									val += (String)jsonObj.get(jsonValArr[1]) + " ";
								} catch(ClassCastException e) {
									try {
										val += ((Long)jsonObj.get(jsonValArr[1])).toString() + " ";
									} catch(ClassCastException f) {
										val += ((Double)jsonObj.get(jsonValArr[1])).toString() + " ";
									}	
								} 
							}
							//logger.info("Value(1) is : " + val);
							tempStr = tempStr.replace(placeHolderStr,val);
						} else {	
						
							//logger.info("<<<< json Val is : "+ jsonKey);
							jsonValArr = jsonKey.split(Pattern.quote("."));
			//				logger.info("***$$ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+" key is "+ jsonKey);
							
							if(jsonObj.get(jsonValArr[1]) == null) {
								logger.info("**** Excepted value not found in the json object ***");
								continue;
							}
							
							try {
								//val = (String)jsonObj.get(jsonValArr[1]);
								if(jsonKey.equals(Constants.DR_ITEM_INVC_ITEM_PRC)|| 
										jsonKey.equals(Constants.DR_ITEM_EXTPRC) ||	
										jsonKey.equals(Constants.DR_ITEM_DOCI_TEM_DISC_AMT)||
										jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_DISC) || 
										jsonKey.equals(Constants.DR_ITEM_TAX) ||
										jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_ORG_PRC) ||
										jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_EXT_ORG_PRC) ){
									 if(matcher.group(0).endsWith("_NDC#") && jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_DISC)){
										 
										 val = discWithNoDecItemsMap.get("#Item.DiscountPercent_NDC#");
									 }
									 else{
										 
										 Double value = (Double) (jsonObj.get(jsonValArr[1]).toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(jsonValArr[1]).toString()));
										 
										 val =(String)decimalFormat.format(value);
										 if(jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_DISC) && val.equals("0.00")){
											 
											 val = Constants.STRING_NILL;
										 }
									 }
									 
								}else{
									val = (String)jsonObj.get(jsonValArr[1]);
								}
							} catch(ClassCastException e) {
								try  {
									val = ((Long)jsonObj.get(jsonValArr[1])).toString();
								} catch(ClassCastException f) {
									val = ((Double)jsonObj.get(jsonValArr[1])).toString();
								}
							}
							
							//logger.info("Value(2) is : " + val);
							tempStr = tempStr.replace(matcher.group(0),val);
						}  
					  } // if
						
						
						try{

							if(processExtPrcWithTax){
								tempStr = tempStr.replace("#Item.extPrcWithTax#",extPrcWithTaxForItemsMap.get("#Item.extPrcWithTax#"));
								extPrcWithTaxForItemsMap.clear();

								logger.debug("tempStr extPrc related >>> "+tempStr);
							}
							if(processdiscPercNDC){
								tempStr = tempStr.replace("#Item.DiscountPercent_NDC#",discWithNoDecItemsMap.get("#Item.DiscountPercent_NDC#"));
								discWithNoDecItemsMap.clear();

								logger.debug("tempStr DiscountPercent_NDC related >>> "+tempStr);
							}
							
						}catch(Exception e){
							logger.error("Exception >>>>>>>> ",e);
						}
						
					}
					
					matcher.reset();
					
					finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
					
				}
				
				//logger.info("final block to replace is "+ finalHtmlBlockVal);
				return finalHtmlBlockVal;
			} catch(Exception e) {
				
				logger.error("Exception ::" , e);
				return null;
			} 
		}

		
		private String replaceExisting_ExtPrcWithTax_IfAny(JSONObject jsonMainObj, String templateContentStr){
			
			
			try{


				
				if(templateContentStr == null){
					return templateContentStr;
				}
				
				JSONArray jsonArr = null;

				jsonArr  = (JSONArray)jsonMainObj.get("Items");

				if(jsonArr == null){
					return templateContentStr;
				}






				String extPrcWithTax_ExtPrc_JsonKey = "";
				String extPrcWithTax_Tax_JsonKey = "";

				for(int i = 0; i < jsonArr.size(); i++)
				{



					JSONObject jsonObj = (JSONObject)jsonArr.get(i);

					if(templateContentStr.contains("#Item.extPrcWithTax#")){




						extPrcWithTax_ExtPrc_JsonKey = "Items.ExtPrc";

						extPrcWithTax_Tax_JsonKey = "Items.Tax";

						String[] extPrcWithTax_ExtPrc_JsonKeyValArray = extPrcWithTax_ExtPrc_JsonKey.split(Pattern.quote("."));

						String[] extPrcWithTax_Tax_JsonKeyValArray = extPrcWithTax_Tax_JsonKey.split(Pattern.quote("."));


						Double valueExtPrcFromJson = (Double) (jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString().isEmpty()  ? 
								Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString()));


						Double valueTaxFromJson = (Double) (jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString().isEmpty()  ? 
								Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString()));



						Double sumOfExtPrcAndTax = (valueExtPrcFromJson + valueTaxFromJson); 

						
						sumOfExtPrcAndTax = Math.round(sumOfExtPrcAndTax*100.0)/100.0;
						logger.debug("sumOfExtPrcAndTax >>>>>>>>> "+sumOfExtPrcAndTax);


						DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
						templateContentStr = templateContentStr.replace("#Item.extPrcWithTax#", decimalFormat.format(sumOfExtPrcAndTax));



					}






				}


				
				
				//templateContentStr = templateContentStr.replaceAll("#Item.extPrcWithTax#", "");
				
				
				
				
				
			}catch(Exception ex){
				logger.error("Exception >>> ",ex);
				return templateContentStr;
			}
			return templateContentStr;
					
					
					
					
				
					
		}
		
		private Map<String, String> getUserJsonSettingMap(String userJSONSettings) throws BaseServiceException{
			Map<String, String> userJSONSettingHM = null;
			try{
				String[] settingFieldsArr = userJSONSettings.split(Pattern.quote("^|^"));
		    	userJSONSettingHM = new HashMap<String, String>();
		    	String[] tokensStr;
		    	for(int i=0;i< settingFieldsArr.length;i++) {
		    		tokensStr = settingFieldsArr[i].split(":");
		    		userJSONSettingHM.put(tokensStr[0], tokensStr[1]);
		    	}
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting userjsonsettingmap ", e);
			}
	    	return userJSONSettingHM;
		}
		
		private boolean testForSONumber(String templateContentStr, String[] itemsSetPlaceHoldersArr, Map<String, String> userJSONSettingHM, 
				JSONObject jsonBodyObj, String[] paymentSetPlaceHoldersArr){
			
			//JSONObject jsonBodyObj = (JSONObject)jsonMainObj.get("Body");
			JSONObject receiptJSONObj = (JSONObject)jsonBodyObj.get("Receipt");
			
			if(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(itemsSetPlaceHoldersArr[5]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) + itemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(itemsSetPlaceHoldersArr[5]));
				logger.debug("Items HTML is :"+ loopBlockOne);
				
				
				String replacedStr = replaceLoopBlock(loopBlockOne, userJSONSettingHM, jsonBodyObj);
				logger.debug("replacedStr ::"+replacedStr);
				/**
				 *  Removed after Faye's Request(With out Items and SONumber also We'l send DR)
				 */
				
			/*	if((receiptJSONObj.get("SONumber") == null || receiptJSONObj.get("SONumber").toString().isEmpty() )  && replacedStr == null) {
					
					return false;
				}*/
		   }
			return true;
		}
		
		private String addDomainNameToImageURLs(String templateContent){
			// Add domain name to images urls
			String templateContentStr = templateContent;
			String appUrl = PropertyUtil.getPropertyValue("subscriberIp");			
			templateContentStr = templateContentStr.replaceAll("/subscriber/SystemData/digital-templates/",  appUrl +"/subscriber/SystemData/digital-templates/" );
			return templateContentStr;			
		}
		// remove all no-value place holders form template
		private String removeNoValuePhValues(String templateContent){
			// remove all no-value place holders form template
				String templateContentStr = templateContent;
				String digiRcptDefPHStr = PropertyUtil.getPropertyValue("DRPlaceHolders");
				if(digiRcptDefPHStr != null) {
					String[] defPHArr = digiRcptDefPHStr.split(Pattern.quote(","));
					for(int i=0;i<defPHArr.length;i++) {
						templateContentStr = templateContentStr.replaceAll(defPHArr[i],"");
					}
				}
				return templateContentStr;
		}
		
		private String replaceIndividualPhValues(Map<String, String> userJSONSettingHM, String templateContent, JSONObject jsonMainObj){
			
			String templateContentStr = templateContent;
			Set<String> set = userJSONSettingHM.keySet();
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	String jsonKeyValue = "";
	    	
			for (String phStr : set) {
				
				jsonKeyValue = "";
	    		JSONObject parentObject = null;
	    		JSONArray parentArrayObject = null;
			
		    		if(templateContentStr.indexOf(phStr) != -1) {			// PH exists in template
		    			
		    			jsonKey = userJSONSettingHM.get(phStr);
		    	//		logger.debug("*******<< jsonKey : "+ jsonKey);
		    			
		    			if(jsonKey.indexOf("_$$_") != -1) {
		    				
		    		//		logger.debug("contains dollar .....");
		    				
		    				String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));
		    				String DocDate=null;
		    				for(int j=0;j<jsonKeytokens.length;j++) {
		    					
					               // . path exists in json key 
			    				
			    				jsonPathArr = jsonKeytokens[j].split(Pattern.quote("."));
			    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
			    				
			    				try {
			    					
			    					//logger.debug("SINGLE OBJ CREATED...");
			    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
			    				} catch(ClassCastException e) {
			    					
			    				//	logger.debug("ARRAY OBJ CREATED...");
			    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
			    				//	logger.debug("--Array---"+ parentArrayObject);
			    					parentObject  = (JSONObject)parentArrayObject.get(0);
			    				//	logger.debug("--First element in array---"+ parentObject);
			    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
			    				}	
			    			
			    				if(parentObject != null) {
                                   try {
			    						if(jsonPathArr[1].equals("DocDate") && (parentObject.get(jsonPathArr[1])!=null || !(parentObject.get(jsonPathArr[1]).toString()).isEmpty())){
												DocDate = parentObject.get(jsonPathArr[1]) +"";
												logger.info("DocDate-------------"+DocDate);
												logger.info("DocDate value------------"+jsonPathArr[1].toString());
												logger.info("DocDate value------------"+jsonPathArr[1]);
												
											}
											if(jsonPathArr[1].equals("DocTime")  && (parentObject.get(jsonPathArr[1]).toString().isEmpty())){
									
											     jsonKeyValue = DocDate;
											     logger.info("Date:--------------->" +jsonKeyValue);
											}
											else if(jsonPathArr[1].equals("DocTime")  && (parentObject.get(jsonPathArr[1])!=null || !(parentObject.get(jsonPathArr[1]).toString()).isEmpty())){
												// DOCTIME = "23:43:23" --> 11:43:23 PM
												if(DocDate != null && !DocDate.toString().isEmpty()){
												String docTime= parentObject.get(jsonPathArr[1]) + "";
												String 	docTime_AM_PM =	MyCalendar.calendarToString(MyCalendar.string2Calendar(DocDate+ " " +docTime),MyCalendar.FORMAT_TIME_AM, null);	
												//String docTime_AM_PM = MyCalendar.calendarToString(MyCalendar.string2Calendar(jsonKeyValue +(String)parentObject.get(jsonPathArr[1])),MyCalendar.FORMAT_TIME_AM, null);
												jsonKeyValue += docTime_AM_PM;
												logger.info("Receipt time (AM/PM) is" +jsonKeyValue);
											}
											}
											
		
			    									
											else
												jsonKeyValue += (String)parentObject.get(jsonPathArr[1]) + " ";
												logger.info("Enterd to else");
								
			    						
			    						
			    					} catch(ClassCastException e) {
			    						try {
			    							jsonKeyValue += ((Double)parentObject.get(jsonPathArr[1]))+ " ";
			    						} catch(ClassCastException f) { 
			    							jsonKeyValue += ((Long)parentObject.get(jsonPathArr[1])) + " ";
			    						}	
			    					}
			 
			    				} else {
			    					jsonKeyValue = Constants.STRING_NILL;
			    		//			logger.debug("** Parent JSON is null *****");
			    				} 
			    			
		    				}
		    				
		    			//	logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
	    					//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				logger.info("phStr------------"+phStr);
		    				logger.info("jsonKeyValue--------------------"+jsonKeyValue);
		    				
		    			} else if(jsonKey.contains(".")) {			               // . path exists in json key 
		    				
		    				jsonPathArr = jsonKey.split(Pattern.quote("."));
		    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
		    				
		    				try {
		    					
		    					//logger.debug("SINGLE OBJ CREATED...");
		    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
		    				} catch(ClassCastException e) {
		    					
		    					//logger.debug("ARRAY OBJ CREATED...");
		    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
		    					//logger.debug("--Array---"+ parentArrayObject);
		    					parentObject  = (JSONObject)parentArrayObject.get(0);
		    				//	logger.debug("--First element in array---"+ parentObject);
		    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
		    				}	
		    			
		    				if(parentObject != null) {
		    					
		    					try {
		    						
		    						if(parentObject.get(jsonPathArr[1]) != null) {  
		    							
		    							jsonKeyValue = (String)parentObject.get(jsonPathArr[1]);
		    						}	
		    					} catch(ClassCastException e) {
		    						try {
		    							jsonKeyValue = ((Double)parentObject.get(jsonPathArr[1])).toString();
		    						} catch(ClassCastException f) { 
		    							jsonKeyValue = ((Long)parentObject.get(jsonPathArr[1])).toString();
		    						}	
		    					} 
		    					
		    				//	logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
		    				} else {
		    					jsonKeyValue = Constants.STRING_NILL;
		    				//	logger.debug("** Parent JSON is null *****");
		    				}
		    				/**
		    				 * APP-1222
		    				 * 0 = Normal sales receipt, 2 = Return receipt, 3 = Check in, 4 = Check out, 6 = Lost sale,7 = High security
		    				 * 10 = open register, 11 = close register, 12 = pay out , 13 = override
		    				 * */
		    				if(phStr.equalsIgnoreCase("#Receipt.InvcHdrReceiptType#")) {
		    					/** TODO get the key values from DB
		    					 * spit and assign the value.
		    					 */
		    					try{	
		    						//0:Regular^|^2:Return^|^3:Check in^|^4:Check out^|^6:Lost sale^|^7:High security^|^10:open register^|^11:close register^|^12:payout^|^13:override
		    						if(jsonKeyValue!=null&&!jsonKeyValue.isEmpty()){
		    							String invcHdrReceiptTypeSettings = PropertyUtil.getPropertyValueFromDB("Receipt_InvcHdrReceiptType"); 
		    							Map<String, String> invcHdrReceiptTypeSettingsHM  = null;

		    							invcHdrReceiptTypeSettingsHM = getUserJsonSettingMap(invcHdrReceiptTypeSettings); 
		    							jsonKeyValue = invcHdrReceiptTypeSettingsHM.containsKey(jsonKeyValue) ? invcHdrReceiptTypeSettingsHM.get(jsonKeyValue) : jsonKeyValue;
		    						}
		    						
		    					}catch(Exception e){
		    						logger.error("Exception :: ",e);

		    					}
		    				}else if(phStr.equalsIgnoreCase("#Receipt.Amount#") || phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")) {
		    					
		    					//BigDecimal tenderedRounded = null;
		    					if(phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")){
		    						logger.debug("==insidedec3===");
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Amount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Amount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    						
		    					}
		    					
		    					
		    				}else if(phStr.equalsIgnoreCase("#Receipt.Subtotal#") || phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")) {
		    					
		    					
		    					if(phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")){
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Subtotal_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Subtotal#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}
		    					
		    				}else if(phStr.equalsIgnoreCase("#Receipt.TaxAmount#") || phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")) {
		    					
		    					
		    					if(phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")){
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						
		    						templateContentStr = templateContentStr.replace("#Receipt.TaxAmount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.TaxAmount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}
		    					
		    				}
		    				
		    				//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				
		    			}  
		    		}
		    } // for 
			return templateContentStr;
		}
		
private String replaceItemArrayNew(String loopBlockOne, String ItemPHTag, Double amtVal){
			
			String Editor = editorType;
			logger.info("Editor Type..................."+Editor);
			String itemHolder = loopBlockOne;
			String preparedString="";
			try{
				final String COLSPAN = "COLSPAN";
				final String Width1 = "Width1";
				final String Width2 = "Width2";
				final String LABEL = "LABEL";
				final String VALUE = "VALUE";
				final String CELLSPACING = "CELLSPACING";
				final String BENEATH_ITEMS_TR_STRUCTURE;
				String [] itemArray;
				DecimalFormat decimalFormat;
			

				if (Editor!=null && Editor.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {
					BENEATH_ITEMS_TR_STRUCTURE = 
									"<table>"+
									"<tr>"+ 
									"<td colspan="+COLSPAN+">" +
									"<div class=\"block-grid \" style=\"Margin: 0 auto; min-width: 500px; max-width: fit-content; width:inherit; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;;\"> " +
									"<table width=\"100%\"  style=\"color:#333333; font-family:arial; font-size:14px;\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
									"<tr>" +
									"<td width="+Width1+"> </td>" +
									"<td width=\"20%\" style=\"text-align:left; color:#333333; font-family:arial; font-size:14px;\">"+LABEL+"</td>" +
									"<td width="+Width2+" style=\"text-align:right; color:#333333; font-family:arial; font-size:14px;\">"+VALUE+"</td>"+
									"</tr>"+
									"</table>"+
									"</div>" +
									"</td>"+
									"</tr>"+
									"</table>";
					
					itemArray = getItemsArray(loopBlockOne);
					logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
					decimalFormat = new DecimalFormat("#,###,##0.00");
					
					logger.info("EditorType   >>>>>>>>>>>>>>>>>>>>>>> "+Editor );
					logger.info("itemArray.length    >>>>>>>>>>>>>>>>>>>>>>> "+itemArray.length  );
					
					if (itemArray.length == 4){
						
						//logger.info("four itemsssss.........."+ItemPHTag.trim().length());
						if(ItemPHTag.trim().length() == 0){
							preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "50% ").replace(Width2, "30% ").
							replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "0");
					
							logger.info("line spacing >>> "+preparedString);
							return preparedString;
						}
						preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "50% ").replace(Width2, "30% ").
								replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
								.replace(CELLSPACING, "0");
						
					} else if (itemArray.length == 5){
						
						//logger.info("five itemsssss.........."+ItemPHTag.trim().length());
						if(ItemPHTag.trim().length() == 0){
							preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "60% ").replace(Width2, "20% ").
							replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "0");
					
							logger.info("line spacing >>> "+preparedString);
							return preparedString;
						}
						preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "60% ").replace(Width2, "20% ").
								replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
								.replace(CELLSPACING, "0");
					} else {
						
						//logger.info("six itemsssss.........."+ItemPHTag.trim().length());
						if(ItemPHTag.trim().length() == 0){
							preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "66% ").replace(Width2, "14% ").
							replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "7");
					
							logger.info("line spacing >>> "+preparedString);
							return preparedString;
						}
						preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "66% ").replace(Width2, "14% ").
								replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
								.replace(CELLSPACING, "0");
					}
				
				} else {
					
					BENEATH_ITEMS_TR_STRUCTURE = 
							"<tr>"+ 
									"<td colspan="+COLSPAN+">" +
									"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
									"<tr>" +
									"<td width="+Width1+"> </td>" +
									"<td width=\"20%\" style=\"text-align:left;\">"+LABEL+"</td>" +
									"<td width="+Width2+" style=\"text-align:right;\">"+VALUE+"</td>"+
									"</tr>"+
									"</table>"+
									"</td>"+
									"</tr>";

					
					itemArray = getItemsArray(loopBlockOne);
					logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
					decimalFormat = new DecimalFormat("#,###,##0.00");
					
				logger.info("EditorType   >>>>>>>>>>>>>>>>>>>>>>> "+Editor );
					if(ItemPHTag.trim().length() == 0){
					preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").replace(Width1, "50% ").replace(Width2, "30% ").
							replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "7");
					
					logger.info("line spacing >>> "+preparedString);
					return preparedString;
					}
					
				
					preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").replace(Width1, "50% ").replace(Width2, "30% ").
						replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
						.replace(CELLSPACING, "0");
			 }//else
				
				logger.info("preparedString >>> "+preparedString);
				

			}catch(Exception e){
				logger.error("Exception >>> ",e);
				return itemHolder;
			}
		
			return preparedString;
			
		}

public String[] getItemsArray(String itemHtml) throws BaseServiceException{
	/*String text = "<tr><td>#Item.Description1#</td><td>#Item.Quantity#</td>"
					+ "<td>#Item.Discount#</td><td>#Item.DiscountPercent#</td>"
					+ "<td>#Item.UnitPrice#</td><td>#Item.Total#</td></tr>";
	*/
	 String pattern = "<td.*?>([^<]+)</td.*?>";
	   Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(itemHtml);
	
			
	 String resultStr =Constants.STRING_NILL;
    while (m.find()) {
    	       	
    	String itemValue = m.group(1);
    	if(! resultStr.isEmpty())	resultStr+=Constants.DELIMETER_COMMA;
    	resultStr += itemValue;
    	
    }
  String [] itemsArray = resultStr.split(Constants.DELIMETER_COMMA);
		  
  return itemsArray;  
}

public String replaceItemArray(String loopBlockOne, String ItemPHTag, Double amtVal) throws BaseServiceException{
	
	String itemHolder = loopBlockOne;
	try {
		//int itemHolderIndex = 0;
		 String [] itemArray = getItemsArray(loopBlockOne);
		 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
		int rightAlignIndex = (itemArray.length-2);
		for (int itemHolderIndex = 0; itemHolderIndex<itemArray.length ; itemHolderIndex++) {
			if(itemHolderIndex == rightAlignIndex) {
				//logger.debug("in if ::"+ItemPHTag + itemHolderIndex+ itemArray[itemHolderIndex] + itemArray[itemHolderIndex+1]);
				try {
					String str = itemArray[itemHolderIndex];
					//String substr = str.substring(0,str.length()-1);
					String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
					String itemSubStr = originalitemSubStr.replace(str, "<strong> "+ItemPHTag+" </strong>");
					//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
					itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
					//logger.debug("itemHolder 1::"+itemHolder);
					itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<div align='right'><strong>"+ decimalFormat.format(amtVal)+"</strong></div>");
					//logger.debug("itemHolder2 ::"+itemHolder);
					
					try{
						itemHolder = itemHolder.replace("text-align:left", "text-align:right");
					}catch(Exception e){
						logger.error("Exception >>>>>>>>>>>>> ",e);
					}
					
					//logger.debug("prepared  itemHolder ::"+itemHolder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				break;
				
			}else{
				//logger.debug("in else ::"+itemHolderIndex);
				
				String str = itemArray[itemHolderIndex];
				//String substr = str.substring(0,str.length()-1);
				String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
				String itemSubStr = originalitemSubStr.replace(str, "&nbsp;");
				//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
				itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
				//logger.debug("itemHolder 1::"+itemHolder);
				//itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<strong>"+ decimalFormat.format(amtVal)+"</strong>");
				//logger.debug("itemHolder2 ::"+itemHolder);
				
				//itemHolder = itemHolder.replace(itemArray[itemHolderIndex], "&nbsp;");
			}
				//itemHolderIndex ++;
		}
		logger.debug("itemHolder3 ::"+itemHolder);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}
	return itemHolder;
}

		private String replaceReceiptPhValues(String templateContentStr, String[] itemsSetPlaceHoldersArr, Map<String, String> userJSONSettingHM, 
				JSONObject jsonBodyObj, String[] paymentSetPlaceHoldersArr,Users user) throws BaseServiceException{
			
			//JSONObject jsonBodyObj = (JSONObject)jsonMainObj.get("Body");
			JSONObject jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
			
			if(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(itemsSetPlaceHoldersArr[5]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) + itemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(itemsSetPlaceHoldersArr[5]));
				logger.debug("Items HTML is :"+ loopBlockOne);
				
				String replacedStr = replaceLoopBlock(loopBlockOne, userJSONSettingHM, jsonBodyObj);
				
				
				 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00"); 
				/* Double totTax = (Double) (jsonReceiptObj.get("TotalTax").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("TotalTax").toString()));
				 Double subTotal = (Double) (jsonReceiptObj.get("Subtotal").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("Subtotal").toString()));
				 Double shipping = (Double) (jsonReceiptObj.get("Shipping").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("Shipping").toString()));
				 Double fee = (Double) (jsonReceiptObj.get("Fee").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("Fee").toString()));
				 Double total = (Double) (jsonReceiptObj.get("Total").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("Total").toString()));
				
				 
				 List<Double> amtValues = new ArrayList<Double>();
				 amtValues.add(subTotal);
				 amtValues.add(totTax);
				 amtValues.add(shipping);
				 amtValues.add(fee);
				 amtValues.add(total);*/
				 Map<String, Double> mescilleneousAmtMap = new LinkedHashMap<String, Double>();
					
					Object subTotalJsonObj = jsonReceiptObj.get("Subtotal");
					if(subTotalJsonObj != null && !subTotalJsonObj.toString().trim().isEmpty() && !subTotalJsonObj.toString().trim().equals("0") ){
						
						Double subTotal = Double.parseDouble(subTotalJsonObj.toString().trim());
						mescilleneousAmtMap.put("Sub Total", subTotal);
						//amtValues.add(subTotal);
						
					}
					
					 Object totTaxJsonObj = jsonReceiptObj.get("TotalTax");
					 if(totTaxJsonObj != null && !totTaxJsonObj.toString().trim().isEmpty() && !totTaxJsonObj.toString().trim().equals("0")){
						 
						 Double totTax = Double.parseDouble(totTaxJsonObj.toString().trim());
						// amtValues.add(totTax);
						 if(totTax>0){
							 mescilleneousAmtMap.put("Tax", totTax);
							 isTaxExists = true;
						 }

					 }
					 
					 
					 Object shippingJsonObj = jsonReceiptObj.get("Shipping");
					 if(shippingJsonObj != null && !shippingJsonObj.toString().trim().isEmpty() && !shippingJsonObj.toString().trim().equals("0")){
						 
						 Double shipping = Double.parseDouble(shippingJsonObj.toString().trim());
						 if(shipping>0) mescilleneousAmtMap.put("Shipping", shipping);
						// amtValues.add(shipping);

					 }
					 
					 Object feeJsonObj = jsonReceiptObj.get("Fee");
					 String helperKey="";
					 if(feeJsonObj != null && !feeJsonObj.toString().trim().isEmpty() && !feeJsonObj.toString().trim().equals("0")){
						 
						 Object feeType = jsonReceiptObj.get("FeeType");
						 Double fee = Double.parseDouble(feeJsonObj.toString().trim());
						/* mescilleneousAmtMap.put("Fee -"+((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL), fee); */
						 
						 helperKey = ((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee";
						 
						 /*mescilleneousAmtMap.put(((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee", fee); */
						 
						 if(fee>0) mescilleneousAmtMap.put(helperKey, fee);
						 //amtValues.add(fee);

					 }
					 
					 Object discountJsonObj = jsonReceiptObj.get("Discount");
					 
					 if(discountJsonObj != null && !discountJsonObj.toString().trim().isEmpty() && !discountJsonObj.toString().trim().equals("0")){
						 
						 Double discount = Double.parseDouble(discountJsonObj.toString().trim());
						if(discount>0) mescilleneousAmtMap.put("Rcpt. Discount", discount); 
						 //amtValues.add(total);
						 
					 }
					 Object totalJsonObj = jsonReceiptObj.get("Total");
					 if(totalJsonObj != null && !totalJsonObj.toString().trim().isEmpty() && !totalJsonObj.toString().trim().equals("0")){
						 
						 Double total = Double.parseDouble(totalJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total", total); 
						 //amtValues.add(total);

						 
					 }
					 
					 Object totalSavingJsonObj = jsonReceiptObj.get("TotalSavings");
					 if(totalSavingJsonObj != null && !totalSavingJsonObj.toString().trim().isEmpty() && !totalSavingJsonObj.toString().trim().equals("0")){
						 
						 Double totalSaving = Double.parseDouble(totalSavingJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total Savings", totalSaving); 
						 //amtValues.add(total);

					 }
					 
				/*if((receiptJSONObj.get("SONumber") == null || receiptJSONObj.get("SONumber").toString().isEmpty() )  && replacedStr == null) {
					errorCode = 300008;
					jsonMessage="unable to find proper mappings.";
					logger.debug("**** Exception : Error occured while replacing the item default values ... ");
					return null;
				}*/
				
				// Commented after default values in DR
			//	if(replacedStr != null) {
				 if(replacedStr == null)replacedStr= Constants.STRING_NILL;
				 String itemHolder = "";
					// Set<String> DR_PLACEHOLDERS_LIST = Constants.DR_PLACEHOLDERS_LIST;
					 Set<String> mescelleneousAmtKeySet = mescilleneousAmtMap.keySet();
					// int amountIndex = 0;
					 
					 try {
						 
						 DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = 
								 (DigitalReceiptUserSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
						 
						 DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
						 boolean tax = (digitalReceiptUserSettings.isIncludeTax() && (mescilleneousAmtMap.get("Tax") != null));
						 boolean fee = (digitalReceiptUserSettings.isIncludeFee() && (mescilleneousAmtMap.get(helperKey) != null));
						 boolean shipping = (digitalReceiptUserSettings.isIncludeShipping() && (mescilleneousAmtMap.get("Shipping") != null));
						 boolean globalDisc = (digitalReceiptUserSettings.isIncludeGlobalDiscount() && (mescilleneousAmtMap.get("Rcpt. Discount") != null));
						 boolean totalAmount = (digitalReceiptUserSettings.isIncludeTotalAmount() && (mescilleneousAmtMap.get("Total") != null));
						 
						 itemHolder = replaceItemArrayNew(loopBlockOne, Constants.STRING_NILL, 0.0 ); 
						 replacedStr += itemHolder;
						 
						 
						 
						 for (String ItemPHTag : mescelleneousAmtKeySet) {
							 
							 if(ItemPHTag.equalsIgnoreCase("Tax")){
								 if(tax){ 
									 	itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 	replacedStr += itemHolder;
								     }
							 } else if(ItemPHTag.equalsIgnoreCase(helperKey)){ // i.e. for fee
								 if(fee){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 } else if(ItemPHTag.equalsIgnoreCase("Shipping")){
								 if(shipping){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }else if(ItemPHTag.equalsIgnoreCase("Rcpt. Discount")){
								 if(globalDisc){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 } else if(ItemPHTag.equalsIgnoreCase("Sub Total")){
								 if(tax || fee || shipping || globalDisc){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }else if(ItemPHTag.equalsIgnoreCase("Total")){
								 if(totalAmount){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }
							 else{
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) );
								 replacedStr += itemHolder;
							 }
							 
							 
							 
							
							 
							 
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("Exception>>>>>>>>>>>>>>"+e);
					}
					 
					/*String itemHolder = "";
					itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            
		            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
		            
		            
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Sub Total </strong>");
		           // itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("Subtotal") + "</strong>");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(subTotal) + "</strong>");
		            replacedStr += itemHolder;

		            // Tax
		            itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            
		            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
		            
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "Tax");
		          //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("TotalTax") + "</strong>");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(totTax) + "</strong>");
		            replacedStr += itemHolder;
		            
		            // Shipping
		            if (((JSONObject)jsonBodyObj.get("Receipt")).get("Shipping") != null) 
		            {
		                itemHolder = loopBlockOne;
		                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		                
		                itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
			            
		                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Shipping");
		              //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Shipping")  + "</strong>");
		                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(shipping)  + "</strong>");
		                replacedStr += itemHolder;
		            }

		            // Fee
		            if (((JSONObject)jsonBodyObj.get("Receipt")).get("Fee") != null) {
		                itemHolder = loopBlockOne;
		                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		                
		                itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
			            
		                itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Fee-" + ((JSONObject)jsonBodyObj.get("Receipt")).get("FeeType"));
		              //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Fee") + "</strong>");
		                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(fee) + "</strong>");
		                replacedStr += itemHolder;
		            }
		            
		            // Total
		            itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            
		            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
		            
		            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Total </strong>");
		          //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Total") + "</strong>");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(total) + "</strong>");
		            replacedStr += itemHolder;
				*/
				
					
					templateContentStr = templateContentStr.replace(loopBlockOne, replacedStr);
					
					// Commented after default values in DR	
					
				/*} 
				else { 
					

					templateContentStr = templateContentStr.replace("#Item.Description#", "&nbsp;").
		           replace("#Item.Attr#", "&nbsp;").
		           replace("#Item.Description1#", "&nbsp;")
		           .replace("#Item.Description2#", "&nbsp;")
		           .replace("#Item.Quantity#", "&nbsp;")
		           .replace("#Item.Size#", "&nbsp;")
		            .replace("#Item.SerialNumber#", "&nbsp;")
		            
		           .replace("#Item.ALU#", "&nbsp;")
		           .replace("#Item.Lookup#", "&nbsp;")
		            .replace("#Item.Discount#", "&nbsp;")
		           .replace("#Item.DiscountPercent#", "&nbsp;")
		            
		           .replace("#Item.UnitPrice#", "<strong> Sub Total </strong>")
		         //  .replace("#Item.Total#", "<strong>" + receiptJSONObj.get("Subtotal") + "</strong>");
		           .replace("#Item.Total#", "<strong>" + decimalFormat.format(subTotal) + "</strong>");
					
				}*/
			}
			
			 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
			 Double amount = (Double) (jsonReceiptObj.get("Total").toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("Total").toString()));
			 templateContentStr = templateContentStr.replace("#Receipt.Amount#", (String)decimalFormat.format(amount));
			 
			 
			 DecimalFormat decimalFormat1 = new DecimalFormat("#,###,##0.00");
			 Double amount1 = (Double) (jsonReceiptObj.get("InvcTotalOfLineDisc").toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("InvcTotalOfLineDisc").toString()));
			 templateContentStr = templateContentStr.replace("#Receipt.InvcTotalLineDisc#", (String)decimalFormat1.format(amount1));
			 
			 Double totalSaving = (Double)(jsonReceiptObj.get("TotalSavings").
		    			toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonReceiptObj.get("TotalSavings").toString()));
		    	templateContentStr = templateContentStr.replace(Constants.DR_RECEIPT_FOOTER, (String)decimalFormat.format(totalSaving)).
		    			replace(Constants.DR_RECEIPT_TOTALSAVING, (String)decimalFormat.format(totalSaving));
		    	
			
			if(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]) != -1) 
			{
			
				String loopBlockTwo = templateContentStr.substring(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) + paymentSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]));
				
				String paymentParts ="";
				 DecimalFormat deciFormat = new DecimalFormat("#,###,##0.00"); 
				// ADDED AFTER NEW DR SCHEMA 
				if (jsonBodyObj.get("Cash") != null)  {
					JSONObject cashObj =(JSONObject)jsonBodyObj.get("Cash");
 
					 Double cashVal = (Double) (cashObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(cashObj.get("Amount").toString()));
					   
					   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Cash", deciFormat.format(cashVal)) ;
		            }
				 
		            if (jsonBodyObj.get("StoreCredit") != null)   {
		            	JSONArray storeCreditCardArr = (JSONArray)jsonBodyObj.get("StoreCredit");
		            	
		            	for (Object object : storeCreditCardArr) {
							
		            		JSONObject tempStoreCreditObj = (JSONObject)object;
		            		  Double storeCreditVal = (Double) (tempStoreCreditObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempStoreCreditObj.get("Amount").toString()));
				            	//	String storeCreditVal = deciFormat.format(tempStoreCreditObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempStoreCreditObj.get("Amount")).toString();
				            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Store Credit",deciFormat.format(storeCreditVal) );
						}
		            	
		            }
		            
		            
		            if (jsonBodyObj.get("CreditCard") != null) {
		            	JSONArray creditCardArr = (JSONArray)jsonBodyObj.get("CreditCard");
		            	
		            	for (Object object : creditCardArr) {
							
		            		JSONObject tempObj = (JSONObject)object;
		            		 Double creditCardVal = (Double) (tempObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempObj.get("Amount").toString()));
			            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
			            	
		            		// Added after Payement.Number implementation
		            		 String ccNum = tempObj.get("Number").toString();
		            		 
		            		String creditCardNum = Constants.STRING_NILL;
							if(!ccNum.isEmpty() && ccNum.length() >= 4){
								creditCardNum =maskCardNumber(ccNum);
							}
							else if(!ccNum.isEmpty()){
								creditCardNum = ccNum.toString();
							}
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,	creditCardNum, "Credit Card - "+tempObj.get("Type").toString(),	deciFormat.format(creditCardVal) );
						}
		            }	
		                
		            
		            if (jsonBodyObj.get("DebitCard") != null)    {
		            	
		            	JSONArray debitCreditCardArr = (JSONArray)jsonBodyObj.get("DebitCard");
		            	
		            	for (Object object : debitCreditCardArr) {
							
		            		JSONObject tempDebitCreditObj = (JSONObject)object;
		            		 Double debitVal = (Double) (tempDebitCreditObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempDebitCreditObj.get("Amount").toString()));
			            		//String debitVal = deciFormat.format(tempDebitCreditObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempDebitCreditObj.get("Amount")).toString();

		            		 // Added after Payement.Number implementation
		            		 String debitNum = tempDebitCreditObj.get("Number").toString();
		            		 
		            		String debitCardNum = Constants.STRING_NILL;
							if(!debitNum.isEmpty() && debitNum.length() >= 4){
								debitCardNum =maskCardNumber(debitNum);	
							}else if(!debitNum.isEmpty()){
								debitCardNum=debitNum.toString();
							}
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,debitCardNum,"Debit Card",	deciFormat.format(debitVal) );
						}
		            	
		            }
		            
		            if (jsonBodyObj.get("Gift") != null) {
		            	
		            	JSONArray giftArr = (JSONArray)jsonBodyObj.get("Gift");
		            	
		            	for (Object object : giftArr) {
							
		            		JSONObject tempGiftObj = (JSONObject)object;
		            		Double giftVal = (Double) (tempGiftObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempGiftObj.get("Amount").toString()));
		            		//String giftVal = deciFormat.format(tempGiftObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00")  : deciFormat.format(tempGiftObj.get("Amount")).toString();
		            		

		            		 // Added after Payement.Number implementation
		            		 String giftNum = tempGiftObj.get("GiftNum").toString();
		            		
		            		paymentParts += GetMergedPaymentPart(loopBlockTwo,giftNum,"Gift Certificate", deciFormat.format(giftVal));
						}
		            	
		            }
		            
		            if (jsonBodyObj.get("GiftCard") != null) {
		            	
		            	JSONArray giftArr = (JSONArray)jsonBodyObj.get("GiftCard");
		            	
		            		for (Object object : giftArr) {
							
		            		JSONObject tempGiftCardObj = (JSONObject)object;
		            		 Double giftCrdVal = (Double) (tempGiftCardObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempGiftCardObj.get("Amount").toString()));
		            		//String giftCrdVal = deciFormat.format(tempGiftCardObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.get("Amount")).toString();
		            		

		            		 // Added after Payement.Number implementation
		            		 String gcNum = tempGiftCardObj.get("Number").toString();
		            		 
		            		String giftCardNum = Constants.STRING_NILL;
							if(!gcNum.isEmpty() && gcNum.length() >= 4 ){
								giftCardNum =maskCardNumber(gcNum);
							}
							else if(!gcNum.isEmpty()){
								giftCardNum =gcNum.toString();
							}
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo, giftCardNum ,"Gift Card",deciFormat.format(giftCrdVal));
						}
		            	
		                
		            }
		            
		            if (jsonBodyObj.get("Charge") != null) {
		            	JSONObject chargeObj = (JSONObject)jsonBodyObj.get("Charge");
		           	 Double chargeVal = (Double) (chargeObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(chargeObj.get("Amount").toString()));
		            	//String chargeVal = deciFormat.format(chargeObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(chargeObj.get("Amount")).toString();
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Charge",	deciFormat.format(chargeVal ));
		            }
		            
		            if (jsonBodyObj.get("COD") != null) {

		            	JSONObject CODObj = (JSONObject)jsonBodyObj.get("COD");
		           	 Double codVal = (Double) (CODObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(CODObj.get("Amount").toString()));
		            	//String codVal = deciFormat.format(CODObj.get("Amount")).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(CODObj.get("Amount")).toString() ;
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","COD ",deciFormat.format(codVal));
		            }
		            
		            if (jsonBodyObj.get("Deposit") != null){
		            	JSONObject depositObj = (JSONObject)jsonBodyObj.get("Deposit");
		            	 Double depositVal = (Double) (depositObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(depositObj.get("Amount").toString()));
			            	//String depositVal = deciFormat.format(depositObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.get("Amount")).toString();
			            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Deposit", deciFormat.format(depositVal));
		            }
		            
		            if (jsonBodyObj.get("Check") != null) {
		            	
		            	JSONObject checkObj = (JSONObject)jsonBodyObj.get("Check");
		            
		            	 Double checkVal = (Double) (checkObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(checkObj.get("Amount").toString()));
			            	//String checkVal = deciFormat.format(checkObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(checkObj.get("Amount")).toString();
			            
		            	// Added after Payement.Number implementation
	            		 String checkNum = checkObj.get("Number").toString();
		            	 
		                paymentParts += GetMergedPaymentPart(loopBlockTwo, checkNum ,"Check" ,deciFormat.format(checkVal));
		            }
		            
		            if (jsonBodyObj.get("FC") != null) {
		            	JSONObject FCObj = (JSONObject)jsonBodyObj.get("FC");
		            	 Double fcVal = (Double) (FCObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(FCObj.get("Amount").toString()));
			            	//String fcVal = deciFormat.format(FCObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(FCObj.get("Amount")).toString();
			            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Foreign Currency",	deciFormat.format(fcVal) );
		            }
					
		            // Added another 3 tender types(rarely used)
		            
		            if (jsonBodyObj.get("Payments") != null) {
		            	
		            	JSONArray paymentsArr = (JSONArray)jsonBodyObj.get("Payments");
		            	
		            	for (Object object : paymentsArr) {
							
		            		JSONObject tempPaymentObj = (JSONObject)object;
		            		 Double paymentVal = (Double) (tempPaymentObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempPaymentObj.get("Amount").toString()));
			            		//String paymentVal = deciFormat.format(tempPaymentObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00"): deciFormat.format(tempPaymentObj.get("Amount")).toString();
			            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Payments", deciFormat.format(paymentVal ));
						}
		            	
		                
		            }
		            
		            if (jsonBodyObj.get("TravelerCheck") != null) {
		            	JSONObject travelerCheckObj = (JSONObject)jsonBodyObj.get("TravelerCheck");
		            	 Double travelerCheckVal = (Double) (travelerCheckObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(travelerCheckObj.get("Amount").toString()));
			            	//String travelerCheckVal = deciFormat.format(travelerCheckObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00"): deciFormat.format(travelerCheckObj.get("Amount")).toString(); 
			            	
		            	// Added after Payement.Number implementation
	            		 String travCheckNum = travelerCheckObj.get("Number").toString();
		            	 
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo, travCheckNum ,"Traveler Check",deciFormat.format(travelerCheckVal));
		            }
		            
		            
		            if (jsonBodyObj.get("FCCheck") != null) {

		            	JSONObject FCCheckObj = (JSONObject)jsonBodyObj.get("FCCheck");
		            	 Double fcCheckval = (Double) (FCCheckObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(FCCheckObj.get("Amount").toString()));
			            	//String fcCheckval =  deciFormat.format(FCCheckObj.get("Amount")).toString().isEmpty() ?  Double.parseDouble("0.00") :  deciFormat.format(FCCheckObj.get("Amount")).toString();
			            	
		            	 // Added after Payement.Number implementation
	            		 String fcCheckNum = FCCheckObj.get("Number").toString();
		            	 
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo, fcCheckNum ,"FC Check",deciFormat.format(fcCheckval));
		            }
		            

				
				/*if (jsonBodyObj.get("Cash") != null)
				{
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","CASH",
	                		deciFormat.format((((JSONObject)jsonBodyObj.get("Cash")).get("Amount")).equals("") ? Double.parseDouble("0.00"):(((JSONObject)jsonBodyObj.get("Cash")).get("Amount")).toString()) );
				}
			 
				if (jsonBodyObj.get("StoreCredit") != null)
				{
	            	JSONObject tempStoreCreditObj = null;
	            	try {
	            		tempStoreCreditObj = (JSONObject)jsonBodyObj.get("StoreCredit");
	            	} catch(ClassCastException e) {
	            		tempStoreCreditObj = (JSONObject)((JSONArray)jsonBodyObj.get("StoreCredit")).get(0);
	            	}	
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","SC",
	                		deciFormat.format(tempStoreCreditObj.get("Amount").equals("") ? Double.parseDouble("0.00"):tempStoreCreditObj.get("Amount").toString()) );
	            }
	            
	            if (jsonBodyObj.get("CreditCard") != null)
	            {
	            	JSONArray creditCardArr = (JSONArray)jsonBodyObj.get("CreditCard");
	            	for (Object object : creditCardArr) {
	            		JSONObject tempObj = (JSONObject)object;
	            		paymentParts += GetMergedPaymentPart(loopBlockTwo,
	            				tempObj.get("Number").toString(),
	                    		tempObj.get("Type").toString(),
	                    		deciFormat.format(tempObj.get("Amount").equals("") ? Double.parseDouble("0.00"): tempObj.get("Amount").toString()) );
					}
	            }	
	            
	            if (jsonBodyObj.get("DebitCard") != null)
	            {
	            	JSONObject tempDebitCreditObj = null;
	            	try {
	            		tempDebitCreditObj = (JSONObject)jsonBodyObj.get("DebitCard");
	            	} catch(ClassCastException e) {
	            		tempDebitCreditObj = (JSONObject)((JSONArray)jsonBodyObj.get("DebitCard")).get(0);
	            	}
	            	
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,
	                		tempDebitCreditObj.get("Number").toString(),
	                		"DEBIT",
	                		deciFormat.format(tempDebitCreditObj.get("Amount").equals("") ? Double.parseDouble("0.00"): tempDebitCreditObj.get("Amount").toString())  );
	            }
	            
	            if (jsonBodyObj.get("Gift") != null)
	            {
	            	JSONObject tempGiftObj = null;
	            	try {
	            		tempGiftObj = (JSONObject)jsonBodyObj.get("GiftCertificate");
	            	} catch(ClassCastException e) {
	            		tempGiftObj = (JSONObject)((JSONArray)jsonBodyObj.get("GiftCertificate")).get(0);
	            	}
	            	
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,
	                    "",
	                    "GiftCertificate",
	                    deciFormat.format(tempGiftObj.get("Amount").equals("")? Double.parseDouble("0.00"):tempGiftObj.get("Amount").toString()) );
	            }
	            
	            if (jsonBodyObj.get("GiftCard") != null)
	            {
	            	JSONObject tempGiftCardObj = null;
	            	try {
	            		tempGiftCardObj = (JSONObject)jsonBodyObj.get("GiftCard");
	            	} catch(ClassCastException e) {
	            		tempGiftCardObj = (JSONObject)((JSONArray)jsonBodyObj.get("GiftCard")).get(0);
	            	}
	            	
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","GC",
	                		deciFormat.format(tempGiftCardObj.get("Amount").equals("")?Double.parseDouble("0.00"):tempGiftCardObj.get("Amount").toString()) );
	            }
	            
	            if (jsonBodyObj.get("Charge") != null)
	            {
	            }
	            
	            if (jsonBodyObj.get("COD") != null)
	            {
	            }
	            
	            if (jsonBodyObj.get("Deposit") != null)
	            {
	            }
	            
	            if (jsonBodyObj.get("Check") != null)
	            {
	            	JSONObject checkObj = (JSONObject)jsonBodyObj.get("Check");
	                paymentParts += GetMergedPaymentPart(loopBlockTwo,
	                    "","Check" + "-" + checkObj.get("Number"),
	                    deciFormat.format(checkObj.get("Amount").equals("") ? Double.parseDouble("0.00"):checkObj.get("Amount").toString()) );
	            }
	            
	            if (jsonBodyObj.get("FC") != null)
	            {
	            }*/
				
				if(paymentParts != null) {
					templateContentStr = templateContentStr.replace(loopBlockTwo, paymentParts);
				}
			}
			return templateContentStr;
		}
		// Added for any card Masking
				public static String maskCardNumber(String ccnum){
				        int total = ccnum.length();
				        int endlen = 4;
				        int masklen = total - endlen ;
				        StringBuffer maskedbuf = new StringBuffer();
				        for(int i=0;i<masklen;i++) {
				            maskedbuf.append('X');
				        }
				        maskedbuf.append(ccnum.substring(masklen, total));
				        String masked = maskedbuf.toString();
				     
				        return masked;
			    }
				
				
				
				private String replaceBillTo_ShipToPhValues(String templateContentStr, Map<String, String> userJSONSettingHM,JSONObject jsonBodyObj,
						String[] billToSetPlaceHoldersArr , String[] shipToSetPlaceHoldersArr) throws BaseServiceException{

					
					JSONObject jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
					
					if(templateContentStr.indexOf(billToSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(billToSetPlaceHoldersArr[1]) != -1) 
					{
						String loopBlockBillTo = templateContentStr.substring(templateContentStr.indexOf(billToSetPlaceHoldersArr[0]) + billToSetPlaceHoldersArr[0].length(),
								templateContentStr.indexOf(billToSetPlaceHoldersArr[1]));
						logger.debug("BillTo HTML is :"+ loopBlockBillTo);
						
						//String replacedStr = replaceLoopBlock(loopBlockOne, userJSONSettingHM, jsonBodyObj);
						isBillTo = replaceLoopBlockBillTo(loopBlockBillTo, userJSONSettingHM, jsonReceiptObj);
					}
					
					if(templateContentStr.indexOf(shipToSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(shipToSetPlaceHoldersArr[1]) != -1) 
					{
						String loopBlockShipTo = templateContentStr.substring(templateContentStr.indexOf(shipToSetPlaceHoldersArr[0]) + shipToSetPlaceHoldersArr[0].length(),
								templateContentStr.indexOf(shipToSetPlaceHoldersArr[1]));
						logger.debug("ShipTo HTML is :"+ loopBlockShipTo);
						
						isShipTo = replaceLoopBlockBillTo(loopBlockShipTo, userJSONSettingHM, jsonReceiptObj);
						
					}
					return templateContentStr;
				
				}
				
				
				
				
			private boolean replaceLoopBlockBillTo(String loopBlock, Map<String,String> userJSONSettingHM,JSONObject jsonReceiptObj) {
					try {
						
						Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(loopBlock);
						//JSONObject jsonObj = null;
						while(matcher.find()) {
							logger.info("--PLACE HOLDERS FOUND --"+ matcher.group(0));
							logger.info("--1--"+ userJSONSettingHM.get(matcher.group(0)));
							if(userJSONSettingHM.get(matcher.group(0)) == null) {
								logger.info("*** Place holder value is not found in HashMap ***");
								return false;
							}
							String[] arrayElement = userJSONSettingHM.get(matcher.group(0)).split(Pattern.quote("."));
							logger.info("arrayElement[0]" + arrayElement[0]);
							logger.info("arrayElement[1]" + arrayElement[1]);
							Object JsonObj = jsonReceiptObj.get(arrayElement[1]);
							if(JsonObj != null && !JsonObj.toString().trim().isEmpty())	return true;
						}
						return false;
					} catch(Exception e) {
						logger.error("Exception ::" , e);
						return false;
					} 
				}

				
	 			
				
				
				
				
				
				
				
				
				
	}
