package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiryUtil;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyRewardExpiryThread implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private MembershipProvider provider;
	private Map<Long, LoyaltyProgramTier> tierMap;
	private LoyaltyProgram loyaltyProgram ;
	boolean considerBonus = false;
	boolean onlyBonusExpiration = false;
	public LoyaltyRewardExpiryThread(MembershipProvider provider, LoyaltyProgram loyaltyProgram, 
			Map<Long, LoyaltyProgramTier> tierMap) {
		this.provider = provider;
		this.loyaltyProgram = loyaltyProgram;
		this.tierMap = tierMap;
	}
	public  LoyaltyRewardExpiryThread(MembershipProvider provider, LoyaltyProgram loyaltyProgram) {
		this.provider = provider;
		this.loyaltyProgram = loyaltyProgram;
		this.onlyBonusExpiration = true;
	}
	
	@Override
	public void run() {
		
		 logger.info("--------- LoyaltyRewardExpiryThread started--------"+ Thread.currentThread().getName());
		if(!onlyBonusExpiration){
			
			ContactsLoyalty contactLoyalty = null;
			
			/*if(contactLoyalty != null){
				logger.info("membership number = "+contactLoyalty.getCardNumber());
			}*/
			
			do{
				try{
					if(loyaltyProgram == null) return;
					contactLoyalty = provider.getMembership();
					
					if(contactLoyalty == null) return;
					
					logger.info("membership number ="+contactLoyalty.getCardNumber());
					Double[] totExpiredValuesArr = new Double[3];
					try {
						considerBonus = loyaltyProgram.getExpireBonusList() == null || loyaltyProgram.getExpireBonusList().isEmpty();
						List<ContactsLoyalty> childMembershipList = getChildMemberships(contactLoyalty);
						if(childMembershipList != null) {
							for (ContactsLoyalty childLoyalty : childMembershipList) {
								
								childLoyalty.setDestMembership(contactLoyalty);//for each child membership
								Object[] expiredValuesArr = processRewardExpiry(childLoyalty, considerBonus);
								totExpiredValuesArr = calculateTotalExpiredValues(childLoyalty, totExpiredValuesArr, expiredValuesArr);
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception while fetching the childMembershipList ", e);
					}
					
					Object[] expiredValuesArr = processRewardExpiry(contactLoyalty, considerBonus);//for every membership
					totExpiredValuesArr = calculateTotalExpiredValues(contactLoyalty, totExpiredValuesArr, expiredValuesArr);
					initiateCreateingChildTransaction(contactLoyalty, totExpiredValuesArr);
					if(!considerBonus){
						
						List<LoyaltyThresholdBonus> bonusList = loyaltyProgram.getExpireBonusList();
						if(bonusList!= null && !bonusList.isEmpty()) {
							String bonusIds = "";
							for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
								if(!bonusIds.isEmpty()) bonusIds+= ",";
								bonusIds += loyaltyThresholdBonus.getThresholdBonusId();
							}
							//for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
								//expiredValuesArr = new Object[3];
								//expiredValuesArr = processBonusRewardExpiry(contactLoyalty, loyaltyThresholdBonus);//for every membership
							expiredValuesArr = processRewardExpiry(contactLoyalty, bonusIds);//for every membership
							totExpiredValuesArr = new Double[3];
							totExpiredValuesArr = calculateTotalExpiredValues(contactLoyalty, totExpiredValuesArr, expiredValuesArr);
							initiateCreateingChildTransaction(contactLoyalty, totExpiredValuesArr);
							
							//}
						}
						
					}
					
				}catch(Exception e){
					logger.error("Exception in loyalty rewardexpriy thread ....", e);
				}
			}while(contactLoyalty != null);
			
			logger.info("completed loyalty rewardexpriy thread ....");
			
		}else{
			LoyaltyTransactionExpiryUtil LoyaltyTransactionExpiryUtil = null;
			do{
				logger.info("started loyalty bonus rewardexpriy thread ....");

				try{
					if(loyaltyProgram == null) return;
					LoyaltyTransactionExpiryUtil = provider.getMembershipForBonusExpiry();
					
					if(LoyaltyTransactionExpiryUtil != null &&
							LoyaltyTransactionExpiryUtil.getLoyaltyId() !=null &&
							 (LoyaltyTransactionExpiryUtil.getExpiryAmount() >0 ||
							 LoyaltyTransactionExpiryUtil.getExpiryPoints()>0)) {
						
					
					
					Double[] totExpiredValuesArr = new Double[3];
					Object[] expiryValueArr = new Object[3];
					
					ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					ContactsLoyalty contactsLoyaty = contactsLoyaltyDao.findAllByLoyaltyId(LoyaltyTransactionExpiryUtil.getTransferedTo() != null
							? LoyaltyTransactionExpiryUtil.getTransferedTo() : LoyaltyTransactionExpiryUtil.getLoyaltyId());
					expiryValueArr[1] = LoyaltyTransactionExpiryUtil.getExpiryAmount();
					expiryValueArr[2] = LoyaltyTransactionExpiryUtil.getExpiryPoints();
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						long expPoints = 0;
						double expAmt = 0;
						if(expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0){
							expAmt = (Double.valueOf(expiryValueArr[1].toString()));
						}
						if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
							expPoints = (Double.valueOf(expiryValueArr[2].toString())).longValue();
						}
							
						expiryValueArr[1] = expAmt;
						expiryValueArr[2] = expPoints;
						updateMembershipBalances(contactsLoyaty, expPoints, expAmt, 0);
						totExpiredValuesArr = calculateTotalExpiredValues(contactsLoyaty, totExpiredValuesArr, expiryValueArr);
						initiateCreateingChildTransaction(contactsLoyaty, totExpiredValuesArr);
						
						
					}
					}
				}catch(Exception e){
					logger.error("Exception ==", e);
				}
				
			}while(LoyaltyTransactionExpiryUtil != null);
			logger.info("completed loyalty bonus rewardexpriy thread ....");
		}
	}
	
	private Double[] calculateTotalExpiredValues(ContactsLoyalty contactLoyalty, 
			Double[] totExpiredValuesArr, Object[] expiredValuesArr) throws Exception{
		
		if((expiredValuesArr[0] != null && Double.valueOf(expiredValuesArr[0].toString()) > 0) 	){
			//gift amt
			totExpiredValuesArr[0] = (totExpiredValuesArr[0] != null ? Double.valueOf(expiredValuesArr[0].toString())+ Double.valueOf(totExpiredValuesArr[0].toString()) : Double.valueOf(expiredValuesArr[0].toString()));
			logger.debug("totExpiredValuesArr[0].longValue() "+totExpiredValuesArr[0].longValue());

			//if(createChildTrx) createChildTransaction(contactLoyalty, 0, totExpiredValuesArr[0], OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
		}
		if(expiredValuesArr[1] != null && Double.valueOf(expiredValuesArr[1].toString()) > 0 ){
			//loyalty amt
			totExpiredValuesArr[1] = (totExpiredValuesArr[1] != null ? Double.valueOf(expiredValuesArr[1].toString())+ Double.valueOf(totExpiredValuesArr[1].toString()) : Double.valueOf(expiredValuesArr[1].toString()));
			logger.debug("totExpiredValuesArr[1].longValue() "+totExpiredValuesArr[1].longValue());
			//if(createChildTrx) createChildTransaction(contactLoyalty, 0, totExpiredValuesArr[1], OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
		}
		if(expiredValuesArr[2] != null && Double.valueOf(expiredValuesArr[2].toString()) > 0 ){
			//loyalty pts
			totExpiredValuesArr[2] = (totExpiredValuesArr[2] != null ? Double.valueOf(expiredValuesArr[2].toString())+ Double.valueOf(totExpiredValuesArr[2].toString()) : Double.valueOf(expiredValuesArr[2].toString()));
			logger.debug("totExpiredValuesArr[2].longValue() "+totExpiredValuesArr[2].longValue());
			//if(createChildTrx) createChildTransaction(contactLoyalty, totExpiredValuesArr[2].longValue(), 0, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
		}
		
		return totExpiredValuesArr;
	}
	private Object[] processBonusRewardExpiry(ContactsLoyalty contactLoyalty, LoyaltyThresholdBonus loyaltyThresholdBonus) throws Exception{
		
		List<LoyaltyThresholdBonus> bonusList = loyaltyProgram.getExpireBonusList();
		Object[] expiredValuesArr = new Object[3];
		if((OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactLoyalty.getRewardFlag()) || 
				OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactLoyalty.getRewardFlag())) ){

			
				
				
				if( loyaltyThresholdBonus.getBonusExpiryDateType() != null 
						&& loyaltyThresholdBonus.getBonusExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue())));
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
							updateMembershipBalances(contactLoyalty, expPoints, expAmt, 0);
							
							// create a child transaction
							//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
							
							// reset transaction values to zero.
							resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, loyaltyThresholdBonus.getThresholdBonusId());
						
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
	private Object[] processRewardExpiry(ContactsLoyalty contactLoyalty, String bonusIds) throws Exception{
		

		
		LoyaltyProgramTier tier = null;
		Object[] expiredValuesArr = new Object[3];
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactLoyalty.getRewardFlag())){
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			
			if('Y' == loyaltyProgram.getGiftAmountExpiryFlag() && loyaltyProgram.getGiftAmountExpiryDateType() != null 
					&& loyaltyProgram.getGiftAmountExpiryDateValue() != null){
				
				Calendar cal = Calendar.getInstance();
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(loyaltyProgram.getGiftAmountExpiryDateValue().intValue()));
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*(loyaltyProgram.getGiftAmountExpiryDateValue().intValue())));
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
				
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(),bonusIds );
				
				if(expiryValueArr != null  && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					
					if(expGift > 0){
						// update contactloyalty gift balances
						//check if this memebership is closed and transfered to another
					//	ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
						
						//contactLoyalty.setDestMembership(destLoyalty);
						
						expiredValuesArr[0] = expGift;
						updateMembershipBalances(contactLoyalty, 0, 0, expGift);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, 0, expGift, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, bonusIds);
					}
				}
			}
		}
		else if( OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactLoyalty.getRewardFlag())){
			//check if the card is transfered
			/*ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			contactLoyalty.setDestMembership(destLoyalty);
*/
			//logger.info("membership type = "+contactLoyalty.getRewardFlag());
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
			
			if(considerTierID != null) {
				
				tier = tierMap.get(considerTierID);
				
				/*if(contactLoyalty.getProgramTierId()== null) continue;
				tier = tierMap.get(contactLoyalty.getProgramTierId());
				 */
				if(tier != null && 'Y' == loyaltyProgram.getRewardExpiryFlag() && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(tier.getRewardExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(tier.getRewardExpiryDateValue().intValue())));
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
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(), bonusIds);
					
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
							updateMembershipBalances(contactLoyalty, expPoints, expAmt, 0);
							
							// create a child transaction
							//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
							
							// reset transaction values to zero.
							resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, bonusIds);
						
						/*if(expPoints > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
						}
						
						if(expAmt > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
						}*/
					}
				}
				
			}
			
		}
		else if( OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactLoyalty.getRewardFlag())){
			//ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			//contactLoyalty.setDestMembership(destLoyalty);
			
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			if('Y' == loyaltyProgram.getGiftAmountExpiryFlag() && loyaltyProgram.getGiftAmountExpiryDateType() != null 
					&& loyaltyProgram.getGiftAmountExpiryDateValue() != null){
				Calendar cal = Calendar.getInstance();
				
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(loyaltyProgram.getGiftAmountExpiryDateValue().intValue()));
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*(loyaltyProgram.getGiftAmountExpiryDateValue().intValue())));
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
				logger.info("expDate = "+expDate);
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, bonusIds);
				
				if(expiryValueArr != null && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					
					if(expGift > 0){
						// update contactloyalty gift balances
						//check if this memebership is closed and transfered to another
						expiredValuesArr[0] = expGift;
						
						updateMembershipBalances(contactLoyalty, 0, 0, expGift);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, 0, expGift, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, bonusIds);
					}
				}
			}
			
			/*if(contactLoyalty.getProgramTierId() == null) continue;
			tier = tierMap.get(contactLoyalty.getProgramTierId());
			*/
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
			
			if(considerTierID != null) {
				
				tier = tierMap.get(considerTierID);
				
				
				if(tier != null && 'Y' == loyaltyProgram.getRewardExpiryFlag() && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(tier.getRewardExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(tier.getRewardExpiryDateValue().intValue())));
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
					logger.info("expDate = "+expDate);
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, bonusIds);
					
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						long expPoints = 0; //Long.valueOf(expiryValueArr[1].toString());
						double expAmt = 0;
						if(expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0){
							expPoints = Long.valueOf(expiryValueArr[1].toString());
						}
						if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
							expAmt = Double.valueOf(expiryValueArr[2].toString());
						}
						
						//if(expPoints > 0 && expAmt > 0){
						//check if this memebership is closed and transfered to another
						
						//contactLoyalty.setDestMembership(destLoyalty);
						// update contactloyalty gift balances
						expiredValuesArr[1] = expAmt;
						expiredValuesArr[2] = expPoints;
						updateMembershipBalances(contactLoyalty, expPoints, expAmt, 0);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, bonusIds);
						//}
						
						/*if(expPoints > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
						}
						
						if(expAmt > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
						}*/
					}
				}
			}
		}
		else{
			logger.info("Invalid loyalty membership..... LOOK THIS MEMBERSHIP...loyaltyId = "+contactLoyalty.getLoyaltyId()+" "
					+ "membership number = "+contactLoyalty.getCardNumber());
		}
		
		return expiredValuesArr;
		
	
		
		
	}
		
		
		
		
	private Object[] processRewardExpiry(ContactsLoyalty contactLoyalty, boolean considerBonus) throws Exception{
		
		LoyaltyProgramTier tier = null;
		Object[] expiredValuesArr = new Object[3];
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactLoyalty.getRewardFlag())){
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			
			if('Y' == loyaltyProgram.getGiftAmountExpiryFlag() && loyaltyProgram.getGiftAmountExpiryDateType() != null 
					&& loyaltyProgram.getGiftAmountExpiryDateValue() != null){
				
				Calendar cal = Calendar.getInstance();
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(loyaltyProgram.getGiftAmountExpiryDateValue().intValue()));
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*(loyaltyProgram.getGiftAmountExpiryDateValue().intValue())));
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
				
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(),considerBonus );
				
				if(expiryValueArr != null  && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					
					if(expGift > 0){
						// update contactloyalty gift balances
						//check if this memebership is closed and transfered to another
					//	ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
						
						//contactLoyalty.setDestMembership(destLoyalty);
						
						expiredValuesArr[0] = expGift;
						updateMembershipBalances(contactLoyalty, 0, 0, expGift);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, 0, expGift, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, considerBonus);
					}
				}
			}
		}
		else if( OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactLoyalty.getRewardFlag())){
			//check if the card is transfered
			/*ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			contactLoyalty.setDestMembership(destLoyalty);
*/
			//logger.info("membership type = "+contactLoyalty.getRewardFlag());
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
			
			if(considerTierID != null) {
				
				tier = tierMap.get(considerTierID);
				
				/*if(contactLoyalty.getProgramTierId()== null) continue;
				tier = tierMap.get(contactLoyalty.getProgramTierId());
				 */
				if(tier != null && 'Y' == loyaltyProgram.getRewardExpiryFlag() && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(tier.getRewardExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
//						cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(tier.getRewardExpiryDateValue().intValue())));
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
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, contactLoyalty.getRewardFlag(), considerBonus);
					
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
							updateMembershipBalances(contactLoyalty, expPoints, expAmt, 0);
							
							// create a child transaction
							//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
							
							// reset transaction values to zero.
							resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, considerBonus);
						
						/*if(expPoints > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
						}
						
						if(expAmt > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
						}*/
					}
				}
				
			}
			
		}
		else if( OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactLoyalty.getRewardFlag())){
			//ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactLoyalty);
			
			//contactLoyalty.setDestMembership(destLoyalty);
			
			//logger.info("membership type : "+contactLoyalty.getRewardFlag());
			if('Y' == loyaltyProgram.getGiftAmountExpiryFlag() && loyaltyProgram.getGiftAmountExpiryDateType() != null 
					&& loyaltyProgram.getGiftAmountExpiryDateValue() != null){
				Calendar cal = Calendar.getInstance();
				
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(loyaltyProgram.getGiftAmountExpiryDateValue().intValue()));
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgram.getGiftAmountExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
					cal.add(Calendar.MONTH, -(12*(loyaltyProgram.getGiftAmountExpiryDateValue().intValue())));
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
				logger.info("expDate = "+expDate);
				Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, considerBonus);
				
				if(expiryValueArr != null && expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
					//long expPoints = Long.valueOf(expiryValueArr[1].toString());
					double expGift = Double.valueOf(expiryValueArr[2].toString());
					
					if(expGift > 0){
						// update contactloyalty gift balances
						//check if this memebership is closed and transfered to another
						expiredValuesArr[0] = expGift;
						
						updateMembershipBalances(contactLoyalty, 0, 0, expGift);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, 0, expGift, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, considerBonus);
					}
				}
			}
			
			/*if(contactLoyalty.getProgramTierId() == null) continue;
			tier = tierMap.get(contactLoyalty.getProgramTierId());
			*/
			Long considerTierID = contactLoyalty.getDestMembership() != null ? 
					contactLoyalty.getDestMembership().getProgramTierId() : contactLoyalty.getProgramTierId();
			
			if(considerTierID != null) {
				
				tier = tierMap.get(considerTierID);
				
				
				if(tier != null && 'Y' == loyaltyProgram.getRewardExpiryFlag() && tier.getRewardExpiryDateType() != null 
						&& tier.getRewardExpiryDateValue() != null){
					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(tier.getRewardExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
//					cal.add(Calendar.MONTH, 1);
						cal.add(Calendar.MONTH, -(12*(tier.getRewardExpiryDateValue().intValue())));
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
					logger.info("expDate = "+expDate);
					Object[] expiryValueArr = fetchExpiryValues(contactLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, considerBonus);
					
					if(expiryValueArr != null && ((expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0) ||
							(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0))){
						long expPoints = 0; //Long.valueOf(expiryValueArr[1].toString());
						double expAmt = 0;
						if(expiryValueArr[1] != null && Double.valueOf(expiryValueArr[1].toString()) > 0){
							expPoints = Long.valueOf(expiryValueArr[1].toString());
						}
						if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) > 0){
							expAmt = Double.valueOf(expiryValueArr[2].toString());
						}
						
						//if(expPoints > 0 && expAmt > 0){
						//check if this memebership is closed and transfered to another
						
						//contactLoyalty.setDestMembership(destLoyalty);
						// update contactloyalty gift balances
						expiredValuesArr[1] = expAmt;
						expiredValuesArr[2] = expPoints;
						updateMembershipBalances(contactLoyalty, expPoints, expAmt, 0);
						
						// create a child transaction
						//createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
						
						// reset transaction values to zero.
						resetExpiryTransValues(contactLoyalty, expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, considerBonus);
						//}
						
						/*if(expPoints > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
						}
						
						if(expAmt > 0){
							createChildTransaction(contactLoyalty, expPoints, expAmt, OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
						}*/
					}
				}
			}
		}
		else{
			logger.info("Invalid loyalty membership..... LOOK THIS MEMBERSHIP...loyaltyId = "+contactLoyalty.getLoyaltyId()+" "
					+ "membership number = "+contactLoyalty.getCardNumber());
		}
		
		return expiredValuesArr;
		
	}
	
	private List<ContactsLoyalty> getChildMemberships(ContactsLoyalty destMembership) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		List<ContactsLoyalty> childMembershipList = loyaltyDao.findChildrenByParent(destMembership.getUserId(), destMembership.getLoyaltyId());
		
		return childMembershipList;
	}
	
	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, boolean considerBonus) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, considerBonus);
	}
	
private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, String bonusIDs) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, bonusIDs);
	}
	
	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, Long bonusID) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, bonusID);
	}
	/*private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}*/
	private void updateMembershipBalances(ContactsLoyalty loyalty, long expPts, double expAmt, double expGift)  {
		try {
			logger.info("Entered into update memberships balances...");
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			if(loyalty.getTransferedTo() != null){
				
				if(loyalty.getDestMembership() == null) return;
				else loyalty = loyalty.getDestMembership();
			}
			// subtract balances and add expiry balances
			
			if(expGift > 0 ){
				if( loyalty.getGiftBalance() != null && loyalty.getGiftBalance() < expGift){
					
					expGift = loyalty.getGiftBalance();
				}
				loyalty.setGiftBalance(loyalty.getGiftBalance() != null ? loyalty.getGiftBalance()- expGift : 0);
				loyalty.setGiftBalance(loyalty.getGiftBalance()!=null && loyalty.getGiftBalance() <0 ? 0 : loyalty.getGiftBalance());
				
				loyalty.setExpiredGiftAmount(loyalty.getExpiredGiftAmount() == null ? expGift : loyalty.getExpiredGiftAmount() + expGift);
				logger.info("Gift amount expired value: :: "+expGift);
			}
			
			if(expAmt > 0){
				if( loyalty.getGiftcardBalance() != null && loyalty.getGiftcardBalance() < expAmt){
					
					expAmt = loyalty.getGiftcardBalance();
				}
				if( loyalty.getGiftcardBalance() != null) {
					
					loyalty.setGiftcardBalance(loyalty.getGiftcardBalance() - expAmt);
					loyalty.setGiftcardBalance(loyalty.getGiftcardBalance() <0 ? 0 : loyalty.getGiftcardBalance());
					loyalty.setExpiredRewardAmount(loyalty.getExpiredRewardAmount() == null ? expAmt : loyalty.getExpiredRewardAmount() + expAmt);
					logger.info("Reward amount expired value : "+expAmt);
				}
			}
			
			if(expPts > 0){
				if( loyalty.getLoyaltyBalance() != null &&  loyalty.getLoyaltyBalance() < expPts){
					
					expPts = loyalty.getLoyaltyBalance().longValue();
				}
				if(loyalty.getLoyaltyBalance() != null) {
					
					loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance() - expPts);
					loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance() < 0 ? 0 : loyalty.getLoyaltyBalance());
					loyalty.setExpiredPoints(loyalty.getExpiredPoints() == null ? expPts : loyalty.getExpiredPoints() + expPts);
					logger.info("Loyalty points expired value : "+expPts);
				}
				
			}
			
			contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
			logger.info("completed update memberships balances...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		
	}
	
	private void resetExpiryTransValues(ContactsLoyalty loyalty, String expDate, String rewardFlag, boolean considerBonus) throws Exception {
		logger.info("Entered into resetExpiryTransValues ...");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(rewardFlag)){
			//expiryDao.resetAmountExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag);
			expiryDaoForDML.resetAmountExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag, considerBonus);
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(rewardFlag)){
			//expiryDao.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag);
			expiryDaoForDML.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag, considerBonus);
		}
		else{
			logger.info("INVALID MEMBERSHIP REWARD TYPE..."+loyalty.getCardNumber());
		}
		logger.info("Completed resetExpiryTransValues ...");
	}
	private void resetExpiryTransValues(ContactsLoyalty loyalty, String expDate, String rewardFlag, String bonusIDs) throws Exception {
		logger.info("Entered into resetExpiryTransValues ...");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(rewardFlag)){
			//expiryDao.resetAmountExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag);
			expiryDaoForDML.resetAmountExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag, bonusIDs);
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(rewardFlag)){
			//expiryDao.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag);
			expiryDaoForDML.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag, bonusIDs);
		}
		else{
			logger.info("INVALID MEMBERSHIP REWARD TYPE..."+loyalty.getCardNumber());
		}
		logger.info("Completed resetExpiryTransValues ...");
	}
	private void resetExpiryTransValues(ContactsLoyalty loyalty, String expDate, String rewardFlag, Long bonusID) throws Exception {
		logger.info("Entered into resetExpiryTransValues ...");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		 if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(rewardFlag)){
			//expiryDao.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag);
			expiryDaoForDML.resetAmtAndPtsExpiryTransValues(loyalty.getLoyaltyId(), expDate, rewardFlag, bonusID);
		}
		
		logger.info("Completed resetExpiryTransValues ...");
	}
	private void initiateCreateingChildTransaction(ContactsLoyalty contactLoyalty, Double[] totExpiredValuesArr) throws Exception {
		
		if(totExpiredValuesArr[0] != null && Double.valueOf(totExpiredValuesArr[0].toString()) > 0){
			//gift amt
			logger.debug("totExpiredValuesArr[0].longValue() "+totExpiredValuesArr[0].longValue());
			 createChildTransaction(contactLoyalty, 0, totExpiredValuesArr[0], OCConstants.LOYALTY_EXPIRY_TYPE_GIFT);
		}
		if(	totExpiredValuesArr[1] != null && Double.valueOf(totExpiredValuesArr[1].toString()) > 0){
			//loyalty amt
			logger.debug("totExpiredValuesArr[1].longValue() "+totExpiredValuesArr[1].longValue());
			 createChildTransaction(contactLoyalty, 0, totExpiredValuesArr[1], OCConstants.LOYALTY_EXPIRY_TYPE_AMOUNT);
		}
		if(	totExpiredValuesArr[2] != null && Double.valueOf(totExpiredValuesArr[2].toString()) > 0){
			//loyalty pts
			logger.debug("totExpiredValuesArr[2].longValue() "+totExpiredValuesArr[2].longValue());
			createChildTransaction(contactLoyalty, totExpiredValuesArr[2].longValue(), 0, OCConstants.LOYALTY_EXPIRY_TYPE_POINTS);
		}
		
		
	}
	private void createChildTransaction(ContactsLoyalty loyalty, long expPts, double expAmt, String expType) throws Exception {
		logger.info("Entered into create child transaction...");
		
		if(loyalty.getTransferedTo() != null){
			
			if(loyalty.getDestMembership() == null) return;//it means some error here in this case no child will be created?????
			else loyalty = loyalty.getDestMembership();
		}
		
		//check if the membership is closed and transfered to another
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
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
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY);
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransChildDao.saveOrUpdate(transaction);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("Completed creating child transaction..");
	}
	
	
}
