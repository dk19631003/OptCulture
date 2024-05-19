package org.mq.optculture.business.helper;
/**
 * This class helps in data  Data Accessing.
 * @author vinod.bokare
 */
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.LoyaltySettings;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionChild;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyProgramService {

	public LoyaltyProgramService() {
		super();
	}

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private LoyaltyTransactionChildDao loyaltyTransactionChildDao;
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	
	/**
	 * This method retrieve Loyalty Program Tier
	 * @param tierId
	 * @return LoyaltyProgramTier
	 */
	public LoyaltyProgramTier getTierObj(Long tierId) {
		LoyaltyProgramTier tierObj=null;
		try {
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		logger.info("loyaltyProgramTierDao::"+loyaltyProgramTierDao);
		tierObj = loyaltyProgramTierDao.getTierById(tierId);
		logger.info("tierObj::"+tierObj);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return tierObj;
	}//getTierObj
	
	
	/**
	 * This method retrieve Loyalty Program
	 * @param prgmId
	 * @return Loyalty Program
	 */
	public LoyaltyProgram getProgmObj(Long prgmId) {
		LoyaltyProgram progObj = null;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			progObj = loyaltyProgramDao.findById(prgmId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return progObj;
		
	}//getProgmObj

	/**
	 * This method retrieve child memberships(if any ) 
	 * </br>used to retrieve Loyalty Transaction Child for source + dest
	 * @param destMembership
	 * @return
	 * @throws Exception
	 */
	private List<ContactsLoyalty> getChildMemberships(ContactsLoyalty destMembership) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		List<ContactsLoyalty> childMembershipList = loyaltyDao.findChildrenByParent(destMembership.getUserId(), destMembership.getLoyaltyId());
		
		return childMembershipList;
	}
	
	/**
	 * This method retrieve Loyalty Transaction Child
	 * @param membershipNumber
	 * @param transactionType
	 * @return LoyaltyTransactionChild
	 */
	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId,String transactionType){
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			loyaltyTransactionChild = loyaltyTransactionChildDao.getTransByMembershipNoAndTransType(loyaltyId,transactionType);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		return loyaltyTransactionChild;
	}//getTransByMembershipNoAndTransType

	
	/**
	 * This method get's LoyaltySettings by User Id.
	 * @param userId
	 * @return loyaltySettings.
	 */
	public LoyaltySettings findLoyaltySettingsByOrgId(Long orgId) {
		logger.debug(">>>>>>>>>>>>> entered in findLoyaltySettingsByOrgId");
		LoyaltySettings loyaltySettings=null;
		try{
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			loyaltySettings = loyaltySettingsDao.findByOrgId(orgId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findLoyaltySettingsByOrgId ");
		return loyaltySettings;
	}//findByUserId

	/**
	 * This method returns User Organization Name.
	 * @param userId
	 * @param defVal 
	 * @return organizationName
	 *//*
	public String findUserOrgNameByUserId(Long userId, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in findUserOrgNameByUserId");
		Users users =null;
		String organizationName = defVal;
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			users = usersDao.findByUserId(userId);
			organizationName = users.getUserOrganization().getOrganizationName();
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findUserOrgNameByUserId ");
		return organizationName;
	}//findUserOrgNameByUserId
*/
}//EOF

