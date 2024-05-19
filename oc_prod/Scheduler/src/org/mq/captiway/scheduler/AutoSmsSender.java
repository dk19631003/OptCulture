package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.beans.AutosmsUrls;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSCampaignUrls;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDao;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDaoForDML;
import org.mq.captiway.scheduler.dao.AutoSmsUrlDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.SMSCampaignUrlDao;
import org.mq.captiway.scheduler.dao.SMSCampaignUrlDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.UrlShortCodeMappingDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.services.CaptiwayToSMSApiGateway;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class AutoSmsSender extends Thread {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public volatile boolean isRunning = false; 
	
	private AutoSMSObjectsQueue autoSMSObjectsQueue;
	
	private Set<String> urlSet;
	
	public Set<String> getUrlSet() {
		return urlSet;
	}
	
	private Set<AutosmsUrls> autosmsUrlList;

	public Set<AutosmsUrls> getAutoSmsUrlList() {
		return autosmsUrlList;
	}

	public AutoSmsSender() {
	}
	
	public AutoSmsSender(AutoSMSObjectsQueue autoSMSObjectsQueue) {
		
		this.autoSMSObjectsQueue = autoSMSObjectsQueue;
	}
	
	private static Object currentRunningObj= null;
	
	public static Object getCurrentRunningObj() {
		return currentRunningObj;
	}
	
	public boolean isRunning() {
		
		return isRunning;
	}
	
	
	public void run() {
		
		try {
			isRunning = true;
			logger.info(" -------------------- just entered AutoSmsSender ------------");
			
			AutoSmsQueue autoSmsQueue = null;
			List<AutoSmsQueue> list = new ArrayList<AutoSmsQueue>();
			Long userId = null;
			Object obj = null;
			
			while ((currentRunningObj = obj =  autoSMSObjectsQueue.getObjFromQueue()) != null) {
				
				autoSmsQueue = (AutoSmsQueue) obj;
				logger.info("in while ----"+autoSmsQueue.getId());
				try {
					if(userId == null) {
						userId = autoSmsQueue.getUserId();
					}
					else if(userId != null && userId == autoSmsQueue.getUserId()){
						list.add(autoSmsQueue);
						continue;
					}
					else if(userId != null && userId != autoSmsQueue.getUserId()){
						sendSMS(list);
						list.clear();
					}
					list.add(autoSmsQueue);
					userId = autoSmsQueue.getUserId();
				}
				catch(Exception e) {
					logger.error("Exception ::",e);
				}

			} // while
			
			if(userId!=null && list.size() > 0){
				sendSMS(list);
				list.clear();
			}
		
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		isRunning = false;
	}//run()

	
	@SuppressWarnings("unchecked")
	public void sendSMS(List<AutoSmsQueue> list) {
		
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
		for(AutoSmsQueue autoSmsqueue : list) {
			logger.debug("msg q id ==="+autoSmsqueue.getId());
			String footerMsg="";
			int charCount =0;
			int usedCount =0;
			if(userId == null) {
				userId = autoSmsqueue.getUserId();
				user = usersDao.find(userId);
				ocgateway = GatewayRequestProcessHelper.getOcSMSGateway(user, SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				
				if(ocgateway == null) {
					throw new BaseServiceException("no gateway is available ");
				}
				if(!ocgateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocgateway, list.size()))  {
					logger.debug("low credits with clickatell");
					return ;
				}
				List<SMSSuppressedContacts> SuprList =  smsSuppressedContactsDao.searchContactsById(user.getUserId(), autoSmsqueue.getToMobileNo());
				if(!SuprList.isEmpty() && SuprList.size()==1) {
						
						logger.info("Autosms cannot be sent for Suppressed number");
						return;
				} 
				//check with header
				
				/*if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {
					SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
					List<SMSSettings> smsSettings = smsSettingsDao.findByUser(userId);
					if(smsSettings != null) {

						SMSSettings optinSettings = null;
						SMSSettings optOutSettings = null;
						SMSSettings helpSettings = null;

						for (SMSSettings eachSMSSetting : smsSettings) {

							if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
							else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
							else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;

						}
						if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
						else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
						else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
						
						if(optOutSettings != null){
							footerMsg = (optOutSettings.getKeyword() != null ?
									("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
											: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));

						}

					}
				}*/
				

				
				
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
						continue;
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
				continue;
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
			Object[] retObjArr = getUrls(msgContent, userId);
			urlSet = (Set<String>) retObjArr[0];
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
				
				ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(retailProSalesDao, organizationStoresDao, 
						contactsLoyaltyDao, mailingListDao, usersDao, user.getUserOrganization().getUserOrgId());
				
				msgContent = replacePlaceHolders.getAutoSMSPhValue(autoSmsqueue.getUserId(), contact, msgContent,totalPhSet,urlSet,
						autosmsUrlList,Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS, toMobileNo, user, autoSmsqueue.getId(), autoSmsqueue.getLoyaltyId());
				
			}
			
			if( autoSmsqueue.getSenderId() == null || autoSmsqueue.getSenderId().trim().equals(Constants.STRING_NILL)) {
				if(logger.isInfoEnabled()) logger.info("no senderid is found , returning....");
				continue;
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
					,autoSmsqueue.getId(),autoSmsqueue.getTemplateRegisteredId());
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
			autoSmsqueue.setDlrStatus(Constants.SMS_SENT_STATUS_STATUS_PENDING);
			autoSmsqueue.setTemplateRegisteredId(autoSmsqueue.getTemplateRegisteredId());
			updatedList.add(autoSmsqueue);
		}
		if(updatedList != null && updatedList.size() > 0) {
			//autoSmsQueueDao.saveByCollection(updatedList);
			autoSmsQueueDaoForDML.saveByCollection(updatedList);
		}
		
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}//sendSMS
	
	
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

		urlSet = new HashSet<String>();
		autosmsUrlList = new HashSet<AutosmsUrls>();

		if (smsMsgContent == null)
			return null;

		try {
			String appshortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
			Pattern p = Pattern.compile(PropertyUtil.getPropertyValue(OCConstants.SMS_URL_PATTERN),	options);
			Matcher m = p.matcher(smsMsgContent);
			String linkUrl = null;
			UrlShortCodeMappingDao urlShortCodeMappingDao = null;
			try {
				urlShortCodeMappingDao = (UrlShortCodeMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.URLSHORTCODEMAPPING_DAO);
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
}
