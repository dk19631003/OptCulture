package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class MembershipProviderComm {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Iterator<Object[]> iter;
	private Object[] membership;
	private int currentSizeInt = 0;
	private long maxLoyaltyId = 0;
	private LoyaltyProgram loyaltyProgram ;
	public MembershipProviderComm(){}
	
	public MembershipProviderComm(LoyaltyProgram loyaltyProgram){
		
		this.loyaltyProgram = loyaltyProgram;
	}
	public synchronized Object[] getMembership() {
    	
    	logger.info("<< getMembership() just entered : "+ Thread.currentThread().getName());

        if (this.iter != null && this.iter.hasNext()) {
        	membership = iter.next();
        	//logger.info("membership iter = "+membership.getCardNumber());
            return membership;
        }

        //logger.info(" Getting the 10000 contact loyalty objects from ContactsLoyalty Table from :"+
    				//currentSizeInt+"-"+(currentSizeInt+10000));
        logger.info(" Getting the 10000 contact loyalty objects from ContactsLoyalty Table from loyalty ID :"+maxLoyaltyId);
    		
    	//List<Object[]> loyaltyList = getMemberships(loyaltyProgram.getUserId(), loyaltyProgram.getProgramId(), currentSizeInt, 10000);
    	List<Object[]> loyaltyList = getMemberships(loyaltyProgram.getUserId(), loyaltyProgram.getProgramId(), maxLoyaltyId);
    	
    	if(loyaltyList == null || loyaltyList.size() <= 0){
    		return null;
    	}
    	
		if(logger.isDebugEnabled()) {
			logger.info(">>>>>Got the contact loyalty from DB: "+loyaltyList.size());
		}
		/*for(Object[] loyalty : loyaltyList){
		}*/
		Object[] object = loyaltyList.get(loyaltyList.size()-1);
		ContactsLoyalty loyalty = (ContactsLoyalty)object[0];
		maxLoyaltyId = loyalty.getLoyaltyId();
		
		/*for(Object[] object : loyaltyList) {
			ContactsLoyalty loyalty = (ContactsLoyalty)object[0];
			if(loyalty.getLoyaltyId()>maxLoyaltyId)
				maxLoyaltyId = loyalty.getLoyaltyId();
		}*/
		logger.info("max loyalty id "+maxLoyaltyId +" program id "+loyalty.getProgramId());
		//currentSizeInt += 10000;

		this.iter = loyaltyList.listIterator();
		membership = iter.next();

        return membership;

    }


	private List<Object[]> getMemberships(Long userID, Long programID, long loyaltyId){
	
	List<Object[]> loyaltyList = new ArrayList<Object[]>();
	try{
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyaltyList = loyaltyDao.getAllOCMembershipsForComm(userID, programID, loyaltyId);
		return		loyaltyList;
	}catch(Exception e){
		logger.error("Exception in fetching 1000 memberships...", e);
	}
	return loyaltyList;
	
	}
	
}
