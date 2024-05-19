package org.mq.optculture.timer;

import java.io.File;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.LoyaltyHelper;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyCustomerDumpGenerator extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public void run() {
		logger.info("Loyalty customer dump generator timer started...");
		
		String dumpFilePath = PropertyUtil.getPropertyValue("outboxloyaltydatadump");
		
		List<SparkBaseLocationDetails> sblist = getActiveSBList();
		for(SparkBaseLocationDetails sbloc : sblist){
			
			try{
				UserOrganization userOrg = sbloc.getUserOrganization();
				
				if(!isOptSyncEnabled(userOrg.getUserOrgId())) continue;
				
				
				logger.info("Generating for organization...."+userOrg.getOrgExternalId());
				String orgFilePath = dumpFilePath+"/"+userOrg.getOrgExternalId();
				createOrgIdFolder(orgFilePath);
				
				String userIdStr = getOrgIdStr(userOrg.getUserOrgId());
				
				logger.info("Started for org ="+userOrg.getOrgExternalId());
				LoyaltyHelper helper = new LoyaltyHelper();
				helper.generateLoyaltyCustomers(userIdStr, userOrg.getOrgExternalId(), orgFilePath);
				logger.info("Completed for org ="+userOrg.getOrgExternalId());
				
			}catch(Exception e){
				logger.error("Exception in customer dump for org ", e);
				continue;
			}
				
		}
		
		logger.info("Loyalty customer dump generator timer completed...");
	}

	private List<SparkBaseLocationDetails> getActiveSBList(){
		List<SparkBaseLocationDetails> sblist = null;
		try{
				
			SparkBaseLocationDetailsDao sblocDao = 
			(SparkBaseLocationDetailsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
			sblist = sblocDao.findAllActiveLoc();
		}catch(Exception e){
			logger.error("Exception in get active sb list...", e);
		}
		return sblist;
	}
	
	private void createOrgIdFolder(String orgFilePath) {
		File orgPath = new File(orgFilePath);
		if(orgPath.exists()){
			for(File file : orgPath.listFiles()){
//				if(file.getName().startsWith("LoyaltyCustomerID")) file.delete();
			}
		}
		else{
			orgPath.mkdirs();
		}
	}
	
	private String getOrgIdStr(Long orgId) throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		List<Users> userList = usersDao.getUsersListByOrg(orgId);
		String userIdStr = null;
		for(Users users : userList){
			if(userIdStr == null){
				userIdStr = ""+users.getUserId();
			}
			else{
				userIdStr += ","+users.getUserId();
			}
		}
		return userIdStr;
	}
	
	private boolean isOptSyncEnabled(Long orgId){
		
		boolean optSyncStatus = false;
		try{
		
			UpdateOptSyncDataDao optSyncDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			List<UpdateOptSyncData> optSyncList = optSyncDao.findByOrgId(orgId);
			if(optSyncList != null){
				for(UpdateOptSyncData optSync : optSyncList){
					if("Y".equalsIgnoreCase(optSync.getEnabledOptSyncFlag())){
						optSyncStatus = true;
						return optSyncStatus;
					}
				}
			}
		}catch(Exception e){
			logger.error("exception ...", e);
			optSyncStatus = false;
		}
		return optSyncStatus;
	}
			
}