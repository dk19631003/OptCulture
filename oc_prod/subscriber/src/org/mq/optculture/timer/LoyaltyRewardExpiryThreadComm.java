package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyRewardExpiryThreadComm implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private MembershipProviderComm provider;
	private LoyaltyProgram program;
	private LoyaltyAutoComm autoComm;// = getAutoCommByProgramId(contactLoyalty.getProgramId());
	//private Map<Long, LoyaltyProgram> programMap;
	private Map<Long, LoyaltyProgramTier> tierMap;
	private Users user;
	boolean considerBonus = false;
	boolean onlyBonusExpiration = false;
	public LoyaltyRewardExpiryThreadComm(){}
	public LoyaltyRewardExpiryThreadComm(MembershipProviderComm provider, LoyaltyProgram program,
			Map<Long, LoyaltyProgramTier> tierMap, LoyaltyAutoComm autoComm, Users user) {
		this.provider = provider;
		//this.programMap = programMap;
		this.program = program;
		this.tierMap = tierMap;
		this.autoComm = autoComm;
		this.user = user;
	}
	public  LoyaltyRewardExpiryThreadComm(MembershipProviderComm provider, LoyaltyProgram loyaltyProgram, Users user) {
		this.provider = provider;
		this.program = loyaltyProgram;
		this.onlyBonusExpiration = true;
		this.user = user;
	}
	
	@Override
	public void run() {
		
		//1.source n dest card expiration mail shud go as only one?
		//2. keeping dest lty id in email queue and mergetags values are from source??
		//3.
		
		logger.info("loyalty reward expiry comm thread ..."+Thread.currentThread().getName());
		
		Object[] object = null;
		if(!onlyBonusExpiration){
			
			do{
				try{
					if(program == null) return;
					
					
					object = provider.getMembership();
					
					if(object == null || object[0] == null) return;
					
					ContactsLoyalty contactLoyalty = (ContactsLoyalty)object[0];
					logger.info("membership number = "+contactLoyalty.getCardNumber());
					
					if(contactLoyalty == null || contactLoyalty.getProgramId() == null) continue;
					
					
					
					if(autoComm == null){
						logger.info("Auto comm is null for ...program Id = "+contactLoyalty.getProgramId());
						continue;
					}
					
					contactLoyalty.setConEmail((String)object[1]);
					contactLoyalty.setConMobile((String)object[2]);
					Double[] totExpiredValuesArr = new Double[3];
					try {
						List<Object[]> childMembershipList = getChildMemberships(contactLoyalty);
						if(childMembershipList != null) {
							for (Object[] objArr : childMembershipList) {
								ContactsLoyalty childLoyalty = (ContactsLoyalty)objArr[0];
								childLoyalty.setConEmail((String)objArr[1]);
								childLoyalty.setConMobile((String)objArr[2]);
								childLoyalty.setDestMembership(contactLoyalty);//for each child membership
								Object[] expiredValuesArr = processRewardExpiry(childLoyalty);
								totExpiredValuesArr = calculateTotalExpiredValues(childLoyalty, totExpiredValuesArr, expiredValuesArr, false, autoComm, user);
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception while fetching the childMembershipList ", e);
					}
					
					Object[] expiredValuesArr = processRewardExpiry(contactLoyalty);//for every membership
					totExpiredValuesArr = calculateTotalExpiredValues(contactLoyalty, totExpiredValuesArr, expiredValuesArr, true, autoComm, user);
					considerBonus = program.getExpireBonusList() == null || program.getExpireBonusList().isEmpty();
					if(!considerBonus){
						List<LoyaltyThresholdBonus> bonusList = program.getExpireBonusList();
						for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
							
							 expiredValuesArr = processBonusRewardExpiry(contactLoyalty, loyaltyThresholdBonus);//for every membership
							 
							 totExpiredValuesArr = calculateTotalExpiredValues(contactLoyalty, totExpiredValuesArr, expiredValuesArr, true, loyaltyThresholdBonus, user);
							 totExpiredValuesArr =  new Double[3];
							
						}
						
					}
				}catch(Exception e){
					logger.error("Exception in loyalty rewardexpriy thread ....", e);
				}
			
			}while(object != null);
		}else{
			

			ContactsLoyalty contactLoyalty = null;
			do{
				try{
					if(program == null) return;
					object = provider.getMembership();
					
					if(object == null || object[0] == null) return;
					
					contactLoyalty = (ContactsLoyalty)object[0];
					logger.info("membership number = "+contactLoyalty.getCardNumber());
					
					if(contactLoyalty == null || contactLoyalty.getProgramId() == null) continue;
					contactLoyalty.setConEmail((String)object[1]);
					contactLoyalty.setConMobile((String)object[2]);
					
					Double[] totExpiredValuesArr = new Double[3];
					
					List<LoyaltyThresholdBonus> bonusList = program.getExpireBonusList();
					for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
						
						
						Object[] expiredValuesArr = processBonusRewardExpiry(contactLoyalty, loyaltyThresholdBonus);//for every membership
						totExpiredValuesArr = calculateTotalExpiredValues(contactLoyalty, totExpiredValuesArr, expiredValuesArr, true, loyaltyThresholdBonus, user);
						totExpiredValuesArr =  new Double[3];
						
					}

				}catch(Exception e){
					
				}
			}while(contactLoyalty != null);
		
		}
			
		
			
		
			logger.info("Completed loyalty reward expiry comm...");
		
	}
	
private Map<Long, LoyaltyProgramTier> fetchTiersList(List<LoyaltyProgramTier> tierList) {
		
		Map<Long, LoyaltyProgramTier> tierMap = null;
		String tierIdStr = null;
		
		for(LoyaltyProgramTier tier : tierList){
			
			if(tierIdStr == null){
				tierIdStr = ""+tier.getTierId();
			}
			else{
				tierIdStr += ","+tier.getTierId();
			}
		}
		
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersBytierIdStr(tierIdStr);
			
			if(listOfTiers != null && listOfTiers.size() > 0){
				tierMap = new HashMap<Long, LoyaltyProgramTier>();
				
				for(LoyaltyProgramTier tier : listOfTiers){
					tierMap.put(tier.getTierId(), tier);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching tiers...",e);
		}
		return tierMap;
	}
	
	private List<LoyaltyProgramTier> getAllTiers(Long programID) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getAllTierByProgramId(programID);
	}
	
	
	private Double[] calculateTotalExpiredValues(ContactsLoyalty contactLoyalty, 
			Double[] totExpiredValuesArr, Object[] expiredValuesArr, boolean sendAutoComm, LoyaltyAutoComm autoComm, Users user) throws Exception{
		
		if(expiredValuesArr[0] != null && Double.valueOf(expiredValuesArr[0].toString()) > 0){
			//gift amt
			totExpiredValuesArr[0] = (totExpiredValuesArr[0] != null ? Double.valueOf(expiredValuesArr[0].toString())+ Double.valueOf(totExpiredValuesArr[0].toString()) : Double.valueOf(expiredValuesArr[0].toString()));
		}
		if(expiredValuesArr[1] != null && Double.valueOf(expiredValuesArr[1].toString()) > 0 ){
			//loyalty amt
			totExpiredValuesArr[1] = (totExpiredValuesArr[1] != null ? Double.valueOf(expiredValuesArr[1].toString())+ Double.valueOf(totExpiredValuesArr[1].toString()) : Double.valueOf(expiredValuesArr[1].toString()));
			
		}
		if(expiredValuesArr[2] != null && Double.valueOf(expiredValuesArr[2].toString()) > 0 ){
			//loyalty pts
			totExpiredValuesArr[2] = (totExpiredValuesArr[2] != null ? Double.valueOf(expiredValuesArr[2].toString())+ Double.valueOf(totExpiredValuesArr[2].toString()) : Double.valueOf(expiredValuesArr[2].toString()));
			
		}
		if(sendAutoComm) {
			
			if(totExpiredValuesArr[0] != null && totExpiredValuesArr[0]>0) {
				sendAutoComm(contactLoyalty, autoComm, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT, user);
			}if((totExpiredValuesArr[1] != null && totExpiredValuesArr[1]>0) || (totExpiredValuesArr[2] != null && totExpiredValuesArr[2]>0)) {
				sendAutoComm(contactLoyalty, autoComm, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT, user);
			}
		}
		
		return totExpiredValuesArr;
	}
	
	
	private Double[] calculateTotalExpiredValues(ContactsLoyalty contactLoyalty, 
			Double[] totExpiredValuesArr, Object[] expiredValuesArr, boolean sendAutoComm, LoyaltyThresholdBonus bonus, Users user) throws Exception{
		
		if(expiredValuesArr[0] != null && Double.valueOf(expiredValuesArr[0].toString()) > 0){
			//gift amt
			totExpiredValuesArr[0] = (totExpiredValuesArr[0] != null ? Double.valueOf(expiredValuesArr[0].toString())+ Double.valueOf(totExpiredValuesArr[0].toString()) : Double.valueOf(expiredValuesArr[0].toString()));
		}
		if(expiredValuesArr[1] != null && Double.valueOf(expiredValuesArr[1].toString()) > 0 ){
			//loyalty amt
			totExpiredValuesArr[1] = (totExpiredValuesArr[1] != null ? Double.valueOf(expiredValuesArr[1].toString())+ Double.valueOf(totExpiredValuesArr[1].toString()) : Double.valueOf(expiredValuesArr[1].toString()));
			
		}
		if(expiredValuesArr[2] != null && Double.valueOf(expiredValuesArr[2].toString()) > 0 ){
			//loyalty pts
			totExpiredValuesArr[2] = (totExpiredValuesArr[2] != null ? Double.valueOf(expiredValuesArr[2].toString())+ Double.valueOf(totExpiredValuesArr[2].toString()) : Double.valueOf(expiredValuesArr[2].toString()));
			
		}
		if(sendAutoComm) {
			
			if((totExpiredValuesArr[1] != null && totExpiredValuesArr[1]>0) || (totExpiredValuesArr[2] != null && totExpiredValuesArr[2]>0)) {
				sendBonusAutoComm(contactLoyalty, bonus,  OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT, user);
			}
		}
		
		return totExpiredValuesArr;
	}
	
	
	private void sendAutoComm(ContactsLoyalty contactLoyalty, LoyaltyAutoComm autoComm, String expiryType, Users user) throws Exception{
		
		if(contactLoyalty.getContact() == null || contactLoyalty.getContact().getContactId() == null){
			logger.info("contact is null for membership number = "+contactLoyalty.getCardNumber());
			return;
		}
		
		/*Contacts contact = getContactByCid(contactLoyalty.getContact().getContactId());
		if(contact == null){
			logger.info("contact not found / email address is empty for membership : "+contactLoyalty.getCardNumber());
			return;
		}*/
		
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		String emailID = contactLoyalty.getConEmail();
		boolean sendEmail = (emailID != null && !emailID.isEmpty());
		boolean canSendSMS = user.isEnableSMS() && contactLoyalty.getConMobile() != null && !contactLoyalty.getConMobile().isEmpty()	;
		Long contactId = null;
		if(contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null){
			contactId = contactLoyalty.getContact().getContactId();
		}
		if(autoComm != null){
			
			if(expiryType.equalsIgnoreCase(OCConstants.LOYALTY_EXPIRY_TYPE_GIFT)){
				if(autoComm.getGiftAmtExpiryEmailTmpltId() != null && sendEmail) {
					logger.info("Sending Gift expiry email template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendGiftExpiryTemplate(autoComm.getGiftAmtExpiryEmailTmpltId(),
							""+contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(), user,
							emailID, contactId, contactLoyalty.getLoyaltyId());
				
				}
				if(canSendSMS && autoComm.getGiftAmtExpirySmsTmpltId() != null){
					
					logger.info("Sending Gift expiry sms template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendGiftAmtExpirySMSTemplate(autoComm.getGiftAmtExpirySmsTmpltId(), user, contactId,
							contactLoyalty.getLoyaltyId(), null);
				}
			}else if(expiryType.equalsIgnoreCase(OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT) || 
					expiryType.equalsIgnoreCase(OCConstants.LOYALTY_EXPIRY_TYPE_POINTS)) {
				if(autoComm.getRewardExpiryEmailTmpltId() != null && sendEmail) {
					
					logger.info("Sending Reward expiry email template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendRewardExpiryTemplate(autoComm.getRewardExpiryEmailTmpltId(),
							""+contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(), user,
							emailID,  contactId, contactLoyalty.getLoyaltyId());
					
				}
				if(canSendSMS && autoComm.getRewardExpirySmsTmpltId() != null) {
					
					logger.info("Sending Reward expiry sms template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendRewardAmtExpirySMSTemplate(autoComm.getRewardExpirySmsTmpltId(), user,
							contactId, contactLoyalty.getLoyaltyId(), contactLoyalty.getConMobile());
					
				}
			}
		}
		
	}
	private void sendBonusAutoComm(ContactsLoyalty contactLoyalty, LoyaltyThresholdBonus bonus,  String expiryType, Users user) throws Exception{
		
		

		
		if(contactLoyalty.getContact() == null || contactLoyalty.getContact().getContactId() == null){
			logger.info("contact is null for membership number = "+contactLoyalty.getCardNumber());
			return;
		}
		
		/*Contacts contact = getContactByCid(contactLoyalty.getContact().getContactId());
		if(contact == null){
			logger.info("contact not found / email address is empty for membership : "+contactLoyalty.getCardNumber());
			return;
		}*/
		
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		String emailID = contactLoyalty.getConEmail();
		boolean sendEmail = (emailID != null && !emailID.isEmpty());
		boolean canSendSMS = user.isEnableSMS() && contactLoyalty.getConMobile() != null && !contactLoyalty.getConMobile().isEmpty()	;
		Long contactId = null;
		if(contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null){
			contactId = contactLoyalty.getContact().getContactId();
		}
			
			 if(expiryType.equalsIgnoreCase(OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT) || 
					expiryType.equalsIgnoreCase(OCConstants.LOYALTY_EXPIRY_TYPE_POINTS)) {
				if(bonus.getEmailExpiryTempId() != null && sendEmail) {
					
					logger.info("Sending Reward expiry email template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendRewardExpiryTemplate(bonus.getEmailExpiryTempId(),
							""+contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(), user,
							emailID,  contactId, contactLoyalty.getLoyaltyId());
					
				}
				if(canSendSMS && bonus.getSmsExpiryTempId() != null) {
					
					logger.info("Sending Reward expiry sms template for "+contactLoyalty.getCardNumber());
					autoCommGen.sendRewardAmtExpirySMSTemplate(bonus.getSmsExpiryTempId(), user,
							contactId, contactLoyalty.getLoyaltyId(), contactLoyalty.getConMobile());
					
				}
			}
		
	
		
	}
	private Object[] processBonusRewardExpiry(ContactsLoyalty contactLoyalty, LoyaltyThresholdBonus loyaltyThresholdBonus) throws Exception{
		
		List<LoyaltyThresholdBonus> bonusList = program.getExpireBonusList();
		Object[] expiredValuesArr = new Object[3];
		if((OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactLoyalty.getRewardFlag()) || 
				OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactLoyalty.getRewardFlag())) ){

			
				
				
				if( loyaltyThresholdBonus.getBonusExpiryDateType() != null 
						&& loyaltyThresholdBonus.getBonusExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue()-1));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue()-1)));
					}
					else{
						logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
					}
					String expDate = "";
					if(cal.get(Calendar.MONTH) == 0){
						int year = cal.get(Calendar.YEAR)-1;
						expDate = year+"-12";
					}
					else{
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					logger.info("expDate = "+expDate);
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(),loyaltyThresholdBonus.getThresholdBonusId() );
					
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						long expPoints = 0;
						double expAmt = 0;
						if(expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0){
							expPoints = Long.valueOf(expiryValueArr[1].toString());
						}
						if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
							expAmt = Double.valueOf(expiryValueArr[2].toString());
						}
						
							// update contactloyalty gift balances
							//check if this memebership is closed and transfered to another
							expiredValuesArr[1] = expAmt;
							expiredValuesArr[2] = expPoints;
							
							// create a child transaction
							//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
							
							// reset transaction values to zero.
						
						/*if(expPoints > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
						}
						
						if(expAmt > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
						}*/
					}
				}
		
		}
		return expiredValuesArr;
	}
	
	
	private Object[] processRewardExpiry(ContactsLoyalty contactLoyalty) throws Exception{
		LoyaltyProgramTier tier = null;
		Object[] expiredValuesArr = new Object[3];
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactLoyalty.getRewardFlag())){
			logger.info("membership type = "+contactLoyalty.getRewardFlag());
			//program = programMap.get(contactLoyalty.getProgramId());
			
			if('Y' == program.getGiftAmountExpiryFlag() && program.getGiftAmountExpiryDateType() != null 
					&& program.getGiftAmountExpiryDateValue() != null){
				
				Calendar cal = Calendar.getInstance();
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -program.getGiftAmountExpiryDateValue().intValue());
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*program.getGiftAmountExpiryDateValue().intValue()));
				}
				else{
					logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
				}
				
//				String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
				String expDate = "";
				if(cal.get(Calendar.MONTH) == 0){
					int year = cal.get(Calendar.YEAR)-1;
					expDate = year+"-12";
				}
				else{
					expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
				}
				
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(), considerBonus);
				logger.debug("===============expiryValueArr"+expiryValueArr+"--------------------"+expiryValueArr[2]);
				if(expiryValueArr != null && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					logger.info("expiryValueArr ... expPoints = "+expiryValueArr[1]+" expiryamount = "+expiryValueArr[2]);
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					
					if(expGift > 0){
						
						expiredValuesArr[0] = expGift;
						/*
						
						// Add Gift Amount Expiration template in emailqueue
						if(contactLoyalty.getContact() == null || contactLoyalty.getContact().getContactId() == null){
							logger.info("contact is null for membership number = "+contactLoyalty.getCardNumber());
							continue;
						}
						
						Contacts contact = getContactByCid(contactLoyalty.getContact().getContactId());
						if(contact == null){
							logger.info("contact not found / email address is empty for membership : "+contactLoyalty.getCardNumber());
							continue;
						}
						
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						if(autoComm != null && autoComm.getGiftAmtExpiryEmailTmpltId() != null){
							if(contact != null && contact.getEmailId() != null){
								logger.info("Sending Gift expiry email template for "+contactLoyalty.getCardNumber());
								autoCommGen.sendGiftExpiryTemplate(autoComm.getGiftAmtExpiryEmailTmpltId(),
										""+contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(), contact.getUsers(),
										contact.getEmailId(), contact.getFirstName(), contact.getContactId(), contactLoyalty.getLoyaltyId());
							}
							
						}
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user =userDao.findByUserId(contactLoyalty.getUserId());
						 
						if(user.isEnableSMS() && autoComm != null && autoComm.getGiftAmtExpirySmsTmpltId() != null) {
							Long contactId = null;
							if(contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null){
								contactId = contactLoyalty.getContact().getContactId();
							}
							logger.info("Sending Gift expiry sms template for "+contactLoyalty.getCardNumber());
							autoCommGen.sendGiftAmtExpirySMSTemplate(autoComm.getGiftAmtExpirySmsTmpltId(), user, contactId,
									contactLoyalty.getLoyaltyId(), null);
						}
						continue;
						
					*/}

				}
				
			}
			
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactLoyalty.getRewardFlag())){
			
			/*//check if the card is transfered
			ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			contactLoyalty.setDestMembership(destLoyalty);
*/
			logger.info("membership type = "+contactLoyalty.getRewardFlag());
			//program = programMap.get(contactLoyalty.getProgramId());
			
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
					logger.info("considerTierID "+considerTierID);
			if(considerTierID != null) {
				logger.info("considerTierID "+considerTierID.longValue());
				tier = tierMap.get(considerTierID);
				logger.info("tier "+tier);
				if(tier != null && program.getRewardExpiryFlag() == 'Y' && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equalsIgnoreCase(tier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -tier.getRewardExpiryDateValue().intValue());
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equalsIgnoreCase(tier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*tier.getRewardExpiryDateValue().intValue()));
					}
					else{
						logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
						return expiredValuesArr;
					}
//					String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					String expDate = "";
					if(cal.get(Calendar.MONTH) == 0){
						int year = cal.get(Calendar.YEAR)-1;
						expDate = year+"-12";
					}
					else{
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					logger.info("expiry date = "+expDate);
					
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(), considerBonus);
					logger.debug("===============expiryValueArr"+expiryValueArr+"--------------------"+expiryValueArr[2]);
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						logger.info("expiryValueArr ... expPoints = "+expiryValueArr[1]+" expiryamount = "+expiryValueArr[2]);
						
						
						long expPoints = Long.valueOf(expiryValueArr[1].toString());
						double expAmt = Double.valueOf(expiryValueArr[2].toString());
						
						expiredValuesArr[1] = expAmt;
						expiredValuesArr[2] = expPoints;
						//if(expPoints > 0 && expAmt > 0){
							
							// Add Gift Amount Expiration template in emailqueue
							/*ContactsLoyalty memberShip = contactLoyalty.getDestMembership() != null ? contactLoyalty.getDestMembership() : contactLoyalty;
							if(memberShip.getContact() == null || memberShip.getContact().getContactId() == null){
								logger.info("contact is null for membership number = "+memberShip.getCardNumber());
								continue;
							}
							
							Contacts contact = getContactByCid(memberShip.getContact().getContactId());
							if(contact == null){
								logger.info("contact not found / email address is empty for membership : "+memberShip.getCardNumber());
								continue;
							}
							LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
							if(autoComm != null && autoComm.getRewardExpiryEmailTmpltId() != null){
								if(contact != null && contact.getEmailId() != null){
									logger.info("Sending Reward expiry email template for "+memberShip.getCardNumber());
									autoCommGen.sendRewardExpiryTemplate(autoComm.getRewardExpiryEmailTmpltId(),
											""+memberShip.getCardNumber(), memberShip.getCardPin(), contact.getUsers(),
											contact.getEmailId(), contact.getFirstName(), contact.getContactId(), memberShip.getLoyaltyId());
								}
								
							}
							
							UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
							Users user = userDao.findByUserId(memberShip.getUserId());
							if(user.isEnableSMS() && autoComm != null && autoComm.getRewardExpirySmsTmpltId() != null) { 
								Long contactId = null;
								if(memberShip.getContact() != null && memberShip.getContact().getContactId() != null) {
									contactId = memberShip.getContact().getContactId();
								}
								logger.info("Sending Reward expiry sms template for "+contactLoyalty.getCardNumber());
								autoCommGen.sendRewardAmtExpirySMSTemplate(autoComm.getRewardExpirySmsTmpltId(), user,
										contactId, memberShip.getLoyaltyId(), null);
							}
							continue;
							*/
						//}
						
							}
					
				}
				
				
			}
			
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactLoyalty.getRewardFlag())){
			logger.info("membership type ="+contactLoyalty.getRewardFlag());
			
			//program = programMap.get(contactLoyalty.getProgramId());
			
			if(program.getGiftAmountExpiryFlag() == 'Y' && program.getGiftAmountExpiryDateType() != null 
					&& program.getGiftAmountExpiryDateValue() != null){
				
				Calendar cal = Calendar.getInstance();
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -program.getGiftAmountExpiryDateValue().intValue());
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*program.getGiftAmountExpiryDateValue().intValue()));
				}
				else{
					logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
				}
//				String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
				String expDate = "";
				if(cal.get(Calendar.MONTH) == 0){
					int year = cal.get(Calendar.YEAR)-1;
					expDate = year+"-12";
				}
				else{
					expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
				}
				
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, considerBonus);
				
				if(expiryValueArr != null && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					logger.info("expiryValueArr ... expPoints = "+expiryValueArr[1]+" expiryamount = "+expiryValueArr[2]);
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					expiredValuesArr[0] = expGift;
					//if(expGift > 0){
					/*ContactsLoyalty memberShip = contactLoyalty.getDestMembership() != null ? contactLoyalty.getDestMembership() : contactLoyalty;
					if(memberShip.getContact() == null || memberShip.getContact().getContactId() == null){
						logger.info("contact is null for membership number = "+memberShip.getCardNumber());
						continue;
					}
					
					Contacts contact = getContactByCid(memberShip.getContact().getContactId());
					if(contact == null){
						logger.info("contact not found / email address is empty for membership : "+memberShip.getCardNumber());
						continue;
					}
						
						
						
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						if(autoComm != null && autoComm.getGiftAmtExpiryEmailTmpltId() != null){
							if(contact != null && contact.getEmailId() != null){
								logger.info("Sending Gift expiry email template for "+memberShip.getCardNumber());
								autoCommGen.sendGiftExpiryTemplate(autoComm.getGiftAmtExpiryEmailTmpltId(),
										""+memberShip.getCardNumber(), memberShip.getCardPin(), contact.getUsers(),
										contact.getEmailId(), contact.getFirstName(), contact.getContactId(), memberShip.getLoyaltyId());
							}
							
						}
						
						
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user =userDao.findByUserId(memberShip.getUserId());
						if(user.isEnableSMS() && autoComm != null && autoComm.getGiftAmtExpirySmsTmpltId() != null) {
							Long contactId = null;
							if(memberShip.getContact() != null && memberShip.getContact().getContactId() != null){
								contactId = memberShip.getContact().getContactId();
							}
							logger.info("Sending Gift expiry sms template for "+memberShip.getCardNumber());
							autoCommGen.sendGiftAmtExpirySMSTemplate(autoComm.getGiftAmtExpirySmsTmpltId(), user,
									contactId, memberShip.getLoyaltyId(), null);
						}*/
//						continue;
					//}

				}
				
			}
			//check if the card is transfered
			/*ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			contactLoyalty.setDestMembership(destLoyalty);
*/
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
			
			if(considerTierID != null) {
				tier = tierMap.get(considerTierID);
				
				if(tier != null && program.getRewardExpiryFlag() == 'Y' && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -tier.getRewardExpiryDateValue().intValue());
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*tier.getRewardExpiryDateValue().intValue()));
					}
					else{
						logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
					}
//					String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					String expDate = "";
					if(cal.get(Calendar.MONTH) == 0){
						int year = cal.get(Calendar.YEAR)-1;
						expDate = year+"-12";
					}
					else{
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, considerBonus);
					
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						logger.info("expiryValueArr ... expPoints = "+expiryValueArr[1]+" expiryamount = "+expiryValueArr[2]);
						long expPoints = Long.valueOf(expiryValueArr[1].toString());
						double expAmt = Double.valueOf(expiryValueArr[2].toString());
						expiredValuesArr[1] = expAmt;
						expiredValuesArr[2] = expPoints;
						//if(expPoints > 0 && expAmt > 0){
							
							// Add Gift Amount Expiration template in emailqueue
						/*ContactsLoyalty memberShip = contactLoyalty.getDestMembership() != null ? contactLoyalty.getDestMembership() : contactLoyalty;
						if(memberShip.getContact() == null || memberShip.getContact().getContactId() == null){
							logger.info("contact is null for membership number = "+memberShip.getCardNumber());
							continue;
						}
						
						Contacts contact = getContactByCid(memberShip.getContact().getContactId());
						if(contact == null){
							logger.info("contact not found / email address is empty for membership : "+memberShip.getCardNumber());
							continue;
						}
							
							
							LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
							if(autoComm != null && autoComm.getRewardExpiryEmailTmpltId() != null){
								if(contact != null && contact.getEmailId() != null){
									logger.info("Sending Reward expiry email template for "+memberShip.getCardNumber());
									autoCommGen.sendRewardExpiryTemplate(autoComm.getRewardExpiryEmailTmpltId(),
											""+memberShip.getCardNumber(), memberShip.getCardPin(), contact.getUsers(),
											contact.getEmailId(), contact.getFirstName(), contact.getContactId(), memberShip.getLoyaltyId());
								}
							}
							UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
							Users user =userDao.findByUserId(memberShip.getUserId());
							if(user.isEnableSMS() && autoComm != null && autoComm.getRewardExpirySmsTmpltId() != null ) {
								Long contactId = null;
								if(memberShip.getContact() != null && memberShip.getContact().getContactId() != null){
									contactId = memberShip.getContact().getContactId();
								}
								logger.info("Sending Reward expiry sms template for "+memberShip.getCardNumber());
								autoCommGen.sendRewardAmtExpirySMSTemplate(autoComm.getRewardExpirySmsTmpltId(), user,
										contactId, memberShip.getLoyaltyId(), null);
							}
							continue;
						//}
*/						
					}
				
				
			}
		
			//if(contactLoyalty.getProgramTierId() == null) continue;
				
			}
			
		}
		else{
			logger.info("Invalid loyalty membership..... LOOK THIS MEMBERSHIP...loyaltyId = "+contactLoyalty.getLoyaltyId()+" "
					+ "membership number = "+contactLoyalty.getCardNumber());
		}
		
		return expiredValuesArr;
	}
	
	private List<Object[]> getChildMemberships(ContactsLoyalty destMembership) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		List<Object[]> childMembershipList = loyaltyDao.findChildrenByParentForComm(destMembership.getUserId(), destMembership.getLoyaltyId());
		
		return childMembershipList;
	}
	
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	
	/*private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag);
	}*/
	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, boolean considerBonus) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, considerBonus);
	}
	
	
private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, Long bonusID) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, bonusID);
	}
	/*	
	private LoyaltyProgram getLoyaltyProgram(Long programId) throws Exception {
		
		LoyaltyProgram program = programMap.get(programId);
		if(program == null){
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			program = programDao.findById(programId);
			programMap.put(programId, program);
		}
		return program;
	}
	
	private LoyaltyProgramTier getLoyaltyProgramTier(Long tierId) throws Exception {
		
		LoyaltyProgramTier tier = tierMap.get(tierId);
		if(tier == null){
			LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			tier = tierDao.getTierById(tierId);
			tierMap.put(tierId, tier);
		}
		return tier;
	}
	
	
	
	private void updateMembershipBalances(ContactsLoyalty loyalty, long expPts, double expAmt, double expGift) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		// subtract balances and add expiry balances
		
		if(expGift > 0){
			loyalty.setGiftBalance(loyalty.getGiftBalance() - expGift);
			loyalty.setExpiredGiftAmount(loyalty.getExpiredGiftAmount() == null ? expGift : loyalty.getExpiredGiftAmount() + expGift);
		}
		
		if(expAmt > 0){
			loyalty.setGiftcardBalance(loyalty.getGiftcardBalance() - expAmt);
			loyalty.setExpiredRewardAmount(loyalty.getExpiredRewardAmount() == null ? expAmt : loyalty.getExpiredRewardAmount() + expAmt);
		}
		
		if(expPts > 0){
			loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance() - expPts);
			loyalty.setExpiredPoints(loyalty.getExpiredPoints() == null ? expPts : loyalty.getExpiredPoints() + expPts);
			
		}
		
		loyaltyDao.saveOrUpdate(loyalty);
		
	}
	
	private void resetExpiryTransValues(ContactsLoyalty loyalty, String expDate) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(loyalty.getRewardFlag())){
			
			expiryDao.resetAmountExpiryTransValues(loyalty.getCardNumber(), expDate, loyalty.getRewardFlag());
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(loyalty.getRewardFlag()) ||
				OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(loyalty.getRewardFlag())){
			
			expiryDao.resetAmtAndPtsExpiryTransValues(loyalty.getCardNumber(), expDate, loyalty.getRewardFlag());
		}
		else{
			logger.info("INVALID MEMBERSHIP REWARD TYPE..."+loyalty.getCardNumber());
		}
		
	}
	
	private void createChildTransaction(ContactsLoyalty loyalty, long expPts, double expAmt, String expType) throws Exception {
		
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			//transaction.setTransactionId(transactionId);
			
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			//transaction.setPointsDifference(pointsDiff);
			//transaction.setAmountDifference(amountDiff);
			if(OCConstants.LOYALTY_EXPIRY_TYPE_POINTS.equals(expType)){
				transaction.setEnteredAmount((double)expPts);
			}
			else if(OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT.equals(expType) || OCConstants.LOYALTY_EXPIRY_TYPE_GIFT.equals(expType)){
				transaction.setEnteredAmount((double)expAmt);
			}
			
			transaction.setEnteredAmountType(expType);
			
			transaction.setUserId(loyalty.getUserId());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY);
			transaction.setCardSetId(loyalty.getCardSetId());
			//transaction.setStoreNumber(redemptionRequest.getHeader().getStoreNumber());
			//transaction.setDocSID(redemptionRequest.getHeader().getDocSID());
			
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			loyaltyTransChildDao.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		
	}
	*/
	private Contacts getContactByCid(Long cid) throws Exception {
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	private LoyaltyAutoComm getAutoCommByProgramId(Long programId) throws Exception {
		
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	
	public static void main(String[] args) {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.MONTH, -(12*1));
		String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
		System.out.println(expDate);
		
	}
}
