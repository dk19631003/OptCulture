package org.mq.optculture.timer;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class PerkExpirySchedularThread extends Thread {
	
	private LoyaltyProgramTier tier;
	private LoyaltyProgram loyaltyProgram;
	
	public PerkExpirySchedularThread(LoyaltyProgramTier tier, LoyaltyProgram loyaltyProgram) {
		this.tier = tier;
		this.loyaltyProgram = loyaltyProgram;
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public void run() {
		
		try {
			logger.info("Perk expiry thread has started....>>");
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.MONTH, -(Integer.parseInt(tier.getRewardExpiryDateValue().toString())));
			int currentMonth = cal.get(Calendar.MONTH);
			
			if(tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_QUARTER)) {
				if(!(currentMonth==3 || currentMonth==6 || currentMonth==9 || currentMonth==0)) {
					return;
				}
			} else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_HALFYEAR)) {
				if(!(currentMonth==6 || currentMonth==0)) {
					return;
				}
			}else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_YEAR)) {
				if(currentMonth!=0) {
					return;
				}
			}
			//else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_MONTH)) {
				
			//}
			
			/*String expDate = "";
			if(cal.get(Calendar.MONTH) == 0){
				int year = cal.get(Calendar.YEAR)-1;
				expDate = year+"-12";
			}
			else{
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}*/
			LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			
			
			long insertedChildForRewardExpirycnt = 0l;
			long VCcount = 0l;
			
				
				VCcount = expiryDaoForDML.expirePerks(tier.getTierId(), tier.getEarnType(), loyaltyProgram.getProgramId(), Long.parseLong(tier.getCreatedBy()));
				logger.debug("expired valuecodes transaction "+VCcount);
				
			
			//so query must be from expiry table based the tier expiry date value , matching to the valuecode + tierid
			
            if( VCcount != -1) {
				//this will also b chnaged
				insertedChildForRewardExpirycnt = expiryDaoForDML.insertChildRecordOnPerksExpiry(tier.getTierId(), 
						Long.parseLong(tier.getCreatedBy()), loyaltyProgram.getOrgId(), loyaltyProgram.getProgramId());
				
				logger.debug("inserted expired reward transaction "+insertedChildForRewardExpirycnt);
			}
            
            if(insertedChildForRewardExpirycnt != -1){
				//same here
				long movedTobackupcnt = expiryDaoForDML.moveExpiredPerksToBackupTable(tier.getTierId(), tier.getEarnType(), loyaltyProgram.getProgramId(), Long.parseLong(tier.getCreatedBy()));
				logger.debug("moved to the backup table "+movedTobackupcnt);
				
				long reissuePerksTo = expiryDaoForDML.reIssuePerksOnExpiry(tier.getTierId(), tier.getEarnType(), loyaltyProgram.getProgramId(), Long.parseLong(tier.getCreatedBy()));
				logger.debug("moved to the backup table "+movedTobackupcnt);
				
				long deleteCount = expiryDaoForDML.deletePerkRewardTrx(tier.getTierId(), tier.getEarnType(), loyaltyProgram.getProgramId(), Long.parseLong(tier.getCreatedBy()));
				logger.debug("moved to the backup table "+movedTobackupcnt);
				
				logger.info("before calling re issue perks endpoint>>>>>");
				//add the request params of program n tier id just like 
				String url =  PropertyUtil.getPropertyValueFromDB((OCConstants.REISSUE_PERKS_SERVICE_ENDPOINT));
				logger.info("endPoint URL ====="+url);
				String finalUrl=url.replace("|^", "").replace("^|", "");
				finalUrl=finalUrl.replaceAll("tier_Id",tier.getTierId().toString()).replaceAll("prgm_Id", loyaltyProgram.getProgramId().toString()); 
				logger.info("finalUrl "+finalUrl);
				Utility.pingSubscriberService(finalUrl);
				logger.info("after re issue perks end point>>>");
				//Utility.pingSubscriberService(PropertyUtil.getPropertyValueFromDB(OCConstants.REISSUE_PERKS_SERVICE_ENDPOINT));//define it in app.props table
				
			}
            
            logger.info("Perk expiry thread has ended....>>");
           
			
		} catch (NumberFormatException e) {
			logger.error("Exception ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		// reissue perks for all those rewards which are expired.
	}

}
