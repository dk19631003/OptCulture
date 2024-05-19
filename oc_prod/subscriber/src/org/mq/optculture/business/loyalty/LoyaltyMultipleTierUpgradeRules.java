package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyMultipleTierUpgradeRules {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	//when it has multiple tier rules based on newly added column.
	
	public  LoyaltyProgramTier applyMultipleTierUpgdRules(Long contactId, ContactsLoyalty contactsLoyalty,
			LoyaltyProgramTier currTier) throws Exception {
		
		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao
				.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tempTiersList == null || tempTiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return currTier;
		} else if (tempTiersList.size() >= 1) {
			Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if (num1 < num2) {
						return -1;
					} else if (num1 == num2) {
						return 0;
					} else {
						return 1;
					}
				}
			});
		}

		List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
		boolean flag = false;
		for (LoyaltyProgramTier tier : tempTiersList) {
			logger.info("tier level : " + tier.getTierType());
			if (currTier.getTierType().equalsIgnoreCase(tier.getTierType())) {
				flag = true;
			}
			if (flag) {
				tiersList.add(tier);
			}
		}

		// Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while (iterTier.hasNext()) {
			nexttier = iterTier.next();
			
				
				if (currTier.getTierType().equals(nexttier.getTierType())) {
					eligibleMap.put(nexttier, null);
				} else {
					
					String[] listOfRules = prevtier.getMultipleTierUpgrdRules().split("\\|\\|");
					
					for(String rule : listOfRules) {
						
						String[] listOfAttributes = rule.split(":");
						
					if ((Integer.valueOf(prevtier.getTierType().substring(5)) + 1) == Integer
							.valueOf(nexttier.getTierType().substring(5))
							&& listOfAttributes[1] != null) {
						eligibleMap.put(nexttier, prevtier);
						logger.info("eligible tier =" + nexttier.getTierType() + " upgdconstrant value = "
								+ listOfAttributes[1]);
					}
				}
			}
			prevtier = nexttier;
		}
		
		Iterator<LoyaltyProgramTier> it = tiersList.iterator();
		LoyaltyProgramTier prevKeyTier = null;
		LoyaltyProgramTier nextKeyTier = null;
		LoyaltyProgramTier finalTier = null;
		boolean tierChngd = false;
		LoyaltyProgramTier updTier = null;
		while (it.hasNext()) {
			nextKeyTier = it.next();
			
			if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
				prevKeyTier = nextKeyTier;
				continue;
			}
			
			String[] listOfRules = eligibleMap.get(nextKeyTier).getMultipleTierUpgrdRules().split("\\|\\|");
			
			for(String rule : listOfRules) {
				
				String[] listOfAttributes = null;
				String[] combinedRules = rule.split(Constants.ADDR_COL_DELIMETER);
				boolean conditionmet = false;
				for (String combinedrule : combinedRules) {
				
				listOfAttributes = combinedrule.split(":");
				String type = listOfAttributes[0];
				String value = listOfAttributes[1];
				
				if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
						type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) {

					Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00
							: contactsLoyalty.getTotalLoyaltyEarned();
					logger.info("totLoyaltyPointsValue value = " + totLoyaltyPointsValue);

					if (totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0) {
						logger.info("totLoyaltyPointsValue value is empty...");
						if(tierChngd) finalTier= updTier;//APP-4500
						else
						finalTier = currTier;
					} else {
						
						/*
						 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
						 * prevKeyTier = nextKeyTier; continue; }
						 */
							
							if (totLoyaltyPointsValue > 0
									&& totLoyaltyPointsValue < Integer.parseInt(value)) {
								
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								/*if (prevKeyTier == null) {
									logger.info("selected tier is currTier..." + currTier.getTierType());
									finalTier = currTier;
									//break;
									//return currTier;
								} else {
									
									logger.info("selected tier..." + prevKeyTier.getTierType());
									finalTier = prevKeyTier;
									//break;
									//return prevKeyTier;
								}*/
							} else if (totLoyaltyPointsValue > 0
									&& totLoyaltyPointsValue >= Integer.parseInt(value)) {
									//&& !it.hasNext()) {
								logger.info("selected tier..." + nextKeyTier.getTierType());
								
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_LIFETIME_POINTS);
								//return nextKeyTier
							}
							//prevKeyTier = nextKeyTier;
						
						//return currTier;
					} // else
				
				} else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
						       type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) {
					
					
					Double totPurchaseValue = null;
					
					totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
					logger.info("purchase value = " + totPurchaseValue);

					
					if (totPurchaseValue == null || totPurchaseValue <= 0) {
						logger.info("purchase value is empty...");
						if(tierChngd) finalTier= updTier;//APP-4500
						else
						finalTier = currTier;
						//return currTier;
					} else {
						
						/*
						 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
						 * prevKeyTier = nextKeyTier; continue; }
						 */
							
							if (totPurchaseValue > 0
									&& totPurchaseValue < Double.parseDouble(value)) {
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								/*if (prevKeyTier == null) {
									logger.info("selected tier is currTier..." + currTier.getTierType());
									finalTier = currTier;
									//break;
									//return currTier;
								} else {
									
									logger.info("selected tier..." + prevKeyTier.getTierType());
									finalTier = prevKeyTier;
									//break;
									//return prevKeyTier;
								}*/
							} else if (totPurchaseValue > 0
									&& totPurchaseValue >= Double.parseDouble(value)) {
									//&& !it.hasNext()) {
								logger.info("selected tier..." + nextKeyTier.getTierType());
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
								//return nextKeyTier;
							}
							//prevKeyTier = nextKeyTier;
						
						//return currTier;
					} // else
					
				}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() &&
						   type.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
					
					Double cumulativeAmount = 0.0;
					String period = listOfAttributes[2];

						
						Calendar startCal = Calendar.getInstance();
						Calendar endCal = Calendar.getInstance();
						endCal.add(Calendar.MONTH, -Integer.parseInt(period));
						//endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

						String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
						String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
						logger.info("contactId = " + contactId + " startDate = " + startDate + " endDate = " + endDate);


						LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
								.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
						cumulativeAmount = Double
								.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(),
										contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

						if (cumulativeAmount == null || cumulativeAmount <= 0) {
							logger.info("cumulative purchase value is empty...");
							if(tierChngd) finalTier= updTier;//APP-4500
							else
							finalTier = currTier;
							//continue;
						} else {
							
							/*
							 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
							 * prevKeyTier = nextKeyTier; continue; }
							 */
							
							if (cumulativeAmount > 0
									&& cumulativeAmount < Double.parseDouble(value)) {
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								/*if (prevKeyTier == null) {
									logger.info("selected tier is currTier..." + currTier.getTierType());
									finalTier = currTier;
									//break;
									//return currTier;
								} else {
									
									logger.info("selected tier..." + prevKeyTier.getTierType());
									finalTier = prevKeyTier;
									//break;
									//return prevKeyTier;
								}*/
							} else if (cumulativeAmount > 0
									&& cumulativeAmount >= Double.parseDouble(value)) {
									//&& !it.hasNext()) {
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE);
								//return nextKeyTier;
							}
						}
					
					//return currTier;
					
				}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
							type.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {//App-4488
						

						
						Double cumulativeAmount = 0.0;
						String period = listOfAttributes[2];
						//conditionmet = false;
							
							String startDate = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
									contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
									MyCalendar.FORMAT_DATETIME_STYEAR);//starting from when the member upgraded to this tier
							String endCal1 = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
									contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
									MyCalendar.FORMAT_DATETIME_STYEAR);
							
							Calendar endCal = MyCalendar.string2Calendar(endCal1);
							endCal.add(Calendar.MONTH, Integer.parseInt(period));
							logger.info("date to string "+startDate+" "+endCal1);

							//String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
							String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
							logger.info("contactId = " + contactId + " startDate = " + startDate + " endDate = " + endDate);


							LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
									.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
							cumulativeAmount = Double
									.valueOf(loyaltyTransactionChildDao.getCumulativePoints(contactsLoyalty.getUserId(),
											contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

							if (cumulativeAmount == null || cumulativeAmount <= 0) {
								logger.info("cumulative points is empty...");
								if(tierChngd) finalTier= updTier;//APP-4500
								else
								finalTier = currTier;
								conditionmet = false;
								//continue;
							} else {
								
								/*
								 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
								 * prevKeyTier = nextKeyTier; continue; }
								 */
							if (cumulativeAmount > 0
										&& cumulativeAmount < Double.parseDouble(value)) {
									finalTier = currTier;
									if(tierChngd) finalTier=updTier;
									conditionmet = false;
									/*if (prevKeyTier == null) {
										logger.info("selected tier is currTier..." + currTier.getTierType());
										finalTier = currTier;
										//break;
										//return currTier;
									} else {
										
										logger.info("selected tier..." + prevKeyTier.getTierType());
										finalTier = prevKeyTier;
										//break;
										//return prevKeyTier;
									}*/
								} else if (cumulativeAmount > 0
										&& cumulativeAmount >= Double.parseDouble(value)) {
										//&& !it.hasNext()) {
									conditionmet = true;
									/*tierChngd = true;
									finalTier= nextKeyTier;
									updTier= finalTier;*/
									contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_CUMULATIVE_POINTS);
									//return nextKeyTier;
								}
							}
						
						//return currTier;

						
					}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
							type.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {//App-4488

						

						
						Double cumulativeAmount = 0.0;
						String period = listOfAttributes[2];

							
							/*Calendar startCal = Calendar.getInstance();//starting from when the member upgraded to this tier
							Calendar endCal = Calendar.getInstance();
							endCal.add(Calendar.MONTH, Integer.parseInt(period));
							//endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

							String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
							String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
							logger.info("contactId = " + contactId + " startDate = " + startDate + " endDate = " + endDate);*/
						
						String startDate = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
								contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
								MyCalendar.FORMAT_DATETIME_STYEAR);//starting from when the member upgraded to this tier
						String endCal1 = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
								contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
								MyCalendar.FORMAT_DATETIME_STYEAR);
						
						Calendar endCal = MyCalendar.string2Calendar(endCal1);
						endCal.add(Calendar.MONTH, Integer.parseInt(period));
						logger.info("date to string "+startDate+" "+endCal1);

						//String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
						String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
						logger.info("contactId = " + contactId + " startDate = " + startDate + " endDate = " + endDate);


							LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
									.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
							cumulativeAmount = Double
									.valueOf(loyaltyTransactionChildDao.getCumulativeVisitBy(contactsLoyalty.getLoyaltyId(),contactsLoyalty.getUserId(),
											 startDate, endDate));

							if (cumulativeAmount == null || cumulativeAmount <= 0) {
								logger.info("cumulative visits value is empty...");
								if(tierChngd) finalTier= updTier;//APP-4500
								else
								finalTier = currTier;
								conditionmet = false;
								//continue;
							} else {
								
								/*
								 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
								 * prevKeyTier = nextKeyTier; continue; }
								 */
							if (cumulativeAmount > 0
										&& cumulativeAmount < Double.parseDouble(value)) {
									finalTier = currTier;
									if(tierChngd) finalTier=updTier;
									conditionmet = false;
									/*if (prevKeyTier == null) {
										logger.info("selected tier is currTier..." + currTier.getTierType());
										finalTier = currTier;
										//break;
										//return currTier;
									} else {
										
										logger.info("selected tier..." + prevKeyTier.getTierType());
										finalTier = prevKeyTier;
										//break;
										//return prevKeyTier;
									}*/
								} else if (cumulativeAmount > 0
										&& cumulativeAmount >= Double.parseDouble(value)) {
										//&& !it.hasNext()) {
									conditionmet = true;
									contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_CUMULATIVE_VISITS);
									//return nextKeyTier;
								}
							}
						
						//return currTier;

						
					
						
					}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
							type.equalsIgnoreCase(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) {
					
	                Double singleShotPurchaseValue = null;
					
	                LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
							.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	                
	                singleShotPurchaseValue = loyaltyTransactionChildDao.findPurchaseAmountInLast24Hours(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
	                
					logger.info("purchase value = " + singleShotPurchaseValue);

					
					if (singleShotPurchaseValue == null || singleShotPurchaseValue <= 0) {
						logger.info("single purchase value is empty...");
						if(tierChngd) finalTier= updTier;//APP-4500
						else
						finalTier = currTier;
						//return currTier;
					} else {
						
						/*
						 * if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
						 * prevKeyTier = nextKeyTier; continue; }
						 */
							
							if (singleShotPurchaseValue > 0
									&& singleShotPurchaseValue < Double.parseDouble(value)) {
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								/*if (prevKeyTier == null) {
									logger.info("selected tier is currTier..." + currTier.getTierType());
									finalTier = currTier;
									//break;
									//return currTier;
								} else {
									
									logger.info("selected tier..." + prevKeyTier.getTierType());
									finalTier = prevKeyTier;
									//break;
									//return prevKeyTier;
								}*/
							} else if (singleShotPurchaseValue > 0
									&& singleShotPurchaseValue >= Double.parseDouble(value)) {
									//&& !it.hasNext()) {
								logger.info("selected tier..." + nextKeyTier.getTierType());
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE);
								//return nextKeyTier;
							}
							//prevKeyTier = nextKeyTier;
						}
						//return currTier;
					
				}
				/*if(finalTier!=null && !currTier.getTierType().equalsIgnoreCase(finalTier.getTierType())) {
					break;
				}*/
				if(combinedRules.length>1 ) {
					if(!conditionmet) break;
					
				}
				
			}
			prevKeyTier = nextKeyTier;
			if((combinedRules.length>1 && conditionmet) || (combinedRules.length<=1 && conditionmet)) {
				tierChngd = true;
				finalTier= nextKeyTier;
				updTier= finalTier;
				//contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_CUMULATIVE_VISITS);
			}
		}
		}
		
		return finalTier;
			
		
		
	}
		
		/*LoyaltyProgramTier finalTier = null;
		String[] listOfRules = currTier.getMultipleTierUpgrdRules().split("\\|\\|");
		
		for(String rule : listOfRules) {
			
			String[] listOfAttributes = rule.split(":");
			
			String type = listOfAttributes[0];
			String value = listOfAttributes[1];
			//String period = listOfAttributes[2];
			
			if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
					type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) {

				Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00
						: contactsLoyalty.getTotalLoyaltyEarned();
				logger.info("totLoyaltyPointsValue value = " + totLoyaltyPointsValue);

				if (totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0) {
					logger.info("totLoyaltyPointsValue value is empty...");
					finalTier = currTier;
				} else {
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while (it.hasNext()) {
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::" + nextKeyTier.getTierType());
						logger.info("-------------currTier::" + currTier.getTierType());
						if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
							prevKeyTier = nextKeyTier;
							continue;
						}
						
						if (totLoyaltyPointsValue > 0
								&& totLoyaltyPointsValue < Integer.parseInt(value)) {
							if (prevKeyTier == null) {
								logger.info("selected tier is currTier..." + currTier.getTierType());
								finalTier = currTier;
								//break;
								//return currTier;
							} else {
								
								logger.info("selected tier..." + prevKeyTier.getTierType());
								finalTier = prevKeyTier;
								//break;
								//return prevKeyTier;
							}
						} else if (totLoyaltyPointsValue > 0
								&& totLoyaltyPointsValue >= Integer.parseInt(value)
								&& !it.hasNext()) {
							logger.info("selected tier..." + nextKeyTier.getTierType());
							
							finalTier= nextKeyTier;
							//return nextKeyTier
						}
						prevKeyTier = nextKeyTier;
					}
					//return currTier;
				} // else
			
			} else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
					       type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) {
				
				
				Double totPurchaseValue = null;
				
				totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
				logger.info("purchase value = " + totPurchaseValue);

				
				if (totPurchaseValue == null || totPurchaseValue <= 0) {
					logger.info("purchase value is empty...");
					finalTier = currTier;
					//return currTier;
				} else {
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while (it.hasNext()) {
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::" + nextKeyTier.getTierType());
						logger.info("-------------currTier::" + currTier.getTierType());
						if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
							prevKeyTier = nextKeyTier;
							continue;
						}
						
						if (totPurchaseValue > 0
								&& totPurchaseValue < Double.parseDouble(value)) {
							if (prevKeyTier == null) {
								logger.info("selected tier is currTier..." + currTier.getTierType());
								finalTier = currTier;
								//break;
								//return currTier;
							} else {
								
								logger.info("selected tier..." + prevKeyTier.getTierType());
								finalTier = prevKeyTier;
								//break;
								//return prevKeyTier;
							}
						} else if (totPurchaseValue > 0
								&& totPurchaseValue >= Double.parseDouble(value)
								&& !it.hasNext()) {
							logger.info("selected tier..." + nextKeyTier.getTierType());
							finalTier = nextKeyTier;
							//return nextKeyTier;
						}
						prevKeyTier = nextKeyTier;
					}
					//return currTier;
				} // else
				
			}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() &&
					   type.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
				
				Double cumulativeAmount = 0.0;
				String period = listOfAttributes[2];

				ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());

				LoyaltyProgramTier nextKeyTier = null;
				while (it.hasPrevious()) {
					nextKeyTier = it.previous();
					logger.info("------------nextKeyTier::" + nextKeyTier.getTierType());
					logger.info("-------------currTier::" + currTier.getTierType());
					if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {

						return currTier;
					}
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -Integer.parseInt(period));
					//endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = " + contactId + " startDate = " + startDate + " endDate = " + endDate);


					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
							.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					cumulativeAmount = Double
							.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(),
									contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

					if (cumulativeAmount == null || cumulativeAmount <= 0) {
						logger.info("cumulative purchase value is empty...");
						continue;
					}

					if (cumulativeAmount > 0
							&& cumulativeAmount >= Double.parseDouble(value)) {
						finalTier = nextKeyTier;
						//return nextKeyTier;
					}

				}
				
				//return currTier;
				
			}else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && type.equalsIgnoreCase(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) {
				
                Double singleShotPurchaseValue = null;
				
                LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
                
                singleShotPurchaseValue = loyaltyTransactionChildDao.findPurchaseAmountInLast24Hours(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
                
				logger.info("purchase value = " + singleShotPurchaseValue);

				
				if (singleShotPurchaseValue == null || singleShotPurchaseValue <= 0) {
					logger.info("purchase value is empty...");
					finalTier = currTier;
					//return currTier;
				} else {
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while (it.hasNext()) {
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::" + nextKeyTier.getTierType());
						logger.info("-------------currTier::" + currTier.getTierType());
						if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
							prevKeyTier = nextKeyTier;
							continue;
						}
						
						if (singleShotPurchaseValue > 0
								&& singleShotPurchaseValue < Double.parseDouble(value)) {
							if (prevKeyTier == null) {
								logger.info("selected tier is currTier..." + currTier.getTierType());
								finalTier = currTier;
								//break;
								//return currTier;
							}else {
								
								logger.info("selected tier..." + prevKeyTier.getTierType());
								finalTier = prevKeyTier;
								//break;
								//return prevKeyTier;
							}
						} else if (singleShotPurchaseValue > 0
								&& singleShotPurchaseValue >= Double.parseDouble(value)
								&& !it.hasNext()) {
							logger.info("selected tier..." + nextKeyTier.getTierType());
							finalTier = nextKeyTier;
							//return nextKeyTier;
						}
						prevKeyTier = nextKeyTier;
					}
					//return currTier;
				} // else
				
			}
			if(!currTier.getTierType().equalsIgnoreCase(finalTier.getTierType())) {
				break;
			}
		}*/
		
		

}
