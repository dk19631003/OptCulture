package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltyMembershipExpiryAutoComm{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	//@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
@Scheduled(cron="0 0 0 5 * ?")    //for every month 1st at 00:00hrs
	public void run_task() {
		logger.info(">>> Started loyalty membership Expiry timer >>>");
		List<LoyaltyProgram> programsList;
		try {
			//get only active users active programs list
			programsList = fetchAllPrograms();
			if(programsList == null || programsList.isEmpty()){
				return;
			}
			logger.info(">>>>>>>>>>> list size::"+programsList.size());
			LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			for (LoyaltyProgram programObj : programsList) {
				logger.info(">>>>>>>>>>> entered for::");
				LoyaltyAutoComm loyaltyAutoComm = loyaltyAutoCommDao.findById(programObj.getProgramId());
				 Users user = userDao.findByUserId(programObj.getUserId());
				if(loyaltyAutoComm != null) {

					LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
//					List<ContactsLoyalty> conList = null ;

					if(OCConstants.FLAG_YES == programObj.getGiftMembrshpExpiryFlag()){

						Calendar currentCal = Calendar.getInstance();

						if(programObj.getGiftMembrshpExpiryDateType() != null && !programObj.getGiftMembrshpExpiryDateType().isEmpty()){

							if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(programObj.getGiftMembrshpExpiryDateType())){
								currentCal.add(Calendar.MONTH, 1);
								currentCal.add(Calendar.MONTH, -programObj.getGiftMembrshpExpiryDateValue().intValue());
							}
							else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(programObj.getGiftMembrshpExpiryDateType())){
								currentCal.add(Calendar.MONTH, 1);
								currentCal.add(Calendar.MONTH, -(12*programObj.getGiftMembrshpExpiryDateValue().intValue()));
							}

//							logger.info("currentCal is"+currentCal);
//							String currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
							String currentDateStr = "";
							if(currentCal.get(Calendar.MONTH) == 0){
								int year = currentCal.get(Calendar.YEAR)-1;
								currentDateStr = year+"-12";
							}
							else{
								currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
							}
							logger.info("currentDtStr is"+currentDateStr);
							//fetch chunks wise and 1000 for each
							Long totalCount = getCountGiftCustomersToExpire(programObj.getUserId(), programObj.getProgramId(),currentDateStr);
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
									 List<Object[]> conList = getGiftCustomersToExpire(programObj.getUserId(), programObj.getProgramId(),currentDateStr, initialIndex,threshold);
									 if(conList != null && !conList.isEmpty()) {
										 for(Object[] objArr : conList) {
											 logger.info("Loyalty autocommunication obj"+loyaltyAutoComm);
											 
											 if(objArr[2] != null){
												 Long userId = (Long) objArr[1];
												 
												 Long loyaltyId = (Long) objArr[0];
												// Contacts contactObj = contactsDao.findById((Long) objArr[2]);
												 
												 //send email
												 String emailId = (String)objArr[4];//contactObj.getEmailId();
												 Long tempId = loyaltyAutoComm.getGiftMembrshpExpiryEmailTmpltId();
												 logger.info("tempId,emailId------"+tempId+emailId);
												 if(tempId!=null && emailId != null && !emailId.trim().isEmpty()) {
													 autoCommGen.sendGiftMembershipExpiryTemplate(emailId,tempId,user,(Long) objArr[2],loyaltyId);
												 }
												 
												 //send auto-sms
												 String mobileNo = (String)objArr[5];//contactObj.getMobilePhone();
												 Long smsTempId = loyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId();
												 if(smsTempId != null && mobileNo != null && !mobileNo.isEmpty()) {
													 autoCommGen.sendGiftMembershipExpirySMSTemplate(mobileNo,smsTempId,user,(Long) objArr[2],loyaltyId);
												 }
											 }
										 }
									 }
									 initialIndex =(loop_var*threshold)+1;
								 }
							}
							
						}
					}


					if(OCConstants.FLAG_YES == programObj.getMembershipExpiryFlag()) {
						List<LoyaltyProgramTier> tierList = null;
						tierList = getAllTiersByPrgmId(programObj.getProgramId());
						if(tierList != null && !tierList.isEmpty()) {
							logger.info("tierList size ::::::::::"+tierList.size());

							for(LoyaltyProgramTier tierObj : tierList) {
								Calendar currentCal = Calendar.getInstance();
								List<Object[]> conList = null;
								if(tierObj.getMembershipExpiryDateType() != null && !tierObj.getMembershipExpiryDateType().isEmpty()){
									if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(tierObj.getMembershipExpiryDateType())){
										currentCal.add(Calendar.MONTH, 1);
										currentCal.add(Calendar.MONTH, -tierObj.getMembershipExpiryDateValue().intValue());
									}
									else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(tierObj.getMembershipExpiryDateType())){
										currentCal.add(Calendar.MONTH, 1);
										currentCal.add(Calendar.MONTH, -(12*tierObj.getMembershipExpiryDateValue().intValue()));
									}

//									logger.info("currentCal is"+currentCal);
//									String currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
									String currentDateStr = "";
									if(currentCal.get(Calendar.MONTH) == 0){
										int year = currentCal.get(Calendar.YEAR)-1;
										currentDateStr = year+"-12";
									}
									else{
										currentDateStr = currentCal.get(Calendar.YEAR)+"-"+currentCal.get(Calendar.MONTH);
									}
//									logger.info("currentDtStr is"+currentDateStr);
									List<Object[]> createdDtList = null;
									List<Object[]> upgdDtList = null;
									 if(programObj.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES) {
										 Long totalCount = getCountOfLtyCustomersToExpireByUpgradedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr);
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
													 conList = getLtyCustomersToExpireByUpgradedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr, initialIndex, threshold);
													//can get the contactsids all at once
													 if(conList != null && !conList.isEmpty()) {
															for(Object[] objArr : conList) {
																if(objArr[2] != null){
																	Long userId = (Long) objArr[1];
																	Long loyaltyId = (Long) objArr[0];

																	//send auto-email
																	String emailId = (String)objArr[4];//contactObj.getEmailId();
																	Long tempId = loyaltyAutoComm.getMbrshipExpiryEmailTmpltId();
																	logger.info("tempId,emailId------"+tempId+emailId);
																	if(tempId!=null && emailId != null && !emailId.trim().isEmpty()) {
																		autoCommGen.sendMembershipExpiryTemplate(emailId, tempId, user, (Long) objArr[2], loyaltyId);
																	}
																	
																	//send auto-sms
																	String mobileNo = (String)objArr[5];//contactObj.getMobilePhone();
																	Long smsTempId = loyaltyAutoComm.getMbrshipExpirySmsTmpltId();
																	if(smsTempId != null && mobileNo != null && !mobileNo.isEmpty()) {
																		autoCommGen.sendMembershipExpirySMSTemplate(mobileNo, smsTempId, user, (Long) objArr[2], loyaltyId);
																	}
																}
															}
														}
													 
													 initialIndex =(loop_var*threshold)+1;
												 }
											
											
											}
										 
									 }else{
										 
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
															for(Object[] objArr : conList) {
																if(objArr[2] != null){
																	Long userId = (Long) objArr[1];
																	Long loyaltyId = (Long) objArr[0];
	
																	//send auto-email
																	String emailId = (String)objArr[4];
																	Long tempId = loyaltyAutoComm.getMbrshipExpiryEmailTmpltId();
																	logger.info("tempId,emailId------"+tempId+emailId);
																	if(tempId!=null && emailId != null && !emailId.trim().isEmpty()) {
																		autoCommGen.sendMembershipExpiryTemplate(emailId, tempId, user, (Long) objArr[2], loyaltyId);
																	}
																	
																	//send auto-sms
																	String mobileNo = (String)objArr[5];
																	Long smsTempId = loyaltyAutoComm.getMbrshipExpirySmsTmpltId();
																	if(smsTempId != null && mobileNo != null && !mobileNo.isEmpty()) {
																		autoCommGen.sendMembershipExpirySMSTemplate(mobileNo, smsTempId, user, (Long) objArr[2], loyaltyId);
																	}
																}
															}
														}
													 initialIndex =(loop_var*threshold)+1;
													 
												 }
											
											
											}
										 
									 }
									
									/* if(programObj.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES) {
										 createdDtList = getLtyCustomersToExpireByCreatedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr, OCConstants.FLAG_YES);
										 upgdDtList = getLtyCustomersToExpireByUpgradedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr);
									 }
									 else {
										 createdDtList = getLtyCustomersToExpireByCreatedDate(programObj.getUserId(), programObj.getProgramId(), tierObj.getTierId(),currentDateStr, OCConstants.FLAG_NO);
									 }
									*/
									/*if(createdDtList != null && !createdDtList.isEmpty() && upgdDtList != null && !upgdDtList.isEmpty()) {
										conList = createdDtList;
										for (Object[] objArr : upgdDtList) {
											conList.add(objArr);
										}
									}
									else {
										if(createdDtList == null || createdDtList.isEmpty()) {
											conList = upgdDtList;
										}
										else if(upgdDtList == null || upgdDtList.isEmpty()) {
											conList = createdDtList;
										}

									}*/
									logger.info("conList size ::::::::::"+conList);
								}
								
							}
						}
					}
				}
			}
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.info(">>> Completed loyalty membership Expiry timer >>>");
	}

	private List<LoyaltyProgram> fetchAllPrograms() throws LoyaltyProgramException {

		List<LoyaltyProgram> programsList =null;
		try{

			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			programsList = loyaltyProgramDao.fetchAllMemberExpiryAutoCommEnabledPrograms();
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

	private List<LoyaltyProgramTier> getAllTiersByPrgmId(Long prgmId) throws LoyaltyProgramException {

		List<LoyaltyProgramTier> tierList =null;
		try{
			logger.info("Fetching tier method");
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			tierList = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
			logger.info("Tier list "+tierList);
			if(tierList != null && tierList.size() > 0){
				logger.info("list size ::::::::::"+tierList.size());
				return tierList;
			}

		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all tiers failed.");
		}

		return tierList;

	}
	private long getCountOfLtyCustomersToExpireByCreatedDate(Long userId,Long programID,  Long tierId, String currentDateStr, char resetFlag) throws LoyaltyProgramException {
		long retCount = 0l;
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			retCount = contactsLoyaltyDao.getCountOfLtyCustomersToExpireByCreatedDateForComm(userId, programID, tierId, currentDateStr, resetFlag);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return retCount;
	}
	private List<Object[]> getLtyCustomersToExpireByCreatedDate(Long userId,Long programID,  Long tierId, String currentDateStr, char resetFlag, int startFrom, int maxRecords) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getLtyCustomersToExpireByCreatedDateForComm(userId, programID, tierId, currentDateStr, resetFlag, startFrom, maxRecords);
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
	private long getCountOfLtyCustomersToExpireByUpgradedDate(Long userId,Long programID,  Long tierId, String currentDateStr) throws LoyaltyProgramException {
		Long totalCount = 0l;
		
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalCount = contactsLoyaltyDao.getCountOfLtyCustomersToExpireByUpgradedDateForComm(userId, programID, tierId, currentDateStr);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return totalCount;
	}
	private List<Object[]> getLtyCustomersToExpireByUpgradedDate(Long userId,Long programID,  Long tierId, String currentDateStr, int startFrom, int maxRecord) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getLtyCustomersToExpireByUpgradedDateForComm(userId, programID, tierId, currentDateStr, startFrom, maxRecord);
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
	private long getCountGiftCustomersToExpire(long userId, Long programId, String currentDateStr) throws LoyaltyProgramException {
		Long totalCount = 0l;
		
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalCount = contactsLoyaltyDao.getCountOfGiftCustomersToExpireForComm(userId, programId, currentDateStr);
			
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch all customers failed.");
		}
		
		return totalCount;
	}
	private List<Object[]> getGiftCustomersToExpire(long userId, Long programId, String currentDateStr, int startFrom, int maxRecords) throws LoyaltyProgramException {
		List<Object[]> contList = null;
		try{
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contList = contactsLoyaltyDao.getGiftCustomersToExpireForComm(userId, programId, currentDateStr, startFrom, maxRecords);
			
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
	

}
