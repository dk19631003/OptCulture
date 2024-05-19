package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiryUtil;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class MembershipProvider {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Iterator<ContactsLoyalty> iter;
	
	private Iterator<LoyaltyTransactionExpiryUtil> Bonusiter;
	private LoyaltyTransactionExpiryUtil LoyaltyTransactionExpiryUtil;
	private ContactsLoyalty membership;
	private int currentSizeInt = 0;
	private long maxLoyaltyId = 0;
	private LoyaltyProgram loyaltyProgram ;
	
	public MembershipProvider(){}
	
	public MembershipProvider(LoyaltyProgram loyaltyProgram){
		
		this.loyaltyProgram = loyaltyProgram;
	}
	public synchronized ContactsLoyalty getMembership() {
    	
    	logger.info("<< getMembership() just entered : "+ Thread.currentThread().getName());

        if (this.iter != null && this.iter.hasNext()) {
        	membership = iter.next();
            return membership;
        }

        //logger.info(" Getting the 10000 contact loyalty objects from ContactsLoyalty Table from :"+
    				//currentSizeInt+"-"+(currentSizeInt+10000));
        logger.info(" Getting the 10000 contact loyalty objects from ContactsLoyalty Table from loyalty ID :"+maxLoyaltyId);
    	
    		
    	List<ContactsLoyalty> loyaltyList = getMemberships(loyaltyProgram.getUserId(), loyaltyProgram.getProgramId(), maxLoyaltyId);
    	
    	if(loyaltyList == null || loyaltyList.size() <= 0){
    		logger.info("loyalty size is empty...");
    		return null;
    	}
    	
		if(logger.isDebugEnabled()) {
			logger.info(">>>>>Got the contact loyalty from DB: "+loyaltyList.size());
		}
		
		ContactsLoyalty loyalty = loyaltyList.get(loyaltyList.size()-1);
		maxLoyaltyId = loyalty.getLoyaltyId();

		/*for(ContactsLoyalty loyalty : loyaltyList) {
			if(loyalty.getLoyaltyId()>maxLoyaltyId)
				maxLoyaltyId = loyalty.getLoyaltyId();
		}*/
		logger.info("max loyalty id "+maxLoyaltyId +" program id "+loyalty.getProgramId());
		//currentSizeInt += 10000;

		this.iter = loyaltyList.listIterator();
		membership = iter.next();

        return membership;

    }
	
public synchronized LoyaltyTransactionExpiryUtil getMembershipForBonusExpiry() {
    	
    	logger.info("<< getMembership() just entered : "+ Thread.currentThread().getName());

        if (this.Bonusiter != null && this.Bonusiter.hasNext()) {
        	LoyaltyTransactionExpiryUtil = Bonusiter.next();
            return LoyaltyTransactionExpiryUtil;
        }

        logger.info(" Getting the 10000 contact loyalty objects from ContactsLoyalty Table from :"+
    				currentSizeInt+"-"+(currentSizeInt+10000));
    		
    	List<LoyaltyTransactionExpiryUtil> loyaltyList = getMembershipsForBonusExpiry(loyaltyProgram.getUserId(), loyaltyProgram.getProgramId(), currentSizeInt, 10000);
    	
    	if(loyaltyList == null || loyaltyList.size() <= 0){
    		logger.info("loyalty size is empty...");
    		return null;
    	}
    	
		if(logger.isDebugEnabled()) {
			logger.info(">>>>>Got the contact loyalty from DB: "+loyaltyList.size());
		}

		currentSizeInt += 10000;

		this.Bonusiter = loyaltyList.listIterator();
		LoyaltyTransactionExpiryUtil = Bonusiter.next();

        return LoyaltyTransactionExpiryUtil;

    }

private List<LoyaltyTransactionExpiryUtil> getMembershipsForBonusExpiry(Long userId, Long programID, int currentSize, int size){
	
	List<LoyaltyTransactionExpiryUtil> loyaltyList = new ArrayList<LoyaltyTransactionExpiryUtil>();
	try{
		
		LoyaltyTransactionExpiryDao loyaltyDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return loyaltyDao.getAllOCMembershipsBonusExpiry(userId, programID, currentSize, size);
		
	}catch(Exception e){
		logger.error("Exception in fetching 1000 memberships...", e);
	}
	return loyaltyList;
	
}

	private List<ContactsLoyalty> getMemberships(Long userId, Long programID, long loyaltyId){
	
		List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
		try{
			
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			return loyaltyDao.getAllOCMemberships(userId, programID, loyaltyId);
			
		}catch(Exception e){
			logger.error("Exception in fetching 1000 memberships...", e);
		}
		return loyaltyList;
		
	}
	
}
