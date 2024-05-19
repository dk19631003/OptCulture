package org.mq.optculture.business.loyalty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.AutosmsUrls;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.AutoSmsUrlDaoForDML;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ReplacePlaceHolders;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class LoyaltyAutoCommGenerator {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	
	
	public void sendEnrollTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId, String password) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
			if(templateId == -1){
				
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			
			/*message = message.replace("<OrganisationName>", user.getUserOrganization().getOrganizationName())
			     	  .replace("[CardNumber]", ""+mbrshipNo).replace("[CardPin]", cardPin);*/
			
			String subject = "Welcome to [OrganizationName]'s Loyalty Program!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			message = replaceMembershipPwd(message, password);
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(),subject, 
					contactId, loyaltyId);
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	
	public void sendGiftIssueTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTISSUE);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTISSUE);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
					
			String subject = "Thank you for purchasing Gift Card from [OrganizationName]!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	}
	
	public void sendTierUpgdTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TIERUPGRADE);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TIERUPGRADE);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			
			String subject = "[OrganizationName]'s Loyalty Program - Membership Upgraded!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);
						
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	public void sendEarnBonusTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			String subject = "[OrganizationName]'s Loyalty Program - Earned Additional Reward!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);			
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);

			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	
	public void sendEarnBonusTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId,long trxId,LoyaltyThresholdBonus bonus) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			//Long thrldId=null;
			if(bonus!=null){
				
				//thrldId=bonus.getThresholdBonusId();
				if(bonus.getEmailTempId()!=null){
					templateId=bonus.getEmailTempId();
					logger.info("template id from threhold==="+templateId);
				}
			}
			logger.info("template id==="+templateId);
			String message = "";
		if(templateId!=null){
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			/*ContactsLoyaltyDao contactsLoyaltyDao = null;
			contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");	
			ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyId);*/
			LoyaltyTransactionChildDao	loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionChildDao");	
			LoyaltyTransactionChild transaction=loyaltyTransactionChildDao.findTransactionByTrxId(trxId);
			LoyaltyThresholdBonus threshold=null;
			
			logger.info("Transaction Id::"+trxId+"    And Obj::"+transaction);
			/*if(transaction.getEarnType().equals("Amount"))
			message=message.replace("|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|",transaction.getEarnedAmount()!=null ?transaction.getEarnedAmount()+"":"0.0");
			else
				message=message.replace("|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|",transaction.getEarnedPoints()!=null ?transaction.getEarnedPoints()+"":"0");
			message=message.replace("|^GEN_loyaltyPointsBalance / DEFAULT=Not Available^|", transaction.getPointsBalance()!=null?transaction.getPointsBalance()+"":"Not Available");
			message=message.replace("|^GEN_loyaltyMembershipCurrencyBalance / DEFAULT=Not Available^|", transaction.getAmountBalance()!=null?transaction.getAmountBalance()+"":"Not Available");
			*/
			if(bonus!=null){
				
				message=getCustomFields(message,transaction,bonus);
			}
			
			String subject = "[OrganizationName]'s Loyalty Program - Earned Additional Reward!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);			
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
		}

			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	private String getCustomFields(String content,LoyaltyTransactionChild transaction,LoyaltyThresholdBonus threshold) {
		content = content.replace("|^", "[").replace("^|", "]");
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
				ph = m.group(1); //.toUpperCase()
				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
			  } 
			}// while
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}
        if(totalPhSet.size()>0){
        	return getContactPhValue(content,totalPhSet,transaction,threshold);
        }
		return content;

	}
	private String getContactPhValue( String tempHtmlContent, Set<String> totalPhSet,LoyaltyTransactionChild transaction,LoyaltyThresholdBonus threshold){
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		try {
	
			String value=Constants.STRING_NILL;

			String preStr = Constants.STRING_NILL; 

			for (String cfStr : totalPhSet) {
				preStr = cfStr;
				if(cfStr.startsWith("GEN_")) {
					cfStr = cfStr.substring(4);
					String defVal="";
					int defIndex = cfStr.indexOf('=');
					if(defIndex != -1) {
						defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
					} // if
					if(OCConstants.CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE.equalsIgnoreCase(cfStr)){
						if(transaction.getEarnType().equals("Amount"))
						value = transaction.getEarnedAmount() != null ?  decimalFormat.format(transaction.getEarnedAmount()) : defVal;
						else
							value=transaction.getEarnedPoints()!=null? transaction.getEarnedPoints().longValue() +" Points":defVal;
						if(value != null && !value.trim().isEmpty()) {
							value = ( value.equals("--") &&  defVal != null) ? defVal : value;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
						} else {
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
						}
					}
					else if(OCConstants.CAMPAIGN_PH_LOYALTY_LAST_THRESHOLD_LEVEL.equalsIgnoreCase(cfStr)){
						logger.info("Before Threshold level");
						if(threshold.getEarnedLevelType().equals("Amount")||transaction.getEarnType().equals("LPV"))
						value = threshold.getEarnedLevelValue() != null ?  decimalFormat.format(threshold.getEarnedLevelValue()) : defVal;
						else
							value=threshold.getEarnedLevelValue()!=null? threshold.getEarnedLevelValue().longValue() +" Points":defVal;
						if(value != null && !value.trim().isEmpty()) {
							logger.info("CAMPAIGN_PH_LOYALTY_LAST_THRESHOLD_LEVEL"+value);
							value = ( value.equals("--") &&  defVal != null) ? defVal : value;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
						} else {
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							logger.info("CAMPAIGN_PH_LOYALTY_LAST_THRESHOLD_LEVEL (def)"+value);

						}
					}
					else if(OCConstants.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(cfStr)){
						value = transaction.getPointsBalance() != null ?  transaction.getPointsBalance().longValue()+" Points" : defVal;
						if(value != null && !value.trim().isEmpty()) {
							value = ( value.equals("--") &&  defVal != null) ? defVal : value;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
						} else {
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							
						}
					}
					else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(cfStr)){
						value = transaction.getAmountBalance() != null ?  decimalFormat.format(transaction.getAmountBalance()) : defVal;
						if(value != null && !value.trim().isEmpty()) {
							value = ( value.equals("--") &&  defVal != null) ? defVal : value;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
																				
						} else {
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							
						}
					}
			}
		} 
		} catch (Exception e) {
			logger.error("Exception while adding the General Fields as place holders ", e);
		}
		return tempHtmlContent;
	}
	
	private String getLastBonusValue(Long loyaltyId,String transactionType, String defVal,Long userId) {
		logger.info("--Start of getLastBonusValue--");
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, transactionType,userId);
		if(loyaltyTransactionChild != null){
			if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount())+" & "+loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() == null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
			}
			else if(loyaltyTransactionChild.getEarnedAmount() == null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else{
				loyaltyPlaceholder = defVal;
			}
		}
		else{
			loyaltyPlaceholder = defVal;
		}
		logger.info("--Exit of getLastBonusValue--");
		return loyaltyPlaceholder;
	}//getLastBonusValue

	public void sendRewardExpiryTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId,  Long contactId, Long loyaltyId) {
		logger.info("saving reward expiry template...");
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
  					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			
			String subject = "[OrganizationName]'s Loyalty Program - Reward Expiring!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);
						
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	
	
	 public void sendMembershipExpiryTemplate(String emailId, Long tempId, Users user,
				Long contactObjID, Long loyaltyId) {

			try {
				logger.debug("-------entered sendMembershipExpiryTemplate---------");
				CustomTemplatesDao customTemplatesDao=(CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
				EmailQueueDao emailQueueDao=(EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML=(EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				CustomTemplates custTemplate = null;
				String message = "";
				custTemplate = customTemplatesDao.findCustTemplateById(tempId);
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_LOYALTYMEMBSHIPEXPIRY);
				if(custTemplate == null) {
					message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_LOYALTYMEMBSHIPEXPIRY);
				}
				else {
					if(custTemplate != null) {
						if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
						  message = custTemplate.getHtmlText();
						}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null)message = myTemplates.getContent();
						}
				  }
				}
				
				String subject = "[OrganizationName]'s Loyalty Program - Membership Expiring!";
				subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());

				EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL, message, "Active", emailId, user, MyCalendar.getNewCalendar()
						, subject, contactObjID, loyaltyId);
				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);
				logger.debug("-------exit  sendMembershipExpiryTemplate---------");
			}catch (Exception e) {
				logger.error("Exception  ::", e);
			}

		}

	 public void sendGiftMembershipExpiryTemplate(String emailId, Long tempId,
				Users user, Long contactObjId, Long loyaltyId) {

			try {
				logger.debug("-------entered sendGiftMembershipExpiryTemplate---------");
				CustomTemplatesDao customTemplatesDao=(CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
				EmailQueueDao emailQueueDao=(EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML=(EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				CustomTemplates custTemplate = null;
				String message = "";
				custTemplate = customTemplatesDao.findCustTemplateById(tempId);
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTMEMBSHIPEXPIRY);
				if(custTemplate == null) {
					message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTMEMBSHIPEXPIRY);
				}
				else {
					if(custTemplate != null) {
						if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
						  message = custTemplate.getHtmlText();
						}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null)message = myTemplates.getContent();
						}
				  }
				}
				
				String subject = "Your [OrganizationName]'s Gift Card Is Expiring!";
				subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());

				EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL, message, "Active", emailId, user, MyCalendar.getNewCalendar()
						, subject, contactObjId, loyaltyId);
				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);
				logger.debug("-------exit  sendGiftMembershipExpiryTemplate---------");
			}catch (Exception e) {
				logger.error("Exception  ::", e);
			}

		}

	
	public void sendGiftExpiryTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId,  Long contactId, Long loyaltyId) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTAMTEXPIRY);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTAMTEXPIRY);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			
			String subject = "Your [OrganizationName]'s Gift Amount Is Expiring!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(),subject, 
					contactId, loyaltyId);
						
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	
	public void sendEnrollSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo, String password) {

		logger.info("Entered sendEnrollSMSTemplate...");
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String message = "";
			String senderId="";
			logger.info("template id >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}
			message = replaceMembershipPwd(message, password);
			
			//replace all place holders here including Coupons
			
			
			if(user.isEnabled() && user.isEnableSMS()) {
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_DETAILS
					, OCConstants.ASQ_STATUS_PROCESSING, toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
			sendSMS(autoSmsQueue);
			}
			}catch(Exception e){
			logger.error("Exception in placing loyalty template enroll in sms queue...", e);
		}

	}
	public void sendSMS(AutoSmsQueue autoSmsqueue) {
		

		
		try {
			Long userId = null;
			Users user = null;
			String messageHeader = Constants.STRING_NILL;
			OCSMSGateway ocgateway = null;
			logger.debug("running for the user "+userId);
		AutoSmsQueueDao autoSmsQueueDao = (AutoSmsQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
		AutoSmsQueueDaoForDML autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
		
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		SMSSuppressedContactsDao smsSuppressedContactsDao=(SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);


		CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
		
			List<AutoSmsQueue> updatedList = new ArrayList<AutoSmsQueue>();
			logger.debug("msg q id ==="+autoSmsqueue.getId());
			String footerMsg="";
			int charCount =0;
			int usedCount =0;
			userId = autoSmsqueue.getUserId();
			user = usersDao.find(userId);
			if(!user.isEnabled() || !user.isEnableSMS()) return;
			ocgateway = GatewayRequestProcessHelper.getOcSMSGateway(user, SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			
			if(ocgateway == null) {
				throw new BaseServiceException("no gateway is available ");
			}
			if(!ocgateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocgateway, 1))  {
				logger.debug("low credits with clickatell");
				return ;
			}
				
			String toMobileNo = autoSmsqueue.getToMobileNo();
			Users currUser = user;
			Contacts contact = null;
			if(autoSmsqueue.getContactId() != null) {
				
				ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				logger.info("smsqueue.getContactId() = "+autoSmsqueue.getContactId());
				
				
				 contact = contactsDao.findById(autoSmsqueue.getContactId());
				logger.info("contact object with cid ="+contact);
				
				//Mobile number validation
				if( toMobileNo == null || toMobileNo.trim().equals("")) {
					
					if(contact.getMobilePhone() != null && 
							!contact.getMobilePhone().isEmpty()) {
						toMobileNo = contact.getMobilePhone();
					}
					else {
						if(logger.isInfoEnabled()) logger.info("no 'TO' mobile number found , returning....");
						autoSmsqueue.setStatus(OCConstants.ASQ_STATUS_FAILURE);
						//autoSmsQueueDao.saveOrUpdate(smsqueue);
						autoSmsQueueDaoForDML.saveOrUpdate(autoSmsqueue);
						return;
					}
				}
			}
			
			//toMobileNo = Utility.phoneParse(toMobileNo);
			if(toMobileNo != null) {
				toMobileNo = toMobileNo.trim();
				if(currUser.getUserOrganization().isRequireMobileValidation()){
					if(!toMobileNo.startsWith(user.getCountryCarrier().toString()) && 
							(toMobileNo.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
							&& toMobileNo.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())	) {
						
						toMobileNo = user.getCountryCarrier().toString()+toMobileNo;
					}
				}
			}
			
			
			if( autoSmsqueue.getMessage() == null || autoSmsqueue.getMessage().trim().equals("")) {
				if(logger.isInfoEnabled()) logger.info("no message is found , returning....");
				return;
			}
			
			
			String msgContent = autoSmsqueue.getMessage();
			//msgContent = messageHeader != null ? messageHeader +" "+msgContent : msgContent;
			//msgContent = msgContent + footerMsg;
			//replace placeholders
			
			//msgContent = msgContent.replaceAll("|^", "[").replaceAll("^|", "]");
			
			msgContent = replaceDatePh(msgContent);
			logger.info("msgContent after replacing date ph = "+msgContent);
			
			Set<String> totalPhSet = getPhSet(msgContent);
			logger.info("totalPhSet = "+totalPhSet);
			
			
			/*CouponCodesDao couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			MessagesDao messagesDao = (MessagesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MESSAGES_DAO);
			
			
			// check if the promo is valid to send auto-SMS
			if(totalPhSet != null && totalPhSet.size() > 0) {
				String msgStr = "";
				Coupons coupon = null;
				Long couponId = null; 
				for (String eachPh : totalPhSet) {
					if(!eachPh.startsWith("CC_") ) continue;
					
						//only for CC
						String[] strArr = eachPh.split("_");
						
						if(logger.isInfoEnabled()) logger.info("Filling  Promo-codes with Id = "+strArr[1]);
						try {
							couponId = Long.parseLong(strArr[1]);
							
						} catch (NumberFormatException e) {
							couponId = null;
						}
						
						if(couponId == null) {
							continue;
						}
						coupon = couponsDao.findById(couponId);
						
						if(coupon == null) {
							msgStr =  "Auto-SMS Type : " + smsqueue.getType() + "\r\n";
							msgStr = msgStr +" \r\n Status : Could not be sent \r\n";
							msgStr = msgStr + "Auto-SMS for type '"+smsqueue.getType()+"' could not be sent as you have added  Promo-code: "+eachPh +" \r\n" ;
							msgStr = msgStr + "This  Promo-code no longer exists, you might have deleted it.  \r\n";
							
							if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not avalable: "+ eachPh);
							
							Messages msgs = new Messages("Auto-SMS", "Auto-SMS sending failed ",
									msgStr, Calendar.getInstance(), "Inbox", false, "Info", user);

							messagesDao.saveOrUpdate(msgs);
							return;
							
							
						}
						
						//to drop the sending sms when it has a barcode link in it and coupon is not as barcode
						String appshortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
						if(msgContent.toLowerCase().contains(appshortUrl.toLowerCase() +"["+eachPh.toLowerCase()+"]" )) {
							
							if(!coupon.getEnableBarcode()) {
								
								msgStr =  "Auto-SMS Type : " + smsqueue.getType() + "\r\n";
								msgStr =  msgStr +" \r\n Status : Could not be sent \r\n";
								msgStr = msgStr + "Auto-SMS for type '"+smsqueue.getType()+"' could not "
										+ "be sent as you have added a Barcode link: "+(appshortUrl.toLowerCase() +"["+eachPh.toLowerCase()+"]") +" \r\n" ;
								msgStr = msgStr + "This Promo-code no longer enabled as barcode.  \r\n";
								
								if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not a barcode: "+ eachPh);
								Messages msgs = new Messages("Auto-SMS", "Auto-SMS sending failed ",
										msgStr, Calendar.getInstance(), "Inbox", false, "Info", user);

								messagesDao.saveOrUpdate(msgs);
								return;
								
								
							}
							
						}
						
						//only for running coupons
						if(coupon.getStatus().equals(Constants.COUP_STATUS_EXPIRED) || 
								coupon.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {
							
							msgStr =  "Auto-SMS Type : " + smsqueue.getType() + "\r\n";
							msgStr =  msgStr +" \r\n Status : Could not be sent \r\n";
							msgStr = msgStr + "Auto-SMS could not be sent as you have added  Promo-code: "+coupon.getCouponName() +" \r\n" ;
							msgStr = msgStr + "This  Promo-code's status is "+coupon.getStatus()+" and  valid period is from "+ 
							MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
							MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
							
							if(logger.isWarnEnabled()) logger.warn(coupon.getCouponName() + "  Promo-code is not in running state, Status : "+ coupon.getStatus());
							Messages msgs = new Messages("Auto-SMS", "Auto-SMS sending failed ",
									msgStr, Calendar.getInstance(), "Inbox", false, "Info", user);

							messagesDao.saveOrUpdate(msgs);
							return;
							
						}//if
						
						if( coupon.getAutoIncrCheck() == true ) {
							continue;
						}
						else if(coupon.getAutoIncrCheck() == false) {
							//need to decide only when auto is false
							long couponCodeCount = couponCodesDao.getCouponCodeCountByStatus(couponId, Constants.COUP_CODE_STATUS_INVENTORY);
							if(couponCodeCount < 1 ) {
								
								msgStr =  "Auto-SMS Type : " + smsqueue.getType() + "\r\n";
								msgStr =  msgStr +" \r\n Status : Could not be sent \r\n";
								msgStr = msgStr + "Auto-SMS campaign could not be sent as you have added  Promo-code : "+coupon.getCouponName() +" \r\n" ;
								msgStr = msgStr + "Available  Promo-codes you can send :"+couponCodeCount+" \r\n";
								
								if(logger.isWarnEnabled()) logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
								Messages msgs = new Messages("Auto-SMS", "Auto-SMS sending failed ",
										msgStr, Calendar.getInstance(), "Inbox", false, "Info", user);
								messagesDao.saveOrUpdate(msgs);
								return;
							}
						
						}//else 
				}//for
				
			}//if
*/			
			List<AutosmsUrls> urlList = new ArrayList<AutosmsUrls>();
			Set<AutosmsUrls> autosmsUrlList = new HashSet<AutosmsUrls>();
			Object[] retObjArr = getUrls(msgContent, userId);
			Set<String> urlSet = (Set<String>) retObjArr[0];
			Map<String, String> urlMap = (Map<String, String>) retObjArr[1];
			msgContent = (String) retObjArr[2];
			logger.info("auto sms msg content" +msgContent);
			String shortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
			logger.info("urlSet size" +urlSet.size());
			if (urlSet != null && urlSet.size() > 0) {
				AutosmsUrls autosmsUrls = null;
				for (String url : urlSet) {

					String[] codeTokenArr = url.split(shortUrl);
					String typeOfUrl = OCConstants.SHORTURL_TYPE_SHORTCODE;
					
					if (codeTokenArr[1].startsWith(OCConstants.SHORTURL_CODE_PREFIX_U))
						typeOfUrl = OCConstants.SHORTURL_TYPE_SHORTCODE;
					/*else if (couponTypeMap.containsKey(codeTokenArr[1])) {
						if (couponTypeMap.get(codeTokenArr[1]).equals(Constants.COUP_GENT_TYPE_MULTIPLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE;
						else if (couponTypeMap.get(codeTokenArr[1]).equals(Constants.COUP_GENT_TYPE_SINGLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_SINGLE;// startsWith(OCConstants.SHORTURL_CODE_PREFIX_CC)// ?);
					}*/
					autosmsUrls = new AutosmsUrls(autoSmsqueue.getId(), null, url,codeTokenArr[1], typeOfUrl);
					if (urlMap != null && urlMap.containsKey(url))
						autosmsUrls.setOriginalUrl(urlMap.get(url));
						urlList.add(autosmsUrls);
				}
				AutoSmsUrlDaoForDML autoSmsUrlDaoForDML = (AutoSmsUrlDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTOSMS_URL_DAO_FOR_DML);
				if (urlList.size() > 0) {
					autoSmsUrlDaoForDML.saveByCollection(urlList);
					autosmsUrlList.addAll(urlList);
					urlList.clear();
				}

			}
			
			
			
			
			
			
			if(totalPhSet != null && totalPhSet.size() > 0) {
				logger.info("totalPhSet1 = "+totalPhSet);
				
				//fetch daos 
				
				RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName("retailProSalesDao");
				OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName("organizationStoresDao");
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
				MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName("mailingListDao");
				
				ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
				
				msgContent = replacePlaceHolders.getAutoSMSPhValue(autoSmsqueue.getUserId(), contact, msgContent,totalPhSet,urlSet,
						autosmsUrlList,OCConstants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS, toMobileNo, user, autoSmsqueue.getId(), autoSmsqueue.getLoyaltyId());
				
			}
			
			if( autoSmsqueue.getSenderId() == null || autoSmsqueue.getSenderId().trim().equals(Constants.STRING_NILL)) {
				if(logger.isInfoEnabled()) logger.info("no senderid is found , returning....");
				return;
			}
			logger.info("msgContent after PH replacement = "+msgContent);
			charCount = msgContent.length();
		    usedCount =1;
		    if(charCount>160) usedCount=(charCount/160) + 1;
		    
		    if( (  (user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount() ) ) <  usedCount) {
		    //alert to support
		    Calendar now=Calendar.getInstance();
		    Utility.sendCampaignFailureAlertMailToSupport(user,"Type:AutoSMS","","",now,"Low credits","","","");
		    return;
		    }
		    
			//String msgId = captiwayToSMSApiGateway.sendSingleSms(ocgateway, msgContent, toMobileNo, autoSmsqueue.getSenderId());
			String msgId = captiwayToSMSApiGateway.sendSingleSms(ocgateway, msgContent, toMobileNo, autoSmsqueue.getSenderId()
					,autoSmsqueue.getTemplateRegisteredId(), autoSmsqueue.getId());
			/**
			 * Used SMS Count
			 */
			try {
				
				//int updatedCount = usersDao.updateUsedSMSCount(user.getUserId(), 1);
				//int updatedCount = usersDaoForDML.updateUsedSMSCount(user.getUserId(), 1);
			    int updatedCount = usersDaoForDML.updateUsedSMSCount(user.getUserId(), usedCount);
				logger.info("updatedCountupdatedCountupdatedCount .."+updatedCount);
				

				/**
				 * Update SMS Queue.
				 */
				SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
				smsQueueHelper.updateSMSQueue(toMobileNo, msgContent, Constants.SMS_MSG_TYPE_AUTOSMS, user, autoSmsqueue.getSenderId());
				
				
			} catch (Exception e) {
				logger.error("Exception while updating used sms count",e);
			}
			autoSmsqueue.setStatus(OCConstants.ASQ_STATUS_SENT);
			autoSmsqueue.setToMobileNo(toMobileNo);
			autoSmsqueue.setMessage(msgContent);
			autoSmsqueue.setMessageId(msgId);
			autoSmsqueue.setDlrStatus(OCConstants.SMS_SENT_STATUS_STATUS_PENDING);
			autoSmsqueue.setTemplateRegisteredId(autoSmsqueue.getTemplateRegisteredId());
			updatedList.add(autoSmsqueue);
		if(updatedList != null && updatedList.size() > 0) {
			//autoSmsQueueDao.saveByCollection(updatedList);
			autoSmsQueueDaoForDML.saveByCollection(updatedList);
		}
		
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	
	}
	
private String replaceDatePh(String msgContent) {
		
		Set<String> datePhSet = getDateFields(msgContent);
		String content = msgContent.replace("|^", "[").replace("^|", "]");
		logger.info("datePhSet inside replace date ph = "+datePhSet);
		if(datePhSet != null && datePhSet.size()>0) {
			
			for (String symbol : datePhSet) {
				 if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
					if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
						Calendar cal = MyCalendar.getNewCalendar();
						content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
					}
					else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
						Calendar cal = MyCalendar.getNewCalendar();
						cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
						content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
					}
					else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
						
						try {
							String[] days = symbol.split("_");
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
							content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY));
						} catch (Exception e) {
							logger.debug("exception in parsing date placeholder");
						}
					}//else if
				}//if
			}//for
		}
		return content;
	}

private Set<String> getDateFields(String content) {
	
	content = content.replace("|^", "[").replace("^|", "]");
	
	String cfpattern = "\\[([^\\[]*?)\\]";
	Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
	Matcher m = r.matcher(content);

	String ph = null;
	Set<String> dateFieldsSet = new HashSet<String>();

	try {
		while(m.find()) {

			ph = m.group(1); //.toUpperCase()
			if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

			if(ph.startsWith(Constants.DATE_PH_DATE_)){
				dateFieldsSet.add(ph);
			}
			
		} // while
		
		//logger.debug("+++ Exiting : "+ totalPhSet);
	} catch (Exception e) {
		logger.error("Exception while getting the symbol place holders ", e);
	}

	if(logger.isInfoEnabled()) logger.info("symbol PH  Set : "+ dateFieldsSet);

	return dateFieldsSet;
}

private Object[] getUrls(String smsMsgContent, Long userId) {
	logger.info("get auto sms url");
	int options = 0;
	options |= 128; // This option is for Case insensitive
	options |= 32;

	Object[] retObjArr = new Object[3];

	Set<String> urlSet = new HashSet<String>();
	Set<AutosmsUrls> autosmsUrlList = new HashSet<AutosmsUrls>();

	if (smsMsgContent == null)
		return null;

	try {
		String appshortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
		Pattern p = Pattern.compile(PropertyUtil.getPropertyValue(OCConstants.SMS_URL_PATTERN),	options);
		Matcher m = p.matcher(smsMsgContent);
		String linkUrl = null;
		UrlShortCodeMappingDao urlShortCodeMappingDao = null;
		try {
			urlShortCodeMappingDao = (UrlShortCodeMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.URL_SHORTCODE_MAPPING_DAO);
		} catch (Exception e1) {
			logger.error("No bean found with given id");
			return null;
		}
		Map<String, String> urlMap = new HashMap<String, String>();
		String shortCodeStr = Constants.STRING_NILL;
		while (m.find()) {
			linkUrl = m.group().trim();
			if ((linkUrl.toLowerCase().startsWith(appshortUrl))) {
				urlSet.add(linkUrl);
				String code = linkUrl.split(appshortUrl)[1];
				if (code.startsWith(OCConstants.SHORTURL_CODE_PREFIX_U)) {
					if (!shortCodeStr.isEmpty())
						shortCodeStr += Constants.DELIMETER_COMMA;
						shortCodeStr += "'" + code + "'";
				} else if (code.startsWith(OCConstants.SHORTURL_CODE_PREFIX_COUPONPH)) {
					urlMap.put(linkUrl, linkUrl);
				}else{
                       if (!shortCodeStr.isEmpty())
                           shortCodeStr += Constants.DELIMETER_COMMA;
                       shortCodeStr += "'" + code + "'";
                   }
			}

		} // while
		if (!shortCodeStr.isEmpty()) {
			List<UrlShortCodeMapping> retList = urlShortCodeMappingDao.findBy(userId, shortCodeStr);
			if (retList != null) {// to know the actual url of all the
									// shorten urls

				for (UrlShortCodeMapping urlShortCodeMapping : retList) {
					if (urlMap.containsKey(appshortUrl + urlShortCodeMapping.getShortCode()))
						continue;
					urlMap.put(appshortUrl + urlShortCodeMapping.getShortCode(), urlShortCodeMapping.getUrlContent());

				}

			}

		}

		retObjArr[0] = urlSet;
		retObjArr[1] = urlMap;
		retObjArr[2] = smsMsgContent;
		logger.info("get auto sms url exit" + retObjArr);
	} catch (Exception e) {
		logger.error("** Exception : Problem while getting the URL set ", e);
	}
	return retObjArr;

}

	
	private Set<String> getPhSet(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
				if(ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				}else if(ph.startsWith("REF_CC_")) {
					totalPhSet.add(ph);
				}
			} // while
			
			if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}

		return totalPhSet;
	}
	

	public void sendWebEnrollSMSTemplate(String invoiceAmount,Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo, String password) {

		logger.info("Entered sendEnrollSMSTemplate...");
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String message = "";
			String senderId="";
			logger.info("template id >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}
			message = getreplacedValueFor(invoiceAmount,message);
			message = replaceMembershipPwd(message, password);
			
			logger.info("user status "+user.isEnabled());
			if(user.isEnabled()) {
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_DETAILS
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
			}}catch(Exception e){
			logger.error("Exception in placing loyalty template enroll in sms queue...", e);
		}

	}

	public void sendGiftIssueSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
		
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
		}
	}
	
	
	
	
	public void sendSpecialRewardTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId, SpecialReward itemBased, String pointsEarned) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_EMAIL_TEMPLATE_TYPE_SPECIAL_REWARDS);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_EMAIL_TEMPLATE_TYPE_SPECIAL_REWARDS);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			//??Have to make changes		
			String subject = "Congrats! You've just unlocked a new loyalty reward!";
			//subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			if(message != null && !message.isEmpty()){
				if( itemBased != null  && itemBased.isAssociatedWithFBP() && itemBased.getPromoCode() != null){
					String desc = itemBased.getDescription();
					if(desc != null &&  !desc.isEmpty()){
						String curr =  Utility.countryCurrencyMap.get(user.getCountryType());
						desc = desc.replace("[PHCurr]",(curr == null ? Utility.countryCurrencyMap.get(Constants.SMS_COUNTRY_US) : curr) );
						logger.debug("desc==="+desc);
						message = message.replace("[DiscountDescription]", desc);
					}
					message = message.replace("[DiscountCode]", itemBased.getPromoCode() != null ? itemBased.getPromoCode() :"").
							replace("[DiscountCodeName]", itemBased.getPromoCodeName() != null ? itemBased.getPromoCodeName() : "").replace("[PointsEarned]", pointsEarned != null ? pointsEarned : "");
					//message = message.replace("[PointsEarned]", itemBased.getPromoCode()).replace("[DiscountCodeName]", itemBased.getPromoCodeName());
				}
				EmailQueue emailQueue = new EmailQueue(custTemplate.getTemplateId(), Constants.EQ_TYPE_SPECIAL_REWARDS, 
						message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
						contactId, loyaltyId);
				
				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	}
	
	
	
	public void sendAdjustmentTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId,String receiptAmount) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTYADJUSTMENT);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTYADJUSTMENT);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			//??Have to make changes		
			String subject = "Congrats! You've just unlocked a new loyalty reward!";
			//subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			message = getreplacedValueForAdj(receiptAmount,message);
			EmailQueue emailQueue = new EmailQueue(custTemplate.getTemplateId(), Constants.EQ_TYPE_LOYALTY_ADJUSTMENT, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	}
	
	


	public void sendAdjustmentSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo,String receiptAmount) {
		
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTYADJUSTMENT);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}
			message = getreplacedValueForAdj(receiptAmount,message);
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, Constants.EQ_TYPE_LOYALTY_ADJUSTMENT
					, "Active", toMobileNo, accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
		}
	}
	
	public void sendLoyaltyIssueTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId) {
		
		try{
			
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_ISSUANCE);
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_ISSUANCE);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
					
			String subject = "Thank you for purchasing from [OrganizationName]!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_ISSUANCE, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
					contactId, loyaltyId);
			
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	}
	
	
	
public void sendLoyaltyIssueSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, 
		String toMobileNo, String receiptNumber,  String receiptAmount) {
		
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTYISSUANCE);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			message = message.replace("[Receipt#]", receiptNumber);
			message = getreplacedValueFor(receiptAmount, message);
			
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_ISSUANCE
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
		}
	}
	
public void sendLoyaltyRedemptionTemplate(Long templateId, String mbrshipNo, String cardPin, Users user, 
		String emailId, String firstName, Long contactId, Long loyaltyId) {
	
	try{
		
		
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
		EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		CustomTemplates custTemplate = null;
		String message = "";
		message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTION);
		if(templateId == -1){
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTION);
		}
		else{
			custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			if(custTemplate != null) {
				if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
				  message = custTemplate.getHtmlText();
				}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
				  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
				  if(myTemplates != null)message = myTemplates.getContent();
				}
		  }
		}
				
		String subject = "Thank you for purchasing from [OrganizationName]!";
		subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
		
		EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_REDEMPTION, 
				message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
				contactId, loyaltyId);
		
		
		//emailQueueDao.saveOrUpdate(emailQueue);
		emailQueueDaoForDML.saveOrUpdate(emailQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template email in email queue...", e);
		}
}

public void sendLoyaltyRedemptionSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
	
	try{

		OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
		UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
		AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
		AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
		AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
		SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
		SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
		AutoSMS autoSms = null;
		OCSMSGateway ocSMSGateway = null;
		UserSMSGateway userSMSGateway = null;
		String senderId ="";
		String message = "";
		logger.info("templateId >>> "+templateId);
		if(templateId == -1){
			String senderIdWithOrWithoutComma = null;

			userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
			senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
			String[] senderIdArr;
			senderIdArr = senderIdWithOrWithoutComma.split(",");
			senderId = senderIdArr[0];
			senderId = senderId.trim();
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTYREDEMPTION);
			if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
			{
				message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
			}else{
				message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
			}
		}
		else{
			autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
			message = autoSms.getMessageContent();
			senderId = autoSms.getSenderId();
			if(message.contains("[Organization Name]")){
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}	
			}
		}

		//String senderId = "";
		String accountType = null;

		accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

		//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

		//if(userSMSSenderId != null) {
		//		senderId = userSMSSenderId.getSenderId();
		//}

		AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_REDEMPTION
				, OCConstants.ASQ_STATUS_PROCESSING, toMobileNo, 
				accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
		if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
			autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
		}
		if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
			autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
		}
		//smsQueueDao.saveOrUpdate(autoSmsQueue);
		smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		sendSMS(autoSmsQueue);
	}catch(Exception e){
		logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
	}
}	
	
	
	public void sendSpecialRewardIssueSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
		
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.AUTO_EMAIL_TEMPLATE_TYPE_SPECIAL_REWARDS);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, Constants.EQ_TYPE_SPECIAL_REWARDS
					, OCConstants.ASQ_STATUS_PROCESSING, toMobileNo, accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
			sendSMS(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
		}
	}

	
	
	public String sendOtpMessagesSMSTemplate(Long templateId, Users user, String OTPCode,
			String toMobileNo) {
			
		//receip no,receipt amunt removed
		String message = "";
			try{
				OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
				UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
				AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
				AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
				AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
				SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
				SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
				AutoSMS autoSms = null;
				OCSMSGateway ocSMSGateway = null;
				UserSMSGateway userSMSGateway = null;
				String senderId ="";
				//String message = "";
				logger.info("templateId >>> "+templateId);
				if(templateId == -1  ){
					String senderIdWithOrWithoutComma = null;

					userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
					ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
					senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
					String[] senderIdArr;
					senderIdArr = senderIdWithOrWithoutComma.split(",");
					senderId = senderIdArr[0];
					senderId = senderId.trim();
					message = PropertyUtil.getPropertyValueFromDB(OCConstants.OTPMESSAGES);
				
				/*	if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}*/
				}
				else{
					autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
					message = autoSms.getMessageContent();
					senderId = autoSms.getSenderId();
				/*	if(message.contains("[Organization Name]")){
						if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
						{
							message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
						}else{
							message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
						}	
					}*/
				}

				//String senderId = "";
				String accountType = null;

				accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

				UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

				if(userSMSSenderId != null) {
						senderId = userSMSSenderId.getSenderId();
				}

		
			}catch(Exception e){
				logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
			}
		logger.info("the sms msg is"+message);
		return message;
	}

	public String sendRedemptionOtpSMSTemplate(Long templateId, Users user, String OTPCode,
			String toMobileNo) {
	
		String message = "";
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			//String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1  ){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.REDEMPTIONOTP);
			
			/*	if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}*/
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
			/*	if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}*/
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			if(userSMSSenderId != null) {
					senderId = userSMSSenderId.getSenderId();
			}

	
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift issue in sms queue...", e);
		}
	logger.info("the sms msg is"+message);
	return message;
	
	
		
		
	
	}
	
	
	public String sendOtpMessagesTemplate(Long templateId,  Users user, String OTPCode,
			String emailId  ) {
		
		String message = "";
		try{
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
		//	String message = "";
		
		//	message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_OTPMESSAGE);
		
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_OTPMESSAGE);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
					
			String subject = "";
			subject =  user.getUserOrganization().getOrganizationName();
			
		//	EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_OTP_MAIL, 
		//			message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
		//			contactId, loyaltyId);
			//emailQueueDao.saveOrUpdate(emailQueue);
		//	emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	
	return message;
	}
	
	public String sendRedemptionOtpTemplate(Long templateId,  Users user, String OTPCode,
			String emailId  ) {
	
		String message = "";
		try{
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
		//	String message = "";
		
		//	message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_OTPMESSAGE);
		
			if(templateId == -1){
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTIONOTP);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
					
			String subject = "";
			subject =  user.getUserOrganization().getOrganizationName();
			
		//	EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_OTP_MAIL, 
		//			message, "Active", emailId, user, MyCalendar.getNewCalendar(), subject, 
		//			contactId, loyaltyId);
			//emailQueueDao.saveOrUpdate(emailQueue);
		//	emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
	
	return message;
		
	
	}
	
public void sendTierUpgdSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
		
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			String senderId="";
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_TIER_UPGRADATION
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);

			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template tier upgd in sms queue...", e);
		}

	}
	
	public void sendEarnBonusSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
		
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String message = "";
			String senderId="";
			logger.info("template id >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;
				
				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}
			
			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}
					AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_EARNING_BONUS
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
					if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
						autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
					}
					if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
						autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
					}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template earn bonus in sms queue...", e);
		}
		
	}
//public void sendEarnBonusSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo,long trxId,Long thrldId) {
	public void sendEarnBonusSMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo,long trxId,LoyaltyThresholdBonus bonus) {
		
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String message = "";
			String senderId="";
			//Long thrldId=null;
			if(bonus!=null){
				
				//thrldId=bonus.getThresholdBonusId();
				if(bonus.getSmsTempId()!=null){
					templateId=bonus.getSmsTempId();
				}
			}
			logger.info("template id >>> "+templateId);
			if(templateId!=null){
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;
				
				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}
			
			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}
			if(trxId!=0){
			LoyaltyTransactionChildDao	loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionChildDao");	
			LoyaltyTransactionChild transaction=loyaltyTransactionChildDao.findTransactionByTrxId(trxId);
			
			logger.info("Transaction Id::"+trxId+"    And Obj::"+transaction);
		/*if(transaction.getEarnType().equals("Amount"))
			message=message.replace("|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|",transaction.getEnteredAmount()!=null ?transaction.getEnteredAmount()+"":"0.0");
			else
				message=message.replace("|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|",transaction.getEarnedPoints()!=null ?transaction.getEarnedPoints()+"":"0");
			message=message.replace("|^GEN_loyaltyPointsBalance / DEFAULT=Not Available^|", transaction.getAmountBalance()!=null?transaction.getAmountBalance()+"":"Not Available");
	*/	
			if(bonus!=null){
				
				message=getCustomFields(message,transaction,bonus);
			}
			}
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_EARNING_BONUS
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
						
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
			}
		}catch(Exception e){
			logger.error("Exception in placing loyalty template earn bonus in sms queue...", e);
		}
		
	}
	public void sendRewardAmtExpirySMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {
		logger.info("saving reward amount expiry sms template...");
		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			UserSMSGateway userSMSGateway = null;
			OCSMSGateway ocSMSGateway = null;
			String senderId ="";
			String message = "";
			
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_REWARD_EXPIRY
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template reward expiry in sms queue...", e);
		}

	}

	public void sendMembershipExpirySMSTemplate(String mobileNo,Long smsTempId,Users user, 
			Long contactObjId, Long loyaltyId) {
		try {

			if(user.isEnableSMS()) {

				logger.debug("-------entered sendAutoCommSMS---------");

				AutoSMSDao autoSMSDao=(AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
				OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
				UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
				AutoSmsQueueDao autoSmsQueueDao=(AutoSmsQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
				AutoSmsQueueDaoForDML autoSmsQueueDaoForDML=(AutoSmsQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
				SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
				SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
				AutoSMS autoSMS = null;
				OCSMSGateway ocSMSGateway = null;
				UserSMSGateway userSMSGateway = null;
				String message = null;
				String senderId = "";
				String accountType = null;

				autoSMS = autoSMSDao.getAutoSmsTemplateById(smsTempId);

				logger.info("autoSMS >>> "+autoSMS);
				if(autoSMS == null) {
					String senderIdWithOrWithoutComma = null;

					userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
					ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
					senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
					String[] senderIdArr;
					senderIdArr = senderIdWithOrWithoutComma.split(",");
					senderId = senderIdArr[0];
					senderId = senderId.trim();
					message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY);
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}

					accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

					//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());
					//if(userSMSSenderId != null) {
					//		senderId = userSMSSenderId.getSenderId();
					//}
				}
				else {
					message = autoSMS.getMessageContent();
					if(message.contains("[Organization Name]")){
						if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
						{
							message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
						}else{
							message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
						}	
					}
					accountType = autoSMS.getMessageType();
					senderId = autoSMS.getSenderId();
				}

				AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY,
						"Active",mobileNo,accountType,senderId,Calendar.getInstance(),
						user.getUserId(),contactObjId, loyaltyId);
				logger.debug("autoSmsQueue--------------------"+autoSmsQueue);
				//autoSmsQueueDao.saveOrUpdate(autoSmsQueue);
				
				if(autoSMS!=null && autoSMS.getAutoSmsId() !=null) {
					autoSmsQueue.setTemplateId(autoSMS.getAutoSmsId());
				}
				if(autoSMS!=null && autoSMS.getTemplateRegisteredId() !=null) {
					autoSmsQueue.setTemplateRegisteredId(autoSMS.getTemplateRegisteredId());
				}
				autoSmsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
				logger.debug("-------exit  sendAutoCommSMS---------");
			}

		}
		catch(Exception e) {
			logger.error("Exception  ::", e);
		}

	}
	
	public void sendGiftAmtExpirySMSTemplate(Long templateId, Users user, Long cid, Long loyaltyId, String toMobileNo) {

		try{

			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			logger.info("templateId >>> "+templateId);
			if(templateId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(templateId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift expiry in sms queue...", e);
		}

	}
	
	public void sendGiftMembershipExpirySMSTemplate(String toMobileNo,Long smsTempId, Users user, Long cid, Long loyaltyId) {
		
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId ="";
			String message = "";
			
			logger.info("smsTempId >>> "+smsTempId);
			if(smsTempId == -1){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();

				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			else{
				autoSms = autoSmsDao.getAutoSmsTemplateById(smsTempId);
				message = autoSms.getMessageContent();
				senderId = autoSms.getSenderId();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}
			}

			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//		senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY
					, "Active", toMobileNo,accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			if(autoSms!=null && autoSms.getAutoSmsId() !=null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift expiry in sms queue...", e);
		}

	}
	public void sendTransferSMS(ContactsLoyalty sourceLty, ContactsLoyalty destLty, Users user) {
		
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TRANSFER);
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			String senderIdWithOrWithoutComma = null;

			userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
			senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
			String[] senderIdArr;
			senderIdArr = senderIdWithOrWithoutComma.split(",");
			senderId = senderIdArr[0];
			senderId = senderId.trim();
			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {

			//		senderId = userSMSSenderId.getSenderId();
			//logger.debug("===sender ids are ! null====="+senderId);
			//}

			logger.debug("===accountType====="+accountType);
			Map<String, ContactsLoyalty> mobiles = new HashMap<String, ContactsLoyalty>();
			if(sourceLty.getContact().getMobilePhone() != null && !sourceLty.getContact().getMobilePhone().isEmpty())
				mobiles.put(sourceLty.getContact().getMobilePhone(), sourceLty) ;

			if(destLty.getContact().getMobilePhone() != null && !destLty.getContact().getMobilePhone().isEmpty())
				mobiles.put(destLty.getContact().getMobilePhone(), destLty) ;
			logger.debug("===mobiles size====="+mobiles.size());
			boolean isSameMobile = false;
			String previousMobile = Constants.STRING_NILL;
			for (String mobile : mobiles.keySet()) {
				logger.debug("===for mobile====="+mobile);
				if(!previousMobile.isEmpty() && (previousMobile.length() > mobile.length() && previousMobile.endsWith(mobile) || 
						mobile.length()>previousMobile.length() && mobile.endsWith(previousMobile))) isSameMobile = true;
				if(!isSameMobile) {
					logger.debug("===isSameMobile====="+isSameMobile);
					String finalMessage = prepareTransferContent(message, mobiles.get(mobile), sourceLty.getCardNumber()+"", destLty.getCardNumber()+"", user.getUserOrganization().getOrganizationName());

					logger.debug("saving for mobile==="+mobile);
					AutoSmsQueue autoSmsQueue = new AutoSmsQueue(finalMessage, OCConstants.ASQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP
							, "Active", mobile,accountType, senderId, Calendar.getInstance(), user.getUserId(),
							mobiles.get(mobile).getContact().getContactId(), mobiles.get(mobile).getLoyaltyId());
					//smsQueueDao.saveOrUpdate(autoSmsQueue);
					smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
				}
				previousMobile = mobile;
				
				
			}
			
			
		}catch(Exception e){
			logger.error("Exception in placing loyalty template gift expiry in sms queue...", e);
		}
		
	}
	public void sendTransferEmail(ContactsLoyalty sourceLty, ContactsLoyalty destLty, Users user) {
	
	try{
		
		EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		CustomTemplates custTemplate = null;
		String message = "";
		message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TRANSFER);
	
		String subject = "[Organization Name]: Here's your new card details!";
		subject = subject.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());
		
		Map<String, ContactsLoyalty> meailIds = new HashMap<String, ContactsLoyalty>();
		if(sourceLty.getContact().getEmailId() != null && !sourceLty.getContact().getEmailId().isEmpty())
			meailIds.put(sourceLty.getContact().getEmailId(), sourceLty) ;
		
		if(destLty.getContact().getEmailId() != null && !destLty.getContact().getEmailId().isEmpty())
			meailIds.put(destLty.getContact().getEmailId(), destLty) ;
		
		boolean isSameEmail = false;
		String previousEmail = Constants.STRING_NILL;
		for (String email : meailIds.keySet()) {
			
			if(previousEmail.equalsIgnoreCase(email)) isSameEmail = true;
			if(!isSameEmail) {
				
				String finalMessage = prepareTransferContent(message, meailIds.get(email), sourceLty.getCardNumber()+"", destLty.getCardNumber()+"", user.getUserOrganization().getOrganizationName());
				EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL, 
						finalMessage, "Active", email, user, MyCalendar.getNewCalendar(),subject, 
						meailIds.get(email).getContact().getContactId(), meailIds.get(email).getLoyaltyId());
				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);
				
			}
			previousEmail = email;
			
			
		}
		
					
		}catch(Exception e){
			logger.error("Exception in placing loyalty template email in email queue...", e);
		}
	
	}
	
	public void sendTransferEmailforInventory(ContactsLoyalty sourceLty, ContactsLoyalty destLty, Users user) {
		
		try{
			
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_TRANSFER_INVENTORY);
		
			String subject = "[Organization Name]: Here's your new card details!";
			subject = subject.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());
			
			Map<String, ContactsLoyalty> meailIds = new HashMap<String, ContactsLoyalty>();
			if(sourceLty.getContact().getEmailId() != null && !sourceLty.getContact().getEmailId().isEmpty())
				meailIds.put(sourceLty.getContact().getEmailId(), sourceLty) ;
			
			if(destLty.getContact().getEmailId() != null && !destLty.getContact().getEmailId().isEmpty())
				meailIds.put(destLty.getContact().getEmailId(), destLty) ;
			
			boolean isSameEmail = false;
			String previousEmail = Constants.STRING_NILL;
			for (String email : meailIds.keySet()) {
				
				if(previousEmail.equalsIgnoreCase(email)) isSameEmail = true;
				if(!isSameEmail) {
					
					String finalMessage = prepareTransferContentforInventory(message, meailIds.get(email), sourceLty.getCardNumber()+"", destLty.getCardNumber()+"", user.getUserOrganization().getOrganizationName(),"");
					EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL, 
							finalMessage, "Active", email, user, MyCalendar.getNewCalendar(),subject, 
							meailIds.get(email).getContact().getContactId(), meailIds.get(email).getLoyaltyId());
					//emailQueueDao.saveOrUpdate(emailQueue);
					emailQueueDaoForDML.saveOrUpdate(emailQueue);
					
				}
				previousEmail = email;
				
				
			}
			
						
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
		}


	public String prepareTransferContent(String message, ContactsLoyalty lty, String sourceCard, String destCard, String orgName) {
		
		String finalContent = Constants.STRING_NILL;
		String FN = lty.getContact().getFirstName() != null ? lty.getContact().getFirstName() : "Valued Customer" ;
		sourceCard = sourceCard == null || sourceCard.isEmpty() ? "Not Available" : sourceCard;
		destCard = destCard == null || destCard.isEmpty() ? "Not Available" : destCard;
		finalContent = message.replace("[firstName]", FN).replace("[previousMembershipNumber]", sourceCard).replace("[newMembershipNumber]", destCard).replace("[Organization Name]", orgName);
		return finalContent;
	}
	
     public String prepareTransferContentforInventory(String message, ContactsLoyalty lty, String sourceCard, String destCard, String orgName, String pwd) {
		
		String finalContent = Constants.STRING_NILL;
		String FN = lty.getContact().getFirstName() != null ? lty.getContact().getFirstName() : "Valued Customer" ;
		sourceCard = sourceCard == null || sourceCard.isEmpty() ? "Not Available" : sourceCard;
		destCard = destCard == null || destCard.isEmpty() ? "Not Available" : destCard;
		finalContent = message.replace("[firstName]", FN).replace("[previousMembershipNumber]", sourceCard).replace("[newMembershipNumber]", destCard).replace("[Organization Name]", orgName).replace("[password]", "same as the source card.");
		return finalContent;
	}
	
	public UserSMSSenderId  getsenderIds(Long userId) {
		List<UserSMSSenderId> retSenderIds =  null;
		try{
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			logger.info("the logged user id is====>"+userId);
			Users user = usersDao.findByUserId(userId);
			String type = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());
			retSenderIds = userSMSSenderIdDao.findSenderIdBySMSType(userId, type);
			if(retSenderIds == null) return null;
			return (UserSMSSenderId) retSenderIds.get(0);
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return (UserSMSSenderId) retSenderIds.get(0);
	}
	private String getreplacedValueForAdj(String receiptAmount, String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		content = content.replace("|^", "[").replace("^|", "]");
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String > totalPhSet = new HashSet<String>();
		String cfNameListStr = "";
		
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
			}
			String preStr = "";
			for (String cfStr : totalPhSet) {
				logger.debug("<<<<   cfStr : "+ cfStr);
				preStr = cfStr;
				if(cfStr.startsWith("GEN_")) {
					cfStr = cfStr.substring(4);
					String defVal="";
					int defIndex = cfStr.indexOf('=');

					if(defIndex != -1) {

						defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
					} // if
					
					if(cfStr.equalsIgnoreCase("receiptAmount")){
						String value = receiptAmount;
						try {

							if(value != null && !value.trim().isEmpty()) {

								value = ( value.equals("--") &&  defVal != null) ? defVal : value;
								content = content.replace("["+preStr+"]", Utility.truncateUptoTwoDecimal(value));

							} else {

								value = defVal;
								content = content.replace("["+preStr+"]", value);

							}

						} catch (Exception e) {
							logger.error("Exception while adding the General Fields as place holders ", e);
						}
						break;
					}
				}
			}
			return content;
	}
	private String getreplacedValueFor(String receiptAmount, String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		content = content.replace("|^", "[").replace("^|", "]");
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String > totalPhSet = new HashSet<String>();
		String cfNameListStr = "";

			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
//				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
			}
			String preStr = "";
			for (String cfStr : totalPhSet) {
				logger.debug("<<<<   cfStr : "+ cfStr);
				preStr = cfStr;
				if(cfStr.startsWith("GEN_")) {
					cfStr = cfStr.substring(4);
					String defVal="";
					int defIndex = cfStr.indexOf('=');

					if(defIndex != -1) {

						defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
					} // if
					
					if(cfStr.equalsIgnoreCase("receiptAmount")){
						String value = receiptAmount;
						try {

							if(value != null && !value.trim().isEmpty()) {

								value = ( value.equals("--") &&  defVal != null) ? defVal : value;
								content = content.replace("["+preStr+"]", Utility.truncateUptoTwoDecimal(value));

							} else {

								value = defVal;
								content = content.replace("["+preStr+"]", value);

							}

						} catch (Exception e) {
							logger.error("Exception while adding the General Fields as place holders ", e);
						}
						break;
					}
				}
			}
			return content;
	}
	
	public void sendWebEnrollTemplate(String invoiceAmount,Long templateId, String mbrshipNo, String cardPin, Users user, 
			String emailId, String firstName, Long contactId, Long loyaltyId, String password) {
		
		try{
			
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplates custTemplate = null;
			String message = "";
			message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
			if(templateId == -1){
				
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
			}
			else{
				custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			
			/*message = message.replace("<OrganisationName>", user.getUserOrganization().getOrganizationName())
			     	  .replace("[CardNumber]", ""+mbrshipNo).replace("[CardPin]", cardPin);*/
			
			String subject = "Welcome to [OrganizationName]'s Loyalty Program!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			message = getreplacedValueFor(invoiceAmount,message);
			
			message = replaceMembershipPwd(message, password);
			EmailQueue emailQueue = new EmailQueue(custTemplate != null ? custTemplate.getTemplateId() : null, Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(),subject, 
					contactId, loyaltyId);
			
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			}catch(Exception e){
				logger.error("Exception in placing loyalty template email in email queue...", e);
			}
		
	}
	
	private String replaceMembershipPwd(String content, String password){
		logger.debug("password ==="+password);
		Set<String> totalPhSet = Utility.getDRConTentPlaceHolder(content);
		content = content.replace("|^", "[").replace("^|", "]");
		Iterator<String> phSetItr = totalPhSet.iterator();
		
		while (phSetItr.hasNext()) {
			String cfStr = (String) phSetItr.next();
			
			String preStr = cfStr;
			if(cfStr.startsWith("GEN_")) {
				
				cfStr = cfStr.substring(4);
				String defVal="";
				int defIndex = cfStr.indexOf('=');
				
				if(defIndex != -1) {
					
					defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
					cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
					/*defVal = cfStr.substring(defIndex+1);
					cfStr = cfStr.substring(0,defIndex);*/
				}
				
				if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {
					if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(cfStr)){
						logger.debug("preStr==="+preStr);
						content = content.replace("["+preStr+"]", password);
						break;
					}
					
				}
			}
		}
		return content;
		
	}
	
}
