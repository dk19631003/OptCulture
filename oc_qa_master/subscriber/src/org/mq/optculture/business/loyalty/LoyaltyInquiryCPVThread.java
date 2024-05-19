package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyInquiryCPVThread implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private List<LoyaltyProgramTier> tiersList;
	private Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap;
	private Long userId;
	private Long contactId;
	private Long loyaltyId;

	public LoyaltyInquiryCPVThread(Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap, Long userId, Long contactId, List<LoyaltyProgramTier> tierList, Long loyaltyId) {
		this.eligibleMap = eligibleMap;
		this.userId = userId;
		this.contactId = contactId;
		this.tiersList = tierList;
		this.loyaltyId = loyaltyId;
	}

	@Override
	public void run() {

		LoyaltyProgramTier tier =  findTier();
		saveProperties(tier);
	}

	private void saveProperties(LoyaltyProgramTier tier){

		try{
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty loyalty = loyaltyDao.findAllByLoyaltyId(loyaltyId);

			loyalty.setProgramTierId(tier.getTierId());
			saveContactLoyalty(loyalty);

		}catch(Exception e){
			logger.error("Exception while saving tier data...",e);
		}
	}

	private void saveContactLoyalty(ContactsLoyalty loyalty) throws Exception{

		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(loyalty);
	}

	private LoyaltyProgramTier findTier(){
		try{
			Double cumulativeAmount = 0.0;
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty contactsLoyalty = null;
			contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contactId);
//			Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//			LoyaltyProgramTier prevKeyTier = null;
			LoyaltyProgramTier nextKeyTier = null;
			while(it.hasPrevious()){
				nextKeyTier = it.previous();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------currTier::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//					prevKeyTier = nextKeyTier;
					return tiersList.get(0);
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				/*Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));


				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			/*while(it.hasNext()){
				nextKeyTier = it.next();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
					prevKeyTier = nextKeyTier;
					continue;
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -((LoyaltyProgramTier) eligibleMap.get(nextKeyTier)).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				if(cumulativeAmount > 0 && cumulativeAmount < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					if(prevKeyTier == null){
						logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
						return tiersList.get(0);
					}
					logger.info("selected tier..."+prevKeyTier.getTierType());
					return prevKeyTier;
				}
				else if (cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
					logger.info("selected tier..."+nextKeyTier.getTierType());
					return nextKeyTier;
				}
				prevKeyTier = nextKeyTier;
			}*/
			return tiersList.get(0);
		}catch(Exception e){
			logger.error("Excepion in cpv thread ", e);
			return tiersList.get(0);
		}
	}
	private Object[] getCumulativeValue(String startDate, String endDate) throws Exception {

		RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
		Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(userId, contactId, startDate, endDate);
		return cumulativeAmountArr;
	}
}

