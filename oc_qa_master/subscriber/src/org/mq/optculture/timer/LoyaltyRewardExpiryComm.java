package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltyRewardExpiryComm {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	//@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
	//@Scheduled(cron="0 0 0 2 * ?")    //for every month 2nd at 00:00hrs
	@Scheduled(cron="0 30 9 1 * ?")    //for every month 1st at 09:30hrs
	public void run_task() {
		logger.info("Entered into  loyalty reward expiry plus bonus communication...");
		List<LoyaltyProgram> listOfPrograms = null;
		List<LoyaltyThresholdBonus> listOfBonus = null;
		try{
			
			listOfPrograms = fetchAllPrograms();
			listOfBonus = fetchOnlyExpireBonus();
			if((listOfPrograms == null || listOfPrograms.isEmpty()) && (listOfBonus == null || listOfBonus.isEmpty()) ){
				
				logger.debug("No programs currently found, so returning");
				return;
			}
			if(listOfPrograms != null && !listOfPrograms.isEmpty()) {
				 /*ExecutorService executor = null;
				 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++){
					 
					 executor = Executors.newFixedThreadPool(NumOfUserThreads);
					 
					 Runnable rewardThread  = new LoyaltyRewardExpiryThreadComm();
					 executor.execute(rewardThread);
				 }*/
				for (LoyaltyProgram loyaltyProgram : listOfPrograms) {
					List<Thread> lisfOfThreads = new ArrayList<Thread>();
					
					MembershipProviderComm provider = new MembershipProviderComm(loyaltyProgram);
					
					Map<Long, LoyaltyProgramTier> tierMap = prepareTierMapForProgram(loyaltyProgram);
					LoyaltyAutoComm autoComm = getAutoCommByProgramId(loyaltyProgram.getProgramId());
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Users user = userDao.findByUserId(loyaltyProgram.getUserId());
					List<LoyaltyThresholdBonus> expireBonusLst = getExpireBonus(loyaltyProgram);
					loyaltyProgram.setExpireBonusList(expireBonusLst);
					if(autoComm == null) continue;
					for(int i = 0; i <= 10; i++){
						
						Thread th = new Thread(new LoyaltyRewardExpiryThreadComm(provider, loyaltyProgram, tierMap, autoComm, user));
						lisfOfThreads.add(th);
						th.start();
						
					}
					
					Iterator<Thread> iter;
					iter = lisfOfThreads.iterator();
					
					while (iter.hasNext()) {
						try {
							iter.next().join();
						}
						catch (InterruptedException e) {
							logger.error("Exception while joining threads... ", e);
						}
					}
					
				}
				
			}
			if(listOfBonus != null && !listOfBonus.isEmpty()){
				

				 UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				
				Map<Long, List<LoyaltyThresholdBonus>> ProgWiseBonusMap = prepareProgWiseBonusMap(listOfBonus);
				for (Long programID : ProgWiseBonusMap.keySet()) {
					List<Thread> lisfOfThreads = new ArrayList<Thread>();
					LoyaltyProgram loyaltyProgram = findProgramBy(programID);
					List<LoyaltyThresholdBonus> expireBonusLst = getExpireBonus(loyaltyProgram);
					MembershipProviderComm provider = new MembershipProviderComm(loyaltyProgram);
					
					
					Users user = userDao.findByUserId(loyaltyProgram.getUserId());
					loyaltyProgram.setExpireBonusList(expireBonusLst);
					for(int i = 0; i <= 10; i++){
						
						Thread th = new Thread(new LoyaltyRewardExpiryThreadComm(provider, loyaltyProgram,  user));
						lisfOfThreads.add(th);
						th.start();
						
					}
					
				
				Iterator<Thread> iter;
				iter = lisfOfThreads.iterator();
				
				while (iter.hasNext()) {
					try {
						iter.next().join();
					}
					catch (InterruptedException e) {
						logger.error("Exception while joining threads... ", e);
					}
				}
				}
			
			}
			
		}catch(Exception e){
			logger.error("Exception in LoyaltyRewardExpiry commm timer...", e);
		}
		finally{
			logger.info("Completed loyalty reward expiry comm timer");
			
		}
	}
	
private LoyaltyAutoComm getAutoCommByProgramId(Long programId) throws Exception {
		
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	
	/*public void run() {
		logger.info(">>> Started loyalty Rewards Expiry timer >>>");
		
		Calendar currentCal = Calendar.getInstance();
		//String currentDtStr = MyCalendar.calendarToString(currentDate, MyCalendar.FORMAT_YEARTODATE);
		
		List<Object[]> listOfTrans;
		try {
			
			List<Long> listOfPrgIds = fetchAllProgramsIds();
			for(Long programId : listOfPrgIds){
				
				List<LoyaltyProgramTier> listOfTier = getTierListByPrgmId(programId);
				for(LoyaltyProgramTier tier : listOfTier){
					
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tier.getRewardExpiryDateType())){
						currentCal.add(Calendar.MONTH, -tier.getRewardExpiryDateValue().intValue());
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tier.getRewardExpiryDateType())){
						currentCal.add(Calendar.YEAR, -tier.getRewardExpiryDateValue().intValue());
					}
					String currentDtStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
					listOfTrans = fetchCurrentActiveTransForExpiry(currentDtStr, programId, tier.getTierId());
					
					for(Object[] expTrans : listOfTrans){
						
						//String mbrshipNo = expTrans
						//TODO ADJUST BALANCES FROM CONTACT LOYALTY TABLE
						//TODO CREATE TRANSACTION IN TRANSACTION TABLE
						//TODO RESET VALUES TO ZERO IN EXPIRY TABLE
					}
				}
			}
			
			
			//logger.info(">>>>>>>>>>> list size::"+listOfTrans.size());
			//if(listOfTrans == null || listOfTrans.isEmpty()){
			//	return;
			//}
			for (LoyaltyTransactionExpiry transaction : listOfTrans) {
				logger.info(">>>>>>>>>>> entered for::");
				LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
				transaction.setExpiryStatus(OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_WORKING);
				//loyaltyProgramTransDao.saveOrUpdate(transaction);
				
				//updateExpiry(transaction);
				
				//transaction.setNetEarnedValueStatus("Expired");
				//transaction.setExpiryStatus(OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
				//loyaltyProgramTransDao.saveOrUpdate(transaction);
			}
		
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		
		logger.info(">>> Completed loyalty Rewards Expiry timer >>>");
	}*/

private LoyaltyProgram findProgramBy(Long programID) throws Exception {
	
	LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	return programDao.findById(programID);
	
}
public Map<Long, List<LoyaltyThresholdBonus>> prepareProgWiseBonusMap(List<LoyaltyThresholdBonus> bonus){
	
	Map<Long, List<LoyaltyThresholdBonus>> ProgWiseBonusMap = new HashMap<Long, List<LoyaltyThresholdBonus>>();
	for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonus) {
		Long programID = loyaltyThresholdBonus.getProgramId();
			
		List<LoyaltyThresholdBonus> bonusLst = ProgWiseBonusMap.get(programID);
		if(bonusLst == null || bonusLst.isEmpty()) bonusLst = new ArrayList<LoyaltyThresholdBonus>();
		
		bonusLst.add(loyaltyThresholdBonus);
		ProgWiseBonusMap.put(programID, bonusLst);
		
	}
	
	return ProgWiseBonusMap;
}
public Map<Long, LoyaltyProgramTier> prepareTierMapForProgram(LoyaltyProgram program){
		
	Map<Long, LoyaltyProgramTier> tierMap = null;
		try{
			List<LoyaltyProgramTier> retTier = getAllTiers(program.getProgramId());
			
			if(retTier != null && retTier.size() > 0){
				tierMap = new HashMap<Long, LoyaltyProgramTier>();
				
				for(LoyaltyProgramTier tier : retTier){
					tierMap.put(tier.getTierId(), tier);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching tiers...",e);
		}
		return tierMap;
		
		
	}
	
	/*private List<Object[]> fetchCurrentActiveTransForExpiry(String currCal, Long programId, Long tierId) throws LoyaltyProgramException {
		
		List<Object[]> listOfTrans =null;
		try{
		
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			
			listOfTrans = loyaltyTransactionExpiryDao.fetchCurrentActiveTransForExpiry(currCal, programId, tierId);
			logger.info("list  ::::::::::"+listOfTrans);
			logger.info("list size ::::::::::"+listOfTrans.size());
			return listOfTrans;
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch current active trans failed.");
		}
		
	}*/
	
	/*private void updateExpiry(LoyaltyTransactionExpiry transaction) {
		try {
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
			//ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(Long.valueOf(transaction.getCardNumber()));
			
			String valueCode = transaction.getEarnType();
//			Double netEarnedPoints = transaction.getNetEarnedPoints();
//			Double netEarnedAmount = transaction.getNetEarnedAmount();
			Double availableValue = transaction.getAvailableValue();
			
			if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_POINTS) && availableValue != null){
				
				contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - availableValue);
				
			}
			else if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_USD) && availableValue != null){
			
				contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() - availableValue);
				
			}				

			contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
			
		
		} catch (Exception e) {
			logger.error("Exception in update expiry points...", e);
		} 
		
	}*/
	
	
	private List<LoyaltyProgram> fetchAllPrograms() throws Exception {
		
		LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return programDao.fetchAllRewardExpiryAutoCommEnabledPrograms();
		
	}
	
private List<LoyaltyThresholdBonus> fetchOnlyExpireBonus() throws Exception {
		
		LoyaltyThresholdBonusDao LoyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		return LoyaltyThresholdBonusDao.getAllOnlyAutoCommExpireBy();
		
	}
	private List<LoyaltyProgramTier> getAllTiers(Long programID) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getAllTierByProgramId(programID);
	}
	
	private Map<Long, LoyaltyProgram> fetchProgramsList(List<LoyaltyProgram> programList) {
		Map<Long, LoyaltyProgram> programMap = null;
		String programIdStr = null;
		
		for(LoyaltyProgram program : programList){
			
			if(programIdStr == null){
				programIdStr = ""+program.getProgramId();
			}
			else{
				programIdStr += ","+program.getProgramId();
			}
			
		}
		
		try{
			
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			List<LoyaltyProgram> listOfPrograms = loyaltyProgramDao.fetchProgramsByProgramIdStr(programIdStr);
			
			if(listOfPrograms != null && listOfPrograms.size() > 0){
				programMap = new HashMap<Long, LoyaltyProgram>();
				
				for(LoyaltyProgram program : listOfPrograms){
					programMap.put(program.getProgramId(), program);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching programs...", e);
		}
		
		return programMap;
		
	}
	
	
	private Map<Long, LoyaltyProgramTier> fetchTiersList(List<LoyaltyProgramTier> tierList) {
		
		Map<Long, LoyaltyProgramTier> tierMap = null;
		String tierIdStr = null;
		
		for(LoyaltyProgramTier tier : tierList){
			
			if(tierIdStr == null){
				tierIdStr = ""+tier.getTierId();
			}
			else{
				tierIdStr += ","+tier.getTierId();
			}
		}
		
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersBytierIdStr(tierIdStr);
			
			if(listOfTiers != null && listOfTiers.size() > 0){
				tierMap = new HashMap<Long, LoyaltyProgramTier>();
				
				for(LoyaltyProgramTier tier : listOfTiers){
					tierMap.put(tier.getTierId(), tier);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching tiers...",e);
		}
		return tierMap;
	}
	
	public List<LoyaltyThresholdBonus> getExpireBonus(LoyaltyProgram loyaltyProgram) throws Exception{
		
		LoyaltyThresholdBonusDao LoyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO); 
		
		 List<LoyaltyThresholdBonus> retList = LoyaltyThresholdBonusDao.getAllExpireComm(loyaltyProgram.getProgramId());
		 
		 return retList;
	}
}
