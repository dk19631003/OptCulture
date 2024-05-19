package org.mq.marketer.campaign.general;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.optculture.model.couponcodes.CouponDiscountInfo;
import org.mq.optculture.model.couponcodes.DiscountInfo;
import org.mq.optculture.model.couponcodes.ItemAttribute;
import org.mq.optculture.model.couponcodes.ItemCodeInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class CouponDescriptionAlgorithm {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String userCurrencySymbol = "$ ";

	
	//****algorithmic desc ****
	
	public CouponDiscountInfo preparecouponDisc(CouponDiscountInfo couponDiscountInfo, Users users, boolean onItems){
		
		String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		if (currSymbol != null && !currSymbol.isEmpty())
			userCurrencySymbol = currSymbol;
		
		StringBuilder description = new StringBuilder();
		List<DiscountInfo> discounts = couponDiscountInfo.getDISCOUNTINFO();
		String eachDiscDesc = "";
		int rulescnt = 0;
		if(couponDiscountInfo.getAPPLYATTRIBUTES().equals("Combination")) {
			
			
			StringBuilder attrDesc = new StringBuilder(Constants.STRING_NILL);
			int attrCount =0;
			for (DiscountInfo discountInfo : discounts) {
				List<ItemCodeInfo> itemCodes = discountInfo.getITEMCODEINFO();
				for (ItemCodeInfo itemCodeInfo : itemCodes) {
					List<ItemAttribute> attrList = itemCodeInfo.getITEMATTRIBUTE();
					for (ItemAttribute itemAttribute : attrList) {
						
						String skuAttributeValue = itemAttribute.getATTRIBUTECODE();
						if(attrDesc.length() > 0 ) attrDesc.append(Constants.DELIMETER_COMMA+" ");
						attrDesc.append(skuAttributeValue);
						
					}
				
					description .append( attrDesc.toString());
					if(itemCodeInfo.getQUANTITY() != null && !itemCodeInfo.getQUANTITY().isEmpty()) {
						description .append(" up to "+(itemCodeInfo.getQUANTITY())+" item(s)");
					}else{
						description .append(" items");
					}
					
				
				}
				
				description .append(Constants.DELIMETER_COLON+" "+(discountInfo.getVALUECODE().equals("V") ? 
						(userCurrencySymbol +discountInfo.getVALUE()+" "):(discountInfo.getVALUE()+"%")));
				
				
			}
			couponDiscountInfo.setDESCRIPTION(description.toString());
			return couponDiscountInfo;
			
			
			
		}else{

			description = new StringBuilder(Constants.STRING_NILL);
					
				
			String quantity = null; 
			for (DiscountInfo discountInfo : discounts) {
				StringBuilder attrDesc = new StringBuilder(Constants.STRING_NILL);
				List<ItemCodeInfo> itemCodes = discountInfo.getITEMCODEINFO();
				for (ItemCodeInfo itemCodeInfo : itemCodes) {
					List<ItemAttribute> attrList = itemCodeInfo.getITEMATTRIBUTE();
					for (ItemAttribute itemAttribute : attrList) {
						
						String skuAttributeValue = itemAttribute.getATTRIBUTECODE();
						if(attrDesc.length() > 0 ) attrDesc.append(Constants.DELIMETER_COMMA+" ");
						attrDesc.append(skuAttributeValue);
						
					}
				
					description .append( attrDesc.toString());
					if(itemCodeInfo.getQUANTITY() != null && !itemCodeInfo.getQUANTITY().isEmpty()) {
						description .append(" up to "+(itemCodeInfo.getQUANTITY())+" item(s)");
					}else{
						description .append(" items");
					}
					
				
				}
				
				description.append(Constants.DELIMETER_SEMICOLON+" "+
						(discountInfo.getVALUECODE().equals("V") ? 
								(userCurrencySymbol +discountInfo.getVALUE()):
									(discountInfo.getVALUE()+"%")));
				
			}
		
			couponDiscountInfo.setDESCRIPTION(description.toString());
			return couponDiscountInfo;
		}
			
	}
	public CouponDiscountInfo preparecouponDisc(CouponDiscountInfo couponDiscountInfo, Users users){
		
		String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		if (currSymbol != null && !currSymbol.isEmpty())
			userCurrencySymbol = currSymbol;
		
		StringBuilder description = new StringBuilder();
		List<DiscountInfo> discounts = couponDiscountInfo.getDISCOUNTINFO();
		String eachDiscDesc = "";
		int rulescnt = 0;
		
		for (DiscountInfo discountInfo : discounts) {
			
			if(eachDiscDesc.length() > 0) eachDiscDesc += Constants.DELIMETER_SEMICOLON+" ";
			if(discountInfo.getMINPURCHASEVALUE() != null && !discountInfo.getMINPURCHASEVALUE().isEmpty() && 
					Long.parseLong(discountInfo.getMINPURCHASEVALUE()) > 0){
				
				if(rulescnt <= 0)description.append("Discount on receipt, ");
				eachDiscDesc += " minimum amount "+userCurrencySymbol+
						discountInfo.getMINPURCHASEVALUE().toString()+Constants.DELIMETER_COLON+" "+ 
						(discountInfo.getVALUECODE().equals("V") ? (userCurrencySymbol +
								discountInfo.getVALUE()):(discountInfo.getVALUE()+"%")) ;
			}else{
				if(rulescnt <= 0)description.append("Discount on receipt: ");
				eachDiscDesc += (discountInfo.getVALUECODE().equals("V") ? (userCurrencySymbol +
						discountInfo.getVALUE()):(discountInfo.getVALUE()+"%")) ;
			}
			rulescnt += 1;
			
		}//for
		
		description.append(eachDiscDesc);
		description.append(".");
		/*if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
			String ltyStr = (rulescnt > 1 ? (""+". Against ") : " against ")+coupon.getRequiredLoyltyPoits()+
					" "+(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode());
			logger.debug("ltyStr==="+ltyStr);
			description.append(ltyStr );
			
		}*/
		couponDiscountInfo.setDESCRIPTION(description.toString());
		
		return couponDiscountInfo;
	}
	public String preparecouponDisc(Coupons coupon, Users users){
		String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		if (currSymbol != null && !currSymbol.isEmpty())
			userCurrencySymbol = currSymbol;
		
		StringBuilder description = new StringBuilder();
			try {
				ServiceLocator locator = ServiceLocator.getInstance();
				POSMappingDao posMappingDao = (POSMappingDao)locator.getDAOByName(OCConstants.POSMAPPING_DAO);
				CouponDiscountGenerateDao coupDiscGenDao = (CouponDiscountGenerateDao)locator.getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
				List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", users.getUserId());
				Map<String , String> posMappingMap = new HashMap<String , String>();
				if(posMappingsList !=null && !posMappingsList.isEmpty()){
					
					for (POSMapping posMapping : posMappingsList) {
						if(posMapping.getCustomFieldName().toLowerCase().startsWith("udf")){
							posMappingMap.put(posMapping.getCustomFieldName().toLowerCase(), posMapping.getDisplayLabel());

						}else{
							
							posMappingMap.put(posMapping.getCustomFieldName(), posMapping.getDisplayLabel());
						}
					}
				}
				
				
				if(coupon.getDiscountCriteria().equals("Total Purchase Amount")) {
					
					List<CouponDiscountGeneration> coupDisList = coupDiscGenDao.findByCoupon(coupon);
					String eachDiscDesc = "";
					int rulescnt = 0;
					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						if(eachDiscDesc.length() > 0) eachDiscDesc += "; ";
						if(coupDisGenObj.getTotPurchaseAmount() != null && coupDisGenObj.getTotPurchaseAmount()!=0){
							if(rulescnt <= 0)description.append("Discount on receipt, ");
							eachDiscDesc += " minimum amount "+userCurrencySymbol+
									coupDisGenObj.getTotPurchaseAmount().toString()+Constants.DELIMETER_COLON+" "+ 
									(coupon.getDiscountType().equals("Value") ? (userCurrencySymbol +
											getActualNumber(coupDisGenObj.getDiscount())):(getActualNumber(coupDisGenObj.getDiscount())+"%")) ;
						}else{
							if(rulescnt <= 0)description.append("Discount on receipt: ");
							eachDiscDesc += (coupon.getDiscountType().equals("Value") ? (userCurrencySymbol +
									getActualNumber(coupDisGenObj.getDiscount())):(getActualNumber(coupDisGenObj.getDiscount())+"%")) ;
						}
						rulescnt += 1;
						
					}
					description.append(eachDiscDesc);
					if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
						String ltyStr = (rulescnt > 1 ? (""+" against ") : " against ")+coupon.getRequiredLoyltyPoits()+
								" Reward type: "+(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode()+".");
						logger.debug("ltyStr==="+ltyStr);
						description.append(ltyStr );
						
					}else if(coupon.getLoyaltyPoints() != null && coupon.getMultiplierValue() != null){
						
						String ltyStr = (" against "+coupon.getMultiplierValue()+"x points of the discount value. Reward type: "+
								(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode())+".");
						logger.debug("ltyStr==="+ltyStr);
						description.append(ltyStr );
					}else{
						description.append(".");
					}
				}
				else if(coupon.getDiscountCriteria().equals("SKU") ){
					if(coupon.isCombineItemAttributes()) {
						
						List<Object[]> retList = coupDiscGenDao.findDistinctAttrCombos(coupon.getCouponId());
						if(retList != null && !retList.isEmpty()) {
							String comboQuery = Constants.STRING_NILL;
							Double discount = null;
							Double maxDiscount = null;
							String quantity = null;
							String quantityCriteria = null;
							int attrCount = 0;
							List<ItemAttribute> itemAttrLst = new ArrayList<ItemAttribute>();
							StringBuilder attrDesc = new StringBuilder(Constants.STRING_NILL);
							for (Object[] objects : retList) {
								
								String skuAttribute = (String)objects[0];
								String skuAttributeValue = (String)objects[1];
								
								if(discount == null)discount = objects[2] != null ? (Double)objects[2] : null;
								
								//if(maxDiscount == null)maxDiscount = objects[3] != null ? (Double)objects[3] : null;
								
								if(quantity == null)quantity = (String)objects[4];
								if(quantityCriteria == null) quantityCriteria = (String)objects[5];
								if(attrDesc.length() > 0 ) attrDesc.append(Constants.DELIMETER_COMMA+" ");
								attrDesc.append(skuAttributeValue);
								
								attrCount +=1;
							}
							description .append( attrDesc.toString());
							if(quantity != null && !quantity.isEmpty()) {
								if(quantityCriteria == null || quantityCriteria.isEmpty()){
									
									description .append(" maximum "+(getActualNumber(Double.parseDouble(quantity)))+" item(s)");
									
								}else{
									
									description .append(" quantity "+Utility.limitQuantityMap.get(quantityCriteria).toLowerCase()+" "+(getActualNumber(Double.parseDouble(quantity)))+" item(s)");
								}
							}else{
								description .append(" items");
								/*if(quantityCriteria == null || quantityCriteria.isEmpty()){
									
								}else{
									description .append(" quantity "+Utility.limitQuantityMap.get(quantityCriteria).toLowerCase()+" "+(getActualNumber(Double.parseDouble(quantity)))+" item(s)");
								}*/
							}
							description .append(Constants.DELIMETER_COLON+" "+(coupon.getDiscountType().equals("Value") ? 
									(userCurrencySymbol +getActualNumber(discount)+" "):(getActualNumber(discount)+"%")));
							
							if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
								/*description .append( attrDesc+Constants.DELIMETER_SEMICOLON+" Discount of "+
										(coupon.getDiscountType().equals("Value") ? 
												(Utility.countryCurrencyMap.get(users.getCountryType()) +discount+" "):(discount+"% ")));
								if(quantity != null && !quantity.isEmpty()) {
									description .append("on "+(quantity)+" item(s) ");
								}*/
								String ltyStr = (""+" against "+coupon.getRequiredLoyltyPoits()+" Reward type:"+
										" "+(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode())+".");
								logger.debug("ltyStr==="+ltyStr);
								description.append(ltyStr );
								
							}else if(coupon.getLoyaltyPoints() != null && coupon.getMultiplierValue() != null){
								
								String ltyStr = (" against "+coupon.getMultiplierValue()+"x points of the discount value. Reward type: "+
										(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode())+".");
								logger.debug("ltyStr==="+ltyStr);
								description.append(ltyStr );
							}/*else{
								
								description .append( attrDesc+Constants.DELIMETER_SEMICOLON+" Discount of "+
										(coupon.getDiscountType().equals("Value") ? 
												(Utility.countryCurrencyMap.get(users.getCountryType()) +discount):(discount+"%")));
								if(quantity != null && !quantity.isEmpty()) {
									description .append(" on up to "+(quantity)+" item(s)");
								}
								
							}*/
						
						}
					
					}else{
						
						logger.info("=====In else block - OR==========");
						description = new StringBuilder(Constants.STRING_NILL);
						List<String> retList = coupDiscGenDao.findDistinctAttr(coupon.getCouponId());
						List<Double> retDiscList = coupDiscGenDao.findDistinctCoupDiscount(coupon.getCouponId());
						
						logger.info("=====Got retDiscList - OR==========");
						List<CouponDiscountGeneration> coupDisList = new ArrayList<CouponDiscountGeneration>();
						for (String CDGAttr : retList) {

							List<CouponDiscountGeneration> tempcoupDisList = coupDiscGenDao.findAllBy(coupon.getCouponId(), users.getUserId(), 
									Utility.CDGAttrToSKUMap.containsKey(CDGAttr) ? Utility.CDGAttrToSKUMap.get(CDGAttr) : CDGAttr, CDGAttr);
							
							logger.info("received ==="+retDiscList);
							
							logger.info("tempcoupDisList ==="+tempcoupDisList);

							
							if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

								coupDisList.addAll(tempcoupDisList);
							}
							
							logger.info("===end of for====");

						}//for
						
						logger.info("retDiscList==="+retDiscList);
						
						for (double obj : retDiscList) {
						
							logger.info("===Inside for loop1===");
							//String quantity = null; 
							String attrQty = null;
							String attrQtyCriteria = null;
							String allQtyCriteria = null;
							Map<String, Set<String>> sameAttr = new HashMap<String, Set<String>>();
							Map<String, String> qtyAndQtyCriMap = new HashMap<String, String>();
							for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
								logger.info("===Inside for loop2===");

								
								if((obj) != coupDisGenObj.getDiscount() 	) continue;
								
								if(coupDisGenObj.getQuantity() == null || coupDisGenObj.getQuantity().isEmpty()){
									
									logger.info("===coupDisGenObj.getQuantity() == null===");

									attrQty = "all";
									if(coupDisGenObj.getLimitQuantity() != null) allQtyCriteria = coupDisGenObj.getLimitQuantity();
								}
								else {
									attrQty = coupDisGenObj.getQuantity();
									if(coupDisGenObj.getLimitQuantity() != null) allQtyCriteria = coupDisGenObj.getLimitQuantity();
									
									//qtyAndQtyCriMap.put(attrQty, attrQtyCriteria);
									qtyAndQtyCriMap.put(attrQty, allQtyCriteria);
									
									logger.info("value inserted in map==="+allQtyCriteria);
								}
									if(sameAttr.containsKey(attrQty)) {
										
										Set<String> attrSet = sameAttr.get(attrQty);
										attrSet.add(coupDisGenObj.getSkuValue());
										sameAttr.put(attrQty, attrSet);
										
									}else{
										Set<String> attrSet = new HashSet<String>();
										attrSet.add(coupDisGenObj.getSkuValue());
										sameAttr.put(attrQty, attrSet);
									}
									/*sameAttr.put(coupDisGenObj.getSkuValue(), coupDisGenObj.getQuantity());
									
									if(coupDisGenObj.getQuantity() != null && quantity != null)
									
									if(description.length() >0) description.append(Constants.DELIMETER_COMMA+" ");
									description.append(coupDisGenObj.getSkuValue());
									
									
									
									if(attrQty == null){
										attrQty = coupDisGenObj.getQuantity();
										quantity = coupDisGenObj.getQuantity();
									}
									attrQty = coupDisGenObj.getQuantity();
									if(attrQty != null && !attrQty.isEmpty()) {
										
										if(attrQty.equals(coupDisGenObj.getQuantity())){
											
											
										}else{
											description.append(" up to "+(quantity)+" item(s)");
													if(description.length() >0) description.append(Constants.DELIMETER_COMMA+" ");
													description.append(coupDisGenObj.getSkuValue());
													
										}
										
										
									}
									
									logger.debug("quantity=="+quantity);*/
									
								
								/*if(attributeMap.containsKey(coupDisGenObj.getSkuAttribute())){
									
									atributeValue = attributeMap.get(coupDisGenObj.getSkuAttribute());
									if(atributeValue == null) atributeValue = new HashSet<String>();
									
									atributeValue.add(coupDisGenObj.getSkuValue());
								}else{
									
									atributeValue = new HashSet<String>();
									atributeValue.add(coupDisGenObj.getSkuValue());
								}*/
								
								/*if(quantity != null && !quantity.isEmpty()) {
									if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
										
										description .append(" on "+(quantity)+" item(s)");
									}else{
										
										description .append(" on up to "+(quantity)+" item(s)");
										
									}
									//description.append(".");
								}
								
								*/
								
							}
							for (String qty : sameAttr.keySet()) {
								
								Set<String> attrSet = sameAttr.get(qty);
								for (String attr : attrSet) {
									if(description.length() >0) description.append(Constants.DELIMETER_COMMA+" ");
									description.append(attr);
									
								}
								/*if(quantity != null && !quantity.isEmpty()) {
									if(quantityCriteria == null || quantityCriteria.isEmpty()){
										
										description .append(" up to "+(getActualNumber(Double.parseDouble(quantity)))+" item(s)");
										
									}else{
										
										description .append(" quantity "+Utility.limitQuantityMap.get(quantityCriteria).toLowerCase()+" "+(getActualNumber(Double.parseDouble(quantity)))+" item(s)");
									}
								}		*/						
								
								
								logger.info("desc 1 ==="+description);
								
								logger.info("qty value=="+qty);
								
								logger.info("qtyAndQtyCriMap.get(qty)====="+qtyAndQtyCriMap.get(qty));
								
								if(qty.equals("all")) {
									
									description .append(" items");
								}else{
									if(qtyAndQtyCriMap.get(qty) == null ){
										
										description .append(" up to "+(getActualNumber(Double.parseDouble(qty)))+" item(s)");
										
									}else{
										
										description .append(" quantity "+Utility.limitQuantityMap.get(qtyAndQtyCriMap.get(qty)).toLowerCase()+" "+(getActualNumber(Double.parseDouble(qty)))+" item(s)");
									}
								}
								
								logger.info("desc after appending ==="+description);

								
								description.append(Constants.DELIMETER_SEMICOLON+" "+
										(coupon.getDiscountType().equals("Value") ? 
												(userCurrencySymbol +getActualNumber(obj)):(getActualNumber(obj)+"%")));
								
							}
						
						}
							
							
						if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
							String ltyStr = (" against "+coupon.getRequiredLoyltyPoits()+" Reward type: "+
									(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode())+".");
							logger.debug("ltyStr==="+ltyStr);
							description.append(ltyStr );
							
						}else if(coupon.getLoyaltyPoints() != null && coupon.getMultiplierValue() != null){
							
							String ltyStr = (" against "+coupon.getMultiplierValue()+"x points of the discount value. Reward type: "+
									(coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode())+".");
							logger.debug("ltyStr==="+ltyStr);
							description.append(ltyStr );
						}
					}
						
						
					description = new StringBuilder( "Discount on "+description.toString());
				}//else SKU
				
				
				logger.debug("desc ==="+description);
			} catch (Exception e) {
				logger.debug("Exception ", e);
			}
			return description.toString();
		}
	
	public String getActualNumber(Double givenDecimalNum){
		
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(givenDecimalNum));
		int intValue = bigDecimal.intValue();
		//bigDecimal.toPlainString());
		BigDecimal decimalPart =  bigDecimal.subtract( new BigDecimal(intValue));
		if(decimalPart.doubleValue() != 0.0) return bigDecimal.toPlainString();
		else return intValue+"";
	}
}
