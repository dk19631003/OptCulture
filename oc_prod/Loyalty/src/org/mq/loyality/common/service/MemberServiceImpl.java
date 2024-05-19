package org.mq.loyality.common.service;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.ContactsDao;
import org.mq.loyality.common.dao.ContactsLoyaltyDao;
import org.mq.loyality.common.dao.LoyaltyProgramDao;
import org.mq.loyality.common.dao.LoyaltyProgramTierDao;
import org.mq.loyality.common.dao.LoyaltyTransactionExpiryDao;
import org.mq.loyality.common.dao.RetailProSalesDao;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoyaltyProgram;
import org.mq.loyality.common.hbmbean.LoyaltyProgramTier;
import org.mq.loyality.exception.LoyaltyProgramException;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.MyCalendar;
import org.mq.loyality.utils.OCConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MembershipService {
	@Autowired
	private LoyaltyProgramDao loyalityDao;
	@Autowired
	private LoyaltyProgramTierDao loyaityTierDao;
	@Autowired
	private ContactsLoyaltyDao contactsLoyalityDao;
	@Autowired
	private LoyaltyTransactionExpiryDao expiryDao;
	@Autowired
	private ContactsDao contactDao;
	@Autowired
	private RetailProSalesDao retailProSalesDao;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	@Override
	@Transactional
	public LoyaltyProgram findById(Long prgmId) {
		// TODO Auto-generated method stub
		return loyalityDao.findById(prgmId);
	}

	@Override
	@Transactional
	public LoyaltyProgramTier getTierById(Long tierId) {
		// TODO Auto-generated method stub
		return loyaityTierDao.getTierById(tierId);
	}

	@Override
	@Transactional
	public ContactsLoyalty getContactsLoyaltyByCardId(Long cardId) {
		// TODO Auto-generated method stub
		return contactsLoyalityDao.getContactsLoyaltyByCardId(cardId);
	}
	
	@Override
	@Transactional
	public Object[] fetchExpiryValues(Long loyaltyId,
			String expDate, String rewardFlag) {
		// TODO Auto-generated method stub
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag);
	}

	@Override
	@Transactional
	public ContactsLoyalty getLoyaltyByPrgmAndPhone(Long programId, String phone, String countryCarrier) {
		// TODO Auto-generated method stub
		return loyalityDao.getLoyaltyByPrgmAndPhone(programId, phone, countryCarrier);
	}

	@Override
	@Transactional
	public ContactsLoyalty getLoyaltyByPrgmAndMembrshp(Long programId,
			String newPhone) {
		// TODO Auto-generated method stub
		return loyalityDao.getLoyaltyByPrgmAndMembrshp( programId, newPhone);
	}

	@Override
	public Double findPointsToUpgrade(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier loyaltyProgramTier) throws LoyaltyProgramException {

		Contacts contact = contactsLoyalty.getContact();

		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(loyaltyProgramTier.getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);

			Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
			logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);


			return totLoyaltyPointsValue;

		}else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(loyaltyProgramTier.getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);


			Double totPurchaseValue = null;
			Double purchaseValue = contactDao.findContactPurchaseDetails(contact.getUser().getUserId(), contact.getContactId());
				
						totPurchaseValue = Double.valueOf(purchaseValue != null ? purchaseValue.toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
				
			

			return totPurchaseValue;
		}else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(loyaltyProgramTier.getTierUpgdConstraint())){
			try{
				/*Double cumulativeAmount = 0.0;
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -loyaltyProgramTier.getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contact.getContactId()+" startDate = "+startDate+" endDate = "+endDate);


				Object cumulativeAmountArr = retailProSalesDao.getCumulativePurchase(contact.getUser().getUserId(), contact.getContactId(), startDate, endDate);

				if(cumulativeAmountArr!=null)cumulativeAmount = Double.valueOf(cumulativeAmountArr.toString());

				return cumulativeAmount;*/
				
				return loyaltyProgramTier.getTierUpgdConstraintValue() != null ? loyaltyProgramTier.getTierUpgdConstraintValue().doubleValue():0.00;

			}catch(Exception e){
				logger.error("Excepion in cpv thread ", e);
				return 0.00;
			}
		}
		else{
			return null;
		}
	}
}
