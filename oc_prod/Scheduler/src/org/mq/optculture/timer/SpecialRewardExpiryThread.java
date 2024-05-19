package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SpecialReward;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class SpecialRewardExpiryThread extends Thread{

	private SpecialReward specialReward;
	
	public SpecialRewardExpiryThread(SpecialReward specialReward){
		this.specialReward = specialReward;
		
	}
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public void run() {
		logger.debug("running for Special Reward : "+specialReward.getRewardId().longValue());
		try {
			Calendar cal = Calendar.getInstance();
			if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(specialReward.getRewardExpiryType())){
//			cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.MONTH, -(Integer.parseInt(specialReward.getRewardExpiryValue())));
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(specialReward.getRewardExpiryType())){
//			cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.MONTH, -(12*(Integer.parseInt(specialReward.getRewardExpiryValue()))));
			}
			
//		String expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			
			String expDate = "";
			if(cal.get(Calendar.MONTH) == 0){
				int year = cal.get(Calendar.YEAR)-1;
				expDate = year+"-12";
			}
			else{
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}
			LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			
			long insertedChildForLtyPointsExpirycnt =0;
			long insertedChildForLtyAmtExpirycnt =0l;
			long insertedChildForRewardExpirycnt = 0l;
			long VCcount = expiryDaoForDML.expireSPRewardTrxForVC(specialReward.getRewardId(), expDate, Long.parseLong(specialReward.getCreatedBy()));
			logger.debug("expired valuecodes transaction "+VCcount);
			
			long rewardCnt = expiryDaoForDML.expireSPRewardTrxForReward(specialReward.getRewardId(), expDate, Long.parseLong(specialReward.getCreatedBy()));
			
			logger.debug("expired reward transaction "+rewardCnt);
			if( VCcount > 0) {
				
				insertedChildForRewardExpirycnt = expiryDaoForDML.insertChildRecordOnRewardExpiry(specialReward.getRewardId(), expDate, 
						Long.parseLong(specialReward.getCreatedBy()), Long.parseLong(specialReward.getOrgId()));
				
				logger.debug("inserted expired reward transaction "+insertedChildForRewardExpirycnt);
				if(insertedChildForRewardExpirycnt != -1 ){
					
					long movedTobackupcnt = expiryDaoForDML.moveRewardExpiredToBackupTable(specialReward.getRewardId(), expDate);
					logger.debug("moved to the backup table "+movedTobackupcnt);
				}
			}
			if(rewardCnt >0){
				
				
				insertedChildForLtyPointsExpirycnt = expiryDaoForDML.insertChildRecordOnLtyPointsExpiry(specialReward.getRewardId(), expDate, 
						Long.parseLong(specialReward.getCreatedBy()), Long.parseLong(specialReward.getOrgId()));
				
				logger.debug("inserted expired lty points transaction "+insertedChildForLtyPointsExpirycnt);
				if(insertedChildForLtyPointsExpirycnt >0 ){
					
					long movedTobackupcnt = expiryDaoForDML.movePointsExpiredToBackupTable(specialReward.getRewardId(), expDate);
					logger.debug("moved to the backup table "+movedTobackupcnt);
				}

				 insertedChildForLtyAmtExpirycnt = expiryDaoForDML.insertChildRecordOnLtyAmountExpiry(specialReward.getRewardId(), expDate, 
						Long.parseLong(specialReward.getCreatedBy()), Long.parseLong(specialReward.getOrgId()));
				
				logger.debug("inserted expired reward transaction "+insertedChildForLtyAmtExpirycnt);
				if( insertedChildForLtyAmtExpirycnt >0){
					
					long movedTobackupcnt = expiryDaoForDML.moveAmountExpiredToBackupTable(specialReward.getRewardId(), expDate);
					logger.debug("moved to the backup table "+movedTobackupcnt);
				}

				
			}
			
			
		} catch (NumberFormatException e) {
			logger.error("Exception ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		
	}
	/*private List<Object[]> fetchExpiryValues(Long programId, String expDate, String rewardFlag) throws Exception {
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		return expiryDao.fetchExpiryValues(programId, expDate, rewardFlag);
	}*/
}
