package org.mq.marketer.campaign.controller.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyIssuanceOCService;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class UpdateLoyaltyTransactionChildForRewardsWebForm extends Thread {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyTransactionChildDao loyaltyTransactionChildDao;
	private FormMapping formMapping;
	private Long contactId;
	
	
	public UpdateLoyaltyTransactionChildForRewardsWebForm(FormMapping formMapping, Long contactId){
		this.formMapping = formMapping;
		this.contactId = contactId;
		
	}

	@Override
	public void run() {
		 try {
		 ServiceLocator locator = ServiceLocator.getInstance();
		 Calendar curentdate = Calendar.getInstance();
		 
		String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
		int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
		logger.info("ServerTime.... "+serverTimeZoneValInt);
		String timezoneDiffrenceMinutes = formMapping.getUsers().getClientTimeZone();
		int timezoneDiffrenceMinutesInt = 0;
		if(timezoneDiffrenceMinutes != null) 
			timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
		logger.info("ClientTime.... "+timezoneDiffrenceMinutesInt);
		timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
		logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
		curentdate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt );
		String curentdateString =  MyCalendar.calendarToString(curentdate, MyCalendar.FORMAT_DATETIME_STYEAR);
		 
		 ContactsLoyalty contactsLoyalty = findLoyaltyListByContactId(formMapping.getUsers().getUserId(),contactId);
		 if(contactsLoyalty!= null && contactsLoyalty.getCardNumber()!=null) {
	     loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)locator.getDAOByName("loyaltyTransactionChildDao");
		 Object[] loyaltyTransactionChild  = loyaltyTransactionChildDao.getLatestDocsidByCreatedDateAndTransctionType(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(),timezoneDiffrenceMinutesInt,curentdateString,OCConstants.LOYALTY_TRANSACTION_ISSUANCE,OCConstants.LOYALTY_TYPE_PURCHASE);
		 if(loyaltyTransactionChild == null) 
			return ; 
		 	//loyaltyRewardTransaction = loyaltyTransactionChildDao.getLatestDocsidByCreatedDateAndTransctionTypeAndDocSId(formMapping.getUsers().getUserId(),contactsLoyalty.getLoyaltyId(),loyaltyTransactionChild.getCreatedDate(), OCConstants.LOYALTY_TYPE_REWARD, loyaltyTransactionChild.getDocSID(), Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
		/* if(loyaltyRewardTransaction != null ) 
			 return ;*/
		 
				
						LoyaltyIssuanceRequest loyaltyIssuanceRequest = new LoyaltyIssuanceRequest();
						MembershipRequest membershipRequest = new MembershipRequest(); 
						RequestHeader header =  new RequestHeader();
						Amount amount =  new Amount();
						LoyaltyUser loyaltyUser = new LoyaltyUser();
						if(formMapping.getIssueRewardType()!=null && !formMapping.getIssueRewardType().isEmpty()) {
							amount.setValueCode(formMapping.getIssueRewardType());
						}
						if(formMapping.getIssueRewardValue()!=null && !formMapping.getIssueRewardValue().isEmpty()) {
							amount.setEnteredValue(formMapping.getIssueRewardValue());
						}
						amount.setType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD);
						String requestId = "FED_"+formMapping.getUsers().getToken()+"_"+System.currentTimeMillis();
						Calendar cal = new MyCalendar(Calendar.getInstance(), null,MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
						header.setRequestId(""+requestId);
						header.setRequestDate(""+cal);
						header.setSourceType(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
						header.setDocSID(loyaltyTransactionChild[0].toString());
						header.setStoreNumber(loyaltyTransactionChild[2] != null ? loyaltyTransactionChild[2].toString() : contactsLoyalty.getPosStoreLocationId());
						
						membershipRequest.setCardNumber(contactsLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD) ? contactsLoyalty.getCardNumber() :"");
						membershipRequest.setPhoneNumber(contactsLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ? contactsLoyalty.getCardNumber() :"");
					
						loyaltyUser.setUserName(Utility.getOnlyUserName(formMapping.getUsers().getUserName()));
						loyaltyUser.setOrganizationId(Utility.getOnlyOrgId(formMapping.getUsers().getUserName()));
						loyaltyUser.setToken(formMapping.getUsers().getUserOrganization().getOptSyncKey());
						
						loyaltyIssuanceRequest.setHeader(header);
						loyaltyIssuanceRequest.setAmount(amount);
						loyaltyIssuanceRequest.setUser(loyaltyUser);
						loyaltyIssuanceRequest.setMembership(membershipRequest);
						
						logger.info(":: created object for loyaltyIssuanceRequest ::");
						
						LoyaltyTransactionParent tranParent = createNewTransaction(); 
						Date date = tranParent.getCreatedDate().getTime();
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
						String transDate = df.format(date);
						
						LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
						loyaltyIssuanceOCService.processIssuanceRequest(loyaltyIssuanceRequest,OCConstants.LOYALTY_ONLINE_MODE, ""+tranParent.getTransactionId(),transDate,OCConstants.DR_TO_LTY_EXTRACTION);
						logger.info(":: returned form processIssuanceRequest ::");
						}
		 }catch (Exception e) {
			logger.error("UpdateLoyaltyTransactionChildForRewardsWebForm ::"+e);
		}
	}
	
	
	private LoyaltyTransactionParent createNewTransaction(){
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private ContactsLoyalty findLoyaltyListByContactId(Long userId, Long contactId) throws Exception {

		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(userId,contactId);
		if (loyaltyList != null && loyaltyList.size() > 0) {
			Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
			ContactsLoyalty latestLoyalty = null;
			ContactsLoyalty iterLoyalty = null;
			while (iterList.hasNext()) {
				iterLoyalty = iterList.next();
				if (latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())) {
					continue;
				}
				latestLoyalty = iterLoyalty;
			}
			contactLoyalty = latestLoyalty;
		}
		return contactLoyalty;
	}

}
