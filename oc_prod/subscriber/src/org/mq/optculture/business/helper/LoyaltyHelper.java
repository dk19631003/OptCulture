package org.mq.optculture.business.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyCustomer;
import org.mq.optculture.model.loyalty.LoyaltyCustomerInformation;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

public class LoyaltyHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public void generateLoyaltyCustomers(String userIdStr, String orgExtId, String filePath){
		
		logger.info("generate Loyalty customers method started...");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH))-1);
		
		String sqlQuery = "select c.external_id, cl.card_number, cl.card_pin "
				+ "from contacts c, contacts_loyalty cl where c.user_id in ("+ userIdStr+")"
				+ " and c.cid = cl.contact_id and cl.mode='offline' and cl.created_date > '" + format.format(calendar.getTime()) + "'";
		logger.info("customer dump query ="+sqlQuery);
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(sqlQuery);
		
		int totalCount = jdbcResultsetHandler.totalRecordsSize();
		LoyaltyCustomerInformation loyaltyCustomerInformation = new LoyaltyCustomerInformation();
		List<LoyaltyCustomer> loyaltyCustomerList = new ArrayList<LoyaltyCustomer>();
		LoyaltyCustomer loyaltyCustomer = null;
		List<String> resultList = jdbcResultsetHandler.getRecords();
		logger.info("customer data result set size = "+resultList.size());
		do{
			for (String recordString : resultList) {
				loyaltyCustomer = new LoyaltyCustomer();
				StringTokenizer strTokenizer = new StringTokenizer(recordString, ";");
				while(strTokenizer.hasMoreElements()){
					
					String keyValue = (String) strTokenizer.nextElement();
					String[] keyValueArray = keyValue.split("=");
					if(keyValueArray[0].equals("card_number")){
						loyaltyCustomer.setCardNumber(keyValueArray[1] == null ? "" : keyValueArray[1].toString());
						logger.info("card number= "+keyValueArray[1]);
					}
					if(keyValueArray[0].equals("external_id")){
						loyaltyCustomer.setCustomerId(keyValueArray[1] == null ? "" : keyValueArray[1].toString());
					}
					if(keyValueArray[0].equals("pin")){
						loyaltyCustomer.setPin(keyValueArray[1] == null ? "" : keyValueArray[1].toString());
					}
				}
				loyaltyCustomerList.add(loyaltyCustomer);
			}
			loyaltyCustomerInformation.setLoyaltyCustomerList(loyaltyCustomerList);
			
		}while(jdbcResultsetHandler.getCurrentFetchingCount() < totalCount-1);
		
		StringBuilder fileName = new StringBuilder(filePath).append("/")
				.append("LoyaltyCustomerID_Dump_").append(orgExtId).append(".xml");
		try {
			XMLUtil.marshalAndWriteToFileAsXML(fileName.toString(), LoyaltyCustomerInformation.class, loyaltyCustomerInformation);
		} catch (BaseServiceException e) {
			logger.error("Exception ....", e);
		}
		
		logger.info("generate Loyalty customers method completed...");
	}

	
	/**
	 * This method retrieve Loyalty Program Tier
	 * @param tierId
	 * @return LoyaltyProgramTier
	 */
	public LoyaltyProgramTier getTierObj(Long tierId) {
		LoyaltyProgramTier tierObj=null;
		try {
		LoyaltyProgramTierDao	loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
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
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
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
	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId,String transactionType,Long userId){
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		try {
			LoyaltyTransactionChildDao	loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			loyaltyTransactionChild = loyaltyTransactionChildDao.getTransByMembershipNoAndTransType(loyaltyId,transactionType,userId);
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


}
