package com.optculture.launchpad.services;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.configs.OCConstants;
import com.optculture.launchpad.dto.CustomExpiryResult;
import com.optculture.launchpad.repositories.LoyaltyProgramRepository;
import com.optculture.launchpad.repositories.LoyaltyProgramTierRepository;
import com.optculture.launchpad.repositories.LoyaltySettingRepository;
import com.optculture.launchpad.repositories.LoyaltyTransactionChildRepository;
import com.optculture.launchpad.repositories.LoyaltyTransactionExpiryRepository;
import com.optculture.launchpad.repositories.UserRepository;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyProgram;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;
import com.optculture.shared.entities.loyalty.LoyaltySetting;
import com.optculture.shared.entities.loyalty.LoyaltyThresholdBonus;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionExpiry;
import com.optculture.shared.entities.org.User;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class LoyaltyProgramService {

	Logger logger = LoggerFactory.getLogger(LoyaltyProgramService.class);

	
	LoyaltyTransactionChildRepository transactionChildRepo;
	
	@Autowired
	public void setLoyaltyTransactionChildRepository(LoyaltyTransactionChildRepository transactionChildRepo) {
		this.transactionChildRepo = transactionChildRepo;
	}

	@Autowired
	UserRepository userRepo;

	@Autowired
	LoyaltySettingRepository loyaltySettingRepo;
	
	@Autowired
	LoyaltyProgramRepository loyaltyProgramRepo;

	public void setLoyaltyProgramRepository(LoyaltyProgramRepository loyaltyProgramRepo){
		this.loyaltyProgramRepo = loyaltyProgramRepo;
	}

	@Autowired
	LoyaltyProgramTierRepository loyaltyProgramTierRepo;

	@Autowired
	LoyaltyTransactionExpiryRepository transactionExpiryRepo;

	/*
	 * Purpose : replace ${loyalty.redeemedCurrecyValue ! "Not Available"} 
	 * params : contactLoyalty , LoyaltyProgramTier
	 * Returns : value of the redeemedCurrencyValue.
	 */
	public double getRedeemedCurrencyValue(ContactLoyalty loyalty, LoyaltyProgramTier tier) {
		double result = 0.0;
		double pointsAmount = 0.0;
		try {
			double loyaltyAmount = (loyalty.getLoyaltyCurrencyBalance());
			double giftAmount = (loyalty.getGiftBalance()) ;

			if (( tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0)
					&& (loyalty.getLoyaltyPointBalance() != null && loyalty.getLoyaltyPointBalance() > 0)) {

				double factor = loyalty.getLoyaltyPointBalance() / tier.getConvertFromPoints();
				int intFactor = (int) factor;
				pointsAmount = tier.getConvertToAmount() * intFactor;

			}
			result = loyaltyAmount + pointsAmount + giftAmount;

		} catch (Exception e) {
			logger.error("Exception while calculating redeemed currency ",e);
		}
		return result;
	}
	
	/*
	 * Purpose : replace ${loyalty.holdBalance !"Not Available"}
	 * Params : contactLoyalty
	 * Returns : string to replace the hold Balance 
	 */
	public String getHoldBalanceValue(ContactLoyalty loyalty) {
		String result = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		if(loyalty.getHoldamountBalance() != null  && loyalty.getHoldpointsBalance() !=null ){
			result = decimalFormat.format(loyalty.getHoldamountBalance()) +" & "+loyalty.getHoldpointsBalance().intValue()+ " Points";
		}
		else if((loyalty.getHoldamountBalance() != null &&loyalty.getHoldamountBalance() > 0)   && (loyalty.getHoldpointsBalance()!=null && loyalty.getHoldpointsBalance() == 0.0)){
			result = decimalFormat.format(loyalty.getHoldamountBalance());
		}
		else if((loyalty.getHoldamountBalance() !=null && loyalty.getHoldamountBalance() == 0.0)  && (loyalty.getHoldpointsBalance() !=null && loyalty.getHoldpointsBalance() > 0)){
			result = loyalty.getHoldpointsBalance().intValue() + " Points";
		}

		return result;
	}
	
	/*
	 * Purpose : to replace ${loyalty.lastEarnedValue ! "Not Available"}
	 * Params : User Id, transaction Id and Transaction Type : Issuance
	 * Returns : String replaced with last earned value. 
	 */

	public String getLoyaltyLastEarnedValue(Long userId, Long loyaltyId, String transactionType) {
		String result = null;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");

		List<LoyaltyTransactionChild> childList = transactionChildRepo.findByTransactionType(userId, loyaltyId,
				transactionType);
		LoyaltyTransactionChild child = childList != null ? childList.get(0) : null;
		
		if (child != null) {
			if ( child.getEarnedAmount() != null && child.getEarnedAmount() > 0) {
				result = decimalFormat.format(child.getEarnedAmount());
			}
			if (child.getEarnedPoints() != null && child.getEarnedPoints() > 0) {
				if(result !=null && !result.isEmpty())
					result +="&";
				
				result = child.getEarnedPoints().intValue() + "";
			} 
		}

		return result;
	}
	
	/* 
	 * Purpose : to replace the ${loyalty.lastRedeemedValue ! "Not Available"}
	 * Params : User Id, Transaction Id and TransactionType. 
	 * returns : String of lastredeemed value.
	 */
	public String getLoyaltyLastRedeemedValue(Long userId, Long loyaltyId, String transactionType) {
		String result = null;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");

		List<LoyaltyTransactionChild> childList = transactionChildRepo.findByTransactionType(userId, loyaltyId,
				transactionType);
		LoyaltyTransactionChild child = childList != null ? childList.get(0) : null;

		if (child != null) {
			if (child.getEnteredAmount() != null && child.getEnteredAmount() > 0  && child.getEnteredAmountType().equalsIgnoreCase("PointsRedeem")) {
				result = child.getEnteredAmount().intValue() + " Points";
			} else if (child.getEnteredAmount()!= null && child.getEnteredAmount() > 0
					&& child.getEnteredAmountType().equalsIgnoreCase("AmountRedeem")) {
				result = decimalFormat.format(child.getEnteredAmount());
			} else {
				result = null;
			}
		}

		return result;
	}
	
	/*
	 * Purpose : replaces tag ${loyalty.membershipPassword ! "Not Available"}
	 * Params : Contactloyalty and flag to differemtiate Email/SMS
	 * Returns : String of membership Password. 
	 */

	public String getMembershipPasswordValue(ContactLoyalty contactLoyalty, boolean isSms) {
		String result = "";
		User user = userRepo.findByuserId(contactLoyalty.getUserId());
		LoyaltySetting loyaltySettings = loyaltySettingRepo.findByuserOrgId(user.getUserOrganization().getUserOrgId());
		if (loyaltySettings != null) {
			result = loyaltySettings.getUrlStr() + "/forgotPassword";
			if (!isSms) {
				result = "<a href=" + result + ">Reset your Password</a>";
			}
		}

		return result;
	}
/*
 * Purpose : Replace ${loyalty.tier.rewardExpirationPeriod ! "Not Available"}
 * Params : ContactLoyalty Object
 * Returns : String after replacing.
 */
	public String getRewardExpirationPeriodValue(ContactLoyalty contactsLoyalty) {
		String rewardExpirationPeriod = null;
	
		Long tierId = contactsLoyalty.getProgramTierId();

		if (tierId != null) {

			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram = null;

			if (contactsLoyalty.getProgramId() != null && !contactsLoyalty.getRewardFlag().isEmpty()) {

				loyaltyProgram = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());
				loyaltyProgramTier = loyaltyProgramTierRepo.findBytierId(contactsLoyalty.getProgramTierId());

				if (loyaltyProgram != null && loyaltyProgramTier != null
						&& loyaltyProgram.getRewardExpiryFlag() == 'Y') {

					if ((OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag())
							|| OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag()))
				&&	(loyaltyProgramTier.getRewardExpiryDateValue() != null && loyaltyProgramTier.getRewardExpiryDateValue() > 0
								&& loyaltyProgramTier.getRewardExpiryDateType() !=null && !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())) {
							rewardExpirationPeriod = loyaltyProgramTier.getRewardExpiryDateValue() + " "
									+ loyaltyProgramTier.getRewardExpiryDateType() + "(s)";
						} // if

				} // if lty !=null
			} // if cont
		} // tier id
		return rewardExpirationPeriod;
	}

	/*
	 * Purpose : Replaces tag ${loyalty.giftAmountExpirationPeriod ! "Not Available"}
	 * Params : ContactLoyalty Object
	 * Returns : String of the replaced value. 
	 */
	public String getGiftAmountExpirationPeriod(ContactLoyalty contactsLoyalty) {
		String giftAmountExpirationPeriod = null;

		LoyaltyProgram loyaltyProgram = null;

		if (contactsLoyalty.getProgramId() != null  && 
				!contactsLoyalty.getRewardFlag().isEmpty()) {
			loyaltyProgram = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());

			if (loyaltyProgram != null && (loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y')) {

				if (OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())
						|| OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())
					&& ((loyaltyProgram.getGiftAmountExpiryDateValue()!= null && loyaltyProgram.getGiftAmountExpiryDateValue() > 0
							&&loyaltyProgram.getGiftAmountExpiryDateType()!=null && !loyaltyProgram.getGiftAmountExpiryDateType().isEmpty()))) {
						giftAmountExpirationPeriod = loyaltyProgram.getGiftAmountExpiryDateValue() + " "
								+ loyaltyProgram.getGiftAmountExpiryDateType() + "(s)";
					} // if

			} // if lty !=null
		} // if cont
		return giftAmountExpirationPeriod;
	}
/*
 * Purpose : Replaces tag ${loyalty.membershipExpirationDate ! "Not Available} 
 * Params : ContactLoyalty Objects
 * Returns : String replaced with the membership expiredDate.
 */
	public LocalDateTime getLoyaltyMembershipExpirationDate(ContactLoyalty contactsLoyalty) {
		LocalDateTime expiryDate = null;
		LoyaltyProgramTier loyaltyProgramTier = null;
		LoyaltyProgram loyaltyProgram = null;

		if (contactsLoyalty.getProgramId() !=null && contactsLoyalty.getProgramTierId() != null
				&& contactsLoyalty.getRewardFlag() !=null && !contactsLoyalty.getRewardFlag().isEmpty()) {
			loyaltyProgram = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());
			loyaltyProgramTier = loyaltyProgramTierRepo.findBytierId(contactsLoyalty.getProgramTierId());

			if (loyaltyProgram != null && loyaltyProgramTier != null) {

				if (OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag())
						|| OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL
								.equalsIgnoreCase(contactsLoyalty.getRewardFlag())) {
					//// if flag L or GL
					if (loyaltyProgram.getMembershipExpiryFlag() == 'Y'
							&& (loyaltyProgramTier.getMembershipExpiryDateType() !=null && !loyaltyProgramTier.getMembershipExpiryDateType().isEmpty()) 
				  			&& (loyaltyProgramTier.getMembershipExpiryDateValue()!=null && loyaltyProgramTier.getMembershipExpiryDateValue() > 0)) {

						boolean upgdReset = (loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y');

								
					expiryDate = getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(),
							contactsLoyalty.getTierUpgradedDate(), upgdReset,
								loyaltyProgramTier.getMembershipExpiryDateType(),
								loyaltyProgramTier.getMembershipExpiryDateValue());
					}
				} // if

			} // loyaltyProgram && loyaltyProgramTier
		}
		return expiryDate;

	}
/*
 * Purpose : process expiry date based on created date on basis of mentioned Year/Month
 * Params : createdDate, TierUpgradedDate, Flag for the membership upgrading, Data Type(Month/Year) and value of the type.
 * Returns : String of the expired day formed after calculation.
 */
	private LocalDateTime getMbrshipExpiryDate(LocalDateTime createdDate, LocalDateTime upgradedDate, boolean upgdResetFlag,
			String dateType, Long dateValue) {

		LocalDateTime expiryCal = null;

		if (upgdResetFlag) {

			if (upgradedDate != null) {
				expiryCal =  upgradedDate;
			} else {
				expiryCal =  createdDate;
			}

		} else {
			expiryCal =  createdDate;
		}

		if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(dateType)) {
			expiryCal = expiryCal.plusMonths(dateValue.intValue());
		} else if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(dateType)) {
			expiryCal = expiryCal.plusYears( dateValue.intValue());
		}

		
		return expiryCal;

	}
	
	/*
	 * Purpose : Replace tags ${loyalty.giftCardExpriationDate !"Not Available"}
	 * Params : ContactLoyalty
	 * Returns : replaces the giftcard expiration Date.
	 */

	public LocalDateTime getGiftCardExpirationDate(ContactLoyalty contactsLoyalty) {
		LocalDateTime giftCardExpriationDate = null;

		LoyaltyProgram loyaltyProgram = null;

		if (contactsLoyalty.getProgramId() != null) {
			loyaltyProgram = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());
			if (loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null
					&& OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())) {
				if (loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y'
						&& (loyaltyProgram.getGiftMembrshpExpiryDateType() !=null && !loyaltyProgram.getGiftMembrshpExpiryDateType().isEmpty())
						&& (loyaltyProgram.getGiftMembrshpExpiryDateValue() !=null && loyaltyProgram.getGiftMembrshpExpiryDateValue() > 0)) {

					giftCardExpriationDate = getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(),
							loyaltyProgram.getGiftMembrshpExpiryDateType(),
							loyaltyProgram.getGiftMembrshpExpiryDateValue());
				} // if
			}
		}
		return giftCardExpriationDate;
	}// getGiftCardExpirationDate

	/*
	 * Purpose : to Process the Date from the mebership created Date on basis of mentioned Type.
	 * Params : Created Date , Date Type (Year/Month), Value of that date
	 * Returns : String of the date expiry date after calculation. 
	 */
	public LocalDateTime getGiftMbrshipExpiryDate(LocalDateTime createdDate, String dateType, Long dateValue) {

		LocalDateTime expiryCal = createdDate;

		if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(dateType)) {
			expiryCal =	expiryCal.plusMonths( dateValue.intValue());
		} else if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(dateType)) {
			expiryCal = expiryCal.plusYears( dateValue.intValue());
		}

		return expiryCal;

	}
	/*
	 * Purpose : Replaces tag ${loyalty.lastBonusValue ! "Not Available"}
	 * Params : UserId , loyaltyId and transaction type 
	 * Returns : String with last bonus value.
	 */
	public String getLastBonusValue(Long userId, Long loyaltyId, String transactionType) {
		String result = null;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		List<LoyaltyTransactionChild> loyaltyTransactionChildList =  transactionChildRepo.findByTransactionType(userId, loyaltyId, transactionType);
	
		LoyaltyTransactionChild loyaltyTransactionChild = (loyaltyTransactionChildList != null && !loyaltyTransactionChildList.isEmpty()) ?  loyaltyTransactionChildList.get(0) :null;
		
		if (loyaltyTransactionChild != null) {
			if ( (loyaltyTransactionChild.getEarnedAmount() !=null && loyaltyTransactionChild.getEarnedAmount() >0)
					&& (loyaltyTransactionChild.getEarnedPoints() !=null && loyaltyTransactionChild.getEarnedPoints() > 0 )){
				result = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount()) + " & "
						+ loyaltyTransactionChild.getEarnedPoints().intValue() + " Points";
			} else if ((loyaltyTransactionChild.getEarnedAmount() != null  && loyaltyTransactionChild.getEarnedAmount() > 0)
					&& ( loyaltyTransactionChild.getEarnedPoints() != null && loyaltyTransactionChild.getEarnedPoints() == 0)) {
				result = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
			} else if ((loyaltyTransactionChild.getEarnedAmount() !=null) && loyaltyTransactionChild.getEarnedAmount() == 0
					&&(loyaltyTransactionChild.getEarnedPoints() !=null && loyaltyTransactionChild.getEarnedPoints() > 0)) {
				result = loyaltyTransactionChild.getEarnedPoints().intValue() + " Points";
			}
		}
		return result;
	}

	//TODO this is used in auto communicaiton while sending threshold bonus
	// communication
	private String getLastThresholdLevelValue(LoyaltyThresholdBonus threshold, LoyaltyTransactionChild transaction) {
		String result = "Not Available";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String value = "";

		if ( (threshold.getEarnedLevelType() !=null && threshold.getEarnedLevelType().equals("Amount")) || (transaction.getEarnType() !=null && transaction.getEarnType().equals("LPV")))
		{
			value = (threshold.getEarnedLevelValue() !=null) ? decimalFormat.format(threshold.getEarnedLevelValue())
					: result;
		}

		else
			value = threshold.getEarnedLevelValue() != null ? threshold.getEarnedLevelValue().longValue() + " Points"
					: result;

		if (value != null && !value.trim().isEmpty()) {
			value = (value.equals("--") && result != null) ? result : value;
		} else {
			value = result;
		}

		return result;
	}
/*
 * Purpose : Replaces tag ${loyalty.giftAmountExpiringValue ! "Not Available"}
 * Params : ContactLoyalty
 * Returns : String of the expiring Value
 */
	public String getGiftAmountExpiringValue(ContactLoyalty contactsLoyalty) {
		logger.info("--Start of getGiftAmountExpiringValue--");
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String giftExpValue = "Not Available";
		double value = 0.0;
		try {
			if (contactsLoyalty.getProgramId() == 0)
				return giftExpValue;
			LoyaltyProgram program = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());

			if (OCConstants.FLAG_YES == program.getGiftAmountExpiryFlag()
					&&(program.getGiftAmountExpiryDateType() != null && program.getGiftAmountExpiryDateType().isEmpty())
					&& ( program.getGiftAmountExpiryDateValue() != null && program.getGiftAmountExpiryDateValue() > 0)) {

			//	Calendar cal = Calendar.getInstance();
				LocalDateTime local = LocalDateTime.now();
				if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH
						.equals(program.getGiftAmountExpiryDateType())) {
					local = local.minusMonths(program.getGiftAmountExpiryDateValue().intValue());
				} else if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR
						.equals(program.getGiftAmountExpiryDateType())) {
					local =local.minusYears(program.getGiftAmountExpiryDateValue().intValue());
				}
				LocalDateTime expDate ;
				
					expDate = local.plusMonths(1);
				
				logger.info("expDate = {}", expDate);

				List<Object[]> customExpiryResultList = transactionExpiryRepo.findByTransOnCondition(
						contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);

				Object[] expiry = customExpiryResultList == null ? null : customExpiryResultList.get(0);
				
				if (expiry != null) {
					
				//	String memberShip = expiry[]
				//	value = expiry;
				/*
				 * if (value > 0) { giftExpValue = decimalFormat.format(value); }
				 */
				}

				/*
				 * if(expiryValueArr != null && expiryValueArr[2] != null){ DecimalFormat
				 * decimalFormat = new DecimalFormat("#0.00"); double expGift =
				 * Double.valueOf(expiryValueArr[2].toString()); if(expGift > 0){ giftExpValue
				 * =decimalFormat.format(expGift); } }
				 */

			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return giftExpValue;
	}
/*
 * Purpose : Replaces tag ${loyalty.tier.rewardExpiringValue ! "Not Available"}
 * Params : ContactLoyalty
 * Returns : String of reward expiring value.
 */
	public String getRewardExpiringValue(ContactLoyalty contactsLoyalty) {
		logger.info("--Start of getRewardExpiringValue--");
		String rewardExpVal = null;
		try {

			if (contactsLoyalty.getProgramTierId() != null )
				return rewardExpVal;
			
			LoyaltyProgram program = loyaltyProgramRepo.findByProgramId(contactsLoyalty.getProgramId());

			LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierRepo
					.findBytierId(contactsLoyalty.getProgramTierId());

			if (OCConstants.FLAG_YES == program.getRewardExpiryFlag()
					&& ( loyaltyProgramTier.getRewardExpiryDateType() !=null && !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())
					&& ( loyaltyProgramTier.getRewardExpiryDateValue() !=null && loyaltyProgramTier.getRewardExpiryDateValue() > 0)) {

				LocalDateTime local = LocalDateTime.now();
				if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH
						.equals(loyaltyProgramTier.getRewardExpiryDateType())) {
				local =	local.minusMonths(loyaltyProgramTier.getRewardExpiryDateValue().intValue());
				} else if (OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR
						.equals(loyaltyProgramTier.getRewardExpiryDateType())) {
				local =	local.minusYears(loyaltyProgramTier.getRewardExpiryDateValue().intValue());
				}

				LocalDateTime expDate;
				
					expDate = local.plusMonths(1);
				//	expDate = local.getYear() + "-" + local.getMonthValue();
	
				logger.info("expDate = {}" , expDate);
				
			  rewardExpVal = getCustomRewardExpiryValue(contactsLoyalty.getLoyaltyId(),expDate,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
				
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return rewardExpVal;
	}
	//TODO - check the query for this tag
	private String getCustomRewardExpiryValue(Long loyaltyId, LocalDateTime expDate, String loyaltyMembershipRewardFlagL) {
		
		String rewardExpVal="Not Available";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//TODO : check this method in Repo
		List<Object[]> customExpiryResultList = transactionExpiryRepo.findByTransOnCondition(
				loyaltyId, expDate, loyaltyMembershipRewardFlagL);

		if (customExpiryResultList != null && !customExpiryResultList.isEmpty()) {
			
			Object[] expiry = customExpiryResultList.get(0);
			
			// 0. membership
			// 1.aggExpPoints
			// 2.aggExpAmount
			
			Long expPoints  = (Long) expiry[1];
			Double expAmount  = (Double) expiry[2];
		//	String memberShip = (String) expiry[0];
			
			if (expPoints > 0 && expAmount> 0 && expAmount > 0.0) {
				rewardExpVal = expPoints+ " Points" + " & "
						+ decimalFormat.format(expAmount);
			} else if (expPoints > 0 && expAmount == 0.0) {
				rewardExpVal = expPoints + " Points";
			} else if (expAmount > 0.0 && expPoints == 0) {
				rewardExpVal = decimalFormat.format(expAmount);
			}
		}
		return rewardExpVal;
	}

	public String getEnrollmentSource(String loyaltyType) {
    	String loyaltyPH = null;
    	if(OCConstants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(loyaltyType)) {
    		loyaltyPH = OCConstants.CONTACT_LOYALTY_TYPE_STORE;
    	}
    	else {
    		loyaltyPH = loyaltyType;
    	}
    	return loyaltyPH;
    }//getEnrollmentSource	

	public LoyaltyProgramTier getLoyaltyTierObj(ContactLoyalty contactLoyaltyObj) {
		
		LoyaltyProgramTier loyaltyTier = loyaltyProgramTierRepo.findBytierId(contactLoyaltyObj.getProgramTierId());
		
		return loyaltyTier;
	}
	public Double getLPV(ContactLoyalty contactsLoyalty) throws Exception{
		Double totPurchaseValue = null;
		Double cummulativePurchaseValue = contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
		Double cummulativeReturnValue = contactsLoyalty.getCummulativeReturnValue() == null ? 0.0 : contactsLoyalty.getCummulativeReturnValue();
		totPurchaseValue = cummulativePurchaseValue-cummulativeReturnValue;
		return totPurchaseValue;
	}

}
