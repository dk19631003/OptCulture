package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltyMembershipExpiry{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	//@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
@Scheduled(cron="0 0 0 3 * ?")    //for every month 1st at 00:00hrs
	public void run_task() {
		logger.info(">>> Started loyalty membership Expiry timer >>>");
		List<LoyaltyProgram> programsList;
		try {
			
			programsList = fetchAllPrograms();
			if(programsList == null || programsList.isEmpty()){
				return;
			}
			logger.info(">>>>>>>>>>> list size::"+programsList.size());
			for (LoyaltyProgram programObj : programsList) {
				logger.info(">>>>>>>>>>> entered for::");
				List<LoyaltyProgramTier> tierList = null;

				if(OCConstants.FLAG_YES == programObj.getGiftMembrshpExpiryFlag()) {
					Calendar currentCal = Calendar.getInstance();

					if(programObj.getGiftMembrshpExpiryDateType() != null && !programObj.getGiftMembrshpExpiryDateType().isEmpty()){
						if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(programObj.getGiftMembrshpExpiryDateType())){
							currentCal.add(Calendar.MONTH, -programObj.getGiftMembrshpExpiryDateValue().intValue());
						}
						else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(programObj.getGiftMembrshpExpiryDateType())){
							currentCal.add(Calendar.MONTH, -(12*programObj.getGiftMembrshpExpiryDateValue().intValue()));
						}
//						String currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
						List<Object[]> conList = null ;
						String currentDateStr = "";
						if(currentCal.get(Calendar.MONTH) == 0){
							int year = currentCal.get(Calendar.YEAR)-1;
							currentDateStr = year+"-12";
						}
						else{
							currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
						}
						logger.info("currentDateStr ::::::::::"+currentDateStr);
						//just update with a single query.
						
						Long totalCount = updateGiftCustomersToExpire(programObj.getUserId(), programObj.getProgramId(),currentDateStr);
						logger.info("totalCount ::::::::::"+totalCount);
					/*	Long totalCount = getCountGiftCustomersToExpire(programObj.getUserId(), programObj.getProgramId(),currentDateStr);
						if(totalCount > 0) {
							
							int threshold=1000;
							 int initialIndex=0;
							 long num_of_chunks= 1;
							 if(totalCount > threshold){
								 
								  num_of_chunks=(totalCount/threshold);
								 if(totalCount<threshold)
								 {
									 num_of_chunks=1;
								 }
								 else if((totalCount%threshold)>0){
									 num_of_chunks=(totalCount/threshold)+1;
								 }
								 else
								 {
									 num_of_chunks=(totalCount/threshold);
								 }
								 
							 }
							 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
							 {
								 conList = getGiftCustomersToExpire(programObj.getUserId(), programObj.getProgramId(),currentDateStr, initialIndex, threshold);
								 if(conList != null && !conList.isEmpty()){
										logger.info("conList size ::::::::::"+conList.size());
										for(Object[] objArr : conList) {
											logger.info("contactId ::::::::::"+objArr[0]);
											ContactsLoyalty contactsLoyalty = contactLoyaltyDao.findAllByLoyaltyId((Long) objArr[0]);
											contactsLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED);
											contactLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
										}
								 }
								 initialIndex =(loop_var*threshold)+1;
							 }
						
						}*/

					
					}
				}
				if(OCConstants.FLAG_YES == programObj.getMembershipExpiryFlag()) {
					tierList = getAllTiersByPrgmId(programObj.getProgramId());

					if(tierList != null && !tierList.isEmpty()){
						logger.info("tierList size ::::::::::"+tierList.size());

						for(LoyaltyProgramTier tierObj : tierList) {
							logger.info("programId ::::::::::"+tierObj.getProgramId()+":::: tierId ::::::::"+tierObj.getTierId());
							List<Object[]> conList = null ;
							Calendar currentCal = Calendar.getInstance();

							if(tierObj.getMembershipExpiryDateType() != null && !tierObj.getMembershipExpiryDateType().isEmpty()){

								if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tierObj.getMembershipExpiryDateType())){
									currentCal.add(Calendar.MONTH, -tierObj.getMembershipExpiryDateValue().intValue());
								}
								else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tierObj.getMembershipExpiryDateType())){
									currentCal.add(Calendar.MONTH, -(12*tierObj.getMembershipExpiryDateValue().intValue()));
								}
//								String currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
								String currentDateStr = "";
								if(currentCal.get(Calendar.MONTH) == 0){
									int year = currentCal.get(Calendar.YEAR)-1;
									currentDateStr = year+"-12";
								}
								else{
									currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
								}
								logger.info("currentDateStr ::::::::::"+currentDateStr);

								List<Object[]> createdDtList = null;
								List<Object[]> upgdDtList = null;
								
								 if(programObj.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES) {
									 
									 Long totalCount = updateLtyCustomersToExpireByUpgradedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr);
									 logger.info("totalCount:: "+totalCount);
									 
									 /*if(totalCount > 0) {
											
											int threshold=1000;
											 int initialIndex=0;
											 long num_of_chunks= 1;
											 if(totalCount > threshold){
												 
												  num_of_chunks=(totalCount/threshold);
												 if(totalCount<threshold)
												 {
													 num_of_chunks=1;
												 }
												 else if((totalCount%threshold)>0){
													 num_of_chunks=(totalCount/threshold)+1;
												 }
												 else
												 {
													 num_of_chunks=(totalCount/threshold);
												 }
												 
											 }
											 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
											 {
												 conList = getLtyCustomersToExpireByUpgradedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr, initialIndex, threshold);
												 if(conList != null && !conList.isEmpty()){
													logger.info("conList size ::::::::::"+conList.size());
													for(Object[] objArr : conList) {
														logger.info("contactId ::::::::::"+objArr[0]);
														ContactsLoyalty contactsLoyalty = contactLoyaltyDao.findAllByLoyaltyId((Long) objArr[0]);
														contactsLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED);
														contactLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
													}
												}
											 }
									 
									 
										}*/
								 }else {
									 
									 Long totalCount = updateLtyCustomersToExpireByCreatedDate(programObj.getUserId(), programObj.getProgramId(),tierObj.getTierId(),currentDateStr, OCConstants.FLAG_NO);
									 logger.debug("totalCount ::"+totalCount);
									 /*
									 
									 Long totalCount = getCountOfLtyCustomersToExpireByCreatedDate(programObj.getUserId(), programObj.getProgramId(),tierObj.getTierId(),currentDateStr, OCConstants.FLAG_NO);
										if(totalCount > 0) {
											
											int threshold=1000;
											 int initialIndex=0;
											 long num_of_chunks= 1;
											 if(totalCount > threshold){
												 
												  num_of_chunks=(totalCount/threshold);
												 if(totalCount<threshold)
												 {
													 num_of_chunks=1;
												 }
												 else if((totalCount%threshold)>0){
													 num_of_chunks=(totalCount/threshold)+1;
												 }
												 else
												 {
													 num_of_chunks=(totalCount/threshold);
												 }
												 
											 }
											 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
											 {
												 
												 conList = getLtyCustomersToExpireByCreatedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr,OCConstants.FLAG_NO, initialIndex, threshold);
												 if(conList != null && !conList.isEmpty()) {
													 logger.info("conList size ::::::::::"+conList.size());
														for(Object[] objArr : conList) {
															logger.info("contactId ::::::::::"+objArr[0]);
															ContactsLoyalty contactsLoyalty = contactLoyaltyDao.findAllByLoyaltyId((Long) objArr[0]);
															contactsLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED);
															contactLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
														}
													 
												 }
											 }
								
										}
		
							
								 */}
							}
							 
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		logger.info(">>> Completed loyalty Rewards Expiry timer >>>");
	}
	private long updateLtyCustomersToExpireByCreatedDate(Long userId,Long programID,  Long tierId, String currentDateStr, char resetFlag) throws LoyaltyProgramException {
		long retCount = 0l;
			try{
				ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				
				retCount = contactsLoyaltyDaoForDML.updateLtyCustomersToExpireByUpgradedDate(userId, programID, tierId, currentDateStr);
				
			
			}catch(Exception e){
				logger.error("Exception in dao service...");
				throw new LoyaltyProgramException("fetch all customers failed.");
			}
			
		
		
		
		return retCount;
	}
	private List<LoyaltyProgram> fetchAllPrograms() throws LoyaltyProgramException {

		List<LoyaltyProgram> programsList =null;
		try{

			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			programsList = loyaltyProgramDao.fetchAllMemberExpPrograms();
			if(programsList != null && programsList.size() > 0){
				logger.info("list size ::::::::::"+programsList.size());
				return programsList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all programs failed.");
		}

		return programsList;

	}
	private long updateLtyCustomersToExpireByUpgradedDate(Long userId,Long programID,  Long tierId, String currentDateStr) throws LoyaltyProgramException {
		Long totalCount = 0l;
		
		try{
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			
			totalCount = contactsLoyaltyDaoForDML.updateLtyCustomersToExpireByUpgradedDate(userId, programID, tierId, currentDateStr);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return totalCount;
	}
	private List<LoyaltyProgramTier> getAllTiersByPrgmId(Long prgmId) throws LoyaltyProgramException {

		List<LoyaltyProgramTier> tierList =null;
		try{

			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			tierList = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
			if(tierList != null && tierList.size() > 0){
				logger.info("tier list size ::::::::::"+tierList.size());
				return tierList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all tiers failed.");
		}

		return tierList;

	}
	private long updateGiftCustomersToExpire(long userId, Long programId, String currentDateStr) throws LoyaltyProgramException {
		Long totalCount = 0l;
		
		try{
			
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			totalCount = contactsLoyaltyDaoForDML.updateGiftCustomersToExpire(userId, programId, currentDateStr);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return totalCount;
	}
	/*private long getCountGiftCustomersToExpire(long userId, Long programId, String currentDateStr) throws LoyaltyProgramException {
		Long totalCount = 0l;
		
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalCount = contactsLoyaltyDao.getCountOfGiftCustomersToExpire(userId, programId, currentDateStr);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return totalCount;
	}*/
	/*private List<Object[]> getGiftCustomersToExpire(Long userId, Long programId, String currentDateStr, int startFrom, int maxRecords) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getGiftCustomersToExpire(userId, programId, currentDateStr,  startFrom, maxRecords);
			if(contList != null && contList.size() > 0){
				logger.info("contact loyalty list size ::::::::::"+contList.size());
				return contList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}

		return contList;
	}
*/
/*	private List<Object[]> getLtyCustomersToExpireByCreatedDate(Long userId,Long programId,  Long tierId, String currentDateStr, char resetFlag, int startFrom, int maxRecord) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getLtyCustomersToExpireByCreatedDate(userId, programId, tierId, currentDateStr, resetFlag, startFrom, maxRecord);
			if(contList != null && contList.size() > 0){
				logger.info("contact loyalty list size ::::::::::"+contList.size());
				return contList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}

		return contList;
	}

	private List<Object[]> getLtyCustomersToExpireByUpgradedDate(Long userId, Long programID, Long tierId, String currentDateStr, int startFrom, int maxRecord) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getLtyCustomersToExpireByUpgradedDate(userId, programID, tierId, currentDateStr, startFrom, maxRecord);
			if(contList != null && contList.size() > 0){
				logger.info("contact loyalty list size ::::::::::"+contList.size());
				return contList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}

		return contList;
	}
*/



}
