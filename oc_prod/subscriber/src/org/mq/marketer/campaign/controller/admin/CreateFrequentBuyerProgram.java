package org.mq.marketer.campaign.controller.admin;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


public class CreateFrequentBuyerProgram extends Thread{
private Coupons coupon;
private boolean isNew;
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
public CreateFrequentBuyerProgram(Coupons coupon, boolean isNew) {
	
	this.coupon = coupon;
	this.isNew = isNew;
}
	public Coupons setRequiredRewardSetup() {
		try {
			ValueCodesDao valueCodesDao = (ValueCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
			ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
			if(isNew && coupon.getValueCode() == null){
				
				long exitingcount = valueCodesDao.getCountOfFBP(coupon.getOrgId());
				String currentCode = getCurrentCode(exitingcount);
				
				ValueCodes valueCode = new ValueCodes();
				valueCode.setValuCode(currentCode);
				valueCode.setCreatedBy(coupon.getUserId()+"");
				valueCode.setCreatedDate(Calendar.getInstance());
				valueCode.setOrgId(coupon.getOrgId());
				
				valueCode.setModifiedBy(coupon.getUserId()+"");
				valueCode.setModifiedDate(Calendar.getInstance());
				valueCode.setDescription("A special reward code associated with the Discount:"+coupon.getCouponCode());
				valueCode.setAssociatedWithFBP(true);
				
				valueCodesDaoForDML.saveOrUpdate(valueCode);
				coupon.setValueCode(currentCode);
				
			}
			coupon.setRequiredLoyltyPoits(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ", e);
		}
		
		return coupon;
	}


	@Override
	public void run() {
		
		try {
			CouponsDaoForDML couponsDaoForDML = (CouponsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONS_DAOForDML);
			CouponDiscountGenerateDao couponDiscountGenerateDao = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			List<CouponDiscountGeneration> retList = couponDiscountGenerateDao.findByCoupon(coupon);
			if(retList == null || retList.isEmpty()){
				logger.debug("nothing to do");
				return;
			}
			/*if(isNew && coupon.getValueCode() == null){
				
				long exitingcount = valueCodesDao.getCountOfFBP(coupon.getOrgId());
				String currentCode = getCurrentCode(exitingcount);
				
				ValueCodes valueCode = new ValueCodes();
				valueCode.setValuCode(currentCode);
				valueCode.setCreatedBy(coupon.getUserId()+"");
				valueCode.setCreatedDate(Calendar.getInstance());
				valueCode.setOrgId(coupon.getOrgId());
				
				valueCode.setModifiedBy(coupon.getUserId()+"");
				valueCode.setModifiedDate(Calendar.getInstance());
				valueCode.setDescription("A special reward code associated with the Discount:"+coupon.getCouponCode());
				valueCode.setAssociatedWithFBP(true);
				
				valueCodesDaoForDML.saveOrUpdate(valueCode);
				coupon.setValueCode(currentCode);
				
			}
			coupon.setRequiredLoyltyPoits(1);*/
			
			SpecialReward specialReward = null;
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			SpecialRewardsDaoForDML specialRewardsDaoForDML = (SpecialRewardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
			List<CustomTemplates> retEmailList = customTemplatesDao.findByTemplateName(coupon.getUserId(), "Discount Against Purchases");
			String rewardTempId = null;
			String rewardSMSTempID = null;
			if(retEmailList != null && !retEmailList.isEmpty() ) {
				rewardTempId = retEmailList.get(0).getTemplateId()+"";
				
			}
			
			List<AutoSMS> retSMSList = autoSmsDao.findByTemplateName(coupon.getUserId(), "Discount Against Purchases");
			if(retSMSList != null && !retSMSList.isEmpty() ) {
				rewardSMSTempID = retSMSList.get(0).getAutoSmsId()+"";
				
			}
			SpecialRewardsDao specialRewardsDao  = (SpecialRewardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
			if(coupon.getSpecialRewadId() != null ){
				
				List<SpecialReward> spretList = specialRewardsDao.findBy(coupon.getSpecialRewadId()+"");//RewarRuleByName(rewardName, user.getUserId());
				
				if(spretList != null && !spretList.isEmpty()) {
					
					specialReward = spretList.get(0);
				}
				
			}
				
			if(specialReward == null){
				
				LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO); 
				
				List<LoyaltyProgram> program = programDao.getProgListByUserId(coupon.getUserId());
				
				if(program == null || program.isEmpty()){
					
					logger.debug("No LoyaltyProgram Found.");
					return;
				}
				Set<LoyaltyProgram> programSet = new HashSet<LoyaltyProgram>();
				programSet.addAll(program);
				
				specialReward = new SpecialReward(coupon.getCouponName(), null, 
						(coupon.getCouponDescription()), "F", 
						coupon.getValueCode(), 1+"", coupon.getRewardExpiryType(), coupon.getRewardExpiryValue(), 
						null, null, programSet, coupon.getOrgId()+"", coupon.getUserId()+"", 
						Calendar.getInstance(), "Active", coupon.isDeductItemPrice(),  true);
			}
			specialReward.setRewardExpiryType(coupon.getRewardExpiryType());
			specialReward.setDescription(coupon.getCouponDescription());
			specialReward.setRewardExpiryValue(coupon.getRewardExpiryValue());
			specialReward.setDeductItemPrice(coupon.isDeductItemPrice());
			specialReward.setAssociatedWithFBP(true);
			specialReward.setPromoCode(coupon.getCouponCode());
			specialReward.setPromoCodeName(coupon.getCouponName());
			specialReward.setExcludeQty(1.0);
			specialReward.setAutoCommEmail(rewardTempId);
			specialReward.setAutoCommSMS(rewardSMSTempID);
			String rewardRule = "[#ItemFactor#];=;<json>:<jsonAttribute>;=;<programId>;=;"+(coupon.isExcludeDiscountedItems()?"E":"I")+";=;"+coupon.getPurchaseQty()+";=;"+"M"+"<OR>||";
			for (CouponDiscountGeneration discRule : retList) {
				if(discRule.getSkuAttribute().equalsIgnoreCase("SKU")){
					rewardRule += "[#PurchasedItem#];=;items:skuNumber;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Description")){
					rewardRule += "[#PurchasedItem#];=;items:description;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Item Category")){
					rewardRule += "[#PurchasedItem#];=;items:itemCategory;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("DCS")){
					rewardRule += "[#PurchasedItem#];=;items:DCS;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Vendor")){
					rewardRule += "[#PurchasedItem#];=;items:vendorCode;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Department")){
					rewardRule += "[#PurchasedItem#];=;items:departmentCode;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Class")){
					rewardRule += "[#PurchasedItem#];=;items:itemClass;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Subclass")){
					rewardRule += "[#PurchasedItem#];=;items:itemSubClass;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().equalsIgnoreCase("Subsidiary Number")){
					rewardRule += "[#PurchasedItem#];=;items:subsidiaryNumber;=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}else if(discRule.getSkuAttribute().toLowerCase().startsWith("udf")){
					rewardRule += "[#PurchasedItem#];=;items:"+discRule.getSkuAttribute().toLowerCase()+";=;<programId>;=;"+discRule.getSkuValue()+";=;<val2>;=;<val3><OR>||";
				}
				
				
			}
			specialReward.setRewardRule(rewardRule);
			
			specialRewardsDaoForDML.saveOrUpdate(specialReward);
			if(coupon.getSpecialRewadId() == null){
				coupon.setSpecialRewadId(specialReward.getRewardId());
			}
			couponsDaoForDML.saveOrUpdate(coupon);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		
	}
	
	private String getCurrentCode(long exitingcount){
		String currentCode = "";
		if(exitingcount < 9999){
			currentCode = "C"+String.format("%04d", exitingcount+1);
			
		}else{
			currentCode = "C"+(exitingcount+1);
		}
		
		return currentCode;
	}
}

