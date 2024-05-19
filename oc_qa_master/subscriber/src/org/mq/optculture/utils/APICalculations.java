package org.mq.optculture.utils;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;

public class APICalculations {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public Long[] pointsCal() {
		
		
		
		
		return null;
	}
	
	
	
	
	public double getAutoConvertionReversalVal(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) {
		/* Amith Lulla: e.g. rule 100 points to $1
[12:53:27] Amith Lulla: sorry, you understood na?
[12:53:41] Amith Lulla: anyways...
[12:53:58] Amith Lulla: 550 points earned. converts to $5 and 50 points.
[12:54:11] Amith Lulla: if product worth returned and 350 points return
[12:54:25] proumya Acharya: autoconvertion pura rollback karna hein na?
[12:54:34] proumya Acharya: plz continue
[12:54:35] Amith Lulla: The calculator will convert $5 x 100 points + 50- points = 550 points
[12:54:45] Amith Lulla: -350 points= 250 points
[12:54:59] Amith Lulla: now bal =250 points = $2 and 50p */

		double unitAmtFactor = (double)tier.getConvertFromPoints()/tier.getConvertToAmount();
		//int multiple = (int)unitAmtFactor;
		double multiple = (double)unitAmtFactor;
		double totConvertedPts = contactsLoyalty.getGiftcardBalance() * multiple;

		return totConvertedPts;//changes long conversion was removed APP-1072
		//double subPoints = multiple * tier.getConvertFromPoints();
		
		
	}
	public Double getConversionVal(Double remainingPoints, LoyaltyProgramTier tier){
		try{
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) && 
					tier.getConvertFromPoints() != null && tier.getConvertFromPoints()>0){
				double multipledouble = tier.getConvertToAmount()/tier.getConvertFromPoints();
				double multiple = (double)multipledouble;
				double convertedAmount = Double.parseDouble(Utility.truncateUptoTwoDecimal(remainingPoints * multiple));
				return convertedAmount;
			}else{
				return null;
			}

		}catch(Exception e){
				logger.error("Exception while applying auto conversion rules...", e);
				return null;
		}

	}

	public ContactsLoyalty applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier){
		//This calculation only works if points balance is less  than 0. 
		logger.info("-- Entered applyConversionRules --");
		String[] differenceArr = null;

		try{
			
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
					
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					/*int multiple = (int)multipledouble; App 910*/
					double multiple = multipledouble;
					double convertedAmount = tier.getConvertToAmount() * multiple;
					double subPoints = multiple * tier.getConvertFromPoints();
					
					differenceArr[0] = Constants.STRING_NILL+convertedAmount;
					differenceArr[1] = Constants.STRING_NILL+subPoints;
					differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					/*contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					*/
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					/*List<Object> retList = new ArrayList<Object>();
					retList.set(0, contactsLoyalty);*/
					
					return contactsLoyalty;
					
					//deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		logger.info("-- Exit applyConversionRules --");
		//return null;
		return contactsLoyalty;
	}
	
}
