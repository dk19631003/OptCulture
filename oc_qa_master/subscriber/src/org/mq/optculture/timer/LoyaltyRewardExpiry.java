package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltyRewardExpiry {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	//@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
	//@Scheduled(cron="0 0 1 3 1/1 ?") //for every month 3rd at 01:00hrs
	@Scheduled(cron="0 30 11 1 * ?") //for every month 1st at 11:30hrs
	//@Scheduled(cron="0 0 0 2 1/1 ?")    //for every month 2nd at 00:00hrs
	public void run_task() {
		logger.info("Loyalty reward expiry timer started...");
		
		List<LoyaltyProgram> listOfPrograms = null;
		List<LoyaltyProgramTier> listOfTiers = null;
		List<LoyaltyThresholdBonus> listOfBonus = null;
		try{
			
			listOfPrograms = fetchAllPrograms();
			listOfBonus = fetchOnlyExpireBonus();
			if((listOfPrograms == null || listOfPrograms.isEmpty()) && (listOfBonus == null || listOfBonus.isEmpty()) ){
				
				logger.debug("No programs currently found, so returning");
				return;
			}
			if(listOfPrograms != null && !listOfPrograms.isEmpty()) {
				
				for (LoyaltyProgram loyaltyProgram : listOfPrograms) {
					List<Thread> lisfOfThreads = new ArrayList<Thread>();
					
					MembershipProvider provider = new MembershipProvider(loyaltyProgram);
					
					Map<Long, LoyaltyProgramTier> tierMap = prepareTierMapForProgram(loyaltyProgram);
					List<LoyaltyThresholdBonus> expireBonusLst = getExpireBonus(loyaltyProgram, false);
					loyaltyProgram.setExpireBonusList(expireBonusLst);
					for(int i = 0; i <= 10; i++){
				
						Thread th = new Thread(new LoyaltyRewardExpiryThread(provider, loyaltyProgram, tierMap));
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
					logger.debug("-ALL Thread executed successfully-"+loyaltyProgram.getProgramId());
				}
				
			}
			if(listOfBonus != null && !listOfBonus.isEmpty()){
				
				Map<Long, List<LoyaltyThresholdBonus>> ProgWiseBonusMap = prepareProgWiseBonusMap(listOfBonus, listOfPrograms);
				logger.debug("ProgWiseBonusMap size ==>"+ProgWiseBonusMap.size());
				for (Long programID : ProgWiseBonusMap.keySet()) {
					logger.debug("programID =>"+programID );
					List<Thread> lisfOfThreads = new ArrayList<Thread>();
					LoyaltyProgram loyaltyProgram = findProgramBy(programID);
					List<LoyaltyThresholdBonus> expireBonusLst = getExpireBonus(loyaltyProgram, true);
					logger.debug("expireBonusLst size is=="+expireBonusLst.size() );
					MembershipProvider provider = new MembershipProvider(loyaltyProgram);
					if(expireBonusLst == null || expireBonusLst.isEmpty()) continue;
					
					List<LoyaltyThresholdBonus> expireBonusLstFinal = new ArrayList<LoyaltyThresholdBonus>();
					LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);	
					String deleteTheExisting = " DELETE FROM loyalty_transaction_expiry_util";
					int deletedcount = expiryDaoForDML.executeJdbcUpdateQuery(deleteTheExisting);
					
					logger.debug( "deletedcount==="+deletedcount);
					
					for (LoyaltyThresholdBonus loyaltyThresholdBonus : expireBonusLst) {
						
						
						Calendar cal = Calendar.getInstance();
						if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//							cal.add(Calendar.MONTH, 1);
							cal.add(Calendar.MONTH, -(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue()));
						}
						else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//							cal.add(Calendar.MONTH, 1);
							cal.add(Calendar.MONTH, -(12*(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue())));
						}
						else{
							logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
						}
						String expDate = "";
						if(cal.get(Calendar.MONTH) == 0){
							int year = cal.get(Calendar.YEAR)-1;
							expDate = year+"-12";
						}
						else{
							expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
						}
						
						//insert into the bonus  to be expired table first so that only those memberships will be processed
						String queryStr = " INSERT INTO loyalty_transaction_expiry_util (membership_number, user_id, org_id, program_id, "
								+ "expiry_points, expiry_amount, loyalty_id, transfered_to,bonus_id)  (SELECT membership_number,"+loyaltyProgram.getUserId()+","+
								loyaltyProgram.getOrgId()+","+loyaltyProgram.getProgramId()+", SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt,loyalty_id, transfered_to,bonus_id "
								+ " FROM loyalty_transaction_expiry WHERE  user_id="+loyaltyProgram.getUserId()
								+ " AND special_reward_id IS NULL AND bonus_id ="+loyaltyThresholdBonus.getThresholdBonusId()
								+ " AND (expiry_points > 0 OR expiry_amount > 0)"
								+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by loyalty_id, bonus_id) ";
						
						logger.info("query String : "+queryStr);
						
						int insertCount = expiryDaoForDML.executeJdbcUpdateQuery(queryStr);
						logger.info("insertCount : "+insertCount);
						if(insertCount <= 0) {
							continue;
						}
						expireBonusLstFinal.add(loyaltyThresholdBonus);
						
					}
					if(expireBonusLstFinal.isEmpty()) {
						logger.info("expireBonusLstFinal is empty : ");
						continue;
					}
					loyaltyProgram.setExpireBonusList(expireBonusLstFinal);
					//for(int i = 1; i <= 3; i++){
				
					Thread th = new Thread(new LoyaltyRewardExpiryThread(provider, loyaltyProgram));
					lisfOfThreads.add(th);
					th.start();
					
					//}
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
					for (LoyaltyThresholdBonus loyaltyThresholdBonus : expireBonusLstFinal) {
						Calendar cal = Calendar.getInstance();
						if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//							cal.add(Calendar.MONTH, 1);
							cal.add(Calendar.MONTH, -(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue()));
						}
						else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
//							cal.add(Calendar.MONTH, 1);
							cal.add(Calendar.MONTH, -(12*(loyaltyThresholdBonus.getBonusExpiryDateValue().intValue())));
						}
						else{
							logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
						}
						String expDate = "";
						if(cal.get(Calendar.MONTH) == 0){
							int year = cal.get(Calendar.YEAR)-1;
							expDate = year+"-12";
						}
						else{
							expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
						}
						String queryStr = " UPDATE loyalty_transaction_expiry e, loyalty_transaction_expiry_util u "
								+ " SET e.expiry_points = 0, e.expiry_amount = 0 WHERE e.loyalty_id = u.loyalty_id and e.bonus_id=u.bonus_id"
								+ " AND (e.expiry_points > 0 OR e.expiry_amount > 0)"
								+ " AND STR_TO_DATE(concat(year(e.created_date),'-', month(e.created_date)), '%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
						logger.debug("query =="+queryStr);
						expiryDaoForDML.executeJdbcUpdateQuery(queryStr);
					}
					logger.debug("-ALL Bonus Thread executed successfully-"+loyaltyProgram.getProgramId());
				}
				
			}
			
			
		}catch(Exception e){
			logger.error("Exception in LoyaltyRewardExpiry timer...", e);
		}
		finally{
			logger.info("Completed loyalty reward expiry timer...");
		}
	}
	public Map<Long, List<LoyaltyThresholdBonus>> prepareProgWiseBonusMap(List<LoyaltyThresholdBonus> bonus, List<LoyaltyProgram> listOfPrograms){
		
		Map<Long, List<LoyaltyThresholdBonus>> ProgWiseBonusMap = new HashMap<Long, List<LoyaltyThresholdBonus>>();
		//Set<Long> alreadyAddressedProgramsSet = new HashSet<Long>();
		/*if(listOfPrograms != null && !listOfPrograms.isEmpty()) {
			
			for (LoyaltyProgram loyaltyProgram : listOfPrograms) {
				alreadyAddressedProgramsSet.add(loyaltyProgram.getProgramId());
			}
		}*/
		for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonus) {
			Long programID = loyaltyThresholdBonus.getProgramId();
				//if(alreadyAddressedProgramsSet.contains(programID)) continue;
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
	
	public List<LoyaltyThresholdBonus> getExpireBonus(LoyaltyProgram loyaltyProgram, boolean withExpirity) throws Exception{
		
		LoyaltyThresholdBonusDao LoyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO); 
		
		 List<LoyaltyThresholdBonus> retList = LoyaltyThresholdBonusDao.getAllExpireBy(loyaltyProgram.getProgramId(), withExpirity);
		 
		 return retList;
	}
private List<LoyaltyProgramTier> getAllTiers(Long programID) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getAllTierByProgramId(programID);
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
	
	private List<LoyaltyProgram> fetchAllPrograms() throws Exception {
		
		LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return programDao.fetchAllPrograms();
		
	}
	private LoyaltyProgram findProgramBy(Long programID) throws Exception {
		
		LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return programDao.findById(programID);
		
	}
	
	private List<LoyaltyThresholdBonus> fetchOnlyExpireBonus() throws Exception {
		
		LoyaltyThresholdBonusDao LoyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		return LoyaltyThresholdBonusDao.getAllOnlyExpireBy();
		
	}
	
	private List<LoyaltyProgramTier> getAllTiers() throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.findAllTiers();
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
	
	
}
